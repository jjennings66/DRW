package com.drw.dashboard;
  


import com.drw.dashboard.DRW_TradeListDashboardLibrary.COMMON_FUNC;
import com.drw.dashboard.DRW_TradeListDashboardLibrary.FORMAT;
import com.drw.dashboard.DRW_TradeListDashboardLibrary.GUI;
import com.drw.dashboard.DRW_TradeListDashboardLibrary.GUICOLS;
import com.drw.dashboard.DRW_TradeListDashboardLibrary.GUICOLS.GUICREATE_TABLE;
import com.drw.dashboard.DRW_TradeListDashboardLibrary.IBUTTON;
import com.drw.dashboard.DRW_TradeListDashboardLibrary.ICALENDAR;
import com.drw.dashboard.DRW_TradeListDashboardLibrary.ICOMBOBOX;
import com.drw.dashboard.DRW_TradeListDashboardLibrary.ILABEL;
import com.drw.dashboard.DRW_TradeListDashboardLibrary.SUBSCRIPTION;
import com.drw.dashboard.DRW_TradeListReportsLibrary.COLS;
import com.drw.dashboard.DRW_TradeListReportsLibrary.CONST_VALUES;
import com.drw.dashboard.DRW_TradeListReportsLibrary.DEBUG_LOGFILE;
import com.drw.dashboard.DRW_TradeListReportsLibrary.LIB;
import com.drw.dashboard.DRW_TradeListReportsLibrary.REPORT;
import com.drw.dashboard.DRW_TradeListReportsLibrary.REPORT_NAME;
import com.olf.openjvs.IContainerContext;
import com.olf.openjvs.IScript;
import com.olf.openjvs.OCalendar;
import com.olf.openjvs.OException;
import com.olf.openjvs.PluginCategory;
import com.olf.openjvs.PluginType;
import com.olf.openjvs.Ref;
import com.olf.openjvs.Services;
import com.olf.openjvs.Str;
import com.olf.openjvs.Table;
import com.olf.openjvs.Util;
import com.olf.openjvs.enums.COL_TYPE_ENUM;
import com.olf.openjvs.enums.DATE_FORMAT;
import com.olf.openjvs.enums.SCRIPT_CATEGORY_ENUM;
import com.olf.openjvs.enums.SCRIPT_PANEL_CALLBACK_TYPE_ENUM;
import com.olf.openjvs.enums.SCRIPT_PANEL_REFRESH_ENUM;
import com.olf.openjvs.enums.SCRIPT_PANEL_WIDGET_TYPE_ENUM;
import com.olf.openjvs.enums.SCRIPT_TYPE_ENUM;
import com.olf.openjvs.enums.SHM_USR_TABLES_ENUM;
  
/* 

Name:  DRW_TradeListDashboardMainPanel.java

Deployment Instructions
Set this up as a 'Script Panel' in a 'Desktop'.

This is an interactive viewer for the DRW Trade Listing Reports

Revision History 
25-Apr-2023 Brian New script
   
*/


@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_TRADE_INPUT)
@PluginType(SCRIPT_TYPE_ENUM.MAIN_SCRIPT)
public class DRW_TradeListDashboardMainPanel implements IScript {
    
	// set this to true for normal usage
	// this is what lets this one script panel communicate with other script panels
	// putting this as a variable to help with debug and testing 
	static boolean bPublishMessageWithData = true;
  
    public void execute(IContainerContext context) throws OException
    {
    	String className = this.getClass().getSimpleName();
    	try {
    		
    		doEverything(context, className);
    		
    	} catch (Exception e) {
    		LIB.log("execute", className);
    	}
    }     
 
    public static void doEverything(IContainerContext context, String className) throws OException
    {
    	Table tExtraData = Util.NULL_TABLE;
    	
    	try {
        	
        	// We use returnt, not 'argt'
        	Table returnt = context.getReturnTable();
        	
        	int iCallBackType = returnt.scriptDataGetCallbackType();
        	SCRIPT_PANEL_CALLBACK_TYPE_ENUM callbackType = SCRIPT_PANEL_CALLBACK_TYPE_ENUM.fromInt(iCallBackType);

    		LIB.log("RUNNING, with callback type: " + callbackType.toString(), className);

        	boolean bCallBackTypeIsInit = false;
        	if (callbackType == SCRIPT_PANEL_CALLBACK_TYPE_ENUM.SCRIPT_PANEL_INIT) {
        		bCallBackTypeIsInit = true;
        	}
        	
        	if (bCallBackTypeIsInit == true) {
            	int iRunDate = OCalendar.today();
            	int iRunNumber = DEBUG_LOGFILE.getRunNumber(className);
            	GUI_HANDLER.INIT_HANDLER.doEverythingInit(iRunDate, iRunNumber, className);
        	}

        	// Do *NOT* do this if run for the first time, i.e., if 'init'
        	if (bCallBackTypeIsInit == false) {
        		
            	// Get Extra data
            	tExtraData = returnt.scriptDataGetExtraDataTable();
            	Table tExtraDataSubTable = LIB.safeGetTable(tExtraData, GUICOLS.PANEL.EXTRA_DATA_TABLE.SUB_TABLE, CONST_VALUES.ROW_TO_GET_OR_SET);
            	int iRunNumber = LIB.safeGetInt(tExtraDataSubTable, GUICOLS.PANEL.EXTRA_DATA_TABLE.SETTINGS.RUN_NUMBER, CONST_VALUES.ROW_TO_GET_OR_SET);
            	int iRunDate = LIB.safeGetInt(tExtraDataSubTable, GUICOLS.PANEL.EXTRA_DATA_TABLE.SETTINGS.RUN_DATE, CONST_VALUES.ROW_TO_GET_OR_SET);
            	String sRunDate = LIB.safeGetString(tExtraDataSubTable, GUICOLS.PANEL.EXTRA_DATA_TABLE.SETTINGS.RUN_DATE_STRING, CONST_VALUES.ROW_TO_GET_OR_SET);

            	if (callbackType == SCRIPT_PANEL_CALLBACK_TYPE_ENUM.SCRIPT_PANEL_PUSHBUTTON) {
            		GUI_HANDLER.BUTTON_HANDLER.doEverythingButton(returnt, tExtraData, iRunNumber, iRunDate, sRunDate, className);
            	} 
            	if (callbackType == SCRIPT_PANEL_CALLBACK_TYPE_ENUM.SCRIPT_PANEL_COMBOBOX) {
            		GUI_HANDLER.COMBOBOX_HANDLER.doEverythingComboBox(returnt, tExtraData, iRunNumber, iRunDate, sRunDate, className);
            	}
            	if (callbackType == SCRIPT_PANEL_CALLBACK_TYPE_ENUM.SCRIPT_PANEL_DBLCLICK) {
            		GUI_HANDLER.DOUBLE_CLICK_HANDLER.doEverythingDoubleClick(returnt, iRunDate, sRunDate, className);
            	}
            	if (callbackType == SCRIPT_PANEL_CALLBACK_TYPE_ENUM.SCRIPT_PANEL_CALENDAR) {
            		// Modify the *APPLY FILTERS* button to Red.  Not the 'Load Data' button
        			//GUI.modifyButtonWithMakeRed(returnt, BUTTON.BUTTON2, className);
            	}
            	if (callbackType == SCRIPT_PANEL_CALLBACK_TYPE_ENUM.SCRIPT_PANEL_TED) {
            		GUI_HANDLER.TED_HANDLER.doEverythingTED(returnt, iRunDate, sRunDate, className);
            	}
            	 
        	} // END  if (bCallBackTypeIsInit == false) 
    		 
    	} catch (Exception e) {
    		LIB.log("doEverything", className);
    	} 
    } 
 
    public static class MAIN_PANEL_PICK_LIST {  
	        
