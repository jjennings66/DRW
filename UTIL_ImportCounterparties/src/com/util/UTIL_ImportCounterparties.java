package com.util;
  

import com.olf.openjvs.DBUserTable;
import com.olf.openjvs.DBaseTable;
import com.olf.openjvs.IContainerContext;
import com.olf.openjvs.IScript;
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
import com.olf.openjvs.enums.PARTY_FUNCTION_TYPE;
import com.olf.openjvs.enums.SCRIPT_CATEGORY_ENUM;
import com.olf.openjvs.enums.SCRIPT_TYPE_ENUM;
import com.olf.openjvs.enums.SHM_USR_TABLES_ENUM;
import com.olf.openjvs.enums.TABLE_VIEWER_MODE;

/*
Script Name: UTIL_ImportCounterparties
Description:  UTIL_ImportCounterparties

Revision History
20-Apr-2023   Brian   New Script
21-Apr-2023   Brian   Now set the Party Info field as well... with the long name of the counterparty... for business units only


*/

@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_GENERIC)
@PluginType(SCRIPT_TYPE_ENUM.MAIN_SCRIPT)
public class UTIL_ImportCounterparties implements IScript {
	 
	static boolean bExtraDebug = true;
	
	public void execute(IContainerContext context) throws OException {
		String className = this.getClass().getSimpleName();
		int iRunNumber = DEBUG_LOGFILE.getRunNumber(className); 
		
		try {
			DEBUG_LOGFILE.logToFile("START", iRunNumber, className);
			

			// TODO
			// for Safety, don't do anything
			//Util.exitFail();
			
			doEverything(iRunNumber, className);
			DEBUG_LOGFILE.logToFile("END", iRunNumber, className);
			 
		} catch (Exception e) {
			// do not log this
		}
		Util.exitSucceed();
	} 
	
