package com.infisight.hudprojector.kdxfspeech;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.LexiconListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.VoiceWakeuper;
import com.iflytek.cloud.WakeuperListener;
import com.iflytek.cloud.WakeuperResult;
import com.iflytek.cloud.util.ResourceUtil;
import com.iflytek.cloud.util.ResourceUtil.RESOURCE_TYPE;
import com.infisight.hudprojector.MainActivity;
import com.infisight.hudprojector.R;
import com.infisight.hudprojector.data.SpeechVoiceData;
import com.infisight.hudprojector.data.VoiceDataClass;
import com.infisight.hudprojector.util.CommonUtil;
import com.infisight.hudprojector.util.Constants;
import com.infisight.hudprojector.util.Utils;

/**
 * 语音识别，语音合成，语音唤醒
 * 
 * @author hao
 * 
 */
public class SpeechVoiceRecognition {

	public String TAG = "SpeechVoiceRecognition";
	// 语音听写对象
	private SpeechRecognizer mIat = null;
	String content;
	static Gson gson;
	static VoiceDataClass vdc;
	// 语音唤醒对象
	private VoiceWakeuper mIvw;
	private int curThresh = -10;// 门限值
	private int score = 0;
	private SoundPool soundPool;
	MainActivity main = new MainActivity();
	public int retKey = -10;
	// 语音合成
	public static SpeechVoiceRecognition svrManager;
	// 语音合成对象
	private SpeechSynthesizer mTts;
	// 默认云端发音人
	public String voicerCloud = "xiaoyan";
	// 默认本地发音人
	public String voicerLocal = "xiaoyan";
	static Context mContext = null;
	// 引擎类型SpeechConstant(公共常量)
	private String mEngineType = SpeechConstant.TYPE_LOCAL;
	// 控制设备音量
	static AudioManager audioManager;

	public int invalidRecKey = 0;// 无效命令词达到15个时，提示一次

	public boolean isGoback = false;// 默认第二次自动停止识别时，设置为true
	private Handler handler = new Handler();

	// private String transferKey = null;
	@SuppressWarnings("deprecation")
	public SpeechVoiceRecognition(Context con) {
		audioManager = (AudioManager) con
				.getSystemService(Context.AUDIO_SERVICE);
		soundPool = new SoundPool(4, AudioManager.STREAM_ALARM, 5);
		soundPool.load(con, R.raw.t_sorce, 1);
		mContext = con;
		initIat(con);
		initIts(con);
		// initIvw(con);
	}

	public static SpeechVoiceRecognition getInstance(Context context) {
		if (svrManager == null) {
			svrManager = new SpeechVoiceRecognition(context);
		}
		return svrManager;
	}

	/**
	 * @param c
	 *            初始化语音识别
	 */
	void initIat(Context c) {

		if (mIat != null && mIat.isListening()) {
			return;
		}

		if (mIat == null) {
			mIat = SpeechRecognizer.createRecognizer(c, mInitListener);
			setParamRec();
			mIat.startListening(recognizerListener);
			return;
		}

		if (mIat != null && !mIat.isListening()) {
			mIat.startListening(recognizerListener);
		}

	}

	/**
	 * 销毁语音识别
	 */
	void stopIat() {
		if (mIat != null) {
			mIat.stopListening();
		}
	}

