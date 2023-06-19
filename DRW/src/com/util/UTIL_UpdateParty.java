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
import com.olf.openjvs.enums.SCRIPT_CATEGORY_ENUM;
import com.olf.openjvs.enums.SCRIPT_TYPE_ENUM;
import com.olf.openjvs.enums.SHM_USR_TABLES_ENUM;
import com.olf.openjvs.enums.TABLE_VIEWER_MODE; 

/*
Script Name: UTIL_UpdatePartyInfo
Description:  UTIL_UpdatePartyInfo

Revision History
30-May-2023   Joe   New Script
05-Jun-2023	  Joe	Updated for long_name update
08-Jun-2023	  Joe	Renaming has begun	

*/

@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_GENERIC)
@PluginType(SCRIPT_TYPE_ENUM.MAIN_SCRIPT)
public class UTIL_UpdateParty implements IScript {
	 
	static boolean bExtraDebug = true;
	
	public void execute(IContainerContext context) throws OException {
		String className = this.getClass().getSimpleName();
		int iRunNumber = DEBUG_LOGFILE.getRunNumber(className); 
		
		try {
			DEBUG_LOGFILE.logToFile("START", iRunNumber, className);
			 
			
			doEverything(iRunNumber, className);
			DEBUG_LOGFILE.logToFile("END", iRunNumber, className);
			 
		} catch (Exception e) {
			// do not log this
		}
		Util.exitSucceed();
	} 
	
