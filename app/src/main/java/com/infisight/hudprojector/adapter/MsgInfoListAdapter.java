package com.infisight.hudprojector.adapter;

import java.util.List;

import com.infisight.hudprojector.R;
import com.infisight.hudprojector.data.MsgInfoClass;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MsgInfoListAdapter extends BaseAdapter {

	private List<MsgInfoClass> MsgInfoLists = null;
	private Context mContext = null;

	public MsgInfoListAdapter(List<MsgInfoClass> list, Context mContext) {
		super();
		this.MsgInfoLists = list;
		this.mContext = mContext;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return MsgInfoLists.size();
		
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	public final class ListItemView {// 提供缓存

		public TextView Msg_TelNum = null;
		public TextView Msg_Content = null;
		public TextView Msg_Time = null;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		// TODO Auto-generated method stub
		ListItemView listItemView = new ListItemView();
		if (convertView == null || position < MsgInfoLists.size()) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.msg_info_detail, null);
			listItemView.Msg_TelNum = (TextView) convertView
					.findViewById(R.id.Msg_TelNum);
			listItemView.Msg_Content = (TextView) convertView
					.findViewById(R.id.Msg_Content);
			listItemView.Msg_Time = (TextView) convertView
					.findViewById(R.id.Msg_Time);
			convertView.setTag(listItemView);

		} else {
			listItemView = (ListItemView) convertView.getTag();
		}
		listItemView.Msg_TelNum.setText(MsgInfoLists.get(position).getMsg_TelNum());
		listItemView.Msg_Content.setText(MsgInfoLists.get(position).getMsg_Content());
		listItemView.Msg_Time.setText(MsgInfoLists.get(position).getMsg_Time());
		return convertView;
	}

}
