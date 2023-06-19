package com.drw.dashboard; 


import com.drw.dashboard.DRW_TradeListReportsLibrary.COLS;
import com.drw.dashboard.DRW_TradeListReportsLibrary.CONST_VALUES;
import com.drw.dashboard.DRW_TradeListReportsLibrary.ID_VALUE_AND_NAME_ENUM;
import com.drw.dashboard.DRW_TradeListReportsLibrary.LIB;
import com.drw.dashboard.DRW_TradeListReportsLibrary.REPORT;
import com.drw.dashboard.DRW_TradeListReportsLibrary.REPORT_NAME;
import com.olf.openjvs.Afe;
import com.olf.openjvs.OCalendar;
import com.olf.openjvs.OConsole;
import com.olf.openjvs.OException;
import com.olf.openjvs.PluginCategory;
import com.olf.openjvs.PluginType;
import com.olf.openjvs.Str;
import com.olf.openjvs.Table;
import com.olf.openjvs.Transaction;
import com.olf.openjvs.Util;
import com.olf.openjvs.enums.COL_TYPE_ENUM;
import com.olf.openjvs.enums.DATE_FORMAT;
import com.olf.openjvs.enums.SCRIPT_CATEGORY_ENUM;
import com.olf.openjvs.enums.SCRIPT_PANEL_REFRESH_ENUM;
import com.olf.openjvs.enums.SCRIPT_PANEL_WIDGET_TYPE_ENUM;
import com.olf.openjvs.enums.SCRIPT_TYPE_ENUM;
import com.olf.openjvs.enums.SEARCH_CASE_ENUM;


/* 
Name:  DRW_TradeListDashboardSubPanelBaseClass.java

This is a function library for sub panels for the DRW Trade Listing Reports

Revision History 
25-Apr-2023 Brian New script

*/


@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_GENERIC)
@PluginType(SCRIPT_TYPE_ENUM.MAIN_SCRIPT)
public class DRW_TradeListDashboardLibrary {

	// common meaning applies to the 'Main' panel and also to the sub-panels
	// i.e., and not between the Script Panel version of the reports and the stand-alone version of the reports.
    public static class COMMON_FUNC {  

        public static class HELPER {  
       
			public static class TRADE_LIST {
				
	        	public static void getReportAndAddToScriptPanel(Table tScriptPanel, Table tTradeDataOneRowPerLeaseAsSubTableWithSettingsToo,
	        			REPORT_NAME reportName,
	               		int iRunNumber, String className) throws OException {
	        		
	        		Table tRawReport = Util.NULL_TABLE;
	        		Table tFormattedReport = Util.NULL_TABLE;
	        		try{   
	        			
	        			LIB.log("TRADE_LIST.getReportAndAddToScriptPanel: START", className);
	        			
	        			tRawReport = REPORT.GET_RAW_REPORT.TRADE_LIST.getRawReport(tTradeDataOneRowPerLeaseAsSubTableWithSettingsToo, reportName, iRunNumber, className);
 
	        			// convert the raw data/raw report into the formatting that the user wants
	        			boolean bWebServicesMode = false;
	        			tFormattedReport = REPORT.GET_REPORT.TRADE_LIST.getReport(tRawReport, bWebServicesMode, iRunNumber, className);

	        			// This is a generic function that works for all reports.  This will also add deal_num as a cell column
	        			COMMON_FUNC.addReportTableToScriptPanel(tScriptPanel, tFormattedReport, className);

	        			// Add formating to the script panel, including grouping/sorting and setting the column titles.  Do this as an explicit step, since it isn't automatic
	        			REPORT.GET_REPORT.TRADE_LIST.formatReport(tScriptPanel, bWebServicesMode, iRunNumber, className);
	        			
	        			LIB.log("TRADE_LIST.getReportAndAddToScriptPanel: END" + ", Num Rows Raw Data: " + LIB.safeGetNumRows(tRawReport) + ", Num Rows in Report: " + LIB.safeGetNumRows(tFormattedReport), className);
	        			
	        		} catch(Exception e ){               
	            		LIB.log("TRADE_LIST.getReportAndAddToScriptPanel", e, className);
	        		}
	        		tRawReport = LIB.destroyTable(tRawReport);
	        		tFormattedReport = LIB.destroyTable(tFormattedReport);
	        	}
			} // end class TRADE_LIST
			
			public static class TRADE_LIST_QUORUM {
				
