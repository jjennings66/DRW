package com.drw.deal_import;
  

import com.olf.openjvs.DBUserTable;
import com.olf.openjvs.DBaseTable;
import com.olf.openjvs.IContainerContext;
import com.olf.openjvs.IScript;
import com.olf.openjvs.OConsole;
import com.olf.openjvs.OException;
import com.olf.openjvs.PluginCategory;
import com.olf.openjvs.PluginType;
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
import com.olf.openjvs.enums.TABLE_SORT_DIR_ENUM;
import com.olf.openjvs.enums.TABLE_VIEWER_MODE;

/*
Script Name: DRW_ConvertSourceQuorumFileToImportFormat
Description:  DRW_ConvertSourceQuorumFileToImportFormat

Revision History
13-Apr-2023   Brian   New Script
14-Apr-2023		Joe		Update
1-May-2023		Joe		Update for storage deals
31-May-2023		Joe		Update for Int/Ext Pfolios
12-Jun-2023		Joe		Troubleshooting for new extract file
13-Jun-2023		Joe		Added deal numbers to NOT book

*/

@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_GENERIC)
@PluginType(SCRIPT_TYPE_ENUM.MAIN_SCRIPT)
public class DRW_ConvertSourceQuorumFileToImportFormat implements IScript {
	
	
	public void execute(IContainerContext context) throws OException {
		String className = this.getClass().getSimpleName();
		int iRunNumber = DEBUG_LOGFILE.getRunNumber(className); 
		
		try {
			DEBUG_LOGFILE.logToFile("START", iRunNumber, className);
			
//			Table argt = context.getArgumentsTable();
//			int iReadOnly = LIB.safeGetInt(argt, "read_only", CONST_VALUES.ROW_TO_GET_OR_SET);
			
			doEverything(iRunNumber, className);
			DEBUG_LOGFILE.logToFile("END", iRunNumber, className);
			 
		} catch (Exception e) {
			// do not log this
		}
		Util.exitSucceed();
	}

