package com.drw.dealimport;
  

import com.olf.openjvs.DBUserTable;
import com.olf.openjvs.DBaseTable;
import com.olf.openjvs.IContainerContext;
import com.olf.openjvs.IScript;
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
import com.olf.openjvs.enums.SCRIPT_CATEGORY_ENUM;
import com.olf.openjvs.enums.SCRIPT_TYPE_ENUM;
import com.olf.openjvs.enums.SHM_USR_TABLES_ENUM;
import com.olf.openjvs.enums.TABLE_VIEWER_MODE;

/*
Script Name: DRW_DealImportViewUserTableData
Description:  DRW_DealImportViewUserTableData

Revision History
20-Apr-2023   Brian   New Script
25-Apr-2023   Brian   Updated user table name to user_di_staging

*/

@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_GENERIC)
@PluginType(SCRIPT_TYPE_ENUM.MAIN_SCRIPT)
public class DRW_DealImportViewUserTableData implements IScript { 
	public void execute(IContainerContext context) throws OException {
		String className = this.getClass().getSimpleName();
		int iRunNumber = DEBUG_LOGFILE.getRunNumber(className); 
		
		try {
		
			DEBUG_LOGFILE.logToFile("START", iRunNumber, className);
  			
			doEverything(iRunNumber, className);
			DEBUG_LOGFILE.logToFile("END", iRunNumber, className);
			 
		} catch (Exception e) {
			// do not log this
		}
		Util.exitSucceed();
	}

	public void doEverything(int iRunNumber, String className) throws OException {
		try {

			Table tOutput = Table.tableNew();
			LIB.safeAddCol(tOutput, "id_number", COL_TYPE_ENUM.COL_INT);

			LIB.safeAddCol(tOutput, "last_update", COL_TYPE_ENUM.COL_DATE_TIME);
			LIB.safeAddCol(tOutput, "update_user", COL_TYPE_ENUM.COL_INT);
			LIB.safeAddCol(tOutput, "data", COL_TYPE_ENUM.COL_TABLE);
			LIB.safeAddCol(tOutput, "status", COL_TYPE_ENUM.COL_STRING);
			LIB.safeAddCol(tOutput, "endur_deal_num", COL_TYPE_ENUM.COL_INT);
			LIB.safeAddCol(tOutput, "source_system", COL_TYPE_ENUM.COL_STRING);
			LIB.safeAddCol(tOutput, "source_system_deal", COL_TYPE_ENUM.COL_STRING);
			
			LIB.safeAddCol(tOutput, "num_rows_for_deal", COL_TYPE_ENUM.COL_INT);

			LIB.safeAddCol(tOutput, "ins_type", COL_TYPE_ENUM.COL_STRING);
			LIB.safeAddCol(tOutput, "ins_type", COL_TYPE_ENUM.COL_STRING);
			LIB.safeAddCol(tOutput, "counterparty", COL_TYPE_ENUM.COL_STRING);

			LIB.safeAddCol(tOutput, "info_comment", COL_TYPE_ENUM.COL_STRING);
			 
			LIB.safeAddCol(tOutput, "the_blob", COL_TYPE_ENUM.COL_BYTE_ARRAY);
			
			LIB.safeSetColFormatAsRef(tOutput, "update_user", SHM_USR_TABLES_ENUM.PERSONNEL_TABLE);
			
			
			Table tSourceData = Table.tableNew();
			String sSQL = "select * from " + USER_TABLE.user_di_staging;
			LIB.execISql(tSourceData, sSQL, false, className);
			
			if (LIB.safeGetNumRows(tSourceData) >= 1) {
				tSourceData.copyRowAddAllByColName(tOutput);
			}
			
			// populate the Data column
			if (LIB.safeGetNumRows(tOutput) >= 1) {
				int iNumRows = LIB.safeGetNumRows(tOutput);
				for (int iCounter = 1; iCounter <= iNumRows; iCounter++) {
					Table tSubTable = tOutput.getTableFromByteArray("the_blob", iCounter);
					
					String sInsType = LIB.safeGetString(tSubTable, "ins_type", CONST_VALUES.ROW_TO_GET_OR_SET);
					String sCounterparty = LIB.safeGetString(tSubTable, "counterparty", CONST_VALUES.ROW_TO_GET_OR_SET);
					
					LIB.safeSetString(tOutput, "ins_type", iCounter, sInsType);
					LIB.safeSetString(tOutput, "counterparty", iCounter, sCounterparty);
					
					String sSourceSystemDeal = LIB.safeGetString(tOutput, "source_system_deal", iCounter);
					String sTableTitle = "Data for Deal " + sSourceSystemDeal;
					tSubTable.setTableTitle(sTableTitle);
					String sTableName = "Data";
					tSubTable.setTableName(sTableName);
					LIB.safeSetTable(tOutput, "data", iCounter, tSubTable);
				}
			}
			
			// Don't need the blob
			LIB.safeDelCol(tOutput, "the_blob");

			
//			id_number	CHECK_MARK					COL_INT	FMT_NONE						id_number	
//			insert_time	CHECK_MARK					COL_DATE_TIME	FMT_DATE_TIME	MDY_SLASH	U.S.	NONE			insert_time	
//			last_update	CHECK_MARK					COL_DATE_TIME	FMT_DATE_TIME	MDY_SLASH	U.S.	NONE			last_update	
//			update_user	CHECK_MARK					COL_INT	FMT_NONE						update_user	
//			status	CHECK_MARK					COL_STRING	FMT_NONE						status	
//			info_comment	CHECK_MARK					COL_STRING	FMT_NONE						info_comment	
//			endur_deal_num	CHECK_MARK					COL_INT	FMT_NONE						endur_deal_num	
//			source_system	CHECK_MARK					COL_STRING	FMT_NONE						source_system	
//			source_system_deal	CHECK_MARK					COL_STRING	FMT_NONE						source_system_deal	
//			source_system_deal_int	CHECK_MARK					COL_INT	FMT_NONE						source_system_deal_int	
//			num_rows_for_deal	CHECK_MARK					COL_INT	FMT_NONE						num_rows_for_deal	
//			extra_int1	CHECK_MARK					COL_INT	FMT_NONE						extra_int1	
//			extra_int2	CHECK_MARK					COL_INT	FMT_NONE						extra_int2	
//			extra_int3	CHECK_MARK					COL_INT	FMT_NONE						extra_int3	
//			extra_string1	CHECK_MARK					COL_STRING	FMT_NONE						extra_string1	
//			extra_string2	CHECK_MARK					COL_STRING	FMT_NONE						extra_string2	
//			extra_string3	CHECK_MARK					COL_STRING	FMT_NONE						extra_string3	
//			the_blob						COL_BYTE_ARRAY	FMT_NONE						the_blob	

			
			LIB.viewTable(tOutput, "user_di_deal_import_staging, num Rows: " + LIB.safeGetNumRows(tOutput));
			
			 
		} catch (Exception e) {
			// do not log this
		}	 
	}
		


public static class USER_TABLE {
	public static String user_di_staging = "user_di_staging";
}

	 
	public static class CONST_VALUES {
	public static final int VALUE_OF_TRUE = 1;
	public static final int VALUE_OF_FALSE = 0;

