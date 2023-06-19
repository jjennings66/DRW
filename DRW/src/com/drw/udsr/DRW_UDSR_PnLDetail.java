package com.drw.udsr;

import com.olf.openjvs.Afs;
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
import com.olf.openjvs.enums.COL_TYPE_ENUM;
import com.olf.openjvs.enums.DATE_FORMAT;
import com.olf.openjvs.enums.DATE_LOCALE;
import com.olf.openjvs.enums.PFOLIO_RESULT_TYPE;
import com.olf.openjvs.enums.SCRIPT_CATEGORY_ENUM;
import com.olf.openjvs.enums.SCRIPT_TYPE_ENUM;
import com.olf.openjvs.enums.SHM_USR_TABLES_ENUM;
import com.olf.openjvs.enums.TABLE_VIEWER_MODE;
import com.olf.openjvs.enums.TRANF_FIELD;
import com.olf.openjvs.enums.TRANF_GROUP;
import com.olf.openjvs.enums.USER_RESULT_OPERATIONS;

/*

Name: DRW_UDSR_PnLDetail

Description:
This is a copy of the OpenLink PnL Detail sim result with DRW-Specific changes/enhancements

1) Set this up as a UDSR. 

2) Dependency:  PnL Detail
  
Revision History:
30-May-2023  	Brian       New Script  
01-Jun-2023     Brian       Added Extract to DW Tables functionality
  
*/

@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_SIM_RESULT)
@PluginType(SCRIPT_TYPE_ENUM.MAIN_SCRIPT)

public class DRW_UDSR_PnLDetail implements IScript {

	// TODO, set to false for production
	static boolean DEBUG_FLAG = false;

	public void execute(IContainerContext context) throws OException {

		String className = this.getClass().getSimpleName();

		try {
			// This is functional as we include only payments with dates on or after this day
			int iToday = OCalendar.today();

			LIB.logForDebug("RUNNING DRW_UDSR_PnLDetail Result, Current Date: " +
			OCalendar.formatJd(iToday, DATE_FORMAT.DATE_FORMAT_DMLY_NOSLASH) + " (" + iToday + ")", DEBUG_FLAG, className);

			Table argt = context.getArgumentsTable();
			Table returnt = context.getReturnTable();
 
			this.doEverything(argt, returnt, iToday, className);

			LIB.logForDebug("END", DEBUG_FLAG, className);

		} catch (Exception e) {
			LIB.log("execute", e, className);
		}
 	}

	void doEverything(Table argt, Table returnt, int iToday, String className) throws OException {

		int iOperation = LIB.safeGetInt(argt, "operation", CONST_VALUES.ROW_TO_GET_OR_SET);

		if (iOperation == USER_RESULT_OPERATIONS.USER_RES_OP_CALCULATE.toInt()) {
			
			CREATE_TABLE.addColsToResultTable(returnt, className);

			// Do not destroy this
			Table tTranPointersFromArgt = LIB.safeGetTable(argt, COLS.ARGT.transactions, CONST_VALUES.ROW_TO_GET_OR_SET);

			int iNumTrans = LIB.safeGetNumRows(tTranPointersFromArgt);
			LIB.logForDebug("Number of TRANS (tran pointers) found: " + iNumTrans, DEBUG_FLAG, className);

			// Testing confirmed that all deals are passed in, even if
			// a) they are not CallNot and
			// b) the sim results are setup in the Admin Manager to be run only for CallNot.
			// So *do* need to do some filtering, which we'll do using the Tran Results
			Table tSims = LIB.safeGetTable(argt, COLS.ARGT.sim_results, CONST_VALUES.ROW_TO_GET_OR_SET);

			Table tGenResults = LIB.safeGetTable(tSims, "result_class", CONST_VALUES.GEN_RESULTS_ROW);
			
			// TOxDO
			//LIB.viewTable(tGenResults, "tGenResults");
			
			int iRowNumPnLDetail = tGenResults.unsortedFindInt("result_type", PFOLIO_RESULT_TYPE.PNL_DETAIL_RESULT.toInt());
			if(iRowNumPnLDetail >= 1) {
				Table tPnLDetail = LIB.safeGetTable(tGenResults, "result", iRowNumPnLDetail);

				if (LIB.safeGetNumRows(tPnLDetail) >= 1) {
					FUNC.doEverythingCalculate(tTranPointersFromArgt, returnt, tPnLDetail, iToday, className);
				}
			}

		}

		if (iOperation == USER_RESULT_OPERATIONS.USER_RES_OP_FORMAT.toInt()) {
			LIB.log("Running Format Function", className);
			FUNC.format_result(returnt, className);
		}

		if (iOperation == USER_RESULT_OPERATIONS.USER_RES_OP_DWEXTRACT.toInt()) {
			LIB.log("Running Extract to DW (Data Warehouse) Table Function", className);
			FUNC.dw_extract_result(argt, returnt, className);
		}

	}


	private static class FUNC {
  
