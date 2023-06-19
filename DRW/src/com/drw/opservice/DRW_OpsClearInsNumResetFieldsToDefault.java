package com.drw.opservice; 


import com.olf.openjvs.DBaseTable;
import com.olf.openjvs.IContainerContext;
import com.olf.openjvs.IScript;
import com.olf.openjvs.OConsole;
import com.olf.openjvs.OException;
import com.olf.openjvs.OpService;
import com.olf.openjvs.Str;
import com.olf.openjvs.SystemUtil;
import com.olf.openjvs.Table;
import com.olf.openjvs.Transaction;
import com.olf.openjvs.Util;
import com.olf.openjvs.enums.COL_TYPE_ENUM;
import com.olf.openjvs.enums.DATE_FORMAT;
import com.olf.openjvs.enums.DATE_LOCALE;
import com.olf.openjvs.enums.SHM_USR_TABLES_ENUM;
import com.olf.openjvs.enums.TABLE_VIEWER_MODE;
import com.olf.openjvs.enums.TRANF_FIELD;
import com.olf.openjvs.enums.TRANF_GROUP;

public class DRW_OpsClearInsNumResetFieldsToDefault implements IScript {

	public void execute(IContainerContext context) throws OException {

		String className = this.getClass().getSimpleName();

		try {
			LIB.log("START", className);
			Table argt = context.getArgumentsTable();
			Table returnt = context.getReturnTable();

			this.doEverything(argt, returnt, className);

			LIB.log("END", className);

		} catch (Exception e) {
			LIB.log("execute", e, className);
		}
		 
	}

	void doEverything(Table argt, Table returnt, String className) throws OException {
		
		try {
 
			// this only works for 'Trading' ops service type (or 'Deal Enrichment"
			//Transaction tran = OpService.retrieveOriginalTran(1);

			Transaction tran = argt.getTran("Tran", CONST_VALUES.ROW_TO_GET_OR_SET);
			
			String sInsType = TRANF_HELPER.safeGetTranfField(tran, TRANF_FIELD.TRANF_INS_TYPE, className);
			LIB.log("The Ins Type: " + sInsType, className);
			
			LIB.log("Blanking out 'Book'", className);
			TRANF_HELPER.safeSetTranfField(tran, TRANF_FIELD.TRANF_BOOK, "", className);

			TRAN_INFO tranInfo = TRAN_INFO.SourceSystem;
			LIB.log("Blanking out TranInfo '" + tranInfo.strValue() + "'", className);
			TRANF_HELPER.safeSetTranfInfoField(tran, tranInfo, "", className);

			tranInfo = TRAN_INFO.ExternalDealRef;
			LIB.log("Blanking out TranInfo '" + tranInfo.strValue() + "'", className);
			TRANF_HELPER.safeSetTranfInfoField(tran, tranInfo, "", className);
 			
		} catch (Exception e) {
			LIB.log("doEverything", e, className);
		}
	}


	public static class CONST_VALUES {
		public static final int VALUE_OF_TRUE = 1;
		public static final int VALUE_OF_FALSE = 0;

		public static final int VALUE_OF_NOT_APPLICABLE = -1;

		public static final double A_SMALL_NUMBER = 0.000001;

		public static final int ROW_TO_GET_OR_SET = 1;

		public static final String BOOK_TO_SET = "AutoBookedDealImporter";

	}
	

	public enum TRAN_INFO { 
		ExternalDealRef ("ExternalDealRef", true),
		SourceSystem ("SourceSystem", true),
		ExternalCounterparty ("ExternalCounterparty", true),
		ExternalLocation ("ExternalLocation", true),
		ExternalContact ("ExternalContact", true),
		;
  		 
		final private String sValue; 
        final private boolean bIsRequiredFlag;  
	      
        //Constructor
        private TRAN_INFO(String sValue, boolean bIsRequiredFlag) {
            this.sValue = sValue;
            this.bIsRequiredFlag = bIsRequiredFlag; 
        }
        public String strValue() {
            return this.sValue;
        }  
        public boolean getIsRequiredFlag() {
            return this.bIsRequiredFlag;
        }   
	} 

	public static class TRANF_HELPER {

		// this isn't used, but need to pass something in to the function
		private final static String TRAN_INFO_FIELD_NAME = "";
		private final static int SIDE0 = 0;

