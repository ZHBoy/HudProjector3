package com.infisight.hudprojector.adapter;

import java.util.List;

import com.infisight.hudprojector.R;
import com.infisight.hudprojector.data.AssociateKeyClass;
import com.infisight.hudprojector.data.PhoneInfoDataClass;
import com.infisight.hudprojector.util.CommonUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * AutoCompleteTextView Created by Administrator on 15-5-08.
 */
@SuppressLint("ResourceAsColor")
public class PhoneConnectAdapter extends BaseAdapter {

	private Context mContext;
	public List<PhoneInfoDataClass> lists;

	public PhoneConnectAdapter(List<PhoneInfoDataClass> list, Context mContext) {
		super();
		this.lists = list;
		this.mContext = mContext;
	}

	public PhoneConnectAdapter() {

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		Log.i("", lists.size() + "---");
		return lists.size();
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

		public TextView pw_tv_sequence = null;
		public TextView pw_tv_connect_name = null;
		public TextView pw_tv_connect_number = null;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ListItemView listItemView = new ListItemView();
		if (convertView == null || position < lists.size()) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.popupwindow_connect_info_detail, null);
			listItemView.pw_tv_sequence = (TextView) convertView
					.findViewById(R.id.pw_tv_sequence);
			listItemView.pw_tv_connect_name = (TextView) convertView
					.findViewById(R.id.pw_tv_connect_name);
			listItemView.pw_tv_connect_number = (TextView) convertView
					.findViewById(R.id.pw_tv_connect_number);
			convertView.setTag(listItemView);

		} else {
			listItemView = (ListItemView) convertView.getTag();
		}
		listItemView.pw_tv_sequence.setText(String.valueOf(position + 1));
		listItemView.pw_tv_connect_name.setText(lists.get(position)
				.getPhoneName());
		listItemView.pw_tv_connect_number.setText(lists.get(position)
				.getPhoneNumber());
		// convertView.setTag(R.id.tagItemCchoose, lists.get(position));

		// listItemView.loc_city.setText(lists.get(position).getCityName());
		// if(position ==0){
		// convertView.setBackgroundResource(R.color.grayblackish_green);
		// }
		return convertView;
	}

}