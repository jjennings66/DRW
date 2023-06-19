package com.drw.dealimport;
 
import com.drw.dealimport.DRW_DealImportLibrary.CONST_VALUES;
import com.drw.dealimport.DRW_DealImportLibrary.DEAL_BOOKING_STATUS;
import com.drw.dealimport.DRW_DealImportLibrary.DEAL_BOOKING_TEMPLATE;
import com.drw.dealimport.DRW_DealImportLibrary.DEBUG_LOGFILE;
import com.drw.dealimport.DRW_DealImportLibrary.ERROR_TYPE_ENUM;
import com.drw.dealimport.DRW_DealImportLibrary.LIB;
import com.drw.dealimport.DRW_DealImportLibrary.TRANF_HELPER;
import com.drw.dealimport.DRW_DealImportLibrary.TRAN_INFO;
import com.drw.dealimport.DRW_DealImportLibrary.USER_TABLE;
import com.drw.dealimport.DRW_DealImportLibrary.USER_TABLES;
import com.olf.openjvs.DBUserTable;
import com.olf.openjvs.IContainerContext;
import com.olf.openjvs.IScript;
import com.olf.openjvs.OCalendar;
import com.olf.openjvs.OException;
import com.olf.openjvs.PluginCategory;
import com.olf.openjvs.PluginType;
import com.olf.openjvs.Ref;
import com.olf.openjvs.Str;
import com.olf.openjvs.Table;
import com.olf.openjvs.Transaction;
import com.olf.openjvs.Util;
import com.olf.openjvs.enums.COL_TYPE_ENUM;
import com.olf.openjvs.enums.DATE_FORMAT;
import com.olf.openjvs.enums.SCRIPT_CATEGORY_ENUM;
import com.olf.openjvs.enums.SCRIPT_TYPE_ENUM;
import com.olf.openjvs.enums.SEARCH_CASE_ENUM;
import com.olf.openjvs.enums.SHM_USR_TABLES_ENUM;
import com.olf.openjvs.enums.TRANF_FIELD;
import com.olf.openjvs.enums.TRAN_STATUS_ENUM;

/*
Script Name: DRW_DealImport
Description:  Main script for deal importing
 
// TODO
 2) Add Cutoff Date for Deal Booking 
 4) Generate an error if the tran info field for an 'Extra' tran info does not work (field is missing)
 
// DONE
 1) Add other fields... start date, end date, location - DONE
 2) Add initial check for deal templates to make sure they are found - DONE
 3) Show if no errors - DONE
 4) Add functionality for mappings, counterparty, curve, etc.
 5) Populate Book (done)
 6) add broker 'natively', meaning not as a 'TRANF_'
 7) support dynamic SeqNum2 (profile)... not just side
 8) Add functionality for mappings, curve 
 9) Add functionality for mappings, location 
 10) Add support for index type deals
 11) Add full support for Location, with pipeline/zone set
 12) Set side 2 for basis swaps
 13) Add a script post status for every 10 trades booked
 14) Add a check for Commodity Toolset to *not* try to book a deal if the location is inactive.
 15) Add a check for if a Location is not active
 16) Set the correct end date
 17) Add Internal Contact
 18) Add 'Run Info' table for when you run this script
 
   
Revision History
25-Apr-2023   Brian   New Script
27-Apr-2023   Brian   1) Added support for location2 for COMM-TRANS
                      2) Now will auto-calc the correct Pipeline to set and/or Zone to set if left blank (or no column) when needed
                      3) for COMM-TRANS, added support for columns variable_cost and variable_demand
01-May-2023   Brian   1) Added checks for missing required fields:  Portfolio, Ext Bunit and Location
                      2) Added a Post Status every X deals processed
                      3) Added version number to the 'Deal Booking Errors'
                      4) Added support for COMM-STOR Trades
                      5) Added a 'Run Info' view table 
22-May-2023   Brian   1) Added External Portfolio      external_portfolio
                      2) Added Internal Business Unit  internal_bunit
                      3) Added Location #3 for Storage Deals (with support for mapping) location3
02-Jun-2023   Brian   1) Added Support for Param Script for running in read only mode                
06-Jun-2023   Brian   1) Added support for two locations having the same name.  Need to put the Pipeline into user_di_mapping_location.extra_string1 (extra string one)                    
                       
*/

@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_GENERIC)
@PluginType(SCRIPT_TYPE_ENUM.MAIN_SCRIPT)
public class DRW_DealImport implements IScript {
	
	final static int NUM_DEALS_PER_STATUS_UPDATE = 5;
	  
	public void execute(IContainerContext context) throws OException {
		String className = this.getClass().getSimpleName();
		int iRunNumber = DEBUG_LOGFILE.getRunNumber(className);
		boolean bDoFail = false;
 		
		try {
			boolean bReadOnly = false;

			Table argt = context.getArgumentsTable();

			// get the value from the argt if the column is there
 			final String READ_ONLY_COL_NAME = "read_only_flag";
			if (argt.getColNum(READ_ONLY_COL_NAME) >= 1) {
				int iValue = LIB.safeGetInt(argt, READ_ONLY_COL_NAME, CONST_VALUES.ROW_TO_GET_OR_SET);
				if (iValue == CONST_VALUES.VALUE_OF_TRUE) {
					bReadOnly = true;
				}
			} 
 		
//			if (Str.iEqual(Ref.getUserName(), "bshydlo") == 1) {
//				className = "BrianTest";
//				bReadOnly = true;
//			}
			
			DEBUG_LOGFILE.logToFile("START", iRunNumber, className);
			DEBUG_LOGFILE.logToFile("Value of bReadOnly: " + bReadOnly, iRunNumber, className);
			int iToday = OCalendar.today();
			bDoFail = this.doEverything(bReadOnly, iToday, iRunNumber, className);
			DEBUG_LOGFILE.logToFile("END", iRunNumber, className);
			
		} catch (Exception e) {
			// do not log this
		}
		
		if (bDoFail == true) {
			Util.exitFail();
		} else {
			Util.exitSucceed();
		}
	}
 
	
	public boolean doEverything(boolean bReadOnly, int iToday, int iRunNumber, String className) throws OException {
		
		Table tErrors = Util.NULL_TABLE;
		boolean bDoFailThisFunction = false;
		try {
			boolean bOkToProceed = true;
			
			{
				int iNumTrades = 0;
			
				{
					{
						Table tTemp = Table.tableNew();
						String sSQL = "select id_number from " + USER_TABLE.user_di_staging + " WHERE endur_deal_num < 1";
						LIB.execISql(tTemp, sSQL, false, className);
						iNumTrades = LIB.safeGetNumRows(tTemp);
						tTemp = LIB.destroyTable(tTemp);
					}
				}
			
				Table tInfo = Table.tableNew();
				LIB.safeAddCol(tInfo, "message_number", COL_TYPE_ENUM.COL_INT, "#");
				LIB.safeAddCol(tInfo, "message", COL_TYPE_ENUM.COL_STRING, "Message");
				{
					int iMaxRow = tInfo.addRow();
					LIB.safeSetString(tInfo, "message", iMaxRow, "Deal Import Version: " + LIB.VERSION_NUMBER);
					
					iMaxRow = tInfo.addRow();
					if (bReadOnly == true) {
						LIB.safeSetString(tInfo, "message", iMaxRow, "Running in READ ONLY mode (will not book trades)");
					} else {
						LIB.safeSetString(tInfo, "message", iMaxRow, "Running in TRADE BOOKING mode (not read only)");
					}

					// this does not apply if Read Only is true
					if (bReadOnly == false) {
						iMaxRow = tInfo.addRow();
						LIB.safeSetString(tInfo, "message", iMaxRow, "WILL book trades even if there is an error");
					}
					
					iMaxRow = tInfo.addRow();
					LIB.safeSetString(tInfo, "message", iMaxRow, "Processing this many trades: " + iNumTrades);
					
					tInfo.setColIncrementInt("message_number", 1, 1);
					
					LIB.viewTable(tInfo, "Run Info");
					tInfo = LIB.destroyTable(tInfo);
				}
			}
 				
			tErrors = FUNC.ERROR_HANDLING.createErrorTable(iRunNumber, className);
			
			{
				boolean bDoFail = FUNC.CHECK.checkForTemplate(tErrors, iRunNumber, className);
				 
				if (bDoFail == true) {
					bOkToProceed = false;
					bDoFailThisFunction = true;
				}
			}
			
			// Check for valid values even if 'bOkToProceed' is false
			{
				boolean bDoFail = FUNC.CHECK.checkForTranInfoType(tErrors, iRunNumber, className);
				 
				if (bDoFail == true) {
					bOkToProceed = false;
					bDoFailThisFunction = true;
				}
			}
			
			// Check for valid values even if 'bOkToProceed' is false
			{
				boolean bDoFail = FUNC.CHECK.checkForMissingUserTables(tErrors, iRunNumber, className);
				 
				if (bDoFail == true) {
					bOkToProceed = false;
					bDoFailThisFunction = true;
				}
			} 
			
			if (bOkToProceed == true) {
			
				Table tUserTable = Table.tableNew();
				{
					String sSQL = "select * from " + USER_TABLE.user_di_staging + " WHERE endur_deal_num < 1";

					// TODO
					// sSQL = "select * from " + USER_TABLE.user_di_staging + " WHERE source_system_deal = '61211'";
 					
//					if (Str.iEqual(Ref.getUserName(), "bshydlo") == 1) {
//						sSQL = "select * from " + USER_TABLE.user_di_staging + " WHERE source_system_deal = '76317'";
//					}
 					
					LIB.execISql(tUserTable, sSQL, false, className);
				}
				
				DEBUG_LOGFILE.logToFile("Number of Rows in User Table: " + LIB.safeGetNumRows(tUserTable), iRunNumber, className);
				
				int iNumRows = LIB.safeGetNumRows(tUserTable);
				
				if (iNumRows < 1) {
					bOkToProceed = false;
					String sMessage = "No rows in user table *or* no rows with " + COLS.USER_DEAL_STAGING_TABLE.endur_deal_num + " not already populated";
					FUNC.ERROR_HANDLING.addErrorToTable(tErrors, ERROR_TYPE_ENUM.NO_ROWS_IN_USER_TABLE, "", sMessage, "");
					// do *not* fail the overall script.. it is 'ok' if there are no rows to import
				}
				
				if (bOkToProceed == true) {
					
					DEBUG_LOGFILE.logToFile("Step 100, just before the deal booking loop", iRunNumber, className);
					
					// Get templates from the database
					Table tTemplates = Table.tableNew();
					{
						LIB.safeAddCol(tTemplates, COLS.TEMPLATES.tran_num, COL_TYPE_ENUM.COL_INT);
						LIB.safeAddCol(tTemplates, COLS.TEMPLATES.template_name, COL_TYPE_ENUM.COL_STRING);
						
						String sSQL = "select tran_num, reference from ab_tran WHERE tran_status = 15 AND base_ins_type = 48010";
						LIB.execISql(tTemplates, sSQL, false, className);
					}
		 			
					for (int iCounter = 1; iCounter <= iNumRows; iCounter++) {
 						this.processOneDeal(tUserTable, iCounter, iNumRows, bReadOnly, iToday, tTemplates, tErrors, iRunNumber, className);
					} 
			
					tTemplates = LIB.destroyTable(tTemplates);
					
					if (LIB.safeGetNumRows(tErrors) < 1) {
						FUNC.ERROR_HANDLING.addErrorToTable(tErrors, ERROR_TYPE_ENUM.INFO, "", "No Errors", "");
					} 
					
					if (bReadOnly == true) {
						FUNC.ERROR_HANDLING.addErrorToTable(tErrors, ERROR_TYPE_ENUM.INFO, "", "Ran in Read-only mode", "");
						FUNC.ERROR_HANDLING.addErrorToTable(tErrors, ERROR_TYPE_ENUM.INFO, "", "Ran for this many deals: " + iNumRows, "");
					}
					if (bReadOnly == false) {
						FUNC.ERROR_HANDLING.addErrorToTable(tErrors, ERROR_TYPE_ENUM.INFO, "", "Ran in deal booking mode (not read only)", "");
						FUNC.ERROR_HANDLING.addErrorToTable(tErrors, ERROR_TYPE_ENUM.INFO, "", "Processed this many deals: " + iNumRows, "");
					} 
	 
				} // END  if (bOkToProceed == true)
				
				tUserTable = LIB.destroyTable(tUserTable);
			} // END 			if (bOkToProceed == true) 
			 
			LIB.viewTable(tErrors, "Deal Booking Errors");
			
		} catch (Exception e) {
			LIB.log("doEverything", e, className);
		}
		tErrors = LIB.destroyTable(tErrors);
		return bDoFailThisFunction;
	}
	
	public void processOneDeal(Table tUserTable, int iCounter, int iNumDeals, boolean bReadOnly, int iToday, 
			Table tTemplates, Table tErrors, int iRunNumber, String className) throws OException {
		
		try {

			String sDealNum = LIB.safeGetString(tUserTable, "source_system_deal", iCounter);
			
			String sMessage = "Loop " + iCounter + " of " + iNumDeals + ", Deal: " + sDealNum;
			LIB.log(sMessage, className);

			Table tOneDealErrors = Util.NULL_TABLE;
			try {
				 tOneDealErrors = tErrors.cloneTable();
				 String sTitle = "Errors for " + sDealNum;
				 tOneDealErrors.setTableTitle(sTitle);
				 tOneDealErrors.setTableName(sTitle);
			} catch (Exception e) {
				// do not log this
			}  
 			
			Table tOneDealSourceData = FUNC.STEP_1_SOURCE_DEAL.getDealTableFromUserTable(sDealNum, iToday, iRunNumber, className);
			int iUniqueDbIdNumber = LIB.safeGetInt(tOneDealSourceData, COLS.SOURCE_DEAL.user_table_unique_id, CONST_VALUES.ROW_TO_GET_OR_SET);
			String sSourceSystem = LIB.safeGetString(tOneDealSourceData, COLS.SOURCE_DEAL.source_system, CONST_VALUES.ROW_TO_GET_OR_SET);
 			
			FUNC.STEP_4_BOOK_DEAL.bookDeal(iCounter, iNumDeals, sDealNum, tOneDealSourceData, tTemplates, tOneDealErrors, bReadOnly, 
					sSourceSystem, iUniqueDbIdNumber, iRunNumber, className);

			try {
				if (LIB.safeGetNumRows(tOneDealErrors) >= 1) {
					tOneDealErrors.copyRowAddAllByColName(tErrors);
				}
			} catch (Exception e) {
				// do not log this
			}
 			
			tOneDealErrors = LIB.destroyTable(tOneDealErrors);
 			
		} catch (Exception e) {
			LIB.log("processOneDeal", e, className);
		}
	}

public static class FUNC {


	public static class CHECK {
		
		public static boolean checkForTemplate(Table tErrors, int iRunNumber, String className) throws OException {
			
			Table tData = Util.NULL_TABLE;
			boolean bDoFail = false;
			try {
				// Get templates from the database
				tData = Table.tableNew();
				LIB.safeAddCol(tData, COLS.TEMPLATES.tran_num, COL_TYPE_ENUM.COL_INT);
				LIB.safeAddCol(tData, COLS.TEMPLATES.template_name, COL_TYPE_ENUM.COL_STRING);
				
				String sSQL = "select tran_num, reference from ab_tran WHERE tran_status = 15 AND base_ins_type = 48010";
				LIB.execISql(tData, sSQL, false, className);
				
				int iNumRows = LIB.safeGetNumRows(tData);
				
				for (DEAL_BOOKING_TEMPLATE template: DEAL_BOOKING_TEMPLATE.values()) {
					
					if (template.getIsRequiredFlag() == true) {
						String sTemplateEnum = template.strValue();
						boolean bFoundFlag = false;
						
						for (int iCounter = 1; iCounter <= iNumRows; iCounter++) {
							String sTemplateFromDb = LIB.safeGetString(tData, COLS.TEMPLATES.template_name, iCounter);
							int iTemplateTranNum = LIB.safeGetInt(tData, COLS.TEMPLATES.tran_num, iCounter);
							if (Str.iEqual(sTemplateEnum, sTemplateFromDb) == CONST_VALUES.VALUE_OF_TRUE) {
								bFoundFlag = true;
								String sMessage = "Found (required) Deal Template " + "'" + sTemplateEnum + "'" + ", Tran Num: " + iTemplateTranNum;
								DEBUG_LOGFILE.logToFile(sMessage, iRunNumber, className);
							}
						}
						
						if (bFoundFlag == false) {
							bDoFail = true;
							String sMessage = "ERROR: Did not find (required) Deal Template";
							FUNC.ERROR_HANDLING.addErrorToTable(tErrors, ERROR_TYPE_ENUM.MISSING_TEMPLATE, "", sMessage, sTemplateEnum);
							sMessage = sMessage + ", Missing Deal Template " + "'" + sTemplateEnum + "'";
							DEBUG_LOGFILE.logToFile(sMessage, iRunNumber, className);
						}
					}
				}
				
			} catch (Exception e) {
				LIB.log("checkForTemplate", e, className);
			}
			tData = LIB.destroyTable(tData);
			return bDoFail;
		}
		

