package com.drw.interfaces;

import com.olf.openjvs.DBaseTable;
import com.olf.openjvs.IContainerContext;
import com.olf.openjvs.IScript;
import com.olf.openjvs.Index;
import com.olf.openjvs.OCalendar;
import com.olf.openjvs.OConsole;
import com.olf.openjvs.OException;
import com.olf.openjvs.Table;
import com.olf.openjvs.enums.BMO_ENUMERATION;
import com.olf.openjvs.enums.COL_TYPE_ENUM;
import com.olf.openjvs.enums.IDX_DB_STATUS_ENUM;

public class ForwardPrices_m implements IScript {

	@Override
	public void execute(IContainerContext context) throws OException {
		Table tArg = null;

		try {
			ForwardPrices oFowardPricesImport = new ForwardPrices();
			oFowardPricesImport.run();
			
//			tArg = context.getArgumentsTable();
//			String sFilePath = tArg.getString("import_file_path", 1);
//			Table tForwardPriceImport = getForwardPricesImport(sFilePath);
//			tForwardPriceImport.viewTable();
//			
//			updateGridPoints(tForwardPriceImport);
			//importForwardPrices(tSettlePricesImport);
		}
		catch (Exception e) {
    		OConsole.oprint("Error: " + e.getLocalizedMessage());
		}
	}
	private Table getForwardPricesImport(String sFilePath) throws OException {
		Table tForwardPricesImport = null;
		try {
			tForwardPricesImport = Table.tableNew();
			tForwardPricesImport.addCol("HDR", COL_TYPE_ENUM.COL_STRING);
			tForwardPricesImport.addCol("IDX", COL_TYPE_ENUM.COL_STRING);
			tForwardPricesImport.addCol("index_name", COL_TYPE_ENUM.COL_STRING);
			tForwardPricesImport.addCol("gridpoint_name", COL_TYPE_ENUM.COL_STRING);
			tForwardPricesImport.addCol("val_set", COL_TYPE_ENUM.COL_STRING);
			tForwardPricesImport.addCol("val1", COL_TYPE_ENUM.COL_DOUBLE);
			tForwardPricesImport.addCol("val2", COL_TYPE_ENUM.COL_DOUBLE);
			tForwardPricesImport.addCol("date_match", COL_TYPE_ENUM.COL_STRING); //field type?? > default to string for now
			tForwardPricesImport.addCol("field_name", COL_TYPE_ENUM.COL_STRING);
			int iRetVal = tForwardPricesImport.inputFromCSVFile(sFilePath);
		}
		catch (Exception e) {
    		OConsole.oprint("Error: " + e.getLocalizedMessage());
		}
		return tForwardPricesImport;
	}
	
	private Table getIndexes() throws OException {
		Table tIndexes = null;
		StringBuilder sbQuery = null;
		try {
			sbQuery = new StringBuilder();
			sbQuery.append("select index_id ");
			sbQuery.append("from idx_def ");
			sbQuery.append("where db_status = " + IDX_DB_STATUS_ENUM.IDX_DB_STATUS_VALIDATED.toInt());

			tIndexes = Table.tableNew();
			int iRetVal = DBaseTable.execISql(getIndexes(), sbQuery.toString());
		}
		catch (Exception e) {
    		OConsole.oprint("Error: " + e.getLocalizedMessage());
		}
		return tIndexes;
	}
	private void updateIndexes(Table tSource, Table tIndexes) throws OException {
		try {
			
		}
		catch (Exception e) {
    		OConsole.oprint("Error: " + e.getLocalizedMessage());
		}
	}
	private void saveIndexes(Table tIndexes) throws OException {
		try {
//			com.olf.openjvs.Index.exportData(tIndexes, tIndexes)	
//			Index.importData(tIndexes, tIndexes, 0);
//			Index.saveHistoricalPrices(tIndexes);
		}
		catch (Exception e) {
    		OConsole.oprint("Error: " + e.getLocalizedMessage());
		}
	}
	
	
//	public void updateGridPoints(Table tForwardPriceImport) throws OException
//    {
//		Table tGpts = null;
//		Table tDistinctIndex = null;
//		BMO_ENUMERATION oBMOEnum = null;
//		int iSaveUniversal = 0;
//		int iSaveClose = 0;
//		int iCloseDate = 0;
//		
//		String sIndexName = "";
//		String sGridPointName = "";
//		try {
//			String sIndexName = "NYM_NG";
//
//			// Load current grid points and reset values on first grid point
//			tGpts = Index.loadAllGpts(sIndexName);
////			tGpts.setDouble("input.bid", 1, 0.012);
////			tGpts.setDouble("input.mid", 1, 0.0);
////			tGpts.setDouble("input.offer", 1, 0.030);
////			tGpts.setDouble("bo_spd", 1, -0.01);
//	
//			String sGridPointName = "@131ng";
//			int iFoundRow = tGpts.unsortedFindString("name", sGridPointName, SEARCH_CASE_ENUM.CASE_INSENSITIVE);
//			tGpts.setDouble("input.mid", iFoundRow, 55);
//			tGpts.setDouble("effective.mid", iFoundRow, 55);
//
//			tGpts.setTableName("Grid Point Udate - input data");
//			tGpts.viewTable();
//			// Update the BID and OFFER Prices on first grid point
//			int iCloseDate = OCalendar.today();
//			int iRetVal = Index.updateGpts(sIndexName, tGpts, BMO_ENUMERATION.BMO_MID, 1, 1, iCloseDate);
//			if (iRetVal != OLF_RETURN_CODE.OLF_RETURN_SUCCEED.toInt()) {
//				throw new Exception("Failed to update forward prices");
//			}
//			
//			// View updated grid point
//			tGpts = Index.loadAllGpts(sIndexName);
//			tGpts.setTableName("Grid Point Udate - updated data");
//			tGpts.viewTable();
//		} catch (Exception e) {
//			OConsole.oprint("Error: " + e.getLocalizedMessage());
//		}
//		finally {
//			tGpts.destroy();
//
//		}
//    }
	
}
