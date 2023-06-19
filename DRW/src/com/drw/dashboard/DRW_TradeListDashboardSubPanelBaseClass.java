package com.drw.dashboard;


import com.drw.dashboard.DRW_TradeListDashboardLibrary.COMMON_FUNC;
import com.drw.dashboard.DRW_TradeListDashboardLibrary.GUI;
import com.drw.dashboard.DRW_TradeListDashboardLibrary.GUICOLS;
import com.drw.dashboard.DRW_TradeListDashboardLibrary.GUICOLS.GUICREATE_TABLE;
import com.drw.dashboard.DRW_TradeListDashboardLibrary.IBUTTON;
import com.drw.dashboard.DRW_TradeListDashboardLibrary.ICOMBOBOX;
import com.drw.dashboard.DRW_TradeListDashboardLibrary.ILABEL;
import com.drw.dashboard.DRW_TradeListDashboardLibrary.SUBSCRIPTION;
import com.drw.dashboard.DRW_TradeListDashboardMainPanel.MAIN_PANEL_PICK_LIST;
import com.drw.dashboard.DRW_TradeListReportsLibrary.COLS;
import com.drw.dashboard.DRW_TradeListReportsLibrary.CONST_VALUES;
import com.drw.dashboard.DRW_TradeListReportsLibrary.LIB;
import com.drw.dashboard.DRW_TradeListReportsLibrary.REPORT_NAME;
import com.olf.openjvs.IContainerContext;
import com.olf.openjvs.OCalendar;
import com.olf.openjvs.OException;
import com.olf.openjvs.PluginCategory;
import com.olf.openjvs.PluginType;
import com.olf.openjvs.Str;
import com.olf.openjvs.Table;
import com.olf.openjvs.Util;
import com.olf.openjvs.enums.DATE_FORMAT;
import com.olf.openjvs.enums.ECOM_SCOPE;
import com.olf.openjvs.enums.MSG_TYPE_ENUM;
import com.olf.openjvs.enums.NOTIFY_MSG_DATA_TYPE;
import com.olf.openjvs.enums.SCRIPT_CATEGORY_ENUM;
import com.olf.openjvs.enums.SCRIPT_PANEL_CALLBACK_TYPE_ENUM;
import com.olf.openjvs.enums.SCRIPT_TYPE_ENUM; 
    
/* 
Name:  DRW_TradeListDashboardSubPanelBaseClass.java

This is a function library for sub panels for the DRW Trade Listing Reports

Revision History 
23-Apr-2023 Brian New script
   
*/

@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_GENERIC)
@PluginType(SCRIPT_TYPE_ENUM.MAIN_SCRIPT)
public class DRW_TradeListDashboardSubPanelBaseClass {
 
