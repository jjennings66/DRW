package com.custom;
  

import com.olf.openjvs.DBUserTable;
import com.olf.openjvs.DBaseTable;
import com.olf.openjvs.IContainerContext;
import com.olf.openjvs.IScript;
import com.olf.openjvs.Index;
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
Script Name: DRW_ViewNatGasPricesSpreadsCurveOutput
Description: DRW_ViewNatGasPricesSpreadsCurveOutput

Revision History
21-May-2023   Brian   New Script

*/

@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_GENERIC)
@PluginType(SCRIPT_TYPE_ENUM.MAIN_SCRIPT)
public class DRW_ViewNatGasPricesSpreadsCurveOutput implements IScript {
	 
	public void execute(IContainerContext context) throws OException {
		String className = this.getClass().getSimpleName();
		int iRunNumber = DEBUG_LOGFILE.getRunNumber(className);

		try {

			LIB.log("START", className);
			this.doEverything(iRunNumber, className);
			LIB.log("END", className);
 			
		} catch (Exception e) {
			// do not log this
		}
	}
	

	public void doEverything(int iRunNumber, String className) throws OException {
		
 		try {
			this.doEverythingIF(iRunNumber, className);
			this.doEverythingGD(iRunNumber, className);
		} catch (Exception e) {
			// do not log this
		}
	}
	

	 
	public void doEverythingIF(int iRunNumber, String className) throws OException {
		
		Table tIFOutput = Util.NULL_TABLE; 
		try {
			
			int IF_REF_SOURCE = Ref.getValue(SHM_USR_TABLES_ENUM.REF_SOURCE_TABLE, "IF");
			int GD_REF_SOURCE = Ref.getValue(SHM_USR_TABLES_ENUM.REF_SOURCE_TABLE, "GD");
			int PHYS_GAS_REF_SOURCE = Ref.getValue(SHM_USR_TABLES_ENUM.REF_SOURCE_TABLE, "PHYS GAS");
			
			LIB.log("IF_REF_SOURCE: " + IF_REF_SOURCE, className);
			LIB.log("GD_REF_SOURCE: " + GD_REF_SOURCE, className);
			LIB.log("PHYS_GAS_REF_SOURCE: " + PHYS_GAS_REF_SOURCE, className);
			

			tIFOutput = Table.tableNew();
			LIB.safeAddCol(tIFOutput, "Date", COL_TYPE_ENUM.COL_INT);
			LIB.safeAddCol(tIFOutput, "month", COL_TYPE_ENUM.COL_STRING);
			LIB.safeSetColFormatAsDate(tIFOutput, "Date");


//			tOutput = Table.tableNew();
//			LIB.safeAddCol(tOutput, COLS.OUTPUT.row_number, COL_TYPE_ENUM.COL_INT, "Row#");

			Table tIFIndexes = Table.tableNew();
			LIB.safeAddCol(tIFIndexes, "index_id", COL_TYPE_ENUM.COL_INT);
			LIB.safeAddCol(tIFIndexes, "index_name", COL_TYPE_ENUM.COL_STRING);
			LIB.safeAddCol(tIFIndexes, "ref_source", COL_TYPE_ENUM.COL_INT);

			String sSQL = "select index_id, index_name, ref_source FROM idx_def WHERE db_status = 1 AND index_name like 'NG%' and index_name like '%IF' order by index_name";
			
//			String sSQL = "select index_id, index_name, ref_source FROM idx_def WHERE db_status = 1 ";
			LIB.execISql(tIFIndexes, sSQL, false, className);
			
			// Grouping is Cosmetic
			tIFIndexes.group("index_name");
			
			//LIB.viewTable(tIFIndexes, "IF Curve Data");
			
			{
				// Assume all curves are like this
				Table tIndex = Index.getOutput("NG_HSC_IF");
				
				tIndex.colConvertDateTimeToInt("Date");
				LIB.safeSetColFormatAsDate(tIndex, "Date");
				
				tIndex.setColName(2, "month");
				
				String sWhat = "Date, month";
				String sWhere = "Date GE -1";
				LIB.select(tIFOutput, tIndex, sWhat, sWhere, false, className);
				
				tIndex = LIB.destroyTable(tIndex);
			}
			
			// Grouping is Cosmetic
			tIFIndexes.group("index_name");
			
			int iNumRows = LIB.safeGetNumRows(tIFIndexes);
			// TODO
		//	iNumRows = 2;
			
			for (int iCounter = 1; iCounter <= iNumRows; iCounter++) {
				int iIndexID = LIB.safeGetInt(tIFIndexes, "index_id", iCounter);
				String sIndexName = LIB.safeGetString(tIFIndexes, "index_name", iCounter);
				
				try {
					Table tIndex = Index.getOutput(iIndexID);
					
					tIndex.colConvertDateTimeToInt("Date");
					LIB.safeSetColFormatAsDate(tIndex, "Date");
					
					tIndex.setColName(2, "month");
					tIndex.setColName(LIB.safeGetNumCols(tIndex), "the_price");

					// TOxDO
					//LIB.viewTable(tIndex, "Data for " + sIndexName);


					LIB.safeAddCol(tIFOutput, "the_price", COL_TYPE_ENUM.COL_DOUBLE);
					String sWhat = "the_price";
					String sWhere = "Date EQ $Date";
					LIB.select(tIFOutput, tIndex, sWhat, sWhere, false, className);
					
					tIFOutput.setColName(LIB.safeGetNumCols(tIFOutput), sIndexName);
					tIFOutput.setColTitle(LIB.safeGetNumCols(tIFOutput), sIndexName);
					

					
					tIndex = LIB.destroyTable(tIndex);
					
				} catch (Exception e) {
					// do not log this
				}
			}
 			  
			// this grouping is cosmetic
		//	tOutput.group(COLS.OUTPUT.row_number);
			LIB.viewTable(tIFOutput, "IF Curve Data, Num Rows: " + LIB.safeGetNumRows(tIFOutput));
			
			
		} catch (Exception e) {
			LIB.log("doEverythingIF", e, className);
		}
		tIFOutput = LIB.destroyTable(tIFOutput); 
	}

	
 
