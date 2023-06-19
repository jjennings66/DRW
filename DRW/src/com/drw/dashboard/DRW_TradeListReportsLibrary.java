package com.drw.dashboard;
           
import com.olf.openjvs.Ask;
import com.olf.openjvs.DBUserTable;
import com.olf.openjvs.DBaseTable;
import com.olf.openjvs.Index;
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
import com.olf.openjvs.enums.TOOLSET_ENUM; 

/* 
Name:  DRW_TradeListReportsLibrary.java

This is a function library for sub panels for the DRW Trade Listing Reports

Revision History 
23-Apr-2023 Brian New script
   
*/

@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_GENERIC)
@PluginType(SCRIPT_TYPE_ENUM.MAIN_SCRIPT)
public class DRW_TradeListReportsLibrary {
  
	// TODO
	// set to false for production
	public static boolean bDebugLogging = false;

	// set to false for now
	public static boolean bGetTradesForDebugData = false;
 	
	public static class CREATE_TABLE {     
	
		public static Table createRawMasterReportDataTableAndOneRow(String className) throws OException{
	     
			Table tReturn = Util.NULL_TABLE;
			try{
				tReturn = Table.tableNew();

				LIB.safeAddCol(tReturn, COLS.REPORT.RAW_BY_UNIQUE_KEY_REPORT_DATA.TOOLSET.COM_SWAP_POSITION, COL_TYPE_ENUM.COL_TABLE);
				LIB.safeAddCol(tReturn, COLS.REPORT.RAW_BY_UNIQUE_KEY_REPORT_DATA.TOOLSET.COMMODITY_POSITION, COL_TYPE_ENUM.COL_TABLE);
 
				// Currency
//				LIB.safeAddCol(tReturn, COLS.REPORT.RAW_BY_UNIQUE_KEY_REPORT_DATA.SETTING.CURRENCY_LIST, COL_TYPE_ENUM.COL_TABLE); 
//				LIB.safeAddCol(tReturn, COLS.REPORT.RAW_BY_UNIQUE_KEY_REPORT_DATA.SETTING.ALL_CURRENCIES_FLAG, COL_TYPE_ENUM.COL_INT); 
//				LIB.safeAddCol(tReturn, COLS.REPORT.RAW_BY_UNIQUE_KEY_REPORT_DATA.SETTING.NUM_CURRENCIES, COL_TYPE_ENUM.COL_INT); 
//
//				// Portfolios
//				LIB.safeAddCol(tReturn, COLS.REPORT.RAW_BY_UNIQUE_KEY_REPORT_DATA.SETTING.PORTFOLIO_LIST, COL_TYPE_ENUM.COL_TABLE); 
//				LIB.safeAddCol(tReturn, COLS.REPORT.RAW_BY_UNIQUE_KEY_REPORT_DATA.SETTING.ALL_PORTFOLIOS_FLAG, COL_TYPE_ENUM.COL_INT); 
//				LIB.safeAddCol(tReturn, COLS.REPORT.RAW_BY_UNIQUE_KEY_REPORT_DATA.SETTING.NUM_PORTFOLIOS, COL_TYPE_ENUM.COL_INT); 
//
//				// Ins Types
//				LIB.safeAddCol(tReturn, COLS.REPORT.RAW_BY_UNIQUE_KEY_REPORT_DATA.SETTING.INS_TYPE_LIST, COL_TYPE_ENUM.COL_TABLE); 
//				LIB.safeAddCol(tReturn, COLS.REPORT.RAW_BY_UNIQUE_KEY_REPORT_DATA.SETTING.ALL_INS_TYPES_FLAG, COL_TYPE_ENUM.COL_INT); 
//				LIB.safeAddCol(tReturn, COLS.REPORT.RAW_BY_UNIQUE_KEY_REPORT_DATA.SETTING.NUM_INS_TYPES, COL_TYPE_ENUM.COL_INT); 
  
			//	LIB.safeAddCol(tReturn, COLS.REPORT.RAW_BY_UNIQUE_KEY_REPORT_DATA.SETTING.CURRENT_DATA_SOURCE, COL_TYPE_ENUM.COL_STRING);  

				LIB.safeAddCol(tReturn, COLS.REPORT.RAW_BY_UNIQUE_KEY_REPORT_DATA.SETTING.SHOW_ALL_COLUMNS_FLAG, COL_TYPE_ENUM.COL_INT);  
	            		 
				LIB.safeAddCol(tReturn, COLS.REPORT.RAW_BY_UNIQUE_KEY_REPORT_DATA.SETTING.RUN_NUMBER, COL_TYPE_ENUM.COL_INT); 
  
				tReturn.addRow();

				LIB.safeSetColFormatAsRef(tReturn, COLS.REPORT.RAW_BY_UNIQUE_KEY_REPORT_DATA.SETTING.SHOW_ALL_COLUMNS_FLAG, SHM_USR_TABLES_ENUM.NO_YES_TABLE);
	            	
			}catch(Exception e ){
				LIB.log("createRawMasterReportDataTableAndOneRow", e, className);
			}
			return tReturn; 
		}    
	        	
//		public static Table createCurrentDataTable(String className) throws OException{
//	       	     
//			Table tReturn = Util.NULL_TABLE;
//			try{
//				tReturn = Table.tableNew();
//				
//				LIB.safeAddCol(tReturn, COLS.CURRENT_DATA.deal_num, COL_TYPE_ENUM.COL_INT); 
//				LIB.safeAddCol(tReturn, COLS.CURRENT_DATA.toolset, COL_TYPE_ENUM.COL_INT); 
//				LIB.safeAddCol(tReturn, COLS.CURRENT_DATA.ins_type, COL_TYPE_ENUM.COL_INT); 
//				LIB.safeAddCol(tReturn, COLS.CURRENT_DATA.portfolio, COL_TYPE_ENUM.COL_INT); 
//				LIB.safeAddCol(tReturn, COLS.CURRENT_DATA.account_id, COL_TYPE_ENUM.COL_INT); 
//				LIB.safeAddCol(tReturn, COLS.CURRENT_DATA.currency, COL_TYPE_ENUM.COL_INT); 
//				LIB.safeAddCol(tReturn, COLS.CURRENT_DATA.volume, COL_TYPE_ENUM.COL_DOUBLE); 
//				
//				LIB.safeSetColFormatAsRef(tReturn, COLS.CURRENT_DATA.portfolio, SHM_USR_TABLES_ENUM.PORTFOLIO_TABLE);
//				LIB.safeSetColFormatAsRef(tReturn, COLS.CURRENT_DATA.account_id, SHM_USR_TABLES_ENUM.ACCOUNT_TABLE);
//				LIB.safeSetColFormatAsRef(tReturn, COLS.CURRENT_DATA.toolset, SHM_USR_TABLES_ENUM.TOOLSETS_TABLE);
//				LIB.safeSetColFormatAsRef(tReturn, COLS.CURRENT_DATA.ins_type, SHM_USR_TABLES_ENUM.INS_TYPE_TABLE);
//				LIB.safeSetColFormatAsRef(tReturn, COLS.CURRENT_DATA.currency, SHM_USR_TABLES_ENUM.CURRENCY_TABLE);
//				
//			}catch(Exception e ){
//				LIB.log("createCurrentDataTable", e, className);
//				}
//			return tReturn; 
//		}        
			
	} // end class CREATE_TABLE
 	 
    public static class REPORT {     
    	  
        public static class GET_RAW_DATA_FOR_REPORTS {     
        	
            public static class HELPER {     

                public static class LIVE_DATA {     
 
 

public static class TOOLSET {     
	 
	public static class COM_SWAP {  

                			public static Table createAbTranTable(String className) throws OException{
                				
                    			Table tReturn = Util.NULL_TABLE; 
                    		    try{
                    		    	// This *must* match the SQL 
                    		    	
                    		    	tReturn = Table.tableNew();
                    		    	LIB.safeAddCol(tReturn, COLS.COM_SWAP_DATA.deal_num, COL_TYPE_ENUM.COL_INT);
                    		    	LIB.safeAddCol(tReturn, COLS.COM_SWAP_DATA.tran_num, COL_TYPE_ENUM.COL_INT);
                    		    	LIB.safeAddCol(tReturn, COLS.COM_SWAP_DATA.ins_num, COL_TYPE_ENUM.COL_INT);
                    		    	LIB.safeAddCol(tReturn, COLS.COM_SWAP_DATA.ins_type, COL_TYPE_ENUM.COL_INT);
                    		    	LIB.safeAddCol(tReturn, COLS.COM_SWAP_DATA.buy_sell, COL_TYPE_ENUM.COL_INT);
                    		    	LIB.safeAddCol(tReturn, COLS.COM_SWAP_DATA.position, COL_TYPE_ENUM.COL_DOUBLE); 
                    		    	LIB.safeAddCol(tReturn, COLS.COM_SWAP_DATA.external_bunit, COL_TYPE_ENUM.COL_INT); 
                    		    	LIB.safeAddCol(tReturn, COLS.COM_SWAP_DATA.portfolio, COL_TYPE_ENUM.COL_INT); 
                    		    	LIB.safeAddCol(tReturn, COLS.COM_SWAP_DATA.external_portfolio, COL_TYPE_ENUM.COL_INT); 
                    		    	LIB.safeAddCol(tReturn, COLS.COM_SWAP_DATA.start_date, COL_TYPE_ENUM.COL_INT); 
                    		    	LIB.safeAddCol(tReturn, COLS.COM_SWAP_DATA.end_date, COL_TYPE_ENUM.COL_INT); 
                    		    	LIB.safeAddCol(tReturn, COLS.COM_SWAP_DATA.offset_tran_type, COL_TYPE_ENUM.COL_INT); 
                    		    	LIB.safeAddCol(tReturn, COLS.COM_SWAP_DATA.offset_tran_num, COL_TYPE_ENUM.COL_INT); 
                           		 
                    		    	tReturn.defaultFormat();
                    		    	  
                    		    	
                    		    } catch(Exception e ){               
                    		    	LIB.log("createAbTranTable", className);
                    		    } 
                    		     
                    		    return tReturn;
                    		}
                			
                			public static Table createComSwapTable(String className) throws OException{
                				
                    			Table tReturn = Util.NULL_TABLE; 
                    		    try{
                    		    	tReturn = Table.tableNew();
                    		    	LIB.safeAddCol(tReturn, COLS.COM_SWAP_DATA.deal_num, COL_TYPE_ENUM.COL_INT);
                    		    	LIB.safeAddCol(tReturn, COLS.COM_SWAP_DATA.tran_num, COL_TYPE_ENUM.COL_INT);
                    		    	LIB.safeAddCol(tReturn, COLS.COM_SWAP_DATA.ins_num, COL_TYPE_ENUM.COL_INT);
                    		    	LIB.safeAddCol(tReturn, COLS.COM_SWAP_DATA.ins_type, COL_TYPE_ENUM.COL_INT);
                    		    	LIB.safeAddCol(tReturn, COLS.COM_SWAP_DATA.buy_sell, COL_TYPE_ENUM.COL_INT);
                    		    	LIB.safeAddCol(tReturn, COLS.COM_SWAP_DATA.position, COL_TYPE_ENUM.COL_DOUBLE); 
                    		    	LIB.safeAddCol(tReturn, COLS.COM_SWAP_DATA.external_bunit, COL_TYPE_ENUM.COL_INT); 
                    		    	LIB.safeAddCol(tReturn, COLS.COM_SWAP_DATA.portfolio, COL_TYPE_ENUM.COL_INT); 
                    		    	LIB.safeAddCol(tReturn, COLS.COM_SWAP_DATA.external_portfolio, COL_TYPE_ENUM.COL_INT); 
                    		    	LIB.safeAddCol(tReturn, COLS.COM_SWAP_DATA.start_date, COL_TYPE_ENUM.COL_INT); 
                    		    	LIB.safeAddCol(tReturn, COLS.COM_SWAP_DATA.end_date, COL_TYPE_ENUM.COL_INT); 
                    		    	LIB.safeAddCol(tReturn, COLS.COM_SWAP_DATA.offset_tran_type, COL_TYPE_ENUM.COL_INT); 
                    		    	LIB.safeAddCol(tReturn, COLS.COM_SWAP_DATA.offset_tran_num, COL_TYPE_ENUM.COL_INT); 
                           		 
                    		    	// default formatting for the columns we added so far
                    		    	tReturn.defaultFormat();
                    		    	
                    		    	// And now add the rest of the columns
                    		     	LIB.safeAddCol(tReturn, COLS.COM_SWAP_DATA.EXTRA.pay_rec, COL_TYPE_ENUM.COL_INT);
                    		    	LIB.safeAddCol(tReturn, COLS.COM_SWAP_DATA.EXTRA.proj_index, COL_TYPE_ENUM.COL_INT);
                    		    	LIB.safeAddCol(tReturn, COLS.COM_SWAP_DATA.EXTRA.idx_subgroup, COL_TYPE_ENUM.COL_INT);
                    		    	LIB.safeAddCol(tReturn, COLS.COM_SWAP_DATA.EXTRA.vintage, COL_TYPE_ENUM.COL_STRING);
                    		    	LIB.safeAddCol(tReturn, COLS.COM_SWAP_DATA.EXTRA.contract_month, COL_TYPE_ENUM.COL_STRING);
                    		    	LIB.safeAddCol(tReturn, COLS.COM_SWAP_DATA.EXTRA.year, COL_TYPE_ENUM.COL_INT);
                    		    	

                    		    	LIB.safeAddCol(tReturn, COLS.COM_SWAP_DATA.PROFILE.profile_seq_num, COL_TYPE_ENUM.COL_INT); 
                    		    	LIB.safeAddCol(tReturn, COLS.COM_SWAP_DATA.PROFILE.profile_notnl, COL_TYPE_ENUM.COL_DOUBLE); 
                    		    	LIB.safeAddCol(tReturn, COLS.COM_SWAP_DATA.PROFILE.profile_start_date, COL_TYPE_ENUM.COL_INT); 
                    		    	LIB.safeAddCol(tReturn, COLS.COM_SWAP_DATA.PROFILE.profile_end_date, COL_TYPE_ENUM.COL_INT); 
                    		    	LIB.safeAddCol(tReturn, COLS.COM_SWAP_DATA.PROFILE.rate_dtmn_date, COL_TYPE_ENUM.COL_INT); 
                    		    	LIB.safeAddCol(tReturn, COLS.COM_SWAP_DATA.PROFILE.rate_status, COL_TYPE_ENUM.COL_INT); 
                    		    	LIB.safeAddCol(tReturn, COLS.COM_SWAP_DATA.PROFILE.pymt_date, COL_TYPE_ENUM.COL_INT); 
 
                    		    	// this is position_in_mt
                    		    	LIB.safeAddCol(tReturn, COLS.COM_SWAP_DATA.EXTRA.position_in_mmbtu, COL_TYPE_ENUM.COL_DOUBLE); 
                    		    	LIB.safeAddCol(tReturn, COLS.COM_SWAP_DATA.EXTRA.unit, COL_TYPE_ENUM.COL_INT);
                    		    	LIB.safeAddCol(tReturn, COLS.COM_SWAP_DATA.EXTRA.fixed_price, COL_TYPE_ENUM.COL_DOUBLE); 
                    		    	LIB.safeAddCol(tReturn, COLS.COM_SWAP_DATA.EXTRA.is_exchange_flag, COL_TYPE_ENUM.COL_INT);
                    		    	LIB.safeAddCol(tReturn, COLS.COM_SWAP_DATA.EXTRA.is_generic_flag, COL_TYPE_ENUM.COL_INT);
                     		    	
                    		    	// this is cosmetic
                      		    	LIB.safeSetColFormatAsRef(tReturn, COLS.COM_SWAP_DATA.EXTRA.proj_index, SHM_USR_TABLES_ENUM.INDEX_TABLE);
                      		    	LIB.safeSetColFormatAsRef(tReturn, COLS.COM_SWAP_DATA.EXTRA.pay_rec, SHM_USR_TABLES_ENUM.REC_PAY_TABLE);
                      		     	LIB.safeSetColFormatAsRef(tReturn, COLS.COM_SWAP_DATA.EXTRA.idx_subgroup, SHM_USR_TABLES_ENUM.IDX_SUBGROUP_TABLE);
                      		     	LIB.safeSetColFormatAsRef(tReturn, COLS.COM_SWAP_DATA.EXTRA.unit, SHM_USR_TABLES_ENUM.IDX_UNIT_TABLE);
                      		     	LIB.safeSetColFormatAsRef(tReturn, COLS.COM_SWAP_DATA.PROFILE.rate_status, SHM_USR_TABLES_ENUM.VALUE_STATUS_TABLE);
                      		     	LIB.safeSetColFormatAsRef(tReturn, COLS.COM_SWAP_DATA.EXTRA.is_exchange_flag, SHM_USR_TABLES_ENUM.NO_YES_TABLE);
                      		     	LIB.safeSetColFormatAsRef(tReturn, COLS.COM_SWAP_DATA.EXTRA.is_generic_flag, SHM_USR_TABLES_ENUM.NO_YES_TABLE);

                      		    	// date formatting
                      		    	LIB.safeSetColFormatAsDate(tReturn, COLS.COM_SWAP_DATA.start_date);
                      		    	LIB.safeSetColFormatAsDate(tReturn, COLS.COM_SWAP_DATA.end_date);
                      		    	
                      		    	// Profile Date formatting
                      		    	LIB.safeSetColFormatAsDate(tReturn, COLS.COM_SWAP_DATA.PROFILE.profile_start_date);
                      		    	LIB.safeSetColFormatAsDate(tReturn, COLS.COM_SWAP_DATA.PROFILE.profile_end_date);
                      		    	LIB.safeSetColFormatAsDate(tReturn, COLS.COM_SWAP_DATA.PROFILE.rate_dtmn_date);
                      		    	LIB.safeSetColFormatAsDate(tReturn, COLS.COM_SWAP_DATA.PROFILE.pymt_date);
                      		    	
                      		    	// Set Col Titles
                      		    	boolean bSetColTitles = false;
                      		    	if (bSetColTitles == true) {
                          		    	tReturn.setColTitle(COLS.COM_SWAP_DATA.deal_num, "Deal Num"); 
                          		    	tReturn.setColTitle(COLS.COM_SWAP_DATA.tran_num, "Tran Num"); 
                          		    	tReturn.setColTitle(COLS.COM_SWAP_DATA.ins_num, "Ins Num"); 

                          		    	tReturn.setColTitle(COLS.COM_SWAP_DATA.ins_type, "Ins Type");
                          		    	tReturn.setColTitle(COLS.COM_SWAP_DATA.buy_sell, "Buy/Sell");
                          		    	tReturn.setColTitle(COLS.COM_SWAP_DATA.position, "Trade Level\nPosition");

                          		    	tReturn.setColTitle(COLS.COM_SWAP_DATA.external_bunit, "External\nBunit");

                          		    	tReturn.setColTitle(COLS.COM_SWAP_DATA.portfolio, "Portfolio");   
                          		    	tReturn.setColTitle(COLS.COM_SWAP_DATA.external_portfolio, "External\nPortfolio");   

                          		    	tReturn.setColTitle(COLS.COM_SWAP_DATA.offset_tran_type, "Offset\nTran Type");   
                          		    	tReturn.setColTitle(COLS.COM_SWAP_DATA.offset_tran_num, "Offset\nTran Num");   

                          		    	tReturn.setColTitle(COLS.COM_SWAP_DATA.EXTRA.pay_rec, "Pay\nRec");
                          		    	tReturn.setColTitle(COLS.COM_SWAP_DATA.EXTRA.proj_index, "Proj\nIndex");
                           		    	tReturn.setColTitle(COLS.COM_SWAP_DATA.PROFILE.profile_seq_num, "Profile\nSeq Num");
                          		    	tReturn.setColTitle(COLS.COM_SWAP_DATA.PROFILE.profile_start_date, "Profile\nStart Date");
                          		    	tReturn.setColTitle(COLS.COM_SWAP_DATA.PROFILE.profile_end_date, "Profile\nEnd Date");
                          		    	tReturn.setColTitle(COLS.COM_SWAP_DATA.PROFILE.profile_notnl, "Profile\nNotional");
                          		    	tReturn.setColTitle(COLS.COM_SWAP_DATA.PROFILE.pymt_date, "Payment\nDate");
                          		    	tReturn.setColTitle(COLS.COM_SWAP_DATA.PROFILE.rate_dtmn_date, "Rate\nDetermination\nDate");
                          		    	tReturn.setColTitle(COLS.COM_SWAP_DATA.PROFILE.rate_status, "Rate\nStatus");

                          		    	tReturn.setColTitle(COLS.COM_SWAP_DATA.EXTRA.position_in_mmbtu, "Position in MMBTU");
                      		    	}
                    		    	
                    		    } catch(Exception e ){               
                    		    	LIB.log("createComSwapTable", className);
                    		    } 
                    		     
                    		    return tReturn;
                    		}
                			