	/**
	 * @param c
	 *            初始化语音唤醒
	 */
	void initIvw(Context c) {
		mIvw = VoiceWakeuper.getWakeuper();
		if (mIvw != null && mIvw.isListening()) {
			return;
		}
		if (mIvw != null && !mIvw.isListening()) {
			mIvw.startListening(mWakeuperListener);
			return;
		}
		if (mIvw == null) {
			StringBuffer param = new StringBuffer();
			String resPath = ResourceUtil.generateResourcePath(mContext,
					RESOURCE_TYPE.assets, "ivw/" + "558139a8" + ".jet");
			param.append(ResourceUtil.IVW_RES_PATH + "=" + resPath);
			param.append("," + ResourceUtil.ENGINE_START + "="
					+ SpeechConstant.ENG_IVW);
			boolean ret = SpeechUtility.getUtility().setParameter(
					ResourceUtil.ENGINE_START, param.toString());
			if (!ret) {
				Log.d(TAG, "语音唤醒启动本地引擎失败！");
			}
			// 初始化唤醒对象
			mIvw = VoiceWakeuper.createWakeuper(c, null);
			mIvw = VoiceWakeuper.getWakeuper();
			// 清空参数
			mIvw.setParameter(SpeechConstant.PARAMS, null);
			mIvw.setParameter(SpeechConstant.IVW_SST, "wakeup");
			// 设置持续进行唤醒
			mIvw.setParameter(SpeechConstant.KEEP_ALIVE, "1");
			mIvw.startListening(mWakeuperListener);
		}
	}

	/**
	 * 停止语音唤醒对象
	 */
	void stopIvw() {
		mIvw = VoiceWakeuper.getWakeuper();
		if (mIvw != null) {
			mIvw.stopListening();
		}
	}

	/**
	 * @param c
	 *            初始化语音合成
	 */
	void initIts(Context c) {

		if (null == mTts) {
			// 初始化合成对象
			mTts = SpeechSynthesizer.createSynthesizer(c, mTtsInitListener);
			setParamCompoud();
		}
		if (!mTts.isSpeaking()) {
			mTts.startSpeaking("", mSynthesizerListener);
		}
	}

	/**
	 * 初始化监听器。
	 */
	private InitListener mInitListener = new InitListener() {

		@Override
		public void onInit(int code) {
			if (code != ErrorCode.SUCCESS) {
				// showTip("初始化失败,错误码："+code);
				Log.d(TAG, "SpeechRecognizer init() code = " + code);
			} else {
				Log.d(TAG, "语音听写对象引擎初始化成功");
			}
		}
	};