	        public static Table getInsTypePicklist(String className) throws OException {
	        	
	        	  Table tReturn = Util.NULL_TABLE;
	        	  try{
	        		  // TOxDO
	        		  // make sure to add all relevant ins types to this list
	        	 
	          		tReturn = GUICREATE_TABLE.PANEL.createComboBoxTable(className);

	          		boolean bGetValuesBasedOnHardCodedList = true;
	          		if (bGetValuesBasedOnHardCodedList == true) {

	          			// Pending
	          			//	          			toolset		ins_type	ins_type
//	          			Commodity	COMM-PHYS	48010
//	          			Commodity	COMM-EXCH	48035
	        
	          			String sSQL = "select id_number, name from instruments where id_number in (12100, 12101, 26001, 27001, 30201, 31201, 31202, 32007, 36001, 36004, 47002, 48010, 48640, 1000031, 1000032)";
	          			LIB.execISql(tReturn, sSQL, className);
	          		} else {
	          			String sSQL = "select id_number, name from instruments where toolset in (6, 15, 16, 32, 17, 18, 36, 10, 16, 15, 9)";
	          			LIB.execISql(tReturn, sSQL, className);
	          		}
          			
 	          		// grouping is COSMETIC
	          		tReturn.group(GUICOLS.PANEL.COMBOBOX.ITEM);
 
	          		// put 'all' at the top
	          		if (LIB.safeGetNumRows(tReturn) < 1) {
	          			tReturn.addRow();
	          		} else {
	          			tReturn.insertRowBefore(CONST_VALUES.ROW_TO_GET_OR_SET);
	          		}
	          		
	          		LIB.safeSetInt(tReturn, GUICOLS.PANEL.COMBOBOX.ITEM_ID, CONST_VALUES.ROW_TO_GET_OR_SET, CONST_VALUES.ALL.ID);
	              	LIB.safeSetString(tReturn, GUICOLS.PANEL.COMBOBOX.ITEM, CONST_VALUES.ROW_TO_GET_OR_SET, CONST_VALUES.ALL.NAME);
	           		 
	        	  } catch(Exception e ){               
	          		LIB.log("getInsTypePicklist", e, className);
	        	  } 
	        	  
	          	return tReturn;
	          }	  
	        
	        public static Table getPortfolioPicklist(String className) throws OException {
	        	
	        	  Table tReturn = Util.NULL_TABLE;
	        	  try{
	        	 
	          		tReturn = GUICREATE_TABLE.PANEL.createComboBoxTable(className);

          			String sSQL = "SELECT id_number, name FROM portfolio";
          			LIB.execISql(tReturn, sSQL, className);
  	                  
	          		// grouping is COSMETIC
	          		tReturn.group(GUICOLS.PANEL.COMBOBOX.ITEM);
 
	          		// put 'all' at the top
	          		if (LIB.safeGetNumRows(tReturn) < 1) {
	          			tReturn.addRow();
	          		} else {
	          			tReturn.insertRowBefore(CONST_VALUES.ROW_TO_GET_OR_SET);
	          		}
	          		
	          		LIB.safeSetInt(tReturn, GUICOLS.PANEL.COMBOBOX.ITEM_ID, CONST_VALUES.ROW_TO_GET_OR_SET, CONST_VALUES.ALL.ID);
	              	LIB.safeSetString(tReturn, GUICOLS.PANEL.COMBOBOX.ITEM, CONST_VALUES.ROW_TO_GET_OR_SET, CONST_VALUES.ALL.NAME);
	           		 
	        	  } catch(Exception e ){               
	          		LIB.log("getPortfolioPicklist", e, className);
	        	  } 
	        	  
	          	return tReturn;
	          }	  
  
        public static Table getCurrencyPicklist(String className) throws OException {
        	
        	  Table tReturn = Util.NULL_TABLE;
        	  try{
          		
          		tReturn = GUICREATE_TABLE.PANEL.createComboBoxTable(className);
   
          		{
          			// get items from DB
          			String ID_COL_NAME = "id_number";
          			Table tListOfItems = Table.tableNew();
          			LIB.safeAddCol(tListOfItems, ID_COL_NAME, COL_TYPE_ENUM.COL_INT);
          			String sSQL = "select id_number from currency WHERE precious_metal = 1";
          			LIB.execISql(tListOfItems, sSQL, className);
   
          			int iNumRows = LIB.safeGetNumRows(tListOfItems);
          			for (int iCounter = 1; iCounter <= iNumRows; iCounter++) {
          				int iValue = LIB.safeGetInt(tListOfItems, ID_COL_NAME, iCounter);
          				String sValue = Ref.getName(SHM_USR_TABLES_ENUM.CURRENCY_TABLE, iValue);
          				int iMaxRow = tReturn.addRow();
          				LIB.safeSetInt(tReturn, GUICOLS.PANEL.COMBOBOX.ITEM_ID, iMaxRow, iValue);
          				LIB.safeSetString(tReturn, GUICOLS.PANEL.COMBOBOX.ITEM, iMaxRow, sValue);
          			}
                      
          			tListOfItems = LIB.destroyTable(tListOfItems);
          		}
                  
          		// grouping is COSMETIC
          		tReturn.group(GUICOLS.PANEL.COMBOBOX.ITEM);

          		// put 'all' at the top
          		if (LIB.safeGetNumRows(tReturn) < 1) {
          			tReturn.addRow();
                  } else {
                	  tReturn.insertRowBefore(CONST_VALUES.ROW_TO_GET_OR_SET);
                  }
          		LIB.safeSetInt(tReturn, GUICOLS.PANEL.COMBOBOX.ITEM_ID, CONST_VALUES.ROW_TO_GET_OR_SET, CONST_VALUES.ALL.ID);
              	LIB.safeSetString(tReturn, GUICOLS.PANEL.COMBOBOX.ITEM, CONST_VALUES.ROW_TO_GET_OR_SET, CONST_VALUES.ALL.NAME);
           		
        	  } catch(Exception e ){               
          		LIB.log("getCurrencyPicklist", e, className);
        	  } 
          	return tReturn;
          }
        
        public static Table getReportNamePicklist(String className) throws OException {
        	
        	  Table tReturn = Util.NULL_TABLE;
        	  try{
          		
          		tReturn = GUICREATE_TABLE.PANEL.createComboBoxTable(className); 
          		LIB.safeAddCol(tReturn, GUICOLS.PANEL.COMBOBOX.EXTRA.DESCRIPTION, COL_TYPE_ENUM.COL_STRING);
          		
          		for (REPORT_NAME item : REPORT_NAME.values()) {
          			// only include reports that are to show the user
          			// some of the reports are not 'real' reports... and instead are Script Panel (Pane) names
          			if (item.bIncludeInGuiPickListFlag() == true) {
              			int iMaxRow = tReturn.addRow();
              			
              			LIB.safeSetInt(tReturn, GUICOLS.PANEL.COMBOBOX.ITEM_ID, iMaxRow, item.getValue());
              			LIB.safeSetString(tReturn, GUICOLS.PANEL.COMBOBOX.ITEM, iMaxRow, item.getName());
              			LIB.safeSetString(tReturn, GUICOLS.PANEL.COMBOBOX.EXTRA.DESCRIPTION, iMaxRow, item.getDescriptionToShowEndUser());
          			}
          		}
          		
          		// We'll assume that the ID value is also the sort order
          		tReturn.group(GUICOLS.PANEL.COMBOBOX.ITEM);
          		 
        	  } catch(Exception e ){               
          		LIB.log("getReportNamePicklist", e, className);
        	  } 
          	return tReturn;
          }
   

		// For the Metal MARKET position Dashboard, only allow Live Data
		public static Table getCurrentDataSourcePickList(String className) throws OException {

			Table tReturn = Util.NULL_TABLE;
			try {
				
				tReturn = GUICREATE_TABLE.PANEL.createComboBoxTable(className);
 				
				// now add in Live Data
				int iMaxRow = tReturn.addRow();
				// set to a very large negative number to have this sort at the top
				LIB.safeSetInt(tReturn, GUICOLS.PANEL.COMBOBOX.ITEM_ID, iMaxRow, -999999); 
        		LIB.safeSetString(tReturn, GUICOLS.PANEL.COMBOBOX.ITEM, iMaxRow, CONST_VALUES.LIVE_DATA);
            	   
			} catch (Exception e) {
				LIB.log("getCurrentDataSourcePickList", e, className);
			}
			return tReturn;
		}

		public static Table getYesNoPicklist(String className) throws OException {

			Table tReturn = Util.NULL_TABLE;
			try {

				tReturn = GUICREATE_TABLE.PANEL.createComboBoxTable(className);
				
				tReturn.addRowsWithValues("0,(No)");
				tReturn.addRowsWithValues("1,(Yes)");
				
			} catch (Exception e) {
				LIB.log("getYesNoPicklist", e, className);
			}
			return tReturn;
		} 
    }

    public static class GUI_HANDLER { 

         public static class BUTTON_HANDLER { 
        	
