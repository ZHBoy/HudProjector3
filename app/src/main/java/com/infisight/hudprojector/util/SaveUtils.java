package com.infisight.hudprojector.util;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;


public class SaveUtils {
	private static SharedPreferences mySharedPreferences;

	public static boolean saveArray(Context context,String spname,ArrayList<String> list) {  
		mySharedPreferences= context.getSharedPreferences(spname, Context.MODE_PRIVATE);
		Editor edit = mySharedPreferences.edit();
	     edit.putInt("Status_size",list.size()); /*sKey is an array*/   
	  
	    for(int i=0;i<list.size();i++) {  
	        edit.putString("Status_" + i, list.get(i)+"");
	    }  
//	    Log.i("saveArray", list+"");
	    return edit.commit();       
	} 
	
	
	public static ArrayList<String> loadArray(Context context,String spname) {  
		ArrayList<String> list=new ArrayList<String>();
		try{
			mySharedPreferences= context.getSharedPreferences(spname, Context.MODE_PRIVATE);
		}catch (Exception e) {
			Log.e("SaveUtils", "could not find spf");
			return new ArrayList<String>();
		}
		Log.i("SaveUtils", "load address");
	    int size = mySharedPreferences.getInt("Status_size", 0);    
	    for(int i=0;i<size;i++) {
	        String ble=mySharedPreferences.getString("Status_" + i, null);
	        list.add(ble);
//	        String[] split = ble.split("=");
//	        Log.i("Loadarray",ble);
//	        BleDevice bleDevice =new BleDevice();
//	        bleDevice.setImei(split[1].substring(1, 18));
//	        bleDevice.setImsi(split[2].substring(1, 18));
//	        bleDevice.setName(split[3].substring(1, split[3].length()-2));
//	        list.add(bleDevice);
	    }
	    if(size==0){
	    	return new ArrayList<String>();
	    }
		return list;  
	}  
}