                			public static Table getComSwapData(int iCurrentDate, String className) throws OException{
                				
                    			Table tReturn = Util.NULL_TABLE;  

                    		    try{
                    		    	
                    		    	tReturn = createComSwapTable(className);
                    		    	
                    		    
                    		    	{
                    		    		// This must match the SQL for column names and order and column type 
                    		    		Table tAbTran = createAbTranTable(className);
                    		    		
                    		    		{
                    		    			Table tTranNums = Table.tableNew();
                    		    			LIB.safeAddCol(tTranNums, "tran_num", COL_TYPE_ENUM.COL_INT);
                    		    			
                    		    			// ComSwaps 
                    		    			String sSQL = "SELECT \n" + 
                    		    					"distinct a.tran_num  \n" + 
                    		    					"FROM\n" + 
                    		    					"ab_tran a, parameter p, \n" + 
                    		    					"(select index_id from idx_def WHERE db_status = 1) i\n" + 
                    		    					"WHERE\n" + 
                    		    					"a.ins_num = p.ins_num AND\n" + 
                    		    					"a.tran_status = 3 AND \n" + 
                    		    					"a.toolset = 15 AND \n" + 
                    		    					"i.index_id = p.proj_index";
                    		    			
                    		    			 
                    		    			if (bGetTradesForDebugData == true) {
                        		    			LIB.execISql(tTranNums, sSQL, bDebugLogging, className);
                    		    			}
                    		    			
                    		    			String sWhat = "deal_tracking_num deal_num, tran_num, ins_num, ins_type, buy_sell, position, external_bunit, internal_portfolio, external_portfolio, start_date, maturity_date end_date, offset_tran_type, offset_tran_num";
                    		    			String sWhere = "1=1";
                    		    			LIB.loadFromDbWithWhatWhere(tAbTran, "ab_tran", tTranNums, sWhat, sWhere, bDebugLogging, className);
                    		    			
                    		    			tTranNums = LIB.destroyTable(tTranNums);
                    		    		}
                    		    		                       		    	
                        		    	if (LIB.safeGetNumRows(tAbTran) >= 1) {
                            		    	tAbTran.copyRowAddAllByColName(tReturn);
                        		    	}
                        		    	tAbTran = LIB.destroyTable(tAbTran);
                    		    	}
                    		    	
                     		    	if (LIB.safeGetNumRows(tReturn) >= 1) {
 
                          		    	// Default to -1
                          		    	LIB.safeSetColValInt(tReturn, COLS.COM_SWAP_DATA.EXTRA.proj_index, -1);

                      		    		Table tInsNums = Table.tableNew();
                        		    	LIB.safeAddCol(tInsNums, "ins_num", COL_TYPE_ENUM.COL_INT);
                        		    	tReturn.copyColDistinct(COLS.COM_SWAP_DATA.ins_num, tInsNums, "ins_num");
                      		    		
                    		    		// get proj_index from parameter data side 0
                        		    	{
                        		    		Table tParameter = Table.tableNew();
                            		    	LIB.safeAddCol(tParameter, COLS.COM_SWAP_DATA.ins_num, COL_TYPE_ENUM.COL_INT);
                            		    	LIB.safeAddCol(tParameter, COLS.COM_SWAP_DATA.EXTRA.proj_index, COL_TYPE_ENUM.COL_INT);
                            		    	LIB.safeAddCol(tParameter, COLS.COM_SWAP_DATA.EXTRA.pay_rec, COL_TYPE_ENUM.COL_INT); 
                            		    	LIB.safeAddCol(tParameter, COLS.COM_SWAP_DATA.EXTRA.unit, COL_TYPE_ENUM.COL_INT); 
                        	
                            		    	String sWhat = "ins_num, proj_index, pay_rec, unit"; 
                        		    		String sWhere = "param_seq_num = 0";
                        		    		LIB.loadFromDbWithWhatWhere(tParameter, "parameter", tInsNums, sWhat, sWhere, bDebugLogging, className);
                        		    		 
                        		    		// param notional is not signed.  Always positive.  Need to adjust by pay_rec to get the sign
                        		    		sWhat = COLS.COM_SWAP_DATA.EXTRA.proj_index + ", " + 
                        		    				COLS.COM_SWAP_DATA.EXTRA.pay_rec + ", " + 
                        		    				COLS.COM_SWAP_DATA.EXTRA.unit  ;
                        		    		sWhere = COLS.COM_SWAP_DATA.ins_num + " EQ $" + COLS.COM_SWAP_DATA.ins_num;
                        		    		LIB.select(tReturn, tParameter, sWhat, sWhere, bDebugLogging, className);
                        		    		
                        		    		tParameter = LIB.destroyTable(tParameter);
                        		    	}

                    		    		// get fixed price from parameter data side 1
                        		    	{
                        		    		Table tParameter = Table.tableNew();
                            		    	LIB.safeAddCol(tParameter, COLS.COM_SWAP_DATA.ins_num, COL_TYPE_ENUM.COL_INT);
                            		    	LIB.safeAddCol(tParameter, COLS.COM_SWAP_DATA.EXTRA.fixed_price, COL_TYPE_ENUM.COL_DOUBLE);
                        	
                            		    	String sWhat = "ins_num, rate"; 
                        		    		String sWhere = "param_seq_num = 1";
                        		    		LIB.loadFromDbWithWhatWhere(tParameter, "parameter", tInsNums, sWhat, sWhere, bDebugLogging, className);
                        		    		 
                        		    		// param notional is not signed.  Always positive.  Need to adjust by pay_rec to get the sign
                        		    		sWhat = COLS.COM_SWAP_DATA.EXTRA.fixed_price;
                        		    		sWhere = COLS.COM_SWAP_DATA.ins_num + " EQ $" + COLS.COM_SWAP_DATA.ins_num;
                        		    		LIB.select(tReturn, tParameter, sWhat, sWhere, bDebugLogging, className);
                        		    		
                        		    		tParameter = LIB.destroyTable(tParameter);
                        		    	}


                    		    		// Populate idx_subgroup
                    		    		{
                    		    			Table tTemp = Table.tableNew();
                    		    			LIB.safeAddCol(tTemp, COLS.COM_SWAP_DATA.EXTRA.proj_index , COL_TYPE_ENUM.COL_INT);
                    		    			LIB.safeAddCol(tTemp, COLS.COM_SWAP_DATA.EXTRA.idx_subgroup , COL_TYPE_ENUM.COL_INT);
                    		    			String sSQL = "select index_id, idx_subgroup from idx_def WHERE db_status = 1";
                    		    			LIB.execISql(tTemp, sSQL, false, className);
                    		    			
                    		    			String sWhat = COLS.COM_SWAP_DATA.EXTRA.idx_subgroup;
                    		    			String sWhere = COLS.COM_SWAP_DATA.EXTRA.proj_index + " EQ $" + COLS.COM_SWAP_DATA.EXTRA.proj_index;
                        		    		LIB.select(tReturn, tTemp, sWhat, sWhere, bDebugLogging, className);
                        		    		  
                    		    			tTemp = LIB.destroyTable(tTemp);
                    		    		}

                    		    		// Populate Year and Vintage
                    		    		{
                    		    			int iNumRows = LIB.safeGetNumRows(tReturn);
                    		    			for (int iCounter = 1; iCounter <= iNumRows; iCounter++) {
                    		    				int iProjIndex = LIB.safeGetInt(tReturn, COLS.COM_SWAP_DATA.EXTRA.proj_index, iCounter);
                    		    				try {
                    		    					int iYear = -100;
                    		    					boolean bGeneric = false;
                    		    					int iIsGeneric = CONST_VALUES.VALUE_OF_FALSE;
                    		    					String sVintage = "Unknown";
                    		    					String sProjIndex = Ref.getName(SHM_USR_TABLES_ENUM.INDEX_TABLE, iProjIndex);
                    		    					
                    		    					boolean bFoundTheVintage = false;
                    		    					
                    		    					// For ICE_EUA
                    		    					if (Str.iEqual(sProjIndex, "ICE_EUA") == 1) {
                    		    						iYear = -1;
                    		    						sVintage = CONST_VALUES.VINTAGE_FOR_EUA;
                    		    						bFoundTheVintage = true;
                    		    					} 
                    		    					
                    		    					// For ICE_EUA
                    		    					if (Str.iEqual(sProjIndex, "NYM_GEO") == 1 ||
                    		    							Str.iEqual(sProjIndex, "NYM_N_GEO") == 1 ||
                    		    							Str.iEqual(sProjIndex, "NYM_C_GEO") == 1) {
                    		    						iYear = -1;
                    		    						sVintage = CONST_VALUES.VINTAGE_FOR_NYM_GEO;
                    		    						bFoundTheVintage = true;
                    		    					} 
                    		    					  
                    		    					// now try for RGGI
                    		    					if (bFoundTheVintage == false) {
                    		    						String sLowerCaseProjIndex = Str.toLower(sProjIndex);
                    		    						int iFirstChar = 0;
                    		    						if (Str.findSubString(sLowerCaseProjIndex, "rggi") == iFirstChar) {

                        		    						if (Str.iEqual(sProjIndex, "RGGI_2020") == 1) {
                            		    						iYear = 2020;
                            		    						sVintage = Str.intToStr(iYear);
                            		    						bFoundTheVintage = true;
                            		    					} else
                        		    						if (Str.iEqual(sProjIndex, "RGGI_2021") == 1) {
                            		    						iYear = 2021;
                            		    						sVintage = Str.intToStr(iYear);
                            		    						bFoundTheVintage = true;
                            		    					} else
                        		    						if (Str.iEqual(sProjIndex, "RGGI_2022") == 1) {
                            		    						iYear = 2022;
                            		    						sVintage = Str.intToStr(iYear);
                            		    						bFoundTheVintage = true;
                            		    					} else
                        		    						if (Str.iEqual(sProjIndex, "RGGI_2023") == 1) {
                            		    						iYear = 2023;
                            		    						sVintage = Str.intToStr(iYear);
                            		    						bFoundTheVintage = true;
                            		    					} else
                        		    						if (Str.iEqual(sProjIndex, "RGGI_2024") == 1) {
                            		    						iYear = 2024;
                            		    						sVintage = Str.intToStr(iYear);
                            		    						bFoundTheVintage = true;
                            		    					} else
                        		    						if (Str.iEqual(sProjIndex, "RGGI_2025") == 1) {
                            		    						iYear = 2025;
                            		    						sVintage = Str.intToStr(iYear);
                            		    						bFoundTheVintage = true;
                            		    					} else
                        		    						if (Str.iEqual(sProjIndex, "RGGI_2026") == 1) {
                            		    						iYear = 2026;
                            		    						sVintage = Str.intToStr(iYear);
                            		    						bFoundTheVintage = true;
                            		    					} else
                        		    						if (Str.iEqual(sProjIndex, "RGGI_2027") == 1) {
                            		    						iYear = 2027;
                            		    						sVintage = Str.intToStr(iYear);
                            		    						bFoundTheVintage = true;
                            		    					} else
                            		    					if (Str.iEqual(sProjIndex, "RGGI_2028") == 1) {
                            		    						iYear = 2028;
                            		    						sVintage = Str.intToStr(iYear);
                            		    						bFoundTheVintage = true;
                            		    					}else
                                		    				if (Str.iEqual(sProjIndex, "RGGI_2029") == 1) {
                                		    					iYear = 2029;
                                		    					sVintage = Str.intToStr(iYear);
                            		    						bFoundTheVintage = true;
                                    		    			}else
                                    		    			if (Str.iEqual(sProjIndex, "RGGI_2030") == 1) {
                                    		    				iYear = 2030;
                                    		    				sVintage = Str.intToStr(iYear);
                            		    						bFoundTheVintage = true;
                                    		    			}

                    		    						}
                    		    						
                    		    					}

                    		    					// if we did not already find it, then keep looking
                    		    					if (bFoundTheVintage == false) {

                        		    					if (Str.findSubString(sProjIndex.toUpperCase(), "GENERIC") >= 1) {
                        		    						bGeneric = true;
                        		    						iIsGeneric = CONST_VALUES.VALUE_OF_TRUE;
                        		    					}
                        		    					
                        		    					if (bGeneric == true) {
                            		    					if (Str.iEqual(sProjIndex, "CCA_Generic") == 1) {
                            		    						iYear = -1;
                            		    						sVintage = CONST_VALUES.GENERIC;
                            		    					} else
                        		    						if (Str.iEqual(sProjIndex, "CCA_Generic_2020") == 1) {
                            		    						iYear = 2020;
                            		    						sVintage = CONST_VALUES.GENERIC + " " + iYear;
                            		    					} else
                        		    						if (Str.iEqual(sProjIndex, "CCA_Generic_2021") == 1) {
                            		    						iYear = 2021;
                            		    						sVintage = CONST_VALUES.GENERIC + " " + iYear;
    	                    		    					} else
    	                		    						if (Str.iEqual(sProjIndex, "CCA_Generic_2022") == 1) {
    	                    		    						iYear = 2022;
                            		    						sVintage = CONST_VALUES.GENERIC + " " + iYear;
    	                    		    					} 
                        		    						if (Str.iEqual(sProjIndex, "CCA_Generic_2023") == 1) {
                            		    						iYear = 2023;
                            		    						sVintage = CONST_VALUES.GENERIC + " " + iYear;
                            		    					} else
                        		    						if (Str.iEqual(sProjIndex, "CCA_Generic_2024") == 1) {
                            		    						iYear = 2024;
                            		    						sVintage = CONST_VALUES.GENERIC + " " + iYear;
    	                    		    					} else
    	                		    						if (Str.iEqual(sProjIndex, "CCA_Generic_2025") == 1) {
    	                    		    						iYear = 2025;
                            		    						sVintage = CONST_VALUES.GENERIC + " " + iYear;
    	                    		    					} 
                        		    						if (Str.iEqual(sProjIndex, "CCA_Generic_2026") == 1) {
                            		    						iYear = 2026;
                            		    						sVintage = CONST_VALUES.GENERIC + " " + iYear;
                            		    					} else
                        		    						if (Str.iEqual(sProjIndex, "CCA_Generic_2027") == 1) {
                            		    						iYear = 2027;
                            		    						sVintage = CONST_VALUES.GENERIC + " " + iYear;
    	                    		    					} else
    	                		    						if (Str.iEqual(sProjIndex, "CCA_Generic_2028") == 1) {
    	                    		    						iYear = 2028;
                            		    						sVintage = CONST_VALUES.GENERIC + " " + iYear;
    	                    		    					} 
                        		    						if (Str.iEqual(sProjIndex, "CCA_Generic_2029") == 1) {
                            		    						iYear = 2029;
                            		    						sVintage = CONST_VALUES.GENERIC + " " + iYear;
                            		    					} else
                        		    						if (Str.iEqual(sProjIndex, "CCA_Generic_2030") == 1) {
                            		    						iYear = 2030;
                            		    						sVintage = CONST_VALUES.GENERIC + " " + iYear;
    	                    		    					} else
    	                		    						if (Str.iEqual(sProjIndex, "CCA_Generic_2019") == 1) {
    	                    		    						iYear = 2019;
                            		    						sVintage = CONST_VALUES.GENERIC + " " + iYear;
    	                    		    					} else
    	                		    						if (Str.iEqual(sProjIndex, "CCA_Generic_2018") == 1) {
    	                    		    						iYear = 2018;
                            		    						sVintage = CONST_VALUES.GENERIC + " " + iYear;
    	                    		    					} else
    	                		    						if (Str.iEqual(sProjIndex, "CCA_Generic_2017") == 1) {
    	                    		    						iYear = 2017;
                            		    						sVintage = CONST_VALUES.GENERIC + " " + iYear;
    	                    		    					} 
                        		    					}

                        		    					if (bGeneric == false) {
                        		    					
                        		    						if (Str.iEqual(sProjIndex, "CCA_2020") == 1) {
                            		    						iYear = 2020;
                            		    						sVintage = Str.intToStr(iYear);
                            		    					} else
                            		    					if (Str.iEqual(sProjIndex, "CCA_2021") == 1) {
                            		    						iYear = 2021;
                            		    						sVintage = Str.intToStr(iYear);
                            		    					} else
                            		    					if (Str.iEqual(sProjIndex, "CCA_2022") == 1) {
                            		    						iYear = 2022;
                            		    						sVintage = Str.intToStr(iYear);
                            		    					} else
                            		    					if (Str.iEqual(sProjIndex, "CCA_2023") == 1) {
                            		    						iYear = 2023;
                            		    						sVintage = Str.intToStr(iYear);
                            		    					} else
                            		    					if (Str.iEqual(sProjIndex, "CCA_2024") == 1) {
                            		    						iYear = 2024;
                            		    						sVintage = Str.intToStr(iYear);
                            		    					} else
                            		    					if (Str.iEqual(sProjIndex, "CCA_2025") == 1) {
                            		    						iYear = 2025;
                            		    						sVintage = Str.intToStr(iYear);
                            		    					} else
                            		    					if (Str.iEqual(sProjIndex, "CCA_2026") == 1) {
                            		    						iYear = 2026;
                            		    						sVintage = Str.intToStr(iYear);
                            		    					} else
                            		    					if (Str.iEqual(sProjIndex, "CCA_2027") == 1) {
                            		    						iYear = 2027;
                            		    						sVintage = Str.intToStr(iYear);
                            		    					} else
                            		    					if (Str.iEqual(sProjIndex, "CCA_2028") == 1) {
                            		    						iYear = 2028;
                            		    						sVintage = Str.intToStr(iYear);
                            		    					} else
                            		    					if (Str.iEqual(sProjIndex, "CCA_2029") == 1) {
                            		    						iYear = 2029;
                            		    						sVintage = Str.intToStr(iYear);
                            		    					} else
                            		    					if (Str.iEqual(sProjIndex, "CCA_2030") == 1) {
                            		    						iYear = 2030;
                            		    						sVintage = Str.intToStr(iYear);
                            		    					} else
                            		    					if (Str.iEqual(sProjIndex, "CCA_2019") == 1) {
                            		    						iYear = 2019;
                            		    						sVintage = Str.intToStr(iYear);
                            		    					} else
                            		    					if (Str.iEqual(sProjIndex, "CCA_2018") == 1) {
                            		    						iYear = 2018;
                            		    						sVintage = Str.intToStr(iYear);
                            		    					} else
                            		    					if (Str.iEqual(sProjIndex, "CCA_2017") == 1) {
                            		    						iYear = 2017;
                            		    						sVintage = Str.intToStr(iYear);
                            		    					}  
                        		    					}
                    		    					} // END  if (bFoundTheVintage == false) 
                     		    					
                    		    					LIB.safeSetString(tReturn, COLS.COM_SWAP_DATA.EXTRA.vintage, iCounter, sVintage);
                    		    					LIB.safeSetInt(tReturn, COLS.COM_SWAP_DATA.EXTRA.year, iCounter, iYear);
                    		    					LIB.safeSetInt(tReturn, COLS.COM_SWAP_DATA.EXTRA.is_generic_flag, iCounter, iIsGeneric);
                     		    					
                    		    				} catch (Exception e) {
                    		    					// do not log this
                    		    				}
                    		    			}
                    		    			
                    		    		}
                      		    		 
                    		    		tInsNums = LIB.destroyTable(tInsNums);
                    		    	}
                    		    	 
                    		    	// now get profile data
                    		    	if (LIB.safeGetNumRows(tReturn) >= 1) {
                        		    	
                      		    		Table tInsNums = Table.tableNew();
                        		    	LIB.safeAddCol(tInsNums, "ins_num", COL_TYPE_ENUM.COL_INT);
                        		    	tReturn.copyColDistinct(COLS.COM_SWAP_DATA.ins_num, tInsNums, "ins_num");
                      		    		
                    		    		// get profile data
                    		    		Table tProfile = Table.tableNew();
                        		    	LIB.safeAddCol(tProfile, COLS.COM_SWAP_DATA.ins_num, COL_TYPE_ENUM.COL_INT);
                        		    	LIB.safeAddCol(tProfile, COLS.COM_SWAP_DATA.PROFILE.profile_seq_num, COL_TYPE_ENUM.COL_INT);
                        		    	LIB.safeAddCol(tProfile, COLS.COM_SWAP_DATA.PROFILE.profile_start_date, COL_TYPE_ENUM.COL_INT);
                        		    	LIB.safeAddCol(tProfile, COLS.COM_SWAP_DATA.PROFILE.profile_end_date, COL_TYPE_ENUM.COL_INT);
                        		    	LIB.safeAddCol(tProfile, COLS.COM_SWAP_DATA.PROFILE.rate_dtmn_date, COL_TYPE_ENUM.COL_INT);
                        		    	LIB.safeAddCol(tProfile, COLS.COM_SWAP_DATA.PROFILE.rate_status, COL_TYPE_ENUM.COL_INT);
                        		    	LIB.safeAddCol(tProfile, COLS.COM_SWAP_DATA.PROFILE.profile_notnl, COL_TYPE_ENUM.COL_DOUBLE);
                        		    	LIB.safeAddCol(tProfile, COLS.COM_SWAP_DATA.PROFILE.pymt_date, COL_TYPE_ENUM.COL_INT);
                        		   
                        		    	String sWhat = "ins_num, profile_seq_num, start_date profile_start_date, end_date profile_end_date, rate_dtmn_date, rate_status, notnl profile_notnl, pymt_date"; 
                    		    		String sWhere = "param_seq_num = 0";
                    		    		LIB.loadFromDbWithWhatWhere(tProfile, "profile", tInsNums, sWhat, sWhere, bDebugLogging, className);
                     		    		
                    		    		// param notional is not signed.  Always positive.  Need to adjust by pay_rec to get the sign
                    		    		sWhat = COLS.COM_SWAP_DATA.PROFILE.profile_seq_num + ", " + 
                    		    				COLS.COM_SWAP_DATA.PROFILE.profile_start_date + ", " +
                    		    				COLS.COM_SWAP_DATA.PROFILE.profile_end_date + ", " +
                    		    				COLS.COM_SWAP_DATA.PROFILE.rate_dtmn_date + ", " +
                    		    				COLS.COM_SWAP_DATA.PROFILE.rate_status + ", " +
                    		    				COLS.COM_SWAP_DATA.PROFILE.profile_notnl + ", " +
                    		    				COLS.COM_SWAP_DATA.PROFILE.pymt_date;
                    		    		sWhere = COLS.COM_SWAP_DATA.ins_num + " EQ $" + COLS.COM_SWAP_DATA.ins_num;
                    		    		LIB.select(tReturn, tProfile, sWhat, sWhere, bDebugLogging, className);
                    		    		   
                    		    		tInsNums = LIB.destroyTable(tInsNums);
                    		    		tProfile = LIB.destroyTable(tProfile);
                    		    	}
                    		    	
                    		    	// now get reset data at the summary level
                    		    	if (LIB.safeGetNumRows(tReturn) >= 1) {
                        		    	
                      		    		Table tInsNums = Table.tableNew();
                        		    	LIB.safeAddCol(tInsNums, "ins_num", COL_TYPE_ENUM.COL_INT);
                        		    	tReturn.copyColDistinct(COLS.COM_SWAP_DATA.ins_num, tInsNums, "ins_num");
                      		    		
                    		    		// get reset data
                    		    		Table tReset = Table.tableNew();
                        		    	LIB.safeAddCol(tReset, COLS.COM_SWAP_DATA.ins_num, COL_TYPE_ENUM.COL_INT);
                        		    	LIB.safeAddCol(tReset, COLS.COM_SWAP_DATA.PROFILE.profile_seq_num, COL_TYPE_ENUM.COL_INT);
                        		    	LIB.safeAddCol(tReset, COLS.COM_SWAP_DATA.EXTRA.position_in_mmbtu, COL_TYPE_ENUM.COL_DOUBLE);
                        		   
                        		    	String sWhat = "ins_num, profile_seq_num, sum(reset_notional) ohd_reset_notional"; 
                    		    		String sWhere = "param_seq_num = 0 AND block_end = 0 AND value_status = 2 GROUP BY ins_num, profile_seq_num";
                    		    		LIB.loadFromDbWithWhatWhere(tReset, "reset", tInsNums, sWhat, sWhere, bDebugLogging, className);
                    		    		 
                    		    		// We only load for param_seq_num = 0, so don't need to worry about joining on that
                    		    		sWhat = COLS.COM_SWAP_DATA.EXTRA.position_in_mmbtu;
                    		    		sWhere = COLS.COM_SWAP_DATA.ins_num + " EQ $" + COLS.COM_SWAP_DATA.ins_num + " AND " + 
                    		    				COLS.COM_SWAP_DATA.PROFILE.profile_seq_num + " EQ $" + COLS.COM_SWAP_DATA.PROFILE.profile_seq_num;
                    		    		LIB.select(tReturn, tReset, sWhat, sWhere, bDebugLogging, className);
 	
                    		    		tInsNums = LIB.destroyTable(tInsNums);
                    		    		tReset = LIB.destroyTable(tReset);
                    		    	} 	
                    		    	

                    		    	if (LIB.safeGetNumRows(tReturn) >= 1) {
                    		    		
                    		    		final int ICE_SWAP = 1000001;
                    		    		final int NYMEX_SWAP = 1000004;
                    		    		
                    		    		int iNumRows = LIB.safeGetNumRows(tReturn);
                    		    		for (int iCounter = 1; iCounter <= iNumRows; iCounter++) {
                    		    			int iInsType = LIB.safeGetInt(tReturn, COLS.COM_SWAP_DATA.ins_type, iCounter);
                    		    			// must be ICE Swap or NYMEX Swap to be 'exchange'
                    		    			if (iInsType == ICE_SWAP || iInsType == NYMEX_SWAP) {
                    		    				// and counterparty must be LCH, ICE, ICE1 or NYMEX to be 'Exchange'
                        		    			int iCounterparty = LIB.safeGetInt(tReturn, COLS.COM_SWAP_DATA.external_bunit, iCounter);
                        		    			String sCounterparty = Ref.getName(SHM_USR_TABLES_ENUM.PARTY_TABLE, iCounterparty);
                        		    			if (Str.iEqual(sCounterparty, "LCH") == 1 || Str.iEqual(sCounterparty, "ICE1") == 1 || Str.iEqual(sCounterparty, "NYMEX") == 1 || Str.iEqual(sCounterparty, "ICE") == 1) {
                        		    				LIB.safeSetInt(tReturn, COLS.COM_SWAP_DATA.EXTRA.is_exchange_flag, iCounter, CONST_VALUES.VALUE_OF_TRUE);
                        		    			}
                    		    			}
                    		    		}
                    		    	} 
                    		    	
                    		    	{
                    		    		try {
                    		    			Table tOutput = Index.getOutput("CCA_2021");
                    		    			tOutput.colConvertDateTimeToInt("Date");

                    		    			tOutput.setColName(1, COLS.COM_SWAP_DATA.PROFILE.rate_dtmn_date);
                    		    			tOutput.setColName(2, COLS.COM_SWAP_DATA.EXTRA.contract_month); 
                    		    			
                    		    			String sWhat = COLS.COM_SWAP_DATA.EXTRA.contract_month;
                    		    			String sWhere = COLS.COM_SWAP_DATA.PROFILE.rate_dtmn_date + " EQ $" + COLS.COM_SWAP_DATA.PROFILE.rate_dtmn_date;
                    		    			LIB.select(tReturn, tOutput, sWhat, sWhere, false, className);
                    		    		 
                    		    			int iNumRows = LIB.safeGetNumRows(tReturn);
                    		    			for (int iCounter = 1; iCounter <= iNumRows; iCounter++) {
                    		    				try {
                    		    					String sContractMonth = LIB.safeGetString(tReturn, COLS.COM_SWAP_DATA.EXTRA.contract_month, iCounter);
                    		    					if (sContractMonth == null || Str.len(sContractMonth) < 3) {
                    		    						int iDate = LIB.safeGetInt(tReturn, COLS.COM_SWAP_DATA.PROFILE.rate_dtmn_date, iCounter);
                    		    						String sDate = OCalendar.formatDateInt(iDate, DATE_FORMAT.DATE_FORMAT_IMM);
                    		    						LIB.safeSetString(tReturn, COLS.COM_SWAP_DATA.EXTRA.contract_month, iCounter, sDate);
                    		    					}
                    		    				} catch (Exception e) {
                    		    					// do not log this
                    		    				}
                    		    			}
                    		    			
                    		    			//LIB.viewTable(tOutput, "sgsdfgsdf 4544");
                    		    			
                    		    			tOutput = LIB.destroyTable(tOutput);
                    		    		} catch (Exception e) {
                    		    			// do not log this
                    		    		}
                    		    	}

                    		       	String sGroupCols = COLS.COM_SWAP_DATA.deal_num + ", " + 
                    		    			COLS.COM_SWAP_DATA.PROFILE.profile_seq_num;
                    		    	tReturn.group(sGroupCols);
                    		    	
                    		    } catch(Exception e ){               
                    		    	LIB.log("getComSwapData", className);
                    		    } 
  
                    		    // TOxDO
                   		 //   LIB.viewTable(tReturn, "ComSwap data bdfdf444");
                    		    
                    		    return tReturn;
                    		}
                			
                		} // END public static class COM_SWAP
                 		
                		public static class COMMODITY {  
                			

                			public static Table createCommodityTable(String className) throws OException{
                				
                    			Table tReturn = Util.NULL_TABLE; 
                    		    try{
                    		    	tReturn = Table.tableNew();
                    		    	LIB.safeAddCol(tReturn, COLS.COMMODITY_DATA.deal_num, COL_TYPE_ENUM.COL_INT);
                    		    	LIB.safeAddCol(tReturn, COLS.COMMODITY_DATA.tran_num, COL_TYPE_ENUM.COL_INT);
                    		    	LIB.safeAddCol(tReturn, COLS.COMMODITY_DATA.ins_num, COL_TYPE_ENUM.COL_INT);
                    		    	LIB.safeAddCol(tReturn, COLS.COMMODITY_DATA.ins_type, COL_TYPE_ENUM.COL_INT);
                    		    	
                    		    	LIB.safeAddCol(tReturn, COLS.COMMODITY_DATA.buy_sell, COL_TYPE_ENUM.COL_INT); 
                    		    	
                    		    	LIB.safeAddCol(tReturn, COLS.COMMODITY_DATA.external_bunit, COL_TYPE_ENUM.COL_INT); 
                    		    	LIB.safeAddCol(tReturn, COLS.COMMODITY_DATA.portfolio, COL_TYPE_ENUM.COL_INT); 
                    		    	
                    		    	LIB.safeAddCol(tReturn, COLS.COMMODITY_DATA.start_date, COL_TYPE_ENUM.COL_INT); 
                    		    	LIB.safeAddCol(tReturn, COLS.COMMODITY_DATA.end_date, COL_TYPE_ENUM.COL_INT); 
                           		 
                    		    	tReturn.defaultFormat();
                    		      	
                    		    	LIB.safeSetColFormatAsRef(tReturn, COLS.COMMODITY_DATA.external_bunit, SHM_USR_TABLES_ENUM.PARTY_TABLE);
                    		    	LIB.safeSetColFormatAsRef(tReturn, COLS.COMMODITY_DATA.portfolio, SHM_USR_TABLES_ENUM.PORTFOLIO_TABLE);
                    		    	LIB.safeSetColFormatAsRef(tReturn, COLS.COMMODITY_DATA.ins_type, SHM_USR_TABLES_ENUM.INSTRUMENTS_TABLE);
                    		    	LIB.safeSetColFormatAsRef(tReturn, COLS.COMMODITY_DATA.buy_sell, SHM_USR_TABLES_ENUM.BUY_SELL_TABLE);
 
                    		    } catch(Exception e ){               
                    		    	LIB.log("createCommodityTable", className);
                    		    } 
                    		     
                    		    return tReturn;
                    		}
                			
                			public static Table getCommodityData(int iCurrentDate, String className) throws OException{
                				
                    			Table tReturn = Util.NULL_TABLE; 
                    		    try{
                    		    	
                    		    	tReturn = createCommodityTable(className);
                    		    	 
                    		    //	String sDate = "'" + OCalendar.formatJdForDbAccess(iCurrentDate) + "'";
                    		     
                    		     
                    		    	
                    		    	{
                        		    	Table tInsNums = Table.tableNew();
                        		    	LIB.safeAddCol(tInsNums, "ins_num", COL_TYPE_ENUM.COL_INT);
                        		    	
                        		    	{
                        		    		// get trades 
                        		    		String sSQL = "select distinct a.ins_num from gas_phys_param_view g, ab_tran a WHERE a.ins_num = g.ins_num AND a.tran_status = 3";
                        		    		
                        		    		if (bGetTradesForDebugData == true) {
                            		    		LIB.execISql(tInsNums, sSQL, false, className); 
                        		    		}
                        		    	}
                        		    	 
                        		    	{
                        		    		if (LIB.safeGetNumRows(tInsNums) >= 1) {
                               		    		String sWhat = "deal_tracking_num deal_num, tran_num, ins_num, ins_type, buy_sell, external_bunit, internal_portfolio portfolio, start_date, maturity_date end_date";
                            		    		String sWhere = "1=1";
                            		    		LIB.loadFromDbWithWhatWhere(tReturn, "ab_tran", tInsNums, sWhat, sWhere, bDebugLogging, className);
                            		    		}
                        		    	}
                        		    	
                        		    	tInsNums = LIB.destroyTable(tInsNums);
                     		    	}
  
                    		    	LIB.safeAddCol(tReturn, COLS.COMMODITY_DATA.PARAM.param_seq_num, COL_TYPE_ENUM.COL_INT); 
                    		    	LIB.safeAddCol(tReturn, COLS.COMMODITY_DATA.PARAM.proj_index, COL_TYPE_ENUM.COL_INT); 
                    		    	LIB.safeAddCol(tReturn, COLS.COMMODITY_DATA.PARAM.settlement_type, COL_TYPE_ENUM.COL_INT); 
                    		    	LIB.safeAddCol(tReturn, COLS.COMMODITY_DATA.PARAM.pay_rec, COL_TYPE_ENUM.COL_INT); 
                    		    	LIB.safeAddCol(tReturn, COLS.COMMODITY_DATA.PARAM.fx_flt, COL_TYPE_ENUM.COL_INT); 
                    		    	LIB.safeAddCol(tReturn, COLS.COMMODITY_DATA.PARAM.fixed_price, COL_TYPE_ENUM.COL_DOUBLE); 
                    		    	LIB.safeAddCol(tReturn, COLS.COMMODITY_DATA.PARAM.delivery_type, COL_TYPE_ENUM.COL_INT); 
                     		     	
                    		    	LIB.safeAddCol(tReturn, COLS.COMMODITY_DATA.PROFILE.profile_seq_num, COL_TYPE_ENUM.COL_INT); 
                    		    	LIB.safeAddCol(tReturn, COLS.COMMODITY_DATA.PROFILE.profile_notnl, COL_TYPE_ENUM.COL_DOUBLE); 
                    		    	LIB.safeAddCol(tReturn, COLS.COMMODITY_DATA.PROFILE.profile_start_date, COL_TYPE_ENUM.COL_INT); 
                    		    	LIB.safeAddCol(tReturn, COLS.COMMODITY_DATA.PROFILE.profile_end_date, COL_TYPE_ENUM.COL_INT); 
                    		    	LIB.safeAddCol(tReturn, COLS.COMMODITY_DATA.PROFILE.rate_dtmn_date, COL_TYPE_ENUM.COL_INT); 
                    		    	LIB.safeAddCol(tReturn, COLS.COMMODITY_DATA.PROFILE.rate_status, COL_TYPE_ENUM.COL_INT); 
                    		    	LIB.safeAddCol(tReturn, COLS.COMMODITY_DATA.PROFILE.pymt_date, COL_TYPE_ENUM.COL_INT); 

                    		    	// this will be the position from the reset table
                    		    	LIB.safeAddCol(tReturn, COLS.COMMODITY_DATA.EXTRA.position_in_mmbtu, COL_TYPE_ENUM.COL_DOUBLE); 
  
                    		    	// Param
                    		    	LIB.safeSetColFormatAsRef(tReturn, COLS.COMMODITY_DATA.PARAM.settlement_type, SHM_USR_TABLES_ENUM.SETTLEMENT_TYPE_TABLE);
                    		    	LIB.safeSetColFormatAsRef(tReturn, COLS.COMMODITY_DATA.PARAM.pay_rec, SHM_USR_TABLES_ENUM.REC_PAY_TABLE);
                    		    	LIB.safeSetColFormatAsRef(tReturn, COLS.COMMODITY_DATA.PARAM.fx_flt, SHM_USR_TABLES_ENUM.FX_FLT_TABLE);
                    		    	LIB.safeSetColFormatAsRef(tReturn, COLS.COMMODITY_DATA.PARAM.proj_index, SHM_USR_TABLES_ENUM.INDEX_TABLE);
                    		    	LIB.safeSetColFormatAsRef(tReturn, COLS.COMMODITY_DATA.PARAM.delivery_type, SHM_USR_TABLES_ENUM.DELIVERY_TYPE_TABLE);
                    		    	
                     		    	// Profile
                    		    	LIB.safeSetColFormatAsRef(tReturn, COLS.COMMODITY_DATA.PROFILE.rate_status, SHM_USR_TABLES_ENUM.VALUE_STATUS_TABLE);

                       		    	// date formatting
                      		    	LIB.safeSetColFormatAsDate(tReturn, COLS.COMMODITY_DATA.start_date);
                      		    	LIB.safeSetColFormatAsDate(tReturn, COLS.COMMODITY_DATA.end_date);
                      		    	
                      		    	// Profile Date formatting
                      		    	LIB.safeSetColFormatAsDate(tReturn, COLS.COMMODITY_DATA.PROFILE.profile_start_date);
                      		    	LIB.safeSetColFormatAsDate(tReturn, COLS.COMMODITY_DATA.PROFILE.profile_end_date);
                      		    	LIB.safeSetColFormatAsDate(tReturn, COLS.COMMODITY_DATA.PROFILE.rate_dtmn_date);
                      		    	LIB.safeSetColFormatAsDate(tReturn, COLS.COMMODITY_DATA.PROFILE.pymt_date);
                      		        
                      		    	// Default to -1
                      		    	LIB.safeSetColValInt(tReturn, COLS.COMMODITY_DATA.PARAM.proj_index, -1);
  
                     		    	if (LIB.safeGetNumRows(tReturn) >= 1) {
                    		    	
                      		    		Table tInsNums = Table.tableNew();
                        		    	LIB.safeAddCol(tInsNums, "ins_num", COL_TYPE_ENUM.COL_INT);
                        		    	tReturn.copyColDistinct(COLS.COMMODITY_DATA.ins_num, tInsNums, "ins_num");
                      		    		
                    		    		// get parameter data
                    		    		Table tParameter = Table.tableNew();
                        		    	LIB.safeAddCol(tParameter, COLS.COMMODITY_DATA.ins_num, COL_TYPE_ENUM.COL_INT);
                        		    	LIB.safeAddCol(tParameter, COLS.COMMODITY_DATA.PARAM.param_seq_num, COL_TYPE_ENUM.COL_INT);
                        		    	LIB.safeAddCol(tParameter, COLS.COMMODITY_DATA.PARAM.proj_index, COL_TYPE_ENUM.COL_INT);
                        		    	LIB.safeAddCol(tParameter, COLS.COMMODITY_DATA.PARAM.fx_flt, COL_TYPE_ENUM.COL_INT);
                        		    	LIB.safeAddCol(tParameter, COLS.COMMODITY_DATA.PARAM.pay_rec, COL_TYPE_ENUM.COL_INT); 
                        		    	LIB.safeAddCol(tParameter, COLS.COMMODITY_DATA.PARAM.settlement_type, COL_TYPE_ENUM.COL_INT); 
                        		    	LIB.safeAddCol(tParameter, COLS.COMMODITY_DATA.PARAM.delivery_type, COL_TYPE_ENUM.COL_INT); 
                        		    	LIB.safeAddCol(tParameter, COLS.COMMODITY_DATA.PARAM.fixed_price, COL_TYPE_ENUM.COL_DOUBLE); 
                    	
                        		    	String sWhat = "ins_num, param_seq_num, proj_index, fx_flt, pay_rec, settlement_type, delivery_type, rate"; 
                        		    	// do not load deal level data
                    		    		String sWhere = "param_seq_num > 0"; 
                    		    		LIB.loadFromDbWithWhatWhere(tParameter, "parameter", tInsNums, sWhat, sWhere, bDebugLogging, className);

                    		    		{ 
                        		    		
                            		    	sWhat = COLS.COMMODITY_DATA.PARAM.param_seq_num + ", " 
                                    		    	+ COLS.COMMODITY_DATA.PARAM.proj_index + ", " 
                                    		    	+ COLS.COMMODITY_DATA.PARAM.fx_flt + ", " 
                                    		    	+ COLS.COMMODITY_DATA.PARAM.pay_rec + ", " 
                                    		    	+ COLS.COMMODITY_DATA.PARAM.settlement_type + ", " 
                                    		    	+ COLS.COMMODITY_DATA.PARAM.delivery_type + ", " 
                                    		    	+ COLS.COMMODITY_DATA.PARAM.fixed_price;
                            		    	sWhere = COLS.COMMODITY_DATA.ins_num + " EQ $" + COLS.COMMODITY_DATA.ins_num;;
                            		    	LIB.select(tReturn, tParameter, sWhat, sWhere, bDebugLogging, className);
                    		    		}
                    		    	 
                    		    		  
                    		    		tInsNums = LIB.destroyTable(tInsNums);
                    		    		tParameter = LIB.destroyTable(tParameter);
                    		    	}
                     		    	 
                    		    	// now get profile data
                    		    	if (LIB.safeGetNumRows(tReturn) >= 1) {
                        		    	
                      		    		Table tInsNums = Table.tableNew();
                        		    	LIB.safeAddCol(tInsNums, "ins_num", COL_TYPE_ENUM.COL_INT);
                        		    	tReturn.copyColDistinct(COLS.COMMODITY_DATA.ins_num, tInsNums, "ins_num");
                      		    		
                    		    		// get profile data
                    		    		Table tProfile = Table.tableNew();
                        		    	LIB.safeAddCol(tProfile, COLS.COMMODITY_DATA.ins_num, COL_TYPE_ENUM.COL_INT);
                        		    	LIB.safeAddCol(tProfile, COLS.COMMODITY_DATA.PARAM.param_seq_num, COL_TYPE_ENUM.COL_INT);
                        		    	LIB.safeAddCol(tProfile, COLS.COMMODITY_DATA.PROFILE.profile_seq_num, COL_TYPE_ENUM.COL_INT);
                        		    	LIB.safeAddCol(tProfile, COLS.COMMODITY_DATA.PROFILE.profile_start_date, COL_TYPE_ENUM.COL_INT);
                        		    	LIB.safeAddCol(tProfile, COLS.COMMODITY_DATA.PROFILE.profile_end_date, COL_TYPE_ENUM.COL_INT);
                        		    	LIB.safeAddCol(tProfile, COLS.COMMODITY_DATA.PROFILE.rate_dtmn_date, COL_TYPE_ENUM.COL_INT);
                        		    	LIB.safeAddCol(tProfile, COLS.COMMODITY_DATA.PROFILE.rate_status, COL_TYPE_ENUM.COL_INT);
                        		    	LIB.safeAddCol(tProfile, COLS.COMMODITY_DATA.PROFILE.profile_notnl, COL_TYPE_ENUM.COL_DOUBLE);
                        		    	LIB.safeAddCol(tProfile, COLS.COMMODITY_DATA.PROFILE.pymt_date, COL_TYPE_ENUM.COL_INT);
                        		   
                        		    	String sWhat = "ins_num, param_seq_num, profile_seq_num, start_date profile_start_date, end_date profile_end_date, rate_dtmn_date, rate_status, notnl profile_notnl, pymt_date"; 
                    		    		String sWhere = "param_seq_num > 0";
                    		    		LIB.loadFromDbWithWhatWhere(tProfile, "profile", tInsNums, sWhat, sWhere, bDebugLogging, className);
                     		    		
                    		    		// param notional is not signed.  Always positive.  Need to adjust by pay_rec to get the sign
                    		    		sWhat = COLS.COMMODITY_DATA.PROFILE.profile_seq_num + ", " + 
                    		    				COLS.COMMODITY_DATA.PROFILE.profile_start_date + ", " +
                    		    				COLS.COMMODITY_DATA.PROFILE.profile_end_date + ", " +
                    		    				COLS.COMMODITY_DATA.PROFILE.rate_dtmn_date + ", " +
                    		    				COLS.COMMODITY_DATA.PROFILE.rate_status + ", " +
                    		    				COLS.COMMODITY_DATA.PROFILE.profile_notnl + ", " +
                    		    				COLS.COMMODITY_DATA.PROFILE.pymt_date;
                    		    		sWhere = COLS.COMMODITY_DATA.ins_num + " EQ $" + COLS.COMMODITY_DATA.ins_num + " AND " +
                    		    				COLS.COMMODITY_DATA.PARAM.param_seq_num + " EQ $" + COLS.COMMODITY_DATA.PARAM.param_seq_num;
                    		    		LIB.select(tReturn, tProfile, sWhat, sWhere, bDebugLogging, className);
                    		    		   
                    		    		tInsNums = LIB.destroyTable(tInsNums);
                    		    		tProfile = LIB.destroyTable(tProfile);
                    		    	}
                    		    	  
                    		    	// Copy Profile Notional to be Position In MT
                    		    	tReturn.mathAddColConst(COLS.COMMODITY_DATA.PROFILE.profile_notnl, 0, COLS.COMMODITY_DATA.EXTRA.position_in_mmbtu);

                    		    	{ 
                		    			// Do Join to wipe out Proj Index for Fx Flt = fixed
                		    			Table tTemp = Table.tableNew();
                        		    	LIB.safeAddCol(tTemp, COLS.COMMODITY_DATA.PARAM.proj_index, COL_TYPE_ENUM.COL_INT);
                        		    	LIB.safeAddCol(tTemp, COLS.COMMODITY_DATA.PARAM.fx_flt, COL_TYPE_ENUM.COL_INT);
                        		    	LIB.safeAddCol(tTemp, COLS.COMMODITY_DATA.EXTRA.position_in_mmbtu, COL_TYPE_ENUM.COL_DOUBLE);
                        		    	int iMaxRow = tTemp.addRow();
                        		    	LIB.safeSetInt(tTemp, COLS.COMMODITY_DATA.PARAM.proj_index, iMaxRow, -1);
                        		    	final int FIXED = 0;
                        		    	LIB.safeSetInt(tTemp, COLS.COMMODITY_DATA.PARAM.fx_flt, iMaxRow, FIXED);
                        		    	LIB.safeSetDouble(tTemp, COLS.COMMODITY_DATA.EXTRA.position_in_mmbtu, iMaxRow, 0);
                        		    	
                        		    	// and set position in oz to zero
                        		    	String sWhat = COLS.COMMODITY_DATA.PARAM.proj_index + ", " + COLS.COMMODITY_DATA.EXTRA.position_in_mmbtu;
                        		    	String sWhere = COLS.COMMODITY_DATA.PARAM.fx_flt + " EQ $" + COLS.COMMODITY_DATA.PARAM.fx_flt;
                        		    	LIB.select(tReturn, tTemp, sWhat, sWhere, bDebugLogging, className);
                        		    	
                        		    	tTemp = LIB.destroyTable(tTemp);
                		    		}
                    		    	
                    		    	{ 
                    		    		// TODO
                		    			// Do Join to wipe out position in mt for any Cash sides (until we can add this functionality)
                		    			Table tTemp = Table.tableNew();
                        		    	LIB.safeAddCol(tTemp, COLS.COMMODITY_DATA.PARAM.settlement_type, COL_TYPE_ENUM.COL_INT);
                        		    	LIB.safeAddCol(tTemp, COLS.COMMODITY_DATA.EXTRA.position_in_mmbtu, COL_TYPE_ENUM.COL_DOUBLE);
                        		    	int iMaxRow = tTemp.addRow();
                		    			final int CASH = 1;
                        		    	LIB.safeSetInt(tTemp, COLS.COMMODITY_DATA.PARAM.settlement_type, iMaxRow, CASH);
                        		    	LIB.safeSetDouble(tTemp, COLS.COMMODITY_DATA.EXTRA.position_in_mmbtu, iMaxRow, 0);
                        		    	
                        		    	// and set position in mt to zero
                        		    	String sWhat = COLS.COMMODITY_DATA.EXTRA.position_in_mmbtu;
                        		    	String sWhere = COLS.COMMODITY_DATA.PARAM.settlement_type + " EQ $" + COLS.COMMODITY_DATA.PARAM.settlement_type;
                        		    	LIB.select(tReturn, tTemp, sWhat, sWhere, bDebugLogging, className);
                        		    	
                        		    	tTemp = LIB.destroyTable(tTemp);
                		    		}
                    		    	 
                    		    	// TOxDO
//                    		    	LIB.viewTable(tReturn, "Commodity data sdvsdfg344 ");
                    		     	
                    		    } catch(Exception e ){               
                    		    	LIB.log("getCommodityData", className);
                    		    } 
                    		 
                    		    
                    		    return tReturn;
                    		}
                			
                		} // END public static class COMMODITY 
                		
                		
                    } // end class TOOLSET
                    