            public static void doEverythingButton(Table tScriptPanel, Table tExtraData,
            		int iRunNumber, int iRunDate, String sRunDate, 
            		String className) throws OException {
            	
            	try{
            		String sButtonName = tScriptPanel.scriptDataGetCallbackName();
            		String sButtonLabel = tScriptPanel.scriptDataGetWidgetString(sButtonName);
            		LIB.log("ButtonName: " + sButtonName + ", ButtonLabel: " + sButtonLabel, className);
 
            		boolean bHandledFlag = false;
               		if (Str.equal(sButtonName, BUTTON.BUTTON1.getName()) == CONST_VALUES.VALUE_OF_TRUE) {
               			
            			FUNC.getAndRefreshDataIntoScriptPanel(tScriptPanel, tExtraData, iRunNumber, iRunDate, sRunDate, className);

            			if (bPublishMessageWithData == true) {
            				// I don't think we need to make a copy of this. We'll see
            				// this table, i.e., Extra Data, should have anything and everything that we might want to send, so just send it all
            				try {
                				Services.publishLocal(tExtraData, SUBSCRIPTION.ALL_DATA.getName());
            				} catch (Exception e) {
            					LIB.log("doEverythingButton.Services.publishLocal", e, className);
            				}
            			}
            			
            			// Loading data sets back to original color
            			GUI.modifyButtonWithUndoMakeRed(tScriptPanel, BUTTON.BUTTON1, className); 
            			
            			bHandledFlag = true;
            		}
               		 
               		// Reset Filter Button
               		if (Str.equal(sButtonName, BUTTON.BUTTON3.getName()) == CONST_VALUES.VALUE_OF_TRUE) {
               			  
                		// Modify the *Load Data* button to Red.  
            			GUI.modifyButtonWithMakeRed(tScriptPanel, BUTTON.BUTTON1, className);
            			
            			// Set all Filter Combo Boxes to All
            			COMBOBOX_HANDLER.HELPER.updateComboBoxSelectedValuesToAll(tScriptPanel, COMBOBOX.COMBOBOX30, className);
            			COMBOBOX_HANDLER.HELPER.updateComboBoxSelectedValuesToAll(tScriptPanel, COMBOBOX.COMBOBOX31, className);
            			COMBOBOX_HANDLER.HELPER.updateComboBoxSelectedValuesToAll(tScriptPanel, COMBOBOX.COMBOBOX32, className);
            			
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
            		
            		if (Str.iEqual(sColName, COLS.REPORT.DEAL_NUM_COL_NAME) == CONST_VALUES.VALUE_OF_TRUE) {
            			int iDealNum = tScriptPanel.getCellInt(sColName, iRowNum);
            			COMMON_FUNC.showTransactionGivenDealNum(iDealNum, className);
            			bHandledFlag = true;
            		}
//            		 
//            		if (Str.iEqual(sColName, COLS.REPORT.FORMATTED_REPORT.IMS_OC_ACTIVITY_LOG_DATA.request_data_cell) == CONST_VALUES.VALUE_OF_TRUE) {
//            			Table tData = LIB.safeGetTable(tScriptPanel, COLS.REPORT.FORMATTED_REPORT.IMS_OC_ACTIVITY_LOG_DATA.request_data, iRowNum); 
//            			String sJobID = LIB.safeGetString(tScriptPanel, COLS.REPORT.FORMATTED_REPORT.IMS_OC_ACTIVITY_LOG_DATA.job_id, iRowNum);
//            			LIB.viewTable(tData, "IMS Trade Details as Sent by IMS for Job: " + sJobID);
//            			bHandledFlag = true;
//            		}
//            		
//            		if (Str.iEqual(sColName, COLS.REPORT.FORMATTED_REPORT.IMS_OC_ACTIVITY_LOG_DATA.response_data_cell) == CONST_VALUES.VALUE_OF_TRUE) {
//            			Table tData = LIB.safeGetTable(tScriptPanel, COLS.REPORT.FORMATTED_REPORT.IMS_OC_ACTIVITY_LOG_DATA.response_data, iRowNum); 
//            			String sJobID = LIB.safeGetString(tScriptPanel, COLS.REPORT.FORMATTED_REPORT.IMS_OC_ACTIVITY_LOG_DATA.job_id, iRowNum);
//            			LIB.viewTable(tData, "Response by OpenLink for Job: " + sJobID);
//            			bHandledFlag = true;
//            		}
            		 
            		if (bHandledFlag == false) {
            			LIB.log("WARNING: This doubleclick was not handled, Column: " + "'" + sColName + "'" + ", Row Number: " + iRowNum, className);
            		}
            		  
            	} catch(Exception e ){               
            		LIB.log("doEverythingTimer", e, className);
            	} 
            }
        }
        

        public static class TED_HANDLER { 
 
        	  public static void doEverythingTED(Table tScriptPanel,
              		int iRunDate, String sRunDate, 
              		String className) throws OException {
              	
              	try{
              		 
              		// the name of the TED (Text Edit) the user modified
              		String sTEDName          = tScriptPanel.scriptDataGetCallbackName();

              		boolean bHandledFlag = false;
 
              		// This is the Show All Columns or not
              		// This will push to the sub panels
              		// Note that this *is* the name of 'ComboBox4'. ComboBoxes can work as a TED as well as a ComboBox, but only if they are multi-select
              		if (Str.equal(sTEDName, COMBOBOX.COMBOBOX5.getName()) == CONST_VALUES.VALUE_OF_TRUE) {
              			if (bPublishMessageWithData == true) {
              				COMBOBOX_HANDLER.HELPER.publishDataToSubPanelsBasedOnShowAllColsComboboxChanging(tScriptPanel, className);
              			}
              			bHandledFlag = true;
              		}
                       
              		if (bHandledFlag == false) {
              			LIB.log("WARNING: This TED (Text Edit) was not handled. Name is: " + "'" + sTEDName + "'", className);
              		} 
                      
              	} catch(Exception e ){               
              		LIB.log("doEverythingTED", e, className);
              	} 
              }
        	  
        } // END class TED_HANDLER

        public static class COMBOBOX_HANDLER { 
        	
            public static class HELPER { 
            	
            	 public static void getReportTypeAndGetSourceDataFromExtraDataTableAndPopulatePanel(Table tScriptPanel, Table tExtraData,
            			 int iRunNumber, String className) throws OException {
                 	
                 	try{
                		
                 		// Get the report that the user has selected based on the PickList/ComboBox
            			String sReportFromComboBox = tScriptPanel.scriptDataGetWidgetString(COMBOBOX.COMBOBOX20.getName());
            			
            			// Default to this report 
            			REPORT_NAME reportName = REPORT_NAME.TRADE_LIST;

            			// Use this loop to iterate through the enums and figure out what report we should show based on the 
            			// user-selected value from the ComboBox
            			for (REPORT_NAME reportItem: REPORT_NAME.values()) {
            				String sReportNameFromItem = reportItem.getName();
            				if (Str.iEqual(sReportNameFromItem, sReportFromComboBox) == 1) {
                    			reportName = reportItem;
            				}
            			}
            			
            			COMMON_FUNC.populatePanelBasedOnReportTypeAndGetSourceDataFromExtraDataTable(tScriptPanel, tExtraData, reportName, iRunNumber, className);
            			
                 	} catch(Exception e ){               
                 		LIB.log("getReportTypeAndGetSourceDataFromExtraDataTableAndPopulatePanel", e, className);
                 	} 
                 }
            	 
