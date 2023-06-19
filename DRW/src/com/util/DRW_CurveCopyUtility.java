package com.util;
  

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;

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
Script Name: DRW_CurveCopyUtility
Description:  DRW_CurveCopyUtility

Revision History
19-Apr-2023   Brian   New Script
28-Apr-2023  Brian   Updated the list of PP curves
05-May-2023  Brian   Updated the list of IF, GD, PP curves to add 18 more curves
10-May-2023  Brian   Updated the list of IF, GD, PP curves to add 14 more curves


THIS HAS IT SET TO EXIT WITHOUT DOING ANYTHINGS   

*/

@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_GENERIC)
@PluginType(SCRIPT_TYPE_ENUM.MAIN_SCRIPT)
public class DRW_CurveCopyUtility implements IScript {
	
	// if false... will *not* replace existing curves
	
	// set to false for extra safety
	static boolean bReplaceExistingCurvesFlag = false;
	
	// TODO
	// these were set to FALSE for safety.. set these to 'true' to actually do something
	static boolean bImportNGICurves = false;
	static boolean bImportIFCurves = false;
	static boolean bImportGDCurves = false;
	static boolean bImportPPCurves = false;
	
	public void execute(IContainerContext context) throws OException {
		String className = this.getClass().getSimpleName();
		int iRunNumber = DEBUG_LOGFILE.getRunNumber(className); 
		
		try {
			DEBUG_LOGFILE.logToFile("START", iRunNumber, className);
  			
            // TODO
            Util.exitSucceed();
			
			doEverything(iRunNumber, className);
			DEBUG_LOGFILE.logToFile("END", iRunNumber, className);
			 
		} catch (Exception e) {
			// do not log this
		}
		Util.exitSucceed();
	}