            		public static void addTableToMasterTableAfterFirstDeletingAndThenAddingTheColumn(Table tMaster, String sColName, Table tSubTable, String sNewTableTitle, String className) throws OException{
            		    try{
            				    
        				  // this will destroy the table (data) in that column if any, i.e., avoid mem leak
        		    	
  		            	  // not sure if there would be stuff already there.  So just in case, delete the col and re-add it
  		            	  int iColNum = tMaster.getColNum(sColName);
  		            	  if (iColNum >= 1) {
  		            		  LIB.safeDelCol(tMaster, sColName);
  		            		  tMaster.insertCol(sColName, iColNum, COL_TYPE_ENUM.COL_TABLE);
  		            	  }

  		            	  // Add it if needed, though this should never be needed
  		            	  if (iColNum < 1) {
  		            		  tMaster.addCol(sColName, COL_TYPE_ENUM.COL_TABLE);
  		            	  }
  		            	  
  		            	  // check for a valid table (indirectly) by checking 1 or more *columns* not rows
  		            	  if (LIB.safeGetNumCols(tMaster) >= 1) {
  	  		            	  // Set table name and title
  		            		  String sName = sNewTableTitle + " Data: " + LIB.safeGetNumRows(tSubTable);
  		            		  tSubTable.setTableName(sName);
  		            		  tSubTable.setTableTitle(sName);
  	  		            	  
  	    	            	  // And set the Sub table
  	    	            	  LIB.safeSetTable(tMaster, sColName, CONST_VALUES.ROW_TO_GET_OR_SET, tSubTable);
  		            	  }
     	            	  
            		    } catch(Exception e ){               
            		    	LIB.log("addTableToMasterTableAfterFirstDeletingAndThenAddingTheColumn", className);
            		    }
            		}
 
                	public static void getLiveDataAndAddToMasterTable(Table tMaster, int iRunNumber, String className) throws OException{ 

                		try {
                			
                		  int iRunDate = OCalendar.today();
    
              			  // Get ComSwap data
              			  {
              				  Table tComSwapData = TOOLSET.COM_SWAP.getComSwapData(iRunDate, className);
               				  
              				  addTableToMasterTableAfterFirstDeletingAndThenAddingTheColumn(tMaster, COLS.REPORT.RAW_BY_UNIQUE_KEY_REPORT_DATA.TOOLSET.COM_SWAP_POSITION,
              						tComSwapData, "ComSwap", className);
              			  } 
  
              			  // Get Commodity data
              			  {
              				  Table tCommodity = TOOLSET.COMMODITY.getCommodityData(iRunDate, className);
              				  
              				  addTableToMasterTableAfterFirstDeletingAndThenAddingTheColumn(tMaster, COLS.REPORT.RAW_BY_UNIQUE_KEY_REPORT_DATA.TOOLSET.COMMODITY_POSITION,
              						tCommodity, "Commodity", className);
              			  } 
               			  
                		} catch (Exception e) {
                			LIB.log("getLiveDataAndAddToMasterTable", e, className);
                		} 
                	} 
                	
                	
                } // LIVE_DATA
                

        		public static void copySettingsToReturnTable(Table tReturn, 
            			Table tLeaseList,  
//            			Table tCurrencyList, int iAllCurrenciesFlag,  
//            			Table tPortfolioList, int iAllPortfoliosFlag,
//            			Table tInsTypeList, int iAllInsTypesFlag, 
            			String sCurrentDataSource,
        			 int iShowAllColumns,
         				int iRunNumber, String className) throws OException{ 
            		try {

            			// For each of the items, below:
                 		// Retain the settings (original parameters for loading trades) here
                 		// make a *copy* of the tables, for extra safety
            		  
            			// CURRENCY LIST
//            			{
//                     		if (Table.isTableValid(tCurrencyList) == 1) {
//                     			Table tCopy = tCurrencyList.copyTable();
//                     			String sTableTitle = "CurrencyList";
//                     			tCopy.setTableName(sTableTitle);
//                     			tCopy.setTableTitle(sTableTitle);
//                     			
//                     			// Need to update the column names to what the Reporting Code would expect and show all columns
//                 				tCopy.colAllShow();
//                     			String sOriginalColName = GUICOLS.PANEL.COMBOBOX.ITEM_ID;
//                     			if (tCopy.getColNum(sOriginalColName) >= 1) {
//                     				tCopy.setColName(sOriginalColName, COLS.CURRENCY_LIST.CURRENCY_ID);
//                     			}
//                     			sOriginalColName = GUICOLS.PANEL.COMBOBOX.ITEM;
//                     			if (tCopy.getColNum(sOriginalColName) >= 1) {
//                     				tCopy.setColName(sOriginalColName, COLS.CURRENCY_LIST.CURRENCY_NAME);
//                     			} 
//                     			
//                         		LIB.safeSetTable(tReturn, COLS.REPORT.RAW_BY_UNIQUE_KEY_REPORT_DATA.SETTING.CURRENCY_LIST, CONST_VALUES.ROW_TO_GET_OR_SET, tCopy);
//                         		LIB.safeSetInt(tReturn, COLS.REPORT.RAW_BY_UNIQUE_KEY_REPORT_DATA.SETTING.NUM_CURRENCIES, CONST_VALUES.ROW_TO_GET_OR_SET, LIB.safeGetNumRows(tCopy));
//                     		}
//                     		LIB.safeSetInt(tReturn, COLS.REPORT.RAW_BY_UNIQUE_KEY_REPORT_DATA.SETTING.ALL_CURRENCIES_FLAG, CONST_VALUES.ROW_TO_GET_OR_SET, iAllCurrenciesFlag);
//            			}   
            			 
            			// Portfolio LIST
//            			{
//                     		if (Table.isTableValid(tPortfolioList) == 1) {
//                     			Table tCopy = tPortfolioList.copyTable();
//                     			String sTableTitle = "PortfolioList";
//                     			tCopy.setTableName(sTableTitle);
//                     			tCopy.setTableTitle(sTableTitle);
//                     			
//                     			// Need to update the column names to what the Reporting Code would expect and show all columns
//                 				tCopy.colAllShow();
//                     			String sOriginalColName = GUICOLS.PANEL.COMBOBOX.ITEM_ID;
//                     			if (tCopy.getColNum(sOriginalColName) >= 1) {
//                     				tCopy.setColName(sOriginalColName, COLS.PORTFOLIO_LIST.PORTFOLIO_ID);
//                     			}
//                     			sOriginalColName = GUICOLS.PANEL.COMBOBOX.ITEM;
//                     			if (tCopy.getColNum(sOriginalColName) >= 1) {
//                     				tCopy.setColName(sOriginalColName, COLS.PORTFOLIO_LIST.PORTFOLIO_NAME);
//                     			} 
//                     			  
//                         		LIB.safeSetTable(tReturn, COLS.REPORT.RAW_BY_UNIQUE_KEY_REPORT_DATA.SETTING.PORTFOLIO_LIST, CONST_VALUES.ROW_TO_GET_OR_SET, tCopy);
//                         		LIB.safeSetInt(tReturn, COLS.REPORT.RAW_BY_UNIQUE_KEY_REPORT_DATA.SETTING.NUM_PORTFOLIOS, CONST_VALUES.ROW_TO_GET_OR_SET, LIB.safeGetNumRows(tCopy));
//                     		}
//                     		LIB.safeSetInt(tReturn, COLS.REPORT.RAW_BY_UNIQUE_KEY_REPORT_DATA.SETTING.ALL_PORTFOLIOS_FLAG, CONST_VALUES.ROW_TO_GET_OR_SET, iAllPortfoliosFlag);
//            			}   
            			
            			// InsType LIST
//            			{
//                     		if (Table.isTableValid(tInsTypeList) == 1) {
//                     			Table tCopy = tInsTypeList.copyTable();
//                     			String sTableTitle = "InsTypeList";
//                     			tCopy.setTableName(sTableTitle);
//                     			tCopy.setTableTitle(sTableTitle);
//                     			
//                     			// Need to update the column names to what the Reporting Code would expect and show all columns
//                 				tCopy.colAllShow();
//                     			String sOriginalColName = GUICOLS.PANEL.COMBOBOX.ITEM_ID;
//                     			if (tCopy.getColNum(sOriginalColName) >= 1) {
//                     				tCopy.setColName(sOriginalColName, COLS.INS_TYPE_LIST.INS_TYPE_ID);
//                     			}
//                     			sOriginalColName = GUICOLS.PANEL.COMBOBOX.ITEM;
//                     			if (tCopy.getColNum(sOriginalColName) >= 1) {
//                     				tCopy.setColName(sOriginalColName, COLS.INS_TYPE_LIST.INS_TYPE_NAME);
//                     			} 
//                     			  
//                         		LIB.safeSetTable(tReturn, COLS.REPORT.RAW_BY_UNIQUE_KEY_REPORT_DATA.SETTING.INS_TYPE_LIST, CONST_VALUES.ROW_TO_GET_OR_SET, tCopy);
//                         		LIB.safeSetInt(tReturn, COLS.REPORT.RAW_BY_UNIQUE_KEY_REPORT_DATA.SETTING.NUM_INS_TYPES, CONST_VALUES.ROW_TO_GET_OR_SET, LIB.safeGetNumRows(tCopy));
//                     		}
//                     		LIB.safeSetInt(tReturn, COLS.REPORT.RAW_BY_UNIQUE_KEY_REPORT_DATA.SETTING.ALL_INS_TYPES_FLAG, CONST_VALUES.ROW_TO_GET_OR_SET, iAllInsTypesFlag);
//            			}   
            			   
                 //		LIB.safeSetString(tReturn, COLS.REPORT.RAW_BY_UNIQUE_KEY_REPORT_DATA.SETTING.CURRENT_DATA_SOURCE, CONST_VALUES.ROW_TO_GET_OR_SET, sCurrentDataSource);
                 		
                 		LIB.safeSetInt(tReturn, COLS.REPORT.RAW_BY_UNIQUE_KEY_REPORT_DATA.SETTING.SHOW_ALL_COLUMNS_FLAG, CONST_VALUES.ROW_TO_GET_OR_SET, iShowAllColumns);
                 		
                 		LIB.safeSetInt(tReturn, COLS.REPORT.RAW_BY_UNIQUE_KEY_REPORT_DATA.SETTING.RUN_NUMBER, CONST_VALUES.ROW_TO_GET_OR_SET, iRunNumber);

            		 
            		} catch (Exception e) {
            			LIB.log("copySettingsToReturnTable", e, className);
            		} 
            	}
            	  
            } // END class HELPER

        	// this is the one function that returns all of the *raw* data.  Generally, this is not shown to the user
        	// this takes parameters as booleans.  It then just calls the other function, the one that takes integers
        	public static Table getTradeDataOneRowPerUniqueKeyAsSubTableWithSettingsToo(Table tSearchByDateType, 
//        			Table tCurrencyList, boolean bAllCurrenciesFlag,  
//        			Table tPortfolioList, boolean bAllPortfoliosFlag,
//        			Table tInsTypeList, boolean bAllInsTypesFlag, 
        			String sCurrentDataSource,
    				 int iShowAllColumns, 
     				int iRunNumber, String className) throws OException{
        		Table tReturn = Util.NULL_TABLE;
        		try {
//        			int iAllCurrenciesFlag = CONST_VALUES.VALUE_OF_FALSE;
//        			if (bAllCurrenciesFlag == true) {
//        				iAllCurrenciesFlag = CONST_VALUES.VALUE_OF_TRUE;
//        			}  
//        			int iAllPortfoliosFlag = CONST_VALUES.VALUE_OF_FALSE;
//        			if (bAllPortfoliosFlag == true) {
//        				iAllPortfoliosFlag = CONST_VALUES.VALUE_OF_TRUE;
//        			} 
//        			int iAllInsTypesFlag = CONST_VALUES.VALUE_OF_FALSE;
//        			if (bAllInsTypesFlag == true) {
//        				iAllInsTypesFlag = CONST_VALUES.VALUE_OF_TRUE;
//        			}  
        			
        			tReturn = getTradeDataWithOneRowPerDealWithSettingsToo(tSearchByDateType,
//                			tCurrencyList, iAllCurrenciesFlag,  
//                			tPortfolioList, iAllPortfoliosFlag,
//                			tInsTypeList, iAllInsTypesFlag, 
        					sCurrentDataSource,
        					 iShowAllColumns,
        					iRunNumber, className);

        		} catch (Exception e) {
        			LIB.log("getTradeDataOneRowPerUniqueKeyAsSubTableWithSettingsToo", e, className);
        		}
        		return tReturn;
        	}

        	// This is only for backwards compatibility
        	public static Table getTradeDataOneRowPerUniqueKeyAsSubTableWithSettingsToo(Table tSearchByDateType, 
        			Table tNotUsed1, boolean bNotUsed1,  
        			Table tNotUsed2, boolean bNotUsed2,
        			Table tNotUsed3, boolean bNotUsed3, 
        			String sCurrentDataSource,
    				 int iShowAllColumns, 
     				int iRunNumber, String className) throws OException{
        		Table tReturn = Util.NULL_TABLE;
        		try {
        			
        			tReturn = getTradeDataWithOneRowPerDealWithSettingsToo(tSearchByDateType,
        					sCurrentDataSource,
        					 iShowAllColumns,
        					iRunNumber, className);

        		} catch (Exception e) {
        			LIB.log("getTradeDataOneRowPerUniqueKeyAsSubTableWithSettingsToo", e, className);
        		}
        		return tReturn;
        	}
        	
        	// this is the one function that returns all of the *raw* data.  Generally, this is not shown to the user
        	public static Table getTradeDataWithOneRowPerDealWithSettingsToo(Table tSearchByDateType, 
//        			Table tCurrencyList, int iAllCurrenciesFlag, 
//        			Table tPortfolioList, int iAllPortfoliosFlag,
//        			Table tInsTypeList, int iAllInsTypesFlag,
        			String sCurrentDataSource,
        			int iShowAllColumns,  
     				int iRunNumber, String className) throws OException{
             	
        		Table tReturn = Util.NULL_TABLE; 
        		try {
        			
        			 // This has data *and* the settings, which are retained for when we call this in the future
        			 tReturn = CREATE_TABLE.createRawMasterReportDataTableAndOneRow(className);
        			 
        			 HELPER.copySettingsToReturnTable(tReturn, tSearchByDateType,
//                 			 tCurrencyList, iAllCurrenciesFlag,  
//                 			 tPortfolioList, iAllPortfoliosFlag,
//                			 tInsTypeList, iAllInsTypesFlag, 
        	        		 sCurrentDataSource,
        						 iShowAllColumns,
        					 iRunNumber, className);
  	 
    				 // Need do to this after the 'copySettingsToReturnTable'
       		//	     String sCurrentDaySourceStringValue = LIB.safeGetString(tReturn, COLS.REPORT.RAW_BY_UNIQUE_KEY_REPORT_DATA.SETTING.CURRENT_DATA_SOURCE, CONST_VALUES.ROW_TO_GET_OR_SET);

       			     boolean bGetLiveData = true;
       			     
//       			     if (Str.iEqual(sCurrentDaySourceStringValue, CONST_VALUES.LIVE_DATA) == CONST_VALUES.VALUE_OF_TRUE) {
//       			    	 bGetLiveData = true;
//       			     }

       			     if (bGetLiveData == true) {
       			    	 HELPER.LIVE_DATA.getLiveDataAndAddToMasterTable(tReturn, iRunNumber, className);
       			     }

       			     if (bGetLiveData == false) {
       			    	 Ask.ok("Unsupported way to get data");
       			     }
         			  
        		 } catch (Exception e) {
    				LIB.log("getMetalTradeDataWithOneRowPerDealWithSettingsToo", e, className);
        		 }
        		   
        		 return tReturn;
             }  
        } // end class GET_RAW_DATA_FOR_REPORTS

        // some of these functions take the 'one row per Unique Key' table (with the source data) 
        // and combine it, for the various trades into raw data reports
        // these are intended to get one more round of updating/massaging before the user sees them, which is done in the GET_FORMATTED_REPORT class
        public static class GET_RAW_REPORT {
         	 
             
        	 public static class TRADE_LIST {
 		
        private static Table createRawReportTable(String className) throws OException{
        	Table tData = Util.NULL_TABLE;
              try{
            	  LIB.log("TRADE_LIST.createRawReportTable: START", className);

            	  tData = Table.tableNew();
            	  LIB.safeAddCol(tData, COLS.REPORT.RAW_REPORT.TRADE_LIST.deal_num, COL_TYPE_ENUM.COL_INT);
            	  LIB.safeAddCol(tData, COLS.REPORT.RAW_REPORT.TRADE_LIST.tran_num, COL_TYPE_ENUM.COL_INT);
            	  LIB.safeAddCol(tData, COLS.REPORT.RAW_REPORT.TRADE_LIST.toolset, COL_TYPE_ENUM.COL_INT);
            	  LIB.safeAddCol(tData, COLS.REPORT.RAW_REPORT.TRADE_LIST.ins_type, COL_TYPE_ENUM.COL_INT);
            	  LIB.safeAddCol(tData, COLS.REPORT.RAW_REPORT.TRADE_LIST.base_ins_type, COL_TYPE_ENUM.COL_INT);
            	  LIB.safeAddCol(tData, COLS.REPORT.RAW_REPORT.TRADE_LIST.trade_date, COL_TYPE_ENUM.COL_INT);
            	  
            	  LIB.safeAddCol(tData, COLS.REPORT.RAW_REPORT.TRADE_LIST.ins_num, COL_TYPE_ENUM.COL_INT);
            	  LIB.safeAddCol(tData, COLS.REPORT.RAW_REPORT.TRADE_LIST.counterparty, COL_TYPE_ENUM.COL_STRING);
            	  
            	  LIB.safeAddCol(tData, COLS.REPORT.RAW_REPORT.TRADE_LIST.param_seq_num, COL_TYPE_ENUM.COL_INT);
            	  LIB.safeAddCol(tData, COLS.REPORT.RAW_REPORT.TRADE_LIST.buy_sell, COL_TYPE_ENUM.COL_INT);
            	  LIB.safeAddCol(tData, COLS.REPORT.RAW_REPORT.TRADE_LIST.deal_price_string, COL_TYPE_ENUM.COL_STRING);

            	  LIB.safeAddCol(tData, COLS.REPORT.RAW_REPORT.TRADE_LIST.proj_index_int, COL_TYPE_ENUM.COL_INT);

            	  // add proj_index as a string
            	  LIB.safeAddCol(tData, COLS.REPORT.RAW_REPORT.TRADE_LIST.proj_index, COL_TYPE_ENUM.COL_STRING);
            	  // add index_location as a string
            	  LIB.safeAddCol(tData, COLS.REPORT.RAW_REPORT.TRADE_LIST.index_location, COL_TYPE_ENUM.COL_STRING);

            	  LIB.safeAddCol(tData, COLS.REPORT.RAW_REPORT.TRADE_LIST.price, COL_TYPE_ENUM.COL_DOUBLE);
            	  LIB.safeAddCol(tData, COLS.REPORT.RAW_REPORT.TRADE_LIST.float_spd, COL_TYPE_ENUM.COL_DOUBLE);
            	  LIB.safeAddCol(tData, COLS.REPORT.RAW_REPORT.TRADE_LIST.index_percent, COL_TYPE_ENUM.COL_DOUBLE);
            	  LIB.safeAddCol(tData, COLS.REPORT.RAW_REPORT.TRADE_LIST.quantity, COL_TYPE_ENUM.COL_DOUBLE);

            	  LIB.safeAddCol(tData, COLS.REPORT.RAW_REPORT.TRADE_LIST.quantity, COL_TYPE_ENUM.COL_DOUBLE);
            	  LIB.safeAddCol(tData, COLS.REPORT.RAW_REPORT.TRADE_LIST.total_quantity, COL_TYPE_ENUM.COL_DOUBLE);
 
            	  LIB.safeAddCol(tData, COLS.REPORT.RAW_REPORT.TRADE_LIST.start_date, COL_TYPE_ENUM.COL_INT);
            	  LIB.safeAddCol(tData, COLS.REPORT.RAW_REPORT.TRADE_LIST.end_date, COL_TYPE_ENUM.COL_INT);

            	  LIB.safeAddCol(tData, COLS.REPORT.RAW_REPORT.TRADE_LIST.fx_flt, COL_TYPE_ENUM.COL_INT);
            	  LIB.safeAddCol(tData, COLS.REPORT.RAW_REPORT.TRADE_LIST.currency, COL_TYPE_ENUM.COL_INT);

            	  // add as a string
            	  LIB.safeAddCol(tData, COLS.REPORT.RAW_REPORT.TRADE_LIST.pipeline, COL_TYPE_ENUM.COL_STRING);
            	  LIB.safeAddCol(tData, COLS.REPORT.RAW_REPORT.TRADE_LIST.location, COL_TYPE_ENUM.COL_STRING);
            	  LIB.safeAddCol(tData, COLS.REPORT.RAW_REPORT.TRADE_LIST.location_string, COL_TYPE_ENUM.COL_STRING);
            	  
            	  
            	  
            	  
            	  // Reference Data Formatting
            	  LIB.safeSetColFormatAsRef(tData, COLS.REPORT.RAW_REPORT.TRADE_LIST.toolset, SHM_USR_TABLES_ENUM.TOOLSETS_TABLE); 
            	  LIB.safeSetColFormatAsRef(tData, COLS.REPORT.RAW_REPORT.TRADE_LIST.ins_type, SHM_USR_TABLES_ENUM.INSTRUMENTS_TABLE); 
            	  LIB.safeSetColFormatAsRef(tData, COLS.REPORT.RAW_REPORT.TRADE_LIST.base_ins_type, SHM_USR_TABLES_ENUM.INSTRUMENTS_TABLE); 
            	  LIB.safeSetColFormatAsRef(tData, COLS.REPORT.RAW_REPORT.TRADE_LIST.buy_sell, SHM_USR_TABLES_ENUM.BUY_SELL_TABLE); 
            	  LIB.safeSetColFormatAsRef(tData, COLS.REPORT.RAW_REPORT.TRADE_LIST.proj_index_int, SHM_USR_TABLES_ENUM.INDEX_TABLE); 
            //	  LIB.safeSetColFormatAsRef(tData, COLS.REPORT.RAW_REPORT.TRADE_LIST.index_location, SHM_USR_TABLES_ENUM.INDEX_LOCATION_TABLE); 
            
            	  // Dates
            	  LIB.safeSetColFormatAsDate(tData, COLS.REPORT.RAW_REPORT.TRADE_LIST.trade_date);
            	  LIB.safeSetColFormatAsDate(tData, COLS.REPORT.RAW_REPORT.TRADE_LIST.start_date);
            	  LIB.safeSetColFormatAsDate(tData, COLS.REPORT.RAW_REPORT.TRADE_LIST.end_date);
            	   
            	  
              } catch(Exception e ){               
            	  LIB.log("TRADE_LIST.createRawReportTable", e, className);
              } 
              return tData;
        } 
        