                public static void checkComboBoxSelectedValuesAndSetToAllIfNeeded(Table tScriptPanel, Table tComboBoxFullMenu, Table tComboBoxReturnMenu, COMBOBOX comboBox,
                		String className) throws OException {
                	
                	try{

                		boolean bUpdateTheSelectedValueOfTheComboBoxToAll = false;

                        int iNumRowsSelectedByUser = LIB.safeGetNumRows(tComboBoxReturnMenu);

                    	if (iNumRowsSelectedByUser < 1) {
                    		LIB.log("In the special logic section for Combobox " + comboBox.getLabel() + " where the user did not select anything.  Will set to default of 'All'", className);
                    		bUpdateTheSelectedValueOfTheComboBoxToAll = true;
                    	}
                    	
                    	if (iNumRowsSelectedByUser >= 2) {
                    		// if All is in the list at all, and there is more than one row, then we will just set to 'All', since it is redundant 
                    		int iColNumToUse = 1;
                    		if (tComboBoxReturnMenu.unsortedFindInt(iColNumToUse, CONST_VALUES.ALL.LOCATION_IN_PICK_LIST) >= 1) {
                        		LIB.log("In the special logic section for Combobox " + comboBox.getLabel() + " where the user selected 'All' and another value.  Will set to just 'All'", className);
                    			bUpdateTheSelectedValueOfTheComboBoxToAll = true;
                    		}
                    	}
                    	 
                    	if (bUpdateTheSelectedValueOfTheComboBoxToAll == true) {
                        	updateComboBoxSelectedValuesToAll(tScriptPanel, tComboBoxFullMenu, comboBox, className);
                    	}
                        
//                    	if (bUpdateTheSelectedValueOfTheComboBoxToAll == true) {
//                    		Table tComboBoxSelectedItems = Table.tableNew();
//                    		// column name doesn't matter, just needs one int column
//                    		final String COL_NAME_TO_USE = "col1";
//                    		LIB.safeAddCol(tComboBoxSelectedItems, "col1", COL_TYPE_ENUM.COL_INT);
//                    		int iMaxRow = tComboBoxSelectedItems.addRow();
//                    		// pick the first row
//                    		LIB.safeSetInt(tComboBoxSelectedItems, COL_NAME_TO_USE, iMaxRow, CONST_VALUES.ALL.LOCATION_IN_PICK_LIST);
//                    		
//                    		// Based on testing, it turns out that you for sure need to call *BOTH* 'scriptDataModifyWidget' and 'scriptDataSetWidgetMenu'
//                    		// originally it seemed like you could to just 'scriptDataSetWidgetMenu', but when we tested it, the label/display wasn't updated correctly
//                    		String widget_options = FORMAT.MULTISELECT_AND_LABEL + CONST_VALUES.ALL.NAME;
//                    		tScriptPanel.scriptDataModifyWidget(comboBox.getName(), 
//            	    				SCRIPT_PANEL_WIDGET_TYPE_ENUM.SCRIPT_PANEL_COMBOBOX_WIDGET.toInt(), 
//            	    				comboBox.getPosition(), widget_options);
//                    		
//                    		// pass in tComboBoxFullMenu, which we got from above, i.e., no need to get it again
//            				tScriptPanel.scriptDataSetWidgetMenu(comboBox.getName(), tComboBoxFullMenu, tComboBoxSelectedItems);
//
//            				// this isn't needed, it turns out, so keep this at false in production
//            				boolean bForceScreenRefresh = false;
//            				if (bForceScreenRefresh == true) {
//            					tScriptPanel.scriptDataSetRefreshType(SCRIPT_PANEL_REFRESH_ENUM.SCRIPT_PANEL_REFRESH_ALL.toInt());
//            				}
//            				
//            				tComboBoxSelectedItems = LIB.destroyTable(tComboBoxSelectedItems);
//                    	}
                        
                	} catch(Exception e ){               
                		LIB.log("checkComboBoxSelectedValuesAndSetToAllIfNeeded", e, className);
                	} 
                }
                
               

                public static void updateComboBoxSelectedValuesToAll(Table tScriptPanel, Table tComboBoxFullMenu, COMBOBOX comboBox,
                		String className) throws OException {
                	
                //	Table tComboBoxFullMenu = Util.NULL_TABLE;
                	try{

                		boolean bUpdateTheSelectedValueOfTheComboBoxToAll = true;

                		
                		// The full menu with all of the items a user can select
                	//	tComboBoxFullMenu      = tScriptPanel.scriptDataGetWidgetMenu(comboBox.getName());

                      
                    	if (bUpdateTheSelectedValueOfTheComboBoxToAll == true) {
                    		

                    	//	updateComboBoxSelectedValuesToAll(tScriptPanel, tComboBoxFullMenu, comboBox, className);
                             
                    		Table tComboBoxSelectedItems = Table.tableNew();
                    		// column name doesn't matter, just needs one int column
                    		final String COL_NAME_TO_USE = "col1";
                    		LIB.safeAddCol(tComboBoxSelectedItems, "col1", COL_TYPE_ENUM.COL_INT);
                    		int iMaxRow = tComboBoxSelectedItems.addRow();
                    		// pick the first row
                    		LIB.safeSetInt(tComboBoxSelectedItems, COL_NAME_TO_USE, iMaxRow, CONST_VALUES.ALL.LOCATION_IN_PICK_LIST);
                    		
                    		// Based on testing, it turns out that you for sure need to call *BOTH* 'scriptDataModifyWidget' and 'scriptDataSetWidgetMenu'
                    		// originally it seemed like you could to just 'scriptDataSetWidgetMenu', but when we tested it, the label/display wasn't updated correctly
                    		String widget_options = FORMAT.MULTISELECT_AND_LABEL + CONST_VALUES.ALL.NAME;
                    		tScriptPanel.scriptDataModifyWidget(comboBox.getName(), 
            	    				SCRIPT_PANEL_WIDGET_TYPE_ENUM.SCRIPT_PANEL_COMBOBOX_WIDGET.toInt(), 
            	    				comboBox.getPosition(), widget_options);
                    		
                    		// pass in tComboBoxFullMenu, which we got from above, i.e., no need to get it again
            				tScriptPanel.scriptDataSetWidgetMenu(comboBox.getName(), tComboBoxFullMenu, tComboBoxSelectedItems);

            				// this isn't needed, it turns out, so keep this at false in production
            				boolean bForceScreenRefresh = false;
            				if (bForceScreenRefresh == true) {
            					tScriptPanel.scriptDataSetRefreshType(SCRIPT_PANEL_REFRESH_ENUM.SCRIPT_PANEL_REFRESH_ALL.toInt());
            				}
            				
            				tComboBoxSelectedItems = LIB.destroyTable(tComboBoxSelectedItems);
                    	}
                        
                	} catch(Exception e ){               
                		LIB.log("updateComboBoxSelectedValuesToAll", e, className);
                	} 
                	
                //	tComboBoxFullMenu = LIB.destroyTable(tComboBoxFullMenu);
                }
                
                public static void updateComboBoxSelectedValuesToAll(Table tScriptPanel, COMBOBOX comboBox,
                		String className) throws OException {
                	
                	Table tComboBoxFullMenu = Util.NULL_TABLE;
                	try{

                	//	boolean bUpdateTheSelectedValueOfTheComboBoxToAll = true;

                		
                		// The full menu with all of the items a user can select
                		tComboBoxFullMenu      = tScriptPanel.scriptDataGetWidgetMenu(comboBox.getName());
                		updateComboBoxSelectedValuesToAll(tScriptPanel, tComboBoxFullMenu, comboBox, className);
                       
//                    	if (bUpdateTheSelectedValueOfTheComboBoxToAll == true) {
//                             
//                    		Table tComboBoxSelectedItems = Table.tableNew();
//                    		// column name doesn't matter, just needs one int column
//                    		final String COL_NAME_TO_USE = "col1";
//                    		LIB.safeAddCol(tComboBoxSelectedItems, "col1", COL_TYPE_ENUM.COL_INT);
//                    		int iMaxRow = tComboBoxSelectedItems.addRow();
//                    		// pick the first row
//                    		LIB.safeSetInt(tComboBoxSelectedItems, COL_NAME_TO_USE, iMaxRow, CONST_VALUES.ALL.LOCATION_IN_PICK_LIST);
//                    		
//                    		// Based on testing, it turns out that you for sure need to call *BOTH* 'scriptDataModifyWidget' and 'scriptDataSetWidgetMenu'
//                    		// originally it seemed like you could to just 'scriptDataSetWidgetMenu', but when we tested it, the label/display wasn't updated correctly
//                    		String widget_options = FORMAT.MULTISELECT_AND_LABEL + CONST_VALUES.ALL.NAME;
//                    		tScriptPanel.scriptDataModifyWidget(comboBox.getName(), 
//            	    				SCRIPT_PANEL_WIDGET_TYPE_ENUM.SCRIPT_PANEL_COMBOBOX_WIDGET.toInt(), 
//            	    				comboBox.getPosition(), widget_options);
//                    		
//                    		// pass in tComboBoxFullMenu, which we got from above, i.e., no need to get it again
//            				tScriptPanel.scriptDataSetWidgetMenu(comboBox.getName(), tComboBoxFullMenu, tComboBoxSelectedItems);
//
//            				// this isn't needed, it turns out, so keep this at false in production
//            				boolean bForceScreenRefresh = false;
//            				if (bForceScreenRefresh == true) {
//            					tScriptPanel.scriptDataSetRefreshType(SCRIPT_PANEL_REFRESH_ENUM.SCRIPT_PANEL_REFRESH_ALL.toInt());
//            				}
//            				
//            				tComboBoxSelectedItems = LIB.destroyTable(tComboBoxSelectedItems);
//                    	}
                        
                	} catch(Exception e ){               
                		LIB.log("updateComboBoxSelectedValuesToAll", e, className);
                	} 
                	
                	tComboBoxFullMenu = LIB.destroyTable(tComboBoxFullMenu);
                }
                 
