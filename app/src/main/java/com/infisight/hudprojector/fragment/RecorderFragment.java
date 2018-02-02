package com.infisight.hudprojector.fragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnInfoListener;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.infisight.hudprojector.MainActivity;
import com.infisight.hudprojector.R;
import com.infisight.hudprojector.data.SpeechVoiceData;
import com.infisight.hudprojector.data.VoiceDataClass;
import com.infisight.hudprojector.kdxfspeech.SpeechVoiceRecognition;
import com.infisight.hudprojector.service.BackgroundRecorder;
import com.infisight.hudprojector.util.Constants;
import com.infisight.hudprojector.util.RecordUtils;

public class RecorderFragment extends Fragment implements
		SurfaceHolder.Callback, OnClickListener {
	private TextView start;// 开始录制按钮
	private TextView stop;// 停止录制按钮
	private TextView save;
	private TextView photo;
	private ImageButton openfile;
	// private View btn_layout;
	private TextView recoding;
	private MediaRecorder mediaRecorder;// 录制视频的类
	private SurfaceView surfaceview;// 显示视频的控件
	private Size previewSize;
	private LinearLayout ll_hud_record_view;

	private final int MENU_HIDE_MSG = 0x001;
	private final int TIMER_GOING_MSG = 0x002;
	private final int RECORDER_RESTART_MSG = 0x003;
	private final int LAUNCH_RECORD = 0x004;
	// 用来显示视频的一个接口，我靠不用还不行，也就是说用mediarecorder录制视频还得给个界面看
	// 想偷偷录视频的同学可以考虑别的办法。。嗯需要实现这个接口的Callback接口
	private SurfaceHolder surfaceHolder;
	private String nowTime;
	private Camera mCamera;
	private TextView timer;
	private int second;
	private int minute;
	private int hour;
	private SoundPool soundPool;
	private Animation mShowAction, mHiddenAction;
	// root完以后把所有的innerSDcard替换为extSDcard，文件存储位置就会变成外置的SD卡（别忘了Manifest里的权限）
	private String extSDcard;// 储存外部SD卡的路径（需要root权限）
	private String innerSDcard;// 储存内部SD卡路径
	private int cameraSound;// 音效的ID
	private boolean isRecording = false;
	// 检测震动相关对象（加速度传感器）
	private SensorManager sensorManager;
	// private Vibrator vibrator;
	// List<Sensor> list;
	private Gson gson;
	private boolean safeToTakePicture = false;

	// private SpeechVoiceRecognition svr;

	public static RecorderFragment newInstance(String musicInfo) {
		RecorderFragment fragment = new RecorderFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.recorderactivity_main, container,
				false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		init();
		initclick();
		innerSDcard = Environment.getExternalStorageDirectory().getPath();
		File file = new File(innerSDcard + "/Recorders/forever/");
		if (!file.exists()) {
			file.mkdirs();
		}
		// handler.sendEmptyMessageDelayed(MENU_HIDE_MSG, 5000);

		sensorManager = (SensorManager) getActivity().getSystemService(
				Context.SENSOR_SERVICE);
		// vibrator = (Vibrator) getActivity().getSystemService(
		// Context.VIBRATOR_SERVICE);

		// 传感器列表打印
		// list=sensorManager.getSensorList(Sensor.TYPE_ALL);
		// for(Sensor sensor : list){
		// Log.i("sensor","名字"+sensor.getName()+ "type:"+ sensor.getType()
		// +"  vendor:" + sensor.getVendor()
		// +"  version:" + sensor.getVersion()
		// +"  resolution:" + sensor.getResolution()
		// +"  max range:" + sensor.getMaximumRange()
		// +"  power:" + sensor.getPower());
		// }

		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onResume() {
		getActivity()
				.registerReceiver(recordReceiver, makeNewMsgIntentFilter());
		if (sensorManager != null) {// 注册监听器
			sensorManager.registerListener(sensorEventListener,
					sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
					SensorManager.SENSOR_DELAY_NORMAL);
			// 第一个参数是Listener，第二个参数是所得传感器类型，第三个参数值获取传感器信息的频率
		}
		super.onResume();
	}

	@Override
	public void onPause() {
		getActivity().unregisterReceiver(recordReceiver);
		Log.i("RecorderFrag", "RecorderFragisDes");
		if (sensorManager != null) {// 取消监听器
			sensorManager.unregisterListener(sensorEventListener);
		}
		super.onPause();
	}

	@Override
	public void onDestroyView() {
		Log.i("RecorderFrag", "RecorderFragisDes");
		if (mCamera != null) {
			mCamera.release();
			mCamera = null;
		}
		super.onDestroyView();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	private void init() {
		ll_hud_record_view = (LinearLayout) getActivity().findViewById(
				R.id.ll_hud_record_view);
		// btn_layout = (View) getActivity().findViewById(R.id.btn_layout);
		recoding = (TextView) getActivity().findViewById(R.id.recording);
		timer = (TextView) getActivity().findViewById(R.id.timer);
		timer.setVisibility(View.GONE);
		start = (TextView) getActivity().findViewById(R.id.start);
		stop = (TextView) getActivity().findViewById(R.id.stop);
		save = (TextView) getActivity().findViewById(R.id.save);
		photo = (TextView) getActivity().findViewById(R.id.photo);
		openfile = (ImageButton) getActivity().findViewById(R.id.openfile);
		mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
				1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
		mShowAction.setDuration(500);
		mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
				0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
				1.0f);
		mHiddenAction.setDuration(500);
		surfaceview = (SurfaceView) getActivity()
				.findViewById(R.id.surfaceview);
		surfaceview.setClickable(true);
		SurfaceHolder holder = surfaceview.getHolder();// 取得holder
		// surfaceview.setOnClickListener(sfvlistener);
		holder.addCallback(this); // holder加入回调接口
		// setType必须设置，要不出错.(在android 3。0版本之前必须要使用，之后则不需要）
		// holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		// 音效的数量，类型，质量
		soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
		cameraSound = soundPool.load(getActivity(), R.raw.camera, 0);
	}

	private void initclick() {
		start.setOnClickListener(this);
		save.setOnClickListener(this);
		photo.setOnClickListener(this);
		stop.setOnClickListener(this);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// 将holder，这个holder为开始在oncreat里面取得的holder，将它赋给surfaceHolder
		surfaceHolder = holder;
		if (surfaceHolder.getSurface() == null) {
			// 预览surface不存在
			return;
		}
		try {
			mCamera.stopPreview();
		} catch (Exception e) {
			// ignore: tried to stop a non-existent preview
		}
		if (mCamera != null) {
			Parameters parameters = mCamera.getParameters();
			parameters = mCamera.getParameters();
			previewSize = parameters.getPreviewSize();
			mCamera.startPreview();
			safeToTakePicture = true;
		}
	}

	/**
	 * 在surfaceView创建时，把摄像头画面呈现在surfaceView上面
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// 将holder，这个holder为开始在oncreat里面取得的holder，将它赋给surfaceHolder
		surfaceHolder = holder;
		startCamera(holder);
		Log.i("afterstartCamera", mCamera + "");
		// prepareRecorder();
		if (isRecording) {
			// handler.sendEmptyMessageDelayed(LAUNCH_RECORD, 5000);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// surfaceDestroyed的时候同时对象设置为null
		if (mediaRecorder != null) {
			stopRecorder();
		}
		surfaceview = null;
		surfaceHolder = null;
		mediaRecorder = null;
		if (mCamera != null) {
			mCamera.autoFocus(null);
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}
	}

	/**
	 * 重命名文件（保存文件）
	 */
	private void renameVideo() {
		File oldFile = new File(innerSDcard + "/Recorders/temporary/" + nowTime
				+ ".mp4"); // 要重命名的文件或文件夹
		File file = new File(innerSDcard + "/Recorders/forever/");
		if (!file.exists()) {
			file.mkdirs();
		}
		File newFile = new File(innerSDcard + "/Recorders/forever/" + nowTime
				+ ".mp4");// 重命名为
		oldFile.renameTo(newFile); // 执行重命名
	}

	/**
	 * 设置MediaRecorder（初始化）
	 */
	public void prepareRecorder() {
		Log.i("afterstartCamera", mCamera + "");
		recoding.setVisibility(View.VISIBLE);
		isRecording = true;
		// 每次准备录像的时候生成nowTime作为文件名（可以确保每次录制的文件名不一样）
		nowTime = RecordUtils.getCurrentTime("yyyyMMddHHmmss");
		second = 0;
		minute = 0;
		hour = 0;
		timer.setVisibility(View.VISIBLE);
		handler.sendEmptyMessage(TIMER_GOING_MSG);
		File file = new File(innerSDcard + "/Recorders/temporary/");
		Log.i("fileLocal",
				String.valueOf(Environment.getExternalStorageDirectory()));
		if (!file.exists()) {
			file.mkdirs();
		}
		mediaRecorder = new MediaRecorder();// 创建mediarecorder对象
		CamcorderProfile camcorderProfile = CamcorderProfile
				.get(CamcorderProfile.QUALITY_HIGH);
		unlockCamera();
		// 设置声源
		// mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		// 设置录制视频源为Camera(相机)
		mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
		// 可以设置视频录制完以后输出的角度
		mediaRecorder.setOrientationHint(0);
		mediaRecorder.setOutputFormat(camcorderProfile.fileFormat);
		mediaRecorder.setVideoFrameRate(camcorderProfile.videoFrameRate);
		mediaRecorder.setVideoSize(camcorderProfile.videoFrameWidth,
				camcorderProfile.videoFrameHeight);
		mediaRecorder.setVideoEncodingBitRate(camcorderProfile.videoBitRate);
		mediaRecorder.setVideoEncoder(camcorderProfile.videoCodec);
		// 设置录制完成后视频的封装格式THREE_GPP为3gp.MPEG_4为mp4
		// mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
		// 设置录制的视频编码h263 h264
		// mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
		// 设置声音编码器
		// mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		// 设置视频录制的分辨率。必须放在设置编码和格式的后面，否则报错
		// mediaRecorder.setVideoSize(176, 144);
		// 设置录制的视频帧率。必须放在设置编码和格式的后面，否则报错
		// mediaRecorder.setVideoFrameRate(30);
		// 清晰度调整
		// mediaRecorder.setProfile(CamcorderProfile
		// .get(CamcorderProfile.QUALITY_HIGH));
		// 设置摄像机的预览界面为surfaceHolder.getSurface()，也就是surfaceView
		mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());
		// 设置视频文件输出的路径

		// mediaRecorder.setOutputFile("/sdcard/driveVideo.mp4");
		mediaRecorder.setOutputFile(innerSDcard + "/Recorders/temporary/"
				+ nowTime + ".mp4");
		// 设置录制文件最长时间(半个小时)
		mediaRecorder.setMaxDuration(60000 * 30);
		// mediaRecorder.setMaxDuration(10000);
		try {
			// 准备录制（prepare为耗时操作，会造成主界面的ANR，如果可以，尽量放在子线程中）（导致开始录像时画面卡顿的原因）
			mediaRecorder.prepare();
			mediaRecorder.start();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		mediaRecorder.setOnInfoListener(new OnInfoListener() {

			@Override
			public void onInfo(MediaRecorder mr, int what, int extra) {
				if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
					Log.i("OninfoListener",
							"MEDIA_RECORDER_INFO_MAX_DURATION_REACHED");
					// 当录像因为达到最大时间而结束时，重新启动录像
					stopRecorder();
					handler.sendEmptyMessageDelayed(RECORDER_RESTART_MSG, 500);
				}
			}
		});
	}

	/**
	 * 停止录制并解锁camera
	 */
	private void stopRecorder() {
		if (mediaRecorder != null) {
			// 停止录制
			try {
				mediaRecorder.stop();
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(getActivity(), "您操作速度过快，小瑞正全力加载，请稍候",
						Toast.LENGTH_SHORT).show();
				return;
			}
			mediaRecorder.reset();
			// 释放资源
			mediaRecorder.release();
			mediaRecorder = null;
			// 释放相机，否则预览将会停止
		}
		isRecording = false;
		handler.removeMessages(TIMER_GOING_MSG);
		timer.setVisibility(View.GONE);
		if (mCamera != null) {
			mCamera.lock();
		}
	}

	private void startCamera(SurfaceHolder holder) {
		// try {
		final int FRONT_CAMERA = 1;
		final int REAR_CAMERA = 0;
		// 多摄像头信息获取与处理
		openCamera();
		if (mCamera != null) {
			// mCamera = Camera.open(1);//0为后置摄像头，1为前置摄像头，不填默认为后置
			Log.i("camera", mCamera + "");
			Parameters parameters = mCamera.getParameters();
			// Configuration.ORIENTATION_PORTRAIT 的值是1，表示是竖屏
			// Configuration.ORIENTATION_LANDSCAPE 的值是2，表示是横屏
			if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
				parameters.set("orientation", "portrait");
				mCamera.setDisplayOrientation(90);
			} else {
				parameters.set("orientation", "landscape");
				mCamera.setDisplayOrientation(0);
			}
			// TODO 前后摄像头角度调整
			int fittedWidth = 640;
			int fittedHeight = 480;
			previewSize = mCamera.new Size(fittedWidth, fittedHeight);
			parameters.setPreviewSize(fittedWidth, fittedHeight);
			mCamera.setParameters(parameters);
			try {
				mCamera.setPreviewDisplay(holder);
			} catch (IOException e) {
				Log.e("tag", "IOException caused by setPreviewDisplay");
			}
			mCamera.startPreview();
		}
	}

	/**
	 * 开始摄像前的准备工作
	 */
	private void unlockCamera() {
		if (mCamera != null) {
			mCamera.unlock();
			mediaRecorder.setCamera(mCamera);
			Log.i("Record", "unlockCamera");
		} else {
			Log.i("Record", "mCamera is null");
		}

	}

	PictureCallback mPictureCallback = new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			String path = innerSDcard + "/Recorders/photo/";
			File photoFile = new File(path);
			if (!photoFile.exists()) {
				photoFile.mkdirs();
			}
			// mCamera.stopPreview();
			if (isRecording == false) {
				mCamera.stopPreview();
			}
			File pictureFile = new File(path, System.currentTimeMillis()
					+ ".jpg");
			try {
				FileOutputStream fos = new FileOutputStream(pictureFile);
				fos.write(data);
				fos.close();
			} catch (FileNotFoundException e) {
				Log.d("takePhoto", "File not found: " + e.getMessage());
			} catch (IOException e) {
				Log.d("takePhoto", "Error accessing file: " + e.getMessage());
			}
			safeToTakePicture = true;
			if (isRecording == false) {
				mCamera.startPreview();
			}
			// mCamera.startPreview();
		}

	};

	BroadcastReceiver recordReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			gson = new Gson();
			final String action = intent.getAction();
			SpeechVoiceData svd;
			VoiceDataClass vdc;
			if (action.equals(Constants.HUD_MENU_SHOW_ACTION)) {
				// 确定键
				if (intent.getIntExtra("keycode", -1) == KeyEvent.KEYCODE_ENTER) {
					if (ll_hud_record_view.getVisibility() == View.GONE) {
						// 显示按钮条
						ll_hud_record_view.startAnimation(mShowAction);
						ll_hud_record_view.setVisibility(View.VISIBLE);
					}
				} else {
					if (ll_hud_record_view.getVisibility() == View.VISIBLE) {
						ll_hud_record_view.startAnimation(mHiddenAction);
						ll_hud_record_view.setVisibility(View.GONE);
					}
				}
			}
			if (Constants.MAIN_RECORDER_ACTION.equals(action)
					|| Constants.COMMON_UTIL_ACTION.equals(action)) {
				svd = gson.fromJson(intent.getStringExtra(action),
						SpeechVoiceData.class);
				vdc = gson.fromJson(svd.getValue(), VoiceDataClass.class);
				// 开始录像
				if (vdc.getType().equals(Constants.F_R_RECORD_START[0])) {
					RecordUtils.spaceNotEnoughDeleteTempFile(getActivity());
					if (mediaRecorder == null && !isRecording) {
						prepareRecorder();
					} else {
						Toast.makeText(getActivity(), "视频录制中",
								Toast.LENGTH_SHORT).show();
					}
				}
				// 停止录像
				else if (vdc.getType().equals(Constants.F_R_RECORD_END[0])) {
					if (mediaRecorder == null) {
						Toast.makeText(getActivity(), "当前没有录制，无法停止",
								Toast.LENGTH_SHORT).show();
					}
					stopRecorder();
					isRecording = false;
					recoding.setVisibility(View.GONE);
				}
				// 拍照
				else if (vdc.getType()
						.equals(Constants.F_R_RECORD_MAKEPHOTO[0])) {
					// ID,左声道，右声道，，循环次数，速率
					soundPool.play(cameraSound, 1f, 1f, 0, 0, 1f);
					try {
						if (safeToTakePicture) {
							mCamera.takePicture(null, null, mPictureCallback);
							safeToTakePicture = false;
							Toast.makeText(getActivity(), "拍照",
									Toast.LENGTH_SHORT).show();
						}
					} catch (Exception e) {
						e.printStackTrace();
						Toast.makeText(getActivity(), "拍照失败",
								Toast.LENGTH_SHORT).show();
					}

				}
				// 后台录像
				else if (vdc.getType().equals(
						Constants.F_R_RECORD_BACKGROUND[0])) {
					stopRecorder();
					isRecording = false;
					recoding.setVisibility(View.GONE);
					HomePJFragment hf = new HomePJFragment();
					getFragmentManager().beginTransaction()
							.replace(R.id.info_container, hf).commit();
					Intent service = new Intent(getActivity(),
							BackgroundRecorder.class);
					getActivity().startService(service);
				}
				// 打开文件
				// else if
				// (vdc.getType().equals(Constants.F_R_RECORD_OPENFILE[0])) {
				// FileManageFragment ra = new FileManageFragment();
				// getFragmentManager().beginTransaction()
				// .replace(R.id.info_container, ra).commit();
				// }
			}
		};

	};

	/**
	 * 定时器设置，实现计时
	 */
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == TIMER_GOING_MSG) {
				// handler.postDelayed(this, 1000);
				handler.sendEmptyMessageDelayed(TIMER_GOING_MSG, 1000);
				second++;
				if (second >= 60) {
					minute++;
					second = second % 60;
				}
				if (minute >= 60) {
					hour++;
					minute = minute % 60;
				}
				timer.setText(RecordUtils.format(hour) + ":"
						+ RecordUtils.format(minute) + ":"
						+ RecordUtils.format(second));
			}
			if (msg.what == RECORDER_RESTART_MSG) {
				prepareRecorder();
			}
			if (msg.what == MENU_HIDE_MSG) {
				start.setVisibility(View.GONE);
				stop.setVisibility(View.GONE);
				save.setVisibility(View.GONE);
				photo.setVisibility(View.GONE);
				openfile.setVisibility(View.GONE);
			}
			if (msg.what == LAUNCH_RECORD) {
				RecordUtils.spaceNotEnoughDeleteTempFile(getActivity());
				if (mediaRecorder == null) {
					prepareRecorder();
				} else {
					Toast.makeText(getActivity(), "视频录制中", Toast.LENGTH_SHORT)
							.show();
				}
			}
			super.handleMessage(msg);
		}

	};

	/**
	 * 广播过滤器
	 * 
	 * @return
	 */
	private static IntentFilter makeNewMsgIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Constants.MAIN_RECORDER_ACTION);
		intentFilter.addAction(Constants.HUD_MENU_SHOW_ACTION);
		return intentFilter;
	}

	// 震动传感器监听
	private SensorEventListener sensorEventListener = new SensorEventListener() {

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {

		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			float[] values = event.values;
			float x = values[0]; // x轴方向的重力加速度，向右为正
			float y = values[1]; // y轴方向的重力加速度，向前为正
			float z = values[2]; // z轴方向的重力加速度，向上为正
			// Log.i("Sensor", "x轴方向的重力加速度" + x + "；y轴方向的重力加速度" + y +
			// "；z轴方向的重力加速度" + z);
			int medumValue = 20;
			if (Math.abs(x) > medumValue || Math.abs(y) > medumValue
					|| Math.abs(z) > medumValue) {
				RecordUtils.spaceNotEnoughDeleteTempFile(getActivity());
				if (mediaRecorder == null) {
					prepareRecorder();
				} else {
					Toast.makeText(getActivity(), "视频录制中", Toast.LENGTH_SHORT)
							.show();
				}
			}
		}

	};

	@Override
	public void onClick(View v) {
		if (v == start) {
			// 开始录制时，检查存储空间是否够用
			Log.i("afterstartCamera", mCamera + "");
			RecordUtils.spaceNotEnoughDeleteTempFile(getActivity());
			if (mediaRecorder == null && !isRecording) {
				prepareRecorder();
			} else {
				Toast.makeText(getActivity(), "视频录制中", Toast.LENGTH_SHORT)
						.show();
			}
		}
		if (v == stop) {
			if (mediaRecorder == null) {
				Toast.makeText(getActivity(), "当前没有录制，无法停止", Toast.LENGTH_SHORT)
						.show();
			}
			stopRecorder();
			isRecording = false;
			recoding.setVisibility(View.GONE);
			// startCamera(surfaceHolder);
		}
		if (v == save) {
			// 必须释放camera资源否则会导致camera被占用，无法被再次开启
			stopRecorder();
			isRecording = false;
			recoding.setVisibility(View.GONE);
			HomePJFragment hf = new HomePJFragment();
			getFragmentManager().beginTransaction()
					.replace(R.id.info_container, hf).commit();
			Intent service = new Intent(getActivity(), BackgroundRecorder.class);
			getActivity().startService(service);
			MainActivity.progressDialog.show();
		}

		if (v == photo) {
			// ID,左声道，右声道，，循环次数，速率
			soundPool.play(cameraSound, 1f, 1f, 0, 0, 1f);
			try {
				if (safeToTakePicture) {
					mCamera.takePicture(null, null, mPictureCallback);
					safeToTakePicture = false;
					Toast.makeText(getActivity(), "拍照", Toast.LENGTH_SHORT)
							.show();
				}
			} catch (Exception e) {
				Toast.makeText(getActivity(), "拍照失败", Toast.LENGTH_SHORT)
						.show();
				e.printStackTrace();
			}
		}
	}

	public void showMenu() {
		if (ll_hud_record_view.getVisibility() == View.VISIBLE) {
			ll_hud_record_view.startAnimation(mHiddenAction);
			ll_hud_record_view.setVisibility(View.GONE);
		} else {
			ll_hud_record_view.startAnimation(mHiddenAction);
			ll_hud_record_view.setVisibility(View.VISIBLE);
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
			HomePJFragment hf = new HomePJFragment();
			getFragmentManager().beginTransaction()
					.replace(R.id.info_container, hf).commit();
			MainActivity.svr.startSpeaking("无法搜索到摄像设备");
			Toast.makeText(getActivity(), "无法搜索到摄像设备", Toast.LENGTH_SHORT)
					.show();
		}
	}

	/**
	 * 当activity上的控件被设置监听后，activity的OnTouchEvent将会监听不到事件的发生，（
	 * 原因是其上控件的监听优先权大于activity的监听），这时需要重写 dispatchTouchEvent才能做到监听
	 */
	// @Override
	// public boolean dispatchTouchEvent(MotionEvent ev) {
	// switch (ev.getAction()) {
	// case MotionEvent.ACTION_DOWN:
	// Log.i("ACTION", "DOWN");
	// handler.removeMessages(MENU_HIDE_MSG);
	// break;
	// case MotionEvent.ACTION_UP:
	// Log.i("ACTION", "UP");
	// handler.sendEmptyMessageDelayed(MENU_HIDE_MSG, 5000);
	// break;
	// default:
	// break;
	// }
	// return super.dispatchTouchEvent(ev);
	// }

	// @Override
	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// // if (mediaRecorder != null) {
	// if (keyCode == KeyEvent.KEYCODE_BACK) {
	// AlertDialog.Builder builder = new Builder(RecorderActivity.this);
	// builder.setMessage("是否启动后台服务");
	// builder.setPositiveButton("是",
	// new DialogInterface.OnClickListener() {
	//
	// @Override
	// public void onClick(DialogInterface arg0, int arg1) {
	// stopRecorder();
	// Intent service = new Intent(RecorderActivity.this,
	// BackgroundRecorder.class);
	// startService(service);
	// finish();
	// }
	// });
	// builder.setNegativeButton("否",
	// new DialogInterface.OnClickListener() {
	//
	// @Override
	// public void onClick(DialogInterface arg0, int arg1) {
	// finish();
	// }
	// });
	// builder.create().show();
	// }
	// // }
	// return super.onKeyDown(keyCode, event);
	// }

	// @Override
	// protected void onDestroy() {
	// // 必须释放camera资源否则会导致camera被占用，无法被再次开启
	// if (mCamera != null) {
	// mCamera.release();
	// mCamera = null;
	// }
	// super.onDestroy();
	// }

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// // 参数：组，ID，排序，内容
	// menu.add(1, 1, 1, "查看录像和照片");
	// // menu.add(1, 2, 1, "设置");
	// return super.onCreateOptionsMenu(menu);
	// }

	// @Override
	// public boolean onOptionsItemSelected(MenuItem item) {
	// switch (item.getItemId()) {
	// case 1:
	// if(isRecording){
	// Toast.makeText(this, "请先停止录制再查看文件", Toast.LENGTH_SHORT).show();
	// return true;
	// }
	// Log.i("onclick", "play");
	// Intent intent = new Intent(RecorderActivity.this,
	// FileManageActivity.class);
	// startActivity(intent);
	// finish();
	// break;
	// default:
	// break;
	// }
	// return super.onOptionsItemSelected(item);
	// }

}