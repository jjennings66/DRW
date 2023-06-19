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
24-May-2023   Brian   New Script

*/

@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_GENERIC)
@PluginType(SCRIPT_TYPE_ENUM.MAIN_SCRIPT)
public class UTIL_UpdatePartyInfo implements IScript {
	 
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
			

			// false is no table viewer
			bExtraDebug = false;
			/*this.updateIntLEPartyInfo("DRW ENERGY TRADING LLC", "DRW Energy Trading LLC", 10276, iRunNumber, className);


			this.updateIntBUPartyInfo("NATURAL GAS NORTH AMERICA", "Natural Gas North America", 10529, iRunNumber, className);
			this.updateIntBUPartyInfo("NATURAL GAS NW BASIS", "Natural Gas NW Basis", 10541, iRunNumber, className);
			this.updateIntBUPartyInfo("NATURAL GAS MIDCON BASIS", "Natural Gas Midcon Basis", 10549, iRunNumber, className);
			this.updateIntBUPartyInfo("NATURAL GAS MIDWEST", "Natural Gas Midwest", 10497, iRunNumber, className);

			this.updateExtLEPartyInfo("CONOCOPHILLIPS COMPANY - LE", "ConocoPhillips Company", 10394, iRunNumber, className);
			this.updateExtLEPartyInfo("DTE ENERGY TRADING INC - LE", "DTE Energy Trading, Inc.", 10384, iRunNumber, className);
			this.updateExtLEPartyInfo("ECO-ENERGY NATURAL GAS LLC - LE", "Copersucar North America, LLC DBA Eco-Energy Natural Gas, LLC", 10416, iRunNumber, className);
			this.updateExtLEPartyInfo("ELEVATION ENERGY GROUP, LLC - LE", "Elevation Energy Group, LLC", 10480, iRunNumber, className);
			this.updateExtLEPartyInfo("EXELON GENERATION COMPANY, LLC - LE", "Exelon Generation Company, LLC", 10404, iRunNumber, className);
			this.updateExtLEPartyInfo("HARTREE PARTNERS LP - LE", "Hartree Partners, LP", 10417, iRunNumber, className);
			this.updateExtLEPartyInfo("KOCH ENERGY SERVICES LLC - LE", "Kaes Domestic Holdings, LLC DBA Koch Energy Services, LLC", 10396, iRunNumber, className);
			this.updateExtLEPartyInfo("MARATHON PETROLEUM COMPANY LP - LE", "Marathon Petroleum Company LP", 10419, iRunNumber, className);
			this.updateExtLEPartyInfo("MERCURIA ENERGY AMERICA LLC - LE", "Mercuria Energy Company LLC DBA Mercuria Energy America, LLC", 10397, iRunNumber, className);
			this.updateExtLEPartyInfo("MIECO LLC - LE", "Mieco LLC", 10405, iRunNumber, className);
			this.updateExtLEPartyInfo("NATURAL GAS PIPELINE COMPANY OF AMERICA - LE", "Natural Gas Pipeline Company of America", 10379, iRunNumber, className);
			this.updateExtLEPartyInfo("SEQUENT ENERGY MANAGEMENT LLC - LE", "Sequent Energy Management, L.P.", 10470, iRunNumber, className);
			this.updateExtLEPartyInfo("SPOTLIGHT ENERGY LLC - LE", "Spotlight Energy, LLC", 10449, iRunNumber, className);
			this.updateExtLEPartyInfo("UNIPER GLOBAL COMMODITIES NORTH AMERICA LLC - LE", "Uniper Global Commodities North America LLC", 10411, iRunNumber, className);
			this.updateExtLEPartyInfo("VITOL INC - LE", "Vitol Inc.", 10390, iRunNumber, className);
			this.updateExtLEPartyInfo("XTO ENERGY INC - LE", "XTO Energy Inc.", 10486, iRunNumber, className);
			this.updateExtLEPartyInfo("ARM ENERGY MANAGEMENT LLC - LE", "ARM Energy Management LLC", 10412, iRunNumber, className);
			this.updateExtLEPartyInfo("EQT ENERGY LLC - LE", "EQT Production Company DBA EQT Energy, LLC", 10453, iRunNumber, className);
			this.updateExtLEPartyInfo("ALLIANCE PIPELINE - LE", "Alliance Pipeline L.P.", 10487, iRunNumber, className);
			this.updateExtLEPartyInfo("ANADARKO ENERGY SERVICES COMPANY - LE", "Anadarko Energy Services Company", 10488, iRunNumber, className);
			this.updateExtLEPartyInfo("ANCOVA ENERGY MARKETING, LLC - LE", "Ancova Energy Marketing, LLC", 10489, iRunNumber, className);
			this.updateExtLEPartyInfo("ANGEL INVESTING GROUP, LLLP - LE", "ANGEL INVESTING GROUP, LLLP", 10490, iRunNumber, className);
			this.updateExtLEPartyInfo("ANR PIPELINE COMPANY - LE", "ANR Pipeline Company", 10491, iRunNumber, className);
			this.updateExtLEPartyInfo("ASSOCIATED ELECTRIC COOPERATIVE INC - LE", "Associated Electric Cooperative Inc", 10492, iRunNumber, className);
			this.updateExtLEPartyInfo("BLACKBEARD OPERATING, LLC - LE", "Blackbeard Operating, LLC", 10493, iRunNumber, className);
			this.updateExtLEPartyInfo("BLUE MOUNTAIN MIDSTREAM LLC - LE", "Blue Mountain Midstream LLC", 10494, iRunNumber, className);
			this.updateExtLEPartyInfo("BLUEGRASS ENERGY - LE", "BLUEGRASS ENERGY", 10495, iRunNumber, className);
			this.updateExtLEPartyInfo("BP ENERGY COMPANY - LE", "BP Energy Company", 10496, iRunNumber, className);
			this.updateExtLEPartyInfo("CENTURYLINK - LE", "CENTURYLINK", 10497, iRunNumber, className);
			this.updateExtLEPartyInfo("CFE INTERNATIONAL LLC - LE", "CFE International LLC", 10498, iRunNumber, className);
			this.updateExtLEPartyInfo("CHEVRON NATURAL GAS - LE", "Chevron U.S.A. Inc. DBA Chevron Natural Gas, a division of Chevron U.S.A. Inc.", 10499, iRunNumber, className);
			this.updateExtLEPartyInfo("CIMA ENERGY LP - LE", "CIMA ENERGY, LTD", 10500, iRunNumber, className);
			this.updateExtLEPartyInfo("CITADEL ENERGY MARKETING LLC - LE", "Citadel Energy Marketing LLC", 10501, iRunNumber, className);
			this.updateExtLEPartyInfo("CITY UTILITIES OF SPRINGFIELD, MISSOURI - LE", "City Utilities of Springfield, Missouri", 10502, iRunNumber, className);
			this.updateExtLEPartyInfo("CLEARWATER ENTERPRISES LLC - LE", "Clearwater Enterprises, L.L.C.", 10503, iRunNumber, className);
			this.updateExtLEPartyInfo("COGENCY GLOBAL - LE", "COGENCY GLOBAL", 10504, iRunNumber, className);
			this.updateExtLEPartyInfo("CONCORD ENERGY LLC - LE", "Concord Energy LLC", 10505, iRunNumber, className);
			this.updateExtLEPartyInfo("CONSUMERS ENERGY COMPANY - LE", "Consumers Energy Company", 10506, iRunNumber, className);
			this.updateExtLEPartyInfo("DCP MIDSTREAM MARKETING LLC - LE", "DCP Midstream, LP DBA DCP Midstream Marketing, LLC", 10507, iRunNumber, className);
			this.updateExtLEPartyInfo("DTE GAS COMPANY - LE", "DTE Gas Company", 10508, iRunNumber, className);
			this.updateExtLEPartyInfo("EDF TRADING NORTH AMERICA LLC - LE", "EDF Trading North America, LLC", 10509, iRunNumber, className);
			this.updateExtLEPartyInfo("ELEMENT MARKETS, LLC DBA ELEMENT MARKETS RENEWABLE ENERGY, LLC - LE", "Element Markets, LLC DBA Element Markets Renewable Energy, LLC", 10510, iRunNumber, className);
			this.updateExtLEPartyInfo("EMERA ENERGY SERVICES INC - LE", "Emera Energy Services, Inc.", 10511, iRunNumber, className);
			this.updateExtLEPartyInfo("ENABLE ENERGY RESOURCES, LLC - LE", "Enable Midstream Partners, LP DBA Enable Energy Resources, LLC", 10512, iRunNumber, className);
			this.updateExtLEPartyInfo("ENABLE GAS TRANSMISSION LLC - LE", "Enable Midstream Partners, LP DBA Enable Gas Transmission, LLC", 10513, iRunNumber, className);
			this.updateExtLEPartyInfo("ENABLE OKLAHOMA INTRASTATE TRANSMISSION LLC - LE", "Enable Midstream Partners, LP DBA Enable Oklahoma Intrastate Transmission, LLC", 10514, iRunNumber, className);
			this.updateExtLEPartyInfo("ENSIGN SERVICES, INC. - LE", "ENSIGN SERVICES, INC.", 10515, iRunNumber, className);
			this.updateExtLEPartyInfo("EOG RESOURCES INC - LE", "EOG Resources, Inc.", 10516, iRunNumber, className);
			this.updateExtLEPartyInfo("EOX HOLDINGS, LLC - LE", "EOX HOLDINGS, LLC", 10517, iRunNumber, className);
			this.updateExtLEPartyInfo("ETC MARKETING LTD - LE", "Energy Transfer LP DBA ETC Marketing, LTD.", 10518, iRunNumber, className);
			this.updateExtLEPartyInfo("EVERGY KANSAS CENTRAL INC. DBA WESTAR ENERGY INC - LE", "Evergy Kansas Central Inc. DBA Westar Energy Inc", 10519, iRunNumber, className);
			this.updateExtLEPartyInfo("ICBC STANDARD BANK PLC - LE", "ICBC Standard Bank PLC", 10520, iRunNumber, className);

			this.updateExtLEPartyInfo("K2 COMMODITIES LLC - LE", "K2 Commodities, LLC", 10522, iRunNumber, className);
			this.updateExtLEPartyInfo("LUMINANT ENERGY COMPANY LLC - LE", "Luminant Energy Company LLC", 10523, iRunNumber, className);

			this.updateExtLEPartyInfo("MIDAMERICAN ENERGY COMPANY - LE", "MidAmerican Energy Company", 10525, iRunNumber, className);
			this.updateExtLEPartyInfo("MILLENNIUM TRUST COMPANY, LLC - LE", "MILLENNIUM TRUST COMPANY, LLC", 10526, iRunNumber, className);
			this.updateExtLEPartyInfo("MORGAN STANLEY CAPITAL GROUP INC. - LE", "Morgan Stanley Capital Group Inc.", 10527, iRunNumber, className);
			this.updateExtLEPartyInfo("NGP XI US HOLDINGS LP - LE", "NGP XI US HOLDINGS LP", 10528, iRunNumber, className);
			this.updateExtLEPartyInfo("NORTHERN ILLINOIS GAS COMPANY - LE", "Northern Illinois Gas Company DBA Nicor Gas Co", 10529, iRunNumber, className);
			this.updateExtLEPartyInfo("NORTHERN INDIANA PUBLIC SERVICE COMPANY LLC - LE", "Northern Indiana Public Service Company LLC", 10530, iRunNumber, className);
			this.updateExtLEPartyInfo("NORTHERN NATURAL GAS COMPANY - LE", "Northern Natural Gas Company", 10531, iRunNumber, className);
			this.updateExtLEPartyInfo("OASIS PIPELINE LP - LE", "Energy Transfer Partners, L.P. DBA Oasis Pipeline, LP", 10532, iRunNumber, className);
			this.updateExtLEPartyInfo("OKLAHOMA GAS & ELECTRIC COMPANY - LE", "Oklahoma Gas & Electric Company", 10533, iRunNumber, className);
			this.updateExtLEPartyInfo("ONEOK FIELD SERVICES COMPANY LLC - LE", "Oneok Partners Intermediate Limited Partnership DBA ONEOK Field Services Company, L.L.C.", 10534, iRunNumber, className);
			this.updateExtLEPartyInfo("ONEOK GAS TRANSPORATION LLC - LE", "Oneok Gas Transporation, L.L.C.", 10535, iRunNumber, className);
			this.updateExtLEPartyInfo("ONEOK WESTEX TRANSMISSION LLC - LE", "Oneok Partners Intermediate Limited Partnership DBA ONEOK WesTex Transmission, L.L.C.", 10536, iRunNumber, className);
			this.updateExtLEPartyInfo("ONIX SOLUTIONS LIMITED - LE", "ONIX SOLUTIONS LIMITED", 10537, iRunNumber, className);
			this.updateExtLEPartyInfo("OVINTIV MARKETING INC - LE", "Ovintiv Marketing Inc.", 10538, iRunNumber, className);
			this.updateExtLEPartyInfo("PANHANDLE EASTERN PIPE LINE COMPANY LP - LE", "Energy Transfer LP DBA Panhandle Eastern Pipe Line Company, LP", 10539, iRunNumber, className);
			this.updateExtLEPartyInfo("PRESIDIO INVESTMENT HOLDINGS LLC DBA PRESIDIO PETROLEUM LLC - LE", "Presidio Investment Holdings LLC DBA Presidio Petroleum LLC", 10540, iRunNumber, className);
			this.updateExtLEPartyInfo("PUBLIC SERVICE COMPANY OF NEW MEXICO - LE", "Public Service Company of New Mexico", 10541, iRunNumber, className);
			this.updateExtLEPartyInfo("SHELL ENERGY NORTH AMERICA (US) LP - LE", "Shell Energy North America (US), L.P.", 10542, iRunNumber, className);
			this.updateExtLEPartyInfo("SOUTHERN STAR CENTRAL GAS PIPELINE INC - LE", "Southern Star Central Gas Pipeline, Inc.", 10543, iRunNumber, className);
			this.updateExtLEPartyInfo("SOUTHWEST ENERGY LP - LE", "Southwest Energy, L.P.", 10544, iRunNumber, className);

			this.updateExtLEPartyInfo("SPIRE MARKETING INC - LE", "Spire Marketing Inc.", 10546, iRunNumber, className);
			this.updateExtLEPartyInfo("SPIRE MISSOURI INC. - LE", "Spire Missouri Inc.", 10547, iRunNumber, className);
			this.updateExtLEPartyInfo("SUPERIOR PIPELINE COMPANY, LLC DBA SUPERIOR PIPELINE TEXAS, LLC - LE", "Superior Pipeline Company, LLC DBA Superior Pipeline Texas, LLC", 10548, iRunNumber, className);
			this.updateExtLEPartyInfo("TAPSTONE ENERGY, LLC DBA TAPSTONE SELLER LLC - LE", "Tapstone Energy, LLC DBA Tapstone Seller LLC", 10549, iRunNumber, className);
			this.updateExtLEPartyInfo("TARGA GAS MARKETING LLC - LE", "Targa Midstream Services LLC DBA Targa Gas Marketing LLC", 10550, iRunNumber, className);
			this.updateExtLEPartyInfo("THE EMPIRE DISTRICT ELECTRIC COMPANY - LE", "The Empire District Electric Company", 10551, iRunNumber, className);
			this.updateExtLEPartyInfo("THE ENERGY AUTHORITY INC - LE", "The Energy Authority, Inc", 10552, iRunNumber, className);
			this.updateExtLEPartyInfo("TRANSWESTERN PIPELINE COMPANY - LE", "Energy Transfer Partners, L.P. DBA Transwestern Pipeline Company, LLC", 10553, iRunNumber, className);
			this.updateExtLEPartyInfo("TWIN EAGLE RESOURCE MANAGEMENT LLC - LE", "Twin Eagle Holdings N.A., LLC DBA Twin Eagle Resource Management, LLC", 10554, iRunNumber, className);
			this.updateExtLEPartyInfo("WGL MIDSTREAM, INC. - LE", "WGL Midstream, Inc.", 10555, iRunNumber, className);
			this.updateExtLEPartyInfo("THE WILLIAMS COMPANIES, INC. DBA WILLIAMS ENERGY RESOURCES LLC - LE", "The Williams Companies, Inc. DBA Williams Energy Resources LLC", 10556, iRunNumber, className);
			this.updateExtLEPartyInfo("WISCONSIN POWER AND LIGHT COMPANY - LE", "Wisconsin Power and Light Company", 10557, iRunNumber, className);
			this.updateExtLEPartyInfo("WORLD FUEL SERVICES INC - LE", "World Fuel Services, Inc.", 10558, iRunNumber, className);
			this.updateExtLEPartyInfo("WTG MIDSTREAM MARKETING LLC - LE", "WTG Midstream Marketing, LLC", 10559, iRunNumber, className);
			this.updateExtLEPartyInfo("CASTLETON - LE", "Castleton Global Trading LLC DBA Castleton Commodities Merchant Trading L.P.", 10562, iRunNumber, className);
			this.updateExtLEPartyInfo("GREAT LAKES GAS TRANSMISSION - LE", "Great Lakes Gas Transmission Limited Partnership", 10563, iRunNumber, className);
			this.updateExtLEPartyInfo("ICE NGX CANADA INC - LE", "ICE NGX Canada Inc", 10382, iRunNumber, className);
			this.updateExtLEPartyInfo("MARKWEST PIONEER LLC - LE", "MarkWest Pioneer, LLC", 10565, iRunNumber, className);
			this.updateExtLEPartyInfo("ENTERPRISE TEXAS PIPELINE LLC - LE", "Enterprise Texas Pipeline LLC", 10569, iRunNumber, className);
			this.updateExtLEPartyInfo("ONE GAS, INC. DBA OKLAHOMA NATURAL GAS - LE", "ONE Gas, Inc. DBA Oklahoma Natural Gas", 10566, iRunNumber, className);
			this.updateExtLEPartyInfo("ONEOK GAS STORAGE LLC - LE", "Oneok Partners Intermediate Limited Partnership DBA ONEOK Gas Storage, L.L.C.", 10567, iRunNumber, className);
			this.updateExtLEPartyInfo("WASHINGTON 10 STORAGE CORPORATION - LE", "Washington 10 Storage Corporation", 10568, iRunNumber, className);
			this.updateExtLEPartyInfo("VECTOR PIPELINE LP - LE", "Vector Pipeline Limited Partnership", 10581, iRunNumber, className);
			this.updateExtLEPartyInfo("NEXTERA ENERGY MARKETING LLC - LE", "NextEra Energy Capital Holdings, Inc. DBA NextEra Energy Marketing, LLC", 10395, iRunNumber, className);
			this.updateExtLEPartyInfo("PUBLIC SERVICE COMPANY OF OKLAHOMA DBA AEP PSO - LE", "Public Service Company of Oklahoma DBA AEP PSO", 10582, iRunNumber, className);
			this.updateExtLEPartyInfo("SYMMETRY ENERGY SOLUTIONS LLC - LE", "Symmetry Energy Solutions, LLC", 10436, iRunNumber, className);
			this.updateExtLEPartyInfo("TEXLA ENERGY MANAGEMENT INC - LE", "Texla Energy Management, Inc.", 10444, iRunNumber, className);
			this.updateExtLEPartyInfo("ENTERPRISE PRODUCTS OPERATING LLC - LE", "Enterprise Products Operating LLC", 10583, iRunNumber, className);
			this.updateExtLEPartyInfo("CARBONBETTER LLC - LE", "Carbonbetter, LLC", 10587, iRunNumber, className);
			this.updateExtLEPartyInfo("COLORADO SPRINGS UTILITIES - LE", "Colorado Springs Utilities", 10588, iRunNumber, className);
			this.updateExtLEPartyInfo("MANSFIELD POWER & GAS LLC - LE", "Mansfield Energy Corp DBA Mansfield Power and Gas, LLC", 10455, iRunNumber, className);
			this.updateExtLEPartyInfo("CHESAPEAKE ENERGY MARKETING LLC - LE", "Chesapeake Energy Corporation DBA Chesapeake Energy Marketing L.L.C.", 10611, iRunNumber, className);
			this.updateExtLEPartyInfo("CONTINENTAL RESOURCES INC - LE", "Continental Resources, Inc.", 10614, iRunNumber, className);
			this.updateExtLEPartyInfo("DXT COMMODITIES NORTH AMERICA INC - LE", "DXT Commodities North America Inc.", 10619, iRunNumber, className);
			this.updateExtLEPartyInfo("INTERSTATE GAS SUPPLY LLC - LE", "Interstate Gas Supply, Inc.", 10634, iRunNumber, className);
			this.updateExtLEPartyInfo("BP CANADA ENERGY MARKETING CORP - LE", "BP Canada Energy Marketing Corp", 10608, iRunNumber, className);
			this.updateExtLEPartyInfo("LEGACY RESERVES ENERGY SERVICES LLC - LE", "Legacy Reserves Energy Services LLC", 10695, iRunNumber, className);
			this.updateExtLEPartyInfo("UNITED ENERGY TRADING LLC - LE", "United Energy Trading, LLC", 10443, iRunNumber, className);
			this.updateExtLEPartyInfo("DEVON GAS SERVICES, L.P. - LE", "Devon Gas Services, L.P.", 10618, iRunNumber, className);

			this.updateExtLEPartyInfo("EVERGY MISSOURI WEST INC DBA KCP&L GREATER MISSOURI OPERATIONS COMPANY - LE", "Evergy Missouri West Inc DBA KCP&L Greater Missouri Operations Company", 10626, iRunNumber, className);
			this.updateExtLEPartyInfo("GUNVOR USA LLC - LE", "Gunvor USA LLC", 10427, iRunNumber, className);
			this.updateExtLEPartyInfo("PINE PRAIRIE ENERGY CENTER LLC - LE", "Pine Prairie Energy Center, LLC", 10705, iRunNumber, className);
			this.updateExtLEPartyInfo("TC ENERGY MARKETING INC - LE", "TC Energy Marketing Inc.", 10679, iRunNumber, className);
			this.updateExtLEPartyInfo("CONSTELLATION ENERGY GENERATION LLC - LE", "Constellation Energy Generation, LLC", 10710, iRunNumber, className);
			this.updateExtLEPartyInfo("OCCIDENTAL ENERGY MARKETING INC - LE", "Occidental Energy Marketing, Inc.", 10660, iRunNumber, className);
			this.updateExtLEPartyInfo("SIX ONE COMMODITIES LLC - LE", "Six One Commodities LLC", 10408, iRunNumber, className);
			this.updateExtLEPartyInfo("AGUA BLANCA PIPELINE - LE", "Agua Blanca, LLC", 10713, iRunNumber, className);
			this.updateExtLEPartyInfo("DK TRADING & SUPPLY LLC - LE", "Delek US Energy, Inc DBA DK Trading & Supply, LLC", 10617, iRunNumber, className);
			this.updateExtLEPartyInfo("PIONEER NATURAL RESOURCES USA, INC. - LE", "Pioneer Natural Resources USA, Inc.", 10666, iRunNumber, className);
			this.updateExtLEPartyInfo("NORTHERN STATES POWER COMPANY - LE", "Northern States Power Company, Minnesota", 10719, iRunNumber, className);
			this.updateExtLEPartyInfo("REPSOL ENERGY NORTH AMERICA CORPORATION - LE", "Repsol Energy North America Corporation", 10402, iRunNumber, className);
			this.updateExtLEPartyInfo("ANR PIPELINE COMPANY - LE", "ANR Pipeline Company", 10491, iRunNumber, className);
			this.updateExtLEPartyInfo("WASHINGTON 10 STORAGE CORPORATION - LE", "Washington 10 Storage Corporation", 10568, iRunNumber, className);
			this.updateExtLEPartyInfo("NATURAL GAS PIPELINE COMPANY OF AMERICA - LE", "Natural Gas Pipeline Company of America", 10379, iRunNumber, className);
			this.updateExtLEPartyInfo("TOURMALINE OIL MARKETING CORP. - LE", "Tourmaline Oil Marketing Corp.", 10696, iRunNumber, className);
			this.updateExtLEPartyInfo("TRANSCONTINENTAL GAS PIPE LINE COMPANY LLC - LE", "Transcontinental Gas Pipeline Company LLC", 10723, iRunNumber, className);
			this.updateExtLEPartyInfo("ENLINK MIDSTREAM OPERATING, LP DBA ENLINK GAS MARKETING, LP - LE", "EnLink Midstream Operating, LP DBA EnLink Gas Marketing, LP", 10621, iRunNumber, className);
			this.updateExtLEPartyInfo("ARKANSAS ELECTRIC COOPERATIVE CORPORATION - LE", "Arkansas Electric Cooperative Corporation", 10599, iRunNumber, className);
			this.updateExtLEPartyInfo("RADIATE ENERGY LLC - LE", "Radiate Energy LLC", 10702, iRunNumber, className);
			this.updateExtLEPartyInfo("MADISON GAS & ELECTRIC - LE", "Madison Gas & Electric", 10744, iRunNumber, className);
			this.updateExtLEPartyInfo("WWM LOGISTICS, LLC - LE", "WWM Logistics, LLC", 10691, iRunNumber, className);
			this.updateExtLEPartyInfo("DIRECT ENERGY BUSINESS MARKETING LLC - LE", "Direct Energy Business Marketing, LLC", 10398, iRunNumber, className);
			this.updateExtLEPartyInfo("GULF SOUTH PIPELINE COMPANY - LE", "Gulf South Pipeline Company, LLC", 10471, iRunNumber, className);
			this.updateExtLEPartyInfo("PERRYVILLE GAS STORAGE LLC - LE", "Perryville Gas Storage, LLC", 10373, iRunNumber, className);
			this.updateExtLEPartyInfo("FREEBIRD GAS STORAGE LLC - LE", "Freebird Gas Storage LLC", 10380, iRunNumber, className);
			this.updateExtLEPartyInfo("GULF SOUTH PIPELINE COMPANY - LE", "Gulf South Pipeline Company, LLC", 10471, iRunNumber, className);
			this.updateExtLEPartyInfo("PERRYVILLE GAS STORAGE LLC - LE", "Perryville Gas Storage, LLC", 10373, iRunNumber, className);
			this.updateExtLEPartyInfo("FREEBIRD GAS STORAGE LLC - LE", "Freebird Gas Storage LLC", 10380, iRunNumber, className);
			this.updateExtLEPartyInfo("TENNESSEE VALLEY AUTHORITY - LE", "Tennessee Valley Authority", 10454, iRunNumber, className);
			this.updateExtLEPartyInfo("KAISER MARKETING APPALACHIAN LLC - LE", "Kaiser Marketing Appalachian, LLC", 10401, iRunNumber, className);
			this.updateExtLEPartyInfo("MAGELLAN PIPELINE COMPANY, L.P. - LE", "Magellan Pipeline Company, L.P.", 10358, iRunNumber, className);
			this.updateExtLEPartyInfo("TEXAS GAS TRANSMISSION LLC - LE", "Texas Gas Transmission, LLC", 10749, iRunNumber, className);
			this.updateExtLEPartyInfo("CORPUS CHRISTI LIQUEFACTION LLC - LE", "Corpus Christi Liquefaction, LLC", 10756, iRunNumber, className);
			this.updateExtLEPartyInfo("COLUMBIA GULF TRANSMISSION LLC - LE", "Columbia Gulf Transmission, LLC", 10448, iRunNumber, className);
			this.updateExtLEPartyInfo("ASCENT RESOURCES-UTICA LLC - LE", "Ascent Resources - Utica, LLC", 10757, iRunNumber, className);
			this.updateExtLEPartyInfo("MEX GAS SUPPLY SL - LE", "Mex Gas Supply, S.L.", 10649, iRunNumber, className);
			this.updateExtLEPartyInfo("NATURGY SERVICIOS SA DE CV - LE", "Naturgy Servicios, S.A. de C.V.", 10768, iRunNumber, className);
			this.updateExtLEPartyInfo("ENGIE ENERGY MARKETING NA INC - LE", "ENGIE Energy Marketing NA, Inc.", 10392, iRunNumber, className);
			this.updateExtLEPartyInfo("BE ANADARKO II LLC - LE", "BE Anadarko II, LLC", 10769, iRunNumber, className);
			this.updateExtLEPartyInfo("TIDAL ENERGY MARKETING (U.S.) L.L.C. - LE", "Tidal Energy Marketing (U.S.) L.L.C.", 10794, iRunNumber, className);
			this.updateExtLEPartyInfo("J ARON & COMPANY LLC - LE", "J. Aron & Company LLC", 10423, iRunNumber, className);
			this.updateExtLEPartyInfo("SWN ENERGY SERVICES COMPANY LLC - LE", "SWN Energy Services Company, LLC", 10770, iRunNumber, className);
			this.updateExtLEPartyInfo("TENNESSEE GAS PIPELINE LLC - LE", "Tennessee Gas Pipeline, L.L.C.", 10797, iRunNumber, className);
			this.updateExtLEPartyInfo("PWEP AUGUSTA, LLC - LE", "PWEP Augusta, LLC", 10795, iRunNumber, className);
			this.updateExtLEPartyInfo("PATHPOINT ENERGY LLC - LE", "PathPoint Energy LLC", 10796, iRunNumber, className);
			this.updateExtLEPartyInfo("RANGE RESOURCES - APPALACHIA, LLC - LE", "Range Resources - Appalachia, LLC", 10585, iRunNumber, className);
			this.updateExtLEPartyInfo("GCC SUPPLY & TRADING LLC - LE", "GCC Supply & Trading LLC", 10798, iRunNumber, className);
			this.updateExtLEPartyInfo("BBT MID LOUISIANA GAS TRANSMISSION, LLC - LE", "BBT Mid Louisiana Gas Transmission, LLC", 10799, iRunNumber, className);
			this.updateExtLEPartyInfo("OZARK GAS TRANSMISSION LLC - LE", "Ozark Gas Transmission, LLC", 10800, iRunNumber, className);
			this.updateExtLEPartyInfo("MU MARKETING LLC - LE", "MU Marketing LLC", 10778, iRunNumber, className);
			this.updateExtLEPartyInfo("SABINE PASS LIQUEFACTION LLC - LE", "Sabine Pass Liquefaction, LLC", 10803, iRunNumber, className);
			this.updateExtLEPartyInfo("CALEDONIA ENERGY PARTNERS LLC - LE", "Caledonia Energy Partners, L.L.C.", 10802, iRunNumber, className);
			this.updateExtLEPartyInfo("ENESTAS, S.A. DE C.V. - LE", "Enestas, S.A. de C.V.", 10804, iRunNumber, className);
			this.updateExtLEPartyInfo("WILD GOOSE STORAGE LLC - LE", "Wild Goose Storage, LLC", 10805, iRunNumber, className);
			this.updateExtLEPartyInfo("WILD GOOSE STORAGE LLC - LE", "Wild Goose Storage, LLC", 10805, iRunNumber, className);
			this.updateExtLEPartyInfo("TGNR NLA LLC - LE", "TGNR NLA LLC", 10806, iRunNumber, className);
			this.updateExtLEPartyInfo("NET MEXICO GAS PIPELINE PARTNERS LLC - LE", "NET Mexico Gas Pipeline Partners, LLC", 10807, iRunNumber, className);
			this.updateExtLEPartyInfo("SAAVI ENERGY SOLUTIONS, LLC - LE", "Saavi Energy Solutions, LLC", 10808, iRunNumber, className);
			this.updateExtLEPartyInfo("SOUTH JERSEY RESOURCES GROUP LLC - LE", "South Jersey Resources Group LLC", 10811, iRunNumber, className);
			this.updateExtLEPartyInfo("INTERCONN RESOURCES, LLC - LE", "Interconn Resources, LLC", 10633, iRunNumber, className);
			this.updateExtLEPartyInfo("FREEPOINT COMMODITIES LLC - LE", "Freepoint Commodities LLC", 10813, iRunNumber, className);
			this.updateExtLEPartyInfo("TGNR TVL LLC - LE", "TGNR TVL LLC", 10814, iRunNumber, className);
			this.updateExtLEPartyInfo("TGNR EAST TEXAS LLC - LE", "TGNR East Texas LLC", 10815, iRunNumber, className);
			this.updateExtLEPartyInfo("WELLS FARGO COMMODITIES - LE", "Wells Fargo Commodities, LLC", 10816, iRunNumber, className);
			this.updateExtLEPartyInfo("ENTERGY LOUISIANA LLC - LE", "Entergy Louisiana, LLC", 10817, iRunNumber, className);
			this.updateExtLEPartyInfo("MIDAMERICAN ENERGY SERVICES LLC - LE", "MidAmerican Energy Services, LLC", 10820, iRunNumber, className);
			this.updateExtLEPartyInfo("GULF RUN TRANSMISSION, LLC - LE", "Gulf Run Transmission, LLC", 10821, iRunNumber, className);
			this.updateExtLEPartyInfo("ENTERGY NEW ORLEANS, LLC - LE", "Entergy New Orleans, LLC", 10822, iRunNumber, className);
			this.updateExtLEPartyInfo("ENTERGY MISSISSIPPI, LLC - LE", "Entergy Mississippi, LLC", 10823, iRunNumber, className);
			this.updateExtLEPartyInfo("ENTERGY TEXAS, INC. - LE", "Entergy Texas, Inc.", 10824, iRunNumber, className);
			this.updateExtLEPartyInfo("ENTERGY ARKANSAS, LLC - LE", "Entergy Arkansas, LLC", 10825, iRunNumber, className);
			this.updateExtLEPartyInfo("WORSHAM-STEED GAS STORAGE LLC - LE", "Worsham-Steed Storage LLC", 10833, iRunNumber, className);
			this.updateExtLEPartyInfo("PACIFIC GAS AND ELECTRIC COMPANY - LE", "Pacific Gas and Electric Company", 10826, iRunNumber, className);
			this.updateExtLEPartyInfo("MACQUARIE ENERGY LLC - LE", "Macquarie Energy North America Trading Inc. DBA Macquarie Energy LLC", 10643, iRunNumber, className);
			this.updateExtLEPartyInfo("GOLDEN TRIANGLE STORAGE LLC - LE", "Golden Triangle Storage, Inc", 10832, iRunNumber, className);
			this.updateExtLEPartyInfo("GOLDEN TRIANGLE STORAGE LLC - LE", "Golden Triangle Storage, Inc", 10832, iRunNumber, className);
			this.updateExtLEPartyInfo("WORSHAM-STEED GAS STORAGE LLC - LE", "Worsham-Steed Storage LLC", 10833, iRunNumber, className);
			this.updateExtLEPartyInfo("LODI GAS STORAGE, L.L.C. - LE", "Lodi Gas Storage, L.L.C.", 10836, iRunNumber, className);
			this.updateExtLEPartyInfo("LODI GAS STORAGE, L.L.C. - LE", "Lodi Gas Storage, L.L.C.", 10836, iRunNumber, className);
			this.updateExtLEPartyInfo("ANEW RNG LLC - LE", "Anew RNG, LLC", 10837, iRunNumber, className);
			this.updateExtLEPartyInfo("ENSIGHT IV ENERGY PARTNERS, LLC - LE", "EnSight IV Energy Partners, LLC", 10840, iRunNumber, className);
			this.updateExtLEPartyInfo("SILVERBOW RESOURCES OPERATING LLC - LE", "SilverBow Resources Operating, LLC", 10839, iRunNumber, className);
			this.updateExtLEPartyInfo("TRES PALACIOS GAS STORAGE LLC - LE", "Tres Palacios Gas Storage LLC", 10838, iRunNumber, className);
			this.updateExtLEPartyInfo("TRAFIGURA MEXICO, S.A. DE C.V. - LE", "Trafigura Mexico, S.A. de C.V.", 10847, iRunNumber, className);
			this.updateExtLEPartyInfo("NEXUS GAS TRANSMISSION, LLC - LE", "Nexus Gas Transmission, LLC", 10844, iRunNumber, className);
			this.updateExtLEPartyInfo("EQUINOR NATURAL GAS LLC - LE", "Equinor Natural Gas LLC", 10624, iRunNumber, className);
			this.updateExtLEPartyInfo("COLUMBIA GAS (TCO) - LE", "Columbia Gas Transmission, LLC", 10855, iRunNumber, className);
			this.updateExtLEPartyInfo("FLORIDA GAS TRANSMISSION - LE", "Florida Gas Transmission Company, LLC", 10858, iRunNumber, className);

			this.updateExtBUPartyInfo("CONOCOPHILLIPS COMPANY - BU", "ConocoPhillips Company", 10394, "CONP3", 12082, iRunNumber, className);
			this.updateExtBUPartyInfo("DTE ENERGY TRADING INC - BU", "DTE Energy Trading, Inc.", 10384, "DTET3", 12083, iRunNumber, className);
			this.updateExtBUPartyInfo("ECO-ENERGY NATURAL GAS, LLC - BU", "Eco-Energy Natural Gas, LLC", 10416, "ECOE3", 12084, iRunNumber, className);
			this.updateExtBUPartyInfo("EEGL - BU", "EEGL", 10480, "EEGL3", 12085, iRunNumber, className);
			this.updateExtBUPartyInfo("EXGC - BU", "EXGC", 10404, "EXGC3", 12086, iRunNumber, className);
			this.updateExtBUPartyInfo("HARTREE PARTNERS LP - BU", "Hartree Partners, LP", 10417, "HART3", 12087, iRunNumber, className);
			this.updateExtBUPartyInfo("KOCH ENERGY SERVICES LLC - BU", "Koch Energy Services, LLC", 10396, "KESL3", 12088, iRunNumber, className);
			this.updateExtBUPartyInfo("MARATHON PETROLEUM COMPANY LP - BU", "Marathon Petroleum Company LP", 10419, "MARA3", 12089, iRunNumber, className);
			this.updateExtBUPartyInfo("MERCURIA ENERGY AMERICA LLC - BU", "Mercuria Energy America, LLC", 10397, "MEAI3", 12090, iRunNumber, className);
			this.updateExtBUPartyInfo("MIECO LLC - BU", "Mieco LLC", 10405, "MIEC3", 12091, iRunNumber, className);
			this.updateExtBUPartyInfo("NATURAL GAS PIPELINE COMPANY OF AMERICA - BU", "NGPC", 10379, "NGPC3", 12092, iRunNumber, className);
			this.updateExtBUPartyInfo("SEQUENT ENERGY MANAGEMENT LLC - BU", "Sequent Energy Management, L.P.", 10470, "SEQU3", 12093, iRunNumber, className);
			this.updateExtBUPartyInfo("SPOTLIGHT ENERGY LLC - BU", "Spotlight Energy, LLC", 10449, "SPOT3", 12094, iRunNumber, className);
			this.updateExtBUPartyInfo("UNIPER GLOBAL COMMODITIES NORTH AMERICA LLC - BU", "Uniper Global Commodities North America LLC", 10411, "UNIP3", 12095, iRunNumber, className);
			this.updateExtBUPartyInfo("VITOL INC - BU", "Vitol Inc.", 10390, "VTOL3", 12096, iRunNumber, className);
			this.updateExtBUPartyInfo("XTO ENERGY INC - BU", "XTO Energy Inc.", 10486, "XTOE3", 12097, iRunNumber, className);
			this.updateExtBUPartyInfo("ARM ENERGY MANAGEMENT LLC - BU", "ARM Energy Management LLC", 10412, "ARME3", 12098, iRunNumber, className);
			this.updateExtBUPartyInfo("EQT ENERGY LLC - BU", "EQT Energy, LLC", 10453, "EQTE3", 12099, iRunNumber, className);
			this.updateExtBUPartyInfo("ALLIANCE PIPELINE - BU", "Alliance Pipeline L.P.", 10487, "APLP3", 12100, iRunNumber, className);
			this.updateExtBUPartyInfo("ANADARKO ENERGY SERVICES COMPANY - BU", "Anadarko Energy Services Company", 10488, "AESC3", 12101, iRunNumber, className);
			this.updateExtBUPartyInfo("ANCOVA ENERGY MARKETING, LLC - BU", "Ancova Energy Marketing, LLC", 10489, "AEML3", 12102, iRunNumber, className);
			this.updateExtBUPartyInfo("ANIG - BU", "ANIG", 10490, "ANIG3", 12103, iRunNumber, className);
			this.updateExtBUPartyInfo("ANR PIPELINE COMPANY - BU", "ANR Pipeline Company", 10491, "ANRP3", 12104, iRunNumber, className);
			this.updateExtBUPartyInfo("ASSOCIATED ELECTRIC COOPERATIVE INC - BU", "Associated Electric Cooperative Inc", 10492, "AECO3", 12105, iRunNumber, className);
			this.updateExtBUPartyInfo("BLACKBEARD OPERATING, LLC - BU", "Blackbeard Operating, LLC", 10493, "BLOL3", 12106, iRunNumber, className);
			this.updateExtBUPartyInfo("BLUE MOUNTAIN MIDSTREAM LLC - BU", "Blue Mountain Midstream LLC", 10494, "BMML3", 12107, iRunNumber, className);
			this.updateExtBUPartyInfo("BLUEGRASS ENERGY - BU", "BLEN", 10495, "BLEN3", 12108, iRunNumber, className);
			this.updateExtBUPartyInfo("BP ENERGY COMPANY - BU", "BP Energy Company", 10496, "BPEC3", 12109, iRunNumber, className);
			this.updateExtBUPartyInfo("CENT - BU", "CENT", 10497, "CENT3", 12110, iRunNumber, className);
			this.updateExtBUPartyInfo("CFE INTERNATIONAL LLC - BU", "CFE International LLC", 10498, "CFEI3", 12111, iRunNumber, className);
			this.updateExtBUPartyInfo("CHEVRON NATURAL GAS - BU", "Chevron Natural Gas, a division of Chevron U.S.A. Inc.", 10499, "CHEV3", 12112, iRunNumber, className);
			this.updateExtBUPartyInfo("CIMA ENERGY LP - BU", "CIEN", 10500, "CIEN3", 12113, iRunNumber, className);
			this.updateExtBUPartyInfo("CITADEL ENERGY MARKETING LLC - BU", "Citadel Energy Marketing LLC", 10501, "CEML3", 12114, iRunNumber, className);
			this.updateExtBUPartyInfo("CITY UTILITIES OF SPRINGFIELD, MISSOURI - BU", "City Utilities of Springfield, Missouri", 10502, "CUSM3", 12115, iRunNumber, className);
			this.updateExtBUPartyInfo("CLEARWATER ENTERPRISES LLC - BU", "Clearwater Enterprises, L.L.C.", 10503, "CLEAR3", 12116, iRunNumber, className);
			this.updateExtBUPartyInfo("COGE - BU", "COGE", 10504, "COGE3", 12117, iRunNumber, className);
			this.updateExtBUPartyInfo("CONCORD ENERGY LLC - BU", "Concord Energy LLC", 10505, "CONC3", 12118, iRunNumber, className);
			this.updateExtBUPartyInfo("CONSUMERS ENERGY COMPANY - BU", "Consumers Energy Company", 10506, "COEC3", 12119, iRunNumber, className);
			this.updateExtBUPartyInfo("DCP MIDSTREAM MARKETING LLC - BU", "DCP Midstream Marketing, LLC", 10507, "DCPM3", 12120, iRunNumber, className);
			this.updateExtBUPartyInfo("DTE GAS COMPANY - BU", "DTE Gas Company", 10508, "DTGC3", 12121, iRunNumber, className);
			this.updateExtBUPartyInfo("EDF TRADING NORTH AMERICA LLC - BU", "EDF Trading North America, LLC", 10509, "EDFT3", 12122, iRunNumber, className);
			this.updateExtBUPartyInfo("ELEMENT MARKETS RENEWABLE ENERGY, LLC - BU", "Element Markets Renewable Energy, LLC", 10510, "EMRE3", 12123, iRunNumber, className);
			this.updateExtBUPartyInfo("EMERA ENERGY SERVICES INC - BU", "Emera Energy Services, Inc.", 10511, "EESI3", 12124, iRunNumber, className);
			this.updateExtBUPartyInfo("ENABLE ENERGY RESOURCES, LLC - BU", "Enable Energy Resources, LLC", 10512, "EERL3", 12125, iRunNumber, className);
			this.updateExtBUPartyInfo("ENABLE GAS TRANSMISSION LLC - BU", "Enable Gas Transmission, LLC", 10513, "EGTL3", 12126, iRunNumber, className);
			this.updateExtBUPartyInfo("ENABLE OKLAHOMA INTRASTATE TRANSMISSION LLC - BU", "Enable Oklahoma Intrastate Transmission, LLC", 10514, "EOIT3", 12127, iRunNumber, className);
			this.updateExtBUPartyInfo("ENSI - BU", "ENSI", 10515, "ENSI3", 12128, iRunNumber, className);
			this.updateExtBUPartyInfo("EOG RESOURCES INC - BU", "EOG Resources, Inc.", 10516, "EOGR3", 12129, iRunNumber, className);
			this.updateExtBUPartyInfo("EOXH - BU", "EOXH", 10517, "EOXH3", 12130, iRunNumber, className);
			this.updateExtBUPartyInfo("ETC MARKETING LTD - BU", "ETC Marketing, LTD.", 10518, "ETCM3", 12131, iRunNumber, className);
			this.updateExtBUPartyInfo("WESTAR ENERGY INC - BU", "Westar Energy Inc", 10519, "EVER3", 12132, iRunNumber, className);
			this.updateExtBUPartyInfo("ICBC STANDARD BANK PLC - BU", "ICBC Standard Bank PLC", 10520, "ICBS3", 12133, iRunNumber, className);

			this.updateExtBUPartyInfo("K2 COMMODITIES LLC - BU", "K2 Commodities, LLC", 10522, "KCOM3", 12135, iRunNumber, className);
			this.updateExtBUPartyInfo("LUMINANT ENERGY COMPANY LLC - BU", "Luminant Energy Company LLC", 10523, "LUMI3", 12136, iRunNumber, className);

			this.updateExtBUPartyInfo("MIDAMERICAN ENERGY COMPANY - BU", "MidAmerican Energy Company", 10525, "MIDE3", 12138, iRunNumber, className);
			this.updateExtBUPartyInfo("MTCL - BU", "MTCL", 10526, "MTCL3", 12139, iRunNumber, className);
			this.updateExtBUPartyInfo("MORGAN STANLEY CAPITAL GROUP INC. - BU", "Morgan Stanley Capital Group Inc.", 10527, "MSCG3", 12140, iRunNumber, className);
			this.updateExtBUPartyInfo("NGPX - BU", "NGPX", 10528, "NGPX3", 12141, iRunNumber, className);
			this.updateExtBUPartyInfo("NORTHERN ILLINOIS GAS COMPANY - BU", "Nicor Gas Co", 10529, "NICG3", 12142, iRunNumber, className);
			this.updateExtBUPartyInfo("NORTHERN INDIANA PUBLIC SERVICE COMPANY LLC - BU", "Northern Indiana Public Service Company LLC", 10530, "NIPS3", 12143, iRunNumber, className);
			this.updateExtBUPartyInfo("NORTHERN NATURAL GAS COMPANY - BU", "Northern Natural Gas Company", 10531, "NNGA3", 12144, iRunNumber, className);
			this.updateExtBUPartyInfo("OASIS PIPELINE LP - BU", "Oasis Pipeline, LP", 10532, "OASI3", 12145, iRunNumber, className);
			this.updateExtBUPartyInfo("OKLAHOMA GAS & ELECTRIC COMPANY - BU", "Oklahoma Gas & Electric Company", 10533, "OGEC3", 12146, iRunNumber, className);
			this.updateExtBUPartyInfo("ONEOK FIELD SERVICES COMPANY LLC - BU", "ONEOK Field Services Company, L.L.C.", 10534, "OFSC3", 12147, iRunNumber, className);
			this.updateExtBUPartyInfo("ONEOK GAS TRANSPORATION LLC - BU", "Oneok Gas Transporation, L.L.C.", 10535, "OGTL3", 12148, iRunNumber, className);
			this.updateExtBUPartyInfo("ONEOK WESTEX TRANSMISSION LLC - BU", "ONEOK WesTex Transmission, L.L.C.", 10536, "OWTL3", 12149, iRunNumber, className);
			this.updateExtBUPartyInfo("ONIX - BU", "ONIX", 10537, "ONIX3", 12150, iRunNumber, className);
			this.updateExtBUPartyInfo("OVINTIV MARKETING INC - BU", "Ovintiv Marketing Inc.", 10538, "OVIN3", 12151, iRunNumber, className);
			this.updateExtBUPartyInfo("PANHANDLE EASTERN PIPE LINE COMPANY LP - BU", "Panhandle Eastern Pipe Line Company, LP", 10539, "PEPC3", 12152, iRunNumber, className);
			this.updateExtBUPartyInfo("PRESIDIO PETROLEUM LLC - BU", "Presidio Petroleum LLC", 10540, "PRES3", 12153, iRunNumber, className);
			this.updateExtBUPartyInfo("PUBLIC SERVICE COMPANY OF NEW MEXICO - BU", "Public Service Company of New Mexico", 10541, "PSCN3", 12154, iRunNumber, className);
			this.updateExtBUPartyInfo("SHELL ENERGY NORTH AMERICA (US) LP - BU", "Shell Energy North America (US), L.P.", 10542, "SENA3", 12155, iRunNumber, className);
			this.updateExtBUPartyInfo("SOUTHERN STAR CENTRAL GAS PIPELINE INC - BU", "Southern Star Central Gas Pipeline, Inc.", 10543, "SSCG3", 12156, iRunNumber, className);
			this.updateExtBUPartyInfo("SOUTHWEST ENERGY LP - BU", "Southwest Energy, L.P.", 10544, "SOEL3", 12157, iRunNumber, className);

			this.updateExtBUPartyInfo("SPIRE MARKETING INC - BU", "Spire Marketing Inc.", 10546, "SPIR3", 12159, iRunNumber, className);
			this.updateExtBUPartyInfo("SPIRE MISSOURI INC. - BU", "Spire Missouri Inc.", 10547, "SMOI3", 12160, iRunNumber, className);
			this.updateExtBUPartyInfo("SUPERIOR PIPELINE TEXAS, LLC - BU", "Superior Pipeline Texas, LLC", 10548, "SPTL3", 12161, iRunNumber, className);
			this.updateExtBUPartyInfo("TAPSTONE SELLER LLC - BU", "Tapstone Seller LLC", 10549, "TAPS3", 12162, iRunNumber, className);
			this.updateExtBUPartyInfo("TARGA GAS MARKETING LLC - BU", "Targa Gas Marketing LLC", 10550, "TARG3", 12163, iRunNumber, className);
			this.updateExtBUPartyInfo("THE EMPIRE DISTRICT ELECTRIC COMPANY - BU", "The Empire District Electric Company", 10551, "TEDE3", 12164, iRunNumber, className);
			this.updateExtBUPartyInfo("THE ENERGY AUTHORITY INC - BU", "The Energy Authority, Inc", 10552, "TEAI3", 12165, iRunNumber, className);
			this.updateExtBUPartyInfo("TRANSWESTERN PIPELINE COMPANY - BU", "Transwestern Pipeline Company, LLC", 10553, "TPCL3", 12166, iRunNumber, className);
			this.updateExtBUPartyInfo("TWIN EAGLE RESOURCE MANAGEMENT LLC - BU", "Twin Eagle Resource Management, LLC", 10554, "TWIN3", 12167, iRunNumber, className);
			this.updateExtBUPartyInfo("WGL MIDSTREAM, INC. - BU", "WGL Midstream, Inc.", 10555, "WGLM3", 12168, iRunNumber, className);
			this.updateExtBUPartyInfo("WILLIAMS ENERGY RESOURCES LLC - BU", "Williams Energy Resources LLC", 10556, "WERL3", 12169, iRunNumber, className);
			this.updateExtBUPartyInfo("WISCONSIN POWER AND LIGHT COMPANY - BU", "Wisconsin Power and Light Company", 10557, "WPLC3", 12170, iRunNumber, className);
			this.updateExtBUPartyInfo("WORLD FUEL SERVICES INC - BU", "World Fuel Services, Inc.", 10558, "WFSI3", 12171, iRunNumber, className);
			this.updateExtBUPartyInfo("WTG MIDSTREAM MARKETING LLC - BU", "WTG Midstream Marketing, LLC", 10559, "WTGG3", 12172, iRunNumber, className);
			this.updateExtBUPartyInfo("CASTLETON - BU", "Castleton Commodities Merchant Trading L.P.", 10562, "CCMT3", 12189, iRunNumber, className);
			this.updateExtBUPartyInfo("GREAT LAKES GAS TRANSMISSION - BU", "Great Lakes Gas Transmission Limited Partnership", 10563, "GLGT3", 12190, iRunNumber, className);
			this.updateExtBUPartyInfo("ICE NGX CANADA INC - BU", "NGX", 10382, "INGX3", 12191, iRunNumber, className);
			this.updateExtBUPartyInfo("MARKWEST PIONEER LLC - BU", "MarkWest Pioneer, LLC", 10565, "MWPL3", 12192, iRunNumber, className);
			this.updateExtBUPartyInfo("ENTERPRISE TEXAS PIPELINE LLC - BU", "Enterprise Texas Pipeline LLC", 10569, "ETPL3", 12193, iRunNumber, className);
			this.updateExtBUPartyInfo("OKLAHOMA NATURAL GAS - BU", "Oklahoma Natural Gas", 10566, "ONGC3", 12194, iRunNumber, className);
			this.updateExtBUPartyInfo("ONEOK GAS STORAGE LLC - BU", "ONEOK Gas Storage, L.L.C.", 10567, "OGSL3", 12195, iRunNumber, className);
			this.updateExtBUPartyInfo("WASHINGTON 10 STORAGE CORPORATION - BU", "WASH", 10568, "WASH3", 12196, iRunNumber, className);
			this.updateExtBUPartyInfo("VECTOR PIPELINE LP - BU", "Vector Pipeline Limited Partnership", 10581, "VECT3", 12253, iRunNumber, className);
			this.updateExtBUPartyInfo("NEXTERA ENERGY MARKETING LLC - BU", "NextEra Energy Marketing, LLC", 10395, "NEXT3", 12254, iRunNumber, className);
			this.updateExtBUPartyInfo("AEP PSO - BU", "AEP PSO", 10582, "PSCO3", 12255, iRunNumber, className);
			this.updateExtBUPartyInfo("SYMMETRY ENERGY SOLUTIONS LLC - BU", "SYMM", 10436, "SYMM3", 12256, iRunNumber, className);
			this.updateExtBUPartyInfo("TEXLA ENERGY MANAGEMENT INC - BU", "Texla Energy Management, Inc.", 10444, "TEXL3", 12257, iRunNumber, className);
			this.updateExtBUPartyInfo("ENTERPRISE PRODUCTS OPERATING LLC - BU", "Enterprise Products Operating LLC", 10583, "EPOL3", 12258, iRunNumber, className);
			this.updateExtBUPartyInfo("CARBONBETTER LLC - BU", "Carbonbetter, LLC", 10587, "CABE3", 12279, iRunNumber, className);
			this.updateExtBUPartyInfo("COLORADO SPRINGS UTILITIES - BU", "Colorado Springs Utilities", 10588, "COLO3", 12280, iRunNumber, className);
			this.updateExtBUPartyInfo("MANSFIELD POWER & GAS LLC - BU", "Mansfield Power and Gas, LLC", 10455, "MANS3", 12281, iRunNumber, className);
			this.updateExtBUPartyInfo("CHESAPEAKE ENERGY MARKETING LLC - BU", "Chesapeake Energy Marketing, L.L.C.", 10611, "CHEM3", 12306, iRunNumber, className);
			this.updateExtBUPartyInfo("CONTINENTAL RESOURCES INC - BU", "Continental Resources, Inc.", 10614, "CONT3", 12307, iRunNumber, className);
			this.updateExtBUPartyInfo("DXT COMMODITIES NORTH AMERICA INC - BU", "DXT Commodities North America Inc.", 10619, "DXTC3", 12308, iRunNumber, className);
			this.updateExtBUPartyInfo("INTERSTATE GAS SUPPLY LLC - BU", "Interstate Gas Supply, Inc.", 10634, "INTR3", 12309, iRunNumber, className);
			this.updateExtBUPartyInfo("BP CANADA ENERGY MARKETING CORP - BU", "BP Canada Energy Marketing Corp", 10608, "BPCE3", 12323, iRunNumber, className);
			this.updateExtBUPartyInfo("LEGACY RESERVES ENERGY SERVICES LLC - BU", "Legacy Reserves Energy Services LLC", 10695, "LRES3", 12337, iRunNumber, className);
			this.updateExtBUPartyInfo("UNITED ENERGY TRADING LLC - BU", "United Energy Trading, LLC", 10443, "UNTD3", 12356, iRunNumber, className);
			this.updateExtBUPartyInfo("DEVON GAS SERVICES, L.P. - BU", "Devon Gas Services, L.P.", 10618, "DEVO3", 12361, iRunNumber, className);

			this.updateExtBUPartyInfo("KCP&L GREATER MISSOURI OPERATIONS COMPANY - BU", "KCP&L Greater Missouri Operations Company", 10626, "EVMW3", 12389, iRunNumber, className);
			this.updateExtBUPartyInfo("GUNVOR USA LLC - BU", "GUNV", 10427, "GUNV3", 12390, iRunNumber, className);
			this.updateExtBUPartyInfo("PINE PRAIRIE ENERGY CENTER LLC - BU", "Pine Prairie Energy Center, LLC", 10705, "PPEC3", 12391, iRunNumber, className);
			this.updateExtBUPartyInfo("TC ENERGY MARKETING INC - BU", "TC Energy Marketing Inc.", 10679, "TCEM3", 12430, iRunNumber, className);
			this.updateExtBUPartyInfo("CONSTELLATION ENERGY GENERATION LLC - BU", "Constellation Energy Generation, LLC", 10710, "CEGL3", 12431, iRunNumber, className);
			this.updateExtBUPartyInfo("OCCIDENTAL ENERGY MARKETING INC - BU", "Occidental Energy Marketing, Inc.", 10660, "OCCI3", 12432, iRunNumber, className);
			this.updateExtBUPartyInfo("SIX ONE COMMODITIES LLC - BU", "Six One Commodities LLC", 10408, "SIXO3", 12433, iRunNumber, className);
			this.updateExtBUPartyInfo("AGUA BLANCA PIPELINE - BU", "Agua Blanca, LLC", 10713, "AGUA3", 12437, iRunNumber, className);
			this.updateExtBUPartyInfo("DK TRADING & SUPPLY LLC - BU", "DK Trading & Supply, LLC", 10617, "DKTS3", 12438, iRunNumber, className);
			this.updateExtBUPartyInfo("PIONEER NATURAL RESOURCES USA, INC. - BU", "Pioneer Natural Resources USA, Inc.", 10666, "PION3", 12447, iRunNumber, className);
			this.updateExtBUPartyInfo("NORTHERN STATES POWER COMPANY - BU", "Northern States Power Company, Minnesota", 10719, "MINN3", 12454, iRunNumber, className);
			this.updateExtBUPartyInfo("REPSOL ENERGY NORTH AMERICA CORPORATION - BU", "Repsol Energy North America Corporation", 10402, "RENA3", 12508, iRunNumber, className);
			this.updateExtBUPartyInfo("ANR PIPELINE COMPANY - BU", "ANR Pipeline Company", 10491, "ANRP4", 12517, iRunNumber, className);
			this.updateExtBUPartyInfo("WASHINGTON 10 STORAGE CORPORATION - BU", "WASH", 10568, "WASH4", 12518, iRunNumber, className);
			this.updateExtBUPartyInfo("NATURAL GAS PIPELINE COMPANY OF AMERICA - BU", "NGPC", 10379, "NGPC4", 12519, iRunNumber, className);
			this.updateExtBUPartyInfo("TOURMALINE OIL MARKETING CORP. - BU", "Tourmaline Oil Marketing Corp.", 10696, "TOUR3", 12520, iRunNumber, className);
			this.updateExtBUPartyInfo("TRANSCONTINENTAL GAS PIPE LINE COMPANY LLC - BU", "Transcontinental Gas Pipeline Company LLC", 10723, "TGPL3", 12526, iRunNumber, className);
			this.updateExtBUPartyInfo("ENLINK GAS MARKETING, LP - BU", "EnLink Gas Marketing, LP", 10621, "ENGM3", 12528, iRunNumber, className);
			this.updateExtBUPartyInfo("ARKANSAS ELECTRIC COOPERATIVE CORPORATION - BU", "Arkansas Electric Cooperative Corporation", 10599, "ARKC3", 12535, iRunNumber, className);
			this.updateExtBUPartyInfo("RADIATE ENERGY LLC - BU", "Radiate Energy LLC", 10702, "RADI3", 12557, iRunNumber, className);
			this.updateExtBUPartyInfo("MADISON GAS & ELECTRIC - BU", "Madison Gas & Electric", 10744, "MGEC3", 12586, iRunNumber, className);
			this.updateExtBUPartyInfo("WWM LOGISTICS, LLC - BU", "WWM Logistics, LLC", 10691, "WWML3", 12595, iRunNumber, className);
			this.updateExtBUPartyInfo("DIRECT ENERGY BUSINESS MARKETING LLC - BU", "DEBM", 10398, "DEBM3", 12596, iRunNumber, className);
			this.updateExtBUPartyInfo("GULF SOUTH PIPELINE COMPANY - BU", "GSPC", 10471, "GSPC3", 12597, iRunNumber, className);
			this.updateExtBUPartyInfo("PERRYVILLE GAS STORAGE LLC - BU", "PVGS", 10373, "PVGS3", 12598, iRunNumber, className);
			this.updateExtBUPartyInfo("FREEBIRD GAS STORAGE LLC - BU", "FREE", 10380, "FREE3", 12599, iRunNumber, className);
			this.updateExtBUPartyInfo("GULF SOUTH PIPELINE COMPANY - BU", "GSPC", 10471, "GSPC4", 12600, iRunNumber, className);
			this.updateExtBUPartyInfo("PERRYVILLE GAS STORAGE LLC - BU", "PVGS", 10373, "PVGS4", 12601, iRunNumber, className);
			this.updateExtBUPartyInfo("FREEBIRD GAS STORAGE LLC - BU", "FREE", 10380, "FREE4", 12602, iRunNumber, className);
			this.updateExtBUPartyInfo("TENNESSEE VALLEY AUTHORITY - BU", "TVAL", 10454, "TVAL3", 12603, iRunNumber, className);
			this.updateExtBUPartyInfo("KAISER MARKETING APPALACHIAN LLC - BU", "KAIM", 10401, "KAIM3", 12604, iRunNumber, className);
			this.updateExtBUPartyInfo("MMPT - BU", "MMPT", 10358, "MMPT3", 12629, iRunNumber, className);
			this.updateExtBUPartyInfo("TEXAS GAS TRANSMISSION LLC - BU", "Texas Gas Transmission, LLC", 10749, "TXGT3", 12645, iRunNumber, className);
			this.updateExtBUPartyInfo("CORPUS CHRISTI LIQUEFACTION LLC - BU", "Corpus Christi Liquefaction, LLC", 10756, "CCLL3", 12703, iRunNumber, className);
			this.updateExtBUPartyInfo("COLUMBIA GULF TRANSMISSION LLC - BU", "CGTL", 10448, "CGTL3", 12704, iRunNumber, className);
			this.updateExtBUPartyInfo("ASCENT RESOURCES-UTICA LLC - BU", "ARUL3", 10757, "ARUL3", 12717, iRunNumber, className);
			this.updateExtBUPartyInfo("MEX GAS SUPPLY SL - BU", "Mex Gas Supply, S.L.", 10649, "MEXG3", 12720, iRunNumber, className);
			this.updateExtBUPartyInfo("NATURGY SERVICIOS SA DE CV - BU", "NSSA3", 10768, "NSSA3", 12813, iRunNumber, className);
			this.updateExtBUPartyInfo("ENGIE ENERGY MARKETING NA INC - BU", "ENGE", 10392, "ENGE3", 12816, iRunNumber, className);
			this.updateExtBUPartyInfo("BE ANADARKO II LLC - BU", "BEAN3", 10769, "BEAN3", 12817, iRunNumber, className);
			this.updateExtBUPartyInfo("TIDAL ENERGY MARKETING (U.S.) L.L.C. - BU", "Tidal Energy Marketing (U.S.) L.L.C.", 10794, "TIDA3", 12955, iRunNumber, className);
			this.updateExtBUPartyInfo("J ARON & COMPANY LLC - BU", "ARON", 10423, "ARON3", 12964, iRunNumber, className);
			this.updateExtBUPartyInfo("SWN ENERGY SERVICES COMPANY LLC - BU", "SWN Energy Services Company, LLC", 10770, "SWNE3", 12965, iRunNumber, className);
			this.updateExtBUPartyInfo("TENNESSEE GAS PIPELINE LLC - BU", "Tennessee Gas Pipeline, L.L.C.", 10797, "TENN3", 12966, iRunNumber, className);
			this.updateExtBUPartyInfo("PWEP AUGUSTA, LLC - BU", "PWEP Augusta, LLC", 10795, "FORE3", 12967, iRunNumber, className);
			this.updateExtBUPartyInfo("PATHPOINT ENERGY LLC - BU", "PathPoint Energy LLC", 10796, "PATH3", 12968, iRunNumber, className);
			this.updateExtBUPartyInfo("RANG - BU", "RANG", 10585, "RANG3", 12969, iRunNumber, className);
			this.updateExtBUPartyInfo("GCC SUPPLY & TRADING LLC - BU", "GCC Supply & Trading LLC", 10798, "GCCS3", 12976, iRunNumber, className);
			this.updateExtBUPartyInfo("BBT MID LOUISIANA GAS TRANSMISSION, LLC - BU", "BBT Mid Louisiana Gas Transmission, LLC", 10799, "LOUT3", 12977, iRunNumber, className);
			this.updateExtBUPartyInfo("OZARK GAS TRANSMISSION LLC - BU", "Ozark Gas Transmission, LLC", 10800, "OZAR3", 12978, iRunNumber, className);
			this.updateExtBUPartyInfo("MU MARKETING LLC - BU", "MU Marketing LLC", 10778, "MUMA3", 12993, iRunNumber, className);
			this.updateExtBUPartyInfo("SABINE PASS LIQUEFACTION LLC - BU", "Sabine Pass Liquefaction, LLC", 10803, "SABI3", 12994, iRunNumber, className);
			this.updateExtBUPartyInfo("CALEDONIA ENERGY PARTNERS LLC - BU", "Caledonia Energy Partners, L.L.C.", 10802, "CALE3", 12995, iRunNumber, className);
			this.updateExtBUPartyInfo("ENESTAS, S.A. DE C.V. - BU", "Enestas, S.A. de C.V.", 10804, "ENES3", 12999, iRunNumber, className);
			this.updateExtBUPartyInfo("WILD GOOSE STORAGE LLC - BU", "GOOS3", 10805, "GOOS3", 13000, iRunNumber, className);
			this.updateExtBUPartyInfo("WILD GOOSE STORAGE LLC - BU", "GOOS3", 10805, "GOOS4", 13001, iRunNumber, className);
			this.updateExtBUPartyInfo("TGNR3 - BU", "TGNR3", 10806, "TGNR3", 13004, iRunNumber, className);
			this.updateExtBUPartyInfo("NET MEXICO GAS PIPELINE PARTNERS LLC - BU", "NET Mexico Gas Pipeline Partners, LLC", 10807, "NETM3", 13005, iRunNumber, className);
			this.updateExtBUPartyInfo("SAAV3 - BU", "SAAV3", 10808, "SAAV3", 13007, iRunNumber, className);
			this.updateExtBUPartyInfo("SOUTH JERSEY RESOURCES GROUP LLC - BU", "South Jersey Resources Group LLC", 10811, "JERS3", 13022, iRunNumber, className);
			this.updateExtBUPartyInfo("INTERCONN RESOURCES, LLC - BU", "Interconn Resources, LLC", 10633, "INTER3", 13023, iRunNumber, className);
			this.updateExtBUPartyInfo("FREEPOINT COMMODITIES LLC - BU", "Freepoint Commodities LLC", 10813, "FCOM3", 13033, iRunNumber, className);
			this.updateExtBUPartyInfo("TVLL3 - BU", "TVLL3", 10814, "TVLL3", 13034, iRunNumber, className);
			this.updateExtBUPartyInfo("EAST3 - BU", "EAST3", 10815, "EAST3", 13035, iRunNumber, className);
			this.updateExtBUPartyInfo("WELLS FARGO COMMODITIES - BU", "WELL3", 10816, "WELL3", 13047, iRunNumber, className);
			this.updateExtBUPartyInfo("ENTERGY LOUISIANA LLC - BU", "ELOU3", 10817, "ELOU3", 13048, iRunNumber, className);
			this.updateExtBUPartyInfo("MIDAMERICAN ENERGY SERVICES LLC - BU", "MidAmerican Energy Services, LLC", 10820, "MESL3", 13066, iRunNumber, className);
			this.updateExtBUPartyInfo("GLFRN - BU", "GLFRN", 10821, "GLFRN3", 13100, iRunNumber, className);
			this.updateExtBUPartyInfo("ENTNO - BU", "ENTNO", 10822, "ENTNO3", 13101, iRunNumber, className);
			this.updateExtBUPartyInfo("ENTMS - BU", "ENTMS", 10823, "ENTMS3", 13102, iRunNumber, className);
			this.updateExtBUPartyInfo("ENTTX - BU", "ENTTX", 10824, "ENTTX3", 13103, iRunNumber, className);
			this.updateExtBUPartyInfo("ENTAR - BU", "ENTAR", 10825, "ENTAR3", 13104, iRunNumber, className);
			this.updateExtBUPartyInfo("WORSHAM-STEED GAS STORAGE LLC - BU", "WORST", 10833, "WORS3", 13115, iRunNumber, className);
			this.updateExtBUPartyInfo("PGAEC - BU", "PGAEC", 10826, "PGEC3", 13116, iRunNumber, className);
			this.updateExtBUPartyInfo("MACQUARIE ENERGY LLC - BU", "Macquarie Energy LLC", 10643, "MACQ3", 13117, iRunNumber, className);
			this.updateExtBUPartyInfo("GOLDEN TRIANGLE STORAGE LLC - BU", "GLDNT", 10832, "GTRI3", 13118, iRunNumber, className);
			this.updateExtBUPartyInfo("GOLDEN TRIANGLE STORAGE LLC - BU", "GLDNT", 10832, "GTRI4", 13119, iRunNumber, className);
			this.updateExtBUPartyInfo("WORSHAM-STEED GAS STORAGE LLC - BU", "WORST", 10833, "WORS4", 13125, iRunNumber, className);
			this.updateExtBUPartyInfo("LODI - BU", "LODI", 10836, "LODI3", 13126, iRunNumber, className);
			this.updateExtBUPartyInfo("LODI - BU", "LODI", 10836, "LODI4", 13127, iRunNumber, className);
			this.updateExtBUPartyInfo("ANEW RNG LLC - BU", "Anew RNG, LLC", 10837, "ANEW3", 13128, iRunNumber, className);
			this.updateExtBUPartyInfo("ENSIGHT - BU", "ENSIGHT", 10840, "IVEP3", 13149, iRunNumber, className);
			this.updateExtBUPartyInfo("SILVERBOW RESOURCES OPERATING LLC - BU", "SIBOW", 10839, "SBOW3", 13150, iRunNumber, className);
			this.updateExtBUPartyInfo("TRESPAL - BU", "TRESPAL", 10838, "TRES3", 13151, iRunNumber, className);
			this.updateExtBUPartyInfo("TRAFM - BU", "TRAFM", 10847, "TRAF3", 13247, iRunNumber, className);
			this.updateExtBUPartyInfo("NEXUS - BU", "NEXUS", 10844, "NEXU3", 13248, iRunNumber, className);
			this.updateExtBUPartyInfo("EQUINOR NATURAL GAS LLC - BU", "Equinor Natural Gas LLC", 10624, "EQNX3", 13250, iRunNumber, className);
			this.updateExtBUPartyInfo("COLUMBIA GAS (TCO) - BU", "CGTM", 10855, "CGTM3", 13282, iRunNumber, className);
			this.updateExtBUPartyInfo("FLORIDA GAS TRANSMISSION - BU", "FGTC", 10858, "FGTC3", 13285, iRunNumber, className);
*/
			
