package com.infisight.hudprojector.fragment;

/**
 * Created by Administrator on 2015/9/17.
 */

import java.io.DataOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.infisight.hudprojector.R;
import com.infisight.hudprojector.adapter.FileListAdapter;
import com.infisight.hudprojector.data.SpeechVoiceData;
import com.infisight.hudprojector.data.VoiceDataClass;
import com.infisight.hudprojector.kdxfspeech.SpeechVoiceRecognition;
import com.infisight.hudprojector.player.PicturePlayer;
import com.infisight.hudprojector.player.RecorderPlayer;
import com.infisight.hudprojector.util.CommonUtil;
import com.infisight.hudprojector.util.Constants;

/**
 * 文件管理activity
 *
 * @author qyl
 */
public class FileManageFragment extends Fragment implements
        OnItemClickListener, OnItemLongClickListener {

    private ListView mListView;
    private TextView mPathView;
    private FileListAdapter mFileAdpter;
    private TextView mItemCount;
    private String path;// 用于储存文件路径
    private ArrayList<HashMap<String, String>> list;
    private ArrayList<File> files;// adapter的源
    
    private Gson gson;
	private SpeechVoiceRecognition svr;
    
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
    		Bundle savedInstanceState) {
    	return inflater.inflate(R.layout.activity_filemanager, container, false);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	initView();
    	super.onActivityCreated(savedInstanceState);
    }

    
    @Override
	public void onResume() {
		getActivity()
				.registerReceiver(fileManagerReceiver, makeNewMsgIntentFilter());
		super.onResume();
	}

	@Override
	public void onPause() {
		getActivity().unregisterReceiver(fileManagerReceiver);
		Log.i("RecorderFrag", "RecorderFragisDes");
		super.onPause();
	}
	
	BroadcastReceiver fileManagerReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			gson=new Gson();
			String action =intent.getAction();
			SpeechVoiceData svd;
			VoiceDataClass vdc;
			if(Constants.MAIN_FILEMANAGE_ACTION.equals(action)||
					Constants.COMMON_UTIL_ACTION.equals(action)){
				svd = gson.fromJson(intent.getStringExtra(action),
						SpeechVoiceData.class);
				vdc = gson.fromJson(svd.getValue(), VoiceDataClass.class);
				if(vdc.getType().equals(Constants.F_R_CHOOSE_ITEM[0])){
					if (CommonUtil.getIndex(vdc.getValue()) == -1) {
						svr.startSpeaking("没有这个条目");
						return;
					}
					int index = CommonUtil.getIndex(vdc.getValue());
					
					File file =new File(list.get(index+1).get("path"));
			        String path = file.getAbsolutePath();
			        if (path.equals(Environment.getExternalStorageDirectory().getPath())) {
			            Toast.makeText(getActivity(), "已在最上级菜单", Toast.LENGTH_SHORT).show();
			            return;
			        } else {
			            if (!file.canRead()) {
			                new AlertDialog.Builder(getActivity()).setTitle("提示").setMessage("权限不足")
			                        .setPositiveButton(android.R.string.ok, null).show();
			            } else if (file.isDirectory()) {
			                initData(file);
			            } else {
			                openFile(file);
			            }
			        }
				}else if(vdc.getType().equals(Constants.F_R_FILE_BACK[0])){
					File file = new File(list.get(0).get("path"));
					String path = file.getAbsolutePath();
					if(path.equals(Environment.getExternalStorageDirectory().getPath())){
						Toast.makeText(getActivity(), "已在最上级菜单", Toast.LENGTH_SHORT).show();
			            return;
					}else {
			            if (!file.canRead()) {
			                new AlertDialog.Builder(getActivity()).setTitle("提示").setMessage("权限不足")
			                        .setPositiveButton(android.R.string.ok, null).show();
			            } else if (file.isDirectory()) {
			                initData(file);
			            } else {
			                openFile(file);
			            }
			        }
				}
			}
		}
	};
	
	private static IntentFilter makeNewMsgIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Constants.MAIN_FILEMANAGE_ACTION);
		intentFilter.addAction(Constants.COMMON_UTIL_ACTION);
		return intentFilter;
	}

    /**
     * initView之后添加apk的权限，777 表示可读可写可操作
     */
    private void initView() {
        mListView = (ListView) getActivity().findViewById(R.id.file_list);
        mPathView = (TextView) getActivity().findViewById(R.id.path);
        mItemCount = (TextView) getActivity().findViewById(R.id.item_count);
        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(this);
        // String apkRoot = "chmod 777 " + getPackageCodePath();
        // RootCommand(apkRoot);
        File folder = new File("/sdcard/Recorders");
        initData(folder);
    }

    /**
     * 修改Root权限的方法
     */
    public static boolean RootCommand(String command) {
        Process process = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(command + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * 获取根目录下面的所有的数据，然后设置到我们的ListView中让它显示出来
     */
    private void initData(File folder) {
        boolean isRoot = folder.getParent() == null;
        mPathView.setText(folder.getAbsolutePath());
        list = new ArrayList<HashMap<String, String>>();
        files = new ArrayList<File>();
        if (!isRoot) {
            files.add(folder.getParentFile());
            HashMap<String,String> map=new HashMap<String,String>();
            map.put("path",folder.getParent());
            map.put("ischecked","false");
            list.add(map);
        }
        File[] filterFiles = folder.listFiles();
        mItemCount.setText(filterFiles.length + "项");
        if (null != filterFiles && filterFiles.length > 0) {
            for (File file : filterFiles) {
                files.add(file);
            }
            for(int i=0;i<filterFiles.length;i++){
                HashMap<String,String> map=new HashMap<String,String>();
                map.put("path", filterFiles[i].toString());
                map.put("ischecked", "false");
                list.add(map);
            }

        }
        mFileAdpter = new FileListAdapter(getActivity(), list, isRoot);
        mListView.setAdapter(mFileAdpter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        File file =new File(list.get(position).get("path"));
        String path = file.getAbsolutePath();
        if (path.equals("/sdcard")) {
            Toast.makeText(getActivity(), "已在最上级菜单", Toast.LENGTH_SHORT).show();
            return;
        } else {
            if (!file.canRead()) {
                new AlertDialog.Builder(getActivity()).setTitle("提示").setMessage("权限不足")
                        .setPositiveButton(android.R.string.ok, null).show();
            } else if (file.isDirectory()) {
                initData(file);
            } else {
                openFile(file);
            }
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view,
                                   int position, long id) {
        final int needposition = position;
        Log.i("position", needposition + "");
        final File file = new File(list.get(position).get("path"));
        final String path = file.getAbsolutePath();
        Log.i("filepath", path);
        CharSequence[] items = {"删除", "重命名", "移动到forever文件夹"};


        new AlertDialog.Builder(getActivity()).setItems(items, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                Log.i("itemWhich", which + "");
                switch (which) {
                    case 0:
                        new AlertDialog.Builder(getActivity())
                                .setTitle("确定删除？")
                                .setPositiveButton("确定", new OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface arg0,
                                                        int arg1) {
                                        file.delete();
                                        list.remove(needposition);// 这句必须要不然就算删除了也不会马上刷新
                                        mFileAdpter.notifyDataSetChanged();
                                    }
                                }).setNegativeButton("取消", null).show();
                        break;
                    case 1:
                        final EditText et = new EditText(getActivity());
                        AlertDialog.Builder builder = new AlertDialog.Builder(
                        		getActivity());
                        builder.setTitle("重命名");
                        builder.setView(et);
                        builder.setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        String filename = et.getText().toString();
                                        boolean renameTo = file.renameTo(new File(
                                                file.getParent() + "/" + filename));
                                        Log.i("123", renameTo + "");
                                        list.remove(needposition);
                                        HashMap<String,String> map=new HashMap<String, String>();
                                        map.put("path",file.getParent() + "/" + filename);
                                        map.put("ischecked","false");
                                        list.add(needposition, map);
                                        mFileAdpter.notifyDataSetChanged();
                                    }
                                });
                        builder.setNegativeButton("取消", null);
                        builder.create().show();
                        break;
                    case 2:
                        if (path.contains("forever")) {
                            return;
                        }
                        String newpath = path.replace("temporary", "forever");
                        file.renameTo(new File(newpath));
                        list.remove(needposition);
                        mFileAdpter.notifyDataSetChanged();
                        break;
                    default:
                        break;
                }

            }
        }).show();
        return true;
    }

    /**
     * 打开文件
     *
     * @param file
     */
    private void openFile(File file) {
        path = file.getAbsolutePath();
        String parentPath = file.getParentFile().toString();
        Intent intent = null;
        if (path.endsWith(".mp4") || path.endsWith(".3gp")) {
//            intent = new Intent(getActivity(), RecorderPlayer.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            // Log.i("path", path);
//            intent.putExtra("path", path);
        	RecorderPlayer fragment=RecorderPlayer.newInstance(path);
        	FragmentTransaction transaction = getFragmentManager().beginTransaction();  
        	transaction.add(R.id.info_container, fragment);
        	transaction.addToBackStack(null);
            transaction.commit();  
        } else if (path.endsWith(".jpg")) {
            PicturePlayer fragment =PicturePlayer.newInstance(path);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();  
        	transaction.add(R.id.info_container, fragment);
        	transaction.addToBackStack(null);
            transaction.commit();  
            
        }
        // intent.setAction(Intent.ACTION_VIEW);
        try {
            startActivity(intent);
        } catch (Exception e) {
//            Toast.makeText(getActivity(), "未知类型，不能打开", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 获得文件的类型（后缀名）
     */
    private String getMIMEType(File file) {
        String type = "*/*";
        String fileName = file.getName();
        int dotIndex = fileName.indexOf('.');
        if (dotIndex < 0) {
            return type;
        }
        String end = fileName.substring(dotIndex, fileName.length())
                .toLowerCase();
        if (end == "") {
            return type;
        }
        for (int i = 0; i < MIME_MapTable.length; i++) {
            if (end == MIME_MapTable[i][0]) {
                type = MIME_MapTable[i][1];
            }
        }
        return type;
    }

    private final String[][] MIME_MapTable = {
            // {后缀名， MIME类型}
            {".3gp", "video/3gpp"},
            {".apk", "application/vnd.android.package-archive"},
            {".asf", "video/x-ms-asf"},
            {".avi", "video/x-msvideo"},
            {".bin", "application/octet-stream"},
            {".bmp", "image/bmp"},
            {".c", "text/plain"},
            {".class", "application/octet-stream"},
            {".conf", "text/plain"},
            {".cpp", "text/plain"},
            {".doc", "application/msword"},
            {".docx",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
            {".xls", "application/vnd.ms-excel"},
            {".xlsx",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"},
            {".exe", "application/octet-stream"},
            {".gif", "image/gif"},
            {".gtar", "application/x-gtar"},
            {".gz", "application/x-gzip"},
            {".h", "text/plain"},
            {".htm", "text/html"},
            {".html", "text/html"},
            {".jar", "application/java-archive"},
            {".java", "text/plain"},
            {".jpeg", "image/jpeg"},
            {".jpg", "image/jpeg"},
            {".js", "application/x-javascript"},
            {".log", "text/plain"},
            {".m3u", "audio/x-mpegurl"},
            {".m4a", "audio/mp4a-latm"},
            {".m4b", "audio/mp4a-latm"},
            {".m4p", "audio/mp4a-latm"},
            {".m4u", "video/vnd.mpegurl"},
            {".m4v", "video/x-m4v"},
            {".mov", "video/quicktime"},
            {".mp2", "audio/x-mpeg"},
            {".mp3", "audio/x-mpeg"},
            {".mp4", "video/mp4"},
            {".mpc", "application/vnd.mpohun.certificate"},
            {".mpe", "video/mpeg"},
            {".mpeg", "video/mpeg"},
            {".mpg", "video/mpeg"},
            {".mpg4", "video/mp4"},
            {".mpga", "audio/mpeg"},
            {".msg", "application/vnd.ms-outlook"},
            {".ogg", "audio/ogg"},
            {".pdf", "application/pdf"},
            {".png", "image/png"},
            {".pps", "application/vnd.ms-powerpoint"},
            {".ppt", "application/vnd.ms-powerpoint"},
            {".pptx",
                    "application/vnd.openxmlformats-officedocument.presentationml.presentation"},
            {".prop", "text/plain"}, {".rc", "text/plain"},
            {".rmvb", "audio/x-pn-realaudio"}, {".rtf", "application/rtf"},
            {".sh", "text/plain"}, {".tar", "application/x-tar"},
            {".tgz", "application/x-compressed"}, {".txt", "text/plain"},
            {".wav", "audio/x-wav"}, {".wma", "audio/x-ms-wma"},
            {".wmv", "audio/x-ms-wmv"},
            {".wps", "application/vnd.ms-works"}, {".xml", "text/plain"},
            {".z", "application/x-compress"},
            {".zip", "application/x-zip-compressed"}, {"", "*/*"}};

}