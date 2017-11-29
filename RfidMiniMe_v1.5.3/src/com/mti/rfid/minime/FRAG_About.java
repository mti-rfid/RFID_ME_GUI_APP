package com.mti.rfid.minime;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;

public class FRAG_About extends PreferenceFragment{
	public static FRAG_About newInstance() {
		FRAG_About f = new FRAG_About();
		
		return f;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.frag_about);
		SharedPreferences mSharedpref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

	    Preference prefSn = (Preference) getPreferenceScreen().findPreference("about_reader_sn");
	    prefSn.setSummary(mSharedpref.getString("about_reader_sn_sum", "n/a"));

	    Preference prefWeb = (Preference) getPreferenceScreen().findPreference("about_web");
	    prefWeb.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				if(preference.getKey().equals("about_web")) {
					Uri uri = Uri.parse(getString(R.string.about_web_sum));
					Intent intent = new Intent(Intent.ACTION_VIEW, uri);
					startActivity(intent);
				}
				return true;
			}
        });
	}
}
