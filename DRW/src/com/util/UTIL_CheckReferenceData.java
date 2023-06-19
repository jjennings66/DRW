package com.util;
  

import com.olf.openjvs.DBUserTable;
import com.olf.openjvs.DBaseTable;
import com.olf.openjvs.IContainerContext;
import com.olf.openjvs.IScript;
import com.olf.openjvs.JvsExitException;
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
Script Name: UTIL_CheckReferenceData
Description:  UTIL_CheckReferenceData

Revision History
24-Apr-2023   Brian   New Script
02-May-2023   Brian   1) Added Categories
                      2) For tables with more than 100 rows, now only get/retrieve the 100 rows that most recently changed.  Only if the table has a last_update column.

  
// All Tables
select owner, object_name from all_objects WHERE object_type = 'TABLE' AND owner not in ('SYS', 'SYSTEM', 'XDB', 'CTXSYS', 'MDSYS') AND upper(object_name) not like '%_H' AND upper(object_name) not like '%_HIST'

// Get database name (not reliable)
select env_value from global_env_settings WHERE env_id = 20161

*/

@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_GENERIC)
@PluginType(SCRIPT_TYPE_ENUM.MAIN_SCRIPT)
public class UTIL_CheckReferenceData implements IScript {
	
	final static int MAX_ROWS_TO_SHOW = 100;
	final static int POST_STATUS_NUM_DEALS = 100;
	
	@SuppressWarnings("deprecation")
	public void execute(IContainerContext context) throws OException {
		String className = this.getClass().getSimpleName();
		int iRunNumber = DEBUG_LOGFILE.getRunNumber(className);

	 	// false means all tables
		boolean bRunForSelectedTables = false;

		try {
			String sDBName = Util.getEnv("AB_DB_NAME");

			LIB.log("START for database: " + sDBName, className);
			this.doEverything(bRunForSelectedTables, sDBName, iRunNumber, className);
			LIB.log("END", className);
 			
		} catch (Exception e) {
			// do not log this
		}
		Util.exitTerminate(JvsExitException.SUCCEED_TERMINATE);
	}
 
