package com.infisight.hudprojector.adapter;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.infisight.hudprojector.R;
import com.infisight.hudprojector.util.CommonUtil;

public class RoutePlanItemChooseAdapter extends BaseAdapter {

	private Context mContext;
	public List<String> lists;

	// int height;

	public RoutePlanItemChooseAdapter(List<String> routePlans, Context mContext) {
		super();
		this.lists = routePlans;
		this.mContext = mContext;
	}

	public RoutePlanItemChooseAdapter() {

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
			convertView.setTag(listItemView);

		} else {
			listItemView = (ListItemView) convertView.getTag();
		}
		// if (height != 0) {
		// listItemView.KeyName.setTextSize(CommonUtil.getTextSizes(720, 6));
		// }
		listItemView.KeyIndex.setText(String.valueOf(position + 1));
		listItemView.KeyName.setText(lists.get(position));

		return convertView;
	}

}
