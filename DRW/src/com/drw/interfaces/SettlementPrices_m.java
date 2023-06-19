package com.drw.interfaces;

import com.olf.openjvs.DBaseTable;
import com.olf.openjvs.IContainerContext;
import com.olf.openjvs.IScript;
import com.olf.openjvs.Index;
import com.olf.openjvs.OCalendar;
import com.olf.openjvs.OConsole;
import com.olf.openjvs.OException;
import com.olf.openjvs.Table;
import com.olf.openjvs.enums.COL_TYPE_ENUM;
import com.olf.openjvs.enums.IDX_DB_STATUS_ENUM;
import com.olf.openjvs.enums.OLF_RETURN_CODE;

public class SettlementPrices_m implements IScript {

	@Override
	public void execute(IContainerContext context) throws OException {
		Table tArg = null;
		Table tSettlePricesImport = null;
		Table tFormattedSettlePricesImport = null;
		int iRetVal = -1;
		String sFilePath = "";
		
		try {
			tArg = context.getArgumentsTable();
			sFilePath = tArg.getString("import_file_path", 1);
			tSettlePricesImport = getSettlePricesImport(sFilePath);
			//tSettlePricesImport.viewTable();
			
			if (tSettlePricesImport != null && tSettlePricesImport.getNumRows() > 0) {
				tFormattedSettlePricesImport = getFormattedSettlePricesImport(tSettlePricesImport);
				iRetVal = importSettlePrices(tFormattedSettlePricesImport);
			}
			if (iRetVal != OLF_RETURN_CODE.OLF_RETURN_SUCCEED.toInt()) {
				throw new Exception("Failed to import settle prices.");
			}
		}
		catch (Exception e) {
    		OConsole.oprint("Error: " + e.getLocalizedMessage());
		}
	}
	private Table getFormattedSettlePricesImport(Table tSettlePricesImport) throws OException {
		Table tFormattedSettlePricesImport = null;
		Table tSettlePricesImportCopy = null;
		Table tQuorumCurveMap = null;
		Table tRefSourceMap = null;
		Table tIdxDef = null;
		int iNumRows = -1;
		int iRow = -1;
		int iLastIndexSub = -1;
		int iDate = -1;
		String sDate = "";
		String sEndurIndexName = "";
		String sRefSourceShortName = "";
		
		try {
			tSettlePricesImportCopy = tSettlePricesImport.copyTable();
			tSettlePricesImportCopy.setTableName("Settle Prices Import Copy");
			
			tQuorumCurveMap = getQuorumCurveMap();
			tRefSourceMap = getRefSourceMap();
			tQuorumCurveMap.addCol("ref_source_short_name", COL_TYPE_ENUM.COL_STRING);
			iNumRows = tQuorumCurveMap.getNumRows();
			for (iRow = 1; iRow <= iNumRows; iRow++) {
				sEndurIndexName = tQuorumCurveMap.getString("endur_index_name", iRow);
				
				iLastIndexSub = sEndurIndexName.lastIndexOf("_");
				sRefSourceShortName = sEndurIndexName.substring(iLastIndexSub + 1, sEndurIndexName.length());
				if (sEndurIndexName.contentEquals("NYM_NG")) {
					sRefSourceShortName = "NYMEX";
				}
				tQuorumCurveMap.setString("ref_source_short_name", iRow, sRefSourceShortName);
			}
			tQuorumCurveMap.select(tRefSourceMap, "name(ref_source), id_number(ref_source_id_number)", "ref_source_short_name EQ $ref_source_short_name");
			//tQuorumCurveMap.viewTable();

			tIdxDef = getIdxDef();
			tQuorumCurveMap.select(tIdxDef, "index_id", "index_name EQ $endur_index_name");
			
			tSettlePricesImportCopy.select(tQuorumCurveMap, "endur_index_name, index_id, ref_source, ref_source_id_number, exists_flag", "quorum_index_name EQ $quorum_index_name");
			
			//Change date column from date_time to date format
			tSettlePricesImportCopy.addCol("temp_date", COL_TYPE_ENUM.COL_INT);
			iNumRows = tSettlePricesImportCopy.getNumRows();
			for (iRow = 1; iRow <= iNumRows; iRow++) {
				sDate = tSettlePricesImportCopy.getString("date", iRow);
				iDate = OCalendar.parseString(sDate);
				tSettlePricesImportCopy.setInt("temp_date", iRow, iDate);
			}
			tSettlePricesImportCopy.viewTable();
						
			tFormattedSettlePricesImport = Table.tableNew("Formatted Settle Price Import");
			tFormattedSettlePricesImport.addCol("index_id", COL_TYPE_ENUM.COL_INT);
			tFormattedSettlePricesImport.addCol("date", COL_TYPE_ENUM.COL_INT);
			tFormattedSettlePricesImport.addCol("start_date", COL_TYPE_ENUM.COL_INT);
			tFormattedSettlePricesImport.addCol("ref_source", COL_TYPE_ENUM.COL_INT);
			tFormattedSettlePricesImport.addCol("price", COL_TYPE_ENUM.COL_DOUBLE);

			tFormattedSettlePricesImport.select(tSettlePricesImportCopy, 
					"index_id, temp_date(date), temp_date(start_date), ref_source_id_number(ref_source), price", "exists_flag EQ 1");
			tFormattedSettlePricesImport.group("index_id, date");
			
			//tFormattedSettlePricesImport.viewTable();
		}
		catch (Exception e) {
    		OConsole.oprint("Error: " + e.getLocalizedMessage());
		}
		finally {
			tSettlePricesImportCopy.destroy();
			tQuorumCurveMap.destroy();
			tRefSourceMap.destroy();
			tIdxDef.destroy();
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
	private Table getQuorumCurveMap() throws OException {
		Table tQuorumCurveMap = null;
		try {
			tQuorumCurveMap = Table.tableNew("Quorum Curve Map");
			DBaseTable.execISql(tQuorumCurveMap, "select original_value as quorum_index_name, mapped_value as endur_index_name, 1 as exists_flag from user_di_mapping_curve");
		}
		catch (Exception e) {
    		OConsole.oprint("Error: " + e.getLocalizedMessage());
		}
		
		return tQuorumCurveMap;
	}
	private Table getIdxDef() throws OException {
		Table tIdxDef = null;
		try {
			tIdxDef = Table.tableNew("Index Def");
			DBaseTable.execISql(tIdxDef, "select index_id, index_name from idx_def where db_status = " + IDX_DB_STATUS_ENUM.IDX_DB_STATUS_VALIDATED.toInt());
		}
		catch (Exception e) {
    		OConsole.oprint("Error: " + e.getLocalizedMessage());
		}	
		return tIdxDef;
	}
	private Table getSettlePricesImport(String sFilePath) throws OException {
		Table tSettlePricesImport = null;
		try {
			tSettlePricesImport = Table.tableNew();
//			tSettlePricesImport.addCol("index_id", COL_TYPE_ENUM.COL_STRING);
//			tSettlePricesImport.addCol("date", COL_TYPE_ENUM.COL_DATE);
//			tSettlePricesImport.addCol("start_date", COL_TYPE_ENUM.COL_DATE);
//			tSettlePricesImport.addCol("end_date", COL_TYPE_ENUM.COL_DATE);
//			tSettlePricesImport.addCol("yield_basis", COL_TYPE_ENUM.COL_DOUBLE);
//			tSettlePricesImport.addCol("ref_source", COL_TYPE_ENUM.COL_STRING);
//			tSettlePricesImport.addCol("index_location", COL_TYPE_ENUM.COL_STRING);
//			tSettlePricesImport.addCol("price", COL_TYPE_ENUM.COL_DOUBLE);
			
			//TODO this is temporary for settle prices
			tSettlePricesImport.addCol("quorum_index_name", COL_TYPE_ENUM.COL_STRING); //index_name
			tSettlePricesImport.addCol("date", COL_TYPE_ENUM.COL_STRING); //settle_date
			tSettlePricesImport.addCol("price", COL_TYPE_ENUM.COL_DOUBLE); //settle_amount
			
			int iRetVal = tSettlePricesImport.inputFromCSVFile(sFilePath);
			if (iRetVal != OLF_RETURN_CODE.OLF_RETURN_SUCCEED.toInt()) {
				throw new Exception("Failed to load import file.");
			}
		}
		catch (Exception e) {
    		OConsole.oprint("Error: " + e.getLocalizedMessage());
		}
		return tSettlePricesImport;
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
}