        public static Table getRawReport(Table tTradeDataOneRowPerUniqueKeyAsSubTableWithSettingsToo, REPORT_NAME reportName,
 				int iRunNumber, String className) throws OException{
         	
    		 Table tReturn = Util.NULL_TABLE;
    		 Table tAbTran  = Util.NULL_TABLE;
//    		 Table tDistinctLocationIDs  = Util.NULL_TABLE;
//    		 Table tCommInvCriteria  = Util.NULL_TABLE;
    		 
    	//	 Table tCommInvLedger  = Util.NULL_TABLE;
    		 try {
    			 
    			 tReturn = createRawReportTable(className);
    			 
    			 // We may be loading one extra day on each side, to help with certain reports
    			 // So make sure to only get for this report items in the user-requested date range
     			// int iStartDateForFilteringReportOutput = LIB.safeGetInt(tTradeDataOneRowPerUniqueKeyAsSubTableWithSettingsToo, COLS.REPORT.RAW_BY_UNIQUE_KEY_REPORT_DATA.SETTING.START_DATE, CONST_VALUES.ROW_TO_GET_OR_SET);
     			// int iEndDateFilteringReportOutput =  LIB.safeGetInt(tTradeDataOneRowPerUniqueKeyAsSubTableWithSettingsToo, COLS.REPORT.RAW_BY_UNIQUE_KEY_REPORT_DATA.SETTING.END_DATE, CONST_VALUES.ROW_TO_GET_OR_SET);
     			
    			 //Table tCurrentData = LIB.safeGetTable(tTradeDataOneRowPerUniqueKeyAsSubTableWithSettingsToo, COLS.REPORT.RAW_BY_UNIQUE_KEY_REPORT_DATA.CURRENT_DATA, CONST_VALUES.ROW_TO_GET_OR_SET);
    		 	        			 
   		//	 Table tSummaryPositionData = LIB.safeGetTable(tTradeDataOneRowPerUniqueKeyAsSubTableWithSettingsToo, COLS.REPORT.RAW_BY_UNIQUE_KEY_REPORT_DATA.CURRENT_DATA_SUMMARIZED, CONST_VALUES.ROW_TO_GET_OR_SET);
    		//	 Table tSummaryPositionData = LIB.safeGetTable(tTradeDataOneRowPerUniqueKeyAsSubTableWithSettingsToo, COLS.REPORT.RAW_BY_UNIQUE_KEY_REPORT_DATA.CURRENT_DATA_SUMMARIZED_AND_FILTERED, CONST_VALUES.ROW_TO_GET_OR_SET);
    		//	 int iCurrentDataPositionDate = LIB.safeGetInt(tTradeDataOneRowPerUniqueKeyAsSubTableWithSettingsToo, COLS.REPORT.RAW_BY_UNIQUE_KEY_REPORT_DATA.CURRENT_DATA_POSITION_DATE, CONST_VALUES.ROW_TO_GET_OR_SET);


    			 
    			 
    			 tAbTran = Table.tableNew();
    			 LIB.safeAddCol(tAbTran, COLS.REPORT.RAW_REPORT.TRADE_LIST.deal_num, COL_TYPE_ENUM.COL_INT);
    			 LIB.safeAddCol(tAbTran, COLS.REPORT.RAW_REPORT.TRADE_LIST.tran_num, COL_TYPE_ENUM.COL_INT);
    			 LIB.safeAddCol(tAbTran, COLS.REPORT.RAW_REPORT.TRADE_LIST.ins_num, COL_TYPE_ENUM.COL_INT);
    			 LIB.safeAddCol(tAbTran, COLS.REPORT.RAW_REPORT.TRADE_LIST.toolset, COL_TYPE_ENUM.COL_INT);
    			 LIB.safeAddCol(tAbTran, COLS.REPORT.RAW_REPORT.TRADE_LIST.ins_type, COL_TYPE_ENUM.COL_INT);
    			 LIB.safeAddCol(tAbTran, COLS.REPORT.RAW_REPORT.TRADE_LIST.base_ins_type, COL_TYPE_ENUM.COL_INT);
    			 LIB.safeAddCol(tAbTran, COLS.REPORT.RAW_REPORT.TRADE_LIST.trade_date, COL_TYPE_ENUM.COL_INT);
    			 LIB.safeAddCol(tAbTran, COLS.REPORT.RAW_REPORT.TRADE_LIST.buy_sell, COL_TYPE_ENUM.COL_INT);
    			 LIB.safeAddCol(tAbTran, COLS.REPORT.RAW_REPORT.TRADE_LIST.counterparty, COL_TYPE_ENUM.COL_INT);

    		 
    			 String sSQL = "select deal_tracking_num, tran_num, ins_num, toolset, ins_type, base_ins_type, trade_date, buy_sell, external_bunit FROM ab_tran WHERE tran_status = 3 AND trade_flag = 1";
    			 LIB.execISql(tAbTran, sSQL, false, className);
    			 
    			 {
    				 // Formatting is functional
    				 LIB.safeSetColFormatAsRef(tAbTran, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.counterparty, SHM_USR_TABLES_ENUM.PARTY_TABLE); 
    				 
    				 // This converts counterparty from an Int to a String
    				 String sWhat = COLS.REPORT.RAW_REPORT.TRADE_LIST.deal_num + ", " +
    						 COLS.REPORT.RAW_REPORT.TRADE_LIST.tran_num + ", " +
    						 COLS.REPORT.RAW_REPORT.TRADE_LIST.ins_num + ", " +
    						 COLS.REPORT.RAW_REPORT.TRADE_LIST.toolset + ", " +
    						 COLS.REPORT.RAW_REPORT.TRADE_LIST.ins_type + ", " +
    						 COLS.REPORT.RAW_REPORT.TRADE_LIST.base_ins_type + ", " +
    						 COLS.REPORT.RAW_REPORT.TRADE_LIST.trade_date + ", " +
    						 COLS.REPORT.RAW_REPORT.TRADE_LIST.buy_sell + ", " +
    						 COLS.REPORT.RAW_REPORT.TRADE_LIST.counterparty;
    				 String sWhere = COLS.REPORT.RAW_REPORT.TRADE_LIST.deal_num + " GT 0";
    				 LIB.select(tReturn, tAbTran, sWhat, sWhere, false, className);
    			 }
    			 

    			 // Get Parameter data
    			 {
    				 Table tInsNums =  Table.tableNew();
    				 LIB.safeAddCol(tInsNums, "ins_num", COL_TYPE_ENUM.COL_INT);

    				 tReturn.copyColDistinct(COLS.REPORT.RAW_REPORT.TRADE_LIST.ins_num, tInsNums, "ins_num");
    				 
    				 Table tParameter = Table.tableNew();
    				 LIB.safeAddCol(tParameter, COLS.REPORT.RAW_REPORT.TRADE_LIST.ins_num, COL_TYPE_ENUM.COL_INT);
    				 LIB.safeAddCol(tParameter, COLS.REPORT.RAW_REPORT.TRADE_LIST.param_seq_num, COL_TYPE_ENUM.COL_INT);
    				 LIB.safeAddCol(tParameter, COLS.REPORT.RAW_REPORT.TRADE_LIST.proj_index_int, COL_TYPE_ENUM.COL_INT);
    				 LIB.safeAddCol(tParameter, COLS.REPORT.RAW_REPORT.TRADE_LIST.proj_index, COL_TYPE_ENUM.COL_INT);
    				 LIB.safeAddCol(tParameter, COLS.REPORT.RAW_REPORT.TRADE_LIST.index_location, COL_TYPE_ENUM.COL_INT);
    				 LIB.safeAddCol(tParameter, COLS.REPORT.RAW_REPORT.TRADE_LIST.currency, COL_TYPE_ENUM.COL_INT);
    				 LIB.safeAddCol(tParameter, COLS.REPORT.RAW_REPORT.TRADE_LIST.fx_flt, COL_TYPE_ENUM.COL_INT);
    				 LIB.safeAddCol(tParameter, COLS.REPORT.RAW_REPORT.TRADE_LIST.start_date, COL_TYPE_ENUM.COL_INT);
    				 LIB.safeAddCol(tParameter, COLS.REPORT.RAW_REPORT.TRADE_LIST.end_date, COL_TYPE_ENUM.COL_INT);
    				 
    				 LIB.safeAddCol(tParameter, COLS.REPORT.RAW_REPORT.TRADE_LIST.quantity, COL_TYPE_ENUM.COL_DOUBLE);
    				 LIB.safeAddCol(tParameter, COLS.REPORT.RAW_REPORT.TRADE_LIST.price, COL_TYPE_ENUM.COL_DOUBLE);
    				 LIB.safeAddCol(tParameter, COLS.REPORT.RAW_REPORT.TRADE_LIST.float_spd, COL_TYPE_ENUM.COL_DOUBLE);
    				 LIB.safeAddCol(tParameter, COLS.REPORT.RAW_REPORT.TRADE_LIST.index_percent, COL_TYPE_ENUM.COL_DOUBLE);

    				 String sWhat = "ins_num, param_seq_num, proj_index, proj_index, index_loc_id, currency, fx_flt, start_date, mat_date, notnl, rate, float_spd, index_multiplier";
    				 String sWhere = "1 = 1";
    				 LIB.loadFromDbWithWhatWhere(tParameter, "parameter", tInsNums, sWhat, sWhere, false, className);
	    				 
    				 // This is functional, needed for column conversion
    				 LIB.safeSetColFormatAsRef(tParameter, COLS.REPORT.RAW_REPORT.TRADE_LIST.proj_index, SHM_USR_TABLES_ENUM.INDEX_TABLE);
    				 LIB.safeSetColFormatAsRef(tParameter, COLS.REPORT.RAW_REPORT.TRADE_LIST.index_location, SHM_USR_TABLES_ENUM.INDEX_LOCATION_TABLE);
    				 
    				 // Add ins type and toolset to the tParameter table.. we'll need it for the joins
    				 {
	    				 LIB.safeAddCol(tParameter, COLS.REPORT.RAW_REPORT.TRADE_LIST.ins_type, COL_TYPE_ENUM.COL_INT);
	    				 LIB.safeAddCol(tParameter, COLS.REPORT.RAW_REPORT.TRADE_LIST.toolset, COL_TYPE_ENUM.COL_INT);
	    				 
	    				 sWhat = COLS.REPORT.RAW_REPORT.TRADE_LIST.ins_type + ", " + 
	    						 COLS.REPORT.RAW_REPORT.TRADE_LIST.toolset;
	    				 sWhere = COLS.REPORT.RAW_REPORT.TRADE_LIST.ins_num + " EQ $" + COLS.REPORT.RAW_REPORT.TRADE_LIST.ins_num;
	    				 LIB.select(tParameter, tReturn, sWhat, sWhere, false, className);
    				 }

    				 // Commodity Toolset
    				 {
	    				 sWhat = COLS.REPORT.RAW_REPORT.TRADE_LIST.param_seq_num + ", " +
	    						 COLS.REPORT.RAW_REPORT.TRADE_LIST.start_date + ", " +
	    						 COLS.REPORT.RAW_REPORT.TRADE_LIST.end_date + ", " +
	    						 COLS.REPORT.RAW_REPORT.TRADE_LIST.quantity;
	    				 sWhere = COLS.REPORT.RAW_REPORT.TRADE_LIST.ins_num + " EQ $" + COLS.REPORT.RAW_REPORT.TRADE_LIST.ins_num + " AND " + 
	    						 COLS.REPORT.RAW_REPORT.TRADE_LIST.toolset + " EQ " + TOOLSETS.COMMODITY + " AND " + 
	    						 COLS.REPORT.RAW_REPORT.TRADE_LIST.param_seq_num + " EQ 1";
	    				 LIB.select(tReturn, tParameter, sWhat, sWhere, false, className);
	    				 
	    				 sWhat = COLS.REPORT.RAW_REPORT.TRADE_LIST.fx_flt + ", " +
	    						 COLS.REPORT.RAW_REPORT.TRADE_LIST.proj_index_int + ", " +
	    						 COLS.REPORT.RAW_REPORT.TRADE_LIST.proj_index + ", " +
	    						 COLS.REPORT.RAW_REPORT.TRADE_LIST.index_location + ", " +
	    						 COLS.REPORT.RAW_REPORT.TRADE_LIST.index_percent + ", " +
	    						 COLS.REPORT.RAW_REPORT.TRADE_LIST.float_spd + ", " +
	    						 COLS.REPORT.RAW_REPORT.TRADE_LIST.price;
	    				 sWhere = COLS.REPORT.RAW_REPORT.TRADE_LIST.ins_num + " EQ $" + COLS.REPORT.RAW_REPORT.TRADE_LIST.ins_num + " AND " + 
	    						 COLS.REPORT.RAW_REPORT.TRADE_LIST.toolset + " EQ " + TOOLSETS.COMMODITY + " AND " + 
	    						 COLS.REPORT.RAW_REPORT.TRADE_LIST.param_seq_num + " EQ 2";
	    				 LIB.select(tReturn, tParameter, sWhat, sWhere, false, className);

    				 }


    				 // ComSwap Toolset
    				 {
	    				 sWhat = COLS.REPORT.RAW_REPORT.TRADE_LIST.param_seq_num + ", " +
	    						 COLS.REPORT.RAW_REPORT.TRADE_LIST.start_date + ", " +
	    						 COLS.REPORT.RAW_REPORT.TRADE_LIST.end_date + ", " +
	    						 COLS.REPORT.RAW_REPORT.TRADE_LIST.quantity;
 	    				 sWhere = COLS.REPORT.RAW_REPORT.TRADE_LIST.ins_num + " EQ $" + COLS.REPORT.RAW_REPORT.TRADE_LIST.ins_num + " AND " + 
	    						 COLS.REPORT.RAW_REPORT.TRADE_LIST.toolset + " EQ " + TOOLSETS.COM_SWAP + " AND " + 
	    						 COLS.REPORT.RAW_REPORT.TRADE_LIST.param_seq_num + " EQ 0";
	    				 LIB.select(tReturn, tParameter, sWhat, sWhere, false, className);

	    				 sWhat = COLS.REPORT.RAW_REPORT.TRADE_LIST.fx_flt + ", " +
	    						 COLS.REPORT.RAW_REPORT.TRADE_LIST.proj_index_int + ", " +
	    						 COLS.REPORT.RAW_REPORT.TRADE_LIST.proj_index + ", " +
	    						 COLS.REPORT.RAW_REPORT.TRADE_LIST.index_location + ", " +
	    						 COLS.REPORT.RAW_REPORT.TRADE_LIST.index_percent + ", " +
	    						 COLS.REPORT.RAW_REPORT.TRADE_LIST.float_spd + ", " +
	    						 COLS.REPORT.RAW_REPORT.TRADE_LIST.price;
	    				 sWhere = COLS.REPORT.RAW_REPORT.TRADE_LIST.ins_num + " EQ $" + COLS.REPORT.RAW_REPORT.TRADE_LIST.ins_num + " AND " + 
	    						 COLS.REPORT.RAW_REPORT.TRADE_LIST.toolset + " EQ " + TOOLSETS.COM_SWAP + " AND " + 
	    						 COLS.REPORT.RAW_REPORT.TRADE_LIST.param_seq_num + " EQ 1";
	    				 LIB.select(tReturn, tParameter, sWhat, sWhere, false, className);

    				 }
    				 
    				 Table tProfile = Table.tableNew();
    				 {
    					 LIB.safeAddCol(tProfile, COLS.REPORT.RAW_REPORT.TRADE_LIST.ins_num, COL_TYPE_ENUM.COL_INT);
    					 LIB.safeAddCol(tProfile, COLS.REPORT.RAW_REPORT.TRADE_LIST.param_seq_num, COL_TYPE_ENUM.COL_INT);
	    				 LIB.safeAddCol(tProfile, COLS.REPORT.RAW_REPORT.TRADE_LIST.total_quantity, COL_TYPE_ENUM.COL_DOUBLE);

	    				 sWhat = "ins_num, param_seq_num, sum(notnl) ohd_quanity";
	    				 sWhere = "1 = 1 GROUP BY ins_num, param_seq_num";
	    				 LIB.loadFromDbWithWhatWhere(tProfile, "profile", tInsNums, sWhat, sWhere, false, className);
 	    				 
	    				 // For this report, show the absolute value 
	    				 tProfile.mathABSCol(COLS.REPORT.RAW_REPORT.TRADE_LIST.total_quantity);
	    				 
	    				 sWhat = COLS.REPORT.RAW_REPORT.TRADE_LIST.total_quantity;
	    				 sWhere = COLS.REPORT.RAW_REPORT.TRADE_LIST.ins_num + " EQ $" + COLS.REPORT.RAW_REPORT.TRADE_LIST.ins_num + " AND " + 
	    						 COLS.REPORT.RAW_REPORT.TRADE_LIST.param_seq_num + " EQ $" + COLS.REPORT.RAW_REPORT.TRADE_LIST.param_seq_num;
	    				 LIB.select(tReturn, tProfile, sWhat, sWhere, false, className);
	    				 
	    				 // TOxDO
	    				 //LIB.viewTable(tProfile, "profile sgddfd");
	    				 
	    				 tProfile = LIB.destroyTable(tProfile);
	    			
    				 }
    				 
    				 {
    					 Table tGasPhysParam = Table.tableNew();
        	    			
    					 LIB.safeAddCol(tGasPhysParam, COLS.REPORT.RAW_REPORT.TRADE_LIST.ins_num, COL_TYPE_ENUM.COL_INT);
    					 LIB.safeAddCol(tGasPhysParam, COLS.REPORT.RAW_REPORT.TRADE_LIST.param_seq_num, COL_TYPE_ENUM.COL_INT);
	    				 LIB.safeAddCol(tGasPhysParam, COLS.REPORT.RAW_REPORT.TRADE_LIST.pipeline, COL_TYPE_ENUM.COL_INT);
	    				 LIB.safeAddCol(tGasPhysParam, COLS.REPORT.RAW_REPORT.TRADE_LIST.location, COL_TYPE_ENUM.COL_INT);

	    				 sWhat = "ins_num, param_seq_num, pipeline_id, location_id";
	    				 sWhere = "1 = 1";
	    				 LIB.loadFromDbWithWhatWhere(tGasPhysParam, "gas_phys_param_view", tInsNums, sWhat, sWhere, false, className);
 	    				 
	    				 // Formatting is functional for string conversion
	    				 LIB.safeSetColFormatAsRef(tGasPhysParam, COLS.REPORT.RAW_REPORT.TRADE_LIST.pipeline, SHM_USR_TABLES_ENUM.GAS_PHYS_PIPELINE_TABLE);
	    				 LIB.safeSetColFormatAsRef(tGasPhysParam, COLS.REPORT.RAW_REPORT.TRADE_LIST.location, SHM_USR_TABLES_ENUM.GAS_PHYS_LOCATION_TABLE);

	    				 
	    				 sWhat = COLS.REPORT.RAW_REPORT.TRADE_LIST.pipeline + ", " + 
	    						 COLS.REPORT.RAW_REPORT.TRADE_LIST.location;
	    				 sWhere = COLS.REPORT.RAW_REPORT.TRADE_LIST.ins_num + " EQ $" + COLS.REPORT.RAW_REPORT.TRADE_LIST.ins_num + " AND " + 
	    						 COLS.REPORT.RAW_REPORT.TRADE_LIST.param_seq_num + " EQ $" + COLS.REPORT.RAW_REPORT.TRADE_LIST.param_seq_num;
	    				 LIB.select(tReturn, tGasPhysParam, sWhat, sWhere, false, className);
	    				 
	    				 // TOxDO
	    				 //LIB.viewTable(tProfile, "profile sgddfd");
	    				 
	    				 tGasPhysParam = LIB.destroyTable(tGasPhysParam);
	    			
    				 }

    				 
    				 // TODO
    			//  LIB.viewTable(tParameter, "parameter estegsd dd");
    				  
//    				 sWhat = COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.external_deal_ref;
//    				 sWhere = COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.tran_num + " EQ $" + COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.tran_num;
//    				 LIB.select(tReturn, tParameter, sWhat, sWhere, false, className);
    				 
    				 
    				 // TOxDO
    				// LIB.viewTable(tTranInfo, "tran tTranInfo tests");
    				 
    				 
    				 tInsNums = LIB.destroyTable(tInsNums);
    				 tParameter = LIB.destroyTable(tParameter);
    				 tProfile = LIB.destroyTable(tProfile);
    			 }
    			 
    			 int xdsdvsd = 3;
    			 
//    			 // Get Alternate Index Location
//    			 {
//    				 Table tIndexLocation = Table.tableNew();
//    				 LIB.safeAddCol(tIndexLocation, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.proj_index_int, COL_TYPE_ENUM.COL_INT);
//    				 LIB.safeAddCol(tIndexLocation, "index_location2", COL_TYPE_ENUM.COL_STRING);
//    				 
//    				 LIB.execISql(tIndexLocation, "select index_id, index_loc_name from index_location", false, className);
//    				 
//    				 // This is functional to remove duplicates
//    				 tIndexLocation.group(COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.proj_index_int);
//    				 tIndexLocation.distinctRows();
//
//    				 LIB.safeAddCol(tReturn, "index_location2", COL_TYPE_ENUM.COL_STRING);
//    				 String sWhat = "index_location2";
//    				 String sWhere =COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.proj_index_int + " EQ $" + COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.proj_index_int;
//    				 LIB.select(tReturn, tIndexLocation, sWhat, sWhere, false, className);
//    				 
//    				 // TOxDO
//    				 //LIB.viewTable(tIndexLocation, " tIndexLocation sfgsdgff");
//    				 
//    				 tIndexLocation = LIB.destroyTable(tIndexLocation);
//    			 }
    			 
    			  

    			 // Populate Ins Type and Deal Type
    			 {
    				 int FIXED = 0;
    				 int FLOAT = 1;
    				 int iNumRows = LIB.safeGetNumRows(tReturn);
    				 for (int iCounter = 1; iCounter <= iNumRows; iCounter++) {
    					 
//    					 String sPipeline = LIB.safeGetString(tReturn, COLS.REPORT.RAW_REPORT.TRADE_LIST.pipeline, iCounter);
    					 String sLocation = LIB.safeGetString(tReturn, COLS.REPORT.RAW_REPORT.TRADE_LIST.location, iCounter);
//      	    					 String sLocationString = sPipeline + "/" + sLocation;
    					 LIB.safeSetString(tReturn,  COLS.REPORT.RAW_REPORT.TRADE_LIST.location_string, iCounter, sLocation);
 	    				

    					 
    					 int iFxFlt = LIB.safeGetInt(tReturn, COLS.REPORT.RAW_REPORT.TRADE_LIST.fx_flt, iCounter);
    					 
//    					 String sInsType = "Unknown";
//    					 String sDealType = "Unknown";
//    					 int iToolset = LIB.safeGetInt(tReturn, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.toolset, iCounter);
//    					 
//    					 if (iToolset == TOOLSETS.COMMODITY) {
//    						 sInsType = "Physical";
//	    					 sDealType = "Fixed";
//	    					 
//	    					 if (iFxFlt == FLOAT) {
//    	    					 sDealType = "Index";
//	    					 }
//    					 }
//    					 
//    					 LIB.safeSetString(tReturn,  COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.quorum_ins_type, iCounter, sInsType);
//    					 LIB.safeSetString(tReturn,  COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.quorum_deal_type, iCounter, sDealType);
    					 
    					 if (iFxFlt == FLOAT) {
    						 String sProjIndex = LIB.safeGetString(tReturn, COLS.REPORT.RAW_REPORT.TRADE_LIST.proj_index, iCounter);
//    						 String sIndexLocOnDeal = LIB.safeGetString(tReturn, COLS.REPORT.RAW_REPORT.TRADE_LIST.index_location, iCounter);
//    						 String sIndexLocCalced = LIB.safeGetString(tReturn, "index_location2", iCounter);
//	    					 
//    						 String sIndex = sIndexLocOnDeal;
//    						 if (Str.len(sIndex) <= 2) {
//    							 sIndex = sIndexLocCalced;
//    						 }
//    						 if (Str.len(sIndex) <= 2) {
//    							 sIndex = sProjIndex;
//    						 }
    						 
    						 
	    					 double dPrice = LIB.safeGetDouble(tReturn, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.price, iCounter);
	    					 double dFloatSpd = LIB.safeGetDouble(tReturn, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.float_spd, iCounter);
	    					 dPrice = dPrice + dFloatSpd;
	    					 LIB.safeSetDouble(tReturn, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.price, iCounter, dPrice);
	    					 
	    					 String sPrice = Str.doubleToStr(dPrice);
	    					// sPrice = "0.20";

	    					 // TODO, replace the 1x with the real thing
	    					 String sDealPriceString = sProjIndex + " " + "1x + " + sPrice;
	    					 LIB.safeSetString(tReturn, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.deal_price_string, iCounter, sDealPriceString);
    					 }

    					 
    					 if (iFxFlt == FIXED) {
    						 // zero or blank out items
	    					 LIB.safeSetString(tReturn, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.proj_index, iCounter, "");
	    					 LIB.safeSetString(tReturn, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.index_location, iCounter, "");
	    					 LIB.safeSetInt(tReturn, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.proj_index_int, iCounter, 1);
	    					 LIB.safeSetDouble(tReturn, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.index_percent, iCounter, 1);
	    					 	
	    					 double dPrice = LIB.safeGetDouble(tReturn, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.price, iCounter);
	    					 String sPrice = Str.doubleToStr(dPrice);
	    					 LIB.safeSetString(tReturn, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.deal_price_string, iCounter, sPrice);
    					 }
    					 
    					 

    				 }
    				 
    			 }

    			 
    			 
    			 
//    			 tGasPhysLocation = Table.tableNew();
//    			 LIB.safeAddCol(tGasPhysLocation, "location_id", COL_TYPE_ENUM.COL_INT);
//    			 LIB.safeAddCol(tGasPhysLocation, "location_name", COL_TYPE_ENUM.COL_STRING);
//    			 LIB.safeAddCol(tGasPhysLocation, "zone_id", COL_TYPE_ENUM.COL_INT);
//    			 LIB.safeAddCol(tGasPhysLocation, "pipeline_id", COL_TYPE_ENUM.COL_INT);
//    			 LIB.safeAddCol(tGasPhysLocation, "idx_subgroup", COL_TYPE_ENUM.COL_INT);
//    			 {
//    				 // 40009 is Emissions
//        			 String sSQL = "select location_id, location_name, zone_id, pipeline_id, idx_subgroup from gas_phys_location WHERE pipeline_id = 40009";
//        			 LIB.execISql(tGasPhysLocation, sSQL, bDebugLogging, className);
//    			 }
//    			 
//    			 LIB.safeAddCol(tGasPhysLocation, "year", COL_TYPE_ENUM.COL_INT);
//    			 LIB.safeAddCol(tGasPhysLocation, "is_pre_registry_flag", COL_TYPE_ENUM.COL_INT);
//    			 LIB.safeAddCol(tGasPhysLocation, "is_in_registry_flag", COL_TYPE_ENUM.COL_INT);
//    			 LIB.safeAddCol(tGasPhysLocation, "is_generic_flag", COL_TYPE_ENUM.COL_INT);
//    			 LIB.safeSetColFormatAsRef(tGasPhysLocation, "is_pre_registry_flag", SHM_USR_TABLES_ENUM.NO_YES_TABLE); 
//    			 LIB.safeSetColFormatAsRef(tGasPhysLocation, "is_in_registry_flag", SHM_USR_TABLES_ENUM.NO_YES_TABLE); 
//    			 LIB.safeSetColFormatAsRef(tGasPhysLocation, "is_generic_flag", SHM_USR_TABLES_ENUM.NO_YES_TABLE); 
//    			 
//
//    			 {
//    				 Map<String, EMISSION_LOCATION> locationDictionary = getLocationDictionary(className);
//    				 
//    				 int iNumRows = LIB.safeGetNumRows(tGasPhysLocation);
//    				 for (int iCounter = 1; iCounter <= iNumRows; iCounter++) {
//    					 String sName = LIB.safeGetString(tGasPhysLocation, "location_name", iCounter);
//    					 try {
//    						 EMISSION_LOCATION location = locationDictionary.get(sName);
//    						 LIB.safeSetInt(tGasPhysLocation, "year", iCounter, location.getYear());
//    						 LIB.safeSetInt(tGasPhysLocation, "is_pre_registry_flag", iCounter, location.getPreRegistryFlagAsInt());
//    						 LIB.safeSetInt(tGasPhysLocation, "is_in_registry_flag", iCounter, location.getInRegistryFlagAsInt());
//    						 LIB.safeSetInt(tGasPhysLocation, "is_generic_flag", iCounter, location.getIsGenericFlagAsInt());
//    					 } catch (Exception e) {
//    						 // don't log this
//    					 }
//    				 }
//    			 }
// 
//    			 
//    			 tDistinctLocationIDs = Table.tableNew();
//    			 LIB.safeAddCol(tDistinctLocationIDs, "location_id", COL_TYPE_ENUM.COL_INT);
//    			 tGasPhysLocation.copyColDistinct("location_id", tDistinctLocationIDs, "location_id");
//
//    			 // get a distinct list by cic_id, internal_portfolio and by location
//    			 tCommInvCriteria = Table.tableNew();
//    			 LIB.safeAddCol(tCommInvCriteria, "cic_id", COL_TYPE_ENUM.COL_INT);
//    			 LIB.safeAddCol(tCommInvCriteria, "internal_portfolio", COL_TYPE_ENUM.COL_INT);
//    			 LIB.safeAddCol(tCommInvCriteria, "location_id", COL_TYPE_ENUM.COL_INT);
//    			 tCommInvCriteria.defaultFormat();
//    			 
//    			 {
//    				 String sWhat = "cic_id, internal_portfolio, location_id";
//    				 String sWhere = "1=1";
//    				 LIB.loadFromDbWithWhatWhere(tCommInvCriteria, "comm_inv_criteria", tDistinctLocationIDs, sWhat, sWhere, bDebugLogging, className);
//    			 }
//
//    			 Table tCiCDistinctList = Table.tableNew();
//    			 LIB.safeAddCol(tCiCDistinctList, "cic_id", COL_TYPE_ENUM.COL_INT);
//
//    			 tCommInvCriteria.copyColDistinct("cic_id", tCiCDistinctList, "cic_id");
//
//    			 tCommInvLedger = Table.tableNew();
//    			 LIB.safeAddCol(tCommInvLedger, "cic_id", COL_TYPE_ENUM.COL_INT);
//    			 LIB.safeAddCol(tCommInvLedger, "deal_num", COL_TYPE_ENUM.COL_INT);
//    			 LIB.safeAddCol(tCommInvLedger, "tran_num", COL_TYPE_ENUM.COL_INT);
//    			 LIB.safeAddCol(tCommInvLedger, "param_seq_num", COL_TYPE_ENUM.COL_INT);
//    			 LIB.safeAddCol(tCommInvLedger, "profile_seq_num", COL_TYPE_ENUM.COL_INT);
//    			 // do this as a date
//    			 LIB.safeAddCol(tCommInvLedger, "flow_date", COL_TYPE_ENUM.COL_INT);
//    			 LIB.safeAddCol(tCommInvLedger, "volume", COL_TYPE_ENUM.COL_DOUBLE);
//    			 LIB.safeAddCol(tCommInvLedger, "last_update", COL_TYPE_ENUM.COL_DATE_TIME);
//    			 
//    			 LIB.safeSetColFormatAsDate(tCommInvLedger, "flow_date");
//    			 
//    			 {
//    				 //select cic_id, deal_num, tran_num, param_seq_num, profile_seq_num, flow_date, deal_unadj_quantity, last_update from comm_inv_ledger
//    				 String sWhat = "cic_id, deal_num, tran_num, param_seq_num, profile_seq_num, flow_date, deal_unadj_quantity, last_update";
//    				 String sWhere = "1=1";
//    				 LIB.loadFromDbWithWhatWhere(tCommInvLedger, "comm_inv_ledger", tCiCDistinctList, sWhat, sWhere, bDebugLogging, className);
//    			 }
// 
//    			 {
//    				 String sWhat = "cic_id, deal_num, tran_num, param_seq_num, profile_seq_num, flow_date, volume, last_update";
//    				 String sWhere = "cic_id GE -1";
//    				 LIB.select(tReturn, tCommInvLedger, sWhat, sWhere, false, className);
//    			 }
//    			 
//    			 // add internal_portfolio and location_id from tCommInvCriteria
//    			 LIB.safeAddCol(tReturn, "internal_portfolio", COL_TYPE_ENUM.COL_INT);
//    			 LIB.safeAddCol(tReturn, "location_id", COL_TYPE_ENUM.COL_INT);
//    			 LIB.safeSetColFormatAsRef(tReturn, "internal_portfolio", SHM_USR_TABLES_ENUM.PORTFOLIO_TABLE); 
//    			 LIB.safeSetColFormatAsRef(tReturn, "location_id", SHM_USR_TABLES_ENUM.GAS_PHYS_LOCATION_TABLE); 
//    			 {
//    				 String sWhat = "internal_portfolio, location_id";
//    				 String sWhere = "cic_id EQ $cic_id";
//    				 LIB.select(tReturn, tCommInvCriteria, sWhat, sWhere, false, className);
//    			 }
//    			 
//    			 // add gas phys location level fields, e.g., Year for the Emissions
//    			 LIB.safeAddCol(tReturn, "year", COL_TYPE_ENUM.COL_INT);
//    			 LIB.safeAddCol(tReturn, "is_pre_registry_flag", COL_TYPE_ENUM.COL_INT);
//    			 LIB.safeAddCol(tReturn, "is_in_registry_flag", COL_TYPE_ENUM.COL_INT);
//    			 LIB.safeAddCol(tReturn, "is_generic_flag", COL_TYPE_ENUM.COL_INT);
//    			 LIB.safeAddCol(tReturn, "zone_id", COL_TYPE_ENUM.COL_INT);
//    			 LIB.safeAddCol(tReturn, "pipeline_id", COL_TYPE_ENUM.COL_INT);
//    			 LIB.safeSetColFormatAsRef(tReturn, "is_pre_registry_flag", SHM_USR_TABLES_ENUM.NO_YES_TABLE); 
//    			 LIB.safeSetColFormatAsRef(tReturn, "is_in_registry_flag", SHM_USR_TABLES_ENUM.NO_YES_TABLE); 
//    			 LIB.safeSetColFormatAsRef(tReturn, "is_generic_flag", SHM_USR_TABLES_ENUM.NO_YES_TABLE); 
//    			 LIB.safeSetColFormatAsRef(tReturn, "zone_id", SHM_USR_TABLES_ENUM.GAS_PHYS_ZONE_TABLE); 
//    			 LIB.safeSetColFormatAsRef(tReturn, "pipeline_id", SHM_USR_TABLES_ENUM.GAS_PHYS_PIPELINE_TABLE); 
//
//    			 {
//    				 String sWhat = "year, is_pre_registry_flag, is_in_registry_flag, is_generic_flag, pipeline_id, zone_id, idx_subgroup";
//    				 String sWhere = "location_id EQ $location_id";
//    				 LIB.select(tReturn, tGasPhysLocation, sWhat, sWhere, false, className);
//    			 }
//    			 
//    			 {
//    				 Table tTranNums = Table.tableNew();
//        			 LIB.safeAddCol(tTranNums, "tran_num", COL_TYPE_ENUM.COL_INT);
//    				 
//        			 tReturn.copyColDistinct("tran_num", tTranNums, "tran_num");
//        			 
//    				 Table tAbTran = Table.tableNew();
//        			 LIB.safeAddCol(tAbTran, "tran_num", COL_TYPE_ENUM.COL_INT);
//        			 LIB.safeAddCol(tAbTran, "ins_num", COL_TYPE_ENUM.COL_INT);
//        			 LIB.safeAddCol(tAbTran, "ins_type", COL_TYPE_ENUM.COL_INT);
//        			 LIB.safeAddCol(tAbTran, "toolset", COL_TYPE_ENUM.COL_INT);
//        			 tAbTran.defaultFormat();
//        			 
//        			 String sWhat = "tran_num, ins_num, ins_type, toolset";
//    				 String sWhere = "1=1";
//    				 LIB.loadFromDbWithWhatWhere(tAbTran, "ab_tran", tTranNums, sWhat, sWhere, bDebugLogging, className);
//    	  			 
//    				 {
//        				 sWhat = "ins_num, ins_type, toolset";
//        				 sWhere = "tran_num EQ $tran_num";
//        				 LIB.select(tReturn, tAbTran, sWhat, sWhere, false, className);
//        			 }
// 
//        			 tTranNums = LIB.destroyTable(tTranNums);
//        			 tAbTran = LIB.destroyTable(tAbTran);
//        	    		
//    			 }
    			  
    			 
    			 String sGroupCols = COLS.REPORT.RAW_REPORT.TRADE_LIST.deal_num + ", " + 
    					 COLS.REPORT.RAW_REPORT.TRADE_LIST.param_seq_num;
    			 tReturn.group(sGroupCols);
    			 
    			   
    		 } catch (Exception e) {
				LIB.log("TRADE_LIST.getRawReport", e, className);
    		 } 
    		 
//    		 tGasPhysLocation = LIB.destroyTable(tGasPhysLocation);
//    		 tCommInvCriteria = LIB.destroyTable(tCommInvCriteria);
//    		 tDistinctLocationIDs = LIB.destroyTable(tDistinctLocationIDs);
//    		 tCommInvLedger = LIB.destroyTable(tCommInvLedger);
    		 
    		 return tReturn;
         }  
        
	} // end TRADE_LIST
    
        	 
        	 public static class TRADE_LIST_QUORUM {
        	 		