	        	public static void getReportAndAddToScriptPanel(Table tScriptPanel, Table tTradeDataOneRowPerLeaseAsSubTableWithSettingsToo,
	        			REPORT_NAME reportName,
	               		int iRunNumber, String className) throws OException {
	        		
	        		Table tRawReport = Util.NULL_TABLE;
	        		Table tFormattedReport = Util.NULL_TABLE;
	        		try{   
	        			
	        			LIB.log("TRADE_LIST_QUORUM.getReportAndAddToScriptPanel: START", className);
	        			
	        			tRawReport = REPORT.GET_RAW_REPORT.TRADE_LIST_QUORUM.getRawReport(tTradeDataOneRowPerLeaseAsSubTableWithSettingsToo, reportName, iRunNumber, className);
 
	        			// convert the raw data/raw report into the formatting that the user wants
	        			boolean bWebServicesMode = false;
	        			tFormattedReport = REPORT.GET_REPORT.TRADE_LIST_QUORUM.getReport(tRawReport, bWebServicesMode, iRunNumber, className);

	        			// This is a generic function that works for all reports.  This will also add deal_num as a cell column
	        			COMMON_FUNC.addReportTableToScriptPanel(tScriptPanel, tFormattedReport, className);

	        			// Add formating to the script panel, including grouping/sorting and setting the column titles.  Do this as an explicit step, since it isn't automatic
	        			REPORT.GET_REPORT.TRADE_LIST_QUORUM.formatReport(tScriptPanel, bWebServicesMode, iRunNumber, className);
	        			
	        			LIB.log("TRADE_LIST_QUORUM.getReportAndAddToScriptPanel: END" + ", Num Rows Raw Data: " + LIB.safeGetNumRows(tRawReport) + ", Num Rows in Report: " + LIB.safeGetNumRows(tFormattedReport), className);
	        			
	        		} catch(Exception e ){               
	            		LIB.log("TRADE_LIST_QUORUM.getReportAndAddToScriptPanel", e, className);
	        		}
	        		tRawReport = LIB.destroyTable(tRawReport);
	        		tFormattedReport = LIB.destroyTable(tFormattedReport);
	        	}
			} // end class TRADE_LIST_QUORUM
	 
 			
        } // end class HELPER

    	 
   	 public static void populatePanelBasedOnReportTypeAndGetSourceDataFromExtraDataTable(Table tScriptPanel, Table tExtraData, REPORT_NAME reportName,
   			 int iRunNumber, String className) throws OException {
        	
        	try{
       		 
        		// Need to do this, since we are adding/removing rows
        		// this is not invoked immediately, instead it sets a flag which is looked at at the end of the script, so OK to set this now, i.e., at the start
        		tScriptPanel.scriptDataSetRefreshType(SCRIPT_PANEL_REFRESH_ENUM.SCRIPT_PANEL_REFRESH_ALL.toInt());
       		
	       		// and clear our any existing rows
	       		tScriptPanel.clearRows();
	       		
	       		// add a col, if needed, and then delete all other cols.  We are adding the one column for safety, as I think OpenLink may want always at least one column
	       		// this part may not, strictly speaking, be needed, but keep it anyway
	       		LIB.safeAddCol(tScriptPanel, GUICOLS.PANEL.SCRIPT_PANEL.GENERIC.DATA, COL_TYPE_ENUM.COL_CELL);
	       		LIB.safeColHide(tScriptPanel, GUICOLS.PANEL.SCRIPT_PANEL.GENERIC.DATA);
	       		
	       		// Delete all of the other columns, i.e., everything but our temp 'data' one
	       		int iNumCols = LIB.safeGetNumCols(tScriptPanel);
	       		for (int iColCounter = iNumCols; iColCounter >= 1; iColCounter--) {
	       			String sColName = tScriptPanel.getColName(iColCounter);
	       			if (Str.equal(sColName, GUICOLS.PANEL.SCRIPT_PANEL.GENERIC.DATA) != CONST_VALUES.VALUE_OF_TRUE) {
	       				LIB.safeDelCol(tScriptPanel, sColName);
	       			}
	       		}
	       		 
	       		// get this from the tExtraData Table
	       		// Don't destroy this, keep it as a sub-table of tExtraData Table
	       		Table tTradeDataOneRowPerLeaseAsSubTableWithSettingsToo = LIB.safeGetTable(tExtraData, GUICOLS.PANEL.EXTRA_DATA_TABLE.RAW_DATA_BY_UNIQUE_KEY_AND_SETTINGS, CONST_VALUES.ROW_TO_GET_OR_SET);
	       		 
	       		// at this point, we should have a Script Panel table with no rows and one 'data' column
	       		
	       		// We now are going to handle each type
	       		boolean bReportHandled = false;

//	       		if (reportName == REPORT_NAME.TRADE_LISTING) {
//	       			HELPER.TRADE_LISTING.getReportAndAddToScriptPanel(tScriptPanel, tTradeDataOneRowPerLeaseAsSubTableWithSettingsToo, reportName, iRunNumber, className);
//	       			bReportHandled = true;
//	       		}    

//	       		if (reportName == REPORT_NAME.INVENTORY_TRADE_LIST) {
//	       			HELPER.INVENTORY_TRADE_LIST.getReportAndAddToScriptPanel(tScriptPanel, tTradeDataOneRowPerLeaseAsSubTableWithSettingsToo, reportName, iRunNumber, className);
//	       			bReportHandled = true;
//	       		}    
//
//	       		if (reportName == REPORT_NAME.OTC_TRADE_LIST) {
//	       			HELPER.OTC_TRADE_LIST.getReportAndAddToScriptPanel(tScriptPanel, tTradeDataOneRowPerLeaseAsSubTableWithSettingsToo, reportName, iRunNumber, className);
//	       			bReportHandled = true;
//	       		}     
//
//	       		if (reportName == REPORT_NAME.EXCHANGE_TRADE_LIST) {
//	       			HELPER.EXCHANGE_TRADE_LIST.getReportAndAddToScriptPanel(tScriptPanel, tTradeDataOneRowPerLeaseAsSubTableWithSettingsToo, reportName, iRunNumber, className);
//	       			bReportHandled = true;
//	       		}     
//	       		
//	       		if (reportName == REPORT_NAME.EXCHANGE_POSITION_ALL ||
//	       				reportName == REPORT_NAME.EXCHANGE_POSITION_CCA ||
//	       				reportName == REPORT_NAME.EXCHANGE_POSITION_EUA ||
//	    	    	    reportName == REPORT_NAME.EXCHANGE_POSITION_RGGI ) {
//	       			HELPER.EXCHANGE_POSITION.getReportAndAddToScriptPanel(tScriptPanel, tTradeDataOneRowPerLeaseAsSubTableWithSettingsToo, reportName, iRunNumber, className);
//	       			bReportHandled = true;
//	       		}     

	       		if (reportName == REPORT_NAME.TRADE_LIST) {
	       			HELPER.TRADE_LIST.getReportAndAddToScriptPanel(tScriptPanel, tTradeDataOneRowPerLeaseAsSubTableWithSettingsToo, reportName, iRunNumber, className);
	       			bReportHandled = true;
	       		}     
	       		
	       		if (reportName == REPORT_NAME.TRADE_LIST_QUORUM) {
	       			HELPER.TRADE_LIST_QUORUM.getReportAndAddToScriptPanel(tScriptPanel, tTradeDataOneRowPerLeaseAsSubTableWithSettingsToo, reportName, iRunNumber, className);
	       			bReportHandled = true;
	       		}     
  
	       		// TODO
	       		// Add new reports here

	       		// do this for backup, in case the report was not handled.  
	       		// That said, this should never happen, i.e., all reports should be handled above
	       		if (bReportHandled == false) { 
	       			HELPER.TRADE_LIST.getReportAndAddToScriptPanel(tScriptPanel, tTradeDataOneRowPerLeaseAsSubTableWithSettingsToo, reportName, iRunNumber, className);
	       		}
	       		
	       		// This is for debug purposes... the hidden columns are hidden because users don't want to see them in the reports
	       		int iShowAllColumnsFlag = LIB.safeGetInt(tTradeDataOneRowPerLeaseAsSubTableWithSettingsToo, COLS.REPORT.RAW_BY_UNIQUE_KEY_REPORT_DATA.SETTING.SHOW_ALL_COLUMNS_FLAG, CONST_VALUES.ROW_TO_GET_OR_SET);
         	 
	       		if (iShowAllColumnsFlag == CONST_VALUES.VALUE_OF_TRUE) {
         			iNumCols = LIB.safeGetNumCols(tScriptPanel);
         			for (int iColCounter = 1; iColCounter <= iNumCols; iColCounter++) {
             			boolean bShowCol = true;
             			String sColName = tScriptPanel.getColName(iColCounter);
             			 
             			// Still keep this column hidden.  Since it is a duplicate from the Deal Number as a Cell col
             			if (Str.iEqual(sColName, COLS.REPORT.ORIGINAL_DEAL_NUM_COL_NAME) == 1) {
             				bShowCol = false;
             			}
             			if (bShowCol == true) {
             				tScriptPanel.colShow(iColCounter);
             			}
         			}
         		}
	   			
        	} catch(Exception e ){               
        		LIB.log("populatePanelBasedOnReportTypeAndGetSourceDataFromExtraDataTable", e, className);
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

     	public static void convertDealNumColFromIntToCell(Table tScriptPanel, String className) throws OException{
     		convertDealNumColFromIntToCellWithNamedColumns(tScriptPanel, COLS.REPORT.DEAL_NUM_COL_NAME, COLS.REPORT.ORIGINAL_DEAL_NUM_COL_NAME, className);
	   	}  	
     	
     	public static void convertDealNumColFromIntToCellWithNamedColumns(Table tScriptPanel, String sSourceDealNumColName, String sDestinationDealNumColName, String className) throws OException{
    		 
	       	try{
	       		
	       		// If column is named 'deal_num' and if it is an integer
	       		
	       		if (tScriptPanel.getColNum(sSourceDealNumColName) >= 1) {
	       			if (tScriptPanel.getColType(sSourceDealNumColName) == COL_TYPE_ENUM.COL_INT.toInt()) {
	       				// delete if there and re-add for no particular reason
	       				LIB.safeDelCol(tScriptPanel, sDestinationDealNumColName);
	       				// insert it before the 'deal_num' column
	       				tScriptPanel.insertCol(sDestinationDealNumColName, sSourceDealNumColName, COL_TYPE_ENUM.COL_INT);
	       				
	       				// Copy over the deal num using this method
	       				tScriptPanel.mathAddColConst(sSourceDealNumColName, 0, sDestinationDealNumColName);
	       				
	       				// delete the original column and add as a cell column, insert it before the 'deal_num_int' column
	       				LIB.safeDelCol(tScriptPanel, sSourceDealNumColName);
	       				tScriptPanel.insertCol(sSourceDealNumColName, sDestinationDealNumColName, COL_TYPE_ENUM.COL_CELL);
	       				
	       				// now populate the deal_num cell columns
	       				int iNumRows = LIB.safeGetNumRows(tScriptPanel);
	       				for (int iCounter = 1; iCounter <= iNumRows; iCounter++) {
	       					int iDealNum = LIB.safeGetInt(tScriptPanel, sDestinationDealNumColName, iCounter);
		       				if (iDealNum >= 1) {
			       				LIB.safeSetCellIntWithDoubleClickAndRightJustify(tScriptPanel, sSourceDealNumColName, iCounter, iDealNum);
		       				} else {
		       					LIB.safeSetCellString(tScriptPanel, sSourceDealNumColName, iCounter, "", "");
		       				}
	       				} 
	       				
	       				// hide the deal_num as an integer column
	       				LIB.safeColHide(tScriptPanel, sDestinationDealNumColName);
	       			}
	       		}
	       		 
	       	} catch(Exception e ){               
	       		LIB.log("convertDealNumColFromIntToCellWithNamedColumns", e, className);
	       	}
	   	}  	 
     	 
     	
     	public static void addReportTableToScriptPanel(Table tScriptPanel, Table tReportToAdd, String className) throws OException{
     		 
	       	try{
	       		
	       		// this should not be needed, as the table is likely already emptied of rows. Add it just in case.
	       		tScriptPanel.clearRows();
	       		
	       		// Lots of ways we might potentially copy from one table to another, including select * from x
	       		// chose this way for no particular reason
	       		int iNumCols = LIB.safeGetNumCols(tReportToAdd);
	       		for (int iCounter = 1; iCounter <= iNumCols; iCounter++) {
	       			String sColName = tReportToAdd.getColName(iCounter);
	       			int iColType = tReportToAdd.getColType(iCounter);
	       			LIB.safeAddCol(tScriptPanel, sColName, COL_TYPE_ENUM.fromInt(iColType));
	       			// This probable isn't needed, but include it anyway.  It isn't needed, because we'll explicitly set the formatting later
	       			tReportToAdd.copyColFormat(sColName, tScriptPanel, sColName);
	       		}
	       		
	       		tReportToAdd.copyRowAddAllByColName(tScriptPanel);
	       		
	       		// special handling for deal number col name
	       		convertDealNumColFromIntToCell(tScriptPanel, className); 
	       		 
	   			// now delete the temporary column, if found
	       		// Need to do this here *BEFORE* doing the formatting/grouping
	   			LIB.safeDelCol(tScriptPanel, GUICOLS.PANEL.SCRIPT_PANEL.GENERIC.DATA);
	   			
	   			// Turn off Sums and Group Sums as a default. Meaning, the format function may add these back later
	   			iNumCols = LIB.safeGetNumCols(tScriptPanel);
        		for (int iColCounter = 1; iColCounter <= iNumCols; iColCounter++) {
        			int iColType = tScriptPanel.getColType(iColCounter);
	       			try {
	       				if (iColType == COL_TYPE_ENUM.COL_DOUBLE.toInt()) {
			       			tScriptPanel.setColSumStatus(iColCounter, 0);
			       			tScriptPanel.setColGroupSumStatus(iColCounter, 0);
	       				}
	       			} catch (Exception e) {
	       				// Don't Log this
	       			}
        		}

	       	} catch(Exception e ){               
	       		LIB.log("addReportTableToScriptPanel", e, className);
	       	}
	   	}  	 
     	
     	public static void showTransactionGivenDealNum(int iDealNum, String className) throws OException{
     		 
     		Table tData = Util.NULL_TABLE;
	       	try{
    			LIB.log("About to pop up deal " + iDealNum, className);
    			
    			// get tran num.  Always get from the database, even if tran num happens to be there as a column, just to make the logic simpler, i.e., just one way to do it).
    			tData = Table.tableNew();
    			LIB.safeAddCol(tData, COLS.REPORT.TRAN_NUM_COL_NAME, COL_TYPE_ENUM.COL_INT);
    			String sSQL = "SELECT tran_num FROM ab_tran WHERE current_flag = 1 AND deal_tracking_num = " + iDealNum;
    			LIB.execISql(tData, sSQL, false, className);
    			
    			if (LIB.safeGetNumRows(tData) >= 1) {
    				int iTranNum = LIB.safeGetInt(tData, COLS.REPORT.TRAN_NUM_COL_NAME, CONST_VALUES.ROW_TO_GET_OR_SET);
    				OConsole.oprint(", Tran Num is: " + iTranNum + ", about to call Transaction.viewTransaction");
    				
    				Transaction tran = Transaction.retrieve(iTranNum);
    				tran.viewTransaction();
    				// Don't destroy the transaction.  if you do, then it won't pop up. not sure why that is the case, but testing confirms that it is
    				boolean bDestroyTran = false;
    				if (bDestroyTran == true) {
    					tran = LIB.destroyTran(tran);
    				}
    				
    				// tried this and it did not work for Transaction Input.  It worked only for Transaction Viewer
    				// keeping the code here for now, in case I want to try again in the future
    				boolean bUseAfeApproach = false;
    				if (bUseAfeApproach == true) {
        				Table arg_table = Afe.createServiceRequestArgTable();
        				int iMaxRow = arg_table.addRow();
					 
        				LIB.safeSetString(arg_table, "afe_arg_name", iMaxRow, "tran_num");
						// set the col type as an integer
						// COL_INT 0, COL_DOUBLE 1, COL_TRAN 13, COL_DATE 19, COL_STRING 2
						LIB.safeSetInt(arg_table, "afe_arg_type", iMaxRow, COL_TYPE_ENUM.COL_INT.toInt());
						// the value is always a string, i.e., need to
						// convert this integer value to a string.
						LIB.safeSetString(arg_table, "afe_arg_value", iMaxRow, Str.intToStr(iTranNum));
						
						Afe.issueServiceRequestWithTableArg(AFE_SERVICE.TRANSACTION_INPUT, arg_table);
						arg_table = LIB.destroyTable(arg_table);
    				}
    			}

	       	} catch(Exception e ){               
	       		LIB.log("showTransactionGivenDealNum", e, className);
	       	}
			tData = LIB.destroyTable(tData);
	   	}  	 
    	 
    }  // end class COMMON_FUNC
    
	
    public static class GUI { 

    	public static void addButton(Table tScriptPanel, IBUTTON button, String className) throws OException{

        	try{
        		
        		String widget_options = "label=" + button.getLabel();
        		tScriptPanel.scriptDataAddWidget(button.getName(), 
        				SCRIPT_PANEL_WIDGET_TYPE_ENUM.SCRIPT_PANEL_PUSHBUTTON_WIDGET.toInt(), 
        				button.getPosition(), widget_options);
        	} catch(Exception e ){               
        		LIB.log("addButton", e, className);
        	}
    	} 
    	
    	public static void addCalendar(Table tScriptPanel, ICALENDAR calendar, String className) throws OException{

        	try{
        		String widget_options = "label=" + calendar.calendarDefaultDate();
        		tScriptPanel.scriptDataAddWidget(calendar.getName(), 
        				SCRIPT_PANEL_WIDGET_TYPE_ENUM.SCRIPT_PANEL_CALENDAR_WIDGET.toInt(), 
        				calendar.getPosition(), widget_options);
        	} catch(Exception e ){               
        		LIB.log("addCalendar", e, className);
        	}
    	} 
    	 
		public static void modifyCalendarWithUpdatedDate(Table tScriptPanel, ICALENDAR calendar, int iNewDate, String className) throws OException{
	
	    	try{
	    		String widget_options = "label=" + OCalendar.formatJd(iNewDate, DATE_FORMAT.DATE_FORMAT_DMLY_NOSLASH);
	    		
	    		tScriptPanel.scriptDataModifyWidget(calendar.getName(), 
	    				SCRIPT_PANEL_WIDGET_TYPE_ENUM.SCRIPT_PANEL_CALENDAR_WIDGET.toInt(), 
	    				calendar.getPosition(), widget_options);
	    	} catch(Exception e ){               
	    		LIB.log("modifyCalendarWithUpdatedDate", e, className);
	    	}
		}   

		public static void modifyButtonWithMakeRed(Table tScriptPanel, IBUTTON button, String className) throws OException{
	
	    	try{
	    		String widget_options = "bg=RED,fg=BLACK,label=" + "***" + button.getLabel() + "***";
	    		
	    		tScriptPanel.scriptDataModifyWidget(button.getName(), 
	    				SCRIPT_PANEL_WIDGET_TYPE_ENUM.SCRIPT_PANEL_PUSHBUTTON_WIDGET.toInt(), 
	    				button.getPosition(), widget_options);
	    	} catch(Exception e ){               
	    		LIB.log("modifyButtonWithMakeRed", e, className);
	    	}
		}   
		
		public static void modifyButtonWithUndoMakeRed(Table tScriptPanel, IBUTTON button, String className) throws OException{
	
	    	try{
	    		// These are the colors available
//	    		AQUAMARINE, BLACK, BLUE, CYAN, CORNFLOWERBLUE, CORAL, DARKGREEN, GRAY, GREY,
//	    		GOLD, GREEN, KHAKI, LIGHTGRAY, LIGHTGREY, LIGHTSTEELBLUE, LIGHTBLUE, LIGHTPURPLE,
//	    		MAGENTA, MEDIUMAQUAMARINE, MEDIUMGOLDENROD, MEDIUMORCHID, ORANGE, PLUM,
//	    		PALEGREEN, PINK, RED, SIENNA, SKYBLUE, SALMON, THISTLE, VERYLIGHTGRAY, VERYLIGHTGREY, VIOLET, WHITE, WHEAT, YELLOW, YELLOWGREEN
	    		
	    		String widget_options = "bg=LIGHTGREY,fg=BLACK,label=" + button.getLabel();
	    		
	    		tScriptPanel.scriptDataModifyWidget(button.getName(), 
	    				SCRIPT_PANEL_WIDGET_TYPE_ENUM.SCRIPT_PANEL_PUSHBUTTON_WIDGET.toInt(), 
	    				button.getPosition(), widget_options);
	    	} catch(Exception e ){               
	    		LIB.log("modifyButtonWithMakeRed", e, className);
	    	}
		}   

    	public static void addLabel(Table tScriptPanel, ILABEL label, String className) throws OException{

        	try{
        		String widget_options = "label=" + label.getLabel();
        		tScriptPanel.scriptDataAddWidget(label.getName(), 
        				SCRIPT_PANEL_WIDGET_TYPE_ENUM.SCRIPT_PANEL_LABEL_WIDGET.toInt(), 
        				label.getPosition(), widget_options);
        	} catch(Exception e ){               
        		LIB.log("addLabel", e, className);
        	}
    	} 

    	public static void addLabelRED(Table tScriptPanel, ILABEL label, String className) throws OException{

        	try{
        		String widget_options = "bg=RED,label=" + label.getLabel();
        		tScriptPanel.scriptDataAddWidget(label.getName(), 
        				SCRIPT_PANEL_WIDGET_TYPE_ENUM.SCRIPT_PANEL_LABEL_WIDGET.toInt(), 
        				label.getPosition(), widget_options);
        	} catch(Exception e ){               
        		LIB.log("addLabel", e, className);
        	}
    	} 
    	
    	public static void addLabelBLUE(Table tScriptPanel, ILABEL label, String className) throws OException{

        	try{
        	//	AQUAMARINE, BLACK, BLUE, CYAN, CORNFLOWERBLUE, CORAL, DARKGREEN, GRAY, GREY, 
        		// GOLD, GREEN, KHAKI, LIGHTGRAY, LIGHTGREY, LIGHTSTEELBLUE, LIGHTBLUE, LIGHTPURPLE, 
        		// MAGENTA, MEDIUMAQUAMARINE, MEDIUMGOLDENROD, MEDIUMORCHID, ORANGE, PLUM, 
        		// PALEGREEN, PINK, RED, SIENNA, SKYBLUE, SALMON, THISTLE, VERYLIGHTGRAY, VERYLIGHTGREY, VIOLET, WHITE, WHEAT, YELLOW, YELLOWGREEN 

        		
        		String widget_options = "fg=CORNFLOWERBLUE,label=" + label.getLabel();
        		tScriptPanel.scriptDataAddWidget(label.getName(), 
        				SCRIPT_PANEL_WIDGET_TYPE_ENUM.SCRIPT_PANEL_LABEL_WIDGET.toInt(), 
        				label.getPosition(), widget_options);
        	} catch(Exception e ){               
        		LIB.log("addLabel", e, className);
        	}
    	} 
		
	    public static void addLabelWithSetTextToShowUser(Table tScriptPanel, ILABEL label, String sTextToShowUser, String className) throws OException{
	
	    	try{
	    		String widget_options = "label=" + sTextToShowUser;
	    		tScriptPanel.scriptDataAddWidget(label.getName(), 
	    				SCRIPT_PANEL_WIDGET_TYPE_ENUM.SCRIPT_PANEL_LABEL_WIDGET.toInt(), 
	    				label.getPosition(), widget_options);
	    	} catch(Exception e ){               
	    		LIB.log("addLabelWithSetTextToShowUser", e, className);
	    	}
		} 
	    
		public static void modifyLabelWithTextToShowUser(Table tScriptPanel, ILABEL label, String sTextToShowUser, String className) throws OException{
	
	    	try{
	    		String widget_options = "label=" + sTextToShowUser;
	    		
	    		tScriptPanel.scriptDataModifyWidget(label.getName(), 
	    				SCRIPT_PANEL_WIDGET_TYPE_ENUM.SCRIPT_PANEL_LABEL_WIDGET.toInt(), 
	    				label.getPosition(), widget_options);
	    	} catch(Exception e ){               
	    		LIB.log("modifyLabelWithTextToShowUser", e, className);
	    	}
		}  
		
		public static void modifyLabelWithWidgetOptions(Table tScriptPanel, ILABEL label, String widget_options, String className) throws OException{
	
	    	try{
	    		tScriptPanel.scriptDataModifyWidget(label.getName(), 
	    				SCRIPT_PANEL_WIDGET_TYPE_ENUM.SCRIPT_PANEL_LABEL_WIDGET.toInt(), 
	    				label.getPosition(), widget_options);
	    	} catch(Exception e ){               
	    		LIB.log("modifyLabelWithWidgetOptions", e, className);
	    	}
		} 
     	
    	public static void _addComboxBox(Table tScriptPanel, ICOMBOBOX comboBox, Table tComboBoxMenu, boolean bIsMultiSelectFlag, String sDefaultValue, String className) throws OException{

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
            		int iRowNumOfItem = tComboBoxMenu.unsortedFindString(GUICOLS.PANEL.COMBOBOX.ITEM, sDefaultValue, SEARCH_CASE_ENUM.CASE_SENSITIVE);
            		if (iRowNumOfItem < 1) {
            			iRowNumOfItem = 1;
            		}
            		LIB.safeSetInt(tComboBoxSelectMenu, COL_NAME_TO_USE, iMaxRow, iRowNumOfItem);

            		// We need to set the label to the String value, for the user to see the selected item
            		String sValueToDisplay = LIB.safeGetString(tComboBoxMenu, GUICOLS.PANEL.COMBOBOX.ITEM, iRowNumOfItem);

            		String widget_options = "label=" + sValueToDisplay; 
            		if (bIsMultiSelectFlag == true) {
            			widget_options = FORMAT.MULTISELECT + "," + widget_options;
            		}
        			
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
        		LIB.log("addComboxBox", e, className);
        	}
    	} 
    	
    	public static void addSingleSelectComboxBox(Table tScriptPanel, ICOMBOBOX comboBox, Table tComboBoxMenu, String sDefaultValue, String className) throws OException{
    		_addComboxBox(tScriptPanel, comboBox, tComboBoxMenu, false, sDefaultValue, className);
    	} 
    	public static void addMultiSelectComboxBox(Table tScriptPanel, ICOMBOBOX comboBox, Table tComboBoxMenu, String sDefaultValue, String className) throws OException{
    		_addComboxBox(tScriptPanel, comboBox, tComboBoxMenu, true, sDefaultValue, className);
    	}
 
    	   
    }  // end class GUI

 
	public static interface IBUTTON {
		public String getName();
		public String getLabel();
		public String getPosition();
	}
	