	public void doEverything(int iRunNumber, String className) throws OException {
		try {
 			
			// Extra Debug to true pops up a table viewer..
			bExtraDebug = true;
			//this.updateLEName("DRW ENERGY TRADING LLC - LE", "DRW ENERGY TRADING LLC - LE", "DRW Energy Trading LLC", iRunNumber, className);

			// false is no table viewer
			bExtraDebug = false;
			/*
			this.updateBUName("AEP PSO - BU", "AEP PSO - BU", "Public Service Company of Oklahoma DBA AEP PSO", iRunNumber, className);
			this.updateBUName("AGUA BLANCA PIPELINE - BU", "AGUA BLANCA, LLC - BU", "Agua Blanca, LLC", iRunNumber, className);
			this.updateBUName("ALLIANCE PIPELINE - BU", "ALLIANCE PIPELINE L.P. - BU", "Alliance Pipeline L.P.", iRunNumber, className);
			this.updateBUName("INTERSTATE POWER AND LIGHT COMPANY - BU", "ALLIANT ENERGY - BU", "Interstate Power and Light Company DBA Alliant Energy", iRunNumber, className);
			this.updateBUName("ANADARKO ENERGY SERVICES COMPANY - BU", "ANADARKO ENERGY SERVICES COMPANY - BU", "Anadarko Energy Services Company", iRunNumber, className);
			this.updateBUName("ANCOVA ENERGY MARKETING, LLC - BU", "ANCOVA ENERGY MARKETING, LLC - BU", "Ancova Energy Marketing, LLC", iRunNumber, className);
			this.updateBUName("ANEW RNG LLC - BU", "ANEW RNG, LLC - BU", "Anew RNG, LLC", iRunNumber, className);
			this.updateBUName("ANIG - BU", "ANIG - BU", "ANGEL INVESTING GROUP, LLLP", iRunNumber, className);
			this.updateBUName("ANR PIPELINE COMPANY - BU", "ANR PIPELINE COMPANY - BU", "ANR Pipeline Company", iRunNumber, className);
			this.updateBUName("APACHE CORPORATION - BU", "APACHE CORPORATION - BU", "Apache Corporation", iRunNumber, className);
			this.updateBUName("ARKANSAS ELECTRIC COOPERATIVE CORPORATION - BU", "ARKANSAS ELECTRIC COOPERATIVE CORPORATION - BU", "Arkansas Electric Cooperative Corporation", iRunNumber, className);
			this.updateBUName("ARM ENERGY MANAGEMENT LLC - BU", "ARM ENERGY MANAGEMENT LLC - BU", "ARM Energy Management LLC", iRunNumber, className);
			this.updateBUName("J ARON & COMPANY LLC - BU", "ARON - BU", "J. Aron & Company LLC", iRunNumber, className);
			this.updateBUName("ASCENT RESOURCES-UTICA LLC - BU", "ARUL3 - BU", "Ascent Resources - Utica, LLC", iRunNumber, className);
			this.updateBUName("ASSOCIATED ELECTRIC COOPERATIVE INC - BU", "ASSOCIATED ELECTRIC COOPERATIVE INC - BU", "Associated Electric Cooperative Inc", iRunNumber, className);
			this.updateBUName("BBT MID LOUISIANA GAS TRANSMISSION, LLC - BU", "BBT MID LOUISIANA GAS TRANSMISSION, LLC - BU", "BBT Mid Louisiana Gas Transmission, LLC", iRunNumber, className);
			this.updateBUName("BE ANADARKO II LLC - BU", "BEAN3 - BU", "BE Anadarko II, LLC", iRunNumber, className);
			this.updateBUName("BLACKBEARD OPERATING, LLC - BU", "BLACKBEARD OPERATING, LLC - BU", "Blackbeard Operating, LLC", iRunNumber, className);
			this.updateBUName("BLUEGRASS ENERGY - BU", "BLEN - BU", "BLUEGRASS ENERGY", iRunNumber, className);
			this.updateBUName("BLUE MOUNTAIN MIDSTREAM LLC - BU", "BLUE MOUNTAIN MIDSTREAM LLC - BU", "Blue Mountain Midstream LLC", iRunNumber, className);
			this.updateBUName("BP CANADA ENERGY MARKETING CORP - BU", "BP CANADA ENERGY MARKETING CORP - BU", "BP Canada Energy Marketing Corp", iRunNumber, className);
			this.updateBUName("BP ENERGY COMPANY - BU", "BP ENERGY COMPANY - BU", "BP Energy Company", iRunNumber, className);
			this.updateBUName("BRMS - BU", "BRMS - BU", "Blue Racer Midstream, LLC", iRunNumber, className);
			this.updateBUName("CALEDONIA ENERGY PARTNERS LLC - BU", "CALEDONIA ENERGY PARTNERS, L.L.C. - BU", "Caledonia Energy Partners, L.L.C.", iRunNumber, className);
			this.updateBUName("CARBONBETTER LLC - BU", "CARBONBETTER, LLC - BU", "Carbonbetter, LLC", iRunNumber, className);
			this.updateBUName("CASTLETON - BU", "CASTLETON COMMODITIES MERCHANT TRADING L.P. - BU", "Castleton Global Trading LLC DBA Castleton Commodities Merchant Trading L.P.", iRunNumber, className);
			this.updateBUName("CENT - BU", "CENT - BU", "CENTURYLINK", iRunNumber, className);
			this.updateBUName("CFE INTERNATIONAL LLC - BU", "CFE INTERNATIONAL LLC - BU", "CFE International LLC", iRunNumber, className);
			this.updateBUName("COLUMBIA GULF TRANSMISSION LLC - BU", "CGTL - BU", "Columbia Gulf Transmission, LLC", iRunNumber, className);
			this.updateBUName("COLUMBIA GAS (TCO) - BU", "CGTM - BU", "Columbia Gas Transmission, LLC", iRunNumber, className);
			this.updateBUName("CHESAPEAKE ENERGY MARKETING LLC - BU", "CHESAPEAKE ENERGY MARKETING, L.L.C. - BU", "Chesapeake Energy Corporation DBA Chesapeake Energy Marketing L.L.C.", iRunNumber, className);
			this.updateBUName("CHEVRON NATURAL GAS - BU", "CHEVRON NATURAL GAS, A DIVISION OF CHEVRON U.S.A. INC. - BU", "Chevron U.S.A. Inc. DBA Chevron Natural Gas, a division of Chevron U.S.A. Inc.", iRunNumber, className);
			this.updateBUName("CIMA ENERGY LP - BU", "CIEN - BU", "CIMA ENERGY, LTD", iRunNumber, className);
			this.updateBUName("CITADEL ENERGY MARKETING LLC - BU", "CITADEL ENERGY MARKETING LLC - BU", "Citadel Energy Marketing LLC", iRunNumber, className);
			this.updateBUName("CITY UTILITIES OF SPRINGFIELD, MISSOURI - BU", "CITY UTILITIES OF SPRINGFIELD, MISSOURI - BU", "City Utilities of Springfield, Missouri", iRunNumber, className);
			this.updateBUName("CLEARWATER ENTERPRISES LLC - BU", "CLEARWATER ENTERPRISES, L.L.C. - BU", "Clearwater Enterprises, L.L.C.", iRunNumber, className);
			this.updateBUName("COEC - BU", "COEC - BU", "Cokinos Energy Corporation", iRunNumber, className);
			this.updateBUName("COGE - BU", "COGE - BU", "COGENCY GLOBAL", iRunNumber, className);
			this.updateBUName("COLORADO SPRINGS UTILITIES - BU", "COLORADO SPRINGS UTILITIES - BU", "Colorado Springs Utilities", iRunNumber, className);
			this.updateBUName("CONCORD ENERGY LLC - BU", "CONCORD ENERGY LLC - BU", "Concord Energy LLC", iRunNumber, className);
			this.updateBUName("CONOCOPHILLIPS COMPANY - BU", "CONOCOPHILLIPS COMPANY - BU", "ConocoPhillips Company", iRunNumber, className);
			this.updateBUName("CONSTELLATION ENERGY GENERATION LLC - BU", "CONSTELLATION ENERGY GENERATION, LLC - BU", "Constellation Energy Generation, LLC", iRunNumber, className);
			this.updateBUName("CONSUMERS ENERGY COMPANY - BU", "CONSUMERS ENERGY COMPANY - BU", "Consumers Energy Company", iRunNumber, className);
			this.updateBUName("CONTINENTAL RESOURCES INC - BU", "CONTINENTAL RESOURCES, INC. - BU", "Continental Resources, Inc.", iRunNumber, className);
			this.updateBUName("COPQ - BU", "COPQ - BU", "Copeq Trading Co.", iRunNumber, className);
			this.updateBUName("CORPUS CHRISTI LIQUEFACTION LLC - BU", "CORPUS CHRISTI LIQUEFACTION, LLC - BU", "Corpus Christi Liquefaction, LLC", iRunNumber, className);
			this.updateBUName("CSTK3 - BU", "CSTK3 - BU", "Comstock Oil & Gas, LLC", iRunNumber, className);
			this.updateBUName("DCP MIDSTREAM MARKETING LLC - BU", "DCP MIDSTREAM MARKETING, LLC - BU", "DCP Midstream, LP DBA DCP Midstream Marketing, LLC", iRunNumber, className);
			this.updateBUName("DIRECT ENERGY BUSINESS MARKETING LLC - BU", "DEBM - BU", "Direct Energy Business Marketing, LLC", iRunNumber, className);
			this.updateBUName("DEML - BU", "DEML - BU", "Diversified Energy Marketing LLC", iRunNumber, className);
			this.updateBUName("DEVON GAS SERVICES, L.P. - BU", "DEVON GAS SERVICES, L.P. - BU", "Devon Gas Services, L.P.", iRunNumber, className);
			this.updateBUName("DK TRADING & SUPPLY LLC - BU", "DK TRADING & SUPPLY, LLC - BU", "Delek US Energy, Inc DBA DK Trading & Supply, LLC", iRunNumber, className);
			this.updateBUName("DTE ENERGY TRADING INC - BU", "DTE ENERGY TRADING, INC. - BU", "DTE Energy Trading, Inc.", iRunNumber, className);
			this.updateBUName("DTE GAS COMPANY - BU", "DTE GAS COMPANY - BU", "DTE Gas Company", iRunNumber, className);
			this.updateBUName("DXT COMMODITIES NORTH AMERICA INC - BU", "DXT COMMODITIES NORTH AMERICA INC. - BU", "DXT Commodities North America Inc.", iRunNumber, className);
			this.updateBUName("EAST3 - BU", "EAST3 - BU", "TGNR East Texas LLC", iRunNumber, className);
			this.updateBUName("ECO-ENERGY NATURAL GAS, LLC - BU", "ECO-ENERGY NATURAL GAS, LLC - BU", "Copersucar North America, LLC DBA Eco-Energy Natural Gas, LLC", iRunNumber, className);
			this.updateBUName("EDF TRADING NORTH AMERICA LLC - BU", "EDF TRADING NORTH AMERICA, LLC - BU", "EDF Trading North America, LLC", iRunNumber, className);
			this.updateBUName("EEGL - BU", "EEGL - BU", "Elevation Energy Group, LLC", iRunNumber, className);
			this.updateBUName("ELEMENT MARKETS RENEWABLE ENERGY, LLC - BU", "ELEMENT MARKETS RENEWABLE ENERGY, LLC - BU", "Element Markets, LLC DBA Element Markets Renewable Energy, LLC", iRunNumber, className);
			this.updateBUName("ENTERGY LOUISIANA LLC - BU", "ELOU3 - BU", "Entergy Louisiana, LLC", iRunNumber, className);
			this.updateBUName("EMERA ENERGY SERVICES INC - BU", "EMERA ENERGY SERVICES, INC. - BU", "Emera Energy Services, Inc.", iRunNumber, className);
			this.updateBUName("ENABLE ENERGY RESOURCES, LLC - BU", "ENABLE ENERGY RESOURCES, LLC - BU", "Enable Midstream Partners, LP DBA Enable Energy Resources, LLC", iRunNumber, className);
			this.updateBUName("ENABLE GAS TRANSMISSION LLC - BU", "ENABLE GAS TRANSMISSION, LLC - BU", "Enable Midstream Partners, LP DBA Enable Gas Transmission, LLC", iRunNumber, className);
			this.updateBUName("ENABLE OKLAHOMA INTRASTATE TRANSMISSION LLC - BU", "ENABLE OKLAHOMA INTRASTATE TRANSMISSION, LLC - BU", "Enable Midstream Partners, LP DBA Enable Oklahoma Intrastate Transmission, LLC", iRunNumber, className);
			this.updateBUName("ENESTAS, S.A. DE C.V. - BU", "ENESTAS, S.A. DE C.V. - BU", "Enestas, S.A. de C.V.", iRunNumber, className);
			this.updateBUName("ENGIE ENERGY MARKETING NA INC - BU", "ENGE - BU", "ENGIE Energy Marketing NA, Inc.", iRunNumber, className);
			this.updateBUName("ENLINK GAS MARKETING, LP - BU", "ENLINK GAS MARKETING, LP - BU", "EnLink Midstream Operating, LP DBA EnLink Gas Marketing, LP", iRunNumber, className);
			this.updateBUName("ENSI - BU", "ENSI - BU", "ENSIGN SERVICES, INC.", iRunNumber, className);
			this.updateBUName("ENSIGHT - BU", "ENSIGHT - BU", "EnSight IV Energy Partners, LLC", iRunNumber, className);
			this.updateBUName("ENTAR - BU", "ENTAR - BU", "Entergy Arkansas, LLC", iRunNumber, className);
			this.updateBUName("ENTERPRISE PRODUCTS OPERATING LLC - BU", "ENTERPRISE PRODUCTS OPERATING LLC - BU", "Enterprise Products Operating LLC", iRunNumber, className);
			this.updateBUName("ENTERPRISE TEXAS PIPELINE LLC - BU", "ENTERPRISE TEXAS PIPELINE LLC - BU", "Enterprise Texas Pipeline LLC", iRunNumber, className);
			this.updateBUName("ENTMS - BU", "ENTMS - BU", "Entergy Mississippi, LLC", iRunNumber, className);
			this.updateBUName("ENTNO - BU", "ENTNO - BU", "Entergy New Orleans, LLC", iRunNumber, className);
			this.updateBUName("ENTTX - BU", "ENTTX - BU", "Entergy Texas, Inc.", iRunNumber, className);
			this.updateBUName("EOG RESOURCES INC - BU", "EOG RESOURCES, INC. - BU", "EOG Resources, Inc.", iRunNumber, className);
			this.updateBUName("EOXH - BU", "EOXH - BU", "EOX HOLDINGS, LLC", iRunNumber, className);
			this.updateBUName("EQT ENERGY LLC - BU", "EQT ENERGY, LLC - BU", "EQT Production Company DBA EQT Energy, LLC", iRunNumber, className);
			this.updateBUName("EQUINOR NATURAL GAS LLC - BU", "EQUINOR NATURAL GAS LLC - BU", "Equinor Natural Gas LLC", iRunNumber, className);
			this.updateBUName("ESGL - BU", "ESGL - BU", "Esentia Gas LLC", iRunNumber, className);
			this.updateBUName("ETC MARKETING LTD - BU", "ETC MARKETING, LTD. - BU", "Energy Transfer LP DBA ETC Marketing, LTD.", iRunNumber, className);
			this.updateBUName("EXGC - BU", "EXGC - BU", "Exelon Generation Company, LLC", iRunNumber, className);
			this.updateBUName("FLORIDA GAS TRANSMISSION - BU", "FGTC - BU", "Florida Gas Transmission Company, LLC", iRunNumber, className);
			this.updateBUName("FREEBIRD GAS STORAGE LLC - BU", "FREE - BU", "Freebird Gas Storage LLC", iRunNumber, className);
			this.updateBUName("FREEPOINT COMMODITIES LLC - BU", "FREEPOINT COMMODITIES LLC - BU", "Freepoint Commodities LLC", iRunNumber, className);
			this.updateBUName("GCC SUPPLY & TRADING LLC - BU", "GCC SUPPLY & TRADING LLC - BU", "GCC Supply & Trading LLC", iRunNumber, className);
			this.updateBUName("GOLDEN TRIANGLE STORAGE LLC - BU", "GLDNT - BU", "Golden Triangle Storage, Inc", iRunNumber, className);
			this.updateBUName("GLFRN - BU", "GLFRN - BU", "Gulf Run Transmission, LLC", iRunNumber, className);
			this.updateBUName("WILD GOOSE STORAGE LLC - BU", "GOOS3 - BU", "Wild Goose Storage, LLC", iRunNumber, className);
			this.updateBUName("GPLNG - BU", "GPLNG - BU", "Golden Pass LNG Terminal LLC", iRunNumber, className);
			this.updateBUName("GREAT LAKES GAS TRANSMISSION - BU", "GREAT LAKES GAS TRANSMISSION LIMITED PARTNERSHIP - BU", "Great Lakes Gas Transmission Limited Partnership", iRunNumber, className);
			this.updateBUName("GULF SOUTH PIPELINE COMPANY - BU", "GSPC - BU", "Gulf South Pipeline Company, LLC", iRunNumber, className);
			this.updateBUName("GUNVOR USA LLC - BU", "GUNV - BU", "Gunvor USA LLC", iRunNumber, className);
			this.updateBUName("HARTREE PARTNERS LP - BU", "HARTREE PARTNERS, LP - BU", "Hartree Partners, LP", iRunNumber, className);
			this.updateBUName("ICBC STANDARD BANK PLC - BU", "ICBC STANDARD BANK PLC - BU", "ICBC Standard Bank PLC", iRunNumber, className);
			this.updateBUName("INTERCONN RESOURCES, LLC - BU", "INTERCONN RESOURCES, LLC - BU", "Interconn Resources, LLC", iRunNumber, className);
			this.updateBUName("INTERSTATE GAS SUPPLY LLC - BU", "INTERSTATE GAS SUPPLY, INC. - BU", "Interstate Gas Supply, Inc.", iRunNumber, className);
			this.updateBUName("K2 COMMODITIES LLC - BU", "K2 COMMODITIES, LLC - BU", "K2 Commodities, LLC", iRunNumber, className);
			this.updateBUName("KAISER MARKETING APPALACHIAN LLC - BU", "KAIM - BU", "Kaiser Marketing Appalachian, LLC", iRunNumber, className);
			this.updateBUName("KCP&L GREATER MISSOURI OPERATIONS COMPANY - BU", "KCP&L GREATER MISSOURI OPERATIONS COMPANY - BU", "Evergy Missouri West Inc DBA KCP&L Greater Missouri Operations Company", iRunNumber, className);
			this.updateBUName("KOCH ENERGY SERVICES LLC - BU", "KOCH ENERGY SERVICES, LLC - BU", "Kaes Domestic Holdings, LLC DBA Koch Energy Services, LLC", iRunNumber, className);
			this.updateBUName("LEGACY RESERVES ENERGY SERVICES LLC - BU", "LEGACY RESERVES ENERGY SERVICES LLC - BU", "Legacy Reserves Energy Services LLC", iRunNumber, className);
			this.updateBUName("LODI - BU", "LODI - BU", "Lodi Gas Storage, L.L.C.", iRunNumber, className);
			this.updateBUName("LUMINANT ENERGY COMPANY LLC - BU", "LUMINANT ENERGY COMPANY LLC - BU", "Luminant Energy Company LLC", iRunNumber, className);
			this.updateBUName("MACQUARIE ENERGY LLC - BU", "MACQUARIE ENERGY LLC - BU", "Macquarie Energy North America Trading Inc. DBA Macquarie Energy LLC", iRunNumber, className);
			this.updateBUName("MADISON GAS & ELECTRIC - BU", "MADISON GAS & ELECTRIC - BU", "Madison Gas & Electric", iRunNumber, className);
			this.updateBUName("MANSFIELD POWER & GAS LLC - BU", "MANSFIELD POWER AND GAS, LLC - BU", "Mansfield Energy Corp DBA Mansfield Power and Gas, LLC", iRunNumber, className);
			this.updateBUName("MARATHON PETROLEUM COMPANY LP - BU", "MARATHON PETROLEUM COMPANY LP - BU", "Marathon Petroleum Company LP", iRunNumber, className);
			this.updateBUName("MARKWEST PIONEER LLC - BU", "MARKWEST PIONEER, LLC - BU", "MarkWest Pioneer, LLC", iRunNumber, className);
			this.updateBUName("MERCURIA ENERGY AMERICA LLC - BU", "MERCURIA ENERGY AMERICA, LLC - BU", "Mercuria Energy Company LLC DBA Mercuria Energy America, LLC", iRunNumber, className);
			this.updateBUName("MEX GAS SUPPLY SL - BU", "MEX GAS SUPPLY, S.L. - BU", "Mex Gas Supply, S.L.", iRunNumber, className);
			this.updateBUName("MIDAMERICAN ENERGY COMPANY - BU", "MIDAMERICAN ENERGY COMPANY - BU", "MidAmerican Energy Company", iRunNumber, className);
			this.updateBUName("MIDAMERICAN ENERGY SERVICES LLC - BU", "MIDAMERICAN ENERGY SERVICES, LLC - BU", "MidAmerican Energy Services, LLC", iRunNumber, className);
			this.updateBUName("MIECO LLC - BU", "MIECO LLC - BU", "Mieco LLC", iRunNumber, className);
			this.updateBUName("MMPT - BU", "MMPT - BU", "Magellan Pipeline Company, L.P.", iRunNumber, className);
			this.updateBUName("MORGAN STANLEY CAPITAL GROUP INC. - BU", "MORGAN STANLEY CAPITAL GROUP INC. - BU", "Morgan Stanley Capital Group Inc.", iRunNumber, className);
			this.updateBUName("MTCL - BU", "MTCL - BU", "MILLENNIUM TRUST COMPANY, LLC", iRunNumber, className);
			this.updateBUName("MU MARKETING LLC - BU", "MU MARKETING LLC - BU", "MU Marketing LLC", iRunNumber, className);
			this.updateBUName("NET MEXICO GAS PIPELINE PARTNERS LLC - BU", "NET MEXICO GAS PIPELINE PARTNERS, LLC - BU", "NET Mexico Gas Pipeline Partners, LLC", iRunNumber, className);
			this.updateBUName("NEXTERA ENERGY MARKETING LLC - BU", "NEXTERA ENERGY MARKETING, LLC - BU", "NextEra Energy Capital Holdings, Inc. DBA NextEra Energy Marketing, LLC", iRunNumber, className);
			this.updateBUName("NEXUS - BU", "NEXUS - BU", "Nexus Gas Transmission, LLC", iRunNumber, className);
			this.updateBUName("NATURAL GAS PIPELINE COMPANY OF AMERICA - BU", "NGPC - BU", "Natural Gas Pipeline Company of America", iRunNumber, className);
			this.updateBUName("NGPX - BU", "NGPX - BU", "NGP XI US HOLDINGS LP", iRunNumber, className);
			this.updateBUName("ICE NGX CANADA INC - BU", "NGX - BU", "ICE NGX Canada Inc", iRunNumber, className);
			this.updateBUName("NICOR GAS CO - BU", "NICOR GAS CO - BU", "Northern Illinois Gas Company DBA Nicor Gas Co", iRunNumber, className);
			this.updateBUName("NORTHERN INDIANA PUBLIC SERVICE COMPANY LLC - BU", "NORTHERN INDIANA PUBLIC SERVICE COMPANY LLC - BU", "Northern Indiana Public Service Company LLC", iRunNumber, className);
			this.updateBUName("NORTHERN NATURAL GAS COMPANY - BU", "NORTHERN NATURAL GAS COMPANY - BU", "Northern Natural Gas Company", iRunNumber, className);
			this.updateBUName("NORTHERN STATES POWER COMPANY - BU", "NORTHERN STATES POWER COMPANY, MINNESOTA - BU", "Northern States Power Company, Minnesota", iRunNumber, className);
			this.updateBUName("NATURGY SERVICIOS SA DE CV - BU", "NSSA3 - BU", "Naturgy Servicios, S.A. de C.V.", iRunNumber, className);
			this.updateBUName("OASIS PIPELINE LP - BU", "OASIS PIPELINE, LP - BU", "Energy Transfer Partners, L.P. DBA Oasis Pipeline, LP", iRunNumber, className);
			this.updateBUName("OCCIDENTAL ENERGY MARKETING INC - BU", "OCCIDENTAL ENERGY MARKETING, INC. - BU", "Occidental Energy Marketing, Inc.", iRunNumber, className);
			this.updateBUName("OKLAHOMA GAS & ELECTRIC COMPANY - BU", "OKLAHOMA GAS & ELECTRIC COMPANY - BU", "Oklahoma Gas & Electric Company", iRunNumber, className);
			this.updateBUName("OKLAHOMA NATURAL GAS - BU", "OKLAHOMA NATURAL GAS - BU", "ONE Gas, Inc. DBA Oklahoma Natural Gas", iRunNumber, className);
			this.updateBUName("ONEOK FIELD SERVICES COMPANY LLC - BU", "ONEOK FIELD SERVICES COMPANY, L.L.C. - BU", "Oneok Partners Intermediate Limited Partnership DBA ONEOK Field Services Company, L.L.C.", iRunNumber, className);
			this.updateBUName("ONEOK GAS STORAGE LLC - BU", "ONEOK GAS STORAGE, L.L.C. - BU", "Oneok Partners Intermediate Limited Partnership DBA ONEOK Gas Storage, L.L.C.", iRunNumber, className);
			this.updateBUName("ONEOK GAS TRANSPORATION LLC - BU", "ONEOK GAS TRANSPORATION, L.L.C. - BU", "Oneok Gas Transporation, L.L.C.", iRunNumber, className);
			this.updateBUName("ONEOK WESTEX TRANSMISSION LLC - BU", "ONEOK WESTEX TRANSMISSION, L.L.C. - BU", "Oneok Partners Intermediate Limited Partnership DBA ONEOK WesTex Transmission, L.L.C.", iRunNumber, className);
			this.updateBUName("ONIX - BU", "ONIX - BU", "ONIX SOLUTIONS LIMITED", iRunNumber, className);
			this.updateBUName("OVINTIV MARKETING INC - BU", "OVINTIV MARKETING INC. - BU", "Ovintiv Marketing Inc.", iRunNumber, className);
			this.updateBUName("OZARK GAS TRANSMISSION LLC - BU", "OZARK GAS TRANSMISSION, LLC - BU", "Ozark Gas Transmission, LLC", iRunNumber, className);
			this.updateBUName("PANHANDLE EASTERN PIPE LINE COMPANY LP - BU", "PANHANDLE EASTERN PIPE LINE COMPANY, LP - BU", "Energy Transfer LP DBA Panhandle Eastern Pipe Line Company, LP", iRunNumber, className);
			this.updateBUName("PATHPOINT ENERGY LLC - BU", "PATHPOINT ENERGY LLC - BU", "PathPoint Energy LLC", iRunNumber, className);
			this.updateBUName("PGAEC - BU", "PGAEC - BU", "Pacific Gas and Electric Company", iRunNumber, className);
			this.updateBUName("PINE PRAIRIE ENERGY CENTER LLC - BU", "PINE PRAIRIE ENERGY CENTER, LLC - BU", "Pine Prairie Energy Center, LLC", iRunNumber, className);
			this.updateBUName("PIONEER NATURAL RESOURCES USA, INC. - BU", "PIONEER NATURAL RESOURCES USA, INC. - BU", "Pioneer Natural Resources USA, Inc.", iRunNumber, className);
			this.updateBUName("PRESIDIO PETROLEUM LLC - BU", "PRESIDIO PETROLEUM LLC - BU", "Presidio Investment Holdings LLC DBA Presidio Petroleum LLC", iRunNumber, className);
			this.updateBUName("PUBLIC SERVICE COMPANY OF NEW MEXICO - BU", "PUBLIC SERVICE COMPANY OF NEW MEXICO - BU", "Public Service Company of New Mexico", iRunNumber, className);
			this.updateBUName("PERRYVILLE GAS STORAGE LLC - BU", "PVGS - BU", "Perryville Gas Storage, LLC", iRunNumber, className);
			this.updateBUName("PWEP AUGUSTA, LLC - BU", "PWEP AUGUSTA, LLC - BU", "PWEP Augusta, LLC", iRunNumber, className);
			this.updateBUName("RADIATE ENERGY LLC - BU", "RADIATE ENERGY LLC - BU", "Radiate Energy LLC", iRunNumber, className);
			this.updateBUName("RANG - BU", "RANG - BU", "Range Resources - Appalachia, LLC", iRunNumber, className);
			this.updateBUName("REPSOL ENERGY NORTH AMERICA CORPORATION - BU", "REPSOL ENERGY NORTH AMERICA CORPORATION - BU", "Repsol Energy North America Corporation", iRunNumber, className);
			this.updateBUName("SAAV3 - BU", "SAAV3 - BU", "Saavi Energy Solutions, LLC", iRunNumber, className);
			this.updateBUName("SABINE PASS LIQUEFACTION LLC - BU", "SABINE PASS LIQUEFACTION, LLC - BU", "Sabine Pass Liquefaction, LLC", iRunNumber, className);
			this.updateBUName("SEQUENT ENERGY MANAGEMENT LLC - BU", "SEQUENT ENERGY MANAGEMENT, L.P. - BU", "Sequent Energy Management, L.P.", iRunNumber, className);
			this.updateBUName("SHELL ENERGY NORTH AMERICA (US) LP - BU", "SHELL ENERGY NORTH AMERICA (US), L.P. - BU", "Shell Energy North America (US), L.P.", iRunNumber, className);
			this.updateBUName("SILVERBOW RESOURCES OPERATING LLC - BU", "SIBOW - BU", "SilverBow Resources Operating, LLC", iRunNumber, className);
			this.updateBUName("SIX ONE COMMODITIES LLC - BU", "SIX ONE COMMODITIES LLC - BU", "Six One Commodities LLC", iRunNumber, className);
			this.updateBUName("SMEC - BU", "SMEC - BU", "SM Energy Company", iRunNumber, className);
			this.updateBUName("SOUTH JERSEY RESOURCES GROUP LLC - BU", "SOUTH JERSEY RESOURCES GROUP LLC - BU", "South Jersey Resources Group LLC", iRunNumber, className);
			this.updateBUName("SOUTHERN STAR CENTRAL GAS PIPELINE INC - BU", "SOUTHERN STAR CENTRAL GAS PIPELINE, INC. - BU", "Southern Star Central Gas Pipeline, Inc.", iRunNumber, className);
			this.updateBUName("SOUTHWEST ENERGY LP - BU", "SOUTHWEST ENERGY, L.P. - BU", "Southwest Energy, L.P.", iRunNumber, className);
			this.updateBUName("SPIRE MARKETING INC - BU", "SPIRE MARKETING INC. - BU", "Spire Marketing Inc.", iRunNumber, className);
			this.updateBUName("SPIRE MISSOURI INC. - BU", "SPIRE MISSOURI INC. - BU", "Spire Missouri Inc.", iRunNumber, className);
			this.updateBUName("SPOTLIGHT ENERGY LLC - BU", "SPOTLIGHT ENERGY, LLC - BU", "Spotlight Energy, LLC", iRunNumber, className);
			this.updateBUName("SUPERIOR PIPELINE TEXAS, LLC - BU", "SUPERIOR PIPELINE TEXAS, LLC - BU", "Superior Pipeline Company, LLC DBA Superior Pipeline Texas, LLC", iRunNumber, className);
			this.updateBUName("SWN ENERGY SERVICES COMPANY LLC - BU", "SWN ENERGY SERVICES COMPANY, LLC - BU", "SWN Energy Services Company, LLC", iRunNumber, className);
			this.updateBUName("SYMMETRY ENERGY SOLUTIONS LLC - BU", "SYMM - BU", "Symmetry Energy Solutions, LLC", iRunNumber, className);
			this.updateBUName("TAPSTONE SELLER LLC - BU", "TAPSTONE SELLER LLC - BU", "Tapstone Energy, LLC DBA Tapstone Seller LLC", iRunNumber, className);
			this.updateBUName("TARGA GAS MARKETING LLC - BU", "TARGA GAS MARKETING LLC - BU", "Targa Midstream Services LLC DBA Targa Gas Marketing LLC", iRunNumber, className);
			this.updateBUName("TC ENERGY MARKETING INC - BU", "TC ENERGY MARKETING INC. - BU", "TC Energy Marketing Inc.", iRunNumber, className);
			this.updateBUName("TENNESSEE GAS PIPELINE LLC - BU", "TENNESSEE GAS PIPELINE, L.L.C. - BU", "Tennessee Gas Pipeline, L.L.C.", iRunNumber, className);
			this.updateBUName("TEXAS GAS TRANSMISSION LLC - BU", "TEXAS GAS TRANSMISSION, LLC - BU", "Texas Gas Transmission, LLC", iRunNumber, className);
			this.updateBUName("TEXLA ENERGY MANAGEMENT INC - BU", "TEXLA ENERGY MANAGEMENT, INC. - BU", "Texla Energy Management, Inc.", iRunNumber, className);
			this.updateBUName("TGNR3 - BU", "TGNR3 - BU", "TGNR NLA LLC", iRunNumber, className);
			this.updateBUName("THE EMPIRE DISTRICT ELECTRIC COMPANY - BU", "THE EMPIRE DISTRICT ELECTRIC COMPANY - BU", "The Empire District Electric Company", iRunNumber, className);
			this.updateBUName("THE ENERGY AUTHORITY INC - BU", "THE ENERGY AUTHORITY, INC - BU", "The Energy Authority, Inc", iRunNumber, className);
			this.updateBUName("TIDAL ENERGY MARKETING (U.S.) L.L.C. - BU", "TIDAL ENERGY MARKETING (U.S.) L.L.C. - BU", "Tidal Energy Marketing (U.S.) L.L.C.", iRunNumber, className);
			this.updateBUName("TOURMALINE OIL MARKETING CORP. - BU", "TOURMALINE OIL MARKETING CORP. - BU", "Tourmaline Oil Marketing Corp.", iRunNumber, className);
			this.updateBUName("TRAFM - BU", "TRAFM - BU", "Trafigura Mexico, S.A. de C.V.", iRunNumber, className);
			this.updateBUName("TRANSCONTINENTAL GAS PIPE LINE COMPANY LLC - BU", "TRANSCONTINENTAL GAS PIPELINE COMPANY LLC - BU", "Transcontinental Gas Pipeline Company LLC", iRunNumber, className);
			this.updateBUName("TRANSWESTERN PIPELINE COMPANY - BU", "TRANSWESTERN PIPELINE COMPANY, LLC - BU", "Energy Transfer Partners, L.P. DBA Transwestern Pipeline Company, LLC", iRunNumber, className);
			this.updateBUName("TRESPAL - BU", "TRESPAL - BU", "Tres Palacios Gas Storage LLC", iRunNumber, className);
			this.updateBUName("TENNESSEE VALLEY AUTHORITY - BU", "TVAL - BU", "Tennessee Valley Authority", iRunNumber, className);
			this.updateBUName("TVLL3 - BU", "TVLL3 - BU", "TGNR TVL LLC", iRunNumber, className);
			this.updateBUName("TWIN EAGLE RESOURCE MANAGEMENT LLC - BU", "TWIN EAGLE RESOURCE MANAGEMENT, LLC - BU", "Twin Eagle Holdings N.A., LLC DBA Twin Eagle Resource Management, LLC", iRunNumber, className);
			this.updateBUName("UECA - BU", "UECA - BU", "Union Electric Company dba Ameren Missouri", iRunNumber, className);
			this.updateBUName("UNIPER GLOBAL COMMODITIES NORTH AMERICA LLC - BU", "UNIPER GLOBAL COMMODITIES NORTH AMERICA LLC - BU", "Uniper Global Commodities North America LLC", iRunNumber, className);
			this.updateBUName("UNITED ENERGY TRADING LLC - BU", "UNITED ENERGY TRADING, LLC - BU", "United Energy Trading, LLC", iRunNumber, className);
			this.updateBUName("VCPL - BU", "VCPL - BU", "Valley Crossing Pipeline, LLC", iRunNumber, className);
			this.updateBUName("VECTOR PIPELINE LP - BU", "VECTOR PIPELINE LIMITED PARTNERSHIP - BU", "Vector Pipeline Limited Partnership", iRunNumber, className);
			this.updateBUName("VITOL INC - BU", "VITOL INC. - BU", "Vitol Inc.", iRunNumber, className);
			this.updateBUName("WASHINGTON 10 STORAGE CORPORATION - BU", "WASH - BU", "Washington 10 Storage Corporation", iRunNumber, className);
			this.updateBUName("WELLS FARGO COMMODITIES - BU", "WELL3 - BU", "Wells Fargo Commodities, LLC", iRunNumber, className);
			this.updateBUName("WESTAR ENERGY INC - BU", "WESTAR ENERGY INC - BU", "Evergy Kansas Central Inc. DBA Westar Energy Inc", iRunNumber, className);
			this.updateBUName("WGL MIDSTREAM, INC. - BU", "WGL MIDSTREAM, INC. - BU", "WGL Midstream, Inc.", iRunNumber, className);
			this.updateBUName("WILLIAMS ENERGY RESOURCES LLC - BU", "WILLIAMS ENERGY RESOURCES LLC - BU", "The Williams Companies, Inc. DBA Williams Energy Resources LLC", iRunNumber, className);
			this.updateBUName("WISCONSIN POWER AND LIGHT COMPANY - BU", "WISCONSIN POWER AND LIGHT COMPANY - BU", "Wisconsin Power and Light Company", iRunNumber, className);
			this.updateBUName("WORLD FUEL SERVICES INC - BU", "WORLD FUEL SERVICES, INC. - BU", "World Fuel Services, Inc.", iRunNumber, className);
			this.updateBUName("WORSHAM-STEED GAS STORAGE LLC - BU", "WORST - BU", "Worsham-Steed Storage LLC", iRunNumber, className);
			this.updateBUName("MONT - BU", "WSTMT - BU", "Westmont Energy LP", iRunNumber, className);
			this.updateBUName("WTG MIDSTREAM MARKETING LLC - BU", "WTG MIDSTREAM MARKETING, LLC - BU", "WTG Midstream Marketing, LLC", iRunNumber, className);
			this.updateBUName("WWM LOGISTICS, LLC - BU", "WWM LOGISTICS, LLC - BU", "WWM Logistics, LLC", iRunNumber, className);
			this.updateBUName("SOUTHWESTERN PUBLIC SERVICE COMPANY - BU", "XCEL ENERGY - BU", "Southwestern Public Service Company DBA Xcel Energy", iRunNumber, className);
			this.updateBUName("XTO ENERGY INC - BU", "XTO ENERGY INC. - BU", "XTO Energy Inc.", iRunNumber, className);

			this.updateLEName("PUBLIC SERVICE COMPANY OF OKLAHOMA DBA AEP PSO - LE", "AEP PSO - LE", "Public Service Company of Oklahoma DBA AEP PSO", iRunNumber, className);
			this.updateLEName("AGUA BLANCA PIPELINE - LE", "AGUA BLANCA, LLC - LE", "Agua Blanca, LLC", iRunNumber, className);
			this.updateLEName("ALLIANCE PIPELINE - LE", "ALLIANCE PIPELINE L.P. - LE", "Alliance Pipeline L.P.", iRunNumber, className);
			this.updateLEName("INTERSTATE POWER AND LIGHT COMPANY - LE", "ALLIANT ENERGY - LE", "Interstate Power and Light Company DBA Alliant Energy", iRunNumber, className);
			this.updateLEName("ANADARKO ENERGY SERVICES COMPANY - LE", "ANADARKO ENERGY SERVICES COMPANY - LE", "Anadarko Energy Services Company", iRunNumber, className);
			this.updateLEName("ANCOVA ENERGY MARKETING, LLC - LE", "ANCOVA ENERGY MARKETING, LLC - LE", "Ancova Energy Marketing, LLC", iRunNumber, className);
			this.updateLEName("ANEW RNG LLC - LE", "ANEW RNG, LLC - LE", "Anew RNG, LLC", iRunNumber, className);
			this.updateLEName("ANGEL INVESTING GROUP, LLLP - LE", "ANIG - LE", "ANGEL INVESTING GROUP, LLLP", iRunNumber, className);
			this.updateLEName("ANR PIPELINE COMPANY - LE", "ANR PIPELINE COMPANY - LE", "ANR Pipeline Company", iRunNumber, className);
			this.updateLEName("APACHE CORPORATION - LE", "APACHE CORPORATION - LE", "Apache Corporation", iRunNumber, className);
			this.updateLEName("ARKANSAS ELECTRIC COOPERATIVE CORPORATION - LE", "ARKANSAS ELECTRIC COOPERATIVE CORPORATION - LE", "Arkansas Electric Cooperative Corporation", iRunNumber, className);
			this.updateLEName("ARM ENERGY MANAGEMENT LLC - LE", "ARM ENERGY MANAGEMENT LLC - LE", "ARM Energy Management LLC", iRunNumber, className);
			this.updateLEName("J ARON & COMPANY LLC - LE", "ARON - LE", "J. Aron & Company LLC", iRunNumber, className);
			this.updateLEName("ASCENT RESOURCES-UTICA LLC - LE", "ARUL3 - LE", "Ascent Resources - Utica, LLC", iRunNumber, className);
			this.updateLEName("ASSOCIATED ELECTRIC COOPERATIVE INC - LE", "ASSOCIATED ELECTRIC COOPERATIVE INC - LE", "Associated Electric Cooperative Inc", iRunNumber, className);
			this.updateLEName("BBT MID LOUISIANA GAS TRANSMISSION, LLC - LE", "BBT MID LOUISIANA GAS TRANSMISSION, LLC - LE", "BBT Mid Louisiana Gas Transmission, LLC", iRunNumber, className);
			this.updateLEName("BE ANADARKO II LLC - LE", "BEAN3 - LE", "BE Anadarko II, LLC", iRunNumber, className);
			this.updateLEName("BLACKBEARD OPERATING, LLC - LE", "BLACKBEARD OPERATING, LLC - LE", "Blackbeard Operating, LLC", iRunNumber, className);
			this.updateLEName("BLUEGRASS ENERGY - LE", "BLEN - LE", "BLUEGRASS ENERGY", iRunNumber, className);
			this.updateLEName("BLUE MOUNTAIN MIDSTREAM LLC - LE", "BLUE MOUNTAIN MIDSTREAM LLC - LE", "Blue Mountain Midstream LLC", iRunNumber, className);
			this.updateLEName("BP CANADA ENERGY MARKETING CORP - LE", "BP CANADA ENERGY MARKETING CORP - LE", "BP Canada Energy Marketing Corp", iRunNumber, className);
			this.updateLEName("BP ENERGY COMPANY - LE", "BP ENERGY COMPANY - LE", "BP Energy Company", iRunNumber, className);
			this.updateLEName("BLUE RACER MIDSTREAM, LLC - LE", "BRMS - LE", "Blue Racer Midstream, LLC", iRunNumber, className);
			this.updateLEName("CALEDONIA ENERGY PARTNERS LLC - LE", "CALEDONIA ENERGY PARTNERS, L.L.C. - LE", "Caledonia Energy Partners, L.L.C.", iRunNumber, className);
			this.updateLEName("CARBONBETTER LLC - LE", "CARBONBETTER, LLC - LE", "Carbonbetter, LLC", iRunNumber, className);
			this.updateLEName("CASTLETON - LE", "CASTLETON COMMODITIES MERCHANT TRADING L.P. - LE", "Castleton Global Trading LLC DBA Castleton Commodities Merchant Trading L.P.", iRunNumber, className);
			this.updateLEName("CENTURYLINK - LE", "CENT - LE", "CENTURYLINK", iRunNumber, className);
			this.updateLEName("CFE INTERNATIONAL LLC - LE", "CFE INTERNATIONAL LLC - LE", "CFE International LLC", iRunNumber, className);
			this.updateLEName("COLUMBIA GULF TRANSMISSION LLC - LE", "CGTL - LE", "Columbia Gulf Transmission, LLC", iRunNumber, className);
			this.updateLEName("COLUMBIA GAS (TCO) - LE", "CGTM - LE", "Columbia Gas Transmission, LLC", iRunNumber, className);
			this.updateLEName("CHESAPEAKE ENERGY MARKETING LLC - LE", "CHESAPEAKE ENERGY MARKETING, L.L.C. - LE", "Chesapeake Energy Corporation DBA Chesapeake Energy Marketing L.L.C.", iRunNumber, className);
			this.updateLEName("CHEVRON NATURAL GAS - LE", "CHEVRON NATURAL GAS, A DIVISION OF CHEVRON U.S.A. INC. - LE", "Chevron U.S.A. Inc. DBA Chevron Natural Gas, a division of Chevron U.S.A. Inc.", iRunNumber, className);
			this.updateLEName("CIMA ENERGY LP - LE", "CIEN - LE", "CIMA ENERGY, LTD", iRunNumber, className);
			this.updateLEName("CITADEL ENERGY MARKETING LLC - LE", "CITADEL ENERGY MARKETING LLC - LE", "Citadel Energy Marketing LLC", iRunNumber, className);
			this.updateLEName("CITY UTILITIES OF SPRINGFIELD, MISSOURI - LE", "CITY UTILITIES OF SPRINGFIELD, MISSOURI - LE", "City Utilities of Springfield, Missouri", iRunNumber, className);
			this.updateLEName("CLEARWATER ENTERPRISES LLC - LE", "CLEARWATER ENTERPRISES, L.L.C. - LE", "Clearwater Enterprises, L.L.C.", iRunNumber, className);
			this.updateLEName("COKINOS ENERGY CORPORATION - LE", "COEC - LE", "Cokinos Energy Corporation", iRunNumber, className);
			this.updateLEName("COGENCY GLOBAL - LE", "COGE - LE", "COGENCY GLOBAL", iRunNumber, className);
			this.updateLEName("COLORADO SPRINGS UTILITIES - LE", "COLORADO SPRINGS UTILITIES - LE", "Colorado Springs Utilities", iRunNumber, className);
			this.updateLEName("CONCORD ENERGY LLC - LE", "CONCORD ENERGY LLC - LE", "Concord Energy LLC", iRunNumber, className);
			this.updateLEName("CONOCOPHILLIPS COMPANY - LE", "CONOCOPHILLIPS COMPANY - LE", "ConocoPhillips Company", iRunNumber, className);
			this.updateLEName("CONSTELLATION ENERGY GENERATION LLC - LE", "CONSTELLATION ENERGY GENERATION, LLC - LE", "Constellation Energy Generation, LLC", iRunNumber, className);
			this.updateLEName("CONSUMERS ENERGY COMPANY - LE", "CONSUMERS ENERGY COMPANY - LE", "Consumers Energy Company", iRunNumber, className);
			this.updateLEName("CONTINENTAL RESOURCES INC - LE", "CONTINENTAL RESOURCES, INC. - LE", "Continental Resources, Inc.", iRunNumber, className);
			this.updateLEName("COPEQ TRADING CO. - LE", "COPQ - LE", "Copeq Trading Co.", iRunNumber, className);
			this.updateLEName("CORPUS CHRISTI LIQUEFACTION LLC - LE", "CORPUS CHRISTI LIQUEFACTION, LLC - LE", "Corpus Christi Liquefaction, LLC", iRunNumber, className);
			this.updateLEName("COMSTOCK OIL & GAS, LLC - LE", "CSTK3 - LE", "Comstock Oil & Gas, LLC", iRunNumber, className);
			this.updateLEName("DCP MIDSTREAM MARKETING LLC - LE", "DCP MIDSTREAM MARKETING, LLC - LE", "DCP Midstream, LP DBA DCP Midstream Marketing, LLC", iRunNumber, className);
			this.updateLEName("DIRECT ENERGY BUSINESS MARKETING LLC - LE", "DEBM - LE", "Direct Energy Business Marketing, LLC", iRunNumber, className);
			this.updateLEName("DIVERSIFIED ENERGY MARKETING LLC - LE", "DEML - LE", "Diversified Energy Marketing LLC", iRunNumber, className);
			this.updateLEName("DEVON GAS SERVICES, L.P. - LE", "DEVON GAS SERVICES, L.P. - LE", "Devon Gas Services, L.P.", iRunNumber, className);
			this.updateLEName("DK TRADING & SUPPLY LLC - LE", "DK TRADING & SUPPLY, LLC - LE", "Delek US Energy, Inc DBA DK Trading & Supply, LLC", iRunNumber, className);
			this.updateLEName("DTE ENERGY TRADING INC - LE", "DTE ENERGY TRADING, INC. - LE", "DTE Energy Trading, Inc.", iRunNumber, className);
			this.updateLEName("DTE GAS COMPANY - LE", "DTE GAS COMPANY - LE", "DTE Gas Company", iRunNumber, className);
			this.updateLEName("DXT COMMODITIES NORTH AMERICA INC - LE", "DXT COMMODITIES NORTH AMERICA INC. - LE", "DXT Commodities North America Inc.", iRunNumber, className);
			this.updateLEName("TGNR EAST TEXAS LLC - LE", "EAST3 - LE", "TGNR East Texas LLC", iRunNumber, className);
			this.updateLEName("ECO-ENERGY NATURAL GAS, LLC - LE", "ECO-ENERGY NATURAL GAS, LLC - LE", "Copersucar North America, LLC DBA Eco-Energy Natural Gas, LLC", iRunNumber, className);
			this.updateLEName("EDF TRADING NORTH AMERICA LLC - LE", "EDF TRADING NORTH AMERICA, LLC - LE", "EDF Trading North America, LLC", iRunNumber, className);
			this.updateLEName("ELEVATION ENERGY GROUP, LLC - LE", "EEGL - LE", "Elevation Energy Group, LLC", iRunNumber, className);
			this.updateLEName("ELEMENT MARKETS, LLC DBA ELEMENT MARKETS RENEWABLE ENERGY, LLC - LE", "ELEMENT MARKETS RENEWABLE ENERGY, LLC - LE", "Element Markets, LLC DBA Element Markets Renewable Energy, LLC", iRunNumber, className);
			this.updateLEName("ENTERGY LOUISIANA LLC - LE", "ELOU3 - LE", "Entergy Louisiana, LLC", iRunNumber, className);
			this.updateLEName("EMERA ENERGY SERVICES INC - LE", "EMERA ENERGY SERVICES, INC. - LE", "Emera Energy Services, Inc.", iRunNumber, className);
			this.updateLEName("ENABLE ENERGY RESOURCES, LLC - LE", "ENABLE ENERGY RESOURCES, LLC - LE", "Enable Midstream Partners, LP DBA Enable Energy Resources, LLC", iRunNumber, className);
			this.updateLEName("ENABLE GAS TRANSMISSION LLC - LE", "ENABLE GAS TRANSMISSION, LLC - LE", "Enable Midstream Partners, LP DBA Enable Gas Transmission, LLC", iRunNumber, className);
			this.updateLEName("ENABLE OKLAHOMA INTRASTATE TRANSMISSION LLC - LE", "ENABLE OKLAHOMA INTRASTATE TRANSMISSION, LLC - LE", "Enable Midstream Partners, LP DBA Enable Oklahoma Intrastate Transmission, LLC", iRunNumber, className);
			this.updateLEName("ENESTAS, S.A. DE C.V. - LE", "ENESTAS, S.A. DE C.V. - LE", "Enestas, S.A. de C.V.", iRunNumber, className);
			this.updateLEName("ENGIE ENERGY MARKETING NA INC - LE", "ENGE - LE", "ENGIE Energy Marketing NA, Inc.", iRunNumber, className);
			this.updateLEName("ENLINK MIDSTREAM OPERATING, LP DBA ENLINK GAS MARKETING, LP - LE", "ENLINK GAS MARKETING, LP - LE", "EnLink Midstream Operating, LP DBA EnLink Gas Marketing, LP", iRunNumber, className);
			this.updateLEName("ENSIGN SERVICES, INC. - LE", "ENSI - LE", "ENSIGN SERVICES, INC.", iRunNumber, className);
			this.updateLEName("ENSIGHT IV ENERGY PARTNERS, LLC - LE", "ENSIGHT - LE", "EnSight IV Energy Partners, LLC", iRunNumber, className);
			this.updateLEName("ENTERGY ARKANSAS, LLC - LE", "ENTAR - LE", "Entergy Arkansas, LLC", iRunNumber, className);
			this.updateLEName("ENTERPRISE PRODUCTS OPERATING LLC - LE", "ENTERPRISE PRODUCTS OPERATING LLC - LE", "Enterprise Products Operating LLC", iRunNumber, className);
			this.updateLEName("ENTERPRISE TEXAS PIPELINE LLC - LE", "ENTERPRISE TEXAS PIPELINE LLC - LE", "Enterprise Texas Pipeline LLC", iRunNumber, className);
			this.updateLEName("ENTERGY MISSISSIPPI, LLC - LE", "ENTMS - LE", "Entergy Mississippi, LLC", iRunNumber, className);
			this.updateLEName("ENTERGY NEW ORLEANS, LLC - LE", "ENTNO - LE", "Entergy New Orleans, LLC", iRunNumber, className);
			this.updateLEName("ENTERGY TEXAS, INC. - LE", "ENTTX - LE", "Entergy Texas, Inc.", iRunNumber, className);
			this.updateLEName("EOG RESOURCES INC - LE", "EOG RESOURCES, INC. - LE", "EOG Resources, Inc.", iRunNumber, className);
			this.updateLEName("EOX HOLDINGS, LLC - LE", "EOXH - LE", "EOX HOLDINGS, LLC", iRunNumber, className);
			this.updateLEName("EQT ENERGY LLC - LE", "EQT ENERGY, LLC - LE", "EQT Production Company DBA EQT Energy, LLC", iRunNumber, className);
			this.updateLEName("EQUINOR NATURAL GAS LLC - LE", "EQUINOR NATURAL GAS LLC - LE", "Equinor Natural Gas LLC", iRunNumber, className);
			this.updateLEName("ESENTIA GAS LLC - LE", "ESGL - LE", "Esentia Gas LLC", iRunNumber, className);
			this.updateLEName("ETC MARKETING LTD - LE", "ETC MARKETING, LTD. - LE", "Energy Transfer LP DBA ETC Marketing, LTD.", iRunNumber, className);
			this.updateLEName("EXELON GENERATION COMPANY, LLC - LE", "EXGC - LE", "Exelon Generation Company, LLC", iRunNumber, className);
			this.updateLEName("FLORIDA GAS TRANSMISSION - LE", "FGTC - LE", "Florida Gas Transmission Company, LLC", iRunNumber, className);
			this.updateLEName("FREEBIRD GAS STORAGE LLC - LE", "FREE - LE", "Freebird Gas Storage LLC", iRunNumber, className);
			this.updateLEName("FREEPOINT COMMODITIES LLC - LE", "FREEPOINT COMMODITIES LLC - LE", "Freepoint Commodities LLC", iRunNumber, className);
			this.updateLEName("GCC SUPPLY & TRADING LLC - LE", "GCC SUPPLY & TRADING LLC - LE", "GCC Supply & Trading LLC", iRunNumber, className);
			this.updateLEName("GOLDEN TRIANGLE STORAGE LLC - LE", "GLDNT - LE", "Golden Triangle Storage, Inc", iRunNumber, className);
			this.updateLEName("GULF RUN TRANSMISSION, LLC - LE", "GLFRN - LE", "Gulf Run Transmission, LLC", iRunNumber, className);
			this.updateLEName("WILD GOOSE STORAGE LLC - LE", "GOOS3 - LE", "Wild Goose Storage, LLC", iRunNumber, className);
			this.updateLEName("GOLDEN PASS LNG TERMINAL LLC - LE", "GPLNG - LE", "Golden Pass LNG Terminal LLC", iRunNumber, className);
			this.updateLEName("GREAT LAKES GAS TRANSMISSION - LE", "GREAT LAKES GAS TRANSMISSION LIMITED PARTNERSHIP - LE", "Great Lakes Gas Transmission Limited Partnership", iRunNumber, className);
			this.updateLEName("GULF SOUTH PIPELINE COMPANY - LE", "GSPC - LE", "Gulf South Pipeline Company, LLC", iRunNumber, className);
			this.updateLEName("GUNVOR USA LLC - LE", "GUNV - LE", "Gunvor USA LLC", iRunNumber, className);
			this.updateLEName("HARTREE PARTNERS LP - LE", "HARTREE PARTNERS, LP - LE", "Hartree Partners, LP", iRunNumber, className);
			this.updateLEName("ICBC STANDARD BANK PLC - LE", "ICBC STANDARD BANK PLC - LE", "ICBC Standard Bank PLC", iRunNumber, className);
			this.updateLEName("INTERCONN RESOURCES, LLC - LE", "INTERCONN RESOURCES, LLC - LE", "Interconn Resources, LLC", iRunNumber, className);
			this.updateLEName("INTERSTATE GAS SUPPLY LLC - LE", "INTERSTATE GAS SUPPLY, INC. - LE", "Interstate Gas Supply, Inc.", iRunNumber, className);
			this.updateLEName("K2 COMMODITIES LLC - LE", "K2 COMMODITIES, LLC - LE", "K2 Commodities, LLC", iRunNumber, className);
			this.updateLEName("KAISER MARKETING APPALACHIAN LLC - LE", "KAIM - LE", "Kaiser Marketing Appalachian, LLC", iRunNumber, className);
			this.updateLEName("EVERGY MISSOURI WEST INC DBA KCP&L GREATER MISSOURI OPERATIONS COMPANY - LE", "KCP&L GREATER MISSOURI OPERATIONS COMPANY - LE", "Evergy Missouri West Inc DBA KCP&L Greater Missouri Operations Company", iRunNumber, className);
			this.updateLEName("KOCH ENERGY SERVICES LLC - LE", "KOCH ENERGY SERVICES, LLC - LE", "Kaes Domestic Holdings, LLC DBA Koch Energy Services, LLC", iRunNumber, className);
			this.updateLEName("LEGACY RESERVES ENERGY SERVICES LLC - LE", "LEGACY RESERVES ENERGY SERVICES LLC - LE", "Legacy Reserves Energy Services LLC", iRunNumber, className);
			this.updateLEName("LODI GAS STORAGE, L.L.C. - LE", "LODI - LE", "Lodi Gas Storage, L.L.C.", iRunNumber, className);
			this.updateLEName("LUMINANT ENERGY COMPANY LLC - LE", "LUMINANT ENERGY COMPANY LLC - LE", "Luminant Energy Company LLC", iRunNumber, className);
			this.updateLEName("MACQUARIE ENERGY LLC - LE", "MACQUARIE ENERGY LLC - LE", "Macquarie Energy North America Trading Inc. DBA Macquarie Energy LLC", iRunNumber, className);
			this.updateLEName("MADISON GAS & ELECTRIC - LE", "MADISON GAS & ELECTRIC - LE", "Madison Gas & Electric", iRunNumber, className);
			this.updateLEName("MANSFIELD POWER & GAS LLC - LE", "MANSFIELD POWER AND GAS, LLC - LE", "Mansfield Energy Corp DBA Mansfield Power and Gas, LLC", iRunNumber, className);
			this.updateLEName("MARATHON PETROLEUM COMPANY LP - LE", "MARATHON PETROLEUM COMPANY LP - LE", "Marathon Petroleum Company LP", iRunNumber, className);
			this.updateLEName("MARKWEST PIONEER LLC - LE", "MARKWEST PIONEER, LLC - LE", "MarkWest Pioneer, LLC", iRunNumber, className);
			this.updateLEName("MERCURIA ENERGY AMERICA LLC - LE", "MERCURIA ENERGY AMERICA, LLC - LE", "Mercuria Energy Company LLC DBA Mercuria Energy America, LLC", iRunNumber, className);
			this.updateLEName("MEX GAS SUPPLY SL - LE", "MEX GAS SUPPLY, S.L. - LE", "Mex Gas Supply, S.L.", iRunNumber, className);
			this.updateLEName("MIDAMERICAN ENERGY COMPANY - LE", "MIDAMERICAN ENERGY COMPANY - LE", "MidAmerican Energy Company", iRunNumber, className);
			this.updateLEName("MIDAMERICAN ENERGY SERVICES LLC - LE", "MIDAMERICAN ENERGY SERVICES, LLC - LE", "MidAmerican Energy Services, LLC", iRunNumber, className);
			this.updateLEName("MIECO LLC - LE", "MIECO LLC - LE", "Mieco LLC", iRunNumber, className);
			this.updateLEName("MAGELLAN PIPELINE COMPANY, L.P. - LE", "MMPT - LE", "Magellan Pipeline Company, L.P.", iRunNumber, className);
			this.updateLEName("MORGAN STANLEY CAPITAL GROUP INC. - LE", "MORGAN STANLEY CAPITAL GROUP INC. - LE", "Morgan Stanley Capital Group Inc.", iRunNumber, className);
			this.updateLEName("MILLENNIUM TRUST COMPANY, LLC - LE", "MTCL - LE", "MILLENNIUM TRUST COMPANY, LLC", iRunNumber, className);
			this.updateLEName("MU MARKETING LLC - LE", "MU MARKETING LLC - LE", "MU Marketing LLC", iRunNumber, className);
			this.updateLEName("NET MEXICO GAS PIPELINE PARTNERS LLC - LE", "NET MEXICO GAS PIPELINE PARTNERS, LLC - LE", "NET Mexico Gas Pipeline Partners, LLC", iRunNumber, className);
			this.updateLEName("NEXTERA ENERGY MARKETING LLC - LE", "NEXTERA ENERGY MARKETING, LLC - LE", "NextEra Energy Capital Holdings, Inc. DBA NextEra Energy Marketing, LLC", iRunNumber, className);
			this.updateLEName("NEXUS GAS TRANSMISSION, LLC - LE", "NEXUS - LE", "Nexus Gas Transmission, LLC", iRunNumber, className);
			this.updateLEName("NATURAL GAS PIPELINE COMPANY OF AMERICA - LE", "NGPC - LE", "Natural Gas Pipeline Company of America", iRunNumber, className);
			this.updateLEName("NGP XI US HOLDINGS LP - LE", "NGPX - LE", "NGP XI US HOLDINGS LP", iRunNumber, className);
			this.updateLEName("ICE NGX CANADA INC - LE", "NGX - LE", "ICE NGX Canada Inc", iRunNumber, className);
			this.updateLEName("NICOR GAS CO - LE", "NICOR GAS CO - LE", "Northern Illinois Gas Company DBA Nicor Gas Co", iRunNumber, className);
			this.updateLEName("NORTHERN INDIANA PUBLIC SERVICE COMPANY LLC - LE", "NORTHERN INDIANA PUBLIC SERVICE COMPANY LLC - LE", "Northern Indiana Public Service Company LLC", iRunNumber, className);
			this.updateLEName("NORTHERN NATURAL GAS COMPANY - LE", "NORTHERN NATURAL GAS COMPANY - LE", "Northern Natural Gas Company", iRunNumber, className);
			this.updateLEName("NORTHERN STATES POWER COMPANY - LE", "NORTHERN STATES POWER COMPANY, MINNESOTA - LE", "Northern States Power Company, Minnesota", iRunNumber, className);
			this.updateLEName("NATURGY SERVICIOS SA DE CV - LE", "NSSA3 - LE", "Naturgy Servicios, S.A. de C.V.", iRunNumber, className);
			this.updateLEName("OASIS PIPELINE LP - LE", "OASIS PIPELINE, LP - LE", "Energy Transfer Partners, L.P. DBA Oasis Pipeline, LP", iRunNumber, className);
			this.updateLEName("OCCIDENTAL ENERGY MARKETING INC - LE", "OCCIDENTAL ENERGY MARKETING, INC. - LE", "Occidental Energy Marketing, Inc.", iRunNumber, className);
			this.updateLEName("OKLAHOMA GAS & ELECTRIC COMPANY - LE", "OKLAHOMA GAS & ELECTRIC COMPANY - LE", "Oklahoma Gas & Electric Company", iRunNumber, className);
			this.updateLEName("ONE GAS, INC. DBA OKLAHOMA NATURAL GAS - LE", "OKLAHOMA NATURAL GAS - LE", "ONE Gas, Inc. DBA Oklahoma Natural Gas", iRunNumber, className);
			this.updateLEName("ONEOK FIELD SERVICES COMPANY LLC - LE", "ONEOK FIELD SERVICES COMPANY, L.L.C. - LE", "Oneok Partners Intermediate Limited Partnership DBA ONEOK Field Services Company, L.L.C.", iRunNumber, className);
			this.updateLEName("ONEOK GAS STORAGE LLC - LE", "ONEOK GAS STORAGE, L.L.C. - LE", "Oneok Partners Intermediate Limited Partnership DBA ONEOK Gas Storage, L.L.C.", iRunNumber, className);
			this.updateLEName("ONEOK GAS TRANSPORATION LLC - LE", "ONEOK GAS TRANSPORATION, L.L.C. - LE", "Oneok Gas Transporation, L.L.C.", iRunNumber, className);
			this.updateLEName("ONEOK WESTEX TRANSMISSION LLC - LE", "ONEOK WESTEX TRANSMISSION, L.L.C. - LE", "Oneok Partners Intermediate Limited Partnership DBA ONEOK WesTex Transmission, L.L.C.", iRunNumber, className);
			this.updateLEName("ONIX SOLUTIONS LIMITED - LE", "ONIX - LE", "ONIX SOLUTIONS LIMITED", iRunNumber, className);
			this.updateLEName("OVINTIV MARKETING INC - LE", "OVINTIV MARKETING INC. - LE", "Ovintiv Marketing Inc.", iRunNumber, className);
			this.updateLEName("OZARK GAS TRANSMISSION LLC - LE", "OZARK GAS TRANSMISSION, LLC - LE", "Ozark Gas Transmission, LLC", iRunNumber, className);
			this.updateLEName("PANHANDLE EASTERN PIPE LINE COMPANY LP - LE", "PANHANDLE EASTERN PIPE LINE COMPANY, LP - LE", "Energy Transfer LP DBA Panhandle Eastern Pipe Line Company, LP", iRunNumber, className);
			this.updateLEName("PATHPOINT ENERGY LLC - LE", "PATHPOINT ENERGY LLC - LE", "PathPoint Energy LLC", iRunNumber, className);
			this.updateLEName("PACIFIC GAS AND ELECTRIC COMPANY - LE", "PGAEC - LE", "Pacific Gas and Electric Company", iRunNumber, className);
			this.updateLEName("PINE PRAIRIE ENERGY CENTER LLC - LE", "PINE PRAIRIE ENERGY CENTER, LLC - LE", "Pine Prairie Energy Center, LLC", iRunNumber, className);
			this.updateLEName("PIONEER NATURAL RESOURCES USA, INC. - LE", "PIONEER NATURAL RESOURCES USA, INC. - LE", "Pioneer Natural Resources USA, Inc.", iRunNumber, className);
			this.updateLEName("PRESIDIO INVESTMENT HOLDINGS LLC DBA PRESIDIO PETROLEUM LLC - LE", "PRESIDIO PETROLEUM LLC - LE", "Presidio Investment Holdings LLC DBA Presidio Petroleum LLC", iRunNumber, className);
			this.updateLEName("PUBLIC SERVICE COMPANY OF NEW MEXICO - LE", "PUBLIC SERVICE COMPANY OF NEW MEXICO - LE", "Public Service Company of New Mexico", iRunNumber, className);
			this.updateLEName("PERRYVILLE GAS STORAGE LLC - LE", "PVGS - LE", "Perryville Gas Storage, LLC", iRunNumber, className);
			this.updateLEName("PWEP AUGUSTA, LLC - LE", "PWEP AUGUSTA, LLC - LE", "PWEP Augusta, LLC", iRunNumber, className);
			this.updateLEName("RADIATE ENERGY LLC - LE", "RADIATE ENERGY LLC - LE", "Radiate Energy LLC", iRunNumber, className);
			this.updateLEName("RANGE RESOURCES - APPALACHIA, LLC - LE", "RANG - LE", "Range Resources - Appalachia, LLC", iRunNumber, className);
			this.updateLEName("REPSOL ENERGY NORTH AMERICA CORPORATION - LE", "REPSOL ENERGY NORTH AMERICA CORPORATION - LE", "Repsol Energy North America Corporation", iRunNumber, className);
			this.updateLEName("SAAVI ENERGY SOLUTIONS, LLC - LE", "SAAV3 - LE", "Saavi Energy Solutions, LLC", iRunNumber, className);
			this.updateLEName("SABINE PASS LIQUEFACTION LLC - LE", "SABINE PASS LIQUEFACTION, LLC - LE", "Sabine Pass Liquefaction, LLC", iRunNumber, className);
			this.updateLEName("SEQUENT ENERGY MANAGEMENT LLC - LE", "SEQUENT ENERGY MANAGEMENT, L.P. - LE", "Sequent Energy Management, L.P.", iRunNumber, className);
			this.updateLEName("SHELL ENERGY NORTH AMERICA (US) LP - LE", "SHELL ENERGY NORTH AMERICA (US), L.P. - LE", "Shell Energy North America (US), L.P.", iRunNumber, className);
			this.updateLEName("SILVERBOW RESOURCES OPERATING LLC - LE", "SIBOW - LE", "SilverBow Resources Operating, LLC", iRunNumber, className);
			this.updateLEName("SIX ONE COMMODITIES LLC - LE", "SIX ONE COMMODITIES LLC - LE", "Six One Commodities LLC", iRunNumber, className);
			this.updateLEName("SM ENERGY COMPANY - LE", "SMEC - LE", "SM Energy Company", iRunNumber, className);
			this.updateLEName("SOUTH JERSEY RESOURCES GROUP LLC - LE", "SOUTH JERSEY RESOURCES GROUP LLC - LE", "South Jersey Resources Group LLC", iRunNumber, className);
			this.updateLEName("SOUTHERN STAR CENTRAL GAS PIPELINE INC - LE", "SOUTHERN STAR CENTRAL GAS PIPELINE, INC. - LE", "Southern Star Central Gas Pipeline, Inc.", iRunNumber, className);
			this.updateLEName("SOUTHWEST ENERGY LP - LE", "SOUTHWEST ENERGY, L.P. - LE", "Southwest Energy, L.P.", iRunNumber, className);
			this.updateLEName("SPIRE MARKETING INC - LE", "SPIRE MARKETING INC. - LE", "Spire Marketing Inc.", iRunNumber, className);
			this.updateLEName("SPIRE MISSOURI INC. - LE", "SPIRE MISSOURI INC. - LE", "Spire Missouri Inc.", iRunNumber, className);
			this.updateLEName("SPOTLIGHT ENERGY LLC - LE", "SPOTLIGHT ENERGY, LLC - LE", "Spotlight Energy, LLC", iRunNumber, className);
			this.updateLEName("SUPERIOR PIPELINE COMPANY, LLC DBA SUPERIOR PIPELINE TEXAS, LLC - LE", "SUPERIOR PIPELINE TEXAS, LLC - LE", "Superior Pipeline Company, LLC DBA Superior Pipeline Texas, LLC", iRunNumber, className);
			this.updateLEName("SWN ENERGY SERVICES COMPANY LLC - LE", "SWN ENERGY SERVICES COMPANY, LLC - LE", "SWN Energy Services Company, LLC", iRunNumber, className);
			this.updateLEName("SYMMETRY ENERGY SOLUTIONS LLC - LE", "SYMM - LE", "Symmetry Energy Solutions, LLC", iRunNumber, className);
			this.updateLEName("TAPSTONE ENERGY, LLC DBA TAPSTONE SELLER LLC - LE", "TAPSTONE SELLER LLC - LE", "Tapstone Energy, LLC DBA Tapstone Seller LLC", iRunNumber, className);
			this.updateLEName("TARGA GAS MARKETING LLC - LE", "TARGA GAS MARKETING LLC - LE", "Targa Midstream Services LLC DBA Targa Gas Marketing LLC", iRunNumber, className);
			this.updateLEName("TC ENERGY MARKETING INC - LE", "TC ENERGY MARKETING INC. - LE", "TC Energy Marketing Inc.", iRunNumber, className);
			this.updateLEName("TENNESSEE GAS PIPELINE LLC - LE", "TENNESSEE GAS PIPELINE, L.L.C. - LE", "Tennessee Gas Pipeline, L.L.C.", iRunNumber, className);
			this.updateLEName("TEXAS GAS TRANSMISSION LLC - LE", "TEXAS GAS TRANSMISSION, LLC - LE", "Texas Gas Transmission, LLC", iRunNumber, className);
			this.updateLEName("TEXLA ENERGY MANAGEMENT INC - LE", "TEXLA ENERGY MANAGEMENT, INC. - LE", "Texla Energy Management, Inc.", iRunNumber, className);
			this.updateLEName("TGNR NLA LLC - LE", "TGNR3 - LE", "TGNR NLA LLC", iRunNumber, className);
			this.updateLEName("THE EMPIRE DISTRICT ELECTRIC COMPANY - LE", "THE EMPIRE DISTRICT ELECTRIC COMPANY - LE", "The Empire District Electric Company", iRunNumber, className);
			this.updateLEName("THE ENERGY AUTHORITY INC - LE", "THE ENERGY AUTHORITY, INC - LE", "The Energy Authority, Inc", iRunNumber, className);
			this.updateLEName("TIDAL ENERGY MARKETING (U.S.) L.L.C. - LE", "TIDAL ENERGY MARKETING (U.S.) L.L.C. - LE", "Tidal Energy Marketing (U.S.) L.L.C.", iRunNumber, className);
			this.updateLEName("TOURMALINE OIL MARKETING CORP. - LE", "TOURMALINE OIL MARKETING CORP. - LE", "Tourmaline Oil Marketing Corp.", iRunNumber, className);
			this.updateLEName("TRAFIGURA MEXICO, S.A. DE C.V. - LE", "TRAFM - LE", "Trafigura Mexico, S.A. de C.V.", iRunNumber, className);
			this.updateLEName("TRANSCONTINENTAL GAS PIPE LINE COMPANY LLC - LE", "TRANSCONTINENTAL GAS PIPELINE COMPANY LLC - LE", "Transcontinental Gas Pipeline Company LLC", iRunNumber, className);
			this.updateLEName("TRANSWESTERN PIPELINE COMPANY - LE", "TRANSWESTERN PIPELINE COMPANY, LLC - LE", "Energy Transfer Partners, L.P. DBA Transwestern Pipeline Company, LLC", iRunNumber, className);
			this.updateLEName("TRES PALACIOS GAS STORAGE LLC - LE", "TRESPAL - LE", "Tres Palacios Gas Storage LLC", iRunNumber, className);
			this.updateLEName("TENNESSEE VALLEY AUTHORITY - LE", "TVAL - LE", "Tennessee Valley Authority", iRunNumber, className);
			this.updateLEName("TGNR TVL LLC - LE", "TVLL3 - LE", "TGNR TVL LLC", iRunNumber, className);
			this.updateLEName("TWIN EAGLE RESOURCE MANAGEMENT LLC - LE", "TWIN EAGLE RESOURCE MANAGEMENT, LLC - LE", "Twin Eagle Holdings N.A., LLC DBA Twin Eagle Resource Management, LLC", iRunNumber, className);
			this.updateLEName("UNION ELECTRIC COMPANY DBA AMEREN MISSOURI - LE", "UECA - LE", "Union Electric Company dba Ameren Missouri", iRunNumber, className);
			this.updateLEName("UNIPER GLOBAL COMMODITIES NORTH AMERICA LLC - LE", "UNIPER GLOBAL COMMODITIES NORTH AMERICA LLC - LE", "Uniper Global Commodities North America LLC", iRunNumber, className);
			this.updateLEName("UNITED ENERGY TRADING LLC - LE", "UNITED ENERGY TRADING, LLC - LE", "United Energy Trading, LLC", iRunNumber, className);
			this.updateLEName("VALLEY CROSSING PIPELINE, LLC - LE", "VCPL - LE", "Valley Crossing Pipeline, LLC", iRunNumber, className);
			this.updateLEName("VECTOR PIPELINE LP - LE", "VECTOR PIPELINE LIMITED PARTNERSHIP - LE", "Vector Pipeline Limited Partnership", iRunNumber, className);
			this.updateLEName("VITOL INC - LE", "VITOL INC. - LE", "Vitol Inc.", iRunNumber, className);
			this.updateLEName("WASHINGTON 10 STORAGE CORPORATION - LE", "WASH - LE", "Washington 10 Storage Corporation", iRunNumber, className);
			this.updateLEName("WELLS FARGO COMMODITIES - LE", "WELL3 - LE", "Wells Fargo Commodities, LLC", iRunNumber, className);
			this.updateLEName("EVERGY KANSAS CENTRAL INC. DBA WESTAR ENERGY INC - LE", "WESTAR ENERGY INC - LE", "Evergy Kansas Central Inc. DBA Westar Energy Inc", iRunNumber, className);
			this.updateLEName("WGL MIDSTREAM, INC. - LE", "WGL MIDSTREAM, INC. - LE", "WGL Midstream, Inc.", iRunNumber, className);
			this.updateLEName("THE WILLIAMS COMPANIES, INC. DBA WILLIAMS ENERGY RESOURCES LLC - LE", "WILLIAMS ENERGY RESOURCES LLC - LE", "The Williams Companies, Inc. DBA Williams Energy Resources LLC", iRunNumber, className);
			this.updateLEName("WISCONSIN POWER AND LIGHT COMPANY - LE", "WISCONSIN POWER AND LIGHT COMPANY - LE", "Wisconsin Power and Light Company", iRunNumber, className);
			this.updateLEName("WORLD FUEL SERVICES INC - LE", "WORLD FUEL SERVICES, INC. - LE", "World Fuel Services, Inc.", iRunNumber, className);
			this.updateLEName("WORSHAM-STEED GAS STORAGE LLC - LE", "WORST - LE", "Worsham-Steed Storage LLC", iRunNumber, className);
			this.updateLEName("WESTMONT ENERGY LP - LE", "WSTMT - LE", "Westmont Energy LP", iRunNumber, className);
			this.updateLEName("WTG MIDSTREAM MARKETING LLC - LE", "WTG MIDSTREAM MARKETING, LLC - LE", "WTG Midstream Marketing, LLC", iRunNumber, className);
			this.updateLEName("WWM LOGISTICS, LLC - LE", "WWM LOGISTICS, LLC - LE", "WWM Logistics, LLC", iRunNumber, className);
			this.updateLEName("SOUTHWESTERN PUBLIC SERVICE COMPANY - LE", "XCEL ENERGY - LE", "Southwestern Public Service Company DBA Xcel Energy", iRunNumber, className);
			this.updateLEName("XTO ENERGY INC - LE", "XTO ENERGY INC. - LE", "XTO Energy Inc.", iRunNumber, className);
*/
			
			this.updateBUName("ANIG - BU", "ANGEL INVESTING GROUP LLLP - BU", iRunNumber, className);
			this.updateBUName("ARON - BU", "J ARON & COMPANY - BU", iRunNumber, className);
			this.updateBUName("ARUL3 - BU", "ASCENT RESOURCES - UTICA - BU", iRunNumber, className);
			this.updateBUName("BEAN3 - BU", "BE ANADARKO II - BU", iRunNumber, className);
			this.updateBUName("BLEN - BU", "BLUEGRASS ENERGY - BU", iRunNumber, className);
			this.updateBUName("BRMS - BU", "BLUE RACER MIDSTREAM - BU", iRunNumber, className);
			this.updateBUName("CENT - BU", "CENTURYLINK - BU", iRunNumber, className);
			this.updateBUName("CGTL - BU", "COLUMBIA GULF TRANSMISSION - BU", iRunNumber, className);
			this.updateBUName("CGTM - BU", "COLUMBIA GAS TRANSMISSION - BU", iRunNumber, className);
			this.updateBUName("CIEN - BU", "CIMA ENERGY LTD - BU", iRunNumber, className);
			this.updateBUName("COEC - BU", "COKINOS ENERGY CORPORATION - BU", iRunNumber, className);
			this.updateBUName("COGE - BU", "COGENCY GLOBAL - BU", iRunNumber, className);
			this.updateBUName("COPQ - BU", "COPEQ TRADING CO - BU", iRunNumber, className);
			this.updateBUName("CSTK3 - BU", "COMSTOCK OIL & GAS - BU", iRunNumber, className);
			this.updateBUName("DEBM - BU", "DIRECT ENERGY - BU", iRunNumber, className);
			this.updateBUName("DEML - BU", "DIVERSIFIED ENERGY MARKETING - BU", iRunNumber, className);
			this.updateBUName("EAST3 - BU", "TGNR EAST TEXAS - BU", iRunNumber, className);
			this.updateBUName("EEGL - BU", "ELEVATION ENERGY GROUP - BU", iRunNumber, className);
			this.updateBUName("ELOU3 - BU", "ENTERGY LOUISIANA - BU", iRunNumber, className);
			this.updateBUName("ENGE - BU", "ENGIE ENERGY MARKETING NA - BU", iRunNumber, className);
			this.updateBUName("ENSI - BU", "ENSIGN SERVICES - BU", iRunNumber, className);
			this.updateBUName("ENSIGHT - BU", "ENSIGHT IV ENERGY PARTNERS - BU", iRunNumber, className);
			this.updateBUName("ENTAR - BU", "ENTERGY ARKANSAS - BU", iRunNumber, className);
			this.updateBUName("ENTMS - BU", "ENTERGY MISSISSIPPI - BU", iRunNumber, className);
			this.updateBUName("ENTNO - BU", "ENTERGY NEW ORLEANS - BU", iRunNumber, className);
			this.updateBUName("ENTTX - BU", "ENTERGY TEXAS - BU", iRunNumber, className);
			this.updateBUName("EOXH - BU", "EOX HOLDINGS - BU", iRunNumber, className);
			this.updateBUName("ESGL - BU", "ESENTIA GAS - BU", iRunNumber, className);
			this.updateBUName("EXGC - BU", "EXELON GENERATION COMPANY - BU", iRunNumber, className);
			this.updateBUName("FGTC - BU", "FLORIDA GAS TRANSMISSION CO - BU", iRunNumber, className);
			this.updateBUName("FREE - BU", "FREEBIRD GAS STORAGE - BU", iRunNumber, className);
			this.updateBUName("GLDNT - BU", "GOLDEN TRIANGLE STORAGE - BU", iRunNumber, className);
			this.updateBUName("GLFRN - BU", "GULF RUN TRANSMISSION - BU", iRunNumber, className);
			this.updateBUName("GOOS3 - BU", "WILD GOOSE STORAGE - BU", iRunNumber, className);
			this.updateBUName("GPLNG - BU", "GOLDEN PASS LNG TERMINAL - BU", iRunNumber, className);
			this.updateBUName("GSPC - BU", "GULF SOUTH PIPELINE COMPANY - BU", iRunNumber, className);
			this.updateBUName("GUNV - BU", "GUNVOR USA - BU", iRunNumber, className);
			this.updateBUName("KAIM - BU", "KAISER MARKETING APPALACHIAN - BU", iRunNumber, className);
			this.updateBUName("LODI - BU", "LODI GAS STORAGE - BU", iRunNumber, className);
			this.updateBUName("MMPT - BU", "MAGELLAN PIPELINE COMPANY - BU", iRunNumber, className);
			this.updateBUName("MTCL - BU", "MILLENNIUM TRUST COMPANY - BU", iRunNumber, className);
			this.updateBUName("NEXTERA ENERGY MARKETING, LLC - BU", "NEXTERA ENERGY - BU", iRunNumber, className);
			this.updateBUName("NEXUS - BU", "NEXUS GAS TRANSMISSION - BU", iRunNumber, className);
			this.updateBUName("NGPC - BU", "NATURAL GAS PIPELINE CO - BU", iRunNumber, className);
			this.updateBUName("NGPX - BU", "NGP XI US HOLDINGS - BU", iRunNumber, className);
			this.updateBUName("NGX - BU", "ICE NGX CANADA - BU", iRunNumber, className);
			this.updateBUName("NSSA3 - BU", "NATURGY SERVICIOS - BU", iRunNumber, className);
			this.updateBUName("ONIX - BU", "ONIX SOLUTIONS LIMITED - BU", iRunNumber, className);
			this.updateBUName("PGAEC - BU", "PACIFIC GAS AND ELECTRIC CO - BU", iRunNumber, className);
			this.updateBUName("PVGS - BU", "PERRYVILLE GAS STORAGE - BU", iRunNumber, className);
			this.updateBUName("RANG - BU", "RANGE RESOURCES - APPALACHIA - BU", iRunNumber, className);
			this.updateBUName("SAAV3 - BU", "SAAVI ENERGY SOLUTIONS - BU", iRunNumber, className);
			this.updateBUName("SIBOW - BU", "SILVERBOW RESOURCES OPERATING - BU", iRunNumber, className);
			this.updateBUName("SMEC - BU", "SM ENERGY COMPANY - BU", iRunNumber, className);
			this.updateBUName("SYMM - BU", "SYMMETRY ENERGY SOLUTIONS - BU", iRunNumber, className);
			this.updateBUName("TGNR3 - BU", "TGNR NLA - BU", iRunNumber, className);
			this.updateBUName("TRAFM - BU", "TRAFIGURA MEXICO - BU", iRunNumber, className);
			this.updateBUName("TRESPAL - BU", "TRES PALACIOS GAS STORAGE - BU", iRunNumber, className);
			this.updateBUName("TVAL - BU", "TENNESSEE VALLEY AUTHORITY - BU", iRunNumber, className);
			this.updateBUName("TVLL3 - BU", "TGNR TVL - BU", iRunNumber, className);
			this.updateBUName("UECA - BU", "UNION ELECTRIC CO - BU", iRunNumber, className);
			this.updateBUName("VCPL - BU", "VALLEY CROSSING PIPELINE - BU", iRunNumber, className);
			this.updateBUName("WASH - BU", "WASHINGTON 10 STORAGE CORP - BU", iRunNumber, className);
			this.updateBUName("WELL3 - BU", "WELLS FARGO COMMODITIES - BU", iRunNumber, className);
			this.updateBUName("WORST - BU", "WORSHAM-STEED STORAGE - BU", iRunNumber, className);
			this.updateBUName("WSTMT - BU", "WESTMONT ENERGY - BU", iRunNumber, className);

			this.updateLEName("ANIG - LE", "ANGEL INVESTING GROUP LLLP - LE", iRunNumber, className);
			this.updateLEName("ARON - LE", "J ARON & COMPANY - LE", iRunNumber, className);
			this.updateLEName("ARUL3 - LE", "ASCENT RESOURCES - UTICA - LE", iRunNumber, className);
			this.updateLEName("BEAN3 - LE", "BE ANADARKO II - LE", iRunNumber, className);
			this.updateLEName("BLEN - LE", "BLUEGRASS ENERGY - LE", iRunNumber, className);
			this.updateLEName("BRMS - LE", "BLUE RACER MIDSTREAM - LE", iRunNumber, className);
			this.updateLEName("CENT - LE", "CENTURYLINK - LE", iRunNumber, className);
			this.updateLEName("CGTL - LE", "COLUMBIA GULF TRANSMISSION - LE", iRunNumber, className);
			this.updateLEName("CGTM - LE", "COLUMBIA GAS TRANSMISSION - LE", iRunNumber, className);
			this.updateLEName("CIEN - LE", "CIMA ENERGY LTD - LE", iRunNumber, className);
			this.updateLEName("COEC - LE", "COKINOS ENERGY CORPORATION - LE", iRunNumber, className);
			this.updateLEName("COGE - LE", "COGENCY GLOBAL - LE", iRunNumber, className);
			this.updateLEName("COPQ - LE", "COPEQ TRADING CO - LE", iRunNumber, className);
			this.updateLEName("CSTK3 - LE", "COMSTOCK OIL & GAS - LE", iRunNumber, className);
			this.updateLEName("DEBM - LE", "DIRECT ENERGY - LE", iRunNumber, className);
			this.updateLEName("DEML - LE", "DIVERSIFIED ENERGY MARKETING - LE", iRunNumber, className);
			this.updateLEName("EAST3 - LE", "TGNR EAST TEXAS - LE", iRunNumber, className);
			this.updateLEName("EEGL - LE", "ELEVATION ENERGY GROUP - LE", iRunNumber, className);
			this.updateLEName("ELOU3 - LE", "ENTERGY LOUISIANA - LE", iRunNumber, className);
			this.updateLEName("ENGE - LE", "ENGIE ENERGY MARKETING NA - LE", iRunNumber, className);
			this.updateLEName("ENSI - LE", "ENSIGN SERVICES - LE", iRunNumber, className);
			this.updateLEName("ENSIGHT - LE", "ENSIGHT IV ENERGY PARTNERS - LE", iRunNumber, className);
			this.updateLEName("ENTAR - LE", "ENTERGY ARKANSAS - LE", iRunNumber, className);
			this.updateLEName("ENTMS - LE", "ENTERGY MISSISSIPPI - LE", iRunNumber, className);
			this.updateLEName("ENTNO - LE", "ENTERGY NEW ORLEANS - LE", iRunNumber, className);
			this.updateLEName("ENTTX - LE", "ENTERGY TEXAS - LE", iRunNumber, className);
			this.updateLEName("EOXH - LE", "EOX HOLDINGS - LE", iRunNumber, className);
			this.updateLEName("ESGL - LE", "ESENTIA GAS - LE", iRunNumber, className);
			this.updateLEName("EXGC - LE", "EXELON GENERATION COMPANY - LE", iRunNumber, className);
			this.updateLEName("FGTC - LE", "FLORIDA GAS TRANSMISSION CO - LE", iRunNumber, className);
			this.updateLEName("FREE - LE", "FREEBIRD GAS STORAGE - LE", iRunNumber, className);
			this.updateLEName("GLDNT - LE", "GOLDEN TRIANGLE STORAGE - LE", iRunNumber, className);
			this.updateLEName("GLFRN - LE", "GULF RUN TRANSMISSION - LE", iRunNumber, className);
			this.updateLEName("GOOS3 - LE", "WILD GOOSE STORAGE - LE", iRunNumber, className);
			this.updateLEName("GPLNG - LE", "GOLDEN PASS LNG TERMINAL - LE", iRunNumber, className);
			this.updateLEName("GSPC - LE", "GULF SOUTH PIPELINE COMPANY - LE", iRunNumber, className);
			this.updateLEName("GUNV - LE", "GUNVOR USA - LE", iRunNumber, className);
			this.updateLEName("KAIM - LE", "KAISER MARKETING APPALACHIAN - LE", iRunNumber, className);
			this.updateLEName("LODI - LE", "LODI GAS STORAGE - LE", iRunNumber, className);
			this.updateLEName("MMPT - LE", "MAGELLAN PIPELINE COMPANY - LE", iRunNumber, className);
			this.updateLEName("MTCL - LE", "MILLENNIUM TRUST COMPANY - LE", iRunNumber, className);
			this.updateLEName("NEXTERA ENERGY MARKETING, LLC - LE", "NEXTERA ENERGY - LE", iRunNumber, className);
			this.updateLEName("NEXUS - LE", "NEXUS GAS TRANSMISSION - LE", iRunNumber, className);
			this.updateLEName("NGPC - LE", "NATURAL GAS PIPELINE CO - LE", iRunNumber, className);
			this.updateLEName("NGPX - LE", "NGP XI US HOLDINGS - LE", iRunNumber, className);
			this.updateLEName("NGX - LE", "ICE NGX CANADA - LE", iRunNumber, className);
			this.updateLEName("NSSA3 - LE", "NATURGY SERVICIOS - LE", iRunNumber, className);
			this.updateLEName("ONIX - LE", "ONIX SOLUTIONS LIMITED - LE", iRunNumber, className);
			this.updateLEName("PGAEC - LE", "PACIFIC GAS AND ELECTRIC CO - LE", iRunNumber, className);
			this.updateLEName("PVGS - LE", "PERRYVILLE GAS STORAGE - LE", iRunNumber, className);
			this.updateLEName("RANG - LE", "RANGE RESOURCES - APPALACHIA - LE", iRunNumber, className);
			this.updateLEName("SAAV3 - LE", "SAAVI ENERGY SOLUTIONS - LE", iRunNumber, className);
			this.updateLEName("SIBOW - LE", "SILVERBOW RESOURCES OPERATING - LE", iRunNumber, className);
			this.updateLEName("SMEC - LE", "SM ENERGY COMPANY - LE", iRunNumber, className);
			this.updateLEName("SYMM - LE", "SYMMETRY ENERGY SOLUTIONS - LE", iRunNumber, className);
			this.updateLEName("TGNR3 - LE", "TGNR NLA - LE", iRunNumber, className);
			this.updateLEName("TRAFM - LE", "TRAFIGURA MEXICO - LE", iRunNumber, className);
			this.updateLEName("TRESPAL - LE", "TRES PALACIOS GAS STORAGE - LE", iRunNumber, className);
			this.updateLEName("TVAL - LE", "TENNESSEE VALLEY AUTHORITY - LE", iRunNumber, className);
			this.updateLEName("TVLL3 - LE", "TGNR TVL - LE", iRunNumber, className);
			this.updateLEName("UECA - LE", "UNION ELECTRIC CO - LE", iRunNumber, className);
			this.updateLEName("VCPL - LE", "VALLEY CROSSING PIPELINE - LE", iRunNumber, className);
			this.updateLEName("WASH - LE", "WASHINGTON 10 STORAGE CORP - LE", iRunNumber, className);
			this.updateLEName("WELL3 - LE", "WELLS FARGO COMMODITIES - LE", iRunNumber, className);
			this.updateLEName("WORST - LE", "WORSHAM-STEED STORAGE - LE", iRunNumber, className);
			this.updateLEName("WSTMT - LE", "WESTMONT ENERGY - LE", iRunNumber, className);

			
		} catch (Exception e) {
			// do not log this
		}
	}	

