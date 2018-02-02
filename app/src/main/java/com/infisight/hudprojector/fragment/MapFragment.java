package com.infisight.hudprojector.fragment;

import com.infisight.hudprojector.R;

import android.os.Build;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
/**
 显示地图的Fragment
 */
@SuppressLint("NewApi")
public class MapFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
     //   return inflater.inflate(R.layout.fragment_map, container, false);
    	return null;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	super.onCreate(savedInstanceState);
    	//创建NmapView
    }
	    
	@Override
    public void onResume() {
        super.onResume();
    };

    @Override
    public void onPause() {
        super.onPause();
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
    
    public void onBackPressed(){
    }
    
    @Override
    public void onDestroy(){
    	super.onDestroy();
    }
}
