package com.drw;
  
import com.olf.openjvs.ConnexUtility;
import com.olf.openjvs.DBUserTable;
import com.olf.openjvs.DBaseTable;
import com.olf.openjvs.IContainerContext;
import com.olf.openjvs.IScript;
import com.olf.openjvs.OCalendar;
import com.olf.openjvs.OConsole;
import com.olf.openjvs.OException;
import com.olf.openjvs.PluginCategory;
import com.olf.openjvs.PluginType;
import com.olf.openjvs.Str;
import com.olf.openjvs.SystemUtil;
import com.olf.openjvs.Table;
import com.olf.openjvs.Transaction;
import com.olf.openjvs.Util;
import com.olf.openjvs.XString;
import com.olf.openjvs.enums.COL_TYPE_ENUM;
import com.olf.openjvs.enums.DATE_FORMAT;
import com.olf.openjvs.enums.DATE_LOCALE;
import com.olf.openjvs.enums.OC_XML_TYPES_ENUM;
import com.olf.openjvs.enums.SCRIPT_CATEGORY_ENUM;
import com.olf.openjvs.enums.SCRIPT_TYPE_ENUM;
import com.olf.openjvs.enums.SEARCH_CASE_ENUM;
import com.olf.openjvs.enums.SHM_USR_TABLES_ENUM;
import com.olf.openjvs.enums.TABLE_VIEWER_MODE;

/*  

Name: DRW_ViewConnexLogData

Description:
1) Shows XML Log Data from Connex for the ICE Gateway
2) This gets data only for the 'Current Date' of the user (not the system/business date of Endur).
3) And it will load Activity for the Date (time stamp) from oc_activity_log
 
Revision History:
dd-mmm-yyyy      Developer       Description
19-Jun-2023        Brian         New Script

*/

@PluginCategory({SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_GENERIC})
@PluginType(SCRIPT_TYPE_ENUM.MAIN_SCRIPT)
public class DRW_ViewConnexLogData implements IScript
{
 
	public void execute(IContainerContext context) throws OException {
    	String className = this.getClass().getSimpleName();
    	LIB.log("START", className);
   
    	int iUniqueTaskRunNumber = LIB.getTaskRunID();
    	
    	Table tArgt = context.getArgumentsTable();

    	// default bDoNotShowRawDataViewer to false
    	boolean bDoNotShowRawDataViewer = true;
    	// default bShowAllData to true
    	boolean bShowAllData = true;
    	
    	boolean bOkToProceed = true;
    	
    	if (tArgt.getColNum(COLS.ARGT.EXTRA.user_pressed_cancel) >= 1) {
    		bOkToProceed = false;
        	LIB.log("User Pressed Cancel", className);
    	}
    	
    	if (bOkToProceed == true) {
        	if (LIB.safeGetNumRows(tArgt) >= 1) {
        		{
            		int iShowRawDataFlag = LIB.safeGetInt(tArgt, COLS.ARGT.do_not_show_raw_data_flag, CONST_VALUES.ROW_TO_GET_OR_SET);
            		if (iShowRawDataFlag == CONST_VALUES.VALUE_OF_TRUE) {
            			bDoNotShowRawDataViewer = true;
            		}
        		}
        		{
            		int iOnlyShowTradeXmlDataFlag = LIB.safeGetInt(tArgt, COLS.ARGT.only_show_trade_logs_flag, CONST_VALUES.ROW_TO_GET_OR_SET);
            		if (iOnlyShowTradeXmlDataFlag == CONST_VALUES.VALUE_OF_TRUE) {
            			// note that this is reversed... if 'only show... ' is 'true', then set 'show all' to false
            			bShowAllData = false;
            		}
        		}
        	} 
        	this.doEverything(iUniqueTaskRunNumber, bDoNotShowRawDataViewer, bShowAllData, className);
    	}
    	
    	LIB.log("END", className);
    }
	   
