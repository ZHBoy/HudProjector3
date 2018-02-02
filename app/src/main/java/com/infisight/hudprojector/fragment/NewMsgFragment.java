package com.infisight.hudprojector.fragment;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.infisight.hudprojector.R;
import com.infisight.hudprojector.adapter.MsgInfoListAdapter;
import com.infisight.hudprojector.data.MsgInfoClass;
import com.infisight.hudprojector.util.Constants;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
	消息展示模块
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class NewMsgFragment extends Fragment {
	SharedPreferences sharedPreferences = null;
	MsgInfoClass msgInfo = new MsgInfoClass("1008611", "尊敬的中國移動客戶你好！！！！！！aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", "2015-04-30 10:10:10","zyh","",null);
	Gson gson = new Gson();
	int index = 0;
	MsgInfoListAdapter telAdapter;
	ListView mListView ;
	List<MsgInfoClass> lstMsg = new ArrayList<MsgInfoClass>();
	List<MsgInfoClass> lstMsg2 = new ArrayList<MsgInfoClass>();

	public static NewMsgFragment newInstance()
	{
		NewMsgFragment fragment = new NewMsgFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}
	public NewMsgFragment()
	{}
	/**
	 * 创建
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		if(getArguments() != null)
		{}
		
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		InitView();
	}
	private void InitView()
	{
		mListView = (ListView) getActivity().findViewById(R.id.msg_Infos);
		sharedPreferences = getActivity().getSharedPreferences(Constants.FILE_MSGCONGIGXML, getActivity().MODE_PRIVATE); 
		SaveInfosToSP(sharedPreferences);
	}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_msg, container, false);
    }
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
			
			telAdapter = new MsgInfoListAdapter(lstMsg2,getActivity());
			mListView.setAdapter(telAdapter);
			
		}
	}
}