	public void doEverything(int iRunNumber, String className) throws OException {
		Table tSourceData = Util.NULL_TABLE;
		Table tOutput = Util.NULL_TABLE;
		try {

			String sDirectory = Util.reportGetDirForToday();
			 
			sDirectory = sDirectory + "/" + "csv";
			DEBUG_LOGFILE.logToFile("Source Directory is: " + sDirectory, iRunNumber, className);
			String sFileName = "source.csv";
			String sFullFilePath = sDirectory + "/" + sFileName;
			DEBUG_LOGFILE.logToFile("About to import from: " + sFullFilePath, iRunNumber, className);
			Table tData = this.getOriginalSourceData(sFullFilePath, iRunNumber, className);
			DEBUG_LOGFILE.logToFile("SOURCE DATA: Number of Rows: " + LIB.safeGetNumRows(tData) + ", Number of Columns: " + LIB.safeGetNumCols(tData), iRunNumber, className);
				
			tOutput = tData.copyTable();
			DEBUG_LOGFILE.logToFile("DESTINATION: Number of Rows: " + LIB.safeGetNumRows(tOutput) + ", Number of Columns: " + LIB.safeGetNumCols(tOutput), iRunNumber, className);
 
			Table tTrackingTable = Table.tableNew();
			for (int iCounter = 1; iCounter <= LIB.safeGetNumCols(tOutput); iCounter++) {
				LIB.safeAddCol(tTrackingTable, "col" + iCounter, COL_TYPE_ENUM.COL_INT);
			}
			tTrackingTable.addRow();
			
			this.updateColumnNames(tOutput, tTrackingTable, iRunNumber, className);
			
			// delete the first row.. that was column names
			tOutput.delRow(1);
			{
				int iNumCols = LIB.safeGetNumCols(tOutput);
				// count backwards
				for (int iColCounter = iNumCols; iColCounter >= 1; iColCounter--){
					// assume tTrackingTable has the same number of columns and structure
					int iColIsUsedFlag = tTrackingTable.getInt(iColCounter, CONST_VALUES.ROW_TO_GET_OR_SET);
					// delete if not used
					if (iColIsUsedFlag == CONST_VALUES.VALUE_OF_FALSE) {
						tOutput.delCol(iColCounter);
					}
				}
			}
			
			LIB.safeAddCol(tOutput, "portfolio", COL_TYPE_ENUM.COL_STRING);
			LIB.safeAddCol(tOutput, "fixed_float", COL_TYPE_ENUM.COL_STRING);
			LIB.safeAddCol(tOutput, "proj_index2", COL_TYPE_ENUM.COL_STRING);
			LIB.safeAddCol(tOutput, "TRANF_EXTERNAL_PORTFOLIO.0", COL_TYPE_ENUM.COL_STRING);
			LIB.safeAddCol(tOutput, "QUANTITY_NOT_DIV", COL_TYPE_ENUM.COL_STRING);
			LIB.safeAddCol(tOutput, "TRAN_INFO_ExternalCounterparty", COL_TYPE_ENUM.COL_STRING);
			LIB.safeAddCol(tOutput, "TRAN_INFO_ExternalLocation", COL_TYPE_ENUM.COL_STRING);
			LIB.safeAddCol(tOutput, "TRANF_ALLOW_PATHING.0", COL_TYPE_ENUM.COL_STRING);
			
			String sDestinationFileName = "deal_import.csv";
			String sDestinationFullFilePath = sDirectory + "/" + sDestinationFileName;
			DEBUG_LOGFILE.logToFile("About to write to: " + sDestinationFullFilePath, iRunNumber, className);
			tOutput.printTableDumpToFile(sDestinationFullFilePath);
			
			String[] sIns_Types = {"fixed","fixed_swap", "swap", "index","transport","storage","fee","broker_fee"};
			String[] sDoNotBook = { "354", "360", "1067", "5452", "9901", "17642", "19029", "29142", "30323", "32659",
					"32942", "32943", "35481", "35647", "37836", "39182", "39688", "40055", "40056", "40057", "40059",
					"40061", "40062", "40063", "40064", "40065", "40066", "40067", "40068", "40069", "40070", "40071",
					"40072", "40073", "40074", "41065", "41252", "41253", "41254", "41289", "42009", "42144", "42687",
					"42872", "43391", "43392", "43501", "44102", "44671", "45998", "48837", "54418", "57106", "60190",
					"60606", "61211", "64381", "65960", "68874", "68947", "70391", "71550", "72816", "74326", "74327",
					"74328", "74331", "74350", "74537", "75029", "75220", "75238", "75239", "75615", "76165", "76374",
					"76400", "76401", "76403", "76404", "76406", "76408", "76413", "76414", "76415", "76416", "76417",
					"76418", "77208", "77228", "77371", "77455", "77456", "77457", "77471", "77556", "77751", "77842",
					"78141", "78147", "78151", "78522", "78523", "78524", "79690", "79772", "80397", "80404", "80481",
					"80587", "80589", "80785", "80803", "80804", "80846", "80882", "80972", "81102", "81104", "81105",
					"81195", "81196", "81281", "81284", "81285", "81343", "81363", "81418", "81428", "81485", "81498",
					"81567", "81643", "81654", "81708", "81734", "81805", "81834", "81915", "81932", "81944", "82010",
					"82011", "82016", "82022", "82036", "82087", "82098", "82108", "82189", "82192", "82196", "82249",
					"82298", "82302", "82304", "82383", "82438", "82522", "82601", "82607", "82609", "82666", "82765",
					"82766", "82850", "82939", "83100", "83477", "83478", "83978", "83979", "84004", "84039", "84063",
					"84076", "84174", "85482", "85873", "86671", "86728", "86731", "86790", "86856", "86916", "86984",
					"87080", "87138", "87145", "87159", "87249", "87326", "87390", "87391", "87401", "87473", "87492",
					"87523", "87539", "87604", "87689", "87698", "87712", "87756", "87781", "87858", "87860", "87861",
					"87896", "87900", "87963", "88021", "88031", "88032" };
			DEBUG_LOGFILE.logToFile("Made arrays",iRunNumber, className);
			
			Table tFixedTemp= tOutput.cloneTable();
			tFixedTemp.addRow();
			Table tFixedSwapTemp=tOutput.cloneTable();
			tFixedSwapTemp.addRow();
			Table tSwapTemp=tOutput.cloneTable();
			tSwapTemp.addRow();
			Table tIndexTemp=tOutput.cloneTable();
			tIndexTemp.addRow();
			Table tTransportTemp=tOutput.cloneTable();
			tTransportTemp.addRow();
			Table tStorageTemp=tOutput.cloneTable();
			tStorageTemp.addRow();
			//tStorageTemp.addCol("location3", COL_TYPE_ENUM.COL_STRING);
			Table tFeeTemp=tOutput.cloneTable();
			tFeeTemp.addRow();
			Table tBrokerFeeTemp=tOutput.cloneTable();
			tBrokerFeeTemp.addRow();
			Table tEmptyTemp=tOutput.cloneTable();
			tEmptyTemp.addRow();
			
			String sSQL="select original_value, mapped_value from user_di_mapping_portfolio";
			Table tUserPfolio = Table.tableNew();
			LIB.execISql(tUserPfolio, sSQL, false, className);
			sSQL="select original_value, mapped_value from user_di_mapping_curve";
			Table tUserCurve = Table.tableNew();
			LIB.execISql(tUserCurve, sSQL, false, className);
			sSQL="select original_value, mapped_value from user_di_mapping_counterparty";
			Table tUserParty = Table.tableNew();
			LIB.execISql(tUserParty, sSQL, false, className);
			sSQL="select original_value, mapped_value from user_di_mapping_location";
			Table tUserLocation = Table.tableNew();
			LIB.execISql(tUserLocation, sSQL, false, className);
			sSQL="select location_name as mapped_value, active  from gas_phys_location";
			Table tLocation = Table.tableNew();
			LIB.execISql(tLocation, sSQL, false, className);
			
			Table tExternalPfolio=Table.tableNew();
			tExternalPfolio.addCol("party", COL_TYPE_ENUM.COL_STRING);
			tExternalPfolio.addRow();
			
			DEBUG_LOGFILE.logToFile("Did SQL", iRunNumber, className);
			
			//go row-by-row of Quorum file
			int iNumRows = LIB.safeGetNumRows(tOutput);
			for (int iRowCounter = 1; iRowCounter <=iNumRows; iRowCounter++){
				//DEBUG_LOGFILE.logToFile("Row: "+iRowCounter, iRunNumber, className);
				boolean isDuplicateRowSwap=false;
				boolean isDuplicateRowIndex=false;
				boolean isDuplicateRowFixed=false;
				boolean DoNotBook=false;
				
				String sIns_type=LIB.safeGetString(tOutput, "ins_type", iRowCounter);
				String sClearing_broker=LIB.safeGetString(tOutput, "clearing_broker", iRowCounter);
				String sProj_index=LIB.safeGetString(tOutput, "proj_index", iRowCounter);
				String sLocation=LIB.safeGetString(tOutput, "location", iRowCounter);
				String sZone=LIB.safeGetString(tOutput, "zone_NONFUNC", iRowCounter);
				String sPipeline=LIB.safeGetString(tOutput, "pipeline_NONFUNC", iRowCounter);
				String sCounterparty=LIB.safeGetString(tOutput, "counterparty", iRowCounter);
				String sTemplate=LIB.safeGetString(tOutput, "template", iRowCounter);
				String sDeal_num=LIB.safeGetString(tOutput, "deal_num", iRowCounter);
				String sStart_date=LIB.safeGetString(tOutput, "start_date", iRowCounter);
				String sTran_status=LIB.safeGetString(tOutput, "tran_status", iRowCounter);
				String sPrice=LIB.safeGetString(tOutput, "price", iRowCounter);
				String sQuantity=LIB.safeGetString(tOutput, "quantity", iRowCounter);
				String sNum_days=LIB.safeGetString(tOutput, "num_days", iRowCounter);
				String sRow_type=LIB.safeGetString(tOutput, "row_type", iRowCounter);
				String sPfolio=LIB.safeGetString(tOutput, "internal_portfolio", iRowCounter);
				String sPrevDeal_num="", sPrevStart_date="",sPrevProj_index="";
				if (iRowCounter>=1) {
					sPrevDeal_num=LIB.safeGetString(tOutput, "deal_num", iRowCounter-1);
					sPrevStart_date=LIB.safeGetString(tOutput, "start_date", iRowCounter-1);
					sPrevProj_index=LIB.safeGetString(tOutput, "proj_index", iRowCounter-1);
				}
				
				//TODO
				//skip rows where tran_status==Void
				//if(Str.iEqual(sTran_status, "Void") == CONST_VALUES.VALUE_OF_TRUE){continue;}
				
				//skip rows that are in Lawrence's deal numbers list to NOT book
				for(int i=0;i<sDoNotBook.length;i++){
					if(Str.iEqual(sDeal_num, sDoNotBook[i])==CONST_VALUES.VALUE_OF_TRUE){
						DoNotBook=true;
					}
				}
				if(DoNotBook){
					continue;
				}
				
				//set tran_info field values
				LIB.safeSetString(tOutput, "TRAN_INFO_ExternalLocation", iRowCounter, sLocation);
				LIB.safeSetString(tOutput, "TRAN_INFO_ExternalCounterparty", iRowCounter, sCounterparty);
				
				//get Internal party and pfolio from portfolio mapping
				LIB.safeSetString(tOutput, "internal_bunit", iRowCounter, FUNC.getInternalBU(sPfolio));
				LIB.safeSetString(tOutput, "portfolio", iRowCounter, FUNC.getInternalPort(sPfolio));
				
				//get external pfolio from user table and set it
				LIB.safeSetString(tExternalPfolio, "party", 1, sCounterparty);
				tExternalPfolio.select(tUserPfolio, "mapped_value", "original_value EQ $party");
				LIB.safeSetString(tOutput, "TRANF_EXTERNAL_PORTFOLIO.0", iRowCounter, LIB.safeGetString(tExternalPfolio, "mapped_value", 1));
				//DEBUG_LOGFILE.logToFile("table select", iRunNumber, className);
				//tExternalPfolio.clearRows();
				
				//replace 'NULL' values with blanks
				if(Str.iEqual(sClearing_broker, "NULL") == CONST_VALUES.VALUE_OF_TRUE){
					LIB.safeSetString(tOutput, "clearing_broker", iRowCounter, "");
				}
				if(Str.iEqual(sProj_index, "NULL") == CONST_VALUES.VALUE_OF_TRUE){
					LIB.safeSetString(tOutput, "proj_index", iRowCounter, "");
				}
				if(Str.iEqual(sLocation, "NULL") == CONST_VALUES.VALUE_OF_TRUE){
					LIB.safeSetString(tOutput, "location", iRowCounter, "");
				}
				if(Str.iEqual(sPipeline, "NULL") == CONST_VALUES.VALUE_OF_TRUE){
					LIB.safeSetString(tOutput, "pipeline_NONFUNC", iRowCounter, "");
				}
				if(Str.iEqual(sZone, "NULL") == CONST_VALUES.VALUE_OF_TRUE){
					LIB.safeSetString(tOutput, "zone_NONFUNC", iRowCounter, "");
				}
				if(Str.iEqual(sTemplate, "NULL") == CONST_VALUES.VALUE_OF_TRUE){
					LIB.safeSetString(tOutput, "template", iRowCounter, "");
				}
				if(Str.iEqual(sPrice, "NULL") == CONST_VALUES.VALUE_OF_TRUE){
					LIB.safeSetString(tOutput, "price", iRowCounter, "");
				}
				//when quantity is not NULL, quantity=quantity/num_days. If quantity does not divide by num_days, QUANTITY_NOT_DIV will be populated (for troubleshooting).
				if(Str.iEqual(sQuantity, "NULL") == CONST_VALUES.VALUE_OF_TRUE){
					LIB.safeSetString(tOutput, "quantity", iRowCounter, "NULL");
				} else {
					//DEBUG_LOGFILE.logToFile("Quantity=" +sQuantity, iRunNumber, className);
					int iQuantity=Integer.parseInt(sQuantity);
					int iNum_days=Integer.parseInt(sNum_days);
					if(iQuantity%iNum_days==0){
						LIB.safeSetString(tOutput, "quantity", iRowCounter, Integer.toString((iQuantity/iNum_days)));
					} else{
						LIB.safeSetString(tOutput, "QUANTITY_NOT_DIV", iRowCounter, sQuantity);
					}
				}			
				
				//DEBUG_LOGFILE.logToFile("fixed ins_types", iRunNumber, className);
				
				//ins_type==FIXED
				if(Str.iEqual(sIns_type, "Fixed") == CONST_VALUES.VALUE_OF_TRUE){
					if (Str.iEqual(sTemplate, "NULL") == CONST_VALUES.VALUE_OF_TRUE){
						LIB.safeSetString(tOutput, "template", iRowCounter, "NG Fixed Price Physical");
					}
					LIB.safeSetString(tOutput, "fixed_float", iRowCounter, "Fixed");
					int iCurrentRow=LIB.safeGetNumRows(tFixedTemp);
					if (Str.iEqual(sDeal_num, sPrevDeal_num) == CONST_VALUES.VALUE_OF_TRUE && Str.iEqual(sStart_date, sPrevStart_date) == CONST_VALUES.VALUE_OF_TRUE){
						if(Str.iEqual(sQuantity, "0") == CONST_VALUES.VALUE_OF_TRUE){
							isDuplicateRowFixed=true;		
						}
						else {
							LIB.safeSetString(tFixedTemp, "quantity", iCurrentRow, sQuantity);
							isDuplicateRowFixed=true;
						}
					}
					if(!isDuplicateRowFixed){
						tOutput.copyRowAdd(iRowCounter, tFixedTemp);
						isDuplicateRowFixed=false;
					}
				} 
				//ins_type==FIXED SWAP
				else if(Str.iEqual(sIns_type, "Fixed Swap") == CONST_VALUES.VALUE_OF_TRUE){
					//DEBUG_LOGFILE.logToFile("fixed swap ins_types", iRunNumber, className);
					if (Str.iEqual(sTemplate, "NULL") == CONST_VALUES.VALUE_OF_TRUE){
						LIB.safeSetString(tOutput, "template", iRowCounter, "Fixed Swap");
					}
					LIB.safeSetString(tOutput, "fixed_float", iRowCounter, "");
					tOutput.copyRowAdd(iRowCounter, tFixedSwapTemp);
				} 
				//ins_type==SWAP
				else if(Str.iEqual(sIns_type, "Swap") == CONST_VALUES.VALUE_OF_TRUE){
					//DEBUG_LOGFILE.logToFile("swap ins_types", iRunNumber, className);
					int iCurrentRow=LIB.safeGetNumRows(tSwapTemp);
					if (Str.iEqual(sDeal_num, sPrevDeal_num) == CONST_VALUES.VALUE_OF_TRUE && Str.iEqual(sStart_date, sPrevStart_date) == CONST_VALUES.VALUE_OF_TRUE){
						if(Str.containsSubString(sProj_index, "Nymex") == CONST_VALUES.VALUE_OF_TRUE){
							sPrevProj_index=LIB.safeGetString(tOutput, "proj_index", iRowCounter-1);
							LIB.safeSetString(tSwapTemp, "proj_index2", iCurrentRow, sPrevProj_index);
							LIB.safeSetString(tSwapTemp, "proj_index", iCurrentRow, sProj_index);
							isDuplicateRowSwap=true;		
						} else if ((Str.containsSubString(sProj_index, "IF") ==CONST_VALUES.VALUE_OF_TRUE || Str.containsSubString(sProj_index, "NGI") ==CONST_VALUES.VALUE_OF_TRUE)  && Str.containsSubString(sPrevProj_index, "GD")==CONST_VALUES.VALUE_OF_TRUE) {
							sPrevProj_index=LIB.safeGetString(tOutput, "proj_index", iRowCounter-1);
							LIB.safeSetString(tSwapTemp, "proj_index2", iCurrentRow, sPrevProj_index);
							LIB.safeSetString(tSwapTemp, "proj_index", iCurrentRow, sProj_index);
							isDuplicateRowSwap=true;
						}
						else {			
							LIB.safeSetString(tSwapTemp, "proj_index2", iCurrentRow, sProj_index);
							isDuplicateRowSwap=true;
						}
					}
					if (Str.iEqual(sTemplate, "NULL") == CONST_VALUES.VALUE_OF_TRUE){
						LIB.safeSetString(tOutput, "template", iRowCounter, "NG Basis Swap");
					}
					LIB.safeSetString(tOutput, "fixed_float", iRowCounter, "Index");
					if(!isDuplicateRowSwap){
						tOutput.copyRowAdd(iRowCounter, tSwapTemp);
						isDuplicateRowSwap=false;
					}
				} 
				//ins_type==INDEX
				else if(Str.iEqual(sIns_type, "Index") == CONST_VALUES.VALUE_OF_TRUE){
					//DEBUG_LOGFILE.logToFile("index ins_types", iRunNumber, className);
					if (Str.containsSubString(sTemplate, "Index") == CONST_VALUES.VALUE_OF_FALSE && Str.iEqual(sTemplate, "NG Fixed Price Physical") == CONST_VALUES.VALUE_OF_FALSE){
						LIB.safeSetString(tOutput, "template", iRowCounter, "NG Index Physical");
					}
					LIB.safeSetString(tOutput, "fixed_float", iRowCounter, "Index");
					/*int iCurrentRow=LIB.safeGetNumRows(tIndexTemp);
					if (Str.iEqual(sDeal_num, sPrevDeal_num) == CONST_VALUES.VALUE_OF_TRUE && Str.iEqual(sStart_date, sPrevStart_date) == CONST_VALUES.VALUE_OF_TRUE){
						if(Str.iEqual(sProj_index, "NULL") == CONST_VALUES.VALUE_OF_TRUE){
							isDuplicateRowIndex=true;		
						}
						else {
							LIB.safeSetString(tIndexTemp, "proj_index", iCurrentRow, sProj_index);
							isDuplicateRowIndex=true;
						}
					}*/
					if(Str.iEqual(sRow_type, "VP") == CONST_VALUES.VALUE_OF_TRUE){
						isDuplicateRowIndex=true;
					}
					if(!isDuplicateRowIndex){
						tOutput.copyRowAdd(iRowCounter, tIndexTemp);
						isDuplicateRowIndex=false;
					}
				} 
				//ins_type==TRANSPORT
				else if(Str.iEqual(sIns_type, "Transport") == CONST_VALUES.VALUE_OF_TRUE){
					//DEBUG_LOGFILE.logToFile("trans ins_types", iRunNumber, className);
					LIB.safeSetString(tOutput, "template", iRowCounter, "NG Transport Physical FIRM");
					LIB.safeSetString(tOutput, "fixed_float", iRowCounter, "Fixed");
					LIB.safeSetString(tOutput, "TRANF_ALLOW_PATHING.0", iRowCounter, "No");
					
					//duplicate removal occurs later
					tOutput.copyRowAdd(iRowCounter, tTransportTemp);
				} 
				//ins_type==Storage
				else if(Str.iEqual(sIns_type, "Storage") == CONST_VALUES.VALUE_OF_TRUE){
					//DEBUG_LOGFILE.logToFile("stor ins_types", iRunNumber, className);
					LIB.safeSetString(tOutput, "template", iRowCounter, "NG Storage Physical (Deal Level)");
					LIB.safeSetString(tOutput, "fixed_float", iRowCounter, "Fixed");
					LIB.safeSetString(tOutput, "TRANF_ALLOW_PATHING.0", iRowCounter, "No");	
					tOutput.copyRowAdd(iRowCounter, tStorageTemp);
					//LIB.safeSetString(tStorageTemp, "TRANF_LOCATION.5", iRowCounter, sLocation);
				} 
				//ins_type==Fee
				else if(Str.iEqual(sIns_type, "Fee") == CONST_VALUES.VALUE_OF_TRUE){
					//DEBUG_LOGFILE.logToFile("fee ins_types", iRunNumber, className);
					if (Str.iEqual(sTemplate, "NULL") == CONST_VALUES.VALUE_OF_TRUE){
						LIB.safeSetString(tOutput, "template", iRowCounter, "");
					}
					LIB.safeSetString(tOutput, "fixed_float", iRowCounter, "");
					tOutput.copyRowAdd(iRowCounter, tFeeTemp);
				}
				//ins_type==Broker Fee
				else if(Str.iEqual(sIns_type, "Broker Fee") == CONST_VALUES.VALUE_OF_TRUE){
					//DEBUG_LOGFILE.logToFile("broker fee ins_types", iRunNumber, className);
					if (Str.iEqual(sTemplate, "NULL") == CONST_VALUES.VALUE_OF_TRUE){
						LIB.safeSetString(tOutput, "template", iRowCounter, "");
					}
					LIB.safeSetString(tOutput, "fixed_float", iRowCounter, "");
					tOutput.copyRowAdd(iRowCounter, tBrokerFeeTemp);
				}
				//ins_type==SOMETHING ELSE
				else {
					tOutput.copyRowAdd(iRowCounter, tEmptyTemp);
				}
			}
			
			//DEBUG_LOGFILE.logToFile("Did BIG loop", iRunNumber, className);
			
			Table tSub=tTransportTemp.cloneTable();
			Table tTransDeals=Table.tableNew();
			tTransDeals.addCol("deal_num", COL_TYPE_ENUM.COL_STRING);
			tSub.addCol("location2", COL_TYPE_ENUM.COL_STRING);
			tSub.addCol("zone2", COL_TYPE_ENUM.COL_STRING);
			Table tTransportTotal=tSub.cloneTable();
			tTransDeals.select(tTransportTemp,"DISTINCT, deal_num","deal_num GT 1");
			iNumRows = LIB.safeGetNumRows(tTransDeals);
			
			for(int iRowCounter=1;iRowCounter<=iNumRows;iRowCounter++){
				String sCurrentDeal_num=LIB.safeGetString(tTransDeals, "deal_num", iRowCounter);
				tSub.select(tTransportTemp, "*", "deal_num EQ " +sCurrentDeal_num);
				tSub.sortCol("start_date", TABLE_SORT_DIR_ENUM.TABLE_SORT_DIR_ASCENDING);
				//LIB.viewTable(tSub, "Sub");
				int iNumSubRows=LIB.safeGetNumRows(tSub);
				for(int i=1;i<=iNumSubRows;i++){
					String start_date=LIB.safeGetString(tSub, "start_date", i);
					String nextStart_date=LIB.safeGetString(tSub, "start_date", i+1);
					String row_type=LIB.safeGetString(tSub, "row_type", i);
					if(Str.iEqual(start_date, nextStart_date)==CONST_VALUES.VALUE_OF_TRUE){
						if(Str.iEqual(row_type, "VP")==CONST_VALUES.VALUE_OF_TRUE){
							LIB.safeSetString(tSub, "location2", i+1, LIB.safeGetString(tSub, "location", i+1));
							LIB.safeSetString(tSub, "zone2", i+1,LIB.safeGetString(tSub, "zone_NONFUNC", i+1));
							LIB.safeSetString(tSub, "location", i+1,LIB.safeGetString(tSub, "location", i));
							LIB.safeSetString(tSub, "zone_NONFUNC", i+1,LIB.safeGetString(tSub, "zone_NONFUNC", i));
							//LIB.safeSetString(tSub, "price", i+1,LIB.safeGetString(tSub, "price", i+1));
						}else if(Str.iEqual(row_type, "VPD")==CONST_VALUES.VALUE_OF_TRUE) {
							LIB.safeSetString(tSub, "location2", i, LIB.safeGetString(tSub, "location", i));
							LIB.safeSetString(tSub, "zone2", i,LIB.safeGetString(tSub, "zone_NONFUNC", i));
							LIB.safeSetString(tSub, "location", i,LIB.safeGetString(tSub, "location", i+1));
							LIB.safeSetString(tSub, "zone_NONFUNC", i,LIB.safeGetString(tSub, "zone_NONFUNC", i+1));
						}
					}
				}
				tTransportTotal.select(tSub, "*", "row_type EQ VPD");
				//LIB.viewTable(tSub2, "Sub");
				tSub.clearRows();
			}
			/*LIB.viewTable(tTransDeals, "trans_deals");
			//LIB.viewTable(tTransportTotal, "total");*/
			
			//tTransportTotal.setColName("location2", "TRANF_LOCATION.3");
			//tTransportTotal.setColName("zone2", "TRANF_ZONE.3");
			tTransportTotal.setColName("zone2", "zone2_NONFUNC");
			
			
			Table tSub2=tStorageTemp.cloneTable();
			Table tStorDeals=Table.tableNew();
			tStorDeals.addCol("deal_num", COL_TYPE_ENUM.COL_STRING);
			tSub2.addCol("location2", COL_TYPE_ENUM.COL_STRING);
			tSub2.addCol("zone2", COL_TYPE_ENUM.COL_STRING);
			tSub2.addCol("location3", COL_TYPE_ENUM.COL_STRING);
			Table tStorageTotal=tSub2.cloneTable();
			tStorDeals.select(tStorageTemp,"DISTINCT, deal_num","deal_num GT 1");
			iNumRows = LIB.safeGetNumRows(tStorDeals);
			
			for(int iRowCounter=1;iRowCounter<=iNumRows;iRowCounter++){
				String sCurrentDeal_num=LIB.safeGetString(tStorDeals, "deal_num", iRowCounter);
				tSub2.select(tStorageTemp, "*", "deal_num EQ "+sCurrentDeal_num);
				tSub2.sortCol("start_date", TABLE_SORT_DIR_ENUM.TABLE_SORT_DIR_ASCENDING);
				//LIB.viewTable(tSub2, "Sub");
				int iNumSubRows=LIB.safeGetNumRows(tSub2);
				for(int i=1;i<=iNumSubRows;i++){
					String start_date=LIB.safeGetString(tSub2, "start_date", i);
					String nextStart_date=LIB.safeGetString(tSub2, "start_date", i+1);
					String sPrice=LIB.safeGetString(tSub2, "price", i);
					LIB.safeSetString(tSub2, "location2", i, LIB.safeGetString(tSub2, "location", i));
					LIB.safeSetString(tSub2, "location3", i, LIB.safeGetString(tSub2, "location", i));
					LIB.safeSetString(tSub2, "zone2", i, LIB.safeGetString(tSub2, "zone_NONFUNC", i));
					/*if(Str.iEqual(start_date, nextStart_date)==CONST_VALUES.VALUE_OF_TRUE){
						if(Str.iEqual(sPrice, "0") == CONST_VALUES.VALUE_OF_TRUE){
							tSub2.delRow(i);
						}else{
							tSub2.delRow(i+1);
						}
					}*/
				}
				//LIB.viewTable(tSub2, "Sub");
				tStorageTotal.select(tSub2, "*", "deal_num GT 1");
				tSub2.clearRows();
			}
			tStorageTotal.setColName("zone2", "zone2_NONFUNC");
			
			DEBUG_LOGFILE.logToFile("Did Loops", iRunNumber, className);
			
			Table[] tTempTables={tFixedTemp,tFixedSwapTemp,tSwapTemp,tIndexTemp,tTransportTotal,tStorageTotal,tFeeTemp, tBrokerFeeTemp, tEmptyTemp};
			String[] sLists = {"proj_index","counterparty"};
			
			Table[] tUsers = {tUserCurve,tUserParty};
			
			Table tListLocation=Table.tableNew();
			tListLocation.addCol("location", COL_TYPE_ENUM.COL_STRING);
			tListLocation.select(tOutput, "DISTINCT, location","location NE NULL");
			tListLocation.select(tUserLocation,"mapped_value","original_value EQ $location");
			tListLocation.select(tLocation,"mapped_value","mapped_value EQ $location");
			tListLocation.select(tLocation,"active","mapped_value EQ $mapped_value");
			sDestinationFileName = "list_location.csv";
			sDestinationFullFilePath = sDirectory + "/" + sDestinationFileName;
			DEBUG_LOGFILE.logToFile("About to write to: " + sDestinationFullFilePath, iRunNumber, className);
			tListLocation.printTableDumpToFile(sDestinationFullFilePath);
			LIB.viewTable(tListLocation, "list_location");
			
			for(int i=0;i<sLists.length;i++){
				Table tListTemp=Table.tableNew();
				tListTemp.addCol(sLists[i], COL_TYPE_ENUM.COL_STRING);
				tListTemp.select(tOutput, "DISTINCT, "+sLists[i],sLists[i] + " NE NULL");
				tListTemp.select(tUsers[i],"mapped_value","original_value EQ $"+sLists[i]);
				sDestinationFileName = "list_" + sLists[i] +".csv";
				sDestinationFullFilePath = sDirectory + "/" + sDestinationFileName;
				DEBUG_LOGFILE.logToFile("About to write to: " + sDestinationFullFilePath, iRunNumber, className);
				tListTemp.printTableDumpToFile(sDestinationFullFilePath);
				LIB.viewTable(tListTemp, "list_"+sLists[i]);
			}
			for(int i=0;i<sIns_Types.length;i++){
				tTempTables[i].delRow(1);
				sDestinationFileName = "deal_import_" + sIns_Types[i] +".csv";
				sDestinationFullFilePath = sDirectory + "/" + sDestinationFileName;
				DEBUG_LOGFILE.logToFile("About to write to: " + sDestinationFullFilePath, iRunNumber, className);
				tTempTables[i].printTableDumpToFile(sDestinationFullFilePath);
				LIB.viewTable(tTempTables[i], sIns_Types[i]);
				tTempTables[i]=LIB.destroyTable(tTempTables[i]);
			}
			
			sDestinationFileName = "deal_import_empty.csv";
			sDestinationFullFilePath = sDirectory + "/" + sDestinationFileName;
			DEBUG_LOGFILE.logToFile("About to write to: " + sDestinationFullFilePath, iRunNumber, className);
			tTempTables[tTempTables.length-1].printTableDumpToFile(sDestinationFullFilePath);
			LIB.viewTable(tTempTables[tTempTables.length-1], "empty");
			tTempTables[tTempTables.length-1] = LIB.destroyTable(tTempTables[tTempTables.length-1]);
			
			// TODO
			//LIB.viewTable(tOutput, "output ");
			//LIB.viewTable(tTrackingTable, "tTrackingTable ");	
		 
		} catch (Exception e) {
			LIB.log("doEverything",e, className);
		}	 
		tSourceData = LIB.destroyTable(tSourceData);
		tOutput = LIB.destroyTable(tOutput);
	}

