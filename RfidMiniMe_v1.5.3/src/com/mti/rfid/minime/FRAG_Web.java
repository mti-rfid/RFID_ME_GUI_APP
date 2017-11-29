package com.mti.rfid.minime;

import org.apache.http.util.EncodingUtils;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class FRAG_Web extends Fragment {
	private WebView wvContant;
	
	public static FRAG_Web newInstance(int index, String tagId) {
		FRAG_Web f = new FRAG_Web();
		
		Bundle args = new Bundle();
		args.putInt("index", index);
		args.putString("tagid", tagId);
		f.setArguments(args);
		
		return f;
	}
	
	private int getShownIndex() {
		return getArguments().getInt("index", 0);
	}
	
	private String getTagId() {
		return getArguments().getString("tagid");
	}

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SharedPreferences mSharedpref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
	    final String prefixUrl = mSharedpref.getString("cfg_web_url", "");
		
		View view = inflater.inflate(R.layout.frag_web, container, false);
		wvContant =(WebView) view.findViewById(R.id.wv_contant);
				
        WebSettings webSettings = wvContant.getSettings();
        webSettings.setJavaScriptEnabled(true);
        wvContant.setWebViewClient(new WebViewClient(){
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				wvContant.setWebViewClient(null);
			}
        });

//        if(prefixUrl.contains("?")) {						// #### GET  ####
        	wvContant.loadUrl(prefixUrl + getTagId());
//        } else {											// #### POST ####
//        	wvContant.postUrl(prefixUrl, EncodingUtils.getBytes("epc=" + getTagId(), "BASE64"));
//        }
		return view;
	}
}