		public static boolean checkForTranInfoType(Table tErrors, int iRunNumber, String className) throws OException {
			
			Table tData = Util.NULL_TABLE;
			boolean bDoFail = false;
			try {
				// Get templates from the database
				tData = Table.tableNew();
				LIB.safeAddCol(tData, "type_id", COL_TYPE_ENUM.COL_INT);
				LIB.safeAddCol(tData, "type_name", COL_TYPE_ENUM.COL_STRING);
				
				String sSQL = "select type_id, type_name from tran_info_types";
				LIB.execISql(tData, sSQL, false, className);
				
				int iNumRows = LIB.safeGetNumRows(tData);
				
				for (TRAN_INFO tranInfoTypeName: TRAN_INFO.values()) {
					
					if (tranInfoTypeName.getIsRequiredFlag() == true) {
						String sTranInfoTypeNameEnum = tranInfoTypeName.strValue();
						boolean bFoundFlag = false;
						
						for (int iCounter = 1; iCounter <= iNumRows; iCounter++) {
							String sTranInfoTypeNameFromDb = LIB.safeGetString(tData, "type_name", iCounter);
							int iTranInfoTypeID = LIB.safeGetInt(tData, "type_id", iCounter);
							if (Str.iEqual(sTranInfoTypeNameEnum, sTranInfoTypeNameFromDb) == CONST_VALUES.VALUE_OF_TRUE) {
								bFoundFlag = true;
								String sMessage = "Found (required) TranInfo " + "'" + sTranInfoTypeNameEnum + "'" + ", Type ID: " + iTranInfoTypeID;
								DEBUG_LOGFILE.logToFile(sMessage, iRunNumber, className);
							}
						}
						
						if (bFoundFlag == false) {
							bDoFail = true;
							String sMessage = "ERROR: Did not find (required) Tran Info";
							FUNC.ERROR_HANDLING.addErrorToTable(tErrors, ERROR_TYPE_ENUM.MISSING_TRAN_INFO, "", sMessage, sTranInfoTypeNameEnum);
							sMessage = sMessage + ", Missing Tran Info " + "'" + sTranInfoTypeNameEnum + "'";
							DEBUG_LOGFILE.logToFile(sMessage, iRunNumber, className);
						}
					}
				}
				
			} catch (Exception e) {
				LIB.log("checkForTranInfoType", e, className);
			}
			tData = LIB.destroyTable(tData);
			return bDoFail;
		}
		

		public static boolean checkForMissingUserTables(Table tErrors, int iRunNumber, String className) throws OException {
			
//			Table tData = Util.NULL_TABLE;
			boolean bDoFail = false;
			try {
//				// Get templates from the database
//				tData = Table.tableNew();
//				LIB.safeAddCol(tData, "type_id", COL_TYPE_ENUM.COL_INT);
//				LIB.safeAddCol(tData, "type_name", COL_TYPE_ENUM.COL_STRING);
//				
//				String sSQL = "select type_id, type_name from tran_info_types";
//				LIB.execISql(tData, sSQL, false, className);
				
		//		int iNumRows = LIB.safeGetNumRows(tData);
				
				for (USER_TABLES userTableName: USER_TABLES.values()) {
					
					
					boolean bTableExists = false;
					{
						try {
							// table name is functional... needed for DB Structure
							Table tData = Table.tableNew(userTableName.strValue());
							DBUserTable.structure(tData);
							if (LIB.safeGetNumCols(tData) >= 2) {
								bTableExists = true;
							}
							tData = LIB.destroyTable(tData);
						} catch (Exception e) {
							// do not log this
						}
					}
					
					if (bTableExists == false) {
						// get error or warning based on getIsRequiredFlag

						if (userTableName.getIsRequiredFlag() == false) {
							String sMessage = "WARNING: Did not find (optional) User Table";
							FUNC.ERROR_HANDLING.addErrorToTable(tErrors, ERROR_TYPE_ENUM.MISSING_USER_TABLE_WARN, "", sMessage, userTableName.strValue());
							sMessage = sMessage + ", Missing (optional) User Table " + "'" + userTableName.strValue() + "'";
							DEBUG_LOGFILE.logToFile(sMessage, iRunNumber, className);
						} 

						if (userTableName.getIsRequiredFlag() == true) {
							bDoFail = true;
							String sMessage = "ERROR: Did not find (required) User Table";
							FUNC.ERROR_HANDLING.addErrorToTable(tErrors, ERROR_TYPE_ENUM.MISSING_USER_TABLE, "", sMessage, userTableName.strValue());
							sMessage = sMessage + ", Missing User Table " + "'" + userTableName.strValue() + "'";
							DEBUG_LOGFILE.logToFile(sMessage, iRunNumber, className);
						} 

					} // if (bTableExists == false) 
				 
				}
				
			} catch (Exception e) {
				LIB.log("checkForMissingUserTables", e, className);
			}
		//	tData = LIB.destroyTable(tData);
			return bDoFail;
		}
		
		
		
	} // END class CHECK

	
	
	public static class ERROR_HANDLING {
		
		public static Table createErrorTable(int iRunNumber, String className) throws OException {
			
			Table tReturn = Util.NULL_TABLE;
			try {
				tReturn = Table.tableNew();
				// don't use this
				//tReturn.addCol("deal_num", COL_TYPE_ENUM.COL_STRING);
				LIB.safeAddCol(tReturn, COLS.ERROR.error_type, COL_TYPE_ENUM.COL_STRING);
				LIB.safeAddCol(tReturn, COLS.ERROR.deal_num, COL_TYPE_ENUM.COL_STRING);
				LIB.safeAddCol(tReturn, COLS.ERROR.message, COL_TYPE_ENUM.COL_STRING);
				LIB.safeAddCol(tReturn, COLS.ERROR.field_name, COL_TYPE_ENUM.COL_STRING);  
				
			} catch (Exception e) {
				// do not log this
			}
			return tReturn;
		}
	
		public static void addErrorToTable(Table tErrors, ERROR_TYPE_ENUM errorType, String sDealNum, String sErrorMessage, String sFieldName) throws OException {
			
 			try {
 				
 				if (Str.iEqual(sDealNum, "") == CONST_VALUES.ROW_TO_GET_OR_SET) {
 					sDealNum = "n/a";
 				}	
 				if (Str.iEqual(sFieldName, "") == CONST_VALUES.ROW_TO_GET_OR_SET) {
 					sFieldName = "n/a";
 				}
 				
				int iMaxRow = tErrors.addRow();
				LIB.safeSetString(tErrors, COLS.ERROR.error_type, iMaxRow, errorType.getDescription());
				LIB.safeSetString(tErrors, COLS.ERROR.deal_num, iMaxRow, sDealNum);
				LIB.safeSetString(tErrors, COLS.ERROR.message, iMaxRow, sErrorMessage);
				LIB.safeSetString(tErrors, COLS.ERROR.field_name, iMaxRow, sFieldName); 
				  
			} catch (Exception e) {
				// do not log this
			} 
		}
		
	} //END ERROR_HANDLING
	
	public static class STEP_1_SOURCE_DEAL {
	
		public static Table createDealTableFromUserTable(int iRunNumber, String className) throws OException {
			
			Table tReturn = Util.NULL_TABLE;
			try {
				tReturn = Table.tableNew();
				// don't use this
				//tReturn.addCol("deal_num", COL_TYPE_ENUM.COL_STRING);
				LIB.safeAddCol(tReturn, COLS.SOURCE_DEAL.deal_num, COL_TYPE_ENUM.COL_STRING);
				LIB.safeAddCol(tReturn, COLS.SOURCE_DEAL.template, COL_TYPE_ENUM.COL_STRING);
				LIB.safeAddCol(tReturn, COLS.SOURCE_DEAL.internal_bunit, COL_TYPE_ENUM.COL_STRING);
				LIB.safeAddCol(tReturn, COLS.SOURCE_DEAL.counterparty, COL_TYPE_ENUM.COL_STRING);
				LIB.safeAddCol(tReturn, COLS.SOURCE_DEAL.external_portfolio, COL_TYPE_ENUM.COL_STRING);
				LIB.safeAddCol(tReturn, COLS.SOURCE_DEAL.start_date, COL_TYPE_ENUM.COL_INT);
				LIB.safeAddCol(tReturn, COLS.SOURCE_DEAL.end_date, COL_TYPE_ENUM.COL_INT);
				LIB.safeAddCol(tReturn, COLS.SOURCE_DEAL.buy_sell, COL_TYPE_ENUM.COL_STRING);
				LIB.safeAddCol(tReturn, COLS.SOURCE_DEAL.quantity, COL_TYPE_ENUM.COL_DOUBLE);
				LIB.safeAddCol(tReturn, COLS.SOURCE_DEAL.price, COL_TYPE_ENUM.COL_DOUBLE);
				
				LIB.safeAddCol(tReturn, COLS.SOURCE_DEAL.trade_date, COL_TYPE_ENUM.COL_INT);
				
				LIB.safeAddCol(tReturn, COLS.SOURCE_DEAL.portfolio, COL_TYPE_ENUM.COL_STRING);
				LIB.safeAddCol(tReturn, COLS.SOURCE_DEAL.pipeline, COL_TYPE_ENUM.COL_STRING);
				LIB.safeAddCol(tReturn, COLS.SOURCE_DEAL.zone, COL_TYPE_ENUM.COL_STRING);
				LIB.safeAddCol(tReturn, COLS.SOURCE_DEAL.zone2, COL_TYPE_ENUM.COL_STRING);
				LIB.safeAddCol(tReturn, COLS.SOURCE_DEAL.location, COL_TYPE_ENUM.COL_STRING);
				LIB.safeAddCol(tReturn, COLS.SOURCE_DEAL.location2, COL_TYPE_ENUM.COL_STRING);
				LIB.safeAddCol(tReturn, COLS.SOURCE_DEAL.location3, COL_TYPE_ENUM.COL_STRING);
 				
				LIB.safeAddCol(tReturn, COLS.SOURCE_DEAL.trader, COL_TYPE_ENUM.COL_STRING);
				LIB.safeAddCol(tReturn, COLS.SOURCE_DEAL.fixed_float, COL_TYPE_ENUM.COL_STRING);
				LIB.safeAddCol(tReturn, COLS.SOURCE_DEAL.proj_index, COL_TYPE_ENUM.COL_STRING);
				LIB.safeAddCol(tReturn, COLS.SOURCE_DEAL.proj_index2, COL_TYPE_ENUM.COL_STRING);
				LIB.safeAddCol(tReturn, COLS.SOURCE_DEAL.spread, COL_TYPE_ENUM.COL_DOUBLE);

				LIB.safeAddCol(tReturn, COLS.SOURCE_DEAL.broker, COL_TYPE_ENUM.COL_STRING);
				LIB.safeAddCol(tReturn, COLS.SOURCE_DEAL.broker_fee, COL_TYPE_ENUM.COL_STRING);

				// For COMM-TRANS
				LIB.safeAddCol(tReturn, COLS.SOURCE_DEAL.variable_cost, COL_TYPE_ENUM.COL_DOUBLE);
				LIB.safeAddCol(tReturn, COLS.SOURCE_DEAL.variable_demand, COL_TYPE_ENUM.COL_DOUBLE);

				LIB.safeAddCol(tReturn, COLS.SOURCE_DEAL.user_table_unique_id, COL_TYPE_ENUM.COL_INT);
				LIB.safeAddCol(tReturn, COLS.SOURCE_DEAL.source_system, COL_TYPE_ENUM.COL_STRING);

				LIB.safeAddCol(tReturn, COLS.SOURCE_DEAL.tran_info_table, COL_TYPE_ENUM.COL_TABLE);
				LIB.safeAddCol(tReturn, COLS.SOURCE_DEAL.tranf_field_table, COL_TYPE_ENUM.COL_TABLE);
 				 
				tReturn.defaultFormat();
				LIB.safeSetColFormatAsDate(tReturn, COLS.SOURCE_DEAL.start_date);
				LIB.safeSetColFormatAsDate(tReturn, COLS.SOURCE_DEAL.end_date);
				LIB.safeSetColFormatAsDate(tReturn, COLS.SOURCE_DEAL.trade_date);
				
			} catch (Exception e) {
				// do not log this
			}
			return tReturn;
		}
	