		private static void doEverythingCalculate(Table tTranPointersFromArgt, Table returnt, Table tPnLDetail, int iToday, String className) throws OException {

			try {
				
				// Start by copying from the original
				tPnLDetail.copyRowAddAllByColName(returnt);
			 
				// Get Fields from Trades
				{
					Table tTemp = Table.tableNew();
					LIB.safeAddCol(tTemp, COLS.RESULT.deal_num, COL_TYPE_ENUM.COL_INT);
					LIB.safeAddCol(tTemp, COLS.RESULT.EXTRA.toolset, COL_TYPE_ENUM.COL_INT);
					LIB.safeAddCol(tTemp, COLS.RESULT.EXTRA.ins_type, COL_TYPE_ENUM.COL_INT);
					LIB.safeAddCol(tTemp, COLS.RESULT.EXTRA.internal_portfolio, COL_TYPE_ENUM.COL_INT);
					LIB.safeAddCol(tTemp, COLS.RESULT.EXTRA.external_portfolio, COL_TYPE_ENUM.COL_INT);
	            	int iNumRows = tTranPointersFromArgt.getNumRows();
	            	for (int iCounter = 1; iCounter <= iNumRows; iCounter++) {
	            		Transaction tran = tTranPointersFromArgt.getTran("tran_ptr", iCounter);
	            		int iDealNum = tran.getFieldInt(TRANF_FIELD.TRANF_DEAL_TRACKING_NUM);
	            		int iToolset = tran.getFieldInt(TRANF_FIELD.TRANF_TOOLSET_ID);
	            		int iInsType = tran.getFieldInt(TRANF_FIELD.TRANF_INS_TYPE);
	            		int iIntPortfolio = tran.getFieldInt(TRANF_FIELD.TRANF_INTERNAL_PORTFOLIO);
	            		int iExtPortfolio = tran.getFieldInt(TRANF_FIELD.TRANF_EXTERNAL_PORTFOLIO);
 	            		
	            		int iMaxRow = tTemp.addRow();
	            		tTemp.setInt(COLS.RESULT.deal_num, iMaxRow, iDealNum);
	            		tTemp.setInt(COLS.RESULT.EXTRA.toolset, iMaxRow, iToolset);
	            		tTemp.setInt(COLS.RESULT.EXTRA.ins_type, iMaxRow, iInsType);
	            		tTemp.setInt(COLS.RESULT.EXTRA.internal_portfolio, iMaxRow, iIntPortfolio);
	            		tTemp.setInt(COLS.RESULT.EXTRA.external_portfolio, iMaxRow, iExtPortfolio);
 	            	}
	            	tTemp.group(COLS.RESULT.deal_num);
	            	tTemp.distinctRows();
	            	
	            	String sWhat = COLS.RESULT.EXTRA.toolset + ", " + 
	            				COLS.RESULT.EXTRA.ins_type + ", " + 
	            				COLS.RESULT.EXTRA.internal_portfolio + ", " + 
	            				COLS.RESULT.EXTRA.external_portfolio;
	            	String sWhere = COLS.RESULT.deal_num + " EQ $" + COLS.RESULT.deal_num;
	            	LIB.select(returnt, tTemp, sWhat, sWhere, false, className);
	            	
 	            	tTemp = LIB.destroyTable(tTemp);
				}
 				
				// manually calculate Tran Num as a double
				try {
					if (LIB.safeGetNumRows(returnt) >= 1) {
						returnt.mathAddColConst(COLS.RESULT.tran_num, 0, COLS.RESULT.HIDDEN.tran_num_for_double_click);
					}
				} catch (Exception e) {
					// do not log this
				}
				
				// TOxDO
				// LIB.viewTable(tPnLDetail, "tPnLDetail");
				
 
				// TOxDO
				//LIB.viewTable(returnt, "returnt step 100");

				// this is cosmetic
				String sGroup = COLS.RESULT.deal_num + ", " + 
						COLS.RESULT.deal_leg + ", " + 
						COLS.RESULT.deal_leg_1 + ", " + 
						COLS.RESULT.deal_pdc + ", " + 
						COLS.RESULT.price_band;
				returnt.group(sGroup);
 
			} catch (Exception e) {
				LIB.log("doEverythingCalculate", e, className);
			}
		}
		

		private static void dw_extract_result(Table argt, Table returnt, String className) throws OException {

			try {
				String sTableName = LIB.safeGetString(argt, "dw_user_table_name", CONST_VALUES.ROW_TO_GET_OR_SET);
				returnt.setTableName(sTableName);
				DBUserTable.structure(returnt);
				
				int iThisResult = LIB.safeGetInt(argt, "result_type", CONST_VALUES.ROW_TO_GET_OR_SET);
				
				Table tSims = LIB.safeGetTable(argt, "sim_results", CONST_VALUES.ROW_TO_GET_OR_SET);
			 	Table tGenResults = LIB.safeGetTable(tSims, "result_class", CONST_VALUES.GEN_RESULTS_ROW);
			 	
				int iRowNumThisResults = tGenResults.unsortedFindInt("result_type", iThisResult);
				if(iRowNumThisResults >= 1) {
					Table tThisResult = LIB.safeGetTable(tGenResults, "result", iRowNumThisResults);
 
					if (LIB.safeGetNumRows(tThisResult) >= 1) {
						Table tThisResultCopy = tThisResult.copyTable();
						// format the result so strings show up formatted
						FUNC.format_result(tThisResultCopy, className);
						
						// get rid of column #40
						LIB.safeDelCol(tThisResultCopy, COLS.RESULT.HIDDEN.tran_num_for_double_click);
						
						// get rid of toolset_id if it is there
						LIB.safeDelCol(tThisResultCopy, "toolset_id");
						

//						try {
//							// it is called toolset in the database table
//							tThisResultCopy.setColName(COLS.RESULT.EXTRA.toolset_id, "toolset");
//						} catch (Exception e) {
//							// do not log this
//						} 
						
						// Convert Int Columns to Dates
						{
							try {
								tThisResultCopy.colConvertIntToDateTime(COLS.RESULT.start_date);
							} catch (Exception e) {
								// do not log this
							}
							try {
								tThisResultCopy.colConvertIntToDateTime(COLS.RESULT.end_date);
							} catch (Exception e) {
								// do not log this
							}
							try {
								tThisResultCopy.colConvertIntToDateTime(COLS.RESULT.rate_dtmn_date);
							} catch (Exception e) {
								// do not log this
							}
							try {
								tThisResultCopy.colConvertIntToDateTime(COLS.RESULT.pymt_date);
							} catch (Exception e) {
								// do not log this
							}	
						}
						 
						
						Table tOriginalTable = returnt.copyTable();
 						
						int iNumColsReturntBefore = LIB.safeGetNumCols(returnt);
						String sWhat = "*";
						String sWhere = "deal_num GE -1";
						LIB.select(returnt, tThisResultCopy, sWhat, sWhere, false, className);
						int iNumColsReturntAfter = LIB.safeGetNumCols(returnt);
						LIB.log("****** Num Cols Before: " + iNumColsReturntBefore , className);
						LIB.log("****** Num Cols After : " + iNumColsReturntAfter, className);
						
						if (iNumColsReturntAfter > iNumColsReturntBefore) {
							int iNumCols = LIB.safeGetNumCols(returnt);
							for (int iColCounter = iNumCols; iColCounter >= 1; iColCounter--) {
								String sColName = tOriginalTable.getColName(iColCounter);
								int iColNumNewTable = returnt.getColNum(sColName);
								if (iColNumNewTable < 1) {
									returnt.delCol(iColCounter);
								}
							}
							LIB.log("****** Num Cols After Removing Extras : " + LIB.safeGetNumCols(returnt), className);
						}

						tThisResultCopy = LIB.destroyTable(tThisResultCopy);
						tOriginalTable = LIB.destroyTable(tOriginalTable);
					}
				}
				
//				returnt.addRow();

				int iRunNum = DBUserTable.getUniqueId();
				Afs.saveTable(argt, "argt." + iRunNum);
				Afs.saveTable(returnt, "returnt." + iRunNum);
				
			} catch (Exception e) {
				LIB.log("dw_extract_result", e, className);
			}
		}
		
				