	public static final int VALUE_OF_NOT_APPLICABLE = -1;

	public static final double A_SMALL_NUMBER = 0.000001;

	public static final int ROW_TO_GET_OR_SET = 1;
	

}

public static class DEBUG_LOGFILE {

	public static void logToFile(String sMessage, int iRunNumber, String className) {
		try {

			  
			double dMemSize = SystemUtil.memorySizeDouble();
			double tMemSizeMegs = dMemSize / 1024 / 1024;
			String sMemSize = Str.doubleToStr(tMemSizeMegs, 2) + " megs";

			String sNewMessage = "Time:" + Util.timeGetServerTimeHMS() + "|" + sMemSize + "|" + "Version:"
						+ LIB.VERSION_NUMBER + "| " + sMessage;

			// need a newline
			sNewMessage = sNewMessage + "\r\n";

			String sFileName = className + "." + "TroubleshootingLog" + "." + iRunNumber + "." + "txt";

			String sReportDir = Util.reportGetDirForToday();
			String sFullPath = sReportDir + "/" + sFileName;

			int iAppendFlag = 1;
			Str.printToFile(sFullPath, sNewMessage, iAppendFlag);

			LIB.logForDebug(sMessage, className);

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

	public static final String VERSION_NUMBER = "V1.008 (25Apr2023)";

	public static void select(Table tDestination, Table tSourceTable, String sWhat, String sWhere, String className)
			throws OException {
		LIB.select(tDestination, tSourceTable, sWhat, sWhere, false, className);
	}

	private static void select(Table tDestination, Table tSourceTable, String sWhat, String sWhere,
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

	public static void logForDebug(String sMessage, String className) throws OException {
		try {
			 LIB.log(sMessage, className);
			
		} catch (Exception e) {
			// don't log this
		}
	}

	public static void log(String sMessage, String className) throws OException {
		try {

			double dMemSize = SystemUtil.memorySizeDouble();
			double tMemSizeMegs = dMemSize / 1024 / 1024;
			String sMemSize = Str.doubleToStr(tMemSizeMegs, 2) + " megs";

			OConsole.oprint("\n" + className + ":" + LIB.VERSION_NUMBER + ":" + Util.timeGetServerTimeHMS() + ":"
					+ sMemSize + ": " + sMessage);
		} catch (Exception e) {
			// don't log this
		}
	}

	public static void log(String sMessage, Exception e, String className) throws OException {
		try {
			LIB.log("ERROR: " + sMessage + ":" + e.getLocalizedMessage(), className);
		} catch (Exception e1) {
			// don't log this
		}
	}

	public static Table destroyTable(Table tDestroy) throws OException {
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

	public static void adjustStrategyNameIfNeeded(Table tData, String sStrategyColName, int iRunNumber,
			String className) throws OException {

		try {

			// characters start at 0, not 1
			final int CHAR_NUMBER_OF_FIRST_CHAR = 0;
			final String CHAR_THAT_INDICATES_OLD_STRATEGY_NAME = "X";

			// in the unlikely event that the ref copy didn't work, make the
			// 'name' be the ID number
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

					// Strip out a leading X, if found and at least 2
					// characters
					if (sStrategyName != null && iLen >= 2) {
						String sUpperCaseName = Str.toUpper(sStrategyName);
						if (Str.findSubString(sUpperCaseName,
								CHAR_THAT_INDICATES_OLD_STRATEGY_NAME) == CHAR_NUMBER_OF_FIRST_CHAR) {
							// Use 'Start Char' to make sure that the 'Chars
							// To Get' is always valid
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

		} catch (Exception e) {
			LIB.log("adjustStrategyNameIfNeeded", e, className);
		}
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
