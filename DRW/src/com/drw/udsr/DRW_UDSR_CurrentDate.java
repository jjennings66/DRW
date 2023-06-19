package com.drw.udsr;

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
import com.olf.openjvs.enums.SCRIPT_TYPE_ENUM;
import com.olf.openjvs.enums.SHM_USR_TABLES_ENUM;
import com.olf.openjvs.enums.TABLE_VIEWER_MODE;

/*

Name: DRW_UDSR_CurrentDate

Description:

1) Set this up as a UDSR.  No Dependencies are needed
  
Revision History:
29-May-2023  	Brian       New Script  
 
*/

@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_SIM_RESULT)
@PluginType(SCRIPT_TYPE_ENUM.MAIN_SCRIPT)

public class DRW_UDSR_CurrentDate implements IScript {

	// TODO, set to false for production
	static boolean DEBUG_FLAG = false;

	public void execute(IContainerContext context) throws OException {

		String className = this.getClass().getSimpleName();

		try {
			// This is functional as we include only payments with dates on or after this day
			int iToday = OCalendar.today();

			LIB.logForDebug("RUNNING DRW_UDSR_CurrentDate Result, Current Date: " +
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
			 
			String sToday = OCalendar.formatDateInt(iToday, DATE_FORMAT.DATE_FORMAT_DMLY_NOSLASH);

			int iMaxRow = returnt.addRow();
			LIB.safeSetInt(returnt, COLS.RESULT.current_date, iMaxRow, iToday);
			LIB.safeSetString(returnt, COLS.RESULT.current_date_string, iMaxRow, sToday);
			
			Table tRefData = Ref.getInfo();
			String sTableTitle = "Ref Data";
			tRefData.setTableName(sTableTitle);
			tRefData.setTableTitle(sTableTitle);
			LIB.safeSetTable(returnt, COLS.RESULT.ref_data, iMaxRow, tRefData);
  		}

		if (iOperation == CONST_VALUES.USER_RES_OP_FORMAT) {
			FUNC.format_result(returnt, className);
		}
	}


	private static class FUNC {
  		 
		private static void format_result(Table tMaster, String className) throws OException {

			try {

				tMaster.setColTitle(COLS.RESULT.current_date, "Current\nDate");
				tMaster.setColTitle(COLS.RESULT.current_date_string, "Current\nDate\nas String");
				tMaster.setColTitle(COLS.RESULT.ref_data, "Ref Data");

				LIB.safeSetColFormatAsDate(tMaster, COLS.RESULT.current_date);
				 
			} catch (Exception e) {
				LIB.log("format_result", e, className);
			}
		}

	} // END private static class FUNC

	public static class CREATE_TABLE {

		static void addColsToResultTable(Table tMaster, String className) {

			try {
 
				LIB.safeAddCol(tMaster, COLS.RESULT.current_date,  COL_TYPE_ENUM.COL_INT);
				LIB.safeAddCol(tMaster, COLS.RESULT.current_date_string,  COL_TYPE_ENUM.COL_STRING);
				LIB.safeAddCol(tMaster, COLS.RESULT.ref_data,  COL_TYPE_ENUM.COL_TABLE);
  				 
				FUNC.format_result(tMaster, className);

			} catch (Exception e) {
				LIB.log("addColsToResultTable", e, className);
			}
		}

	} // end CREATE_TABLE

    public static class COLS {
 
		public static class RESULT {

			public static final String current_date = "current_date"; // COL_INT
			public static final String current_date_string = "current_date_string"; // COL_STRING
			public static final String ref_data = "ref_data"; // COL_TABLE
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
	    
 
public static class CONST_VALUES {
	public static final int VALUE_OF_TRUE = 1;
	public static final int VALUE_OF_FALSE = 0;

	public static final int ROW_TO_GET_OR_SET = 1;

	// OpenLink has an enum for this, but don't bother to use it for no particular reason, i.e., user_res_op***
	public final static int USER_RES_OP_CALCULATE = 1;
	public final static int USER_RES_OP_FORMAT = 2;

	// OpenLink has an enum for this, but don't bother to use it for no particular reason, i.e., TRAN_RESULTS_ROW, etc.
	public final static int TRAN_RESULTS_ROW = 1;
	public final static int GEN_RESULTS_ROW = 4;
 
}
 
public static class LIB {

	public static final String VERSION_NUMBER = "V1.003 (29May2023)";

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