	public void doEverything(int iRunNumber, String className) throws OException {
		try {
			
		//	this.createIndex("NG_HSC_IF", "NG_ELPASO_IF", iRunNumber, className);

  				
			if (bImportNGICurves == true) {
				this.createNGICurves(iRunNumber, className);
			}
			if (bImportIFCurves == true) {
				this.createIFCurves(iRunNumber, className);
			}
			if (bImportGDCurves == true) {
				this.createGDCurves(iRunNumber, className);
			}
			if (bImportPPCurves == true) {
				this.createPPCurves(iRunNumber, className);
			} 
			 
		} catch (Exception e) {
			// do not log this
		}	 
	}
		

	 
		public void createPPCurves(int iRunNumber, String className) throws OException {
			try {
				
				this.createIndex("NG_HSC_PP", "NG_ATMOS_ZONE_3_PP", iRunNumber, className);
				this.createIndex("NG_HSC_PP", "NG_BISTINEAU_STRG_PP", iRunNumber, className);
				this.createIndex("NG_HSC_PP", "NG_GTN_MALIN_PP", iRunNumber, className); 
				this.createIndex("NG_HSC_PP", "NG_NNG_1_7_PP", iRunNumber, className);
				this.createIndex("NG_HSC_PP", "NG_NORTEX_TOLAR_PP", iRunNumber, className);
				this.createIndex("NG_HSC_PP", "NG_OGT_PP", iRunNumber, className);
				this.createIndex("NG_HSC_PP", "NG_OWT_PP", iRunNumber, className);
				this.createIndex("NG_HSC_PP", "NG_PGE_DAGGETT_PP", iRunNumber, className);
				this.createIndex("NG_HSC_PP", "NG_PGE_FREEMONT_PP", iRunNumber, className);
				this.createIndex("NG_HSC_PP", "NG_PGE_KRS_PP", iRunNumber, className);
				this.createIndex("NG_HSC_PP", "NG_PGE_TOPOCK_PP", iRunNumber, className);
				this.createIndex("NG_HSC_PP", "NG_TGP_ZONE_0_SOUTH_PP", iRunNumber, className);
				this.createIndex("NG_HSC_PP", "NG_TGP_ZONE_1_100_LEG_PP", iRunNumber, className);
				this.createIndex("NG_HSC_PP", "NG_WASH10_STRG_PP", iRunNumber, className);
				
				
//				this.createIndex("NG_HSC_PP", "NG_ANR_ML7_EAST_PP", iRunNumber, className);
//				this.createIndex("NG_HSC_PP", "NG_ANR_ML7_WEST_PP", iRunNumber, className);
//				this.createIndex("NG_HSC_PP", "NG_ANR_SE_PP", iRunNumber, className);
//				this.createIndex("NG_HSC_PP", "NG_ANR_SW_PP", iRunNumber, className);
//				this.createIndex("NG_HSC_PP", "NG_BENNINGTON_PP", iRunNumber, className);
//				this.createIndex("NG_HSC_PP", "NG_ELPASO_PP", iRunNumber, className);
//				this.createIndex("NG_HSC_PP", "NG_NGPL_TOK_EAST_PP", iRunNumber, className);
//				this.createIndex("NG_HSC_PP", "NG_NNG_13_16A_PP", iRunNumber, className);
//				this.createIndex("NG_HSC_PP", "NG_NNG_VENT_PP", iRunNumber, className);
//				this.createIndex("NG_HSC_PP", "NG_REX_E_NGPL_PP", iRunNumber, className);
//				this.createIndex("NG_HSC_PP", "NG_REX_NGPL_IA_GC_PP", iRunNumber, className);
//				this.createIndex("NG_HSC_PP", "NG_ROVER_ANR_PP", iRunNumber, className);
//				this.createIndex("NG_HSC_PP", "NG_SOUTHERNSTAR_PP", iRunNumber, className);
//				this.createIndex("NG_HSC_PP", "NG_TETCO_WLA_PP", iRunNumber, className);
//				this.createIndex("NG_HSC_PP", "NG_TGP_500_LEG_PP", iRunNumber, className);
//				this.createIndex("NG_HSC_PP", "NG_TGP_ZONE_0_NORTH_PP", iRunNumber, className);
//				this.createIndex("NG_HSC_PP", "NG_TGP_ZONE_1_STA_87_PP", iRunNumber, className);
//				this.createIndex("NG_HSC_PP", "NG_TW_CENTRAL_PP", iRunNumber, className);


//				this.createIndex("NG_HSC_PP", "NG_ALLIANCE_PP", iRunNumber, className);
//				this.createIndex("NG_HSC_PP", "NG_ANR_OK_PP", iRunNumber, className);
//				this.createIndex("NG_HSC_PP", "NG_CG_MAIN_PP", iRunNumber, className);
//				this.createIndex("NG_HSC_PP", "NG_CHICAGO_CITYGATE_PP", iRunNumber, className);
//				this.createIndex("NG_HSC_PP", "NG_CONSUMERS_CITYGATE_PP", iRunNumber, className);
//				this.createIndex("NG_HSC_PP", "NG_DAWN_ONTARIO_PP", iRunNumber, className);
//				this.createIndex("NG_HSC_PP", "NG_DEMARC_PP", iRunNumber, className);
//				this.createIndex("NG_HSC_PP", "NG_EPNG_WEST_TEXAS_PP", iRunNumber, className);
//				this.createIndex("NG_HSC_PP", "NG_MICH_CON_PP", iRunNumber, className);
//				this.createIndex("NG_HSC_PP", "NG_NGPL_MC_PP", iRunNumber, className);
//				this.createIndex("NG_HSC_PP", "NG_NGPL_STX_PP", iRunNumber, className);
//				this.createIndex("NG_HSC_PP", "NG_NGPL_TOK_PP", iRunNumber, className);
//				this.createIndex("NG_HSC_PP", "NG_PEPL_PP", iRunNumber, className);
//				this.createIndex("NG_HSC_PP", "NG_PGE_CITYGATE_PP", iRunNumber, className);
//				this.createIndex("NG_HSC_PP", "NG_PGE_SO_PP", iRunNumber, className);
//				this.createIndex("NG_HSC_PP", "NG_PINE_PRAIRIE_HUB_PP", iRunNumber, className);
//				this.createIndex("NG_HSC_PP", "NG_REX_PP", iRunNumber, className);
//				this.createIndex("NG_HSC_PP", "NG_REX_ZONE_3_PP", iRunNumber, className);
//				this.createIndex("NG_HSC_PP", "NG_TENN_500_LEG_PP", iRunNumber, className);
//				this.createIndex("NG_HSC_PP", "NG_TGP_ZONE_0_PP", iRunNumber, className);
//				this.createIndex("NG_HSC_PP", "NG_TGP_ZONE_1_PP", iRunNumber, className);
//				this.createIndex("NG_HSC_PP", "NG_TRANSCO_Z5_SOUTH_PP", iRunNumber, className);
//				this.createIndex("NG_HSC_PP", "NG_TRANSCO_ZONE_4_PP", iRunNumber, className);
//				this.createIndex("NG_HSC_PP", "NG_TX_GAS_ZONE_1_PP", iRunNumber, className);
//				this.createIndex("NG_HSC_PP", "NG_WAHA_PP", iRunNumber, className);
//
//				
//				this.createIndex("NG_HH_PP", "NG_ALGONQUIN_PP", iRunNumber, className);
//			//	this.createIndex("NG_HH_PP", "NG_CHIC_PP", iRunNumber, className);
//				this.createIndex("NG_HH_PP", "NG_CIGRK_PP", iRunNumber, className);
//				this.createIndex("NG_HH_PP", "NG_HSC_PP", iRunNumber, className);
//			//	this.createIndex("NG_HH_PP", "NG_MALIN_PP", iRunNumber, className);
//				this.createIndex("NG_HH_PP", "NG_MICHCON_PP", iRunNumber, className);
//				this.createIndex("NG_HH_PP", "NG_NNGDMK_PP", iRunNumber, className);
//				this.createIndex("NG_HH_PP", "NG_NNGVENT_PP", iRunNumber, className);
//				this.createIndex("NG_HH_PP", "NG_NWPCB_PP", iRunNumber, className);
//				this.createIndex("NG_HH_PP", "NG_NWPRM_PP", iRunNumber, className);
//			//	this.createIndex("NG_HH_PP", "NG_PGE_PP", iRunNumber, className);
//				this.createIndex("NG_HH_PP", "NG_SOCAL_PP", iRunNumber, className);
//			//	this.createIndex("NG_HH_PP", "NG_TETCO_ELA_PP", iRunNumber, className);
//				this.createIndex("NG_HH_PP", "NG_TRANSCOZ3_PP", iRunNumber, className);
//				
			} catch (Exception e) {
				LIB.log("createGDCurves", e, className);
			} 
		}
 