	public void doEverything(int iRunNumber, String className) throws OException {
		try {
 			
			bExtraDebug = true;
			//this.createPartyGroupAndBUandLE("TEST_GROUP", "TEST_BUNIT", "TEST_LUNIT","TEST_long",  iRunNumber, className);
 			
			bExtraDebug = false;
			//this.createPartyGroupAndBUandLE("TEST_GROUP", "TEST_BUNIT", "TEST_LUNIT","TEST_long",  iRunNumber, className);
			/*this.createPartyGroupAndBUandLE("ATMOS PIPELINE", "ATMOS PIPELINE", "ATMOS PIPELINE","",  iRunNumber, className);
			this.createPartyGroupAndBUandLE("CHEYENNE PLAINS GAS PIPELINE", "CHEYENNE PLAINS GAS PIPELINE", "CHEYENNE PLAINS GAS PIPELINE","",  iRunNumber, className);
			this.createPartyGroupAndBUandLE("COLORADO INTERSTATE GAS (CIG)", "COLORADO INTERSTATE GAS (CIG)", "COLORADO INTERSTATE GAS (CIG)","",  iRunNumber, className);
			this.createPartyGroupAndBUandLE("COLUMBIA GAS (TCO)", "COLUMBIA GAS (TCO)", "COLUMBIA GAS (TCO)","",  iRunNumber, className);
			this.createPartyGroupAndBUandLE("FLORIDA GAS TRANSMISSION", "FLORIDA GAS TRANSMISSION", "FLORIDA GAS TRANSMISSION","",  iRunNumber, className);
			this.createPartyGroupAndBUandLE("GUARDIAN PIPELINE", "GUARDIAN PIPELINE", "GUARDIAN PIPELINE","",  iRunNumber, className);
			this.createPartyGroupAndBUandLE("IROQUOIS GAS TRANSMISSION", "IROQUOIS GAS TRANSMISSION", "IROQUOIS GAS TRANSMISSION","",  iRunNumber, className);
			this.createPartyGroupAndBUandLE("KERN RIVER", "KERN RIVER", "KERN RIVER","",  iRunNumber, className);
			this.createPartyGroupAndBUandLE("MICHCON", "MICHCON", "MICHCON","",  iRunNumber, className);
			this.createPartyGroupAndBUandLE("MILLENNIUM PL", "MILLENNIUM PL", "MILLENNIUM PL","",  iRunNumber, className);
			this.createPartyGroupAndBUandLE("MISSISSIPPI HUB", "MISSISSIPPI HUB", "MISSISSIPPI HUB","",  iRunNumber, className);
			this.createPartyGroupAndBUandLE("NICOR", "NICOR", "NICOR","",  iRunNumber, className);
			this.createPartyGroupAndBUandLE("NIPSCO", "NIPSCO", "NIPSCO","",  iRunNumber, className);
			this.createPartyGroupAndBUandLE("NORTHERN BORDER", "NORTHERN BORDER", "NORTHERN BORDER","",  iRunNumber, className);
			this.createPartyGroupAndBUandLE("NORTHWEST PL", "NORTHWEST PL", "NORTHWEST PL","",  iRunNumber, className);
			this.createPartyGroupAndBUandLE("OGT", "OGT", "OGT","",  iRunNumber, className);
			this.createPartyGroupAndBUandLE("ROCKIES EXPRESS", "ROCKIES EXPRESS", "ROCKIES EXPRESS","",  iRunNumber, className);
			this.createPartyGroupAndBUandLE("ROVER PIPELINE", "ROVER PIPELINE", "ROVER PIPELINE","",  iRunNumber, className);
			this.createPartyGroupAndBUandLE("RUBY PL", "RUBY PL", "RUBY PL","",  iRunNumber, className);
			this.createPartyGroupAndBUandLE("TALLGRASS INTERSTATE", "TALLGRASS INTERSTATE", "TALLGRASS INTERSTATE","",  iRunNumber, className);
			this.createPartyGroupAndBUandLE("TEXAS EASTERN TRANSMISSION (TETC", "TEXAS EASTERN TRANSMISSION (TETCO)", "TEXAS EASTERN TRANSMISSION (TETCO)","",  iRunNumber, className);
			this.createPartyGroupAndBUandLE("TRAILBLAZER", "TRAILBLAZER", "TRAILBLAZER","",  iRunNumber, className);
			this.createPartyGroupAndBUandLE("VIKING GAS TRANSMISSION", "VIKING GAS TRANSMISSION", "VIKING GAS TRANSMISSION","",  iRunNumber, className);
*/
			this.createPartyGroupAndBUandLE("APACHE CORPORATION", "APACHE CORPORATION", "APACHE CORPORATION","Apache Corporation",  iRunNumber, className);
			this.createPartyGroupAndBUandLE("COKINOS ENERGY CORPORATION", "COEC", "COKINOS ENERGY CORPORATION","Cokinos Energy Corporation",  iRunNumber, className);
			this.createPartyGroupAndBUandLE("WESTMONT ENERGY LP", "WSTMT", "WESTMONT ENERGY LP","Westmont Energy LP",  iRunNumber, className);
			this.createPartyGroupAndBUandLE("GOLDEN PASS LNG TERMINAL LLC", "GPLNG", "GOLDEN PASS LNG TERMINAL LLC","Golden Pass LNG Terminal LLC",  iRunNumber, className);
			this.createPartyGroupAndBUandLE("VALLEY CROSSING PIPELINE, LLC", "VCPL", "VALLEY CROSSING PIPELINE, LLC","Valley Crossing Pipeline, LLC",  iRunNumber, className);
			this.createPartyGroupAndBUandLE("ESENTIA GAS LLC", "ESGL", "ESENTIA GAS LLC","Esentia Gas LLC",  iRunNumber, className);
			this.createPartyGroupAndBUandLE("COMSTOCK OIL & GAS, LLC", "CSTK3", "COMSTOCK OIL & GAS, LLC","Comstock Oil & Gas, LLC",  iRunNumber, className);
			this.createPartyGroupAndBUandLE("COPEQ TRADING CO.", "COPQ", "COPEQ TRADING CO.","Copeq Trading Co.",  iRunNumber, className);
			this.createPartyGroupAndBUandLE("UNION ELECTRIC COMPANY", "UECA", "UNION ELECTRIC COMPANY DBA AMEREN MISSOURI","Union Electric Company dba Ameren Missouri",  iRunNumber, className);
			this.createPartyGroupAndBUandLE("DIVERSIFIED ENERGY MARKETING LLC", "DEML", "DIVERSIFIED ENERGY MARKETING LLC","Diversified Energy Marketing LLC",  iRunNumber, className);
			this.createPartyGroupAndBUandLE("SM ENERGY COMPANY", "SMEC", "SM ENERGY COMPANY","SM Energy Company",  iRunNumber, className);
			this.createPartyGroupAndBUandLE("BLUE RACER MIDSTREAM, LLC", "BRMS", "BLUE RACER MIDSTREAM, LLC","Blue Racer Midstream, LLC",  iRunNumber, className);

			
			/*
			//TODO
			//Ref.linkPartyToPortfolio(20001, 20001);
			//Ref.linkPartyToPortfolio(20001, 20235);
			//Ref.linkPartyToPortfolio(20001, 20237);
			//Ref.linkPartyToPortfolio(20001, 20234);
			//Ref.linkPartyToPortfolio(20001, 20236);
			//Ref.linkPartyToPortfolio(20003, 20128);
			/*Ref.linkPartyToPortfolio(20005, 20021);
			Ref.linkPartyToPortfolio(20007, 20233);
			Ref.linkPartyToPortfolio(20009, 20218);
			Ref.linkPartyToPortfolio(20011, 20134);
			Ref.linkPartyToPortfolio(20011, 20025);
			Ref.linkPartyToPortfolio(20013, 20140);
			Ref.linkPartyToPortfolio(20015, 20019);
			Ref.linkPartyToPortfolio(20017, 20163);
			Ref.linkPartyToPortfolio(20019, 20167);
			Ref.linkPartyToPortfolio(20021, 20029);
			Ref.linkPartyToPortfolio(20023, 20115);
			Ref.linkPartyToPortfolio(20025, 20030);
			Ref.linkPartyToPortfolio(20027, 20190);
			Ref.linkPartyToPortfolio(20029, 20108);
			Ref.linkPartyToPortfolio(20031, 20094);
			Ref.linkPartyToPortfolio(20033, 20032);
			Ref.linkPartyToPortfolio(20035, 20111);
			Ref.linkPartyToPortfolio(20037, 20033);
			Ref.linkPartyToPortfolio(20039, 20233);
			Ref.linkPartyToPortfolio(20041, 20034);
			Ref.linkPartyToPortfolio(20043, 20035);
			Ref.linkPartyToPortfolio(20045, 20037);
			Ref.linkPartyToPortfolio(20047, 20162);
			Ref.linkPartyToPortfolio(20049, 20039);
			Ref.linkPartyToPortfolio(20051, 20003);
			Ref.linkPartyToPortfolio(20053, 20125);
			Ref.linkPartyToPortfolio(20055, 20040);
			Ref.linkPartyToPortfolio(20057, 20112);
			Ref.linkPartyToPortfolio(20059, 20161);
			Ref.linkPartyToPortfolio(20061, 20041);
			Ref.linkPartyToPortfolio(20063, 20147);
			Ref.linkPartyToPortfolio(20065, 20129);
			Ref.linkPartyToPortfolio(20069, 20004);
			Ref.linkPartyToPortfolio(20071, 20042);
			Ref.linkPartyToPortfolio(20073, 20113);
			Ref.linkPartyToPortfolio(20075, 20005);
			Ref.linkPartyToPortfolio(20077, 20043);
			Ref.linkPartyToPortfolio(20079, 20233);
			Ref.linkPartyToPortfolio(20081, 20045);
			Ref.linkPartyToPortfolio(20083, 20233);
			Ref.linkPartyToPortfolio(20085, 20048);
			Ref.linkPartyToPortfolio(20087, 20166);
			Ref.linkPartyToPortfolio(20089, 20203);
			Ref.linkPartyToPortfolio(20091, 20107);
			Ref.linkPartyToPortfolio(20093, 20098);
			Ref.linkPartyToPortfolio(20095, 20050);
			Ref.linkPartyToPortfolio(20097, 20020);
			Ref.linkPartyToPortfolio(20099, 20232);
			Ref.linkPartyToPortfolio(20101, 20052);
			Ref.linkPartyToPortfolio(20103, 20233);
			Ref.linkPartyToPortfolio(20105, 20153);
			Ref.linkPartyToPortfolio(20105, 20150);
			Ref.linkPartyToPortfolio(20107, 20199);
			Ref.linkPartyToPortfolio(20109, 20233);
			Ref.linkPartyToPortfolio(20111, 20233);
			Ref.linkPartyToPortfolio(20113, 20095);
			Ref.linkPartyToPortfolio(20115, 20151);
			Ref.linkPartyToPortfolio(20115, 20148);
			Ref.linkPartyToPortfolio(20117, 20121);
			Ref.linkPartyToPortfolio(20119, 20008);
			Ref.linkPartyToPortfolio(20121, 20096);
			Ref.linkPartyToPortfolio(20123, 20233);
			Ref.linkPartyToPortfolio(20125, 20114);
			Ref.linkPartyToPortfolio(20127, 20055);
			Ref.linkPartyToPortfolio(20129, 20179);
			Ref.linkPartyToPortfolio(20131, 20056);
			Ref.linkPartyToPortfolio(20133, 20155);
			Ref.linkPartyToPortfolio(20135, 20009);
			Ref.linkPartyToPortfolio(20137, 20116);
			Ref.linkPartyToPortfolio(20139, 20057);
			Ref.linkPartyToPortfolio(20141, 20212);
			Ref.linkPartyToPortfolio(20143, 20110);
			Ref.linkPartyToPortfolio(20145, 20058);
			Ref.linkPartyToPortfolio(20147, 20097);
			Ref.linkPartyToPortfolio(20149, 20011);
			Ref.linkPartyToPortfolio(20151, 20164);
			Ref.linkPartyToPortfolio(20153, 20059);
			Ref.linkPartyToPortfolio(20155, 20204);
			Ref.linkPartyToPortfolio(20157, 20012);
			Ref.linkPartyToPortfolio(20159, 20188);
			Ref.linkPartyToPortfolio(20161, 20136);
			Ref.linkPartyToPortfolio(20161, 20013);
			Ref.linkPartyToPortfolio(20163, 20165);
			Ref.linkPartyToPortfolio(20165, 20195);
			Ref.linkPartyToPortfolio(20167, 20103);
			Ref.linkPartyToPortfolio(20169, 20233);
			Ref.linkPartyToPortfolio(20171, 20063);
			Ref.linkPartyToPortfolio(20173, 20064);
			Ref.linkPartyToPortfolio(20175, 20065);
			Ref.linkPartyToPortfolio(20177, 20131);
			Ref.linkPartyToPortfolio(20179, 20066);
			Ref.linkPartyToPortfolio(20181, 20126);
			Ref.linkPartyToPortfolio(20183, 20068);
			Ref.linkPartyToPortfolio(20185, 20100);
			Ref.linkPartyToPortfolio(20187, 20069);
			Ref.linkPartyToPortfolio(20189, 20070);
			Ref.linkPartyToPortfolio(20191, 20072);
			Ref.linkPartyToPortfolio(20193, 20187);
			Ref.linkPartyToPortfolio(20195, 20073);
			Ref.linkPartyToPortfolio(20197, 20183);
			Ref.linkPartyToPortfolio(20199, 20149);
			Ref.linkPartyToPortfolio(20199, 20152);
			Ref.linkPartyToPortfolio(20201, 20122);
			Ref.linkPartyToPortfolio(20203, 20141);
			Ref.linkPartyToPortfolio(20205, 20133);
			Ref.linkPartyToPortfolio(20207, 20189);
			Ref.linkPartyToPortfolio(20209, 20014);
			Ref.linkPartyToPortfolio(20211, 20076);
			Ref.linkPartyToPortfolio(20213, 20220);
			Ref.linkPartyToPortfolio(20215, 20197);
			Ref.linkPartyToPortfolio(20217, 20077);
			Ref.linkPartyToPortfolio(20219, 20078);
			Ref.linkPartyToPortfolio(20221, 20079);
			Ref.linkPartyToPortfolio(20223, 20080);
			Ref.linkPartyToPortfolio(20225, 20015);
			Ref.linkPartyToPortfolio(20227, 20231);
			Ref.linkPartyToPortfolio(20227, 20144);
			Ref.linkPartyToPortfolio(20227, 20142);
			Ref.linkPartyToPortfolio(20227, 20002);
			Ref.linkPartyToPortfolio(20227, 20158);
			Ref.linkPartyToPortfolio(20227, 20159);
			Ref.linkPartyToPortfolio(20227, 20143);
			Ref.linkPartyToPortfolio(20229, 20180);
			Ref.linkPartyToPortfolio(20231, 20105);
			Ref.linkPartyToPortfolio(20233, 20084);
			Ref.linkPartyToPortfolio(20235, 20124);
			Ref.linkPartyToPortfolio(20237, 20181);
			Ref.linkPartyToPortfolio(20239, 20154);
			Ref.linkPartyToPortfolio(20241, 20157);
			Ref.linkPartyToPortfolio(20243, 20106);
			Ref.linkPartyToPortfolio(20245, 20233);
			Ref.linkPartyToPortfolio(20247, 20085);
			Ref.linkPartyToPortfolio(20249, 20086);
			Ref.linkPartyToPortfolio(20251, 20138);
			Ref.linkPartyToPortfolio(20253, 20087);
			Ref.linkPartyToPortfolio(20255, 20233);
			Ref.linkPartyToPortfolio(20257, 20088);
			Ref.linkPartyToPortfolio(20259, 20016);
			Ref.linkPartyToPortfolio(20261, 20117);
			Ref.linkPartyToPortfolio(20263, 20102);
			Ref.linkPartyToPortfolio(20265, 20017);
			Ref.linkPartyToPortfolio(20267, 20101);
			Ref.linkPartyToPortfolio(20267, 20135);
			Ref.linkPartyToPortfolio(20269, 20193);
			Ref.linkPartyToPortfolio(20269, 20192);
			Ref.linkPartyToPortfolio(20271, 20091);
			Ref.linkPartyToPortfolio(20273, 20092);
			Ref.linkPartyToPortfolio(20275, 20215);
			Ref.linkPartyToPortfolio(20275, 20210);
			Ref.linkPartyToPortfolio(20277, 20093);
			Ref.linkPartyToPortfolio(20279, 20018);
			Ref.linkPartyToPortfolio(20281, 20202);
			
			
			Ref.linkPartyToPortfolio(20283, 20022);
			Ref.linkPartyToPortfolio(20285, 20023);
			Ref.linkPartyToPortfolio(20287, 20024);
			Ref.linkPartyToPortfolio(20289, 20026);
			Ref.linkPartyToPortfolio(20291, 20233);
			Ref.linkPartyToPortfolio(20293, 20027);
			Ref.linkPartyToPortfolio(20295, 20028);
			Ref.linkPartyToPortfolio(20297, 20031);
			Ref.linkPartyToPortfolio(20299, 20036);
			Ref.linkPartyToPortfolio(20301, 20038);
			Ref.linkPartyToPortfolio(20303, 20109);
			Ref.linkPartyToPortfolio(20305, 20005);
			Ref.linkPartyToPortfolio(20307, 20118);
			Ref.linkPartyToPortfolio(20309, 20044);
			Ref.linkPartyToPortfolio(20311, 20006);
			Ref.linkPartyToPortfolio(20313, 20046);
			Ref.linkPartyToPortfolio(20315, 20139);
			Ref.linkPartyToPortfolio(20317, 20049);
			Ref.linkPartyToPortfolio(20319, 20209);
			Ref.linkPartyToPortfolio(20321, 20207);
			Ref.linkPartyToPortfolio(20323, 20206);
			Ref.linkPartyToPortfolio(20325, 20208);
			Ref.linkPartyToPortfolio(20327, 20051);
			Ref.linkPartyToPortfolio(20329, 20053);
			Ref.linkPartyToPortfolio(20331, 20120);
			Ref.linkPartyToPortfolio(20333, 20007);
			Ref.linkPartyToPortfolio(20335, 20233);
			Ref.linkPartyToPortfolio(20337, 20054);
			Ref.linkPartyToPortfolio(20339, 20198);
			Ref.linkPartyToPortfolio(20341, 20145);
			Ref.linkPartyToPortfolio(20345, 20060);
			Ref.linkPartyToPortfolio(20347, 20061);
			Ref.linkPartyToPortfolio(20351, 20230);
			Ref.linkPartyToPortfolio(20353, 20062);
			Ref.linkPartyToPortfolio(20355, 20067);
			Ref.linkPartyToPortfolio(20357, 20099);
			Ref.linkPartyToPortfolio(20359, 20071);
			Ref.linkPartyToPortfolio(20361, 20211);
			Ref.linkPartyToPortfolio(20363, 20074);
			Ref.linkPartyToPortfolio(20365, 20075);
			Ref.linkPartyToPortfolio(20367, 20104);
			Ref.linkPartyToPortfolio(20369, 20184);
			Ref.linkPartyToPortfolio(20371, 20196);
			Ref.linkPartyToPortfolio(20373, 20127);
			Ref.linkPartyToPortfolio(20375, 20080);
			Ref.linkPartyToPortfolio(20377, 20082);
			Ref.linkPartyToPortfolio(20379, 20083);
			Ref.linkPartyToPortfolio(20381, 20201);
			Ref.linkPartyToPortfolio(20383, 20194);
			Ref.linkPartyToPortfolio(20385, 20200);
			Ref.linkPartyToPortfolio(20387, 20137);
			Ref.linkPartyToPortfolio(20389, 20089);
			Ref.linkPartyToPortfolio(20391, 20090);
			Ref.linkPartyToPortfolio(20393, 20146);
			Ref.linkPartyToPortfolio(20395, 20233);
			Ref.linkPartyToPortfolio(20397, 20233);
			Ref.linkPartyToPortfolio(20399, 20233);
			Ref.linkPartyToPortfolio(20401, 20233);
			Ref.linkPartyToPortfolio(20403, 20233);
			Ref.linkPartyToPortfolio(20405, 20233);
			Ref.linkPartyToPortfolio(20407, 20233);
			Ref.linkPartyToPortfolio(20409, 20233);
			Ref.linkPartyToPortfolio(20411, 20233);
			Ref.linkPartyToPortfolio(20413, 20233);
			Ref.linkPartyToPortfolio(20415, 20233);
			Ref.linkPartyToPortfolio(20417, 20233);
			Ref.linkPartyToPortfolio(20419, 20233);
			Ref.linkPartyToPortfolio(20421, 20233);
			Ref.linkPartyToPortfolio(20423, 20233);
			*/
			

			
		} catch (Exception e) {
			// do not log this
		}
	}
		
