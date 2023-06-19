package com.drw.dashboard;
 
import com.olf.openjvs.Afe;
import com.olf.openjvs.Afs;
import com.olf.openjvs.DBaseTable;
import com.olf.openjvs.IContainerContext;
import com.olf.openjvs.OCalendar;
import com.olf.openjvs.OConsole;
import com.olf.openjvs.OException;
import com.olf.openjvs.PluginCategory;
import com.olf.openjvs.PluginType;
import com.olf.openjvs.Ref;
import com.olf.openjvs.SimResult;
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
import com.olf.openjvs.enums.SIMULATION_RUN_TYPE;
import com.olf.openjvs.enums.TABLE_VIEWER_MODE;
import com.olf.openjvs.enums.TOOLSET_ENUM;
import com.olf.openjvs.enums.TRAN_STATUS_ENUM;

/* 
 
Name:  DRW_EOD_DashboardBaseClass.java
   
Deployment instructions
This script is not used directly.  It is a library for other scripts.
   
Revision History

19-Apr-2023 Brian New script
   
 */

@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_GENERIC)
@PluginType(SCRIPT_TYPE_ENUM.MAIN_SCRIPT)
public class DRW_EOD_DashboardBaseClass 
{

    public static void main(IContainerContext context, SIMULATION_RUN_TYPE revalRunType, REFRESH_INTERVAL defaultRefreshInterval, String className) throws OException
    {
    	Table tExtraData = Util.NULL_TABLE;
    	
    	try {
    		LIB.log("RUNNING for reval type: " + revalRunType.toString(), className);
        	
        	//Table argt = context.getArgumentsTable();
        	// We use returnt, not 'argt'
        	Table returnt = context.getReturnTable();
        	
        	int iCallBackType = returnt.scriptDataGetCallbackType();
        	SCRIPT_PANEL_CALLBACK_TYPE_ENUM callbackType = SCRIPT_PANEL_CALLBACK_TYPE_ENUM.fromInt(iCallBackType);

        	// i.e., this will not the portfolios with no trades (i.e., 0 trads)
        	PORTFOLIOS_TO_SHOW portfoliosToShow = PORTFOLIOS_TO_SHOW.WITH_TRADES;
        	try {
        		String sPortfoliosToShow = returnt.scriptDataGetWidgetString(COMBOBOX.COMBOBOX2.getName());
        		if (Str.iEqual(sPortfoliosToShow, PORTFOLIOS_TO_SHOW.ALL.getName()) == CONST_VALUES.VALUE_OF_TRUE) {
        			portfoliosToShow = PORTFOLIOS_TO_SHOW.ALL;
        		}
        	} catch (Exception e) {
        		// don't log this
        	}
        	  

        	if (callbackType == SCRIPT_PANEL_CALLBACK_TYPE_ENUM.SCRIPT_PANEL_INIT) {
            	int iRunDate = OCalendar.today();
        		GUI.INIT_HANDLER.doEverythingInit(iRunDate, revalRunType, defaultRefreshInterval, className);
        	} 
        	// Do *NOT* do this if run for the first time, i.e., if 'init'
        	if (callbackType != SCRIPT_PANEL_CALLBACK_TYPE_ENUM.SCRIPT_PANEL_INIT) {
        		
            	// Get Extra data
            	tExtraData = returnt.scriptDataGetExtraDataTable();
            	Table tExtraDataSubTable = LIB.safeGetTable(tExtraData, COLS.EXTRA_DATA_TABLE.SUB_TABLE, CONST_VALUES.ROW_TO_GET_OR_SET);
            	int iRunDate = LIB.safeGetInt(tExtraDataSubTable, COLS.EXTRA_DATA_TABLE.SETTINGS.RUN_DATE, CONST_VALUES.ROW_TO_GET_OR_SET);
            	String sRunDate = LIB.safeGetString(tExtraDataSubTable, COLS.EXTRA_DATA_TABLE.SETTINGS.RUN_DATE_STRING, CONST_VALUES.ROW_TO_GET_OR_SET);

            	if (callbackType == SCRIPT_PANEL_CALLBACK_TYPE_ENUM.SCRIPT_PANEL_PUSHBUTTON) {
            		GUI.BUTTON_HANDLER.doEverythingButton(returnt, tExtraData, revalRunType, portfoliosToShow, iRunDate, sRunDate, className);
            	}
            	if (callbackType == SCRIPT_PANEL_CALLBACK_TYPE_ENUM.SCRIPT_PANEL_TIMER) {
            		GUI.TIMER_HANDLER.doEverythingTimer(returnt, revalRunType, portfoliosToShow, iRunDate, sRunDate, className);
            	}
            	if (callbackType == SCRIPT_PANEL_CALLBACK_TYPE_ENUM.SCRIPT_PANEL_COMBOBOX) {
            		GUI.COMBOBOX_HANDLER.doEverythingComboBox(returnt, portfoliosToShow, iRunDate, sRunDate, className);
            	}
            	if (callbackType == SCRIPT_PANEL_CALLBACK_TYPE_ENUM.SCRIPT_PANEL_DBLCLICK) {
            		GUI.DOUBLE_CLICK_HANDLER.doEverythingDoubleClick(returnt, revalRunType, portfoliosToShow, iRunDate, sRunDate, className);
            	}
            	
        	} // END        	if (callbackType != SCRIPT_PANEL_CALLBACK_TYPE_ENUM.SCRIPT_PANEL_INIT) 
     		
    	} catch (Exception e) {
    		LIB.log("main", e, className);
    	} 
    }
  
    public static class CREATE_TABLE { 

    	public static Table createTradeCountByPortfolio(String className) throws OException{
          	Table tReturn = Util.NULL_TABLE;
            try{
            	tReturn = Table.tableNew();
            	LIB.safeAddCol(tReturn, COLS.PORTFOLIO_LIST_WITH_TRADE_COUNT.PORTFOLIO_ID, COL_TYPE_ENUM.COL_INT);
            	LIB.safeAddCol(tReturn, COLS.PORTFOLIO_LIST_WITH_TRADE_COUNT.NUM_TRADES, COL_TYPE_ENUM.COL_INT);

            	LIB.safeSetColFormatAsRef(tReturn, COLS.PORTFOLIO_LIST_WITH_TRADE_COUNT.PORTFOLIO_ID, SHM_USR_TABLES_ENUM.PORTFOLIO_TABLE);
          	   
            } catch(Exception e ){               
                 LIB.log("createTradeCountByPortfolio", e, className);
            }
             
			return tReturn; 
          } 

    	public static Table createSimHeaderTable(String className) throws OException{
          	Table tReturn = Util.NULL_TABLE;
            try{
            	tReturn = Table.tableNew();
            	LIB.safeAddCol(tReturn, COLS.SIM_HEADER.SIM_RUN_ID, COL_TYPE_ENUM.COL_INT);
            	LIB.safeAddCol(tReturn, COLS.SIM_HEADER.SIM_DEF_ID, COL_TYPE_ENUM.COL_INT);
            	LIB.safeAddCol(tReturn, COLS.SIM_HEADER.GEN_TIME, COL_TYPE_ENUM.COL_DATE_TIME);
            	LIB.safeAddCol(tReturn, COLS.SIM_HEADER.RUN_DATE, COL_TYPE_ENUM.COL_INT);
            	LIB.safeAddCol(tReturn, COLS.SIM_HEADER.REVAL_TYPE, COL_TYPE_ENUM.COL_INT);
            	LIB.safeAddCol(tReturn, COLS.SIM_HEADER.REFERENCE, COL_TYPE_ENUM.COL_STRING);
            	LIB.safeAddCol(tReturn, COLS.SIM_HEADER.PORTFOLIO, COL_TYPE_ENUM.COL_INT);
        	              	  
            	LIB.safeSetColFormatAsDate(tReturn, COLS.SIM_HEADER.RUN_DATE);
            	LIB.safeSetColFormatAsRef(tReturn, COLS.SIM_HEADER.PORTFOLIO, SHM_USR_TABLES_ENUM.PORTFOLIO_TABLE);
            	LIB.safeSetColFormatAsRef(tReturn, COLS.SIM_HEADER.REVAL_TYPE, SHM_USR_TABLES_ENUM.REVAL_TYPE_TABLE);
          	   
            } catch(Exception e ){               
                 LIB.log("createSimHeaderTable", e, className);
            }
             
			return tReturn; 
          } 
    	
    	public static Table createSimBlobSizeTable(String className) throws OException{
          	Table tReturn = Util.NULL_TABLE;
            try{
            	tReturn = Table.tableNew();
            	LIB.safeAddCol(tReturn, COLS.SIM_BLOB_SIZE.SIM_RUN_ID, COL_TYPE_ENUM.COL_INT); 
            	LIB.safeAddCol(tReturn, COLS.SIM_BLOB_SIZE.BLOB_SIZE_MEGS, COL_TYPE_ENUM.COL_DOUBLE);
                
            } catch(Exception e ){               
                 LIB.log("createSimBlobSizeTable", e, className);
            }
             
			return tReturn; 
          }  

    	public static Table createPortfolioTable(String className) throws OException{
          	Table tReturn = Util.NULL_TABLE;
            try{
              tReturn = Table.tableNew();
        	  LIB.safeAddCol(tReturn, COLS.PORTFOLIO.PORTFOLIO_ID, COL_TYPE_ENUM.COL_INT);
        	  LIB.safeAddCol(tReturn, COLS.PORTFOLIO.PORTFOLIO, COL_TYPE_ENUM.COL_STRING);
          	   
            } catch(Exception e ){               
            	LIB.log("createPortfolioTable", e, className);
            }
             
			return tReturn; 
          } 

