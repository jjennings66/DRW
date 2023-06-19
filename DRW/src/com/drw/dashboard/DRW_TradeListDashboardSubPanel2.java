package com.drw.dashboard;
 
import com.drw.dashboard.DRW_TradeListReportsLibrary.LIB;
import com.drw.dashboard.DRW_TradeListReportsLibrary.REPORT_NAME;
import com.olf.openjvs.IContainerContext;
import com.olf.openjvs.IScript;
import com.olf.openjvs.OException;
import com.olf.openjvs.PluginCategory;
import com.olf.openjvs.PluginType;
import com.olf.openjvs.enums.SCRIPT_CATEGORY_ENUM;
import com.olf.openjvs.enums.SCRIPT_TYPE_ENUM; 
  
/* 

Name:  DRW_TradeListDashboardSubPanel2.java

Deployment Instructions
Set this up as a 'Script Panel' in a 'Desktop'.

This is a 'Script Panel'for sub panels for the DRW Trade Listing Reports

Revision History 
23-Apr-2023 Brian New script
   
*/


@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_TRADE_INPUT)
@PluginType(SCRIPT_TYPE_ENUM.MAIN_SCRIPT)
public class DRW_TradeListDashboardSubPanel2 implements IScript
{   
    public void execute(IContainerContext context) throws OException
    {
    	String className = this.getClass().getSimpleName();
    	try {
    		  
    		// this means, to get the report to show from whatever the *PARENT* panel (the one with the pick-lists) said to show for 'Panel 2', which will be in the context (argt)
    		REPORT_NAME reportName = REPORT_NAME.SUB_PANEL2;
    		DRW_TradeListDashboardSubPanelBaseClass.doEverything(context, reportName, className);
    		
    	} catch (Exception e) {
    		LIB.log("execute", className);
    	}
    }      
}