	public static interface ICALENDAR {
		public String getName();
		public String getLabel(); 
		public String calendarDefaultDate();
		public String getPosition();
	}

	public static interface ILABEL {
		public String getName();
		public String getLabel();
		public String getPosition();
	}

	public static interface ICOMBOBOX {
		public String getName();
		public String getLabel();
		public String getPosition();
	}
	
    public static class GUICOLS {     
  	  
		 public static class GUICREATE_TABLE {     

			  public static class PANEL { 
		         	  
		        	public static Table createComboBoxTable(String className) throws OException{
		              	Table tReturn = Util.NULL_TABLE;
		                    try{
		                      tReturn = Table.tableNew();

		                	  LIB.safeAddCol(tReturn, GUICOLS.PANEL.COMBOBOX.ITEM_ID, COL_TYPE_ENUM.COL_INT);
		                	  LIB.safeAddCol(tReturn, GUICOLS.PANEL.COMBOBOX.ITEM, COL_TYPE_ENUM.COL_STRING);
		                	  
		                	  LIB.safeColHide(tReturn, GUICOLS.PANEL.COMBOBOX.ITEM_ID);
		                	  
		                    } catch(Exception e ){               
		                    	LIB.log("createComboBoxTable", e, className);
		                    }
		                     
		    				return tReturn; 
		              } 


