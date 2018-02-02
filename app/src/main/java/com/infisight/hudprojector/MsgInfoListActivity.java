package com.infisight.hudprojector;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.infisight.hudprojector.adapter.MsgInfoListAdapter;
import com.infisight.hudprojector.data.MsgInfoClass;
import com.infisight.hudprojector.util.Constants;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.widget.ListView;
import android.widget.Toast;

public class MsgInfoListActivity extends Activity {
	SharedPreferences sharedPreferences = null;
	MsgInfoClass msgInfo = new MsgInfoClass("1008611", "尊敬的中國移動客戶你好！！！！！！aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", "2015-04-30 10:10:10","zyh","",null);
	Gson gson=null;
	int index = 0;
	MsgInfoListAdapter telAdapter;
	ListView mListView ;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.msg_info);
		mListView = (ListView) findViewById(R.id.msg_Infos);
		sharedPreferences = getSharedPreferences(Constants.FILE_MSGCONGIGXML, Context.MODE_PRIVATE); 
		SaveInfosToSP(sharedPreferences);
		
	}
	
	List<MsgInfoClass> lstMsg;
	List<MsgInfoClass> lstMsg2;
	/**
	 * sp信息保存和读取
	 * 
	 * @param sharedPreferences
	 */
	public void SaveInfosToSP(SharedPreferences sp) {

		String spInfo = sp.getString(Constants.KEY_MSGINFOS, null);
		
		if(index == 1){//清空
			
			sharedPreferences.edit().clear().commit();
			
		}else if(index == 0){ //添加
			
			gson = new Gson();	
			
			lstMsg = new ArrayList<MsgInfoClass>();
			
			lstMsg2 = new ArrayList<MsgInfoClass>();
			
			if (spInfo != null) {
				
				lstMsg = gson.fromJson(spInfo,new TypeToken<List<MsgInfoClass>>() {}.getType());
				
			}
			lstMsg.add(msgInfo);
			
			String retCRC = gson.toJson(lstMsg);
			
			sp.edit().putString(Constants.KEY_MSGINFOS, retCRC).commit();
			
			if(retCRC != null){
				
				lstMsg2 = gson.fromJson(retCRC,
						new TypeToken<List<MsgInfoClass>>() {
						}.getType());
				System.out.println(lstMsg2.size());
			}
			
			telAdapter = new MsgInfoListAdapter(lstMsg2, this);
			mListView.setAdapter(telAdapter);
			
		}
	}
	
}
