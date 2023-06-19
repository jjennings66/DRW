package com.drw.dealimport;
    
import com.olf.openjvs.DBUserTable;
import com.olf.openjvs.DBaseTable;
import com.olf.openjvs.IContainerContext;
import com.olf.openjvs.IScript;
import com.olf.openjvs.OCalendar;
import com.olf.openjvs.OConsole;
import com.olf.openjvs.OException;
import com.olf.openjvs.PluginCategory;
import com.olf.openjvs.PluginType;
import com.olf.openjvs.Ref;
import com.olf.openjvs.Str;
import com.olf.openjvs.SystemUtil;
import com.olf.openjvs.Table;
import com.olf.openjvs.Transaction;
import com.olf.openjvs.Util;
import com.olf.openjvs.enums.COL_TYPE_ENUM;
import com.olf.openjvs.enums.DATE_FORMAT;
import com.olf.openjvs.enums.DATE_LOCALE;
import com.olf.openjvs.enums.SCRIPT_CATEGORY_ENUM;
import com.olf.openjvs.enums.SCRIPT_TYPE_ENUM;
import com.olf.openjvs.enums.SHM_USR_TABLES_ENUM;
import com.olf.openjvs.enums.TABLE_VIEWER_MODE;

/*
Script Name: DRW_DealImportPopulateUserTableFromCSV
Description:  DRW_DealImportPopulateUserTableFromCSV

Revision History
18-Apr-2023   Brian   New Script
25-Apr-2023   Brian   Updated user table name to user_di_staging

*/

@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_GENERIC)
@PluginType(SCRIPT_TYPE_ENUM.MAIN_SCRIPT)
public class DRW_DealImportPopulateUserTableFromCSV implements IScript {
	 
