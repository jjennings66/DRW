package com.drw.dashboard;


import com.olf.openjvs.Afe;
import com.olf.openjvs.Afs;
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
import com.olf.openjvs.enums.SCRIPT_PANEL_CALLBACK_TYPE_ENUM;
import com.olf.openjvs.enums.SCRIPT_PANEL_WIDGET_TYPE_ENUM;
import com.olf.openjvs.enums.SCRIPT_TYPE_ENUM;
import com.olf.openjvs.enums.SEARCH_CASE_ENUM;
import com.olf.openjvs.enums.SHM_USR_TABLES_ENUM;
import com.olf.openjvs.enums.TABLE_VIEWER_MODE; 
 

/* 
Name:  DRW_EOD_ClosingPrice_Dashboard.java

Deployment Instructions
Set this up as a 'Script Panel' in a 'Desktop'.

This is an interactive viewing, one row per index, of closing price status, i.e., have closing prices or not

Revision History

19-Apr-2023 Brian New script

*/

@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_GENERIC)
@PluginType(SCRIPT_TYPE_ENUM.MAIN_SCRIPT)
public class DRW_EOD_ClosingPrice_Dashboard implements IScript
{
    
    public void execute(IContainerContext context) throws OException
    {
    	String className = this.getClass().getSimpleName();
    	try {
    		final REFRESH_INTERVAL defaultRefreshInterval = REFRESH_INTERVAL.NO_REFRESH;
    		doEverything(context, defaultRefreshInterval, className);
    		
    	} catch (Exception e) {
    		LIB.log("execute", className);
    	}
    }     
 
    public static void doEverything(IContainerContext context, REFRESH_INTERVAL defaultRefreshInterval, String className) throws OException
    {
    	Table tExtraData = Util.NULL_TABLE;
    	
    	try {
    		LIB.log("RUNNING", className);
        	
        	//Table argt = context.getArgumentsTable();
        	// We use returnt, not 'argt'
        	Table returnt = context.getReturnTable();
        	
        	int iCallBackType = returnt.scriptDataGetCallbackType();
        	SCRIPT_PANEL_CALLBACK_TYPE_ENUM callbackType = SCRIPT_PANEL_CALLBACK_TYPE_ENUM.fromInt(iCallBackType);
  
        	if (callbackType == SCRIPT_PANEL_CALLBACK_TYPE_ENUM.SCRIPT_PANEL_INIT) {
            	int iRunDate = OCalendar.today();
        		GUI.INIT_HANDLER.doEverythingInit(iRunDate, defaultRefreshInterval, className);
        	}

        	// Do *NOT* do this if run for the first time, i.e., if 'init'
        	if (callbackType != SCRIPT_PANEL_CALLBACK_TYPE_ENUM.SCRIPT_PANEL_INIT) {
        		
            	// Get Extra data
            	tExtraData = returnt.scriptDataGetExtraDataTable();
            	Table tExtraDataSubTable = LIB.safeGetTable(tExtraData, COLS.EXTRA_DATA_TABLE.SUB_TABLE, CONST_VALUES.ROW_TO_GET_OR_SET);
            	int iRunDate = LIB.safeGetInt(tExtraDataSubTable, COLS.EXTRA_DATA_TABLE.SETTINGS.RUN_DATE, CONST_VALUES.ROW_TO_GET_OR_SET);
            	String sRunDate = LIB.safeGetString(tExtraDataSubTable, COLS.EXTRA_DATA_TABLE.SETTINGS.RUN_DATE_STRING, CONST_VALUES.ROW_TO_GET_OR_SET);

            	if (callbackType == SCRIPT_PANEL_CALLBACK_TYPE_ENUM.SCRIPT_PANEL_PUSHBUTTON) {
            		GUI.BUTTON_HANDLER.doEverythingButton(returnt, tExtraData, iRunDate, sRunDate, className);
            	}
            	if (callbackType == SCRIPT_PANEL_CALLBACK_TYPE_ENUM.SCRIPT_PANEL_TIMER) {
            		GUI.TIMER_HANDLER.doEverythingTimer(returnt, iRunDate, sRunDate, className);
            	}
            	if (callbackType == SCRIPT_PANEL_CALLBACK_TYPE_ENUM.SCRIPT_PANEL_COMBOBOX) {
            		GUI.COMBOBOX_HANDLER.doEverythingComboBox(returnt, iRunDate, sRunDate, className);
            	}
            	if (callbackType == SCRIPT_PANEL_CALLBACK_TYPE_ENUM.SCRIPT_PANEL_DBLCLICK) {
            		GUI.DOUBLE_CLICK_HANDLER.doEverythingDoubleClick(returnt, iRunDate, sRunDate, className);
            	}
            	
        	} // END        	if (callbackType != SCRIPT_PANEL_CALLBACK_TYPE_ENUM.SCRIPT_PANEL_INIT) 
    		
        	
        	
    	} catch (Exception e) {
    		LIB.log("doEverything", className);
    	} 
    	
    }
 
    public static class CREATE_TABLE { 
 
    	public static Table createIndexListTable(String className) throws OException{
          	Table tReturn = Util.NULL_TABLE;
            try{
            	tReturn = Table.tableNew();
            	LIB.safeAddCol(tReturn, COLS.INDEX_LIST.INDEX_ID, COL_TYPE_ENUM.COL_INT);
            	LIB.safeAddCol(tReturn, COLS.INDEX_LIST.INDEX_NAME, COL_TYPE_ENUM.COL_STRING);

            	LIB.safeAddCol(tReturn, COLS.INDEX_LIST.INDEX_TYPE, COL_TYPE_ENUM.COL_INT);
            	LIB.safeAddCol(tReturn, COLS.INDEX_LIST.INDEX_STATUS, COL_TYPE_ENUM.COL_INT);
            	LIB.safeAddCol(tReturn, COLS.INDEX_LIST.MARKET, COL_TYPE_ENUM.COL_INT);
            	LIB.safeAddCol(tReturn, COLS.INDEX_LIST.IDX_GROUP, COL_TYPE_ENUM.COL_INT);

            	LIB.safeSetColFormatAsRef(tReturn, COLS.INDEX_LIST.INDEX_TYPE, SHM_USR_TABLES_ENUM.IDX_INDEX_TYPE_TABLE);
            	LIB.safeSetColFormatAsRef(tReturn, COLS.INDEX_LIST.INDEX_STATUS, SHM_USR_TABLES_ENUM.INDEX_STATUS_TABLE);
            	LIB.safeSetColFormatAsRef(tReturn, COLS.INDEX_LIST.MARKET, SHM_USR_TABLES_ENUM.INDEX_MARKET_TABLE);
            	LIB.safeSetColFormatAsRef(tReturn, COLS.INDEX_LIST.IDX_GROUP, SHM_USR_TABLES_ENUM.IDX_GROUP_TABLE);
            	
            } catch(Exception e ){               
                 LIB.log("createIndexListTable", e, className);
            }
            
			return tReturn; 
          } 


    	public static Table createIndexClosingStatusTable(String className) throws OException{
          	Table tReturn = Util.NULL_TABLE;
            try{
            	tReturn = Table.tableNew();
            	LIB.safeAddCol(tReturn, COLS.INDEX_CLOSING_STATUS.INDEX_ID, COL_TYPE_ENUM.COL_INT); 
            	LIB.safeAddCol(tReturn, COLS.INDEX_CLOSING_STATUS.DATASET_TIME, COL_TYPE_ENUM.COL_INT);
            	LIB.safeAddCol(tReturn, COLS.INDEX_CLOSING_STATUS.USER_ID, COL_TYPE_ENUM.COL_INT);
            	LIB.safeAddCol(tReturn, COLS.INDEX_CLOSING_STATUS.ROW_CREATION, COL_TYPE_ENUM.COL_DATE_TIME); 
            	
	           	LIB.safeSetColFormatAsDate(tReturn, COLS.INDEX_CLOSING_STATUS.DATASET_TIME);
	           	LIB.safeSetColFormatAsRef(tReturn, COLS.INDEX_CLOSING_STATUS.INDEX_ID, SHM_USR_TABLES_ENUM.INDEX_TABLE);
	           	LIB.safeSetColFormatAsRef(tReturn, COLS.INDEX_CLOSING_STATUS.USER_ID, SHM_USR_TABLES_ENUM.PERSONNEL_TABLE);
            	 
            } catch(Exception e ){               
                 LIB.log("createIndexClosingStatusTable", e, className);
            }
            
			return tReturn; 
          } 