                public static void publishDataToSubPanelsBasedOnShowAllColsComboboxChanging(Table tScriptPanel,  
                		String className) throws OException {
                	
                	try{

    					Table tExtraData = tScriptPanel.scriptDataGetExtraDataTable();
    					Table tRawLeaseData = LIB.safeGetTable(tExtraData, GUICOLS.PANEL.EXTRA_DATA_TABLE.RAW_DATA_BY_UNIQUE_KEY_AND_SETTINGS, CONST_VALUES.ROW_TO_GET_OR_SET);
    					
    					int iShowAllColumns = CONST_VALUES.VALUE_OF_FALSE;
    	        		try {
    	            		String sShowAllColumns = tScriptPanel.scriptDataGetWidgetString(COMBOBOX.COMBOBOX5.getName());
    	            		if (Str.containsSubString(sShowAllColumns.toUpperCase(), "Y") == 1) {
    	            			iShowAllColumns = CONST_VALUES.VALUE_OF_TRUE;
    	            		}
    	        		} catch (Exception e) {
    	        			// don't log this
    	        		}
    					
    					LIB.safeSetInt(tRawLeaseData, COLS.REPORT.RAW_BY_UNIQUE_KEY_REPORT_DATA.SETTING.SHOW_ALL_COLUMNS_FLAG, CONST_VALUES.ROW_TO_GET_OR_SET, iShowAllColumns);

        				try {
            				Services.publishLocal(tExtraData, SUBSCRIPTION.ALL_DATA.getName());
        				} catch (Exception e) {
        					LIB.log("publishDataToSubPanelsBasedOnShowAllColsComboboxChanging: Services.publishLocal", e, className);
        				}
                        
                	} catch(Exception e ){               
                		LIB.log("publishDataToSubPanelsBasedOnShowAllColsComboboxChanging", e, className);
                	} 
                }
            } // end class HELPER
 
            public static void doEverythingComboBox(Table tScriptPanel,
            		Table tExtraData, int iRunNumber,
            		int iRunDate, String sRunDate, 
            		String className) throws OException {
            	
            	try{
            		
            		boolean bMarkLoadButtonRed = false; 
            		// the name of the combobox the user modified
            		String sComboBoxName          = tScriptPanel.scriptDataGetCallbackName();
                    // a one column, one row table with the row number of the item the user selected
                    Table tComboBoxReturnMenu    = tScriptPanel.scriptDataGetWidgetMenuSelect(sComboBoxName);
                    // in multi select pick-lists, this can be 0, meaning no rows selected
					@SuppressWarnings("unused")
					int iNumRowsSelectedByUser = LIB.safeGetNumRows(tComboBoxReturnMenu);
                    // The full menu with all of the items a user can select
                    Table tComboBoxFullMenu      = tScriptPanel.scriptDataGetWidgetMenu(sComboBoxName);
                      
                    boolean bHandledFlag = false;
                    // This is the Show Trades By Which Date List List
                    if (Str.equal(sComboBoxName, COMBOBOX.COMBOBOX1.getName()) == CONST_VALUES.VALUE_OF_TRUE) {
                    	bMarkLoadButtonRed = true;
                    	bHandledFlag = true;
                    }
  
                    // This is the Currency List
                    COMBOBOX comboBox = COMBOBOX.COMBOBOX31;
                    if (Str.equal(sComboBoxName, comboBox.getName()) == CONST_VALUES.VALUE_OF_TRUE) {
                    	HELPER.checkComboBoxSelectedValuesAndSetToAllIfNeeded(tScriptPanel, tComboBoxFullMenu, tComboBoxReturnMenu, comboBox, className);
                    	bMarkLoadButtonRed = true;
                    	bHandledFlag = true;
                    }
                     
                    // This is the Portfolio List
                    comboBox = COMBOBOX.COMBOBOX30;
                    if (Str.equal(sComboBoxName, comboBox.getName()) == CONST_VALUES.VALUE_OF_TRUE) {
                    	HELPER.checkComboBoxSelectedValuesAndSetToAllIfNeeded(tScriptPanel, tComboBoxFullMenu, tComboBoxReturnMenu, comboBox, className);
                    	bMarkLoadButtonRed = true;
                    	bHandledFlag = true;
                    }

                    // This is the Ins Type List
                    comboBox = COMBOBOX.COMBOBOX32;
                    if (Str.equal(sComboBoxName, comboBox.getName()) == CONST_VALUES.VALUE_OF_TRUE) {
                    	HELPER.checkComboBoxSelectedValuesAndSetToAllIfNeeded(tScriptPanel, tComboBoxFullMenu, tComboBoxReturnMenu, comboBox, className);
                    	bMarkLoadButtonRed = true;
                    	bHandledFlag = true;
                    } 
                     
                    // This is the Show All Columns or not
                    // This will push to the sub panels
                    if (Str.equal(sComboBoxName, COMBOBOX.COMBOBOX5.getName()) == CONST_VALUES.VALUE_OF_TRUE) {
            			if (bPublishMessageWithData == true) {
            				HELPER.publishDataToSubPanelsBasedOnShowAllColsComboboxChanging(tScriptPanel, className);
            			}
                    	HELPER.getReportTypeAndGetSourceDataFromExtraDataTableAndPopulatePanel(tScriptPanel, tExtraData, iRunNumber, className);

                    	bHandledFlag = true;
                    }   
                    
                    // This is the Current Data Source List
                    if (Str.equal(sComboBoxName, COMBOBOX.COMBOBOX15.getName()) == CONST_VALUES.VALUE_OF_TRUE) {
                    	bMarkLoadButtonRed = true;
                    	bHandledFlag = true;
                    }
                      
                    // COMBOBOX20 = 'Report Name'
                    if (Str.equal(sComboBoxName, COMBOBOX.COMBOBOX20.getName()) == CONST_VALUES.VALUE_OF_TRUE) {
                    	
            			// This will key which report to display based on the Report Name selected (by the user) in ComboBox1
                    	HELPER.getReportTypeAndGetSourceDataFromExtraDataTableAndPopulatePanel(tScriptPanel, tExtraData, iRunNumber, className);
                    	bHandledFlag = true;
                    } 
                    
            		if (bHandledFlag == false) {
            			LIB.log("WARNING: This ComboBox was not handled. Name is: " + "'" + sComboBoxName + "'", className);
            		}
            		
            		if (bMarkLoadButtonRed == true) {
            			GUI.modifyButtonWithMakeRed(tScriptPanel, BUTTON.BUTTON1, className);
            		} 
            		
            	} catch(Exception e ){               
            		LIB.log("doEverythingComboBox", e, className);
            	} 
            }
           
        }
         
        public static class INIT_HANDLER { 
         