	public void execute(IContainerContext context) throws OException {
		String className = this.getClass().getSimpleName();
		int iRunNumber = DEBUG_LOGFILE.getRunNumber(className);
		boolean bDoFail = false;
		
		try {
			DEBUG_LOGFILE.logToFile("START", iRunNumber, className);
			bDoFail = doEverything(iRunNumber, className);
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
 
	public boolean doEverything(int iRunNumber, String className) throws OException {
		
		boolean bDoFail = false;
		try {
		 
			String sDirectory = Util.reportGetDirForToday();
 
			sDirectory = sDirectory + "/" + "csv";
			DEBUG_LOGFILE.logToFile("Source Directory is: " + sDirectory, iRunNumber, className);
			
			String sFileName = "Deals.csv";
			String sFullFilePath = sDirectory + "/" + sFileName;

			DEBUG_LOGFILE.logToFile("About to import from: " + sFullFilePath, iRunNumber, className);

			Table tData = Table.tableNew();
			
			tData.inputFromCSVFile(sFullFilePath);
 
			DEBUG_LOGFILE.logToFile("Number of Rows Found: " + LIB.safeGetNumRows(tData), iRunNumber, className);
			DEBUG_LOGFILE.logToFile("Number of Cols Found: " + LIB.safeGetNumCols(tData), iRunNumber, className);
			
			LIB.viewTable(tData, "source data CSV");
			
			if (LIB.safeGetNumRows(tData) >= 1) {
				// column names are in row 1
				int iNumCols = LIB.safeGetNumCols(tData);
				for (int iCounter = 1; iCounter <= iNumCols; iCounter++) {
					String sColName = tData.getString(iCounter, CONST_VALUES.ROW_TO_GET_OR_SET);
					tData.setColName(iCounter, sColName);
				}
				// delete the first row
				tData.delRow(1);
			}
			
			// if deal_num not found
			if (tData.getColNum(CONST_VALUES.deal_num) < 1) {
				String sColName = "Deal Num";
				if (tData.getColNum(sColName) >= 1) {
					tData.setColName(sColName, CONST_VALUES.deal_num);
				}
				sColName = "Deal Number";
				if (tData.getColNum(sColName) >= 1) {
					tData.setColName(sColName, CONST_VALUES.deal_num);
				}
				sColName = "Deal_Num";
				if (tData.getColNum(sColName) >= 1) {
					tData.setColName(sColName, CONST_VALUES.deal_num);
				}
			}
 
			// if no deal_num, then Fail
			if (tData.getColNum(CONST_VALUES.deal_num) < 1) {
				DEBUG_LOGFILE.logToFile(CONST_VALUES.deal_num + " is a required field. That field was not found, so going to stop this script and exit with a status of Failed" , iRunNumber, className);
				bDoFail = true;
			}

			if (bDoFail == false) { 
				DEBUG_LOGFILE.logToFile("Found this many rows in the import file: " + LIB.safeGetNumRows(tData), iRunNumber, className);
 				
				// Deal Number is required, so remove rows that have no deal number, and see if the row count changes
				int iNumRowsBefore = LIB.safeGetNumRows(tData);
				tData.deleteWhereString(CONST_VALUES.deal_num, "");
				int iNumRowsAfter = LIB.safeGetNumRows(tData);
				int iNumRowsDiff = iNumRowsBefore - iNumRowsAfter;
				if (iNumRowsDiff == 0) {
					DEBUG_LOGFILE.logToFile("Found a valid " + CONST_VALUES.deal_num + " in all rows, so going to proceed", iRunNumber, className);
				}
				if (iNumRowsDiff != 0) {
					DEBUG_LOGFILE.logToFile("This many rows where missing a valid " + CONST_VALUES.deal_num + ": " + iNumRowsDiff + ".  Deal Num is requried in all rows, so going to stop this script and exit with a status of Failed", iRunNumber, className);
					bDoFail = true;
				}
			}
   			
			// proceed
			if (bDoFail == false) { 
				this.insertIntoUserTable(tData, iRunNumber, className);
			}
  
			 // LIB.viewTable(tData, "Deal to Import Data");
			
		} catch (Exception e) {
			LIB.log("doEverything", e, className);
		}
		return bDoFail;
	}


public void insertIntoUserTable(Table tData, int iRunNumber, String className) throws OException {
		
		try {
			Table tUserTable = Table.tableNew();
			tUserTable.setTableName(USER_TABLE.user_di_staging);
			DBUserTable.structure(tUserTable);

			// Add start and end date
			LIB.safeAddCol(tData, "int_start_date", COL_TYPE_ENUM.COL_INT);
			LIB.safeAddCol(tData, "int_end_date", COL_TYPE_ENUM.COL_INT);

			{
				if (tData.getColNum("start_date") >= 1) {
					int iNumRows = LIB.safeGetNumRows(tData);
					for (int iCounter = 1; iCounter <= iNumRows; iCounter++) {
						
						try {
							String sValue = LIB.safeGetString(tData, "start_date", iCounter);
							int iValue = OCalendar.parseString(sValue);
							
							LIB.safeSetInt(tData, "int_start_date", iCounter, iValue);
						} catch (Exception e) {
							// do not log this
						}
					}
				}
			}

			{
				if (tData.getColNum("end_date") >= 1) {
					int iNumRows = LIB.safeGetNumRows(tData);
					for (int iCounter = 1; iCounter <= iNumRows; iCounter++) {
						
						try {
							String sValue = LIB.safeGetString(tData, "end_date", iCounter);
							int iValue = OCalendar.parseString(sValue);
							
							LIB.safeSetInt(tData, "int_end_date", iCounter, iValue);
						} catch (Exception e) {
							// do not log this
						}
					}
				}
			}

			int iDate = OCalendar.getServerDate();
			int iTime = Util.timeGetServerTime();
			int iUser = Ref.getUserId();
			
			// sorting on deal num is functional (not cosmetic)
			tData.group(CONST_VALUES.deal_num);
			
			// Add this temporary column for the Sub Table
			final String TEMP_COL_NAME = "TEMP_COL_NAME";
			LIB.safeAddCol(tUserTable, TEMP_COL_NAME, COL_TYPE_ENUM.COL_TABLE);
			
			{
				String sPriorDealNum = "TempXXX";
				Table tSubTable = Util.NULL_TABLE; 
				int iMaxRow = -1;
				int iNumRows = LIB.safeGetNumRows(tData);
				for (int iCounter = 1; iCounter <= iNumRows; iCounter++) {
 					
					String sDealNum = LIB.safeGetString(tData, CONST_VALUES.deal_num, iCounter);
					boolean bAddNewRow = false;
					if (Str.iEqual(sDealNum, sPriorDealNum) != CONST_VALUES.VALUE_OF_TRUE) {
						bAddNewRow = true;
					}
					if (bAddNewRow == true) {
						tSubTable = tData.cloneTable();
						iMaxRow = tUserTable.addRow();
						
						tUserTable.setDateTimeByParts(COLS.USER_DEAL_STAGING_TABLE.insert_time, iMaxRow, iDate, iTime);
						tUserTable.setDateTimeByParts(COLS.USER_DEAL_STAGING_TABLE.last_update, iMaxRow, iDate, iTime);
	 
						LIB.safeSetInt(tUserTable, COLS.USER_DEAL_STAGING_TABLE.id_number, iMaxRow, DBUserTable.getUniqueId());
						LIB.safeSetString(tUserTable, COLS.USER_DEAL_STAGING_TABLE.source_system_deal, iMaxRow, sDealNum);
  
						LIB.safeSetTable(tUserTable, TEMP_COL_NAME, iMaxRow, tSubTable);
					}

					tData.copyRowAddByColName(iCounter, tSubTable);
					 
					sPriorDealNum = sDealNum;
				}
			}
 			
			// Now populate table level columns
			LIB.safeSetColValInt(tUserTable, COLS.USER_DEAL_STAGING_TABLE.update_user, iUser);
			LIB.safeSetColValString(tUserTable, COLS.USER_DEAL_STAGING_TABLE.source_system, SOURCE_SYSTEM.QUORUM);
			
			// Second run to populate the bytearray
			int iNumRows = LIB.safeGetNumRows(tData);
			for (int iCounter = 1; iCounter <= iNumRows; iCounter++) {
				Table tOneDealTable = LIB.safeGetTable(tUserTable, TEMP_COL_NAME, iCounter);
				tUserTable.setTableToByteArray(COLS.USER_DEAL_STAGING_TABLE.the_blob, iCounter, tOneDealTable);
				int iSubNumRows = LIB.safeGetNumRows(tOneDealTable);

				LIB.safeSetInt(tUserTable, COLS.USER_DEAL_STAGING_TABLE.num_rows_for_deal, iCounter, iSubNumRows);
				
				String sDealNum = LIB.safeGetString(tUserTable, COLS.USER_DEAL_STAGING_TABLE.source_system_deal, iCounter);
				
				int iDealNum = 0;
				try {
					if (Str.isInt(sDealNum) == CONST_VALUES.VALUE_OF_TRUE) {
						iDealNum = Str.strToInt(sDealNum);
						LIB.safeSetInt(tUserTable, COLS.USER_DEAL_STAGING_TABLE.source_system_deal_int, iCounter, iDealNum);
					}
				} catch (Exception e) {
					// do not log this
				}
			}
			
			// Delete the temp col
			LIB.safeDelCol(tUserTable, TEMP_COL_NAME);
			
//			
//			int iNumRows = LIB.safeGetNumRows(tData);
//			for (int iCounter = 1; iCounter <= iNumRows; iCounter++) {
//				Table tOneDealTable = tData.cloneTable();
//				tData.copyRowAddByColName(iCounter, tOneDealTable);
//				
//				int iMaxRow = tUserTable.addRow();
//				
//				String sDealNum = LIB.safeGetString(tData, CONST_VALUES.deal_num, iCounter);
//						
//				
//				tUserTable.setDateTimeByParts(COLS.USER_DEAL_STAGING_TABLE.insert_time, iMaxRow, iDate, iTime);
//				tUserTable.setDateTimeByParts(COLS.USER_DEAL_STAGING_TABLE.last_update, iMaxRow, iDate, iTime);
//
//				LIB.safeSetInt(tUserTable, COLS.USER_DEAL_STAGING_TABLE.update_user, iMaxRow, iUser);
//				LIB.safeSetInt(tUserTable, COLS.USER_DEAL_STAGING_TABLE.id_number, iMaxRow, DBUserTable.getUniqueId());
//				LIB.safeSetString(tUserTable, COLS.USER_DEAL_STAGING_TABLE.source_system_deal, iMaxRow, sDealNum);
//				
//				tUserTable.setTableToByteArray(COLS.USER_DEAL_STAGING_TABLE.the_blob, iCounter, tOneDealTable);
//				
//				int iDealNum = 0;
//				try {
//					if (Str.isInt(sDealNum) == CONST_VALUES.VALUE_OF_TRUE) {
//						iDealNum = Str.strToInt(sDealNum);
//						LIB.safeSetInt(tUserTable, COLS.USER_DEAL_STAGING_TABLE.source_system_deal_int, iMaxRow, iDealNum);
//					}
//				} catch (Exception e) {
//					// do not log this
//				}
//				
//				// LIB.viewTable(tOneDealTable, "one deal");
//				
//				tOneDealTable = LIB.destroyTable(tOneDealTable);
//			}
			
			LIB.viewTable(tUserTable, USER_TABLE.user_di_staging + ", Num Rows: " + LIB.safeGetNumRows(tUserTable));
		 	
			
			// this is required
			tUserTable.group("id_number");
			
			
			// Does not work
///			DBUserTable.bcpIn(tTest);
			DBUserTable.insert(tUserTable);
			
		} catch (Exception e) {
			LIB.log("insertIntoUserTable", e, className);
		}
	}
	

public static class USER_TABLE {
	public static String user_di_staging = "user_di_staging";
}


public static class COLS {

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

}


public static class SOURCE_SYSTEM {
	public static final String QUORUM = "Quorum";
} 

   

public static class CONST_VALUES {
	public static final int VALUE_OF_TRUE = 1;
	public static final int VALUE_OF_FALSE = 0;

	public static final int VALUE_OF_NOT_APPLICABLE = -1;

	public static final double A_SMALL_NUMBER = 0.000001;

	public static final int ROW_TO_GET_OR_SET = 1;
	
	public static final String deal_num = "deal_num";
	

}

public static class DEBUG_LOGFILE {

	public static void logToFile(String sMessage, int iRunNumber, String className) {
		try {

			  
			double dMemSize = SystemUtil.memorySizeDouble();
			double tMemSizeMegs = dMemSize / 1024 / 1024;
			String sMemSize = Str.doubleToStr(tMemSizeMegs, 2) + " megs";

			String sNewMessage = "Time:" + Util.timeGetServerTimeHMS() + "|" + sMemSize + "|" + "Version:"
						+ LIB.VERSION_NUMBER + "| " + sMessage;

			// need a newline
			sNewMessage = sNewMessage + "\r\n";

			String sFileName = className + "." + "TroubleshootingLog" + "." + iRunNumber + "." + "txt";

			String sReportDir = Util.reportGetDirForToday();
			String sFullPath = sReportDir + "/" + sFileName;

			int iAppendFlag = 1;
			Str.printToFile(sFullPath, sNewMessage, iAppendFlag);

			LIB.logForDebug(sMessage, className);

		} catch (Exception e) {
			// don't log this
		}
	}

	public static int getRunNumber(String className) {

		int iReturn = 0;
		try {
			iReturn = DBUserTable.getUniqueId();
		} catch (Exception e) {
			// don't log this
		}
		try {
			if (iReturn < 1) {
				// if the above didn't work, just use seconds since midnight
				iReturn = Util.timeGetServerTime();
			}
		} catch (Exception e) {
			// don't log this
		}
		return iReturn;
	}

} // END class DEBUG_LOGFILE

public static class LIB {

	public static final String VERSION_NUMBER = "V1.006 (25Apr2023)";

	public static void select(Table tDestination, Table tSourceTable, String sWhat, String sWhere, String className)
			throws OException {
		LIB.select(tDestination, tSourceTable, sWhat, sWhere, false, className);
	}

	private static void select(Table tDestination, Table tSourceTable, String sWhat, String sWhere,
			boolean bLogFlag, String className) throws OException {
		try {
			tDestination.select(tSourceTable, sWhat, sWhere);
		} catch (Exception e) {
			LIB.log("select", e, className);
		}
	}

	public static void viewTable(Table tMaster) throws OException {
		try {
			tMaster.scriptDataHideIconPanel();
			tMaster.setTableViewerMode(TABLE_VIEWER_MODE.TABLE_VIEWER_LEGACY);
			String sTableName = tMaster.getTableName();
			String sTableTitle = sTableName + ", " + "Number of Rows: " + LIB.safeGetNumRows(tMaster);
			LIB.viewTable(tMaster, sTableTitle);
		} catch (Exception e) {
			// don't log this
		}
	}

	public static void viewTable(Table tMaster, String sTableTitle) throws OException {
		try {
			tMaster.scriptDataHideIconPanel();
			tMaster.setTableViewerMode(TABLE_VIEWER_MODE.TABLE_VIEWER_LEGACY);
			tMaster.setTableTitle(sTableTitle);
			tMaster.viewTable();

		} catch (Exception e) {
			// don't log this
		}
	}

	public static void logForDebug(String sMessage, String className) throws OException {
		try {
			 LIB.log(sMessage, className);
			
		} catch (Exception e) {
			// don't log this
		}
	}

	public static void log(String sMessage, String className) throws OException {
		try {

			double dMemSize = SystemUtil.memorySizeDouble();
			double tMemSizeMegs = dMemSize / 1024 / 1024;
			String sMemSize = Str.doubleToStr(tMemSizeMegs, 2) + " megs";

			OConsole.oprint("\n" + className + ":" + LIB.VERSION_NUMBER + ":" + Util.timeGetServerTimeHMS() + ":"
					+ sMemSize + ": " + sMessage);
		} catch (Exception e) {
			// don't log this
		}
	}

	public static void log(String sMessage, Exception e, String className) throws OException {
		try {
			LIB.log("ERROR: " + sMessage + ":" + e.getLocalizedMessage(), className);
		} catch (Exception e1) {
			// don't log this
		}
	}

	public static Table destroyTable(Table tDestroy) throws OException {
		try {
			if (Table.isTableValid(tDestroy) == 1) {
				// clear rows seems to help free up memory quicker
				tDestroy.clearRows();
				tDestroy.destroy();
			}
		} catch (Exception e) {
			// don't log this
		}
		return Util.NULL_TABLE;
	}

	public static void adjustStrategyNameIfNeeded(Table tData, String sStrategyColName, int iRunNumber,
			String className) throws OException {

		try {

			// characters start at 0, not 1
			final int CHAR_NUMBER_OF_FIRST_CHAR = 0;
			final String CHAR_THAT_INDICATES_OLD_STRATEGY_NAME = "X";

			// in the unlikely event that the ref copy didn't work, make the
			// 'name' be the ID number
			int iNumRows = LIB.safeGetNumRows(tData);
			for (int iCounter = 1; iCounter <= iNumRows; iCounter++) {

				// try/catch for each loop
				try {
					// Check for null
					String sStrategyName = LIB.safeGetString(tData, sStrategyColName, iCounter);
					if (sStrategyName == null) {
						sStrategyName = "";
					}

					// get length
					int iLen = Str.len(sStrategyName);

					// Strip out a leading X, if found and at least 2
					// characters
					if (sStrategyName != null && iLen >= 2) {
						String sUpperCaseName = Str.toUpper(sStrategyName);
						if (Str.findSubString(sUpperCaseName,
								CHAR_THAT_INDICATES_OLD_STRATEGY_NAME) == CHAR_NUMBER_OF_FIRST_CHAR) {
							// Use 'Start Char' to make sure that the 'Chars
							// To Get' is always valid
							int iStartChar = 1;
							int iCharsToGet = iLen - iStartChar;
							String sNewStrategyName = Str.substr(sStrategyName, iStartChar, iCharsToGet);

							LIB.safeSetString(tData, sStrategyColName, iCounter, sNewStrategyName);
						}
					}

				} catch (Exception e) {
					// don't log this
				}

			}

		} catch (Exception e) {
			LIB.log("adjustStrategyNameIfNeeded", e, className);
		}
	}

	public static Transaction destroyTran(Transaction tDestroy) throws OException {
		try {
			if (Transaction.isNull(tDestroy) != CONST_VALUES.VALUE_OF_TRUE) {
				tDestroy.destroy();
			}
		} catch (Exception e) {
			// don't log this
		}
		return Util.NULL_TRAN;
	}

	public static int safeGetInt(Table tMaster, String sColName, int iRowNum) throws OException {
		int iReturn = 0;
		try {
			if (Table.isTableValid(tMaster) == 1) {
				if (tMaster.getColNum(sColName) >= 1) {
					iReturn = tMaster.getInt(sColName, iRowNum);
				} else {
					LIB.log("Can't find a column named " + "'" + sColName + "'" + " for " + "safeGetInt", "");
				}
			}
		} catch (Exception e) {
			// don't log this
		}
		return iReturn;
	}

	public static String safeGetString(Table tMaster, String sColName, int iRowNum) throws OException {
		String sReturn = "";
		try {
			if (Table.isTableValid(tMaster) == 1) {
				if (tMaster.getColNum(sColName) >= 1) {
					sReturn = tMaster.getString(sColName, iRowNum);
				} else {
					LIB.log("Can't find a column named " + "'" + sColName + "'" + " for " + "safeGetString", "");
				}
			}
		} catch (Exception e) {
			// don't log this
		}
		return sReturn;
	}

	public static double safeGetDouble(Table tMaster, String sColName, int iRowNum) throws OException {
		double dReturn = 0;
		try {
			if (Table.isTableValid(tMaster) == 1) {
				if (tMaster.getColNum(sColName) >= 1) {
					dReturn = tMaster.getDouble(sColName, iRowNum);
				} else {
					LIB.log("Can't find a column named " + "'" + sColName + "'" + " for " + "safeGetDouble", "");
				}
			}
		} catch (Exception e) {
			// don't log this
		}
		return dReturn;
	}

	public static Table safeGetTable(Table tMaster, String sColName, int iRowNum) throws OException {
		Table tReturn = Util.NULL_TABLE;
		try {
			if (Table.isTableValid(tMaster) == 1) {
				if (tMaster.getColNum(sColName) >= 1) {
					tReturn = tMaster.getTable(sColName, iRowNum);
				} else {
					LIB.log("Can't find a column named " + "'" + sColName + "'" + " for " + "safeGetTable", "");
				}
			}
		} catch (Exception e) {
			// don't log this
		}
		return tReturn;
	}

	public static void safeSetColValInt(Table tMaster, String sColName, int iValue) throws OException {
		try {
			if (Table.isTableValid(tMaster) == 1) {
				if (tMaster.getColNum(sColName) >= 1) {
					tMaster.setColValInt(sColName, iValue);
				} else {
					LIB.log("Can't find a column named " + "'" + sColName + "'" + " for " + "safeSetColValInt", "");
				}
			}
		} catch (Exception e) {
			// don't log this
		}
	}

	public static void safeSetColValDouble(Table tMaster, String sColName, double dValue) throws OException {
		try {
			if (Table.isTableValid(tMaster) == 1) {
				if (tMaster.getColNum(sColName) >= 1) {
					tMaster.setColValDouble(sColName, dValue);
				} else {
					LIB.log("Can't find a column named " + "'" + sColName + "'" + " for " + "safeSetColValDouble",
							"");
				}
			}
		} catch (Exception e) {
			// don't log this
		}
	}

	public static void safeSetColValString(Table tMaster, String sColName, String sValue) throws OException {
		try {
			if (Table.isTableValid(tMaster) == 1) {
				if (tMaster.getColNum(sColName) >= 1) {
					tMaster.setColValString(sColName, sValue);
				} else {
					LIB.log("Can't find a column named " + "'" + sColName + "'" + " for " + "safeSetColValString",
							"");
				}
			}
		} catch (Exception e) {
			// don't log this
		}
	}

	public static void safeSetTable(Table tMaster, String sColName, int iRowNum, Table tSubTable)
			throws OException {
		try {
			if (Table.isTableValid(tMaster) == 1) {
				if (tMaster.getColNum(sColName) >= 1) {
					tMaster.setTable(sColName, iRowNum, tSubTable);
				} else {
					LIB.log("Can't find a column named " + "'" + sColName + "'" + " for " + "safeSetTable", "");
				}
			}
		} catch (Exception e) {
			// don't log this
		}
	}

	public static void safeSetTableWithSetTableTitle(Table tMaster, String sColName, int iRowNum, Table tSubTable,
			String sTableTitle) throws OException {
		try {
			if (Table.isTableValid(tMaster) == 1) {
				if (tMaster.getColNum(sColName) >= 1) {

					tSubTable.setTableName(sTableTitle);
					tSubTable.setTableTitle(sTableTitle);

					tMaster.setTable(sColName, iRowNum, tSubTable);
				} else {
					LIB.log("Can't find a column named " + "'" + sColName + "'" + " for " + "safeSetTable", "");
				}
			}
		} catch (Exception e) {
			// don't log this
		}
	}

	public static void safeSetInt(Table tMaster, String sColName, int iRowNum, int iValue) throws OException {
		try {
			if (Table.isTableValid(tMaster) == 1) {
				if (tMaster.getColNum(sColName) >= 1) {
					tMaster.setInt(sColName, iRowNum, iValue);
				} else {
					LIB.log("Can't find a column named " + "'" + sColName + "'" + " for " + "safeSetInt", "");
				}
			}
		} catch (Exception e) {
			// don't log this
		}
	}

	public static void safeSetString(Table tMaster, String sColName, int iRowNum, String sValue) throws OException {
		try {
			if (Table.isTableValid(tMaster) == 1) {
				if (tMaster.getColNum(sColName) >= 1) {
					tMaster.setString(sColName, iRowNum, sValue);
				} else {
					LIB.log("Can't find a column named " + "'" + sColName + "'" + " for " + "safeSetString", "");
				}
			}
		} catch (Exception e) {
			// don't log this
		}
	}

	public static void safeSetDouble(Table tMaster, String sColName, int iRowNum, double dValue) throws OException {
		try {
			if (Table.isTableValid(tMaster) == 1) {
				if (tMaster.getColNum(sColName) >= 1) {
					tMaster.setDouble(sColName, iRowNum, dValue);
				} else {
					LIB.log("Can't find a column named " + "'" + sColName + "'" + " for " + "safeSetDouble", "");
				}
			}
		} catch (Exception e) {
			// don't log this
		}
	}

	public static void safeAddCol(Table tMaster, String sColName, COL_TYPE_ENUM colType) throws OException {
		try {
			if (Table.isTableValid(tMaster) == 1) {
				// Only add if not already there
				if (tMaster.getColNum(sColName) < 1) {
					tMaster.addCol(sColName, colType);
				}
			}
		} catch (Exception e) {
			// don't log this
		}
	}

	public static void safeDelCol(Table tMaster, String sColName) throws OException {
		try {
			if (Table.isTableValid(tMaster) == 1) {
				// Only delete if already there
				if (tMaster.getColNum(sColName) >= 1) {
					tMaster.delCol(sColName);
				}
			}
		} catch (Exception e) {
			// don't log this
		}
	}

	public static void safeColHide(Table tMaster, String sColName) throws OException {
		try {
			if (Table.isTableValid(tMaster) == 1) {
				// Only hide if already there
				if (tMaster.getColNum(sColName) >= 1) {
					tMaster.colHide(sColName);
				}
			}
		} catch (Exception e) {
			// don't log this
		}
	}

	public static int safeGetNumRows(Table tMaster) throws OException {
		int iReturn = 0;
		try {
			if (Table.isTableValid(tMaster) == 1) {
				iReturn = tMaster.getNumRows();
			}
		} catch (Exception e) {
			// don't log this
		}
		return iReturn;
	}

	public static int safeGetNumCols(Table tMaster) throws OException {
		int iReturn = 0;
		try {
			if (Table.isTableValid(tMaster) == 1) {
				iReturn = tMaster.getNumCols();
			}
		} catch (Exception e) {
			// don't log this
		}
		return iReturn;
	}

	public static void execISql(Table tMaster, String sSQL, String className) throws OException {
		LIB.execISql(tMaster, sSQL, true, className);
	}

	public static void execISql(Table tMaster, String sSQL, boolean bLogFlag, String className) throws OException {
		try {
			if (bLogFlag == true) {
				LIB.log("About to run this SQL:\n\t\t" + sSQL, className);
			}
			DBaseTable.execISql(tMaster, sSQL);
			if (bLogFlag == true) {
				LIB.log("Got back this many rows: " + LIB.safeGetNumRows(tMaster), className);
			}
		} catch (Exception e) {
			LIB.log("execISql", e, className);
		}
	}

	public static void loadFromDbWithWhatWhere(Table tMaster, String db_tablename, Table tIDNumbers, String sWhat,
			String sWhere, boolean bLogFlag, String className) throws OException {
		try {
			if (bLogFlag == true) {
				LIB.log("About to run DBaseTable.loadFromDbWithWhatWhere with this WHAT and FROM:\n\t\t" + sWhat
						+ " FROM " + db_tablename + ", and this many ID Values: " + LIB.safeGetNumRows(tIDNumbers),
						className);
			}
			DBaseTable.loadFromDbWithWhatWhere(tMaster, db_tablename, tIDNumbers, sWhat, sWhere);
			if (bLogFlag == true) {
				LIB.log("Got back this many rows: " + LIB.safeGetNumRows(tMaster), className);
			}
		} catch (Exception e) {
			LIB.log("execISql", e, className);
		}
	}

	public static void safeSetColFormatAsRef(Table tMaster, String sColName, SHM_USR_TABLES_ENUM refEnum)
			throws OException {
		try {
			tMaster.setColFormatAsRef(sColName, refEnum);
		} catch (Exception e) {
			// don't log this
		}
	}

	public static void safeSetColFormatAsDate(Table tMaster, String sColName) throws OException {
		try {
			tMaster.setColFormatAsDate(sColName, DATE_FORMAT.DATE_FORMAT_DMLY_NOSLASH, DATE_LOCALE.DATE_LOCALE_US);
		} catch (Exception e) {
			// don't log this
		}
	}

} // END LIB


}