    	public static Table createIndexUniversalStatusTable(String className) throws OException{
          	Table tReturn = Util.NULL_TABLE;
            try{
            	tReturn = Table.tableNew();
            	LIB.safeAddCol(tReturn, COLS.INDEX_UNIVERSAL_STATUS.INDEX_ID, COL_TYPE_ENUM.COL_INT);
            	LIB.safeAddCol(tReturn, COLS.INDEX_UNIVERSAL_STATUS.USER_ID, COL_TYPE_ENUM.COL_INT);
            	LIB.safeAddCol(tReturn, COLS.INDEX_UNIVERSAL_STATUS.ROW_CREATION, COL_TYPE_ENUM.COL_DATE_TIME); 
            	 
	           	LIB.safeSetColFormatAsRef(tReturn, COLS.INDEX_UNIVERSAL_STATUS.INDEX_ID, SHM_USR_TABLES_ENUM.INDEX_TABLE);
	           	LIB.safeSetColFormatAsRef(tReturn, COLS.INDEX_UNIVERSAL_STATUS.USER_ID, SHM_USR_TABLES_ENUM.PERSONNEL_TABLE);
            	 
            } catch(Exception e ){               
                 LIB.log("createIndexUniversalStatusTable", e, className);
            }
            
			return tReturn; 
          } 
    	 
    	
    	
    	
    	public static Table createRawReportTable(String className) throws OException{
          	Table tReturn = Util.NULL_TABLE;
          	
            try{
              tReturn = Table.tableNew(); 
        	  
        	  LIB.safeAddCol(tReturn, COLS.RAW_REPORT.RUN_DATE, COL_TYPE_ENUM.COL_INT);
        	   
        	  LIB.safeAddCol(tReturn, COLS.RAW_REPORT.INDEX_ID, COL_TYPE_ENUM.COL_INT);
        	  LIB.safeAddCol(tReturn, COLS.RAW_REPORT.INDEX_NAME, COL_TYPE_ENUM.COL_STRING);
        	  
        	  LIB.safeAddCol(tReturn, COLS.RAW_REPORT.CLOSING_PRICES_SAVED_FLAG, COL_TYPE_ENUM.COL_INT);

        	  LIB.safeAddCol(tReturn, COLS.RAW_REPORT.USER_SAVED_CLOSING, COL_TYPE_ENUM.COL_INT);
        	  LIB.safeAddCol(tReturn, COLS.RAW_REPORT.USER_SAVED_UNIVERSAL, COL_TYPE_ENUM.COL_INT);

        	  LIB.safeAddCol(tReturn, COLS.RAW_REPORT.LAST_UPDATE_CLOSING, COL_TYPE_ENUM.COL_STRING);
        	  LIB.safeAddCol(tReturn, COLS.RAW_REPORT.LAST_UPDATE_UNIVERSAL, COL_TYPE_ENUM.COL_STRING);

        	  // These are ints in the Index List table.  We'll convert them to Strings for this table
        	  LIB.safeAddCol(tReturn, COLS.RAW_REPORT.INDEX_TYPE, COL_TYPE_ENUM.COL_STRING);
        	  LIB.safeAddCol(tReturn, COLS.RAW_REPORT.INDEX_STATUS, COL_TYPE_ENUM.COL_STRING);
        	  LIB.safeAddCol(tReturn, COLS.RAW_REPORT.MARKET, COL_TYPE_ENUM.COL_STRING);
        	  LIB.safeAddCol(tReturn, COLS.RAW_REPORT.IDX_GROUP, COL_TYPE_ENUM.COL_STRING);
          	
              	 
            } catch(Exception e ){               
                	LIB.log("createRawReportTable", e, className);
            }
             
			return tReturn; 
    	} 

    	public static Table createScriptPanelTable(String className) throws OException{
          	Table tReturn = Util.NULL_TABLE;
                try{
                  tReturn = Table.tableNew();
            	  LIB.safeAddCol(tReturn, COLS.SCRIPT_PANEL.RUN_DATE, COL_TYPE_ENUM.COL_CELL); 

            	  LIB.safeAddCol(tReturn, COLS.SCRIPT_PANEL.INDEX_ID, COL_TYPE_ENUM.COL_CELL);
            	  LIB.safeAddCol(tReturn, COLS.SCRIPT_PANEL.INDEX_NAME, COL_TYPE_ENUM.COL_CELL);
 
            	  LIB.safeAddCol(tReturn, COLS.SCRIPT_PANEL.CLOSING_PRICES_SAVED_FLAG, COL_TYPE_ENUM.COL_INT);

            	  LIB.safeAddCol(tReturn, COLS.SCRIPT_PANEL.USER_SAVED_CLOSING, COL_TYPE_ENUM.COL_CELL);
            	  LIB.safeAddCol(tReturn, COLS.SCRIPT_PANEL.LAST_UPDATE_CLOSING, COL_TYPE_ENUM.COL_CELL);
            	  
            	  LIB.safeAddCol(tReturn, COLS.SCRIPT_PANEL.USER_SAVED_UNIVERSAL, COL_TYPE_ENUM.COL_CELL);
            	  LIB.safeAddCol(tReturn, COLS.SCRIPT_PANEL.LAST_UPDATE_UNIVERSAL, COL_TYPE_ENUM.COL_CELL);
 
            	  LIB.safeAddCol(tReturn, COLS.SCRIPT_PANEL.INDEX_TYPE, COL_TYPE_ENUM.COL_CELL);
            	  LIB.safeAddCol(tReturn, COLS.SCRIPT_PANEL.INDEX_STATUS, COL_TYPE_ENUM.COL_CELL);
            	  LIB.safeAddCol(tReturn, COLS.SCRIPT_PANEL.MARKET, COL_TYPE_ENUM.COL_CELL);
            	  LIB.safeAddCol(tReturn, COLS.SCRIPT_PANEL.IDX_GROUP, COL_TYPE_ENUM.COL_CELL);
              	 
            	  tReturn.setColFormatAsBool(COLS.SCRIPT_PANEL.CLOSING_PRICES_SAVED_FLAG);
            	  
            	  // Keep this as an int
            	  LIB.safeAddCol(tReturn, COLS.SCRIPT_PANEL.HIDDEN_INDEX_ID, COL_TYPE_ENUM.COL_INT);
            	  tReturn.colHide(COLS.SCRIPT_PANEL.HIDDEN_INDEX_ID);
             	  
                } catch(Exception e ){               
                	LIB.log("createScriptPanelTable", e, className);
                }
                 
				return tReturn; 
          } 
    	
    	public static Table createComboBoxTable(String className) throws OException{
          	Table tReturn = Util.NULL_TABLE;
                try{
                  tReturn = Table.tableNew();

            	  LIB.safeAddCol(tReturn, COLS.COMBOBOX.ITEM_ID, COL_TYPE_ENUM.COL_INT);
            	  LIB.safeAddCol(tReturn, COLS.COMBOBOX.ITEM, COL_TYPE_ENUM.COL_STRING);
            	  
            	  LIB.safeColHide(tReturn, COLS.COMBOBOX.ITEM_ID);
            	  
                } catch(Exception e ){               
                	LIB.log("createComboBoxTable", e, className);
                }
                 
				return tReturn; 
          } 


    	public static Table createExtraDataTableAndAddOneRow(String className) throws OException{
          	Table tReturn = Util.NULL_TABLE;
                try{
                  tReturn = Table.tableNew();

            	  LIB.safeAddCol(tReturn, COLS.EXTRA_DATA_TABLE.SUB_TABLE, COL_TYPE_ENUM.COL_TABLE);
            	  LIB.safeAddCol(tReturn, COLS.EXTRA_DATA_TABLE.VERSION, COL_TYPE_ENUM.COL_STRING);
            	  
            	  tReturn.addRow();
            	  
                } catch(Exception e ){               
                	LIB.log("createExtraDataTableAndAddOneRow", e, className);
                }
                 
				return tReturn; 
          } 

    	public static Table createExtraDataSubTableAndAddOneRow(String className) throws OException{
          	Table tReturn = Util.NULL_TABLE;
                try{
                  tReturn = Table.tableNew("Data");

            	  LIB.safeAddCol(tReturn, COLS.EXTRA_DATA_TABLE.SETTINGS.RUN_DATE, COL_TYPE_ENUM.COL_INT);
            	  LIB.safeAddCol(tReturn, COLS.EXTRA_DATA_TABLE.SETTINGS.RUN_DATE_STRING, COL_TYPE_ENUM.COL_STRING);

            	  tReturn.addRow();

                } catch(Exception e ){               
                	LIB.log("createExtraDataSubTableAndAddOneRow", e, className);
                }
                 
				return tReturn; 
          }  
    	
    } // END public static class CREATE_TABLE


    public static class GET_DATA { 
    	 
    	public static Table getIndexlistTable(int iRunDate, String className) throws OException{
          	Table tReturn = Util.NULL_TABLE;
                try{
                  tReturn = CREATE_TABLE.createIndexListTable(className);
                  
                  String sSQL = "SELECT\n" + 
                  		"index_id, index_name, index_type, index_status, market, idx_group \n" + 
                  		"FROM\n" + 
                  		"idx_def\n" + 
                  		"WHERE \n" + 
                  		"db_status = 1";
                  LIB.execISql(tReturn, sSQL, false, className);
                    
                } catch(Exception e ){               
                     LIB.log("getIndexlistTable", e, className);
                }
                 
				return tReturn; 
          } 

    	public static Table getIndexClosingStatusTable(int iRunDate, String className) throws OException{
          	Table tReturn = Util.NULL_TABLE;
                try{
                  tReturn = CREATE_TABLE.createIndexClosingStatusTable(className);
                 
            	  // add the single quotes here
                  String sDateFormattedForDB = "'" + OCalendar.formatJdForDbAccess(iRunDate) + "'";
              	 	String sSQL = "SELECT \n" + 
              	 			"index_id, dataset_time, user_id, row_creation \n" + 
              	 			"FROM \n" + 
              	 			"idx_market_data\n" + 
              	 			"WHERE \n" + 
              	 			"dataset_type = 1 AND dataset_time = " + sDateFormattedForDB;
              	 	LIB.execISql(tReturn, sSQL, true, className);
   
                } catch(Exception e ){               
                     LIB.log("getIndexClosingStatusTable", e, className);
                }
                 
				return tReturn; 
          } 

    	public static Table getIndexUniversalStatusTable(String className) throws OException{
          	Table tReturn = Util.NULL_TABLE;
                try{
                  tReturn = CREATE_TABLE.createIndexUniversalStatusTable(className);
                  
                  // This does *not* use or need a date
                  String sSQL = "SELECT \n" + 
                  		"m.index_id, m.user_id, m.row_creation \n" + 
                  		"FROM \n" + 
                  		"idx_market_data m, idx_def i\n" + 
                  		"WHERE \n" + 
                  		"i.index_version_id = m.index_version_id AND\n" + 
                  		"i.db_status = 1 AND \n" + 
                  		"m.dataset_type = 2";
                  LIB.execISql(tReturn, sSQL, false, className);
   
                } catch(Exception e ){               
                     LIB.log("getIndexUniversalStatusTable", e, className);
                }
                 
				return tReturn; 
          } 
    	 
    	 
    	
    } // END public static class GET_DATA
     

    public static class PICK_LIST { 

        public static Table getRefreshIntervalPicklist(String className) throws OException {
        	
      	  Table tReturn = Util.NULL_TABLE;
      	  try{
        		
        		tReturn = CREATE_TABLE.createComboBoxTable(className);
        		
        		for (REFRESH_INTERVAL item : REFRESH_INTERVAL.values()) {
        			int iMaxRow = tReturn.addRow();
        			
        			LIB.safeSetInt(tReturn, COLS.COMBOBOX.ITEM_ID, iMaxRow, item.getTimeInSeconds());
        			LIB.safeSetString(tReturn, COLS.COMBOBOX.ITEM, iMaxRow, item.getName());
        		}
        		
      	  } catch(Exception e ){               
        		LIB.log("getRefreshIntervalPicklist", e, className);
      	  } 
        	return tReturn;
        }
   
