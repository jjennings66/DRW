package com.drw.udsr;
 
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

/*

Name: DRW_UDSR_CashMonthInfo

Description:

1) Set this up as a UDSR. 

2) Dependency:  
     Cash Month Info 
     Size by Leg Result
     Settlement Type

3) This is like Cash Month Info, but has the volumes signed (positive or negative)
  
Revision History:
01-Jun-2023  	Brian       New Script  
 
*/ 

@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_SIM_RESULT)
@PluginType(SCRIPT_TYPE_ENUM.MAIN_SCRIPT)

public class DRW_UDSR_CashMonthInfo implements IScript {

	// TODO, set to false for production
	static boolean DEBUG_FLAG = false;

	public void execute(IContainerContext context) throws OException {

		String className = this.getClass().getSimpleName();

		try {
			// This is functional as we include only payments with dates on or after this day
			int iToday = OCalendar.today();

			LIB.logForDebug("RUNNING DRW_UDSR_CashMonthInfo Result, Current Date: " +
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

		if (iOperation == CONST_VALUES.USER_RES_OP_CALCULATE) {
			
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

			Table tTranResults = LIB.safeGetTable(tSims, "result_class", CONST_VALUES.TRAN_RESULTS_ROW);
			Table tLegResults = LIB.safeGetTable(tSims, "result_class", CONST_VALUES.LEG_RESULTS_ROW);
			Table tGenResults = LIB.safeGetTable(tSims, "result_class", CONST_VALUES.GEN_RESULTS_ROW);

			// TOxDO
			// LIB.viewTable(tTranResults, "tTranResults");

			// TOxDO
			// LIB.viewTable(tLegResults, "tLegResults");

			// TOxDO
			//LIB.viewTable(tGenResults, "tGenResults");
			
			int iRowNumCashMonthInfo = tGenResults.unsortedFindInt("result_type", PFOLIO_RESULT_TYPE.CASH_MONTH_INFO_RESULT.toInt());
			if(iRowNumCashMonthInfo >= 1) {
				Table tCashMonthInfo = LIB.safeGetTable(tGenResults, "result", iRowNumCashMonthInfo);

				if (LIB.safeGetNumRows(tCashMonthInfo) >= 1) {
					FUNC.doEverythingCalculate(tTranPointersFromArgt, returnt, tCashMonthInfo, tTranResults, tLegResults, iToday, className);
				}
			}

		}

		if (iOperation == CONST_VALUES.USER_RES_OP_FORMAT) {
			FUNC.format_result(returnt, className);
		}

	}


	private static class FUNC {
  
		private static void doEverythingCalculate(Table tTranPointersFromArgt, Table returnt, Table tCashMonthInfo, 
				Table tTranResults, Table tLegResults, int iToday, String className) throws OException {

			try {
				
				// Start by copying from the original
				tCashMonthInfo.copyRowAddAllByColName(returnt);

				// add ins type and tran number as *double*
				{
					final String SETTLEMENT_TYPE_TEMP_COL_NAME = Str.intToStr(com.olf.openjvs.enums.PFOLIO_RESULT_TYPE.SETTLEMENT_TYPE_RESULT.toInt());

					LIB.safeAddCol(returnt, SETTLEMENT_TYPE_TEMP_COL_NAME, COL_TYPE_ENUM.COL_DOUBLE);
					
					// join by deal_num and deal_leg
 					String sWhat = SETTLEMENT_TYPE_TEMP_COL_NAME + ", " + 
 									COLS.RESULT.EXTRA.ins_type + ", " + 
 							COLS.RESULT.HIDDEN.tran_num_for_double_click;
					String sWhere = "deal_num EQ $" + COLS.RESULT.deal_num + "  AND " + 
							"deal_leg EQ $" + COLS.RESULT.deal_leg;
					LIB.select(returnt, tTranResults, sWhat, sWhere, true, className);
					  
					// manually calculate Tran Num as an int (from the double)
					try {
						if (LIB.safeGetNumRows(returnt) >= 1) {
							returnt.mathAddColConst(COLS.RESULT.HIDDEN.tran_num_for_double_click, 0, COLS.RESULT.EXTRA.tran_num);
						}
					} catch (Exception e) {
						// do not log this
					}  

	 				// manually calculate Settlement Type as an int (from the double)
					try {
						if (LIB.safeGetNumRows(returnt) >= 1) {
							returnt.mathAddColConst(SETTLEMENT_TYPE_TEMP_COL_NAME, 0, COLS.RESULT.EXTRA.settlement_type);
						}
					} catch (Exception e) {
						// do not log this
					} 
					
					// delete the temp column
					LIB.safeDelCol(returnt, SETTLEMENT_TYPE_TEMP_COL_NAME);
				}
 
				try {
					
						final String SIZE_BY_LEG = Str.intToStr(com.olf.openjvs.enums.PFOLIO_RESULT_TYPE.SIZE_BY_LEG_RESULT.toInt());
						Table tSizeByLeg = Table.tableNew();
						LIB.safeAddCol(tSizeByLeg, COLS.RESULT.deal_num, COL_TYPE_ENUM.COL_INT);
						LIB.safeAddCol(tSizeByLeg, COLS.RESULT.deal_leg, COL_TYPE_ENUM.COL_INT);
						LIB.safeAddCol(tSizeByLeg, "size_by_leg", COL_TYPE_ENUM.COL_DOUBLE);
						LIB.safeAddCol(tSizeByLeg, "abs_size_by_leg", COL_TYPE_ENUM.COL_DOUBLE);
						LIB.safeAddCol(tSizeByLeg, "multiplier", COL_TYPE_ENUM.COL_INT);

						{
							// get a distinct list
							String sWhat = "DISTINCT, " + COLS.RESULT.deal_num + ", " + 
										  COLS.RESULT.deal_leg;
							// get all rows 
							String sWhere = COLS.RESULT.deal_leg + " GE -1";
							LIB.select(tSizeByLeg, tLegResults, sWhat, sWhere, false, className);
						}

						{
							// Now sum the volume
							String sWhat = "SUM, " + SIZE_BY_LEG + "(size_by_leg)";
							String sWhere = COLS.RESULT.deal_num + " EQ $" + COLS.RESULT.deal_num + 
									 " AND " + COLS.RESULT.deal_leg + " EQ $" +  COLS.RESULT.deal_leg;
							LIB.select(tSizeByLeg, tLegResults, sWhat, sWhere, false, className);
						}

						// convert to being a -1 or +1
						tSizeByLeg.mathAddColConst("size_by_leg", 0, "abs_size_by_leg");
						tSizeByLeg.mathABSCol("abs_size_by_leg");
						tSizeByLeg.mathDivCol("size_by_leg", "abs_size_by_leg", "multiplier");
 
						// get rid of zeroes
						tSizeByLeg.deleteWhereValue("multiplier", 0);
						
						// add a temp column and default to 1
						LIB.safeAddCol(returnt, "multiplier", COL_TYPE_ENUM.COL_INT);
						LIB.safeSetColValInt(returnt, "multiplier", 1);
						
						// select into returnt the value of multiplier
						{
							{
								// Now sum the volume
								String sWhat = "multiplier";
								String sWhere = COLS.RESULT.deal_num + " EQ $" + COLS.RESULT.deal_num + 
										 " AND " + COLS.RESULT.deal_leg + " EQ $" +  COLS.RESULT.deal_leg;
								LIB.select(returnt, tSizeByLeg, sWhat, sWhere, false, className);
							}
							
							// calc the signed value
							returnt.mathMultCol(COLS.RESULT.volume, "multiplier", COLS.RESULT.volume);
							
							// delete the temp column 
							LIB.safeDelCol(returnt, "multiplier");
						}
						
						// TODO
						// LIB.viewTable(tSizeByLeg, "tSizeByLeg");
						
						tSizeByLeg = LIB.destroyTable(tSizeByLeg);
					
				} catch (Exception e) {
					// do not log this
				}  
				
				
				// TOxDO
				// LIB.viewTable(tCashMonthInfo, "tCashMonthInfo");
				
 
				// TOxDO
				// LIB.viewTable(returnt, "returnt step 100");

				// this is cosmetic
				String sGroup = COLS.RESULT.deal_num + ", " + 
						COLS.RESULT.deal_leg + ", " + 
						COLS.RESULT.price_band + ", " + 
						COLS.RESULT.date;
				returnt.group(sGroup);
 
			} catch (Exception e) {
				LIB.log("doEverythingCalculate", e, className);
			}
		}

		private static void format_result(Table tMaster, String className) throws OException {

			try {
			 
				// Out of the box columns	
				tMaster.setColTitle(COLS.RESULT.deal_num, "\nDeal\nNum");
				tMaster.setColTitle(COLS.RESULT.deal_leg, "Param\nSeq\nNum");
				tMaster.setColTitle(COLS.RESULT.price_band, "\nPrice\nBand");
				tMaster.setColTitle(COLS.RESULT.date, "\nStart\nDate");
				tMaster.setColTitle(COLS.RESULT.price, "\n\nPrice");
				tMaster.setColTitle(COLS.RESULT.volume, "\n\nVolume");
				tMaster.setColTitle(COLS.RESULT.tier, "\n\nTier");
				tMaster.setColTitle(COLS.RESULT.end_date, "\nEnd\nDate");
				tMaster.setColTitle(COLS.RESULT.event_source, "\nEvent\nSource");
				tMaster.setColTitle(COLS.RESULT.ins_seq_num, "Instrument\nSeq\nNum");
				tMaster.setColTitle(COLS.RESULT.ins_source_id, "Instrument\nSource\nID");
				tMaster.setColTitle(COLS.RESULT.param_id, "\nParam\nID");

				tMaster.setColTitle(COLS.RESULT.EXTRA.settlement_type, "\nSettle\nType");
				tMaster.setColTitle(COLS.RESULT.EXTRA.ins_type, "\nIns Type");
				tMaster.setColTitle(COLS.RESULT.EXTRA.tran_num, "\nTran\nNum");
 
				// Date Formatting
				LIB.safeSetColFormatAsDate(tMaster, COLS.RESULT.date);
				LIB.safeSetColFormatAsDate(tMaster, COLS.RESULT.end_date);
 
				// Ref Formatting 
				LIB.safeSetColFormatAsRef(tMaster, COLS.RESULT.price_band, SHM_USR_TABLES_ENUM.PRICE_BAND_TABLE);
				LIB.safeSetColFormatAsRef(tMaster, COLS.RESULT.event_source, SHM_USR_TABLES_ENUM.EVENT_SOURCE_TABLE);
				LIB.safeSetColFormatAsRef(tMaster, COLS.RESULT.EXTRA.ins_type, SHM_USR_TABLES_ENUM.INS_TYPE_TABLE);
				LIB.safeSetColFormatAsRef(tMaster, COLS.RESULT.EXTRA.settlement_type, SHM_USR_TABLES_ENUM.SETTLEMENT_TYPE_TABLE);

				// this is not used
				final int width = 10;
				final int volume_precision = 0;
				final int price_precision = 4;
//				final int mtm_precision = 2;
//				final int df_precision = 8;
				tMaster.setColFormatAsNotnl(COLS.RESULT.volume, width, volume_precision, 0);
				tMaster.setColFormatAsNotnl(COLS.RESULT.price, width, price_precision, 0);

				// Hide Cols
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
				LIB.safeAddCol(tMaster, COLS.RESULT.EXTRA.ins_type,  COL_TYPE_ENUM.COL_INT);
				LIB.safeAddCol(tMaster, COLS.RESULT.deal_leg,  COL_TYPE_ENUM.COL_INT);
				LIB.safeAddCol(tMaster, COLS.RESULT.EXTRA.settlement_type,  COL_TYPE_ENUM.COL_INT);
				LIB.safeAddCol(tMaster, COLS.RESULT.price_band,  COL_TYPE_ENUM.COL_INT);
				LIB.safeAddCol(tMaster, COLS.RESULT.date,  COL_TYPE_ENUM.COL_INT);
				LIB.safeAddCol(tMaster, COLS.RESULT.price,  COL_TYPE_ENUM.COL_DOUBLE);
				LIB.safeAddCol(tMaster, COLS.RESULT.volume,  COL_TYPE_ENUM.COL_DOUBLE);
				LIB.safeAddCol(tMaster, COLS.RESULT.tier,  COL_TYPE_ENUM.COL_INT);
				LIB.safeAddCol(tMaster, COLS.RESULT.end_date,  COL_TYPE_ENUM.COL_INT);
				LIB.safeAddCol(tMaster, COLS.RESULT.event_source,  COL_TYPE_ENUM.COL_INT);
				LIB.safeAddCol(tMaster, COLS.RESULT.ins_seq_num,  COL_TYPE_ENUM.COL_INT);
				LIB.safeAddCol(tMaster, COLS.RESULT.ins_source_id,  COL_TYPE_ENUM.COL_INT);
				LIB.safeAddCol(tMaster, COLS.RESULT.param_id,  COL_TYPE_ENUM.COL_INT);
 
				// Add at the end
				// Though tran_num is generally an integer, for double click to work, it needs to be a DOUBLE
				LIB.safeAddCol(tMaster, COLS.RESULT.EXTRA.tran_num,  COL_TYPE_ENUM.COL_INT);
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

				public static final String deal_leg = "deal_leg"; //COL_INT
			public static final String price_band = "price_band"; //COL_INT
			public static final String date = "date"; //COL_INT
			public static final String price = "price"; //COL_DOUBLE
			public static final String volume = "volume"; //COL_DOUBLE
			public static final String tier = "tier"; //COL_INT
			public static final String end_date = "end_date"; //COL_INT
			public static final String event_source = "event_source"; //COL_INT
			public static final String ins_seq_num = "ins_seq_num"; //COL_INT
			public static final String ins_source_id = "ins_source_id"; //COL_INT
			public static final String param_id = "param_id"; //COL_INT

			public static class EXTRA {
				public static final String tran_num = "tran_num"; //COL_INT
				public static final String ins_type = "ins_type"; //COL_INT
				public static final String settlement_type = "settlement_type"; //COL_INT
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

	// OpenLink has an enum for this, but don't bother to use it for no particular reason, i.e., user_res_op***
	public final static int USER_RES_OP_CALCULATE = 1;
	public final static int USER_RES_OP_FORMAT = 2;

	// OpenLink has an enum for this, but don't bother to use it for no particular reason, i.e., TRAN_RESULTS_ROW, etc.
	public final static int TRAN_RESULTS_ROW = 1;
	public final static int LEG_RESULTS_ROW = 3;
	public final static int GEN_RESULTS_ROW = 4;
}

  
public static class LIB {

	public static final String VERSION_NUMBER = "V1.005 (01Jun2023)";

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
			double tMemSizeMegs = dMemSize / 1024 / 1024 / 1024;
			String sMemSize = Str.doubleToStr(tMemSizeMegs, 2) + " gigs";

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