	public  void updateLEName(String sOldName, String sNewName, int iRunNumber, String className) throws OException {
		FUNC.updateLEName(sOldName, sNewName,  iRunNumber, className);
	}
	public  void updateBUName(String sOldName, String sNewName,  int iRunNumber, String className) throws OException {
		FUNC.updateBUName(sOldName, sNewName, iRunNumber, className);
	}
		
	public static class FUNC {
 		
		public static void updateLEName(String sOldName, String sNewName, int iRunNumber, String className) throws OException {
			try {
				int iLegalEntityID = Ref.getValue(SHM_USR_TABLES_ENUM.PARTY_TABLE, sOldName);

				if (iLegalEntityID < 1) {			
					LIB.log("*ERROR*:  Party ID not found for Legal Entity: " + "'" + sOldName + "'", className);
				}
				
				if (iLegalEntityID > 0) {
					LIB.log("Running for Legal Entity: " + "'" + sOldName + "'" + ", with Party ID: " + iLegalEntityID , className);
					{
						int party_class = PARTY_TYPES.LEGAL_ENTITY;											

						if (iLegalEntityID >= 1) {
							Table tPartyInsert = Ref.retrieveParty(iLegalEntityID); 
							//LIB.viewTable(tPartyInsert);
							
							{
								try {
									tPartyInsert.setString("short_name", 1, sNewName);
									//tPartyInsert.setString("long_name", 1, sNewLongName);
								} catch (Exception e) {
									LIB.log("adding/setting Party Info for type id: ", e, className);
								}	
							}					
							
							try {
							//	int retval = 0;
							 int	  retval = Ref.updateParty(tPartyInsert);
							 
								LIB.log("Ref.updateParty retval: " + retval, className);
							} catch (Exception e) {
								LIB.log("Ref.updateParty", e, className);
							}
							
							if (bExtraDebug == true) {
								LIB.viewTable(tPartyInsert, "LE Retrieved party Table");
							}
						}		 
					}
				} // END 						
			} catch (Exception e) {
				LIB.log("createPartyGroupAndBUandLE", e, className);
			}			 
		}
		