    public void doEverything(int iUniqueTaskRunNumber, boolean bDoNotShowRawDataViewer, boolean bShowAllData, String className) throws OException {

    	Table tJobLevel = Util.NULL_TABLE; 
    	// This table has sub tables for each report.  This approach allows us to have just one 'viewTable' instead of one per report
    	Table tMasterReportTable = Util.NULL_TABLE; 
    	 try{
    		 LIB.log("doEverything: START", className);
    		 
    		 int iToday = OCalendar.today();
    		 int iTomorrow = iToday + 1;
    		 
    		 // TODO to get all days
    	 //	  iToday = iToday - 100;
    	// 	 iTomorrow = iToday + 1000;
    		 
    		 String sTodayForDB = OCalendar.formatJdForDbAccess(iToday);
    		 String sTomorrowForDb = OCalendar.formatJdForDbAccess(iTomorrow);

    		 {
        		 Table tOcActivityLogDataRawFormOneRowPerActivityID = Util.NULL_TABLE; 

        		 // Get raw source data, get tOcActivityLogDataRawFormOneRowPerActivityID
        		 {
            		 // normally don't use 'select *'.  However... in this case, for debug purposes, we actually do want all of the columns, and we don't necessarily know in advance what they'll be
            		 tOcActivityLogDataRawFormOneRowPerActivityID = Table.tableNew();
            		 String sSQL = "select * from oc_activity_log WHERE   activity_timestamp >= " + "'" + sTodayForDB + "'" + "  AND activity_timestamp < " + "'" + sTomorrowForDb + "'" + "  AND lower(logged_by) like lower('%XMLengine%') order by activity_timestamp";
            	
            		 
            		 // TODO
            		 boolean bGetAlertJobsOnlyForTesting = false;
            		 if (bGetAlertJobsOnlyForTesting == true) {
//                		 sSQL = "select * from oc_activity_log WHERE job_id like '1584262%' AND  activity_timestamp >= " + "'" + sTodayForDB + "'" + "  AND activity_timestamp < " + "'" + sTomorrowForDb + "'" + "  AND lower(logged_by) like lower('%XMLengine%') order by activity_timestamp";
                		 sSQL = "select * from oc_activity_log WHERE job_id like '1584262%' AND  activity_timestamp >= " + "'" + sTodayForDB + "'" + "  AND activity_timestamp < " + "'" + sTomorrowForDb + "'" + "  AND lower(logged_by) like lower('%XMLengine%') order by activity_timestamp";
            		 }
            		 
            		 
            		 
            		 LIB.execISql(tOcActivityLogDataRawFormOneRowPerActivityID, sSQL, className);
            		 
            		 LIB.safeAddCol(tOcActivityLogDataRawFormOneRowPerActivityID, COLS.DATA.job_data_request,  COL_TYPE_ENUM.COL_CLOB);
            		 LIB.safeAddCol(tOcActivityLogDataRawFormOneRowPerActivityID, COLS.DATA.job_data_response,  COL_TYPE_ENUM.COL_CLOB);
            		 
            		 LIB.safeAddCol(tOcActivityLogDataRawFormOneRowPerActivityID, COLS.DATA.job_data_request_table,  COL_TYPE_ENUM.COL_TABLE);
            		 LIB.safeAddCol(tOcActivityLogDataRawFormOneRowPerActivityID, COLS.DATA.job_data_response_table,  COL_TYPE_ENUM.COL_TABLE);

            		 LIB.safeAddCol(tOcActivityLogDataRawFormOneRowPerActivityID, COLS.DATA.has_job_data_request,  COL_TYPE_ENUM.COL_INT);
            		 LIB.safeAddCol(tOcActivityLogDataRawFormOneRowPerActivityID, COLS.DATA.has_job_data_response,  COL_TYPE_ENUM.COL_INT);

            		 LIB.safeSetColFormatAsRef(tOcActivityLogDataRawFormOneRowPerActivityID, COLS.DATA.has_job_data_request, SHM_USR_TABLES_ENUM.NO_YES_TABLE);
            		 LIB.safeSetColFormatAsRef(tOcActivityLogDataRawFormOneRowPerActivityID, COLS.DATA.has_job_data_response, SHM_USR_TABLES_ENUM.NO_YES_TABLE);
            		 
            		 LIB.safeSetColValInt(tOcActivityLogDataRawFormOneRowPerActivityID, COLS.DATA.has_job_data_request, CONST_VALUES.VALUE_OF_FALSE);
            		 LIB.safeSetColValInt(tOcActivityLogDataRawFormOneRowPerActivityID, COLS.DATA.has_job_data_response, CONST_VALUES.VALUE_OF_FALSE);
        		 }
        		 
        		 // remove not needed row based on param script Ask table
        		 if (bShowAllData == false) {
        			 Table tTemp = Table.tableNew();
        			 LIB.safeAddCol(tTemp, "job_id", COL_TYPE_ENUM.COL_STRING); 
        			 LIB.safeAddCol(tOcActivityLogDataRawFormOneRowPerActivityID, "temp_for_select", COL_TYPE_ENUM.COL_INT);
        			 LIB.safeSetColValInt(tOcActivityLogDataRawFormOneRowPerActivityID, "temp_for_select", CONST_VALUES.VALUE_OF_FALSE);
        			 int iNumRows = LIB.safeGetNumRows(tOcActivityLogDataRawFormOneRowPerActivityID);
        			 for (int iCounter = 1; iCounter <= iNumRows; iCounter++) {
        				 String sValue = tOcActivityLogDataRawFormOneRowPerActivityID.getString("client_tag2", iCounter);
        				 if (Str.iEqual(sValue, "dbRetrieveWithSQL") == CONST_VALUES.VALUE_OF_TRUE ||
        						 Str.iEqual(sValue, "updateIceStlDocStatus") == CONST_VALUES.VALUE_OF_TRUE||
        						 Str.iEqual(sValue, "ICERefDataDefnRetrieve") == CONST_VALUES.VALUE_OF_TRUE||
        						 Str.iEqual(sValue, "ICERefDataProcess") == CONST_VALUES.VALUE_OF_TRUE||
        						 Str.iEqual(sValue, "ICERetrieveDealHistory") == CONST_VALUES.VALUE_OF_TRUE||
        						 Str.iEqual(sValue, "configRetrieve") == CONST_VALUES.VALUE_OF_TRUE) {
        					 LIB.safeSetInt(tOcActivityLogDataRawFormOneRowPerActivityID, "temp_for_select", iCounter, CONST_VALUES.VALUE_OF_TRUE);
        				 }
        			 }
        			   
        			 String sWhat = "DISTINCT, job_id";
        			 String sWhere = "temp_for_select EQ 1";
        			 LIB.select(tTemp, tOcActivityLogDataRawFormOneRowPerActivityID, sWhat, sWhere, className);

        			
        			 LIB.safeAddCol(tTemp, "temp_for_delete", COL_TYPE_ENUM.COL_INT);
        			 LIB.safeSetColValInt(tTemp, "temp_for_delete", CONST_VALUES.VALUE_OF_TRUE);
        			 
        			 LIB.safeAddCol(tOcActivityLogDataRawFormOneRowPerActivityID, "temp_for_delete", COL_TYPE_ENUM.COL_INT);
        			 LIB.safeSetColValInt(tOcActivityLogDataRawFormOneRowPerActivityID, "temp_for_delete", CONST_VALUES.VALUE_OF_FALSE);
        			 
        			 sWhat = "temp_for_delete";
        			 sWhere = "job_id EQ $job_id";
        			 LIB.select(tOcActivityLogDataRawFormOneRowPerActivityID, tTemp, sWhat, sWhere, className);
        			 
        			 tOcActivityLogDataRawFormOneRowPerActivityID.deleteWhereValue("temp_for_delete", CONST_VALUES.VALUE_OF_TRUE);
        			   
        			 LIB.safeDelCol(tOcActivityLogDataRawFormOneRowPerActivityID, "temp_for_select");
        			 LIB.safeDelCol(tOcActivityLogDataRawFormOneRowPerActivityID, "temp_for_delete");
        			 
        			 tTemp = LIB.destroyTable(tTemp);
        		 }
      
        		 // For this step, we are going to add the XML 'blob' data to the tOcActivityLogDataRawFormOneRowPerActivityID table
        		 // at this point... we still have not reduced the rows down to the final report, which will be one row per job, with sub tables
        		 LIB.log("STEP 1", className);
        		 FUNC.addTheRawXmlDetailsToTheOneRowPerActivityDataLogTableGetItFromTheDatabase(tOcActivityLogDataRawFormOneRowPerActivityID, className);
     
        		 LIB.log("STEP 2", className);
        		 
        		 if (bDoNotShowRawDataViewer == false) {
            		 LIB.log("About to show Raw Activity Level Data in Table Viewer, Number of Rows: " + LIB.safeGetNumRows(tOcActivityLogDataRawFormOneRowPerActivityID), className);
            		 LIB.viewTable(tOcActivityLogDataRawFormOneRowPerActivityID, "Raw Activity ID level data from OC log: " + LIB.safeGetNumRows(tOcActivityLogDataRawFormOneRowPerActivityID));
        		 }
        	
        		 // Set this to 'false' for production
        		 // did some testing to see what was in the log files for this sort of data.
        		 // it didn't seem useful, so I set this to 'false'.  
        		 // keeping this code in this script for now in case we decide to revisit this for the future
        		 boolean bGetBlobDataTableData = false;
        		 if (bGetBlobDataTableData == true) {
        			 FUNC.addBlobDataTable(tOcActivityLogDataRawFormOneRowPerActivityID, className);
        		 }  
        		 
        		 LIB.log("STEP 3", className);
        		 LIB.log("Getting Job Level Data report, which moves activity level data into sub-tables with a main table of one row per job", className);
        		 tJobLevel = this.getJobLevelData(tOcActivityLogDataRawFormOneRowPerActivityID, iUniqueTaskRunNumber, className);
        		 
        		 // to save memory, destroy this here
        		 tOcActivityLogDataRawFormOneRowPerActivityID = LIB.destroyTable(tOcActivityLogDataRawFormOneRowPerActivityID);    			 
    		 } 
 
    		 Table tTradeGatewaysICE = tJobLevel.cloneTable();
//    		 Table tTradeGatewaysCME = tJobLevel.cloneTable();
//    		 Table teConfirm = tJobLevel.cloneTable();
//    		 Table teConfirmStatus = tJobLevel.cloneTable();
//    		 Table tdbResponseWithSQL = tJobLevel.cloneTable();
//    		 Table tsendAlert = tJobLevel.cloneTable();
    		 Table tOther = tJobLevel.cloneTable();
			 int iNumRows = LIB.safeGetNumRows(tJobLevel);
			 for (int iCounter = 1; iCounter <= iNumRows; iCounter++) {
				 String sClientTag1 = LIB.safeGetString(tJobLevel, "client_tag1", iCounter);
				 String sClientTag2 = LIB.safeGetString(tJobLevel, "client_tag2", iCounter);
				 if (Str.equal(sClientTag2, CLIENT_TAG2.ICEAdapterTradeProcess) == 1) {
					 tJobLevel.copyRowAdd(iCounter, tTradeGatewaysICE);
				 } else
//			     if (sClientTag1 != null && sClientTag2 != null && Str.containsSubString(sClientTag1, "CME") == 1  && Str.equal(sClientTag2, "tradeProcess") == 1) {
//					 tJobLevel.copyRowAdd(iCounter, tTradeGatewaysCME);
//				 } else 
//				 if (Str.equal(sClientTag2, CLIENT_TAG2.updateIceStlDocStatus) == CONST_VALUES.VALUE_OF_TRUE) {
//					 tJobLevel.copyRowAdd(iCounter, teConfirmStatus);
//				 } else 
//				 if (Str.equal(sClientTag2, CLIENT_TAG2.dbRetrieveWithSQL) == CONST_VALUES.VALUE_OF_TRUE) {
//					 tJobLevel.copyRowAdd(iCounter, tdbResponseWithSQL);
//				 } else 
//				 if (Str.equal(sClientTag2, CLIENT_TAG2.sendAlert) == CONST_VALUES.VALUE_OF_TRUE) {
//					 tJobLevel.copyRowAdd(iCounter, tsendAlert);
//				 } else 
//				 if (Str.equal(sClientTag2, CLIENT_TAG2.pushICECM) == CONST_VALUES.VALUE_OF_TRUE) {
//					 tJobLevel.copyRowAdd(iCounter, teConfirm);
				// } else 
					 {
					 tJobLevel.copyRowAdd(iCounter, tOther);
				 }
			 } 

    		 LIB.log("Completed getting job level data report", className);
    		 LIB.log("STEP 4", className);
    		 // Add extra columns and populate for ICE trade gateway
    		 FUNC.addColsForTradeGatewayJobList(tTradeGatewaysICE, className);
    		  
//    		 LIB.log("STEP 4.5", className);
//    		 // Add extra columns and populate for CME trade gateway
//    		 FUNC.addColsForTradeGatewayJobList(tTradeGatewaysCME, className);
//    		 LIB.log("STEP 4.6", className);
    		 
    		 // to save memory/space
    		 if (bShowAllData == false) {
    			 
    			 LIB.safeDelCol(tTradeGatewaysICE, "request_table");
    			 LIB.safeDelCol(tTradeGatewaysICE, "response_table");
    			 LIB.safeDelCol(tTradeGatewaysICE, "first_response_table");
    			 LIB.safeDelCol(tTradeGatewaysICE, "client_tag1_table");
    			 LIB.safeDelCol(tTradeGatewaysICE, "num_client_tag1");
    			 LIB.safeDelCol(tTradeGatewaysICE, "client_tag2_table");
    			 LIB.safeDelCol(tTradeGatewaysICE, "num_client_tag2");

//    			 LIB.safeDelCol(tTradeGatewaysCME, "request_table");
//    			 LIB.safeDelCol(tTradeGatewaysCME, "response_table");
//    			 LIB.safeDelCol(tTradeGatewaysCME, "first_response_table");
//    			 LIB.safeDelCol(tTradeGatewaysCME, "client_tag1_table");
//    			 LIB.safeDelCol(tTradeGatewaysCME, "num_client_tag1");
//    			 LIB.safeDelCol(tTradeGatewaysCME, "client_tag2_table");
//    			 LIB.safeDelCol(tTradeGatewaysCME, "num_client_tag2");
    		 }
    		 
    		// FUNC.addColsForeConfirmJobList(teConfirm, className); 

    	//	 LIB.log("STEP 5", className);
    		// FUNC.massageDataForCmeDbRetrieve(tdbResponseWithSQL, className); 

    		 LIB.log("STEP 5.2", className);

    		 // Move some of the errors to the 'Known Errors' report
//    		 Table tTradeGatewaysICEExpectedErrors = Util.NULL_TABLE;
//    		 {
//    			 tTradeGatewaysICEExpectedErrors = tTradeGatewaysICE.cloneTable();
//        		 iNumRows = LIB.safeGetNumRows(tTradeGatewaysICE);
//    			 for (int iCounter = iNumRows; iCounter >= 1; iCounter--) {
//    				 String last_error_message = LIB.safeGetString(tTradeGatewaysICE, "last_error_message", iCounter);
//    				 if (last_error_message != null && Str.len(last_error_message) >= 1) {
//    					 if (Str.containsSubString(last_error_message, "Int Contact") == 1) {
//    						 if (Str.containsSubString(last_error_message, "DRW TEST") == 1) {
//    							 tTradeGatewaysICE.copyRowAdd(iCounter, tTradeGatewaysICEExpectedErrors);
//    							 tTradeGatewaysICE.delRow(iCounter);
//    						 }
//    					 }
//    				 } 
//    			 }
//    		 }

    		 LIB.log("STEP 5.6", className);
    		 
//    		 LIB.safeAddCol(tsendAlert, "message_type", COL_TYPE_ENUM.COL_STRING);
//    		 LIB.safeAddCol(tsendAlert, "message_details", COL_TYPE_ENUM.COL_STRING);
//    		 LIB.safeAddCol(tsendAlert, "message_details_clob", COL_TYPE_ENUM.COL_CLOB);
    		   
    		 // Move some of the alerts to a 'trade booking error' alert table
//    		 Table tsendAlertTradeBookingError = Util.NULL_TABLE;
//    		 Table tsendAlertTradeBookingExpectedErrors = Util.NULL_TABLE;
//    		 Table tsendAlertIceNonCriticalErrors = Util.NULL_TABLE;
//    		 {
//    			 tsendAlertTradeBookingError = tsendAlert.cloneTable();
//    			 tsendAlertTradeBookingExpectedErrors = tsendAlert.cloneTable();
//    			 tsendAlertIceNonCriticalErrors = tsendAlert.cloneTable();
//        		 iNumRows = LIB.safeGetNumRows(tsendAlert);
//        		 
//    			 for (int iCounter = iNumRows; iCounter >= 1; iCounter--) {
//    				 // For this we want to get the *REQUEST* and not the 'response'.  
//    				 // The response is just that the alert was sent OK, so basically always a value of succeeded.
//    				 Table tLevel1 = LIB.safeGetTable(tsendAlert, "request_table", iCounter);
//    				 if (LIB.safeGetNumRows(tLevel1) >= 1) {
//    					 Table tLevel2 = LIB.safeGetTable(tLevel1, "me:methods", 1);
//    					 if (LIB.safeGetNumRows(tLevel2) >= 1) {
//        					 Table tLevel3 = LIB.safeGetTable(tLevel2, "meth:method", 1);
//        					 if (LIB.safeGetNumRows(tLevel3) >= 1) {
//            					 Table tLevel4 = LIB.safeGetTable(tLevel3, "arg:arguments", 1);
//            					 if (LIB.safeGetNumRows(tLevel4) >= 1) {
//                					 Table tLevel5 = LIB.safeGetTable(tLevel4, "dt:dataTypes", 1);
//                					 if (LIB.safeGetNumRows(tLevel5) >= 1) {
//                    					 Table tLevel6 = LIB.safeGetTable(tLevel5, "oad:auxData", 1);
//                    					 if (LIB.safeGetNumRows(tLevel6) >= 1) {
//                        					 Table tLevel7 = LIB.safeGetTable(tLevel6, "html:BODY", 1);
//                        					 if (LIB.safeGetNumRows(tLevel7) >= 1) {
//                            					 Table tLevel8 = LIB.safeGetTable(tLevel7, "html:TABLE", 1);
//                            					 if (LIB.safeGetNumRows(tLevel8) >= 1) {
//                                					 Table tLevel9 = LIB.safeGetTable(tLevel8, "html:TR", 1);
//                                					 if (LIB.safeGetNumRows(tLevel9) >= 6) {
//                                    					 // Message Type is in row #4 and message details is in row #5
//                                    					 String sMessageType = LIB.safeGetString(tLevel9, "html:TD", 4);
//                                    					 String sMessageDetails = LIB.safeGetString(tLevel9, "html:TD", 5);
//                                    					 LIB.safeSetString(tsendAlert, "message_type", iCounter, sMessageType);
//                                    					 LIB.safeSetString(tsendAlert, "message_details", iCounter, sMessageDetails);
//                                    					 if (Str.len(sMessageDetails) >= 5) {
//                                    						 tsendAlert.setClob("message_details_clob", iCounter, sMessageDetails);
//                                    					 }
//                                					 }
//                            					 }
//                        					 }
//                    					 }
//                					 }
//            					 }
//        					 }
//    					 }
//    				 }
//    				 
//    				 String sMessageDetails = LIB.safeGetString(tsendAlert, "message_details", iCounter);
//    				 // just looking for items with error messages.  A string length of 5 is just an arbitrary number.  Could be 2 or more for same results.
//    				 if (sMessageDetails != null && Str.len(sMessageDetails) >= 5) {
//        				 String sMessageType = LIB.safeGetString(tsendAlert, "message_type", iCounter);
//        				 if (Str.iEqual(sMessageType, ALERT_TYPE.ICEFIX_PE_004) == 1) {
//        					 tsendAlert.copyRowAdd(iCounter, tsendAlertIceNonCriticalErrors);
//        				 } else {// TOxDO
//        					 if ( Str.containsSubString(sMessageDetails, "Int Portfolio value \"bomfid-fx\"") == 1 ||
//        							 Str.containsSubString(sMessageDetails, "has already run as job#") == 1) {
//            					 tsendAlert.copyRowAdd(iCounter, tsendAlertTradeBookingExpectedErrors);
//        					 } else {
//            					 tsendAlert.copyRowAdd(iCounter, tsendAlertTradeBookingError);
//        					 }
//        				 }
//    					 tsendAlert.delRow(iCounter);
//    				 } 
//    			 }
    		// } 
    		  
    		 if (bShowAllData == false) {
//    			 tsendAlert.clearRows();
    			 tOther.clearRows();
//    			 tsendAlertTradeBookingExpectedErrors.clearRows();
//    			 tTradeGatewaysICEExpectedErrors.clearRows();
    		 }
 
    		 // delete these columns in the 'sendAlert' table... they add no value
//    		 LIB.safeDelCol(tsendAlert, "request_table");
//    		 LIB.safeDelCol(tsendAlert, "response_table");
//    		 LIB.safeDelCol(tsendAlert, "client_tag1_table");
//    		 LIB.safeDelCol(tsendAlert, "num_client_tag1");
//    		 LIB.safeDelCol(tsendAlert, "client_tag2_table");
//    		 LIB.safeDelCol(tsendAlert, "num_client_tag2");
//    		 LIB.safeDelCol(tsendAlert, "message_details_clob");
//
//    		 // and delete these columns as the info in them is basically duplicated
//    		 LIB.safeDelCol(tsendAlertTradeBookingError, "request_table");
//    		 LIB.safeDelCol(tsendAlertTradeBookingError, "response_table");
    		 
    		 LIB.log("STEP 5.7", className);
     		 
			 // this is cosmetic, not functional
			 tTradeGatewaysICE.group("start_time");
//			 tTradeGatewaysICEExpectedErrors.group("start_time");
//			 tTradeGatewaysCME.group("start_time");
//			 teConfirm.group("start_time");
//			 teConfirmStatus.group("start_time");
//			 tdbResponseWithSQL.group("start_time");
//			 tsendAlert.group("start_time");
//			 tsendAlertTradeBookingError.group("start_time");
//			 tsendAlertTradeBookingExpectedErrors.group("start_time");
//			 tsendAlertIceNonCriticalErrors.group("start_time");
			 tOther.group("start_time");
			 
    		 LIB.log("STEP 6", className);
    		 tMasterReportTable = CREATE_TABLE.getMasterReportTable(className);

    		 // Add Trade Gateway - ICE
    		 { 
    			 LIB.log("Adding Trade Gateway - ICE to Report Table", className);
    			 int iMaxRow = tMasterReportTable.addRow(); 
        		 LIB.safeSetString(tMasterReportTable, COLS.MASTER_REPORT.description, iMaxRow, "Trade Gateways - ICE Trade Process");
        		 LIB.safeSetInt(tMasterReportTable, COLS.MASTER_REPORT.num_rows, iMaxRow, LIB.safeGetNumRows(tTradeGatewaysICE));
        		 LIB.safeSetTable(tMasterReportTable, COLS.MASTER_REPORT.data, iMaxRow, tTradeGatewaysICE);
        		 String sTableName = "Trade Gateways - ICE";
        		 tTradeGatewaysICE.setTableName(sTableName);
        		 String sTableTitle = "Trade Gateways - ICE: # Rows: " + LIB.safeGetNumRows(tTradeGatewaysICE);
        		 tTradeGatewaysICE.setTableTitle(sTableTitle);
        		 
        		 FUNC.addAndPopulateTheExtraColumnsForIceTradeGateways(tTradeGatewaysICE, className);
    		 }

    		 // Add Trade Gateway - ICE - Expected Errors
//    		 { 
//    			 LIB.log("Adding Trade Gateway - ICE to Report Table", className);
//    			 int iMaxRow = tMasterReportTable.addRow(); 
//        		 LIB.safeSetString(tMasterReportTable, COLS.MASTER_REPORT.description, iMaxRow, "Trade Gateways - ICE Trade Process - Expected Errors");
//        		 LIB.safeSetInt(tMasterReportTable, COLS.MASTER_REPORT.num_rows, iMaxRow, LIB.safeGetNumRows(tTradeGatewaysICEExpectedErrors));
//        		 LIB.safeSetTable(tMasterReportTable, COLS.MASTER_REPORT.data, iMaxRow, tTradeGatewaysICEExpectedErrors);
//        		 String sTableName = "Trade Gateways - ICE - Expected Errors";
//        		 tTradeGatewaysICEExpectedErrors.setTableName(sTableName);
//        		 String sTableTitle = "Trade Gateways - ICE - Expected Errors: # Rows: " + LIB.safeGetNumRows(tTradeGatewaysICEExpectedErrors);
//        		 tTradeGatewaysICEExpectedErrors.setTableTitle(sTableTitle);
//        		  
//        		 if (bShowAllData == false) {
// 					LIB.safeSetString(tMasterReportTable, COLS.MASTER_REPORT.message, iMaxRow, CONST_VALUES.NO_DISPLAY);
//        		 }
//    		 }

    		 // Add Trade Gateway - CME
//    		 { 
//    			 LIB.log("Adding Trade Gateway - CME to Report Table", className);
//        		 int iMaxRow = tMasterReportTable.addRow(); 
//        		 LIB.safeSetString(tMasterReportTable, COLS.MASTER_REPORT.description, iMaxRow, "Trade Gateways - CME Trade Process");
//        		 LIB.safeSetInt(tMasterReportTable, COLS.MASTER_REPORT.num_rows, iMaxRow, LIB.safeGetNumRows(tTradeGatewaysCME));
//        		 LIB.safeSetTable(tMasterReportTable, COLS.MASTER_REPORT.data, iMaxRow, tTradeGatewaysCME);
//        		 String sTableName = "Trade Gateways - CME";
//        		 tTradeGatewaysCME.setTableName(sTableName);
//        		 String sTableTitle = "Trade Gateways - CME: # Rows: " + LIB.safeGetNumRows(tTradeGatewaysCME);
//        		 tTradeGatewaysCME.setTableTitle(sTableTitle);
//    		 }

    		 // Add   teConfirm
//    		 { 
//    			 LIB.log("Adding eConfirm to Report Table", className);
//    			  
//        		 int iMaxRow = tMasterReportTable.addRow(); 
//        		 LIB.safeSetString(tMasterReportTable, COLS.MASTER_REPORT.description, iMaxRow, "eConfirm Send Trades");
//        		 LIB.safeSetInt(tMasterReportTable, COLS.MASTER_REPORT.num_rows, iMaxRow, LIB.safeGetNumRows(teConfirm));
//        		 LIB.safeSetTable(tMasterReportTable, COLS.MASTER_REPORT.data, iMaxRow, teConfirm);
//        		 String sTableName = "eConfirm";
//        		 teConfirm.setTableName(sTableName);
//        		 String sTableTitle = "eConfirm: # Rows: " + LIB.safeGetNumRows(teConfirm);
//        		 teConfirm.setTableTitle(sTableTitle);
//    		 }

    		 // Add   teConfirmStatus
//    		 { 
//    			 LIB.log("Adding eConfirm Status to Report Table", className);
//    			  
//        		 int iMaxRow = tMasterReportTable.addRow(); 
//        		 LIB.safeSetString(tMasterReportTable, COLS.MASTER_REPORT.description, iMaxRow, "eConfirm Status");
//        		 LIB.safeSetInt(tMasterReportTable, COLS.MASTER_REPORT.num_rows, iMaxRow, LIB.safeGetNumRows(teConfirmStatus));
//        		 LIB.safeSetTable(tMasterReportTable, COLS.MASTER_REPORT.data, iMaxRow, teConfirmStatus);
//        		 String sTableName = "eConfirm Status";
//        		 teConfirmStatus.setTableName(sTableName);
//        		 String sTableTitle = "eConfirm Status: # Rows: " + LIB.safeGetNumRows(teConfirmStatus);
//        		 teConfirmStatus.setTableTitle(sTableTitle);
//        		  
//        		 if (bShowAllData == false) {
//            		 LIB.safeSetString(tMasterReportTable, COLS.MASTER_REPORT.message, iMaxRow, CONST_VALUES.NO_DISPLAY);
//        		 }
//    		 }

    		 // Add   CME Gateway dbRetrieveWithSQL
//    		 { 
//    			 LIB.log("Adding CME Gateway dbRetrieveWithSQL to Report Table", className);
//    			 LIB.safeDelCol(tdbResponseWithSQL, "first_response_table");
//    			  
//        		 int iMaxRow = tMasterReportTable.addRow(); 
//        		 LIB.safeSetString(tMasterReportTable, COLS.MASTER_REPORT.description, iMaxRow, "CME Gateway dbRetrieveWithSQL");
//        		 LIB.safeSetInt(tMasterReportTable, COLS.MASTER_REPORT.num_rows, iMaxRow, LIB.safeGetNumRows(tdbResponseWithSQL));
//        		 LIB.safeSetTable(tMasterReportTable, COLS.MASTER_REPORT.data, iMaxRow, tdbResponseWithSQL);
//        		 String sTableName = "CME dbRetrieveWithSQL";
//        		 tdbResponseWithSQL.setTableName(sTableName);
//        		 String sTableTitle = "CME dbRetrieveWithSQL: # Rows: " + LIB.safeGetNumRows(tdbResponseWithSQL);
//        		 tdbResponseWithSQL.setTableTitle(sTableTitle);
//        		 
//				if (bShowAllData == false) {
//					LIB.safeSetString(tMasterReportTable, COLS.MASTER_REPORT.message, iMaxRow, CONST_VALUES.NO_DISPLAY);
//				}
//    		 }

    		 // Add   tsendAlertTradeBookingError
//    		 { 
//    			 LIB.log("Adding Send Alert for Trade Booking Errors to Report Table: Alert - Trade Booking Errors", className);
//    			 LIB.safeDelCol(tsendAlertTradeBookingError, "first_response_table");
//    			  
//        		 int iMaxRow = tMasterReportTable.addRow(); 
//        		 LIB.safeSetString(tMasterReportTable, COLS.MASTER_REPORT.description, iMaxRow, "Alert - Trade Booking Errors");
//        		 LIB.safeSetInt(tMasterReportTable, COLS.MASTER_REPORT.num_rows, iMaxRow, LIB.safeGetNumRows(tsendAlertTradeBookingError));
//        		 LIB.safeSetTable(tMasterReportTable, COLS.MASTER_REPORT.data, iMaxRow, tsendAlertTradeBookingError);
//        		 String sTableName = "Alert - Trade Booking Errors";
//        		 tsendAlertTradeBookingError.setTableName(sTableName);
//        		 String sTableTitle = "Alert - Trade Booking Errors: # Rows: " + LIB.safeGetNumRows(tsendAlertTradeBookingError);
//        		 tsendAlertTradeBookingError.setTableTitle(sTableTitle);
//    		 }  

    		 // Add   tsendAlertTradeBookingExpectedErrors
//    		 { 
//    			 LIB.log("Adding Send Alert for Trade Booking Errors to Report Table: Alert - Expected Trade Booking Errors", className);
//    			 LIB.safeDelCol(tsendAlertTradeBookingExpectedErrors, "first_response_table");
//    			  
//        		 int iMaxRow = tMasterReportTable.addRow(); 
//        		 LIB.safeSetString(tMasterReportTable, COLS.MASTER_REPORT.description, iMaxRow, "Alert - Expected Trade Booking Errors");
//        		 LIB.safeSetInt(tMasterReportTable, COLS.MASTER_REPORT.num_rows, iMaxRow, LIB.safeGetNumRows(tsendAlertTradeBookingExpectedErrors));
//        		 LIB.safeSetTable(tMasterReportTable, COLS.MASTER_REPORT.data, iMaxRow, tsendAlertTradeBookingExpectedErrors);
//        		 String sTableName = "Alert - Expected Trade Booking Errors";
//        		 tsendAlertTradeBookingExpectedErrors.setTableName(sTableName);
//        		 String sTableTitle = "Alert - Expected Trade Booking Errors: # Rows: " + LIB.safeGetNumRows(tsendAlertTradeBookingExpectedErrors);
//        		 tsendAlertTradeBookingExpectedErrors.setTableTitle(sTableTitle);
//        		 
//        		 if (bShowAllData == false) {
//  					LIB.safeSetString(tMasterReportTable, COLS.MASTER_REPORT.message, iMaxRow, CONST_VALUES.NO_DISPLAY);
//         		 }
//    		 }  
    		 
    		 // add tsendAlertIceNonCriticalErrors
//    		 { 
//    			 LIB.log("Adding Send Alert for Trade Booking Errors to Report Table: Alert - ICE Gateway Warnings", className);
//    			 LIB.safeDelCol(tsendAlertIceNonCriticalErrors, "first_response_table");
//    			  
//        		 int iMaxRow = tMasterReportTable.addRow(); 
//        		 LIB.safeSetString(tMasterReportTable, COLS.MASTER_REPORT.description, iMaxRow, "Alert - ICE Gateway Warnings");
//        		 LIB.safeSetInt(tMasterReportTable, COLS.MASTER_REPORT.num_rows, iMaxRow, LIB.safeGetNumRows(tsendAlertIceNonCriticalErrors));
//        		 LIB.safeSetTable(tMasterReportTable, COLS.MASTER_REPORT.data, iMaxRow, tsendAlertIceNonCriticalErrors);
//        		 String sTableName = "Alert - ICE Gateway Warnings";
//        		 tsendAlertIceNonCriticalErrors.setTableName(sTableName);
//        		 String sTableTitle = "Alert - ICE Gateway Warnings: # Rows: " + LIB.safeGetNumRows(tsendAlertIceNonCriticalErrors);
//        		 tsendAlertIceNonCriticalErrors.setTableTitle(sTableTitle);
//    		 }  
    		 
    		 // Add   tsendAlert
//    		 { 
//    			 LIB.log("Adding Send Alert to Report Table", className);
//    			 LIB.safeDelCol(tsendAlert, "first_response_table");
//    			  
//        		 int iMaxRow = tMasterReportTable.addRow(); 
//        		 LIB.safeSetString(tMasterReportTable, COLS.MASTER_REPORT.description, iMaxRow, "OpenLink 'Alert' with no info");
//        		 LIB.safeSetInt(tMasterReportTable, COLS.MASTER_REPORT.num_rows, iMaxRow, LIB.safeGetNumRows(tsendAlert));
//        		 LIB.safeSetTable(tMasterReportTable, COLS.MASTER_REPORT.data, iMaxRow, tsendAlert);
//        		 String sTableName = "Alert (no info)";
//        		 tsendAlert.setTableName(sTableName);
//        		 String sTableTitle = "Alert (no info): # Rows: " + LIB.safeGetNumRows(tsendAlert);
//        		 tsendAlert.setTableTitle(sTableTitle);
//        		 
//        		 if (bShowAllData == false) {
// 					LIB.safeSetString(tMasterReportTable, COLS.MASTER_REPORT.message, iMaxRow, CONST_VALUES.NO_DISPLAY);
//        		 }
//
//    		 }
    		 
    		 // Add   tOther
    		 { 
    			 LIB.log("Adding Other to Report Table", className);
    		//	 LIB.safeDelCol(tdbResponseWithSQL, "first_response_table");
    			 
        		 int iMaxRow = tMasterReportTable.addRow(); 
        		 LIB.safeSetString(tMasterReportTable, "description", iMaxRow, "Other");
        		 LIB.safeSetInt(tMasterReportTable, "num_rows", iMaxRow, LIB.safeGetNumRows(tOther));
        		 LIB.safeSetTable(tMasterReportTable, "data", iMaxRow, tOther);
        		 String sTableName = "Other";
        		 tOther.setTableName(sTableName);
        		 String sTableTitle = "Other: # Rows: " + LIB.safeGetNumRows(tOther);
        		 tOther.setTableTitle(sTableTitle);
        		  
        		 if (bShowAllData == false) {
 					LIB.safeSetString(tMasterReportTable, COLS.MASTER_REPORT.message, iMaxRow, CONST_VALUES.NO_DISPLAY);
        		 }
    		 }
    		 
    		 LIB.log("STEP 7", className);
    		 tMasterReportTable.setColIncrementInt("report_number", 1, 1);
    		 LIB.safeSetColValInt(tMasterReportTable, "run_date", iToday);
    		 LIB.safeSetColFormatAsDate(tMasterReportTable, "run_date");
     		  
    		 // This shows one row per report.  The job level data is in the sub-table
    		 // users will double click on the appropriate report row in the data column to see it
    		 LIB.viewTable(tMasterReportTable, "Activity Log Job and XML Data: " + LIB.VERSION_NUMBER);

    		 boolean bShowJobLevelReport = false;
    		 if (bShowJobLevelReport == true) {
    			 LIB.viewTable(tJobLevel, "Job Level Data, Num Rows: " + LIB.safeGetNumRows(tJobLevel));
    		 }

    		 LIB.log("doEverything: END", className);

         } catch(Exception e ){               
              LIB.log("doEverything", e, className);
         } 

    	// tOcActivityLogDataRawFormOneRowPerActivityID = LIB.destroyTable(tOcActivityLogDataRawFormOneRowPerActivityID);
    	 tJobLevel = LIB.destroyTable(tJobLevel);
    	 tMasterReportTable = LIB.destroyTable(tMasterReportTable); 
    	 
    }
    