	/**
	 * 语音识别监听器。
	 */
	private RecognizerListener recognizerListener = new RecognizerListener() {

		@Override
		public void onBeginOfSpeech() {
			Log.i(TAG, "开始说话");
			// CommonUtil.processBroadcast(mContext,
			// Constants.MAIN_ICON_RUI_STATE, 1);

			// 停止语音唤醒
			stopIvw();

			// setAudioVol(getMaxAlarmCurrentVol(), getMaxMusicCurrentVol());//
			// 音量恢复正常

			// int connType = CommonUtil.connectedType(mContext);
			// Log.i(TAG, "网络类型：" + connType);
			// // 2g网络时
			// if (connType == 2 || connType == 4 || connType == 7
			// || connType == 11) {
			// startSpeaking(Constants.F_C_UNBECOMING_TYPE);
			// }
		}

		@Override
		public void onError(SpeechError error) {
			// 再调用onError的时候会先调用onEndSpeech方法
			Log.i(TAG, "语音识别的错误码是：" + error.getPlainDescription(true));
			setAudioVol(getMaxAlarmCurrentVol(), getMaxMusicCurrentVol());// 音量恢复正常

		}

		// 说话之后，一段时间不说话就调用此方法停止识别
		@Override
		public void onEndOfSpeech() {
			Log.i(TAG, "结束说话");
			// 音量恢复正常
			try {
				setAudioVol(getMaxAlarmCurrentVol(), getMaxMusicCurrentVol());
				startSpeaking(Constants.F_C_RUI_GO_BACK);

				// 语音唤醒开始
				initIvw(mContext);

				invalidRecKey = 0;// 进入休息状态，无效标示置零。
				isGoback = false;
				Intent intentAction = new Intent(Constants.MAIN_KEY_SHOW_ACTION);
				intentAction.putExtra("isShow", false);
				mContext.sendBroadcast(intentAction);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e(TAG, "onEndOfSpeech出现异常");
			}
		}

		@Override
		public void onResult(RecognizerResult results, boolean isLast) {
			gson = new Gson();
			String text = null;
			try {
				text = JsonParser.parseIatResult(results.getResultString());

				if (text != null) {
					Log.i(TAG, "text：" + text);
					// 语音识别过程中呼唤小瑞，将设备声音降低
					if (CommonUtil.recognitionKeyWake(text,
							Constants.F_R_WAKE_NAME)) {
						setAudioVol(getMaxAlarmCurrentVol(),
								getMaxMusicCurrentVol());// 音量恢复正常
						soundPool.play(1, 1, 1, 0, 0, 1);
						startSpeaking(Constants.F_C_WHAT_YOU_DO_WAKE);
						invalidRecKey = 0;
						Intent intentAction = new Intent(
								Constants.MAIN_KEY_SHOW_ACTION);
						intentAction.putExtra("isShow", true);
						mContext.sendBroadcast(intentAction);
						handler.postDelayed(setAudioZero(), 1000);// 唤醒之后音量为零
						return;
					}
					// text不是唤醒词
					else {
						vdc = HandleSpeechData.handleDataFunction(mContext,
								text);
						if (vdc == null) {
							// 语音指令无效时，8个为一组。第一次提示。第二次睡眠。
							invalidRecKey++;
							Log.i(TAG, "text is null" + "invalidRecKey:"
									+ invalidRecKey + "isGoback" + isGoback);
							if (invalidRecKey == 8 && isGoback == false) {
								isGoback = true;
								startSpeaking(Constants.F_C_RUI_TIP_SHOW);
								invalidRecKey = 0;
							}

							if (invalidRecKey == 6 && isGoback == true) {
								stopIat();
								isGoback = false;
								invalidRecKey = 0;
								setAudioVol(getMaxAlarmCurrentVol(),
										getMaxMusicCurrentVol());// 音量恢复正常
								startSpeaking(Constants.F_C_RUI_GO_BACK);
								Log.i(TAG, "无效指令已经超过6个。。。。。。。。。。。");
								Intent intentAction = new Intent(
										Constants.MAIN_KEY_SHOW_ACTION);
								intentAction.putExtra("isShow", false);
								mContext.sendBroadcast(intentAction);
								// 语音唤醒开始
								initIvw(mContext);

							}
						} else {
							invalidRecKey = 0;// 一次命令词有效，无效标示置零。
							setAudioVol(getMaxAlarmCurrentVol(),
									getMaxMusicCurrentVol());// 音量恢复正常
							soundPool.play(1, 1, 1, 0, 0, 1);

							Intent intentAction = new Intent(
									Constants.MAIN_KEY_SHOW_ACTION);
							intentAction.putExtra("isShow", false);
							mContext.sendBroadcast(intentAction);

							SpeechVoiceData svData = new SpeechVoiceData();
							svData.setCommand(vdc.getCommand());
							svData.setValue(gson.toJson(vdc));
							ProcessBroadcast(mContext, svData);
							startSpeaking(vdc.getPromptKey());
							// 结束唤醒
							stopIvw();
							// 语音识别重新开始
							initIat(mContext);
						}
					}
				}
				// json解析出错
			} catch (JSONException e) {
				e.printStackTrace();
				// CommonUtil.processBroadcast(mContext,
				// Constants.MAIN_ICON_RUI_STATE, 4);。

				// 销毁语音识别
				stopIat();
				// 初始化语音唤醒
				initIvw(mContext);
				setAudioVol(getMaxAlarmCurrentVol(), getMaxMusicCurrentVol());// 音量恢复正常
				Log.w(TAG, "语音解析出现问题" + results.getResultString());
			} catch (Exception e) {
				e.printStackTrace();
				Log.w(TAG, "解析语音识别的时候抛出异常");

			}
		}

		@Override
		public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {

			// Log.i(TAG, "onEvent" + "-" + eventType + "-" + arg1 + "-" +
			// arg2);
		}

		@SuppressLint("ShowToast")
		@Override
		public void onVolumeChanged(int arg0, byte[] arg1) {
			// TODO Auto-generated method stub
			Log.d("onVolumeChanged", "音量：" + arg0);

			// int connType = CommonUtil.connectedType(mContext);
			// if (connType != 15 && connType != 8) {
			// Log.i(TAG, "网络类型：" + connType);
			// Toast.makeText(mContext, "网络类型是" + connType,
			// Toast.LENGTH_LONG);
			// }

		}
	};
	/**
	 * 上传联系人/词表监听器。
	 */
	private LexiconListener lexiconListener = new LexiconListener() {

		@Override
		public void onLexiconUpdated(String lexiconId, SpeechError error) {
			if (error != null) {
				Log.i(TAG, "上传联系人此表错误码：" + error.getPlainDescription(true));
			}
		}
	};

