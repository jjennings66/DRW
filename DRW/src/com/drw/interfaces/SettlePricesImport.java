package com.drw.interfaces;

import com.olf.openjvs.Afs;
import com.olf.openjvs.ConnexUtility;
import com.olf.openjvs.DBUserTable;
import com.olf.openjvs.DBaseTable;
import com.olf.openjvs.IContainerContext;
import com.olf.openjvs.IScript;
import com.olf.openjvs.Index;
import com.olf.openjvs.OCalendar;
import com.olf.openjvs.OConsole;
import com.olf.openjvs.OException;
import com.olf.openjvs.Table;
import com.olf.openjvs.enums.COL_TYPE_ENUM;
import com.olf.openjvs.enums.OC_ARGUMENT_TAGS_ENUM;
import com.olf.openjvs.enums.OLF_RETURN_CODE;

public class SettlePricesImport implements IScript {

	@Override
	public void execute(IContainerContext context) throws OException {
		Table tArg = null;
		Table tReturn = null;
		
		Table tSettlePricesImport = null;
		Table tFormattedSettlePricesImport = null;
		int iRetVal = -1;
		String sMessage = "";
		
		try {
			tArg = context.getArgumentsTable();
			tReturn = context.getReturnTable();
			tSettlePricesImport = tArg.copyTable();
			
			tFormattedSettlePricesImport = getFormattedSettlePricesImport(tSettlePricesImport);
			saveAfsReport(tFormattedSettlePricesImport, "FormattedSettlePricesImport");
			iRetVal = importSettlePrices(tFormattedSettlePricesImport);
			if (iRetVal != OLF_RETURN_CODE.OLF_RETURN_SUCCEED.toInt()) {
				throw new Exception("Failed to save settle prices");
			}
			//possibly join ref source to settle prices, validate, import, then send the ones where mappings don't exists back
			
			sMessage = "Settle price import succeeded";
		}
		catch (Exception e) {
			sMessage = "Error: " + e.getLocalizedMessage();
    		OConsole.oprint(sMessage);
		}
		finally {
			int iMaxRow = tReturn.addRow();
			tReturn.setString(ConnexUtility.getArgumentColName(OC_ARGUMENT_TAGS_ENUM.OC_REPORTS_REPORT_NAME_TAG.toInt()), iMaxRow, sMessage);
			tReturn.setString(ConnexUtility.getArgumentColName(OC_ARGUMENT_TAGS_ENUM.OC_REPORTS_REPORT_URL_TAG.toInt()), iMaxRow, this.getClass().getSimpleName());
//			tReturn.setTable(ConnexUtility.getArgumentColName(OC_ARGUMENT_TAGS_ENUM.OC_REPORTS_REPORT_TABLE_TAG.toInt()), iMaxRow, tFormattedSettlePricesImport.copyTable());
//			tReturn.setString(ConnexUtility.getArgumentColName(OC_ARGUMENT_TAGS_ENUM.OC_REPORTS_REPORT_URL_TAG.toInt()), iMaxRow, "SettlePricesImport");
			
			tSettlePricesImport.destroy();
			tFormattedSettlePricesImport.destroy();
		}
	}
	private int saveAfsReport(Table tReport, String sName) throws OException {
		int iRetVal = -1;
		try {
			if (isStringNullOrEmpty(sName)) {
				sName = DBUserTable.getUniqueId() + "";
			}
			iRetVal = Afs.saveTable(tReport, "User:tnguyen:/Thuc/" + sName, 1);
			if (iRetVal != OLF_RETURN_CODE.OLF_RETURN_SUCCEED.toInt()) {
				throw new Exception("Failed to save afs table");
			}

		}
		catch (Exception e) {
    		OConsole.oprint("Error: " + e.getLocalizedMessage());
		}
		return iRetVal;
	}
	private Table getFormattedSettlePricesImport(Table tSettlePricesImport) throws OException {
		int iNumRows = -1;
		int iRow = -1;
		int iLastIndexSub = -1;
		int iSettleDate = -1;
		String sIndexName = "";
		String sRefSourceShortName = "";
		String sSettleDate = "";
		Table tRefSourceMap = null;
		Table tFormattedSettlePricesImport = null;
		try {
			
			tSettlePricesImport.addCol("ref_source_short_name", COL_TYPE_ENUM.COL_STRING);
			tSettlePricesImport.addCol("date", COL_TYPE_ENUM.COL_INT);
			iNumRows = tSettlePricesImport.getNumRows();			
			for (iRow = 1; iRow <= iNumRows; iRow++) {
				sIndexName = tSettlePricesImport.getString("index_name", iRow);
				iLastIndexSub = sIndexName.lastIndexOf("_");
				sRefSourceShortName = sIndexName.substring(iLastIndexSub + 1, sIndexName.length());
				if (sIndexName.contentEquals("NYM_NG")) {
					sRefSourceShortName = "NYMEX";
				}
				tSettlePricesImport.setString("ref_source_short_name", iRow, sRefSourceShortName);
				
				sSettleDate = tSettlePricesImport.getString("settle_date", iRow);
				iSettleDate = OCalendar.parseString(sSettleDate);
				tSettlePricesImport.setInt("date", iRow, iSettleDate);
			}
			
			tRefSourceMap = getRefSourceMap();
			
			tSettlePricesImport.select(tRefSourceMap, "id_number(ref_source)", "ref_source_short_name EQ $ref_source_short_name");
			
			tFormattedSettlePricesImport = Table.tableNew("Formatted Settle Price Import");
			tFormattedSettlePricesImport.addCol("index_id", COL_TYPE_ENUM.COL_INT);
			tFormattedSettlePricesImport.addCol("date", COL_TYPE_ENUM.COL_INT);
			tFormattedSettlePricesImport.addCol("start_date", COL_TYPE_ENUM.COL_INT);
			tFormattedSettlePricesImport.addCol("ref_source", COL_TYPE_ENUM.COL_INT);
			tFormattedSettlePricesImport.addCol("price", COL_TYPE_ENUM.COL_DOUBLE);

			tFormattedSettlePricesImport.select(tSettlePricesImport,
					"index_id, date, date(start_date), ref_source, settle_price(price)", "index_id GT 0");
			tFormattedSettlePricesImport.group("index_id, date");
			
		}
		catch (Exception e) {
    		OConsole.oprint("Error: " + e.getLocalizedMessage());
		}
		finally {
			tRefSourceMap.destroy();
		}
		return tFormattedSettlePricesImport;
	}
	private Table getRefSourceMap() throws OException {
		Table tRefSource = null;
		int iNumRows = -1;
		int iRow = -1;
		String sRefSource = "";
		String sShortName = "";
		
		try {
			
			/*
			 * Note curves map to ref source with below logic
			 * _IF = IF Ref Source
			 * _GD = GD Ref Source
			 * _PP = PP Ref Source
			 * NYM_NG = NYMEX Ref Source
			 */
			tRefSource = Table.tableNew("Ref Source");
			DBaseTable.execISql(tRefSource, "select id_number, name from ref_source");
			tRefSource.addCol("ref_source_short_name", COL_TYPE_ENUM.COL_STRING);
			iNumRows = tRefSource.getNumRows();
			for (iRow = 1; iRow <= iNumRows; iRow++) {
				sShortName = "";
				sRefSource = tRefSource.getString("name", iRow);
				
				if (sRefSource.equalsIgnoreCase("PHYS GAS")) {
					sShortName = "PP";
				}
				if (sRefSource.equalsIgnoreCase("NYMEX")) {
					sShortName = "NYMEX";
				}
				if (sRefSource.equalsIgnoreCase("IF")) {
					sShortName = "IF";
				}
				if (sRefSource.equalsIgnoreCase("GD")) {
					sShortName = "GD";
				}
				if (sRefSource.equalsIgnoreCase("NGI")) {
					sShortName = "NGI";
				}
				
				tRefSource.setString("ref_source_short_name", iRow, sShortName);
			}
		}
		catch (Exception e) {
    		OConsole.oprint("Error: " + e.getLocalizedMessage());
		}
		return tRefSource;
	}
	private void validateSettlePriceIndexesExists(Table tSettlePrices) throws OException 
	{ 
		Table tIndex = null;
		Table tSettlePricesCopy = null;
		Table tDistinctSettlePricesIndexes = null;
		try {
//			tIndex = Table.tableNew();
//			DBaseTable.execISql(tIndex, "select DISTINCT, index_name, 1 as index_exists from idx_def where db_status = " + IDX_DB_STATUS_ENUM.IDX_DB_STATUS_VALIDATED.toInt());
//			formatIndexNames(tIndex, "index_name");
//			tIndex.viewTable();
//			
//			tSettlePricesCopy = tSettlePrices.copyTable();
//			tSettlePricesCopy.addCol("select_flag", COL_TYPE_ENUM.COL_INT);
//			tSettlePricesCopy.setColValInt("select_flag", 1);
//			tSettlePricesCopy.setTableName("Settle Prices Copy");
//			tSettlePricesCopy.viewTable();
//			
//			tDistinctSettlePricesIndexes = Table.tableNew("Distinct Indexes");
//			tDistinctSettlePricesIndexes.select(tSettlePricesCopy, "DISTINCT,index_name", "select_flag GT 0");
//			formatIndexNames(tDistinctSettlePricesIndexes, "index_name");
//			tDistinctSettlePricesIndexes.viewTable();
//
//			tDistinctSettlePricesIndexes.select(tIndex, "index_name(endur_index_name), index_exists", "index_name EQ $index_name");
		}
		catch (Exception e) {
    		OConsole.oprint("Error: " + e.getLocalizedMessage());
		}
	}
	private int importSettlePrices(Table tSettlePrices) throws OException
    {
		int iRetVal = -1;
		try {
			iRetVal = Index.tableImportHistoricalPrices(tSettlePrices);
		}
		catch (Exception e) {
    		OConsole.oprint("Error: " + e.getLocalizedMessage());
		}
		return iRetVal;
    }
	private boolean isStringNullOrEmpty(String sValue) throws OException {
		try {
			if (sValue == null || sValue.isEmpty() || sValue.isBlank()) {
				return true;
			}
		}
		catch (Exception e) {
			//Dont log
		}
		return false;
	}
}
