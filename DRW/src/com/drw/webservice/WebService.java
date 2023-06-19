package com.drw.webservice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import com.olf.openjvs.OConsole;
import com.olf.openjvs.OException;
import com.olf.openjvs.Str;
import com.olf.openjvs.Table;
import com.olf.openjvs.enums.COL_TYPE_ENUM;

public class WebService {

	private URL oURL;
	private HttpURLConnection oConnection;
	
	public WebService(String sEndPoint) throws OException, IOException
	{
		try {
			oURL = new URL(sEndPoint);
			oConnection = (HttpURLConnection) oURL.openConnection();
		} 
		catch (Exception e) {
			OConsole.oprint("Error: " + e.getLocalizedMessage());
		}
	}
	
	public void setConnectionProperties(String sUsername, String sPassword, WebRequestMethod oRequestMethod, 
			WebRequestContentType oRequestContentType, boolean bDoOutput) throws OException {
		String sAuthorization = "";
		
		try {
			 // Set the necessary headers
			if ((Str.isNull(sUsername) == 0 && Str.isEmpty(sUsername) == 0)
					&& (Str.isNull(sPassword) == 0 && Str.isEmpty(sPassword) == 0)) {
				sAuthorization = sUsername + ":" + sPassword;
				byte[] encodedAuth = Base64.getEncoder().encode(sAuthorization.getBytes(StandardCharsets.UTF_8));
				String authHeaderValue = "Basic " + new String(encodedAuth);
				oConnection.setRequestProperty("Authorization", authHeaderValue);
			}
			
			oConnection.setRequestMethod(oRequestMethod.getValue());
			oConnection.setRequestProperty("Content-Type", oRequestContentType.getValue());
			oConnection.setDoOutput(bDoOutput);
		} 
		catch (Exception e) {
			OConsole.oprint("Error: " + e.getLocalizedMessage());
		}		
	}
	public Table sendPostRequest(String sRequestMessage) throws OException {
		Table tReturnMessage = null;
		
		try {
			// Send the request
			OutputStream oOutputStream = oConnection.getOutputStream();
			oOutputStream.write(sRequestMessage.getBytes());
			oOutputStream.flush();
			oOutputStream.close();
			
			tReturnMessage = sendWebRequest();
		} 
		catch (Exception e) {
			OConsole.oprint("Error: " + e.getLocalizedMessage());
		}	
		return tReturnMessage;
	}
	
	public Table sendGetRequest() throws OException {
		Table tReturnMessage = null;
		
		try {
			tReturnMessage = sendWebRequest();
		} 
		catch (Exception e) {
			OConsole.oprint("Error: " + e.getLocalizedMessage());
		}	
		return tReturnMessage;
	}
	
	public Table sendWebRequest() throws Exception {
		String sReturnMessage = "";
		String sResponseLine = "";
		Table tReturnMessages = null;
		int iMaxRow = -1;
		int iResponseCode = -1;
		try {
			tReturnMessages = Table.tableNew();
			tReturnMessages.addCol("messages", COL_TYPE_ENUM.COL_STRING);
			
			iResponseCode = oConnection.getResponseCode();
			BufferedReader oBufferedReader;
			if (iResponseCode == HttpURLConnection.HTTP_OK) {
				oBufferedReader = new BufferedReader(new InputStreamReader(oConnection.getInputStream()));
			} else {
				oBufferedReader = new BufferedReader(new InputStreamReader(oConnection.getErrorStream()));
			}

			while ((sResponseLine = oBufferedReader.readLine()) != null) {
				iMaxRow = tReturnMessages.addRow();
				tReturnMessages.setString("messages", iMaxRow, sResponseLine);
			}
			oBufferedReader.close();
		} 
		catch (Exception e) {
			iMaxRow = tReturnMessages.addRow();
			sReturnMessage = "Error: " + e.getLocalizedMessage();
			OConsole.oprint(sReturnMessage);
			tReturnMessages.setString("messages", iMaxRow, sResponseLine);
		}
		return tReturnMessages;
	}
	public enum WebRequestMethod {
	    POST("POST"),
	    GET("GET");

	    private final String value;

	    WebRequestMethod(String value) {
	        this.value = value;
	    }

	    public String getValue() {
	        return value;
	    }
	}
	public enum WebRequestContentType {
	    JSON("application/json"),
	    TEXT_XML("text/xml; charset=utf-8"),
		TEXT_CSV("text/csv");
		
	    private final String value;

	    WebRequestContentType(String value) {
	        this.value = value;
	    }

	    public String getValue() {
	        return value;
	    }
	}
}
