package com.mti.rfid.minime;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class FrameActivity extends Activity {
	private enum Fragments {About, Config, Detail};
	private FragmentTransaction ft;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        String fragmentType;
        int index;
        String tagId;
        Fragments fragment = null;
        
        setContentView(R.layout.frame_only);
        
        Bundle bundle = getIntent().getExtras();
        fragmentType = bundle.getString("FRAGMENT");
        index = bundle.getInt("INDEX");
        tagId = bundle.getString("TAGID");
        
        for(Fragments frag: Fragments.values()) {
        	if(frag.toString().equals(fragmentType)) {
        		fragment = frag;
        		break;
        	}
        }

        showFragment(fragment, index, tagId);
	}

	
	// #### menu ####
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.menu_option, menu);
    	
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch(item.getItemId()) {
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
			Fragment objFragment = null;
			switch(fragmentType) {
				case Config:
					objFragment = FRAG_Config.newInstance();
					break;
				case About:
					objFragment = FRAG_About.newInstance();
					break;
				case Detail:
					objFragment = FRAG_Details.newInstance(index, tagid);
			}
		    ft = getFragmentManager().beginTransaction();
		    ft.replace(R.id.DetailLayout, objFragment);
		    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		    ft.commit();
	}
}