    public static void doEverything(IContainerContext context, REPORT_NAME reportName, String className) throws OException
    {
    	Table tExtraData = Util.NULL_TABLE;
    	
    	try {
    		// This will have the table sent in by the *message*
    		Table argt = context.getArgumentsTable();
    		
        	// The script panel is returnt, not 'argt'
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
            	// Just use the time for this.  This won't be used/needed for this Script Panel
            	int iRunNumber = Util.timeGetServerTime();
            	GUI_HANDLER.INIT_HANDLER.doEverythingInit(iRunDate, iRunNumber, reportName, className);
        	}

        	// Do *NOT* do this if run for the first time, i.e., if 'init'
        	if (bCallBackTypeIsInit == false) {
        		
            	// Get Extra data
            	tExtraData = returnt.scriptDataGetExtraDataTable();
            	Table tExtraDataSubTable = LIB.safeGetTable(tExtraData, GUICOLS.PANEL.EXTRA_DATA_TABLE.SUB_TABLE, CONST_VALUES.ROW_TO_GET_OR_SET);
            	int iRunNumber = LIB.safeGetInt(tExtraDataSubTable, GUICOLS.PANEL.EXTRA_DATA_TABLE.SETTINGS.RUN_NUMBER, CONST_VALUES.ROW_TO_GET_OR_SET);

            	if (callbackType == SCRIPT_PANEL_CALLBACK_TYPE_ENUM.SCRIPT_PANEL_PUSHBUTTON) {
            		GUI_HANDLER.BUTTON_HANDLER.doEverythingButton(returnt, tExtraData, iRunNumber, className);
            	} 
            	if (callbackType == SCRIPT_PANEL_CALLBACK_TYPE_ENUM.SCRIPT_PANEL_COMBOBOX) {
            		GUI_HANDLER.COMBOBOX_HANDLER.doEverythingComboBox(returnt, tExtraData, iRunNumber, className);
            	}
            	if (callbackType == SCRIPT_PANEL_CALLBACK_TYPE_ENUM.SCRIPT_PANEL_DBLCLICK) {
            		GUI_HANDLER.DOUBLE_CLICK_HANDLER.doEverythingDoubleClick(returnt, className);
            	}
            	if (callbackType == SCRIPT_PANEL_CALLBACK_TYPE_ENUM.SCRIPT_PANEL_RV_MESSAGE) {
            		GUI_HANDLER.MESSAGE_HANDLER.doEverythingMessageReceived(tExtraData, reportName, returnt, argt, iRunNumber, className);
            	}
        	} // END  if (bCallBackTypeIsInit == false) 
    		 
    	} catch (Exception e) {
    		LIB.log("doEverything", className);
    	} 
    	
    }
 
    public static class GUI_HANDLER { 

    	// applies to more than one type of message
        public static class COMMON { 
        	
        	 public static void getReportTypeAndGetSourceDataFromExtraDataTableAndPopulatePanel(Table tScriptPanel, Table tExtraData,
        			 int iRunNumber, String className) throws OException {
             	
             	try{
            		
             		// Get the report that the user has selected based on the PickList/ComboBox
        			String sReportFromComboBox = tScriptPanel.scriptDataGetWidgetString(COMBOBOX.COMBOBOX1.getName());
        			
        			// Default to this report, 
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
        	 
        } // COMMON
        
        public static class BUTTON_HANDLER { 
        	
            public static void doEverythingButton(Table tScriptPanel, Table tExtraData,
            		int iRunNumber, String className) throws OException {
            	
            	try{
            		String sButtonName = tScriptPanel.scriptDataGetCallbackName();
            		String sButtonLabel = tScriptPanel.scriptDataGetWidgetString(sButtonName);
            		LIB.log("ButtonName: " + sButtonName + ", ButtonLabel: " + sButtonLabel, className);
 
            		boolean bHandledFlag = false;
               		if (Str.equal(sButtonName, BUTTON.BUTTON1.getName()) == CONST_VALUES.VALUE_OF_TRUE) {
            			//FUNC.getAndRefreshDataIntoScriptPanel(tScriptPanel, tExtraData, iRunNumber, iRunDate, sRunDate, className);
            			bHandledFlag = true;
            		}

               		if (Str.equal(sButtonName, BUTTON.DEBUG_INFO.getName()) == CONST_VALUES.VALUE_OF_TRUE) {
               			{
               				try {
               					// Make a copy... for no particular reason... maybe is safer?
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
            		String className) throws OException {
            	
            	try{
            		
            		boolean bHandledFlag = false;
            		int iColNum = tScriptPanel.scriptDataGetCallbackCol();
            		int iRowNum = tScriptPanel.scriptDataGetCallbackRow();
            		
        			String sColName = "";
            		if (iColNum >= 1) {
            			sColName = tScriptPanel.getColName(iColNum);
            		}
            		
            		// TODO
            		// need to have this in ***BOTH*** the main script and the SubPanel scripts
            		
            		if (Str.iEqual(sColName, COLS.REPORT.DEAL_NUM_COL_NAME) == CONST_VALUES.VALUE_OF_TRUE) {
            			int iDealNum = tScriptPanel.getCellInt(sColName, iRowNum);
            			COMMON_FUNC.showTransactionGivenDealNum(iDealNum, className);
            			bHandledFlag = true;
            		}

//            		if (Str.iEqual(sColName, COLS.REPORT.FORMATTED_REPORT.IMS_OC_ACTIVITY_LOG_DATA.request_data_cell) == CONST_VALUES.VALUE_OF_TRUE) {
//            			Table tData = LIB.safeGetTable(tScriptPanel, COLS.REPORT.FORMATTED_REPORT.IMS_OC_ACTIVITY_LOG_DATA.request_data, iRowNum); 
//            			String sJobID = LIB.safeGetString(tScriptPanel, COLS.REPORT.FORMATTED_REPORT.IMS_OC_ACTIVITY_LOG_DATA.job_id, iRowNum);
//            			LIB.viewTable(tData, "IMS Trade Details as Sent by IMS for Job: " + sJobID);
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


        public static class COMBOBOX_HANDLER { 
        	 
        	
            public static void doEverythingComboBox(Table tScriptPanel, Table tExtraData,
            		int iRunNumber, String className) throws OException {
            	
            	try{
            		 
            		// the name of the combobox the user modified
            		String sComboBoxName          = tScriptPanel.scriptDataGetCallbackName();
                    // a one column, one row table with the row number of the item the user selected
                    Table tComboBoxReturnMenu    = tScriptPanel.scriptDataGetWidgetMenuSelect(sComboBoxName);
                    // in multi select pick-lists, this can be 0, meaning no rows selected
					@SuppressWarnings("unused")
					int iNumRowsSelectedByUser = LIB.safeGetNumRows(tComboBoxReturnMenu);
                    // The full menu with all of the items a user can select
                    @SuppressWarnings("unused")
					Table tComboBoxFullMenu      = tScriptPanel.scriptDataGetWidgetMenu(sComboBoxName);
                  
                    boolean bHandledFlag = false;
                    // COMBOBOX1 = 'Report Name'
                    if (Str.equal(sComboBoxName, COMBOBOX.COMBOBOX1.getName()) == CONST_VALUES.VALUE_OF_TRUE) {
                    	
            			// This will key which report to display based on the Report Name selected (by the user) in ComboBox1
            			GUI_HANDLER.COMMON.getReportTypeAndGetSourceDataFromExtraDataTableAndPopulatePanel(tScriptPanel, tExtraData, iRunNumber, className);
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
  
        public static class MESSAGE_HANDLER { 
        	
            public static void doEverythingMessageReceived(Table tExtraData, REPORT_NAME reportName, Table tScriptPanel, Table argt,
            		int iRunNumber, String className) throws OException {
            	
            	try{
            		
            		// we don't need this, so don't get it.  
            		// Including this here because maybe we'll need it in the future, though that is not likely.
            		// This is more about completeness, i.e, showing everything OpenLink can do.
            		boolean bGetExtraMessageInformation = false;
            		if (bGetExtraMessageInformation == true) {
            			// this will be 0 (zero), which may be data type table?
                        int msgDataType = tScriptPanel.scriptDataGetMsgDataType();
                        // this will be 60.  That may be user script?
                        int msgType = tScriptPanel.scriptDataGetMsgType();
                        // this will be 1.  that probably means 'local'
                        int msgScope = tScriptPanel.scriptDataGetMsgScope();
                        // this is what was seen in testing.  This will vary from OpenLink instance to instance.  Only the last part of it matters
                        //  _LOCAL.bshydlo.CMTOWDOPLCTX01.14952.USER_SCRIPT.LOANDEP_TRADE_LISTING
                        String msgSubType = tScriptPanel.scriptDataGetMsgSubType();
                        LIB.log("msgDataType: " + msgDataType + ", msgType: " + msgType + ", msgScope: " + msgScope + ", msgSubType: " + msgSubType, className);
            		}

            		boolean bOKToProceed = true;
            		int iNumRows = LIB.safeGetNumRows(argt);
            		// this comes from OpenLink.  Do not change it.
            		final String MESSAGE_TABLE_COL_NAME = "MessageTable";
            		int iColNumOfMessageTable = argt.getColNum(MESSAGE_TABLE_COL_NAME);
            		if (iNumRows < 1) {
            			LIB.log("****WARNING: This script panel received a message with no Message Table (no rows)", className);
            			bOKToProceed = false;
            		}
            		if (bOKToProceed == true) {
            			if (iColNumOfMessageTable < 1) {
                			LIB.log("****WARNING: This script panel received a message with no Message Table (missing '" + MESSAGE_TABLE_COL_NAME + "' column)", className);
                			bOKToProceed = false;
            			}
            		}
            		Table tMessageTable = Util.NULL_TABLE;
            		if (bOKToProceed == true) {
            			tMessageTable = LIB.safeGetTable(argt, MESSAGE_TABLE_COL_NAME, CONST_VALUES.ROW_TO_GET_OR_SET);
            			iNumRows = LIB.safeGetNumRows(tMessageTable);
                		if (iNumRows < 1) {
                			LIB.log("****WARNING: This script panel received a message with no Message Table (no rows in message table)", className);
                			bOKToProceed = false;
                		}
            		}
            		final String MESSAGE_SUBTABLE_COL_NAME = "table_message";
            		if (bOKToProceed == true) {
                		int iColNumOfMessageSubTable = tMessageTable.getColNum(MESSAGE_SUBTABLE_COL_NAME);
            			if (iColNumOfMessageSubTable < 1) {
                			LIB.log("****WARNING: This script panel received a message with no Message Sub Table (missing '" + MESSAGE_SUBTABLE_COL_NAME + "' column)", className);
                			bOKToProceed = false;
            			}
            		}
            		
            		if (bOKToProceed == true) {

            			Table tMessageData = LIB.safeGetTable(tMessageTable, MESSAGE_SUBTABLE_COL_NAME, CONST_VALUES.ROW_TO_GET_OR_SET);
            			
            			// tMessageData is the 'tExtra Data' table.
            			// We are going to move this new data, i.e., from tMessageData, into the Extra Data table. 
            			// Easiest and safest approach is to delete the row and add it.  i.e. do this instead of table select and/or get/set table
            			tExtraData.clearRows();
            			tMessageData.copyRowAddAllByColName(tExtraData);
            			
            			// we don't need tMessageData any more, and while it should be not needed in terms of memory, maybe this will help free some memory up
            			tMessageData.clearRows();
            			
            			// This will key which report to display based on the Report Name selected (by the user) in ComboBox1
            			GUI_HANDLER.COMMON.getReportTypeAndGetSourceDataFromExtraDataTableAndPopulatePanel(tScriptPanel, tExtraData, iRunNumber, className);
            		} 
            	 
            	} catch(Exception e ){               
            		LIB.log("doEverythingMessageReceived", e, className);
            	} 
            }
        }
        
        public static class INIT_HANDLER { 

            public static class HELPER { 
            	
            	 public static void subscribeToMessages(Table tScriptPanel, SUBSCRIPTION subScription, String className) throws OException {
                 	
                 	try {
                 		
                		// This is the main thing here, that we'll subscribe to a message as sent by another script panel
                        // subscribing to all Master console messages
                		tScriptPanel.scriptDataMessageSubscribe(NOTIFY_MSG_DATA_TYPE.MSG_DATA_TABLE.toInt(),
                                        MSG_TYPE_ENUM.USER_SCRIPT_MSG.toInt(), subScription.getName(),
                                        ECOM_SCOPE.LOCAL_MSG.toInt());

                 	} catch (Exception e) {
                 		LIB.log("subscribeToMessages", e, className);
                 	}
                 }

            } // end class HELPER

           

            public static void doEverythingInit(int iRunDate, int iRunNumber, REPORT_NAME reportName, String className) throws OException {

            	Table tScriptPanel = Util.NULL_TABLE;
            	Table tRawReport = Util.NULL_TABLE;
            	try{
            		String sRunDate = OCalendar.formatJd(iRunDate, DATE_FORMAT.DATE_FORMAT_DMLY_NOSLASH); 
            		
            		// this is just an empty table, i.e., no rows and no columns.  Those will get added when we load data, i.e., after the Init
            		tScriptPanel = Table.tableNew();
            		
            		// subscribe to all message
            		// This is very important, because this panel is basing all data on whatever the 'Main' panel (another script) sends to it
            		HELPER.subscribeToMessages(tScriptPanel, SUBSCRIPTION.ALL_DATA, className);

            		// This is the only button we are adding
            		GUI.addButton(tScriptPanel, BUTTON.DEBUG_INFO, className);
            	  
            		// Add Combo Boxes and associated labels when applicable
            		{
            			// Report Name
                		GUI.addLabel(tScriptPanel, LABEL.LABEL1, className);
                		
                		Table tComboBoxMenu = MAIN_PANEL_PICK_LIST.getReportNamePicklist(className);
                		
                		// TODO
                		// Add new reports here
                		  
                		// Special Logic *ONLY* for the Sub-Panels for initial defaults only
                		if (reportName == REPORT_NAME.SUB_PANEL2) {
                			reportName = REPORT_NAME.TRADE_LIST_QUORUM;
                		} else
                		if (reportName == REPORT_NAME.SUB_PANEL3) {
                			reportName = REPORT_NAME.TRADE_LIST;
                		}
                		
                		String sDefaultValue = reportName.getName();
                		
                		GUI.addSingleSelectComboxBox(tScriptPanel, COMBOBOX.COMBOBOX1, tComboBoxMenu, sDefaultValue, className);
                		// OpenLink makes a copy of tComboBoxMenu, so OK to delete it
                		tComboBoxMenu = LIB.destroyTable(tComboBoxMenu);
            		}  
            	 	  
            		String sScriptPanelLocation = "top=" + PANEL.LOCATION.TOP + 
            					",left=" + PANEL.LOCATION.LEFT +
            					",right=" + PANEL.LOCATION.RIGHT + 
            					",bottom=" + PANEL.LOCATION.BOTTOM;
            		tScriptPanel.scriptDataMoveListBox(sScriptPanelLocation);

            		// This is only used when run using a 'ViewTable'. 
            		// Not used when this screen is within a Script Panel
            		String sSize = "x=" + PANEL.SIZE.X + 
        					",y=" + PANEL.SIZE.Y +
        					",w=" + PANEL.SIZE.W + 
        					",h=" + PANEL.SIZE.H;
            		tScriptPanel.scriptDataSetWindowSize(sSize);
            		
            		// To save space, hide the print ICON
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
    } // end class GUI_HANDLER

	  
    public static class PANEL {            
        public static class LOCATION {            
        	public static final int TOP = 22;
        	public static final int LEFT = 1;
        	public static final int RIGHT = 1;
        	public static final int BOTTOM = 1;
        }
        // this size is not important when this script panel is in a Desktop
        public static class SIZE {            
        	public static final int X = 200;
        	public static final int Y = 250;
        	public static final int W = 1000;
        	public static final int H = 1000;
        }
    }

    public enum BUTTON implements IBUTTON{
    	BUTTON1 ("BUTTON1", "Load Data", "x=15, y=10, h=35, w=120"),  
    	DEBUG_INFO ("DEBUG_INFO", "Debug Info", "x=1225, y=1, h=18, w=60"), 
    	
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

    public enum LABEL implements ILABEL {
    	LABEL1 ("LABEL1", "Report:", "x=0, y=0, h=18, w=50"),  
    	
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

    public enum COMBOBOX implements ICOMBOBOX {
    	COMBOBOX1 ("COMBOBOX1", "Report Name", "x=50, y=0, h=18, w=300"),
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