		public static String safeGetTranfField(Transaction tran, TRANF_FIELD tranField, String className)
				throws OException {
			String sReturn = "";
			try {
				sReturn = tran.getField(tranField.toInt());
			} catch (Exception e) {
				sReturn = "";
				LIB.log("safeGetTranfField", e, className);
			}
			if (sReturn == null) {
				sReturn = "";
			}
			return sReturn;
		}

		public static String safeGetTranfField(Transaction tran, TRANF_FIELD tranField, int iSide, String className)
				throws OException {
			String sReturn = "";
			try {
				sReturn = tran.getField(tranField.toInt(), iSide, TRAN_INFO_FIELD_NAME);
			} catch (Exception e) {
				sReturn = "";
				LIB.log("safeGetTranfField with Side", e, className);
			}
			if (sReturn == null) {
				sReturn = "";
			}
			return sReturn;
		}

		public static String safeGetTranfField(Transaction tran, TRANF_FIELD tranField, int iSide, int iSeqNum2,
				String className) throws OException {
			String sReturn = "";
			try {
				sReturn = tran.getField(tranField.toInt(), iSide, TRAN_INFO_FIELD_NAME, iSeqNum2);
			} catch (Exception e) {
				sReturn = "";
				LIB.log("safeGetTranfField with SeqNum2", e, className);
			}
			if (sReturn == null) {
				sReturn = "";
			}
			return sReturn;
		}

		public static double safeGetTranfFieldDouble(Transaction tran, TRANF_FIELD tranField, int iSide, int iSeqNum2,
				String className) throws OException {
			double dReturn = 0.0;
			try {
				dReturn = tran.getFieldDouble(tranField.toInt(), iSide, TRAN_INFO_FIELD_NAME, iSeqNum2);
			} catch (Exception e) {
				LIB.log("safeGetTranfFieldDouble with SeqNum2", e, className);
			}
			return dReturn;
		}

		public static double safeGetTranfFieldDouble(Transaction tran, TRANF_FIELD tranField, int iSide,
				String className) throws OException {
			double dReturn = 0.0;
			try {
				dReturn = tran.getFieldDouble(tranField.toInt(), iSide, TRAN_INFO_FIELD_NAME);
			} catch (Exception e) {
				LIB.log("safeGetTranfFieldDouble with Side", e, className);
			}
			return dReturn;
		}

		public static double safeGetTranfFieldDouble(Transaction tran, TRANF_FIELD tranField, String className)
				throws OException {
			double dReturn = 0.0;
			try {
				dReturn = tran.getFieldDouble(tranField.toInt(), TRANF_HELPER.SIDE0, TRAN_INFO_FIELD_NAME);
			} catch (Exception e) {
				LIB.log("safeGetTranfFieldDouble", e, className);
			}
			return dReturn;
		}

		public static int safeGetTranfFieldInt(Transaction tran, TRANF_FIELD tranField, int iSide, int iSeqNum2,
				String className) throws OException {
			int iReturn = 0;
			try {
				iReturn = tran.getFieldInt(tranField.toInt(), iSide, TRAN_INFO_FIELD_NAME, iSeqNum2);
			} catch (Exception e) {
				LIB.log("safeGetTranfFieldInt with SeqNum2", e, className);
			}
			return iReturn;
		}

		public static int safeGetTranfFieldInt(Transaction tran, TRANF_FIELD tranField, int iSide, String className)
				throws OException {
			int iReturn = 0;
			try {
				iReturn = tran.getFieldInt(tranField.toInt(), iSide, TRAN_INFO_FIELD_NAME);
			} catch (Exception e) {
				LIB.log("safeGetTranfFieldInt with Side", e, className);
			}
			return iReturn;
		}

		public static int safeGetTranfFieldInt(Transaction tran, TRANF_FIELD tranField, String className)
				throws OException {
			int iReturn = 0;
			try {
				iReturn = tran.getFieldInt(tranField.toInt(), TRANF_HELPER.SIDE0, TRAN_INFO_FIELD_NAME);
			} catch (Exception e) {
				LIB.log("safeGetTranfFieldInt", e, className);
			}
			return iReturn;
		}

		public static String safeGetTranfField(Transaction tran, TRANF_FIELD tranField, int iSide, int iSeqNum2,
				int iSeqNum3, String className) throws OException {
			String sReturn = "";
			try {
				sReturn = tran.getField(tranField.toInt(), iSide, TRAN_INFO_FIELD_NAME, iSeqNum2, iSeqNum3);
			} catch (Exception e) {
				sReturn = "";
				LIB.log("safeGetTranfField with SeqNum2/3", e, className);
			}
			if (sReturn == null) {
				sReturn = "";
			}
			return sReturn;
		}