		public static Table getDealTableFromUserTable(String sDealNum, int iToday, int iRunNumber, String className) throws OException {

			Table tReturn = Util.NULL_TABLE;
			Table tUserTable = Util.NULL_TABLE;
			try {
				tReturn = createDealTableFromUserTable(iRunNumber, className);
			
				tUserTable = Table.tableNew();
				String sSQL = "select * from " + USER_TABLE.user_di_staging + " WHERE source_system_deal = " + "'" + sDealNum + "'";
				LIB.execISql(tUserTable, sSQL, false, className);
				
				Table tOneDealData = tUserTable.getTableFromByteArray(COLS.USER_DEAL_STAGING_TABLE.the_blob, CONST_VALUES.ROW_TO_GET_OR_SET);
				
				int iUniqueIDNumber = LIB.safeGetInt(tUserTable, COLS.USER_DEAL_STAGING_TABLE.id_number, CONST_VALUES.ROW_TO_GET_OR_SET);
				String sSourceSystem = LIB.safeGetString(tUserTable, COLS.USER_DEAL_STAGING_TABLE.source_system, CONST_VALUES.ROW_TO_GET_OR_SET);
				try {
					if (Str.len(sSourceSystem) <= 1) {
						sSourceSystem = "UNKNOWN";
					}
				} catch (Exception e) {
					// do not log this
				} 
				
				// this col name is from the CSV file col name
				String sTemplate = LIB.safeGetStringNoError(tOneDealData, COLS.STANDARD_IMPORT_CVS.template, CONST_VALUES.ROW_TO_GET_OR_SET);

				try {
					if (Str.len(sTemplate) <= 1) {
						String sInsType = LIB.safeGetStringNoError(tOneDealData, "ins_type", CONST_VALUES.ROW_TO_GET_OR_SET);
						if (Str.iEqual(sInsType, "Fixed Swap") == CONST_VALUES.VALUE_OF_TRUE) {
							sTemplate = "Fixed Swap";
						}
					}
				} catch (Exception e) {
					// do not log this
				} 
 
				// this is functional... need to sort by end date so that we get the correct values
				try {
					String sGroupCols = COLS.STANDARD_IMPORT_CVS.EXTRA.int_end_date;
					tOneDealData.group(sGroupCols);
				} catch (Exception e) {
					LIB.log("Sorting the deal data by end date", e, className);
				}

				LIB.safeAddCol(tOneDealData, COLS.STANDARD_IMPORT_CVS.EXTRA.proj_index1_to_use, COL_TYPE_ENUM.COL_STRING);
				LIB.safeAddCol(tOneDealData, COLS.STANDARD_IMPORT_CVS.EXTRA.proj_index2_to_use, COL_TYPE_ENUM.COL_STRING);

				LIB.safeAddCol(tOneDealData, COLS.STANDARD_IMPORT_CVS.EXTRA.sorted_table_row_num, COL_TYPE_ENUM.COL_INT);
				tOneDealData.setColIncrementInt(COLS.STANDARD_IMPORT_CVS.EXTRA.sorted_table_row_num, 1, 1);
				
				int iNumRows = LIB.safeGetNumRows(tOneDealData);
				String sCounterparty = LIB.safeGetStringNoError(tOneDealData, COLS.STANDARD_IMPORT_CVS.counterparty, CONST_VALUES.ROW_TO_GET_OR_SET);
				String sBuySell = LIB.safeGetStringNoError(tOneDealData, COLS.STANDARD_IMPORT_CVS.buy_sell, CONST_VALUES.ROW_TO_GET_OR_SET);
				
				String sQuantity = "0";
				{
					// if 'quantity' not found, use volume instead, else position
					String sQuantityColumnName = COLS.STANDARD_IMPORT_CVS.quantity;
					if (tOneDealData.getColNum(sQuantityColumnName) < 1) {
						sQuantityColumnName = "volume";
					}
					if (tOneDealData.getColNum(sQuantityColumnName) < 1) {
						sQuantityColumnName = "position";
					}
					
					// this quantity is the first month quantity
					sQuantity = LIB.safeGetStringNoError(tOneDealData, sQuantityColumnName, CONST_VALUES.ROW_TO_GET_OR_SET);
  				}

				String sPrice = LIB.safeGetStringNoError(tOneDealData, COLS.STANDARD_IMPORT_CVS.price, CONST_VALUES.ROW_TO_GET_OR_SET);
				
				// for COMM-TRANS
				String sVariableCost = LIB.safeGetStringNoError(tOneDealData, COLS.STANDARD_IMPORT_CVS.variable_cost, CONST_VALUES.ROW_TO_GET_OR_SET);
				String sVariableDemand = LIB.safeGetStringNoError(tOneDealData, COLS.STANDARD_IMPORT_CVS.variable_demand, CONST_VALUES.ROW_TO_GET_OR_SET);
				  
				String sStartDate = LIB.safeGetStringNoError(tOneDealData, COLS.STANDARD_IMPORT_CVS.start_date, CONST_VALUES.ROW_TO_GET_OR_SET);

				// for End Date.. need to take the end date of the last row
				String sEndDate = LIB.safeGetStringNoError(tOneDealData, COLS.STANDARD_IMPORT_CVS.end_date, iNumRows);
				
 				String sPortfolio = LIB.safeGetStringNoError(tOneDealData, COLS.STANDARD_IMPORT_CVS.portfolio, CONST_VALUES.ROW_TO_GET_OR_SET);
				String sPipeline = LIB.safeGetStringNoError(tOneDealData, COLS.STANDARD_IMPORT_CVS.pipeline, CONST_VALUES.ROW_TO_GET_OR_SET);
				String sZone = LIB.safeGetStringNoError(tOneDealData, COLS.STANDARD_IMPORT_CVS.zone, CONST_VALUES.ROW_TO_GET_OR_SET);
				String sZone2 = LIB.safeGetStringNoError(tOneDealData, COLS.STANDARD_IMPORT_CVS.zone2, CONST_VALUES.ROW_TO_GET_OR_SET);

 				String sInternalBunit = LIB.safeGetStringNoError(tOneDealData, COLS.STANDARD_IMPORT_CVS.internal_bunit, CONST_VALUES.ROW_TO_GET_OR_SET);
 				String sExternalPortfolio = LIB.safeGetStringNoError(tOneDealData, COLS.STANDARD_IMPORT_CVS.external_portfolio, CONST_VALUES.ROW_TO_GET_OR_SET);
 
				
				String sLocation = LIB.safeGetStringNoError(tOneDealData, COLS.STANDARD_IMPORT_CVS.location, CONST_VALUES.ROW_TO_GET_OR_SET);
				String sLocation2 = LIB.safeGetStringNoError(tOneDealData, COLS.STANDARD_IMPORT_CVS.location2, CONST_VALUES.ROW_TO_GET_OR_SET);
				String sLocation3 = LIB.safeGetStringNoError(tOneDealData, COLS.STANDARD_IMPORT_CVS.location3, CONST_VALUES.ROW_TO_GET_OR_SET);
				
				String sTradeDate = LIB.safeGetStringNoError(tOneDealData, COLS.STANDARD_IMPORT_CVS.trade_date, CONST_VALUES.ROW_TO_GET_OR_SET);
  				
				String sTrader = LIB.safeGetStringNoError(tOneDealData, COLS.STANDARD_IMPORT_CVS.trader, CONST_VALUES.ROW_TO_GET_OR_SET);
				String sFixedFloat = LIB.safeGetStringNoError(tOneDealData, COLS.STANDARD_IMPORT_CVS.fixed_float, CONST_VALUES.ROW_TO_GET_OR_SET);
				
				// project index can be blank in some rows.. so pick a non-blank row
				String sProjIndex = LIB.safeGetStringNoError(tOneDealData, COLS.STANDARD_IMPORT_CVS.proj_index, CONST_VALUES.ROW_TO_GET_OR_SET);
				String sProjIndex2 = LIB.safeGetStringNoError(tOneDealData, COLS.STANDARD_IMPORT_CVS.proj_index2, CONST_VALUES.ROW_TO_GET_OR_SET); 
				
				if (iNumRows >= 1) {
					 for (int iCounter = 1; iCounter <= iNumRows; iCounter++) {
						 String sProjIndexTemp = LIB.safeGetStringNoError(tOneDealData, COLS.STANDARD_IMPORT_CVS.proj_index, iCounter);
						 if (sProjIndexTemp == null) {
							 sProjIndexTemp = "";
						 }
						 if (Str.len(sProjIndexTemp) >= 2) {
							 sProjIndex = sProjIndexTemp;
							 break;
						 }
					 }
					 for (int iCounter = 1; iCounter <= iNumRows; iCounter++) {
						 String sProjIndexTemp = LIB.safeGetStringNoError(tOneDealData, COLS.STANDARD_IMPORT_CVS.proj_index2, iCounter);
						 if (sProjIndexTemp == null) {
							 sProjIndexTemp = "";
						 }
						 if (Str.len(sProjIndexTemp) >= 2) {
							 sProjIndex2 = sProjIndexTemp;
							 break;
						 }
					 }
				}
				// set back into the main table
				LIB.safeSetColValString(tOneDealData, COLS.STANDARD_IMPORT_CVS.proj_index, sProjIndex);
				LIB.safeSetColValString(tOneDealData, COLS.STANDARD_IMPORT_CVS.proj_index2, sProjIndex2);
				
				String sSpread = LIB.safeGetStringNoError(tOneDealData, COLS.STANDARD_IMPORT_CVS.spread, CONST_VALUES.ROW_TO_GET_OR_SET);

				String sBroker = LIB.safeGetStringNoError(tOneDealData, COLS.STANDARD_IMPORT_CVS.broker, CONST_VALUES.ROW_TO_GET_OR_SET);
				String sBrokerFee = LIB.safeGetStringNoError(tOneDealData, COLS.STANDARD_IMPORT_CVS.broker_fee, CONST_VALUES.ROW_TO_GET_OR_SET); 
				
				double dQuantity = 0;
				try {
					dQuantity = Str.strToDouble(sQuantity);
				} catch (Exception e) {
					// do not log this
				} 
				double dPrice = 0;
				try {
					dPrice = Str.strToDouble(sPrice);
				} catch (Exception e) {
					// do not log this
				} 

				double dVariableCost = 0;
				try {
					dVariableCost = Str.strToDouble(sVariableCost);
				} catch (Exception e) {
					// do not log this
				} 

				double dVariableDemand = 0;
				try {
					dVariableDemand = Str.strToDouble(sVariableDemand);
				} catch (Exception e) {
					// do not log this
				} 
  				
				double dSpread = 0;
				try {
					dSpread = Str.strToDouble(sSpread);
				} catch (Exception e) {
					// do not log this
				} 
				
				// if not with a 'S' or 's', then make it a Buy else a Sell
				try {
					if (Str.containsSubString(Str.toUpper(sBuySell), "S") == CONST_VALUES.VALUE_OF_TRUE) {
						sBuySell = "Sell";
					} else {
						sBuySell = "Buy";
					}
				} catch (Exception e) {
					// do not log this
				} 

				{
					int iMaxRow = tReturn.addRow();
					LIB.safeSetString(tReturn, COLS.SOURCE_DEAL.deal_num, iMaxRow, sDealNum);

					LIB.safeSetString(tReturn, COLS.SOURCE_DEAL.template, iMaxRow, sTemplate);

					LIB.safeSetString(tReturn, COLS.SOURCE_DEAL.counterparty, iMaxRow, sCounterparty);
					LIB.safeSetString(tReturn, COLS.SOURCE_DEAL.buy_sell, iMaxRow, sBuySell);
		
		 			int iStartDate = OCalendar.parseString(sStartDate);
					LIB.safeSetInt(tReturn, COLS.SOURCE_DEAL.start_date, iMaxRow, iStartDate);
		
					int iEndDate = OCalendar.parseString(sEndDate);
					LIB.safeSetInt(tReturn, COLS.SOURCE_DEAL.end_date, iMaxRow, iEndDate);

					int iTradeDate = iToday;
					try {
						if (Str.len(sTradeDate) > 1) {
							iTradeDate = OCalendar.parseString(sTradeDate);
						}
					} catch (Exception e) {
						// do not log this
					}
					if (iTradeDate < 1) {
						iTradeDate = iToday;
					}
					LIB.safeSetInt(tReturn, COLS.SOURCE_DEAL.trade_date, iMaxRow, iTradeDate);
 	
					
					LIB.safeSetString(tReturn, COLS.SOURCE_DEAL.internal_bunit, iMaxRow, sInternalBunit);
					LIB.safeSetString(tReturn, COLS.SOURCE_DEAL.external_portfolio, iMaxRow, sExternalPortfolio);
					
					LIB.safeSetString(tReturn, COLS.SOURCE_DEAL.portfolio, iMaxRow, sPortfolio);
					LIB.safeSetString(tReturn, COLS.SOURCE_DEAL.pipeline, iMaxRow, sPipeline);
					LIB.safeSetString(tReturn, COLS.SOURCE_DEAL.zone, iMaxRow, sZone);
					LIB.safeSetString(tReturn, COLS.SOURCE_DEAL.zone2, iMaxRow, sZone2);
					LIB.safeSetString(tReturn, COLS.SOURCE_DEAL.location, iMaxRow, sLocation);
					LIB.safeSetString(tReturn, COLS.SOURCE_DEAL.location2, iMaxRow, sLocation2);
					LIB.safeSetString(tReturn, COLS.SOURCE_DEAL.location3, iMaxRow, sLocation3);
					
					 

 					LIB.safeSetDouble(tReturn, COLS.SOURCE_DEAL.quantity, iMaxRow, dQuantity);
					LIB.safeSetDouble(tReturn, COLS.SOURCE_DEAL.price, iMaxRow, dPrice);

					// For COMM-TRANS
					LIB.safeSetDouble(tReturn, COLS.SOURCE_DEAL.variable_cost, iMaxRow, dVariableCost);
					LIB.safeSetDouble(tReturn, COLS.SOURCE_DEAL.variable_demand, iMaxRow, dVariableDemand);

					
					LIB.safeSetDouble(tReturn, COLS.SOURCE_DEAL.spread, iMaxRow, dSpread);
 
					LIB.safeSetInt(tReturn, COLS.SOURCE_DEAL.user_table_unique_id, iMaxRow, iUniqueIDNumber);
					LIB.safeSetString(tReturn, COLS.SOURCE_DEAL.source_system, iMaxRow, sSourceSystem);

					LIB.safeSetString(tReturn, COLS.SOURCE_DEAL.trader, iMaxRow, sTrader);
					LIB.safeSetString(tReturn, COLS.SOURCE_DEAL.fixed_float, iMaxRow, sFixedFloat);
					LIB.safeSetString(tReturn, COLS.SOURCE_DEAL.proj_index, iMaxRow, sProjIndex);
					LIB.safeSetString(tReturn, COLS.SOURCE_DEAL.proj_index2, iMaxRow, sProjIndex2);
					

					LIB.safeSetString(tReturn, COLS.SOURCE_DEAL.broker, iMaxRow, sBroker);
					LIB.safeSetString(tReturn, COLS.SOURCE_DEAL.broker_fee, iMaxRow, sBrokerFee);
					

					 
					{
						Table tExtraTranInfo = Table.tableNew("Extra Tran Info: " + sDealNum);
						LIB.safeAddCol(tExtraTranInfo, COLS.TRAN_INFO_EXTRA_FIELDS.tran_info_name, COL_TYPE_ENUM.COL_STRING);
						LIB.safeAddCol(tExtraTranInfo, COLS.TRAN_INFO_EXTRA_FIELDS.value, COL_TYPE_ENUM.COL_STRING);

						final int LOCATION_OF_FIRST_CHAR_STARTS_AT_ZERO = 0;
						final String TRAN_INFO_COL_PREFACE = "TRAN_INFO_";
						int iNumCols = LIB.safeGetNumCols(tOneDealData);
						for (int iColCounter = 1; iColCounter <= iNumCols; iColCounter++) {
							
							try {
								String sColName = tOneDealData.getColName(iColCounter);
								
								int iLocationOfTranInfo = Str.findSubString(sColName, TRAN_INFO_COL_PREFACE);
								if (iLocationOfTranInfo == LOCATION_OF_FIRST_CHAR_STARTS_AT_ZERO) {
									int iLen = Str.len(sColName);
									int iStartChar = Str.len(TRAN_INFO_COL_PREFACE);
									int iCharsToGet = iLen - iStartChar; 
									String sTranInfoName = Str.substr(sColName, iStartChar, iCharsToGet);
									
									String sValue = tOneDealData.getString(iColCounter, CONST_VALUES.ROW_TO_GET_OR_SET);
									
									int iSubMaxRow = tExtraTranInfo.addRow();
									LIB.safeSetString(tExtraTranInfo, COLS.TRAN_INFO_EXTRA_FIELDS.tran_info_name, iSubMaxRow, sTranInfoName);
									LIB.safeSetString(tExtraTranInfo, COLS.TRAN_INFO_EXTRA_FIELDS.value, iSubMaxRow, sValue);
								}
							} catch (Exception e) {
								// do not log this
							}
							
						}
						 
						LIB.safeSetTable(tReturn, COLS.SOURCE_DEAL.tran_info_table, iMaxRow, tExtraTranInfo);
					}
  					
					{
						Table tExtraTranfFieldInfo = Table.tableNew("Extra TranF Fields: " + sDealNum);
						LIB.safeAddCol(tExtraTranfFieldInfo, COLS.TRANF_FIELD_EXTRA_FIELDS.tran_field_name, COL_TYPE_ENUM.COL_STRING);
						LIB.safeAddCol(tExtraTranfFieldInfo, COLS.TRANF_FIELD_EXTRA_FIELDS.side, COL_TYPE_ENUM.COL_INT);
						LIB.safeAddCol(tExtraTranfFieldInfo, COLS.TRANF_FIELD_EXTRA_FIELDS.seq_num2, COL_TYPE_ENUM.COL_INT);
						LIB.safeAddCol(tExtraTranfFieldInfo, COLS.TRANF_FIELD_EXTRA_FIELDS.value, COL_TYPE_ENUM.COL_STRING);
						  
						
						// To qualify, the field needs
						// 1) to start with TRANF_
						// 2) to have a period in it to have the side
						// e.g., TRANF_REFERENCE.0.   
						// even fields for side zero *need* a side
						
						final int LOCATION_OF_FIRST_CHAR_STARTS_AT_ZERO = 0;
						final String TRANF_FIELD_COL_PREFACE = "TRANF_";
						final String PERIOD = ".";
						int iNumCols = LIB.safeGetNumCols(tOneDealData);
						for (int iColCounter = 1; iColCounter <= iNumCols; iColCounter++) {
							
							try {
								String sColName = tOneDealData.getColName(iColCounter);
								 
								int iLocationOfTranfFieldIndicator = Str.findSubString(sColName, TRANF_FIELD_COL_PREFACE);
								if (iLocationOfTranfFieldIndicator == LOCATION_OF_FIRST_CHAR_STARTS_AT_ZERO) {
									
									int iLocationOfPeriod = Str.findSubString(sColName, PERIOD);
									if (iLocationOfPeriod >= 1) {
 
										int iStartChar = 0;
										int iCharsToGet = iLocationOfPeriod; 
										String sTranfFieldName = Str.substr(sColName, iStartChar, iCharsToGet);

										// We support two levels:  Side and then SeqNum2, which is usually 'Profile' table
										int iSide = 0;
										// default this to -1
										int iSeqNum2 = -1;
										
										try {
											int iLen = Str.len(sColName);
											iStartChar = iLocationOfPeriod + 1;
											iCharsToGet = iLen - iLocationOfPeriod - 1;

//											LIB.log("sColName value: " + sColName, className);
//											LIB.log("iStartChar value: " + iStartChar, className);
//											LIB.log("iCharsToGet value: " + iCharsToGet, className);
 											
											String sRemainingStringValue = Str.substr(sColName, iStartChar, iCharsToGet);
 											
//											LIB.log("Remaining value: " + sRemainingStringValue, className);
//											LIB.log("Remaining value: " + sRemainingStringValue, className);
//											LIB.log("Remaining value: " + sRemainingStringValue, className);
										 
											// this works even if there is a period.
											// e.g., a value of '2.3' will converted to the integer '2'
											int iValue = Str.strToInt(sRemainingStringValue);
											if (iValue >= 0) {
												iSide = iValue;
											}
//											LIB.log("iSide: " + iSide, className);
 
											
											// Now check to see if the sRemainingStringValue has a period
											// so that we can extract out the iSeqNum2, if needed.
											iLocationOfPeriod = Str.findSubString(sRemainingStringValue, PERIOD);
											if (iLocationOfPeriod >= 1) {
												
												iLen = Str.len(sRemainingStringValue);
												iStartChar = iLocationOfPeriod + 1;
												iCharsToGet = iLen - iLocationOfPeriod - 1;

//												LIB.log("sRemainingStringValue value: " + sColName, className);
//												LIB.log("iStartChar value: " + iStartChar, className);
//												LIB.log("iCharsToGet value: " + iCharsToGet, className);
	 											
												String sFinalRemainingStringValue = Str.substr(sRemainingStringValue, iStartChar, iCharsToGet);
		 										
//												LIB.log("sFinalRemainingStringValue value: " + sFinalRemainingStringValue, className);
												
												// this works even if there is a period.
												// e.g., a value of '2.3' will converted to the integer '2'
												iValue = Str.strToInt(sFinalRemainingStringValue);
												if (iValue >= 0) {
													iSeqNum2 = iValue;
												}
											}
											 
										} catch (Exception e) {
											// do not log this
										}
										 
 										String sValue = tOneDealData.getString(iColCounter, CONST_VALUES.ROW_TO_GET_OR_SET);
										
 										// For Tran Info extra fields... treat space like an empty string
 										if (Str.iEqual(sValue, " ") == CONST_VALUES.VALUE_OF_TRUE) {
											sValue = "";
										}
										
 										int iSubMaxRow = tExtraTranfFieldInfo.addRow();
										LIB.safeSetString(tExtraTranfFieldInfo, COLS.TRANF_FIELD_EXTRA_FIELDS.tran_field_name, iSubMaxRow, sTranfFieldName);
										LIB.safeSetInt(tExtraTranfFieldInfo, COLS.TRANF_FIELD_EXTRA_FIELDS.side, iSubMaxRow, iSide);
										LIB.safeSetInt(tExtraTranfFieldInfo, COLS.TRANF_FIELD_EXTRA_FIELDS.seq_num2, iSubMaxRow, iSeqNum2);
										LIB.safeSetString(tExtraTranfFieldInfo, COLS.TRANF_FIELD_EXTRA_FIELDS.value, iSubMaxRow, sValue);
									}
								}
							} catch (Exception e) {
								// do not log this
							}
						}
						 
						LIB.safeSetTable(tReturn, COLS.SOURCE_DEAL.tranf_field_table, iMaxRow, tExtraTranfFieldInfo);
					}
					
 
				}
  				  
			} catch (Exception e) {
				// do not log this
			}
			tUserTable = LIB.destroyTable(tUserTable);
			
			return tReturn;
		}
	} //END STEP_1_SOURCE_DEAL

	public static class STEP_2_MAPPING {
		
