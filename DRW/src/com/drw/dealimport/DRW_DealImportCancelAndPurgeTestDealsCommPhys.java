package com.drw.dealimport;
  

import com.drw.dealimport.DRW_DealImportCancelAndPurgeTestDeals.DEBUG_LOGFILE;
import com.drw.dealimport.DRW_DealImportCancelAndPurgeTestDeals.FUNC;
import com.olf.openjvs.IContainerContext;
import com.olf.openjvs.IScript;
import com.olf.openjvs.OException;
import com.olf.openjvs.PluginCategory;
import com.olf.openjvs.PluginType;
import com.olf.openjvs.Util;
import com.olf.openjvs.enums.INS_TYPE_ENUM;
import com.olf.openjvs.enums.SCRIPT_CATEGORY_ENUM;
import com.olf.openjvs.enums.SCRIPT_TYPE_ENUM;

/*
Script Name: DRW_DealImportCancelAndPurgeTestDealsCommPhys
Description:  DRW_DealImportCancelAndPurgeTestDealsCommPhys

Revision History
07-Jun-2023   Brian  New Script

*/

@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_GENERIC)
@PluginType(SCRIPT_TYPE_ENUM.MAIN_SCRIPT)
public class DRW_DealImportCancelAndPurgeTestDealsCommPhys implements IScript {
	
	// For Testing set to true
	// boolean bPurgeEveryDealOver30000 = false;

	public void execute(IContainerContext context) throws OException {
		String className = this.getClass().getSimpleName();
		int iRunNumber = DEBUG_LOGFILE.getRunNumber(className);
		
		try {
			DEBUG_LOGFILE.logToFile("START", iRunNumber, className);
			FUNC.doEverything(INS_TYPE_ENUM.comm_physical, iRunNumber, className);
			
			DEBUG_LOGFILE.logToFile("END", iRunNumber, className);
			
		} catch (Exception e) {
			// do not log this
		}
		Util.exitSucceed();
	}


}