        	        private static Table createRawReportTable(String className) throws OException{
        	        	Table tData = Util.NULL_TABLE;
        	              try{
        	            	  LIB.log("TRADE_LIST.createRawReportTable: START", className);

        	            	  tData = Table.tableNew();
        	            	  LIB.safeAddCol(tData, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.deal_num, COL_TYPE_ENUM.COL_INT);
        	            	  LIB.safeAddCol(tData, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.external_deal_ref, COL_TYPE_ENUM.COL_STRING);
        	            	  LIB.safeAddCol(tData, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.quorum_ins_type, COL_TYPE_ENUM.COL_STRING);
        	            	  LIB.safeAddCol(tData, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.quorum_deal_type, COL_TYPE_ENUM.COL_STRING);
        	            	  
        	            	  LIB.safeAddCol(tData, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.tran_num, COL_TYPE_ENUM.COL_INT);
        	            	  LIB.safeAddCol(tData, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.toolset, COL_TYPE_ENUM.COL_INT);
        	            	  LIB.safeAddCol(tData, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.ins_type, COL_TYPE_ENUM.COL_INT);
        	            	  LIB.safeAddCol(tData, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.base_ins_type, COL_TYPE_ENUM.COL_INT);


        	            	  LIB.safeAddCol(tData, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.trade_date, COL_TYPE_ENUM.COL_INT);
 
        	            	  LIB.safeAddCol(tData, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.ins_num, COL_TYPE_ENUM.COL_INT);
        	            	  LIB.safeAddCol(tData, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.counterparty, COL_TYPE_ENUM.COL_STRING);
        	            	  LIB.safeAddCol(tData, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.external_bunit, COL_TYPE_ENUM.COL_INT);
        	            	  
        	            	  
        	            	  LIB.safeAddCol(tData, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.endur_long_name, COL_TYPE_ENUM.COL_STRING);
        	            	  LIB.safeAddCol(tData, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.party_info_quorum_name, COL_TYPE_ENUM.COL_STRING);
 
        	            	  LIB.safeAddCol(tData, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.start_date, COL_TYPE_ENUM.COL_INT);
        	            	  LIB.safeAddCol(tData, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.end_date, COL_TYPE_ENUM.COL_INT);

        	            	  LIB.safeAddCol(tData, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.param_seq_num, COL_TYPE_ENUM.COL_INT);
        	            	  LIB.safeAddCol(tData, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.buy_sell, COL_TYPE_ENUM.COL_INT);
        	            	  LIB.safeAddCol(tData, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.deal_price_string, COL_TYPE_ENUM.COL_STRING);
        	            	  
        	            	  LIB.safeAddCol(tData, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.fx_flt, COL_TYPE_ENUM.COL_INT);

        	            	  LIB.safeAddCol(tData, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.proj_index_int, COL_TYPE_ENUM.COL_INT);

        	            	  // add proj_index as a string
        	            	  LIB.safeAddCol(tData, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.proj_index, COL_TYPE_ENUM.COL_STRING);
        	            	  // add index_location as a string
        	            	  LIB.safeAddCol(tData, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.index_location, COL_TYPE_ENUM.COL_STRING);

        	            	  LIB.safeAddCol(tData, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.price, COL_TYPE_ENUM.COL_DOUBLE);
        	            	  LIB.safeAddCol(tData, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.float_spd, COL_TYPE_ENUM.COL_DOUBLE);
        	            	  LIB.safeAddCol(tData, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.index_percent, COL_TYPE_ENUM.COL_DOUBLE);
 

        	            	  LIB.safeAddCol(tData, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.quantity, COL_TYPE_ENUM.COL_DOUBLE);
        	            	  LIB.safeAddCol(tData, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.total_quantity, COL_TYPE_ENUM.COL_DOUBLE);
        	 

        	            	  LIB.safeAddCol(tData, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.fx_flt, COL_TYPE_ENUM.COL_INT);
        	            	  LIB.safeAddCol(tData, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.currency, COL_TYPE_ENUM.COL_INT);
        	            	   
        	            	  // add as a string
        	            	  LIB.safeAddCol(tData, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.pipeline, COL_TYPE_ENUM.COL_STRING);
        	            	  LIB.safeAddCol(tData, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.location, COL_TYPE_ENUM.COL_STRING);
        	            	  LIB.safeAddCol(tData, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.location_string, COL_TYPE_ENUM.COL_STRING);

        	            	   
        	            	  // Reference Data Formatting
        	            	  LIB.safeSetColFormatAsRef(tData, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.toolset, SHM_USR_TABLES_ENUM.TOOLSETS_TABLE); 
        	            	  LIB.safeSetColFormatAsRef(tData, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.ins_type, SHM_USR_TABLES_ENUM.INSTRUMENTS_TABLE); 
        	            	  LIB.safeSetColFormatAsRef(tData, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.base_ins_type, SHM_USR_TABLES_ENUM.INSTRUMENTS_TABLE); 
        	            	  LIB.safeSetColFormatAsRef(tData, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.external_bunit, SHM_USR_TABLES_ENUM.PARTY_TABLE); 
        	            	  LIB.safeSetColFormatAsRef(tData, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.buy_sell, SHM_USR_TABLES_ENUM.BUY_SELL_TABLE); 
        	            	  LIB.safeSetColFormatAsRef(tData, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.proj_index_int, SHM_USR_TABLES_ENUM.INDEX_TABLE); 
        	            	//  LIB.safeSetColFormatAsRef(tData, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.index_location, SHM_USR_TABLES_ENUM.INDEX_LOCATION_TABLE); 

        	            	  LIB.safeSetColFormatAsRef(tData, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.fx_flt, SHM_USR_TABLES_ENUM.FX_FLT_TABLE); 
        	            	  LIB.safeSetColFormatAsRef(tData, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.currency, SHM_USR_TABLES_ENUM.CURRENCY_TABLE); 

        	            	  
        	            	  // Dates
        	            	  LIB.safeSetColFormatAsDate(tData, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.trade_date);
        	            	  LIB.safeSetColFormatAsDate(tData, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.start_date);
        	            	  LIB.safeSetColFormatAsDate(tData, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.end_date);
        	            	  
        	            	  
        	              } catch(Exception e ){               
        	            	  LIB.log("TRADE_LIST.createRawReportTable", e, className);
        	              } 
        	              return tData;
        	        } 
        	        