		public static Table applyMappingToDeal(Table tOneDeal, int iRunNumber, String className) throws OException {
			
			Table tReturn = Util.NULL_TABLE;

			// TODO
			// Redo this to make it more efficient
 
			try {
				// copy the source deal data
				tReturn = tOneDeal.copyTable();

				{
					// Get Original
					String sCounterParty = LIB.safeGetString(tReturn, COLS.MAPPED_DEAL.counterparty, CONST_VALUES.ROW_TO_GET_OR_SET);
					// Default to be the counterparty name (unmapped)
					String sMapppedCounterparty = sCounterParty;
					
					// Redo this to make it more efficient
					{
						Table tTemp = Table.tableNew();
						LIB.safeAddCol(tTemp, "value", COL_TYPE_ENUM.COL_STRING);
						String sSQL = "select mapped_value from user_di_mapping_counterparty WHERE lower(original_value) = lower('" + sCounterParty + "')";
						LIB.execISql(tTemp, sSQL, false, className);
						if (LIB.safeGetNumRows(tTemp) >= 1) {
							sMapppedCounterparty = LIB.safeGetString(tTemp, "value", CONST_VALUES.ROW_TO_GET_OR_SET);
						}
						tTemp = LIB.destroyTable(tTemp);
					}
					
					LIB.safeSetString(tReturn, COLS.SOURCE_DEAL.counterparty, CONST_VALUES.ROW_TO_GET_OR_SET, sMapppedCounterparty);

  				}

 				{
					// Get Original
					String sLocation = LIB.safeGetString(tReturn, COLS.MAPPED_DEAL.location, CONST_VALUES.ROW_TO_GET_OR_SET);
					// Default to be the location name (unmapped)
					String sMapppedLocation = sLocation;
					
					Table tTemp = Table.tableNew();
					LIB.safeAddCol(tTemp, "value", COL_TYPE_ENUM.COL_STRING);
					LIB.safeAddCol(tTemp, "pipeline", COL_TYPE_ENUM.COL_STRING);
					String sSQL = "select mapped_value, extra_string1 from user_di_mapping_location WHERE lower(original_value) = lower('" + sLocation + "')";
					LIB.execISql(tTemp, sSQL, false, className);
					if (LIB.safeGetNumRows(tTemp) >= 1) {
						sMapppedLocation = LIB.safeGetString(tTemp, "value", CONST_VALUES.ROW_TO_GET_OR_SET);
					
						String sPipeline = LIB.safeGetString(tTemp, "pipeline", CONST_VALUES.ROW_TO_GET_OR_SET);
						
						if (sPipeline == null || Str.iEqual(sPipeline, " ") == CONST_VALUES.VALUE_OF_TRUE) {
							sPipeline = "";
						}
						// TODO
						// revisit this at some point
						if (Str.len(sPipeline) >= 1) { 
							LIB.safeSetString(tReturn, COLS.SOURCE_DEAL.pipeline, CONST_VALUES.ROW_TO_GET_OR_SET, sPipeline);
							LIB.safeSetString(tReturn, COLS.SOURCE_DEAL.zone, CONST_VALUES.ROW_TO_GET_OR_SET, "NULL");
							LIB.safeSetString(tReturn, COLS.SOURCE_DEAL.zone2, CONST_VALUES.ROW_TO_GET_OR_SET, "NULL");
						}
								
					}
					tTemp = LIB.destroyTable(tTemp);
					
					LIB.safeSetString(tReturn, COLS.SOURCE_DEAL.location, CONST_VALUES.ROW_TO_GET_OR_SET, sMapppedLocation);
				}
 				
				{
					
					// Get Original for Location2
					String sLocation2 = LIB.safeGetString(tReturn, COLS.MAPPED_DEAL.location2, CONST_VALUES.ROW_TO_GET_OR_SET);
					// Default to be the location name (unmapped)
					String sMapppedLocation2 = sLocation2;
					
					Table tTemp = Table.tableNew();
					LIB.safeAddCol(tTemp, "value", COL_TYPE_ENUM.COL_STRING);
					String sSQL = "select mapped_value from user_di_mapping_location WHERE lower(original_value) = lower('" + sLocation2 + "')";
					LIB.execISql(tTemp, sSQL, false, className);
					if (LIB.safeGetNumRows(tTemp) >= 1) {
						sMapppedLocation2 = LIB.safeGetString(tTemp, "value", CONST_VALUES.ROW_TO_GET_OR_SET);
					}
					tTemp = LIB.destroyTable(tTemp);
					
					LIB.safeSetString(tReturn, COLS.SOURCE_DEAL.location2, CONST_VALUES.ROW_TO_GET_OR_SET, sMapppedLocation2);
 				}
  				
				{
					
					// Get Original for Location3
					String sLocation3 = LIB.safeGetString(tReturn, COLS.MAPPED_DEAL.location3, CONST_VALUES.ROW_TO_GET_OR_SET);
					// Default to be the location name (unmapped)
					String sMapppedLocation3 = sLocation3;
					
					Table tTemp = Table.tableNew();
					LIB.safeAddCol(tTemp, "value", COL_TYPE_ENUM.COL_STRING);
					String sSQL = "select mapped_value from user_di_mapping_location WHERE lower(original_value) = lower('" + sLocation3 + "')";
					LIB.execISql(tTemp, sSQL, false, className);
					if (LIB.safeGetNumRows(tTemp) >= 1) {
						sMapppedLocation3 = LIB.safeGetString(tTemp, "value", CONST_VALUES.ROW_TO_GET_OR_SET);
					}
					tTemp = LIB.destroyTable(tTemp);
					
					LIB.safeSetString(tReturn, COLS.SOURCE_DEAL.location3, CONST_VALUES.ROW_TO_GET_OR_SET, sMapppedLocation3);
 				}
 
				// get pipeline and/or zone name if blank
				// for Location 1
				{
					String sMapppedLocation = LIB.safeGetString(tReturn, COLS.SOURCE_DEAL.location, CONST_VALUES.ROW_TO_GET_OR_SET);

					if (Str.len(sMapppedLocation) >= 1) {
						
					String sPipeline = LIB.safeGetString(tReturn, COLS.SOURCE_DEAL.pipeline, CONST_VALUES.ROW_TO_GET_OR_SET);
					if (sPipeline == null || Str.iEqual(sPipeline, " ") == CONST_VALUES.VALUE_OF_TRUE) {
						sPipeline = "";
					}
					String sZone = LIB.safeGetString(tReturn, COLS.SOURCE_DEAL.zone, CONST_VALUES.ROW_TO_GET_OR_SET);
					if (sZone == null || Str.iEqual(sZone, " ") == CONST_VALUES.VALUE_OF_TRUE) {
						sZone = "";
					}
					
					boolean bPipelineIsBlank = false;
					boolean bZoneIsBlank = false;
					if (Str.len(sPipeline) < 1) {
						bPipelineIsBlank = true;
					}
					if (Str.len(sZone) < 1) {
						bZoneIsBlank = true;
					}

					if (bPipelineIsBlank == true || bZoneIsBlank == true) {
						Table tTemp = Table.tableNew();
						LIB.safeAddCol(tTemp, "pipeline_id", COL_TYPE_ENUM.COL_INT);
						LIB.safeAddCol(tTemp, "zone_id", COL_TYPE_ENUM.COL_INT);
						// calc the pipeline
						
						String sSQL = "select pipeline_id, zone_id from gas_phys_location WHERE location_name = " +  "'" + sMapppedLocation +  "'" ;
						LIB.execISql(tTemp, sSQL, false, className);
				  		
						// TODO
						tTemp.defaultFormat();
						//tTemp.viewTableForDebugging();
						//LIB.viewTable(tTemp, "table with pipeline data");
						
						// TODO, fix this up
						// take the first row.. 
						if (LIB.safeGetNumRows(tTemp) >= 1) {
							int iPipeline = LIB.safeGetInt(tTemp, "pipeline_id", CONST_VALUES.ROW_TO_GET_OR_SET);
							int iZone = LIB.safeGetInt(tTemp, "zone_id", CONST_VALUES.ROW_TO_GET_OR_SET);
							
							if (bPipelineIsBlank == true) {
								sPipeline = Ref.getName(SHM_USR_TABLES_ENUM.GAS_PHYS_PIPELINE_TABLE, iPipeline);
								LIB.safeSetString(tReturn, COLS.SOURCE_DEAL.pipeline, CONST_VALUES.ROW_TO_GET_OR_SET, sPipeline);
							}
							if (bZoneIsBlank == true) {
								sZone = Ref.getName(SHM_USR_TABLES_ENUM.GAS_PHYS_ZONE_TABLE, iZone);
								LIB.safeSetString(tReturn, COLS.SOURCE_DEAL.zone, CONST_VALUES.ROW_TO_GET_OR_SET, sZone);
							}
						}
						
						tTemp = LIB.destroyTable(tTemp);
					}
					
					} // END if (Str.len(sMapppedLocation) >= 1) {
 
				}
				
				// repeat code for Location 2
				{
 
					// get pipeline and/or zone name if blank
					{
						String sMapppedLocation2 = LIB.safeGetString(tReturn, COLS.SOURCE_DEAL.location2, CONST_VALUES.ROW_TO_GET_OR_SET);

						if (Str.len(sMapppedLocation2) >= 1) {

							String sZone2 = LIB.safeGetString(tReturn, COLS.SOURCE_DEAL.zone2, CONST_VALUES.ROW_TO_GET_OR_SET);
							if (sZone2 == null || Str.iEqual(sZone2, " ") == CONST_VALUES.VALUE_OF_TRUE) {
								sZone2 = "";
							}
							
							boolean bZoneIsBlank = false;
							if (Str.len(sZone2) < 1) {
								bZoneIsBlank = true;
							}

							if (bZoneIsBlank == true) {
								Table tTemp = Table.tableNew();
								LIB.safeAddCol(tTemp, "pipeline_id", COL_TYPE_ENUM.COL_INT);
								LIB.safeAddCol(tTemp, "zone_id", COL_TYPE_ENUM.COL_INT);
								
								// calc the pipeline and/or zone
								String sSQL = "select pipeline_id, zone_id from gas_phys_location WHERE location_name = " +  "'" + sMapppedLocation2 +  "'" ;
								
								LIB.execISql(tTemp, sSQL, false, className);
								  
								// take the first row.. should be just one
								if (LIB.safeGetNumRows(tTemp) > 0) {
								//	int iPipeline = LIB.safeGetInt(tTemp, "pipeline_id", CONST_VALUES.ROW_TO_GET_OR_SET);
									int iZone2 = LIB.safeGetInt(tTemp, "zone_id", CONST_VALUES.ROW_TO_GET_OR_SET);
									
									if (bZoneIsBlank == true) {
										sZone2 = Ref.getName(SHM_USR_TABLES_ENUM.GAS_PHYS_ZONE_TABLE, iZone2);
										LIB.safeSetString(tReturn, COLS.SOURCE_DEAL.zone2, CONST_VALUES.ROW_TO_GET_OR_SET, sZone2);
									}
								}
								
								tTemp = LIB.destroyTable(tTemp);
							}
						} // END if (Str.len(sMapppedLocation) >= 1) {
					}
				}

				{
					// Get Original
					String sCurveName = LIB.safeGetString(tReturn, COLS.MAPPED_DEAL.proj_index, CONST_VALUES.ROW_TO_GET_OR_SET);
					// Default to be the location name (unmapped)
					String sMapppedCurveName = sCurveName;

					// use 2 chars minimum for no particular reason
					if (Str.len(sCurveName) >= 2) {
						Table tTemp = Table.tableNew();
						LIB.safeAddCol(tTemp, "value", COL_TYPE_ENUM.COL_STRING);
						String sSQL = "select mapped_value from user_di_mapping_curve WHERE lower(original_value) = lower('" + sCurveName + "')";
						LIB.execISql(tTemp, sSQL, false, className);
						if (LIB.safeGetNumRows(tTemp) >= 1) {
							sMapppedCurveName = LIB.safeGetString(tTemp, "value", CONST_VALUES.ROW_TO_GET_OR_SET);
						}
						tTemp = LIB.destroyTable(tTemp);
						
						LIB.safeSetString(tReturn, COLS.SOURCE_DEAL.proj_index, CONST_VALUES.ROW_TO_GET_OR_SET, sMapppedCurveName);
					}
 				}
				
				{
					// Get Original
					String sCurveName = LIB.safeGetString(tReturn, COLS.MAPPED_DEAL.proj_index2, CONST_VALUES.ROW_TO_GET_OR_SET);
					// Default to be the location name (unmapped)
					String sMapppedCurveName = sCurveName;

					// use 2 chars minimum for no particular reason
					if (Str.len(sCurveName) >= 2) {
						Table tTemp = Table.tableNew();
						LIB.safeAddCol(tTemp, "value", COL_TYPE_ENUM.COL_STRING);
						String sSQL = "select mapped_value from user_di_mapping_curve WHERE lower(original_value) = lower('" + sCurveName + "')";
						LIB.execISql(tTemp, sSQL, false, className);
						if (LIB.safeGetNumRows(tTemp) >= 1) {
							sMapppedCurveName = LIB.safeGetString(tTemp, "value", CONST_VALUES.ROW_TO_GET_OR_SET);
						}
						tTemp = LIB.destroyTable(tTemp);
						
						LIB.safeSetString(tReturn, COLS.SOURCE_DEAL.proj_index2, CONST_VALUES.ROW_TO_GET_OR_SET, sMapppedCurveName);
					}
				}
  				
			} catch (Exception e) {
				// do not log this
			}
			return tReturn;
		}
	} // END STEP_2_MAPPING

	public static class STEP_3_CREATE_DEAL_IN_MEMORY {
 		
		public static class COMM_PHYS {
			
			public static void setCommPhysFields(Transaction tran, Table tOneDeal, Table tOneDealErrors, int iRunNumber, String className) throws OException {
				 
				try {
					 
					
					// Now start By Side fields
 					// for Commodity Toolset, the sides are 1 and 2
					int SIDE1 = 1;
	 				int SIDE2 = 2;
					int SIDE3 = 3;
 
	 				// Set these fields only for Commodity Toolset
	 				{
	 					{
	 						String sPipeline = LIB.safeGetString(tOneDeal, COLS.MAPPED_DEAL.pipeline, CONST_VALUES.ROW_TO_GET_OR_SET);
	 						if (Str.len(sPipeline) >= 1) {
	 							TRANF_HELPER.safeSetTranfFieldWithErrorHandling(tran, TRANF_FIELD.TRANF_PIPELINE, SIDE1, sPipeline, tOneDealErrors, className);
	 						}
	 					}
	 					{
	 						String sZone = LIB.safeGetString(tOneDeal, COLS.MAPPED_DEAL.zone, CONST_VALUES.ROW_TO_GET_OR_SET);
	 						if (Str.len(sZone) >= 1) {
	 	 						TRANF_HELPER.safeSetTranfFieldWithErrorHandling(tran, TRANF_FIELD.TRANF_ZONE, SIDE1, sZone, tOneDealErrors, className);
	 						}
	 					}
	 					{
	 						String sLocation = LIB.safeGetString(tOneDeal, COLS.MAPPED_DEAL.location, CONST_VALUES.ROW_TO_GET_OR_SET);
	 						if (Str.len(sLocation) >= 1) {
	 							TRANF_HELPER.safeSetTranfFieldWithErrorHandling(tran, TRANF_FIELD.TRANF_LOCATION, SIDE1, sLocation, tOneDealErrors, className);
	 						}
	 					}
	 					{
	 						String sLocation2 = LIB.safeGetString(tOneDeal, COLS.MAPPED_DEAL.location2, CONST_VALUES.ROW_TO_GET_OR_SET);
	 						if (Str.len(sLocation2) >= 1) {
	 							TRANF_HELPER.safeSetTranfFieldWithErrorHandling(tran, TRANF_FIELD.TRANF_LOCATION, SIDE3, sLocation2, tOneDealErrors, className);
	 						}
	 					}
	 				}  

					{
						double dQuantity = LIB.safeGetDouble(tOneDeal, COLS.MAPPED_DEAL.quantity, CONST_VALUES.ROW_TO_GET_OR_SET);
						
						String sQuantity = Str.doubleToStr(dQuantity); 
						TRANF_HELPER.safeSetTranfFieldWithErrorHandling(tran, TRANF_FIELD.TRANF_DAILY_VOLUME, SIDE1, sQuantity, tOneDealErrors, className);
					}

					{
						double dPrice = LIB.safeGetDouble(tOneDeal, COLS.MAPPED_DEAL.price, CONST_VALUES.ROW_TO_GET_OR_SET);
						String sPrice = Str.doubleToStr(dPrice); 
						TRANF_HELPER.safeSetTranfFieldWithErrorHandling(tran, TRANF_FIELD.TRANF_RATE_SPD, SIDE2, sPrice, tOneDealErrors, className);
					}

					{
						int iStartDate = LIB.safeGetInt(tOneDeal, COLS.SOURCE_DEAL.start_date, CONST_VALUES.ROW_TO_GET_OR_SET);
						String sStartDate = OCalendar.formatJd(iStartDate, DATE_FORMAT.DATE_FORMAT_DMLY_NOSLASH);
						TRANF_HELPER.safeSetTranfFieldWithErrorHandling(tran, TRANF_FIELD.TRANF_START_DATE, SIDE1, sStartDate, tOneDealErrors, className);
					}

					{
						int iEndDate = LIB.safeGetInt(tOneDeal, COLS.SOURCE_DEAL.end_date, CONST_VALUES.ROW_TO_GET_OR_SET);
						String sEndDate = OCalendar.formatJd(iEndDate, DATE_FORMAT.DATE_FORMAT_DMLY_NOSLASH);
						TRANF_HELPER.safeSetTranfFieldWithErrorHandling(tran, TRANF_FIELD.TRANF_MAT_DATE, SIDE1, sEndDate, tOneDealErrors, className);
					}
 					

					// Only Set Fixed/Float if Commodity
					{
						{
							String sFixedFloat = LIB.safeGetString(tOneDeal, COLS.MAPPED_DEAL.fixed_float, CONST_VALUES.ROW_TO_GET_OR_SET);
							if (Str.len(sFixedFloat) >= 1) {
								sFixedFloat = Str.toUpper(sFixedFloat);
								String sFixedFloatToSet = "Fixed";
								if (Str.containsSubString(sFixedFloat, "INDEX") == 1 || Str.containsSubString(sFixedFloat, "FLOAT") == 1) {
									sFixedFloatToSet = "Index";
								}
								TRANF_HELPER.safeSetTranfFieldWithErrorHandling(tran, TRANF_FIELD.TRANF_FX_FLT, SIDE2, sFixedFloatToSet, tOneDealErrors, className);
							}
						}
					}

	 
					{
						String sProjIndex = LIB.safeGetString(tOneDeal, COLS.MAPPED_DEAL.proj_index, CONST_VALUES.ROW_TO_GET_OR_SET);
						if (Str.len(sProjIndex) >= 1) {
							int iSideToSet = SIDE2;
	 						TRANF_HELPER.safeSetTranfFieldWithErrorHandling(tran, TRANF_FIELD.TRANF_PROJ_INDEX, iSideToSet, sProjIndex, tOneDealErrors, className);
						}
					}

					{
						double dSpread = LIB.safeGetDouble(tOneDeal, COLS.MAPPED_DEAL.spread, CONST_VALUES.ROW_TO_GET_OR_SET);
						String sSpread = Str.doubleToStr(dSpread); 
						
						// Spread can be in Spread or Price
						// use Spread until Price is non-zero
						double dPrice = LIB.safeGetDouble(tOneDeal, COLS.MAPPED_DEAL.price, CONST_VALUES.ROW_TO_GET_OR_SET);
						String sPrice = Str.doubleToStr(dPrice); 

						// default
						String sSpreadToSet = sSpread;
						if (Math.abs(dPrice) > 0.0000001) {
							sSpreadToSet = sPrice;
						}
						
						TRANF_HELPER.safeSetTranfFieldWithErrorHandling(tran, TRANF_FIELD.TRANF_RATE_SPD, SIDE2, sSpreadToSet, tOneDealErrors, className);
					}
	  
				} catch (Exception e) {
					LIB.log("COMM_PHYS.setCommPhysFields", e, className);
				}
			}
			
			
		} // END COMM_PHYS
		
		

public static class COMM_TRANS {
			
