package com.mti.rfid.minime;

import java.util.ArrayList;
import java.util.Collections;

import com.mti.rfid.minime.CMD_PwrMgt.PowerState;

import android.app.Activity;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FRAG_Tag extends ListFragment implements OnItemLongClickListener {
	private UsbCommunication mUsbCommunication = UsbCommunication.getInstance();
	private MtiCmd mMtiCmd;
	private SharedPreferences mSharedpref;

	public static ArrayList<String> tagList = new ArrayList<String>();
	private ArrayAdapter<String> tagAdapter;

	private View mView;
	private TextView mUsbState;
	OnTagSelectedListener mListener;
	
	private boolean isPhone = false;
	
	public static FRAG_Tag newInstance() {
		FRAG_Tag f = new FRAG_Tag();
		return f;
	}
	
	public interface OnTagSelectedListener {
		public void onTagSelected(int tagPosition, String strTag);
		public void onTagLongPress(int tagPosition, String strTag);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		try {
			mListener = (OnTagSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnTagSelectedListener");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        tagAdapter = new ArrayAdapter<String> (getActivity(), android.R.layout.simple_list_item_1, tagList);
        setListAdapter(tagAdapter);

 		mView = inflater.inflate(R.layout.frag_tag, container, false);

		return mView;
	}


    @Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		Button btnInventory = (Button) mView.findViewById(R.id.btn_inventory);

		mSharedpref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
		
		btnInventory.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
		    	onInventoryClick(v);
			}
		});
        getListView().setOnItemLongClickListener(this);
/*        
        try {
        	this.getActivity().findViewById(R.id.DetailLayout).getResources();
        	isPhone = false;
        } catch (Exception e) {
        	isPhone = true;
        }
*/
		Point point = new Point();

		getActivity().getWindowManager().getDefaultDisplay().getSize(point);
		switch (getActivity().getWindowManager().getDefaultDisplay().getRotation()) {
			case Surface.ROTATION_0:
			case Surface.ROTATION_180:
				isPhone = (point.x - point.y) < 0 ? true : false;
				break;
			case Surface.ROTATION_90:
			case Surface.ROTATION_270:
				isPhone = (point.x - point.y) > 0 ? true : false;
				break;
		}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		String strTag = getListTagId(position);
		
		if(onItemSelect(strTag)) {
			mListener.onTagSelected(position, strTag);
		} else {
			// #### process error ####
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> av, View v, int position, long id) {
	    String strTag = getListTagId(position);
	    mListener.onTagLongPress(position, strTag.replace(" ", ""));

		return true;
	}


	// #### run inventory ####
	public void onInventoryClick(View v) {
		final Handler handler = new Handler();
		
	    final int scantimes = Integer.parseInt((mSharedpref.getString("cfg_inventory_times", "25")));
		
		if(getUsbState()) {
			final ProgressDialog mProgDlg = ProgressDialog.show(getActivity(), "Inventory", "Searching ...", true);
			
			new Thread() {
				int numTags;
				String tagId;

				ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);

	    		public void run() {
	    			setOrientationSensor(false);
			    	tagList.clear();
			    	for(int i = 0; i < scantimes; i++) {
			    		mMtiCmd = new CMD_Iso18k6cTagAccess.RFID_18K6CTagInventory(mUsbCommunication);
						CMD_Iso18k6cTagAccess.RFID_18K6CTagInventory finalCmd = (CMD_Iso18k6cTagAccess.RFID_18K6CTagInventory) mMtiCmd;
						
						if(finalCmd.setCmd(CMD_Iso18k6cTagAccess.Action.StartInventory)) {
							tagId = finalCmd.getTagId();
							if(finalCmd.getTagNumber() > 0) {
								tg.startTone(ToneGenerator.TONE_PROP_BEEP);
								if(!tagList.contains(tagId))
									tagList.add(tagId);
//								finalCmd.setCmd(CMD_Iso18k6cTagAccess.Action.GetAllTags);
							}
							
							for(numTags = finalCmd.getTagNumber(); numTags > 1; numTags--) {
								if(finalCmd.setCmd(CMD_Iso18k6cTagAccess.Action.NextTag)) {
									tagId = finalCmd.getTagId();
									if(!tagList.contains(tagId)){
										tagList.add(tagId);
									}
								}
							}
							Collections.sort(tagList);
							handler.post(updateResult);
						} else {
							// #### process error ####
						}
			    	}
	    			mProgDlg.dismiss();
	    			setOrientationSensor(true);
	    			setPowerState();
	    		}
	    		
	    		final Runnable updateResult = new Runnable() {
					@Override
					public void run() {
						tagAdapter.notifyDataSetChanged();
					}
	    		};
			}.start();
		} else
			Toast.makeText(getActivity(), "The Reader is not connected", Toast.LENGTH_SHORT).show();
	}
	
	private void setOrientationSensor(boolean status) {
		if(status)
			this.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
		else {
			switch(this.getActivity().getWindowManager().getDefaultDisplay().getRotation()) {
				case 0:
					if(isPhone)
						this.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
					else
						this.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);	
					break;
				case 1:
					if(isPhone)
						this.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
					else
						this.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
					break;
				case 2:
					if(isPhone)
						this.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
					else
						this.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
					break;
				case 3:
					if(isPhone)
						this.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
					else
						this.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
					break;
			}
		}
	}

	// #### select a tag ####
	private boolean onItemSelect(String tagId) {
		int intCount = 0;
		boolean bStatus = false;
		
		if(getUsbState()) {
			mMtiCmd = new CMD_Iso18k6cTagAccess.RFID_18K6CTagSelect(mUsbCommunication);
			CMD_Iso18k6cTagAccess.RFID_18K6CTagSelect finalCmd = (CMD_Iso18k6cTagAccess.RFID_18K6CTagSelect) mMtiCmd;
			
			do {
				bStatus = finalCmd.setCmd(finalCmd.byteCmd(tagId.replace(" ", "")));
				intCount++;
			} while (!bStatus && intCount < 3);
			
			if(!bStatus)
				Toast.makeText(getActivity(), "The Tag is not available, please try again.", Toast.LENGTH_SHORT).show();

			setPowerState();
		} else
			Toast.makeText(getActivity(), "The Reader is not connected", Toast.LENGTH_SHORT).show();
		return bStatus;
	}
	
	// #### get tag id ####
	private String getListTagId(int position) {
		return tagList.get(position).toString();
	}


	private void setPowerState() {
		MtiCmd mMtiCmd = new CMD_PwrMgt.RFID_PowerEnterPowerState(mUsbCommunication);
		CMD_PwrMgt.RFID_PowerEnterPowerState finalCmd = (CMD_PwrMgt.RFID_PowerEnterPowerState) mMtiCmd;
		
	    if(mSharedpref.getBoolean("cfg_sleep_mode", false))
	    	finalCmd.setCmd(PowerState.Sleep);
	}

	
	private boolean getUsbState() {
        mUsbState = (TextView) getActivity().findViewById(R.id.tv_usbstate);
        return mUsbState.getText().equals("Connected");
	}
}
