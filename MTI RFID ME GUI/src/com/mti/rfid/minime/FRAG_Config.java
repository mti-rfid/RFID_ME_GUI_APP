package com.mti.rfid.minime;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class FRAG_Config extends PreferenceFragment{
	public PreferenceScreen prefScr;
	
    private UsbCommunication mUsbCommunication = UsbCommunication.getInstance();
	private MtiCmd mMtiCmd;

	private TextView mUsbState;

	public static FRAG_Config newInstance() {
		FRAG_Config f = new FRAG_Config();
		return f;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.frag_config);
        
        prefScr = getPreferenceScreen();

		initListPreference("cfg_region");
		initListPreference("cfg_tag_mode");
		initEditTextPreference("cfg_pwr_level");
		initEditTextPreference("cfg_sen");
		initListPreference("cfg_link_freq");
		initListPreference("cfg_session");
		initListPreference("cfg_coding");
		initEditTextPreference("cfg_q_begin");
		initEditTextPreference("cfg_tid_length");
		initEditTextPreference("cfg_user_length");
		initCheckBoxPreference("cfg_sleep_mode");
		initEditTextPreference("cfg_inventory_times");
		initEditTextPreference("cfg_web_url");
		
		if(savedInstanceState == null) {
        	if(getUsbState()){
				getReaderRegion();
				getTagMode();
				getPowerLevel();
				get18c6Config();
				setPowerState();
			} else {
				Toast.makeText(getActivity(), "The Reader is not connected", Toast.LENGTH_SHORT).show();
			}
		}
	}

	
	private void initListPreference(final String strPrefName) {
        ListPreference lPref = (ListPreference) prefScr.findPreference(strPrefName);
        lPref.setSummary(lPref.getValue());
        
		lPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				ListPreference lPref = (ListPreference) preference;
				String strPref = newValue.toString();
					
				lPref.setValue(strPref);
				lPref.setSummary(strPref);
				
				if(strPrefName.equals("cfg_region"))
					setReaderRegion();
				else if(strPrefName.equals("cfg_tag_mode"))
					setTagMode();
				else if(strPrefName.equals("cfg_link_freq") || strPrefName.equals("cfg_session") || strPrefName.equals("cfg_coding"))
					set18c6Config();
				setPowerState();
				return false;
			}
		});
	}
	
	
	private void initEditTextPreference(final String strPrefName){
		EditTextPreference etPref = (EditTextPreference) prefScr.findPreference(strPrefName);
        etPref.setSummary(etPref.getText());
		etPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				EditTextPreference etPref = (EditTextPreference) preference;
				String strPref = newValue.toString();
				
				etPref.setText(strPref);
				etPref.setSummary(strPref);
				
				if(strPrefName.equals("cfg_pwr_level"))
					setPowerLevel();
				else if(strPrefName.equals("cfg_sen") || strPrefName.equals("cfg_q_begin"))
					set18c6Config();
				else if(strPrefName.equals("cfg_inventory_times")) {
//					setInventoryTimes();
				}
				setPowerState();
				return false;
			}
		});
	}


	private void initCheckBoxPreference(final String strPrefName) {
        CheckBoxPreference cbPref = (CheckBoxPreference) prefScr.findPreference(strPrefName);
        
		cbPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				if(strPrefName.equals("cfg_sleep_mode")) {
					if(getUsbState()){
						mMtiCmd = new CMD_PwrMgt.RFID_PowerEnterPowerState(mUsbCommunication);
						CMD_PwrMgt.RFID_PowerEnterPowerState finalCmd = (CMD_PwrMgt.RFID_PowerEnterPowerState) mMtiCmd;
						if((Boolean)newValue)
							finalCmd.setCmd(CMD_PwrMgt.PowerState.Sleep);
						else
							finalCmd.setCmd(CMD_PwrMgt.PowerState.Standby);
			        }
				}
				return true;
			}
		});
	}
	
	
	private void setReaderRegion() {
		mMtiCmd = new CMD_ModConf.RFID_RadioSetRegion(mUsbCommunication);
		CMD_ModConf.RFID_RadioSetRegion finalCmd = (CMD_ModConf.RFID_RadioSetRegion) mMtiCmd;
		
		ListPreference lPref = (ListPreference) prefScr.findPreference("cfg_region");

		if(finalCmd.setCmd((byte)lPref.findIndexOfValue(lPref.getValue()))) {
			lPref.setSummary(lPref.getValue());
		} else {
			getReaderRegion();
			// #### some module not support for this feature ####
//			Toast.makeText(getActivity(), "The region is not support.", Toast.LENGTH_SHORT).show();
		}
	}
	
	private void getReaderRegion() {
		mMtiCmd = new CMD_ModConf.RFID_RadioGetRegion(mUsbCommunication);
		CMD_ModConf.RFID_RadioGetRegion finalCmd = (CMD_ModConf.RFID_RadioGetRegion) mMtiCmd;

		if(finalCmd.setCmd()) {
			ListPreference lPref = (ListPreference) prefScr.findPreference("cfg_region");
        
			lPref.setValueIndex(finalCmd.getRegion());
			lPref.setSummary(lPref.getValue());
		} else {
			// #### process error ####
		}
	}

	private void setTagMode() {
		ListPreference lPref = (ListPreference) prefScr.findPreference("cfg_tag_mode");
		
		mUsbCommunication.setTagMode(lPref.findIndexOfValue(lPref.getValue()));
	}

	private void getTagMode() {
		ListPreference lPref = (ListPreference) prefScr.findPreference("cfg_tag_mode");
		
		lPref.setValueIndex(mUsbCommunication.getTagMode());
		lPref.setSummary(lPref.getValue());
	}

	
	private void setPowerLevel() {
		mMtiCmd = new CMD_AntPortOp.RFID_AntennaPortSetPowerLevel(mUsbCommunication);
		CMD_AntPortOp.RFID_AntennaPortSetPowerLevel finalCmd = (CMD_AntPortOp.RFID_AntennaPortSetPowerLevel) mMtiCmd;
		
		EditTextPreference etPref = (EditTextPreference) prefScr.findPreference("cfg_pwr_level");

		if(finalCmd.setCmd(Byte.parseByte(etPref.getText()))) {
			etPref.setSummary(etPref.getText());
		} else {
			getPowerLevel();
			// #### some module not support for this feature ####
//			Toast.makeText(getActivity(), "The Power is out of range.", Toast.LENGTH_SHORT).show();
		}
	}
	
	private void getPowerLevel() {
		mMtiCmd = new CMD_AntPortOp.RFID_AntennaPortGetPowerLevel(mUsbCommunication);
		CMD_AntPortOp.RFID_AntennaPortGetPowerLevel finalCmd = (CMD_AntPortOp.RFID_AntennaPortGetPowerLevel) mMtiCmd;

		if(finalCmd.setCmd()) {
			EditTextPreference etPref = (EditTextPreference) prefScr.findPreference("cfg_pwr_level");
        
			etPref.setText(String.valueOf(finalCmd.getPowerLevel()));
			etPref.setSummary(etPref.getText());
		} else {
			// #### process error ####
		}
	}
	
	private void set18c6Config() {
		EditTextPreference etPref;
		ListPreference lPref;
		
		mMtiCmd = new CMD_Iso18k6cTagAccess.RFID_18K6CSetQueryParameter(mUsbCommunication);
		CMD_Iso18k6cTagAccess.RFID_18K6CSetQueryParameter finalCmd = (CMD_Iso18k6cTagAccess.RFID_18K6CSetQueryParameter) mMtiCmd;
		
		etPref = (EditTextPreference) prefScr.findPreference("cfg_sen");
		byte bSensitivity = Byte.parseByte(etPref.getText());
		
		lPref = (ListPreference) prefScr.findPreference("cfg_link_freq");
		byte bLinkFreq = 0x0;
		switch(lPref.findIndexOfValue(lPref.getValue())) {
			case 0:
				bLinkFreq = 0x00; break;
			case 1:
				bLinkFreq = 0x06; break;
			case 2:
				bLinkFreq = 0x08; break;
			case 3:
				bLinkFreq = 0x09; break;
			case 4:
				bLinkFreq = 0x0C; break;
			case 5:
				bLinkFreq = 0x0F; break;
		}

		lPref = (ListPreference) prefScr.findPreference("cfg_session");
		byte bSession = (byte)lPref.findIndexOfValue(lPref.getValue());
		
		lPref = (ListPreference) prefScr.findPreference("cfg_coding");
		byte bCoding = (byte)lPref.findIndexOfValue(lPref.getValue());
		
		etPref = (EditTextPreference) prefScr.findPreference("cfg_q_begin");
		byte bQBegin = Byte.parseByte(etPref.getText());

		if(finalCmd.setCmd((byte)0x01, bLinkFreq, (byte)0x01, bCoding,
				(byte)0x01, bSession, (byte)0x00, (byte)0x00,
				(byte)0x01, bQBegin, (byte)0x01, bSensitivity)) {
		} else {
			// #### process error ####
		}
	}
	
	private void get18c6Config() {
		EditTextPreference etPref;
		ListPreference lPref;
		
		mMtiCmd = new CMD_Iso18k6cTagAccess.RFID_18K6CSetQueryParameter(mUsbCommunication);
		CMD_Iso18k6cTagAccess.RFID_18K6CSetQueryParameter finalCmd = (CMD_Iso18k6cTagAccess.RFID_18K6CSetQueryParameter) mMtiCmd;
		
		if(finalCmd.setCmd()) {
			String strTmp;
			
			etPref = (EditTextPreference) prefScr.findPreference("cfg_sen");
			strTmp = String.valueOf(finalCmd.getSensivity());
			etPref.setText(strTmp);
			etPref.setSummary(strTmp);
			
			lPref = (ListPreference) prefScr.findPreference("cfg_link_freq");
			switch(finalCmd.getLinkFrequency()) {
				case 0x00:
					lPref.setValueIndex(0); break;
				case 0x06:
					lPref.setValueIndex(1); break;
				case 0x08:
					lPref.setValueIndex(2); break;
				case 0x09:
					lPref.setValueIndex(3); break;
				case 0x0C:
					lPref.setValueIndex(4); break;
				case 0x0F:
					lPref.setValueIndex(5); break;
			}
			lPref.setSummary(lPref.getValue());

			lPref = (ListPreference) prefScr.findPreference("cfg_session");
			lPref.setValueIndex(finalCmd.getSession());
			lPref.setSummary(lPref.getValue());
						
			lPref = (ListPreference) prefScr.findPreference("cfg_coding");
			lPref.setValueIndex(finalCmd.getCoding());
			lPref.setSummary(lPref.getValue());
						
			etPref = (EditTextPreference) prefScr.findPreference("cfg_q_begin");
			etPref.setText(String.valueOf(finalCmd.getQBegin()));
			etPref.setSummary(etPref.getText());

		} else {
			// #### process error ####
		}
	}
	
	private void setPowerState() {
		if(getUsbState()) {
			mMtiCmd = new CMD_PwrMgt.RFID_PowerEnterPowerState(mUsbCommunication);
			CMD_PwrMgt.RFID_PowerEnterPowerState finalCmd = (CMD_PwrMgt.RFID_PowerEnterPowerState) mMtiCmd;
			CheckBoxPreference cbPref = (CheckBoxPreference) prefScr.findPreference("cfg_sleep_mode");

			if(cbPref.isChecked())
				finalCmd.setCmd(CMD_PwrMgt.PowerState.Sleep);
			else
				finalCmd.setCmd(CMD_PwrMgt.PowerState.Standby);
			
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {}
        }
	}

	private void setInventoryTimes() {
		EditTextPreference etPref = (EditTextPreference) prefScr.findPreference("cfg_inventory_times");
		if(Integer.parseInt(etPref.getSummary().toString()) > 50) {
			etPref.setText("50");
			etPref.setSummary("50");
		} 
	}
	
	private boolean getUsbState() {
        mUsbState = (TextView) getActivity().findViewById(R.id.tv_usbstate);
        return mUsbState.getText().equals("Connected");
	}
}