	public void createGDCurves(int iRunNumber, String className) throws OException {
		try {
		  	
			this.createIndex("NG_HSC_GD", "NG_ATMOS_ZONE_3_GD", iRunNumber, className);
			this.createIndex("NG_HSC_GD", "NG_BISTINEAU_STRG_GD", iRunNumber, className);
			this.createIndex("NG_HSC_GD", "NG_GTN_MALIN_GD", iRunNumber, className); 
			this.createIndex("NG_HSC_GD", "NG_NNG_1_7_GD", iRunNumber, className);
			this.createIndex("NG_HSC_GD", "NG_NORTEX_TOLAR_GD", iRunNumber, className);
			this.createIndex("NG_HSC_GD", "NG_OGT_GD", iRunNumber, className);
			this.createIndex("NG_HSC_GD", "NG_OWT_GD", iRunNumber, className);
			this.createIndex("NG_HSC_GD", "NG_PGE_DAGGETT_GD", iRunNumber, className);
			this.createIndex("NG_HSC_GD", "NG_PGE_FREEMONT_GD", iRunNumber, className);
			this.createIndex("NG_HSC_GD", "NG_PGE_KRS_GD", iRunNumber, className);
			this.createIndex("NG_HSC_GD", "NG_PGE_TOPOCK_GD", iRunNumber, className);
			this.createIndex("NG_HSC_GD", "NG_TGP_ZONE_0_SOUTH_GD", iRunNumber, className);
			this.createIndex("NG_HSC_GD", "NG_TGP_ZONE_1_100_LEG_GD", iRunNumber, className);
			this.createIndex("NG_HSC_GD", "NG_WASH10_STRG_GD", iRunNumber, className);

//			
//			this.createIndex("NG_HSC_GD", "NG_ANR_ML7_EAST_GD", iRunNumber, className);
//			this.createIndex("NG_HSC_GD", "NG_ANR_ML7_WEST_GD", iRunNumber, className);
//			this.createIndex("NG_HSC_GD", "NG_ANR_SE_GD", iRunNumber, className);
//			this.createIndex("NG_HSC_GD", "NG_ANR_SW_GD", iRunNumber, className);
//			this.createIndex("NG_HSC_GD", "NG_BENNINGTON_GD", iRunNumber, className);
//			this.createIndex("NG_HSC_GD", "NG_ELPASO_GD", iRunNumber, className);
//			this.createIndex("NG_HSC_GD", "NG_NGPL_TOK_EAST_GD", iRunNumber, className);
//			this.createIndex("NG_HSC_GD", "NG_NNG_13_16A_GD", iRunNumber, className);
//			this.createIndex("NG_HSC_GD", "NG_NNG_VENT_GD", iRunNumber, className);
//			this.createIndex("NG_HSC_GD", "NG_REX_E_NGPL_GD", iRunNumber, className);
//			this.createIndex("NG_HSC_GD", "NG_REX_NGPL_IA_GC_GD", iRunNumber, className);
//			this.createIndex("NG_HSC_GD", "NG_ROVER_ANR_GD", iRunNumber, className);
//			this.createIndex("NG_HSC_GD", "NG_SOUTHERNSTAR_GD", iRunNumber, className);
//			this.createIndex("NG_HSC_GD", "NG_TETCO_WLA_GD", iRunNumber, className);
//			this.createIndex("NG_HSC_GD", "NG_TGP_500_LEG_GD", iRunNumber, className);
//			this.createIndex("NG_HSC_GD", "NG_TGP_ZONE_0_NORTH_GD", iRunNumber, className);
//			this.createIndex("NG_HSC_GD", "NG_TGP_ZONE_1_STA_87_GD", iRunNumber, className);
//			this.createIndex("NG_HSC_GD", "NG_TW_CENTRAL_GD", iRunNumber, className);

		
//			this.createIndex("NG_HSC_GD", "NG_ALLIANCE_GD", iRunNumber, className);
//			this.createIndex("NG_HSC_GD", "NG_ANR_OK_GD", iRunNumber, className);
//			this.createIndex("NG_HSC_GD", "NG_CG_MAIN_GD", iRunNumber, className);
//			this.createIndex("NG_HSC_GD", "NG_CHICAGO_CITYGATE_GD", iRunNumber, className);
//			this.createIndex("NG_HSC_GD", "NG_CONSUMERS_CITYGATE_GD", iRunNumber, className);
//			this.createIndex("NG_HSC_GD", "NG_DAWN_ONTARIO_GD", iRunNumber, className);
//			this.createIndex("NG_HSC_GD", "NG_DEMARC_GD", iRunNumber, className);
//			this.createIndex("NG_HSC_GD", "NG_EPNG_WEST_TEXAS_GD", iRunNumber, className);
//			this.createIndex("NG_HSC_GD", "NG_MICH_CON_GD", iRunNumber, className);
//			this.createIndex("NG_HSC_GD", "NG_NGPL_MC_GD", iRunNumber, className);
//			this.createIndex("NG_HSC_GD", "NG_NGPL_STX_GD", iRunNumber, className);
//			this.createIndex("NG_HSC_GD", "NG_NGPL_TOK_GD", iRunNumber, className);
//			this.createIndex("NG_HSC_GD", "NG_PEPL_GD", iRunNumber, className);
//			this.createIndex("NG_HSC_GD", "NG_PGE_CITYGATE_GD", iRunNumber, className);
//			this.createIndex("NG_HSC_GD", "NG_PGE_SO_GD", iRunNumber, className);
//			this.createIndex("NG_HSC_GD", "NG_PINE_PRAIRIE_HUB_GD", iRunNumber, className);
//			this.createIndex("NG_HSC_GD", "NG_REX_GD", iRunNumber, className);
//			this.createIndex("NG_HSC_GD", "NG_REX_ZONE_3_GD", iRunNumber, className);
//			this.createIndex("NG_HSC_GD", "NG_TENN_500_LEG_GD", iRunNumber, className);
//			this.createIndex("NG_HSC_GD", "NG_TGP_ZONE_0_GD", iRunNumber, className);
//			this.createIndex("NG_HSC_GD", "NG_TGP_ZONE_1_GD", iRunNumber, className);
//			this.createIndex("NG_HSC_GD", "NG_TRANSCO_Z5_SOUTH_GD", iRunNumber, className);
//			this.createIndex("NG_HSC_GD", "NG_TRANSCO_ZONE_4_GD", iRunNumber, className);
//			this.createIndex("NG_HSC_GD", "NG_TX_GAS_ZONE_1_GD", iRunNumber, className);
//			this.createIndex("NG_HSC_GD", "NG_WAHA_GD", iRunNumber, className);

			
			
//			// GD-ALLIANCE
//			this.createIndex("NG_HH_GD", "NG_ALLIANCE_GD", iRunNumber, className);
//			
//			// GD-ANR OK
//			this.createIndex("NG_HH_GD", "NG_ANROK_GD", iRunNumber, className);
//			
//			// GD-CG Main and/or GD-CHICAGO CITYGATE
//			this.createIndex("NG_HH_GD", "NG_CGMAIN_GD", iRunNumber, className);
//			
//			// GD-EPNG West Texas
//			// is this related to NG_TRWEST_PERM_IF?
//			this.createIndex("NG_HH_GD", "NG_EPNG_WEST_TEXAS_GD", iRunNumber, className);
//
//			// GD-Mich Con
//			this.createIndex("NG_HH_GD", "NG_MICHCON_GD", iRunNumber, className);
//
//			// GD-NGPL TOK
//			this.createIndex("NG_HH_GD", "NG_NGPLTX_GD", iRunNumber, className);
//			
//			// GD-PEPL
//			this.createIndex("NG_HH_GD", "NG_PEPL_GD", iRunNumber, className);
//			
//			// GD-REX
//			this.createIndex("NG_HH_GD", "NG_REX_GD", iRunNumber, className);
//			
//			// GD-TGP Zone 0
//			this.createIndex("NG_HH_GD", "NG_TGPZ0_GD", iRunNumber, className);
//			
//			// GD-Tenn, 500 Leg ???
//			this.createIndex("NG_HH_GD", "NG_TGPZ1_500_GD", iRunNumber, className);
//			
//
//			
//			// Additional Curves
//		 	this.createIndex("NG_HH_GD", "NG_ALGONQUIN_GD", iRunNumber, className); 
//			this.createIndex("NG_HH_GD", "NG_CIGRK_GD", iRunNumber, className);
//			this.createIndex("NG_HH_GD", "NG_HSC_GD", iRunNumber, className); 
//			this.createIndex("NG_HH_GD", "NG_NNGDMK_GD", iRunNumber, className);
//			this.createIndex("NG_HH_GD", "NG_NNGVENT_GD", iRunNumber, className);
//			this.createIndex("NG_HH_GD", "NG_NWPCB_GD", iRunNumber, className);
//			this.createIndex("NG_HH_GD", "NG_NWPRM_GD", iRunNumber, className); 
//			this.createIndex("NG_HH_GD", "NG_SOCAL_GD", iRunNumber, className); 
//			this.createIndex("NG_HH_GD", "NG_TRANSCOZ3_GD", iRunNumber, className);
//
//		 

			
		} catch (Exception e) {
			LIB.log("createGDCurves", e, className);
		} 
	}
	