		private static void format_result(Table tMaster, String className) throws OException {

			try {
			 
				
				tMaster.setColTitle(COLS.RESULT.deal_num, "\nDeal\nNum");
				tMaster.setColTitle(COLS.RESULT.tran_num, "\nTran\nNum");
				tMaster.setColTitle(COLS.RESULT.deal_leg, "Param\nSeq\nNum");
				tMaster.setColTitle(COLS.RESULT.deal_leg_1, "Param\nSeq\nNum_1");
				tMaster.setColTitle(COLS.RESULT.deal_pdc, "Profile\nSeq\nNum");
				tMaster.setColTitle(COLS.RESULT.ins_seq_num, "Ins\nSeq\nNum");
				tMaster.setColTitle(COLS.RESULT.ins_source_id, "Instrument\nSource\nID");
				tMaster.setColTitle(COLS.RESULT.price_band, "Price\nBand");
				tMaster.setColTitle(COLS.RESULT.price_band_seq_num, "Price\nBand\nSeq\nNum");
				tMaster.setColTitle(COLS.RESULT.comm_opt_exercised_flag, "Commodity\nOption\nExercised\nFlag");
				tMaster.setColTitle(COLS.RESULT.cflow_type, "Cash\nFlow\nType");
				tMaster.setColTitle(COLS.RESULT.broker_fee_type, "Broker\nFee\nType");
				tMaster.setColTitle(COLS.RESULT.start_date, "\nStart\nDate");
				tMaster.setColTitle(COLS.RESULT.end_date, "\nEnd\nDate");
				tMaster.setColTitle(COLS.RESULT.pymt_date, "\nPayment\nDate");
				tMaster.setColTitle(COLS.RESULT.rate_dtmn_date, "Rate\nDtmn\nDate");
				tMaster.setColTitle(COLS.RESULT.currency_id, "\n\nCurrency");
				tMaster.setColTitle(COLS.RESULT.settlement_type, "\nSettlement\nType");
				tMaster.setColTitle(COLS.RESULT.volume, "\n\nVolume");
				tMaster.setColTitle(COLS.RESULT.price, "\n\nPrice");
				tMaster.setColTitle(COLS.RESULT.strike, "\n\nStrike");
				tMaster.setColTitle(COLS.RESULT.pymt, "\n\nPymt");
				tMaster.setColTitle(COLS.RESULT.total_value, "\nTotal\nValue");
				tMaster.setColTitle(COLS.RESULT.realized_value, "\nRealized\nValue");
				tMaster.setColTitle(COLS.RESULT.unrealized_value, "\nUnrealized\nValue");
				tMaster.setColTitle(COLS.RESULT.base_total_value, "Base\nTotal\nValue");
				tMaster.setColTitle(COLS.RESULT.base_realized_value, "Base\nRealized\nValue");
				tMaster.setColTitle(COLS.RESULT.base_unrealized_value, "Base\nUnrealized\nValue");
				tMaster.setColTitle(COLS.RESULT.df, "\n\nDf");
				tMaster.setColTitle(COLS.RESULT.tran_status, "\nTran\nStatus");
				tMaster.setColTitle(COLS.RESULT.rate_status, "\nRate\nStatus");
				tMaster.setColTitle(COLS.RESULT.strategy_id, "\n\nStrategy");
				tMaster.setColTitle(COLS.RESULT.yest_pymt, "\nPrevious\nPymt");
				tMaster.setColTitle(COLS.RESULT.yest_total_value, "Previous\nTotal\nValue");
				tMaster.setColTitle(COLS.RESULT.yest_realized_value, "Previous\nRealized\nValue");
				tMaster.setColTitle(COLS.RESULT.yest_unrealized_value, "Previous\nUnrealized\nValue");
				tMaster.setColTitle(COLS.RESULT.yest_base_total_value, "Previous\nBase Total\nValue");
				tMaster.setColTitle(COLS.RESULT.yest_base_realized_value, "Previous\nBase Realized\nValue");
				tMaster.setColTitle(COLS.RESULT.yest_base_unrealized_value, "Previous\nBase Unrealized\nValue");
				tMaster.setColTitle(COLS.RESULT.change_in_tot_pnl, "Change\nin Total\nValue");
				tMaster.setColTitle(COLS.RESULT.yest_tran_status, "Previous\nTran\nStatus");
				tMaster.setColTitle(COLS.RESULT.new_deal, "\nNew\nDeal");
				tMaster.setColTitle(COLS.RESULT.event_source_id, "\nEvent\nSource");
				tMaster.setColTitle(COLS.RESULT.param_id, "\nParam\nID");
				tMaster.setColTitle(COLS.RESULT.param_id_1, "\nParam\nID_1");
				tMaster.setColTitle(COLS.RESULT.yest_volume, "\nPrevious\nVolume");
				tMaster.setColTitle(COLS.RESULT.yest_price, "\nPrevious\nPrice");

				tMaster.setColTitle(COLS.RESULT.EXTRA.toolset, "\n\nToolset");
				tMaster.setColTitle(COLS.RESULT.EXTRA.ins_type, "\n\nIns Type");
				tMaster.setColTitle(COLS.RESULT.EXTRA.internal_portfolio, "\nInternal\nPortfolio");
				tMaster.setColTitle(COLS.RESULT.EXTRA.external_portfolio, "\nExternal\nPortfolio");
 				
				// Date Formatting
				LIB.safeSetColFormatAsDate(tMaster, COLS.RESULT.start_date);
				LIB.safeSetColFormatAsDate(tMaster, COLS.RESULT.end_date);
				LIB.safeSetColFormatAsDate(tMaster, COLS.RESULT.pymt_date);
				LIB.safeSetColFormatAsDate(tMaster, COLS.RESULT.rate_dtmn_date);
 
				// Ref Formatting 
				LIB.safeSetColFormatAsRef(tMaster, COLS.RESULT.price_band, SHM_USR_TABLES_ENUM.PRICE_BAND_TABLE);
				LIB.safeSetColFormatAsRef(tMaster, COLS.RESULT.comm_opt_exercised_flag, SHM_USR_TABLES_ENUM.NO_YES_TABLE);
				LIB.safeSetColFormatAsRef(tMaster, COLS.RESULT.cflow_type, SHM_USR_TABLES_ENUM.CFLOW_TYPE_TABLE);
				LIB.safeSetColFormatAsRef(tMaster, COLS.RESULT.broker_fee_type, SHM_USR_TABLES_ENUM.PROV_TYPE_TABLE);
				LIB.safeSetColFormatAsRef(tMaster, COLS.RESULT.currency_id, SHM_USR_TABLES_ENUM.CURRENCY_TABLE);
				LIB.safeSetColFormatAsRef(tMaster, COLS.RESULT.settlement_type, SHM_USR_TABLES_ENUM.SETTLEMENT_TYPE_TABLE);
				LIB.safeSetColFormatAsRef(tMaster, COLS.RESULT.tran_status, SHM_USR_TABLES_ENUM.TRANS_STATUS_TABLE);
				LIB.safeSetColFormatAsRef(tMaster, COLS.RESULT.rate_status, SHM_USR_TABLES_ENUM.VALUE_STATUS_TABLE);
				LIB.safeSetColFormatAsRef(tMaster, COLS.RESULT.strategy_id, SHM_USR_TABLES_ENUM.STRATEGY_LISTING_TABLE);
				LIB.safeSetColFormatAsRef(tMaster, COLS.RESULT.yest_tran_status, SHM_USR_TABLES_ENUM.TRANS_STATUS_TABLE);
				LIB.safeSetColFormatAsRef(tMaster, COLS.RESULT.new_deal, SHM_USR_TABLES_ENUM.PNL_DETAIL_TRADE_STATUS_TABLE);
				LIB.safeSetColFormatAsRef(tMaster, COLS.RESULT.event_source_id, SHM_USR_TABLES_ENUM.EVENT_SOURCE_TABLE);
				
				LIB.safeSetColFormatAsRef(tMaster, COLS.RESULT.EXTRA.toolset, SHM_USR_TABLES_ENUM.TOOLSET_ID_TABLE);
				LIB.safeSetColFormatAsRef(tMaster, COLS.RESULT.EXTRA.ins_type, SHM_USR_TABLES_ENUM.INS_TYPE_TABLE);
				LIB.safeSetColFormatAsRef(tMaster, COLS.RESULT.EXTRA.internal_portfolio, SHM_USR_TABLES_ENUM.PORTFOLIO_TABLE);
				LIB.safeSetColFormatAsRef(tMaster, COLS.RESULT.EXTRA.external_portfolio, SHM_USR_TABLES_ENUM.PORTFOLIO_TABLE);
  				
				// this is not used
				final int width = 10;
				final int volume_precision = 0;
				final int price_precision = 4;
				final int mtm_precision = 2;
				final int df_precision = 8;
				tMaster.setColFormatAsNotnl(COLS.RESULT.volume, width, volume_precision, 0);
				tMaster.setColFormatAsNotnl(COLS.RESULT.price, width, price_precision, 0);
				tMaster.setColFormatAsNotnl(COLS.RESULT.strike, width, price_precision, 0);
				tMaster.setColFormatAsNotnl(COLS.RESULT.pymt, width, mtm_precision, 0);
				tMaster.setColFormatAsNotnl(COLS.RESULT.total_value, width, mtm_precision, 0);
				tMaster.setColFormatAsNotnl(COLS.RESULT.realized_value, width, mtm_precision, 0);
				tMaster.setColFormatAsNotnl(COLS.RESULT.unrealized_value, width, mtm_precision, 0);
				tMaster.setColFormatAsNotnl(COLS.RESULT.base_total_value, width, mtm_precision, 0);
				tMaster.setColFormatAsNotnl(COLS.RESULT.base_realized_value, width, mtm_precision, 0);
				tMaster.setColFormatAsNotnl(COLS.RESULT.base_unrealized_value, width, mtm_precision, 0);
				tMaster.setColFormatAsNotnl(COLS.RESULT.df, width, df_precision, 0);
				tMaster.setColFormatAsNotnl(COLS.RESULT.yest_pymt, width, mtm_precision, 0);
				tMaster.setColFormatAsNotnl(COLS.RESULT.yest_total_value, width, mtm_precision, 0);
				tMaster.setColFormatAsNotnl(COLS.RESULT.yest_realized_value, width, mtm_precision, 0);
				tMaster.setColFormatAsNotnl(COLS.RESULT.yest_unrealized_value, width, mtm_precision, 0);
				tMaster.setColFormatAsNotnl(COLS.RESULT.yest_base_total_value, width, mtm_precision, 0);
				tMaster.setColFormatAsNotnl(COLS.RESULT.yest_base_realized_value, width, mtm_precision, 0);
				tMaster.setColFormatAsNotnl(COLS.RESULT.yest_base_unrealized_value, width, mtm_precision, 0);
				tMaster.setColFormatAsNotnl(COLS.RESULT.change_in_tot_pnl, width, mtm_precision, 0);
				tMaster.setColFormatAsNotnl(COLS.RESULT.yest_volume, width, volume_precision, 0);
				tMaster.setColFormatAsNotnl(COLS.RESULT.yest_price, width, price_precision, 0);

				tMaster.colHide(COLS.RESULT.HIDDEN.tran_num_for_double_click);
			} catch (Exception e) {
				LIB.log("format_result", e, className);
			}
		}

	} // END private static class FUNC