    public static Table getPortfoliosToShowPicklist(String className) throws OException {
    	
    	  Table tReturn = Util.NULL_TABLE;
    	  try{
      		
      		tReturn = CREATE_TABLE.createComboBoxTable(className);
      		
      		for (PORTFOLIOS_TO_SHOW item : PORTFOLIOS_TO_SHOW.values()) {
      			int iMaxRow = tReturn.addRow();
      			
      			LIB.safeSetInt(tReturn, COLS.COMBOBOX.ITEM_ID, iMaxRow, item.getValue());
      			LIB.safeSetString(tReturn, COLS.COMBOBOX.ITEM, iMaxRow, item.getName());
      		}
      		
    	  } catch(Exception e ){               
      		LIB.log("getPortfoliosToShowPicklist", e, className);
    	  } 
      	return tReturn;
      }
    }
	  
    public static class GUI { 


        public static class BUTTON_HANDLER { 
        	
            public static void doEverythingButton(Table tScriptPanel, Table tExtraData,
            		int iRunDate, String sRunDate, 
            		String className) throws OException {
            	
            	try{
            		String sButtonName = tScriptPanel.scriptDataGetCallbackName();
            		String sButtonLabel = tScriptPanel.scriptDataGetWidgetString(sButtonName);
            		LIB.log("ButtonName: " + sButtonName + ", ButtonLabel: " + sButtonLabel, className);
 
            		boolean bHandledFlag = false;
               		if (Str.equal(sButtonName, BUTTON.BUTTON1.getName()) == CONST_VALUES.VALUE_OF_TRUE) {
            			FUNC.getAndRefreshDataIntoScriptPanel(tScriptPanel, iRunDate, sRunDate, className);
            			bHandledFlag = true;
            		}
               		if (Str.equal(sButtonName, BUTTON.BUTTON2.getName()) == CONST_VALUES.VALUE_OF_TRUE) {
               			// This button, i.e., giving it as an example... is more about showing how this works, and less about being really 'needed' for this Panel
               			// i.e., want to show an example of how to launch another module in OpenLink
               			Afe.issueServiceRequestByID(AFE_SERVICE.MARKET_MANAGER);
            			bHandledFlag = true;
            		}
               		if (Str.equal(sButtonName, BUTTON.BUTTON3.getName()) == CONST_VALUES.VALUE_OF_TRUE) {
               			Afe.issueServiceRequestByID(AFE_SERVICE.INDEX_LISTING);
            			bHandledFlag = true;
            		}
               		if (Str.equal(sButtonName, BUTTON.BUTTON4.getName()) == CONST_VALUES.VALUE_OF_TRUE) {
               			Afe.issueServiceRequestByID(AFE_SERVICE.HISTORIC_PRICES);
            			bHandledFlag = true;
            		}
               		if (Str.equal(sButtonName, BUTTON.BUTTON5.getName()) == CONST_VALUES.VALUE_OF_TRUE) {
               			Afe.issueServiceRequestByID(AFE_SERVICE.VOLATILITY_LISTING);
            			bHandledFlag = true;
            		}
               		if (Str.equal(sButtonName, BUTTON.BUTTON6.getName()) == CONST_VALUES.VALUE_OF_TRUE) {
               			Afe.issueServiceRequestByID(AFE_SERVICE.CORRELATION_LISTING);
            			bHandledFlag = true;
            		}
               		if (Str.equal(sButtonName, BUTTON.BUTTON7.getName()) == CONST_VALUES.VALUE_OF_TRUE) {
               			int iIndexID = Ref.getValue(SHM_USR_TABLES_ENUM.INDEX_TABLE, CONST_VALUES.DISCOUNT_USD);
               			FUNC.showUserIndexInputScreenForIndex(iIndexID, className);
               			bHandledFlag = true;
            		} 
               		if (Str.equal(sButtonName, BUTTON.DEBUG_INFO.getName()) == CONST_VALUES.VALUE_OF_TRUE) {
               			{
               				try {
                   				Table tCopy = tExtraData.copyTable();
                   				LIB.viewTable(tCopy, "Debug Info for " + className);
                   				tCopy = LIB.destroyTable(tCopy);
               				} catch (Exception e) {
               					// don't log this
               				}
               			}
            			bHandledFlag = true;
            		}
            		if (bHandledFlag == false) {
            			LIB.log("WARNING: This button was not handled. Name is: " + "'" + sButtonName + "'", className);
            		}
 
            	} catch(Exception e ){               
            		LIB.log("doEverythingButton", e, className);
            	} 
            }
        }

        public static class TIMER_HANDLER { 
        	
            public static void doEverythingTimer(Table tScriptPanel,
            		int iRunDate, String sRunDate, 
            		String className) throws OException {
            	
            	try{
            		String sTimerName = tScriptPanel.scriptDataGetTimerName();
            		boolean bHandledFlag = false;
            		if (Str.equal(sTimerName, TIMER.TIMER1.getName()) == CONST_VALUES.VALUE_OF_TRUE) {
            			FUNC.getAndRefreshDataIntoScriptPanel(tScriptPanel, iRunDate, sRunDate, className);
            			bHandledFlag = true;
            		}
            		// This gets triggered by a combobox
            		if (Str.equal(sTimerName, TIMER.TIMER2.getName()) == CONST_VALUES.VALUE_OF_TRUE) {
            			FUNC.getAndRefreshDataIntoScriptPanel(tScriptPanel, iRunDate, sRunDate, className);
            			tScriptPanel.scriptDataEndTimer(sTimerName);
            			bHandledFlag = true;
            		}
            		// This gets triggered when you start the script
            		// this is a one time run, so end the timer after this
            		if (Str.equal(sTimerName, TIMER.TIMER3.getName()) == CONST_VALUES.VALUE_OF_TRUE) {
            			FUNC.setAllLabelValuesForRefresh(tScriptPanel, className);
            			tScriptPanel.scriptDataEndTimer(sTimerName);
            			bHandledFlag = true;
            		}

            		if (bHandledFlag == false) {
            			LIB.log("WARNING: This timer was not handled. Name is: " + "'" + sTimerName + "'", className);
            		}

            	//	Ask.ok("You pressed me!");
            	} catch(Exception e ){               
            		LIB.log("doEverythingTimer", e, className);
            	} 
            }
        }

        public static class DOUBLE_CLICK_HANDLER { 
        	 
            
            public static void doEverythingDoubleClick(Table tScriptPanel,  
            		int iRunDate, String sRunDate, 
            		String className) throws OException {
            	
            	try{
            		
            		boolean bHandledFlag = false;
            		int iColNum = tScriptPanel.scriptDataGetCallbackCol();
            		int iRowNum = tScriptPanel.scriptDataGetCallbackRow();
            		
        			String sColName = "";
            		if (iColNum >= 1) {
            			sColName = tScriptPanel.getColName(iColNum);
            		}

    				int iIndexID = LIB.safeGetInt(tScriptPanel, COLS.SCRIPT_PANEL.HIDDEN_INDEX_ID, iRowNum);
    				@SuppressWarnings("unused")
					String sIndex = tScriptPanel.getCellString(COLS.SCRIPT_PANEL.INDEX_NAME, iRowNum);
  
    				// For now, do for all columns
    				{
    					FUNC.showUserIndexInputScreenForIndex(iIndexID, className);
//    					 Table arg_table = Afe.createServiceRequestArgTable();
//    					 int iMaxRow = arg_table.addRow();
//    					 
//    					 LIB.safeSetString(arg_table, "afe_arg_name", iMaxRow, "index_id");
//    					 // set the col type as an integer
////    					 	COL_INT		0               
////    					 	COL_DOUBLE	1               
////    					 	COL_TRAN	13              
////    					 	COL_DATE	19              
////    					 	COL_STRING	2               
//    					 LIB.safeSetInt(arg_table, "afe_arg_type", iMaxRow, COL_TYPE_ENUM.COL_INT.toInt());
//    					 // the value is always a string, i.e., need to convert this integer value to a string.
//    					 LIB.safeSetString(arg_table, "afe_arg_value", iMaxRow, Str.intToStr(iIndexID));
//    					 
//    					 Afe.issueServiceRequestWithTableArg(AFE_SERVICE.INDEX_INPUT, arg_table);
//    		 
//    					 arg_table = LIB.destroyTable(arg_table);
    					 
    					 bHandledFlag = true;
    				}
        			 
 
            		if (bHandledFlag == false) {
            			LIB.log("WARNING: This doubleclick was not handled, Column: " + "'" + sColName + "'" + ", Row Number: " + iRowNum, className);
            		}
            		  
            	} catch(Exception e ){               
            		LIB.log("doEverythingTimer", e, className);
            	} 
            }
        }

        public static class COMBOBOX_HANDLER { 
        	
            public static void doEverythingComboBox(Table tScriptPanel,
            		int iRunDate, String sRunDate, 
            		String className) throws OException {
            	
            	try{
            		 
            		// the name of the combobox the user modified
            		String sComboBoxName          = tScriptPanel.scriptDataGetCallbackName();
                    // a one column, one row table with the row number of the item the user selected
                    Table tComboBoxReturnMenu    = tScriptPanel.scriptDataGetWidgetMenuSelect(sComboBoxName);
                    // The row the user selected
                    // This can be multiple rows if this is a multi-select combo box
                    int iComboBoxRowSelected   = tComboBoxReturnMenu.getInt(1, 1);
                    // The full menu with all of the items a user can select
                    Table tComboBoxFullMenu      = tScriptPanel.scriptDataGetWidgetMenu(sComboBoxName);

                    // sComboBoxSelectedValue is the value the user selected     
                    int iComboBoxSelectedID    = LIB.safeGetInt(tComboBoxFullMenu, COLS.COMBOBOX.ITEM_ID, iComboBoxRowSelected);
					@SuppressWarnings("unused")
					String sComboBoxSelectedValue    = LIB.safeGetString(tComboBoxFullMenu, COLS.COMBOBOX.ITEM, iComboBoxRowSelected);
                   
                    // Alternate Way to get the value the user selected 
                    //sComboBoxStringValue   = tScriptPanel.scriptDataGetWidgetString(sComboBoxName);
                    
                    boolean bHandledFlag = false;
                    // This is 'refresh interval'
                    if (Str.equal(sComboBoxName, COMBOBOX.COMBOBOX1.getName()) == CONST_VALUES.VALUE_OF_TRUE) {
                    	int iRefreshIntervalInSeconds = iComboBoxSelectedID;
                    	if (iRefreshIntervalInSeconds < 1) {
                    		LIB.log("Refresh Timer is *STOPPED*", className);
                    		tScriptPanel.scriptDataEndTimer(TIMER.TIMER1.getName());
                    	}
                    	if (iRefreshIntervalInSeconds >= 1) {
                    		int iTimeInMilliseconds = iRefreshIntervalInSeconds * 1000;
                    		// I tested... you do need to turn off the timer first
                    		// also.. this is OK to call even if the timer is already off
                        	tScriptPanel.scriptDataEndTimer(TIMER.TIMER1.getName());
                        	LIB.log("Set the timer to kick off an auto-refresh every this many seconds: " + iRefreshIntervalInSeconds, className);
                    		tScriptPanel.scriptDataSetTimer(TIMER.TIMER1.getName(), iTimeInMilliseconds);
                    	}
                    	bHandledFlag = true;
            		} 
                    
            		if (bHandledFlag == false) {
            			LIB.log("WARNING: This ComboBox was not handled. Name is: " + "'" + sComboBoxName + "'", className);
            		}
                    
                    
            	} catch(Exception e ){               
            		LIB.log("doEverythingComboBox", e, className);
            	} 
            }
        }
        