		public static void updateBUName(String sOldName, String sNewName, int iRunNumber, String className) throws OException {
			try {
				int iBusinessUnitID = Ref.getValue(SHM_USR_TABLES_ENUM.PARTY_TABLE, sOldName);

				if (iBusinessUnitID < 1) {			
					LIB.log("*ERROR*:  Party ID not found for Business Unit: " + "'" + sOldName + "'", className);
				}
				
				if (iBusinessUnitID > 0) {
					LIB.log("Running for Business Unit: " + "'" + sOldName + "'" + ", with Party ID: " + iBusinessUnitID , className);
					{
						int party_class = PARTY_TYPES.BUSINESS_UNIT;											

						if (iBusinessUnitID >= 1) {
							Table tPartyInsert = Ref.retrieveParty(iBusinessUnitID); 
							//LIB.viewTable(tPartyInsert);
							
							{
								try {
									tPartyInsert.setString("short_name", 1, sNewName);
									//tPartyInsert.setString("long_name", 1, sNewLongName);
								} catch (Exception e) {
									LIB.log("adding/setting Party Info for type id: ", e, className);
								}	
							}					
							
							try {
							//	int retval = 0;
							 int	  retval = Ref.updateParty(tPartyInsert);
							 
								LIB.log("Ref.updateParty retval: " + retval, className);
							} catch (Exception e) {
								LIB.log("Ref.updateParty", e, className);
							}
							
							if (bExtraDebug == true) {
								LIB.viewTable(tPartyInsert, "BU Retrieved party Table");
							}
						}		 
					}
				} // END 						
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
	
	public static class PARTY_TYPES{
		public static final int LEGAL_ENTITY = 0;
		public static final int BUSINESS_UNIT = 1;
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

	public static final String VERSION_NUMBER = "V1.003 (08JUN2023)";

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