            public static void doEverythingInit(int iRunDate, int iRunNumber, String className) throws OException {

            	Table tScriptPanel = Util.NULL_TABLE;
            	Table tRawReport = Util.NULL_TABLE;
            	try{
            		String sRunDate = OCalendar.formatJd(iRunDate, DATE_FORMAT.DATE_FORMAT_DMLY_NOSLASH); 
            		
            		// Just create an empty table pointer.  All columns or rows get added later, after the Init, e.g., by the Load pushbutton
            		tScriptPanel = Table.tableNew();
            	
            		// Add buttons
            		// set to add them in a loop, for no particular reason, just to show off
            		boolean bAddAllButtonsInLoopFlag = true;
            		if (bAddAllButtonsInLoopFlag == true) {
                		for (BUTTON myButton : BUTTON.values()) {
                			GUI.addButton(tScriptPanel, myButton, className);
                		}
            		} else {
                		GUI.addButton(tScriptPanel, BUTTON.BUTTON1, className);
                	//	GUI.addButton(tScriptPanel, BUTTON.BUTTON2, className);
                		GUI.addButton(tScriptPanel, BUTTON.BUTTON3, className);
                		GUI.addButton(tScriptPanel, BUTTON.DEBUG_INFO, className);
            		}
            	
            		// Add Stand alone labels
            		//GUI.addLabel(tScriptPanel, LABEL.LABEL5, className);
            		//GUI.addLabel(tScriptPanel, LABEL.LABEL6, className);
            		
            		// special labels to add
	          		GUI.addLabel(tScriptPanel, LABEL.LABEL9, className);
            		GUI.addLabelWithSetTextToShowUser(tScriptPanel, LABEL.LABEL10, COMMON_FUNC.getDateTimeForLastUpdatedDateTime(className), className);
            		
            		// Add Calendar select widgets
            	//	GUI.addCalendar(tScriptPanel, CALENDAR.CALENDAR1, className);
            	//	GUI.addCalendar(tScriptPanel, CALENDAR.CALENDAR2, className);
            		
            		// Add Combo Boxes and associated labels when applicable
            		   
            		{
            			// 'Show All' Cols Yes/No flag
            			GUI.addLabel(tScriptPanel, LABEL.LABEL11, className);
                		Table tComboBoxMenu = MAIN_PANEL_PICK_LIST.getYesNoPicklist(className);
                		String sDefaultValue = "No"; 
                		GUI.addSingleSelectComboxBox(tScriptPanel, COMBOBOX.COMBOBOX5, tComboBoxMenu, sDefaultValue, className);
                		// OpenLink makes a copy of tComboBoxMenu, so OK to delete it
                		tComboBoxMenu = LIB.destroyTable(tComboBoxMenu);
            		}  
   
            		{
            			// Add 'Current Data Source' Picklist
                		Table tComboBoxMenu = MAIN_PANEL_PICK_LIST.getCurrentDataSourcePickList(className);
                		//String sDefaultValue =  CURRENT_DATA_SOURCE.TODAY_PRELIM_EOD.getName(); 
                		// Group by ID number, which is the negative
                		tComboBoxMenu.group(GUICOLS.PANEL.COMBOBOX.ITEM_ID);
                		
                		// Default to the first row
                		int iDefaultRowToGet = 1;
                		// if more than one row, then default to the second row
                		// the first row would be live data and we do not want that as the default
                		if (LIB.safeGetNumRows(tComboBoxMenu) >= 2) {
                			iDefaultRowToGet = 2;
                		}
                		
                		boolean bAddLiveData = true;
                		if (bAddLiveData == false) {
                			tComboBoxMenu.delRow(1);
                			iDefaultRowToGet = 1;
                		}
                		 
                		String sDefaultValue = LIB.safeGetString(tComboBoxMenu, GUICOLS.PANEL.COMBOBOX.ITEM, iDefaultRowToGet);
                		
                		GUI.addSingleSelectComboxBox(tScriptPanel, COMBOBOX.COMBOBOX15, tComboBoxMenu, sDefaultValue, className);
                		// OpenLink makes a copy of tComboBoxMenu, so OK to delete it
                		tComboBoxMenu = LIB.destroyTable(tComboBoxMenu);
            		}  

            		// 'Data' label
        			GUI.addLabel(tScriptPanel, LABEL.LABEL16, className);

            		{
            			// Report Name
                		GUI.addLabel(tScriptPanel, LABEL.LABEL20, className);
                		
                		Table tComboBoxMenu = MAIN_PANEL_PICK_LIST.getReportNamePicklist(className);
                		
                		String sDefaultValue = REPORT_NAME.TRADE_LIST.getName();
                		
                		GUI.addSingleSelectComboxBox(tScriptPanel, COMBOBOX.COMBOBOX20, tComboBoxMenu, sDefaultValue, className);
                		// OpenLink makes a copy of tComboBoxMenu, so OK to delete it
                		tComboBoxMenu = LIB.destroyTable(tComboBoxMenu);
            		}  
  
            		// Portfolio Filter 
            		{
        				GUI.addLabel(tScriptPanel, LABEL.LABEL30, className);
                		Table tComboBoxMenu = MAIN_PANEL_PICK_LIST.getPortfolioPicklist(className);
                		String sDefaultValue = CONST_VALUES.ALL.NAME;
                		GUI.addSingleSelectComboxBox(tScriptPanel, COMBOBOX.COMBOBOX30, tComboBoxMenu, sDefaultValue, className);
                		// OpenLink makes a copy of tComboBoxMenu, so OK to delete it
                		tComboBoxMenu = LIB.destroyTable(tComboBoxMenu);
            		}  

            		// Metal Filter 
            		{
        				GUI.addLabel(tScriptPanel, LABEL.LABEL31, className);
                		Table tComboBoxMenu = MAIN_PANEL_PICK_LIST.getCurrencyPicklist(className);
                		String sDefaultValue = CONST_VALUES.ALL.NAME;
                		GUI.addSingleSelectComboxBox(tScriptPanel, COMBOBOX.COMBOBOX31, tComboBoxMenu, sDefaultValue, className);
                		// OpenLink makes a copy of tComboBoxMenu, so OK to delete it
                		tComboBoxMenu = LIB.destroyTable(tComboBoxMenu);
            		}  
            		   
            		// Ins Type Filter 
            		{
        				GUI.addLabel(tScriptPanel, LABEL.LABEL32, className);
                		Table tComboBoxMenu = MAIN_PANEL_PICK_LIST.getInsTypePicklist(className);
                		String sDefaultValue = CONST_VALUES.ALL.NAME;
                		GUI.addMultiSelectComboxBox(tScriptPanel, COMBOBOX.COMBOBOX32, tComboBoxMenu, sDefaultValue, className);
                		// OpenLink makes a copy of tComboBoxMenu, so OK to delete it
                		tComboBoxMenu = LIB.destroyTable(tComboBoxMenu);
            		}  
 
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
                		Table tExtraData = GUICREATE_TABLE.PANEL.createExtraDataTableAndAddOneRow(className);
                		// Don't destroy tExtraDataSubTable we set it into tExtraData
                		Table tExtraDataSubTable = GUICREATE_TABLE.PANEL.createExtraDataSubTableAndAddOneRow(className);
                		LIB.safeSetTable(tExtraData, GUICOLS.PANEL.EXTRA_DATA_TABLE.SUB_TABLE, CONST_VALUES.ROW_TO_GET_OR_SET, tExtraDataSubTable);
                		LIB.safeSetColValString(tExtraData, GUICOLS.PANEL.EXTRA_DATA_TABLE.VERSION, LIB.VERSION_NUMBER);

                		// Now populate the sub table with the settings
                		LIB.safeSetInt(tExtraDataSubTable, GUICOLS.PANEL.EXTRA_DATA_TABLE.SETTINGS.RUN_NUMBER, CONST_VALUES.ROW_TO_GET_OR_SET, iRunNumber);
                		LIB.safeSetInt(tExtraDataSubTable, GUICOLS.PANEL.EXTRA_DATA_TABLE.SETTINGS.RUN_DATE, CONST_VALUES.ROW_TO_GET_OR_SET, iRunDate);
                		LIB.safeSetString(tExtraDataSubTable, GUICOLS.PANEL.EXTRA_DATA_TABLE.SETTINGS.RUN_DATE_STRING, CONST_VALUES.ROW_TO_GET_OR_SET, sRunDate);
                		 
                		tScriptPanel.scriptDataSetExtraData(tExtraData);
            		}
            		
            		// Do this for Script Panel functionality, i.e., just like this
            		tScriptPanel.viewTable();
            		  
            	} catch(Exception e ){               
            		LIB.log("doEverythingInit", e, className);
            	} 
            	tScriptPanel = LIB.destroyTable(tScriptPanel);
            	tRawReport = LIB.destroyTable(tRawReport); 
            }
        }
    }
    
    public static class FUNC {  
     	  
        public static class HELPER {  
        	
        	public static Table getListOfSelectedItemsFromMultiSelectComboBox(Table tScriptPanel, ICOMBOBOX comboBox,
        			int iRunNumber, String className) throws OException{
            	 
            	Table tReturn = Util.NULL_TABLE;  
            	Table tFullMenu = Util.NULL_TABLE;
            	Table tListOfSelectedItems = Util.NULL_TABLE;
            	try{ 
            		
            		// use this, just to make sure we have a good table
            		// This is a table with two columns with names:
            		// GUICOLS.PANEL.COMBOBOX.ITEM_ID 
            		// GUICOLS.PANEL.COMBOBOX.ITEM
            		tReturn = GUICREATE_TABLE.PANEL.createComboBoxTable(className);
            		
            		// this function returns a *copy*, so we are good to destroy this
            		// also, the way we set this up, this should have the same column structure as tReturn in this case
            		tFullMenu = tScriptPanel.scriptDataGetWidgetMenu(comboBox.getName());
            		
            		int iNumRowsFullMenu = LIB.safeGetNumRows(tFullMenu);
            		// this should always have at least one row, but just in case, do this check
            		if (iNumRowsFullMenu >= 1) {

                		// This is row numbers of the user-selected items, where the full list of items is in tFullMenu
            			// So need to do everything by row number
                		tListOfSelectedItems = tScriptPanel.scriptDataGetWidgetMenuSelect(comboBox.getName());

                		// Thought of many different ways to get and return just the items we want, i.e., the ones selected
                		// eventually decided on this approach as being the simplest and safest, after first trying others
                		int iNumRowsSelected = LIB.safeGetNumRows(tListOfSelectedItems);
                		for (int iCounter = 1; iCounter <= iNumRowsSelected; iCounter++) {
                    		// the column name in tListOfSelectedItems will be 'col1'.  We'll get the values by column number for extra safety, in case
                			// OpenLink ever changes the column name
                			final int iColNumToUse = 1;
                			int iRowNumberOfTheItemInTheFullTable = tListOfSelectedItems.getInt(iColNumToUse, iCounter);
                			// now copy the row from the full table, into the return table for this function by the row number that we just got
                			tFullMenu.copyRowAddByColName(iRowNumberOfTheItemInTheFullTable, tReturn);
                		}
            		}
            	} catch(Exception e ){               
            		LIB.log("getListOfSelectedItemsFromMultiSelectComboBox", e, className);
            	}     
            	tFullMenu = LIB.destroyTable(tFullMenu);
            	tListOfSelectedItems = LIB.destroyTable(tListOfSelectedItems);
            	
            	return tReturn;
        	}
        	