        public static class INIT_HANDLER { 
        

            public static void doEverythingInit(int iRunDate, 
            		REFRESH_INTERVAL defaultRefreshInterval, String className) throws OException {

            	Table tScriptPanel = Util.NULL_TABLE;
            	Table tRawReport = Util.NULL_TABLE;
            	Table tIndexList = Util.NULL_TABLE; 
            	Table tIndexClosingStatus = Util.NULL_TABLE;   
            	Table tIndexUniversalStatus = Util.NULL_TABLE;   
            	try{
            		String sRunDate = OCalendar.formatJd(iRunDate, DATE_FORMAT.DATE_FORMAT_DMLY_NOSLASH); 
            		
            		tScriptPanel = CREATE_TABLE.createScriptPanelTable(className);
            		tRawReport = CREATE_TABLE.createRawReportTable(className);
            		tIndexList = GET_DATA.getIndexlistTable(iRunDate, className);
            		tIndexClosingStatus = GET_DATA.getIndexClosingStatusTable(iRunDate, className);
            		tIndexUniversalStatus = GET_DATA.getIndexUniversalStatusTable(className);
            		 
            		FUNC.addDataToRawReportTable(tRawReport, iRunDate, tIndexList, tIndexClosingStatus, tIndexUniversalStatus, className);
 
            		tRawReport.group(COLS.RAW_REPORT.INDEX_NAME);
            		FUNC.addRawReportToScriptPanelForFirstTime(tScriptPanel, tRawReport, sRunDate, className);
            		  
            		// Add buttons
            		// set to add them in a loop, for no particular reason, just to show off
            		boolean bAddAllButtonsInLoopFlag = true;
            		if (bAddAllButtonsInLoopFlag == true) {
                		for (BUTTON myButton : BUTTON.values()) {
                			GUI.addButton(tScriptPanel, myButton, className);
                		}
            		} else {
                		GUI.addButton(tScriptPanel, BUTTON.BUTTON1, className);
                		GUI.addButton(tScriptPanel, BUTTON.BUTTON2, className);
                		GUI.addButton(tScriptPanel, BUTTON.BUTTON3, className);
                		GUI.addButton(tScriptPanel, BUTTON.BUTTON4, className);
                		GUI.addButton(tScriptPanel, BUTTON.BUTTON5, className);
                		GUI.addButton(tScriptPanel, BUTTON.BUTTON6, className);
                		GUI.addButton(tScriptPanel, BUTTON.BUTTON7, className); 
                		GUI.addButton(tScriptPanel, BUTTON.DEBUG_INFO, className);
            		}
            	
            		// Add Stand alone labels
            		GUI.addLabel(tScriptPanel, LABEL.LABEL3, className);
            		GUI.addLabel(tScriptPanel, LABEL.LABEL4, className);
            		GUI.addLabel(tScriptPanel, LABEL.LABEL5, className);
            		GUI.addLabel(tScriptPanel, LABEL.LABEL6, className);
            		GUI.addLabel(tScriptPanel, LABEL.LABEL7, className);
            		GUI.addLabel(tScriptPanel, LABEL.LABEL8, className);
            		GUI.addLabel(tScriptPanel, LABEL.LABEL9, className);
            		GUI.addLabelWithSetTextToShowUser(tScriptPanel, LABEL.LABEL10, FUNC.getDateTimeForLastUpdatedDateTime(className), className);
            		GUI.addLabel(tScriptPanel, LABEL.LABEL11, className);
            		GUI.addLabel(tScriptPanel, LABEL.LABEL12, className);
            		GUI.addLabel(tScriptPanel, LABEL.LABEL13, className);
            		GUI.addLabel(tScriptPanel, LABEL.LABEL14, className);
            		
            		// Add Combo Boxes and associated labels when applicable
            		{
                		GUI.addLabel(tScriptPanel, LABEL.LABEL1, className);
                		Table tComboBoxMenu = PICK_LIST.getRefreshIntervalPicklist(className);
                		GUI.addComboxBox(tScriptPanel, COMBOBOX.COMBOBOX1, tComboBoxMenu, defaultRefreshInterval.getName(), className);
                		// OpenLink makes a copy of tComboBoxMenu, so OK to delete it
                		tComboBoxMenu = LIB.destroyTable(tComboBoxMenu);
            		}
            		
            		// Add Timers
            		if (defaultRefreshInterval != REFRESH_INTERVAL.NO_REFRESH) {
                		GUI.addTimerToWindow(tScriptPanel, TIMER.TIMER1, 
                				defaultRefreshInterval.getTimeInSeconds(), className);
            		}
            		 
            		
                	// set a 1 millisecond timer, and leave it to the timer handler to process this
            		// this is to do an initial set on the # of portfolios labels
                	tScriptPanel.scriptDataSetTimer(TIMER.TIMER3.getName(), 1);
            		
            		String sLocation = "top=" + PANEL.LOCATION.TOP + 
            					",left=" + PANEL.LOCATION.LEFT +
            					",right=" + PANEL.LOCATION.RIGHT + 
            					",bottom=" + PANEL.LOCATION.BOTTOM;
            		tScriptPanel.scriptDataMoveListBox(sLocation);

            		// This is only used when run using a 'ViewTable'. 
            		// Not used when this screen is within a Script Panel
            		String sSize = "x=" + PANEL.SIZE.X + 
        					",y=" + PANEL.SIZE.Y +
        					",w=" + PANEL.SIZE.W + 
        					",h=" + PANEL.SIZE.H;
            		tScriptPanel.scriptDataSetWindowSize(sSize);
            		
            		tScriptPanel.scriptDataHideIconPanel();

            		// Create, Populate, and Set the Extra data table
            		{
                		// Don't destroy tExtraData.  We'll set it into the script panel table below
                		Table tExtraData = CREATE_TABLE.createExtraDataTableAndAddOneRow(className);
                		// Don't destroy tExtraDataSubTable we set it into tExtraData
                		Table tExtraDataSubTable = CREATE_TABLE.createExtraDataSubTableAndAddOneRow(className);
                		LIB.safeSetTable(tExtraData, COLS.EXTRA_DATA_TABLE.SUB_TABLE, CONST_VALUES.ROW_TO_GET_OR_SET, tExtraDataSubTable);
                		LIB.safeSetColValString(tExtraData, COLS.EXTRA_DATA_TABLE.VERSION, LIB.VERSION_NUMBER);

                		// Now populate the sub table with the settings
                		LIB.safeSetInt(tExtraDataSubTable, COLS.EXTRA_DATA_TABLE.SETTINGS.RUN_DATE, CONST_VALUES.ROW_TO_GET_OR_SET, iRunDate);
                		LIB.safeSetString(tExtraDataSubTable, COLS.EXTRA_DATA_TABLE.SETTINGS.RUN_DATE_STRING, CONST_VALUES.ROW_TO_GET_OR_SET, sRunDate);
                		
                		tScriptPanel.scriptDataSetExtraData(tExtraData);
            		}
            		
            		// Do this for Script Panel functionality, i.e., just like this
            		tScriptPanel.viewTable();
            		  
            	} catch(Exception e ){               
            		LIB.log("doEverythingInit", e, className);
            	} 
            	tScriptPanel = LIB.destroyTable(tScriptPanel);
            	tRawReport = LIB.destroyTable(tRawReport);
            	tIndexList = LIB.destroyTable(tIndexList);  
            	tIndexClosingStatus = LIB.destroyTable(tIndexClosingStatus);
            	tIndexUniversalStatus = LIB.destroyTable(tIndexUniversalStatus);
            }
        	
        }

    	public static void addButton(Table tScriptPanel, BUTTON button, String className) throws OException{

        	try{
        		String widget_options = "label=" + button.getLabel();
        		tScriptPanel.scriptDataAddWidget(button.getName(), 
        				SCRIPT_PANEL_WIDGET_TYPE_ENUM.SCRIPT_PANEL_PUSHBUTTON_WIDGET.toInt(), 
        				button.getPosition(), widget_options);
        	} catch(Exception e ){               
        		LIB.log("addButton", e, className);
        	}
    	} 

    	public static void addLabel(Table tScriptPanel, LABEL label, String className) throws OException{

        	try{
        		String widget_options = "label=" + label.getLabel();
        		tScriptPanel.scriptDataAddWidget(label.getName(), 
        				SCRIPT_PANEL_WIDGET_TYPE_ENUM.SCRIPT_PANEL_LABEL_WIDGET.toInt(), 
        				label.getPosition(), widget_options);
        	} catch(Exception e ){               
        		LIB.log("addLabel", e, className);
        	}
    	} 
		