	public static class CREATE_TABLE {

		static void addColsToResultTable(Table tMaster, String className) {

			try {
				
 				LIB.safeAddCol(tMaster, COLS.RESULT.deal_num,  COL_TYPE_ENUM.COL_INT);
				LIB.safeAddCol(tMaster, COLS.RESULT.tran_num,  COL_TYPE_ENUM.COL_INT);
				LIB.safeAddCol(tMaster, COLS.RESULT.deal_leg,  COL_TYPE_ENUM.COL_INT);
				LIB.safeAddCol(tMaster, COLS.RESULT.deal_leg_1,  COL_TYPE_ENUM.COL_INT);
				LIB.safeAddCol(tMaster, COLS.RESULT.deal_pdc,  COL_TYPE_ENUM.COL_INT);
				LIB.safeAddCol(tMaster, COLS.RESULT.ins_seq_num,  COL_TYPE_ENUM.COL_INT);
				LIB.safeAddCol(tMaster, COLS.RESULT.ins_source_id,  COL_TYPE_ENUM.COL_INT);
				LIB.safeAddCol(tMaster, COLS.RESULT.price_band,  COL_TYPE_ENUM.COL_INT);
				LIB.safeAddCol(tMaster, COLS.RESULT.price_band_seq_num,  COL_TYPE_ENUM.COL_INT);
				LIB.safeAddCol(tMaster, COLS.RESULT.EXTRA.toolset,  COL_TYPE_ENUM.COL_INT);
				LIB.safeAddCol(tMaster, COLS.RESULT.EXTRA.ins_type,  COL_TYPE_ENUM.COL_INT);
				LIB.safeAddCol(tMaster, COLS.RESULT.comm_opt_exercised_flag,  COL_TYPE_ENUM.COL_INT);
				LIB.safeAddCol(tMaster, COLS.RESULT.cflow_type,  COL_TYPE_ENUM.COL_INT);
				LIB.safeAddCol(tMaster, COLS.RESULT.broker_fee_type,  COL_TYPE_ENUM.COL_INT);
				LIB.safeAddCol(tMaster, COLS.RESULT.start_date,  COL_TYPE_ENUM.COL_INT);
				LIB.safeAddCol(tMaster, COLS.RESULT.end_date,  COL_TYPE_ENUM.COL_INT);
				LIB.safeAddCol(tMaster, COLS.RESULT.pymt_date,  COL_TYPE_ENUM.COL_INT);
				LIB.safeAddCol(tMaster, COLS.RESULT.rate_dtmn_date,  COL_TYPE_ENUM.COL_INT);
				LIB.safeAddCol(tMaster, COLS.RESULT.currency_id,  COL_TYPE_ENUM.COL_INT);
				LIB.safeAddCol(tMaster, COLS.RESULT.settlement_type,  COL_TYPE_ENUM.COL_INT);
				LIB.safeAddCol(tMaster, COLS.RESULT.volume,  COL_TYPE_ENUM.COL_DOUBLE);
				LIB.safeAddCol(tMaster, COLS.RESULT.price,  COL_TYPE_ENUM.COL_DOUBLE);
				LIB.safeAddCol(tMaster, COLS.RESULT.strike,  COL_TYPE_ENUM.COL_DOUBLE);
				LIB.safeAddCol(tMaster, COLS.RESULT.pymt,  COL_TYPE_ENUM.COL_DOUBLE);
				LIB.safeAddCol(tMaster, COLS.RESULT.total_value,  COL_TYPE_ENUM.COL_DOUBLE);
				LIB.safeAddCol(tMaster, COLS.RESULT.realized_value,  COL_TYPE_ENUM.COL_DOUBLE);
				LIB.safeAddCol(tMaster, COLS.RESULT.unrealized_value,  COL_TYPE_ENUM.COL_DOUBLE);
				LIB.safeAddCol(tMaster, COLS.RESULT.base_total_value,  COL_TYPE_ENUM.COL_DOUBLE);
				LIB.safeAddCol(tMaster, COLS.RESULT.base_realized_value,  COL_TYPE_ENUM.COL_DOUBLE);
				LIB.safeAddCol(tMaster, COLS.RESULT.base_unrealized_value,  COL_TYPE_ENUM.COL_DOUBLE);
				LIB.safeAddCol(tMaster, COLS.RESULT.df,  COL_TYPE_ENUM.COL_DOUBLE);
				LIB.safeAddCol(tMaster, COLS.RESULT.tran_status,  COL_TYPE_ENUM.COL_INT);
				LIB.safeAddCol(tMaster, COLS.RESULT.rate_status,  COL_TYPE_ENUM.COL_INT);
				LIB.safeAddCol(tMaster, COLS.RESULT.strategy_id,  COL_TYPE_ENUM.COL_INT);
				LIB.safeAddCol(tMaster, COLS.RESULT.yest_pymt,  COL_TYPE_ENUM.COL_DOUBLE);
				LIB.safeAddCol(tMaster, COLS.RESULT.yest_total_value,  COL_TYPE_ENUM.COL_DOUBLE);
				LIB.safeAddCol(tMaster, COLS.RESULT.yest_realized_value,  COL_TYPE_ENUM.COL_DOUBLE);
				LIB.safeAddCol(tMaster, COLS.RESULT.yest_unrealized_value,  COL_TYPE_ENUM.COL_DOUBLE);
				LIB.safeAddCol(tMaster, COLS.RESULT.yest_base_total_value,  COL_TYPE_ENUM.COL_DOUBLE);
				LIB.safeAddCol(tMaster, COLS.RESULT.yest_base_realized_value,  COL_TYPE_ENUM.COL_DOUBLE);
				LIB.safeAddCol(tMaster, COLS.RESULT.yest_base_unrealized_value,  COL_TYPE_ENUM.COL_DOUBLE);
				LIB.safeAddCol(tMaster, COLS.RESULT.change_in_tot_pnl,  COL_TYPE_ENUM.COL_DOUBLE);
				LIB.safeAddCol(tMaster, COLS.RESULT.yest_tran_status,  COL_TYPE_ENUM.COL_INT);
				LIB.safeAddCol(tMaster, COLS.RESULT.new_deal,  COL_TYPE_ENUM.COL_INT);
				LIB.safeAddCol(tMaster, COLS.RESULT.event_source_id,  COL_TYPE_ENUM.COL_INT);
				LIB.safeAddCol(tMaster, COLS.RESULT.param_id,  COL_TYPE_ENUM.COL_INT);
				LIB.safeAddCol(tMaster, COLS.RESULT.param_id_1,  COL_TYPE_ENUM.COL_INT);
				LIB.safeAddCol(tMaster, COLS.RESULT.yest_volume,  COL_TYPE_ENUM.COL_DOUBLE);
				LIB.safeAddCol(tMaster, COLS.RESULT.yest_price,  COL_TYPE_ENUM.COL_DOUBLE);
				
				LIB.safeAddCol(tMaster, COLS.RESULT.EXTRA.internal_portfolio,  COL_TYPE_ENUM.COL_INT);
				LIB.safeAddCol(tMaster, COLS.RESULT.EXTRA.external_portfolio,  COL_TYPE_ENUM.COL_INT);
 
				// Though tran_num is generally an integer, for double click to work, it needs to be a DOUBLE
				LIB.safeAddCol(tMaster, COLS.RESULT.HIDDEN.tran_num_for_double_click,  COL_TYPE_ENUM.COL_DOUBLE);
				 
				FUNC.format_result(tMaster, className);

			} catch (Exception e) {
				LIB.log("addColsToResultTable", e, className);
			}
		}

	} // end CREATE_TABLE

