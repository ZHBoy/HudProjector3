package com.infisight.hudprojector.service;

/**
 * Created by Administrator on 2015/9/17.
 * 后台录像服务
 */
import java.io.File;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.infisight.hudprojector.MainActivity;
import com.infisight.hudprojector.R;
import com.infisight.hudprojector.fragment.HomePJFragment;
import com.infisight.hudprojector.fragment.RecorderFragment;
import com.infisight.hudprojector.util.RecordUtils;

public class BackgroundRecorder extends Service implements
		SurfaceHolder.Callback {
	private static final int NOTIFICATION_DI = 1234;
	private static final int LAUNCH_RECORD = 0x001;

	private WindowManager mWindowManager;
	private WindowManager.LayoutParams mLayoutParams;
	// private int screenWidth;
	// private int screenHeight;
	private View mRecorderView;
	private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;
	private ImageButton start, stop, close;
	private ImageView move;
	private MediaRecorder mMediaRecorder;
	private Camera mCamera;
	// private boolean isRecording=false;
	private boolean isRecording = false;
	private NotificationManager manager;
	private Notification.Builder notifiBuilder;
	private Notification notifi;
	// root完以后把所有的innerSDcard替换为extSDcard，文件存储位置就会变成外置的SD卡（别忘了Manifest里的权限）
	private String extSDcard;// 储存外部SD卡的路径（需要root权限）
	private String innerSDcard;// 储存内部SD卡路径
	private float x;
	private float y;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == LAUNCH_RECORD) {
				startRecord();
			}
			super.handleMessage(msg);
		}
	};

	@Override
	public void onCreate() {
		super.onCreate();
		showNotifaction();
		// extSDcard = (String)
		// RecordUtils.getExtSDCardPath(BackgroundRecorder.this)
		// .get(0);
		innerSDcard = Environment.getExternalStorageDirectory().getPath();
		// 服务前台启动
		File file = new File(innerSDcard + File.separator + "Recorders"
				+ File.separator + "temporary");
		if(!file.exists()){
			file.mkdirs();
		}
		startForeground(NOTIFICATION_DI, notifi);

		mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		mRecorderView = LayoutInflater.from(this).inflate(
				R.layout.recorder_layout, null);
		mSurfaceView = (SurfaceView) mRecorderView
				.findViewById(R.id.sv_recorder);
		// mSurfaceView.setClickable(true);
		move = (ImageView) mRecorderView.findViewById(R.id.move);
		start = (ImageButton) mRecorderView.findViewById(R.id.btn_start);
		stop = (ImageButton) mRecorderView.findViewById(R.id.btn_stop);
		close = (ImageButton) mRecorderView.findViewById(R.id.btn_close);
		mSurfaceView.getHolder().addCallback(this);
		// 获取当前运行机器的分辨率
		// DisplayMetrics dm = getResources().getDisplayMetrics();
		// screenWidth = dm.widthPixels;
		// screenHeight = dm.heightPixels;
		paramsButtonSetOnClick();
		mLayoutParams = new WindowManager.LayoutParams();
		// mLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
		mLayoutParams.type = 2003;
		mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
				| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		mLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;
		// mLayoutParams.width = screenWidth;
		// mLayoutParams.height = screenHeight;
		mLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;// 设置悬浮窗口长宽数据
		mLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		mWindowManager.addView(mRecorderView, mLayoutParams);

		mRecorderView.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				x = event.getRawX();
				y = event.getRawY();

				switch (event.getAction()) {

				case MotionEvent.ACTION_DOWN:
					Animation animation1 = AnimationUtils.loadAnimation(
							BackgroundRecorder.this, R.anim.params_anim1);
					// close.startAnimation(animation1);
					move.startAnimation(animation1);
					// move.clearAnimation();
					// close.clearAnimation();
					break;

				case MotionEvent.ACTION_MOVE:
					updateViewPosition(event);
					break;

				case MotionEvent.ACTION_UP:
					updateViewPosition(event);
					// 动画
					Animation animation = AnimationUtils.loadAnimation(
							BackgroundRecorder.this, R.anim.params_anim);
					// close.startAnimation(animation);
					move.startAnimation(animation);
					break;
				}
				return false;
			}
		});

	}

	private void updateViewPosition(MotionEvent event) {
		Log.i("postion", "X=" + x + ",Y=" + y);
		// mLayoutParams.x = (int) (x - mTouchStartX);
		// mLayoutParams.y = (int) (y - mTouchStartY);
		mLayoutParams.x = (int) event.getRawX() - move.getWidth() / 2;
		mLayoutParams.y = (int) event.getRawY() - move.getHeight() / 2;
		Log.i("postion", "X=" + mLayoutParams.x + ",Y=" + mLayoutParams.y);
		mWindowManager.updateViewLayout(mRecorderView, mLayoutParams);
	}

	/**
	 * 显示一个通知
	 */
	@SuppressLint("NewApi")
	private void showNotifaction() {
		manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, RecorderFragment.class), 1);
		// notifi=new Notification();
		// notifi.setLatestEventInfo(this, "行车记录仪", "录影状态:" + "关",
		// pendingIntent);
		notifiBuilder = new Notification.Builder(this);
		notifi = notifiBuilder.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle("行车记录仪").setContentText("")
				.setAutoCancel(true).build();
		// notifi = notifiBuilder.setSmallIcon(R.drawable.ic_launcher)
		// .setContentTitle("行车记录仪").setContentText("点击返回主界面")
		// .setContentIntent(pendingIntent).setAutoCancel(true).build();
		// RemoteViews remoteView = new RemoteViews(this.getPackageName(),
		// R.layout.notifaction_layout);
		// remoteView.setImageViewResource(R.id.icon, R.drawable.ic_launcher);
		// remoteView.setTextViewText(R.id.title, "行车记录仪");
		// remoteView.setTextViewText(R.id.content, "录影状态:" + "开");
		// notifi = new Notification.Builder(this).setContent(remoteView)
		// .setContentIntent(pendingIntent).setAutoCancel(true).build();

	}

	@Override
	public void onDestroy() {
		try {
			if (mMediaRecorder != null) {
				// 停止录制
				mMediaRecorder.stop();
				mMediaRecorder.reset();
				// 释放资源
				mMediaRecorder.release();
				mMediaRecorder = null;
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
			Toast.makeText(BackgroundRecorder.this, "您操作速度过快，小瑞正全力加载，请稍候",
					Toast.LENGTH_SHORT).show();
			return;
		}
		start.setVisibility(View.VISIBLE);
		stop.setVisibility(View.INVISIBLE);
		if (mCamera != null) {
			mCamera.release();
			mCamera = null;
		}
		if (mWindowManager != null && mRecorderView.getVisibility() == 0) {
			mWindowManager.removeView(mRecorderView);
		}
		stopForeground(true);
	}

	public void hideRecorder(boolean isHide) {
		if (isHide) {
			mLayoutParams.width = 1;
			mLayoutParams.height = 1;
		} else {
			// mLayoutParams.width = screenWidth;
			// mLayoutParams.height = screenHeight;
		}
		mWindowManager.updateViewLayout(mRecorderView, mLayoutParams);
	}

	@Override
	public void surfaceCreated(SurfaceHolder surfaceHolder) {
		mSurfaceHolder = surfaceHolder;
		// 如果之前状态为录制中，继续开启录像
		if (true) {
			handler.sendEmptyMessageDelayed(LAUNCH_RECORD, 500);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		mSurfaceHolder = holder;
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mSurfaceView = null;
		mSurfaceHolder = null;
		mMediaRecorder = null;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	private void paramsButtonSetOnClick() {
		start.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				startRecord();
			}
		});
		stop.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				stopRecord();
			}
		});
		close.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				onDestroy();
			}
		});
	}

	/**
	 * 开始录像
	 */
	private void startRecord() {
		RecordUtils.spaceNotEnoughDeleteTempFile(BackgroundRecorder.this);
		start.setVisibility(View.INVISIBLE);
		stop.setVisibility(View.VISIBLE);
		isRecording = true;
		RecordUtils.putData(BackgroundRecorder.this, isRecording);
		String nowTime = RecordUtils.getCurrentTime("yyyyMMddHHmmss");
		mMediaRecorder = new MediaRecorder();// 创建mMediaRecorder对象
		openCamera();
		// mCamera = Camera.open(1);
		if (mCamera != null) {
			mCamera.unlock();
			mMediaRecorder.setCamera(mCamera);
			CamcorderProfile camcorderProfile = CamcorderProfile
					.get(CamcorderProfile.QUALITY_HIGH);
			// 设置声源
			// mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			// 设置录制视频源为Camera(相机)
			mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
			mMediaRecorder.setOutputFormat(camcorderProfile.fileFormat);
			// mMediaRecorder.setVideoFrameRate(camcorderProfile.videoFrameRate);
			// mMediaRecorder.setVideoSize(camcorderProfile.videoFrameWidth,
			// camcorderProfile.videoFrameHeight);
			mMediaRecorder
					.setVideoEncodingBitRate(camcorderProfile.videoBitRate);
			mMediaRecorder.setVideoEncoder(camcorderProfile.videoCodec);
			// 设置录制完成后视频的封装格式THREE_GPP为3gp.MPEG_4为mp4
			// mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
			// // 设置录制的视频编码h263 h264
			// mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
			// // 设置声音编码器
			// mMediaRecorder
			// .setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			// // 设置视频录制的分辨率。必须放在设置编码和格式的后面，否则报错
			// mMediaRecorder.setVideoSize(176, 144);
			// // 设置录制的视频帧率。必须放在设置编码和格式的后面，否则报错
			// mMediaRecorder.setVideoFrameRate(20);
			// 清晰度调整(添加这句会导致文件存储大小变大)
			// mMediaRecorder.setProfile(CamcorderProfile
			// .get(CamcorderProfile.QUALITY_HIGH));
			// 设置摄像机的预览界面为surfaceHolder.getSurface()，也就是surfaceView
			mMediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
			// 设置视频文件输出的路径
			// File.separator为文件路径分割符，各系统文件分割符不一致最好不要用/或者\
			mMediaRecorder.setOutputFile(innerSDcard + File.separator
					+ "Recorders" + File.separator + "temporary"
					+ File.separator + nowTime + ".mp4");
			try {
				// 准备录制（prepare为耗时操作，会造成主界面的ANR，如果可以，尽量放在子线程中）（导致开始录像时画面卡顿的原因）
				mMediaRecorder.prepare();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			mMediaRecorder.start();
			MainActivity.progressDialog.dismiss();
		}
	}

	/**
	 * 停止录像
	 */
	private void stopRecord() {
		start.setVisibility(View.VISIBLE);
		stop.setVisibility(View.INVISIBLE);
		if (mMediaRecorder != null) {
			isRecording = false;
			RecordUtils.putData(BackgroundRecorder.this, isRecording);
			// 停止录制
			mMediaRecorder.stop();
			mMediaRecorder.reset();
			// 释放资源
			mMediaRecorder.release();
			mMediaRecorder = null;
		}
	}

	/**
	 * 打开摄像头
	 */
	public void openCamera() {
		int cameraCount = Camera.getNumberOfCameras();
		Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
		int cameraUse = 0;
		for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
			Camera.getCameraInfo(camIdx, cameraInfo);
			try {
				mCamera = Camera.open(camIdx);
				cameraUse++;
				break;
			} catch (Exception e) {
			}
		}
		if (cameraUse == 0) {
			MainActivity.svr.startSpeaking("无法搜索到摄像设备");
			Toast.makeText(this, "无法搜索到摄像设备", Toast.LENGTH_SHORT).show();
		}
	}
}