	    public static void addLabelWithSetTextToShowUser(Table tScriptPanel, LABEL label, String sTextToShowUser, String className) throws OException{
	
	    	try{
	    		String widget_options = "label=" + sTextToShowUser;
	    		tScriptPanel.scriptDataAddWidget(label.getName(), 
	    				SCRIPT_PANEL_WIDGET_TYPE_ENUM.SCRIPT_PANEL_LABEL_WIDGET.toInt(), 
	    				label.getPosition(), widget_options);
	    	} catch(Exception e ){               
	    		LIB.log("addLabelWithSetTextToShowUser", e, className);
	    	}
		} 
	    
		public static void modifyLabelWithTextToShowUser(Table tScriptPanel, LABEL label, String sTextToShowUser, String className) throws OException{
	
	    	try{
	    		String widget_options = "label=" + sTextToShowUser;
	    		
	    		tScriptPanel.scriptDataModifyWidget(label.getName(), 
	    				SCRIPT_PANEL_WIDGET_TYPE_ENUM.SCRIPT_PANEL_LABEL_WIDGET.toInt(), 
	    				label.getPosition(), widget_options);
	    	} catch(Exception e ){               
	    		LIB.log("modifyLabelWithTextToShowUser", e, className);
	    	}
		}  
		
		public static void modifyLabelWithWidgetOptions(Table tScriptPanel, LABEL label, String widget_options, String className) throws OException{
	
	    	try{
	    		
	    		tScriptPanel.scriptDataModifyWidget(label.getName(), 
	    				SCRIPT_PANEL_WIDGET_TYPE_ENUM.SCRIPT_PANEL_LABEL_WIDGET.toInt(), 
	    				label.getPosition(), widget_options);
	    	} catch(Exception e ){               
	    		LIB.log("modifyLabelWithWidgetOptions", e, className);
	    	}
		} 
     	
    	public static void addComboxBox(Table tScriptPanel, COMBOBOX comboBox, Table tComboBoxMenu, String sDefaultValue, String className) throws OException{

        	try{
        		// don't recall if you need to delete this or not.  In our case, don't delete it.
        		// the user will see the table name, so set it to something generic, like 'picklist'
        		String sTableTitle = comboBox.getLabel(); //"Picklist";
        		tComboBoxMenu.setTableTitle(sTableTitle);
        		tComboBoxMenu.setTableName(sTableTitle);
        		
        		if (LIB.safeGetNumRows(tComboBoxMenu) >= 1) {
        			
            		// this is for the default value
            		Table tComboBoxSelectMenu = Table.tableNew();
            		// column name doesn't matter, just needs one int column
            		final String COL_NAME_TO_USE = "col1";
            		LIB.safeAddCol(tComboBoxSelectMenu, "col1", COL_TYPE_ENUM.COL_INT);
            		int iMaxRow = tComboBoxSelectMenu.addRow();
            		// note:  This is for sure the *row number* and not the ID number of the item, e.g., if this is portfolio 20023, in the first row, use a value of 1
            		int iRowNumOfItem = tComboBoxMenu.unsortedFindString(COLS.COMBOBOX.ITEM, sDefaultValue, SEARCH_CASE_ENUM.CASE_SENSITIVE);
            		if (iRowNumOfItem < 1) {
            			iRowNumOfItem = 1;
            		}
            		LIB.safeSetInt(tComboBoxSelectMenu, COL_NAME_TO_USE, iMaxRow, iRowNumOfItem);

            		// We need to set the label to the String value, for the user to see the selected item
            		String sValueToDisplay = LIB.safeGetString(tComboBoxMenu, COLS.COMBOBOX.ITEM, iRowNumOfItem);

            		String widget_options = "label=" + sValueToDisplay; 
        			
            		tScriptPanel.scriptDataAddWidget(comboBox.getName(), 
            				SCRIPT_PANEL_WIDGET_TYPE_ENUM.SCRIPT_PANEL_COMBOBOX_WIDGET.toInt(), 
            				comboBox.getPosition(), widget_options, tComboBoxMenu, tComboBoxSelectMenu);
            		// OpenLink makes a copy of tComboBoxSelectMenu, so OK to delete it
            		tComboBoxSelectMenu = LIB.destroyTable(tComboBoxSelectMenu);
        		}
        		
        		// You can use this to set the menu later, i.e., make it dynamically changing
        		//tScriptPanel.scriptDataSetWidgetMenu(widget_name, menu, menu_select)
        		//TABLE_ScriptData_SetWidgetMenu(tDialogBoxData, "combo_box_1", tComboBoxMenu1, tComboBoxSelectMenu1);
        	} catch(Exception e ){               
        		LIB.log("addButton", e, className);
        	}
    	} 
    	
    	public static void addTimerToWindow(Table tScriptPanel, TIMER timer, int iTimeInSeconds, String className) throws OException{

        	try{
        		int iTimeInMilliseconds = iTimeInSeconds * 1000;
        		tScriptPanel.scriptDataSetTimer(timer.getName(), iTimeInMilliseconds);
        	} catch(Exception e ){               
        		LIB.log("addTimerToWindow", e, className);
        	}
    	} 
    	  
    }  // end class GUI
    
    public static class FUNC {  

    	  
    	public static int getPriorBusinessDay(int iRunDate, String className) throws OException{
        	 
    		// default this
    		int iReturn = iRunDate - 1;
    		Table tTemp = Util.NULL_TABLE;
        	try{ 
                
        		final String TEMP_COL_NAME = "TEMP_COL_NAME";
 
        		tTemp = Table.tableNew();
        		LIB.safeAddCol(tTemp, TEMP_COL_NAME, COL_TYPE_ENUM.COL_INT);
        		
          	  // add the single quotes here
                String sDateFormattedForDB = "'" + OCalendar.formatJdForDbAccess(iRunDate) + "'";
                String sSQL = "SELECT max(dataset_time) as prior_run_date from idx_market_data WHERE dataset_type = 1 AND dataset_time < " + sDateFormattedForDB;
                LIB.execISql(tTemp, sSQL, true, className); 
             
                if (LIB.safeGetNumRows(tTemp) >= 1) {
                	iReturn = LIB.safeGetInt(tTemp, TEMP_COL_NAME, CONST_VALUES.ROW_TO_GET_OR_SET);
                } 
                
        	} catch(Exception e ){               
        		LIB.log("getPriorBusinessDay", e, className);
        	}   
        	tTemp = LIB.destroyTable(tTemp); 
        	return iReturn;
    	}
    	  
    	public static void showUserIndexInputScreenForIndex(int iIndexID, String className) throws OException{
       	 
       	 
    		Table arg_table = Util.NULL_TABLE;
        	try{ 
        		 arg_table = Afe.createServiceRequestArgTable();
    			 int iMaxRow = arg_table.addRow();

    			 LIB.safeSetString(arg_table, "afe_arg_name", iMaxRow, "index_id"); 
    			 // set the col type as an integer
//    			 	COL_INT		0               
//    			 	COL_DOUBLE	1               
//    			 	COL_TRAN	13              
//    			 	COL_DATE	19              
//    			 	COL_STRING	2               
    			 LIB.safeSetInt(arg_table, "afe_arg_type", iMaxRow, COL_TYPE_ENUM.COL_INT.toInt());
    			 // the value is always a string, i.e., need to convert this integer value to a string.
    			 LIB.safeSetString(arg_table, "afe_arg_value", iMaxRow, Str.intToStr(iIndexID));
    			 
    			 Afe.issueServiceRequestWithTableArg(AFE_SERVICE.INDEX_INPUT, arg_table);
              
        	} catch(Exception e ){               
        		LIB.log("showUserIndexInputScreenForIndex", e, className);
        	}    
			 arg_table = LIB.destroyTable(arg_table);
    	}
    	  
    	public static Table getUpdatedRawData(Table tScriptPanel,
    			int iRunDate, String sRunDate, 
    			String className) throws OException{
        	 
        	Table tRawReport = Util.NULL_TABLE; 
        	Table tIndexList = Util.NULL_TABLE;
        	Table tIndexClosingStatus = Util.NULL_TABLE;
        	Table tIndexUniversalStatus = Util.NULL_TABLE;
        	 
        	try{ 
        		int iNumRows = LIB.safeGetNumRows(tScriptPanel);
        		
        		if (iNumRows >= 1) {
            		
            		tRawReport = CREATE_TABLE.createRawReportTable(className);
            		
            		tIndexList = GET_DATA.getIndexlistTable(iRunDate, className);
            		tIndexClosingStatus = GET_DATA.getIndexClosingStatusTable(iRunDate, className);
            		tIndexUniversalStatus = GET_DATA.getIndexUniversalStatusTable(className);
            		
            		FUNC.addDataToRawReportTable(tRawReport, iRunDate, tIndexList, tIndexClosingStatus, tIndexUniversalStatus, className);
            		
            		tRawReport.group(COLS.RAW_REPORT.INDEX_NAME);
        		}
             
        	} catch(Exception e ){               
        		LIB.log("getUpdatedRawData", e, className);
        	}   
        	tIndexList = LIB.destroyTable(tIndexList); 
        	tIndexClosingStatus = LIB.destroyTable(tIndexClosingStatus); 
        	tIndexUniversalStatus = LIB.destroyTable(tIndexUniversalStatus);
        	
        	return tRawReport;
    	}

    	public static void getAndRefreshDataIntoScriptPanel(Table tScriptPanel,
    			int iRunDate, String sRunDate, 
    			String className) throws OException{
        	 
        	Table tRawReport = Util.NULL_TABLE;
        	try{ 
        		
    	    	tRawReport = FUNC.getUpdatedRawData(tScriptPanel, iRunDate, sRunDate, className);
    			FUNC.refreshDataIntoScriptPanel(tScriptPanel, tRawReport, iRunDate, sRunDate, className);
    		 
    			FUNC.setAllLabelValuesForRefresh(tScriptPanel, className);
    			
        	} catch(Exception e ){               
        		LIB.log("getAndRefreshDataIntoScriptPanel", e, className);
        	}  
        	tRawReport = LIB.destroyTable(tRawReport);
    	}
    	