    public Table getJobLevelData(Table tActivityLog, int iUniqueTaskRunNumber, String className) throws OException {
   	 
    	Table tReturn = Util.NULL_TABLE;
    	
    	try{
    		tReturn = Table.tableNew();
    		LIB.safeAddCol(tReturn, "job_id", COL_TYPE_ENUM.COL_STRING);
    		LIB.safeAddCol(tReturn, "start_time", COL_TYPE_ENUM.COL_DATE_TIME);
    		LIB.safeAddCol(tReturn, "end_time", COL_TYPE_ENUM.COL_DATE_TIME);
    		LIB.safeAddCol(tReturn, "num_rows", COL_TYPE_ENUM.COL_INT);
    		LIB.safeAddCol(tReturn, "data", COL_TYPE_ENUM.COL_TABLE);

    		LIB.safeAddCol(tReturn, "client_tag1", COL_TYPE_ENUM.COL_STRING);
    		LIB.safeAddCol(tReturn, "client_tag2", COL_TYPE_ENUM.COL_STRING);

    		LIB.safeAddCol(tReturn, "request_table", COL_TYPE_ENUM.COL_TABLE);
    		LIB.safeAddCol(tReturn, "response_table", COL_TYPE_ENUM.COL_TABLE);
    		LIB.safeAddCol(tReturn, "first_response_table", COL_TYPE_ENUM.COL_TABLE);

    		LIB.safeAddCol(tReturn, "client_tag1_table", COL_TYPE_ENUM.COL_TABLE);
    		LIB.safeAddCol(tReturn, "num_client_tag1", COL_TYPE_ENUM.COL_INT);
    		LIB.safeAddCol(tReturn, "client_tag2_table", COL_TYPE_ENUM.COL_TABLE);
    		LIB.safeAddCol(tReturn, "num_client_tag2", COL_TYPE_ENUM.COL_INT);
    		
    		LIB.safeColHide(tReturn, "client_tag1_table");
    		LIB.safeColHide(tReturn, "num_client_tag1");
    		LIB.safeColHide(tReturn, "client_tag2_table");
    		LIB.safeColHide(tReturn, "num_client_tag2");
    		
    		// grouping is functional
    		tActivityLog.group("job_id");
    		
    		Table tSubTable = Util.NULL_TABLE;
    		String sPriorJob = "xxx-1";
    		int iNumRows = LIB.safeGetNumRows(tActivityLog);
    		int iMaxRow = -1;
    		for (int iCounter = 1; iCounter <= iNumRows; iCounter++) {
    			String sCurrentJob = LIB.safeGetString(tActivityLog, "job_id", iCounter);
    			// ignore job 0
    			if (Str.iEqual(sCurrentJob, "0") == 1) {
    				continue;
    			}
    			
    			boolean bAddNewRow = false;
    			if (Str.equal(sPriorJob, sCurrentJob) != 1) {
    				bAddNewRow = true;
    			}
    			if (bAddNewRow == true) {
    				iMaxRow = tReturn.addRow();
    				LIB.safeSetString(tReturn, "job_id", iMaxRow, sCurrentJob);
    				tSubTable = tActivityLog.cloneTable();
    				LIB.safeSetTableWithSetTableTitle(tReturn, "data", iMaxRow, tSubTable, "Job: " + sCurrentJob);
    			}
    			
    			tActivityLog.copyRowAdd(iCounter, tSubTable); 
    			
    			// for next loop
    			sPriorJob = sCurrentJob;
    		}

    		// start/end times
    		iNumRows = LIB.safeGetNumRows(tReturn);
    		for (int iCounter = 1; iCounter <= iNumRows; iCounter++) {
    			Table tSub = LIB.safeGetTable(tReturn, "data", iCounter);
    		 	
    			int iSubNumRows = LIB.safeGetNumRows(tSub);
    			if (iSubNumRows >= 1) {
        			// This is functional for times
        			tSub.group("activity_timestamp");
        			int iStartDate = tSub.getDate("activity_timestamp", 1);
        			int iStartTime = tSub.getTime("activity_timestamp", 1);
        			int iEndDate = tSub.getDate("activity_timestamp", iSubNumRows);
        			int iEndTime = tSub.getTime("activity_timestamp", iSubNumRows);
        			
        			tReturn.setDateTimeByParts("start_time", iCounter, iStartDate, iStartTime);
        			tReturn.setDateTimeByParts("end_time", iCounter, iEndDate, iEndTime);
    			}
    		}
    		
    		iNumRows = LIB.safeGetNumRows(tReturn);
    		for (int iCounter = 1; iCounter <= iNumRows; iCounter++) {
    			Table tSub = LIB.safeGetTable(tReturn, "data", iCounter);
    			int iSubNumRows = LIB.safeGetNumRows(tSub);
    			LIB.safeSetInt(tReturn, "num_rows", iCounter, iSubNumRows);
    			
    			// This is cosmetic
    			tSub.group("activity_id");
    		}

    		iNumRows = LIB.safeGetNumRows(tReturn);
    		for (int iCounter = 1; iCounter <= iNumRows; iCounter++) {
    			
    			Table tSub = LIB.safeGetTable(tReturn, "data", iCounter); 

    			Table tClientTag1 = Table.tableNew();
    			LIB.safeAddCol(tClientTag1, "client_tag1", COL_TYPE_ENUM.COL_STRING);
    			
    			Table tClientTag2 = Table.tableNew();
    			LIB.safeAddCol(tClientTag2, "client_tag2", COL_TYPE_ENUM.COL_STRING);
    			
    			int iSubNumRows = LIB.safeGetNumRows(tSub);
        		for (int iSubCounter = 1; iSubCounter <= iSubNumRows; iSubCounter++) {
        			String sClientTag1 = LIB.safeGetString(tSub, "client_tag1", iSubCounter);
        			if (sClientTag1 != null && Str.len(sClientTag1) >= 2) {
        				int iNewRow = tClientTag1.addRow();
        				LIB.safeSetString(tClientTag1, "client_tag1", iNewRow, sClientTag1);
        			}
        			String sClientTag2 = LIB.safeGetString(tSub, "client_tag2", iSubCounter);
        			if (sClientTag2 != null && Str.len(sClientTag2) >= 2) {
        				int iNewRow = tClientTag2.addRow();
        				LIB.safeSetString(tClientTag2, "client_tag2", iNewRow, sClientTag2);
        			}
        		}
        		
        		// grouping is functional, needed for distinct
        		tClientTag1.group("client_tag1");
        		tClientTag1.distinctRows();
        		
        		tClientTag2.group("client_tag2");
        		tClientTag2.distinctRows();
        		
        		int iNumClient1Rows = LIB.safeGetNumRows(tClientTag1);
        		int iNumClient2Rows = LIB.safeGetNumRows(tClientTag2);
        		
        		if (iNumClient1Rows >= 1) {
        			// always get the value from the last row
        			String sClientTag1 = LIB.safeGetString(tClientTag1, "client_tag1", iNumClient1Rows);
    				LIB.safeSetString(tReturn, "client_tag1", iCounter, sClientTag1);
        		}
        		if (iNumClient2Rows >= 1) {
        			// always get the value from the last row
        			String sClientTag2 = LIB.safeGetString(tClientTag2, "client_tag2", iNumClient2Rows);
    				LIB.safeSetString(tReturn, "client_tag2", iCounter, sClientTag2);
        		}

				LIB.safeSetTableWithSetTableTitle(tReturn, "client_tag1_table", iCounter, tClientTag1, "ClientTag1: " + iNumClient1Rows);
				LIB.safeSetInt(tReturn, "num_client_tag1", iCounter, iNumClient1Rows);
				
				LIB.safeSetTableWithSetTableTitle(tReturn, "client_tag2_table", iCounter, tClientTag2, "ClientTag2: " + iNumClient2Rows);
				LIB.safeSetInt(tReturn, "num_client_tag2", iCounter, iNumClient2Rows);
    		}
    		  
    		iNumRows = LIB.safeGetNumRows(tReturn);
    		for (int iCounter = 1; iCounter <= iNumRows; iCounter++) {
    			Table tSub = LIB.safeGetTable(tReturn, "data", iCounter); 

    			// grouping here is functional
    			tSub.group("activity_id");
    			 
    			int iSubNumRows = LIB.safeGetNumRows(tSub);
    			// loop 1 of 3.  Note that we *count down*
        		for (int iSubCounter = iSubNumRows; iSubCounter >= 1; iSubCounter--) {
        			
        			try{
            			int iHasRequestFlag = LIB.safeGetInt(tSub, "has_job_data_request", iSubCounter); 
            			if (iHasRequestFlag == 1) {
            				Table tRequest = LIB.safeGetTable(tSub, "job_data_request_table", iSubCounter);
            				if (LIB.safeGetNumRows(tRequest) >= 1) {
            					Table tCopy = Util.NULL_TABLE;
            					boolean bGotData = false;
            					if (tRequest.getColType(1) == COL_TYPE_ENUM.COL_TABLE.toInt()) {
                					// go down 3 levels
                					Table tLevel2 = tRequest.getTable(1, 1);
                					if (tLevel2.getColType(1) == COL_TYPE_ENUM.COL_TABLE.toInt()) {
                    					Table tLevel3 = tLevel2.getTable(1, 1);
                    					tCopy = tLevel3.copyTable();
                    					bGotData = true;
                					}
            					}
            					
            					// if we didn't get a sub table, get the original/main table
            					if (bGotData == false) {
            						tCopy = tRequest.copyTable();
            					}

            					// back into the main table, iCounter, not iSubCounter
            					LIB.safeSetTable(tReturn, "request_table", iCounter, tCopy);
            				}
            				// only get the first item in the loop
            				break;
            			} 
        			} catch (Exception e) {
        				// don't log this
        			}
        		}
        		 
    			// loop 2 of 3.  Note that we *count down*
        		for (int iSubCounter = iSubNumRows; iSubCounter >= 1; iSubCounter--) {
        			try{
            			int iHasResponseFlag = LIB.safeGetInt(tSub, "has_job_data_response", iSubCounter); 
            			if (iHasResponseFlag == 1) {
            				Table tResponse = LIB.safeGetTable(tSub, "job_data_response_table", iSubCounter);
            				
            				if (LIB.safeGetNumRows(tResponse) >= 1) {
            					Table tCopy = Util.NULL_TABLE;
            					boolean bGotData = false;
            					if (tResponse.getColType(1) == COL_TYPE_ENUM.COL_TABLE.toInt()) {
                					// go down 3 levels
                					Table tLevel2 = tResponse.getTable(1, 1);
                					if (tLevel2.getColType(1) == COL_TYPE_ENUM.COL_TABLE.toInt()) {
                    					Table tLevel3 = tLevel2.getTable(1, 1);
                    					tCopy = tLevel3.copyTable();
                    					bGotData = true;
                					}
            					}
            					
            					// if we didn't get a sub table, get the original/main table
            					if (bGotData == false) {
            						tCopy = tResponse.copyTable();
            					}

            					// back into the main table, iCounter, not iSubCounter
            					LIB.safeSetTable(tReturn, "response_table", iCounter, tCopy);
            				}
            				// only get the first item in the loop
            				break;
            			}
        				
        			} catch (Exception e) {
        				// don't log this
        			}
        			
        		}
        		
    			// loop 3 of 3.  Note that we count *UP*
        		for (int iSubCounter = 1; iSubCounter <= iSubNumRows; iSubCounter++) {
        			try{
            			int iHasResponseFlag = LIB.safeGetInt(tSub, "has_job_data_response", iSubCounter); 
            			if (iHasResponseFlag == 1) {
            				Table tResponse = LIB.safeGetTable(tSub, "job_data_response_table", iSubCounter);
            				
            				if (LIB.safeGetNumRows(tResponse) >= 1) {
            					Table tCopy = Util.NULL_TABLE;
            					boolean bGotData = false;
            					if (tResponse.getColType(1) == COL_TYPE_ENUM.COL_TABLE.toInt()) {
                					// go down 3 levels
                					Table tLevel2 = tResponse.getTable(1, 1);
                					if (tLevel2.getColType(1) == COL_TYPE_ENUM.COL_TABLE.toInt()) {
                    					Table tLevel3 = tLevel2.getTable(1, 1);
                    					tCopy = tLevel3.copyTable();
                    					bGotData = true;
                					}
            					}
            					
            					// if we didn't get a sub table, get the original/main table
            					if (bGotData == false) {
            						tCopy = tResponse.copyTable();
            					}

            					// back into the main table, iCounter, not iSubCounter
            					LIB.safeSetTable(tReturn, "first_response_table", iCounter, tCopy);
            				}
            				// only get the first item in the loop
            				break;
            			}
        				
        			} catch (Exception e) {
        				// don't log this
        			}
        			
        		}
    		} 
    		
    	} catch(Exception e ){               
         LIB.log("getJobLevelData", e, className);
    	}
		return tReturn; 

    }

