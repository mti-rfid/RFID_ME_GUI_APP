package com.mti.rfid.minime;

import java.util.HashMap;
import java.util.Iterator;

import org.apache.http.util.EncodingUtils;

import com.mti.rfid.minime.CMD_PwrMgt.PowerState;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentManager.OnBackStackChangedListener;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class RfidContainer extends Activity implements FRAG_Tag.OnTagSelectedListener{
	private static final boolean DEBUG = false;
	private static final String ACTION_USB_PERMISSION = "com.mti.rfid.minime.USB_PERMISSION";
	private static final int PID = 49193;
	private static final int VID = 4901;

	private TextView tv_usbstate;
	private SharedPreferences mSharedpref;
	
	private enum Fragments {About, Config, Detail, Tag, Web};
	private FragmentTransaction ft;
	private Fragment objFragment;
	private boolean bSavedInst = false;

    private UsbCommunication mUsbCommunication = UsbCommunication.newInstance();
	private UsbManager mManager;
	private PendingIntent mPermissionIntent;
    
	private int iLayout;
	private int iMenu;
    	
	// #### activity ####
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        tv_usbstate = (TextView) findViewById(R.id.tv_usbstate);
        
        mManager = (UsbManager)getSystemService(Context.USB_SERVICE);
        mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);					// will intercept by system
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        filter.addAction(ACTION_USB_PERMISSION);
        registerReceiver(usbReceiver, filter);

        mSharedpref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        try {
        	findViewById(R.id.DetailLayout).getResources();
   			iLayout = R.id.DetailLayout;
   			iMenu = R.menu.menu_option_xlarge;
        } catch (Exception e) {
			iLayout = R.id.TagLayout;
			iMenu = R.menu.menu_option;
        	e.printStackTrace();
        } finally {
        	if(savedInstanceState == null) {
        		bSavedInst = true;
    			showFragment(Fragments.About, 0, null);
        	}
        }
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		HashMap<String, UsbDevice> deviceList = mManager.getDeviceList();
		Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
		while(deviceIterator.hasNext()) {
			UsbDevice device = deviceIterator.next();
			if (device.getProductId() == PID && device.getVendorId() == VID) {
				if(mManager.hasPermission(device))
					mManager.requestPermission(device, mPermissionIntent);
				break;
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		if(tv_usbstate.getText().equals("Connected"))
			mUsbCommunication.setUsbInterface(null, null);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		unregisterReceiver(usbReceiver);
	}

	@Override
	public void onTagSelected(int tagPosition, String strTag) {
		showFragment(Fragments.Detail, tagPosition, strTag);		
	}
	
	@Override
	public void onTagLongPress(int tagPosition, String strTag) {
		showFragment(Fragments.Web, tagPosition, strTag);
	}
	
	// #### menu ####
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(iMenu, menu);
    	
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch(item.getItemId()) {
			case R.id.item_tag:
				showFragment(Fragments.Tag, 0, null);
				break;
			case R.id.item_config:
				showFragment(Fragments.Config, 0, null);
				break;
			case R.id.item_about:
				showFragment(Fragments.About, 0, null);
				break;
			case R.id.item_quit:
				finish();
				break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	
	// #### fragment ####
	private void showFragment(Fragments fragmentType ,int index, String tagid) {
		switch(fragmentType) {
			case Tag:
				objFragment = FRAG_Tag.newInstance();
				break;
			case Config:
				objFragment = FRAG_Config.newInstance();
				break;
			case About:
				objFragment = FRAG_About.newInstance();
				break;
			case Detail:
				objFragment = FRAG_Details.newInstance(index, tagid);
				break;
			case Web:
			    final String prefixUrl = mSharedpref.getString("cfg_web_url", "");

			    if(prefixUrl.isEmpty()) {
					Toast.makeText(this, "Please fill the web url in the configuration page.", Toast.LENGTH_SHORT).show();
			    	return;
			    } else
			    	objFragment = FRAG_Web.newInstance(index, tagid);
				break;
		}
		    ft = getFragmentManager().beginTransaction();
		    ft.replace(iLayout, objFragment);
		    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
//		    if(iLayout == R.id.TagLayout && fragmentType.equals(Fragments.Detail))
//		    	ft.addToBackStack(null);

		    ft.commit();
	}
    
	private void insertFragTag() {
		FragmentTransaction ftTag = getFragmentManager().beginTransaction();
	    ftTag.replace(R.id.TagLayout, FRAG_Tag.newInstance());
	    ftTag.commit();
	}
	
	private void setUsbState(boolean state) {
		if(state) {
			tv_usbstate.setText("Connected");
			tv_usbstate.setTextColor(android.graphics.Color.GREEN);
		} else {
			tv_usbstate.setText("Disconnected");
			tv_usbstate.setTextColor(android.graphics.Color.RED);
		}
	}

	private void setPowerLevel() {
		MtiCmd mMtiCmd = new CMD_AntPortOp.RFID_AntennaPortSetPowerLevel(mUsbCommunication);
		CMD_AntPortOp.RFID_AntennaPortSetPowerLevel finalCmd = (CMD_AntPortOp.RFID_AntennaPortSetPowerLevel) mMtiCmd;
		
		finalCmd.setCmd((byte)18);
	}

	private void setPowerState() {
		MtiCmd mMtiCmd = new CMD_PwrMgt.RFID_PowerEnterPowerState(mUsbCommunication);
		CMD_PwrMgt.RFID_PowerEnterPowerState finalCmd = (CMD_PwrMgt.RFID_PowerEnterPowerState) mMtiCmd;
		
	    if(mSharedpref.getBoolean("cfg_sleep_mode", false)) {
	    	finalCmd.setCmd(PowerState.Sleep);
	    	sleep(200);
	    }
	}
	
	private void getReaderSn(boolean bState) {
		MtiCmd mMtiCmd;

		if(bState) {
			byte[] bSN = new byte[16];
	
		    for (int i = -1; i < 16; i++) {
				mMtiCmd = new CMD_FwAccess.RFID_MacReadOemData(mUsbCommunication);
				CMD_FwAccess.RFID_MacReadOemData finalCmd = (CMD_FwAccess.RFID_MacReadOemData) mMtiCmd;
				if(finalCmd.setCmd(i + 0x50))
					if(i >= 0)
						bSN[i] = finalCmd.getData();
			}
		    mSharedpref.edit().putString("about_reader_sn_sum", EncodingUtils.getAsciiString(bSN)).commit();
		} else
			mSharedpref.edit().putString("about_reader_sn_sum", "n/a").commit();

		showFragment(Fragments.About, 0, null);
	}
	
	
	private void sleep(int millisecond) {
		try{
			Thread.sleep(millisecond);
		} catch (InterruptedException e) {}
	}
	
	// #### broadcast receiver ####
	BroadcastReceiver usbReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(DEBUG) Toast.makeText(context, "Broadcast Receiver", Toast.LENGTH_SHORT).show();
			
			if(UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {					// will intercept by system
				if(DEBUG) Toast.makeText(context, "USB Attached", Toast.LENGTH_SHORT).show();
				UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
				mUsbCommunication.setUsbInterface(mManager, device);
				setUsbState(true);
				
			} else if(UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
				if(DEBUG) Toast.makeText(context, "USB Detached", Toast.LENGTH_SHORT).show();
				mUsbCommunication.setUsbInterface(null, null);
				setUsbState(false);
//				getReaderSn(false);
				
			} else if(ACTION_USB_PERMISSION.equals(action)) {
				if(DEBUG) Toast.makeText(context, "USB Permission", Toast.LENGTH_SHORT).show();
				Log.d(UsbCommunication.TAG, "permission");
				synchronized(this) {
					UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
					if(intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
						mUsbCommunication.setUsbInterface(mManager, device);
						setUsbState(true);
						if(bSavedInst)
							getReaderSn(true);
						setPowerLevel();
						setPowerState();
						if(iMenu == R.menu.menu_option_xlarge)
							insertFragTag();
					} else {
						finish();
					}
				}
			}
		}
	};
}