		public static int safeGetTranfFieldInt(Transaction tran, TRANF_FIELD tranField, int iSide, int iSeqNum2,
				int iSeqNum3, String className) throws OException {
			int iReturn = 0;
			try {
				iReturn = tran.getFieldInt(tranField.toInt(), iSide, TRAN_INFO_FIELD_NAME, iSeqNum2, iSeqNum3);
			} catch (Exception e) {
				LIB.log("safeGetTranfFieldInt with SeqNum2/3", e, className);
			}
			return iReturn;
		}

		public static void safeSetTranfField(Transaction tran, TRANF_FIELD tranField, String sValueToSet,
				String className) throws OException {
			try {
				int SIDE = 0;
				tran.setField(tranField.toInt(), SIDE, TRAN_INFO_FIELD_NAME, sValueToSet);
			} catch (Exception e) {
				LIB.log("safeSetTranfField with Side", e, className);
			}
		}
   

		public static void safeSetTranfField(Transaction tran, TRANF_FIELD tranField, int iSide, String sValueToSet,
				String className) throws OException {
			try {
				tran.setField(tranField.toInt(), iSide, TRAN_INFO_FIELD_NAME, sValueToSet);
			} catch (Exception e) {
				LIB.log("safeSetTranfField with Side", e, className);
			}
		}

		public static void safeSetTranfField(Transaction tran, TRANF_FIELD tranField, int iSide, int iSeqNum2,
				String sValueToSet, String className) throws OException {
			try {
				tran.setField(tranField.toInt(), iSide, TRAN_INFO_FIELD_NAME, sValueToSet, iSeqNum2);
			} catch (Exception e) {
				LIB.log("safeSetTranfField with Side and SeqNum2", e, className);
			}
		}

		public static void safeSetTranfInfoField(Transaction tran, TRAN_INFO tranInfo, String sValueToSet,
				String className) throws OException {
			try {
				tran.setField(TRANF_FIELD.TRANF_TRAN_INFO.toInt(), SIDE0, tranInfo.strValue(), sValueToSet);
			} catch (Exception e) {
				LIB.log("safeSetTranfInfoField", e, className);
			}
		}


		// generally do not call this function, except for when you are dealing with extra (unplanned) tran info
		public static void safeSetTranfInfoFieldByTranInfoStringName(Transaction tran, String sTranInfoName, String sValueToSet,
				String className) throws OException {
			try {
				tran.setField(TRANF_FIELD.TRANF_TRAN_INFO.toInt(), SIDE0, sTranInfoName, sValueToSet);
			} catch (Exception e) {
				LIB.log("safeSetTranfInfoFieldByTranInfoStringName", e, className);
			}
		}

		public static void safeSetTranfInfoFieldNoErrorMessage(Transaction tran, String sInfoFieldName,
				String sValueToSet, String className) throws OException {
			try {
				tran.setField(TRANF_FIELD.TRANF_TRAN_INFO.toInt(), SIDE0, sInfoFieldName, sValueToSet);
			} catch (Exception e) {
				// Don't log this
			}
		}

		public static String safeGetTranfInfoField(Transaction tran, TRAN_INFO tranInfo, String className)
				throws OException {
			String sReturn = "";
			try {
				sReturn = tran.getField(TRANF_FIELD.TRANF_TRAN_INFO.toInt(), SIDE0, tranInfo.strValue());
			} catch (Exception e) {
				sReturn = "";
				LIB.log("safeGetTranfInfoField", e, className);
			}
			if (sReturn == null) {
				sReturn = "";
			}
			return sReturn;
		}

		public static void safeSetTranfFieldNoError(Transaction tran, TRANF_FIELD tranField, int iSide, int iSeqNum2,
				String sValueToSet, String className) throws OException {
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

	} // end class private static class TRANF_HELPER

	public static class LIB {

		public static final String VERSION_NUMBER = "V1.019 (06Jun2023)";

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

		public static String safeGetStringNoError(Table tMaster, String sColName, int iRowNum) throws OException {
			String sReturn = "";
			try {
				// like safeGetString, but don't log to Olisten if the column name was not found
				if (Table.isTableValid(tMaster) == 1) {
					if (tMaster.getColNum(sColName) >= 1) {
						sReturn = tMaster.getString(sColName, iRowNum);
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