		        	public static Table createExtraDataTableAndAddOneRow(String className) throws OException{
		              	Table tReturn = Util.NULL_TABLE;
		                    try{
		                      tReturn = Table.tableNew();

		                	  LIB.safeAddCol(tReturn, GUICOLS.PANEL.EXTRA_DATA_TABLE.SUB_TABLE, COL_TYPE_ENUM.COL_TABLE);
		                	  LIB.safeAddCol(tReturn, GUICOLS.PANEL.EXTRA_DATA_TABLE.RAW_DATA_BY_UNIQUE_KEY_AND_SETTINGS, COL_TYPE_ENUM.COL_TABLE);
		                	    	
		                	  LIB.safeAddCol(tReturn, GUICOLS.PANEL.EXTRA_DATA_TABLE.VERSION, COL_TYPE_ENUM.COL_STRING);
		                	  
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

		                	  LIB.safeAddCol(tReturn, GUICOLS.PANEL.EXTRA_DATA_TABLE.SETTINGS.RUN_NUMBER, COL_TYPE_ENUM.COL_INT);
		                	  LIB.safeAddCol(tReturn, GUICOLS.PANEL.EXTRA_DATA_TABLE.SETTINGS.RUN_DATE, COL_TYPE_ENUM.COL_INT);
		                	  LIB.safeAddCol(tReturn, GUICOLS.PANEL.EXTRA_DATA_TABLE.SETTINGS.RUN_DATE_STRING, COL_TYPE_ENUM.COL_STRING);

		                	  tReturn.addRow();

		                    } catch(Exception e ){               
		                    	LIB.log("createExtraDataSubTableAndAddOneRow", e, className);
		                    }
		                     
		    				return tReturn; 
		              }  
		        	
		        } // END public static class PANEL
			  
		 } // end class GUICREATE_TABLE
		 
		 
	   public static class PANEL {     
		      