    	public static void refreshDataIntoScriptPanel(Table tScriptPanel, Table tRawReport,
    			int iRunDate, String sRunDate, 
    			String className) throws OException{
 
        	
    		int iValue = 0; 
	   		String sValue = "";
			final String TEMP_COL_NAME_FOR_SORTING = "TEMP_COL_NAME_FOR_SORTING";
        	try{
        		
        		// Create temp table for sorting
        		{
        			Table tTemp = Table.tableNew();
        			LIB.safeAddCol(tTemp, COLS.SCRIPT_PANEL.HIDDEN_INDEX_ID, COL_TYPE_ENUM.COL_INT);
        			tScriptPanel.copyCol(COLS.SCRIPT_PANEL.HIDDEN_INDEX_ID, tTemp, COLS.SCRIPT_PANEL.HIDDEN_INDEX_ID);
        			
        			LIB.safeAddCol(tTemp, TEMP_COL_NAME_FOR_SORTING, COL_TYPE_ENUM.COL_INT);
        			tTemp.setColIncrementInt(TEMP_COL_NAME_FOR_SORTING, 1, 1);

        			// add to the tRawReport
        			LIB.safeAddCol(tRawReport, TEMP_COL_NAME_FOR_SORTING, COL_TYPE_ENUM.COL_INT);
        			
        			// add in the temp for sorting col into the Raw Report Table
        			String sWhat = TEMP_COL_NAME_FOR_SORTING;
        			String sWhere = COLS.SCRIPT_PANEL.HIDDEN_INDEX_ID + " EQ $" + COLS.RAW_REPORT.INDEX_ID;
        			LIB.select(tRawReport, tTemp, sWhat, sWhere, false, className);
        			
        			tTemp = LIB.destroyTable(tTemp);
        		}
        		 
        		
        		int iNumRows = LIB.safeGetNumRows(tRawReport);
        		
        		final String TEMP_COL_NAME_FOUND_FLAG = "TEMP_COL_NAME_FOUND_FLAG";
        		LIB.safeAddCol(tScriptPanel, TEMP_COL_NAME_FOUND_FLAG, COL_TYPE_ENUM.COL_INT);
        		LIB.safeSetColValInt(tScriptPanel, TEMP_COL_NAME_FOUND_FLAG, CONST_VALUES.VALUE_OF_FALSE);
        		
        		for (int iReportCounter = 1; iReportCounter <= iNumRows; iReportCounter++) {
        
        			int iRowNum = LIB.safeGetInt(tRawReport, TEMP_COL_NAME_FOR_SORTING, iReportCounter);

         			if (iRowNum >= 1) {
        			
        				// Mark that we found the row
        				LIB.safeSetInt(tScriptPanel, TEMP_COL_NAME_FOUND_FLAG, iRowNum, CONST_VALUES.VALUE_OF_TRUE);
        				
        				
            			
            			{
            				iValue = LIB.safeGetInt(tRawReport, COLS.RAW_REPORT.CLOSING_PRICES_SAVED_FLAG, iReportCounter);
            				LIB.safeSetInt(tScriptPanel, COLS.SCRIPT_PANEL.CLOSING_PRICES_SAVED_FLAG, iRowNum, iValue);
            			}


            			{
            				iValue = LIB.safeGetInt(tRawReport, COLS.RAW_REPORT.USER_SAVED_CLOSING, iReportCounter);
            				sValue = Ref.getName(SHM_USR_TABLES_ENUM.PERSONNEL_TABLE, iValue);
                			tScriptPanel.setCellString(COLS.SCRIPT_PANEL.USER_SAVED_CLOSING, iRowNum, sValue, FORMAT.DOUBLE_CLICK);
            			}
            			{
            				iValue = LIB.safeGetInt(tRawReport, COLS.RAW_REPORT.USER_SAVED_UNIVERSAL, iReportCounter);
            				sValue = Ref.getName(SHM_USR_TABLES_ENUM.PERSONNEL_TABLE, iValue);
                			tScriptPanel.setCellString(COLS.SCRIPT_PANEL.USER_SAVED_UNIVERSAL, iRowNum, sValue, FORMAT.DOUBLE_CLICK);
            			}

            			{
            				sValue = LIB.safeGetString(tRawReport, COLS.RAW_REPORT.LAST_UPDATE_CLOSING, iReportCounter);
                			tScriptPanel.setCellString(COLS.SCRIPT_PANEL.LAST_UPDATE_CLOSING, iRowNum, sValue, FORMAT.DOUBLE_CLICK);
            			}

            			{
            				sValue = LIB.safeGetString(tRawReport, COLS.RAW_REPORT.LAST_UPDATE_UNIVERSAL, iReportCounter);
                			tScriptPanel.setCellString(COLS.SCRIPT_PANEL.LAST_UPDATE_UNIVERSAL, iRowNum, sValue, FORMAT.DOUBLE_CLICK);
            			} 
 
        			}
        			  

        		} // end of loop
        		
        		// after the loop, delete rows that we didn't find
        		try {
            		tScriptPanel.deleteWhereValue(TEMP_COL_NAME_FOUND_FLAG, CONST_VALUES.VALUE_OF_FALSE);
        		} catch(Exception e ){               
        			// Don't log this
        		}
        		
        		// This is redundant... just need to delete
        		LIB.safeColHide(tScriptPanel, TEMP_COL_NAME_FOUND_FLAG);
        		LIB.safeDelCol(tScriptPanel, TEMP_COL_NAME_FOUND_FLAG);
        		
        	} catch(Exception e ){               
        		LIB.log("refreshDataIntoScriptPanel", e, className);
        	}
    		 
    	} 
    	
    	public static void addDataToRawReportTable(Table tRawReport, int iRunDate, Table tIndexList, 
    			Table tIndexClosingStatus, Table tIndexUniversalStatus, String className) throws OException{

        	String sWhat = "";
        	String sWhere = "";
        	try{
        		
        		sWhat = COLS.INDEX_LIST.INDEX_ID + "(" + COLS.RAW_REPORT.INDEX_ID + ")" + ", " +
        				COLS.INDEX_LIST.INDEX_NAME + "(" + COLS.RAW_REPORT.INDEX_NAME + ")" + ", " +
        				COLS.INDEX_LIST.INDEX_TYPE + "(" + COLS.RAW_REPORT.INDEX_TYPE + ")" + ", " +
        				COLS.INDEX_LIST.INDEX_STATUS + "(" + COLS.RAW_REPORT.INDEX_STATUS + ")"+ ", " +
        				COLS.INDEX_LIST.MARKET + "(" + COLS.RAW_REPORT.MARKET + ")"+ ", " +
        				COLS.INDEX_LIST.IDX_GROUP + "(" + COLS.RAW_REPORT.IDX_GROUP + ")";
        		sWhere = COLS.INDEX_LIST.INDEX_ID + " GE -1";
        		LIB.select(tRawReport, tIndexList, sWhat, sWhere, false, className);	
        		  
        		// run date is the same for all rows
        		LIB.safeSetColValInt(tRawReport, COLS.RAW_REPORT.RUN_DATE, iRunDate); 
        		
        		// default these to -1, so that they won't format
        		LIB.safeSetColValInt(tRawReport, COLS.RAW_REPORT.USER_SAVED_CLOSING, CONST_VALUES.NOT_APPLICABLE); 
        		LIB.safeSetColValInt(tRawReport, COLS.RAW_REPORT.USER_SAVED_UNIVERSAL, CONST_VALUES.NOT_APPLICABLE); 
        		
        		sWhat = COLS.INDEX_CLOSING_STATUS.USER_ID + "(" + COLS.RAW_REPORT.USER_SAVED_CLOSING + ")" + ", " +
        				COLS.INDEX_CLOSING_STATUS.ROW_CREATION + "(" + COLS.RAW_REPORT.LAST_UPDATE_CLOSING + ")";
        		sWhere = COLS.INDEX_CLOSING_STATUS.INDEX_ID + " EQ $" + COLS.RAW_REPORT.INDEX_ID;
        		LIB.select(tRawReport, tIndexClosingStatus, sWhat, sWhere, false, className);	

        		
        		sWhat = COLS.INDEX_UNIVERSAL_STATUS.USER_ID + "(" + COLS.RAW_REPORT.USER_SAVED_UNIVERSAL + ")" + ", " +
        				COLS.INDEX_UNIVERSAL_STATUS.ROW_CREATION + "(" + COLS.RAW_REPORT.LAST_UPDATE_UNIVERSAL + ")";
        		sWhere = COLS.INDEX_CLOSING_STATUS.INDEX_ID + " EQ $" + COLS.RAW_REPORT.INDEX_ID;
        		LIB.select(tRawReport, tIndexUniversalStatus, sWhat, sWhere, false, className);	

        		// default this for no particular reason... default should already be 0
        		LIB.safeSetColValInt(tRawReport, COLS.RAW_REPORT.CLOSING_PRICES_SAVED_FLAG, CONST_VALUES.VALUE_OF_FALSE); 

        		// populate the 'closing prices found flag'
        		int iNumRows = LIB.safeGetNumRows(tRawReport);
        		for (int iCounter = 1; iCounter <= iNumRows; iCounter++) {
        			int iUserSavedClosing = LIB.safeGetInt(tRawReport, COLS.RAW_REPORT.USER_SAVED_CLOSING, iCounter);
        			if (iUserSavedClosing >= 0) {
        				LIB.safeSetInt(tRawReport, COLS.RAW_REPORT.CLOSING_PRICES_SAVED_FLAG, iCounter, CONST_VALUES.VALUE_OF_TRUE);
        			}
        		}
        		
        	} catch(Exception e ){               
        		LIB.log("addDataToRawReportTable", e, className);
        	}
    	} 
 
	   	public static String getDateTimeForLastUpdatedDateTime(String className) throws OException{
	
	   		String sReturn = "";
	       	try{
	       		int iServerDate = OCalendar.getServerDate();
	       		String sServerTime = Util.timeGetServerTimeHMS();
	       		String sServerDate = OCalendar.formatJd(iServerDate, DATE_FORMAT.DATE_FORMAT_DMLY_NOSLASH);
	       		
	       		sReturn = sServerDate + " " + sServerTime;
	       		 
	       	} catch(Exception e ){               
	       		LIB.log("getDateTimeForLastUpdatedDateTime", e, className);
	       	}
	       	return sReturn;
	   	}  	
    		