    public static class CREATE_TABLE {    
    	
        private static Table getMasterReportTable(String className) throws OException{
        	Table tReturn = Util.NULL_TABLE;
              try{
            	  
            	  tReturn = Table.tableNew();
            	  LIB.safeAddCol(tReturn, COLS.MASTER_REPORT.run_date, COL_TYPE_ENUM.COL_INT);
            	  LIB.safeAddCol(tReturn, COLS.MASTER_REPORT.report_number, COL_TYPE_ENUM.COL_INT);
            	  LIB.safeAddCol(tReturn, COLS.MASTER_REPORT.description, COL_TYPE_ENUM.COL_STRING);
            	  LIB.safeAddCol(tReturn, COLS.MASTER_REPORT.num_rows, COL_TYPE_ENUM.COL_INT);
            	  LIB.safeAddCol(tReturn, COLS.MASTER_REPORT.data, COL_TYPE_ENUM.COL_TABLE);
            	  LIB.safeAddCol(tReturn, COLS.MASTER_REPORT.message, COL_TYPE_ENUM.COL_STRING);
            	  LIB.safeAddCol(tReturn, "xxx", COL_TYPE_ENUM.COL_CELL);
            	  LIB.safeColHide(tReturn, "xxx");

              } catch(Exception e ){               
            	  LIB.log("getMasterReportTable", e, className);
              } 
              return tReturn;
        }
    }
    
    public static class FUNC {    
     	
    	// TODO
    	// added July 2022
    	private static void addAndPopulateTheExtraColumnsForIceTradeGateways(Table tMaster, String className) throws OException{
            try{ 
         	   
               int iNumRows = LIB.safeGetNumRows(tMaster);
         	   if (iNumRows >= 1) {
         		   
         		   // insert cols
         		  tMaster.insertCol("parent_trade_id", "trade_details", COL_TYPE_ENUM.COL_STRING);
            	  tMaster.insertCol("exchange_product", "trade_details", COL_TYPE_ENUM.COL_STRING);

            	  tMaster.insertCol("pipeline", "trade_details", COL_TYPE_ENUM.COL_STRING);
            	  tMaster.insertCol("zone", "trade_details", COL_TYPE_ENUM.COL_STRING);
            	  tMaster.insertCol("location", "trade_details", COL_TYPE_ENUM.COL_STRING);

            	  tMaster.insertCol("deal_booked_wrong", "trade_details", COL_TYPE_ENUM.COL_STRING);
            	  tMaster.insertCol("current_deal_status", "trade_details", COL_TYPE_ENUM.COL_INT);
  
            	  // set the Col Titles on the new cols
            	  tMaster.setColTitle("parent_trade_id", "Parent\nTrade ID");
    			  tMaster.setColTitle("exchange_product", "Exchange\nProduct");
    			  tMaster.setColTitle("deal_booked_wrong", "Deal Booked\nWrong?");
    			  tMaster.setColTitle("current_deal_status", "Current\nDeal Status?");

    			  // And turn on Ref Formatting
             	  LIB.safeSetColFormatAsRef(tMaster, "current_deal_status", SHM_USR_TABLES_ENUM.TRANS_STATUS_TABLE);
    		
            	  
            	  for (int iCounter = 1; iCounter <= iNumRows; iCounter++) {
            		  Table tTradeDetails = LIB.safeGetTable(tMaster, "trade_details", iCounter);
            		  
            		  int iSubNumRows = LIB.safeGetNumRows(tTradeDetails);
            		  
            		  if (iSubNumRows >= 1) {
                		  int iPremiumPerUnitRow = tTradeDetails.unsortedFindString("tb:name", "Premium/Unit", SEARCH_CASE_ENUM.CASE_INSENSITIVE);
    					  if (iPremiumPerUnitRow >= 1) {
        					  int iRateStrikeRow = tTradeDetails.unsortedFindString("tb:name", "Rate/Strike", SEARCH_CASE_ENUM.CASE_INSENSITIVE);
        					  if (iRateStrikeRow < 1) {
        						  String sValue = "Yes";
        						  LIB.safeSetString(tMaster, "deal_booked_wrong", iCounter, sValue);
        					  }
    					  }
            		  }
            		  
            		  for (int iSubCounter = 1; iSubCounter <= iSubNumRows; iSubCounter++) {
	            		  String sFieldName = LIB.safeGetString(tTradeDetails, "tb:name", iSubCounter);
	            		  if (Str.iEqual(sFieldName, "Parent Trade ID") == 1) {
       	            		  String sValue = LIB.safeGetString(tTradeDetails, "tb:value", iSubCounter);
       	            		  LIB.safeSetString(tMaster, "parent_trade_id", iCounter, sValue);
       	            		  break;
	            		  }
	            	  }
	            	  

	            	  for (int iSubCounter = 1; iSubCounter <= iSubNumRows; iSubCounter++) {
	            		  String sFieldName = LIB.safeGetString(tTradeDetails, "tb:name", iSubCounter);
	            		  if (Str.iEqual(sFieldName, "Exchange Product") == 1) {
       	            		  String sValue = LIB.safeGetString(tTradeDetails, "tb:value", iSubCounter);
       	            		  LIB.safeSetString(tMaster, "exchange_product", iCounter, sValue);
       	            		  break;
	            		  }
	            	  }

	            	  for (int iSubCounter = 1; iSubCounter <= iSubNumRows; iSubCounter++) {
	            		  String sFieldName = LIB.safeGetString(tTradeDetails, "tb:name", iSubCounter);
	            		  if (Str.iEqual(sFieldName, "Pipeline") == 1) {
       	            		  String sValue = LIB.safeGetString(tTradeDetails, "tb:value", iSubCounter);
       	            		  LIB.safeSetString(tMaster, "pipeline", iCounter, sValue);
       	            		  break;
	            		  }
	            	  }

	            	  for (int iSubCounter = 1; iSubCounter <= iSubNumRows; iSubCounter++) {
	            		  String sFieldName = LIB.safeGetString(tTradeDetails, "tb:name", iSubCounter);
	            		  if (Str.iEqual(sFieldName, "Zone") == 1) {
       	            		  String sValue = LIB.safeGetString(tTradeDetails, "tb:value", iSubCounter);
       	            		  LIB.safeSetString(tMaster, "zone", iCounter, sValue);
       	            		  break;
	            		  }
	            	  }
	            	  
	            	  for (int iSubCounter = 1; iSubCounter <= iSubNumRows; iSubCounter++) {
	            		  String sFieldName = LIB.safeGetString(tTradeDetails, "tb:name", iSubCounter);
	            		  if (Str.iEqual(sFieldName, "Location") == 1) {
       	            		  String sValue = LIB.safeGetString(tTradeDetails, "tb:value", iSubCounter);
       	            		  LIB.safeSetString(tMaster, "location", iCounter, sValue);
       	            		  break;
	            		  }
	            	  }
            	  }
         		   
         	   }
         	   

         	   if (iNumRows >= 1) {
         		   
         	   

         		   // get current deal status
         	  
         		  Table tDealsNums = Table.tableNew();
         		  LIB.safeAddCol(tDealsNums, "deal_tracking_num", COL_TYPE_ENUM.COL_INT);
         		  
         		  tMaster.copyColDistinct("deal_num", tDealsNums, "deal_tracking_num");
         		  
         		  tDealsNums.deleteWhereValue("deal_tracking_num", -1);
         		  tDealsNums.deleteWhereValue("deal_tracking_num", 0);
         		  
         		  Table tAbTran = Table.tableNew();
         		  LIB.safeAddCol(tAbTran, "deal_num", COL_TYPE_ENUM.COL_INT);
         		  LIB.safeAddCol(tAbTran, "current_deal_status", COL_TYPE_ENUM.COL_INT);

         		  String sWhat = "deal_tracking_num, tran_status";
         		  String sWhere = "current_flag = 1";
         		  LIB.loadFromDbWithWhatWhere(tAbTran, "ab_tran", tDealsNums, sWhat, sWhere, false, className);
         		  sWhat = "current_deal_status";
         		  sWhere = "deal_num EQ $deal_num";
         		  LIB.select(tMaster, tAbTran, sWhat, sWhere, false, className);
 
         		  // TOxDO
         		//  LIB.viewTable(tDealsNums, "deal Numbers sdfd");
         		//  LIB.viewTable(tAbTran, "tAbTran  dgd dfd");
         		 // LIB.viewTable(tMaster, "tMaster sgsgd   dfd AFTER");
         		  
         		  tDealsNums = LIB.destroyTable(tDealsNums);
         		  tAbTran = LIB.destroyTable(tAbTran);
         		 

         	   }
         	   
            } catch(Exception e ){               
         	  LIB.log("addAndPopulateTheExtraColumnsForIceTradeGateways", e, className);
           } 
     }