        	public static boolean checkToSeeIfComboBoxSelectedItemIsAll(Table tMaster,
        			int iRunNumber, String className) throws OException{
            	 
        		boolean bReturn = false;
            	try{ 
            		int iNumRows = LIB.safeGetNumRows(tMaster);
            		// count no rows as 'all'
            		if (iNumRows < 1) {
            			bReturn = true;
            		}
            		// if it is 'All', then there must be just one row, so check for that
            		if (iNumRows == 1) {
            			int iValue = LIB.safeGetInt(tMaster, GUICOLS.PANEL.COMBOBOX.ITEM_ID, CONST_VALUES.ROW_TO_GET_OR_SET);
            			if (iValue == CONST_VALUES.ALL.ID) {
            				bReturn = true;
            			}
            		}
            		  
            	} catch(Exception e ){               
            		LIB.log("checkToSeeIfComboBoxSelectedItemIsAll", e, className);
            	}     
 
            	return bReturn;
        	}
        	
        	public static int getDateFromCalendarWidget(Table tScriptPanel, CALENDAR calendar,
        			int iRunNumber, int iRunDate, String sRunDate, String className) throws OException{
            	 
        		// default to today
        		int iReturn = iRunDate;
            	try{ 
            		String sDate = tScriptPanel.scriptDataGetWidgetString(calendar.getName());
            		if (Str.len(sDate) >= 1) {
            			iReturn = OCalendar.parseString(sDate);
            		}
            		
            	} catch(Exception e ){               
            		LIB.log("getDateFromCalendarWidget", e, className);
            	}     
 
            	return iReturn;
        	}

        } // end class HELPER

    	public static void getRawDataReportForScriptPanelAndPutIntoExtraDataTable(Table tScriptPanel, Table tExtraData,
    			int iRunNumber, int iRunDate, String sRunDate,
    			String className) throws OException{
        	 
        	try{  
 
        		// STEP 1
        		// do this first
        		{
        			// need to free up/delete the sub tables if there.  This is a 'neat trick' way to delete them if they exist and be OK if they don't exist
        			// since delcol will delete the subtables too, if there
        			LIB.safeDelCol(tExtraData, GUICOLS.PANEL.EXTRA_DATA_TABLE.RAW_DATA_BY_UNIQUE_KEY_AND_SETTINGS);
        			
        			// now add the columns back.  While we could do an 'add col', for cosmetic (non functional reasons) do an insert to get them in the same col order
        			tExtraData.insertCol(GUICOLS.PANEL.EXTRA_DATA_TABLE.RAW_DATA_BY_UNIQUE_KEY_AND_SETTINGS, GUICOLS.PANEL.EXTRA_DATA_TABLE.VERSION, COL_TYPE_ENUM.COL_TABLE);
        		}
        		
        		// STEP 2
        		// get the settings from the Script Panel, i.e., combo-boxes and Calendar Widgets
        		Table tSearchByDateType = HELPER.getListOfSelectedItemsFromMultiSelectComboBox(tScriptPanel, COMBOBOX.COMBOBOX1, iRunNumber, className);
        		//boolean bAllLeaseFlag = HELPER.checkToSeeIfComboBoxSelectedItemIsAll(tLeaseList, iRunNumber, className);

        		// Currency
        		Table tCurrencyList = HELPER.getListOfSelectedItemsFromMultiSelectComboBox(tScriptPanel, COMBOBOX.COMBOBOX31, iRunNumber, className);
        		boolean bAllCurrenciesFlag = HELPER.checkToSeeIfComboBoxSelectedItemIsAll(tCurrencyList, iRunNumber, className);

        		// Portfolio
        		Table tPortfoliosList = HELPER.getListOfSelectedItemsFromMultiSelectComboBox(tScriptPanel, COMBOBOX.COMBOBOX30, iRunNumber, className);
        		boolean bAllPortfoliosFlag = HELPER.checkToSeeIfComboBoxSelectedItemIsAll(tPortfoliosList, iRunNumber, className);

        		// Ins Type
        		Table tInsTypesList = HELPER.getListOfSelectedItemsFromMultiSelectComboBox(tScriptPanel, COMBOBOX.COMBOBOX32, iRunNumber, className);
        		boolean bAllInsTypesFlag = HELPER.checkToSeeIfComboBoxSelectedItemIsAll(tInsTypesList, iRunNumber, className);
 
           		Table tCurrentDataSource = HELPER.getListOfSelectedItemsFromMultiSelectComboBox(tScriptPanel, COMBOBOX.COMBOBOX15, iRunNumber, className);
           		String sCurrentDataSource = "";
        		{
        			int iNumCols = LIB.safeGetNumCols(tCurrentDataSource);
        			for (int iCounter = 1; iCounter <= iNumCols; iCounter++) {
        				int iColType = tCurrentDataSource.getColType(iCounter);
        				if (iColType == COL_TYPE_ENUM.COL_STRING.toInt()) {
        					String sColName = tCurrentDataSource.getColName(iCounter);
        					sCurrentDataSource = LIB.safeGetStringNoErrorMessage(tCurrentDataSource, sColName, CONST_VALUES.ROW_TO_GET_OR_SET);
        				}
        			}
        		} 
        		
        		int iShowAllColumns = CONST_VALUES.VALUE_OF_FALSE;
        		try {
            		String sShowAllColumns = tScriptPanel.scriptDataGetWidgetString(COMBOBOX.COMBOBOX5.getName());
            		if (Str.containsSubString(sShowAllColumns.toUpperCase(), "Y") == 1) {
            			iShowAllColumns = CONST_VALUES.VALUE_OF_TRUE;
            		}
        		} catch (Exception e) {
        			// don't log this
        		}
        		
        		// STEP 3
        		// Set into the Extra Data Table
        		// Do *NOT* destroy this table in this function.  Instead, put it into the Extra Data table as a sub-table
        		// The data is in sub-tables.  one row per lease
        		// in general, do *NOT* get data directly from this table
        		// instead, call one of the 'REPORT' library functions
        		Table tTradeDataOneRowPerLeaseAsSubTableWithSettingsToo = REPORT.GET_RAW_DATA_FOR_REPORTS.getTradeDataOneRowPerUniqueKeyAsSubTableWithSettingsToo(tSearchByDateType,
            			tCurrencyList, bAllCurrenciesFlag,  
            			tPortfoliosList, bAllPortfoliosFlag,
            			tInsTypesList, bAllInsTypesFlag, 
        				sCurrentDataSource,
        				iShowAllColumns, 
        				iRunNumber, className);
        		String sTableName = "Raw Data";
        		tTradeDataOneRowPerLeaseAsSubTableWithSettingsToo.setTableName(sTableName);
        		tTradeDataOneRowPerLeaseAsSubTableWithSettingsToo.setTableTitle(sTableName);
        		LIB.safeSetTable(tExtraData, GUICOLS.PANEL.EXTRA_DATA_TABLE.RAW_DATA_BY_UNIQUE_KEY_AND_SETTINGS, CONST_VALUES.ROW_TO_GET_OR_SET, tTradeDataOneRowPerLeaseAsSubTableWithSettingsToo);
        		  
        	} catch(Exception e ){               
        		LIB.log("getRawDataReportForScriptPanelAndPutIntoExtraDataTable", e, className);
        	}     
    	}