	public Table getOriginalSourceData(String sFullFilePath, int iRunNumber, String className) throws OException {
		Table tData = Util.NULL_TABLE;
		try {
			tData = Table.tableNew();
			
			tData.inputFromCSVFile(sFullFilePath);
 		 
		} catch (Exception e) {
			// do not log this
		}
		return tData;	 
	}

	public void updateColumnNames(Table tData, Table tTrackingTable, int iRunNumber, String className) throws OException {
 		try {
 			this.updateColumnName(tData, tTrackingTable, "SourceSystem", "TRAN_INFO_SourceSystem", iRunNumber, className);
 			this.updateColumnName(tData, tTrackingTable, "deal_num", "deal_num", iRunNumber, className);
 			this.updateColumnName(tData, tTrackingTable, "AGNA_flg", "TRAN_INFO_AGNA", iRunNumber, className);
 			this.updateColumnName(tData, tTrackingTable, "counterparty", "counterparty", iRunNumber, className);
 			this.updateColumnName(tData, tTrackingTable, "buy_sell", "buy_sell", iRunNumber, className);
 			this.updateColumnName(tData, tTrackingTable, "ins_type", "ins_type", iRunNumber, className);
 			this.updateColumnName(tData, tTrackingTable, "internal_party", "internal_bunit", iRunNumber, className);
 			this.updateColumnName(tData, tTrackingTable, "internal_contact", "trader", iRunNumber, className);
 			this.updateColumnName(tData, tTrackingTable, "external_contact", "TRAN_INFO_ExternalContact", iRunNumber, className);
 			this.updateColumnName(tData, tTrackingTable, "Trade_Date", "trade_date", iRunNumber, className);
 			this.updateColumnName(tData, tTrackingTable, "start_date", "start_date", iRunNumber, className);
 			this.updateColumnName(tData, tTrackingTable, "end_date", "end_date", iRunNumber, className);
 			this.updateColumnName(tData, tTrackingTable, "price", "price", iRunNumber, className);
 			this.updateColumnName(tData, tTrackingTable, "pipeline", "pipeline_NONFUNC", iRunNumber, className);
 			this.updateColumnName(tData, tTrackingTable, "zone", "zone_NONFUNC", iRunNumber, className);
 			this.updateColumnName(tData, tTrackingTable, "location", "location", iRunNumber, className);
 			this.updateColumnName(tData, tTrackingTable, "volume", "quantity", iRunNumber, className);
 			this.updateColumnName(tData, tTrackingTable, "template", "template", iRunNumber, className);
 			this.updateColumnName(tData, tTrackingTable, "tran_status", "tran_status", iRunNumber, className);
 			this.updateColumnName(tData, tTrackingTable, "proj_index", "proj_index", iRunNumber, className);
 			this.updateColumnName(tData, tTrackingTable, "clearing_broker", "clearing_broker", iRunNumber, className);
 			this.updateColumnName(tData, tTrackingTable, "num_days", "num_days", iRunNumber, className);
 			this.updateColumnName(tData, tTrackingTable, "row_type", "row_type", iRunNumber, className);
 			this.updateColumnName(tData, tTrackingTable, "index_const", "TRANF_RATE_SPD.2", iRunNumber, className);
 			this.updateColumnName(tData, tTrackingTable, "trans_contract", "TRANF_CONTRACT_NUMBER.0", iRunNumber, className);
 			this.updateColumnName(tData, tTrackingTable, "internal_portfolio", "internal_portfolio", iRunNumber, className);
 			this.updateColumnName(tData, tTrackingTable, "portfolio_business_lv_sd", "TRAN_INFO_ExternalDesk", iRunNumber, className);
 			//this.updateColumnName(tData, tTrackingTable, "pay_receive", "pay_receive", iRunNumber, className);
 			//this.updateColumnName(tData, tTrackingTable, "contract", "contract", iRunNumber, className);
 		
		} catch (Exception e) {
			// do not log this
		}
	}
		