	public void createIFCurves(int iRunNumber, String className) throws OException {
		try {
			
			this.createIndex("NG_HSC_IF", "NG_ATMOS_ZONE_3_IF", iRunNumber, className);
			this.createIndex("NG_HSC_IF", "NG_BISTINEAU_STRG_IF", iRunNumber, className);
			this.createIndex("NG_HSC_IF", "NG_GTN_MALIN_IF", iRunNumber, className); 
			this.createIndex("NG_HSC_IF", "NG_NNG_1_7_IF", iRunNumber, className);
			this.createIndex("NG_HSC_IF", "NG_NORTEX_TOLAR_IF", iRunNumber, className);
			this.createIndex("NG_HSC_IF", "NG_OGT_IF", iRunNumber, className);
			this.createIndex("NG_HSC_IF", "NG_OWT_IF", iRunNumber, className);
			this.createIndex("NG_HSC_IF", "NG_PGE_DAGGETT_IF", iRunNumber, className);
			this.createIndex("NG_HSC_IF", "NG_PGE_FREEMONT_IF", iRunNumber, className);
			this.createIndex("NG_HSC_IF", "NG_PGE_KRS_IF", iRunNumber, className);
			this.createIndex("NG_HSC_IF", "NG_PGE_TOPOCK_IF", iRunNumber, className);
			this.createIndex("NG_HSC_IF", "NG_TGP_ZONE_0_SOUTH_IF", iRunNumber, className);
			this.createIndex("NG_HSC_IF", "NG_TGP_ZONE_1_100_LEG_IF", iRunNumber, className);
			this.createIndex("NG_HSC_IF", "NG_WASH10_STRG_IF", iRunNumber, className);
			
//			
//			this.createIndex("NG_HSC_IF", "NG_ANR_ML7_EAST_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_ANR_ML7_WEST_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_ANR_SE_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_ANR_SW_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_BENNINGTON_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_ELPASO_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_NGPL_TOK_EAST_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_NNG_13_16A_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_NNG_VENT_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_REX_E_NGPL_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_REX_NGPL_IA_GC_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_ROVER_ANR_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_SOUTHERNSTAR_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_TETCO_WLA_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_TGP_500_LEG_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_TGP_ZONE_0_NORTH_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_TGP_ZONE_1_STA_87_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_TW_CENTRAL_IF", iRunNumber, className);


//			this.createIndex("NG_HSC_IF", "NG_ALLIANCE_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_ANR_OK_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_CG_MAIN_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_CHICAGO_CITYGATE_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_CONSUMERS_CITYGATE_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_DAWN_ONTARIO_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_DEMARC_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_EPNG_WEST_TEXAS_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_MICH_CON_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_NGPL_MC_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_NGPL_STX_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_NGPL_TOK_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_PEPL_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_PGE_CITYGATE_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_PGE_SO_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_PINE_PRAIRIE_HUB_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_REX_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_REX_ZONE_3_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_TENN_500_LEG_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_TGP_ZONE_0_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_TGP_ZONE_1_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_TRANSCO_Z5_SOUTH_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_TRANSCO_ZONE_4_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_TX_GAS_ZONE_1_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_WAHA_IF", iRunNumber, className);

		
			
//			
//			// Used by DRW
//			
//			// IF-ALLIANCE
//			this.createIndex("NG_HSC_IF", "NG_ALLIANCE_IF", iRunNumber, className);
//			
//			// IF-ANR OK
//			this.createIndex("NG_HSC_IF", "NG_ANROK_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_CGMAIN_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_EPNG_WEST_TEXAS_IF", iRunNumber, className);
// 
//			// IF-Mich Con
//			this.createIndex("NG_HSC_IF", "NG_MICHCON_IF", iRunNumber, className);
//
//			// IF-NGPL TOK
//			this.createIndex("NG_HSC_IF", "NG_NGPLTX_IF", iRunNumber, className);
//
//			// IF-PEPL
//			this.createIndex("NG_HSC_IF", "NG_PEPL_IF", iRunNumber, className);
//			
//			// IF-REX    
//			this.createIndex("NG_HSC_IF", "NG_REX_IF", iRunNumber, className);
//		
//			// IF-REX, Zone 3
//			this.createIndex("NG_HSC_IF", "NG_REX_ZONE3_IF", iRunNumber, className);
//				
//			
//			// IF-TGP Zone 0
//			this.createIndex("NG_HSC_IF", "NG_TGPZ0_IF", iRunNumber, className);
//			
//			// IF-Tenn, 500 Leg ???
//			this.createIndex("NG_HSC_IF", "NG_TGPZ1_500_IF", iRunNumber, className);
//			
//			 
//			
//			
//			
//			// Additional curves not necessarily used by DRW
//			this.createIndex("NG_HSC_IF", "NG_ALGONQUIN_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_ANRLA_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_CENTERPOINT_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_CGTAPP_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_CGULF_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_CIGRK_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_DOMAPP_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_FGTZ1_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_FGTZ2_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_FGTZ3_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_HSC_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_KATYETX_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_KERN_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_LEIDY_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_NGPLMC_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_NGPLST_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_NGPL_LOU_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_NNGDMK_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_NNGVENT_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_NWPCB_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_NWPRM_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_ONEOK_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_PERMIAN_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_QUESTAR_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_REXZ3_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_SANJUAN_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_SOCAL_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_SONAT_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_SOUTHERN_STAR_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_TETCOELA_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_TETCOETX_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_TETCOM2_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_TETCOM3_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_TETCOSTX_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_TETCOWLA_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_TGPZ1_800_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_TGPZ4_300L_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_TRANSCOZ1_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_TRANSCOZ2_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_TRANSCOZ3_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_TRANSCOZ4_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_TRANSCOZ5_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_TRANSCOZ6NY_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_TRANSCOZ6_NonNY_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_TRKLA_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_TRWEST_PERM_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_TXGSSL_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_TXGSZ1_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_UNIONDAWN_IF", iRunNumber, className);
//			this.createIndex("NG_HSC_IF", "NG_WAHA_IF", iRunNumber, className);

			
		} catch (Exception e) {
			LIB.log("createIFCurves", e, className);
		} 
	}
	
	
	public void createNGICurves(int iRunNumber, String className) throws OException {
		try {

			// TODO
			// get this to work
			
			// Used by DRW
			
			// NGI-CHICAGO
			
			//this.createIndex("NGI-CHICAGO", "NGI-CHICAGO", iRunNumber, className);
			 

		//	this.createIndex("NG_HSC_IF", "NG_CGMAIN_NGI", iRunNumber, className);
			
		} catch (Exception e) {
			LIB.log("createNGICurves", e, className);
		} 
	}
	

