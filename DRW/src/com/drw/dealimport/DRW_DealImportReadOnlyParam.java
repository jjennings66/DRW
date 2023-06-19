package com.drw.dealimport;
 
import com.olf.openjvs.IContainerContext;
import com.olf.openjvs.IScript;
import com.olf.openjvs.OException;
import com.olf.openjvs.PluginCategory;
import com.olf.openjvs.PluginType;
import com.olf.openjvs.Table;
import com.olf.openjvs.enums.COL_TYPE_ENUM;
import com.olf.openjvs.enums.SCRIPT_CATEGORY_ENUM;
import com.olf.openjvs.enums.SCRIPT_TYPE_ENUM;
 
/*
Script Name: DRW_DealImport
Description:  Param script for deal importing to set Read Only mode
    
Revision History
02-Jun-2023   Brian   New Script
                       
*/
 
@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_GENERIC)
@PluginType(SCRIPT_TYPE_ENUM.PARAM_SCRIPT)
public class DRW_DealImportReadOnlyParam implements IScript
{
    public void execute(IContainerContext context) throws OException
    {
    	Table argt = context.getArgumentsTable();
    	argt.addCol("read_only_flag", COL_TYPE_ENUM.COL_INT);
    	
    	if (argt.getNumRows() < 1) {
    		argt.addRow();
    	}
    	
    	final int TRUE = 1;
    	final int ROW_NUM_TO_SET = 1;
    	argt.setInt("read_only_flag", ROW_NUM_TO_SET, TRUE);
    }
}