    	public static void addRawReportToScriptPanelForFirstTime(Table tScriptPanel, Table tRawReport, String sRunDate, 
    			String className) throws OException{

    		int iValue = 0;
    		//double dValue = 0;
    		String sValue = "";
        	try{ 
        		
        		int iNumRows = LIB.safeGetNumRows(tRawReport);
        		tScriptPanel.addNumRows(iNumRows);
        		
        		for (int iCounter = 1; iCounter <= iNumRows; iCounter++) {
        			
        			iValue = LIB.safeGetInt(tRawReport, COLS.RAW_REPORT.INDEX_ID, iCounter);
        			LIB.safeSetInt(tScriptPanel, COLS.SCRIPT_PANEL.HIDDEN_INDEX_ID, iCounter, iValue);
        			
        			tScriptPanel.setCellInt(COLS.SCRIPT_PANEL.INDEX_ID, iCounter, iValue, FORMAT.DOUBLE_CLICK + "," + FORMAT.JUSTIFY.CENTER);
        			
        			sValue = LIB.safeGetString(tRawReport, COLS.RAW_REPORT.INDEX_NAME, iCounter);
        			tScriptPanel.setCellString(COLS.SCRIPT_PANEL.INDEX_NAME, iCounter, sValue, FORMAT.DOUBLE_CLICK);

        			sValue = LIB.safeGetString(tRawReport, COLS.RAW_REPORT.INDEX_TYPE, iCounter);
        			tScriptPanel.setCellString(COLS.SCRIPT_PANEL.INDEX_TYPE, iCounter, sValue, FORMAT.DOUBLE_CLICK);

        			sValue = LIB.safeGetString(tRawReport, COLS.RAW_REPORT.INDEX_STATUS, iCounter);
        			tScriptPanel.setCellString(COLS.SCRIPT_PANEL.INDEX_STATUS, iCounter, sValue, FORMAT.DOUBLE_CLICK);

        			sValue = LIB.safeGetString(tRawReport, COLS.RAW_REPORT.MARKET, iCounter);
        			tScriptPanel.setCellString(COLS.SCRIPT_PANEL.MARKET, iCounter, sValue, FORMAT.DOUBLE_CLICK);

        			sValue = LIB.safeGetString(tRawReport, COLS.RAW_REPORT.IDX_GROUP, iCounter);
        			tScriptPanel.setCellString(COLS.SCRIPT_PANEL.IDX_GROUP, iCounter, sValue, FORMAT.DOUBLE_CLICK);

        			tScriptPanel.setCellString(COLS.SCRIPT_PANEL.RUN_DATE, iCounter, sRunDate);
        		  	
        			{
        				iValue = LIB.safeGetInt(tRawReport, COLS.RAW_REPORT.CLOSING_PRICES_SAVED_FLAG, iCounter);
        				LIB.safeSetInt(tScriptPanel, COLS.SCRIPT_PANEL.CLOSING_PRICES_SAVED_FLAG, iCounter, iValue);
        			}
        			
        			{
        				iValue = LIB.safeGetInt(tRawReport, COLS.RAW_REPORT.USER_SAVED_CLOSING, iCounter);
        				sValue = Ref.getName(SHM_USR_TABLES_ENUM.PERSONNEL_TABLE, iValue);
            			tScriptPanel.setCellString(COLS.SCRIPT_PANEL.USER_SAVED_CLOSING, iCounter, sValue, FORMAT.DOUBLE_CLICK);
        			}
        			{
        				iValue = LIB.safeGetInt(tRawReport, COLS.RAW_REPORT.USER_SAVED_UNIVERSAL, iCounter);
        				sValue = Ref.getName(SHM_USR_TABLES_ENUM.PERSONNEL_TABLE, iValue);
            			tScriptPanel.setCellString(COLS.SCRIPT_PANEL.USER_SAVED_UNIVERSAL, iCounter, sValue, FORMAT.DOUBLE_CLICK);
        			}

        			{
        				sValue = LIB.safeGetString(tRawReport, COLS.RAW_REPORT.LAST_UPDATE_CLOSING, iCounter);
            			tScriptPanel.setCellString(COLS.SCRIPT_PANEL.LAST_UPDATE_CLOSING, iCounter, sValue, FORMAT.DOUBLE_CLICK);
        			}

        			{
        				sValue = LIB.safeGetString(tRawReport, COLS.RAW_REPORT.LAST_UPDATE_UNIVERSAL, iCounter);
            			tScriptPanel.setCellString(COLS.SCRIPT_PANEL.LAST_UPDATE_UNIVERSAL, iCounter, sValue, FORMAT.DOUBLE_CLICK);
        			} 
 
        			
        		}
        		  	
        	} catch(Exception e ){               
        		LIB.log("addRawReportToScriptPanelForFirstTime", e, className);
        	}
          } 


    	
    	public static void setAllLabelValuesForRefresh(Table tScriptPanel, String className) throws OException{

        	try{
        		
        		// This is the total number of portfolios
        		int iNumIndexes = LIB.safeGetNumRows(tScriptPanel);
        		 
        		int iNumWithSavedClosingValues = 0;
        		
        		for (int iCounter = 1; iCounter <= iNumIndexes; iCounter++) {
        			int iClosingValuesSaved = LIB.safeGetInt(tScriptPanel, COLS.SCRIPT_PANEL.CLOSING_PRICES_SAVED_FLAG, iCounter);
        			
        			if (iClosingValuesSaved == CONST_VALUES.VALUE_OF_TRUE) {
        				iNumWithSavedClosingValues = iNumWithSavedClosingValues + 1;
        			}
        		} 
        		   
        		GUI.modifyLabelWithTextToShowUser(tScriptPanel, LABEL.LABEL4, Str.intToStr(iNumIndexes), className);
        		
        		{
            		int iNumWithOutSavedClosingValues = iNumIndexes - iNumWithSavedClosingValues;
            		
            		String widget_options = "";
                	if (iNumWithOutSavedClosingValues == 0) {
            			widget_options = "bg=BLACK,fg=GREEN,label=" + iNumWithOutSavedClosingValues;
            		} else {
                		widget_options = "bg=BLACK,fg=RED,label=" + iNumWithOutSavedClosingValues;
            		}
            		
            		tScriptPanel.scriptDataModifyWidget(LABEL.LABEL6.getName(), 
            				SCRIPT_PANEL_WIDGET_TYPE_ENUM.SCRIPT_PANEL_LABEL_WIDGET.toInt(), 
            				LABEL.LABEL6.getPosition(), widget_options);

            		if (iNumWithOutSavedClosingValues == 0) {
            			widget_options = "bg=BLACK,fg=GREEN,label=" + LABEL.LABEL5.getLabel();
            		} else {
                		widget_options = "bg=RED,fg=BLACK,label=" + LABEL.LABEL5.getLabel();
            		}
            		
            		// This is the widget that shows items *MISSING Closing Values*
            		GUI.modifyLabelWithWidgetOptions(tScriptPanel, LABEL.LABEL5, widget_options, className);
        		}
        		
        		// Number of items that have saved closing values
        		GUI.modifyLabelWithTextToShowUser(tScriptPanel, LABEL.LABEL8, Str.intToStr(iNumWithSavedClosingValues), className);
        		
        		// DateTimeForLastUpdatedDateTime
        		GUI.modifyLabelWithTextToShowUser(tScriptPanel, LABEL.LABEL10, FUNC.getDateTimeForLastUpdatedDateTime(className), className);
        		  
        	} catch(Exception e ){               
        		LIB.log("setAllLabelValuesForRefresh", e, className);
        	}
    	} 

    } // END public static class FUNC
    
    
    
    public static class COLS {     
    
    	// These columns are cosmetic.. can change the names
    	public static class INDEX_LIST {     
        	public static final String INDEX_ID = "index_id"; //COL_INT
        	public static final String INDEX_NAME = "index_name"; //COL_STRING
        	public static final String INDEX_TYPE = "index_type"; //COL_INT
        	public static final String INDEX_STATUS = "index_status"; //COL_INT
        	public static final String MARKET = "market"; //COL_INT
        	public static final String IDX_GROUP = "idx_group"; //COL_INT
    	}
    	
    	// These columns are cosmetic.. can change the names
    	public static class INDEX_CLOSING_STATUS {     
        	public static final String INDEX_ID = "index_id"; //COL_INT 
        	public static final String DATASET_TIME = "dataset_time"; //COL_INT
        	public static final String USER_ID = "user_id"; //COL_INT
        	public static final String ROW_CREATION = "row_creation"; //COL_DATE_TIME
    	} 
    	
    	// These columns are cosmetic.. can change the names
    	public static class INDEX_UNIVERSAL_STATUS {     
        	public static final String INDEX_ID = "index_id"; //COL_INT 
        	public static final String USER_ID = "user_id"; //COL_INT
        	public static final String ROW_CREATION = "row_creation"; //COL_DATE_TIME
    	}  
    	
    	// These columns are cosmetic.. can change the names
    	public static class COMBOBOX {     
        	public static final String ITEM_ID = "item_id"; //COL_INT  
        	public static final String ITEM = "item"; //COL_STRING
    	}
 
    	
    	// These columns are cosmetic.. can change the names
    	public static class RAW_REPORT {      
        	public static final String RUN_DATE = "run_date"; //COL_INT
        	
        	public static final String INDEX_ID = "index_id"; //COL_INT
        	public static final String INDEX_NAME = "index_name"; //COL_STRING
        	
        	public static final String CLOSING_PRICES_SAVED_FLAG = "closing_prices"; //COL_INT

        	public static final String USER_SAVED_CLOSING = "user_saved_closing"; //COL_INT
        	public static final String USER_SAVED_UNIVERSAL = "user_saved_universal"; //COL_INT
        	
        	// these are datetimes in the raw data and a string in this table, i.e., we let OpenLink convert it to a string
        	public static final String LAST_UPDATE_CLOSING = "last_update_closing"; //COL_STRING
        	public static final String LAST_UPDATE_UNIVERSAL = "last_update_universal"; //COL_STRING

        	public static final String INDEX_TYPE = "index_type"; //COL_INT
        	public static final String INDEX_STATUS = "index_status"; //COL_INT
        	public static final String MARKET = "market"; //COL_INT
        	public static final String IDX_GROUP = "idx_group"; //COL_INT
    	}

    	// These columns are cosmetic.. can change the names
    	public static class SCRIPT_PANEL extends RAW_REPORT{     
    		// this will be used for sorting
        	public static final String HIDDEN_INDEX_ID = "hidden_index_id"; //COL_INT
    	}