    public static class TRANF_HELPER {

    	// this isn't used, but need to pass something in to the function
    	private final static String TRAN_INFO_FIELD_NAME = "";
    	private final static int SIDE0 = 0;

    	public static String safeGetTranfField(Transaction tran, TRANF_FIELD tranField, String className) throws OException{
        	String sReturn = "";
              try{
            	  sReturn = tran.getField(tranField.toInt());
              } catch(Exception e ){
            	  sReturn = "";
                   LIB.log("safeGetTranfField", e, className);
              }
              if (sReturn == null) {
            	  sReturn = "";
              }
              return sReturn;
    	}

    	public static String safeGetTranfField(Transaction tran, TRANF_FIELD tranField, int iSide, String className) throws OException{
        	String sReturn = "";
              try{
            	  sReturn = tran.getField(tranField.toInt(), iSide, TRAN_INFO_FIELD_NAME);
              } catch(Exception e ){
            	  sReturn = "";
                  LIB.log("safeGetTranfField with Side", e, className);
              }
              if (sReturn == null) {
            	  sReturn = "";
              }
              return sReturn;
    	}

    	public static String safeGetTranfField(Transaction tran, TRANF_FIELD tranField, int iSide, int iSeqNum2, String className) throws OException{
        	String sReturn = "";
              try{
            	  sReturn = tran.getField(tranField.toInt(), iSide, TRAN_INFO_FIELD_NAME, iSeqNum2);
              } catch(Exception e ){
            	  sReturn = "";
                  LIB.log("safeGetTranfField with SeqNum2", e, className);
              }
              if (sReturn == null) {
            	  sReturn = "";
              }
              return sReturn;
    	}

