package com.opservice;

import com.olf.openjvs.IContainerContext;
import com.olf.openjvs.IScript;
import com.olf.openjvs.OException;
import com.olf.openjvs.Str;
import com.olf.openjvs.Util;


/*
 
 Name: DRW_LoginWarning
 
 Description: Temporary script to warn people who log into the config database
 
 Last Updated:  20-Apr-2023
  
  
 */

public class DRW_LoginWarning implements IScript
{
    public void execute(IContainerContext context) throws OException  {
    	
    	try {
    		String sDatabase = Util.getEnv("AB_LOGON_DATABASE");

    		if (Str.iEqual(sDatabase, "config") == 1){
    			String sMessage = "*WARNING* This is the special 'CONFIG' database." + 
    		   "\n\nDo *NOT* log in this database without getting approval. Check with Dan Lucker or Brian Shydlo";
    			
    			// Allow override of 1 makes this a warning
    			int iAllowOverride = 1;
    			com.olf.openjvs.OpService.serviceFail(sMessage, iAllowOverride);
    		}

    		
    	} catch (Exception e) {
    		// do not log this
    	}
    }
}
