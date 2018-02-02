package com.infisight.hudprojector.adapter;

/**
 * Created by Administrator on 2015/9/17.
 */
import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.infisight.hudprojector.R;

public class FileListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<HashMap<String,String>> list;
    private ArrayList<File> files;
    private boolean isRoot;
    private LayoutInflater mInflater;

    public FileListAdapter(Context context, ArrayList<HashMap<String,String>> list,
                           boolean isRoot) {
        this.context = context;
        this.list = list;
        this.isRoot = isRoot;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView=mInflater.inflate(R.layout.file_list_item,null);

            viewHolder.title = (TextView) convertView
                    .findViewById(R.id.file_title);
            viewHolder.type = (TextView) convertView
                    .findViewById(R.id.file_type);
            viewHolder.data = (TextView) convertView
                    .findViewById(R.id.file_date);
            viewHolder.size = (TextView) convertView
                    .findViewById(R.id.file_size);
//            viewHolder.checkBox= (CheckBox) convertView
//                    .findViewById(R.id.file_checkbox);
            final int p=position;
//            viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                    if (list.get(p).get("ischecked").equals("false")) {
//                        list.get(p).put("ischecked", "true");
//
//                    }else{
//                        list.get(p).put("ischecked", "false");
//                    }
//                    Log.i("checkchange",list.get(p).get("ischecked"));
//                }
//            });
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
//            viewHolder.checkBox.setChecked(list.get(position).get("ischecked").equals("true"));
        }

        File file = new File(list.get(position).get("path"));
        if (position == 0 && !isRoot) {
            viewHolder.title.setText("返回上一级");
            viewHolder.data.setVisibility(View.GONE);
            viewHolder.size.setVisibility(View.GONE);
            viewHolder.type.setVisibility(View.GONE);
//            viewHolder.checkBox.setVisibility(View.GONE);
        } else {
            viewHolder.data.setVisibility(View.VISIBLE);
            viewHolder.size.setVisibility(View.VISIBLE);
            viewHolder.type.setVisibility(View.VISIBLE);
//            viewHolder.checkBox.setVisibility(View.VISIBLE);
            String fileName = file.getName();
            viewHolder.title.setText(fileName);
            if (file.isDirectory()) {
                viewHolder.size.setText("文件夹");
                viewHolder.size.setTextColor(Color.RED);
                viewHolder.type.setVisibility(View.GONE);
                viewHolder.data.setVisibility(View.GONE);
            } else {
                long fileSize = file.length();
                if (fileSize > 1024 * 1024) {
                    float size = fileSize / (1024f * 1024f);
                    viewHolder.size.setText(new DecimalFormat("#.00")
                            .format(size) + "MB");
                } else if (fileSize >= 1024) {
                    float size = fileSize / 1024;
                    viewHolder.size.setText(new DecimalFormat("#.00")
                            .format(size) + "KB");
                } else {
                    viewHolder.size.setText(fileSize + "B");
                }
                int dot = fileName.indexOf('.');
                if (dot > -1 && dot < (fileName.length() - 1)) {
                    viewHolder.type.setText(fileName.substring(dot + 1)
                            + "文件");
                }
                viewHolder.data.setText(new SimpleDateFormat(
                        "yyyy/MM/dd HH:mm").format(file.lastModified()));
            }
        }


        return convertView;
    }

    class ViewHolder {
        private TextView title;
        private TextView type;
        private TextView data;
        private TextView size;
        private CheckBox checkBox;
    }
}

