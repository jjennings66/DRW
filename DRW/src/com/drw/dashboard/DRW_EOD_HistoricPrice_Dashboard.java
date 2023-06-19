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
Name:  DRW_EOD_HistoricPrice_Dashboard.java

Deployment Instructions
Set this up as a 'Script Panel' in a 'Desktop'.

This is an viewing of Historic Prices for the current day

Revision History

19-Apr-2023 Brian New script

*/

@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_GENERIC)
@PluginType(SCRIPT_TYPE_ENUM.MAIN_SCRIPT)
public class DRW_EOD_HistoricPrice_Dashboard implements IScript
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
            		GUI.BUTTON_HANDLER.doEverythingButton(returnt, iRunDate, sRunDate, className);
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
 
    	public static Table createHistoricPriceTable(String className) throws OException{
          	Table tReturn = Util.NULL_TABLE;
            try{
            	tReturn = Table.tableNew(); 

            	LIB.safeAddCol(tReturn, COLS.HISTORIC_PRICE.INDEX_ID, COL_TYPE_ENUM.COL_INT); 
            	LIB.safeAddCol(tReturn, COLS.HISTORIC_PRICE.RESET_DATE, COL_TYPE_ENUM.COL_INT);
            	LIB.safeAddCol(tReturn, COLS.HISTORIC_PRICE.PRICE, COL_TYPE_ENUM.COL_DOUBLE);
            	LIB.safeAddCol(tReturn, COLS.HISTORIC_PRICE.PERSONNEL_ID, COL_TYPE_ENUM.COL_INT);
            	LIB.safeAddCol(tReturn, COLS.HISTORIC_PRICE.LAST_UPDATE, COL_TYPE_ENUM.COL_DATE_TIME);
            	
            	LIB.safeAddCol(tReturn, COLS.HISTORIC_PRICE.START_DATE, COL_TYPE_ENUM.COL_INT);
            	LIB.safeAddCol(tReturn, COLS.HISTORIC_PRICE.END_DATE, COL_TYPE_ENUM.COL_INT);
            	LIB.safeAddCol(tReturn, COLS.HISTORIC_PRICE.YIELD_BASIS, COL_TYPE_ENUM.COL_INT);
          	  	LIB.safeAddCol(tReturn, COLS.HISTORIC_PRICE.REF_SOURCE, COL_TYPE_ENUM.COL_INT);
          	  	LIB.safeAddCol(tReturn, COLS.HISTORIC_PRICE.INDEX_LOCATION, COL_TYPE_ENUM.COL_INT);

            	LIB.safeSetColFormatAsRef(tReturn, COLS.HISTORIC_PRICE.INDEX_ID, SHM_USR_TABLES_ENUM.INDEX_TABLE);
            	LIB.safeSetColFormatAsRef(tReturn, COLS.HISTORIC_PRICE.YIELD_BASIS, SHM_USR_TABLES_ENUM.YIELD_BASIS_TABLE);
            	LIB.safeSetColFormatAsRef(tReturn, COLS.HISTORIC_PRICE.REF_SOURCE, SHM_USR_TABLES_ENUM.REF_SOURCE_TABLE);
            	LIB.safeSetColFormatAsRef(tReturn, COLS.HISTORIC_PRICE.INDEX_LOCATION, SHM_USR_TABLES_ENUM.INDEX_LOCATION_TABLE);
            	
            } catch(Exception e ){               
                 LIB.log("createHistoricPriceTable", e, className);
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
        	  LIB.safeAddCol(tReturn, COLS.RAW_REPORT.RESET_DATE, COL_TYPE_ENUM.COL_INT);
        	  LIB.safeAddCol(tReturn, COLS.RAW_REPORT.PRICE, COL_TYPE_ENUM.COL_DOUBLE);
        	  LIB.safeAddCol(tReturn, COLS.RAW_REPORT.PERSONNEL_ID, COL_TYPE_ENUM.COL_INT);
        	  // In the raw data, last update is a date/time.  we'll treat as an string here
        	  LIB.safeAddCol(tReturn, COLS.RAW_REPORT.LAST_UPDATE, COL_TYPE_ENUM.COL_STRING);
     //   	  LIB.safeAddCol(tReturn, COLS.RAW_REPORT.LAST_UPDATE, COL_TYPE_ENUM.COL_DATE_TIME);
        	  LIB.safeAddCol(tReturn, COLS.RAW_REPORT.START_DATE, COL_TYPE_ENUM.COL_INT);
        	  LIB.safeAddCol(tReturn, COLS.RAW_REPORT.END_DATE, COL_TYPE_ENUM.COL_INT);
        	  LIB.safeAddCol(tReturn, COLS.RAW_REPORT.YIELD_BASIS, COL_TYPE_ENUM.COL_INT);
        	  LIB.safeAddCol(tReturn, COLS.RAW_REPORT.REF_SOURCE, COL_TYPE_ENUM.COL_INT);
        	  LIB.safeAddCol(tReturn, COLS.RAW_REPORT.INDEX_LOCATION, COL_TYPE_ENUM.COL_INT);
   	 
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

            	  // line these up to match what you would see in OpenLink
            	  LIB.safeAddCol(tReturn, COLS.SCRIPT_PANEL.INDEX_NAME, COL_TYPE_ENUM.COL_CELL);
            	  
            	  LIB.safeAddCol(tReturn, COLS.SCRIPT_PANEL.RESET_DATE, COL_TYPE_ENUM.COL_CELL);
            	  LIB.safeAddCol(tReturn, COLS.SCRIPT_PANEL.START_DATE, COL_TYPE_ENUM.COL_CELL);
            	  LIB.safeAddCol(tReturn, COLS.SCRIPT_PANEL.END_DATE, COL_TYPE_ENUM.COL_CELL);
            	  
            	  LIB.safeAddCol(tReturn, COLS.SCRIPT_PANEL.YIELD_BASIS, COL_TYPE_ENUM.COL_CELL);
            	  LIB.safeAddCol(tReturn, COLS.SCRIPT_PANEL.REF_SOURCE, COL_TYPE_ENUM.COL_CELL);
            	  LIB.safeAddCol(tReturn, COLS.SCRIPT_PANEL.INDEX_LOCATION, COL_TYPE_ENUM.COL_CELL);
            	  
            	  // For now, make this a 'double', since I don't know how to format to be 6 decimal places if it is a cell
            	  LIB.safeAddCol(tReturn, COLS.SCRIPT_PANEL.PRICE, COL_TYPE_ENUM.COL_DOUBLE);
            	  
            	  LIB.safeAddCol(tReturn, COLS.SCRIPT_PANEL.PERSONNEL_ID, COL_TYPE_ENUM.COL_CELL);
            	  LIB.safeAddCol(tReturn, COLS.SCRIPT_PANEL.LAST_UPDATE, COL_TYPE_ENUM.COL_CELL); 
            	  
            	  // Keep this as an int
            	  LIB.safeAddCol(tReturn, COLS.SCRIPT_PANEL.HIDDEN.INDEX_ID, COL_TYPE_ENUM.COL_INT);
            	  LIB.safeAddCol(tReturn, COLS.SCRIPT_PANEL.HIDDEN.YIELD_BASIS, COL_TYPE_ENUM.COL_INT);
            	  LIB.safeAddCol(tReturn, COLS.SCRIPT_PANEL.HIDDEN.REF_SOURCE, COL_TYPE_ENUM.COL_INT);
            	  LIB.safeAddCol(tReturn, COLS.SCRIPT_PANEL.HIDDEN.INDEX_LOCATION, COL_TYPE_ENUM.COL_INT);

            	  LIB.safeAddCol(tReturn, COLS.SCRIPT_PANEL.HIDDEN.RESET_DATE, COL_TYPE_ENUM.COL_INT);
            	  LIB.safeAddCol(tReturn, COLS.SCRIPT_PANEL.HIDDEN.START_DATE, COL_TYPE_ENUM.COL_INT);
            	  LIB.safeAddCol(tReturn, COLS.SCRIPT_PANEL.HIDDEN.END_DATE, COL_TYPE_ENUM.COL_INT);
            	  
            	  tReturn.colHide(COLS.SCRIPT_PANEL.HIDDEN.INDEX_ID);
            	  tReturn.colHide(COLS.SCRIPT_PANEL.HIDDEN.YIELD_BASIS);
            	  tReturn.colHide(COLS.SCRIPT_PANEL.HIDDEN.REF_SOURCE);
            	  tReturn.colHide(COLS.SCRIPT_PANEL.HIDDEN.INDEX_LOCATION);

            	  tReturn.colHide(COLS.SCRIPT_PANEL.HIDDEN.RESET_DATE);
            	  tReturn.colHide(COLS.SCRIPT_PANEL.HIDDEN.START_DATE);
            	  tReturn.colHide(COLS.SCRIPT_PANEL.HIDDEN.END_DATE);

             	  
            	  // this, i.e., 'width', is not used
            	  int width = 10;
            	  // 8 should be enough for display
            	  int prec = CONST_VALUES.DISPLAY_PRECISION;
            	  tReturn.setColFormatAsDouble(COLS.SCRIPT_PANEL.PRICE, width, prec);
            	  
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
    	 
    	public static Table getHistoricPriceTable(int iRunDate, String className) throws OException{
          	Table tReturn = Util.NULL_TABLE;
                try{
                  tReturn = CREATE_TABLE.createHistoricPriceTable(className);
                  
                  // add the single quotes here
                  String sDateFormattedForDB = "'" + OCalendar.formatJdForDbAccess(iRunDate) + "'";
                  String sSQL = "SELECT\n" + 
                  		"index_id, reset_date, price, personnel_id, last_update, start_date, end_date, yield_basis, ref_source, index_location\n" + 
                  		"FROM\n" + 
                  		"idx_historical_prices h\n" + 
                  		"WHERE \n" + 
                  		"reset_date = " + sDateFormattedForDB;
                  LIB.execISql(tReturn, sSQL, false, className);
                      
                } catch(Exception e ){               
                     LIB.log("getHistoricPriceTable", e, className);
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
        	
            public static void doEverythingButton(Table tScriptPanel,
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
            		// This is for setting the # of portfolios lables
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

    				int iIndexID = LIB.safeGetInt(tScriptPanel, COLS.SCRIPT_PANEL.HIDDEN.INDEX_ID, iRowNum);
    				@SuppressWarnings("unused")
					String sIndex = tScriptPanel.getCellString(COLS.SCRIPT_PANEL.INDEX_ID, iRowNum);

    				//int iPortfolioID = LIB.safeGetInt(tScriptPanel, COLS.SCRIPT_PANEL.xHIDDEN_PORTFOLIO_ID, iRowNum);
    			//	String sPortfolio = tScriptPanel.getCellString(COLS.SCRIPT_PANEL.PORTFOLIO, iRowNum);

    				// For now, do for all columns
    				{
    					 Table arg_table = Afe.createServiceRequestArgTable();
    					 int iMaxRow = arg_table.addRow();
    					 
    					 LIB.safeSetString(arg_table, "afe_arg_name", iMaxRow, "index_id");
    					 // set the col type as an integer
//    					 	COL_INT		0               
//    					 	COL_DOUBLE	1               
//    					 	COL_TRAN	13              
//    					 	COL_DATE	19              
//    					 	COL_STRING	2               
    					 LIB.safeSetInt(arg_table, "afe_arg_type", iMaxRow, COL_TYPE_ENUM.COL_INT.toInt());
    					 // the value is always a string, i.e., need to convert this integer value to a string.
    					 LIB.safeSetString(arg_table, "afe_arg_value", iMaxRow, Str.intToStr(iIndexID));
    					 
    					 // cmotion scheduling window
    					 Afe.issueServiceRequestWithTableArg(AFE_SERVICE.INDEX_INPUT, arg_table);
    		 
    					 arg_table = LIB.destroyTable(arg_table);
    					 
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
            	Table tHistoricPrice = Util.NULL_TABLE;  
            	try{
            		String sRunDate = OCalendar.formatJd(iRunDate, DATE_FORMAT.DATE_FORMAT_DMLY_NOSLASH); 
            		
            		tScriptPanel = CREATE_TABLE.createScriptPanelTable(className);
            		tRawReport = CREATE_TABLE.createRawReportTable(className);
            		tHistoricPrice = GET_DATA.getHistoricPriceTable(iRunDate, className);
            		 
            		FUNC.addDataToRawReportTable(tRawReport, iRunDate, tHistoricPrice, className);
 
            		tRawReport.group(COLS.RAW_REPORT.INDEX_NAME);
            		FUNC.addRawReportToScriptPanelForFirstTime(tScriptPanel, tRawReport, sRunDate, className);
            		 
            		// Add buttons
            		GUI.addButton(tScriptPanel, BUTTON.BUTTON1, className);
            		GUI.addButton(tScriptPanel, BUTTON.BUTTON2, className);
            		GUI.addButton(tScriptPanel, BUTTON.BUTTON3, className);
            		GUI.addButton(tScriptPanel, BUTTON.BUTTON4, className);

            		// Add Stand alone labels, if any
            		GUI.addLabel(tScriptPanel, LABEL.LABEL2, className);
            		GUI.addLabelWithSetTextToShowUser(tScriptPanel, LABEL.LABEL3, Str.intToStr(LIB.safeGetNumRows(tScriptPanel)), className);
            		GUI.addLabel(tScriptPanel, LABEL.LABEL9, className);
            		GUI.addLabelWithSetTextToShowUser(tScriptPanel, LABEL.LABEL10, FUNC.getDateTimeForLastUpdatedDateTime(className), className);
            		
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
            	tHistoricPrice = LIB.destroyTable(tHistoricPrice);   
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

    	public static Table getUpdatedRawData(Table tScriptPanel,
    			int iRunDate, String sRunDate, 
    			String className) throws OException{
        	 
        	Table tRawReport = Util.NULL_TABLE; 
        	Table tIndexList = Util.NULL_TABLE; 
        	 
        	try{ 
            		
        		tRawReport = CREATE_TABLE.createRawReportTable(className);
        		
        		tIndexList = GET_DATA.getHistoricPriceTable(iRunDate, className);
        		
        		FUNC.addDataToRawReportTable(tRawReport, iRunDate, tIndexList,  className);
        		
        		tRawReport.group(COLS.RAW_REPORT.INDEX_NAME);
             
        	} catch(Exception e ){               
        		LIB.log("getUpdatedRawData", e, className);
        	}   
        	tIndexList = LIB.destroyTable(tIndexList);  
        	
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
	 		double dValue = 0;
	   		String sValue = "";
			final String TEMP_COL_NAME_FOR_SORTING = "TEMP_COL_NAME_FOR_SORTING";
        	try{
        		
        		// Create temp table for sorting
        		{
        			Table tTemp = Table.tableNew();
        			LIB.safeAddCol(tTemp, COLS.SCRIPT_PANEL.HIDDEN.INDEX_ID, COL_TYPE_ENUM.COL_INT);
        			LIB.safeAddCol(tTemp, COLS.SCRIPT_PANEL.HIDDEN.YIELD_BASIS, COL_TYPE_ENUM.COL_INT);
        			LIB.safeAddCol(tTemp, COLS.SCRIPT_PANEL.HIDDEN.REF_SOURCE, COL_TYPE_ENUM.COL_INT);
        			LIB.safeAddCol(tTemp, COLS.SCRIPT_PANEL.HIDDEN.INDEX_LOCATION, COL_TYPE_ENUM.COL_INT);

        			LIB.safeAddCol(tTemp, COLS.SCRIPT_PANEL.HIDDEN.RESET_DATE, COL_TYPE_ENUM.COL_INT);
        			LIB.safeAddCol(tTemp, COLS.SCRIPT_PANEL.HIDDEN.START_DATE, COL_TYPE_ENUM.COL_INT);
        			LIB.safeAddCol(tTemp, COLS.SCRIPT_PANEL.HIDDEN.END_DATE, COL_TYPE_ENUM.COL_INT);
        			
        			tScriptPanel.copyCol(COLS.SCRIPT_PANEL.HIDDEN.INDEX_ID, tTemp, COLS.SCRIPT_PANEL.HIDDEN.INDEX_ID);
        			tScriptPanel.copyCol(COLS.SCRIPT_PANEL.HIDDEN.YIELD_BASIS, tTemp, COLS.SCRIPT_PANEL.HIDDEN.YIELD_BASIS);
        			tScriptPanel.copyCol(COLS.SCRIPT_PANEL.HIDDEN.REF_SOURCE, tTemp, COLS.SCRIPT_PANEL.HIDDEN.REF_SOURCE);
        			tScriptPanel.copyCol(COLS.SCRIPT_PANEL.HIDDEN.INDEX_LOCATION, tTemp, COLS.SCRIPT_PANEL.HIDDEN.INDEX_LOCATION);
        			
        			tScriptPanel.copyCol(COLS.SCRIPT_PANEL.HIDDEN.RESET_DATE, tTemp, COLS.SCRIPT_PANEL.HIDDEN.RESET_DATE);
        			tScriptPanel.copyCol(COLS.SCRIPT_PANEL.HIDDEN.START_DATE, tTemp, COLS.SCRIPT_PANEL.HIDDEN.START_DATE);
        			tScriptPanel.copyCol(COLS.SCRIPT_PANEL.HIDDEN.END_DATE, tTemp, COLS.SCRIPT_PANEL.HIDDEN.END_DATE);
        			
        			LIB.safeAddCol(tTemp, TEMP_COL_NAME_FOR_SORTING, COL_TYPE_ENUM.COL_INT);
        			tTemp.setColIncrementInt(TEMP_COL_NAME_FOR_SORTING, 1, 1);

        			// add to the tRawReport
        			LIB.safeAddCol(tRawReport, TEMP_COL_NAME_FOR_SORTING, COL_TYPE_ENUM.COL_INT);
        			 
        			// add in the temp for sorting col into the Raw Report Table
        			String sWhat = TEMP_COL_NAME_FOR_SORTING;
        			// need *ALL* items to join to be in sync
        			String sWhere = COLS.SCRIPT_PANEL.HIDDEN.INDEX_ID + " EQ $" + COLS.RAW_REPORT.INDEX_ID + " AND " +
    						COLS.SCRIPT_PANEL.HIDDEN.YIELD_BASIS + " EQ $" + COLS.RAW_REPORT.YIELD_BASIS + " AND " +
    						COLS.SCRIPT_PANEL.HIDDEN.REF_SOURCE + " EQ $" + COLS.RAW_REPORT.REF_SOURCE + " AND " +
    						COLS.SCRIPT_PANEL.HIDDEN.INDEX_LOCATION + " EQ $" + COLS.RAW_REPORT.INDEX_LOCATION + " AND " +
    						COLS.SCRIPT_PANEL.HIDDEN.RESET_DATE + " EQ $" + COLS.RAW_REPORT.RESET_DATE + " AND " +
    						COLS.SCRIPT_PANEL.HIDDEN.START_DATE + " EQ $" + COLS.RAW_REPORT.START_DATE + " AND " +
    						COLS.SCRIPT_PANEL.HIDDEN.END_DATE + " EQ $" + COLS.RAW_REPORT.END_DATE;
        			
        			LIB.select(tRawReport, tTemp, sWhat, sWhere, false, className);
        			
        			tTemp = LIB.destroyTable(tTemp);
        		}
        		 
        		
        		int iNumRows = LIB.safeGetNumRows(tRawReport);
        		
        		final String TEMP_COL_NAME_FOUND_FLAG = "TEMP_COL_NAME_FOUND_FLAG";
        		LIB.safeAddCol(tScriptPanel, TEMP_COL_NAME_FOUND_FLAG, COL_TYPE_ENUM.COL_INT);
        		LIB.safeSetColValInt(tScriptPanel, TEMP_COL_NAME_FOUND_FLAG, CONST_VALUES.VALUE_OF_FALSE);
        		
        		for (int iReportCounter = 1; iReportCounter <= iNumRows; iReportCounter++) {
        
        			int iRowNum = LIB.safeGetInt(tRawReport, TEMP_COL_NAME_FOR_SORTING, iReportCounter);


        			if (iRowNum < 1) {
//        				// This is to handle the case where the user chose 'All' for 'Display Portfolios'
//            			int iPortfolioID = LIB.safeGetInt(tRawReport, COLS.RAW_REPORT.PORTFOLIO_ID, iReportCounter);
//            			String sPortfolio = LIB.safeGetString(tRawReport, COLS.RAW_REPORT.PORTFOLIO, iReportCounter);
//            			iRowNum = tScriptPanel.addRow();
//            			
//            			LIB.safeSetInt(tScriptPanel, COLS.SCRIPT_PANEL.HIDDEN_PORTFOLIO_ID, iRowNum, iPortfolioID);
//            			tScriptPanel.setCellInt(COLS.SCRIPT_PANEL.PORTFOLIO_ID, iRowNum, iPortfolioID, FORMAT.JUSTIFY.CENTER);
//
//            			tScriptPanel.setCellString(COLS.SCRIPT_PANEL.PORTFOLIO, iRowNum, sPortfolio);
//            			tScriptPanel.setCellString(COLS.SCRIPT_PANEL.RUN_DATE, iRowNum, sRunDate, FORMAT.JUSTIFY.CENTER);
//            			
            			
        				iRowNum = tScriptPanel.addRow();
            			
            			

            			tScriptPanel.setCellString(COLS.SCRIPT_PANEL.RUN_DATE, iRowNum, sRunDate);
            			 
            			{
                			iValue = LIB.safeGetInt(tRawReport, COLS.RAW_REPORT.INDEX_ID, iReportCounter);
                			LIB.safeSetInt(tScriptPanel, COLS.SCRIPT_PANEL.HIDDEN.INDEX_ID, iRowNum, iValue);
                			
                			tScriptPanel.setCellInt(COLS.SCRIPT_PANEL.INDEX_ID, iRowNum, iValue, FORMAT.DOUBLE_CLICK + "," + FORMAT.JUSTIFY.CENTER);
            			}
            			
            			sValue = LIB.safeGetString(tRawReport, COLS.RAW_REPORT.INDEX_NAME, iReportCounter);
            			tScriptPanel.setCellString(COLS.SCRIPT_PANEL.INDEX_NAME, iRowNum, sValue, FORMAT.DOUBLE_CLICK);

            			// This is the main value we want to show
            			dValue = LIB.safeGetDouble(tRawReport, COLS.RAW_REPORT.PRICE, iReportCounter);
            			LIB.safeSetDouble(tScriptPanel, COLS.SCRIPT_PANEL.PRICE, iRowNum, dValue);

            			// Dates
            			{
                			iValue = LIB.safeGetInt(tRawReport, COLS.RAW_REPORT.RESET_DATE, iReportCounter);
                			LIB.safeSetInt(tScriptPanel, COLS.SCRIPT_PANEL.HIDDEN.RESET_DATE, iRowNum, iValue);
                			tScriptPanel.setCellDate(COLS.SCRIPT_PANEL.RESET_DATE, iRowNum, iValue, FORMAT.DOUBLE_CLICK);

                			iValue = LIB.safeGetInt(tRawReport, COLS.RAW_REPORT.START_DATE, iReportCounter);
                			LIB.safeSetInt(tScriptPanel, COLS.SCRIPT_PANEL.HIDDEN.START_DATE, iRowNum, iValue);
                			tScriptPanel.setCellDate(COLS.SCRIPT_PANEL.START_DATE, iRowNum, iValue, FORMAT.DOUBLE_CLICK);

                			iValue = LIB.safeGetInt(tRawReport, COLS.RAW_REPORT.END_DATE, iReportCounter);
                			LIB.safeSetInt(tScriptPanel, COLS.SCRIPT_PANEL.HIDDEN.END_DATE, iRowNum, iValue);
                			tScriptPanel.setCellDate(COLS.SCRIPT_PANEL.END_DATE, iRowNum, iValue, FORMAT.DOUBLE_CLICK);
            			}

            			
            			// Primary Keys, other than 'Date' keys and index_id
            			{
            				iValue = LIB.safeGetInt(tRawReport, COLS.RAW_REPORT.YIELD_BASIS, iReportCounter);
                			LIB.safeSetInt(tScriptPanel, COLS.SCRIPT_PANEL.HIDDEN.YIELD_BASIS, iRowNum, iValue);
            				sValue = Ref.getName(SHM_USR_TABLES_ENUM.YIELD_BASIS_TABLE, iValue);
                			tScriptPanel.setCellString(COLS.SCRIPT_PANEL.YIELD_BASIS, iRowNum, sValue, FORMAT.DOUBLE_CLICK);
            			}

            			{
            				iValue = LIB.safeGetInt(tRawReport, COLS.RAW_REPORT.REF_SOURCE, iReportCounter);
                			LIB.safeSetInt(tScriptPanel, COLS.SCRIPT_PANEL.HIDDEN.REF_SOURCE, iRowNum, iValue);
            				sValue = Ref.getName(SHM_USR_TABLES_ENUM.REF_SOURCE_TABLE, iValue);
                			tScriptPanel.setCellString(COLS.SCRIPT_PANEL.REF_SOURCE, iRowNum, sValue, FORMAT.DOUBLE_CLICK);
            			}

            			{
            				iValue = LIB.safeGetInt(tRawReport, COLS.RAW_REPORT.INDEX_LOCATION, iReportCounter);
                			LIB.safeSetInt(tScriptPanel, COLS.SCRIPT_PANEL.HIDDEN.INDEX_LOCATION, iRowNum, iValue);
            				sValue = Ref.getName(SHM_USR_TABLES_ENUM.INDEX_LOCATION_TABLE, iValue);
                			tScriptPanel.setCellString(COLS.SCRIPT_PANEL.INDEX_LOCATION, iRowNum, sValue, FORMAT.DOUBLE_CLICK);
            			}

            			// Info only fields
            			{
                			{
                				iValue = LIB.safeGetInt(tRawReport, COLS.RAW_REPORT.PERSONNEL_ID, iReportCounter);
                				sValue = Ref.getName(SHM_USR_TABLES_ENUM.PERSONNEL_TABLE, iValue);
                    			tScriptPanel.setCellString(COLS.SCRIPT_PANEL.PERSONNEL_ID, iRowNum, sValue, FORMAT.DOUBLE_CLICK);
                			}

                			{
                				sValue = LIB.safeGetString(tRawReport, COLS.RAW_REPORT.LAST_UPDATE, iReportCounter);
                    			tScriptPanel.setCellString(COLS.SCRIPT_PANEL.LAST_UPDATE, iRowNum, sValue, FORMAT.DOUBLE_CLICK);
                			}
            			}
            			 
        			}
        			
        			

        			
         			if (iRowNum >= 1) {
        			
        				// Mark that we found the row
        				LIB.safeSetInt(tScriptPanel, TEMP_COL_NAME_FOUND_FLAG, iRowNum, CONST_VALUES.VALUE_OF_TRUE);
        				
        				
            			
            			{
            				dValue = LIB.safeGetDouble(tRawReport, COLS.RAW_REPORT.PRICE, iReportCounter);
            				LIB.safeSetDouble(tScriptPanel, COLS.SCRIPT_PANEL.PRICE, iRowNum, dValue);
            			}
            			

            			// Info only fields
            			{
                			{
                				iValue = LIB.safeGetInt(tRawReport, COLS.RAW_REPORT.PERSONNEL_ID, iReportCounter);
                				sValue = Ref.getName(SHM_USR_TABLES_ENUM.PERSONNEL_TABLE, iValue);
                    			tScriptPanel.setCellString(COLS.SCRIPT_PANEL.PERSONNEL_ID, iRowNum, sValue, FORMAT.DOUBLE_CLICK);
                			}

                			{
                				sValue = LIB.safeGetString(tRawReport, COLS.RAW_REPORT.LAST_UPDATE, iReportCounter);
                    			tScriptPanel.setCellString(COLS.SCRIPT_PANEL.LAST_UPDATE, iRowNum, sValue, FORMAT.DOUBLE_CLICK);
                			}
            			}
            			
            			
//
//
//            			{
//            				iValue = LIB.safeGetInt(tRawReport, COLS.RAW_REPORT.USER_SAVED_CLOSING, iReportCounter);
//            				sValue = Ref.getName(SHM_USR_TABLES_ENUM.PERSONNEL_TABLE, iValue);
//                			tScriptPanel.setCellString(COLS.SCRIPT_PANEL.USER_SAVED_CLOSING, iRowNum, sValue, FORMAT.DOUBLE_CLICK);
//            			}
//            			{
//            				iValue = LIB.safeGetInt(tRawReport, COLS.RAW_REPORT.USER_SAVED_UNIVERSAL, iReportCounter);
//            				sValue = Ref.getName(SHM_USR_TABLES_ENUM.PERSONNEL_TABLE, iValue);
//                			tScriptPanel.setCellString(COLS.SCRIPT_PANEL.USER_SAVED_UNIVERSAL, iRowNum, sValue, FORMAT.DOUBLE_CLICK);
//            			}
//
//            			{
//            				sValue = LIB.safeGetString(tRawReport, COLS.RAW_REPORT.LAST_UPDATE_CLOSING, iReportCounter);
//                			tScriptPanel.setCellString(COLS.SCRIPT_PANEL.LAST_UPDATE_CLOSING, iRowNum, sValue, FORMAT.DOUBLE_CLICK);
//            			}
//
//            			{
//            				sValue = LIB.safeGetString(tRawReport, COLS.RAW_REPORT.LAST_UPDATE_UNIVERSAL, iReportCounter);
//                			tScriptPanel.setCellString(COLS.SCRIPT_PANEL.LAST_UPDATE_UNIVERSAL, iRowNum, sValue, FORMAT.DOUBLE_CLICK);
//            			} 
 
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
    	
    	public static void addDataToRawReportTable(Table tRawReport, int iRunDate, Table tHistoricPrice, 
    			String className) throws OException{

        	String sWhat = "";
        	String sWhere = "";
        	try{
        		
        		// index_id gets selected twice, once as an int and once as a string
        		sWhat = COLS.HISTORIC_PRICE.INDEX_ID + "(" + COLS.RAW_REPORT.INDEX_ID + ")" + ", " +
        				COLS.HISTORIC_PRICE.INDEX_ID + "(" + COLS.RAW_REPORT.INDEX_NAME + ")" + ", " +
        				COLS.HISTORIC_PRICE.RESET_DATE + "(" + COLS.RAW_REPORT.RESET_DATE + ")" + ", " +
        				COLS.HISTORIC_PRICE.PRICE + "(" + COLS.RAW_REPORT.PRICE + ")" + ", " +
        				COLS.HISTORIC_PRICE.PERSONNEL_ID + "(" + COLS.RAW_REPORT.PERSONNEL_ID + ")" + ", " +
        				COLS.HISTORIC_PRICE.LAST_UPDATE + "(" + COLS.RAW_REPORT.LAST_UPDATE + ")" + ", " +
        				COLS.HISTORIC_PRICE.START_DATE + "(" + COLS.RAW_REPORT.START_DATE + ")" + ", " +
        				COLS.HISTORIC_PRICE.END_DATE + "(" + COLS.RAW_REPORT.END_DATE + ")" + ", " +
        				COLS.HISTORIC_PRICE.YIELD_BASIS + "(" + COLS.RAW_REPORT.YIELD_BASIS + ")" + ", " +
        				COLS.HISTORIC_PRICE.REF_SOURCE + "(" + COLS.RAW_REPORT.REF_SOURCE + ")" + ", " +
        				COLS.HISTORIC_PRICE.INDEX_LOCATION + "(" + COLS.RAW_REPORT.INDEX_LOCATION + ")";
        		sWhere = COLS.HISTORIC_PRICE.INDEX_ID + " GE -1";
        		LIB.select(tRawReport, tHistoricPrice, sWhat, sWhere, false, className);	
        		
        		// run date is the same for all rows
        		LIB.safeSetColValInt(tRawReport, COLS.RAW_REPORT.RUN_DATE, iRunDate); 
    
        		// need to massage the data.  yield basis formats as a value when 0.  i.e., '0' is also 'Act/act ISDA'
        		// so set to -1 if needed
        		int iNumRows = LIB.safeGetNumRows(tRawReport);
        		for (int iCounter = 1; iCounter <= iNumRows; iCounter++) {
        			int iEndDate = LIB.safeGetInt(tRawReport, COLS.RAW_REPORT.END_DATE, iCounter);
        			if (iEndDate <= 1) {
        				LIB.safeSetInt(tRawReport, COLS.RAW_REPORT.YIELD_BASIS, iCounter, CONST_VALUES.NOT_APPLICABLE);
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
    		double dValue = 0;
    		String sValue = "";
        	try{ 
        		
        		// Grouping here is cosmetic
        		String sGroupCols = COLS.RAW_REPORT.INDEX_NAME + ", " + 
                		COLS.RAW_REPORT.START_DATE + ", " + 
                		COLS.RAW_REPORT.END_DATE + ", " + 
                		COLS.RAW_REPORT.YIELD_BASIS + ", " + 
                		COLS.RAW_REPORT.REF_SOURCE + ", " + 
                		COLS.RAW_REPORT.INDEX_LOCATION;
        		tRawReport.group(sGroupCols);
        		
        		int iNumRows = LIB.safeGetNumRows(tRawReport);
        		tScriptPanel.addNumRows(iNumRows);
        		  
        		for (int iCounter = 1; iCounter <= iNumRows; iCounter++) {

        			tScriptPanel.setCellString(COLS.SCRIPT_PANEL.RUN_DATE, iCounter, sRunDate);
        			 
        			{
            			iValue = LIB.safeGetInt(tRawReport, COLS.RAW_REPORT.INDEX_ID, iCounter);
            			LIB.safeSetInt(tScriptPanel, COLS.SCRIPT_PANEL.HIDDEN.INDEX_ID, iCounter, iValue);
            			
            			tScriptPanel.setCellInt(COLS.SCRIPT_PANEL.INDEX_ID, iCounter, iValue, FORMAT.DOUBLE_CLICK + "," + FORMAT.JUSTIFY.CENTER);
        			}
        			
        			sValue = LIB.safeGetString(tRawReport, COLS.RAW_REPORT.INDEX_NAME, iCounter);
        			tScriptPanel.setCellString(COLS.SCRIPT_PANEL.INDEX_NAME, iCounter, sValue, FORMAT.DOUBLE_CLICK);

        			// This is the main value we want to show
        			dValue = LIB.safeGetDouble(tRawReport, COLS.RAW_REPORT.PRICE, iCounter);
        			LIB.safeSetDouble(tScriptPanel, COLS.SCRIPT_PANEL.PRICE, iCounter, dValue);

        			// Dates
        			{
            			iValue = LIB.safeGetInt(tRawReport, COLS.RAW_REPORT.RESET_DATE, iCounter);
            			LIB.safeSetInt(tScriptPanel, COLS.SCRIPT_PANEL.HIDDEN.RESET_DATE, iCounter, iValue);
            			tScriptPanel.setCellDate(COLS.SCRIPT_PANEL.RESET_DATE, iCounter, iValue, FORMAT.DOUBLE_CLICK);

            			iValue = LIB.safeGetInt(tRawReport, COLS.RAW_REPORT.START_DATE, iCounter);
            			LIB.safeSetInt(tScriptPanel, COLS.SCRIPT_PANEL.HIDDEN.START_DATE, iCounter, iValue);
            			tScriptPanel.setCellDate(COLS.SCRIPT_PANEL.START_DATE, iCounter, iValue, FORMAT.DOUBLE_CLICK);

            			iValue = LIB.safeGetInt(tRawReport, COLS.RAW_REPORT.END_DATE, iCounter);
            			LIB.safeSetInt(tScriptPanel, COLS.SCRIPT_PANEL.HIDDEN.END_DATE, iCounter, iValue);
            			tScriptPanel.setCellDate(COLS.SCRIPT_PANEL.END_DATE, iCounter, iValue, FORMAT.DOUBLE_CLICK);
        			}

        			
        			// Primary Keys, other than 'Date' keys and index_id
        			{
        				iValue = LIB.safeGetInt(tRawReport, COLS.RAW_REPORT.YIELD_BASIS, iCounter);
            			LIB.safeSetInt(tScriptPanel, COLS.SCRIPT_PANEL.HIDDEN.YIELD_BASIS, iCounter, iValue);
        				sValue = Ref.getName(SHM_USR_TABLES_ENUM.YIELD_BASIS_TABLE, iValue);
            			tScriptPanel.setCellString(COLS.SCRIPT_PANEL.YIELD_BASIS, iCounter, sValue, FORMAT.DOUBLE_CLICK);
        			}

        			{
        				iValue = LIB.safeGetInt(tRawReport, COLS.RAW_REPORT.REF_SOURCE, iCounter);
            			LIB.safeSetInt(tScriptPanel, COLS.SCRIPT_PANEL.HIDDEN.REF_SOURCE, iCounter, iValue);
        				sValue = Ref.getName(SHM_USR_TABLES_ENUM.REF_SOURCE_TABLE, iValue);
            			tScriptPanel.setCellString(COLS.SCRIPT_PANEL.REF_SOURCE, iCounter, sValue, FORMAT.DOUBLE_CLICK);
        			}

        			{
        				iValue = LIB.safeGetInt(tRawReport, COLS.RAW_REPORT.INDEX_LOCATION, iCounter);
            			LIB.safeSetInt(tScriptPanel, COLS.SCRIPT_PANEL.HIDDEN.INDEX_LOCATION, iCounter, iValue);
        				sValue = Ref.getName(SHM_USR_TABLES_ENUM.INDEX_LOCATION_TABLE, iValue);
            			tScriptPanel.setCellString(COLS.SCRIPT_PANEL.INDEX_LOCATION, iCounter, sValue, FORMAT.DOUBLE_CLICK);
        			}

        			// Info only fields
        			{
            			{
            				iValue = LIB.safeGetInt(tRawReport, COLS.RAW_REPORT.PERSONNEL_ID, iCounter);
            				sValue = Ref.getName(SHM_USR_TABLES_ENUM.PERSONNEL_TABLE, iValue);
                			tScriptPanel.setCellString(COLS.SCRIPT_PANEL.PERSONNEL_ID, iCounter, sValue, FORMAT.DOUBLE_CLICK);
            			}

            			{
            				sValue = LIB.safeGetString(tRawReport, COLS.RAW_REPORT.LAST_UPDATE, iCounter);
                			tScriptPanel.setCellString(COLS.SCRIPT_PANEL.LAST_UPDATE, iCounter, sValue, FORMAT.DOUBLE_CLICK);
            			}
        			}
        			 
        			
        			
        			
        			 
        			
        		}
        		  	
        	} catch(Exception e ){               
        		LIB.log("addRawReportToScriptPanelForFirstTime", e, className);
        	}
          } 


    	
    	public static void setAllLabelValuesForRefresh(Table tScriptPanel, String className) throws OException{

        	try{
        		
        		// This is the total number of rows
        		int iNumRows = LIB.safeGetNumRows(tScriptPanel);
      		   
        		GUI.modifyLabelWithTextToShowUser(tScriptPanel, LABEL.LABEL3, Str.intToStr(iNumRows), className);
 
        	} catch(Exception e ){               
        		LIB.log("setAllLabelValuesForRefresh", e, className);
        	}
    	} 

    } // END public static class FUNC
    
    
    
    public static class COLS {     

    	// These columns are cosmetic.. can change the names
    	public static class HISTORIC_PRICE {     
        	public static final String INDEX_ID = "index_id"; //COL_INT
        	public static final String RESET_DATE = "reset_date"; //COL_INT
        	public static final String PRICE = "price"; //COL_DOUBLE
        	public static final String PERSONNEL_ID = "last_update_user"; //COL_INT
        	public static final String LAST_UPDATE = "last_update_time"; //COL_DATE_TIME
        	public static final String TIME = "time"; //COL_INT
        	public static final String START_DATE = "start_date"; //COL_INT
        	public static final String END_DATE = "end_date"; //COL_INT
        	public static final String YIELD_BASIS = "yield_basis"; //COL_INT
        	public static final String REF_SOURCE = "ref_source"; //COL_INT
        	public static final String INDEX_LOCATION = "index_location"; //COL_INT
    	}
    	  
    	// These columns are cosmetic.. can change the names
    	public static class COMBOBOX {     
        	public static final String ITEM_ID = "item_id"; //COL_INT  
        	public static final String ITEM = "item"; //COL_STRING
    	}
     	
    	// These columns are cosmetic.. can change the names
    	public static class RAW_REPORT {      
        	public static final String RUN_DATE = "reset_date"; //COL_INT
        	
        	public static final String INDEX_ID = "index_id"; //COL_INT
        	public static final String INDEX_NAME = "index_name"; //COL_STRING
        	public static final String RESET_DATE = "reset_date"; //COL_INT
        	public static final String PRICE = "price"; //COL_DOUBLE
        	public static final String PERSONNEL_ID = "last_update_user"; //COL_INT
        	public static final String LAST_UPDATE = "last_update_time"; //COL_DATE_TIME 
        	public static final String START_DATE = "start_date"; //COL_INT
        	public static final String END_DATE = "end_date"; //COL_INT
        	public static final String YIELD_BASIS = "yield_basis"; //COL_INT
        	public static final String REF_SOURCE = "ref_source"; //COL_INT
        	public static final String INDEX_LOCATION = "index_location"; //COL_INT

    	}

    	// These columns are cosmetic.. can change the names
    	public static class SCRIPT_PANEL extends RAW_REPORT{     
    		// this will be used for sorting
        	public static class HIDDEN {     
            	public static final String INDEX_ID = "HIDDEN_INDEX_ID"; //COL_INT
            	public static final String YIELD_BASIS = "HIDDEN_YIELD_BASIS"; //COL_INT
            	public static final String REF_SOURCE = "HIDDEN_REF_SOURCE"; //COL_INT
            	public static final String INDEX_LOCATION = "HIDDEN_INDEX_LOCATION"; //COL_INT
            	
            	// dates
            	public static final String RESET_DATE = "HIDDEN_RESET_DATE"; //COL_INT
            	public static final String START_DATE = "HIDDEN_START_DATE"; //COL_INT
            	public static final String END_DATE = "HIDDEN_END_DATE"; //COL_INT
        	}
        	
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
    	BUTTON2 ("BUTTON2", "Launch\nMarket Manager", "x=880, y=1, h=40, w=100"),
    	BUTTON3 ("BUTTON3", "Launch\nIndex Listing", "x=990, y=1, h=40, w=100"),
    	BUTTON4 ("BUTTON4", "Launch\nHistoric Prices", "x=1100, y=1, h=40, w=100")
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
    	 
    	LABEL2 ("LABEL2", "Historic Prices", "x=330, y=19, h=18, w=80"), 
    	LABEL3 ("LABEL3", "99", "x=300, y=19, h=18, w=30"),

    	LABEL9 ("LABEL9", "Last Updated:",       "x=1200, y=1, h=18, w=100"), 
    	LABEL10 ("LABEL10", "dd-mmm-yyyy hh:mm:ss", "x=1207, y=19, h=18, w=120"),

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
    	public static final int DISPLAY_PRECISION = 12;

    	public static final int ROW_TO_GET_OR_SET = 1;

    }
    
    public static class AFE_SERVICE {    
    	// get the full list from 
    	// select * from afe_service_tbl
    	public static final int SERVICES_MANAGER = 74;
    	public static final int MARKET_MANAGER = 18;
    	public static final int INDEX_LISTING = 215;
     	public static final int INDEX_INPUT = 112;
     	public static final int HISTORIC_PRICES = 19;
         	
//    	19	Historical Prices

    	
//    	387	Refresh Indexes	109	0	2	0	0	0	0	11/15/2015 03:08:03 AM	Refreshes Indexes		2
//    	69	Index Location Setup	101	0	13	0	0	0	0	11/15/2015 03:08:03 AM	Open the Index Location Setup window from the Admin Manager		8
//    	112	Index Input	109	0	2	0	0	0	0	11/15/2015 03:08:03 AM	Open the Index Input window for an index id		2
//    	213	Load Indexes	109	0	2	0	0	0	0	11/15/2015 03:08:03 AM	Opens a window to load indexes into memory		2
//    	215	Index Listing	109	0	2	0	0	0	0	11/15/2015 03:08:03 AM	Opens a window with a list of the indexes in the system		2
//    	419	Index Definition	109	0	2	0	0	0	0	11/15/2015 03:08:03 AM	Open the Index Definition Screen		2
//    	515	Index Info	101	0	13	0	0	0	0	11/15/2015 03:08:03 AM	Configure Index Info		8
//    	10034	Index/Vol Defn Rollback	109	0	2	2	0	0	0	11/15/2015 03:08:03 AM	Launch Index/Volatility Definition Rollback Window		2
//    	10049	Index Subgroup Info	127	0	13	0	0	0	0	11/15/2015 03:08:03 AM	Configure Index Subgroup Info		8

    }
    
    public static class FORMAT { 
        public static class JUSTIFY { 
        	public static final String RIGHT = "just=RIGHT";
        	public static final String CENTER = "just=CENTER";
        }
    	
    	public static final String DOUBLE_CLICK = "dblclick=TRUE";

    }
  	
    public static class LIB {            

    	public static final String VERSION_NUMBER = "V1.003 (19-Apr-2023)";

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