			public static void setCommTransFields(Transaction tran, Table tOneDeal, Table tOneDealErrors, int iRunNumber, String className) throws OException {
				 
				try {
					 
					int SIDE1 = 1;
	 				int SIDE2 = 2;
	 				int SIDE3 = 3;
 
	 				{
	 					final int SIDE_TO_SET = SIDE1;
	 					{
	 						String sPipeline = LIB.safeGetString(tOneDeal, COLS.MAPPED_DEAL.pipeline, CONST_VALUES.ROW_TO_GET_OR_SET);
	 						if (Str.len(sPipeline) >= 1) {
	 							TRANF_HELPER.safeSetTranfFieldWithErrorHandling(tran, TRANF_FIELD.TRANF_PIPELINE, SIDE_TO_SET, sPipeline, tOneDealErrors, className);
	 						}
	 					}
	 					{
	 						String sZone = LIB.safeGetString(tOneDeal, COLS.MAPPED_DEAL.zone, CONST_VALUES.ROW_TO_GET_OR_SET);
	 						if (Str.len(sZone) >= 1) {
	 	 						TRANF_HELPER.safeSetTranfFieldWithErrorHandling(tran, TRANF_FIELD.TRANF_ZONE, SIDE_TO_SET, sZone, tOneDealErrors, className);
	 						}
	 					}
	 					{
	 						String sLocation = LIB.safeGetString(tOneDeal, COLS.MAPPED_DEAL.location, CONST_VALUES.ROW_TO_GET_OR_SET);
	 						if (Str.len(sLocation) >= 1) {
	 							TRANF_HELPER.safeSetTranfFieldWithErrorHandling(tran, TRANF_FIELD.TRANF_LOCATION, SIDE_TO_SET, sLocation, tOneDealErrors, className);
	 						}
	 					}
	 				}  
	 				
	 				// first set Location 1
	 				// then set Location 2 (this one)
	 				// needed for COMM-TRANs
	 				{
	 					// Set these on SIDE3
	 					final int SIDE_TO_SET = SIDE3;

	 					{
	 						String sZone2 = LIB.safeGetString(tOneDeal, COLS.MAPPED_DEAL.zone2, CONST_VALUES.ROW_TO_GET_OR_SET);
	 						if (Str.len(sZone2) >= 1) {
	 	 						TRANF_HELPER.safeSetTranfFieldWithErrorHandling(tran, TRANF_FIELD.TRANF_ZONE, SIDE_TO_SET, sZone2, tOneDealErrors, className);
	 						}
	 					}
	 					
	 					// Needed for COMM-TRANS deals
	 					{
	 						String sLocation2 = LIB.safeGetString(tOneDeal, COLS.MAPPED_DEAL.location2, CONST_VALUES.ROW_TO_GET_OR_SET);
	 						if (Str.len(sLocation2) >= 1) {
	 							TRANF_HELPER.safeSetTranfFieldWithErrorHandling(tran, TRANF_FIELD.TRANF_LOCATION, SIDE_TO_SET, sLocation2, tOneDealErrors, className);
	 						}
	 					}
	 				}

	 				// Some of these are SIDE1 and others are SIDE2
					{
						double dQuantity = LIB.safeGetDouble(tOneDeal, COLS.MAPPED_DEAL.quantity, CONST_VALUES.ROW_TO_GET_OR_SET);
						
						String sQuantity = Str.doubleToStr(dQuantity); 
						TRANF_HELPER.safeSetTranfFieldWithErrorHandling(tran, TRANF_FIELD.TRANF_DAILY_VOLUME, SIDE1, sQuantity, tOneDealErrors, className);
					}

					{
						// if price is set then do *not* use variable cost, as that will overwrite it
						boolean bPriceColumnIsBeingUsed = false;
						
						double dPrice = LIB.safeGetDouble(tOneDeal, COLS.MAPPED_DEAL.price, CONST_VALUES.ROW_TO_GET_OR_SET);
						
						if (Math.abs(dPrice) > 0.00001) {
							bPriceColumnIsBeingUsed = true;
						}
						
						if (bPriceColumnIsBeingUsed == true) {
							String sPrice = Str.doubleToStr(dPrice); 
							
							TRANF_HELPER.safeSetTranfFieldWithErrorHandling(tran, TRANF_FIELD.TRANF_VARIABLE_COST, SIDE2, sPrice, tOneDealErrors, className);
						}

						// if price column is not populated (i.e., is zero), then get from variable_cost
 						if (bPriceColumnIsBeingUsed == false) {
							double dVariableCost = LIB.safeGetDouble(tOneDeal, COLS.MAPPED_DEAL.variable_cost, CONST_VALUES.ROW_TO_GET_OR_SET);
							String sVariableCost = Str.doubleToStr(dVariableCost); 
							
							TRANF_HELPER.safeSetTranfFieldWithErrorHandling(tran, TRANF_FIELD.TRANF_VARIABLE_COST, SIDE2, sVariableCost, tOneDealErrors, className);
						}
					}

					{
						double dVariableDemand = LIB.safeGetDouble(tOneDeal, COLS.MAPPED_DEAL.variable_demand, CONST_VALUES.ROW_TO_GET_OR_SET);
						String sVariableDemand = Str.doubleToStr(dVariableDemand); 
						
						TRANF_HELPER.safeSetTranfFieldWithErrorHandling(tran, TRANF_FIELD.TRANF_VARIABLE_DEMAND, SIDE2, sVariableDemand, tOneDealErrors, className);
					}

					{
						int iStartDate = LIB.safeGetInt(tOneDeal, COLS.SOURCE_DEAL.start_date, CONST_VALUES.ROW_TO_GET_OR_SET);
						String sStartDate = OCalendar.formatJd(iStartDate, DATE_FORMAT.DATE_FORMAT_DMLY_NOSLASH);
						TRANF_HELPER.safeSetTranfFieldWithErrorHandling(tran, TRANF_FIELD.TRANF_START_DATE, SIDE1, sStartDate, tOneDealErrors, className);
					}

					{
						int iEndDate = LIB.safeGetInt(tOneDeal, COLS.SOURCE_DEAL.end_date, CONST_VALUES.ROW_TO_GET_OR_SET);
						String sEndDate = OCalendar.formatJd(iEndDate, DATE_FORMAT.DATE_FORMAT_DMLY_NOSLASH);
						TRANF_HELPER.safeSetTranfFieldWithErrorHandling(tran, TRANF_FIELD.TRANF_MAT_DATE, SIDE1, sEndDate, tOneDealErrors, className);
					}
 					
//					{
//						String sTrader = LIB.safeGetString(tOneDeal, COLS.MAPPED_DEAL.trader, CONST_VALUES.ROW_TO_GET_OR_SET);
//						if (Str.len(sTrader) >= 1) {
//							TRANF_HELPER.safeSetTranfFieldWithErrorHandling(tran, TRANF_FIELD.TRANF_INTERNAL_CONTACT, sTrader, tOneDealErrors, className);
//						}
//					}

					// Only Set Fixed/Float if Commodity Toolset
					{
						{
							String sFixedFloat = LIB.safeGetString(tOneDeal, COLS.MAPPED_DEAL.fixed_float, CONST_VALUES.ROW_TO_GET_OR_SET);
							if (Str.len(sFixedFloat) >= 1) {
								sFixedFloat = Str.toUpper(sFixedFloat);
								String sFixedFloatToSet = "Fixed";
								if (Str.containsSubString(sFixedFloat, "INDEX") == 1 || Str.containsSubString(sFixedFloat, "FLOAT") == 1) {
									sFixedFloatToSet = "Index";
								}
								TRANF_HELPER.safeSetTranfFieldWithErrorHandling(tran, TRANF_FIELD.TRANF_FX_FLT, SIDE2, sFixedFloatToSet, tOneDealErrors, className);
							}
						}
					}
 	 
					{
						String sProjIndex = LIB.safeGetString(tOneDeal, COLS.MAPPED_DEAL.proj_index, CONST_VALUES.ROW_TO_GET_OR_SET);
						if (Str.len(sProjIndex) >= 1) {
							int iSideToSet = SIDE2;
	 						TRANF_HELPER.safeSetTranfFieldWithErrorHandling(tran, TRANF_FIELD.TRANF_PROJ_INDEX, iSideToSet, sProjIndex, tOneDealErrors, className);
						}
					}
 
	  
				} catch (Exception e) {
					LIB.log("COMM_PHYS.setCommPhysFields", e, className);
				}
			}
			
			
			 
			
			
			
		} // END COMM_TRANS
		
		
 




public static class COMM_STOR {
			
			public static void setCommStorFields(Transaction tran, Table tOneDeal, Table tOneDealErrors, int iRunNumber, String className) throws OException {
				 
				try {
					 
					
					int SIDE1 = 1;
	 				int SIDE2 = 2;
	 				int SIDE3 = 3;
	 			//	int SIDE4 = 4;
	 				int SIDE5 = 5;
 
	 				{
	 					final int SIDE_TO_SET = SIDE1;
	 					{
	 						String sPipeline = LIB.safeGetString(tOneDeal, COLS.MAPPED_DEAL.pipeline, CONST_VALUES.ROW_TO_GET_OR_SET);
	 						if (Str.len(sPipeline) >= 1) {
	 							TRANF_HELPER.safeSetTranfFieldWithErrorHandling(tran, TRANF_FIELD.TRANF_PIPELINE, SIDE_TO_SET, sPipeline, tOneDealErrors, className);
	 						}
	 					}
	 					{
	 						String sZone = LIB.safeGetString(tOneDeal, COLS.MAPPED_DEAL.zone, CONST_VALUES.ROW_TO_GET_OR_SET);
	 						if (Str.len(sZone) >= 1) {
	 	 						TRANF_HELPER.safeSetTranfFieldWithErrorHandling(tran, TRANF_FIELD.TRANF_ZONE, SIDE_TO_SET, sZone, tOneDealErrors, className);
	 						}
	 					}
	 					{
	 						String sLocation = LIB.safeGetString(tOneDeal, COLS.MAPPED_DEAL.location, CONST_VALUES.ROW_TO_GET_OR_SET);
	 						if (Str.len(sLocation) >= 1) {
	 							TRANF_HELPER.safeSetTranfFieldWithErrorHandling(tran, TRANF_FIELD.TRANF_LOCATION, SIDE_TO_SET, sLocation, tOneDealErrors, className);
	 						}
	 					}
	 				}  
	 				
	 				// first set Location 1
	 				// then set Location 2 (this one)
	 				// needed for COMM-STOR deals
	 				{
	 					// Set these on SIDE3
	 					final int SIDE_TO_SET = SIDE3;

	 					{
	 						String sZone2 = LIB.safeGetString(tOneDeal, COLS.MAPPED_DEAL.zone2, CONST_VALUES.ROW_TO_GET_OR_SET);
	 						if (Str.len(sZone2) >= 1) {
	 	 						TRANF_HELPER.safeSetTranfFieldWithErrorHandling(tran, TRANF_FIELD.TRANF_ZONE, SIDE_TO_SET, sZone2, tOneDealErrors, className);
	 						}
	 					}
	 					
	 					// Needed for COMM-STOR deals
	 					{
	 						String sLocation2 = LIB.safeGetString(tOneDeal, COLS.MAPPED_DEAL.location2, CONST_VALUES.ROW_TO_GET_OR_SET);
	 						if (Str.len(sLocation2) >= 1) {
	 							TRANF_HELPER.safeSetTranfFieldWithErrorHandling(tran, TRANF_FIELD.TRANF_LOCATION, SIDE_TO_SET, sLocation2, tOneDealErrors, className);
	 						}
	 					}
	 				} 
	 				

	 				// first set Location 1 and 2
	 				// then set Location 3 (this one)
	 				// needed for COMM-STOR deals
	 				{
	 					// Set these on SIDE3
	 					final int SIDE_TO_SET = SIDE5;
 
	 					{
	 						// For now, just assume that Zone3 is the same as Zone 2
	 						String sZone3 = LIB.safeGetString(tOneDeal, COLS.MAPPED_DEAL.zone2, CONST_VALUES.ROW_TO_GET_OR_SET);
	 						if (Str.len(sZone3) >= 1) {
	 	 						TRANF_HELPER.safeSetTranfFieldWithErrorHandling(tran, TRANF_FIELD.TRANF_ZONE, SIDE_TO_SET, sZone3, tOneDealErrors, className);
	 						}
	 					}
	 					
	 					// Needed for COMM-STOR deals, Location 3 is the 'Balance' leg
	 					{
	 						String sLocation3 = LIB.safeGetString(tOneDeal, COLS.MAPPED_DEAL.location3, CONST_VALUES.ROW_TO_GET_OR_SET);
	 						if (Str.len(sLocation3) >= 1) {
	 							TRANF_HELPER.safeSetTranfFieldWithErrorHandling(tran, TRANF_FIELD.TRANF_LOCATION, SIDE_TO_SET, sLocation3, tOneDealErrors, className);
	 						}
	 					}
	 				} 
 	 				 
					{
						double dQuantity = LIB.safeGetDouble(tOneDeal, COLS.MAPPED_DEAL.quantity, CONST_VALUES.ROW_TO_GET_OR_SET);
						
						String sQuantity = Str.doubleToStr(dQuantity); 
						TRANF_HELPER.safeSetTranfFieldWithErrorHandling(tran, TRANF_FIELD.TRANF_DAILY_VOLUME, SIDE1, sQuantity, tOneDealErrors, className);
					}


					{
						int iStartDate = LIB.safeGetInt(tOneDeal, COLS.SOURCE_DEAL.start_date, CONST_VALUES.ROW_TO_GET_OR_SET);
						String sStartDate = OCalendar.formatJd(iStartDate, DATE_FORMAT.DATE_FORMAT_DMLY_NOSLASH);
						TRANF_HELPER.safeSetTranfFieldWithErrorHandling(tran, TRANF_FIELD.TRANF_START_DATE, SIDE1, sStartDate, tOneDealErrors, className);
					}

					{
						int iEndDate = LIB.safeGetInt(tOneDeal, COLS.SOURCE_DEAL.end_date, CONST_VALUES.ROW_TO_GET_OR_SET);
						String sEndDate = OCalendar.formatJd(iEndDate, DATE_FORMAT.DATE_FORMAT_DMLY_NOSLASH);
						TRANF_HELPER.safeSetTranfFieldWithErrorHandling(tran, TRANF_FIELD.TRANF_MAT_DATE, SIDE1, sEndDate, tOneDealErrors, className);
					}
 					
//					{
//						String sTrader = LIB.safeGetString(tOneDeal, COLS.MAPPED_DEAL.trader, CONST_VALUES.ROW_TO_GET_OR_SET);
//						if (Str.len(sTrader) >= 1) {
//							TRANF_HELPER.safeSetTranfFieldWithErrorHandling(tran, TRANF_FIELD.TRANF_INTERNAL_CONTACT, sTrader, tOneDealErrors, className);
//						}
//					}

					// Only Set Fixed/Float if Commodity
					{
						{
							String sFixedFloat = LIB.safeGetString(tOneDeal, COLS.MAPPED_DEAL.fixed_float, CONST_VALUES.ROW_TO_GET_OR_SET);
							if (Str.len(sFixedFloat) >= 1) {
								sFixedFloat = Str.toUpper(sFixedFloat);
								String sFixedFloatToSet = "Fixed";
								if (Str.containsSubString(sFixedFloat, "INDEX") == 1 || Str.containsSubString(sFixedFloat, "FLOAT") == 1) {
									sFixedFloatToSet = "Index";
								}
								TRANF_HELPER.safeSetTranfFieldWithErrorHandling(tran, TRANF_FIELD.TRANF_FX_FLT, SIDE2, sFixedFloatToSet, tOneDealErrors, className);
							}
						}
					}

	 
					{
						String sProjIndex = LIB.safeGetString(tOneDeal, COLS.MAPPED_DEAL.proj_index, CONST_VALUES.ROW_TO_GET_OR_SET);
						if (Str.len(sProjIndex) >= 1) {
							int iSideToSet = SIDE2;
	 						TRANF_HELPER.safeSetTranfFieldWithErrorHandling(tran, TRANF_FIELD.TRANF_PROJ_INDEX, iSideToSet, sProjIndex, tOneDealErrors, className);
						}
					}
 
					{
						double dPrice = LIB.safeGetDouble(tOneDeal, COLS.MAPPED_DEAL.price, CONST_VALUES.ROW_TO_GET_OR_SET);
						String sPrice = Str.doubleToStr(dPrice); 
						
						// For COMM-STOR, set this to 
						TRANF_HELPER.safeSetTranfFieldWithErrorHandling(tran, TRANF_FIELD.TRANF_RATE_SPD, SIDE2, sPrice, tOneDealErrors, className);
					}
	  
				} catch (Exception e) {
					LIB.log("COMM_STOR.setCommPhysFields", e, className);
				}
			} 
			
		} // END COMM_STOR
		

		public static class ENGY_SWAP {
			
