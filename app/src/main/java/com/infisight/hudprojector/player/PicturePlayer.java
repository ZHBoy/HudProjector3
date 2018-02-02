package com.infisight.hudprojector.player;

/**
 * Created by Administrator on 2015/9/17.
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.infisight.hudprojector.R;
import com.infisight.hudprojector.data.SpeechVoiceData;
import com.infisight.hudprojector.data.VoiceDataClass;
import com.infisight.hudprojector.util.Constants;

public class PicturePlayer extends Fragment {
    Intent intent;
    ImageView imageView;
    String path;
    Drawable drawable;
    
    private Gson gson;

    public static PicturePlayer newInstance(String text) {  
    	PicturePlayer fragment = new PicturePlayer();  
        Bundle bundle = new Bundle();  
        bundle.putString("path", text);  
        fragment.setArguments(bundle);  
        return fragment;  
    }
    
    BroadcastReceiver playerReceiver=new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			gson = new Gson();
			final String action = intent.getAction();
			SpeechVoiceData svd;
			VoiceDataClass vdc;
			if (Constants.MAIN_VIDEO_ACTION.equals(action)
					|| Constants.COMMON_UTIL_ACTION.equals(action)){
				svd = gson.fromJson(intent.getStringExtra(action),
						SpeechVoiceData.class);
				vdc = gson.fromJson(svd.getValue(), VoiceDataClass.class);
				if (vdc.getType().equals(Constants.F_R_VIDEO_BACK[0])) {
					Log.e("RecordPlayer", "backFragment");
					getFragmentManager().popBackStack();
				}
			}
			
		}
	};
	
	private static IntentFilter makeNewMsgIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Constants.MAIN_VIDEO_ACTION);
		return intentFilter;
	}
    
    
//    @SuppressLint("NewApi")
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
//        setContentView(R.layout.activity_image);
//        
//    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
    		Bundle savedInstanceState) {
    	if (getArguments() != null) {  
            path = getArguments().getString("path");  
        }  
    	return inflater.inflate(R.layout.activity_image, container, false);
    }
    
    @SuppressLint("NewApi")
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	init();
        imageView.setBackground(drawable);
    	super.onActivityCreated(savedInstanceState);
    }
    
    @Override
	public void onResume() {
		getActivity()
				.registerReceiver(playerReceiver, makeNewMsgIntentFilter());
		super.onResume();
	}

	@Override
	public void onPause() {
		getActivity().unregisterReceiver(playerReceiver);
		Log.i("RecorderFrag", "RecorderFragisDes");
		super.onPause();
	}

    private void init() {
        imageView = (ImageView) getActivity().findViewById(R.id.image);
        File file = new File(path);
        if (file.exists()) {
            Log.i("picture", file.exists()+"");
            Bitmap bm = getLoacalBitmap(path);
            // 将图片显示到ImageView中
            imageView.setImageBitmap(bm);
        }
    }
    
    
    public  Bitmap getLoacalBitmap(String url) {  
    	BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();  
        bitmapOptions.inSampleSize = 4;  
        if (url != null) {  
            FileInputStream fis=null;  
            try {  
                fis = new FileInputStream(url);  
                return BitmapFactory.decodeStream(fis,null,bitmapOptions); // /把流转化为Bitmap图片  
            } catch (FileNotFoundException e) {  
                e.printStackTrace();  
                return null;  
            }finally{  
//                StreamService.close(fis);  
                if(fis!=null){  
                    try {  
                        fis.close();  
                    } catch (IOException e) {  
                        e.printStackTrace();  
                    }  
                    fis=null;  
                }  
            }  
        } else {  
            return null;  
        }  
    }  

}