	/**
	 * 语音识别参数设置
	 * 
	 * @param param
	 * @return
	 */
	public void setParamRec() {
		// 清空参数
		mIat.setParameter(SpeechConstant.PARAMS, null);

		// 设置引擎
		mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);

		// 设置语言
		mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");

		// 设置语言区域
		mIat.setParameter(SpeechConstant.ACCENT, "mandarin");

		// 垂直领域 商旅
		mIat.setParameter(SpeechConstant.DOMAIN, "iat");

		// 获取网络类型
		mIat.setParameter(SpeechConstant.ASR_NET_PERF, "true");

		// 设置语音前端点 一直就没说话（会调用onError） 自动停止录音
		mIat.setParameter(SpeechConstant.VAD_BOS, "2000");

		// 设置语音后端点 说话了之后多久
		mIat.setParameter(SpeechConstant.VAD_EOS, "10000");

		// 进行回话之前，先进行网络判断
		mIat.setParameter(SpeechConstant.NET_CHECK, "true");

		// 进行回话之前，先进行网络判断
		mIat.setParameter(SpeechConstant.NET_TIMEOUT, "4000");

		// 设置标点符号
		mIat.setParameter(SpeechConstant.ASR_PTT, "0");

		// 采样率
		mIat.setParameter(SpeechConstant.SAMPLE_RATE, "16000");

		// 音量
		mIat.setParameter(SpeechConstant.VOLUME, "0");

		mIat.setParameter(SpeechConstant.AUDIO_SOURCE,
				MediaRecorder.AudioSource.MIC + "");