	public void doEverything(boolean bRunForSelectedTables, String sDBName, int iRunNumber, String className) throws OException {
		
		Table tOutput = Util.NULL_TABLE; 
		try {
			
			tOutput = Table.tableNew();
			LIB.safeAddCol(tOutput, COLS.OUTPUT.row_number, COL_TYPE_ENUM.COL_INT, "Row#");
			LIB.safeAddCol(tOutput, COLS.OUTPUT.table_name, COL_TYPE_ENUM.COL_STRING, "Table\nName");
			LIB.safeAddCol(tOutput, COLS.OUTPUT.last_update, COL_TYPE_ENUM.COL_DATE_TIME, "Last\nUpdate");
			LIB.safeAddCol(tOutput, COLS.OUTPUT.row_count, COL_TYPE_ENUM.COL_INT, "# of\nRows");
			LIB.safeAddCol(tOutput, COLS.OUTPUT.has_rows, COL_TYPE_ENUM.COL_INT, "Has\nRows?");
			LIB.safeAddCol(tOutput, COLS.OUTPUT.col_count, COL_TYPE_ENUM.COL_INT, "# of\nCols");
			LIB.safeAddCol(tOutput, COLS.OUTPUT.data, COL_TYPE_ENUM.COL_TABLE, "First " + MAX_ROWS_TO_SHOW + " Rows\nof Data");
			LIB.safeAddCol(tOutput, COLS.OUTPUT.category, COL_TYPE_ENUM.COL_STRING, "Category");
			LIB.safeAddCol(tOutput, COLS.OUTPUT.comment, COL_TYPE_ENUM.COL_STRING, "Comment");
			LIB.safeAddCol(tOutput, COLS.OUTPUT.table_title, COL_TYPE_ENUM.COL_STRING, "Comment2");
			LIB.safeAddCol(tOutput, COLS.OUTPUT.table_description, COL_TYPE_ENUM.COL_STRING, "Comment3");

			LIB.safeSetColFormatAsRef(tOutput, COLS.OUTPUT.has_rows, SHM_USR_TABLES_ENUM.NO_YES_TABLE);
 			
			// for cosmetic reasons
			LIB.safeAddCol(tOutput, "xxx", COL_TYPE_ENUM.COL_CELL);
			LIB.safeColHide(tOutput, "xxx");
			tOutput.setTableViewerMode(TABLE_VIEWER_MODE.TABLE_VIEWER_LEGACY);
			
			if (bRunForSelectedTables == true) {
				LIB.log("Running for Selected (SOME) tables", className);
				this.addAllTablesByName(tOutput, iRunNumber, className);
			} 

			// run for all tables
			if (bRunForSelectedTables == false) {
				LIB.log("Running for ALL tables", className);
				this.addAllTablesUsingSQLtoGetTableNames(sDBName, tOutput, iRunNumber, className);
			} 
  			
			// Add values from dm_table_info
			{
				Table tTemp = Table.tableNew();
				LIB.safeAddCol(tTemp, COLS.OUTPUT.table_name, COL_TYPE_ENUM.COL_STRING);
				LIB.safeAddCol(tTemp, COLS.OUTPUT.table_title, COL_TYPE_ENUM.COL_STRING);
				LIB.safeAddCol(tTemp, COLS.OUTPUT.table_description, COL_TYPE_ENUM.COL_STRING);
				
				String sSQL = "select table_name, table_title, table_description from dm_table_info";
				LIB.execISql(tTemp, sSQL, false, className);
				String sWhat = COLS.OUTPUT.table_title + ", " + COLS.OUTPUT.table_description;
				String sWhere = COLS.OUTPUT.table_name + " EQ $" + COLS.OUTPUT.table_name;
				LIB.select(tOutput, tTemp, sWhat, sWhere, false, className);
				tTemp = LIB.destroyTable(tTemp);
			}
			
			// this grouping is cosmetic
			tOutput.group(COLS.OUTPUT.row_number);
			LIB.viewTable(tOutput, "Reference Data, Num Rows: " + LIB.safeGetNumRows(tOutput));
			
			
		} catch (Exception e) {
			LIB.log("doEverything", e, className);
		}
		tOutput = LIB.destroyTable(tOutput); 
	}


	
public void addAllTablesUsingSQLtoGetTableNames(String sDBName, Table tOutput, int iRunNumber, String className) throws OException {
		
	 	Table tTableList = Util.NULL_TABLE;
		try {

			final String TABLE_COL_NAME = "table_name";
			tTableList = Table.tableNew();
			LIB.safeAddCol(tTableList, TABLE_COL_NAME, COL_TYPE_ENUM.COL_STRING);

			{
				// Use this SQL which selects only the OpenLink tables for this Database
				String sSQL = "select distinct object_name from all_objects WHERE object_type = 'TABLE' AND upper(owner) = upper('" + sDBName + "') AND upper(object_name) not like '%_H' AND upper(object_name) not like '%_HIST' AND upper(object_name) not like '%_HISTORY'";
				LIB.execISql(tTableList, sSQL, true, className);
			}

			// If no rows, use this backup SQL
			if (LIB.safeGetNumRows(tTableList) < 1) {
				LIB.log("Did not find any Tables for 'owner' (database) " + sDBName + ", so going to try again with some different SQL", className);
				String sSQL = "select distinct object_name from all_objects WHERE object_type = 'TABLE' AND owner not in ('SYS', 'SYSTEM', 'XDB', 'CTXSYS', 'MDSYS') AND upper(object_name) not like '%_H' AND upper(object_name) not like '%_HIST'  AND upper(object_name) not like '%_HISTORY'";
				LIB.execISql(tTableList, sSQL, true, className);
			}

			{
				int iMaxRow = tTableList.addRow();
				// IDX_HOURLY_HISTORY is not a 'history' table, even though it has 'history' as a suffix
				LIB.safeSetString(tTableList, TABLE_COL_NAME, iMaxRow, "idx_hourly_history");
			}
			
			{
				// Grouping is cosmetic
				tTableList.group(TABLE_COL_NAME);
				LIB.viewTable(tTableList, "All Tables for Database: " + "'" + sDBName + "'");
			}
 
			int iNumRows = LIB.safeGetNumRows(tTableList);
			// TODO
		//	iNumRows = 150;
			for (int iCounter = 1; iCounter <= iNumRows; iCounter++) {
				if (iCounter % POST_STATUS_NUM_DEALS == 0 || iCounter == iNumRows) {
					LIB.log("On table " + iCounter + " of " + iNumRows, className);
					Util.scriptPostStatus("On table " + iCounter + " of " + iNumRows);
				}
				String sTableName = LIB.safeGetString(tTableList, TABLE_COL_NAME, iCounter);
				sTableName = Str.toLower(sTableName);
				this.addTables(tOutput, sTableName, "last_update", iRunNumber, className);
			}

			// Grouping is cosmetic
			tOutput.group(COLS.OUTPUT.table_name);
 			 
 		 
		} catch (Exception e) {
			LIB.log("addAllTablesByName", e, className);
		}
		
		tTableList = LIB.destroyTable(tTableList);
	} 
	
public void addAllTablesByName(Table tOutput, int iRunNumber, String className) throws OException {
		
		try {

			this.addTables(tOutput, "currency", "last_update", iRunNumber, className);
			this.addTables(tOutput, "date_engine_seq", "last_update", iRunNumber, className);

			this.addTables(tOutput, "gas_phys_location", "last_update", iRunNumber, className);
			
			this.addTables(tOutput, "party", "last_update", iRunNumber, className);
			
			this.addTables(tOutput, "ref_source", "last_update", iRunNumber, className);
 		 
		} catch (Exception e) {
			LIB.log("addAllTablesByName", e, className);
		}
	} 


public String getCategory(String sTableName, Table tData, String className) throws OException {
		
	String sCategory = CATEGORIES.UNCATEGORIZED; 
		try {
			 
			
			{
				try {
					final int FIRST_CHAR = 0;
					sTableName = Str.toLower(sTableName);
					
					// TRADE
					if (Str.findSubString(sTableName, "ab_tran") == FIRST_CHAR) {
						sCategory = CATEGORIES.TRADE;
					}
					if (Str.findSubString(sTableName, "ins_") == FIRST_CHAR) {
						sCategory = CATEGORIES.TRADE;
					}
					if (Str.iEqual(sTableName, "int_ins_attributes") == CONST_VALUES.VALUE_OF_TRUE ||
							Str.iEqual(sTableName, "delivery_term") == CONST_VALUES.VALUE_OF_TRUE ||
							Str.iEqual(sTableName, "clearing_data") == CONST_VALUES.VALUE_OF_TRUE ||
							Str.iEqual(sTableName, "confirmation_data") == CONST_VALUES.VALUE_OF_TRUE ||
							Str.iEqual(sTableName, "execution_data") == CONST_VALUES.VALUE_OF_TRUE ||
							Str.iEqual(sTableName, "tran_aux_data") == CONST_VALUES.VALUE_OF_TRUE ||
							Str.iEqual(sTableName, "comm_schedule_delivery") == CONST_VALUES.VALUE_OF_TRUE ||
							Str.iEqual(sTableName, "comm_sched_deliv_deal") == CONST_VALUES.VALUE_OF_TRUE ||
							Str.iEqual(sTableName, "contract_to_nom_model_type") == CONST_VALUES.VALUE_OF_TRUE ||
							Str.iEqual(sTableName, "contract_to_nom_tran_type") == CONST_VALUES.VALUE_OF_TRUE ||
							Str.iEqual(sTableName, "contract_origination") == CONST_VALUES.VALUE_OF_TRUE ||
							Str.iEqual(sTableName, "comm_schedule_header") == CONST_VALUES.VALUE_OF_TRUE) {
						sCategory = CATEGORIES.TRADE;
					}
					

					// MARKET_CURVE
					if (Str.findSubString(sTableName, "idx_") == FIRST_CHAR) {
						sCategory = CATEGORIES.MARKET_CURVE;
					}
					
					// USER_TABLE
					if (Str.findSubString(sTableName, "user_") == FIRST_CHAR) {
						sCategory = CATEGORIES.USER_TABLE;
					}
					 
					// PARTY
					{
						if (Str.findSubString(sTableName, "party_") == FIRST_CHAR) {
							sCategory = CATEGORIES.PARTY;
						}
					
						if (Str.iEqual(sTableName, "party") == CONST_VALUES.VALUE_OF_TRUE ) {
							sCategory = CATEGORIES.PARTY;
						}
					}
					
					// REF_DATA
					{
						if (Str.findSubString(sTableName, "gas_") == FIRST_CHAR) {
							sCategory = CATEGORIES.REF_DATA;
						} 
						if (Str.iEqual(sTableName, "personnel") == CONST_VALUES.VALUE_OF_TRUE ||
								Str.iEqual(sTableName, "party_personnel") == CONST_VALUES.VALUE_OF_TRUE ||
								Str.iEqual(sTableName, "portfolio_personnel") == CONST_VALUES.VALUE_OF_TRUE ||
								Str.iEqual(sTableName, "pipeline_season") == CONST_VALUES.VALUE_OF_TRUE ||
								Str.iEqual(sTableName, "nom_tran_type") == CONST_VALUES.VALUE_OF_TRUE ||
								Str.iEqual(sTableName, "settlement_group") == CONST_VALUES.VALUE_OF_TRUE ||
								Str.iEqual(sTableName, "season") == CONST_VALUES.VALUE_OF_TRUE ||
								Str.iEqual(sTableName, "rate_definition") == CONST_VALUES.VALUE_OF_TRUE ||
								Str.iEqual(sTableName, "rate_schedule") == CONST_VALUES.VALUE_OF_TRUE ||
								Str.iEqual(sTableName, "provider_charge_type") == CONST_VALUES.VALUE_OF_TRUE ||
								Str.iEqual(sTableName, "parent_child_structure") == CONST_VALUES.VALUE_OF_TRUE ||
								Str.iEqual(sTableName, "rate_sched_charge_type") == CONST_VALUES.VALUE_OF_TRUE) {
							sCategory = CATEGORIES.REF_DATA;
						}
					}   
				
					// ID_NAME_TABLE
					if (LIB.safeGetNumCols(tData) == 2) {
						sCategory = CATEGORIES.ID_NAME_TABLE;
					} 
					
					// OPENLINK_SYSTEM
					if (Str.iEqual(sTableName, "abacus_database_admin") == CONST_VALUES.VALUE_OF_TRUE ||
							Str.iEqual(sTableName, "dm_column_info") == CONST_VALUES.VALUE_OF_TRUE ||
							Str.iEqual(sTableName, "license_type_unlicensed_privs") == CONST_VALUES.VALUE_OF_TRUE ||
							Str.iEqual(sTableName, "dm_table_info") == CONST_VALUES.VALUE_OF_TRUE ||
							Str.iEqual(sTableName, "qry_table_detail") == CONST_VALUES.VALUE_OF_TRUE ||
							Str.iEqual(sTableName, "dir_node") == CONST_VALUES.VALUE_OF_TRUE ||
							Str.iEqual(sTableName, "query_info") == CONST_VALUES.VALUE_OF_TRUE ||
							Str.iEqual(sTableName, "abacus_file_system") == CONST_VALUES.VALUE_OF_TRUE ||
							Str.iEqual(sTableName, "reference_table") == CONST_VALUES.VALUE_OF_TRUE) {
						sCategory = CATEGORIES.OPENLINK_SYSTEM;
					}
					
					// LOGS
					if (Str.iEqual(sTableName, "oc_activity_log") == CONST_VALUES.VALUE_OF_TRUE || 
							Str.iEqual(sTableName, "update_log") == CONST_VALUES.VALUE_OF_TRUE || 
							Str.iEqual(sTableName, "job_status_details") == CONST_VALUES.VALUE_OF_TRUE || 
									Str.iEqual(sTableName, "oc_activity_data_log") == CONST_VALUES.VALUE_OF_TRUE || 
						Str.iEqual(sTableName, "ol_alert_log") == CONST_VALUES.VALUE_OF_TRUE) {
						sCategory = CATEGORIES.LOGS;
					}
 					 
					// MISC
					if (Str.iEqual(sTableName, "tranf_import_processing") == CONST_VALUES.VALUE_OF_TRUE ||
							Str.iEqual(sTableName, "lock_history_table") == CONST_VALUES.VALUE_OF_TRUE ||
							Str.iEqual(sTableName, "xxx") == CONST_VALUES.VALUE_OF_TRUE ||
							Str.iEqual(sTableName, "xxx") == CONST_VALUES.VALUE_OF_TRUE ||
							Str.iEqual(sTableName, "xxx") == CONST_VALUES.VALUE_OF_TRUE) {
						sCategory = CATEGORIES.MISC;
					}
					 
					// SETUP_DATA
					if (Str.iEqual(sTableName, "tran_info_types") == CONST_VALUES.VALUE_OF_TRUE ||
							Str.iEqual(sTableName, "global_env_settings") == CONST_VALUES.VALUE_OF_TRUE ||
							Str.iEqual(sTableName, "xxx") == CONST_VALUES.VALUE_OF_TRUE ||
							Str.iEqual(sTableName, "xxx") == CONST_VALUES.VALUE_OF_TRUE ||
							Str.iEqual(sTableName, "xxx") == CONST_VALUES.VALUE_OF_TRUE) {
						sCategory = CATEGORIES.SETUP_DATA;
					}
					
	  
					// OPENLINK_SYSTEM
					if (Str.iEqual(sTableName, "objects") == CONST_VALUES.VALUE_OF_TRUE ||
							Str.iEqual(sTableName, "object_descriptions") == CONST_VALUES.VALUE_OF_TRUE || 
							Str.iEqual(sTableName, "groups_to_objects") == CONST_VALUES.VALUE_OF_TRUE) {
						sCategory = CATEGORIES.SECURITY;
					}
 
				} catch (Exception e) {
					// do not log this
				}
			}
 
		} catch (Exception e) {
			LIB.log("getCategory", e, className);
		}
		return sCategory;
	} 


public String getComment(String sTableName, String className) throws OException {
		
	String sComment = ""; 
		try {
			
			if (Str.iEqual(sTableName, "objects") == CONST_VALUES.VALUE_OF_TRUE) {
				sComment = "Security Priviledges";
			}
			if (Str.iEqual(sTableName, "object_descriptions") == CONST_VALUES.VALUE_OF_TRUE) {
				sComment = "Security Priviledges with Descriptions";
			}
			if (Str.iEqual(sTableName, "parent_child_structure") == CONST_VALUES.VALUE_OF_TRUE) {
				sComment = "For Parties/counterparties";
			}
   
		} catch (Exception e) {
			LIB.log("getComment", e, className);
		}
		return sComment;
	} 




			

public void addTables(Table tOutput, String sTableName, String sLastUpdateColumn, int iRunNumber, String className) throws OException {
		
		try {
			
			int iMaxRow = tOutput.addRow();
 
	

			LIB.safeSetInt(tOutput, COLS.OUTPUT.row_number, iMaxRow, iMaxRow);
			LIB.safeSetString(tOutput, COLS.OUTPUT.table_name, iMaxRow, sTableName);
			int iRowCount = 0;
			{
				Table tTemp = Table.tableNew();
				LIB.safeAddCol(tTemp, "temp", COL_TYPE_ENUM.COL_INT);
				String sSQL = "select count(*) from " + sTableName;
				LIB.execISql(tTemp, sSQL, false, className);
				iRowCount = LIB.safeGetInt(tTemp, "temp", CONST_VALUES.ROW_TO_GET_OR_SET);
				LIB.safeSetInt(tOutput, COLS.OUTPUT.row_count, iMaxRow, iRowCount);
				tTemp = LIB.destroyTable(tTemp);
			}

			if (iRowCount >= 1) {
				LIB.safeSetInt(tOutput, COLS.OUTPUT.has_rows, iMaxRow, CONST_VALUES.VALUE_OF_TRUE);
			}

			int iLastUpdateColNum = -1;
			
			if (iRowCount >= 1) {
				
				// do not destroy this table
				Table tTemp = Table.tableNew();
				tTemp.setTableViewerMode(TABLE_VIEWER_MODE.TABLE_VIEWER_LEGACY);
 
				{
					// Select just the last XXX rows edited, e.g., 100
					String sSQL = "SELECT * from (select rownum temp_123, x.* FROM (select " + sTableName + ".* from " + sTableName + " order by last_update desc) x) WHERE temp_123 <= " + MAX_ROWS_TO_SHOW;
					
//					SELECT * from (select rownum temp_123, x.* FROM (select party.* from party order by last_update desc) x) WHERE temp_123 <= 100
					LIB.execISql(tTemp, sSQL, false, className);
					
					if (LIB.safeGetNumCols(tTemp) >= 2) {
						LIB.safeDelCol(tTemp, "temp_123");
					}
				}

				// If that did not work, then get just XXX (e.g., 100) rows
				if (LIB.safeGetNumRows(tTemp) < 1) {
					tTemp = LIB.destroyTable(tTemp);
					tTemp = Table.tableNew();
					tTemp.setTableViewerMode(TABLE_VIEWER_MODE.TABLE_VIEWER_LEGACY);

					String sSQL = "select * from " + sTableName + " WHERE rownum <= " + MAX_ROWS_TO_SHOW;
					LIB.execISql(tTemp, sSQL, false, className);
 				}
 						
 				
				iLastUpdateColNum = tTemp.getColNum(sLastUpdateColumn);

				String sTableTitle = sTableName + " data, Rows: " + iRowCount;
				if (iRowCount > MAX_ROWS_TO_SHOW) {
					sTableTitle = sTableName + " data, Rows: " + MAX_ROWS_TO_SHOW + " of " + iRowCount;
				}
				tTemp.setTableTitle(sTableTitle);
				tTemp.setTableName(sTableTitle);
				
				tTemp.defaultFormat();
				
				LIB.safeSetTable(tOutput, COLS.OUTPUT.data, iMaxRow, tTemp);
				
				LIB.safeSetInt(tOutput, COLS.OUTPUT.col_count, iMaxRow, LIB.safeGetNumCols(tTemp));

				// need to this prior to adding the xxx COL_CELL
				String sCategory = getCategory(sTableName, tTemp, className);
				LIB.safeSetString(tOutput, COLS.OUTPUT.category, iMaxRow, sCategory);
 
				
				if (tTemp.getColNum("id_number") >= 1 && tTemp.getColNum("name") >= 1) {
					String sComment = "has name/id_number cols";
					LIB.safeSetString(tOutput, COLS.OUTPUT.comment, iMaxRow, sComment);
				}
				
				String sComment = getComment(sTableName, className);
				if (Str.len(sComment) >= 1) {
					LIB.safeSetString(tOutput, COLS.OUTPUT.comment, iMaxRow, sComment);
				}
				
				// need to do this after getting the category
				LIB.safeAddCol(tTemp, "xxx", COL_TYPE_ENUM.COL_CELL);
				LIB.safeColHide(tTemp, "xxx");
			}
			
			// only do this if there are rows and there is a last_update columns
			if (iRowCount >= 1 && iLastUpdateColNum >= 1) {

				Table tTemp = Table.tableNew();
				LIB.safeAddCol(tTemp, "temp", COL_TYPE_ENUM.COL_DATE_TIME);
				String sSQL = "select max(" +sLastUpdateColumn + ") from " + sTableName;
				LIB.execISql(tTemp, sSQL, false, className);
				int iDate = tTemp.getDate("temp", CONST_VALUES.ROW_TO_GET_OR_SET);
				int iTime = tTemp.getTime("temp", CONST_VALUES.ROW_TO_GET_OR_SET);
				
				tOutput.setDateTimeByParts(COLS.OUTPUT.last_update, iMaxRow, iDate, iTime);
				//int iRowCount = LIB.safeGetInt(tTemp, "temp", CONST_VALUES.ROW_TO_GET_OR_SET);
				//LIB.safeSetInt(tOutput, "row_count", iMaxRow, iRowCount);
				tTemp = LIB.destroyTable(tTemp);
			}
 		 
		} catch (Exception e) {
			LIB.log("addTables", e, className);
		}
	}
	