    	public static void getAndRefreshDataIntoScriptPanel(Table tScriptPanel, Table tExtraData,
    			int iRunNumber, int iRunDate, String sRunDate, 
    			String className) throws OException{
        	 
        	try{ 
        		
    	    	FUNC.getRawDataReportForScriptPanelAndPutIntoExtraDataTable(tScriptPanel, tExtraData, iRunNumber, iRunDate, sRunDate, className);
    			
    		   	String sReportFromComboBox = tScriptPanel.scriptDataGetWidgetString(COMBOBOX.COMBOBOX20.getName());

    	    	// default to this
    			REPORT_NAME reportName = REPORT_NAME.TRADE_LIST;

    			// And then use this loop to iterate through the enums and figure out what report we should show based on the 
    			// user-selected value from the ComboBox
    			for (REPORT_NAME reportItem: REPORT_NAME.values()) {
    				String sReportNameFromItem = reportItem.getName();
    				if (Str.iEqual(sReportNameFromItem, sReportFromComboBox) == 1) {
            			reportName = reportItem;
    				}
    			}
    			
    			COMMON_FUNC.populatePanelBasedOnReportTypeAndGetSourceDataFromExtraDataTable(tScriptPanel, tExtraData, reportName, iRunNumber, className);
    			
    			FUNC.setAllLabelValuesForRefresh(tScriptPanel, className);
    			
        	} catch(Exception e ){               
        		LIB.log("getAndRefreshDataIntoScriptPanel", e, className);
        	}   
    	}
    	
    	public static void setAllLabelValuesForRefresh(Table tScriptPanel, String className) throws OException{

        	try{
        		
        		// DateTimeForLastUpdatedDateTime
        		GUI.modifyLabelWithTextToShowUser(tScriptPanel, LABEL.LABEL10, COMMON_FUNC.getDateTimeForLastUpdatedDateTime(className), className);
        		  
        	} catch(Exception e ){               
        		LIB.log("setAllLabelValuesForRefresh", e, className);
        	}
    	} 

    } // END public static class FUNC
    
   
    public static class PANEL {            
        public static class LOCATION {      
        	public static final int TOP = 82;
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
   
    public enum BUTTON implements IBUTTON{
    	BUTTON1 ("BUTTON1", "Load Data", "x=15, y=7, h=36, w=120"),  
      
    	BUTTON3 ("BUTTON3", "Reset Filters", "x=3440, y=57, h=19, w=62"),  
//    	BUTTON3 ("BUTTON3", "Reset Filters", "x=440, y=57, h=19, w=62"),  
    
    //	DEBUG_INFO_FOR_TESTING ("DEBUG_INFO", "Debug Info", "x=340, y=57, h=19, w=62"),
    // 	DEBUG_INFO_REGULAR ("DEBUG_INFO", "Debug Info", "x=1225, y=22, h=18, w=60"),

        
         DEBUG_INFO ("DEBUG_INFO", "Debug Info", "x=1225, y=22, h=18, w=60"),

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


    public enum CALENDAR implements ICALENDAR{

    	//CALENDAR1 ("CALENDAR1", "Start Date", "0d", "x=453, y=18, h=18, w=80"), 
    	//CALENDAR2 ("CALENDAR2", "End Date", "6d", "x=453, y=39, h=18, w=80"), 
    	; 

    	private final String sCalendarName;
    	private final String sCalendarLabel;
    	private final String sCalendarDefaultSymbolicDate;
    	private final String sCalendarPosition;
    		  
      	CALENDAR(String sCalendarName, String sCalendarLabel, String sCalendarDefaultSymbolicDate, String sCalendarPosition) {
		      this.sCalendarName = sCalendarName;
		      this.sCalendarLabel = sCalendarLabel;
		      this.sCalendarDefaultSymbolicDate = sCalendarDefaultSymbolicDate;
		      this.sCalendarPosition = sCalendarPosition;
		  }
		  
		  public String getName() {
			  return this.sCalendarName;
		  }
  
		  public String getLabel() {
			  return this.sCalendarLabel;
		  }

		  public String calendarDefaultDate() {
			  String sReturn = "";
			  try {
				  int iToday = OCalendar.today();

				  // default to today
				  int iDate = iToday;

				  boolean bHandled = false;

				  if (Str.iEqual(sCalendarDefaultSymbolicDate, "-1Sun") == CONST_VALUES.VALUE_OF_TRUE) {
						int iStartDate = OCalendar.today();
						int iDayOfWeek = OCalendar.getDayOfWeek(iStartDate);
						// This will be Sunday to Saturday
						iStartDate = iStartDate - iDayOfWeek;
						iDate = iStartDate;
						bHandled = true;
				  }
				  if (Str.iEqual(sCalendarDefaultSymbolicDate, "+1Sat") == CONST_VALUES.VALUE_OF_TRUE) {
						int iStartDate = OCalendar.today();
						int iDayOfWeek = OCalendar.getDayOfWeek(iStartDate);
						// This will be Sunday to Saturday
						iStartDate = iStartDate - iDayOfWeek;
						int iEndDate = iStartDate + 6;
						iDate = iEndDate;
						bHandled = true;
				  } 
				  
				  if (bHandled == false) {
					  iDate = OCalendar.parseString(sCalendarDefaultSymbolicDate, -1, iToday);
					  bHandled = true;
				  }
				  sReturn = OCalendar.formatJd(iDate, DATE_FORMAT.DATE_FORMAT_DMLY_NOSLASH);
			  } catch (Exception e) {
				  // don't log this
			  }
			  return sReturn;
		  }

		  public String getPosition() {
			  return this.sCalendarPosition;
		  }
    } 

    public enum LABEL implements ILABEL{
    	 

    	LABEL9 ("LABEL9", "Last Updated",       "x=1090, y=1, h=18, w=100"), 
    	LABEL10 ("LABEL10", "dd-mmm-yyyy hh:mm:ss", "x=1097, y=19, h=18, w=120"),
    	LABEL11 ("LABEL11", "Show All Cols", "x=1260, y=1, h=18, w=65"),

    	LABEL20 ("LABEL20", "Report:", "x=0, y=56, h=18, w=50"), 

//    	LABEL16 ("LABEL16", "Data Source", "x=160, y=6, h=16, w=170"), 
//    	LABEL30 ("LABEL30", "Portfolio", "x=336, y=0, h=15, w=44"), 
//    	LABEL31 ("LABEL31", "Metal", "x=346, y=18, h=15, w=35"),  
//    	LABEL32 ("LABEL32", "Ins Type", "x=335, y=36, h=15, w=45"), 

    	LABEL16 ("LABEL16", "Data Source", "x=5160, y=6, h=16, w=170"), 
    	LABEL30 ("LABEL30", "Portfolio", "x=5336, y=0, h=15, w=44"), 
    	LABEL31 ("LABEL31", "Metal", "x=5346, y=18, h=15, w=35"),  
    	LABEL32 ("LABEL32", "Ins Type", "x=5335, y=36, h=15, w=45"), 

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
      
    public enum COMBOBOX implements ICOMBOBOX{

    	// For now... keep these.. and just move way, way offscreen and make it one pixel
    	COMBOBOX1 ("COMBOBOX1", "Search By", "x=3371, y=20, h=20, w=90"),
     	
    	COMBOBOX5 ("COMBOBOX5", "Show All Cols", "x=1225, y=1, h=18, w=30"),
    
    	COMBOBOX20 ("COMBOBOX20", "Report Name", "x=50, y=56, h=18, w=280"),

    	// Filter Combo Boxes
//    	COMBOBOX15 ("COMBOBOX15", "Data Source", "x=160, y=26, h=18, w=170"),
//    	COMBOBOX30 ("COMBOBOX30", "Portfolio", "x=381, y=0, h=15, w=120"),
//    	COMBOBOX31 ("COMBOBOX31", "Metal", "x=381, y=19, h=15, w=120"), 
//    	COMBOBOX32 ("COMBOBOX32", "Ins Type", "x=381, y=38, h=15, w=120"),

    	COMBOBOX15 ("COMBOBOX15", "Data Source", "x=5160, y=26, h=18, w=170"),
    	COMBOBOX30 ("COMBOBOX30", "Portfolio", "x=5381, y=0, h=15, w=120"),
    	COMBOBOX31 ("COMBOBOX31", "Metal", "x=5381, y=19, h=15, w=120"), 
    	COMBOBOX32 ("COMBOBOX32", "Ins Type", "x=5381, y=38, h=15, w=120"),

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
      
  

    
}

