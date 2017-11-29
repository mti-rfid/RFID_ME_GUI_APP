package com.mti.rfid.minime;

public class Soap {
}
/*
import java.util.ArrayList;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.util.Log;

public class Soap {
	private static final String TAG = "james";
	private static final String WSDL_NAMESPACE = "http://axis2service.triple.ch.tw";
	private static final String METHOD = "rxlist";
	private static final String WSDL_URL = "http://192.168.2.17:8080/Axis2Service/services/Axis2Main?wsdl";

	public Soap(ArrayList<String> tagList) {
        SoapObject request = new SoapObject(WSDL_NAMESPACE, METHOD);
        
        for(String msgList : tagList)
        	request.addProperty("list", msgList);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
 		Log.d(TAG, "soap 1");
       
        envelope.bodyOut = request;

        HttpTransportSE httpTransport = new HttpTransportSE(WSDL_URL, 5000);
        httpTransport.debug = true;

        try {
        	httpTransport.call(null, envelope);
        	Object response = envelope.getResponse();
        } catch (Exception exception) {
        	exception.printStackTrace();
        }
	}
}
*/