    	public static Table createRawReportTable(String className) throws OException{
          	Table tReturn = Util.NULL_TABLE;
          	
            try{
              tReturn = Table.tableNew();
        	  LIB.safeAddCol(tReturn, COLS.RAW_REPORT.PORTFOLIO_ID, COL_TYPE_ENUM.COL_INT);
        	  LIB.safeAddCol(tReturn, COLS.RAW_REPORT.PORTFOLIO, COL_TYPE_ENUM.COL_STRING);
        	  
        	  LIB.safeAddCol(tReturn, COLS.RAW_REPORT.RUN_DATE, COL_TYPE_ENUM.COL_INT);
        	  LIB.safeAddCol(tReturn, COLS.RAW_REPORT.NUM_TRADES, COL_TYPE_ENUM.COL_INT);

        	  // Since this is raw data that nobody sees... add all reval types to it as columns.  Later, we'll populate and use just the ones we need

        	  LIB.safeAddCol(tReturn, COLS.RAW_REPORT.EOD.REVAL_TYPE, COL_TYPE_ENUM.COL_INT);
        	  LIB.safeAddCol(tReturn, COLS.RAW_REPORT.EOD.SIM_DEF_ID, COL_TYPE_ENUM.COL_INT);
        	  LIB.safeAddCol(tReturn, COLS.RAW_REPORT.EOD.SIM_DEF_NAME, COL_TYPE_ENUM.COL_STRING);
        	  LIB.safeAddCol(tReturn, COLS.RAW_REPORT.EOD.SIM_RUN_ID, COL_TYPE_ENUM.COL_INT);
        	  // convert it to a String, i.e., from a date
        	  LIB.safeAddCol(tReturn, COLS.RAW_REPORT.EOD.GEN_TIME, COL_TYPE_ENUM.COL_STRING);
        	  LIB.safeAddCol(tReturn, COLS.RAW_REPORT.EOD.SIZE, COL_TYPE_ENUM.COL_DOUBLE); 
        	  
        	  LIB.safeAddCol(tReturn, COLS.RAW_REPORT.PRELIM_EOD.REVAL_TYPE, COL_TYPE_ENUM.COL_INT);
        	  LIB.safeAddCol(tReturn, COLS.RAW_REPORT.PRELIM_EOD.SIM_DEF_ID, COL_TYPE_ENUM.COL_INT);
        	  LIB.safeAddCol(tReturn, COLS.RAW_REPORT.PRELIM_EOD.SIM_DEF_NAME, COL_TYPE_ENUM.COL_STRING);
        	  LIB.safeAddCol(tReturn, COLS.RAW_REPORT.PRELIM_EOD.SIM_RUN_ID, COL_TYPE_ENUM.COL_INT);
        	  // convert it to a String, i.e., from a date
        	  LIB.safeAddCol(tReturn, COLS.RAW_REPORT.PRELIM_EOD.GEN_TIME, COL_TYPE_ENUM.COL_STRING);
        	  LIB.safeAddCol(tReturn, COLS.RAW_REPORT.PRELIM_EOD.SIZE, COL_TYPE_ENUM.COL_DOUBLE); 

        	  LIB.safeAddCol(tReturn, COLS.RAW_REPORT.PRELIM_UNIVERAL.REVAL_TYPE, COL_TYPE_ENUM.COL_INT);
        	  LIB.safeAddCol(tReturn, COLS.RAW_REPORT.PRELIM_UNIVERAL.SIM_DEF_ID, COL_TYPE_ENUM.COL_INT);
        	  LIB.safeAddCol(tReturn, COLS.RAW_REPORT.PRELIM_UNIVERAL.SIM_DEF_NAME, COL_TYPE_ENUM.COL_STRING);
        	  LIB.safeAddCol(tReturn, COLS.RAW_REPORT.PRELIM_UNIVERAL.SIM_RUN_ID, COL_TYPE_ENUM.COL_INT);
        	  // convert it to a String, i.e., from a date
        	  LIB.safeAddCol(tReturn, COLS.RAW_REPORT.PRELIM_UNIVERAL.GEN_TIME, COL_TYPE_ENUM.COL_STRING);
        	  LIB.safeAddCol(tReturn, COLS.RAW_REPORT.PRELIM_UNIVERAL.SIZE, COL_TYPE_ENUM.COL_DOUBLE); 
 
        	  LIB.safeSetColFormatAsDate(tReturn, COLS.RAW_REPORT.RUN_DATE);
        	  LIB.safeSetColFormatAsRef(tReturn, COLS.RAW_REPORT.EOD.REVAL_TYPE, SHM_USR_TABLES_ENUM.REVAL_TYPE_TABLE);
        	  LIB.safeSetColFormatAsRef(tReturn, COLS.RAW_REPORT.PRELIM_EOD.REVAL_TYPE, SHM_USR_TABLES_ENUM.REVAL_TYPE_TABLE);
        	  LIB.safeSetColFormatAsRef(tReturn, COLS.RAW_REPORT.PRELIM_UNIVERAL.REVAL_TYPE, SHM_USR_TABLES_ENUM.REVAL_TYPE_TABLE);
              	 
            } catch(Exception e ){               
                	LIB.log("createRawReportTable", e, className);
            }
             
			return tReturn; 
    	} 