        private static void addBlobDataTable(Table tOcActivityLogDataRawFormOneRowPerActivityID, String className) throws OException{
        	Table tActivityIDs = Util.NULL_TABLE;
        	Table tBlobData = Util.NULL_TABLE; 
              try{ 
            	   
         		 LIB.log("STEP 2.5", className);
         		 tActivityIDs = Table.tableNew();
         		 LIB.safeAddCol(tActivityIDs, "activity_id", COL_TYPE_ENUM.COL_INT);
         		 
         		 tOcActivityLogDataRawFormOneRowPerActivityID.copyColDistinct("activity_id", tActivityIDs, "activity_id");

     			 tBlobData = Table.tableNew();
         		 String sWhat = "activity_id, data_format, data_state, blob_size, the_blob";
         		 String sWhere = "data_format = 5";
         		 LIB.loadFromDbWithWhatWhere(tBlobData, "oc_activity_data_log", tActivityIDs, sWhat, sWhere, true, className);
         		 
         		 LIB.safeAddCol(tBlobData, "data", COL_TYPE_ENUM.COL_TABLE);
         		 LIB.safeAddCol(tBlobData, "num_data_rows", COL_TYPE_ENUM.COL_INT);
         		 int iNumRows = LIB.safeGetNumRows(tBlobData);
         		 for (int iCounter = 1; iCounter <= iNumRows; iCounter++) {
         			 try {
             			 int iDataFormat = LIB.safeGetInt(tBlobData, "data_format", iCounter);
             			 if (iDataFormat == 5) {
                 			 Table tBlobDataOneTable = tBlobData.getTableFromByteArray("the_blob", iCounter);
                 			 LIB.safeSetTableWithSetTableTitle(tBlobData, "data", iCounter, tBlobDataOneTable, "Blob Data");
                 			 int iNumSubRows = LIB.safeGetNumRows(tBlobDataOneTable);
                 			 LIB.safeSetInt(tBlobData, "num_data_rows", iCounter, iNumSubRows);
             			 }
         			 } catch (Exception e) {
         				 // don't log this
         			 }
         		 } 
         		 LIB.viewTable(tBlobData, "blob data");


              } catch(Exception e ){               
            	  LIB.log("addBlobDataTable", e, className);
              } 
           	 tActivityIDs = LIB.destroyTable(tActivityIDs);
           	 tBlobData = LIB.destroyTable(tBlobData);
        }

        
        private static void addColsForTradeGatewayJobList(Table tMaster, String className) throws OException{
              try{

            	  tMaster.setColTitle("start_time", "Start Trade\nBooking Local Time");
            	  tMaster.setColTitle("end_time", "End Trade\nBooking Local Time");
            	  
            	  
            	  tMaster.insertCol("trade_execution_time_GMT", "start_time", COL_TYPE_ENUM.COL_STRING); 

            	  tMaster.insertCol("seconds_part1_ICE_to_XML_Engine", "num_rows", COL_TYPE_ENUM.COL_INT);
            	  tMaster.insertCol("seconds_part2_Trade_Booked", "num_rows", COL_TYPE_ENUM.COL_INT);
            	  tMaster.insertCol("seconds_total_time", "num_rows", COL_TYPE_ENUM.COL_INT);
            	  

            	  
            	  tMaster.insertCol("tran_num", "num_rows", COL_TYPE_ENUM.COL_INT);
            	  tMaster.insertCol("ins_num", "num_rows", COL_TYPE_ENUM.COL_INT);
            	  tMaster.insertCol("deal_num", "num_rows", COL_TYPE_ENUM.COL_INT);
            	  tMaster.insertCol("trade_details", "num_rows", COL_TYPE_ENUM.COL_TABLE);
            	  tMaster.insertCol("template", "num_rows", COL_TYPE_ENUM.COL_STRING);
            	   

    			  tMaster.setColTitle("trade_execution_time_GMT", "Trade Execution\nTime GMT");
    			  tMaster.setColTitle("seconds_part1_ICE_to_XML_Engine", "ICE_to_XML_Engine\nTime In Seconds");
    			  tMaster.setColTitle("seconds_part2_Trade_Booked", "Trade Booking Part2\nTime In Seconds");
    			  tMaster.setColTitle("seconds_total_time", "Total Time\n(Seconds)");

    			 
            	  LIB.safeSetColValInt(tMaster, "tran_num", -1);
            	  LIB.safeSetColValInt(tMaster, "ins_num", -1);
            	  LIB.safeSetColValInt(tMaster, "deal_num", -1);
 
            	  tMaster.insertCol("number_of_errors", "num_rows", COL_TYPE_ENUM.COL_INT);

            	  tMaster.insertCol("error_flag", "num_rows", COL_TYPE_ENUM.COL_INT);
            	  LIB.safeSetColFormatAsRef(tMaster, "error_flag", SHM_USR_TABLES_ENUM.NO_YES_TABLE);
            	  
            	  tMaster.insertCol("last_error_message", "num_rows", COL_TYPE_ENUM.COL_STRING);
             	  
            	  int iNumJobs = LIB.safeGetNumRows(tMaster);
            	  for (int iCounter = 1; iCounter <= iNumJobs; iCounter++) {
            		  int iNumErrors = 0;
            		  String sError = "";
            		  
                	  {
            			  // tData is the set of rows, original raw data from oc_activity_log
                		  Table tData = LIB.safeGetTable(tMaster, "data", iCounter);
                		  
                		  // Useful SQL 
                		  // select exp_defn_id, defn_name, script_id  from rsk_exposure_defn  WHERE script_id in (22225, 21437, 20954, 21103, 21505)
                		  // select node_name, client_data_id from dir_node WHERE client_data_id in (22225, 21437, 20954, 21103, 21505)
 
                		  int iSubNumRows = LIB.safeGetNumRows(tData);
                		  if (iSubNumRows >= 1) {
                			  
                			  tData.insertCol("ops_service_info", "contains_data", COL_TYPE_ENUM.COL_STRING);
                			  
                			  // TODO, redo this at some point to be more generic, so as to work for new scripts
                			  
                        	  for (int iSubCounter = 1; iSubCounter <= iSubNumRows; iSubCounter++) {
                        		  int iCode = LIB.safeGetInt(tData, "activity_code", iSubCounter);
                        		  if (iCode == 1021 || iCode == 1022) {
                            		  String sDescription = LIB.safeGetString(tData, "description", iSubCounter);
                            		  String sInfo = "";
                            		  if (Str.containsSubString(sDescription, "plugin id 22225") == 1) {
                            			  sInfo = "Script: Broker_fee_pre, Ops Service: Broker_fee_pre";
                            		  } else 
                            		  if (Str.containsSubString(sDescription, "plugin id 21437") == 1) {
                            			  sInfo = "Script: BMO_SetFieldValues, Ops Service: SetFieldValues";
                            		  } else
                            		  if (Str.containsSubString(sDescription, "plugin id 20954") == 1) {
                            			  sInfo = "Script: BMO29_RevenueCalculator_pr, Ops Service: BMO29_RevenueCalculator_pr";
                            		  } else
                            		  if (Str.containsSubString(sDescription, "plugin id 21103") == 1) {
                            			  sInfo = "Script: BMO_ICE_Transaction_pr, Ops Service: BMO_ICE_TRANSIT_MAPPING";
                            		  } else
                            		  if (Str.containsSubString(sDescription, "plugin id 21505") == 1) {
                            			  sInfo = "Script: BMO_GMO_Amend_pr, Ops Service: GMO Check";
	                        		  } else
	                        		  if (Str.containsSubString(sDescription, "plugin id 22073") == 1) {
	                        			  sInfo = "Script: bmo_cmtradepreprocess, Ops Service: BMO_ICE_CM_TradeProcessing";
	                        		  } else
	                        		  if (Str.containsSubString(sDescription, "plugin id 20908") == 1) {
	                        			  sInfo = "Script: BMO58_ChangeSwapCurve_pr, Ops Service: BMO58_ChangeSwapCurve_pr";
	                        		  } else
	                        		  if (Str.containsSubString(sDescription, "plugin id 21404") == 1) {
	                        			  sInfo = "Script: BMO_BrokerFeeCalculator_pr, Ops Service: BMO_BrokerFeeCalculator_pr";
	                        		  }
                            		     
                            		  LIB.safeSetString(tData, "ops_service_info", iSubCounter, sInfo);

                        		  }
                        	  }
                		  }
                	  }
            		  
                	  
            		  boolean bAddError = false;
            		  Table tRequestTable = LIB.safeGetTable(tMaster, "request_table", iCounter);
            		  if (Table.isTableValid(tRequestTable) != 1 || LIB.safeGetNumRows(tRequestTable) < 1) {
            			  bAddError = true;
            		  }
            		   
            		  if (bAddError == true) {
            			  // tData is the set of rows, original raw data from oc_activity_log
                		  Table tData = LIB.safeGetTable(tMaster, "data", iCounter);
            			  // sorting is functional
                		  tData.group("activity_id");
            			  int iSubNumRows = LIB.safeGetNumRows(tData);
            			  for (int iSubCounter = 1; iSubCounter <= iSubNumRows; iSubCounter++) {
            				  int iActivityCode = LIB.safeGetInt(tData, "activity_code", iSubCounter);
            				  if (iActivityCode == 1017) {
            					  iNumErrors = iNumErrors + 1;
            					  sError = LIB.safeGetString(tData, "description", iSubCounter);
            				  }
            			  }
            		  }
            		  
            		  if (iNumErrors >= 1) {
    					  LIB.safeSetInt(tMaster, "number_of_errors", iCounter, iNumErrors);
    					  LIB.safeSetInt(tMaster, "error_flag", iCounter, CONST_VALUES.VALUE_OF_TRUE);
    					  LIB.safeSetString(tMaster, "last_error_message", iCounter, sError);
            		  }            		  
            	  }

            	  for (int iCounter = 1; iCounter <= iNumJobs; iCounter++) {
            		  try {
                		  int iErrorFlag = LIB.safeGetInt(tMaster, "error_flag", iCounter);         		  
                		  if (iErrorFlag != CONST_VALUES.VALUE_OF_TRUE) {
                			  
                		 	  Table tRequest = LIB.safeGetTable(tMaster, "request_table", iCounter);
                    		  Table tMethods = LIB.safeGetTable(tRequest, "me:methods", CONST_VALUES.ROW_TO_GET_OR_SET);
            				  Table tMethod = LIB.safeGetTable(tMethods, "meth:method", CONST_VALUES.ROW_TO_GET_OR_SET);
            				  Table tArgs = LIB.safeGetTable(tMethod, "arg:arguments", CONST_VALUES.ROW_TO_GET_OR_SET);
            				  Table tDataTypes = LIB.safeGetTable(tArgs, "dt:dataTypes", CONST_VALUES.ROW_TO_GET_OR_SET);
            				  Table tTradeBuilder = LIB.safeGetTable(tDataTypes, "tb:tradeBuilder", CONST_VALUES.ROW_TO_GET_OR_SET);
            				  Table tTranField = LIB.safeGetTable(tTradeBuilder, "tb:tradeField", CONST_VALUES.ROW_TO_GET_OR_SET);
            				  
            				  if (LIB.safeGetNumRows(tTranField) >= 1) {
            					  Table tCopy = tTranField.copyTable();
            					  LIB.safeSetTable(tMaster, "trade_details", iCounter, tCopy);
            				  }
                		  }
            		  } catch (Exception e) {
            			  
            		  }
            	  }

            	  // get the Execution Time
            	  for (int iCounter = 1; iCounter <= iNumJobs; iCounter++) {
            		  try {
                		  int iErrorFlag = LIB.safeGetInt(tMaster, "error_flag", iCounter);         		  
                		  if (iErrorFlag != CONST_VALUES.VALUE_OF_TRUE) {
            				  Table tTranField = LIB.safeGetTable(tMaster, "trade_details", iCounter);
            				  int iSubNumRows = LIB.safeGetNumRows(tTranField);
            						  
            				  if (iSubNumRows >= 1) {
            	            	  for (int iSubCounter = 1; iSubCounter <= iSubNumRows; iSubCounter++) {
            	            		  String sFieldName = LIB.safeGetString(tTranField, "tb:name", iSubCounter);
            	            		  if (Str.iEqual(sFieldName, "Execution Time (GMT)") == 1) {
                   	            		  String sValue = LIB.safeGetString(tTranField, "tb:value", iSubCounter);
                   	            		  LIB.safeSetString(tMaster, "trade_execution_time_GMT", iCounter, sValue);
                   	            		  break;
            	            		  }
            	            	  }
            				  }
                		  }
            		  } catch (Exception e) {
            			  
            		  }
            	  }

            	  // Attempt one of two to get the deal_tracking_number
            	  // Get from the *last* response_table
            	  for (int iCounter = 1; iCounter <= iNumJobs; iCounter++) {
            		  try {

                		  int iErrorFlag = LIB.safeGetInt(tMaster, "error_flag", iCounter);         		  
                		  if (iErrorFlag != CONST_VALUES.VALUE_OF_TRUE) {

                    		  Table tResponse = LIB.safeGetTable(tMaster, "response_table", iCounter);
                    		  Table tMethods = LIB.safeGetTable(tResponse, "me:methods", CONST_VALUES.ROW_TO_GET_OR_SET);
            				  Table tMethod = LIB.safeGetTable(tMethods, "meth:method", CONST_VALUES.ROW_TO_GET_OR_SET);
            				  Table tResults = LIB.safeGetTable(tMethod, "rslt:results", CONST_VALUES.ROW_TO_GET_OR_SET);
            				  Table tDataTypes = LIB.safeGetTable(tResults, "dt:dataTypes", CONST_VALUES.ROW_TO_GET_OR_SET);
            				  Table tTradeBuilder = LIB.safeGetTable(tDataTypes, "tb:tradeBuilder", CONST_VALUES.ROW_TO_GET_OR_SET);
            				  String sTemplateReference = LIB.safeGetString(tTradeBuilder, "tb:templateReference", CONST_VALUES.ROW_TO_GET_OR_SET);
            				  LIB.safeSetString(tMaster, "template", iCounter, sTemplateReference);
            				  
            				  Table tTradeField = LIB.safeGetTable(tTradeBuilder, "tb:tradeField", CONST_VALUES.ROW_TO_GET_OR_SET);
            				  int iNumRowsTradeField = LIB.safeGetNumRows(tTradeField);
            				  for (int iSubCounter = 1; iSubCounter <= iNumRowsTradeField; iSubCounter++) {
            					  String sName = LIB.safeGetString(tTradeField, "tb:name", iSubCounter);
            					  String sValue = LIB.safeGetString(tTradeField, "tb:value", iSubCounter);
            					  
            					  if (Str.iEqual(sName, "Deal Tracking Num") == 1) {
            						  int iValue = Str.strToInt(sValue);
            						  LIB.safeSetInt(tMaster, "deal_num", iCounter, iValue);
            					  }  
            					  if (Str.iEqual(sName, "Ins Num") == 1) {
            						  int iValue = Str.strToInt(sValue);
            						  LIB.safeSetInt(tMaster, "ins_num", iCounter, iValue);
            					  }  
            					  if (Str.iEqual(sName, "Tran Num") == 1) {
            						  int iValue = Str.strToInt(sValue);
            						  LIB.safeSetInt(tMaster, "tran_num", iCounter, iValue);
            					  }
            				  } 
            				  
                		  }
            		  } catch (Exception e) {
            			  
            		  }
            	  }
   
            	  // Attempt 2 of 2 to get the deal_tracking_number
            	  // Get from the *first* response_table
            	  for (int iCounter = 1; iCounter <= iNumJobs; iCounter++) {
            		  try {

            			  int iErrorFlag = LIB.safeGetInt(tMaster, "error_flag", iCounter);  
            			  int iDealNum = LIB.safeGetInt(tMaster, "deal_num", iCounter);  
                		  
                		  if (iErrorFlag != CONST_VALUES.VALUE_OF_TRUE && iDealNum < 1) {

                    		  Table tResponse = LIB.safeGetTable(tMaster, "first_response_table", iCounter);
                    		  Table tMethods = LIB.safeGetTable(tResponse, "me:methods", CONST_VALUES.ROW_TO_GET_OR_SET);
            				  Table tMethod = LIB.safeGetTable(tMethods, "meth:method", CONST_VALUES.ROW_TO_GET_OR_SET);
            				  Table tResults = LIB.safeGetTable(tMethod, "rslt:results", CONST_VALUES.ROW_TO_GET_OR_SET);
            				  Table tDataTypes = LIB.safeGetTable(tResults, "dt:dataTypes", CONST_VALUES.ROW_TO_GET_OR_SET);
            				  Table tTradeBuilder = LIB.safeGetTable(tDataTypes, "tb:tradeBuilder", CONST_VALUES.ROW_TO_GET_OR_SET);
            				  String sTemplateReference = LIB.safeGetString(tTradeBuilder, "tb:templateReference", CONST_VALUES.ROW_TO_GET_OR_SET);
            				  LIB.safeSetString(tMaster, "template", iCounter, sTemplateReference);
            				  
            				  Table tTradeField = LIB.safeGetTable(tTradeBuilder, "tb:tradeField", CONST_VALUES.ROW_TO_GET_OR_SET);
            				  int iNumRowsTradeField = LIB.safeGetNumRows(tTradeField);
            				  for (int iSubCounter = 1; iSubCounter <= iNumRowsTradeField; iSubCounter++) {
            					  String sName = LIB.safeGetString(tTradeField, "tb:name", iSubCounter);
            					  String sValue = LIB.safeGetString(tTradeField, "tb:value", iSubCounter);
            					  
            					  if (Str.iEqual(sName, "Deal Tracking Num") == 1) {
            						  int iValue = Str.strToInt(sValue);
            						  LIB.safeSetInt(tMaster, "deal_num", iCounter, iValue);
            					  }  
            					  if (Str.iEqual(sName, "Ins Num") == 1) {
            						  int iValue = Str.strToInt(sValue);
            						  LIB.safeSetInt(tMaster, "ins_num", iCounter, iValue);
            					  }  
            					  if (Str.iEqual(sName, "Tran Num") == 1) {
            						  int iValue = Str.strToInt(sValue);
            						  LIB.safeSetInt(tMaster, "tran_num", iCounter, iValue);
            					  }
            				  } 
            				  
                		  }
            		  } catch (Exception e) {
            			  
            		  }
            	  }
            	  
            	  // If we still can't get the deal number, show it as an error
            	  for (int iCounter = 1; iCounter <= iNumJobs; iCounter++) {
            		  try {

            			  int iErrorFlag = LIB.safeGetInt(tMaster, "error_flag", iCounter);  
            			  int iDealNum = LIB.safeGetInt(tMaster, "deal_num", iCounter);  
                		  
                		  if (iErrorFlag != CONST_VALUES.VALUE_OF_TRUE && iDealNum < 1) {

                			  String sErrorMessage = "";
                			  int iNumErrors = 0;
                			  Table tActivityLevel = LIB.safeGetTable(tMaster, "data", iCounter);
                			  int iSubNumRows = LIB.safeGetNumRows(tActivityLevel);
                			  for (int iSubCounter = 1; iSubCounter <= iSubNumRows; iSubCounter++) {
                				  int iActivityCode = LIB.safeGetInt(tActivityLevel, "activity_code", iSubCounter);
                				  if (iActivityCode == 1017) {
                					  iNumErrors = iNumErrors + 1;
                					  // take the last one
                					  sErrorMessage = LIB.safeGetString(tActivityLevel, "description", iSubCounter);
                					  sErrorMessage = Str.pureString(sErrorMessage);
                				  }
                			  }
                			  
                			  LIB.safeSetInt(tMaster, "number_of_errors", iCounter, iNumErrors);  
                			  LIB.safeSetInt(tMaster, "error_flag", iCounter, CONST_VALUES.VALUE_OF_TRUE);  
                			  String sMessage = "Not able to find the Deal Num. This is the error from OpenLink: " + "'" + sErrorMessage + "'";
                			  LIB.safeSetString(tMaster, "last_error_message", iCounter, sMessage);
                			  
                		  }
                		  
            		  } catch (Exception e) {
            			  
            		  }
            	  }

             	  int iTimeOffset = 5;
            	  int iToday = OCalendar.today();
            	  if (iToday > OCalendar.parseString("08-Mar-2020")) {
            		  iTimeOffset = 4;
            	  }
            	  if (iToday > OCalendar.parseString("01-Nov-2020")) {
            		  iTimeOffset = 5;
            	  }
            	  if (iToday > OCalendar.parseString("14-Mar-2021")) {
            		  iTimeOffset = 4;
            	  }
            	  if (iToday > OCalendar.parseString("07-Nov-2021")) {
            		  iTimeOffset = 5;
            	  }
            	  if (iToday > OCalendar.parseString("13-Mar-2022")) {
            		  iTimeOffset = 4;
            	  }
            	  if (iToday > OCalendar.parseString("06-Nov-2022")) {
            		  iTimeOffset = 5;
            	  }
            	  if (iToday > OCalendar.parseString("12-Mar-2023")) {
            		  iTimeOffset = 4;
            	  }
            	  if (iToday > OCalendar.parseString("05-Nov-2023")) {
            		  iTimeOffset = 5;
            	  }
            	  if (iToday > OCalendar.parseString("10-Mar-2024")) {
            		  iTimeOffset = 4;
            	  }
            	  if (iToday > OCalendar.parseString("03-Nov-2024")) {
            		  iTimeOffset = 5;
            	  }
            	  if (iToday > OCalendar.parseString("09-Mar-2025")) {
            		  iTimeOffset = 4;
            	  }
            	  if (iToday > OCalendar.parseString("02-Nov-2025")) {
            		  iTimeOffset = 5;
            	  }
            	  if (iToday > OCalendar.parseString("08-Mar-2026")) {
            		  iTimeOffset = 4;
            	  }
            	  if (iToday > OCalendar.parseString("01-Nov-2026")) {
            		  iTimeOffset = 5;
            	  }
             	  
            	  // calc trade booking time
            	  for (int iCounter = 1; iCounter <= iNumJobs; iCounter++) {
            		  try {

            			  int iErrorFlag = LIB.safeGetInt(tMaster, "error_flag", iCounter);  
            			  if (iErrorFlag == CONST_VALUES.VALUE_OF_TRUE) {
            				  LIB.safeSetString(tMaster, "trade_execution_time_GMT", iCounter, "");
            				  LIB.safeSetInt(tMaster, "seconds_part1_ICE_to_XML_Engine", iCounter, -1);
            				  LIB.safeSetInt(tMaster, "seconds_part2_Trade_Booked", iCounter, -1);
            				  LIB.safeSetInt(tMaster, "seconds_total_time", iCounter, -1);
            			  }

            			  if (iErrorFlag == CONST_VALUES.VALUE_OF_FALSE) {
            				  
      	            		 int iStartDate = tMaster.getDate("start_time", iCounter);
     	            		 int iStartTime = tMaster.getTime("start_time", iCounter);
     	            		 
      	            		 int iEndDate = tMaster.getDate("end_time", iCounter);
     	            		 int iEndTime = tMaster.getTime("end_time", iCounter);
     	            		 
     	            		 if (iStartDate != iEndDate) {
     	            			 LIB.log("Start Date did not equal End Date for some unknown reason", className);
     	            		 }
  
     	            		 Table tTemp = Table.tableNew();
     	            		 tTemp.addCol("temp", COL_TYPE_ENUM.COL_DATE_TIME);
     	            		 tTemp.addCol("temp2", COL_TYPE_ENUM.COL_INT);
     	            		 tTemp.addRow();
     	            		 tTemp.setDateTimeByParts("temp", 1, iStartDate, iStartTime);
     	            		 
     	            		 Table tTemp2 = Table.tableNew();
     	            		 tTemp2.addCol("temp", COL_TYPE_ENUM.COL_STRING);
     	            		 
     	            		 LIB.select(tTemp2, tTemp, "temp", "temp2 GE -1", className);
     	            		 String sFormattedStartDateTime = tTemp2.getString("temp", 1);
     	            		 
      	            		tTemp = LIB.destroyTable(tTemp);
     	            		tTemp2 = LIB.destroyTable(tTemp2);
     	            		 
    	            		 int iModValue2 = iStartTime % 3600;
    	            		 int iCalcedHour = ((iStartTime - iModValue2) / 3600);
    	            		 int iCalcedSecond = iModValue2 % 60;
    	            		 int iCalcedMinute = ((iModValue2 - iCalcedSecond) / 60);
    	            		     	            		 
     	            		 // just assume that the start and end dates will be the same
     	            		 int iTradeBookTime = iEndTime - iStartTime;
     	            		 LIB.safeSetInt(tMaster, "seconds_part2_Trade_Booked", iCounter, iTradeBookTime);
     	            		
     	            		 try {
	           					  String sGMTTime = LIB.safeGetString(tMaster, "trade_execution_time_GMT", iCounter);
	           					
	           					  int iFirstPart = 20;
	           					  if (Str.len(sGMTTime) >= iFirstPart) {
 	           						  
	           						  sGMTTime = Str.substr(sGMTTime, 0, iFirstPart);
	           						  String sHour = Str.substr(sGMTTime, 12, 2);
	           						  String sMinute = Str.substr(sGMTTime, 15, 2);
	           						  String sSecond = Str.substr(sGMTTime, 18, 2);
	           						  int iHour = Str.strToInt(sHour);
	           						  int iMinute = Str.strToInt(sMinute);
	           						  int iSecond = Str.strToInt(sSecond);
	           					
	           						  boolean bPrint = false;
	           						  if (bPrint == true) {
		           						  OConsole.oprint("\n TEST 1: Date for '" + sGMTTime + "'" + " is " + sGMTTime + ", Hour: " 
		    	           						  + iHour + ", sMinute: " + iMinute + ", sSecond: " + iSecond + ", iStartTime: " + iStartTime + ", sFormattedStartDateTime: " + sFormattedStartDateTime  + ", iModValue2: " + iModValue2 + 
		    	           						  ", iCalcedHour: " + iCalcedHour + ", iCalcedMinute: " + iCalcedMinute + ", iCalcedSecond: " + iCalcedSecond);
	           						  }
	           						  
	           						  // TODO, get this working
	           						  
	           						  //  fix this up for daylight savings, to be accurate, and not just by 5
	           						  iHour = iHour - iTimeOffset;
	           						  int iSeconds1 = (iHour * 3600) + (iMinute * 60) + iSecond;
	           						  int iSeconds2 = (iCalcedHour * 3600) + (iCalcedMinute * 60) + iCalcedSecond;
	           						  int iDiff = iSeconds2 - iSeconds1;
		          	            		
	           						  LIB.safeSetInt(tMaster, "seconds_part1_ICE_to_XML_Engine", iCounter, iDiff);
		          	            		
	           						  int iTotalTime = 	iTradeBookTime + iDiff;

	           						  LIB.safeSetInt(tMaster, "seconds_total_time", iCounter, iTotalTime);
	           					  }
	           				  
	           				  } catch (Exception e) {
	           					  // do not log this
	           				  }
 
            			  }
            		  } catch (Exception e) {
            			  
            		  }
            	  }
            	  
              } catch(Exception e ){               
                   LIB.log("addColsForTradeGatewayJobList", e, className);
              } 
        } 