			public static void setEngySwapFields(Transaction tran, Table tOneDeal, Table tOneDealErrors, int iRunNumber, String className) throws OException {
				 
				try {
					
	 				// Now start By Side fields
					// For non-Commodity Toolset fields, the sides are 0 and 1
					int SIDE1 = 0;
	 				int SIDE2 = 1;
	 				 
  
					{
						double dQuantity = LIB.safeGetDouble(tOneDeal, COLS.MAPPED_DEAL.quantity, CONST_VALUES.ROW_TO_GET_OR_SET);

						String sQuantity = Str.doubleToStr(dQuantity); 

						// OpenLink gives an error if you try to set a value to zero
						if (Math.abs(dQuantity) < 0.000001) {
							sQuantity = "0.00001";
						}
							
						TRANF_HELPER.safeSetTranfFieldWithErrorHandling(tran, TRANF_FIELD.TRANF_DAILY_VOLUME, SIDE1, sQuantity, tOneDealErrors, className);
					}

					{
						double dPrice = LIB.safeGetDouble(tOneDeal, COLS.MAPPED_DEAL.price, CONST_VALUES.ROW_TO_GET_OR_SET);
						String sPrice = Str.doubleToStr(dPrice); 
						TRANF_HELPER.safeSetTranfFieldWithErrorHandling(tran, TRANF_FIELD.TRANF_RATE_SPD, SIDE2, sPrice, tOneDealErrors, className);
					}

					{
						int iStartDate = LIB.safeGetInt(tOneDeal, COLS.SOURCE_DEAL.start_date, CONST_VALUES.ROW_TO_GET_OR_SET);
						String sStartDate = OCalendar.formatJd(iStartDate, DATE_FORMAT.DATE_FORMAT_DMLY_NOSLASH);
						TRANF_HELPER.safeSetTranfFieldWithErrorHandling(tran, TRANF_FIELD.TRANF_START_DATE, SIDE1, sStartDate, tOneDealErrors, className);
					}

					{
						int iEndDate = LIB.safeGetInt(tOneDeal, COLS.SOURCE_DEAL.end_date, CONST_VALUES.ROW_TO_GET_OR_SET);
						String sEndDate = OCalendar.formatJd(iEndDate, DATE_FORMAT.DATE_FORMAT_DMLY_NOSLASH);
						TRANF_HELPER.safeSetTranfFieldWithErrorHandling(tran, TRANF_FIELD.TRANF_MAT_DATE, SIDE1, sEndDate, tOneDealErrors, className);
					}

					
//					{
//						String sTrader = LIB.safeGetString(tOneDeal, COLS.MAPPED_DEAL.trader, CONST_VALUES.ROW_TO_GET_OR_SET);
//						if (Str.len(sTrader) >= 1) {
//							TRANF_HELPER.safeSetTranfFieldWithErrorHandling(tran, TRANF_FIELD.TRANF_INTERNAL_CONTACT, sTrader, tOneDealErrors, className);
//						}
//					} 

	 
					{
						String sProjIndex = LIB.safeGetString(tOneDeal, COLS.MAPPED_DEAL.proj_index, CONST_VALUES.ROW_TO_GET_OR_SET);
						if (Str.len(sProjIndex) >= 1) { 
	 						TRANF_HELPER.safeSetTranfFieldWithErrorHandling(tran, TRANF_FIELD.TRANF_PROJ_INDEX, SIDE2, sProjIndex, tOneDealErrors, className);
						}
					}

					{
						double dSpread = LIB.safeGetDouble(tOneDeal, COLS.MAPPED_DEAL.spread, CONST_VALUES.ROW_TO_GET_OR_SET);
						String sSpread = Str.doubleToStr(dSpread); 
						
						// Spread can be in Spread or Price
						// use Spread until Price is non-zero
						double dPrice = LIB.safeGetDouble(tOneDeal, COLS.MAPPED_DEAL.price, CONST_VALUES.ROW_TO_GET_OR_SET);
						String sPrice = Str.doubleToStr(dPrice); 

						// default
						String sSpreadToSet = sSpread;
						if (Math.abs(dPrice) > 0.0000001) {
							sSpreadToSet = sPrice;
						}
						
						TRANF_HELPER.safeSetTranfFieldWithErrorHandling(tran, TRANF_FIELD.TRANF_RATE_SPD, SIDE2, sSpreadToSet, tOneDealErrors, className);
					}
	  
				} catch (Exception e) {
					LIB.log("ENGY_SWAP.setEngySwapFields", e, className);
				}
			}
			
		} // END ENGY_SWAP
		
		public static class ENGY_B_SWAP {
			
			public static void setEngyBSwapFields(Transaction tran, Table tOneDeal, Table tOneDealErrors, int iRunNumber, String className) throws OException {
				 
				try {
					
 	 				// Now start By Side fields
					// For non-Commodity Toolset fields, the sides are 0 and 1
					int SIDE1 = 0;
	 				int SIDE2 = 1;
	
					{
						double dQuantity = LIB.safeGetDouble(tOneDeal, COLS.MAPPED_DEAL.quantity, CONST_VALUES.ROW_TO_GET_OR_SET);

						String sQuantity = Str.doubleToStr(dQuantity); 
						// OpenLink gives an error if you try to set a value to zero
						if (Math.abs(dQuantity) < 0.000001) {
							sQuantity = "0.00001";
						}

						TRANF_HELPER.safeSetTranfFieldWithErrorHandling(tran, TRANF_FIELD.TRANF_DAILY_VOLUME, SIDE1, sQuantity, tOneDealErrors, className);
					}

					{
						double dPrice = LIB.safeGetDouble(tOneDeal, COLS.MAPPED_DEAL.price, CONST_VALUES.ROW_TO_GET_OR_SET);
						String sPrice = Str.doubleToStr(dPrice); 
						TRANF_HELPER.safeSetTranfFieldWithErrorHandling(tran, TRANF_FIELD.TRANF_RATE_SPD, SIDE2, sPrice, tOneDealErrors, className);
					}

					{
						int iStartDate = LIB.safeGetInt(tOneDeal, COLS.SOURCE_DEAL.start_date, CONST_VALUES.ROW_TO_GET_OR_SET);
						String sStartDate = OCalendar.formatJd(iStartDate, DATE_FORMAT.DATE_FORMAT_DMLY_NOSLASH);
						TRANF_HELPER.safeSetTranfFieldWithErrorHandling(tran, TRANF_FIELD.TRANF_START_DATE, SIDE1, sStartDate, tOneDealErrors, className);
					}

					{
						int iEndDate = LIB.safeGetInt(tOneDeal, COLS.SOURCE_DEAL.end_date, CONST_VALUES.ROW_TO_GET_OR_SET);
						String sEndDate = OCalendar.formatJd(iEndDate, DATE_FORMAT.DATE_FORMAT_DMLY_NOSLASH);
						TRANF_HELPER.safeSetTranfFieldWithErrorHandling(tran, TRANF_FIELD.TRANF_MAT_DATE, SIDE1, sEndDate, tOneDealErrors, className);
					}

					
//					{
//						String sTrader = LIB.safeGetString(tOneDeal, COLS.MAPPED_DEAL.trader, CONST_VALUES.ROW_TO_GET_OR_SET);
//						if (Str.len(sTrader) >= 1) {
//							TRANF_HELPER.safeSetTranfFieldWithErrorHandling(tran, TRANF_FIELD.TRANF_INTERNAL_CONTACT, sTrader, tOneDealErrors, className);
//						}
//					} 


					// Side 1 (param seq num 0) for a ENGY-B-SWAP
					{
						String sProjIndex = LIB.safeGetString(tOneDeal, COLS.MAPPED_DEAL.proj_index, CONST_VALUES.ROW_TO_GET_OR_SET);
						if (Str.len(sProjIndex) >= 1) { 
	 						TRANF_HELPER.safeSetTranfFieldWithErrorHandling(tran, TRANF_FIELD.TRANF_PROJ_INDEX, SIDE1, sProjIndex, tOneDealErrors, className);
						}
					}
					
					// Side 2 (param seq num 1) for a ENGY-B-SWAP
					{
						String sProjIndex2 = LIB.safeGetString(tOneDeal, COLS.MAPPED_DEAL.proj_index2, CONST_VALUES.ROW_TO_GET_OR_SET);
						if (Str.len(sProjIndex2) >= 1) { 
	 						TRANF_HELPER.safeSetTranfFieldWithErrorHandling(tran, TRANF_FIELD.TRANF_PROJ_INDEX, SIDE2, sProjIndex2, tOneDealErrors, className);
						}
					}

					{
						double dSpread = LIB.safeGetDouble(tOneDeal, COLS.MAPPED_DEAL.spread, CONST_VALUES.ROW_TO_GET_OR_SET);
						String sSpread = Str.doubleToStr(dSpread); 
						
						// Spread can be in Spread or Price
						// use Spread until Price is non-zero
						double dPrice = LIB.safeGetDouble(tOneDeal, COLS.MAPPED_DEAL.price, CONST_VALUES.ROW_TO_GET_OR_SET);
						String sPrice = Str.doubleToStr(dPrice); 

						// default
						String sSpreadToSet = sSpread;
						if (Math.abs(dPrice) > 0.0000001) {
							sSpreadToSet = sPrice;
						}
						
						TRANF_HELPER.safeSetTranfFieldWithErrorHandling(tran, TRANF_FIELD.TRANF_RATE_SPD, SIDE2, sSpreadToSet, tOneDealErrors, className);
					}
					
				} catch (Exception e) {
					LIB.log("ENGY_SWAP.setEngyBSwapFields", e, className);
				}
			}
			
		} // END ENGY_B_SWAP
		