	public void doEverythingGD(int iRunNumber, String className) throws OException {
		
		Table tGDOutput = Util.NULL_TABLE; 
		try {
			
			int IF_REF_SOURCE = Ref.getValue(SHM_USR_TABLES_ENUM.REF_SOURCE_TABLE, "IF");
			int GD_REF_SOURCE = Ref.getValue(SHM_USR_TABLES_ENUM.REF_SOURCE_TABLE, "GD");
			int PHYS_GAS_REF_SOURCE = Ref.getValue(SHM_USR_TABLES_ENUM.REF_SOURCE_TABLE, "PHYS GAS");
			
			LIB.log("IF_REF_SOURCE: " + IF_REF_SOURCE, className);
			LIB.log("GD_REF_SOURCE: " + GD_REF_SOURCE, className);
			LIB.log("PHYS_GAS_REF_SOURCE: " + PHYS_GAS_REF_SOURCE, className);
			

			tGDOutput = Table.tableNew();
			LIB.safeAddCol(tGDOutput, "Date", COL_TYPE_ENUM.COL_INT);
			LIB.safeAddCol(tGDOutput, "month", COL_TYPE_ENUM.COL_STRING);

			LIB.safeSetColFormatAsDate(tGDOutput, "Date");
			
//			tOutput = Table.tableNew();
//			LIB.safeAddCol(tOutput, COLS.OUTPUT.row_number, COL_TYPE_ENUM.COL_INT, "Row#");

			Table tIFIndexes = Table.tableNew();
			LIB.safeAddCol(tIFIndexes, "index_id", COL_TYPE_ENUM.COL_INT);
			LIB.safeAddCol(tIFIndexes, "index_name", COL_TYPE_ENUM.COL_STRING);
			LIB.safeAddCol(tIFIndexes, "ref_source", COL_TYPE_ENUM.COL_INT);

			String sSQL = "select index_id, index_name, ref_source FROM idx_def WHERE db_status = 1 AND index_name like 'NG%' and index_name like '%GD' order by index_name";
			
//			String sSQL = "select index_id, index_name, ref_source FROM idx_def WHERE db_status = 1 ";
			LIB.execISql(tIFIndexes, sSQL, false, className);
			
			// Grouping is Cosmetic
			tIFIndexes.group("index_name");
			
			//LIB.viewTable(tIFIndexes, "IF Curve Data");
			
			{
				// Assume all curves are like this
				Table tIndex = Index.getOutput("NG_HSC_GD");
				
				tIndex.colConvertDateTimeToInt("Date");
				LIB.safeSetColFormatAsDate(tIndex, "Date");
				
				tIndex.setColName(2, "month");
				
				String sWhat = "Date, month";
				String sWhere = "Date GE -1";
				LIB.select(tGDOutput, tIndex, sWhat, sWhere, false, className);
				
				tIndex = LIB.destroyTable(tIndex);
			}
			
			// Grouping is Cosmetic
			tIFIndexes.group("index_name");
			
			int iNumRows = LIB.safeGetNumRows(tIFIndexes);
			// TODO
		//	iNumRows = 2;
			
			for (int iCounter = 1; iCounter <= iNumRows; iCounter++) {
				int iIndexID = LIB.safeGetInt(tIFIndexes, "index_id", iCounter);
				String sIndexName = LIB.safeGetString(tIFIndexes, "index_name", iCounter);
				
				try {
					Table tIndex = Index.getOutput(iIndexID);
					
					tIndex.colConvertDateTimeToInt("Date");
					LIB.safeSetColFormatAsDate(tIndex, "Date");
					
					tIndex.setColName(2, "month");
					tIndex.setColName(LIB.safeGetNumCols(tIndex), "the_price");

					// TOxDO
					//LIB.viewTable(tIndex, "Data for " + sIndexName);


					LIB.safeAddCol(tGDOutput, "the_price", COL_TYPE_ENUM.COL_DOUBLE);
					String sWhat = "the_price";
					String sWhere = "Date EQ $Date";
					LIB.select(tGDOutput, tIndex, sWhat, sWhere, false, className);
					
					tGDOutput.setColName(LIB.safeGetNumCols(tGDOutput), sIndexName);
					tGDOutput.setColTitle(LIB.safeGetNumCols(tGDOutput), sIndexName);
					

					
					tIndex = LIB.destroyTable(tIndex);
					
				} catch (Exception e) {
					// do not log this
				}
			}
 			  
			// this grouping is cosmetic
		//	tOutput.group(COLS.OUTPUT.row_number);
			LIB.viewTable(tGDOutput, "GD Curve Data, Num Rows: " + LIB.safeGetNumRows(tGDOutput));
			
			
		} catch (Exception e) {
			LIB.log("doEverythingGD", e, className);
		}
		tGDOutput = LIB.destroyTable(tGDOutput); 
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

	public static final String VERSION_NUMBER = "V1.006 (21May2023)";

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
	
	public static void safeAddCol(Table tMaster, String sColName, COL_TYPE_ENUM colType, String sColTitle) throws OException {
		try {
			if (Table.isTableValid(tMaster) == 1) {
				// Only add if not already there
				if (tMaster.getColNum(sColName) < 1) {
					tMaster.addCol(sColName, colType, sColTitle);
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