	public void createIndex(String sSourceIndex, String sDestIndex, int iRunNumber, String className) throws OException {
		try {
			
			
			

			DEBUG_LOGFILE.logToFile("Creating Index: " + sDestIndex, iRunNumber, className);
			
			
			String sTempFilePreface = "OpenLinkSource";
			String sTempFileSuffix = ".txt";
			Path tempFileFullPath1 = Files.createTempFile(sTempFilePreface, sTempFileSuffix);
			String sTempFileFullPath1 = tempFileFullPath1.toString();
			LIB.log("Temp File Name: " + sTempFileFullPath1, className);
			
			sTempFilePreface = "OpenLinkDestination";
			sTempFileSuffix = ".txt";
			Path tempFileFullPath2 = Files.createTempFile(sTempFilePreface, sTempFileSuffix);
			String sTempFileFullPath2 = tempFileFullPath2.toString();
			
			boolean bIsGasDaily = false;
			boolean bIsPhysPremium = false;
			String sOriginalParentName = "";
			String sNewParentName = "";
			{
				int iLen = Str.len(sDestIndex);
				int iStartLastTwo = iLen - 2;
				int iCharsToGet = 2;
				String sLastTwo = Str.substr(sDestIndex, iStartLastTwo, iCharsToGet);
				int iStartChar = 0;
				iCharsToGet = iStartLastTwo;
				String sFirstChars = Str.substr(sDestIndex, iStartChar, iCharsToGet);

				if (Str.iEqual(sLastTwo, "GD") == CONST_VALUES.VALUE_OF_TRUE) {
					bIsGasDaily = true;
					sNewParentName = sFirstChars + "IF";
					LIB.log("sNewParentName: " + sNewParentName, className);
					LIB.log("sNewParentName: " + sNewParentName, className);
					LIB.log("sNewParentName: " + sNewParentName, className);
				}
				if (Str.iEqual(sLastTwo, "PP") == CONST_VALUES.VALUE_OF_TRUE) {
					bIsGasDaily = true;
					sNewParentName = sFirstChars + "GD";
					LIB.log("sNewParentName: " + sNewParentName, className);
					LIB.log("sNewParentName: " + sNewParentName, className);
					LIB.log("sNewParentName: " + sNewParentName, className);
				}
			}
			{
				int iLen = Str.len(sSourceIndex);
				int iStartLastTwo = iLen - 2;
				int iCharsToGet = 2;
				String sLastTwo = Str.substr(sSourceIndex, iStartLastTwo, iCharsToGet);
				int iStartChar = 0;
				iCharsToGet = iStartLastTwo;
				String sFirstChars = Str.substr(sSourceIndex, iStartChar, iCharsToGet);

				if (Str.iEqual(sLastTwo, "GD") == CONST_VALUES.VALUE_OF_TRUE) {
					bIsGasDaily = true;
					sOriginalParentName = sFirstChars + "IF";
					LIB.log("sOriginalParentName: " + sOriginalParentName, className);
					LIB.log("sOriginalParentName: " + sOriginalParentName, className);
					LIB.log("sOriginalParentName: " + sOriginalParentName, className);
				}
				if (Str.iEqual(sLastTwo, "PP") == CONST_VALUES.VALUE_OF_TRUE) {
					bIsGasDaily = true;
					sOriginalParentName = sFirstChars + "GD";
					LIB.log("sOriginalParentName: " + sOriginalParentName, className);
					LIB.log("sOriginalParentName: " + sOriginalParentName, className);
					LIB.log("sOriginalParentName: " + sOriginalParentName, className);
				}
			}
			
			// Table to hold filename
			Table tblFile = Table.tableNew();
			{
				LIB.safeAddCol(tblFile, "file name", COL_TYPE_ENUM.COL_STRING);
				int iMaxRow = tblFile.addRow();
				LIB.safeSetString(tblFile, "file name", iMaxRow, sTempFileFullPath1);
			}
			
			// Table to hold indexes
			Table tblIndex = Table.tableNew();
			{
				LIB.safeAddCol(tblIndex, "index id", COL_TYPE_ENUM.COL_INT);
				LIB.safeAddCol(tblIndex, "index name", COL_TYPE_ENUM.COL_STRING);
				int iMaxRow = tblIndex.addRow();
				int iIndexId = Ref.getValue(SHM_USR_TABLES_ENUM.INDEX_TABLE, sSourceIndex);
				LIB.safeSetInt(tblIndex, "index id", iMaxRow, iIndexId);
				LIB.safeSetString(tblIndex, "index name", iMaxRow, sSourceIndex);
			}
			
			Index.exportDefs(tblFile, tblIndex);
			
			BufferedReader reader;
			
			Table tOutput = Table.tableNew();
			LIB.safeAddCol(tOutput, "line", COL_TYPE_ENUM.COL_STRING);
 			
			LIB.log("got here 7777888", className);
 						
			try {
				
				try {
					int iRow = 0;
					reader = new BufferedReader(new FileReader(sTempFileFullPath1));
					String line = reader.readLine();
					
					// special logic for the first line
					int iMaxRow = tOutput.addRow();
					LIB.safeSetString(tOutput, "line", iMaxRow, line);
					
					try {
						while (line != null) {
							
							try {
								iRow = iRow + 1;
								line = reader.readLine();
								if (line != null) {
									line = line.replace(sSourceIndex, sDestIndex) + "\r\n";

									// TODO
									// test this later
									//if (Str.containsSubString(line, "REC,IDXDEF_PARENT") == CONST_VALUES.VALUE_OF_TRUE) {
										if (bIsGasDaily == true || bIsPhysPremium == true) {
											line = line.replace(sOriginalParentName, sNewParentName);
										}	
									//}
									
									iMaxRow = tOutput.addRow();

									LIB.safeSetString(tOutput, "line", iMaxRow, line);
								}
								
							} catch (Exception e) {
								LIB.log("doing something for row: " + iRow, e, className);								
							}
							
						}
					} catch (Exception e) {
						LIB.log("doing While loop", e, className);
					}
					
					LIB.log("got here 87564", className);
					
					
					try {
						reader.close();
					} catch (Exception e) {
						LIB.log("Closing Reader", e, className);
					}
					
				} catch (Exception e) {
					LIB.log("This part 4444", e, className);
				}
				
			} catch (Exception e) {
				LIB.log("This Part 3333", e, className);
			}
			 
			LIB.log("Got here 1000, num rows: " + LIB.safeGetNumRows(tOutput), className);
			
			try {
				FileWriter fw = new FileWriter(sTempFileFullPath2);
				int iNumRows = LIB.safeGetNumRows(tOutput);
				for (int iCounter = 1; iCounter <= iNumRows; iCounter++) {
					String sValue = LIB.safeGetString(tOutput, "line", iCounter);
					fw.write(sValue + "\n");
				}
				fw.close();
			} catch (Exception e) {
				LIB.log("This part 555", e, className);
			}

			LIB.log("Got here 12345, num rows: " + LIB.safeGetNumRows(tOutput), className);
			
			Table tblImportFile = Table.tableNew();
			LIB.safeAddCol(tblImportFile, "file name", COL_TYPE_ENUM.COL_STRING);
			int iMaxRow = tblImportFile.addRow();
			LIB.safeSetString(tblImportFile, "file name", iMaxRow, sTempFileFullPath2);;
			
			try {
				int iReplaceFlag = CONST_VALUES.VALUE_OF_FALSE;
				if (bReplaceExistingCurvesFlag == true) {
					iReplaceFlag = CONST_VALUES.VALUE_OF_TRUE;	
				}
				Index.importDefs(tblImportFile, Util.NULL_TABLE, iReplaceFlag);
			} catch (Exception e) {
				LIB.log("Index.importDefs ", e, className);
			}
			
			// be kind... delete your temp files when done
			try {
				Files.delete(tempFileFullPath1);
			} catch (Exception e) {
				// don't log this
			}
			try {
				Files.delete(tempFileFullPath2);
			} catch (Exception e) {
				// don't log this
			}

			
		} catch (Exception e) {
			LIB.log("createIndex", e, className);
		} 
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

	public static final String VERSION_NUMBER = "V1.016 (10May2023)";

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