	public void updateColumnName(Table tData, Table tTrackingTable, String sFromName, String sToName, int iRunNumber, String className) throws OException {
 		try {
 
 			boolean bFoundFlag = false;
 			
 			int iNumCols = LIB.safeGetNumCols(tData);
 			// the column name is in row 1
 			for (int iColCounter = 1; iColCounter <= iNumCols; iColCounter++) {
 				String sColNameThisColumn = tData.getString(iColCounter, CONST_VALUES.ROW_TO_GET_OR_SET);
 				if (Str.iEqual(sColNameThisColumn, sFromName) == CONST_VALUES.VALUE_OF_TRUE ){
 					// set the col name
 					tData.setColName(iColCounter, sToName);
 					tTrackingTable.setInt(iColCounter, CONST_VALUES.ROW_TO_GET_OR_SET, CONST_VALUES.VALUE_OF_TRUE);
 					bFoundFlag = true;
 					break;
 				}
 			}
 			if (bFoundFlag == false) {
 				DEBUG_LOGFILE.logToFile("WARNING: was not able to find column: " + sFromName, iRunNumber, className);
 			}
		 
		} catch (Exception e) {
			// do not log this
		}
	}
	
public static class FUNC{
	public static String getInternalBU(String pfolio){
		String sEndurBU="";
		try{
			if (Str.iEqual(pfolio, "CE")==CONST_VALUES.VALUE_OF_TRUE){
				sEndurBU="NATURAL GAS NORTH AMERICA";
			} else if(Str.iEqual(pfolio, "MK")==CONST_VALUES.VALUE_OF_TRUE){
				sEndurBU="NATURAL GAS NORTH AMERICA";
			} else if(Str.iEqual(pfolio, "DM")==CONST_VALUES.VALUE_OF_TRUE){
				sEndurBU="NATURAL GAS NW BASIS";
			} else if(Str.iEqual(pfolio, "PP")==CONST_VALUES.VALUE_OF_TRUE){
				sEndurBU="NATURAL GAS MIDCON BASIS";
			}
		} catch(Exception e){
			//do not log this
		}
		return sEndurBU;
	}
	
	public static String getInternalPort(String pfolio){
		String sIntPort="";
		try{
			if (Str.iEqual(pfolio, "CE")==CONST_VALUES.VALUE_OF_TRUE){
				sIntPort="NGNA Phys";
			} else if(Str.iEqual(pfolio, "MK")==CONST_VALUES.VALUE_OF_TRUE){
				sIntPort="NGNA Phys";
			} else if(Str.iEqual(pfolio, "DM")==CONST_VALUES.VALUE_OF_TRUE){
				sIntPort="NW Basis";
			} else if(Str.iEqual(pfolio, "PP")==CONST_VALUES.VALUE_OF_TRUE){
				sIntPort="NG Midcon Basis Phys";
			}
		} catch(Exception e){
			//do not log this
		}
		return sIntPort;
	}
}
	
public static class CONST_VALUES {
	public static final int VALUE_OF_TRUE = 1;
	public static final int VALUE_OF_FALSE = 0;

	public static final int VALUE_OF_NOT_APPLICABLE = -1;

	public static final double A_SMALL_NUMBER = 0.000001;

	public static final int ROW_TO_GET_OR_SET = 1;
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

	public static final String VERSION_NUMBER = "V1.007 (13Jun2023)";

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