			FUNC.updatePortInfo("CONP3", 12082, iRunNumber, className);
			FUNC.updatePortInfo("DTET3", 12083, iRunNumber, className);
			FUNC.updatePortInfo("ECOE3", 12084, iRunNumber, className);
			FUNC.updatePortInfo("EEGL3", 12085, iRunNumber, className);
			FUNC.updatePortInfo("EXGC3", 12086, iRunNumber, className);
			FUNC.updatePortInfo("HART3", 12087, iRunNumber, className);
			FUNC.updatePortInfo("KESL3", 12088, iRunNumber, className);
			FUNC.updatePortInfo("MARA3", 12089, iRunNumber, className);
			FUNC.updatePortInfo("MEAI3", 12090, iRunNumber, className);
			FUNC.updatePortInfo("MIEC3", 12091, iRunNumber, className);
			FUNC.updatePortInfo("NGPC3", 12092, iRunNumber, className);
			FUNC.updatePortInfo("SEQU3", 12093, iRunNumber, className);
			FUNC.updatePortInfo("SPOT3", 12094, iRunNumber, className);
			FUNC.updatePortInfo("UNIP3", 12095, iRunNumber, className);
			FUNC.updatePortInfo("VTOL3", 12096, iRunNumber, className);
			FUNC.updatePortInfo("XTOE3", 12097, iRunNumber, className);
			FUNC.updatePortInfo("ARME3", 12098, iRunNumber, className);
			FUNC.updatePortInfo("EQTE3", 12099, iRunNumber, className);
			FUNC.updatePortInfo("APLP3", 12100, iRunNumber, className);
			FUNC.updatePortInfo("AESC3", 12101, iRunNumber, className);
			FUNC.updatePortInfo("AEML3", 12102, iRunNumber, className);
			FUNC.updatePortInfo("ANIG3", 12103, iRunNumber, className);
			FUNC.updatePortInfo("ANRP3", 12104, iRunNumber, className);
			FUNC.updatePortInfo("AECO3", 12105, iRunNumber, className);
			FUNC.updatePortInfo("BLOL3", 12106, iRunNumber, className);
			FUNC.updatePortInfo("BMML3", 12107, iRunNumber, className);
			FUNC.updatePortInfo("BLEN3", 12108, iRunNumber, className);
			FUNC.updatePortInfo("BPEC3", 12109, iRunNumber, className);
			FUNC.updatePortInfo("CENT3", 12110, iRunNumber, className);
			FUNC.updatePortInfo("CFEI3", 12111, iRunNumber, className);
			FUNC.updatePortInfo("CHEV3", 12112, iRunNumber, className);
			FUNC.updatePortInfo("CIEN3", 12113, iRunNumber, className);
			FUNC.updatePortInfo("CEML3", 12114, iRunNumber, className);
			FUNC.updatePortInfo("CUSM3", 12115, iRunNumber, className);
			FUNC.updatePortInfo("CLEAR3", 12116, iRunNumber, className);
			FUNC.updatePortInfo("COGE3", 12117, iRunNumber, className);
			FUNC.updatePortInfo("CONC3", 12118, iRunNumber, className);
			FUNC.updatePortInfo("COEC3", 12119, iRunNumber, className);
			FUNC.updatePortInfo("DCPM3", 12120, iRunNumber, className);
			FUNC.updatePortInfo("DTGC3", 12121, iRunNumber, className);
			FUNC.updatePortInfo("EDFT3", 12122, iRunNumber, className);
			FUNC.updatePortInfo("EMRE3", 12123, iRunNumber, className);
			FUNC.updatePortInfo("EESI3", 12124, iRunNumber, className);
			FUNC.updatePortInfo("EERL3", 12125, iRunNumber, className);
			FUNC.updatePortInfo("EGTL3", 12126, iRunNumber, className);
			FUNC.updatePortInfo("EOIT3", 12127, iRunNumber, className);
			FUNC.updatePortInfo("ENSI3", 12128, iRunNumber, className);
			FUNC.updatePortInfo("EOGR3", 12129, iRunNumber, className);
			FUNC.updatePortInfo("EOXH3", 12130, iRunNumber, className);
			FUNC.updatePortInfo("ETCM3", 12131, iRunNumber, className);
			FUNC.updatePortInfo("EVER3", 12132, iRunNumber, className);
			FUNC.updatePortInfo("ICBS3", 12133, iRunNumber, className);
			FUNC.updatePortInfo("IPLC3", 12134, iRunNumber, className);
			FUNC.updatePortInfo("KCOM3", 12135, iRunNumber, className);
			FUNC.updatePortInfo("LUMI3", 12136, iRunNumber, className);
			FUNC.updatePortInfo("MAOC3", 12137, iRunNumber, className);
			FUNC.updatePortInfo("MIDE3", 12138, iRunNumber, className);
			FUNC.updatePortInfo("MTCL3", 12139, iRunNumber, className);
			FUNC.updatePortInfo("MSCG3", 12140, iRunNumber, className);
			FUNC.updatePortInfo("NGPX3", 12141, iRunNumber, className);
			FUNC.updatePortInfo("NICG3", 12142, iRunNumber, className);
			FUNC.updatePortInfo("NIPS3", 12143, iRunNumber, className);
			FUNC.updatePortInfo("NNGA3", 12144, iRunNumber, className);
			FUNC.updatePortInfo("OASI3", 12145, iRunNumber, className);
			FUNC.updatePortInfo("OGEC3", 12146, iRunNumber, className);
			FUNC.updatePortInfo("OFSC3", 12147, iRunNumber, className);
			FUNC.updatePortInfo("OGTL3", 12148, iRunNumber, className);
			FUNC.updatePortInfo("OWTL3", 12149, iRunNumber, className);
			FUNC.updatePortInfo("ONIX3", 12150, iRunNumber, className);
			FUNC.updatePortInfo("OVIN3", 12151, iRunNumber, className);
			FUNC.updatePortInfo("PEPC3", 12152, iRunNumber, className);
			FUNC.updatePortInfo("PRES3", 12153, iRunNumber, className);
			FUNC.updatePortInfo("PSCN3", 12154, iRunNumber, className);
			FUNC.updatePortInfo("SENA3", 12155, iRunNumber, className);
			FUNC.updatePortInfo("SSCG3", 12156, iRunNumber, className);
			FUNC.updatePortInfo("SOEL3", 12157, iRunNumber, className);
			FUNC.updatePortInfo("SWPS3", 12158, iRunNumber, className);
			FUNC.updatePortInfo("SPIR3", 12159, iRunNumber, className);
			FUNC.updatePortInfo("SMOI3", 12160, iRunNumber, className);
			FUNC.updatePortInfo("SPTL3", 12161, iRunNumber, className);
			FUNC.updatePortInfo("TAPS3", 12162, iRunNumber, className);
			FUNC.updatePortInfo("TARG3", 12163, iRunNumber, className);
			FUNC.updatePortInfo("TEDE3", 12164, iRunNumber, className);
			FUNC.updatePortInfo("TEAI3", 12165, iRunNumber, className);
			FUNC.updatePortInfo("TPCL3", 12166, iRunNumber, className);
			FUNC.updatePortInfo("TWIN3", 12167, iRunNumber, className);
			FUNC.updatePortInfo("WGLM3", 12168, iRunNumber, className);
			FUNC.updatePortInfo("WERL3", 12169, iRunNumber, className);
			FUNC.updatePortInfo("WPLC3", 12170, iRunNumber, className);
			FUNC.updatePortInfo("WFSI3", 12171, iRunNumber, className);
			FUNC.updatePortInfo("WTGG3", 12172, iRunNumber, className);
			FUNC.updatePortInfo("CCMT3", 12189, iRunNumber, className);
			FUNC.updatePortInfo("GLGT3", 12190, iRunNumber, className);
			FUNC.updatePortInfo("INGX3", 12191, iRunNumber, className);
			FUNC.updatePortInfo("MWPL3", 12192, iRunNumber, className);
			FUNC.updatePortInfo("ETPL3", 12193, iRunNumber, className);
			FUNC.updatePortInfo("ONGC3", 12194, iRunNumber, className);
			FUNC.updatePortInfo("OGSL3", 12195, iRunNumber, className);
			FUNC.updatePortInfo("WASH3", 12196, iRunNumber, className);
			FUNC.updatePortInfo("VECT3", 12253, iRunNumber, className);
			FUNC.updatePortInfo("NEXT3", 12254, iRunNumber, className);
			FUNC.updatePortInfo("PSCO3", 12255, iRunNumber, className);
			FUNC.updatePortInfo("SYMM3", 12256, iRunNumber, className);
			FUNC.updatePortInfo("TEXL3", 12257, iRunNumber, className);
			FUNC.updatePortInfo("EPOL3", 12258, iRunNumber, className);
			FUNC.updatePortInfo("CABE3", 12279, iRunNumber, className);
			FUNC.updatePortInfo("COLO3", 12280, iRunNumber, className);
			FUNC.updatePortInfo("MANS3", 12281, iRunNumber, className);
			FUNC.updatePortInfo("CHEM3", 12306, iRunNumber, className);
			FUNC.updatePortInfo("CONT3", 12307, iRunNumber, className);
			FUNC.updatePortInfo("DXTC3", 12308, iRunNumber, className);
			FUNC.updatePortInfo("INTR3", 12309, iRunNumber, className);
			FUNC.updatePortInfo("BPCE3", 12323, iRunNumber, className);
			FUNC.updatePortInfo("LRES3", 12337, iRunNumber, className);
			FUNC.updatePortInfo("UNTD3", 12356, iRunNumber, className);
			FUNC.updatePortInfo("DEVO3", 12361, iRunNumber, className);
			FUNC.updatePortInfo("EVMI3", 12388, iRunNumber, className);
			FUNC.updatePortInfo("EVMW3", 12389, iRunNumber, className);
			FUNC.updatePortInfo("GUNV3", 12390, iRunNumber, className);
			FUNC.updatePortInfo("PPEC3", 12391, iRunNumber, className);
			FUNC.updatePortInfo("TCEM3", 12430, iRunNumber, className);
			FUNC.updatePortInfo("CEGL3", 12431, iRunNumber, className);
			FUNC.updatePortInfo("OCCI3", 12432, iRunNumber, className);
			FUNC.updatePortInfo("SIXO3", 12433, iRunNumber, className);
			FUNC.updatePortInfo("AGUA3", 12437, iRunNumber, className);
			FUNC.updatePortInfo("DKTS3", 12438, iRunNumber, className);
			FUNC.updatePortInfo("PION3", 12447, iRunNumber, className);
			FUNC.updatePortInfo("MINN3", 12454, iRunNumber, className);
			FUNC.updatePortInfo("RENA3", 12508, iRunNumber, className);
			FUNC.updatePortInfo("ANRP4", 12517, iRunNumber, className);
			FUNC.updatePortInfo("WASH4", 12518, iRunNumber, className);
			FUNC.updatePortInfo("NGPC4", 12519, iRunNumber, className);
			FUNC.updatePortInfo("TOUR3", 12520, iRunNumber, className);
			FUNC.updatePortInfo("TGPL3", 12526, iRunNumber, className);
			FUNC.updatePortInfo("ENGM3", 12528, iRunNumber, className);
			FUNC.updatePortInfo("ARKC3", 12535, iRunNumber, className);
			FUNC.updatePortInfo("RADI3", 12557, iRunNumber, className);
			FUNC.updatePortInfo("MGEC3", 12586, iRunNumber, className);
			FUNC.updatePortInfo("WWML3", 12595, iRunNumber, className);
			FUNC.updatePortInfo("DEBM3", 12596, iRunNumber, className);
			FUNC.updatePortInfo("GSPC3", 12597, iRunNumber, className);
			FUNC.updatePortInfo("PVGS3", 12598, iRunNumber, className);
			FUNC.updatePortInfo("FREE3", 12599, iRunNumber, className);
			FUNC.updatePortInfo("GSPC4", 12600, iRunNumber, className);
			FUNC.updatePortInfo("PVGS4", 12601, iRunNumber, className);
			FUNC.updatePortInfo("FREE4", 12602, iRunNumber, className);
			FUNC.updatePortInfo("TVAL3", 12603, iRunNumber, className);
			FUNC.updatePortInfo("KAIM3", 12604, iRunNumber, className);
			FUNC.updatePortInfo("MMPT3", 12629, iRunNumber, className);
			FUNC.updatePortInfo("TXGT3", 12645, iRunNumber, className);
			FUNC.updatePortInfo("CCLL3", 12703, iRunNumber, className);
			FUNC.updatePortInfo("CGTL3", 12704, iRunNumber, className);
			FUNC.updatePortInfo("ARUL3", 12717, iRunNumber, className);
			FUNC.updatePortInfo("MEXG3", 12720, iRunNumber, className);
			FUNC.updatePortInfo("NSSA3", 12813, iRunNumber, className);
			FUNC.updatePortInfo("ENGE3", 12816, iRunNumber, className);
			FUNC.updatePortInfo("BEAN3", 12817, iRunNumber, className);
			FUNC.updatePortInfo("TIDA3", 12955, iRunNumber, className);
			FUNC.updatePortInfo("ARON3", 12964, iRunNumber, className);
			FUNC.updatePortInfo("SWNE3", 12965, iRunNumber, className);
			FUNC.updatePortInfo("TENN3", 12966, iRunNumber, className);
			FUNC.updatePortInfo("FORE3", 12967, iRunNumber, className);
			FUNC.updatePortInfo("PATH3", 12968, iRunNumber, className);
			FUNC.updatePortInfo("RANG3", 12969, iRunNumber, className);
			FUNC.updatePortInfo("GCCS3", 12976, iRunNumber, className);
			FUNC.updatePortInfo("LOUT3", 12977, iRunNumber, className);
			FUNC.updatePortInfo("OZAR3", 12978, iRunNumber, className);
			FUNC.updatePortInfo("MUMA3", 12993, iRunNumber, className);
			FUNC.updatePortInfo("SABI3", 12994, iRunNumber, className);
			FUNC.updatePortInfo("CALE3", 12995, iRunNumber, className);
			FUNC.updatePortInfo("ENES3", 12999, iRunNumber, className);
			FUNC.updatePortInfo("GOOS3", 13000, iRunNumber, className);
			FUNC.updatePortInfo("GOOS4", 13001, iRunNumber, className);
			FUNC.updatePortInfo("TGNR3", 13004, iRunNumber, className);
			FUNC.updatePortInfo("NETM3", 13005, iRunNumber, className);
			FUNC.updatePortInfo("SAAV3", 13007, iRunNumber, className);
			FUNC.updatePortInfo("JERS3", 13022, iRunNumber, className);
			FUNC.updatePortInfo("INTER3", 13023, iRunNumber, className);
			FUNC.updatePortInfo("FCOM3", 13033, iRunNumber, className);
			FUNC.updatePortInfo("TVLL3", 13034, iRunNumber, className);
			FUNC.updatePortInfo("EAST3", 13035, iRunNumber, className);
			FUNC.updatePortInfo("WELL3", 13047, iRunNumber, className);
			FUNC.updatePortInfo("ELOU3", 13048, iRunNumber, className);
			FUNC.updatePortInfo("MESL3", 13066, iRunNumber, className);
			FUNC.updatePortInfo("GLFRN3", 13100, iRunNumber, className);
			FUNC.updatePortInfo("ENTNO3", 13101, iRunNumber, className);
			FUNC.updatePortInfo("ENTMS3", 13102, iRunNumber, className);
			FUNC.updatePortInfo("ENTTX3", 13103, iRunNumber, className);
			FUNC.updatePortInfo("ENTAR3", 13104, iRunNumber, className);
			FUNC.updatePortInfo("WORS3", 13115, iRunNumber, className);
			FUNC.updatePortInfo("PGEC3", 13116, iRunNumber, className);
			FUNC.updatePortInfo("MACQ3", 13117, iRunNumber, className);
			FUNC.updatePortInfo("GTRI3", 13118, iRunNumber, className);
			FUNC.updatePortInfo("GTRI4", 13119, iRunNumber, className);
			FUNC.updatePortInfo("WORS4", 13125, iRunNumber, className);
			FUNC.updatePortInfo("LODI3", 13126, iRunNumber, className);
			FUNC.updatePortInfo("LODI4", 13127, iRunNumber, className);
			FUNC.updatePortInfo("ANEW3", 13128, iRunNumber, className);
			FUNC.updatePortInfo("IVEP3", 13149, iRunNumber, className);
			FUNC.updatePortInfo("SBOW3", 13150, iRunNumber, className);
			FUNC.updatePortInfo("TRES3", 13151, iRunNumber, className);
			FUNC.updatePortInfo("TRAF3", 13247, iRunNumber, className);
			FUNC.updatePortInfo("NEXU3", 13248, iRunNumber, className);
			FUNC.updatePortInfo("EQNX3", 13250, iRunNumber, className);
			FUNC.updatePortInfo("APCH3", 13270, iRunNumber, className);
			FUNC.updatePortInfo("COKI3", 13271, iRunNumber, className);
			FUNC.updatePortInfo("MONT3", 13276, iRunNumber, className);
			FUNC.updatePortInfo("CGTM3", 13282, iRunNumber, className);
			FUNC.updatePortInfo("GLNG3", 13283, iRunNumber, className);
			FUNC.updatePortInfo("VCPL3", 13284, iRunNumber, className);
			FUNC.updatePortInfo("FGTC3", 13285, iRunNumber, className);
			FUNC.updatePortInfo("ESGL3", 13287, iRunNumber, className);
			FUNC.updatePortInfo("CSTK3", 13289, iRunNumber, className);
			FUNC.updatePortInfo("COPQ3", 13293, iRunNumber, className);
			FUNC.updatePortInfo("UECA3", 13296, iRunNumber, className);
			FUNC.updatePortInfo("DEML3", 13302, iRunNumber, className);
			FUNC.updatePortInfo("SMEC3", 13307, iRunNumber, className);
			FUNC.updatePortInfo("BRMS3", 13308, iRunNumber, className);

   
			
		} catch (Exception e) {
			// do not log this
		}
	}
	 
	

	public  void updateIntLEPartyInfo(String sLegalEntityName, String sSubsidiary, int iSubsidiaryID, int iRunNumber, String className) throws OException {

		FUNC.updateIntLEPartyInfo(sLegalEntityName, sSubsidiary, iSubsidiaryID, iRunNumber, className);
	}
	public  void updateIntBUPartyInfo(String sBusinessUnitName, String sTradingDesk, int iTradingDeskID, int iRunNumber, String className) throws OException {

		FUNC.updateIntBUPartyInfo(sBusinessUnitName, sTradingDesk, iTradingDeskID,iRunNumber,className);
	}
	public  void updateExtLEPartyInfo(String sLegalEntityName, String sClearingFirm, int iClearingFirmID, int iRunNumber, String className) throws OException {

		FUNC.updateExtLEPartyInfo(sLegalEntityName, sClearingFirm, iClearingFirmID, iRunNumber, className);
	}
	public  void updateExtBUPartyInfo(String sBusinessUnitName, String sClearingShortName, int iClearingFirmID, String sClearingAccount, int iClearingAccountID, int iRunNumber, String className) throws OException {

		FUNC.updateExtBUPartyInfo(sBusinessUnitName, sClearingShortName, iClearingFirmID,sClearingAccount,iClearingAccountID, iRunNumber, className);
	}
	
	public static class FUNC {
 		
		public static void updateIntLEPartyInfo(String sLegalEntityName, String sSubsidiary, int iSubsidiaryID, int iRunNumber, String className) throws OException {
			try {
	 
				int iLegalEntityID = Ref.getValue(SHM_USR_TABLES_ENUM.PARTY_TABLE, sLegalEntityName);

				if (iLegalEntityID < 1) {
					
					LIB.log("*ERROR*:  Party ID not found for Legal Entity: " + "'" + sLegalEntityName + "'", className);

				}

				
				if (iLegalEntityID > 0) {
					LIB.log("Running for Legal Entity: " + "'" + sLegalEntityName + "'" + ", with Party ID: " + iLegalEntityID , className);
 
					//  0=Legal Entity, 1=Business Unit. 
					final int LEGAL_ENTITY = 0;
					final int BUSINESS_UNIT = 1;
					
					// will need this later to link up to the Legal Entity
			//		int iBusinessUnitPartyId = 0;
 

					{
						int party_class = LEGAL_ENTITY;
											

						if (iLegalEntityID >= 1) {
							Table tPartyInsert = Ref.retrieveParty(iLegalEntityID);    	
							// Part 1 of 2
							{
		
								final int iPartyInfoTypeID = PARTY_INFO.PARTY_INFO_SUBSIDIARY;
								try {
									Table tPartyInfo = LIB.safeGetTable(tPartyInsert, "party_info", CONST_VALUES.ROW_TO_GET_OR_SET);

									
									// delete if there already
									tPartyInfo.deleteWhereValue(1, iPartyInfoTypeID);
									
									int iMaxRow = tPartyInfo.addRow();
									tPartyInfo.setInt(1, iMaxRow, iPartyInfoTypeID);
									tPartyInfo.setString(2, iMaxRow, sSubsidiary);
								} catch (Exception e) {
									LIB.log("adding/setting Party Info for type id: " + iPartyInfoTypeID, e, className);
								}
 								
							}

							// Part 2 of 2
							{
								
								final int iPartyInfoTypeID = PARTY_INFO.PARTY_INFO_SUBSIDIARY_ID;
								try {
									Table tPartyInfo = LIB.safeGetTable(tPartyInsert, "party_info", CONST_VALUES.ROW_TO_GET_OR_SET);

									
									// delete if there already
									tPartyInfo.deleteWhereValue(1, iPartyInfoTypeID);
									
									int iMaxRow = tPartyInfo.addRow();
									tPartyInfo.setInt(1, iMaxRow, iPartyInfoTypeID);
									tPartyInfo.setString(2, iMaxRow, Str.intToStr(iSubsidiaryID));
								} catch (Exception e) {
									LIB.log("adding/setting Party Info for type id: " + iPartyInfoTypeID, e, className);
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
								LIB.viewTable(tPartyInsert, "LE Retrived party Table");
							}
						}

			 
					}

				} // END 			
				
				
			} catch (Exception e) {
				LIB.log("createPartyGroupAndBUandLE", e, className);
			}	 
		}
		
		public static void updateIntBUPartyInfo(String sBusinessUnitName, String sTradingDesk, int iTradingDeskID, int iRunNumber, String className) throws OException {
			try {
	 
				int iBusinessUnitID = Ref.getValue(SHM_USR_TABLES_ENUM.PARTY_TABLE, sBusinessUnitName);

				if (iBusinessUnitID < 1) {
					
					LIB.log("*ERROR*:  Party ID not found for Business Unit: " + "'" + sBusinessUnitName + "'", className);

				}

				
				if (iBusinessUnitID > 0) {
					LIB.log("Running for Business Unit: " + "'" + sBusinessUnitName + "'" + ", with Party ID: " + iBusinessUnitID , className);
 
					//  0=Legal Entity, 1=Business Unit. 
					final int LEGAL_ENTITY = 0;
					final int BUSINESS_UNIT = 1;
					
					// will need this later to link up to the Legal Entity
			//		int iBusinessUnitPartyId = 0;
 

					{
						int party_class = BUSINESS_UNIT;
											

						if (iBusinessUnitID >= 1) {
							Table tPartyInsert = Ref.retrieveParty(iBusinessUnitID);    	
							// Part 1 of 2
							{
		
								final int iPartyInfoTypeID = PARTY_INFO.PARTY_INFO_TRADING_DESK;
								try {
									Table tPartyInfo = LIB.safeGetTable(tPartyInsert, "party_info", CONST_VALUES.ROW_TO_GET_OR_SET);

									
									// delete if there already
									tPartyInfo.deleteWhereValue(1, iPartyInfoTypeID);
									
									int iMaxRow = tPartyInfo.addRow();
									tPartyInfo.setInt(1, iMaxRow, iPartyInfoTypeID);
									tPartyInfo.setString(2, iMaxRow, sTradingDesk);
								} catch (Exception e) {
									LIB.log("adding/setting Party Info for type id: " + iPartyInfoTypeID, e, className);
								}
 								
							}

							// Part 2 of 2
							{
								
								final int iPartyInfoTypeID = PARTY_INFO.PARTY_INFO_TRADING_DESK_ID;
								try {
									Table tPartyInfo = LIB.safeGetTable(tPartyInsert, "party_info", CONST_VALUES.ROW_TO_GET_OR_SET);

									
									// delete if there already
									tPartyInfo.deleteWhereValue(1, iPartyInfoTypeID);
									
									int iMaxRow = tPartyInfo.addRow();
									tPartyInfo.setInt(1, iMaxRow, iPartyInfoTypeID);
									tPartyInfo.setString(2, iMaxRow, Str.intToStr(iTradingDeskID));
								} catch (Exception e) {
									LIB.log("adding/setting Party Info for type id: " + iPartyInfoTypeID, e, className);
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
		
		public static void updateExtLEPartyInfo(String sLegalEntityName, String sClearingFirm, int iClearingFirmID, int iRunNumber, String className) throws OException {
			try {
	 
				int iLegalEntityID = Ref.getValue(SHM_USR_TABLES_ENUM.PARTY_TABLE, sLegalEntityName);

				if (iLegalEntityID < 1) {
					
					LIB.log("*ERROR*:  Party ID not found for Legal Entity: " + "'" + sLegalEntityName + "'", className);

				}

				
				if (iLegalEntityID > 0) {
					LIB.log("Running for Legal Entity: " + "'" + sLegalEntityName + "'" + ", with Party ID: " + iLegalEntityID , className);
 
					//  0=Legal Entity, 1=Business Unit. 
					final int LEGAL_ENTITY = 0;
					final int BUSINESS_UNIT = 1;
					
					// will need this later to link up to the Legal Entity
			//		int iBusinessUnitPartyId = 0;
 

					{
						int party_class = LEGAL_ENTITY;
											

						if (iLegalEntityID >= 1) {
							Table tPartyInsert = Ref.retrieveParty(iLegalEntityID);    	
							// Part 1 of 2
							{
		
								final int iPartyInfoTypeID = PARTY_INFO.PARTY_INFO_CLEARING_FIRM;
								try {
									Table tPartyInfo = LIB.safeGetTable(tPartyInsert, "party_info", CONST_VALUES.ROW_TO_GET_OR_SET);

									
									// delete if there already
									tPartyInfo.deleteWhereValue(1, iPartyInfoTypeID);
									
									int iMaxRow = tPartyInfo.addRow();
									tPartyInfo.setInt(1, iMaxRow, iPartyInfoTypeID);
									tPartyInfo.setString(2, iMaxRow, sClearingFirm);
								} catch (Exception e) {
									LIB.log("adding/setting Party Info for type id: " + iPartyInfoTypeID, e, className);
								}
 								
							}

							// Part 2 of 2
							{
								
								final int iPartyInfoTypeID = PARTY_INFO.PARTY_INFO_CLEARING_FIRM_ID_LE;
								try {
									Table tPartyInfo = LIB.safeGetTable(tPartyInsert, "party_info", CONST_VALUES.ROW_TO_GET_OR_SET);

									
									// delete if there already
									tPartyInfo.deleteWhereValue(1, iPartyInfoTypeID);
									
									int iMaxRow = tPartyInfo.addRow();
									tPartyInfo.setInt(1, iMaxRow, iPartyInfoTypeID);
									tPartyInfo.setString(2, iMaxRow, Str.intToStr(iClearingFirmID));
								} catch (Exception e) {
									LIB.log("adding/setting Party Info for type id: " + iPartyInfoTypeID, e, className);
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
								LIB.viewTable(tPartyInsert, "LE Retrived party Table");
							}
						}

			 
					}

				} // END 			
				
				
			} catch (Exception e) {
				LIB.log("createPartyGroupAndBUandLE", e, className);
			}	 
		}
		
		public static void updateExtBUPartyInfo(String sBusinessUnitName, String sClearingShortName, int iClearingFirmID, String sClearingAccount, int iClearingAccountID, int iRunNumber, String className) throws OException {
			try {
	 
				int iBusinessUnitID = Ref.getValue(SHM_USR_TABLES_ENUM.PARTY_TABLE, sBusinessUnitName);

				if (iBusinessUnitID < 1) {
					
					LIB.log("*ERROR*:  Party ID not found for Business Unit: " + "'" + sBusinessUnitName + "'", className);

				}

				
				if (iBusinessUnitID > 0) {
					LIB.log("Running for Business Unit: " + "'" + sBusinessUnitName + "'" + ", with Party ID: " + iBusinessUnitID , className);
 
					//  0=Legal Entity, 1=Business Unit. 
					final int LEGAL_ENTITY = 0;
					final int BUSINESS_UNIT = 1;
					
					// will need this later to link up to the Legal Entity
			//		int iBusinessUnitPartyId = 0;
 

					{
						int party_class = BUSINESS_UNIT;
											

						if (iBusinessUnitID >= 1) {
							Table tPartyInsert = Ref.retrieveParty(iBusinessUnitID);    	
							// Part 1 of 4
							{
		
								final int iPartyInfoTypeID = PARTY_INFO.PARTY_INFO_CLEARING_SHORT_NAME;
								try {
									Table tPartyInfo = LIB.safeGetTable(tPartyInsert, "party_info", CONST_VALUES.ROW_TO_GET_OR_SET);

									
									// delete if there already
									tPartyInfo.deleteWhereValue(1, iPartyInfoTypeID);
									
									int iMaxRow = tPartyInfo.addRow();
									tPartyInfo.setInt(1, iMaxRow, iPartyInfoTypeID);
									tPartyInfo.setString(2, iMaxRow, sClearingShortName);
								} catch (Exception e) {
									LIB.log("adding/setting Party Info for type id: " + iPartyInfoTypeID, e, className);
								}
 								
							}

							// Part 2 of 4
							{
								
								final int iPartyInfoTypeID = PARTY_INFO.PARTY_INFO_CLEARING_FIRM_ID_BU;
								try {
									Table tPartyInfo = LIB.safeGetTable(tPartyInsert, "party_info", CONST_VALUES.ROW_TO_GET_OR_SET);

									
									// delete if there already
									tPartyInfo.deleteWhereValue(1, iPartyInfoTypeID);
									
									int iMaxRow = tPartyInfo.addRow();
									tPartyInfo.setInt(1, iMaxRow, iPartyInfoTypeID);
									tPartyInfo.setString(2, iMaxRow, Str.intToStr(iClearingFirmID));
								} catch (Exception e) {
									LIB.log("adding/setting Party Info for type id: " + iPartyInfoTypeID, e, className);
								}
 								
							}
							// Part 3 of 4
							{
		
								final int iPartyInfoTypeID = PARTY_INFO.PARTY_INFO_CLEARING_ACCOUNT;
								try {
									Table tPartyInfo = LIB.safeGetTable(tPartyInsert, "party_info", CONST_VALUES.ROW_TO_GET_OR_SET);

									
									// delete if there already
									tPartyInfo.deleteWhereValue(1, iPartyInfoTypeID);
									
									int iMaxRow = tPartyInfo.addRow();
									tPartyInfo.setInt(1, iMaxRow, iPartyInfoTypeID);
									tPartyInfo.setString(2, iMaxRow, sClearingAccount);
								} catch (Exception e) {
									LIB.log("adding/setting Party Info for type id: " + iPartyInfoTypeID, e, className);
								}
 								
							}

							// Part 4 of 4
							{
								
								final int iPartyInfoTypeID = PARTY_INFO.PARTY_INFO_CLEARING_ACCOUNT_ID;
								try {
									Table tPartyInfo = LIB.safeGetTable(tPartyInsert, "party_info", CONST_VALUES.ROW_TO_GET_OR_SET);

									
									// delete if there already
									tPartyInfo.deleteWhereValue(1, iPartyInfoTypeID);
									
									int iMaxRow = tPartyInfo.addRow();
									tPartyInfo.setInt(1, iMaxRow, iPartyInfoTypeID);
									tPartyInfo.setString(2, iMaxRow, Str.intToStr(iClearingAccountID));
								} catch (Exception e) {
									LIB.log("adding/setting Party Info for type id: " + iPartyInfoTypeID, e, className);
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
								LIB.viewTable(tPartyInsert, "LE Retrived party Table");
							}
						}

			 
					}

				} // END 			
				
				
			} catch (Exception e) {
				LIB.log("createPartyGroupAndBUandLE", e, className);
			}	 
		}
			
		public static void updatePortInfo(String sPortName, int iClearingAccountID, int iRunNumber, String className) throws OException {
			try {
	 
				int iPortID = Ref.getValue(SHM_USR_TABLES_ENUM.PORTFOLIO_TABLE, sPortName);

				if (iPortID < 1) {
					
					LIB.log("*ERROR*:  Portfolio ID not found for portfolio: " + "'" + sPortName + "'", className);

				}

				
				if (iPortID > 0) {
					LIB.log("Running for Portfolio: " + "'" + sPortName + "'" + ", with Portfolio ID: " + iPortID , className);

					{					
						if (iPortID >= 1) {
							Table tPortInsert = Ref.retrievePortfolio(iPortID);    	
							// Part 1 of 1
							{
		
								final int iPortInfoTypeID = PORT_INFO.PORT_INFO_ACCOUNT_ID;
								try {
									Table tPortInfo = LIB.safeGetTable(tPortInsert, "portfolio_info", CONST_VALUES.ROW_TO_GET_OR_SET);

									
									// delete if there already
									tPortInfo.deleteWhereValue(1, iPortInfoTypeID);
									
									int iMaxRow = tPortInfo.addRow();
									tPortInfo.setInt(1, iMaxRow, iPortInfoTypeID);
									tPortInfo.setString(2, iMaxRow, Str.intToStr(iClearingAccountID));
								} catch (Exception e) {
									LIB.log("adding/setting Party Info for type id: " + iPortInfoTypeID, e, className);
								}
 								
							}
							
							try {
							//	int retval = 0;
							 int	  retval = Ref.updatePortfolio(tPortInsert);
								LIB.log("Ref.updatePortfolio retval: " + retval, className);
							} catch (Exception e) {
								LIB.log("Ref.updatePortfolio", e, className);
							}
							
							if (bExtraDebug == true) {
								LIB.viewTable(tPortInsert, "Retrived portfolio Table");
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
	

	public static class PARTY_INFO {
		//Ext BU
		final static int PARTY_INFO_CLEARING_SHORT_NAME = 20001;
		final static int PARTY_INFO_CLEARING_FIRM_ID_BU = 20012;
		final static int PARTY_INFO_CLEARING_ACCOUNT = 20009;
		final static int PARTY_INFO_CLEARING_ACCOUNT_ID= 20011;
		//Ext LE
		final static int PARTY_INFO_CLEARING_FIRM = 20008;
		final static int PARTY_INFO_CLEARING_FIRM_ID_LE = 20004;
		//Int BU
		final static int PARTY_INFO_TRADING_DESK = 20013;
		final static int PARTY_INFO_TRADING_DESK_ID = 20006;
		//Int LE
		final static int PARTY_INFO_SUBSIDIARY = 20014;
		final static int PARTY_INFO_SUBSIDIARY_ID = 20005;
	}
	
	public static class PORT_INFO {
		final static int PORT_INFO_ACCOUNT_ID = 20001;
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

	public static final String VERSION_NUMBER = "V1.004 (24May2023)";

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