    	public static Table createScriptPanelTable(SIMULATION_RUN_TYPE revalRunType, String className) throws OException{
          	Table tReturn = Util.NULL_TABLE;
                try{
                  tReturn = Table.tableNew();
            	  LIB.safeAddCol(tReturn, COLS.SCRIPT_PANEL.RUN_DATE, COL_TYPE_ENUM.COL_CELL);
            	  LIB.safeAddCol(tReturn, COLS.SCRIPT_PANEL.PORTFOLIO_ID, COL_TYPE_ENUM.COL_CELL);
            	  LIB.safeAddCol(tReturn, COLS.SCRIPT_PANEL.PORTFOLIO, COL_TYPE_ENUM.COL_CELL);
            	  LIB.safeAddCol(tReturn, COLS.SCRIPT_PANEL.NUM_TRADES, COL_TYPE_ENUM.COL_CELL);

            	  if (revalRunType == SIMULATION_RUN_TYPE.P_EOD_SIM_TYPE) {
                	  LIB.safeAddCol(tReturn, COLS.SCRIPT_PANEL.PRELIM_EOD_COMPLETE, COL_TYPE_ENUM.COL_INT);
                	  LIB.safeAddCol(tReturn, COLS.SCRIPT_PANEL.PRELIM_UNIVERAL_COMPLETE, COL_TYPE_ENUM.COL_INT);
                	  
                	  tReturn.setColFormatAsBool(COLS.SCRIPT_PANEL.PRELIM_EOD_COMPLETE);
                	  tReturn.setColFormatAsBool(COLS.SCRIPT_PANEL.PRELIM_UNIVERAL_COMPLETE);
                	  
                	  LIB.safeAddCol(tReturn, COLS.SCRIPT_PANEL.PRELIM_EOD.GEN_TIME, COL_TYPE_ENUM.COL_CELL);
                	  LIB.safeAddCol(tReturn, COLS.SCRIPT_PANEL.PRELIM_UNIVERAL.GEN_TIME, COL_TYPE_ENUM.COL_CELL);

                	  LIB.safeAddCol(tReturn, COLS.SCRIPT_PANEL.PRELIM_EOD.SIZE, COL_TYPE_ENUM.COL_CELL);
                	  LIB.safeAddCol(tReturn, COLS.SCRIPT_PANEL.PRELIM_UNIVERAL.SIZE, COL_TYPE_ENUM.COL_CELL);

                	  LIB.safeAddCol(tReturn, COLS.SCRIPT_PANEL.PRELIM_EOD.SIM_DEF_NAME, COL_TYPE_ENUM.COL_CELL);
                	  LIB.safeAddCol(tReturn, COLS.SCRIPT_PANEL.PRELIM_UNIVERAL.SIM_DEF_NAME, COL_TYPE_ENUM.COL_CELL);
     
                	  LIB.safeAddCol(tReturn, COLS.SCRIPT_PANEL.PRELIM_EOD.SIM_RUN_ID, COL_TYPE_ENUM.COL_CELL);
                	  LIB.safeAddCol(tReturn, COLS.SCRIPT_PANEL.PRELIM_UNIVERAL.SIM_RUN_ID, COL_TYPE_ENUM.COL_CELL);
            	  }

            	  if (revalRunType == SIMULATION_RUN_TYPE.EOD_SIM_TYPE) {
                	  LIB.safeAddCol(tReturn, COLS.SCRIPT_PANEL.EOD_COMPLETE, COL_TYPE_ENUM.COL_INT); 
                	  tReturn.setColFormatAsBool(COLS.SCRIPT_PANEL.EOD_COMPLETE); 
                	  
                	  LIB.safeAddCol(tReturn, COLS.SCRIPT_PANEL.EOD.GEN_TIME, COL_TYPE_ENUM.COL_CELL); 

                	  LIB.safeAddCol(tReturn, COLS.SCRIPT_PANEL.EOD.SIZE, COL_TYPE_ENUM.COL_CELL);

                	  LIB.safeAddCol(tReturn, COLS.SCRIPT_PANEL.EOD.SIM_DEF_NAME, COL_TYPE_ENUM.COL_CELL);
     
                	  LIB.safeAddCol(tReturn, COLS.SCRIPT_PANEL.EOD.SIM_RUN_ID, COL_TYPE_ENUM.COL_CELL);
            	  }
           	  
            	  // Keep this as an int
            	  LIB.safeAddCol(tReturn, COLS.SCRIPT_PANEL.HIDDEN_PORTFOLIO_ID, COL_TYPE_ENUM.COL_INT);
            	  tReturn.colHide(COLS.SCRIPT_PANEL.HIDDEN_PORTFOLIO_ID);
            	  
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
    	 
    	public static Table getSimHeaderTable(int iRunDate, String className) throws OException{
          	Table tReturn = Util.NULL_TABLE;
                try{
                  tReturn = CREATE_TABLE.createSimHeaderTable(className);
                  
                  // add the single quotes here
                  String sDateFormattedForDB = "'" + OCalendar.formatJdForDbAccess(iRunDate) + "'";
                  String sSQL = "SELECT\n" + 
          		 		"sim_run_id, sim_def_id, gen_time, run_time, run_type, reference, pfolio\n" + 
          		 		"FROM\n" + 
          		 		"sim_header \n" + 
          		 		"WHERE \n" + 
          		 		"run_time = " + sDateFormattedForDB;
                  // for now, just do Prelim and EOD
                  sSQL = sSQL + " AND run_type in (1, 7, 8)";
                  LIB.execISql(tReturn, sSQL, false, className);
              	  
                  LIB.safeAddCol(tReturn, COLS.SIM_HEADER.EXTRA.SIM_DEF_NAME, COL_TYPE_ENUM.COL_STRING);

                  // Add in the name of the Sim Def.  Get it from the database
                  {
                	  Table tTemp = Table.tableNew();
                      LIB.safeAddCol(tTemp, COLS.SIM_HEADER.SIM_DEF_ID, COL_TYPE_ENUM.COL_INT);
                      LIB.safeAddCol(tTemp, COLS.SIM_HEADER.EXTRA.SIM_DEF_NAME, COL_TYPE_ENUM.COL_STRING);
                      sSQL = "select sim_def_id, name FROM sim_def";
                      LIB.execISql(tTemp, sSQL, false, className);
                      
                      String sWhat = COLS.SIM_HEADER.EXTRA.SIM_DEF_NAME;
                      String sWhere = COLS.SIM_HEADER.SIM_DEF_ID + " EQ $" + COLS.SIM_HEADER.SIM_DEF_ID;
                      LIB.select(tReturn, tTemp, sWhat, sWhere, false, className);
                      
                      tTemp = LIB.destroyTable(tTemp);
                  }

                } catch(Exception e ){               
                     LIB.log("getSimHeaderTable", e, className);
                }
                 
				return tReturn; 
          } 

    	public static Table getPortfolioTable(String className) throws OException{
          	Table tReturn = Util.NULL_TABLE;
                try{
                  tReturn = CREATE_TABLE.createPortfolioTable(className);
 
                  String sSQL = "select id_number AS portfolio_id, name AS portfolio from portfolio";
                  LIB.execISql(tReturn, sSQL, false, className);
              	   
                } catch(Exception e ){               
                     LIB.log("getPortfolioTable", e, className);
                }
                 
				return tReturn; 
          } 

    	public static Table getTradeCountByPortfolioTable(String className) throws OException{
          	Table tReturn = Util.NULL_TABLE;
                try{
                  tReturn = CREATE_TABLE.createTradeCountByPortfolio(className);
 
                  String sSQL = "SELECT internal_portfolio, count(*) num_trades " + 
                		  " FROM ab_tran WHERE tran_status = " + TRAN_STATUS_ENUM.TRAN_STATUS_VALIDATED.toInt() + " AND trade_flag = 1 AND toolset != " + TOOLSET_ENUM.COMPOSER_TOOLSET.toInt() + 
                		  " GROUP BY internal_portfolio";
                  LIB.execISql(tReturn, sSQL, false, className);
                  
                  // Add in portfolios with Proxy in their name.  There are more robust ways to do this, but this way is good for now and quick
                  // This gets tran status 1, i.e., pending status
                  {
                	  Table tProxy = CREATE_TABLE.createTradeCountByPortfolio(className);
                      sSQL = "SELECT a.internal_portfolio, count(*) num_trades \n" + 
                        		"FROM\n" + 
                        		"ab_tran a, portfolio p \n" + 
                        		"WHERE\n" + 
                        		"a.internal_portfolio = p.id_number AND \n" + 
                        		"a.tran_status = " + TRAN_STATUS_ENUM.TRAN_STATUS_PENDING.toInt() + " AND a.trade_flag = 1 AND toolset != " + TOOLSET_ENUM.COMPOSER_TOOLSET.toInt() + " \n" + 
                        		"AND lower(p.name) like lower('%prox%')\n" + 
                        		"GROUP BY a.internal_portfolio, p.name";
                      LIB.execISql(tProxy, sSQL, false, className);
                      int iNumRows = LIB.safeGetNumRows(tProxy);
                      for (int iCounter = 1; iCounter <= iNumRows; iCounter++) {
                    	  int iPortfolio = LIB.safeGetInt(tProxy, COLS.PORTFOLIO_LIST_WITH_TRADE_COUNT.PORTFOLIO_ID, iCounter);
                    	  int iRowNum = tReturn.unsortedFindInt(COLS.PORTFOLIO_LIST_WITH_TRADE_COUNT.PORTFOLIO_ID, iPortfolio);
                    	  if (iRowNum < 1) {
                    		  tProxy.copyRowAddByColName(iCounter, tReturn);
                    	  }
                    	  if (iRowNum >= 1) {
                        	  int iProxyTrades = LIB.safeGetInt(tProxy, COLS.PORTFOLIO_LIST_WITH_TRADE_COUNT.PORTFOLIO_ID, iCounter);
                        	  int iOriginalTrades = LIB.safeGetInt(tReturn, COLS.PORTFOLIO_LIST_WITH_TRADE_COUNT.PORTFOLIO_ID, iRowNum);
                        	  int iTotalTrades = iProxyTrades + iOriginalTrades;
                        	  LIB.safeSetInt(tReturn, COLS.PORTFOLIO_LIST_WITH_TRADE_COUNT.PORTFOLIO_ID, iRowNum,iTotalTrades);
                    	  }
                      }
                      tProxy = LIB.destroyTable(tProxy);
                  }
                  
              	   
                } catch(Exception e ){               
                     LIB.log("getTradeCountByPortfolioTable", e, className);
                }
                 
				return tReturn; 
          } 

    	public static Table getSimBlobSizeTable(Table tSimHeader, String className) throws OException{
          	Table tReturn = Util.NULL_TABLE;
                try{
                  tReturn = CREATE_TABLE.createSimBlobSizeTable(className);
                  
                  {
                	  Table tSimRunIDs = Table.tableNew();
                	  LIB.safeAddCol(tSimRunIDs, "sim_run_id", COL_TYPE_ENUM.COL_INT);
                	  tSimHeader.copyColDistinct(COLS.SIM_HEADER.SIM_RUN_ID, tSimRunIDs, "sim_run_id");
                	  
                	  Table tSimHeaderDetails = Table.tableNew();
                	  LIB.safeAddCol(tSimHeaderDetails, COLS.SIM_HEADER.SIM_RUN_ID, COL_TYPE_ENUM.COL_INT);
                	  LIB.safeAddCol(tSimHeaderDetails, "unique_sim_key", COL_TYPE_ENUM.COL_INT);
                	  String sWhat = "sim_run_id, unique_sim_key";
                	  String sWhere = "1 = 1";
                	  LIB.loadFromDbWithWhatWhere(tSimHeaderDetails, "sim_header_details", tSimRunIDs, sWhat, sWhere, false, className);
                	
                	  Table tSimUniqueIDs = Table.tableNew();
                	  LIB.safeAddCol(tSimUniqueIDs, "unique_sim_key", COL_TYPE_ENUM.COL_INT);
                	  tSimHeaderDetails.copyColDistinct("unique_sim_key", tSimUniqueIDs, "unique_sim_key");

                	  Table tSimDetailsBlob = Table.tableNew("tSimDetailsBlob");
                	  LIB.safeAddCol(tSimDetailsBlob, "unique_sim_key", COL_TYPE_ENUM.COL_INT);
                	  LIB.safeAddCol(tSimDetailsBlob, "blob_size", COL_TYPE_ENUM.COL_INT);
                	  sWhat = "unique_sim_key, blob_size";
                	  sWhere = "1 = 1";
                	  LIB.loadFromDbWithWhatWhere(tSimDetailsBlob, "sim_details_blob", tSimUniqueIDs, sWhat, sWhere, false, className);

                	  {
                    	  // Add Sim Run ID to tSimDetailsBlob
                    	  LIB.safeAddCol(tSimDetailsBlob, COLS.SIM_BLOB_SIZE.SIM_RUN_ID, COL_TYPE_ENUM.COL_INT);
                    	  
                    	  // and populate SIM_RUN_ID
                    	  sWhat = COLS.SIM_HEADER.SIM_RUN_ID + "(" + COLS.SIM_BLOB_SIZE.SIM_RUN_ID + ")";
                    	  sWhere = "unique_sim_key" + " EQ $" + "unique_sim_key";
                    	  LIB.select(tSimDetailsBlob, tSimHeaderDetails, sWhat, sWhere, false, className);
                	  }
                	  
                	  {
                    	  // and add blob size as a double
                    	  LIB.safeAddCol(tSimDetailsBlob, COLS.SIM_BLOB_SIZE.BLOB_SIZE_MEGS, COL_TYPE_ENUM.COL_DOUBLE);
                    	  
                    	  // and populate
                    	  tSimDetailsBlob.mathAddColConst("blob_size", 0, COLS.SIM_BLOB_SIZE.BLOB_SIZE_MEGS);
                    	  // go from bytes to megs
                    	  tSimDetailsBlob.mathDivColConst(COLS.SIM_BLOB_SIZE.BLOB_SIZE_MEGS, 1024, COLS.SIM_BLOB_SIZE.BLOB_SIZE_MEGS);
                    	  tSimDetailsBlob.mathDivColConst(COLS.SIM_BLOB_SIZE.BLOB_SIZE_MEGS, 1024, COLS.SIM_BLOB_SIZE.BLOB_SIZE_MEGS);
                	  } 
                	  
                	  // populate return table
                	  // this is the first time we add rows
                	  tSimHeader.copyColDistinct(COLS.SIM_HEADER.SIM_RUN_ID, tReturn, COLS.SIM_BLOB_SIZE.SIM_RUN_ID);
                	  
                	  // Make sure to *SUM* this
                	  sWhat = "SUM, " + COLS.SIM_BLOB_SIZE.BLOB_SIZE_MEGS;
                	  sWhere = COLS.SIM_BLOB_SIZE.SIM_RUN_ID + " EQ $" + COLS.SIM_BLOB_SIZE.SIM_RUN_ID;
                	  LIB.select(tReturn, tSimDetailsBlob, sWhat, sWhere, false, className);
                	    
                	  tSimRunIDs = LIB.destroyTable(tSimRunIDs);
                	  tSimHeaderDetails = LIB.destroyTable(tSimHeaderDetails);
                	  tSimUniqueIDs = LIB.destroyTable(tSimUniqueIDs); 
                	  tSimDetailsBlob = LIB.destroyTable(tSimDetailsBlob); 
                  }
 
               //   String sSQL = "SELECT internal_portfolio, count(*) num_trades FROM ab_tran WHERE tran_status = 3 AND trade_flag = 1 AND toolset != 8 GROUP BY internal_portfolio";
                //  LIB.execISql(tReturn, sSQL, false, className);
                 
              	   
                } catch(Exception e ){               
                     LIB.log("getSimBlobSizeTable", e, className);
                }
                 
				return tReturn; 
          } 
    	
    	
    } // END public static class GET_DATA
    
//    display.scriptDataAddWidget("pbut", SCRIPT_PANEL_WIDGET_TYPE_ENUM.
//            SCRIPT_PANEL_PUSHBUTTON_WIDGET.toInt(),
//            "x=2, y=2, h=20, w=100", "label=push to change label");
    

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
        	
            public static void doEverythingButton(Table tScriptPanel, Table tExtraData, SIMULATION_RUN_TYPE revalRunType, PORTFOLIOS_TO_SHOW portfoliosToShow, 
            		int iRunDate, String sRunDate, 
            		String className) throws OException {
            	
            	try{
            		String sButtonName = tScriptPanel.scriptDataGetCallbackName();
            		String sButtonLabel = tScriptPanel.scriptDataGetWidgetString(sButtonName);
            		LIB.log("ButtonName: " + sButtonName + ", ButtonLabel: " + sButtonLabel, className);
 
            		boolean bHandledFlag = false;
               		if (Str.equal(sButtonName, BUTTON.BUTTON1.getName()) == CONST_VALUES.VALUE_OF_TRUE) {
            			FUNC.getAndRefreshDataIntoScriptPanel(tScriptPanel, revalRunType, portfoliosToShow, iRunDate, sRunDate, className);
            			bHandledFlag = true;
            		}
               		if (Str.equal(sButtonName, BUTTON.BUTTON2.getName()) == CONST_VALUES.VALUE_OF_TRUE) {
               			// This button, i.e., giving it as an example... is more about showing how this works, and less about being really 'needed' for this Panel
               			// i.e., want to show an example of how to launch another module in OpenLink
               			Afe.issueServiceRequestByID(AFE_SERVICE.SERVICES_MANAGER);
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
        	
            public static void doEverythingTimer(Table tScriptPanel, SIMULATION_RUN_TYPE revalRunType, PORTFOLIOS_TO_SHOW portfoliosToShow, 
            		int iRunDate, String sRunDate, 
            		String className) throws OException {
            	
            	try{
            		String sTimerName = tScriptPanel.scriptDataGetTimerName();
            		boolean bHandledFlag = false;
            		if (Str.equal(sTimerName, TIMER.TIMER1.getName()) == CONST_VALUES.VALUE_OF_TRUE) {
            			FUNC.getAndRefreshDataIntoScriptPanel(tScriptPanel, revalRunType, portfoliosToShow, iRunDate, sRunDate, className);
            			bHandledFlag = true;
            		}
            		// This gets triggered by a combobox
            		if (Str.equal(sTimerName, TIMER.TIMER2.getName()) == CONST_VALUES.VALUE_OF_TRUE) {
            			FUNC.getAndRefreshDataIntoScriptPanel(tScriptPanel, revalRunType, portfoliosToShow, iRunDate, sRunDate, className);
            			tScriptPanel.scriptDataEndTimer(sTimerName);
            			bHandledFlag = true;
            		}
            		// This gets triggered when you start the script
            		// This is for setting the # of portfolios lables
            		// this is a one time run, so end the timer after this
            		if (Str.equal(sTimerName, TIMER.TIMER3.getName()) == CONST_VALUES.VALUE_OF_TRUE) {
            			FUNC.setAllLabelValuesForRefresh(tScriptPanel, revalRunType, className);
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
        	
        	   public static void formatSimResults(Table tResults, String sResultType, String sRunDate, String sPortfolio, SIMULATION_RUN_TYPE revalRunType, String className) throws OException {
               	
               	try{
               		
               		if (LIB.safeGetNumRows(tResults) >= 1) {
        				tResults.defaultFormat();

            			int iNumCols = LIB.safeGetNumCols(tResults);
            			for (int iColCounter = 1; iColCounter <= iNumCols; iColCounter++) {
        					String sColName = tResults.getColName(iColCounter);
            				String sColTitle = sColName;
            				int iColType = tResults.getColType(iColCounter);
            				if (iColType == COL_TYPE_ENUM.COL_DOUBLE.toInt()) {
            					if (Str.isInt(sColName) == 1) {
            						int iResultType = Str.strToInt(sColName);
            						sColTitle = Ref.getName(SHM_USR_TABLES_ENUM.RESULT_TYPE_TABLE, iResultType);
            					}
            				}
    						tResults.setColTitle(iColCounter, sColTitle);
            			}
            			
            			String sTableTitle = sResultType + " Results as of " + sRunDate + ", " + sPortfolio + ", " + 
            			Ref.getName(SHM_USR_TABLES_ENUM.REVAL_TYPE_TABLE, revalRunType.toInt()) + ", Number of Rows: " + LIB.safeGetNumRows(tResults);
            			// Set the title and *NOT* the table name
                		tResults.setTableTitle(sTableTitle);
        			}
               		
               	} catch(Exception e ){               
               		LIB.log("formatSimResults", e, className);
               	} 
               }
           
            public static void showUserSimResultsForPortfolioForDate(SIMULATION_RUN_TYPE revalRunType, 
            		int iPortfolio, String sPortfolio, 
            		int iRunDate, String sRunDate,
            		String className) throws OException {
            	
            	Table tSims = Util.NULL_TABLE;
            	try{
            		
            		tSims = SimResult.tableLoadSrun(iPortfolio, revalRunType, iRunDate, CONST_VALUES.VALUE_OF_FALSE);
            		
            		String sTableName = "Sim Results as of " + sRunDate + ", " + sPortfolio + ", Reval Type " + "'" + Ref.getName(SHM_USR_TABLES_ENUM.REVAL_TYPE_TABLE, revalRunType.toInt())  + "'";
            		tSims.setTableName(sTableName);
            		tSims.setTableTitle(sTableName);
            		
            		int iNumRows = LIB.safeGetNumRows(tSims);
            		// just show the first scenario
            		if (iNumRows > 1) {
            			iNumRows = 1;
            		}
            		for (int iCounter = 1; iCounter <= iNumRows; iCounter++) {
            			Table tScenario = LIB.safeGetTable(tSims, "scenario_results", iCounter);
            			// Set the title and *NOT* the table name
            			tScenario.setTableTitle(sTableName);

            			{
                			Table tTranResult = LIB.safeGetTable(tScenario, "result_class", 1);
                			DOUBLE_CLICK_HANDLER.formatSimResults(tTranResult, "Tran", sRunDate, sPortfolio, revalRunType, className);
            			}
            			{
                			Table tLegResult = LIB.safeGetTable(tScenario, "result_class", 3);
                			DOUBLE_CLICK_HANDLER.formatSimResults(tLegResult, "Leg", sRunDate, sPortfolio, revalRunType, className);
            			}
            			{
                			Table tCumulativeResult = LIB.safeGetTable(tScenario, "result_class", 2);
                			DOUBLE_CLICK_HANDLER.formatSimResults(tCumulativeResult, "Cumulative", sRunDate, sPortfolio, revalRunType, className);
            			}
            			
            			LIB.viewTable(tScenario, sTableName);
            		}
            		 

            	} catch(Exception e ){               
            		LIB.log("showUserSimResultsForPortfolioForDate", e, className);
            	} 
            	tSims = LIB.destroyTable(tSims);
            }
            
            public static void doEverythingDoubleClick(Table tScriptPanel, SIMULATION_RUN_TYPE revalRunType, PORTFOLIOS_TO_SHOW portfoliosToShow, 
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

    				int iPortfolioID = LIB.safeGetInt(tScriptPanel, COLS.SCRIPT_PANEL.HIDDEN_PORTFOLIO_ID, iRowNum);
    				String sPortfolio = tScriptPanel.getCellString(COLS.SCRIPT_PANEL.PORTFOLIO, iRowNum);

        			if (Str.iEqual(sColName, COLS.SCRIPT_PANEL.NUM_TRADES) == CONST_VALUES.VALUE_OF_TRUE ||
        					Str.iEqual(sColName, COLS.SCRIPT_PANEL.PORTFOLIO_ID) == CONST_VALUES.VALUE_OF_TRUE ||
        					Str.iEqual(sColName, COLS.SCRIPT_PANEL.PORTFOLIO) == CONST_VALUES.VALUE_OF_TRUE) {
            			LIB.log("Received Double Click on column: " + "'" + sColName + "'" + ", Row Number: " + iRowNum + ", Portfolio: " + "'" + sPortfolio + "'", className);
            			
            			FUNC.showTradeListForPortfolio(tScriptPanel, iPortfolioID, sPortfolio, className);
        				bHandledFlag = true;
        			}
        			
        			// *** EOD ***
        			if (Str.iEqual(sColName, COLS.SCRIPT_PANEL.EOD.REVAL_TYPE) == CONST_VALUES.VALUE_OF_TRUE ||
        					Str.iEqual(sColName, COLS.SCRIPT_PANEL.EOD.SIM_DEF_ID) == CONST_VALUES.VALUE_OF_TRUE ||
        					Str.iEqual(sColName, COLS.SCRIPT_PANEL.EOD.SIM_DEF_NAME) == CONST_VALUES.VALUE_OF_TRUE ||
        					Str.iEqual(sColName, COLS.SCRIPT_PANEL.EOD.SIM_RUN_ID) == CONST_VALUES.VALUE_OF_TRUE ||
        					Str.iEqual(sColName, COLS.SCRIPT_PANEL.EOD.GEN_TIME) == CONST_VALUES.VALUE_OF_TRUE ||
        					Str.iEqual(sColName, COLS.SCRIPT_PANEL.EOD.SIZE) == CONST_VALUES.VALUE_OF_TRUE) {
        			
            			LIB.log("Received Double Click on column: " + "'" + sColName + "'" + ", Row Number: " + iRowNum + ", Portfolio: " + "'" + sPortfolio + "'", className);
            			
            			@SuppressWarnings("unused")
						int iSimRunID = tScriptPanel.getCellInt(COLS.SCRIPT_PANEL.EOD.SIM_RUN_ID, iRowNum);
            			
        				SIMULATION_RUN_TYPE revalRunTypeToLoad = SIMULATION_RUN_TYPE.EOD_SIM_TYPE;
        				DOUBLE_CLICK_HANDLER.showUserSimResultsForPortfolioForDate(revalRunTypeToLoad, iPortfolioID, sPortfolio, iRunDate, sRunDate, className);
        				bHandledFlag = true;
        			}

        			// *** PRELIM EOD ***
        			if (Str.iEqual(sColName, COLS.SCRIPT_PANEL.PRELIM_EOD.REVAL_TYPE) == CONST_VALUES.VALUE_OF_TRUE ||
        					Str.iEqual(sColName, COLS.SCRIPT_PANEL.PRELIM_EOD.SIM_DEF_ID) == CONST_VALUES.VALUE_OF_TRUE ||
        					Str.iEqual(sColName, COLS.SCRIPT_PANEL.PRELIM_EOD.SIM_DEF_NAME) == CONST_VALUES.VALUE_OF_TRUE ||
        					Str.iEqual(sColName, COLS.SCRIPT_PANEL.PRELIM_EOD.SIM_RUN_ID) == CONST_VALUES.VALUE_OF_TRUE ||
        					Str.iEqual(sColName, COLS.SCRIPT_PANEL.PRELIM_EOD.GEN_TIME) == CONST_VALUES.VALUE_OF_TRUE ||
        					Str.iEqual(sColName, COLS.SCRIPT_PANEL.PRELIM_EOD.SIZE) == CONST_VALUES.VALUE_OF_TRUE) {
        			
            			LIB.log("Received Double Click on column: " + "'" + sColName + "'" + ", Row Number: " + iRowNum + ", Portfolio: " + "'" + sPortfolio + "'", className);
            			
            			int iSimRunID = tScriptPanel.getCellInt(COLS.SCRIPT_PANEL.PRELIM_EOD.SIM_RUN_ID, iRowNum);
            			
            			// Tested this and it did not work.  Tried various combinations.  The 'Simulation Results' screen did open up.. but it was always blank.
            			// i.e., did not actually show the sim results.  I tried using an 'arg type' of both String (2) and int (0) and neither worked.
            			boolean bDoSimResultsViewerFlag = false;
            			if (bDoSimResultsViewerFlag == true) {
          					 Table arg_table = Afe.createServiceRequestArgTable();
           					 int iMaxRow = arg_table.addRow();
           					 
           					 LIB.safeSetString(arg_table, "afe_arg_name", iMaxRow, "SimResultsId");
           					 // set the col type as an integer
//           					 	COL_INT		0               
//           					 	COL_DOUBLE	1               
//           					 	COL_TRAN	13              
//           					 	COL_DATE	19              
//           					 	COL_STRING	2               
           					 LIB.safeSetInt(arg_table, "afe_arg_type", iMaxRow, COL_TYPE_ENUM.COL_STRING.toInt());
           					 // the value is always a string, i.e., need to convert this integer value to a string.
           					 LIB.safeSetString(arg_table, "afe_arg_value", iMaxRow, Str.intToStr(iSimRunID));
           					 
           					 Afe.issueServiceRequestWithTableArg(AFE_SERVICE.SIMULATION_RESULT_VIEWER, arg_table);
           		 
           					 arg_table = LIB.destroyTable(arg_table);
            			}

        				SIMULATION_RUN_TYPE revalRunTypeToLoad = SIMULATION_RUN_TYPE.P_EOD_SIM_TYPE;
        				DOUBLE_CLICK_HANDLER.showUserSimResultsForPortfolioForDate(revalRunTypeToLoad, iPortfolioID, sPortfolio, iRunDate, sRunDate, className);
        				bHandledFlag = true;
        			}

        			// *** INTRA DAY sims ***
        			if (Str.iEqual(sColName, COLS.SCRIPT_PANEL.PRELIM_UNIVERAL.REVAL_TYPE) == CONST_VALUES.VALUE_OF_TRUE ||
        					Str.iEqual(sColName, COLS.SCRIPT_PANEL.PRELIM_UNIVERAL.SIM_DEF_ID) == CONST_VALUES.VALUE_OF_TRUE ||
        					Str.iEqual(sColName, COLS.SCRIPT_PANEL.PRELIM_UNIVERAL.SIM_DEF_NAME) == CONST_VALUES.VALUE_OF_TRUE ||
        					Str.iEqual(sColName, COLS.SCRIPT_PANEL.PRELIM_UNIVERAL.SIM_RUN_ID) == CONST_VALUES.VALUE_OF_TRUE ||
        					Str.iEqual(sColName, COLS.SCRIPT_PANEL.PRELIM_UNIVERAL.GEN_TIME) == CONST_VALUES.VALUE_OF_TRUE ||
        					Str.iEqual(sColName, COLS.SCRIPT_PANEL.PRELIM_UNIVERAL.SIZE) == CONST_VALUES.VALUE_OF_TRUE) {
        			
            			LIB.log("Received Double Click on column: " + "'" + sColName + "'" + ", Row Number: " + iRowNum + ", Portfolio: " + "'" + sPortfolio + "'", className);
            	
            			@SuppressWarnings("unused")
            			int iSimRunID = tScriptPanel.getCellInt(COLS.SCRIPT_PANEL.PRELIM_UNIVERAL.SIM_RUN_ID, iRowNum);
            			
        				SIMULATION_RUN_TYPE revalRunTypeToLoad = SIMULATION_RUN_TYPE.INTRA_DAY_SIM_TYPE;
        				DOUBLE_CLICK_HANDLER.showUserSimResultsForPortfolioForDate(revalRunTypeToLoad, iPortfolioID, sPortfolio, iRunDate, sRunDate, className);
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
        	
            public static void doEverythingComboBox(Table tScriptPanel, PORTFOLIOS_TO_SHOW portfoliosToShow, 
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
                    // This is for 'Display Portfolios', i.e., Portfolios to Show
                    if (Str.equal(sComboBoxName, COMBOBOX.COMBOBOX2.getName()) == CONST_VALUES.VALUE_OF_TRUE) {
                    	LIB.log("Display Portfolios Value Set to: " + sComboBoxSelectedValue, className);
                    	// set a 1 millisecond timer, and leave it to the timer handler to process this
                    	tScriptPanel.scriptDataSetTimer(TIMER.TIMER2.getName(), 1);
//            	    	Table tRawReport = FUNC.getUpdatedRawData(tScriptPanel, portfoliosToShow, className);
//            			FUNC.refreshDataIntoScriptPanel(tScriptPanel, tRawReport, className);
//            			tRawReport = LIB.destroyTable(tRawReport);
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
        

            public static void doEverythingInit(int iRunDate, SIMULATION_RUN_TYPE revalRunType, 
            		REFRESH_INTERVAL defaultRefreshInterval, String className) throws OException {

            	Table tScriptPanel = Util.NULL_TABLE;
            	Table tRawReport = Util.NULL_TABLE;
            	Table tSimHeader = Util.NULL_TABLE; 
            	Table tPortfolio = Util.NULL_TABLE;
            	Table tTradeCountByPortfolio = Util.NULL_TABLE;
            	try{
            		String sRunDate = OCalendar.formatJd(iRunDate, DATE_FORMAT.DATE_FORMAT_DMLY_NOSLASH);
            		//tScriptPanel.setColValCellString(COLS.SCRIPT_PANEL.RUN_DATE, sRunDate, FORMAT.JUSTIFY.CENTER);
            		
            		tScriptPanel = CREATE_TABLE.createScriptPanelTable(revalRunType, className);
            		tRawReport = CREATE_TABLE.createRawReportTable(className);
            		// This will load for reval types EOD, Prelim EOD and Intraday (1, 8, and 7)
            		tSimHeader = GET_DATA.getSimHeaderTable(iRunDate, className);
            		
            		{
            			Table tSimBlobSize = GET_DATA.getSimBlobSizeTable(tSimHeader, className);
                		FUNC.addSimBlobSizeToSimHeaderTable(tSimHeader, tSimBlobSize, className);
                    	tSimBlobSize = LIB.destroyTable(tSimBlobSize);
            		} 
            		
            		tPortfolio = GET_DATA.getPortfolioTable(className);
            		tTradeCountByPortfolio = GET_DATA.getTradeCountByPortfolioTable(className);
            		 
            		FUNC.addDataToRawReportTable(tRawReport, tPortfolio, revalRunType, 
            				tSimHeader, tTradeCountByPortfolio, PORTFOLIOS_TO_SHOW.WITH_TRADES, className);
            		tRawReport.group(COLS.RAW_REPORT.PORTFOLIO);
            		FUNC.addRawReportToScriptPanelForFirstTime(tScriptPanel, tRawReport, sRunDate, revalRunType, className);
            		
            		// Add buttons
            		GUI.addButton(tScriptPanel, BUTTON.BUTTON1, className);
            		GUI.addButton(tScriptPanel, BUTTON.BUTTON2, className);
            		GUI.addButton(tScriptPanel, BUTTON.DEBUG_INFO, className);

            		// Add Stand alone labels, if any
            		GUI.addLabel(tScriptPanel, LABEL.LABEL3, className);
            		GUI.addLabel(tScriptPanel, LABEL.LABEL4, className);
            		GUI.addLabel(tScriptPanel, LABEL.LABEL5, className);
            		GUI.addLabel(tScriptPanel, LABEL.LABEL6, className);
            		if (revalRunType == SIMULATION_RUN_TYPE.EOD_SIM_TYPE) {
                		GUI.addLabel(tScriptPanel, LABEL.LABEL7_PRELIM_EOD, className);
            		}
            		if (revalRunType == SIMULATION_RUN_TYPE.P_EOD_SIM_TYPE) {
                		GUI.addLabel(tScriptPanel, LABEL.LABEL7_EOD, className);
            		}
            		GUI.addLabel(tScriptPanel, LABEL.LABEL8, className);
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
            		{
                		GUI.addLabel(tScriptPanel, LABEL.LABEL2, className);
                		Table tComboBoxMenu = PICK_LIST.getPortfoliosToShowPicklist(className);
                		GUI.addComboxBox(tScriptPanel, COMBOBOX.COMBOBOX2, tComboBoxMenu, PORTFOLIOS_TO_SHOW.WITH_TRADES.getName(), className);
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
            	tSimHeader = LIB.destroyTable(tSimHeader);
            	tPortfolio = LIB.destroyTable(tPortfolio);
            	tTradeCountByPortfolio = LIB.destroyTable(tTradeCountByPortfolio);
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

    	public static Table getUpdatedRawData(Table tScriptPanel, SIMULATION_RUN_TYPE revalRunType, PORTFOLIOS_TO_SHOW portfoliosToShow, 
    			int iRunDate, String sRunDate, 
    			String className) throws OException{
        	 
        	Table tRawReport = Util.NULL_TABLE;
        	Table tSimHeader = Util.NULL_TABLE; 
        	Table tPortfolio = Util.NULL_TABLE;
        	Table tTradeCountByPortfolio = Util.NULL_TABLE;
        	try{ 
        		int iNumRows = LIB.safeGetNumRows(tScriptPanel);
        		
        		if (iNumRows >= 1) {
            		
            		tRawReport = CREATE_TABLE.createRawReportTable(className);
            		tSimHeader = GET_DATA.getSimHeaderTable(iRunDate, className);
            		
            		{
            			Table tSimBlobSize = GET_DATA.getSimBlobSizeTable(tSimHeader, className);
                		FUNC.addSimBlobSizeToSimHeaderTable(tSimHeader, tSimBlobSize, className);
                    	tSimBlobSize = LIB.destroyTable(tSimBlobSize);
            		} 
            		
            		tPortfolio = GET_DATA.getPortfolioTable(className);
            		tTradeCountByPortfolio = GET_DATA.getTradeCountByPortfolioTable(className);
            		 
            		FUNC.addDataToRawReportTable(tRawReport, tPortfolio, revalRunType,
            				tSimHeader, tTradeCountByPortfolio, portfoliosToShow, className);
            		tRawReport.group(COLS.RAW_REPORT.PORTFOLIO);
            		 
        		}
             
        	} catch(Exception e ){               
        		LIB.log("getUpdatedRawData", e, className);
        	}  
        	tSimHeader = LIB.destroyTable(tSimHeader);
        	tPortfolio = LIB.destroyTable(tPortfolio);
        	tTradeCountByPortfolio = LIB.destroyTable(tTradeCountByPortfolio);
        	
        	return tRawReport;
    	}

    	public static void getAndRefreshDataIntoScriptPanel(Table tScriptPanel, SIMULATION_RUN_TYPE revalRunType, PORTFOLIOS_TO_SHOW portfoliosToShow, 
    			int iRunDate, String sRunDate, 
    			String className) throws OException{
        	 
        	Table tRawReport = Util.NULL_TABLE;
        	try{ 
        		
    	    	tRawReport = FUNC.getUpdatedRawData(tScriptPanel, revalRunType, portfoliosToShow, iRunDate, sRunDate, className);
    			FUNC.refreshDataIntoScriptPanel(tScriptPanel, revalRunType, tRawReport, iRunDate, sRunDate, className);
    		 
    			FUNC.setAllLabelValuesForRefresh(tScriptPanel, revalRunType, className);
    			
        	} catch(Exception e ){               
        		LIB.log("getAndRefreshDataIntoScriptPanel", e, className);
        	}  
        	tRawReport = LIB.destroyTable(tRawReport);
    	}
    	
    	public static void refreshDataIntoScriptPanel(Table tScriptPanel, SIMULATION_RUN_TYPE revalRunType, Table tRawReport,
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
        			LIB.safeAddCol(tTemp, COLS.SCRIPT_PANEL.HIDDEN_PORTFOLIO_ID, COL_TYPE_ENUM.COL_INT);
        			tScriptPanel.copyCol(COLS.SCRIPT_PANEL.HIDDEN_PORTFOLIO_ID, tTemp, COLS.SCRIPT_PANEL.HIDDEN_PORTFOLIO_ID);
        			
        			LIB.safeAddCol(tTemp, TEMP_COL_NAME_FOR_SORTING, COL_TYPE_ENUM.COL_INT);
        			tTemp.setColIncrementInt(TEMP_COL_NAME_FOR_SORTING, 1, 1);

        			// add to the tRawReport
        			LIB.safeAddCol(tRawReport, TEMP_COL_NAME_FOR_SORTING, COL_TYPE_ENUM.COL_INT);
        			
        			// add in the temp for sorting col into the Raw Report Table
        			String sWhat = TEMP_COL_NAME_FOR_SORTING;
        			String sWhere = COLS.SCRIPT_PANEL.HIDDEN_PORTFOLIO_ID + " EQ $" + COLS.RAW_REPORT.PORTFOLIO_ID;
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
        				// This is to handle the case where the user chose 'All' for 'Display Portfolios'
            			int iPortfolioID = LIB.safeGetInt(tRawReport, COLS.RAW_REPORT.PORTFOLIO_ID, iReportCounter);
            			String sPortfolio = LIB.safeGetString(tRawReport, COLS.RAW_REPORT.PORTFOLIO, iReportCounter);
            			iRowNum = tScriptPanel.addRow();
            			
            			LIB.safeSetInt(tScriptPanel, COLS.SCRIPT_PANEL.HIDDEN_PORTFOLIO_ID, iRowNum, iPortfolioID);
            			tScriptPanel.setCellInt(COLS.SCRIPT_PANEL.PORTFOLIO_ID, iRowNum, iPortfolioID, FORMAT.JUSTIFY.CENTER);

            			tScriptPanel.setCellString(COLS.SCRIPT_PANEL.PORTFOLIO, iRowNum, sPortfolio);
            			tScriptPanel.setCellString(COLS.SCRIPT_PANEL.RUN_DATE, iRowNum, sRunDate, FORMAT.JUSTIFY.CENTER);
        			}

        			if (iRowNum >= 1) {
        			
        				// Mark that we found the row
        				LIB.safeSetInt(tScriptPanel, TEMP_COL_NAME_FOUND_FLAG, iRowNum, CONST_VALUES.VALUE_OF_TRUE);
        				
        				iValue = LIB.safeGetInt(tRawReport, COLS.RAW_REPORT.NUM_TRADES, iReportCounter);
            			tScriptPanel.setCellInt(COLS.SCRIPT_PANEL.NUM_TRADES, iRowNum, iValue, FORMAT.DOUBLE_CLICK + ", " + FORMAT.JUSTIFY.RIGHT);

            			if (revalRunType == SIMULATION_RUN_TYPE.EOD_SIM_TYPE) {
            			
            				{
                    			sValue = LIB.safeGetString(tRawReport, COLS.RAW_REPORT.EOD.GEN_TIME, iReportCounter);
                    			tScriptPanel.setCellString(COLS.SCRIPT_PANEL.EOD.GEN_TIME, iRowNum, sValue);
                			}
                			
                			boolean bEODHasSims = false;
                			{
                    			iValue = LIB.safeGetInt(tRawReport, COLS.RAW_REPORT.EOD.SIM_RUN_ID, iReportCounter);
                    			
                    			if (iValue >= 1) {
                    				bEODHasSims = true;
                    				LIB.safeSetInt(tScriptPanel, COLS.SCRIPT_PANEL.EOD_COMPLETE, iRowNum, CONST_VALUES.VALUE_OF_TRUE);
                        			tScriptPanel.setCellInt(COLS.SCRIPT_PANEL.EOD.SIM_RUN_ID, iRowNum, iValue, FORMAT.DOUBLE_CLICK);
                    			} else {
                        			tScriptPanel.setCellString(COLS.SCRIPT_PANEL.EOD.SIM_RUN_ID, iRowNum, "");
                    			}
                			}
                			
                			{
                				if (bEODHasSims == true) {
                        			dValue = LIB.safeGetDouble(tRawReport, COLS.RAW_REPORT.EOD.SIZE, iReportCounter);
                        			tScriptPanel.setCellDouble(COLS.SCRIPT_PANEL.EOD.SIZE, iRowNum, dValue, FORMAT.DOUBLE_CLICK + ", " + FORMAT.JUSTIFY.RIGHT);
                				} else {
                        			tScriptPanel.setCellString(COLS.SCRIPT_PANEL.EOD.SIZE, iRowNum, "");
                				}

                			}
                			
                			{
                    			sValue = LIB.safeGetString(tRawReport, COLS.RAW_REPORT.EOD.SIM_DEF_NAME, iReportCounter);
                    			tScriptPanel.setCellString(COLS.SCRIPT_PANEL.EOD.SIM_DEF_NAME, iRowNum, sValue, FORMAT.DOUBLE_CLICK);
                			}
                			
            			} // END             			if (revalRunType == SIMULATION_RUN_TYPE.EOD_SIM_TYPE) 
            			
            			 
            			if (revalRunType == SIMULATION_RUN_TYPE.P_EOD_SIM_TYPE) {
            			
            				{
                    			sValue = LIB.safeGetString(tRawReport, COLS.RAW_REPORT.PRELIM_EOD.GEN_TIME, iReportCounter);
                    			tScriptPanel.setCellString(COLS.SCRIPT_PANEL.PRELIM_EOD.GEN_TIME, iRowNum, sValue);

                    			sValue = LIB.safeGetString(tRawReport, COLS.RAW_REPORT.PRELIM_UNIVERAL.GEN_TIME, iReportCounter);
                    			tScriptPanel.setCellString(COLS.SCRIPT_PANEL.PRELIM_UNIVERAL.GEN_TIME, iRowNum, sValue);
                			}
                			
                			boolean bPrelimEODHasSims = false;
                			boolean bPrelimUniversalHasSims = false;
                			{
                    			iValue = LIB.safeGetInt(tRawReport, COLS.RAW_REPORT.PRELIM_EOD.SIM_RUN_ID, iReportCounter);
                    			
                    			if (iValue >= 1) {
                    				bPrelimEODHasSims = true;
                    				LIB.safeSetInt(tScriptPanel, COLS.SCRIPT_PANEL.PRELIM_EOD_COMPLETE, iRowNum, CONST_VALUES.VALUE_OF_TRUE);
                        			tScriptPanel.setCellInt(COLS.SCRIPT_PANEL.PRELIM_EOD.SIM_RUN_ID, iRowNum, iValue, FORMAT.DOUBLE_CLICK);
                    			} else {
                        			tScriptPanel.setCellString(COLS.SCRIPT_PANEL.PRELIM_EOD.SIM_RUN_ID, iRowNum, "");
                    			}

                    			iValue = LIB.safeGetInt(tRawReport, COLS.RAW_REPORT.PRELIM_UNIVERAL.SIM_RUN_ID, iReportCounter);
                    			
                    			if (iValue >= 1) {
                    				bPrelimUniversalHasSims = true;
                    				LIB.safeSetInt(tScriptPanel, COLS.SCRIPT_PANEL.PRELIM_UNIVERAL_COMPLETE, iRowNum, CONST_VALUES.VALUE_OF_TRUE);
                        			tScriptPanel.setCellInt(COLS.SCRIPT_PANEL.PRELIM_UNIVERAL.SIM_RUN_ID, iRowNum, iValue, FORMAT.DOUBLE_CLICK);
                    			} else {
                        			tScriptPanel.setCellString(COLS.SCRIPT_PANEL.PRELIM_UNIVERAL.SIM_RUN_ID, iRowNum, "");
                    			}
                			}
                			
                			{
                				if (bPrelimEODHasSims == true) {
                        			dValue = LIB.safeGetDouble(tRawReport, COLS.RAW_REPORT.PRELIM_EOD.SIZE, iReportCounter);
                        			tScriptPanel.setCellDouble(COLS.SCRIPT_PANEL.PRELIM_EOD.SIZE, iRowNum, dValue, FORMAT.DOUBLE_CLICK + "," + FORMAT.JUSTIFY.RIGHT);
                				} else {
                        			tScriptPanel.setCellString(COLS.SCRIPT_PANEL.PRELIM_EOD.SIZE, iRowNum, "");
                				}
                				
                				if (bPrelimUniversalHasSims == true) {
                        			dValue = LIB.safeGetDouble(tRawReport, COLS.RAW_REPORT.PRELIM_UNIVERAL.SIZE, iReportCounter);
                        			tScriptPanel.setCellDouble(COLS.SCRIPT_PANEL.PRELIM_UNIVERAL.SIZE, iRowNum, dValue, FORMAT.DOUBLE_CLICK + "," +FORMAT.JUSTIFY.RIGHT);
                				} else {
                        			tScriptPanel.setCellString(COLS.SCRIPT_PANEL.PRELIM_UNIVERAL.SIZE, iRowNum, "");
                				}
                			}
                			
                			{
                    			sValue = LIB.safeGetString(tRawReport, COLS.RAW_REPORT.PRELIM_EOD.SIM_DEF_NAME, iReportCounter);
                    			tScriptPanel.setCellString(COLS.SCRIPT_PANEL.PRELIM_EOD.SIM_DEF_NAME, iRowNum, sValue, FORMAT.DOUBLE_CLICK);
                    			
                    			sValue = LIB.safeGetString(tRawReport, COLS.RAW_REPORT.PRELIM_UNIVERAL.SIM_DEF_NAME, iReportCounter);
                    			tScriptPanel.setCellString(COLS.SCRIPT_PANEL.PRELIM_UNIVERAL.SIM_DEF_NAME, iRowNum, sValue, FORMAT.DOUBLE_CLICK);
                			}
                			
            			} // END             			if (revalRunType == SIMULATION_RUN_TYPE.P_EOD_SIM_TYPE) 
            			 
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
    	
    	public static void addDataToRawReportTable(Table tRawReport, Table tPortfolio, SIMULATION_RUN_TYPE revalRunType, 
    			Table tSimHeader, Table tTradeCountByPortfolio, PORTFOLIOS_TO_SHOW portfoliosToShow, String className) throws OException{

        	String sWhat = "";
        	String sWhere = "";
        	try{
        		// TOxDO, do this?
        	//	tRawReport.clearRows();
        		
        		sWhat = COLS.PORTFOLIO.PORTFOLIO_ID + "(" + COLS.RAW_REPORT.PORTFOLIO_ID + ")" + ", " +
        				COLS.PORTFOLIO.PORTFOLIO + "(" + COLS.RAW_REPORT.PORTFOLIO + ")";
        		sWhere = COLS.PORTFOLIO.PORTFOLIO_ID + " GE -1";
        		LIB.select(tRawReport, tPortfolio, sWhat, sWhere, false, className);	
        		
        		// Default this to -1 because 0 (zero) is actually a valid value, which we don't want to show
        		LIB.safeSetColValInt(tRawReport, COLS.RAW_REPORT.PRELIM_EOD.REVAL_TYPE, CONST_VALUES.NOT_APPLICABLE);
        		LIB.safeSetColValInt(tRawReport, COLS.RAW_REPORT.PRELIM_UNIVERAL.REVAL_TYPE, CONST_VALUES.NOT_APPLICABLE); 
        		
        		// get the run date from any row... it is all the same for all rows
        		if (LIB.safeGetNumRows(tSimHeader) >= 1) {
            		int iRunDate = LIB.safeGetInt(tSimHeader, COLS.SIM_HEADER.RUN_DATE, CONST_VALUES.ROW_TO_GET_OR_SET);
            		LIB.safeSetColValInt(tRawReport, COLS.RAW_REPORT.RUN_DATE, iRunDate); 
        		}
 
        		
        		if (revalRunType == SIMULATION_RUN_TYPE.P_EOD_SIM_TYPE) {
            		sWhat = COLS.SIM_HEADER.REVAL_TYPE + "(" + COLS.RAW_REPORT.PRELIM_EOD.REVAL_TYPE + ")" + ", " +
            				COLS.SIM_HEADER.SIM_DEF_ID + "(" + COLS.RAW_REPORT.PRELIM_EOD.SIM_DEF_ID + ")" + ", " +
            				COLS.SIM_HEADER.EXTRA.SIM_DEF_NAME + "(" + COLS.RAW_REPORT.PRELIM_EOD.SIM_DEF_NAME + ")" + ", " +
            				COLS.SIM_HEADER.GEN_TIME + "(" + COLS.RAW_REPORT.PRELIM_EOD.GEN_TIME + ")" + ", " +
            				COLS.SIM_HEADER.EXTRA.BLOB_SIZE_MEGS + "(" + COLS.RAW_REPORT.PRELIM_EOD.SIZE + ")" + ", " +
            				COLS.SIM_HEADER.SIM_RUN_ID + "(" + COLS.RAW_REPORT.PRELIM_EOD.SIM_RUN_ID + ")";
            		sWhere = COLS.SIM_HEADER.PORTFOLIO + " EQ $" + COLS.RAW_REPORT.PORTFOLIO_ID + " AND " + 
            				 COLS.SIM_HEADER.REVAL_TYPE + " EQ " + SIMULATION_RUN_TYPE.P_EOD_SIM_TYPE.toInt();
            		LIB.select(tRawReport, tSimHeader, sWhat, sWhere, false, className);	

            		sWhat = COLS.SIM_HEADER.REVAL_TYPE + "(" + COLS.RAW_REPORT.PRELIM_UNIVERAL.REVAL_TYPE + ")" + ", " +
            				COLS.SIM_HEADER.SIM_DEF_ID + "(" + COLS.RAW_REPORT.PRELIM_UNIVERAL.SIM_DEF_ID + ")" + ", " +
            				COLS.SIM_HEADER.EXTRA.SIM_DEF_NAME + "(" + COLS.RAW_REPORT.PRELIM_UNIVERAL.SIM_DEF_NAME + ")" + ", " +
            				COLS.SIM_HEADER.GEN_TIME + "(" + COLS.RAW_REPORT.PRELIM_UNIVERAL.GEN_TIME + ")" + ", " +
            				COLS.SIM_HEADER.EXTRA.BLOB_SIZE_MEGS + "(" + COLS.RAW_REPORT.PRELIM_UNIVERAL.SIZE + ")" + ", " +
            				COLS.SIM_HEADER.SIM_RUN_ID + "(" + COLS.RAW_REPORT.PRELIM_UNIVERAL.SIM_RUN_ID + ")";
            		sWhere = COLS.SIM_HEADER.PORTFOLIO + " EQ $" + COLS.RAW_REPORT.PORTFOLIO_ID + " AND " + 
            				 COLS.SIM_HEADER.REVAL_TYPE + " EQ " + SIMULATION_RUN_TYPE.INTRA_DAY_SIM_TYPE.toInt();
            		LIB.select(tRawReport, tSimHeader, sWhat, sWhere, false, className);	
        		}

        		if (revalRunType == SIMULATION_RUN_TYPE.EOD_SIM_TYPE) {
            		sWhat = COLS.SIM_HEADER.REVAL_TYPE + "(" + COLS.RAW_REPORT.EOD.REVAL_TYPE + ")" + ", " +
            				COLS.SIM_HEADER.SIM_DEF_ID + "(" + COLS.RAW_REPORT.EOD.SIM_DEF_ID + ")" + ", " +
            				COLS.SIM_HEADER.EXTRA.SIM_DEF_NAME + "(" + COLS.RAW_REPORT.EOD.SIM_DEF_NAME + ")" + ", " +
            				COLS.SIM_HEADER.GEN_TIME + "(" + COLS.RAW_REPORT.EOD.GEN_TIME + ")" + ", " +
            				COLS.SIM_HEADER.EXTRA.BLOB_SIZE_MEGS + "(" + COLS.RAW_REPORT.EOD.SIZE + ")" + ", " +
            				COLS.SIM_HEADER.SIM_RUN_ID + "(" + COLS.RAW_REPORT.EOD.SIM_RUN_ID + ")";
            		sWhere = COLS.SIM_HEADER.PORTFOLIO + " EQ $" + COLS.RAW_REPORT.PORTFOLIO_ID + " AND " + 
            				 COLS.SIM_HEADER.REVAL_TYPE + " EQ " + SIMULATION_RUN_TYPE.EOD_SIM_TYPE.toInt();
            		LIB.select(tRawReport, tSimHeader, sWhat, sWhere, false, className);	
        		}
 
        		// Num Trades
        		sWhat = COLS.PORTFOLIO_LIST_WITH_TRADE_COUNT.NUM_TRADES + "(" + COLS.RAW_REPORT.NUM_TRADES + ")";
        		sWhere = COLS.PORTFOLIO_LIST_WITH_TRADE_COUNT.PORTFOLIO_ID + " EQ $" + COLS.RAW_REPORT.PORTFOLIO_ID;
        		LIB.select(tRawReport, tTradeCountByPortfolio, sWhat, sWhere, false, className);	
        		 
        		if (portfoliosToShow == PORTFOLIOS_TO_SHOW.WITH_TRADES) {
            		tRawReport.deleteWhereValue(COLS.RAW_REPORT.NUM_TRADES, 0);
        		}
        		
        	} catch(Exception e ){               
        		LIB.log("addDataToRawReportTable", e, className);
        	}
    	} 

   	 
    	public static void addSimBlobSizeToSimHeaderTable(Table tSimHeader, Table tSimBlobSize, String className) throws OException{

        	try{
  
        		LIB.safeAddCol(tSimHeader, COLS.SIM_HEADER.EXTRA.BLOB_SIZE_MEGS, COL_TYPE_ENUM.COL_DOUBLE);
  
        		String sWhat = COLS.SIM_BLOB_SIZE.BLOB_SIZE_MEGS + "(" + COLS.SIM_HEADER.EXTRA.BLOB_SIZE_MEGS + ")";
        		String sWhere = COLS.SIM_BLOB_SIZE.SIM_RUN_ID + " EQ $" + COLS.SIM_HEADER.SIM_RUN_ID;
        		LIB.select(tSimHeader, tSimBlobSize, sWhat, sWhere, false, className);	
                 
        	} catch(Exception e ){               
        		LIB.log("addSimBlobSizeToSimHeaderTable", e, className);
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
    			SIMULATION_RUN_TYPE revalRunType, String className) throws OException{

    		int iValue = 0;
    		double dValue = 0;
    		String sValue = "";
        	try{
        		//tScriptPanel.clearRows();
        		
        		int iNumRows = LIB.safeGetNumRows(tRawReport);
        		tScriptPanel.addNumRows(iNumRows);
        		
        		for (int iCounter = 1; iCounter <= iNumRows; iCounter++) {
        			iValue = LIB.safeGetInt(tRawReport, COLS.RAW_REPORT.PORTFOLIO_ID, iCounter);
        			LIB.safeSetInt(tScriptPanel, COLS.SCRIPT_PANEL.HIDDEN_PORTFOLIO_ID, iCounter, iValue);
        			tScriptPanel.setCellInt(COLS.SCRIPT_PANEL.PORTFOLIO_ID, iCounter, iValue, FORMAT.DOUBLE_CLICK + "," + FORMAT.JUSTIFY.CENTER);
        			
        			sValue = LIB.safeGetString(tRawReport, COLS.RAW_REPORT.PORTFOLIO, iCounter);
        			tScriptPanel.setCellString(COLS.SCRIPT_PANEL.PORTFOLIO, iCounter, sValue, FORMAT.DOUBLE_CLICK);

        			tScriptPanel.setCellString(COLS.SCRIPT_PANEL.RUN_DATE, iCounter, sRunDate);
         			
        			{
            			iValue = LIB.safeGetInt(tRawReport, COLS.RAW_REPORT.NUM_TRADES, iCounter);
            			tScriptPanel.setCellInt(COLS.SCRIPT_PANEL.NUM_TRADES, iCounter, iValue, FORMAT.DOUBLE_CLICK + ", " + FORMAT.JUSTIFY.RIGHT);
        			}
        			

        			if (revalRunType == SIMULATION_RUN_TYPE.EOD_SIM_TYPE) {
        				
        				{
                			sValue = LIB.safeGetString(tRawReport, COLS.RAW_REPORT.EOD.GEN_TIME, iCounter);
                			tScriptPanel.setCellString(COLS.SCRIPT_PANEL.EOD.GEN_TIME, iCounter, sValue, FORMAT.DOUBLE_CLICK);

            			}

            			boolean bEODHasSims = false; 
            			{
                			iValue = LIB.safeGetInt(tRawReport, COLS.RAW_REPORT.EOD.SIM_RUN_ID, iCounter);
                			
                			if (iValue >= 1) {
                				bEODHasSims = true;
                				LIB.safeSetInt(tScriptPanel, COLS.SCRIPT_PANEL.EOD_COMPLETE, iCounter, CONST_VALUES.VALUE_OF_TRUE);
                    			tScriptPanel.setCellInt(COLS.SCRIPT_PANEL.EOD.SIM_RUN_ID, iCounter, iValue, FORMAT.DOUBLE_CLICK);
                			} else {
                    			tScriptPanel.setCellString(COLS.SCRIPT_PANEL.EOD.SIM_RUN_ID, iCounter, "");
                			}
            			}
            			
            			{
            				if (bEODHasSims == true) {
                    			dValue = LIB.safeGetDouble(tRawReport, COLS.RAW_REPORT.EOD.SIZE, iCounter);
                    			tScriptPanel.setCellDouble(COLS.SCRIPT_PANEL.EOD.SIZE, iCounter, dValue, FORMAT.DOUBLE_CLICK + "," + FORMAT.JUSTIFY.RIGHT);
            				} else {
                    			tScriptPanel.setCellString(COLS.SCRIPT_PANEL.EOD.SIZE, iCounter, "");
            				} 
            			}
            			 
            			{
                			sValue = LIB.safeGetString(tRawReport, COLS.RAW_REPORT.EOD.SIM_DEF_NAME, iCounter);
                			tScriptPanel.setCellString(COLS.SCRIPT_PANEL.EOD.SIM_DEF_NAME, iCounter, sValue, FORMAT.DOUBLE_CLICK);
            			}
        			}

        			if (revalRunType == SIMULATION_RUN_TYPE.P_EOD_SIM_TYPE) {
        			
        				{
                			sValue = LIB.safeGetString(tRawReport, COLS.RAW_REPORT.PRELIM_EOD.GEN_TIME, iCounter);
                			tScriptPanel.setCellString(COLS.SCRIPT_PANEL.PRELIM_EOD.GEN_TIME, iCounter, sValue, FORMAT.DOUBLE_CLICK);

                			sValue = LIB.safeGetString(tRawReport, COLS.RAW_REPORT.PRELIM_UNIVERAL.GEN_TIME, iCounter);
                			tScriptPanel.setCellString(COLS.SCRIPT_PANEL.PRELIM_UNIVERAL.GEN_TIME, iCounter, sValue, FORMAT.DOUBLE_CLICK);
            			}

            			boolean bPrelimEODHasSims = false;
            			boolean bPrelimUniversalHasSims = false;
            			{
                			iValue = LIB.safeGetInt(tRawReport, COLS.RAW_REPORT.PRELIM_EOD.SIM_RUN_ID, iCounter);
                			
                			if (iValue >= 1) {
                				bPrelimEODHasSims = true;
                				LIB.safeSetInt(tScriptPanel, COLS.SCRIPT_PANEL.PRELIM_EOD_COMPLETE, iCounter, CONST_VALUES.VALUE_OF_TRUE);
                    			tScriptPanel.setCellInt(COLS.SCRIPT_PANEL.PRELIM_EOD.SIM_RUN_ID, iCounter, iValue, FORMAT.DOUBLE_CLICK);
                			} else {
                    			tScriptPanel.setCellString(COLS.SCRIPT_PANEL.PRELIM_EOD.SIM_RUN_ID, iCounter, "");
                			}

                			iValue = LIB.safeGetInt(tRawReport, COLS.RAW_REPORT.PRELIM_UNIVERAL.SIM_RUN_ID, iCounter);
                			
                			if (iValue >= 1) {
                				bPrelimUniversalHasSims = true;
                				LIB.safeSetInt(tScriptPanel, COLS.SCRIPT_PANEL.PRELIM_UNIVERAL_COMPLETE, iCounter, CONST_VALUES.VALUE_OF_TRUE);
                    			tScriptPanel.setCellInt(COLS.SCRIPT_PANEL.PRELIM_UNIVERAL.SIM_RUN_ID, iCounter, iValue, FORMAT.DOUBLE_CLICK);
                			} else {
                    			tScriptPanel.setCellString(COLS.SCRIPT_PANEL.PRELIM_UNIVERAL.SIM_RUN_ID, iCounter, "");
                			}
            			}
            			

            			{
            				if (bPrelimEODHasSims == true) {
                    			dValue = LIB.safeGetDouble(tRawReport, COLS.RAW_REPORT.PRELIM_EOD.SIZE, iCounter);
                    			tScriptPanel.setCellDouble(COLS.SCRIPT_PANEL.PRELIM_EOD.SIZE, iCounter, dValue, FORMAT.DOUBLE_CLICK + ", " + FORMAT.JUSTIFY.RIGHT);
            				} else {
                    			tScriptPanel.setCellString(COLS.SCRIPT_PANEL.PRELIM_EOD.SIZE, iCounter, "");
            				}
            				
            				if (bPrelimUniversalHasSims == true) {
                    			dValue = LIB.safeGetDouble(tRawReport, COLS.RAW_REPORT.PRELIM_UNIVERAL.SIZE, iCounter);
                    			tScriptPanel.setCellDouble(COLS.SCRIPT_PANEL.PRELIM_UNIVERAL.SIZE, iCounter, dValue, FORMAT.DOUBLE_CLICK + ", " + FORMAT.JUSTIFY.RIGHT);
            				} else {
                    			tScriptPanel.setCellString(COLS.SCRIPT_PANEL.PRELIM_UNIVERAL.SIZE, iCounter, "");
            				}

            			}
            			 
            			{
                			sValue = LIB.safeGetString(tRawReport, COLS.RAW_REPORT.PRELIM_EOD.SIM_DEF_NAME, iCounter);
                			tScriptPanel.setCellString(COLS.SCRIPT_PANEL.PRELIM_EOD.SIM_DEF_NAME, iCounter, sValue, FORMAT.DOUBLE_CLICK);
                			
                			sValue = LIB.safeGetString(tRawReport, COLS.RAW_REPORT.PRELIM_UNIVERAL.SIM_DEF_NAME, iCounter);
                			tScriptPanel.setCellString(COLS.SCRIPT_PANEL.PRELIM_UNIVERAL.SIM_DEF_NAME, iCounter, sValue, FORMAT.DOUBLE_CLICK);
            			}
            			
        			} // END if (revalRunType == SIMULATION_RUN_TYPE.P_EOD_SIM_TYPE) 

        			
        		}
        		  	
        	} catch(Exception e ){               
        		LIB.log("addRawReportToScriptPanelForFirstTime", e, className);
        	}
          } 


    	
    	public static void setAllLabelValuesForRefresh(Table tScriptPanel, SIMULATION_RUN_TYPE revalRunType, String className) throws OException{

        	try{
        		
        		// This is the total number of portfolios
        		int iNumPortfolios = LIB.safeGetNumRows(tScriptPanel);
        		 
        		int iNumWithSims = 0;
        		
        		if (revalRunType == SIMULATION_RUN_TYPE.EOD_SIM_TYPE) {
            		for (int iCounter = 1; iCounter <= iNumPortfolios; iCounter++) {
            			int iEODComplete = LIB.safeGetInt(tScriptPanel, COLS.SCRIPT_PANEL.EOD_COMPLETE, iCounter);
            			
            			if (iEODComplete == CONST_VALUES.VALUE_OF_TRUE) {
            				iNumWithSims = iNumWithSims + 1;
            			}
            		}
        		}
        		
        		if (revalRunType == SIMULATION_RUN_TYPE.P_EOD_SIM_TYPE) {
            		for (int iCounter = 1; iCounter <= iNumPortfolios; iCounter++) {
            			int iPrelimEODComplete = LIB.safeGetInt(tScriptPanel, COLS.SCRIPT_PANEL.PRELIM_EOD_COMPLETE, iCounter);
            			int iPrelimUniversalComplete = LIB.safeGetInt(tScriptPanel, COLS.SCRIPT_PANEL.PRELIM_UNIVERAL_COMPLETE, iCounter);
            			
            			if (iPrelimEODComplete == CONST_VALUES.VALUE_OF_TRUE &&
            					iPrelimUniversalComplete == CONST_VALUES.VALUE_OF_TRUE) {
            				iNumWithSims = iNumWithSims + 1;
            			}
            		}
        		}
        		   
        		GUI.modifyLabelWithTextToShowUser(tScriptPanel, LABEL.LABEL4, Str.intToStr(iNumPortfolios), className);
        		
        		{
            		int iNumWithOutSims = iNumPortfolios - iNumWithSims;
            		
            		String widget_options = "";
                	if (iNumWithOutSims == 0) {
            			widget_options = "bg=BLACK,fg=GREEN,label=" + iNumWithOutSims;
            		} else {
                		widget_options = "bg=BLACK,fg=RED,label=" + iNumWithOutSims;
            		}
            		
            		tScriptPanel.scriptDataModifyWidget(LABEL.LABEL6.getName(), 
            				SCRIPT_PANEL_WIDGET_TYPE_ENUM.SCRIPT_PANEL_LABEL_WIDGET.toInt(), 
            				LABEL.LABEL6.getPosition(), widget_options);

            		if (iNumWithOutSims == 0) {
            			widget_options = "bg=BLACK,fg=GREEN,label=" + LABEL.LABEL5.getLabel();
            		} else {
                		widget_options = "bg=RED,fg=BLACK,label=" + LABEL.LABEL5.getLabel();
            		}
            		
            		// This is the widget that shows portfolios *MISSING SIMS*
            		GUI.modifyLabelWithWidgetOptions(tScriptPanel, LABEL.LABEL5, widget_options, className);
        		}
        		
        		// Number of Portfolios that do have sims
        		GUI.modifyLabelWithTextToShowUser(tScriptPanel, LABEL.LABEL8, Str.intToStr(iNumWithSims), className);
        		
        		// DateTimeForLastUpdatedDateTime
        		GUI.modifyLabelWithTextToShowUser(tScriptPanel, LABEL.LABEL10, FUNC.getDateTimeForLastUpdatedDateTime(className), className);

        	} catch(Exception e ){               
        		LIB.log("setAllLabelValuesForRefresh", e, className);
        	}
    	} 

    	
    	public static void showTradeListForPortfolio(Table tScriptPanel, int iPortfolioID, String sPortfolio, String className) throws OException{

        	try{
        	 
    			int iTranStatusToGet = TRAN_STATUS_ENUM.TRAN_STATUS_VALIDATED.toInt();
    			if (Str.iEqual(sPortfolio, "CVA RISK PROXIES") == 1 ||
    					Str.iEqual(sPortfolio, "FVA RISK PROXIES") == 1) {
    				iTranStatusToGet = TRAN_STATUS_ENUM.TRAN_STATUS_PENDING.toInt();
    			}
    			  	
    			// This is a quick approach, meant as a proof of concept.  A better approach might add fields like proj index
    			Table tTrades = Table.tableNew("Trades for Portfolio: " + sPortfolio);
    			String sSQL = "SELECT \n" + 
    					"a.deal_tracking_num, a.tran_num, a.toolset, a.ins_type, a.internal_portfolio, a.external_bunit, a.trade_date, a.start_date, a.maturity_date, a.last_update\n" + 
    					"FROM\n" + 
    					"ab_tran a\n" + 
    					"WHERE\n" + 
    					"a.tran_status = " + iTranStatusToGet + " AND\n" + 
    					"a.trade_flag = 1 AND a.toolset != " + TOOLSET_ENUM.COMPOSER_TOOLSET.toInt() + " AND \n" + 
    					"a.internal_portfolio = " + iPortfolioID;
    			LIB.execISql(tTrades, sSQL, true, className);
    			
    			tTrades.colConvertDateTimeToInt("trade_date");
    			tTrades.colConvertDateTimeToInt("start_date");
    			tTrades.colConvertDateTimeToInt("maturity_date");
    			
    			tTrades.defaultFormat();
    			LIB.safeSetColFormatAsDate(tTrades, "trade_date");
    			LIB.safeSetColFormatAsDate(tTrades, "start_date");
    			LIB.safeSetColFormatAsDate(tTrades, "start_date");
    			
    			tTrades.group("deal_tracking_num");
    			
    			// This view table will *not* have a call back to it... i.e., can't do a call back on a viewTable from a callback table
    			LIB.viewTable(tTrades);
    			
    			tTrades = LIB.destroyTable(tTrades);

        	} catch(Exception e ){               
        		LIB.log("showTradeListForPortfolio", e, className);
        	}
    	} 

    } // END public static class FUNC
    
    
    
    public static class COLS {     
 
    	// These columns are cosmetic.. can change the names
    	public static class SIM_HEADER {     
        	public static final String SIM_RUN_ID = "sim_run_id"; //COL_INT
        	public static final String SIM_DEF_ID = "sim_def_id"; //COL_INT
        	public static final String GEN_TIME = "gen_time"; //COL_DATE_TIME
        	public static final String RUN_DATE = "run_date"; //COL_INT
        	public static final String REVAL_TYPE = "reval_type"; //COL_INT
        	public static final String REFERENCE = "reference"; //COL_STRING
        	public static final String PORTFOLIO = "portfolio"; //COL_INT
        	public static class EXTRA {     
            	public static final String SIM_DEF_NAME = "sim_def"; //COL_STRING
            	public static final String BLOB_SIZE_MEGS = "sim_blob_size_megs"; //COL_DOUBLE            	
        	}
    	}

    	// These columns are cosmetic.. can change the names
    	public static class SIM_BLOB_SIZE {     
        	public static final String SIM_RUN_ID = "sim_run_id"; //COL_INT  
        	public static final String BLOB_SIZE_MEGS = "sim_blob_size_megs"; //COL_DOUBLE
    	}
    	
    	// These columns are cosmetic.. can change the names
    	public static class COMBOBOX {     
        	public static final String ITEM_ID = "item_id"; //COL_INT  
        	public static final String ITEM = "item"; //COL_STRING
    	}

    	// These columns are cosmetic.. can change the names
    	public static class PORTFOLIO {     
        	public static final String PORTFOLIO_ID = "portfolio_id"; //COL_INT
        	public static final String PORTFOLIO = "portfolio"; //COL_STRING
    	}

    	public static class PORTFOLIO_LIST_WITH_TRADE_COUNT {     
        	public static final String PORTFOLIO_ID = "portfolio_id";
        	public static final String NUM_TRADES = "num_trades";
        }
    	
    	// These columns are cosmetic.. can change the names
    	public static class RAW_REPORT {     
        	public static final String PORTFOLIO_ID = "portfolio_id"; //COL_INT
        	public static final String PORTFOLIO = "portfolio"; //COL_STRING
        	public static final String RUN_DATE = "run_date"; //COL_INT
        	public static final String NUM_TRADES = "num_trades"; //COL_INT

        	public static class EOD {     
            	public static final String REVAL_TYPE = "EOD_RevalType"; //COL_STRING
            	public static final String SIM_DEF_ID = "EOD_SimDefID"; //COL_INT
            	public static final String SIM_DEF_NAME = "EOD_SimDef"; //COL_STRING
            	public static final String SIM_RUN_ID = "EOD_SimRunID"; //COL_STRING
            	public static final String GEN_TIME = "EOD_gen_time"; //COL_DATE_TIME
            	public static final String SIZE = "EOD_SizeMegs"; //COL_STRING
        	}

        	public static class PRELIM_EOD {     
            	public static final String REVAL_TYPE = "PrelimEOD_RevalType"; //COL_STRING
            	public static final String SIM_DEF_ID = "PrelimEOD_SimDefID"; //COL_INT
            	public static final String SIM_DEF_NAME = "PrelimEOD_SimDef"; //COL_STRING
            	public static final String SIM_RUN_ID = "PrelimEOD_SimRunID"; //COL_STRING
            	public static final String GEN_TIME = "PrelimEOD_gen_time"; //COL_DATE_TIME
            	public static final String SIZE = "PrelimEOD_SizeMegs"; //COL_STRING
        	}
        	public static class PRELIM_UNIVERAL {     
            	public static final String REVAL_TYPE = "PrelimUniversal_RevalType"; //COL_STRING
            	public static final String SIM_DEF_ID = "PrelimUniversal_SimDefID"; //COL_INT
            	public static final String SIM_DEF_NAME = "PrelimUniversal_SimDef"; //COL_STRING
            	public static final String SIM_RUN_ID = "PrelimUniversal_SimRunID"; //COL_STRING
            	public static final String GEN_TIME = "PrelimUniversal_gen_time"; //COL_DATE_TIME
            	public static final String SIZE = "PrelimUniversal_SizeMegs"; //COL_STRING
        	}

    	}

    	// These columns are cosmetic.. can change the names
    	public static class SCRIPT_PANEL extends RAW_REPORT{     
    		// this will be used for sorting
        	public static final String HIDDEN_PORTFOLIO_ID = "hidden_portfolio_id"; //COL_INT
        	public static final String EOD_COMPLETE = "EOD_Complete"; //COL_INT
        	public static final String PRELIM_EOD_COMPLETE = "PrelimEOD_Complete"; //COL_INT
        	public static final String PRELIM_UNIVERAL_COMPLETE = "PrelimUniversal_Complete"; //COL_INT
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
        	public static final int W = 1500;
        	public static final int H = 1000;
        }
		
    }

    public enum BUTTON{
    	BUTTON1 ("BUTTON1", "Reload Data", "x=15, y=10, h=35, w=120"), 
    	BUTTON2 ("BUTTON2", "Launch\nServices Manager", "x=1100, y=1, h=40, w=100"),
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
    	LABEL2 ("LABEL2", "Display Portfolios", "x=260, y=1, h=20, w=110"), 
    	
    	// # Portfolio label
    	LABEL3 ("LABEL3", "Portfolios", 				   "x=381, y=1, h=18, w=100"), 
    	// # Porfolios displayed, i.e., can change
    	LABEL4 ("LABEL4", "99", "x=372, y=1, h=18, w=30"), 

    	LABEL5 ("LABEL5", "Portfolios Missing Sims",       "x=402, y=19, h=18, w=119"), 
    	LABEL6 ("LABEL6", "99", "x=372, y=19, h=18, w=30"),

		LABEL7_PRELIM_EOD ("LABEL7_PRELIM_EOD", "Portfolios with Sims for Both", "x=403, y=37, h=18, w=140"), 
		LABEL7_EOD ("LABEL7_EOD", "Portfolios with EOD Sims", "x=407, y=37, h=18, w=120"), 
    	LABEL8 ("LABEL8", "99", "x=372, y=37, h=18, w=30"),

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
    	COMBOBOX1 ("COMBOBOX1", "Refresh Interval", "x=150, y=20, h=20, w=90"), 
    	COMBOBOX2 ("COMBOBOX2", "Display Portfolios", "x=260, y=20, h=20, w=110"), 
    	COMBOBOX3 ("COMBOBOX3", "COMBOBOX2", "x=400, y=2, h=20, w=100")
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

    }
    
    public static class AFE_SERVICE {    
    	// get the full list from 
    	// select * from afe_service_tbl
    	public static final int SERVICES_MANAGER = 74;
    	public static final int SIMULATION_RESULT_VIEWER = 425;
    }
    
    public static class FORMAT { 
        public static class JUSTIFY { 
        	public static final String RIGHT = "just=RIGHT";
        	public static final String CENTER = "just=CENTER";
        }
    	
    	public static final String DOUBLE_CLICK = "dblclick=TRUE";

    }
  	
    public static class LIB {            

    	public static final String VERSION_NUMBER = "V1.003 (19Apr2023)";

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