    	public static double safeGetTranfFieldDouble(Transaction tran, TRANF_FIELD tranField, int iSide, int iSeqNum2, String className) throws OException{
    		double dReturn = 0.0;
              try{
            	  dReturn = tran.getFieldDouble(tranField.toInt(), iSide, TRAN_INFO_FIELD_NAME, iSeqNum2);
              } catch(Exception e ){
                  LIB.log("safeGetTranfFieldDouble with SeqNum2", e, className);
              }
              return dReturn;
    	}

    	public static double safeGetTranfFieldDouble(Transaction tran, TRANF_FIELD tranField, int iSide, String className) throws OException{
    		double dReturn = 0.0;
              try{
            	  dReturn = tran.getFieldDouble(tranField.toInt(), iSide, TRAN_INFO_FIELD_NAME);
              } catch(Exception e ){
                  LIB.log("safeGetTranfFieldDouble with Side", e, className);
              }
              return dReturn;
    	}

    	public static double safeGetTranfFieldDouble(Transaction tran, TRANF_FIELD tranField, String className) throws OException{
    		double dReturn = 0.0;
              try{
            	  dReturn = tran.getFieldDouble(tranField.toInt(), TRANF_HELPER.SIDE0, TRAN_INFO_FIELD_NAME);
              } catch(Exception e ){
                  LIB.log("safeGetTranfFieldDouble", e, className);
              }
              return dReturn;
    	}

    	public static int safeGetTranfFieldInt(Transaction tran, TRANF_FIELD tranField, int iSide, int iSeqNum2, String className) throws OException{
        	int iReturn = 0;
              try{
            	  iReturn = tran.getFieldInt(tranField.toInt(), iSide, TRAN_INFO_FIELD_NAME, iSeqNum2);
              } catch(Exception e ){
                  LIB.log("safeGetTranfFieldInt with SeqNum2", e, className);
              }
              return iReturn;
    	}

    	public static int safeGetTranfFieldInt(Transaction tran, TRANF_FIELD tranField, int iSide, String className) throws OException{
        	int iReturn = 0;
              try{
            	  iReturn = tran.getFieldInt(tranField.toInt(), iSide, TRAN_INFO_FIELD_NAME);
              } catch(Exception e ){
                  LIB.log("safeGetTranfFieldInt with Side", e, className);
              }
              return iReturn;
    	}

    	public static int safeGetTranfFieldInt(Transaction tran, TRANF_FIELD tranField, String className) throws OException{
        	int iReturn = 0;
              try{
            	  iReturn = tran.getFieldInt(tranField.toInt(), TRANF_HELPER.SIDE0, TRAN_INFO_FIELD_NAME);
              } catch(Exception e ){
                  LIB.log("safeGetTranfFieldInt", e, className);
              }
              return iReturn;
    	}

     	public static String safeGetTranfField(Transaction tran, TRANF_FIELD tranField, int iSide, int iSeqNum2, int iSeqNum3, String className) throws OException{
        	String sReturn = "";
              try{
            	  sReturn = tran.getField(tranField.toInt(), iSide, TRAN_INFO_FIELD_NAME, iSeqNum2, iSeqNum3);
              } catch(Exception e ){
            	  sReturn = "";
                  LIB.log("safeGetTranfField with SeqNum2/3", e, className);
              }
              if (sReturn == null) {
            	  sReturn = "";
              }
              return sReturn;
     	}

    	public static int safeGetTranfFieldInt(Transaction tran, TRANF_FIELD tranField, int iSide, int iSeqNum2, int iSeqNum3, String className) throws OException{
        	int iReturn = 0;
              try{
            	  iReturn = tran.getFieldInt(tranField.toInt(), iSide, TRAN_INFO_FIELD_NAME, iSeqNum2, iSeqNum3);
              } catch(Exception e ){
                   LIB.log("safeGetTranfFieldInt with SeqNum2/3", e, className);
              }
              return iReturn;
    	}

    	public static void safeSetTranfField(Transaction tran, TRANF_FIELD tranField, String sValueToSet, String className) throws OException{
            try{
            	int SIDE = 0;
            	tran.setField(tranField.toInt(), SIDE, TRAN_INFO_FIELD_NAME, sValueToSet);
            } catch(Exception e ){
                LIB.log("safeSetTranfField with Side", e, className);
            }
    	}

