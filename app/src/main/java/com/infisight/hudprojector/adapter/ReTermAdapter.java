package com.infisight.hudprojector.adapter;

import java.util.List;

import com.infisight.hudprojector.R;
import com.infisight.hudprojector.data.AssociateKeyClass;
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
public class ReTermAdapter extends BaseAdapter {

	private Context mContext;
	public List<AssociateKeyClass> lists;
//	int height;

	public ReTermAdapter(List<AssociateKeyClass> list, Context mContext) {

		super();
		this.lists = list;
		this.mContext = mContext;
	}

	public ReTermAdapter() {

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

		public TextView KeyIndex;
		public TextView KeyName = null;
		public TextView keyCity = null;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ListItemView listItemView = new ListItemView();
		if (convertView == null || position < lists.size()) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.st_sug_list, null);
			listItemView.KeyIndex = (TextView) convertView
					.findViewById(R.id.num_index);
			listItemView.KeyName = (TextView) convertView
					.findViewById(R.id.KeyName);
			listItemView.keyCity = (TextView) convertView
					.findViewById(R.id.KeyCity);
			convertView.setTag(listItemView);

		} else {
			listItemView = (ListItemView) convertView.getTag();
		}
//		if (height != 0) {
//			listItemView.KeyName
//					.setTextSize(CommonUtil.getTextSizes(height, 5));
//			listItemView.KeyIndex
//			.setTextSize(CommonUtil.getTextSizes(height, 5));
//	
//		}
		listItemView.KeyIndex.setText(String.valueOf(position + 1));
		listItemView.KeyName.setText(lists.get(position).getKeyName());

		// convertView.setTag(R.id.tagItemCchoose, lists.get(position));

		// listItemView.loc_city.setText(lists.get(position).getCityName());
		// if(position ==0){
		// convertView.setBackgroundResource(R.color.grayblackish_green);
		// }
		return convertView;
	}

}