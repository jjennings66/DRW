package com.drw.dashboard;
 
import com.drw.dashboard.DRW_EOD_DashboardBaseClass.LIB;
import com.drw.dashboard.DRW_EOD_DashboardBaseClass.REFRESH_INTERVAL;
import com.olf.openjvs.IContainerContext;
import com.olf.openjvs.IScript;
import com.olf.openjvs.OException;
import com.olf.openjvs.PluginCategory;
import com.olf.openjvs.PluginType;
import com.olf.openjvs.enums.SCRIPT_CATEGORY_ENUM;
import com.olf.openjvs.enums.SCRIPT_TYPE_ENUM;
import com.olf.openjvs.enums.SIMULATION_RUN_TYPE; 
 

/* 
   Name:  DRW_EOD_BatchSim_Dashboard.java
   
   Deployment Instructions
   Set this up as a 'Script Panel' in a 'Desktop'.
   
   This is an interactive viewing, one row per portfolio, of EOD Batch Sims.
   
Revision History

19-Apr-2023 Brian New script
 
 */

@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_GENERIC)
@PluginType(SCRIPT_TYPE_ENUM.MAIN_SCRIPT)
public class DRW_EOD_BatchSim_Dashboard implements IScript
{
    
    public void execute(IContainerContext context) throws OException
    {
    	String className = this.getClass().getSimpleName();
    	try {
    		final SIMULATION_RUN_TYPE revalRunType = SIMULATION_RUN_TYPE.EOD_SIM_TYPE;
    		final REFRESH_INTERVAL defaultRefreshInterval = REFRESH_INTERVAL.NO_REFRESH;
    		DRW_EOD_DashboardBaseClass.main(context, revalRunType, defaultRefreshInterval, className);
    		 
    	} catch (Exception e) {
    		LIB.log("execute", className);
    	}
    }     
}