        	        public static Table getRawReport(Table tTradeDataOneRowPerUniqueKeyAsSubTableWithSettingsToo, REPORT_NAME reportName,
        	 				int iRunNumber, String className) throws OException{
        	         	
        	    		 Table tReturn = Util.NULL_TABLE;
        	    		 Table tAbTran  = Util.NULL_TABLE;
//        	    		 Table tDistinctLocationIDs  = Util.NULL_TABLE;
//        	    		 Table tCommInvCriteria  = Util.NULL_TABLE;
        	    		 
        	    	//	 Table tCommInvLedger  = Util.NULL_TABLE;
        	    		 try {
        	    			 
        	    			 tReturn = createRawReportTable(className);
        	    			 
        	    			 // We may be loading one extra day on each side, to help with certain reports
        	    			 // So make sure to only get for this report items in the user-requested date range
        	     			// int iStartDateForFilteringReportOutput = LIB.safeGetInt(tTradeDataOneRowPerUniqueKeyAsSubTableWithSettingsToo, COLS.REPORT.RAW_BY_UNIQUE_KEY_REPORT_DATA.SETTING.START_DATE, CONST_VALUES.ROW_TO_GET_OR_SET);
        	     			// int iEndDateFilteringReportOutput =  LIB.safeGetInt(tTradeDataOneRowPerUniqueKeyAsSubTableWithSettingsToo, COLS.REPORT.RAW_BY_UNIQUE_KEY_REPORT_DATA.SETTING.END_DATE, CONST_VALUES.ROW_TO_GET_OR_SET);
        	     			
        	    			 //Table tCurrentData = LIB.safeGetTable(tTradeDataOneRowPerUniqueKeyAsSubTableWithSettingsToo, COLS.REPORT.RAW_BY_UNIQUE_KEY_REPORT_DATA.CURRENT_DATA, CONST_VALUES.ROW_TO_GET_OR_SET);
        	    		 	        			 
        	   		//	 Table tSummaryPositionData = LIB.safeGetTable(tTradeDataOneRowPerUniqueKeyAsSubTableWithSettingsToo, COLS.REPORT.RAW_BY_UNIQUE_KEY_REPORT_DATA.CURRENT_DATA_SUMMARIZED, CONST_VALUES.ROW_TO_GET_OR_SET);
        	    		//	 Table tSummaryPositionData = LIB.safeGetTable(tTradeDataOneRowPerUniqueKeyAsSubTableWithSettingsToo, COLS.REPORT.RAW_BY_UNIQUE_KEY_REPORT_DATA.CURRENT_DATA_SUMMARIZED_AND_FILTERED, CONST_VALUES.ROW_TO_GET_OR_SET);
        	    		//	 int iCurrentDataPositionDate = LIB.safeGetInt(tTradeDataOneRowPerUniqueKeyAsSubTableWithSettingsToo, COLS.REPORT.RAW_BY_UNIQUE_KEY_REPORT_DATA.CURRENT_DATA_POSITION_DATE, CONST_VALUES.ROW_TO_GET_OR_SET);


        	    			 tAbTran = Table.tableNew();
        	    			 LIB.safeAddCol(tAbTran, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.deal_num, COL_TYPE_ENUM.COL_INT);
        	    			 LIB.safeAddCol(tAbTran, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.tran_num, COL_TYPE_ENUM.COL_INT);
        	    			 LIB.safeAddCol(tAbTran, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.ins_num, COL_TYPE_ENUM.COL_INT);
        	    			 LIB.safeAddCol(tAbTran, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.toolset, COL_TYPE_ENUM.COL_INT);
        	    			 LIB.safeAddCol(tAbTran, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.ins_type, COL_TYPE_ENUM.COL_INT);
        	    			 LIB.safeAddCol(tAbTran, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.base_ins_type, COL_TYPE_ENUM.COL_INT);
        	    			 LIB.safeAddCol(tAbTran, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.trade_date, COL_TYPE_ENUM.COL_INT);
        	    			 LIB.safeAddCol(tAbTran, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.buy_sell, COL_TYPE_ENUM.COL_INT);
        	    			 LIB.safeAddCol(tAbTran, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.counterparty, COL_TYPE_ENUM.COL_INT);
        	    			 LIB.safeAddCol(tAbTran, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.external_bunit, COL_TYPE_ENUM.COL_INT);

        	    			 {
            	    			 // load external_bunit twice into two different columns
            	    			 String sSQL = "select deal_tracking_num, tran_num, ins_num, toolset, ins_type, base_ins_type, trade_date, buy_sell, external_bunit, external_bunit FROM ab_tran WHERE tran_status = 3 AND trade_flag = 1";
            	    			 LIB.execISql(tAbTran, sSQL, false, className);
        	    			 }
        	    			   
        	    			 {
        	    				 // Formatting is functional
        	    				 LIB.safeSetColFormatAsRef(tAbTran, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.counterparty, SHM_USR_TABLES_ENUM.PARTY_TABLE); 
        	    				 
        	    				 // This converts counterparty from an Int to a String
        	    				 String sWhat = COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.deal_num + ", " +
        	    						 COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.tran_num + ", " +
        	    						 COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.ins_num + ", " +
        	    						 COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.toolset + ", " +
        	    						 COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.ins_type + ", " +
        	    						 COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.base_ins_type + ", " +
        	    						 COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.trade_date + ", " +
        	    						 COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.buy_sell + ", " +
        	    						 COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.counterparty + ", " + 
        	    						 COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.external_bunit;
        	    				 String sWhere = COLS.REPORT.RAW_REPORT.TRADE_LIST.deal_num + " GT 0";
        	    				 LIB.select(tReturn, tAbTran, sWhat, sWhere, false, className);
        	    			 }
 

        	    			 // get Party Info
        	    			 {
        	    				 Table tPartyInfo = Table.tableNew();
        	        			 LIB.safeAddCol(tPartyInfo, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.external_bunit, COL_TYPE_ENUM.COL_INT);
        	        			 LIB.safeAddCol(tPartyInfo, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.party_info_quorum_name, COL_TYPE_ENUM.COL_STRING);
        	        			
        	    				 String sSQL = "select party_id, value from party_info WHERE type_id = " + PARTY_INFO.ExternalSystemName;
        	        			 LIB.execISql(tPartyInfo, sSQL, false, className);

        	        			 String sWhat = COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.party_info_quorum_name;
        	        			 String sWhere = COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.external_bunit + " EQ $" + COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.external_bunit;
        	        			 LIB.select(tReturn, tPartyInfo, sWhat, sWhere, false, className);
        	        			 
         	        			 tPartyInfo = LIB.destroyTable(tPartyInfo);
        	        			 
        	    			 }
        	    			 
        	    			 // get Party Data, long name
        	    			 {
        	    				 Table tParty = Table.tableNew();
        	        			 LIB.safeAddCol(tParty, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.external_bunit, COL_TYPE_ENUM.COL_INT);
        	        			 LIB.safeAddCol(tParty, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.endur_long_name, COL_TYPE_ENUM.COL_STRING);
        	        			
        	    				 String sSQL = "select party_id, long_name from party";
        	        			 LIB.execISql(tParty, sSQL, false, className);

        	        			 String sWhat = COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.endur_long_name;
        	        			 String sWhere = COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.external_bunit + " EQ $" + COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.external_bunit;
        	        			 LIB.select(tReturn, tParty, sWhat, sWhere, false, className);
        	        			 
         	        			 tParty = LIB.destroyTable(tParty);
        	        			 
        	    			 }
        	    			 
        	    			 // calc counterparty based on the Party Info and the Long Name
        	    			 {
        	    				 int iNumRows = LIB.safeGetNumRows(tReturn);
        	    				 for (int iCounter = 1; iCounter <= iNumRows; iCounter++) {
        	    					 String sQuorumName = LIB.safeGetString(tReturn, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.party_info_quorum_name, iCounter);

        	    					 try {
        	    						 // use 3 for no particular reason
        	    						 final int MIN_LENGHT = 3;
        	    						 if (Str.len(sQuorumName) >= MIN_LENGHT) {
        	    							 // Use Quorum Name if found
                	    					 LIB.safeSetString(tReturn,  COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.counterparty, iCounter, sQuorumName);
        	    						 } else {
        	    							 // Else use Party Long Name
        	    							 String sLongName = LIB.safeGetString(tReturn, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.endur_long_name, iCounter);
            	    						 if (Str.len(sLongName) >= MIN_LENGHT) {
                    	    					 LIB.safeSetString(tReturn,  COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.counterparty, iCounter, sQuorumName);
            	    						 }
        	    						 }
        	    						 
        	    					 } catch (Exception e) {
        	    						 // do not log this
        	    					 }
         	    				 }
        	    			 }
         	    			 
        	    			 // Get Tran Info data
        	    			 {
        	    				 Table tTranNums =  Table.tableNew();
        	    				 LIB.safeAddCol(tTranNums, "tran_num", COL_TYPE_ENUM.COL_INT);

        	    				 tReturn.copyColDistinct(COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.tran_num, tTranNums, "tran_num");
        	    				 
        	    				 Table tTranInfo = Table.tableNew();
        	    				 LIB.safeAddCol(tTranInfo, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.tran_num, COL_TYPE_ENUM.COL_INT);
        	    				 LIB.safeAddCol(tTranInfo, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.external_deal_ref, COL_TYPE_ENUM.COL_STRING);

        	    				 String sWhat = "tran_num, value";
        	    				 String sWhere = "type_id = " + TRAN_INFO.ExternalDealRef;
        	    				 LIB.loadFromDbWithWhatWhere(tTranInfo, "ab_tran_info", tTranNums, sWhat, sWhere, false, className);
        	    				 

        	    				 sWhat = COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.external_deal_ref;
        	    				 sWhere = COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.tran_num + " EQ $" + COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.tran_num;
        	    				 LIB.select(tReturn, tTranInfo, sWhat, sWhere, false, className);
        	    				 
        	    				 
        	    				 // TOxDO
        	    				// LIB.viewTable(tTranInfo, "tran tTranInfo tests");
        	    				 

        	    				 tTranNums = LIB.destroyTable(tTranNums);
        	    				 tTranInfo = LIB.destroyTable(tTranInfo);
        	    			 }
        	    			 
        	    			 
        	    			 // Get Parameter data
        	    			 {
        	    				 Table tInsNums =  Table.tableNew();
        	    				 LIB.safeAddCol(tInsNums, "ins_num", COL_TYPE_ENUM.COL_INT);

        	    				 tReturn.copyColDistinct(COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.ins_num, tInsNums, "ins_num");
        	    				 
        	    				 Table tParameter = Table.tableNew();
        	    				 LIB.safeAddCol(tParameter, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.ins_num, COL_TYPE_ENUM.COL_INT);
        	    				 LIB.safeAddCol(tParameter, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.param_seq_num, COL_TYPE_ENUM.COL_INT);
        	    				 LIB.safeAddCol(tParameter, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.proj_index_int, COL_TYPE_ENUM.COL_INT);
        	    				 LIB.safeAddCol(tParameter, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.proj_index, COL_TYPE_ENUM.COL_INT);
        	    				 LIB.safeAddCol(tParameter, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.index_location, COL_TYPE_ENUM.COL_INT);
        	    				 LIB.safeAddCol(tParameter, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.currency, COL_TYPE_ENUM.COL_INT);
        	    				 LIB.safeAddCol(tParameter, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.fx_flt, COL_TYPE_ENUM.COL_INT);
        	    				 LIB.safeAddCol(tParameter, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.start_date, COL_TYPE_ENUM.COL_INT);
        	    				 LIB.safeAddCol(tParameter, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.end_date, COL_TYPE_ENUM.COL_INT);
        	    				 
        	    				 LIB.safeAddCol(tParameter, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.quantity, COL_TYPE_ENUM.COL_DOUBLE);
        	    				 LIB.safeAddCol(tParameter, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.price, COL_TYPE_ENUM.COL_DOUBLE);
        	    				 LIB.safeAddCol(tParameter, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.float_spd, COL_TYPE_ENUM.COL_DOUBLE);
        	    				 LIB.safeAddCol(tParameter, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.index_percent, COL_TYPE_ENUM.COL_DOUBLE);
   
        	    				 String sWhat = "ins_num, param_seq_num, proj_index, proj_index, index_loc_id, currency, fx_flt, start_date, mat_date, notnl, rate, float_spd, index_multiplier";
        	    				 String sWhere = "1 = 1";
        	    				 LIB.loadFromDbWithWhatWhere(tParameter, "parameter", tInsNums, sWhat, sWhere, false, className);
         	    				 
        	    				 // This is functional, needed for column conversion
        	    				 LIB.safeSetColFormatAsRef(tParameter, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.proj_index, SHM_USR_TABLES_ENUM.INDEX_TABLE);
        	    				 LIB.safeSetColFormatAsRef(tParameter, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.index_location, SHM_USR_TABLES_ENUM.INDEX_LOCATION_TABLE);
        	    				 
        	    				 // Add ins type and toolset to the tParameter table.. we'll need it for the joins
        	    				 {
            	    				 LIB.safeAddCol(tParameter, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.ins_type, COL_TYPE_ENUM.COL_INT);
            	    				 LIB.safeAddCol(tParameter, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.toolset, COL_TYPE_ENUM.COL_INT);
            	    				 
            	    				 sWhat = COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.ins_type + ", " + 
            	    						 COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.toolset;
            	    				 sWhere = COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.ins_num + " EQ $" + COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.ins_num;
            	    				 LIB.select(tParameter, tReturn, sWhat, sWhere, false, className);
        	    				 }
 
        	    				 // Commodity Toolset
        	    				 {
            	    				 sWhat = COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.param_seq_num + ", " +
            	    						 COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.start_date + ", " +
            	    						 COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.end_date + ", " +
            	    						 COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.quantity;
            	    				 sWhere = COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.ins_num + " EQ $" + COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.ins_num + " AND " + 
            	    						 COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.toolset + " EQ " + TOOLSETS.COMMODITY + " AND " + 
            	    						 COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.param_seq_num + " EQ 1";
            	    				 LIB.select(tReturn, tParameter, sWhat, sWhere, false, className);
            	    				 
            	    				 sWhat = COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.fx_flt + ", " +
            	    						 COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.proj_index_int + ", " +
            	    						 COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.proj_index + ", " +
            	    						 COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.index_location + ", " +
            	    						 COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.index_percent + ", " +
            	    						 COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.float_spd + ", " +
            	    						 COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.price;
            	    				 sWhere = COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.ins_num + " EQ $" + COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.ins_num + " AND " + 
            	    						 COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.toolset + " EQ " + TOOLSETS.COMMODITY + " AND " + 
            	    						 COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.param_seq_num + " EQ 2";
            	    				 LIB.select(tReturn, tParameter, sWhat, sWhere, false, className);

        	    				 }


        	    				 // ComSwap Toolset
        	    				 {
            	    				 sWhat = COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.param_seq_num + ", " +
            	    						 COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.start_date + ", " +
            	    						 COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.end_date + ", " +
            	    						 COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.quantity;
             	    				 sWhere = COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.ins_num + " EQ $" + COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.ins_num + " AND " + 
            	    						 COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.toolset + " EQ " + TOOLSETS.COM_SWAP + " AND " + 
            	    						 COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.param_seq_num + " EQ 0";
            	    				 LIB.select(tReturn, tParameter, sWhat, sWhere, false, className);

            	    				 sWhat = COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.fx_flt + ", " +
            	    						 COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.proj_index_int + ", " +
            	    						 COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.proj_index + ", " +
            	    						 COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.index_location + ", " +
            	    						 COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.index_percent + ", " +
            	    						 COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.float_spd + ", " +
            	    						 COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.price;
            	    				 sWhere = COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.ins_num + " EQ $" + COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.ins_num + " AND " + 
            	    						 COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.toolset + " EQ " + TOOLSETS.COM_SWAP + " AND " + 
            	    						 COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.param_seq_num + " EQ 1";
            	    				 LIB.select(tReturn, tParameter, sWhat, sWhere, false, className);

        	    				 }
        	    				 
        	    				 {
        	    					 Table tProfile = Table.tableNew();
        	        	    			
        	    					 LIB.safeAddCol(tProfile, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.ins_num, COL_TYPE_ENUM.COL_INT);
        	    					 LIB.safeAddCol(tProfile, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.param_seq_num, COL_TYPE_ENUM.COL_INT);
            	    				 LIB.safeAddCol(tProfile, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.total_quantity, COL_TYPE_ENUM.COL_DOUBLE);
       
            	    				 sWhat = "ins_num, param_seq_num, sum(notnl) ohd_quanity";
            	    				 sWhere = "1 = 1 GROUP BY ins_num, param_seq_num";
            	    				 LIB.loadFromDbWithWhatWhere(tProfile, "profile", tInsNums, sWhat, sWhere, false, className);
             	    				 
            	    				 // For this report, show the absolute value 
            	    				 tProfile.mathABSCol(COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.total_quantity);
            	    				 
            	    				 sWhat = COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.total_quantity;
            	    				 sWhere = COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.ins_num + " EQ $" + COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.ins_num + " AND " + 
            	    						 COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.param_seq_num + " EQ $" + COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.param_seq_num;
            	    				 LIB.select(tReturn, tProfile, sWhat, sWhere, false, className);
            	    				 
            	    				 // TOxDO
            	    				 //LIB.viewTable(tProfile, "profile sgddfd");
            	    				 
            	    				 tProfile = LIB.destroyTable(tProfile);
            	    			
        	    				 } 
        	    				 
        	    				 {
        	    					 Table tGasPhysParam = Table.tableNew();
        	        	    			
        	    					 LIB.safeAddCol(tGasPhysParam, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.ins_num, COL_TYPE_ENUM.COL_INT);
        	    					 LIB.safeAddCol(tGasPhysParam, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.param_seq_num, COL_TYPE_ENUM.COL_INT);
            	    				 LIB.safeAddCol(tGasPhysParam, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.pipeline, COL_TYPE_ENUM.COL_INT);
            	    				 LIB.safeAddCol(tGasPhysParam, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.location, COL_TYPE_ENUM.COL_INT);
       
            	    				 sWhat = "ins_num, param_seq_num, pipeline_id, location_id";
            	    				 sWhere = "1 = 1";
            	    				 LIB.loadFromDbWithWhatWhere(tGasPhysParam, "gas_phys_param_view", tInsNums, sWhat, sWhere, false, className);
             	    				 
            	    				 // Formatting is functional for string conversion
            	    				 LIB.safeSetColFormatAsRef(tGasPhysParam, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.pipeline, SHM_USR_TABLES_ENUM.GAS_PHYS_PIPELINE_TABLE);
            	    				 LIB.safeSetColFormatAsRef(tGasPhysParam, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.location, SHM_USR_TABLES_ENUM.GAS_PHYS_LOCATION_TABLE);

            	    				 
            	    				 sWhat = COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.pipeline + ", " + 
            	    						 COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.location;
            	    				 sWhere = COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.ins_num + " EQ $" + COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.ins_num + " AND " + 
            	    						 COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.param_seq_num + " EQ $" + COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.param_seq_num;
            	    				 LIB.select(tReturn, tGasPhysParam, sWhat, sWhere, false, className);
            	    				 
            	    				 // TOxDO
            	    				 //LIB.viewTable(tProfile, "profile sgddfd");
            	    				 
            	    				 tGasPhysParam = LIB.destroyTable(tGasPhysParam);
            	    			
        	    				 }
        	    				 
        	    				 {
        	    					 
        	    					 
        	    					// select ins_num, param_seq_num, pipeline_id, location_id from gas_phys_param_view WHERE ins_num = 1000216
        	    							 
        	    				 }

        	    				 
        	    				 // TODO
        	    			//  LIB.viewTable(tParameter, "parameter estegsd dd");
        	    				  
//        	    				 sWhat = COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.external_deal_ref;
//        	    				 sWhere = COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.tran_num + " EQ $" + COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.tran_num;
//        	    				 LIB.select(tReturn, tParameter, sWhat, sWhere, false, className);
        	    				 
        	    				 
        	    				 // TOxDO
        	    				// LIB.viewTable(tTranInfo, "tran tTranInfo tests");
        	    				 
        	    				 
        	    				 tInsNums = LIB.destroyTable(tInsNums);
        	    				 tParameter = LIB.destroyTable(tParameter); 
        	    			 }
        	    			 
        	    			 int xdsdvsd = 3;
        	    			 
        	    			 // Get Alternate Index Location
        	    			 {
        	    				 Table tIndexLocation = Table.tableNew();
        	    				 LIB.safeAddCol(tIndexLocation, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.proj_index_int, COL_TYPE_ENUM.COL_INT);
        	    				 LIB.safeAddCol(tIndexLocation, "index_location2", COL_TYPE_ENUM.COL_STRING);
        	    				 
        	    				 LIB.execISql(tIndexLocation, "select index_id, index_loc_name from index_location", false, className);
        	    				 
        	    				 // This is functional to remove duplicates
        	    				 tIndexLocation.group(COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.proj_index_int);
        	    				 tIndexLocation.distinctRows();

        	    				 LIB.safeAddCol(tReturn, "index_location2", COL_TYPE_ENUM.COL_STRING);
        	    				 String sWhat = "index_location2";
        	    				 String sWhere =COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.proj_index_int + " EQ $" + COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.proj_index_int;
        	    				 LIB.select(tReturn, tIndexLocation, sWhat, sWhere, false, className);
        	    				 
        	    				 // TOxDO
        	    				 //LIB.viewTable(tIndexLocation, " tIndexLocation sfgsdgff");
        	    				 
        	    				 tIndexLocation = LIB.destroyTable(tIndexLocation);
        	    			 }
        	    			 
        	    			  

        	    			 // Populate Ins Type and Deal Type
        	    			 {
        	    				 int FIXED = 0;
        	    				 int FLOAT = 1;
        	    				 int iNumRows = LIB.safeGetNumRows(tReturn);
        	    				 for (int iCounter = 1; iCounter <= iNumRows; iCounter++) {
        	    					 
//        	    					 String sPipeline = LIB.safeGetString(tReturn, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.pipeline, iCounter);
        	    					 String sLocation = LIB.safeGetString(tReturn, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.location, iCounter);
  //      	    					 String sLocationString = sPipeline + "/" + sLocation;
        	    					 LIB.safeSetString(tReturn,  COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.location_string, iCounter, sLocation);
             	    				
        	    					 
        	    					 int iFxFlt = LIB.safeGetInt(tReturn, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.fx_flt, iCounter);
        	    					 
        	    					 String sInsType = "Unknown";
        	    					 String sDealType = "Unknown";
        	    					 int iToolset = LIB.safeGetInt(tReturn, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.toolset, iCounter);
        	    					 
        	    					 if (iToolset == TOOLSETS.COMMODITY) {
        	    						 sInsType = "Physical";
            	    					 sDealType = "Fixed";
            	    					 
            	    					 if (iFxFlt == FLOAT) {
                	    					 sDealType = "Index";
            	    					 }
        	    					 }
        	    					 
        	    					 LIB.safeSetString(tReturn,  COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.quorum_ins_type, iCounter, sInsType);
        	    					 LIB.safeSetString(tReturn,  COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.quorum_deal_type, iCounter, sDealType);
        	    					 
        	    					 if (iFxFlt == FLOAT) {
        	    						 String sProjIndex = LIB.safeGetString(tReturn, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.proj_index, iCounter);
        	    						 String sIndexLocOnDeal = LIB.safeGetString(tReturn, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.index_location, iCounter);
        	    						 String sIndexLocCalced = LIB.safeGetString(tReturn, "index_location2", iCounter);
            	    					 
        	    						 String sIndex = sIndexLocOnDeal;
        	    						 if (Str.len(sIndex) <= 2) {
        	    							 sIndex = sIndexLocCalced;
        	    						 }
        	    						 if (Str.len(sIndex) <= 2) {
        	    							 sIndex = sProjIndex;
        	    						 }
        	    						 
        	    						 
            	    					 double dPrice = LIB.safeGetDouble(tReturn, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.price, iCounter);
            	    					 double dFloatSpd = LIB.safeGetDouble(tReturn, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.float_spd, iCounter);
            	    					 dPrice = dPrice + dFloatSpd;
            	    					 LIB.safeSetDouble(tReturn, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.price, iCounter, dPrice);
            	    					 
            	    					 String sPrice = Str.doubleToStr(dPrice);
            	    					// sPrice = "0.20";

            	    					 // TODO, replace the 1x with the real thing
            	    					 String sDealPriceString = sIndex + " " + "1x + " + sPrice;
            	    					 LIB.safeSetString(tReturn, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.deal_price_string, iCounter, sDealPriceString);
        	    					 }

        	    					 
        	    					 if (iFxFlt == FIXED) {
        	    						 // zero or blank out items
            	    					 LIB.safeSetString(tReturn, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.proj_index, iCounter, "");
            	    					 LIB.safeSetString(tReturn, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.index_location, iCounter, "");
            	    					 LIB.safeSetInt(tReturn, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.proj_index_int, iCounter, 1);
            	    					 LIB.safeSetDouble(tReturn, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.index_percent, iCounter, 1);
            	    					 	
            	    					 double dPrice = LIB.safeGetDouble(tReturn, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.price, iCounter);
            	    					 String sPrice = Str.doubleToStr(dPrice);
            	    					 LIB.safeSetString(tReturn, COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.deal_price_string, iCounter, sPrice);
        	    					 }
        	    					 
        	    					 

        	    				 }
        	    				 
        	    			 }
        	    			 
        	    			 
//        	    			 tGasPhysLocation = Table.tableNew();
//        	    			 LIB.safeAddCol(tGasPhysLocation, "location_id", COL_TYPE_ENUM.COL_INT);
//        	    			 LIB.safeAddCol(tGasPhysLocation, "location_name", COL_TYPE_ENUM.COL_STRING);
//        	    			 LIB.safeAddCol(tGasPhysLocation, "zone_id", COL_TYPE_ENUM.COL_INT);
//        	    			 LIB.safeAddCol(tGasPhysLocation, "pipeline_id", COL_TYPE_ENUM.COL_INT);
//        	    			 LIB.safeAddCol(tGasPhysLocation, "idx_subgroup", COL_TYPE_ENUM.COL_INT);
//        	    			 {
//        	    				 // 40009 is Emissions
//        	        			 String sSQL = "select location_id, location_name, zone_id, pipeline_id, idx_subgroup from gas_phys_location WHERE pipeline_id = 40009";
//        	        			 LIB.execISql(tGasPhysLocation, sSQL, bDebugLogging, className);
//        	    			 }
//        	    			 
//        	    			 LIB.safeAddCol(tGasPhysLocation, "year", COL_TYPE_ENUM.COL_INT);
//        	    			 LIB.safeAddCol(tGasPhysLocation, "is_pre_registry_flag", COL_TYPE_ENUM.COL_INT);
//        	    			 LIB.safeAddCol(tGasPhysLocation, "is_in_registry_flag", COL_TYPE_ENUM.COL_INT);
//        	    			 LIB.safeAddCol(tGasPhysLocation, "is_generic_flag", COL_TYPE_ENUM.COL_INT);
//        	    			 LIB.safeSetColFormatAsRef(tGasPhysLocation, "is_pre_registry_flag", SHM_USR_TABLES_ENUM.NO_YES_TABLE); 
//        	    			 LIB.safeSetColFormatAsRef(tGasPhysLocation, "is_in_registry_flag", SHM_USR_TABLES_ENUM.NO_YES_TABLE); 
//        	    			 LIB.safeSetColFormatAsRef(tGasPhysLocation, "is_generic_flag", SHM_USR_TABLES_ENUM.NO_YES_TABLE); 
//        	    			 
        	//
//        	    			 {
//        	    				 Map<String, EMISSION_LOCATION> locationDictionary = getLocationDictionary(className);
//        	    				 
//        	    				 int iNumRows = LIB.safeGetNumRows(tGasPhysLocation);
//        	    				 for (int iCounter = 1; iCounter <= iNumRows; iCounter++) {
//        	    					 String sName = LIB.safeGetString(tGasPhysLocation, "location_name", iCounter);
//        	    					 try {
//        	    						 EMISSION_LOCATION location = locationDictionary.get(sName);
//        	    						 LIB.safeSetInt(tGasPhysLocation, "year", iCounter, location.getYear());
//        	    						 LIB.safeSetInt(tGasPhysLocation, "is_pre_registry_flag", iCounter, location.getPreRegistryFlagAsInt());
//        	    						 LIB.safeSetInt(tGasPhysLocation, "is_in_registry_flag", iCounter, location.getInRegistryFlagAsInt());
//        	    						 LIB.safeSetInt(tGasPhysLocation, "is_generic_flag", iCounter, location.getIsGenericFlagAsInt());
//        	    					 } catch (Exception e) {
//        	    						 // don't log this
//        	    					 }
//        	    				 }
//        	    			 }
        	// 
//        	    			 
//        	    			 tDistinctLocationIDs = Table.tableNew();
//        	    			 LIB.safeAddCol(tDistinctLocationIDs, "location_id", COL_TYPE_ENUM.COL_INT);
//        	    			 tGasPhysLocation.copyColDistinct("location_id", tDistinctLocationIDs, "location_id");
        	//
//        	    			 // get a distinct list by cic_id, internal_portfolio and by location
//        	    			 tCommInvCriteria = Table.tableNew();
//        	    			 LIB.safeAddCol(tCommInvCriteria, "cic_id", COL_TYPE_ENUM.COL_INT);
//        	    			 LIB.safeAddCol(tCommInvCriteria, "internal_portfolio", COL_TYPE_ENUM.COL_INT);
//        	    			 LIB.safeAddCol(tCommInvCriteria, "location_id", COL_TYPE_ENUM.COL_INT);
//        	    			 tCommInvCriteria.defaultFormat();
//        	    			 
//        	    			 {
//        	    				 String sWhat = "cic_id, internal_portfolio, location_id";
//        	    				 String sWhere = "1=1";
//        	    				 LIB.loadFromDbWithWhatWhere(tCommInvCriteria, "comm_inv_criteria", tDistinctLocationIDs, sWhat, sWhere, bDebugLogging, className);
//        	    			 }
        	//
//        	    			 Table tCiCDistinctList = Table.tableNew();
//        	    			 LIB.safeAddCol(tCiCDistinctList, "cic_id", COL_TYPE_ENUM.COL_INT);
        	//
//        	    			 tCommInvCriteria.copyColDistinct("cic_id", tCiCDistinctList, "cic_id");
        	//
//        	    			 tCommInvLedger = Table.tableNew();
//        	    			 LIB.safeAddCol(tCommInvLedger, "cic_id", COL_TYPE_ENUM.COL_INT);
//        	    			 LIB.safeAddCol(tCommInvLedger, "deal_num", COL_TYPE_ENUM.COL_INT);
//        	    			 LIB.safeAddCol(tCommInvLedger, "tran_num", COL_TYPE_ENUM.COL_INT);
//        	    			 LIB.safeAddCol(tCommInvLedger, "param_seq_num", COL_TYPE_ENUM.COL_INT);
//        	    			 LIB.safeAddCol(tCommInvLedger, "profile_seq_num", COL_TYPE_ENUM.COL_INT);
//        	    			 // do this as a date
//        	    			 LIB.safeAddCol(tCommInvLedger, "flow_date", COL_TYPE_ENUM.COL_INT);
//        	    			 LIB.safeAddCol(tCommInvLedger, "volume", COL_TYPE_ENUM.COL_DOUBLE);
//        	    			 LIB.safeAddCol(tCommInvLedger, "last_update", COL_TYPE_ENUM.COL_DATE_TIME);
//        	    			 
//        	    			 LIB.safeSetColFormatAsDate(tCommInvLedger, "flow_date");
//        	    			 
//        	    			 {
//        	    				 //select cic_id, deal_num, tran_num, param_seq_num, profile_seq_num, flow_date, deal_unadj_quantity, last_update from comm_inv_ledger
//        	    				 String sWhat = "cic_id, deal_num, tran_num, param_seq_num, profile_seq_num, flow_date, deal_unadj_quantity, last_update";
//        	    				 String sWhere = "1=1";
//        	    				 LIB.loadFromDbWithWhatWhere(tCommInvLedger, "comm_inv_ledger", tCiCDistinctList, sWhat, sWhere, bDebugLogging, className);
//        	    			 }
        	// 
//        	    			 {
//        	    				 String sWhat = "cic_id, deal_num, tran_num, param_seq_num, profile_seq_num, flow_date, volume, last_update";
//        	    				 String sWhere = "cic_id GE -1";
//        	    				 LIB.select(tReturn, tCommInvLedger, sWhat, sWhere, false, className);
//        	    			 }
//        	    			 
//        	    			 // add internal_portfolio and location_id from tCommInvCriteria
//        	    			 LIB.safeAddCol(tReturn, "internal_portfolio", COL_TYPE_ENUM.COL_INT);
//        	    			 LIB.safeAddCol(tReturn, "location_id", COL_TYPE_ENUM.COL_INT);
//        	    			 LIB.safeSetColFormatAsRef(tReturn, "internal_portfolio", SHM_USR_TABLES_ENUM.PORTFOLIO_TABLE); 
//        	    			 LIB.safeSetColFormatAsRef(tReturn, "location_id", SHM_USR_TABLES_ENUM.GAS_PHYS_LOCATION_TABLE); 
//        	    			 {
//        	    				 String sWhat = "internal_portfolio, location_id";
//        	    				 String sWhere = "cic_id EQ $cic_id";
//        	    				 LIB.select(tReturn, tCommInvCriteria, sWhat, sWhere, false, className);
//        	    			 }
//        	    			 
//        	    			 // add gas phys location level fields, e.g., Year for the Emissions
//        	    			 LIB.safeAddCol(tReturn, "year", COL_TYPE_ENUM.COL_INT);
//        	    			 LIB.safeAddCol(tReturn, "is_pre_registry_flag", COL_TYPE_ENUM.COL_INT);
//        	    			 LIB.safeAddCol(tReturn, "is_in_registry_flag", COL_TYPE_ENUM.COL_INT);
//        	    			 LIB.safeAddCol(tReturn, "is_generic_flag", COL_TYPE_ENUM.COL_INT);
//        	    			 LIB.safeAddCol(tReturn, "zone_id", COL_TYPE_ENUM.COL_INT);
//        	    			 LIB.safeAddCol(tReturn, "pipeline_id", COL_TYPE_ENUM.COL_INT);
//        	    			 LIB.safeSetColFormatAsRef(tReturn, "is_pre_registry_flag", SHM_USR_TABLES_ENUM.NO_YES_TABLE); 
//        	    			 LIB.safeSetColFormatAsRef(tReturn, "is_in_registry_flag", SHM_USR_TABLES_ENUM.NO_YES_TABLE); 
//        	    			 LIB.safeSetColFormatAsRef(tReturn, "is_generic_flag", SHM_USR_TABLES_ENUM.NO_YES_TABLE); 
//        	    			 LIB.safeSetColFormatAsRef(tReturn, "zone_id", SHM_USR_TABLES_ENUM.GAS_PHYS_ZONE_TABLE); 
//        	    			 LIB.safeSetColFormatAsRef(tReturn, "pipeline_id", SHM_USR_TABLES_ENUM.GAS_PHYS_PIPELINE_TABLE); 
        	//
//        	    			 {
//        	    				 String sWhat = "year, is_pre_registry_flag, is_in_registry_flag, is_generic_flag, pipeline_id, zone_id, idx_subgroup";
//        	    				 String sWhere = "location_id EQ $location_id";
//        	    				 LIB.select(tReturn, tGasPhysLocation, sWhat, sWhere, false, className);
//        	    			 }
//        	    			 
//        	    			 {
//        	    				 Table tTranNums = Table.tableNew();
//        	        			 LIB.safeAddCol(tTranNums, "tran_num", COL_TYPE_ENUM.COL_INT);
//        	    				 
//        	        			 tReturn.copyColDistinct("tran_num", tTranNums, "tran_num");
//        	        			 
//        	    				 Table tAbTran = Table.tableNew();
//        	        			 LIB.safeAddCol(tAbTran, "tran_num", COL_TYPE_ENUM.COL_INT);
//        	        			 LIB.safeAddCol(tAbTran, "ins_num", COL_TYPE_ENUM.COL_INT);
//        	        			 LIB.safeAddCol(tAbTran, "ins_type", COL_TYPE_ENUM.COL_INT);
//        	        			 LIB.safeAddCol(tAbTran, "toolset", COL_TYPE_ENUM.COL_INT);
//        	        			 tAbTran.defaultFormat();
//        	        			 
//        	        			 String sWhat = "tran_num, ins_num, ins_type, toolset";
//        	    				 String sWhere = "1=1";
//        	    				 LIB.loadFromDbWithWhatWhere(tAbTran, "ab_tran", tTranNums, sWhat, sWhere, bDebugLogging, className);
//        	    	  			 
//        	    				 {
//        	        				 sWhat = "ins_num, ins_type, toolset";
//        	        				 sWhere = "tran_num EQ $tran_num";
//        	        				 LIB.select(tReturn, tAbTran, sWhat, sWhere, false, className);
//        	        			 }
        	// 
//        	        			 tTranNums = LIB.destroyTable(tTranNums);
//        	        			 tAbTran = LIB.destroyTable(tAbTran);
//        	        	    		
//        	    			 }
        	    			 
        	    			 String sGroupCols = COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.deal_num + ", " + 
        	    					 COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.param_seq_num;
        	    			 tReturn.group(sGroupCols);
        	    		 
        	    			   
        	    		 } catch (Exception e) {
        					LIB.log("TRADE_LIST_QUORUM.getRawReport", e, className);
        	    		 } 
        	    		 
        	    		 // TOxDO
        	    	//  LIB.viewTable(tReturn, "Raw data, trade list quorum  sdddd");
        	    		 
//        	    		 tGasPhysLocation = LIB.destroyTable(tGasPhysLocation);
//        	    		 tCommInvCriteria = LIB.destroyTable(tCommInvCriteria);
//        	    		 tDistinctLocationIDs = LIB.destroyTable(tDistinctLocationIDs);
//        	    		 tCommInvLedger = LIB.destroyTable(tCommInvLedger);
        	    		 
        	    		 
        	    		 
        	    		 return tReturn;
        	         }  
        	        
        		} // end TRADE_LIST_QUORUM
        	    
	
        } // END class GET_RAW_REPORT

        // all of these functions take the 'one row per lease' table (with the source data) 
        // and combine it, for the various leases into the report the user sees
        public static class GET_REPORT {
 
        	public static class TRADE_LIST {

	           	private static Table createReportTable(String className) throws OException{
		        	Table tReturn = Util.NULL_TABLE;
		              try{
		            	  LIB.log("TRADE_LIST.createReportTable: START", className);

		            	  tReturn = Table.tableNew();
		            	  
		            	  LIB.safeAddCol(tReturn, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.deal_num, COL_TYPE_ENUM.COL_INT);
		            	  LIB.safeAddCol(tReturn, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.toolset, COL_TYPE_ENUM.COL_INT);
		            	  LIB.safeAddCol(tReturn, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.ins_type, COL_TYPE_ENUM.COL_INT);

		            	  LIB.safeAddCol(tReturn, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.counterparty, COL_TYPE_ENUM.COL_STRING);
		            	  
		            	  LIB.safeAddCol(tReturn, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.trade_date, COL_TYPE_ENUM.COL_INT);

		            	  LIB.safeAddCol(tReturn, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.buy_sell, COL_TYPE_ENUM.COL_INT);
		            	  LIB.safeAddCol(tReturn, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.deal_price_string, COL_TYPE_ENUM.COL_STRING);

 		            	  
		            	  LIB.safeAddCol(tReturn, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.start_date, COL_TYPE_ENUM.COL_INT);
		            	  LIB.safeAddCol(tReturn, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.end_date, COL_TYPE_ENUM.COL_INT);
		            	  
		            	  LIB.safeAddCol(tReturn, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.quantity, COL_TYPE_ENUM.COL_DOUBLE);
		            	  LIB.safeAddCol(tReturn, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.total_quantity, COL_TYPE_ENUM.COL_DOUBLE);
 
		            	  

		            	  // add these as COL_STRING
		            	  LIB.safeAddCol(tReturn, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.location_string, COL_TYPE_ENUM.COL_STRING);
		            	  
		            	  
		            	  
		            	  // at the end, hidden
		            	  LIB.safeAddCol(tReturn, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.tran_num, COL_TYPE_ENUM.COL_INT);
		            	  LIB.safeAddCol(tReturn, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.ins_num, COL_TYPE_ENUM.COL_INT);

		            	  LIB.safeAddCol(tReturn, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.param_seq_num, COL_TYPE_ENUM.COL_INT);
		            	  
		            	  // proj_index is a COL_STRING
		            	  LIB.safeAddCol(tReturn, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.proj_index, COL_TYPE_ENUM.COL_STRING);
		            	  
		            	  // index_location is a COL_STRING
		            	  LIB.safeAddCol(tReturn, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.index_location, COL_TYPE_ENUM.COL_STRING);
		            	  
		            	  LIB.safeAddCol(tReturn, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.price, COL_TYPE_ENUM.COL_DOUBLE);

		            	  
		            	  
		             	   
		            	  LIB.safeAddCol(tReturn, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.param_seq_num, COL_TYPE_ENUM.COL_INT);
		            	  // add proj_index as a string
		            	  LIB.safeAddCol(tReturn, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.proj_index, COL_TYPE_ENUM.COL_STRING);
		            	  // add index_location as a string
		            	  LIB.safeAddCol(tReturn, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.index_location, COL_TYPE_ENUM.COL_STRING);
		            	  LIB.safeAddCol(tReturn, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.price, COL_TYPE_ENUM.COL_DOUBLE);
		            	  
		            	  
		            	  LIB.safeAddCol(tReturn, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.index_percent, COL_TYPE_ENUM.COL_DOUBLE);
		            	  
		            	  LIB.safeAddCol(tReturn, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.pipeline, COL_TYPE_ENUM.COL_STRING);
		            	  LIB.safeAddCol(tReturn, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.location, COL_TYPE_ENUM.COL_STRING);

		            	  
		            	  

		            	  
 
//		            	  LIB.safeAddCol(tReturn, "year_formatted", COL_TYPE_ENUM.COL_STRING);
//		            	  LIB.safeAddCol(tReturn, "idx_subgroup", COL_TYPE_ENUM.COL_INT); 
//		            	  LIB.safeAddCol(tReturn, "location_id", COL_TYPE_ENUM.COL_INT);
//		            	  
//		            	  LIB.safeAddCol(tReturn, "flow_date", COL_TYPE_ENUM.COL_INT);
//		            	  LIB.safeAddCol(tReturn, "volume", COL_TYPE_ENUM.COL_DOUBLE);
// 
//		            	  LIB.safeAddCol(tReturn, "internal_portfolio", COL_TYPE_ENUM.COL_INT);
//		            	  LIB.safeAddCol(tReturn, "ins_type", COL_TYPE_ENUM.COL_INT);
//		            	  LIB.safeAddCol(tReturn, "toolset", COL_TYPE_ENUM.COL_INT);
//		            	  LIB.safeAddCol(tReturn, "deal_num", COL_TYPE_ENUM.COL_INT);
//		            	  LIB.safeAddCol(tReturn, "param_seq_num", COL_TYPE_ENUM.COL_INT);
//		            	  LIB.safeAddCol(tReturn, "profile_seq_num", COL_TYPE_ENUM.COL_INT);
//		            	  LIB.safeAddCol(tReturn, "pipeline_id", COL_TYPE_ENUM.COL_INT);
//		            	  LIB.safeAddCol(tReturn, "zone_id", COL_TYPE_ENUM.COL_INT);
//		            	  
//		            	  LIB.safeAddCol(tReturn, "is_pre_registry_flag", COL_TYPE_ENUM.COL_INT);
//		            	  LIB.safeAddCol(tReturn, "is_in_registry_flag", COL_TYPE_ENUM.COL_INT);
//		            	  LIB.safeAddCol(tReturn, "is_generic_flag", COL_TYPE_ENUM.COL_INT);
//		            	  LIB.safeAddCol(tReturn, "last_update", COL_TYPE_ENUM.COL_DATE_TIME);
//		            	  LIB.safeAddCol(tReturn, "tran_num", COL_TYPE_ENUM.COL_INT);
//		            	  LIB.safeAddCol(tReturn, "ins_num", COL_TYPE_ENUM.COL_INT);
//		            	  LIB.safeAddCol(tReturn, "cic_id", COL_TYPE_ENUM.COL_INT);
//
//		            	  LIB.safeAddCol(tReturn, "year", COL_TYPE_ENUM.COL_INT);

		              } catch(Exception e ){               
		            	  LIB.log("TRADE_LIST.createReportTable", e, className);
		              } 
		              return tReturn;
		        }
	           	
	        	public static void setReportTitles(Table tMaster, boolean bWebServicesMode, int iRunNumber, String className) throws OException {

					try {

						LIB.safeSetColTitle(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.deal_num, "Deal Num", null, bWebServicesMode);
						LIB.safeSetColTitle(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.tran_num, "Tran Num", null, bWebServicesMode);
						LIB.safeSetColTitle(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.toolset, "Toolset", null, bWebServicesMode);
				     	LIB.safeSetColTitle(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.ins_type, "Ins Type", null, bWebServicesMode);
				     	LIB.safeSetColTitle(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.trade_date, "Trade Date", null, bWebServicesMode);


				     	LIB.safeSetColTitle(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.ins_num, "Ins Num", null, bWebServicesMode);
				     	LIB.safeSetColTitle(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.counterparty, "Counterparty", null, bWebServicesMode);


				     	LIB.safeSetColTitle(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.param_seq_num, "Param\nSeq Num", null, bWebServicesMode);
				     	LIB.safeSetColTitle(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.buy_sell, "Buy/Sell", null, bWebServicesMode);
				     	LIB.safeSetColTitle(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.deal_price_string, "Contractual Deal Price", null, bWebServicesMode);
				     	LIB.safeSetColTitle(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.proj_index, "Proj\nIndex", null, bWebServicesMode);
				     	LIB.safeSetColTitle(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.index_location, "Index\nLocation", null, bWebServicesMode);
				     	LIB.safeSetColTitle(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.price, "Price", null, bWebServicesMode);

 
				     	LIB.safeSetColTitle(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.index_percent, "Index\nPercent", null, bWebServicesMode);
				     	LIB.safeSetColTitle(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.quantity, "DailyQuantity", null, bWebServicesMode);
				     	LIB.safeSetColTitle(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.total_quantity, "Total Deal Quantity", null, bWebServicesMode);
				     	

				     	LIB.safeSetColTitle(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.start_date, "Deal Del\nBegin Date", null, bWebServicesMode);
				     	LIB.safeSetColTitle(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.end_date, "Deal Del\nEnd Date", null, bWebServicesMode);
 
				    	LIB.safeSetColTitle(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.location, "Location", null, bWebServicesMode);
				    	LIB.safeSetColTitle(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.location_string, "Contractual Location", null, bWebServicesMode);
				    	LIB.safeSetColTitle(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.pipeline, "Pipeline", null, bWebServicesMode);

 						
					} catch (Exception e) {
						LIB.log("TRADE_LIST.setReportTitles", e, className);
					}
				}
 				