    	public static void safeSetTranfField(Transaction tran, TRANF_FIELD tranField, int iSide, String sValueToSet, String className) throws OException{
            try{
          	  tran.setField(tranField.toInt(), iSide, TRAN_INFO_FIELD_NAME, sValueToSet);
            } catch(Exception e ){
                LIB.log("safeSetTranfField with Side", e, className);
            }
    	}

    	public static void safeSetTranfField(Transaction tran, TRANF_FIELD tranField, int iSide, int iSeqNum2, String sValueToSet, String className) throws OException{
              try{
            	  tran.setField(tranField.toInt(), iSide, TRAN_INFO_FIELD_NAME, sValueToSet, iSeqNum2);
              } catch(Exception e ){
                  LIB.log("safeSetTranfField with Side and SeqNum2", e, className);
              }
    	}

    	public static void safeSetTranfInfoField(Transaction tran, String sInfoFieldName, String sValueToSet, String className) throws OException{
              try{
            	  tran.setField(TRANF_FIELD.TRANF_TRAN_INFO.toInt(), SIDE0, sInfoFieldName, sValueToSet);
              } catch(Exception e ){
                  LIB.log("safeSetTranfInfoField", e, className);
              }
    	}

    	public static void safeSetTranfInfoFieldNoErrorMessage(Transaction tran, String sInfoFieldName, String sValueToSet, String className) throws OException{
              try{
            	  tran.setField(TRANF_FIELD.TRANF_TRAN_INFO.toInt(), SIDE0, sInfoFieldName, sValueToSet);
              } catch(Exception e ){
            	  // Don't log this
              }
    	}

     	public static String safeGetTranfInfoField(Transaction tran, String sInfoFieldName, String className) throws OException{
        	String sReturn = "";
              try{
            	  sReturn = tran.getField(TRANF_FIELD.TRANF_TRAN_INFO.toInt(), SIDE0, sInfoFieldName);
              } catch(Exception e ){
            	  sReturn = "";
                  LIB.log("safeGetTranfInfoField", e, className);
              }
              if (sReturn == null) {
            	  sReturn = "";
              }
              return sReturn;
     	}

		public static void safeSetTranfFieldNoError(Transaction tran, TRANF_FIELD tranField, int iSide, int iSeqNum2, String sValueToSet, String className) throws OException {
			try {
				tran.setField(tranField.toInt(), iSide, TRAN_INFO_FIELD_NAME, sValueToSet, iSeqNum2);
			} catch (Exception e) {
				// Don't Log this
			}
		}

		public static int getNumberOfDealCashFlowsOnTranForSide1(Transaction tran, String className) throws OException {
			int iReturn = 0;
			try {
				iReturn = tran.getNumRows(1, TRANF_GROUP.TRANF_GROUP_CFLOW.toInt());

			} catch (Exception e) {
				LIB.log("getNumberOfDealCashFlowsOnTranForSide1", e, className);
			}
			return iReturn;
		}

} // end class  private static class TRANF_HELPER


	public static class COLS {
  		
		public static class RESULT {
 
			public static final String deal_num = "deal_num"; //COL_INT
			public static final String tran_num = "tran_num"; //COL_INT
			public static final String deal_leg = "deal_leg"; //COL_INT
			public static final String deal_leg_1 = "deal_leg_1"; //COL_INT
			public static final String deal_pdc = "deal_pdc"; //COL_INT
			public static final String ins_seq_num = "ins_seq_num"; //COL_INT
			public static final String ins_source_id = "ins_source_id"; //COL_INT
			public static final String price_band = "price_band"; //COL_INT
			public static final String price_band_seq_num = "price_band_seq_num"; //COL_INT
			public static final String comm_opt_exercised_flag = "comm_opt_exercised_flag"; //COL_INT
			public static final String cflow_type = "cflow_type"; //COL_INT
			public static final String broker_fee_type = "broker_fee_type"; //COL_INT
			public static final String start_date = "start_date"; //COL_INT
			public static final String end_date = "end_date"; //COL_INT
			public static final String pymt_date = "pymt_date"; //COL_INT
			public static final String rate_dtmn_date = "rate_dtmn_date"; //COL_INT
			public static final String currency_id = "currency_id"; //COL_INT
			public static final String settlement_type = "settlement_type"; //COL_INT
			public static final String volume = "volume"; //COL_DOUBLE
			public static final String price = "price"; //COL_DOUBLE
			public static final String strike = "strike"; //COL_DOUBLE
			public static final String pymt = "pymt"; //COL_DOUBLE
			public static final String total_value = "total_value"; //COL_DOUBLE
			public static final String realized_value = "realized_value"; //COL_DOUBLE
			public static final String unrealized_value = "unrealized_value"; //COL_DOUBLE
			public static final String base_total_value = "base_total_value"; //COL_DOUBLE
			public static final String base_realized_value = "base_realized_value"; //COL_DOUBLE
			public static final String base_unrealized_value = "base_unrealized_value"; //COL_DOUBLE
			public static final String df = "df"; //COL_DOUBLE
			public static final String tran_status = "tran_status"; //COL_INT
			public static final String rate_status = "rate_status"; //COL_INT
			public static final String strategy_id = "strategy_id"; //COL_INT
			public static final String yest_pymt = "yest_pymt"; //COL_DOUBLE
			public static final String yest_total_value = "yest_total_value"; //COL_DOUBLE
			public static final String yest_realized_value = "yest_realized_value"; //COL_DOUBLE
			public static final String yest_unrealized_value = "yest_unrealized_value"; //COL_DOUBLE
			public static final String yest_base_total_value = "yest_base_total_value"; //COL_DOUBLE
			public static final String yest_base_realized_value = "yest_base_realized_value"; //COL_DOUBLE
			public static final String yest_base_unrealized_value = "yest_base_unrealized_value"; //COL_DOUBLE
			public static final String change_in_tot_pnl = "change_in_tot_pnl"; //COL_DOUBLE
			public static final String yest_tran_status = "yest_tran_status"; //COL_INT
			public static final String new_deal = "new_deal"; //COL_INT
			public static final String event_source_id = "event_source_id"; //COL_INT
			public static final String param_id = "param_id"; //COL_INT
			public static final String param_id_1 = "param_id_1"; //COL_INT
			public static final String yest_volume = "yest_volume"; //COL_DOUBLE
			public static final String yest_price = "yest_price"; //COL_DOUBLE