		// 1) Get a Tran Pointer 
		// 2) populate it 
		// 3) and get errors
		public static Transaction createDealInMemory(Table tOneDeal, Table tOneDealActualData, int iTemplateTranNum, Table tOneDealErrors, 
				String sSourceSystem, int iRunNumber, String className) throws OException {
			
			Transaction tran = Util.NULL_TRAN;
 			
			try {
				
				// Do this first because we rely on this to get the deal number for errors
				String sDealNum = LIB.safeGetString(tOneDeal, COLS.MAPPED_DEAL.deal_num, CONST_VALUES.ROW_TO_GET_OR_SET);
				
				String sTemplate = LIB.safeGetString(tOneDeal, COLS.MAPPED_DEAL.template, CONST_VALUES.ROW_TO_GET_OR_SET);
				if (sTemplate == null) {
					sTemplate = "";
				}
				
				// Check to see if the user supplied a template and use it if found
				// otherwise... use the default Template
				if (Str.len(sTemplate) >= 1) {
					Table tTemp = Table.tableNew();
					LIB.safeAddCol(tTemp, "tran_num", COL_TYPE_ENUM.COL_INT);
					String sSQL = "select tran_num from ab_tran WHERE tran_status = 15 AND lower(reference) like lower('" + sTemplate + "')";
							
					LIB.execISql(tTemp, sSQL, false, className);
					
					// only do this is one Template for this name is found
					final int NUM_ROWS_IS_ONE = 1;
					if (LIB.safeGetNumRows(tTemp) == NUM_ROWS_IS_ONE) {
						iTemplateTranNum = LIB.safeGetInt(tTemp, "tran_num", CONST_VALUES.ROW_TO_GET_OR_SET);
					}

					// if template name is not found, give an error
					final int NUM_ROWS_IS_ZERO = 0;
					if (LIB.safeGetNumRows(tTemp) == NUM_ROWS_IS_ZERO) {
						String sErrorMessage = "Can't find Template " + "'" + sTemplate + "'" + " in Endur";
						FUNC.ERROR_HANDLING.addErrorToTable(tOneDealErrors, ERROR_TYPE_ENUM.MISSING_TEMPLATE_FROM_SOURCE_FILE, sDealNum, sErrorMessage, sTemplate);
					}

					// add a error if there are two or more
					if (LIB.safeGetNumRows(tTemp) > NUM_ROWS_IS_ONE) {
						// if template name is not found, give an error
						//LIB.log("safeSetTranfField with Side", e, className);
						String sErrorMessage = "Duplicate Templates " + "'" + sTemplate + "'" + " in Endur, Found: " + LIB.safeGetNumRows(tTemp);
						FUNC.ERROR_HANDLING.addErrorToTable(tOneDealErrors, ERROR_TYPE_ENUM.MISSING_TEMPLATE_FROM_SOURCE_FILE, sDealNum, sErrorMessage, sTemplate);
					}

					tTemp = LIB.destroyTable(tTemp);
				}
 				
				tran = Transaction.retrieveCopy(iTemplateTranNum);
 
				// set deal Num
				TRANF_HELPER.safeSetTranfInfoField(tran, TRAN_INFO.ExternalDealRef, sDealNum, className);
 				
				String sCounterparty = LIB.safeGetString(tOneDeal, COLS.MAPPED_DEAL.counterparty, CONST_VALUES.ROW_TO_GET_OR_SET);
				TRANF_HELPER.safeSetTranfFieldWithErrorHandling(tran, TRANF_FIELD.TRANF_EXTERNAL_BUNIT, sCounterparty, tOneDealErrors, className);

				// Set External Portfolio *after* setting Counterparty
				{
					String sExternalPortfolio = LIB.safeGetString(tOneDeal, COLS.MAPPED_DEAL.external_portfolio, CONST_VALUES.ROW_TO_GET_OR_SET);
					if (Str.len(sExternalPortfolio) >= 1) {
						TRANF_HELPER.safeSetTranfFieldWithErrorHandling(tran, TRANF_FIELD.TRANF_EXTERNAL_PORTFOLIO, sExternalPortfolio, tOneDealErrors, className);
					}
				}

				{
					int iTradeDate = LIB.safeGetInt(tOneDeal, COLS.SOURCE_DEAL.trade_date, CONST_VALUES.ROW_TO_GET_OR_SET);
					String sTradeDate = OCalendar.formatJd(iTradeDate, DATE_FORMAT.DATE_FORMAT_DMLY_NOSLASH);
					TRANF_HELPER.safeSetTranfFieldWithErrorHandling(tran, TRANF_FIELD.TRANF_TRADE_DATE, sTradeDate, tOneDealErrors, className);
				}


				// Need to set Internal Bunit ('Desk') prior to setting Internal Portfolio ('Portfolio)
				{
					String sInternalBunit = LIB.safeGetString(tOneDeal, COLS.MAPPED_DEAL.internal_bunit, CONST_VALUES.ROW_TO_GET_OR_SET);
					if (Str.len(sInternalBunit) >= 1) {
						TRANF_HELPER.safeSetTranfFieldWithErrorHandling(tran, TRANF_FIELD.TRANF_INTERNAL_BUNIT, sInternalBunit, tOneDealErrors, className);
					}
				}
				
				{
					String sPortfolio = LIB.safeGetString(tOneDeal, COLS.MAPPED_DEAL.portfolio, CONST_VALUES.ROW_TO_GET_OR_SET);
					if (Str.len(sPortfolio) >= 1) {
						TRANF_HELPER.safeSetTranfFieldWithErrorHandling(tran, TRANF_FIELD.TRANF_INTERNAL_PORTFOLIO, sPortfolio, tOneDealErrors, className);
					}
				}
 				
				{
					String sBuySell = LIB.safeGetString(tOneDeal, COLS.MAPPED_DEAL.buy_sell, CONST_VALUES.ROW_TO_GET_OR_SET);
					if (Str.len(sBuySell) >= 1) {
						TRANF_HELPER.safeSetTranfFieldWithErrorHandling(tran, TRANF_FIELD.TRANF_BUY_SELL, sBuySell, tOneDealErrors, className);
					}
				}
			

				// so that we can identify deals booked using this tool
				TRANF_HELPER.safeSetTranfFieldWithErrorHandling(tran, TRANF_FIELD.TRANF_BOOK, CONST_VALUES.BOOK_TO_SET, tOneDealErrors, className);

				int iInsType = TRANF_HELPER.safeGetTranfFieldInt(tran, TRANF_FIELD.TRANF_INS_TYPE, className);
				int iBaseInsType = TRANF_HELPER.safeGetTranfFieldInt(tran, TRANF_FIELD.TRANF_BASE_INS_TYPE, className);
				
				
				// INS_TYPE_ENUM.energy_swap.toInt()???
				final int iENGY_SWAP = 30001;
				// INS_TYPE_ENUM.commodity_basis_swap.toInt()???
				final int iENGY_B_SWAP = 30002;
				final int iCOMM_PHYS = 48010;
				final int iCOMM_TRANS = 48020;
				final int iCOMM_STOR = 48030;
				  
//				name *	id_number
//				COMM-PHYS	48010
//				COMM-TRANS	48020
//				COMM-STOR	48030
//				COMM-EXCH	48035
//				COMM-IMB	48040
//				COMM-PAL	48031
 				
				if (iInsType == iENGY_SWAP || iBaseInsType == iENGY_SWAP) {
					ENGY_SWAP.setEngySwapFields(tran, tOneDeal, tOneDealErrors, iRunNumber, className);
				}
				 
				if (iInsType == iENGY_B_SWAP || iBaseInsType == iENGY_B_SWAP) {
					ENGY_B_SWAP.setEngyBSwapFields(tran, tOneDeal, tOneDealErrors, iRunNumber, className);
				}
				 
				if (iInsType == iCOMM_PHYS || iBaseInsType == iCOMM_PHYS) {
					COMM_PHYS.setCommPhysFields(tran, tOneDeal, tOneDealErrors, iRunNumber, className);
				}
 
				if (iInsType == iCOMM_TRANS || iBaseInsType == iCOMM_TRANS) {
					COMM_TRANS.setCommTransFields(tran, tOneDeal, tOneDealErrors, iRunNumber, className);
				}

				if (iInsType == iCOMM_STOR || iBaseInsType == iCOMM_STOR) {
					COMM_STOR.setCommStorFields(tran, tOneDeal, tOneDealErrors, iRunNumber, className);
				}
				
				// Trader
				{
					String sTrader = LIB.safeGetString(tOneDeal, COLS.MAPPED_DEAL.trader, CONST_VALUES.ROW_TO_GET_OR_SET);
					if (Str.len(sTrader) >= 1) {
						TRANF_HELPER.safeSetTranfFieldWithErrorHandling(tran, TRANF_FIELD.TRANF_INTERNAL_CONTACT, sTrader, tOneDealErrors, className);
					}
				}
				
				// Set Broker and Broker Fee
				{
					String sBroker = LIB.safeGetString(tOneDeal, COLS.MAPPED_DEAL.broker, CONST_VALUES.ROW_TO_GET_OR_SET);
					if (Str.len(sBroker) >= 1) {
						TRANF_HELPER.safeSetTranfFieldWithErrorHandling(tran, TRANF_FIELD.TRANF_BROKER_ID, sBroker, tOneDealErrors, className);
					}
					
					String sBrokerFee = LIB.safeGetString(tOneDeal, COLS.MAPPED_DEAL.broker_fee, CONST_VALUES.ROW_TO_GET_OR_SET);
					if (Str.len(sBrokerFee) >= 1) {
						TRANF_HELPER.safeSetTranfFieldWithErrorHandling(tran, TRANF_FIELD.TRANF_BROKER_FEE, sBrokerFee, tOneDealErrors, className);
					}
				}
				
				 
				// TODO
				// At some point... get all of the 'Actual' data'

				// Actual Data
				// 'Get' order does not matter for this
				{
					String sValue = TRANF_HELPER.safeGetTranfField(tran, TRANF_FIELD.TRANF_TEMPLATE_REFERENCE, className);
					LIB.safeSetString(tOneDealActualData, COLS.MAPPED_DEAL.template, CONST_VALUES.ROW_TO_GET_OR_SET, sValue);
					
					sValue = TRANF_HELPER.safeGetTranfField(tran, TRANF_FIELD.TRANF_EXTERNAL_BUNIT, className);
					LIB.safeSetString(tOneDealActualData, COLS.MAPPED_DEAL.counterparty, CONST_VALUES.ROW_TO_GET_OR_SET, sValue);
					
					int iValue = TRANF_HELPER.safeGetTranfFieldInt(tran, TRANF_FIELD.TRANF_TRADE_DATE, className);
		 			LIB.safeSetInt(tOneDealActualData, COLS.MAPPED_DEAL.trade_date, CONST_VALUES.ROW_TO_GET_OR_SET, iValue);

					sValue = TRANF_HELPER.safeGetTranfField(tran, TRANF_FIELD.TRANF_INTERNAL_BUNIT, className);
					LIB.safeSetString(tOneDealActualData, COLS.MAPPED_DEAL.internal_bunit, CONST_VALUES.ROW_TO_GET_OR_SET, sValue);
					
					sValue = TRANF_HELPER.safeGetTranfField(tran, TRANF_FIELD.TRANF_INTERNAL_PORTFOLIO, className);
					LIB.safeSetString(tOneDealActualData, COLS.MAPPED_DEAL.portfolio, CONST_VALUES.ROW_TO_GET_OR_SET, sValue);
 		 			
					sValue = TRANF_HELPER.safeGetTranfField(tran, TRANF_FIELD.TRANF_EXTERNAL_PORTFOLIO, className);
					LIB.safeSetString(tOneDealActualData, COLS.MAPPED_DEAL.external_portfolio, CONST_VALUES.ROW_TO_GET_OR_SET, sValue);

					sValue = TRANF_HELPER.safeGetTranfField(tran, TRANF_FIELD.TRANF_BUY_SELL, className);
					LIB.safeSetString(tOneDealActualData, COLS.MAPPED_DEAL.buy_sell, CONST_VALUES.ROW_TO_GET_OR_SET, sValue);

					sValue = TRANF_HELPER.safeGetTranfField(tran, TRANF_FIELD.TRANF_INTERNAL_CONTACT, className);
					LIB.safeSetString(tOneDealActualData, COLS.MAPPED_DEAL.trader, CONST_VALUES.ROW_TO_GET_OR_SET, sValue);
					
					sValue = TRANF_HELPER.safeGetTranfField(tran, TRANF_FIELD.TRANF_BROKER_ID, className);
					LIB.safeSetString(tOneDealActualData, COLS.MAPPED_DEAL.broker, CONST_VALUES.ROW_TO_GET_OR_SET, sValue);
 
					sValue = TRANF_HELPER.safeGetTranfField(tran, TRANF_FIELD.TRANF_BROKER_FEE, className);
					LIB.safeSetString(tOneDealActualData, COLS.MAPPED_DEAL.broker_fee, CONST_VALUES.ROW_TO_GET_OR_SET, sValue);

					if (iInsType == iCOMM_PHYS || iBaseInsType == iCOMM_PHYS) {
						
						final int SIDE1 = 1;
						final int SIDE2 = 2;
						{
							final int SIDE_TO_GET = SIDE1;
							sValue = TRANF_HELPER.safeGetTranfField(tran, TRANF_FIELD.TRANF_PIPELINE, SIDE_TO_GET, className);
							LIB.safeSetString(tOneDealActualData, COLS.MAPPED_DEAL.pipeline, CONST_VALUES.ROW_TO_GET_OR_SET, sValue);

							sValue = TRANF_HELPER.safeGetTranfField(tran, TRANF_FIELD.TRANF_ZONE, SIDE_TO_GET, className);
							LIB.safeSetString(tOneDealActualData, COLS.MAPPED_DEAL.zone, CONST_VALUES.ROW_TO_GET_OR_SET, sValue);

							sValue = TRANF_HELPER.safeGetTranfField(tran, TRANF_FIELD.TRANF_LOCATION, SIDE_TO_GET, className);
							LIB.safeSetString(tOneDealActualData, COLS.MAPPED_DEAL.location, CONST_VALUES.ROW_TO_GET_OR_SET, sValue);
						} 
						
						double dValue = TRANF_HELPER.safeGetTranfFieldDouble(tran, TRANF_FIELD.TRANF_DAILY_VOLUME, SIDE1, className);
						LIB.safeSetDouble(tOneDealActualData, COLS.MAPPED_DEAL.quantity, CONST_VALUES.ROW_TO_GET_OR_SET, dValue);
	   				
						iValue = TRANF_HELPER.safeGetTranfFieldInt(tran, TRANF_FIELD.TRANF_START_DATE, SIDE1, className);
						LIB.safeSetInt(tOneDealActualData, COLS.MAPPED_DEAL.start_date, CONST_VALUES.ROW_TO_GET_OR_SET, iValue);
					 
						iValue = TRANF_HELPER.safeGetTranfFieldInt(tran, TRANF_FIELD.TRANF_MAT_DATE, SIDE1, className);
						LIB.safeSetInt(tOneDealActualData, COLS.MAPPED_DEAL.end_date, CONST_VALUES.ROW_TO_GET_OR_SET, iValue);
						
						sValue = TRANF_HELPER.safeGetTranfField(tran, TRANF_FIELD.TRANF_FX_FLT, SIDE2, className);
						LIB.safeSetString(tOneDealActualData, COLS.MAPPED_DEAL.fixed_float, CONST_VALUES.ROW_TO_GET_OR_SET, sValue);
						
						sValue = TRANF_HELPER.safeGetTranfField(tran, TRANF_FIELD.TRANF_PROJ_INDEX, SIDE2, className);
						LIB.safeSetString(tOneDealActualData, COLS.MAPPED_DEAL.proj_index, CONST_VALUES.ROW_TO_GET_OR_SET, sValue);
 
					} // END if (iInsType == iCOMM_PHYS || iBaseInsType == iCOMM_PHYS) 
					 
					if (iInsType == iCOMM_TRANS || iBaseInsType == iCOMM_TRANS) {
						
						final int SIDE1 = 1;
						final int SIDE2 = 2;
						final int SIDE3 = 3;
						{
							final int SIDE_TO_GET = SIDE1;
							sValue = TRANF_HELPER.safeGetTranfField(tran, TRANF_FIELD.TRANF_PIPELINE, SIDE_TO_GET, className);
							LIB.safeSetString(tOneDealActualData, COLS.MAPPED_DEAL.pipeline, CONST_VALUES.ROW_TO_GET_OR_SET, sValue);

							sValue = TRANF_HELPER.safeGetTranfField(tran, TRANF_FIELD.TRANF_ZONE, SIDE_TO_GET, className);
							LIB.safeSetString(tOneDealActualData, COLS.MAPPED_DEAL.zone, CONST_VALUES.ROW_TO_GET_OR_SET, sValue);

							sValue = TRANF_HELPER.safeGetTranfField(tran, TRANF_FIELD.TRANF_LOCATION, SIDE_TO_GET, className);
							LIB.safeSetString(tOneDealActualData, COLS.MAPPED_DEAL.location, CONST_VALUES.ROW_TO_GET_OR_SET, sValue);
						}
						{
	 						final int SIDE_TO_GET = SIDE3;
							sValue = TRANF_HELPER.safeGetTranfField(tran, TRANF_FIELD.TRANF_ZONE, SIDE_TO_GET, className);
							LIB.safeSetString(tOneDealActualData, COLS.MAPPED_DEAL.zone2, CONST_VALUES.ROW_TO_GET_OR_SET, sValue);

							sValue = TRANF_HELPER.safeGetTranfField(tran, TRANF_FIELD.TRANF_LOCATION, SIDE_TO_GET, className);
							LIB.safeSetString(tOneDealActualData, COLS.MAPPED_DEAL.location2, CONST_VALUES.ROW_TO_GET_OR_SET, sValue);
						}
						
 						double dValue = TRANF_HELPER.safeGetTranfFieldDouble(tran, TRANF_FIELD.TRANF_DAILY_VOLUME, SIDE1, className);
						LIB.safeSetDouble(tOneDealActualData, COLS.MAPPED_DEAL.quantity, CONST_VALUES.ROW_TO_GET_OR_SET, dValue);
	 
						dValue = TRANF_HELPER.safeGetTranfFieldDouble(tran, TRANF_FIELD.TRANF_VARIABLE_COST, SIDE2, className);
						LIB.safeSetDouble(tOneDealActualData, COLS.MAPPED_DEAL.variable_cost, CONST_VALUES.ROW_TO_GET_OR_SET, dValue);
						
						dValue = TRANF_HELPER.safeGetTranfFieldDouble(tran, TRANF_FIELD.TRANF_VARIABLE_DEMAND, SIDE2, className);
						LIB.safeSetDouble(tOneDealActualData, COLS.MAPPED_DEAL.variable_demand, CONST_VALUES.ROW_TO_GET_OR_SET, dValue);
						
						iValue = TRANF_HELPER.safeGetTranfFieldInt(tran, TRANF_FIELD.TRANF_START_DATE, SIDE1, className);
						LIB.safeSetInt(tOneDealActualData, COLS.MAPPED_DEAL.start_date, CONST_VALUES.ROW_TO_GET_OR_SET, iValue);
					 
						iValue = TRANF_HELPER.safeGetTranfFieldInt(tran, TRANF_FIELD.TRANF_MAT_DATE, SIDE1, className);
						LIB.safeSetInt(tOneDealActualData, COLS.MAPPED_DEAL.end_date, CONST_VALUES.ROW_TO_GET_OR_SET, iValue);
						
						sValue = TRANF_HELPER.safeGetTranfField(tran, TRANF_FIELD.TRANF_FX_FLT, SIDE2, className);
						LIB.safeSetString(tOneDealActualData, COLS.MAPPED_DEAL.fixed_float, CONST_VALUES.ROW_TO_GET_OR_SET, sValue);
						
						sValue = TRANF_HELPER.safeGetTranfField(tran, TRANF_FIELD.TRANF_PROJ_INDEX, SIDE2, className);
						LIB.safeSetString(tOneDealActualData, COLS.MAPPED_DEAL.proj_index, CONST_VALUES.ROW_TO_GET_OR_SET, sValue);
 
					} // END if (iInsType == iCOMM_TRANS || iBaseInsType == iCOMM_TRANS) 
					

					if (iInsType == iCOMM_STOR || iBaseInsType == iCOMM_STOR) {
						
						final int SIDE1 = 1;
						final int SIDE3 = 3;
						final int SIDE5 = 5;
						{
							final int SIDE_TO_GET = SIDE1;
							sValue = TRANF_HELPER.safeGetTranfField(tran, TRANF_FIELD.TRANF_PIPELINE, SIDE_TO_GET, className);
							LIB.safeSetString(tOneDealActualData, COLS.MAPPED_DEAL.pipeline, CONST_VALUES.ROW_TO_GET_OR_SET, sValue);

							sValue = TRANF_HELPER.safeGetTranfField(tran, TRANF_FIELD.TRANF_ZONE, SIDE_TO_GET, className);
							LIB.safeSetString(tOneDealActualData, COLS.MAPPED_DEAL.zone, CONST_VALUES.ROW_TO_GET_OR_SET, sValue);

							sValue = TRANF_HELPER.safeGetTranfField(tran, TRANF_FIELD.TRANF_LOCATION, SIDE_TO_GET, className);
							LIB.safeSetString(tOneDealActualData, COLS.MAPPED_DEAL.location, CONST_VALUES.ROW_TO_GET_OR_SET, sValue);
						}
						{
	 						final int SIDE_TO_GET = SIDE3;
							sValue = TRANF_HELPER.safeGetTranfField(tran, TRANF_FIELD.TRANF_ZONE, SIDE_TO_GET, className);
							LIB.safeSetString(tOneDealActualData, COLS.MAPPED_DEAL.zone2, CONST_VALUES.ROW_TO_GET_OR_SET, sValue);

							sValue = TRANF_HELPER.safeGetTranfField(tran, TRANF_FIELD.TRANF_LOCATION, SIDE_TO_GET, className);
							LIB.safeSetString(tOneDealActualData, COLS.MAPPED_DEAL.location2, CONST_VALUES.ROW_TO_GET_OR_SET, sValue);
						} 
						{
	 						final int SIDE_TO_GET = SIDE5;
//							sValue = TRANF_HELPER.safeGetTranfField(tran, TRANF_FIELD.TRANF_ZONE, SIDE_TO_GET, className);
//							LIB.safeSetString(tOneDealActualData, COLS.MAPPED_DEAL.zone2, CONST_VALUES.ROW_TO_GET_OR_SET, sValue);

							sValue = TRANF_HELPER.safeGetTranfField(tran, TRANF_FIELD.TRANF_LOCATION, SIDE_TO_GET, className);
							LIB.safeSetString(tOneDealActualData, COLS.MAPPED_DEAL.location3, CONST_VALUES.ROW_TO_GET_OR_SET, sValue);
						} 
 
					} // END if (iInsType == iCOMM_STOR || iBaseInsType == iCOMM_STOR) 
					
 				}
 				
				// Set the extra Tran Info Fields
				{
					Table tExtraTranfFieldInfo = LIB.safeGetTable(tOneDeal, COLS.MAPPED_DEAL.tranf_field_table, CONST_VALUES.ROW_TO_GET_OR_SET);
					int iNumExtraTranfFields = LIB.safeGetNumRows(tExtraTranfFieldInfo);
					if (iNumExtraTranfFields >= 1) {
						for (int iSubCounter = 1; iSubCounter <= iNumExtraTranfFields; iSubCounter++) {
							String sTranFieldName =  "";
							try {
								sTranFieldName = LIB.safeGetString(tExtraTranfFieldInfo, COLS.TRANF_FIELD_EXTRA_FIELDS.tran_field_name, iSubCounter);
								int iSideToSet = LIB.safeGetInt(tExtraTranfFieldInfo, COLS.TRANF_FIELD_EXTRA_FIELDS.side, iSubCounter);
								int iSeqNum2 = LIB.safeGetInt(tExtraTranfFieldInfo, COLS.TRANF_FIELD_EXTRA_FIELDS.seq_num2, iSubCounter);
								String sValue = LIB.safeGetString(tExtraTranfFieldInfo, COLS.TRANF_FIELD_EXTRA_FIELDS.value, iSubCounter);
								if (sValue == null) {
									sValue = "";
								}
								
								// For Tran Field extra fields... treat space like an empty string
								if (Str.iEqual(sValue, " ") == CONST_VALUES.VALUE_OF_TRUE) {
									sValue = "";
								}
 								
								// only set if there is a value
								if (Str.len(sValue) >= 1) {
									
									sTranFieldName = Str.toUpper(sTranFieldName);
									
									boolean bDoExtraLogging = false;
									if (bDoExtraLogging == true) {
										LIB.log("String value Tran Field Name: " + sTranFieldName , className); 
									}
									 
									TRANF_FIELD tranField = TRANF_FIELD.valueOf(sTranFieldName);

									if (bDoExtraLogging == true) {
	 									LIB.log("Enum value, the ID number, for Tran Field: " + tranField.toInt() , className); 
									}
 
									// this should not be needed, theoretically, since a -1 seqnum2 should work the same as not setting
									// however, in practice, some fields may be impacted, even if due to an OpenLink bug, so do it this way
									if (iSeqNum2 < 0) {
										// set without seqnum2
				 						TRANF_HELPER.safeSetTranfFieldWithErrorHandling(tran, tranField, iSideToSet, sValue, tOneDealErrors, className);
									} else {
										// set with seqnum2
				 						TRANF_HELPER.safeSetTranfFieldWithErrorHandling(tran, tranField, iSideToSet, iSeqNum2, sValue, tOneDealErrors, className);
									}
 								 
								}
							} catch (Exception e) { 
								LIB.log("getting the tran field name", e, className);
								
								String sErrorMessage = e.getLocalizedMessage();
								FUNC.ERROR_HANDLING.addErrorToTable(tOneDealErrors, ERROR_TYPE_ENUM.FIELD_SET, sDealNum, sErrorMessage, sTranFieldName);
							}
  							  
						}
					}
				} 
 				
				// TODO, add error handling for this
				TRANF_HELPER.safeSetTranfInfoField(tran, TRAN_INFO.SourceSystem, sSourceSystem, className);
				
				// Do this last (for no particular reason)
				{
					Table tExtraTranInfo = LIB.safeGetTable(tOneDeal, COLS.MAPPED_DEAL.tran_info_table, CONST_VALUES.ROW_TO_GET_OR_SET);
					int iNumExtraTranInfo = LIB.safeGetNumRows(tExtraTranInfo);
					if (iNumExtraTranInfo >= 1) {
						for (int iSubCounter = 1; iSubCounter <= iNumExtraTranInfo; iSubCounter++) {
							try {
								String sTranInfoType = LIB.safeGetString(tExtraTranInfo, COLS.TRAN_INFO_EXTRA_FIELDS.tran_info_name, iSubCounter);
								String sValue = LIB.safeGetString(tExtraTranInfo, COLS.TRAN_INFO_EXTRA_FIELDS.value, iSubCounter);
								if (sValue == null) {
									sValue = "";
								}
								// only set if there is a value
								if (Str.len(sValue) >= 1) {
									TRANF_HELPER.safeSetTranfInfoFieldByTranInfoStringName(tran, sTranInfoType, sValue, className);
								}
							} catch (Exception e) {
								// don't log this
							}
						}
					}
				} 
				
			} catch (Exception e) {
				// do not log this
			}
			return tran;
		}
	} // END STEP_3_CREATE_DEAL_IN_MEMORY
	 