				public static void formatReport(Table tMaster, boolean bWebServicesMode, int iRunNumber, String className) throws OException {

					try {
						
						// Do Grouping
						tMaster.group(COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.deal_num);
						LIB.safeAddCol(tMaster, "temp_for_sort", COL_TYPE_ENUM.COL_INT);
						
						tMaster.setColIncrementInt("temp_for_sort", 1, -1);
						tMaster.group("temp_for_sort");
						LIB.safeColHide(tMaster, "temp_for_sort");
						 
						
//						{
//							// need to add string columns to get correct sort order for grouping
//                			LIB.safeAddCol(tMaster, "temp_portfolio", COL_TYPE_ENUM.COL_STRING);
//                			tMaster.copyColFromRef(COLS.REPORT.FORMATTED_REPORT.AVERAGING_TRADE_LIST.PORTFOLIO, "temp_portfolio", SHM_USR_TABLES_ENUM.PORTFOLIO_TABLE);
//
//                			LIB.safeAddCol(tMaster, "temp_ins_type", COL_TYPE_ENUM.COL_STRING);
//                			tMaster.copyColFromRef(COLS.REPORT.FORMATTED_REPORT.AVERAGING_TRADE_LIST.INS_TYPE, "temp_ins_type", SHM_USR_TABLES_ENUM.INSTRUMENTS_TABLE);
//
//                			LIB.safeAddCol(tMaster, "temp_currency", COL_TYPE_ENUM.COL_STRING);
//                			tMaster.copyColFromRef(COLS.REPORT.FORMATTED_REPORT.AVERAGING_TRADE_LIST.CURRENCY_ID, "temp_currency", SHM_USR_TABLES_ENUM.CURRENCY_TABLE);
//
//                     		String sGroupCols = "temp_portfolio" + ", " + 
//                    				"temp_ins_type"+ ", " + 
//                    				"temp_currency";
//                    		tMaster.group(sGroupCols);
//                			 
//                    		LIB.safeDelCol(tMaster, "temp_portfolio");
//                    		LIB.safeDelCol(tMaster, "temp_ins_type");
//                    		LIB.safeDelCol(tMaster, "temp_currency");
//                		}


						// Set Titles
						setReportTitles(tMaster, bWebServicesMode, iRunNumber, className);
 
						// Ref formatting
						LIB.safeSetColFormatAsRef(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.toolset, SHM_USR_TABLES_ENUM.TOOLSETS_TABLE); 
		            	LIB.safeSetColFormatAsRef(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.ins_type, SHM_USR_TABLES_ENUM.INSTRUMENTS_TABLE); 
 

						LIB.safeSetColFormatAsRef(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.buy_sell, SHM_USR_TABLES_ENUM.BUY_SELL_TABLE); 
						LIB.safeSetColFormatAsRef(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.proj_index, SHM_USR_TABLES_ENUM.INDEX_TABLE); 
						LIB.safeSetColFormatAsRef(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.index_location, SHM_USR_TABLES_ENUM.INDEX_LOCATION_TABLE); 
						
		            	// Date formatting
	               		LIB.safeSetColFormatAsDate(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.trade_date);
	               		LIB.safeSetColFormatAsDate(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.start_date);
	               		LIB.safeSetColFormatAsDate(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.end_date);
	               		
	               		// Format Values to 0 Decimal Places
	               		int DECIMAL_PLACES = 0;
	               		final int NOT_USED_WIDTH = 10;
	               		final int BASE = 0;
	               		final int HIDE_ZERO = 1;
	               	    final int SHOW_ZERO = 0;
	               		int INDEX_PERCENT_DECIMAL_PLACES = 3;
	               		int PRICE_DECIMAL_PLACES = 3;
	               		tMaster.setColFormatAsNotnlAcct(COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.total_quantity, NOT_USED_WIDTH, DECIMAL_PLACES, BASE, SHOW_ZERO);
	               		tMaster.setColFormatAsNotnlAcct(COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.quantity, NOT_USED_WIDTH, DECIMAL_PLACES, BASE, SHOW_ZERO);
	               		tMaster.setColFormatAsNotnlAcct(COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.price, NOT_USED_WIDTH, PRICE_DECIMAL_PLACES, BASE, SHOW_ZERO);
	               		tMaster.setColFormatAsNotnlAcct(COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.index_percent, NOT_USED_WIDTH, INDEX_PERCENT_DECIMAL_PLACES, BASE, HIDE_ZERO);
               		 
	               		
		            	// Hide hidden columns
		            	LIB.safeColHide(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.tran_num);
		            	LIB.safeColHide(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.ins_num);
		            	LIB.safeColHide(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.param_seq_num); 
		            	LIB.safeColHide(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.price); 
		            	LIB.safeColHide(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.index_percent); 
		            	LIB.safeColHide(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.proj_index); 
		            	LIB.safeColHide(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.index_location); 
		            	LIB.safeColHide(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.pipeline); 
		            	LIB.safeColHide(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.location); 
  		            	
					} catch (Exception e) {
						LIB.log("TRADE_LIST.formatReport", e, className);
					}
				}

				public static Table getReport(Table tMaster, boolean bWebServicesMode, int iRunNumber, String className) throws OException {

					Table tReturn = Util.NULL_TABLE;
					try {
						
						tReturn = createReportTable(className);
						
						// Use Select to add data.  Could alternately have used copy by col name.  Each have their pros and cons.  Decided on this
						LIB.safeAddCol(tMaster, COLS.TEMP_COL_NAME_FOR_SELECT, COL_TYPE_ENUM.COL_INT);
						
						String sWhat = COLS.REPORT.RAW_REPORT.TRADE_LIST.deal_num + "(" + COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.deal_num + ")" + 
								", " + COLS.REPORT.RAW_REPORT.TRADE_LIST.tran_num + "(" + COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.tran_num + ")" + 
								", " + COLS.REPORT.RAW_REPORT.TRADE_LIST.ins_num + "(" + COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.ins_num + ")" + 
								", " + COLS.REPORT.RAW_REPORT.TRADE_LIST.toolset + "(" + COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.toolset + ")" + 
								", " + COLS.REPORT.RAW_REPORT.TRADE_LIST.trade_date + "(" + COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.trade_date + ")" + 
								", " + COLS.REPORT.RAW_REPORT.TRADE_LIST.counterparty + "(" + COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.counterparty + ")" + 
								
								", " + COLS.REPORT.RAW_REPORT.TRADE_LIST.param_seq_num + "(" + COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.param_seq_num + ")" + 
								", " + COLS.REPORT.RAW_REPORT.TRADE_LIST.buy_sell + "(" + COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.buy_sell + ")" + 
								", " + COLS.REPORT.RAW_REPORT.TRADE_LIST.deal_price_string + "(" + COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.deal_price_string + ")" + 
								", " + COLS.REPORT.RAW_REPORT.TRADE_LIST.proj_index + "(" + COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.proj_index + ")" + 
								", " + COLS.REPORT.RAW_REPORT.TRADE_LIST.index_location + "(" + COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.index_location + ")" + 
								", " + COLS.REPORT.RAW_REPORT.TRADE_LIST.price + "(" + COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.price + ")" + 

								", " + COLS.REPORT.RAW_REPORT.TRADE_LIST.quantity + "(" + COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.quantity + ")" + 
								", " + COLS.REPORT.RAW_REPORT.TRADE_LIST.total_quantity + "(" + COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.total_quantity + ")" + 
								", " + COLS.REPORT.RAW_REPORT.TRADE_LIST.index_percent + "(" + COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.index_percent + ")" + 

								", " + COLS.REPORT.RAW_REPORT.TRADE_LIST.start_date + "(" + COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.start_date + ")" + 
								", " + COLS.REPORT.RAW_REPORT.TRADE_LIST.end_date + "(" + COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.end_date + ")" + 

								", " + COLS.REPORT.RAW_REPORT.TRADE_LIST.pipeline + "(" + COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.pipeline + ")" + 
								", " + COLS.REPORT.RAW_REPORT.TRADE_LIST.location + "(" + COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.location + ")" + 
								", " + COLS.REPORT.RAW_REPORT.TRADE_LIST.location_string + "(" + COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.location_string + ")" + 


								
								", " + COLS.REPORT.RAW_REPORT.TRADE_LIST.ins_type + "(" + COLS.REPORT.FORMATTED_REPORT.TRADE_LIST.ins_type + ")" ;
						
						
						// to get all rows
						
						// TODO
						//String sWhat = "*"; 
						
						String sWhere = COLS.TEMP_COL_NAME_FOR_SELECT + " GE -1";
						LIB.select(tReturn, tMaster, sWhat, sWhere, false, className);
						
						LIB.safeDelCol(tReturn, COLS.TEMP_COL_NAME_FOR_SELECT);
   
						
						// Format the report, even if we are going to copy it to a script panel
						formatReport(tReturn, bWebServicesMode, iRunNumber, className);
						
					} catch (Exception e) {
						LIB.log("TRADE_LIST.getReport", e, className);
					}
 
					return tReturn;
				}

			} // end class TRADE_LIST
        	
        	
        	
        	public static class TRADE_LIST_QUORUM {

	           	private static Table createReportTable(String className) throws OException{
		        	Table tReturn = Util.NULL_TABLE;
		              try{
		            	  LIB.log("TRADE_LIST_QUORUM.createReportTable: START", className);

		            	  tReturn = Table.tableNew();
		            	  
		            	  LIB.safeAddCol(tReturn, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.deal_num, COL_TYPE_ENUM.COL_INT);
		            	  LIB.safeAddCol(tReturn, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.external_deal_ref, COL_TYPE_ENUM.COL_STRING);
		            	  LIB.safeAddCol(tReturn, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.quorum_ins_type, COL_TYPE_ENUM.COL_STRING);
		            	  LIB.safeAddCol(tReturn, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.quorum_deal_type, COL_TYPE_ENUM.COL_STRING);
		            	  
		            	  LIB.safeAddCol(tReturn, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.counterparty, COL_TYPE_ENUM.COL_STRING);

		            	  LIB.safeAddCol(tReturn, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.trade_date, COL_TYPE_ENUM.COL_INT);
		            	  LIB.safeAddCol(tReturn, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.buy_sell, COL_TYPE_ENUM.COL_INT);
		            	  LIB.safeAddCol(tReturn, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.deal_price_string, COL_TYPE_ENUM.COL_STRING);

		            	  LIB.safeAddCol(tReturn, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.start_date, COL_TYPE_ENUM.COL_INT);
		            	  LIB.safeAddCol(tReturn, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.end_date, COL_TYPE_ENUM.COL_INT);

	
		            	  LIB.safeAddCol(tReturn, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.quantity, COL_TYPE_ENUM.COL_DOUBLE);
		            	  LIB.safeAddCol(tReturn, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.total_quantity, COL_TYPE_ENUM.COL_DOUBLE);
		            	  LIB.safeAddCol(tReturn, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.location_string, COL_TYPE_ENUM.COL_STRING);
		            	  
 	
		            	  // at the end, hidden
		            	  LIB.safeAddCol(tReturn, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.tran_num, COL_TYPE_ENUM.COL_INT);
		            	  LIB.safeAddCol(tReturn, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.ins_num, COL_TYPE_ENUM.COL_INT);
		            	  LIB.safeAddCol(tReturn, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.toolset, COL_TYPE_ENUM.COL_INT);
		            	  LIB.safeAddCol(tReturn, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.ins_type, COL_TYPE_ENUM.COL_INT);

		            	  LIB.safeAddCol(tReturn, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.external_bunit, COL_TYPE_ENUM.COL_INT);
		            	  LIB.safeAddCol(tReturn, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.endur_long_name, COL_TYPE_ENUM.COL_STRING);
		            	  LIB.safeAddCol(tReturn, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.party_info_quorum_name, COL_TYPE_ENUM.COL_STRING);

 
		             	   
		            	  LIB.safeAddCol(tReturn, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.param_seq_num, COL_TYPE_ENUM.COL_INT);
		            	  // add proj_index as a string
		            	  LIB.safeAddCol(tReturn, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.proj_index, COL_TYPE_ENUM.COL_STRING);
		            	  // add index_location as a string
		            	  LIB.safeAddCol(tReturn, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.index_location, COL_TYPE_ENUM.COL_STRING);
		            	  LIB.safeAddCol(tReturn, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.price, COL_TYPE_ENUM.COL_DOUBLE);
		            	  
		            	  
 		            	  
		            	  LIB.safeAddCol(tReturn, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.index_percent, COL_TYPE_ENUM.COL_DOUBLE);
		            	  
		            	  LIB.safeAddCol(tReturn, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.pipeline, COL_TYPE_ENUM.COL_STRING);
		            	  LIB.safeAddCol(tReturn, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.location, COL_TYPE_ENUM.COL_STRING);

		            	   
		              } catch(Exception e ){               
		            	  LIB.log("TRADE_LIST_QUORUM.createReportTable", e, className);
		              } 
		              return tReturn;
		        }
	           	
	        	public static void setReportTitles(Table tMaster, boolean bWebServicesMode, int iRunNumber, String className) throws OException {

					try {
						
						LIB.safeSetColTitle(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.deal_num, "Endur Deal Num", null, bWebServicesMode);
						LIB.safeSetColTitle(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.external_deal_ref, "Deal #", null, bWebServicesMode);
						LIB.safeSetColTitle(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.tran_num, "Tran Num", null, bWebServicesMode);
						LIB.safeSetColTitle(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.toolset, "Toolset", null, bWebServicesMode);
		            	LIB.safeSetColTitle(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.ins_type, "Ins Type", null, bWebServicesMode);

		            	LIB.safeSetColTitle(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.quorum_ins_type, "Instrument Type", null, bWebServicesMode);
		            	LIB.safeSetColTitle(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.quorum_deal_type, "Deal Type", null, bWebServicesMode);

				     	LIB.safeSetColTitle(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.trade_date, "Trade Date", null, bWebServicesMode);

				     	LIB.safeSetColTitle(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.ins_num, "Ins Num", null, bWebServicesMode);
				     	LIB.safeSetColTitle(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.counterparty, "Counterparty", null, bWebServicesMode);

				     	LIB.safeSetColTitle(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.external_bunit, "Endur Party\nShort Name", null, bWebServicesMode);
				     	LIB.safeSetColTitle(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.endur_long_name, "Endur Party\nLong Name", null, bWebServicesMode);
 				     	
				     	LIB.safeSetColTitle(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.party_info_quorum_name, "Quorum Name\n(Party Info)", null, bWebServicesMode);


				     	LIB.safeSetColTitle(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.param_seq_num, "Param\nSeq Num", null, bWebServicesMode);
				     	LIB.safeSetColTitle(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.buy_sell, "Buy/Sell", null, bWebServicesMode);
				     	LIB.safeSetColTitle(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.deal_price_string, "Contractual Deal Price", null, bWebServicesMode);
				     	LIB.safeSetColTitle(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.proj_index, "Proj\nIndex", null, bWebServicesMode);
				     	LIB.safeSetColTitle(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.index_location, "Index\nLocation", null, bWebServicesMode);
				     	LIB.safeSetColTitle(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.price, "Price", null, bWebServicesMode);

				     	LIB.safeSetColTitle(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.index_percent, "Index\nPercent", null, bWebServicesMode);
				     	LIB.safeSetColTitle(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.quantity, "DailyQuantity", null, bWebServicesMode);
				     	LIB.safeSetColTitle(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.total_quantity, "Total Deal Quantity", null, bWebServicesMode);
 

				     	LIB.safeSetColTitle(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.start_date, "Deal Del\nBegin Date", null, bWebServicesMode);
				     	LIB.safeSetColTitle(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.end_date, "Deal Del\nEnd Date", null, bWebServicesMode);

 
				    	LIB.safeSetColTitle(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.location, "Location", null, bWebServicesMode);
				    	LIB.safeSetColTitle(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.location_string, "Contractual Location", null, bWebServicesMode);
				    	LIB.safeSetColTitle(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.pipeline, "Pipeline", null, bWebServicesMode);
			            
					
					} catch (Exception e) {
						LIB.log("TRADE_LIST_QUORUM.setReportTitles", e, className);
					}
				}

 				
				public static void formatReport(Table tMaster, boolean bWebServicesMode, int iRunNumber, String className) throws OException {

					try {
						
 						// Do Grouping
						tMaster.group(COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.deal_num);
						LIB.safeAddCol(tMaster, "temp_for_sort", COL_TYPE_ENUM.COL_INT);
						
						tMaster.setColIncrementInt("temp_for_sort", 1, -1);
						tMaster.group("temp_for_sort");
						LIB.safeColHide(tMaster, "temp_for_sort");
						
											
//						{
//							// need to add string columns to get correct sort order for grouping
//                			LIB.safeAddCol(tMaster, "temp_portfolio", COL_TYPE_ENUM.COL_STRING);
//                			tMaster.copyColFromRef(COLS.REPORT.FORMATTED_REPORT.AVERAGING_TRADE_LIST.PORTFOLIO, "temp_portfolio", SHM_USR_TABLES_ENUM.PORTFOLIO_TABLE);
//
//                			LIB.safeAddCol(tMaster, "temp_ins_type", COL_TYPE_ENUM.COL_STRING);
//                			tMaster.copyColFromRef(COLS.REPORT.FORMATTED_REPORT.AVERAGING_TRADE_LIST.INS_TYPE, "temp_ins_type", SHM_USR_TABLES_ENUM.INSTRUMENTS_TABLE);
//
//                			LIB.safeAddCol(tMaster, "temp_currency", COL_TYPE_ENUM.COL_STRING);
//                			tMaster.copyColFromRef(COLS.REPORT.FORMATTED_REPORT.AVERAGING_TRADE_LIST.CURRENCY_ID, "temp_currency", SHM_USR_TABLES_ENUM.CURRENCY_TABLE);
//
//                     		String sGroupCols = "temp_portfolio" + ", " + 
//                    				"temp_ins_type"+ ", " + 
//                    				"temp_currency";
//                    		tMaster.group(sGroupCols);
//                			 
//                    		LIB.safeDelCol(tMaster, "temp_portfolio");
//                    		LIB.safeDelCol(tMaster, "temp_ins_type");
//                    		LIB.safeDelCol(tMaster, "temp_currency");
//                		}

						
						
						
						// Set Titles
						setReportTitles(tMaster, bWebServicesMode, iRunNumber, className);
 
						// Ref formatting
						LIB.safeSetColFormatAsRef(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.toolset, SHM_USR_TABLES_ENUM.TOOLSETS_TABLE); 
		            	LIB.safeSetColFormatAsRef(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.ins_type, SHM_USR_TABLES_ENUM.INSTRUMENTS_TABLE); 

						LIB.safeSetColFormatAsRef(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.toolset, SHM_USR_TABLES_ENUM.TOOLSETS_TABLE); 
						LIB.safeSetColFormatAsRef(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.ins_type, SHM_USR_TABLES_ENUM.INSTRUMENTS_TABLE); 
						LIB.safeSetColFormatAsRef(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.external_bunit, SHM_USR_TABLES_ENUM.PARTY_TABLE); 


						LIB.safeSetColFormatAsRef(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.buy_sell, SHM_USR_TABLES_ENUM.BUY_SELL_TABLE); 
						LIB.safeSetColFormatAsRef(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.proj_index, SHM_USR_TABLES_ENUM.INDEX_TABLE); 
						LIB.safeSetColFormatAsRef(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.index_location, SHM_USR_TABLES_ENUM.INDEX_LOCATION_TABLE); 
						
		            	// Date formatting
	               		LIB.safeSetColFormatAsDateQuorum(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.trade_date);
	               		LIB.safeSetColFormatAsDateQuorum(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.start_date);
	               		LIB.safeSetColFormatAsDateQuorum(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.end_date);
		            	
	               		// Format Values to 0 Decimal Places
	               		int DECIMAL_PLACES = 0;
	               		final int NOT_USED_WIDTH = 10;
	               		final int BASE = 0;
	               		final int HIDE_ZERO = 1;
	               	    final int SHOW_ZERO = 0;
	               		int INDEX_PERCENT_DECIMAL_PLACES = 3;
	               		int PRICE_DECIMAL_PLACES = 3;
	               		tMaster.setColFormatAsNotnlAcct(COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.total_quantity, NOT_USED_WIDTH, DECIMAL_PLACES, BASE, SHOW_ZERO);
	               		tMaster.setColFormatAsNotnlAcct(COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.quantity, NOT_USED_WIDTH, DECIMAL_PLACES, BASE, SHOW_ZERO);
	               		tMaster.setColFormatAsNotnlAcct(COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.price, NOT_USED_WIDTH, PRICE_DECIMAL_PLACES, BASE, SHOW_ZERO);
	               		tMaster.setColFormatAsNotnlAcct(COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.index_percent, NOT_USED_WIDTH, INDEX_PERCENT_DECIMAL_PLACES, BASE, HIDE_ZERO);
               		 
		            	// Hide hidden columns
		            	LIB.safeColHide(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.tran_num);
		            	LIB.safeColHide(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.ins_num);		            	
		            	LIB.safeColHide(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.param_seq_num);

		            	LIB.safeColHide(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.toolset);
		            	LIB.safeColHide(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.ins_type);
		            	
		            	LIB.safeColHide(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.external_bunit);
		            	LIB.safeColHide(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.endur_long_name);
		            	LIB.safeColHide(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.party_info_quorum_name);
		            	
		            	LIB.safeColHide(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.proj_index);
		            	LIB.safeColHide(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.index_location);

		            	LIB.safeColHide(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.price);
		            	LIB.safeColHide(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.index_percent);
 
		            	LIB.safeColHide(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.pipeline); 
		            	LIB.safeColHide(tMaster, COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.location); 
  		            	
					} catch (Exception e) {
						LIB.log("TRADE_LIST_QUORUM.formatReport", e, className);
					}
				}

				public static Table getReport(Table tMaster, boolean bWebServicesMode, int iRunNumber, String className) throws OException {

					Table tReturn = Util.NULL_TABLE;
					try {
						
						tReturn = createReportTable(className);
						
						// Use Select to add data.  Could alternately have used copy by col name.  Each have their pros and cons.  Decided on this
						LIB.safeAddCol(tMaster, COLS.TEMP_COL_NAME_FOR_SELECT, COL_TYPE_ENUM.COL_INT);
					
						String sWhat = COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.deal_num + "(" + COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.deal_num + ")" + 
								", " + COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.external_deal_ref + "(" + COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.external_deal_ref + ")" + 
								", " + COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.quorum_ins_type + "(" + COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.quorum_ins_type + ")" + 
								", " + COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.quorum_deal_type + "(" + COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.quorum_deal_type + ")" + 
								", " + COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.tran_num + "(" + COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.tran_num + ")" + 
								", " + COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.toolset + "(" + COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.toolset + ")" + 
								", " + COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.trade_date + "(" + COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.trade_date + ")" + 
								", " + COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.ins_num + "(" + COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.ins_num + ")" + 
								", " + COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.counterparty + "(" + COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.counterparty + ")" + 
								", " + COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.external_bunit + "(" + COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.external_bunit + ")" + 
								", " + COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.party_info_quorum_name + "(" + COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.party_info_quorum_name + ")" + 
								", " + COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.endur_long_name + "(" + COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.endur_long_name + ")" + 
								
								", " + COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.param_seq_num + "(" + COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.param_seq_num + ")" + 
								", " + COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.buy_sell + "(" + COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.buy_sell + ")" + 
								", " + COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.deal_price_string + "(" + COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.deal_price_string + ")" + 
								", " + COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.proj_index + "(" + COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.proj_index + ")" + 
								", " + COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.index_location + "(" + COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.index_location + ")" + 
								", " + COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.price + "(" + COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.price + ")" + 


								", " + COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.start_date + "(" + COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.start_date + ")" + 
								", " + COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.end_date + "(" + COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.end_date + ")" + 
 
								", " + COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.quantity + "(" + COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.quantity + ")" + 
								", " + COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.total_quantity + "(" + COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.total_quantity + ")" + 
								", " + COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.index_percent + "(" + COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.index_percent + ")" + 

								", " + COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.pipeline + "(" + COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.pipeline + ")" + 
								", " + COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.location + "(" + COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.location + ")" + 
								", " + COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.location_string + "(" + COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.location_string + ")" + 

								
								", " + COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM.ins_type + "(" + COLS.REPORT.FORMATTED_REPORT.TRADE_LIST_QUORUM.ins_type + ")" ;

						 

						
						
						// TODO
//						String sWhat = "*"; 
						
						String sWhere = COLS.TEMP_COL_NAME_FOR_SELECT + " GE -1";
						LIB.select(tReturn, tMaster, sWhat, sWhere, false, className);
						
						LIB.safeDelCol(tReturn, COLS.TEMP_COL_NAME_FOR_SELECT);
   
						
						// Format the report, even if we are going to copy it to a script panel
						formatReport(tReturn, bWebServicesMode, iRunNumber, className);
						
					} catch (Exception e) {
						LIB.log("TRADE_LIST_QUORUM.getReport", e, className);
					}
 
					return tReturn;
				}

			} // end class TRADE_LIST_QUORUM
        	
        	
        	
		} // end class GET_REPORT
        	
        	
          
    } // end class REPORT
    

	public static class TRAN_INFO {
		public final static int ExternalDealRef = 20001;
	}

	public static class PARTY_INFO {
		public final static int ExternalSystemName = 20001;
	}
 
	public static class INS_TYPES {
		public final static int COMM_PHYS = 48010;
		public final static int ENGY_B_SWAP = 30001;
		public final static int ENGY_SWAP = 30002;
	}

	public static class TOOLSETS {
		public final static int COMMODITY = TOOLSET_ENUM.COMMODITY_TOOLSET.toInt();
		public final static int COM_SWAP = TOOLSET_ENUM.COM_SWAP_TOOLSET.toInt();
	}
	
       
    public static class COLS {     
  
    	public static final String TEMP_ROW_NUM_COL_NAME = "TEMP_ROW_NUM_COL_NAME";
    	public static final String TEMP_COL_NAME_FOR_SELECT = "TEMP_COL_NAME_FOR_SELECT";
    	public static final String TEMP_COL_NAME_FOR_DELETE = "TEMP_COL_NAME_FOR_DELETE"; 
  
//    	public static class CURRENT_DATA {    
//    		public static final String deal_num = "deal_num"; // COL_INT
//    		public static final String toolset = "toolset"; // COL_INT
//    		public static final String ins_type = "ins_type"; // COL_INT
//    		public static final String portfolio = "portfolio"; // COL_INT
//    		public static final String account_id = "account_id"; // COL_INT
//    		public static final String currency = "currency"; // COL_INT
//    		public static final String volume = "volume"; // COL_DOUBLE
//    	}
    	 
      	public static class DELIVERY_TYPE {    
      		public static final String delivery_type = "delivery_type";   // COL_INT 
      		public static final String currency_id = "currency_id"; // COL_INT
      	}  

      	public static class COMM_INV_CRITERIA {    
      		public static final String cic_id = "cic_id"; // COL_INT
      		public static final String internal_portfolio = "internal_portfolio"; // COL_INT
      		//public static final String int_strategy_id = "int_strategy_id"; // COL_INT
      		public static final String location_id = "location_id"; // COL_INT
      		public static final String inv_quantity_unit = "inv_quantity_unit"; // COL_INT
      	} 

      	public static class COMM_INV_LEDGER {    
      		public static final String cic_id = "cic_id"; // COL_INT
      		public static final String deal_num = "deal_num"; // COL_INT
      		public static final String param_seq_num = "param_seq_num"; // COL_INT
      		public static final String profile_seq_num = "profile_seq_num"; // COL_INT
      		// date as an integer
      		public static final String flow_date = "flow_date"; // COL_INT
      		public static final String inv_unadj_quantity = "inv_unadj_quantity"; // COL_INT
      		public static final String idx_unit = "idx_unit"; // COL_INT
      	} 

    	public static class SUMMARIZED_POSITIONS {   
 
    		public static final String current_date = "current_date"; // COL_INT
    		public static final String toolset = "toolset"; // COL_INT
    		public static final String ins_type = "ins_type"; // COL_INT
    		public static final String portfolio = "portfolio"; // COL_INT
    		public static final String currency = "currency"; // COL_INT
    		public static final String volume = "volume"; // COL_DOUBLE
 
    	} // END SUMMARIZED_POSITIONS
    	
 
		
		// don't change these column names
    	public static class INS_NUM {
    		public final static String INS_NUM = "ins_num"; //COL_INT
    	}
    	  
    	public static class CURRENCY_LIST {
    		public final static String CURRENCY_ID = "currency_id";
    		public final static String CURRENCY_NAME = "currency_name";
    	}

    	public static class PORTFOLIO_LIST {
    		public final static String PORTFOLIO_ID = "portfolio_id";
    		public final static String PORTFOLIO_NAME = "portfolio_name";
    	}

    	public static class INS_TYPE_LIST {
    		public final static String INS_TYPE_ID = "ins_type_id";
    		public final static String INS_TYPE_NAME = "ins_type_name";
    	}

    	
		public static class REPORT {

			// These are used to make deal_num a cell column and keep the original as an int column
			final static String DEAL_NUM_COL_NAME = "deal_num";
			final static String STATUS_COL_NAME = "recon_status";
     		final static String ORIGINAL_DEAL_NUM_COL_NAME = "deal_num_int";

			final static String TRAN_NUM_COL_NAME = "tran_num"; 

			public static class FORMATTED_REPORT {

 
				// TODO
		    	public static class TRADE_LIST extends COLS.REPORT.RAW_REPORT.TRADE_LIST  {
		    		
		    		public final static String deal_num = "deal_num"; //COL_INT
		    		public final static String toolset = "toolset"; //COL_INT
		    		public final static String ins_type = "ins_type"; //COL_INT

		    		public final static String external_deal_ref = "external_deal_ref"; //COL_STRING
		    	
		    	} 
		    	
		    	

		    	public static class TRADE_LIST_QUORUM extends COLS.REPORT.RAW_REPORT.TRADE_LIST_QUORUM {
		    		
		    	
		    	} 
		    	 
		    	
			} // end class FORMATTED_REPORT

			public static class RAW_REPORT {
 


				public static class TRADE_LIST  {
		    		
					public final static String deal_num = "deal_num"; //COL_INT
					public final static String tran_num = "tran_num"; //COL_INT
					
		    		public final static String toolset = "toolset"; //COL_INT
		    		public final static String ins_type = "ins_type"; //COL_INT
		    		public final static String base_ins_type = "base_ins_type"; //COL_INT

		    		public final static String external_deal_ref = "external_deal_ref"; //COL_STRING
		    		

		    		public final static String trade_date = "trade_date"; //COL_INT

		    		public final static String ins_num = "ins_num"; //COL_INT
		    		public final static String counterparty = "counterparty"; //COL_STRING

		    		public final static String param_seq_num = "param_seq_num"; //COL_INT
		    		public final static String buy_sell = "buy_sell"; //COL_INT
		    		public final static String deal_price_string = "deal_price_string"; //COL_STRING
		    		public final static String proj_index_int = "proj_index_int"; //COL_INT
		    		public final static String proj_index = "proj_index"; //COL_INT
		    		public final static String index_location = "index_location"; //COL_INT
		    		public final static String price = "price"; //COL_DOUBLE
		    		public final static String float_spd = "float_spd"; //COL_DOUBLE
		    		public final static String index_percent = "index_percent"; //COL_DOUBLE

		    		public final static String start_date = "start_date"; //COL_INT
		    		public final static String end_date = "end_date"; //COL_INT
		    		

		    		public final static String quantity = "quantity"; //COL_DOUBLE
		    		public final static String total_quantity = "total_quantity"; //COL_DOUBLE
		    		

		    		public final static String fx_flt = "fx_flt"; //COL_INT
		    		public final static String currency = "currency"; //COL_INT
		    		