	public static class COLS {

		public static class OUTPUT {
			public static final String row_number = "row_number";
			public static final String table_name = "table_name";
			public static final String last_update = "last_update";
			public static final String row_count = "row_count";
			public static final String has_rows = "has_rows";			
			public static final String col_count = "col_count";
			public static final String data = "data";
			public static final String update_user = "update_user";
			public static final String category = "category";
			public static final String comment = "comment";
			public static final String table_title = "table_title";
			public static final String table_description = "table_description";
		}

	}



public static class CATEGORIES {
	// These are important if changed
	public static final String REF_DATA = "REF_DATA";
	public static final String SETUP_DATA = "SETUP_DATA";
	
	// These are *not* important if changed
	public static final String LOGS = "LOGS";
 	
	// For these, not sure if this is important or not.  It depends on the context
	public static final String TRADE = "TRADE";
	public static final String MARKET_CURVE = "MARKET/CURVE";
	public static final String UNCATEGORIZED = "UNCATEGORIZED";
	public static final String OPENLINK_SYSTEM = "OPENLINK_SYSTEM";
	public static final String MISC = "MISC";
	public static final String SECURITY = "SECURITY";
	public static final String PARTY = "PARTY";
	public static final String ID_NAME_TABLE = "ID_NAME_TABLE";
	public static final String USER_TABLE = "USER_TABLE";
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

	public static final String VERSION_NUMBER = "V1.005 (02May2023)";

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