		// 设置音频保存路径
		mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH,
				Environment.getExternalStorageDirectory()
						+ "/infisight/wavaudios.pcm");
		// 上传用户词表
		String contents = CommonUtil.readFile(mContext, "userwords", "utf-8");
		// 置编码类型
		mIat.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8");
		retKey = mIat.updateLexicon("userword", contents, lexiconListener);
		if (retKey != ErrorCode.SUCCESS) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					if (mIat == null) {
						return;
					}
					while (retKey != ErrorCode.SUCCESS) {
						mIat.startListening(recognizerListener);
					}
					Log.i(TAG, "用户此表上传成功");
				}
			}).start();
		} else {
			Log.i(TAG, "用户此表上传成功");
		}
	}

	/*
	 * 将语音设置为没状，包括语音识别和唤醒。语音合成不关闭
	 */
	public void destroy() {
		if (mIat != null) {
			mIat.cancel();
			mIat.destroy();
		}
		if (mIvw != null) {
			mIvw.cancel();
			mIvw.destroy();
		}
		if (mTts != null) {
			mTts.destroy();
		}
		mContext = null;
	}

	/**
	 * 发送广播，在界面上进行修改
	 */
	private void ProcessBroadcast(Context context, SpeechVoiceData info) {
		gson = new Gson();
		Intent intentAction = new Intent(vdc.getCommand());
		intentAction.putExtra(vdc.getCommand(), gson.toJson(info));
		context.sendBroadcast(intentAction);
		Log.i(TAG, vdc.getCommand() + gson.toJson(info));
	}

	private WakeuperListener mWakeuperListener = new WakeuperListener() {

		@Override
		public void onResult(WakeuperResult result) {
			Log.i(TAG, "语音唤醒-返回结果");
			String text = result.getResultString();
			Log.i(TAG, text);
			JSONObject object;
			try {
				if (text != null && text != "") {

					object = new JSONObject(text);

					score = Integer.parseInt(object.optString("score"));

					Log.i(TAG, score + ":score");

					if (score >= curThresh) {

						if (mContext != null) {

							// 判断网络是否处于连接状态
							// if (!CommonUtil.isConnect(mContext)) {
							// startSpeaking(Constants.F_C_NET_ERROR);
							// return;
							// }

							soundPool.play(1, 1, 1, 0, 0, 1);

							startSpeaking(Constants.F_C_WHAT_YOU_DO_WAKE);

							Intent intentAction = new Intent(
									Constants.MAIN_KEY_SHOW_ACTION);

							intentAction.putExtra("isShow", true);

							mContext.sendBroadcast(intentAction);

							// 结束唤醒
							stopIvw();

							// 初始化语音识别
							initIat(mContext);

							handler.postDelayed(setAudioZero(), 1000);// 唤醒之后音量为零
						}
					}
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.i(TAG, "语音唤醒返回结果的时候出现了问题。。。。。。。。。。");
			}
		}

		@Override
		public void onError(SpeechError error) {
			Log.i(TAG, "语音唤醒的错误码：" + error.getPlainDescription(true));
		}

		@Override
		public void onBeginOfSpeech() {
			Log.i(TAG, "语音唤醒开始了");
		}

		@Override
		public void onEvent(int eventType, int isLast, int arg2, Bundle obj) {
			Log.i(TAG, "语音唤醒开始了----onEvent");
		}

		@Override
		public void onVolumeChanged(int arg0) {
			// TODO Auto-generated method stub
		}

	};

	/**
	 * 初始化监听。 语音+初始化完成回调接口。
	 */
	private InitListener mTtsInitListener = new InitListener() {
		@Override
		public void onInit(int code) {
			Log.d(TAG, "InitListener init() code = " + code);
			if (code != ErrorCode.SUCCESS) {
			}
		}
	};

	/**
	 * 语音合成参数设置
	 * 
	 * @param param
	 * @return
	 */
	private void setParamCompoud() {

		mTts.setParameter(SpeechConstant.PARAMS, null);
		// 设置使用本地引擎
		mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
		// 设置发音人资源路径
		mTts.setParameter(ResourceUtil.TTS_RES_PATH, getResourcePath());
		// 设置发音人
		mTts.setParameter(SpeechConstant.VOICE_NAME, voicerLocal);

		// 设置语速
		mTts.setParameter(SpeechConstant.SPEED, "62");

		// 设置音调
		mTts.setParameter(SpeechConstant.PITCH, "50");

		// 合成音频缓冲时间
		// mTts.setParameter(SpeechConstant.TTS_BUFFER_TIME, "1");

		// 合成及识别采样率
		mTts.setParameter(SpeechConstant.SAMPLE_RATE, "16000");

		// 合成时，暂停音乐
		// mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");

		// 设置音量
		mTts.setParameter(SpeechConstant.VOLUME, "35");

		/*
		 * STREAM_ALARM:手机闹铃声音4 STREAM_DTMF:DTMF音调的声音8 STREAM_MUSIC:手机音乐的声音3
		 * STREAM_NOTFICATION:系统提示的声音5 STREAM_RING:电话铃声的声音2
		 * STREAM_ALARM:手机系统的声音(不知道什么是系统声音,按键的声音?)1 STREAM_VOICE_CALL:语音电话的声音0
		 */
		mTts.setParameter(SpeechConstant.STREAM_TYPE, "4");

	}

	// 获取发音人资源路径
	private String getResourcePath() {
		StringBuffer tempBuffer = new StringBuffer();
		// 合成通用资源
		tempBuffer.append(ResourceUtil.generateResourcePath(mContext,
				RESOURCE_TYPE.assets, "tts/common.jet"));
		tempBuffer.append(";");
		// 发音人资源
		tempBuffer.append(ResourceUtil.generateResourcePath(mContext,
				RESOURCE_TYPE.assets, "tts/" + voicerLocal + ".jet"));
		Log.i("compound", tempBuffer.toString());
		return tempBuffer.toString();
	}

	// 语音合成对象
	private SynthesizerListener mSynthesizerListener = new SynthesizerListener() {

		@Override
		public void onSpeakBegin() {

			int currentVolAlarm = audioManager
					.getStreamVolume(AudioManager.STREAM_ALARM);
			int currentVolMusic = audioManager
					.getStreamVolume(AudioManager.STREAM_MUSIC);
			// 表明当前非静音状态
			if (currentVolAlarm != 0 && currentVolMusic != 0) {
				setAudioVol(getMaxAlarmCurrentVol(),
						getMaxMusicCurrentVol() / 5);
			}

		}

		@Override
		public void onSpeakPaused() {
			// showTip("暂停播放");
		}

		@Override
		public void onSpeakResumed() {
			// showTip("继续播放");
		}

		@Override
		public void onBufferProgress(int percent, int beginPos, int endPos,
				String info) {
		}

		@Override
		public void onSpeakProgress(int percent, int beginPos, int endPos) {
			// Log.i(TAG, "onSpeakProgress:" + percent + "--" + beginPos + "--"
			// + endPos + "--" + endPos);
		}

		@Override
		public void onCompleted(SpeechError error) {
			// 表明当前非静音状态
			int currentVolAlarm = audioManager
					.getStreamVolume(AudioManager.STREAM_ALARM);
			int currentVolMusic = audioManager
					.getStreamVolume(AudioManager.STREAM_MUSIC);

			if (currentVolAlarm != 0 && currentVolMusic != 0) {
				setAudioVol(getMaxAlarmCurrentVol(), getMaxMusicCurrentVol());
			}
			if (error == null) {
				// showTip("播放完成");
				// mTts.stopSpeaking();
			} else {
				Log.i(TAG, "onCompleted：" + error.getPlainDescription(true)
						+ "----------语音合成的错误");
				// 开启监听
				initIat(mContext);// 初始化语音识别
				// 语音唤醒开始
				// if (mIvw == null) {
				// initIvw(mContext);
				// }
				// if (!mIvw.isListening()) {
				// mIvw.startListening(mWakeuperListener);
				// }
			}
			// 开启监听
			// initIat(mContext);// 初始化语音识别
			Log.i(TAG, "onCompleted");
		}

		@Override
		public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
			// TODO Auto-generated method stub

		}
	};

	// 设置默认警报播报音量
	public int getMaxAlarmCurrentVol() {
		int maxAlarm = audioManager
				.getStreamMaxVolume(AudioManager.STREAM_ALARM);
		Log.i(TAG,
				"maxAlarm------------:"
						+ audioManager
								.getStreamVolume(AudioManager.STREAM_ALARM));
		int retAlarmVol = maxAlarm;
		try {
			retAlarmVol = (Integer) Utils.get(mContext,
					Constants.FILE_VOICEMANAGER,
					Constants.KEY_MANAGER_VOICE_SPEAK, maxAlarm);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return retAlarmVol;
	}

	// 设置默认音乐播报音量
	public int getMaxMusicCurrentVol() {
		int maxMusic = audioManager
				.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		int retMusicVol = maxMusic / 2;
		try {
			retMusicVol = (Integer) Utils.get(mContext,
					Constants.FILE_VOICEMANAGER,
					Constants.KEY_MANAGER_VOICE_MUSIC, maxMusic / 2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return retMusicVol;
	}

	// 保存用户设置播报音量
	public int setUserCurrentVol(int vol) {
		return vol;
	}

	// 将音量设置为0
	public int setCurrentVolToZero() {
		return 0;
	}

	// 设置音量（闹钟和音乐）
	public void setAudioVol(int alarmVol, int musicVol) {
		int currentVolAlarm = audioManager
				.getStreamVolume(AudioManager.STREAM_ALARM);
		int currentVolMusic = audioManager
				.getStreamVolume(AudioManager.STREAM_MUSIC);
		// 表明当前已经是静音状态
		if (currentVolAlarm == 0 && currentVolMusic == 0 && alarmVol == 0
				&& musicVol == 0) {
			return;
		}
		// 说明当前处于刚唤醒状态
		audioManager.setStreamVolume(AudioManager.STREAM_ALARM, alarmVol, 0);
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, musicVol, 0);

	}

	/*
	 * 语音合成(在任何情况下使用都行)
	 */
	public void startSpeaking(String str) {
		if (str != null) {
			if (null == mTts) {
				// 初始化合成对象
				try {
					mTts = SpeechSynthesizer.createSynthesizer(mContext,
							mTtsInitListener);
					setParamCompoud();

				} catch (NullPointerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return;
				}
			}
			if (mSynthesizerListener != null) {
				mTts.startSpeaking(str, mSynthesizerListener);
			} else {
				Log.i(TAG, "mSynthesizerListener--是空");
			}
		}
	}

	/**
	 * @return 将设备声音将为0
	 */
	public Runnable setAudioZero() {
		return new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				setAudioVol(0, 0);
			}
		};
	}

	/**
	 * @param isSpeakingWarn
	 *            true:显示语音提示词，降低设备声音 false:不显示，不降低。
	 */
	public void controlToWake(boolean isSpeakingWarn) {
		setAudioVol(getMaxAlarmCurrentVol(), getMaxMusicCurrentVol());
		if (mIat != null && mIat.isListening()) {
			if (isSpeakingWarn) {
				// Log.i(TAG,"controlToWake米有进来----111 ");
				startSpeaking(Constants.F_C_WHAT_YOU_DO);

				handler.postDelayed(setAudioZero(), 1000);
				// Log.i(TAG, "controlToWake米有进来----222");
			}
			Intent intent = new Intent(Constants.MAIN_KEY_SHOW_ACTION);
			intent.putExtra("isShow", true);
			mContext.sendBroadcast(intent);
			Log.i(TAG, "controlToWake开始唤醒了");
			return;
		}
		// 结束唤醒
		stopIvw();

		// 初始化语音识别
		initIat(mContext);

		// soundPool.play(1, 1, 1, 0, 0, 1);
		if (isSpeakingWarn) {
			startSpeaking(Constants.F_C_WHAT_YOU_DO);
		}
		// CommonUtil.processBroadcast(mContext, Constants.MAIN_ICON_RUI_STATE,
		// 0);
		Intent intentAction = new Intent(Constants.MAIN_KEY_SHOW_ACTION);
		intentAction.putExtra("isShow", true);
		mContext.sendBroadcast(intentAction);
		Log.i(TAG, "controlToWake开始唤醒了2");
		if (isSpeakingWarn) {
			handler.postDelayed(setAudioZero(), 1000);
		}
	}

	/**
	 * 控制小瑞进入休眠
	 */
	public void comeToWake() {

		// 音量恢复正常
		setAudioVol(getMaxAlarmCurrentVol(), getMaxMusicCurrentVol());
		startSpeaking(Constants.F_C_RUI_GO_BACK);

		// 销毁语音识别
		stopIat();

		// 语音唤醒开始
		initIvw(mContext);
	}
}