        private static void addColsForeConfirmJobList(Table tMaster, String className) throws OException{
              try{
            	  
            	  tMaster.insertCol("error_flag", "num_rows", COL_TYPE_ENUM.COL_INT);
            	  LIB.safeSetColFormatAsRef(tMaster, "error_flag", SHM_USR_TABLES_ENUM.NO_YES_TABLE);
            	  
            	  tMaster.insertCol("error_message", "num_rows", COL_TYPE_ENUM.COL_STRING);

            	  tMaster.insertCol("TradeDetail", "error_flag", COL_TYPE_ENUM.COL_TABLE);
            	  tMaster.insertCol("SenderTradeRefId", "error_flag", COL_TYPE_ENUM.COL_STRING);
            	  tMaster.insertCol("ClientVersionId", "error_flag", COL_TYPE_ENUM.COL_STRING);
            	  
            	  LIB.safeAddCol(tMaster, "tran_num", COL_TYPE_ENUM.COL_INT);
            	  LIB.safeColHide(tMaster, "tran_num");
            	  LIB.safeSetColValInt(tMaster, "tran_num", -1);
            	  
            	  int iNumJobs = LIB.safeGetNumRows(tMaster);
            	  for (int iCounter = 1; iCounter <= iNumJobs; iCounter++) {
            		  Table tSub = LIB.safeGetTable(tMaster, "response_table", iCounter);
            		  if (LIB.safeGetNumRows(tSub) >= 1) {
            			  if (tSub.getColNum("faultcode") >= 1) {
            				  Table tDetail = LIB.safeGetTable(tSub, "detail", CONST_VALUES.ROW_TO_GET_OR_SET);
            				  Table tResponse = LIB.safeGetTable(tDetail, "ocrsp:response", CONST_VALUES.ROW_TO_GET_OR_SET);
            				  Table tMethods = LIB.safeGetTable(tResponse, "me:methods", CONST_VALUES.ROW_TO_GET_OR_SET);
            				  Table tMethod = LIB.safeGetTable(tMethods, "meth:method", CONST_VALUES.ROW_TO_GET_OR_SET);
            				  Table tMethodStatus = LIB.safeGetTable(tMethod, "methstat:status", CONST_VALUES.ROW_TO_GET_OR_SET);
            				  String sError = LIB.safeGetString(tMethodStatus, "methstat:errorMsg", CONST_VALUES.ROW_TO_GET_OR_SET);
            				  if (sError != null && Str.len(sError) >= 1) {
            					  LIB.safeSetInt(tMaster, "error_flag", iCounter, CONST_VALUES.VALUE_OF_TRUE);
            					  LIB.safeSetString(tMaster, "error_message", iCounter, sError);
            				  }
            			  }
            			  
            		  }
            	  }
            	  
            	  for (int iCounter = 1; iCounter <= iNumJobs; iCounter++) {
            		  
            		  try {
            			  int iErrorFlag = LIB.safeGetInt(tMaster, "error_flag", iCounter);
            			  if (iErrorFlag != CONST_VALUES.VALUE_OF_TRUE) {
            				  
            				  Table tResponse = LIB.safeGetTable(tMaster, "response_table", iCounter);
            				  if (LIB.safeGetNumRows(tResponse) >= 1) {
                				  Table tMethods = LIB.safeGetTable(tResponse, "me:methods", CONST_VALUES.ROW_TO_GET_OR_SET);
                				  Table tMethod = LIB.safeGetTable(tMethods, "meth:method", CONST_VALUES.ROW_TO_GET_OR_SET);
                				  Table tResults = LIB.safeGetTable(tMethod, "rslt:results", CONST_VALUES.ROW_TO_GET_OR_SET);
                				  Table tDataTypes = LIB.safeGetTable(tResults, "dt:dataTypes", CONST_VALUES.ROW_TO_GET_OR_SET);
                				  
                				  String sSenderTradeRef = LIB.safeGetString(tDataTypes, "SenderTradeRefId", CONST_VALUES.ROW_TO_GET_OR_SET);
                				  LIB.safeSetString(tMaster, "SenderTradeRefId", iCounter, sSenderTradeRef);
                				  
                				  String sClientVersionId = LIB.safeGetString(tDataTypes, "ClientVersionId", CONST_VALUES.ROW_TO_GET_OR_SET);
                				  LIB.safeSetString(tMaster, "ClientVersionId", iCounter, sClientVersionId);
                				  
                				  if (sClientVersionId != null && Str.len(sClientVersionId) >= 1) {
                					  int iLocationOfPeriod = Str.findSubString(sClientVersionId, ".");
                					  if (iLocationOfPeriod >= 0) {
                						  int iNumberOfCharsToGet = iLocationOfPeriod;
                						  String sTranNum = Str.substr(sClientVersionId, 0, iNumberOfCharsToGet);
                						  if (Str.isInt(sTranNum) == 1) {
                							  int iTranNum = Str.strToInt(sTranNum);
                            				  LIB.safeSetInt(tMaster, "tran_num", iCounter, iTranNum);
                						  }
                					  }
                				  }

                				  Table tTradeDetail = LIB.safeGetTable(tDataTypes, "TradeDetail", CONST_VALUES.ROW_TO_GET_OR_SET);
                				  Table tCopy = tTradeDetail.copyTable();
                				  LIB.safeSetTable(tMaster, "TradeDetail", iCounter, tCopy);
                			  }                			  
                		  } 
            		  } catch (Exception e) {
            			 // don't log this
            		  }
            		  
        			
            	  }
            	  
              } catch(Exception e ){               
                   LIB.log("addColsForeConfirmJobList", e, className);
              } 
        } 