	public static class STEP_4_BOOK_DEAL {
		
		
		public static boolean checkForFatalErrorSuchThatYouCanNotBookTheDeal(Transaction tran, Table tErrors, boolean bReadOnly, int iRunNumber, String className) throws OException {

			boolean bFatalError = false;
			try {
				
				String sDealNum = TRANF_HELPER.safeGetTranfInfoField(tran, TRAN_INFO.ExternalDealRef, className);
				{
					int iInternalPortfolio = TRANF_HELPER.safeGetTranfFieldInt(tran, TRANF_FIELD.TRANF_INTERNAL_PORTFOLIO, className);
					if (iInternalPortfolio <= 1) {
						bFatalError = true;
						String sErrorMessage = "Missing required field portfolio, so not going to try to book the trade";
						if (bReadOnly == true) {
							sErrorMessage = "Missing required field portfolio";
						}
						String sFieldName = "TRANF_INTERNAL_PORTFOLIO";
						FUNC.ERROR_HANDLING.addErrorToTable(tErrors, ERROR_TYPE_ENUM.MISSING_REQUIRED_FIELD, sDealNum, sErrorMessage, sFieldName);
					}
				}   

				{
					int iExternalBunit = TRANF_HELPER.safeGetTranfFieldInt(tran, TRANF_FIELD.TRANF_EXTERNAL_BUNIT, className);
					if (iExternalBunit <= 1) {
						bFatalError = true;
						String sErrorMessage = "Missing required field external bunit";
						String sFieldName = "TRANF_EXTERNAL_BUNIT";
						FUNC.ERROR_HANDLING.addErrorToTable(tErrors, ERROR_TYPE_ENUM.MISSING_REQUIRED_FIELD, sDealNum, sErrorMessage, sFieldName);
					}
				}   

				{
					final int COMMODITY_TOOLSET = 36;
					int iToolset = TRANF_HELPER.safeGetTranfFieldInt(tran, TRANF_FIELD.TRANF_TOOLSET_ID, className);
					if (iToolset == COMMODITY_TOOLSET) {
						int iNumSides = tran.getNumParams();
						for (int iCounter = 1; iCounter <= iNumSides; iCounter++) {
							// sides start at zero
							int iSide = iCounter - 1;
							int iPipeline = TRANF_HELPER.safeGetTranfFieldInt(tran, TRANF_FIELD.TRANF_PIPELINE, iSide, className);
							int iLocation = TRANF_HELPER.safeGetTranfFieldInt(tran, TRANF_FIELD.TRANF_LOCATION, iSide, className);
//							String sMessage = "For Side: " + iSide + " Pipeline: " + iPipeline + ", Location: " + iLocation;
//							LIB.log(sMessage, className);
							if (iPipeline >= 1) {
								if (iLocation < 1) {
									bFatalError = true;
									String sErrorMessage = "Missing required field location on side " + iSide + ". Check that the location is *both* existing and *active*.";
									
									String sFieldName = "TRANF_LOCATION for Side " + iSide;
									FUNC.ERROR_HANDLING.addErrorToTable(tErrors, ERROR_TYPE_ENUM.MISSING_REQUIRED_FIELD, sDealNum, sErrorMessage, sFieldName);
								}
							}
						}
					} 
				}   

				

				
			} catch (Exception e) {
				// do not log this
			} 
			return bFatalError;
		}
		
		public static int createDealInDatabase(Transaction tran, Table tErrors, int iRunNumber, String className) throws OException {
		 
			int iDealNum = -1;
			try {
				
				tran.insertByStatus(TRAN_STATUS_ENUM.TRAN_STATUS_VALIDATED);
				
				iDealNum = tran.getTranNum();
				
				// keep for now, but maybe remove later
				LIB.log("Deal Booked: " + iDealNum, className);
				
			} catch (Exception e) {
				String sDealNum = TRANF_HELPER.safeGetTranfInfoField(tran, TRAN_INFO.ExternalDealRef, className);
				String sErrorMessage = e.getLocalizedMessage();
				String sFieldName = "";
				FUNC.ERROR_HANDLING.addErrorToTable(tErrors, ERROR_TYPE_ENUM.DEAL_BOOKING, sDealNum, sErrorMessage, sFieldName);
			} 
			return iDealNum;
		}
		
		
		public static void bookDeal(int iCounter, int iNumDeals, String sDealNum, Table tOneDealSourceData, Table tTemplates, Table tOneDealErrors, boolean bReadOnly,
				String sSourceSystem, int iUniqueDbIdNumber, int iRunNumber, String className) throws OException {
		 
			try {
				
				if (iCounter % NUM_DEALS_PER_STATUS_UPDATE  == 0 || iCounter == iNumDeals) {
					String sStatus = "Processing Deal: " + iCounter + " of " + iNumDeals;
					Util.scriptPostStatus(sStatus);
				}
 				 
				Table tOneDealMappedData = FUNC.STEP_2_MAPPING.applyMappingToDeal(tOneDealSourceData, iRunNumber, className);
				 	
				final String sTemplateName = DEAL_BOOKING_TEMPLATE.COMM_PHYS_FIXED_PRICE.strValue();
				int iRowNum = tTemplates.unsortedFindString(COLS.TEMPLATES.template_name, sTemplateName, SEARCH_CASE_ENUM.CASE_INSENSITIVE);
  				
				if (iRowNum < 1) {
					String sErrorMessage = "Unknown issue locating Template Tran Num, not able to book the deal";
					FUNC.ERROR_HANDLING.addErrorToTable(tOneDealErrors, ERROR_TYPE_ENUM.MISSING_TRAN_INFO, sDealNum, sErrorMessage, sTemplateName);
				}

				if (iRowNum >= 1) {
					int iTemplateTranNum = LIB.safeGetInt(tTemplates, COLS.TEMPLATES.tran_num, iRowNum);
					 
					Table tOneDealActualData = tOneDealMappedData.copyTable();
					Transaction tran = FUNC.STEP_3_CREATE_DEAL_IN_MEMORY.createDealInMemory(tOneDealMappedData, tOneDealActualData, iTemplateTranNum, tOneDealErrors, sSourceSystem, iRunNumber, className);
		
					if (iCounter <= 2) {
						Table tCopy = tOneDealSourceData.copyTable();
						tCopy.insertCol("Data_Type", 1, COL_TYPE_ENUM.COL_STRING);
						tCopy.insertCol("Data_Type_ID", 1, COL_TYPE_ENUM.COL_INT);
						LIB.safeSetString(tCopy, "Data_Type", LIB.safeGetNumRows(tCopy), "Source Data");
						LIB.safeSetInt(tCopy, "Data_Type_ID", LIB.safeGetNumRows(tCopy), 1);
						tOneDealMappedData.copyRowAddAllByColName(tCopy);

						LIB.safeSetString(tCopy, "Data_Type", LIB.safeGetNumRows(tCopy), "Mapped Data");
						LIB.safeSetInt(tCopy, "Data_Type_ID", LIB.safeGetNumRows(tCopy), 2);

						tOneDealActualData.copyRowAddAllByColName(tCopy);

						LIB.safeSetString(tCopy, "Data_Type", LIB.safeGetNumRows(tCopy), "Actual Data");
						LIB.safeSetInt(tCopy, "Data_Type_ID", LIB.safeGetNumRows(tCopy), 3);

						if (iCounter == 1) {
							LIB.viewTable(tCopy, "Values for first deal: " + sDealNum);
						}
						if (iCounter == 2) {
							LIB.viewTable(tCopy, "Values for second deal: " + sDealNum);
						}
						tCopy = LIB.destroyTable(tCopy);
					}
					tOneDealActualData = LIB.destroyTable(tOneDealActualData);
 
 				 	boolean bFatalError = checkForFatalErrorSuchThatYouCanNotBookTheDeal(tran, tOneDealErrors, bReadOnly, iRunNumber, className);

 				 	// only put this message if read only is false and we found a fatal error
 					if (bReadOnly == false && bFatalError == true) {
 						String sErrorMessage = "*NOT* going to try to book this trade since it is missing required field(s)";
 						String sFieldName = "See Details";
 						FUNC.ERROR_HANDLING.addErrorToTable(tOneDealErrors, ERROR_TYPE_ENUM.MISSING_REQUIRED_FIELD, sDealNum, sErrorMessage, sFieldName);
 					}
  					
					int iDealNum = -1;
					// Only try to actually book the trade if both we are not in read only mode *AND* we did not detect any fatal errors
					if (bReadOnly == false && bFatalError == false) {
						iDealNum = FUNC.STEP_4_BOOK_DEAL.createDealInDatabase(tran, tOneDealErrors, iRunNumber, className);
					}
					
					// always log status if read only is false, even if there was a fatal error preventing us from trying to book the trade
					if (bReadOnly == false) {
						// Only update status if *not* read only
						FUNC.STEP_5_UPDATE_STATUS.updateDatabaseStatus(iDealNum, tOneDealErrors, iUniqueDbIdNumber, iRunNumber, className);
					}

					
					// Free up memory
					tran = LIB.destroyTran(tran);
 				}
  				
			} catch (Exception e) {
				// Do not log this
			} 
	
		}
	} // END STEP_4_BOOK_DEAL

	public static class STEP_5_UPDATE_STATUS {
		
		public static void updateDatabaseStatus(int iDealNum, Table tOneDealErrors, int iUniqueDbIdNumber, int iRunNumber, String className) throws OException {
		 
			try {
				String sInfoComment = "";
				String sStatus = DEAL_BOOKING_STATUS.SUCCESS;
				if (iDealNum < 1) {
					sStatus = DEAL_BOOKING_STATUS.FAILED;
					sInfoComment = "Unknown Deal Booking Error";
				}
				
				{
					int iNumErrors = LIB.safeGetNumRows(tOneDealErrors);
					// first pass, look only for deal booking errors
					for (int iCounter = 1; iCounter <= iNumErrors; iCounter++) {
						String sErrorType = LIB.safeGetString(tOneDealErrors, COLS.ERROR.error_type, iCounter);
						if (Str.iEqual(sErrorType, ERROR_TYPE_ENUM.DEAL_BOOKING.getDescription()) == CONST_VALUES.VALUE_OF_TRUE) {
							String sError = LIB.safeGetString(tOneDealErrors, COLS.ERROR.message, iCounter);
							sInfoComment = "Error: " + sError;
						} 
					}
					// second pass, look only for field set errors
					for (int iCounter = 1; iCounter <= iNumErrors; iCounter++) {
						String sErrorType = LIB.safeGetString(tOneDealErrors, COLS.ERROR.error_type, iCounter);
						if (Str.iEqual(sErrorType, ERROR_TYPE_ENUM.FIELD_SET.getDescription()) == CONST_VALUES.VALUE_OF_TRUE) {
							String sError = LIB.safeGetString(tOneDealErrors, COLS.ERROR.message, iCounter);
					
							// use 2 instead of 1 for no particular reason for adding a comma
							if (Str.len(sInfoComment) >= 2) {
								sInfoComment = sInfoComment + ", ";
							}
							sInfoComment = sInfoComment + sError;
						}
					}
					if (Str.len(sInfoComment) > 254) {
						sInfoComment = Str.substr(sInfoComment, 0, 254);
					}
				}
				 
				Table tTemp = Table.tableNew(USER_TABLE.user_di_staging);
				LIB.safeAddCol(tTemp, COLS.USER_DEAL_STAGING_TABLE.id_number, COL_TYPE_ENUM.COL_INT);
				LIB.safeAddCol(tTemp, COLS.USER_DEAL_STAGING_TABLE.status, COL_TYPE_ENUM.COL_STRING);
				LIB.safeAddCol(tTemp, COLS.USER_DEAL_STAGING_TABLE.info_comment, COL_TYPE_ENUM.COL_STRING);
				LIB.safeAddCol(tTemp, COLS.USER_DEAL_STAGING_TABLE.endur_deal_num, COL_TYPE_ENUM.COL_INT);
				
				int iMaxRow = tTemp.addRow();
				LIB.safeSetInt(tTemp, COLS.USER_DEAL_STAGING_TABLE.id_number, iMaxRow, iUniqueDbIdNumber);
				LIB.safeSetString(tTemp, COLS.USER_DEAL_STAGING_TABLE.status, iMaxRow, sStatus);
				LIB.safeSetString(tTemp, COLS.USER_DEAL_STAGING_TABLE.info_comment, iMaxRow, sInfoComment);
				LIB.safeSetInt(tTemp, COLS.USER_DEAL_STAGING_TABLE.endur_deal_num, iMaxRow, iDealNum);
  				
				tTemp.group(COLS.USER_DEAL_STAGING_TABLE.id_number);
 				
				DBUserTable.update(tTemp);
				
			} catch (Exception e) {
				// Do not log this
			} 
		}
	} // end class STEP_5_UPDATE_STATUS
	 
	
} // end class FUNC
		
	
public static class COLS {

 	public static class STANDARD_IMPORT_CVS {
		public static String deal_num = "deal_num";
		public static String template = "template";
		public static String counterparty = "counterparty";
		public static String start_date = "start_date";
		public static String end_date = "end_date";
		
		public static String buy_sell = "buy_sell";
		public static String quantity = "quantity";
		public static String price = "price";

		public static String internal_bunit = "internal_bunit";

		public static String portfolio = "portfolio";
		public static String external_portfolio = "external_portfolio";

		public static String pipeline = "pipeline";
		
		public static String zone = "zone";
		public static String zone2 = "zone2";
		public static String location = "location";
		public static String location2 = "location2";
		public static String location3 = "location3";
		public static String trade_date = "trade_date";
		public static String trader = "trader";
		public static String fixed_float = "fixed_float";

		public static String proj_index = "proj_index";
		public static String proj_index2 = "proj_index2";
		public static String spread = "spread";
		public static String broker = "broker";
		public static String broker_fee = "broker_fee";
		
		public static String variable_cost = "variable_cost";
		public static String variable_demand = "variable_demand";
 		
	 	public static class EXTRA {
			public static String int_start_date = "int_start_date";
			public static String int_end_date = "int_end_date";
			public static String sorted_table_row_num = "sorted_table_row_num";
			public static String proj_index1_to_use = "proj_index1_to_use";
			public static String proj_index2_to_use = "proj_index2_to_use";
	 	}
 	}
 	
 	public enum STANDARD_IMPORT_CVSx { 
 		deal_num ("deal_num"), 
 		template ("template"), 
 		counterparty ("counterparty"), 
 		start_date ("start_date"), 
 		end_date ("end_date"),  

 		buy_sell ("buy_sell"), 
 		quantity ("quantity"), 
 		price ("price"), 
 		portfolio ("portfolio"), 
 		pipeline ("pipeline"), 

 		zone ("zone"), 
 		location ("location"), 
 		trade_date ("trade_date"), 
 		trader ("trader"), 
 		fixed_float ("fixed_float"), 

 		proj_index ("proj_index"), 
 		proj_index2 ("proj_index2"), 
 		spread ("spread"), 
 		broker ("broker"), 
 		broker_fee ("broker_fee"), 

		;
  		 
		final private String sValue; 
     //   final private boolean bIsRequiredFlag;  
	      
        //Constructor
        private STANDARD_IMPORT_CVSx(String sValue) {
            this.sValue = sValue;
           // this.bIsRequiredFlag = bIsRequiredFlag; 
        }
        public String getStringColName() { 
            return this.sValue;
        }  
     //   public boolean getIsRequiredFlag() {
     //       return this.bIsRequiredFlag;
      //  }   
	} 
 	
	public static class USER_DEAL_STAGING_TABLE {
		public static String id_number = "id_number";
		public static String insert_time = "insert_time";
		public static String last_update = "last_update";
		public static String update_user = "update_user";
		public static String status = "status";
		public static String info_comment = "info_comment";
		public static String endur_deal_num = "endur_deal_num";
		public static String source_system = "source_system";
		public static String source_system_deal = "source_system_deal";
		public static String source_system_deal_int = "source_system_deal_int";
		public static String num_rows_for_deal = "num_rows_for_deal";		
		public static String extra_int1 = "extra_int1";
		public static String extra_int2 = "extra_int2";
		public static String extra_int3 = "extra_int3";
		public static String extra_string1 = "extra_string1";
		public static String extra_string2 = "extra_string2";
		public static String extra_string3 = "extra_string3";
		public static String the_blob = "the_blob";
	}

	abstract public static class DEAL_BASE_TABLE {
		public static String deal_num = "deal_num";
		public static String template = "template";
		public static String counterparty = "counterparty";		
		public static String start_date = "start_date";
		public static String end_date = "end_date";
		public static String buy_sell = "buy_sell";
		public static String quantity = "quantity";
		public static String price = "price";

		// For COMM-TRANS
		public static String variable_cost = "variable_cost";
		public static String variable_demand = "variable_demand";
 		

		public static String internal_bunit = "internal_bunit";
		public static String external_portfolio = "external_portfolio";


		public static String portfolio = "portfolio";
		public static String pipeline = "pipeline";
		public static String zone = "zone";
		public static String zone2 = "zone2";
		public static String location = "location";
		public static String location2 = "location2";
		public static String location3 = "location3";
		public static String trade_date = "trade_date";

		public static String trader = "trader"; 

		public static String fixed_float = "fixed_float";
		public static String proj_index = "proj_index";
		public static String proj_index2 = "proj_index2";
		public static String spread = "spread";

		public static String broker = "broker";
		public static String broker_fee = "broker_fee";

		public static String tran_info_table = "tran_info_table";
		public static String tranf_field_table = "tranf_field_table";
	}
	 
	public static class TEMPLATES {
		public static String tran_num = "tran_num";
		public static String template_name = "template_name";
	}
 	
	// columns only in this table
	public static class SOURCE_DEAL extends DEAL_BASE_TABLE{
		public static String user_table_unique_id = "user_table_unique_id";
		public static String source_system = "source_system";
	}
	
	// columns only in this table
	public static class MAPPED_DEAL extends DEAL_BASE_TABLE {
		
	}
 	 
	public static class TRAN_INFO_EXTRA_FIELDS {
		public static String tran_info_name = "tran_info_name"; 
		public static String value = "value"; 
	}
	 
	public static class TRANF_FIELD_EXTRA_FIELDS {
		public static String tran_field_name = "tran_field_name"; 
		public static String side = "side"; 
		public static String seq_num2 = "seq_num2"; 
		public static String value = "value"; 
	}

 
	public static class ERROR {
		public static String deal_num = "deal_num";
		public static String error_type = "error_type";
		public static String message = "message";
		public static String field_name = "field_name";
	}

}

// sample file

//deal_num,start_date,end_date,counterparty,quantity,price
//111,4/1/2023,5/1/2023,Acme Corp,1000,4.72
//112,4/2/2023,5/2/2023,Bravo Corp,10000,3.69
//113,4/3/2023,5/3/2023,Charlie Corp,1500,4.03


}