	private void createPartyGroupAndBUandLE(String sPartyGroupName, String sBunitRoot,String sLunitRoot, String sLongName, int iRunNumber,
			String className) throws OException {
		FUNC.createPartyGroupAndBUandLE(sPartyGroupName, sBunitRoot,sLunitRoot, sLongName, iRunNumber, className);
	}

	public static class FUNC {
 		
		public static void createPartyGroupAndBUandLE(String sPartyGroupName, String sBunitRoot, String sLunitRoot, String sLongName, int iRunNumber, String className) throws OException {
			try {
	 
			//	String sPartyGroupName = "BrianTest";
				
			//	 String sPartyGroupName = "Brian." + iRunNumber;
				// int_ext - 0 = Internal; 1 = External 
				int int_ext = 1;
				try {
					// calling this if the name already exits will through an exception
					Ref.addNewPartyGroup(sPartyGroupName, int_ext, sLongName);
				} catch (Exception e) {
					LIB.log("Creating a Party Group (name may already exist)", e, className);
				}
							
				
				
				int iPartyGroupID = -1;
				{
					Table tTemp = Table.tableNew();
					LIB.safeAddCol(tTemp, "value", COL_TYPE_ENUM.COL_INT);
					String sSQL = "select group_id from party_group WHERE upper(short_name) = upper('" + sPartyGroupName + "')";
					LIB.execISql(tTemp, sSQL, false, className);
					iPartyGroupID = LIB.safeGetInt(tTemp, "value", CONST_VALUES.ROW_TO_GET_OR_SET);
					tTemp = LIB.destroyTable(tTemp);
				}
				LIB.log("Created (or found existing) Party Group with Party Group ID: " + iPartyGroupID, className);

				if (iPartyGroupID >= 1) {

					//  0=Legal Entity, 1=Business Unit. 
					final int LEGAL_ENTITY = 0;
					final int BUSINESS_UNIT = 1;
					
					// will need this later to link up to the Legal Entity
					int iBusinessUnitPartyId = 0;

					{
						int party_class = BUSINESS_UNIT;
						
						Table tPartyInsert = Ref.createParty(party_class);
						 
						LIB.safeSetString(tPartyInsert, "short_name", 1, sBunitRoot + " - BU");
						LIB.safeSetString(tPartyInsert, "long_name", 1, sLongName);
						LIB.safeSetInt(tPartyInsert, "int_ext", 1, int_ext);
						LIB.safeSetInt(tPartyInsert, "group_id", 1, iPartyGroupID);
						final int AUTH_PENDING = 0;
						final int AUTHORIZED = 1;
						LIB.safeSetInt(tPartyInsert, "party_status", 1, AUTHORIZED);
 
						// Add Party Function for Trading
						{
							// select * from function_type to get all functions
							
//							name	id_number
//							Office	0
//							Trading	1
//							Settling (Back Office)	2
//							Holding (Bank/Warehouse)	3
//							OTC Broking	4
//							Internal Trading	5
//							Execution Broking	6
//							Exchange Pit	7
//							Clearing Broking	8
//							Carrier	9
//							Underwriter	10
//							Transmission Provider	11
//							OTC Clearing Broking	12
//							Tax Jurisdiction	13
//							Guarantor	14
//							Issuer	15
//							Internal Scheduling	16
//							Agency Activities	17
//							Reference Entity	18
//							Pass Through	19
//							Clearinghouse	20
//							Internal Scheduling - Offset Delivery	21
//							Operator	22

							Table tPartyFunction = LIB.safeGetTable(tPartyInsert, "party_function", CONST_VALUES.ROW_TO_GET_OR_SET);
							int iMaxRow = tPartyFunction.addRow();
							// set the first column
							int iColNumToSet = 1;
							// use a Enum here... or could have used a value of '1' (see above table)
							tPartyFunction.setInt(iColNumToSet, iMaxRow, PARTY_FUNCTION_TYPE.TRADING_PARTY.toInt());
						}
						
						
						final int PARTY_INFO_EXTERNAL_SYSTEM_NAME = 20001;
						{
	
							try {
								Table tPartyInfo = LIB.safeGetTable(tPartyInsert, "party_info", CONST_VALUES.ROW_TO_GET_OR_SET);

								// delete if there already
								tPartyInfo.deleteWhereValue(1, PARTY_INFO_EXTERNAL_SYSTEM_NAME);
								
								int iMaxRow = tPartyInfo.addRow();
								tPartyInfo.setInt(1, iMaxRow, PARTY_INFO_EXTERNAL_SYSTEM_NAME);
								tPartyInfo.setString(2, iMaxRow, sLongName);
							} catch (Exception e) {
								LIB.log("adding/setting Party Info", e, className);
							}
							
							
							
						}
						

						// Business Unit
						try {
							// calling this if the name already exits will through an exception
							Ref.addNewParty(tPartyInsert);
							// if this did not through an exception... it will populate the party ID with the party we just created
							iBusinessUnitPartyId = LIB.safeGetInt(tPartyInsert, "party_id", CONST_VALUES.ROW_TO_GET_OR_SET);
						} catch (Exception e) {
							LIB.log("Creating a new Party Business Unit (name may already exist)", e, className);
						}

						if (bExtraDebug == true) {
							// view the table after the insert
							LIB.viewTable(tPartyInsert, "Party Insert Table for Business Unit");
						}

						if (iBusinessUnitPartyId >= 1) {
							LIB.log("Created Party (Business Unit) with Party ID: " + iBusinessUnitPartyId, className);
						}
					}

					{
						int party_class = LEGAL_ENTITY;
						
						Table tPartyInsert = Ref.createParty(party_class);
						
						LIB.safeSetString(tPartyInsert, "short_name", 1, sLunitRoot + " - LE");
						LIB.safeSetString(tPartyInsert, "long_name", 1, sLongName);
						LIB.safeSetInt(tPartyInsert, "int_ext", 1, int_ext);
						LIB.safeSetInt(tPartyInsert, "group_id", 1, iPartyGroupID);
						final int AUTH_PENDING = 0;
						final int AUTHORIZED = 1;
						LIB.safeSetInt(tPartyInsert, "party_status", 1, AUTHORIZED);

						// Don't do this... testing shows that this did not, in fact, work
						boolean bDoThis = false;
						if (bDoThis == true) {
							// link to the business unit
							// it is a mutual link, so goes two ways
							// so no need to go back to the business unit
							LIB.safeSetInt(tPartyInsert, "linked_party_id", 1, iBusinessUnitPartyId);
						}
						  
						// Legal Entity
						int iPartyId = 0;
						try {
							// calling this if the name already exits will through an exception
							Ref.addNewParty(tPartyInsert);
							// if this did not through an exception... it will populate the party ID with the party we just created
							iPartyId = LIB.safeGetInt(tPartyInsert, "party_id", CONST_VALUES.ROW_TO_GET_OR_SET);
						} catch (Exception e) {
							LIB.log("Creating a new Party Legal Entity (name may already exist)", e, className);
						}
	 					
						if (bExtraDebug == true) {
							// view the table after the insert
							LIB.viewTable(tPartyInsert, "Party Insert Table for Legal Entity");
						}

						if (iPartyId >= 1) {
							LIB.log("Created Party (Legal Entity) with Party ID: " + iPartyId, className);
						}
						

						if (iPartyId >= 1) {
							Table party = Ref.retrieveParty(iPartyId);    
							
							LIB.safeAddCol(party, "party_relationship", COL_TYPE_ENUM.COL_TABLE);

							// TODO
							// This works to set the short name to 'whatever'
							//LIB.safeSetString(party, "short_name", CONST_VALUES.ROW_TO_GET_OR_SET, "Brian" + DBUserTable.getUniqueId());
							
							Table tPartyRelationship = Table.tableNew("party_relationship");
							LIB.safeAddCol(tPartyRelationship, "action", COL_TYPE_ENUM.COL_STRING);
							LIB.safeAddCol(tPartyRelationship, "Default", COL_TYPE_ENUM.COL_INT);
							LIB.safeAddCol(tPartyRelationship, "party_id", COL_TYPE_ENUM.COL_INT);
							
							int iMaxRow = tPartyRelationship.addRow();
							LIB.safeSetString(tPartyRelationship, "action", iMaxRow, "ADD");
							LIB.safeSetInt(tPartyRelationship, "Default", iMaxRow, iBusinessUnitPartyId);
							LIB.safeSetInt(tPartyRelationship, "party_id", iMaxRow, iBusinessUnitPartyId);
							
							// put back into the party table
							
							LIB.safeSetTable(party, "party_relationship", CONST_VALUES.ROW_TO_GET_OR_SET, tPartyRelationship);
							
	//
	//The following columns can be added to the table for processing. The columns depend on the party class. For legal entities it is the following set of possibilities
	//party_relationship - COL_TYPE_ENUM.COL_TABLE - linking one or
	//more business units to the legal entity. The sub-table
	//has the following format
	//
	//action - COL_TYPE_ENUM.COL_STRING - ADD, MODIFY or DELETE
	//Default - COL_TYPE_ENUM.COL_INT - 1 = Default business unit for the legal entity
	//party_id - COL_TYPE_ENUM.COL_INT - The unique id of the business unit to link
							
							
							try {
								int retval = Ref.updateParty(party);
								LIB.log("Ref.updateParty retval: " + retval, className);
							} catch (Exception e) {
								LIB.log("Ref.updateParty", e, className);
							}
							
							if (bExtraDebug == true) {
								LIB.viewTable(party, "LE Retrived party Table");
							}
						}

						
						

			            
	//
//			            bunit = party.getTable("business_unit", 1);
//			            bunit.setString("description", 1, "Testing OpenJvs func RefBase.updateParty()");
	//
//			            party_info = party.getTable("party_info", 1);
//			            row = party_info.addRow();
//			            party_info.setInt(1, row, 20041);
//			            party_info.setString(2, row, "trader1");
	//
//			            row = party_info.unsortedFindInt(1, 20038);
//			            if (row > 0){
//			                    party_info.setClob(3, row, "I am testing Clob party info from OpenJvs");
//			            }
	//
//			            retval = Ref.updateParty(party);
			 
					}


					
					// Legal Entity
//					Table tPartyInsertLE = Ref.createParty(party_class);
//					Ref.addNewParty(tPartyInsertLE);

				} // END 			if (iPartyGroupID >= 1) 
				
				
			} catch (Exception e) {
				LIB.log("createPartyGroupAndBUandLE", e, className);
			}	 
		}
			

		} // public static class FUNC 

 		  
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

	public static final String VERSION_NUMBER = "V1.005 (21Apr2023)";

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