			// these are not in the original PnL Detail
			public static class EXTRA {
				public static final String toolset = "toolset"; //COL_INT
				public static final String ins_type = "ins_type"; //COL_INT
				public static final String internal_portfolio = "internal_portfolio"; //COL_INT
				public static final String external_portfolio = "external_portfolio"; //COL_INT
			}
			
			public static class HIDDEN {
				// yes, this has a weird name
				public static final String tran_num_for_double_click = "40"; // COL_DOUBLE
			}
		}

		public static class ARGT {

			public static final String result_type = "result_type"; // COL_INT
			public static final String operation = "operation"; // COL_INT
			public static final String sim_results = "sim_results"; // COL_TABLE
			public static final String transactions = "transactions"; // COL_TABLE

			// result_type CHECK_MARK COL_INT FMT_NONE result_type
			// operation CHECK_MARK COL_INT FMT_NONE operation
			// sim_def CHECK_MARK COL_TABLE FMT_NONE sim_def
			// scen_id CHECK_MARK COL_INT FMT_NONE scen_id
			// sim_results CHECK_MARK COL_TABLE FMT_NONE sim_results
			// prior_sim_results CHECK_MARK COL_TABLE FMT_NONE prior_sim_results
			// transactions CHECK_MARK COL_TABLE FMT_NONE transactions
			// dw_user_table_name CHECK_MARK COL_STRING FMT_NONE
			// dw_user_table_name
			// error_msg CHECK_MARK COL_STRING FMT_NONE error_msg
			// complete_prior_sim_results CHECK_MARK COL_TABLE FMT_NONE
			// complete_prior_sim_results
			// split_method CHECK_MARK COL_INT FMT_NONE split_method
			// master_results CHECK_MARK COL_TABLE FMT_NONE master_results
			// current_results CHECK_MARK COL_TABLE FMT_NONE current_results
			// prior_sim_results_run_date CHECK_MARK COL_INT FMT_NONE
			// prior_sim_results_run_date
			// is_master CHECK_MARK COL_INT FMT_NONE is_master
			// master_query_id CHECK_MARK COL_INT FMT_NONE master_query_id
			// is_distributed CHECK_MARK COL_INT FMT_NONE is_distributed
			// portfolio CHECK_MARK COL_INT FMT_NONE portfolio
		}

		public static class TRANSACTIONS {
			public static final String deal_num = "deal_num"; // COL_INT
			public static final String tran_num = "tran_num"; // COL_INT
			public static final String tran_ptr = "tran_ptr"; // COL_PTR
		}
	}
	
	public static class CURRENCY {
		static final int XAU = 52;
		static final int XAG = 53;
		static final int XPD = 54;
		static final int XPT = 55;
		
		static final int XRH = 61;
		static final int XRU = 63;
		static final int XRI = 62;
	}
	

	public static class SPOT_INDEX_NY_CURVE {
		static final String XAU = "SPOT_XAU_NY";
		static final String XAG = "SPOT_XAG_NY";
		static final String XPD = "SPOT_XPD_NY";
		static final String XPT = "SPOT_XPT_NY";
		
		static final String XRH = "SPOT_XRH_NY";
		static final String XRU = "SPOT_XRU_NY";
		static final String XRI = "SPOT_XRI_NY";
	}

	public static class CASH_INDEX {
		static final String XAU = "PX_XAU.USD";
		static final String XAG = "PX_XAG.USD";
		static final String XPD = "PX_XPD.USD";
		static final String XPT = "PX_XPT.USD";
		
		static final String XRH = "PX_XRH.USD";
		static final String XRU = "PX_XRU.USD";
		static final String XRI = "PX_XRI.USD";
	}


	public static class CASH_INDEX_NY_CURVE {
		static final String XAU = "PX_XAU_NY.USD";
		static final String XAG = "PX_XAG_NY.USD";
		static final String XPD = "PX_XPD_NY.USD";
		static final String XPT = "PX_XPT_NY.USD";

		// these have no NY Curve as of Jan 2023, so reuse the ones there
		static final String XRH = "PX_XRH.USD";
		static final String XRU = "PX_XRU.USD";
		static final String XRI = "PX_XRI.USD";
	}
 
 
public static class CONST_VALUES {
	public static final int VALUE_OF_TRUE = 1;
	public static final int VALUE_OF_FALSE = 0;

	public static final int ROW_TO_GET_OR_SET = 1;
  
	// OpenLink has an enum for this, but don't bother to use it for no particular reason, i.e., TRAN_RESULTS_ROW, etc.
	public final static int TRAN_RESULTS_ROW = 1;
	public final static int GEN_RESULTS_ROW = 4;
}

  
public static class LIB {

	public static final String VERSION_NUMBER = "V1.006 (01Jun2023)";

	public static void select(Table tDestination, Table tSourceTable, String sWhat, String sWhere, String className)
			throws OException {
		LIB.select(tDestination, tSourceTable, sWhat, sWhere, false, className);
	}

	public static void select(Table tDestination, Table tSourceTable, String sWhat, String sWhere,
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

	public static void logForDebug(String sMessage, boolean DEBUG_FLAG, String className) {
		try {

			if (DEBUG_FLAG == true) {
				double dMemSize = SystemUtil.memorySizeDouble();
				double tMemSizeMegs = dMemSize / 1024 / 1024 / 1024;
				String sMemSize = Str.doubleToStr(tMemSizeMegs, 2) + " gigs";

				OConsole.oprint("\n************************************" + className + ":" + LIB.VERSION_NUMBER
						+ ":" + Util.timeGetServerTimeHMS() + ":" + sMemSize + ": " + sMessage);
			}
		} catch (Exception e) {
			// don't log this
		}
	}

	public static void log(String sMessage, String className) {
		try {

			double dMemSize = SystemUtil.memorySizeDouble();
			double tMemSizeMegs = dMemSize / 1024 / 1024;
			String sMemSize = Str.doubleToStr(tMemSizeMegs, 2) + " megs";

			OConsole.oprint("\n" + className + ":" + LIB.VERSION_NUMBER
					+ ":" + Util.timeGetServerTimeHMS() + ":" + sMemSize + ": " + sMessage);
		} catch (Exception e) {
			// don't log this
		}
	}

	public static void log(String sMessage, Exception e, String className) {
		try {
			LIB.log("ERROR: " + sMessage + ":" + e.getLocalizedMessage(), className);
		} catch (Exception e1) {
			// don't log this
		}
	}

	public static Table destroyTable(Table tDestroy) {
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