        private static void massageDataForCmeDbRetrieve(Table tMaster, String className) throws OException{
        	try{
            	
        		LIB.safeDelCol(tMaster, "client_tag1_table");
        		LIB.safeDelCol(tMaster, "num_client_tag1");
        		LIB.safeDelCol(tMaster, "client_tag2_table");
        		LIB.safeDelCol(tMaster, "num_client_tag2");

        		LIB.safeAddCol(tMaster, "data_retrieved", COL_TYPE_ENUM.COL_TABLE); 
        		LIB.safeAddCol(tMaster, "num_cols_retrieved", COL_TYPE_ENUM.COL_INT);
        		LIB.safeAddCol(tMaster, "num_rows_retrieved", COL_TYPE_ENUM.COL_INT);
        		LIB.safeAddCol(tMaster, "sql", COL_TYPE_ENUM.COL_TABLE);

        		LIB.safeAddCol(tMaster, "sql_from", COL_TYPE_ENUM.COL_STRING);
        		LIB.safeAddCol(tMaster, "sql_what", COL_TYPE_ENUM.COL_STRING);

        		{
            		int iNumJobs = LIB.safeGetNumRows(tMaster);
            		for (int iCounter = 1; iCounter <= iNumJobs; iCounter++) {
            			Table tSub1 = LIB.safeGetTable(tMaster, "request_table", iCounter);
            			if (LIB.safeGetNumRows(tSub1) >= 1) {
            				if (tSub1.getColNum("me:methods") >= 1) {
            					Table tMethods = LIB.safeGetTable(tSub1, "me:methods", CONST_VALUES.ROW_TO_GET_OR_SET);
            					Table tMethod = LIB.safeGetTable(tMethods, "meth:method", CONST_VALUES.ROW_TO_GET_OR_SET);
            					
            					Table tAgs = LIB.safeGetTable(tMethod, "arg:arguments", CONST_VALUES.ROW_TO_GET_OR_SET);

            					Table tDataTypes = LIB.safeGetTable(tAgs, "dt:dataTypes", CONST_VALUES.ROW_TO_GET_OR_SET);
            					
            					Table tQuery = LIB.safeGetTable(tDataTypes, "aq:adhocQuery", CONST_VALUES.ROW_TO_GET_OR_SET);
            					
            					Table tCopy = tQuery.copyTable();
            					String sTitle = "SQL";
            					tCopy.setTableTitle(sTitle);
            					tCopy.setTableName(sTitle);
            					
            					String sWhat = LIB.safeGetString(tCopy, "aq:sqlWhat", CONST_VALUES.ROW_TO_GET_OR_SET);
            					String sFrom = LIB.safeGetString(tCopy, "aq:sqlFrom", CONST_VALUES.ROW_TO_GET_OR_SET);
            					// Where would be in "aq:sqlWhere", but don't get it as it is too big
            					
            					LIB.safeSetString(tMaster, "sql_what", iCounter, sWhat);
            					LIB.safeSetString(tMaster, "sql_from", iCounter, sFrom);
            					
            					LIB.safeSetTable(tMaster, "sql", iCounter, tCopy);
            				}
            			}
            		}
            		
            		LIB.safeDelCol(tMaster, "request_table");
        		}

        		{
            		int iNumJobs = LIB.safeGetNumRows(tMaster);
            		for (int iCounter = 1; iCounter <= iNumJobs; iCounter++) {
            			Table tSub1 = LIB.safeGetTable(tMaster, "response_table", iCounter);
            			if (LIB.safeGetNumRows(tSub1) >= 1) {
            				if (tSub1.getColNum("me:methods") >= 1) {
            					Table tMethods = LIB.safeGetTable(tSub1, "me:methods", CONST_VALUES.ROW_TO_GET_OR_SET);
            					Table tMethod = LIB.safeGetTable(tMethods, "meth:method", CONST_VALUES.ROW_TO_GET_OR_SET);
            					
            					Table tResults = LIB.safeGetTable(tMethod, "rslt:results", CONST_VALUES.ROW_TO_GET_OR_SET);
            					
            					Table tDataTypes = LIB.safeGetTable(tResults, "dt:dataTypes", CONST_VALUES.ROW_TO_GET_OR_SET);

            					Table tReports = LIB.safeGetTable(tDataTypes, "rpt:reports", CONST_VALUES.ROW_TO_GET_OR_SET);

            					Table tReport = LIB.safeGetTable(tReports, "rpt:report", CONST_VALUES.ROW_TO_GET_OR_SET);

            					Table tTable = LIB.safeGetTable(tReport, "rpt:table", CONST_VALUES.ROW_TO_GET_OR_SET);

            					Table tBody = LIB.safeGetTable(tTable, "html:BODY", CONST_VALUES.ROW_TO_GET_OR_SET);
            					
            					Table tSubTable = LIB.safeGetTable(tBody, "html:TABLE", CONST_VALUES.ROW_TO_GET_OR_SET);
            					
            					if (LIB.safeGetNumRows(tSubTable) == 2) {
            						
            						Table tTR = LIB.safeGetTable(tSubTable, "html:TR", 2);
            					 
                					int iDataRows = LIB.safeGetNumRows(tTR);
                					int iNumCols = 0;
                					for (int iDataCounter = 1; iDataCounter <= iDataRows; iDataCounter++) {
                						String sCol = tTR.getString(1, iDataCounter);
                						if (sCol == null || Str.len(sCol) < 1) {
                							iNumCols = iDataCounter - 1;
                							break;
                						}
                					}
                					int iNumRowsRetrieved = 0;
                					LIB.safeSetInt(tMaster, "num_cols_retrieved", iCounter, iNumCols);
                					if (iNumCols >= 1) {
                    					iNumRowsRetrieved = iDataRows - iNumCols;
                    					iNumRowsRetrieved = iNumRowsRetrieved / iNumCols;
                    					LIB.safeSetInt(tMaster, "num_rows_retrieved", iCounter, iNumRowsRetrieved);
                					}
                					
                					if (iNumCols >= 1) {
                    					Table tData = Table.tableNew();
                    					for (int iDataCounter = 1; iDataCounter <= iNumCols; iDataCounter++) {
                    						String sColName = tTR.getString(1, iDataCounter);
                    						tData.addCol(sColName, COL_TYPE_ENUM.COL_STRING);
                    					}
                    					
                    					if (iNumRowsRetrieved >= 1) {
                        					for (int iRowCounter = 1; iRowCounter <= iNumRowsRetrieved; iRowCounter++) {
                        						int iRowToSet = tData.addRow();
                        						for (int iColCounter = 1; iColCounter <= iNumCols; iColCounter++) {
                        							int iRowToGet = (iRowCounter * iNumCols) + iColCounter;
                        							
                        							String sColData = tTR.getString(2, iRowToGet);
                            						tData.setString(iColCounter, iRowToSet, sColData);
                            					}
                        					}
                    						
                    					}

                    					String sTitle = "Data Retrieved";
                    					tData.setTableTitle(sTitle);
                    					tData.setTableName(sTitle);
                    					
                    					LIB.safeSetTable(tMaster, "data_retrieved", iCounter, tData);
                					}
                					
                					
            					}
            					
            				}
            			}
            		}

            		LIB.safeDelCol(tMaster, "response_table");
        		}

 

            } catch(Exception e ){               
                 LIB.log("massageDataForCmeDbRetrieve(Table, String)", e, className);
            } 
      } 

        
        

        private static void addTheRawXmlDetailsToTheOneRowPerActivityDataLogTableGetItFromTheDatabase(Table tOcActivityLogDataRawFormOneRowPerActivityID, String className) throws OException{
        	try{
            	 
        		 int iNumRows = LIB.safeGetNumRows(tOcActivityLogDataRawFormOneRowPerActivityID);

        		 for (int iCounter = 1; iCounter <= iNumRows; iCounter++) {
        			 
        			 if (iCounter % 1000 == 0) {
        				 LIB.log("addTheRawXmlDetailsToTheOneRowPerActivityDataLogTableGetItFromTheDatabase: Loop " + iCounter + " of " + iNumRows, className);
        			 }
        			 
        			 int iContainsData = LIB.safeGetInt(tOcActivityLogDataRawFormOneRowPerActivityID, "contains_data", iCounter);
        			 if (iContainsData == 1) {
            			 try {
                             XString x_string = Str.xstringNew();
                			 String sJobID = LIB.safeGetString(tOcActivityLogDataRawFormOneRowPerActivityID, "job_id", iCounter);
                			 int iActivityID = LIB.safeGetInt(tOcActivityLogDataRawFormOneRowPerActivityID, "activity_id", iCounter);
                			 
                			 String xml_string = ConnexUtility.getJobXml(sJobID, OC_XML_TYPES_ENUM.REQUEST_XML.toInt(), x_string, iActivityID);
                			 if (xml_string != null && Str.len(xml_string) >= 1) {
                    			 tOcActivityLogDataRawFormOneRowPerActivityID.setClob(COLS.DATA.job_data_request, iCounter, xml_string);
                    			 LIB.safeSetInt(tOcActivityLogDataRawFormOneRowPerActivityID, COLS.DATA.has_job_data_request, iCounter, CONST_VALUES.VALUE_OF_TRUE);
                    			 
                    			 Table tXml =  Table.xmlStringToTable(xml_string, x_string);

                    			// Table tXml = ConnexUtility.getJobXmlAsTable(sJobID, OC_XML_TYPES_ENUM.REQUEST_XML.toInt(), x_string, iActivityID);
                    			 LIB.safeSetTable(tOcActivityLogDataRawFormOneRowPerActivityID, COLS.DATA.job_data_request_table, iCounter, tXml);
                			 }
                			 
                			 Str.xstringDestroy(x_string);
            			 } catch (Exception e) {
            				 // don't log this
            			 } 
        			 } // end if (iContainsData == 1)
        			 
        			 if (iContainsData == 8) {
        				 try {
                             XString x_string = Str.xstringNew();
                			 String sJobID = LIB.safeGetString(tOcActivityLogDataRawFormOneRowPerActivityID, "job_id", iCounter);
                			 int iActivityID = LIB.safeGetInt(tOcActivityLogDataRawFormOneRowPerActivityID, "activity_id", iCounter);
                			 String xml_string = ConnexUtility.getJobXml(sJobID, OC_XML_TYPES_ENUM.RESPONSE_XML.toInt(), x_string, iActivityID);
                			 if (xml_string != null && Str.len(xml_string) >= 1) {
                    			 tOcActivityLogDataRawFormOneRowPerActivityID.setClob(COLS.DATA.job_data_response, iCounter, xml_string);
                    			 LIB.safeSetInt(tOcActivityLogDataRawFormOneRowPerActivityID, COLS.DATA.has_job_data_response, iCounter, CONST_VALUES.VALUE_OF_TRUE);
                    			 
                    			 Table tXml =  Table.xmlStringToTable(xml_string, x_string);
                    			 //Table tXml = ConnexUtility.getJobXmlAsTable(sJobID, OC_XML_TYPES_ENUM.RESPONSE_XML.toInt(), x_string, iActivityID);
                    			 LIB.safeSetTable(tOcActivityLogDataRawFormOneRowPerActivityID, COLS.DATA.job_data_response_table, iCounter, tXml);
                			 }
                			 Str.xstringDestroy(x_string);
            			 } catch (Exception e) {
            				 // don't log this
            			 }
        			 } // END if (iContainsData == 8) {
        			 
        			 
        			 if (iContainsData == 17) {
            			 try {
                             XString x_string = Str.xstringNew();
                			 String sJobID = LIB.safeGetString(tOcActivityLogDataRawFormOneRowPerActivityID, "job_id", iCounter);
                			 int iActivityID = LIB.safeGetInt(tOcActivityLogDataRawFormOneRowPerActivityID, "activity_id", iCounter);
                			 
                			 String xml_string = ConnexUtility.getJobXml(sJobID, OC_XML_TYPES_ENUM.REQUEST_XML.toInt(), x_string, iActivityID);
                			 if (xml_string != null && Str.len(xml_string) >= 1) {
                    			 tOcActivityLogDataRawFormOneRowPerActivityID.setClob(COLS.DATA.job_data_request, iCounter, xml_string);
                    			 LIB.safeSetInt(tOcActivityLogDataRawFormOneRowPerActivityID, COLS.DATA.has_job_data_request, iCounter, CONST_VALUES.VALUE_OF_TRUE);
                    			 
                    			 Table tXml =  Table.xmlStringToTable(xml_string, x_string);

                    			// Table tXml = ConnexUtility.getJobXmlAsTable(sJobID, OC_XML_TYPES_ENUM.REQUEST_XML.toInt(), x_string, iActivityID);
                    			 LIB.safeSetTable(tOcActivityLogDataRawFormOneRowPerActivityID, COLS.DATA.job_data_request_table, iCounter, tXml);
                			 }
                			 
                			 Str.xstringDestroy(x_string);
            			 } catch (Exception e) {
            				 // don't log this
            			 } 
        			 } // end if (iContainsData == 17)

        			 
        			 if (iContainsData == 4) {
            			 try {
                             XString x_string = Str.xstringNew();
                			 String sJobID = LIB.safeGetString(tOcActivityLogDataRawFormOneRowPerActivityID, "job_id", iCounter);
                			 int iActivityID = LIB.safeGetInt(tOcActivityLogDataRawFormOneRowPerActivityID, "activity_id", iCounter);
                			 
                			 String xml_string = ConnexUtility.getJobXml(sJobID, OC_XML_TYPES_ENUM.REQUEST_XML.toInt(), x_string, iActivityID);
                			 if (xml_string != null && Str.len(xml_string) >= 1) {
                    			 tOcActivityLogDataRawFormOneRowPerActivityID.setClob(COLS.DATA.job_data_request, iCounter, xml_string);
                    			 LIB.safeSetInt(tOcActivityLogDataRawFormOneRowPerActivityID, COLS.DATA.has_job_data_request, iCounter, CONST_VALUES.VALUE_OF_TRUE);
                    			 
                    			 Table tXml =  Table.xmlStringToTable(xml_string, x_string);

                    			// Table tXml = ConnexUtility.getJobXmlAsTable(sJobID, OC_XML_TYPES_ENUM.REQUEST_XML.toInt(), x_string, iActivityID);
                    			 LIB.safeSetTable(tOcActivityLogDataRawFormOneRowPerActivityID, COLS.DATA.job_data_request_table, iCounter, tXml);
                			 }
                			 
                			 Str.xstringDestroy(x_string);
            			 } catch (Exception e) {
            				 // don't log this
            			 } 
        			 } // end if (iContainsData == 4)
        			 
        		 }
        		  
            } catch(Exception e ){               
                 LIB.log("addTheRawXmlDetailsToTheOneRowPerActivityDataLogTableGetItFromTheDatabase", e, className);
            } 
      } 

        
    } //public static class FUNC

    public static class COLS {     
    	 
    	// columns are cosmetic.. can change
    	public static class DATA { 
			public final static String job_data_request = "job_data_request"; 
			public final static String job_data_response = "job_data_response"; 
			public final static String job_data_request_table = "job_data_request_table"; 
			public final static String job_data_response_table = "job_data_response_table"; 
			public final static String has_job_data_request = "has_job_data_request"; 
			public final static String has_job_data_response = "has_job_data_response";  
    	}
    	 
    	// columns are cosmetic.. can change
    	public static class MASTER_REPORT { 
			public final static String run_date = "run_date"; 
			public final static String report_number = "report_number"; 
			public final static String description = "description"; 
			public final static String num_rows = "num_rows"; 
			public final static String data = "data";   
			public final static String message = "message";   
    	}

		public static class ARGT {
			public final static String do_not_show_raw_data_flag = "do_not_show_raw_data_flag";
			public final static String only_show_trade_logs_flag = "only_show_trade_logs_flag";

			public static class EXTRA {
				public final static String user_pressed_cancel = "user_pressed_cancel";
			}
		}
		 
    } // end class COLS

	//  Don't change these.  These need to match what OpenLink reports
	public static class  CLIENT_TAG2 {
		public final static String pushICECM = "pushICECM"; 
		public final static String ICEAdapterTradeProcess = "ICEAdapterTradeProcess"; 
		public final static String updateIceStlDocStatus = "updateIceStlDocStatus"; 
		public final static String dbRetrieveWithSQL = "dbRetrieveWithSQL"; 
		public final static String sendAlert = "sendAlert"; 
	}
	
	//  Don't change these.  These need to match what OpenLink reports for alerts
	public static class  ALERT_TYPE {
		
