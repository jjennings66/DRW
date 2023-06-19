package com.drw.interfaces;

import com.drw.webservice.WebService;
import com.drw.webservice.WebService.WebRequestContentType;
import com.drw.webservice.WebService.WebRequestMethod;
import com.olf.openjvs.Index;
import com.olf.openjvs.OCalendar;
import com.olf.openjvs.OConsole;
import com.olf.openjvs.OException;
import com.olf.openjvs.Table;
import com.olf.openjvs.Util;
import com.olf.openjvs.enums.BMO_ENUMERATION;
import com.olf.openjvs.enums.COL_TYPE_ENUM;
import com.olf.openjvs.enums.DATE_FORMAT;
import com.olf.openjvs.enums.OLF_RETURN_CODE;

public class ForwardPrices {
	private final String sURL = "http://prices.gas.drw/api/endur/marks.csv";
	private String sReportFileName = "ForwardPricesImport";

	public void run() throws OException {
		Table tForwardPricesImport = null;
		try {			
			tForwardPricesImport = getForwardPricesImport();
			importForwardPrices(tForwardPricesImport);
    		OConsole.oprint("Forward Prices Import Succeeded");
		}
		catch (Exception e) {
    		OConsole.oprint("Error: " + e.getLocalizedMessage());
		}
		finally {
			tForwardPricesImport.destroy();
		}
	}
	private Table getForwardPricesImport() throws OException {
		Table tForwardPricesImport = null;
		int iNumRows = -1;
		int iRow = -1;
		int iMaxRow = -1;
		String sRowItems = "";
		String sHDR = "";
		String sIDX = "";
		String sIndexName = "";
		String sGridPointName = "";
		String sValSet = "";
		String sVal1 = "";
		String sVal2 = "";
		String sDateMatch = "";
		String sFieldName = "";

		try {
			WebService oForwardPricesWb = new WebService(sURL);
			oForwardPricesWb.setConnectionProperties("", "", WebRequestMethod.GET, WebRequestContentType.TEXT_CSV, true);
			Table tMessages = oForwardPricesWb.sendGetRequest();
			
			tForwardPricesImport = createForwardPricesImportTable();
			iNumRows = tMessages.getNumRows();
			//Skippy row 1 cause that's the header row
			for (iRow = 2;iRow <= iNumRows; iRow++) {
				sRowItems = tMessages.getString("messages", iRow);
				String[] oRow = sRowItems.replace("\"", "").split(",");

				sHDR = oRow[0];
				sIDX = oRow[1];
				sIndexName = oRow[2];
				sGridPointName = oRow[3];
				sValSet = oRow[4];
				sVal1 = oRow[5];
				sVal2 = oRow[6];
				sDateMatch = oRow[7];
				sFieldName = oRow[8];
								
				iMaxRow = tForwardPricesImport.addRow();
				tForwardPricesImport.setString("HDR", iMaxRow, isStringNullOrEmpty(sHDR) ? "" : sHDR);
				tForwardPricesImport.setString("IDX", iMaxRow, isStringNullOrEmpty(sIDX) ? "" : sIDX);
				tForwardPricesImport.setString("index_name", iMaxRow, isStringNullOrEmpty(sIndexName) ? "" : sIndexName);
				tForwardPricesImport.setString("gridpoint_name", iMaxRow, isStringNullOrEmpty(sGridPointName) ? "" : sGridPointName);
				tForwardPricesImport.setString("val_set", iMaxRow, isStringNullOrEmpty(sValSet) ? "" : sValSet);
				
				tForwardPricesImport.setDouble("val1", iMaxRow, tryGetDouble(sVal1));
				tForwardPricesImport.setDouble("val2", iMaxRow, tryGetDouble(sVal2));

				tForwardPricesImport.setString("date_match", iMaxRow, isStringNullOrEmpty(sDateMatch) ? "" : sDateMatch);
				tForwardPricesImport.setString("field_name", iMaxRow, isStringNullOrEmpty(sFieldName) ? "" : sFieldName);

			}
		}
		catch (Exception e) {
    		OConsole.oprint("Error: " + e.getLocalizedMessage());
		}
		return tForwardPricesImport;
	}
	public void importForwardPrices(Table tForwardPriceImport) throws OException
    {
		Table tReport = null;
		Table tDistinctIndex = null;
		int iSaveUniversal = 0;
		int iSaveClose = 0;
		int iCloseDate = 0;
		int iRow = -1;
		int iNumRows = -1;
		int iRetVal = -1;
		BMO_ENUMERATION oBmoEnum = null;
		
		String sIndexName = "";
		String sValueSet = "";
		try {
			
			iSaveUniversal = 1;
			iSaveClose = 1;
			iCloseDate = OCalendar.today();
			
			tDistinctIndex = Table.tableNew("Distinct Index");
			tDistinctIndex.addCol("index_name", COL_TYPE_ENUM.COL_STRING);
			tDistinctIndex.select(tForwardPriceImport, "index_name, val_set", "val1 LT 0.0");
			tDistinctIndex.select(tForwardPriceImport, "index_name, val_set", "val1 GT 0.0");
			tDistinctIndex.group("index_name, val_set");
			tDistinctIndex.distinctRows();
			
			tDistinctIndex.addCol("process_status", COL_TYPE_ENUM.COL_STRING);
			tDistinctIndex.addCol("error_msg", COL_TYPE_ENUM.COL_STRING);
			tDistinctIndex.addCol("grid_points", COL_TYPE_ENUM.COL_TABLE);

			iNumRows = tDistinctIndex.getNumRows();
			for (iRow = 1; iRow <= iNumRows; iRow++) {
				sIndexName = tDistinctIndex.getString("index_name", iRow);
				sValueSet = tDistinctIndex.getString("val_set", iRow);
				Table tGpts = Index.loadAllGpts(sIndexName);
				
				if (tGpts == null || tGpts.getNumRows() < 1) {
					tDistinctIndex.setString("process_status", iRow, "error");
					tDistinctIndex.setString("error_msg", iRow, "Index not configured or grid points not available");
					continue;
				}
				tGpts.select(tForwardPriceImport, "val1(input.mid), val1(effective.mid)", "gridpoint_name EQ $name");
				
				oBmoEnum = BMO_ENUMERATION.DEFAULT_BMO;
				if (sValueSet.equalsIgnoreCase("M")) {
					oBmoEnum = BMO_ENUMERATION.BMO_MID;
				}
				
				//Where we insert forward prices
				iRetVal = Index.updateGpts(sIndexName, tGpts, oBmoEnum, iSaveUniversal, iSaveClose, iCloseDate);
				if (iRetVal != OLF_RETURN_CODE.OLF_RETURN_SUCCEED.toInt()) {
					
					tDistinctIndex.setString("process_status", iRow, "error");
					tDistinctIndex.setString("error_msg", iRow, "failed to update forward prices");
				}
				tGpts.group("gpt_end_date");
				tDistinctIndex.setString("process_status", iRow, "success");
				tDistinctIndex.setTable("grid_points", iRow, tGpts);

				tGpts.destroy();
			}
			
			tReport = tDistinctIndex.copyTable();
			tReport.setTableName("Forward Prices Import Report");
			tReport.delCol("grid_points");
			iRetVal = saveReport(tReport);
			
		} catch (Exception e) {
			OConsole.oprint("Error: " + e.getLocalizedMessage());
		}
		finally {
			tDistinctIndex.destroy();
			tReport.destroy();
		}
    }
	private int saveReport(Table tReport) throws OException {
		int iRetVal = -1;
		try {
			iRetVal = tReport.printTableDumpToFile(Util.reportGetDirForToday() + "/" 
					+ sReportFileName + "_" + OCalendar.formatDateInt(OCalendar.today(), DATE_FORMAT.DATE_FORMAT_ISO8601) + ".csv");
		}
		catch (Exception e) {
			OConsole.oprint("Error: " + e.getLocalizedMessage());
		}
		return iRetVal;
	}
	private Table createForwardPricesImportTable() throws OException {
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
		}
		catch (Exception e) {
    		OConsole.oprint("Error: " + e.getLocalizedMessage());
		}
		return tForwardPricesImport;
	}
	private double tryGetDouble(String sValue) throws OException {
		double dRetVal = 0.0;
		try {
			dRetVal = isStringNullOrEmpty(sValue) ? 0.0 : Double.valueOf(sValue);
		}
		catch (Exception e) {
			//Dont log
		}
		return dRetVal;
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