    	public static class EXTRA_DATA_TABLE {     
        	public static final String SUB_TABLE = "sub_table";
        	public static final String VERSION = "version";
        	public static class SETTINGS {     
            	public static final String RUN_DATE = "run_date";
            	public static final String RUN_DATE_STRING = "run_date_string";
        	}
        }

    }
 
    public static class PANEL {            
        public static class LOCATION {            
        	public static final int TOP = 55;
        	public static final int LEFT = 1;
        	public static final int RIGHT = 1;
        	public static final int BOTTOM = 1;
        }
        public static class SIZE {            
        	public static final int X = 200;
        	public static final int Y = 250;
        	public static final int W = 1000;
        	public static final int H = 1000;
        }
		
    }

    public enum BUTTON{
    	BUTTON1 ("BUTTON1", "Reload Data", "x=15, y=10, h=35, w=120"), 
    	BUTTON2 ("BUTTON2", "Launch\nMarket Manager", "x=800, y=1, h=40, w=100"),
    	BUTTON3 ("BUTTON3", "Launch\nIndex Listing", "x=910, y=1, h=40, w=100"),
    	BUTTON4 ("BUTTON4", "Launch\nHistoric Prices", "x=1130, y=1, h=40, w=100"),
    	BUTTON5 ("BUTTON5", "Volatility Listing", "x=1020, y=1, h=18, w=100"),
    	BUTTON6 ("BUTTON6", "Correlation Listing", "x=1020, y=23, h=18, w=100"),

    	BUTTON7 ("BUTTON7", "Show\nDISCOUNT.USD", "x=530, y=1, h=40, w=85"),
    	DEBUG_INFO ("DEBUG_INFO", "Debug Info", "x=1700, y=1, h=20, w=60"),
    	  ; 

    	  private final String sButtonName;
      	  private final String sButtonLabel;
      	  private final String sButtonPosition;
    		  
		  BUTTON(String sButtonName, String sButtonLabel, String sButtonPosition) {
		      this.sButtonName = sButtonName;
		      this.sButtonLabel = sButtonLabel;
		      this.sButtonPosition = sButtonPosition;
		  }
		  
		  public String getName() {
			  return this.sButtonName;
		  }
  
		  public String getLabel() {
			  return this.sButtonLabel;
		  }
		  
		  public String getPosition() {
			  return this.sButtonPosition;
		  }
    }
    

    public enum LABEL {
    	LABEL1 ("LABEL1", "Refresh Interval", "x=150, y=1, h=20, w=90"), 
    	
    	// # Indexes label
    	LABEL3 ("LABEL3", "Indexes", 				   "x=330, y=1, h=18, w=100"), 
    	// # Indexes displayed, i.e., can change
    	LABEL4 ("LABEL4", "99", "x=322, y=1, h=18, w=30"), 

    	LABEL5 ("LABEL5", "Indexes Missing Closing Prices",       "x=352, y=19, h=18, w=160"), 
    	LABEL6 ("LABEL6", "99", "x=322, y=19, h=18, w=30"),

		LABEL7 ("LABEL7", "Indexes With Closing Prices", "x=357, y=37, h=18, w=140"), 
    	LABEL8 ("LABEL8", "99", "x=322, y=37, h=18, w=30"),

    	LABEL9 ("LABEL9", "Last Updated:",       "x=1230, y=1, h=18, w=100"), 
    	LABEL10 ("LABEL10", "dd-mmm-yyyy hh:mm:ss", "x=1237, y=19, h=18, w=120"),

    	LABEL11 ("LABEL11", "|\n|\n|\n|\n|", "x=781, y=1, h=47, w=20"),
    	LABEL12 ("LABEL12", "|\n|\n|\n|", "x=781, y=3, h=47, w=20"),
 
    	LABEL13 ("LABEL13", "|\n|\n|\n|\n|", "x=512, y=1, h=47, w=20"),
    	LABEL14 ("LABEL14", "|\n|\n|\n|", "x=512, y=3, h=47, w=20"),

    	  ; 

    	  private final String sLabelName;
      	  private final String sLabelLabel;
      	  private final String sLabelPosition;
    		  
      	LABEL(String sLabelName, String sLabelLabel, String sLabelPosition) {
		      this.sLabelName = sLabelName;
		      this.sLabelLabel = sLabelLabel;
		      this.sLabelPosition = sLabelPosition;
		  }
		  
		  public String getName() {
			  return this.sLabelName;
		  }
  
		  public String getLabel() {
			  return this.sLabelLabel;
			
		  }
		  
		  public String getPosition() {
			  return this.sLabelPosition;
		  }
    }

    public enum COMBOBOX {
    	COMBOBOX1 ("COMBOBOX1", "Refresh Interval", "x=150, y=20, h=20, w=90") 
    	  ; 

    	  private final String sComboBoxName;
      	  private final String sComboBoxLabel;
      	  private final String sComboBoxPosition;
    		  
      	COMBOBOX(String sComboBoxName, String sComboBoxLabel, String sComboBoxPosition) {
		      this.sComboBoxName = sComboBoxName;
		      this.sComboBoxLabel = sComboBoxLabel;
		      this.sComboBoxPosition = sComboBoxPosition;
		  }
		  
		  public String getName() {
			  return this.sComboBoxName;
		  }
  
		  public String getLabel() {
			  return this.sComboBoxLabel;
		  }
		  
		  public String getPosition() {
			  return this.sComboBoxPosition;
		  }
    } 
    
    public enum TIMER {
    	TIMER1 ("TIMER1"),
    	TIMER2 ("TIMER2"),
    	TIMER3 ("TIMER3")
    	  ; 

    	  private final String sTimerName;
    		  
    	  TIMER(String sTimerName) {
		      this.sTimerName = sTimerName;
		  }
		  
		  public String getName() {
			  return this.sTimerName;
		  }
  
    }
    
    public enum REFRESH_INTERVAL {
    	NO_REFRESH ("None", 0), 
    	REFRESH_30_SECONDS ("30 Seconds", 30),
    //	REFRESH_3_SECONDS_TEST ("3 Seconds TEST", 3), // TOxDO
    	REFRESH_1_MINUTE ("1 minute", 60),
    	REFRESH_2_MINUTE2 ("5 minutes", 300)
    	  ; 

    	  private final String sName;
      	  private final int iTimeInSeconds;
    		  
      	REFRESH_INTERVAL(String sName, int iTimeInSeconds) {
		      this.sName = sName;
		      this.iTimeInSeconds = iTimeInSeconds;
		  }
		  
		  public String getName() {
			  return this.sName;
		  }
  
		  public int getTimeInSeconds() {
			  return this.iTimeInSeconds;
		  }
		  
    }
 
    public enum PORTFOLIOS_TO_SHOW {
    	WITH_TRADES ("With Trades", 1), 
    	ALL ("All", 2)
    	  ; 

    	  private final String sName;
      	  private final int iValue;
    		  
      	PORTFOLIOS_TO_SHOW(String sName, int iValue) {
		      this.sName = sName;
		      this.iValue = iValue;
		  }
		  
		  public String getName() {
			  return this.sName;
		  }
  
		  public int getValue() {
			  return this.iValue;
		  }
    }

    public static class CONST_VALUES {            
    	public static final int VALUE_OF_TRUE = 1;
    	public static final int VALUE_OF_FALSE = 0;
    	
    	public static final int NOT_APPLICABLE = -1;

    	public static final int ROW_TO_GET_OR_SET = 1;
    	
    	public static final String DISCOUNT_USD = "DISCOUNT.USD";

    }
    
    public static class AFE_SERVICE {    
    	// get the full list from 
    	// select * from afe_service_tbl
    	public static final int SERVICES_MANAGER = 74;
    	public static final int MARKET_MANAGER = 18;
    	public static final int INDEX_LISTING = 215;
     	public static final int INDEX_INPUT = 112;
     	public static final int HISTORIC_PRICES = 19;

     	public static final int VOLATILITY_LISTING = 216;
     	public static final int CORRELATION_LISTING = 217;

    }
    
    public static class FORMAT { 
        public static class JUSTIFY { 
        	public static final String RIGHT = "just=RIGHT";
        	public static final String CENTER = "just=CENTER";
        }
    	
    	public static final String DOUBLE_CLICK = "dblclick=TRUE";

    }
  	
    public static class LIB {            

    	public static final String VERSION_NUMBER = "V1.004 (12Apr2023)";

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
                   String sMemSize = Str.doubleToStr(tMemSizeMegs, 2) + " megs";

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
                         } else {
                              LIB.log("Can't find a column named " + "'" + sColName + "'" + " for " + "safeGetString", "");
                         }
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
                         } else {
                              LIB.log("Can't find a column named " + "'" + sColName + "'" + " for " + "safeGetDouble", "");
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
                         } else {
                              LIB.log("Can't find a column named " + "'" + sColName + "'" + " for " + "safeGetTable", "");
                         }
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
                        } else {
                             LIB.log("Can't find a column named " + "'" + sColName + "'" + " for " + "safeSetColValInt", "");
                        }
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

        public static int safeGetNumCols(Table tMaster) throws OException{
              int iReturn = 0;
              try{
                   if (Table.isTableValid(tMaster) == 1) {
                         iReturn = tMaster.getNumCols();
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

        public static void safeAfsSaveTable(Table tMaster, String sAFSFileName, String className) throws OException{
              try{
                   final int SAVE_AS_PUBLIC_FLAG = 1;
                   Afs.saveTable(tMaster, sAFSFileName, SAVE_AS_PUBLIC_FLAG);
              } catch(Exception e ){               
                   LIB.log("safeAfsSaveTable", e, className);
              } 
         } 
        
        public static void safeSetColFormatAsRef(Table tMaster, String sColName, SHM_USR_TABLES_ENUM refEnum) throws OException{
            try{
                 tMaster.setColFormatAsRef(sColName, refEnum);  
            } catch(Exception e ){               
                 // don't log this
            } 
       } 
        
        public static void safeSetColFormatAsDate(Table tMaster, String sColName) throws OException{
            try{
                 tMaster.setColFormatAsDate(sColName, DATE_FORMAT.DATE_FORMAT_DMLY_NOSLASH, DATE_LOCALE.DATE_LOCALE_US);  
            } catch(Exception e ){               
                 // don't log this
            } 
       } 
           
        
        
   } // END LIB


    
}