		    		public final static String pipeline = "pipeline"; //COL_STRING
		    		public final static String location = "location"; //COL_STRING
		    		public final static String location_string = "location_string"; //COL_STRING

		    		
//		    		public final static String TRADE_DATE = "trade_date"; //COL_INT
//		    		public final static String TRADER = "trader"; //COL_INT
//		    		public final static String CLIENT = "client"; //COL_INT
//		    		public final static String START_DATE = "start_date"; //COL_INT
//		    		public final static String END_DATE = "end_date"; //COL_INT
//		    		public final static String TOTAL_QUANTITY = "total_quantity"; //COL_DOUBLE
//		    		public final static String BUY_SELL = "buy_sell"; //COL_INT
//		    		public final static String DAILY_PRICING_QUANTITY = "daily_pricing_quantity"; //COL_DOUBLE
//		    		public final static String BUSINESS_DAYS = "business_days"; //COL_INT
////		    		public final static String BENCHMARK = "benchmark"; //COL_INT
//		    		public final static String BENCHMARK_INDEX_LOC = "benchmark_index_loc"; //COL_STRING
//	
//
//		    		public final static String INTERNAL_PORTFOLIO = "internal_portfolio"; //COL_STRING
//		    		
//		    		public final static String PROJ_INDEX = "proj_index"; //COL_INT
//		    		public final static String PROJ_METHOD = "proj_method"; //COL_INT
//		    		
//		    		public static class HIDDEN {
//			    		public final static String TRAN_NUM = "tran_num"; //COL_INT
//			    		public final static String INS_NUM = "ins_num"; //COL_INT
//			    		public final static String PARAM_SEQ_NUM = "param_seq_num"; //COL_INT
//			    		public final static String CURRENT_DATE = "current_date"; //COL_INT
//			    	}
		    	} 


				public static class TRADE_LIST_QUORUM  {
		    		
					public final static String deal_num = "deal_num"; //COL_INT

					public final static String tran_num = "tran_num"; //COL_INT
		    		public final static String toolset = "toolset"; //COL_INT
		    		public final static String ins_type = "ins_type"; //COL_INT
		    		public final static String base_ins_type = "base_ins_type"; //COL_INT

		    		public final static String external_deal_ref = "external_deal_ref"; //COL_STRING
		    		
		    		public final static String quorum_ins_type = "quorum_ins_type"; //COL_STRING
		    		public final static String quorum_deal_type = "quorum_deal_type"; //COL_STRING

		    		public final static String trade_date = "trade_date"; //COL_INT
		    		

		    		public final static String ins_num = "ins_num"; //COL_INT
		    		public final static String counterparty = "counterparty"; //COL_STRING
		    		public final static String external_bunit = "external_bunit"; //COL_INT


		    		public final static String endur_long_name = "endur_long_name"; //COL_STRING
		    		public final static String party_info_quorum_name = "party_info_quorum_name"; //COL_STRING

		    		public final static String param_seq_num = "param_seq_num"; //COL_INT
		    		public final static String buy_sell = "buy_sell"; //COL_INT
		    		public final static String deal_price_string = "deal_price_string"; //COL_STRING
		    		public final static String proj_index_int = "proj_index_int"; //COL_INT
		    		public final static String proj_index = "proj_index"; //COL_INT
		    		public final static String index_location = "index_location"; //COL_INT
		    		public final static String price = "price"; //COL_DOUBLE
		    		public final static String float_spd = "float_spd"; //COL_DOUBLE

		    		public final static String index_percent = "index_percent"; //COL_DOUBLE

 		    		public final static String start_date = "start_date"; //COL_INT
		    		public final static String end_date = "end_date"; //COL_INT
	     		
		    		public final static String quantity = "quantity"; //COL_DOUBLE
		    		public final static String total_quantity = "total_quantity"; //COL_DOUBLE
		    		
		    		
		    		
		    		public final static String fx_flt = "fx_flt"; //COL_INT
		    		public final static String currency = "currency"; //COL_INT
		    		
		    		public final static String pipeline = "pipeline"; //COL_STRING
		    		public final static String location = "location"; //COL_STRING
		    		public final static String location_string = "location_string"; //COL_STRING
		    		
		    		
		    		

		    		
//		    		public final static String TRADE_DATE = "trade_date"; //COL_INT
//		    		public final static String TRADER = "trader"; //COL_INT
//		    		public final static String CLIENT = "client"; //COL_INT
//		    		public final static String START_DATE = "start_date"; //COL_INT
//		    		public final static String END_DATE = "end_date"; //COL_INT
//		    		public final static String TOTAL_QUANTITY = "total_quantity"; //COL_DOUBLE
//		    		public final static String BUY_SELL = "buy_sell"; //COL_INT
//		    		public final static String DAILY_PRICING_QUANTITY = "daily_pricing_quantity"; //COL_DOUBLE
//		    		public final static String BUSINESS_DAYS = "business_days"; //COL_INT
////		    		public final static String BENCHMARK = "benchmark"; //COL_INT
//		    		public final static String BENCHMARK_INDEX_LOC = "benchmark_index_loc"; //COL_STRING
//	
//
//		    		public final static String INTERNAL_PORTFOLIO = "internal_portfolio"; //COL_STRING
//		    		
//		    		public final static String PROJ_INDEX = "proj_index"; //COL_INT
//		    		public final static String PROJ_METHOD = "proj_method"; //COL_INT
//		    		
//		    		public static class HIDDEN {
//			    		public final static String TRAN_NUM = "tran_num"; //COL_INT
//			    		public final static String INS_NUM = "ins_num"; //COL_INT
//			    		public final static String PARAM_SEQ_NUM = "param_seq_num"; //COL_INT
//			    		public final static String CURRENT_DATE = "current_date"; //COL_INT
//			    	}
		    	} 

		    	
			} // end class RAW_REPORT
  
			// this will have one row, with the Data By Unique Key as a Sub Table
			// and also the settings will be saved here
			public static class RAW_BY_UNIQUE_KEY_REPORT_DATA {
				  
//				public final static String CURRENT_DATA = "current_data"; // COL_TABLE

				public static class TOOLSET { 
					public final static String COM_SWAP_POSITION = "com_swap_position"; // COL_TABLE
					public final static String COMMODITY_POSITION = "commodity_position"; // COL_TABLE
				} 
				
				public static class SETTING { 
					 
//					public final static String CURRENCY_LIST = "currency_list";
//					public final static String ALL_CURRENCIES_FLAG = "all_currencies_flag";
//					public final static String NUM_CURRENCIES = "num_currencies";
//
//					public final static String PORTFOLIO_LIST = "portfolio_list";
//					public final static String ALL_PORTFOLIOS_FLAG = "all_portfolios_flag";
//					public final static String NUM_PORTFOLIOS = "num_portfolios";
//
//					public final static String INS_TYPE_LIST = "ins_type_list";
//					public final static String ALL_INS_TYPES_FLAG = "all_ins_types_flag";
//					public final static String NUM_INS_TYPES = "num_ins_types";

				//	public final static String CURRENT_DATA_SOURCE = "current_data_source";
					
					public final static String SHOW_ALL_COLUMNS_FLAG = "show_all_columns_flag"; 

					public final static String RUN_NUMBER = "run_number";
				}
			}  

		} // end class REPORT


    	// For getting data by toolset
    	public static class AB_TRAN_DATA {    
    		// some of these columns match what is in USER_METAL_POSITION_DETAIL
    		// Others do not match, e.g., 'currency' here versus 'currency_id' in USER_METAL_POSITION_DETAIL
    		// and some columns are only here, e.g., ins_num
    		
    		public static final String deal_num = "deal_num"; // COL_INT
    		public static final String tran_num = "tran_num" ; // COL_INT
    		public static final String ins_num = "ins_num"; // COL_INT 

    		public static final String toolset = "toolset"; // COL_INT
    		public static final String ins_type = "ins_type"; // COL_INT
    		
    		public static final String external_bunit = "external_bunit"; // COL_INT
    		
    		public static final String internal_portfolio = "internal_portfolio"; // COL_INT
    		public static final String int_trading_strategy = "int_trading_strategy"; // COL_INT
    		public static final String tran_status = "tran_status" ; // COL_INT

    		public static final String offset_tran_type = "offset_tran_type";   // COL_INT
    		public static final String offset_tran_num = "offset_tran_num"; // COL_INT
    		public static final String currency = "currency"; // COL_INT
    		public static final String position = "position"; // COL_DOUBLE
    		public static final String input_date = "input_date"; // COL_DATE_TIME
     		
    	} // END class AB_TRAN_DATA

    	// Just as a deliminator
		public static class ___FOR_FX_TOOLSET {
		}
 
 
    	
	private static class COM_SWAP_DATA {   
			public static final String deal_num = "deal_num"; // COL_INT
			public static final String tran_num = "tran_num"; // COL_INT
			public static final String ins_num = "ins_num"; // COL_INT
			public static final String ins_type = "ins_type"; // COL_INT
			public static final String buy_sell = "buy_sell"; // COL_INT
			public static final String position = "position"; // COL_DOUBLE 
			public static final String external_bunit = "external_bunit"; // COL_INT
			public static final String portfolio = "portfolio"; // COL_INT
			public static final String external_portfolio = "external_portfolio"; // COL_INT
			public static final String start_date = "start_date"; // COL_INT
			public static final String end_date = "end_date"; // COL_INT
			public static final String offset_tran_type = "offset_tran_type"; // COL_INT
			public static final String offset_tran_num = "offset_tran_num"; // COL_INT

			
			public static class EXTRA {   
				public static final String pay_rec = "pay_rec"; // COL_INT 
				public static final String proj_index = "proj_index"; // COL_INT
				public static final String fixed_price = "fixed_price"; // COL_DOUBLE
				public static final String idx_subgroup = "idx_subgroup"; // COL_INT
				public static final String unit = "unit"; // COL_INT
				
				public static final String year = "year"; // COL_INT
				// vintage is a string
				public static final String vintage = "vintage"; // COL_STRING
				
				// contract_month is a string
				public static final String contract_month = "contract_month"; // COL_STRING
				
				public static final String position_in_mmbtu = "position_in_mmbtu"; // COL_DOUBLE 
 
				public static final String is_exchange_flag = "is_exchange_flag"; // COL_INT
				public static final String is_generic_flag = "is_generic_flag"; // COL_INT
			}
 			  
			public static class PROFILE {   
				public static final String profile_seq_num = "profile_seq_num"; // COL_INT 
				public static final String profile_notnl = "profile_notnl"; // COL_DOUBLE 
				public static final String profile_start_date = "profile_start_date"; // COL_INT 
				public static final String profile_end_date = "profile_end_date"; // COL_INT 
				public static final String rate_dtmn_date = "rate_dtmn_date"; // COL_INT 
				public static final String rate_status = "rate_status"; // COL_INT 
				public static final String pymt_date = "pymt_date"; // COL_INT 
			}
			   
			
		}
		
		public static class COMMODITY_DATA {   
			public static final String deal_num = "deal_num"; // COL_INT
			public static final String tran_num = "tran_num"; // COL_INT
			public static final String ins_num = "ins_num"; // COL_INT
			public static final String ins_type = "ins_type"; // COL_INT
			public static final String buy_sell = "buy_sell"; // COL_INT
			//public static final String position = "position"; // COL_DOUBLE 
			public static final String external_bunit = "external_bunit"; // COL_INT
			public static final String portfolio = "portfolio"; // COL_INT 
			public static final String start_date = "start_date"; // COL_INT
			public static final String end_date = "end_date"; // COL_INT 

			public static class PARAM {   
				public static final String param_seq_num = "param_seq_num"; // COL_INT
				public static final String proj_index = "proj_index"; // COL_INT
				public static final String fx_flt = "fx_flt"; // COL_INT
				public static final String pay_rec = "pay_rec"; // COL_INT
				public static final String settlement_type = "settlement_type"; // COL_INT
				public static final String delivery_type = "delivery_type"; // COL_INT
				public static final String fixed_price = "fixed_price"; // COL_DOUBLE 
			}
			
			
			public static class EXTRA {   
			//	public static final String currency = "currency"; // COL_INT
				public static final String position_in_mmbtu = "position_in_mmbtu"; // COL_DOUBLE 
				 
			}

			public static class PROFILE {   
				public static final String profile_seq_num = "profile_seq_num"; // COL_INT 
				public static final String profile_notnl = "profile_notnl"; // COL_DOUBLE 
				public static final String profile_start_date = "profile_start_date"; // COL_INT 
				public static final String profile_end_date = "profile_end_date"; // COL_INT 
				public static final String rate_dtmn_date = "rate_dtmn_date"; // COL_INT 
				public static final String rate_status = "rate_status"; // COL_INT 
				public static final String pymt_date = "pymt_date"; // COL_INT 
				public static final String num_profiles = "num_profiles"; // COL_INT 
			}
			
		}
		
		public static class COMM_PHYS_DATA extends COMMODITY_DATA {   
			 

			public static class RESET {   
				public static final String reset_seq_num = "reset_seq_num"; // COL_INT 
				public static final String reset_date = "reset_date"; // COL_INT 
				public static final String riend_date = "riend_date"; // COL_INT 
				public static final String fom_date = "fom_date"; // COL_INT 
				public static final String reset_notional = "reset_notional"; // COL_DOUBLE 
				public static final String payment_date = "payment_date"; // COL_INT 
				public static final String value_status = "value_status"; // COL_INT 
				
				
				public static final String reset_data = "reset_data"; // COL_TABLE 
				
			}
			

		}

	    	 
    } // end class COLS

    public enum PRIOR_DATA_SOURCE {
    	PRIOR_DAY_EOD ("Prior Day EOD", 1),
    	TODAY_PRELIM_EOD ("Today Prelim EOD", 2),
    	CUSTOM_SOURCE ("Custom", 3),
    	; 

    	private final String sName;
    	private final int iValue; 
    		  
    	PRIOR_DATA_SOURCE(String sName, int iValue) {
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
    
    public enum CURRENT_DATA_SOURCE {
    	LIVE_DATA ("Live Data", 1),
    	TODAY_EOD ("Today EOD", 2),
    	TODAY_PRELIM_EOD ("Today Prelim EOD", 3),
    	//CUSTOM_SOURCE ("Custom", 4),
    	; 

    	private final String sName;
    	private final int iValue; 
    		  
      	CURRENT_DATA_SOURCE(String sName, int iValue) {
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
 	
    public interface ID_VALUE_AND_NAME_ENUM {
		  public String getName();
		  public int getValue();
    }
    

    public enum REPORT_NAME implements ID_VALUE_AND_NAME_ENUM{

    	TRADE_LIST (1, "Trade Listing", true, "Trade Listing"),
    	TRADE_LIST_QUORUM (2, "Trade Listing - Quorum Compatible", true, "Trade Listing using Quorum Values"),
   
    	NOT_APPLICABLE (101, "Sub-Panel Selected", false, "Sub-Panel Selected"),
    	SUB_PANEL2 (102, "Sub-Panel 2", false, "Sub-Panel 2"),
    	SUB_PANEL3 (103, "Sub-Panel 3", false, "Sub-Panel 3"),
    	  ; 
    	
    	// iValue is the sort order of the reports in the pick-list
    	// while the iValues values can be the same and it would be OK
    	// that said, the intention is that they would be different
    	private final int iValue; 
    	private final String sName;
    	private final boolean bIncludeInGuiPickListFlag; 
    	private final String sDescriptionToShowEndUser; 
    		  
      	  REPORT_NAME(int iValue, String sName, boolean bIncludeInGuiPickListFlag, String sDescriptionToShowEndUser) {
		      this.iValue = iValue; 
		      this.sName = sName;
		      this.bIncludeInGuiPickListFlag = bIncludeInGuiPickListFlag;
		      this.sDescriptionToShowEndUser = sDescriptionToShowEndUser; 
		  }
		  
		  public int getValue() {
			  return this.iValue;
		  }
		  
		  public boolean bIncludeInGuiPickListFlag() {
			  return this.bIncludeInGuiPickListFlag;
		  }
		  public String getName() {
			  return this.sName;
		  }
		  
		  public String getDescriptionToShowEndUser() {
			  return this.sDescriptionToShowEndUser;
		  } 
		   
    }
    
 
    public static class INS_TYPE_NAME {            
    	public static final String ALL = "All";
    	public static final String FX = "FX";
    	public static final String CASH = "Cash";
    	public static final String COMM_EXCH = "COMM-EXCH";
    	public static final String COMM_PHYS = "COMM-PHYS";
    	public static final String COMM_MTL_WARRANT = "COMM-MTL-CLR";
    } 
    
    public static class CONST_VALUES {            
    	public static final int VALUE_OF_TRUE = 1;
       	public static final int VALUE_OF_FALSE = 0;

       	public static final int VALUE_OF_NOT_APPLICABLE = -1; 
   
        public static class ALL {            
           	public static final String NAME = "All"; 
           	public static final int ID = -1;
           	// will always be the first row
           	public static final int LOCATION_IN_PICK_LIST = 1;
        } 
        public static class NONE {            
           	public static final String NAME = "None"; 
           	// This is just a ID that is expected to be unused
           	public static final int ID = -2;
        } 
       	public static final String GENERIC = "Generic";
       	
       	public static final String VINTAGE_FOR_EUA = "EUA";
       	public static final String VINTAGE_FOR_NYM_GEO = "GEO";
        	
       	public static final String LIVE_DATA = "[Live Data]";
        
       	public static final String NO_LEASE_FOUND = "None";
       	 
       	// should not need more than 2 to 4 decimal places max.  Set to 8 for added safety.
    	public static final int VOLUME_PRECISION = 8;
    	
    	// Seems to be that the most you can have is 4 decimal places, so use that in reports
    	// when actually updating LoanDep trades, then go with 8, for just an extra level/margin of safety
    	public static final int VOLUME_PRECISION_FOR_REPORTS = 4;

    	// Interest Rate Precision for Report
    	public static final int INTEREST_RATE_PRECISION_FOR_REPORTS = 4;

    	// Metal Price per Ounce
    	public static final int PRICE_PRECISION_FOR_REPORTS = 4;

    	// USD/CAD precision, i.e., currency when it is not a metal
    	public static final int DECIMAL_PRECISION_FOR_USD_FOR_REPORTS = 2; 

    	// For the FX rate
    	public static final int DECIMAL_PRECISION_FOR_USD_FOR_FX_RATE = 4; 

    	// Width is not functional, so just set it to some value
    	public static final int WIDTH = 12; 
    	// BASE none
    	public static final int BASE = 0; 
    	// Don't show zeros
    	public static final int DO_NOT_SHOW_ZEROES_FLAG = 1; 
    	public static final int SHOW_ZEROES_FLAG = 0; 
     	
       	// i.e., if the difference is less than this, we will ignore it
       	public static final double THRESHOLD_VALUE_FOR_POSITIONS = 0.00000001; 
    	
    	public static final double A_SMALL_VALUE = 0.000001;
       	
    	// Zero out volume
    	public static final double ZERO_OUT_VOLUME = 0;
    	
    	public static final int COMM_WARRANTS_INV = 1000032; 
    	
    	// OpenLink has an enum for there, but don't bother to use it for no particular reason 
    	public static final int COMM_INV = 48640; 
     	public final static int FX = 26001;

    	public static final int ROW_TO_GET_OR_SET = 1;
    } 
    
  	public static class FORMAT {
  		public static class JUSTIFY {
  			public static final String RIGHT = "just=RIGHT";
  		}

  		public static final String DOUBLE_CLICK = "dblclick=TRUE";
  		public static final String DOUBLE_CLICK_AND_RIGHT_JUST = FORMAT.JUSTIFY.RIGHT + "," + FORMAT.DOUBLE_CLICK;
  	}

	public static class DEBUG_LOGFILE {
		  
		public static void logToFile(String sMessage, int iRunNumber, String className) {
			try {

				// as of Aug 2020.. don't actually write anything to a file.. it is not needed for this.  Just OListen
				boolean bDoFileLogging = false;
				
				double dMemSize = SystemUtil.memorySizeDouble();
				double tMemSizeMegs = dMemSize / 1024 / 1024 / 1024;
				String sMemSize = Str.doubleToStr(tMemSizeMegs, 2) + " gigs";

				String sNewMessage = "Time:" + Util.timeGetServerTimeHMS() + "|" + sMemSize + "|" + "Version:"
						+ LIB.VERSION_NUMBER + "| " + sMessage;
				// need a newline
				sNewMessage = sNewMessage + "\r\n";

				String sFileName = className + "." + "TroubleshootingLog" + "." + iRunNumber + "." + "txt";

				String sReportDir = Util.reportGetDirForToday();
				String sFullPath = sReportDir + "/" + sFileName;

				int iAppendFlag = 1;
				
				if (bDoFileLogging == true) {
					Str.printToFile(sFullPath, sNewMessage, iAppendFlag);
				}

				LIB.log(sMessage, className);

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

    	public static final String VERSION_NUMBER = "V1.005 (25Apr2023)";

    	public static void select(Table tDestination, Table tSourceTable, String sWhat, String sWhere, String className) throws OException{
              LIB.select(tDestination, tSourceTable, sWhat, sWhere, false, className);
    	} 

        public static void select(Table tDestination, Table tSourceTable, String sWhat, String sWhere, boolean bLogFlag, String className) throws OException{
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

        public static void viewTable(Table tMaster, String sTableTitle) {
              try{
            	  tMaster.scriptDataHideIconPanel();
            	  tMaster.setTableViewerMode(TABLE_VIEWER_MODE.TABLE_VIEWER_LEGACY);
            	  tMaster.setTableTitle(sTableTitle);
            	  tMaster.viewTable();
                   
              } catch(Exception e ){               
                   // don't log this
              } 
        } 

        public static void log(String sMessage, String className)  {
              try{
                   
                   double dMemSize = SystemUtil.memorySizeDouble();
                   double tMemSizeMegs = dMemSize / 1024 / 1024 / 1024;
                   String sMemSize = Str.doubleToStr(tMemSizeMegs, 2) + " gigs";

                   OConsole.oprint("\n" + className + ":" + LIB.VERSION_NUMBER + ":" + Util.timeGetServerTimeHMS() + ":" + sMemSize + ": " + sMessage);
              }catch(Exception e ){ 
                   // don't log this
              }
        }     
         
         public static void log(String sMessage, Exception e, String className) {
              try{
                   LIB.log("ERROR: " + sMessage + ":" + e.getLocalizedMessage(), className);
              }catch(Exception e1 ){
                   // don't log this
              }
         }   

         public static void adjustStrategyNameIfNeeded(Table tData, String sStrategyColName,
 				int iRunNumber, String className) throws OException{
         	
               try{

            	   // characters start at 0, not 1
     			   final int CHAR_NUMBER_OF_FIRST_CHAR = 0;
     			   final String CHAR_THAT_INDICATES_OLD_STRATEGY_NAME = "X";

     			  	// in the unlikely event that the ref copy didn't work, make the 'name' be the ID number
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
     			  			
     			  			// Strip out a leading X, if found and at least 2 characters
     			  			if (sStrategyName != null && iLen >= 2) {
     			  				String sUpperCaseName = Str.toUpper(sStrategyName);
     			  				if (Str.findSubString(sUpperCaseName, CHAR_THAT_INDICATES_OLD_STRATEGY_NAME) == CHAR_NUMBER_OF_FIRST_CHAR) {
     			  					// Use 'Start Char' to make sure that the 'Chars To Get' is always valid
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
             	  
               } catch(Exception e ){               
                    LIB.log("adjustStrategyNameIfNeeded", e, className);
               } 
         }  
         
         public static Table destroyTable(Table tDestroy) {
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
        
        public static String safeGetStringWithCheckForNull(Table tMaster, String sColName, int iRowNum) throws OException{
            String sReturn = "";
            try{
                 if (Table.isTableValid(tMaster) == 1) {
                       if (tMaster.getColNum(sColName) >= 1) {
                            sReturn = tMaster.getString(sColName, iRowNum);
                       }  
                 }
            } catch(Exception e ){               
                 // don't log this
            }
            if (sReturn == null) {
            	sReturn = "";
            }
            return sReturn;
       }

        public static String safeGetStringNoErrorMessage(Table tMaster, String sColName, int iRowNum) throws OException{
              String sReturn = "";
              try{
                   if (Table.isTableValid(tMaster) == 1) {
                         if (tMaster.getColNum(sColName) >= 1) {
                              sReturn = tMaster.getString(sColName, iRowNum);
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

        public static Table safeGetTable(Table tMaster, int iCol, int iRowNum) throws OException{
              Table tReturn = Util.NULL_TABLE;
              try{
                   if (Table.isTableValid(tMaster) == 1) {
                	   tReturn = tMaster.getTable(iCol, iRowNum);
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
        
        public static void safeSetCellString(Table tMaster, String sColName, int iRowNum, String sValue, String sFormat) throws OException{
            try{
                 if (Table.isTableValid(tMaster) == 1) {
                       if (tMaster.getColNum(sColName) >= 1) {
                    	   tMaster.setCellString(sColName, iRowNum, sValue, sFormat);
                       } else {
                            LIB.log("Can't find a column named " + "'" + sColName + "'" + " for " + "setCellString", "");
                       }
                 }
            } catch(Exception e ){               
                 // don't log this
            }
        } 
        
       public static void safeSetCellInt(Table tMaster, String sColName, int iRowNum, int iValue, String sFormat) throws OException{
           try{
                if (Table.isTableValid(tMaster) == 1) {
                      if (tMaster.getColNum(sColName) >= 1) {
                   	   tMaster.setCellInt(sColName, iRowNum, iValue, sFormat);
                      } else {
                           LIB.log("Can't find a column named " + "'" + sColName + "'" + " for " + "setCellInt", "");
                      }
                }
           } catch(Exception e ){               
                // don't log this
           }
       } 
       
      public static void safeSetCellTable(Table tMaster, String sColName, int iRowNum, Table tValue, String sFormat) throws OException{
          try{
               if (Table.isTableValid(tMaster) == 1) {
                     if (tMaster.getColNum(sColName) >= 1) {
                  	   tMaster.setCellTable(sColName, iRowNum, tValue, sFormat);
                     } else {
                          LIB.log("Can't find a column named " + "'" + sColName + "'" + " for " + "setCellTable", "");
                     }
               }
          } catch(Exception e ){               
               // don't log this
          }
      } 
         
        
        public static void safeSetColTitle(Table tMaster, String sColName, String sColTitle) throws OException{
            try{
                 if (Table.isTableValid(tMaster) == 1) {
                	 if (tMaster.getColNum(sColName) >= 1) {
                		 tMaster.setColTitle(sColName, sColTitle);
                	 } else {
                		 LIB.log("Can't find a column named " + "'" + sColName + "'" + " for " + "setColTitle", "");
                	 }
                 }
            } catch(Exception e ){               
                 // don't log this
            }
        } 
        
        public static void safeSetColTitle(Table tMaster, String sColName, String sColTitle, String sWebServiceColTitle, boolean bWebServiceFlag) throws OException{
            try{
                 if (Table.isTableValid(tMaster) == 1) {
                	 if (tMaster.getColNum(sColName) >= 1) {
                		 
                		 // always set the title as default (from the non-Web Services one) 
                		 // as we need to at least do this if the Web Services one is null
                		 try {
                    		 tMaster.setColTitle(sColName, sColTitle);
                		 } catch (Exception e) {
                			 // Do not log this
                		 }
                		 
                		 if (bWebServiceFlag == true) {
                			 if (sWebServiceColTitle != null) {
                    			 try {
                    				 if (Str.len(sWebServiceColTitle) >= 1) {
                                		 tMaster.setColTitle(sColName, sWebServiceColTitle);
                    				 }
                    			 } catch (Exception e) {
                    				 // don't log this
                    			 }
                			 }
                			 
                		 }
                	 } else {
                		 // Don't log this if it is invoked via a WebService
                		 if (bWebServiceFlag == false) {
                    		 LIB.log("Can't find a column named " + "'" + sColName + "'" + " for " + "setColTitle", "");
                		 }
                	 }
                 }
            } catch(Exception e ){               
                 // don't log this
            }
        } 

        public static void safeSetCellIntWithDoubleClick(Table tMaster, String sColName, int iRowNum, int iValue) throws OException{
        	safeSetCellInt(tMaster, sColName, iRowNum, iValue, FORMAT.DOUBLE_CLICK);
        }


        public static void safeSetCellStringWithDoubleClick(Table tMaster, String sColName, int iRowNum, String sValue) throws OException{
        	safeSetCellString(tMaster, sColName, iRowNum, sValue, FORMAT.DOUBLE_CLICK);
        }
        
        public static void safeSetCellStringWithDoubleClickAndRightJustify(Table tMaster, String sColName, int iRowNum, String sValue) throws OException{
        	safeSetCellString(tMaster, sColName, iRowNum, sValue, "symbol=TABLE," + FORMAT.DOUBLE_CLICK_AND_RIGHT_JUST);
        }
        
        public static void safeSetCellIntWithDoubleClickAndRightJustify(Table tMaster, String sColName, int iRowNum, int iValue) throws OException{
        	safeSetCellInt(tMaster, sColName, iRowNum, iValue, "symbol=TABLE," + FORMAT.DOUBLE_CLICK_AND_RIGHT_JUST);
        } 
        
        public static void safeSetDouble(Table tMaster, String sColName, int iRowNum, double dValue) throws OException{
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

        public static void safeAddCol(Table tMaster, String sColName, COL_TYPE_ENUM colType) throws OException{
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
        
        public static void safeInsertCol(Table tMaster, String sColName, COL_TYPE_ENUM colType, int iColNumToInsertBefore) throws OException{
			try {
				if (Table.isTableValid(tMaster) == 1) {
					// Only add if not already there
					if (tMaster.getColNum(sColName) < 1) {
						if (iColNumToInsertBefore >= 1 && iColNumToInsertBefore <= LIB.safeGetNumCols(tMaster)) {
							tMaster.insertCol(sColName, iColNumToInsertBefore, colType);
						} else {// add to the end
							tMaster.addCol(sColName, colType);
						}
					}
				}
			} catch (Exception e) {
				// don't log this
			}
        }
        
        public static void safeInsertCol(Table tMaster, String sColName, COL_TYPE_ENUM colType, String sColNameToInsertBefore) throws OException{
			try {
				if (Table.isTableValid(tMaster) == 1) {
					// Only add if not already there
					if (tMaster.getColNum(sColName) < 1) {
						if (tMaster.getColNum(sColNameToInsertBefore) >= 1) {
							tMaster.insertCol(sColName, sColNameToInsertBefore, colType);
						} else
						{
							// add to the end
							tMaster.addCol(sColName, colType);
						}

					}
				}
			} catch (Exception e) {
				// don't log this
			}
        }
        
		public static void safeAddCellCol(Table tMaster) throws OException {
			try {
				if (Table.isTableValid(tMaster) == 1) {
					final String sColName = "xxx";
					LIB.safeAddCol(tMaster, sColName, COL_TYPE_ENUM.COL_CELL);
					LIB.safeColHide(tMaster, sColName);
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
        
		public static void safeColShow(Table tMaster, String sColName) throws OException {
			try {
				if (Table.isTableValid(tMaster) == 1) {
					// Only hide if already there
					if (tMaster.getColNum(sColName) >= 1) {
						tMaster.colShow(sColName);
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
        
        public static void safeSetColFormatAsDateQuorum(Table tMaster, String sColName) throws OException{
            try{
                 tMaster.setColFormatAsDate(sColName, DATE_FORMAT.DATE_FORMAT_MDY_SLASH, DATE_LOCALE.DATE_LOCALE_US);  
            } catch(Exception e ){               
                 // don't log this
            } 
        } 
                
   } // END LIB
  
}

