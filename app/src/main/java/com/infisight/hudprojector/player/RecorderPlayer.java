package com.infisight.hudprojector.player;

/**
 * Created by Administrator on 2015/9/17.
 */

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.TextView;

import com.google.gson.Gson;
import com.infisight.hudprojector.R;
import com.infisight.hudprojector.data.SpeechVoiceData;
import com.infisight.hudprojector.data.VoiceDataClass;
import com.infisight.hudprojector.util.Constants;
import com.infisight.hudprojector.util.RecordUtils;
import com.infisight.hudprojector.widget.FullScreenVideoView;

public class RecorderPlayer extends Fragment {
    private FullScreenVideoView video;
    private TextView tv;
    private String path;
    private StringBuffer sb;
    private int year, month, date, hour, minute, secord;
    private TimeThread tt=null;
    private int position;//获得目前的进度
    private int duration;//获得视频总长
    private SurfaceView sfv;
    private boolean playerOpen;//当前activity开启即为true
    private MediaController mediaController;
    
    private Gson gson;

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
    
    public static RecorderPlayer newInstance(String text) {  
    	RecorderPlayer fragment = new RecorderPlayer();  
        Bundle bundle = new Bundle();  
        bundle.putString("path", text);  
        fragment.setArguments(bundle);  
        return fragment;  
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
    

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
    		Bundle savedInstanceState) {
    	if (getArguments() != null) {  
            path = getArguments().getString("path");  
        }  
    	return inflater.inflate(R.layout.activity_player, container, false);
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	init();
        changeTextView();
        video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                //当videoView准备好以后开启TimeThread
                duration = video.getDuration();
                position = video.getCurrentPosition();
                Log.i("duration", duration + "");
                Log.i("position", position + "");
                if (tt == null) {
                    tt = new TimeThread();
                    tt.start();
                }
                video.start();
            }
        });
        video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                video.start();
//                getFragmentManager().popBackStack();
//                返回前一个Fragment
            }
        });
    	super.onActivityCreated(savedInstanceState);
    }
    

    @Override
	public void onDestroyView() {
    	playerOpen=false;
    	video.clearFocus();
    	video.clearAnimation();
    	mediaController.clearChildFocus(mediaController);
    	mediaController.clearFocus();
		super.onDestroyView();
	}

	private void init() {
        playerOpen=true;
        video = (FullScreenVideoView) getActivity().findViewById(R.id.video);
        video.setFocusable(false);
        tv = (TextView)  getActivity().findViewById(R.id.tv_time);
//        path =  getActivity().getIntent().getStringExtra("path");
        mediaController=new MediaController(getActivity());
        mediaController.setFocusable(false);
        video.setMediaController(mediaController);
        mediaController.setFocusable(false);
        video.setVideoPath(path);

    }
	

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            //结束timeThread线程
//            duration=-1;
//            finish();
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    private void changeTextView() {
        String years, months, dates, hours, minutes, secords;
        // Log.i("path", path);
        int lastIndexOf;
        lastIndexOf=path.lastIndexOf("/");
        path=path.substring(lastIndexOf);
//         Log.i("path", path);
        String[] split = path.split(".mp4");
        // Log.i("path", split[0]);
        String[] fileNames = split[0].split("/");
        // Log.i("path", fileNames.length + "");
        String time = fileNames[1];
        // Log.i("path", Utils.isNumeric(time) + "");
        if (RecordUtils.isNumeric(time)) {
            // 解析字符串，化为时间
            Log.i("timeString", time);
            sb = new StringBuffer(time);
            years = sb.substring(0, 4);
            year = Integer.valueOf(years);
            months = sb.substring(4, 6);
            month = Integer.valueOf(months);
            dates = sb.substring(6, 8);
            date = Integer.valueOf(dates);
            hours = sb.substring(8, 10);
            hour = Integer.valueOf(hours);
            minutes = sb.substring(10, 12);
            minute = Integer.valueOf(minutes);
            secords = sb.substring(12, 14);
            secord = Integer.valueOf(secords);
            // Log.i("time",year);
            // Log.i("time",month);
            // Log.i("time",date);
            // Log.i("time",hour);
            // Log.i("time",minute);
            // Log.i("time",secord);
            sb.insert(4, ".");
            sb.insert(7, ".");
            sb.insert(10, "-");
            sb.insert(13, ":");
            sb.insert(16, ":");
            Log.i("sb", sb + "");
            //sb为视频起始的时间
            tv.setText(sb);

        }

    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x001:
                    duration = video.getDuration();
                    position = video.getCurrentPosition();
                    Log.i("duration", duration + "");
                    Log.i("position", position + "");
                    int secord1,minute1,hour1,date1;
//                    secord++;
//                    if (secord >= 60) {
//                        secord = 0;
//                        minute++;
//                        if (minute >= 60) {
//                            minute = 0;
//                            hour++;
//                            if (hour >= 24) {
//                                hour = 0;
//                                date++;
//                                if (date == 29 && month == 2) {
//                                    date = 01;
//                                    month++;
//                                } else if (date == 31
//                                        && (month == 4 || month == 6 || month == 9 || month == 11)) {
//                                    date = 01;
//                                    month++;
//                                } else if (date == 32
//                                        && (month == 1 || month == 3 || month == 5
//                                        || month == 7 || month == 8
//                                        || month == 10 || month == 12)) {
//                                    date = 01;
//                                    month++;
//                                }
//                                if (month >= 13) {
//                                    month = 00;
//                                    year++;
//                                }
//                            }
//                        }
//                    }
                    int[] ints = RecordUtils.formateTime(position / 1000);
                    secord1=secord+ints[2];
                    minute1=minute+ints[1];
                    hour1=hour+ints[0];
                    if(secord1>=60){
                        minute1++;
                        secord1=secord1%60;
                    }
                    if (minute1>=60){
                        hour1++;
                        minute1=minute1%60;
                    }
                    if (hour1>=24){
                        date++;
                        hour1=hour1%24;
                    }
                    String text = year + "." + month + "." + date + "-" + hour1
                            + ":" + minute1 + ":" + secord1;
                    tv.setText(text);
                    break;

                default:
                    break;
            }
        }
    };

    public class TimeThread extends Thread {
        @Override
        public void run() {
            while (playerOpen) {
                if(video.isPlaying()) {
                    try {
                        Thread.sleep(1000);
                        Message msg = new Message();
                        msg.what = 0x001;
                        mHandler.sendMessage(msg);
                    } catch (InterruptedException e) {
                        e.printStackTrace();

                    }
                }
            }
        }
    }

}