		// example
		// ICEFIX-PE-004	IceFixGatway - IceFixMappingComponent: process message error: COM.olf.adapter.framework.OAFException: Operation Timed Out Severity: ERROR	IceFixGatway - IceFixMappingComponent: process mes
		public final static String ICEFIX_PE_004 = "ICEFIX-PE-004"; 
		
		// example
		// ICEFIX-SD-006	IceFixGateway: (IceFixComponentHelper) - unable to find associated security for tradecapturereport response: 8=FIX.4.49=122335=AE34=177349=ICE52=20191125-19:03:45.98356=28557=117=6832016222=831=0.15232=20039=248=HNG LMX0021-HNG LMK002255=621170960=20191125-18:55:53.59375=20191125150=F207=IFED461=FXXXXX487=0570=N571=236536762=99828=K856=0916=20211101917=202205319018=09022=19028=09064=09413=3552=154=237=6832016111=68320161453=11448=whyde8447=D452=11448=Bank of Montreal447=D452=13448=285447=D452=56448=Marex Spectron International Limited-Broker447=D452=1448=10279447=D452=61448=mabruzzo447=D452=12448=22189447=D452=4448=C0901447=D452=51448=BMO Capital Markets Corp.447=D452=60448=CMC447=D452=63448=F447=D452=5477=O555=2600=1462962602=HNG LMK0022!603=8608=FXXXXX616=IFED624=19019=2009020=202205019021=202205319023=19426=W687=200539=5524=22189525=D538=4524=C0901525=D538=51524=BMO Capital Markets Corp.525=D538=60524=CMC525	IceFixGateway: (IceFixComponentHelper) - unable to
		public final static String ICEFIX_SD_006 = "ICEFIX-SD-006"; 
		
		// ICEFIX_GE_002 can be at least two things
		// ICEFIX-GE-002	ICEAdapterTradeProcess: Error on process_holding_ins() -  OC_CreateSharedIns: Error loading template. Invalid template reference specified: TradeBuilder Contains an Invalid Tran Num, Ins Type, Deal Num or Template Reference! for Ice Deal Id : 17027439.074950:112519.B	ICEAdapterTradeProcess: Error on process_holding_i
		// ICEFIX-GE-002	ICEAdapterTradeProcess Null Trade retrieved by Trade Builder:  Trade field Int Portfolio value "bomfid-fx" is not valid or user lacks the privilege to set! for Ice Deal Id : 17197285.081048:112519.B	ICEAdapterTradeProcess Null Trade retrieved by Tra
		public final static String ICEFIX_GE_002 = "ICEFIX-GE-002"; 
	}
  
    public static class CONST_VALUES {            
    	public static final int VALUE_OF_TRUE = 1;
    	public static final int VALUE_OF_FALSE = 0;
    	public static final int VALUE_OF_NOT_APPLICABLE = -1;

    	public static final int ROW_TO_GET_OR_SET = 1;  
	 
    	public static final double A_SMALL_NUMBER = 0.000001; 
    	
    	public static final String NO_DISPLAY = "Not Displaying These Rows to Reduce Runtime"; 
    }

    public static class LIB {            

    	public static final String VERSION_NUMBER = "V1.003 (19Jun2023)";

    	public static void select(Table tDestination, Table tSourceTable, String sWhat, String sWhere, String className) throws OException{
              LIB.select(tDestination, tSourceTable, sWhat, sWhere, false, className);
    	} 


        private static void select(Table tDestination, Table tSourceTable, String sWhat, String sWhere, boolean bLogFlag, String className) throws OException{
              try{
                   tDestination.select(tSourceTable, sWhat, sWhere);
              } catch(Exception e ){               
                   LIB.log("select", e, className);
              } 
        } 

        public static void viewTable(Table tMaster) throws OException{
              try{
            	  tMaster.scriptDataHideIconPanel();
                  tMaster.setTableViewerMode(TABLE_VIEWER_MODE.TABLE_VIEWER_LEGACY);
                  String sTableName = tMaster.getTableName();
                  String sTableTitle = sTableName + ", " + "Number of Rows: " + LIB.safeGetNumRows(tMaster);
                  LIB.viewTable(tMaster, sTableTitle);
              } catch(Exception e ){               
                   // don't log this
              } 
        } 

        public static void viewTable(Table tMaster, String sTableTitle) throws OException{
              try{
            	  tMaster.scriptDataHideIconPanel();
            	  tMaster.setTableViewerMode(TABLE_VIEWER_MODE.TABLE_VIEWER_LEGACY);
            	  tMaster.setTableTitle(sTableTitle);
            	  tMaster.viewTable();
                   
              } catch(Exception e ){               
                   // don't log this
              } 
         } 
        
        public static void log(String sMessage, String className) throws OException{
              try{
                   
                   double dMemSize = SystemUtil.memorySizeDouble();
                   double tMemSizeMegs = dMemSize / 1024 / 1024 / 1024;
                   String sMemSize = Str.doubleToStr(tMemSizeMegs, 2) + " gigs";

                   OConsole.oprint("\n" + className + ":" + LIB.VERSION_NUMBER + ":" + Util.timeGetServerTimeHMS() + ":" + sMemSize + ": " + sMessage);
              }catch(Exception e ){ 
                   // don't log this
              }
        }    
         
        public static void log(String sMessage, Exception e, String className) throws OException{
        	try{
        		LIB.log("ERROR: " + sMessage + ":" + e.getLocalizedMessage(), className);
        	}catch(Exception e1 ){
        		// don't log this
        	}
        }   

         public static Table destroyTable(Table tDestroy) throws OException{
               try{
                    if (Table.isTableValid(tDestroy) == 1) {
                 	   // clear rows seems to help free up memory quicker
                 	   tDestroy.clearRows();
                 	   tDestroy.destroy();
                    }
               } catch(Exception e ){               
                    // don't log this
               }
               return Util.NULL_TABLE;
          }

         public static Transaction destroyTran(Transaction tDestroy) throws OException{
               try{
                    if (Transaction.isNull(tDestroy) != CONST_VALUES.VALUE_OF_TRUE) {
                 	   tDestroy.destroy();
                    }
               } catch(Exception e ){               
                    // don't log this
               }
               return Util.NULL_TRAN;
          }
  
        public static int safeGetInt(Table tMaster, String sColName, int iRowNum) throws OException{
              int iReturn = 0;
              try{
                   if (Table.isTableValid(tMaster) == 1) {
                         if (tMaster.getColNum(sColName) >= 1) {
                              iReturn = tMaster.getInt(sColName, iRowNum);
                         } else {
                              LIB.log("Can't find a column named " + "'" + sColName + "'" + " for " + "safeGetInt", "");
                         }
                   }
              } catch(Exception e ){               
                   // don't log this
              }
              return iReturn;
         }
        
        public static String safeGetString(Table tMaster, String sColName, int iRowNum) throws OException{
              String sReturn = "";
              try{
                   if (Table.isTableValid(tMaster) == 1) {
                         if (tMaster.getColNum(sColName) >= 1) {
                              sReturn = tMaster.getString(sColName, iRowNum);
                         } //else {
                           //   LIB.log("Can't find a column named " + "'" + sColName + "'" + " for " + "safeGetString", "");
                       //  }
                   }
              } catch(Exception e ){               
                   // don't log this
              }
              return sReturn;
         }
        
        public static double safeGetDouble(Table tMaster, String sColName, int iRowNum) throws OException{
            double dReturn = 0;
            try{
                 if (Table.isTableValid(tMaster) == 1) {
                       if (tMaster.getColNum(sColName) >= 1) {
                            dReturn = tMaster.getDouble(sColName, iRowNum);
                       } //else {
                         //   LIB.log("Can't find a column named " + "'" + sColName + "'" + " for " + "safeGetDouble", "");
                       //}
                 }
            } catch(Exception e ){               
                 // don't log this
            }
            return dReturn;
       }
        
        public static double safeGetDouble(Table tMaster, int iCol, int iRowNum) throws OException{
            double dReturn = 0;
            try{
                 if (Table.isTableValid(tMaster) == 1) {
                       if (iCol >= 1) {
                            dReturn = tMaster.getDouble(iCol, iRowNum);
                       }  
                 }
            } catch(Exception e ){               
                 // don't log this
            }
            return dReturn;
       }
      

        public static Table safeGetTable(Table tMaster, String sColName, int iRowNum) throws OException{
              Table tReturn = Util.NULL_TABLE;
              try{
                   if (Table.isTableValid(tMaster) == 1) {
                         if (tMaster.getColNum(sColName) >= 1) {
                              tReturn = tMaster.getTable(sColName, iRowNum);
                         }// else {
                           //   LIB.log("Can't find a column named " + "'" + sColName + "'" + " for " + "safeGetTable", "");
                         //}
                   }
              } catch(Exception e ){               
                   // don't log this
              }
              return tReturn;
         }

        public static void safeSetColValInt(Table tMaster, String sColName, int iValue) throws OException{
             try{
                 if (Table.isTableValid(tMaster) == 1) {
                        if (tMaster.getColNum(sColName) >= 1) {
                             tMaster.setColValInt(sColName, iValue);
                        } //else {
                          //   LIB.log("Can't find a column named " + "'" + sColName + "'" + " for " + "safeSetColValInt", "");
                        //}
                  }
             } catch(Exception e ){               
                  // don't log this
             }
        }
        
        public static void safeSetColValDouble(Table tMaster, String sColName, double dValue) throws OException{
            try{
                if (Table.isTableValid(tMaster) == 1) {
                       if (tMaster.getColNum(sColName) >= 1) {
                            tMaster.setColValDouble(sColName, dValue);
                       } else {
                            LIB.log("Can't find a column named " + "'" + sColName + "'" + " for " + "safeSetColValDouble", "");
                       }
                 }
            } catch(Exception e ){               
                 // don't log this
            }
       }

         public static void safeSetColValString(Table tMaster, String sColName, String sValue) throws OException{
              try{
                   if (Table.isTableValid(tMaster) == 1) {
                         if (tMaster.getColNum(sColName) >= 1) {
                              tMaster.setColValString(sColName, sValue);
                         } else {
                              LIB.log("Can't find a column named " + "'" + sColName + "'" + " for " + "safeSetColValString", "");
                         }
                   }
              } catch(Exception e ){               
                   // don't log this
              }
         }

        
         public static void safeSetTable(Table tMaster, String sColName, int iRowNum, Table tSubTable) throws OException{
              try{
                   if (Table.isTableValid(tMaster) == 1) {
                         if (tMaster.getColNum(sColName) >= 1) {
                              tMaster.setTable(sColName, iRowNum, tSubTable);
                         } else {
                              LIB.log("Can't find a column named " + "'" + sColName + "'" + " for " + "safeSetTable", "");
                         }
                   }
              } catch(Exception e ){               
                   // don't log this
              }
         }
         
         public static void safeSetTableWithSetTableTitle(Table tMaster, String sColName, int iRowNum, Table tSubTable, String sTableTitle) throws OException{
             try{
                  if (Table.isTableValid(tMaster) == 1) {
                        if (tMaster.getColNum(sColName) >= 1) {
                        	
		    				 tSubTable.setTableName(sTableTitle);
		    				 tSubTable.setTableTitle(sTableTitle);

                             tMaster.setTable(sColName, iRowNum, tSubTable);
                        } else {
                             LIB.log("Can't find a column named " + "'" + sColName + "'" + " for " + "safeSetTable", "");
                        }
                  }
             } catch(Exception e ){               
                  // don't log this
             }
        }
        
        public static void safeSetInt(Table tMaster, String sColName, int iRowNum, int iValue) throws OException{
              try{
                   if (Table.isTableValid(tMaster) == 1) {
                         if (tMaster.getColNum(sColName) >= 1) {
                              tMaster.setInt(sColName, iRowNum, iValue);
                         } else {
                              LIB.log("Can't find a column named " + "'" + sColName + "'" + " for " + "safeSetInt", "");
                         }
                   }
              } catch(Exception e ){               
                   // don't log this
              }
         }
        
        public static void safeSetString(Table tMaster, String sColName, int iRowNum, String sValue) throws OException{
              try{
                   if (Table.isTableValid(tMaster) == 1) {
                         if (tMaster.getColNum(sColName) >= 1) {
                              tMaster.setString(sColName, iRowNum, sValue);
                         } else {
                              LIB.log("Can't find a column named " + "'" + sColName + "'" + " for " + "safeSetString", "");
                         }
                   }
              } catch(Exception e ){               
                   // don't log this
              }
         }
        
        public static void safeSetDouble(Table tMaster, String sColName, int iRowNum, double dValue) throws OException{
              try{
                   if (Table.isTableValid(tMaster) == 1) {
                         if (tMaster.getColNum(sColName) >= 1) {
                              tMaster.setDouble(sColName, iRowNum, dValue);
                         } else {
                              LIB.log("Can't find a column named " + "'" + sColName + "'" + " for " + "safeSetDouble", "");
                         }
                   }
              } catch(Exception e ){               
                   // don't log this
              }
         }

        public static void safeAddCol(Table tMaster, String sColName, COL_TYPE_ENUM colType) throws OException{
              try{
                   if (Table.isTableValid(tMaster) == 1) {
                         // Only add if not already there
                         if (tMaster.getColNum(sColName) < 1) {
                              tMaster.addCol(sColName, colType);
                         }
                   }
              } catch(Exception e ){               
                   // don't log this
              }
         }
        
        public static void safeDelCol(Table tMaster, String sColName) throws OException{
              try{
                   if (Table.isTableValid(tMaster) == 1) {
                         // Only delete if already there
                         if (tMaster.getColNum(sColName) >= 1) {
                              tMaster.delCol(sColName);
                         }
                   }
              } catch(Exception e ){               
                   // don't log this
              }
         }

        public static void safeColHide(Table tMaster, String sColName) throws OException{
              try{
                   if (Table.isTableValid(tMaster) == 1) {
                         // Only hide if already there
                         if (tMaster.getColNum(sColName) >= 1) {
                              tMaster.colHide(sColName);
                         }
                   }
              } catch(Exception e ){               
                   // don't log this
              }
         }

        public static int safeGetNumRows(Table tMaster) throws OException{
              int iReturn = 0;
              try{
                   if (Table.isTableValid(tMaster) == 1) {
                         iReturn = tMaster.getNumRows();
                   }
              } catch(Exception e ){               
                   // don't log this
              }
              return iReturn;
         }
        
        public static void execISql(Table tMaster, String sSQL, String className) throws OException{
              LIB.execISql(tMaster, sSQL, true, className);
         }

        public static void execISql(Table tMaster, String sSQL, boolean bLogFlag, String className) throws OException{
              try{
                   if (bLogFlag == true) {
                         LIB.log("About to run this SQL:\n\t\t" + sSQL, className);
                   }
                   DBaseTable.execISql(tMaster, sSQL); 
                   if (bLogFlag == true) {
                         LIB.log("Got back this many rows: " + LIB.safeGetNumRows(tMaster), className);
                   }
              } catch(Exception e ){               
                   LIB.log("execISql", e, className);
              } 
         } 

        public static void loadFromDbWithWhatWhere(Table tMaster, String db_tablename, Table tIDNumbers, String sWhat, String sWhere, boolean bLogFlag, String className) throws OException{
              try{
                   if (bLogFlag == true) {
                         LIB.log("About to run DBaseTable.loadFromDbWithWhatWhere with this WHAT and FROM:\n\t\t" + sWhat + " FROM " + db_tablename + ", and this many ID Values: " + LIB.safeGetNumRows(tIDNumbers), className);
                   }
                   DBaseTable.loadFromDbWithWhatWhere(tMaster, db_tablename, tIDNumbers, sWhat, sWhere);
                   if (bLogFlag == true) {
                         LIB.log("Got back this many rows: " + LIB.safeGetNumRows(tMaster), className);
                   }
              } catch(Exception e ){               
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

		public static int getTaskRunID() throws OException {

			int iReturn = -1;

			try {
				iReturn = DBUserTable.getUniqueId();
			} catch (Exception e) {
				// don't log this
			}

			// if above did not work just use the runtime in terms of seconds
			// past midnight
			if (iReturn < 1) {
				iReturn = Util.timeGetServerTime();
			}

			return iReturn;
		}

        
        
   } // END LIB


}