	    	// These columns are cosmetic.. can change the names
	    	public static class COMBOBOX {     
	        	public static final String ITEM_ID = "item_id"; //COL_INT  
	        	public static final String ITEM = "item"; //COL_STRING
		    	public static class EXTRA {     
		        	public static final String DESCRIPTION = "description"; //COL_STRING
		    	}
	    	}

	    	// These columns are cosmetic.. can change the names
	    	public static class SCRIPT_PANEL{     
		    	public static class GENERIC{     
		    		public final static String DATA = "data";
		    	}
	    	}

	    	public static class EXTRA_DATA_TABLE {     
	        	public static final String SUB_TABLE = "sub_table";
	        	public static final String RAW_DATA_BY_UNIQUE_KEY_AND_SETTINGS = "raw_data_by_unique_key_and_settings";
	        	public static final String VERSION = "version";
	        	public static class SETTINGS {     
	            	public static final String RUN_NUMBER = "run_number";
	            	public static final String RUN_DATE = "run_date";
	            	public static final String RUN_DATE_STRING = "run_date_string";
	        	}
	        }
	    }
	   
 } // END GUICOLS
 
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
		
		public static final int TRANSACTION_VIEWER = 76;
		public static final int TRANSACTION_INPUT = 77;
	}
    
    public enum SUBSCRIPTION implements ID_VALUE_AND_NAME_ENUM {
    	// was going to have multiple types of 'sends' but for now, it seems, just having one of them
    	// TODO:  Make sure this is unique for all new Dashboards
    	ALL_DATA ("All_Data_DRWTradeListingDashboard", 1), 
    	  ; 
    	  
    	  private final String sName;
      	  private final int iValue; 
    		  
      	SUBSCRIPTION(String sName, int iValue) {
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
    
	public static class FORMAT {
		public static class JUSTIFY {
			public static final String RIGHT = "just=RIGHT";
			public static final String CENTER = "just=CENTER";
		}

		public static final String DOUBLE_CLICK = "dblclick=TRUE";
		public static final String DOUBLE_CLICK_AND_RIGHT_JUST = FORMAT.JUSTIFY.RIGHT + "," + FORMAT.DOUBLE_CLICK;
		public static final String MULTISELECT = "multiselect=TRUE";
		public static final String MULTISELECT_AND_LABEL = "multiselect=TRUE,label=";
		
//		public static final String GREEN_FOR_PASS = "bg=GREEN,fg=BLACK";
//		public static final String RED_FOR_FAIL = "bg=RED,fg=BLACK";
//		public static final String YELLOW_FOR_WARNING = "bg=YELLOW,fg=BLACK";
	}

}

