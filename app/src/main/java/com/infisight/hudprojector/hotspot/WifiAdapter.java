package com.infisight.hudprojector.hotspot;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import com.infisight.hudprojector.R;
import com.infisight.hudprojector.data.ScanWifiClass;

import java.util.ArrayList;
import java.util.List;

/**
 * 扫描到的Wifi适配器
 * Created by Administrator on 2015/9/8.
 */
public class WifiAdapter extends BaseAdapter {
    public List<ScanWifiClass> listWifi = new ArrayList<ScanWifiClass>();
    Context mContext ;
    public WifiAdapter(Context context)
    {
        mContext = context;
    }
    @Override
    public int getCount() {
        return listWifi.size();
    }

    @Override
    public Object getItem(int position) {
        return listWifi.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    public void AddNewItem(ScanWifiClass newItem)
    {
        listWifi.add(newItem);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null)
        {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item,null);
            holder = new ViewHolder();
            holder.tv_capabilities = (TextView)convertView.findViewById(R.id.tv_capabilities);
            holder.tv_ssid = (TextView)convertView.findViewById(R.id.tv_ssid);
            convertView.setTag(holder);
        }
        else
            holder = (ViewHolder)convertView.getTag();
        holder.tv_ssid.setText(listWifi.get(position).getSSID());
        holder.tv_capabilities.setText(listWifi.get(position).getCapabilities());
        return convertView;
    }
    public final class ViewHolder{
        public TextView tv_ssid;
        public TextView tv_capabilities;
    }
}
