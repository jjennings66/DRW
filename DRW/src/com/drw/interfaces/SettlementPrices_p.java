package com.drw.interfaces;

import com.olf.openjvs.Ask;
import com.olf.openjvs.IContainerContext;
import com.olf.openjvs.IScript;
import com.olf.openjvs.OConsole;
import com.olf.openjvs.OException;
import com.olf.openjvs.Table;
import com.olf.openjvs.enums.ASK_TEXT_DATA_TYPES;
import com.olf.openjvs.enums.COL_TYPE_ENUM;

public class SettlementPrices_p implements IScript {

	@Override
	public void execute(IContainerContext context) throws OException {
		Table tAsk = null;
		Table tArg = null;
		try {
            tAsk = Table.tableNew();
            setFileLocation(tAsk);
            
            Table tFileLocation = tAsk.getTable("return_value", 1);
            String sFileLocation = tFileLocation.getString("return_value", 1);
            
            tArg = context.getArgumentsTable();
            tArg.addCol("import_file_path", COL_TYPE_ENUM.COL_STRING);
            int iMaxRow = tArg.addRow();
            tArg.setString("import_file_path", iMaxRow, sFileLocation);
		}
		catch (Exception e) {
    		OConsole.oprint("Error: " + e.getLocalizedMessage());
		}
		finally {
			tAsk.destroy();
		}
	}
	private void setFileLocation(Table tAsk) throws OException {
		int iRetVal = -1;
		try {
			Ask.setTextEdit(tAsk, "File Path", "C:\\", ASK_TEXT_DATA_TYPES.ASK_FILENAME, "Enter Settle Prices Import File Path", 1, 2);
			iRetVal = Ask.viewTable(tAsk, "Settle Prices Import", "Settle Prices Import");

			if(iRetVal <= 0)
            {
                    OConsole.oprint("\nUser pressed cancel. Aborting...");
                    return;
            }

		}
		catch (Exception e) {
    		OConsole.oprint("Error: " + e.getLocalizedMessage());
		}
		
	}
}
