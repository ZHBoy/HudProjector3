package com.infisight.hudprojector.kdxfspeech;

import android.content.Context;
import android.util.Log;

import com.infisight.hudprojector.common.HudProjectApplication;
import com.infisight.hudprojector.data.VoiceDataClass;
import com.infisight.hudprojector.util.CommonUtil;
import com.infisight.hudprojector.util.Constants;

/**
 * @author hao
 * 
 *         指令封装类--语音控制中心 （语音控制逻辑类与SpeechVoiceRecognition结合使用）
 * 
 */
public class HandleSpeechData {

	public static VoiceDataClass handleDataFunction(Context mContext,
			String text) {
		VoiceDataClass vdc = null;
		Log.i("time", "时间1：" + System.currentTimeMillis());
		// 导航去
		if (text != null
				&& CommonUtil.recognitionKeySplit(text, Constants.F_R_NAV_GOTO) != null) {

			vdc = new VoiceDataClass(Constants.MAIN_NAVI_ACTION,
					Constants.F_R_NAV_GOTO[0], CommonUtil.recognitionKeySplit(
							text, Constants.F_R_NAV_GOTO), null);
		}

		// 打开导航
		else if (text != null
				&& CommonUtil.recognitionKey(text, Constants.F_R_OPEN_NAVI) != null) {
			vdc = new VoiceDataClass(Constants.MAIN_NAVI_ACTION,
					Constants.F_R_OPEN_NAVI[0], CommonUtil.recognitionKey(text,
							Constants.F_R_OPEN_NAVI), null);

		}
		// 开始导航
		else if (text != null
				&& CommonUtil.recognitionKey(text, Constants.F_R_START_NAVI) != null) {
			vdc = new VoiceDataClass(Constants.MAIN_NAVI_ACTION,
					Constants.F_R_START_NAVI[0], CommonUtil.recognitionKey(
							text, Constants.F_R_START_NAVI), null);

		}
		// 确定退出导航
		else if (text != null
				&& text.contains(Constants.F_R_IS_TRUE_STOP_NAVI[1])) {
			Log.i("SpeechVoiceRecognition", "Constants.F_R_STOP_NAVI" + text);
			vdc = new VoiceDataClass(Constants.COMMON_UTIL_ACTION,
					Constants.F_R_IS_TRUE_STOP_NAVI[0],
					Constants.F_R_IS_TRUE_STOP_NAVI[1], "");
		}
		// 取消退出导航
		else if (text != null
				&& text.contains(Constants.F_R_NOT_IS_TRUE_STOP_NAVI[1])) {
			Log.i("SpeechVoiceRecognition", "Constants.F_R_STOP_NAVI" + text);

			vdc = new VoiceDataClass(Constants.COMMON_UTIL_ACTION,
					Constants.F_R_NOT_IS_TRUE_STOP_NAVI[0],
					Constants.F_R_NOT_IS_TRUE_STOP_NAVI[1], "");
		}
		// 查看总览图
		else if (text != null
				&& CommonUtil.recognitionKey(text,
						Constants.F_R_NAVI_ROUTE_PLAN_All_SEE) != null) {
			vdc = new VoiceDataClass(Constants.MAIN_HUD_ACTION,
					Constants.F_R_NAVI_ROUTE_PLAN_All_SEE[0],
					CommonUtil.recognitionKey(text,
							Constants.F_R_NAVI_ROUTE_PLAN_All_SEE), "");

		}
		// 收藏当前位置
		else if (text != null
				&& CommonUtil.recognitionKey(text,
						Constants.F_R_SAVE_MY_LOCATION) != null) {
			vdc = new VoiceDataClass(Constants.MAIN_NAVI_ACTION,
					Constants.F_R_SAVE_MY_LOCATION[0],
					CommonUtil.recognitionKey(text,
							Constants.F_R_SAVE_MY_LOCATION), "");

		}
		// 选择导航路线
		else if (text != null
				&& CommonUtil.recognitionKey(text,
						Constants.F_R_NAVI_ROUTE_PLAN) != null) {
			vdc = new VoiceDataClass(Constants.MAIN_NAVI_ACTION,
					Constants.F_R_NAVI_ROUTE_PLAN[0],
					CommonUtil.recognitionKey(text,
							Constants.F_R_NAVI_ROUTE_PLAN), "");

		}
		// 搜索附近地点
		else if (text != null
				&& CommonUtil.recognitionKeySplit(text,
						Constants.F_R_POSITION_SHOW) != null) {

			vdc = new VoiceDataClass(Constants.MAIN_HUD_ACTION,
					Constants.F_R_POSITION_SHOW[0],
					CommonUtil.recognitionKeySplit(text,
							Constants.F_R_POSITION_SHOW), null);
		}

		// 继续导航
		else if (text != null
				&& CommonUtil.recognitionKey(text, Constants.F_R_RESUME_NAVI) != null) {
			Log.i("SpeechVoiceRecognition", "Constants.F_R_RESUME_NAVI" + text);

			vdc = new VoiceDataClass(Constants.MAIN_HUD_ACTION,
					Constants.F_R_RESUME_NAVI[0], CommonUtil.recognitionKey(
							text, Constants.F_R_RESUME_NAVI), "");
		}

		// 暂停导航
		else if (text != null
				&& CommonUtil.recognitionKey(text, Constants.F_R_PAUSE_NAVI) != null) {
			Log.i("SpeechVoiceRecognition", "Constants.F_R_PAUSE_NAVI" + text);

			vdc = new VoiceDataClass(Constants.MAIN_HUD_ACTION,
					Constants.F_R_PAUSE_NAVI[0], CommonUtil.recognitionKey(
							text, Constants.F_R_PAUSE_NAVI), "");
		}
		// 退出导航
		else if (text != null
				&& CommonUtil.recognitionKey(text, Constants.F_R_STOP_NAVI) != null) {
			Log.i("SpeechVoiceRecognition", "Constants.F_R_STOP_NAVI" + text);

			vdc = new VoiceDataClass(Constants.MAIN_NAVI_ACTION,
					Constants.F_R_STOP_NAVI[0], CommonUtil.recognitionKey(text,
							Constants.F_R_STOP_NAVI), "");
		}
		// 打开音乐
		else if (text != null
				&& CommonUtil.recognitionKey(text, Constants.F_R_MUSIC_ON) != null) {
			Log.i("SpeechVoiceRecognition", "首页音乐:" + text);
			vdc = new VoiceDataClass(Constants.MAIN_MUSIC_ACTION,
					Constants.F_R_MUSIC_ON[0], CommonUtil.recognitionKey(text,
							Constants.F_R_MUSIC_ON), Constants.F_C_OPEN_MUSIC);

		}
		// 上一首
		else if (text != null
				&& CommonUtil.recognitionKey(text,
						Constants.F_R_MUSIC_LAST_MUSIC_PLAY) != null) {
			Log.i("SpeechVoiceRecognition", "上一首:" + text);
			vdc = new VoiceDataClass(Constants.MAIN_MUSIC_ACTION,
					Constants.F_R_MUSIC_LAST_MUSIC_PLAY[0],
					CommonUtil.recognitionKey(text,
							Constants.F_R_MUSIC_LAST_MUSIC_PLAY), null);
		}
		// 下一首
		else if (text != null
				&& CommonUtil.recognitionKey(text,
						Constants.F_R_MUSIC_NEXT_MUSIC_PLAY) != null) {
			if ("music".equals(HudProjectApplication.doubanOrMusic)) {
				Log.i("SpeechVoiceRecognition", "下一首:" + text);
				vdc = new VoiceDataClass(Constants.MAIN_MUSIC_ACTION,
						Constants.F_R_MUSIC_NEXT_MUSIC_PLAY[0],
						CommonUtil.recognitionKey(text,
								Constants.F_R_MUSIC_NEXT_MUSIC_PLAY), null);
			} else if ("douban".equals(HudProjectApplication.doubanOrMusic)) {
				Log.i("SpeechVoiceRecognition", "F_R_MUSIC_NEXT_MUSIC_PLAY"
						+ text);
				vdc = new VoiceDataClass(Constants.MAIN_DOUBAN_ACTION,
						Constants.F_R_MUSIC_NEXT_MUSIC_PLAY[0],
						CommonUtil.recognitionKey(text,
								Constants.F_R_MUSIC_NEXT_MUSIC_PLAY), null);
			}
		}
		// 继续播放
		else if (text != null
				&& CommonUtil.recognitionKey(text,
						Constants.F_R_MUSIC_CONTINUE_PLAY) != null) {
			if ("music".equals(HudProjectApplication.doubanOrMusic)) {
				Log.i("SpeechVoiceRecognition", "继续播放:" + text);
				vdc = new VoiceDataClass(Constants.MAIN_MUSIC_ACTION,
						Constants.F_R_MUSIC_CONTINUE_PLAY[0],
						CommonUtil.recognitionKey(text,
								Constants.F_R_MUSIC_CONTINUE_PLAY), "音乐开始播放");
			} else if ("douban".equals(HudProjectApplication.doubanOrMusic)) {
				Log.i("SpeechVoiceRecognition", "F_R_DOUBAN_START_PLAY" + text);
				vdc = new VoiceDataClass(Constants.MAIN_DOUBAN_ACTION,
						Constants.F_R_MUSIC_CONTINUE_PLAY[0],
						CommonUtil.recognitionKey(text,
								Constants.F_R_MUSIC_CONTINUE_PLAY), null);
			}

		}
		// 播放音乐
		else if (text != null
				&& CommonUtil.recognitionKey(text,
						Constants.F_R_MUSIC_MUSIC_PLAY) != null) {
			Log.i("SpeechVoiceRecognition", "继续播放:" + text);
			vdc = new VoiceDataClass(Constants.MAIN_MUSIC_ACTION,
					Constants.F_R_MUSIC_MUSIC_PLAY[0],
					CommonUtil.recognitionKey(text,
							Constants.F_R_MUSIC_MUSIC_PLAY), "音乐开始播放");
		}
		// 单曲循环
		else if (text != null
				&& CommonUtil.recognitionKey(text,
						Constants.F_R_MUSIC_JUST_ONE_PLAY) != null) {
			Log.i("SpeechVoiceRecognition", "单曲循环:" + text);
			vdc = new VoiceDataClass(Constants.MAIN_MUSIC_ACTION,
					Constants.F_R_MUSIC_JUST_ONE_PLAY[0],
					CommonUtil.recognitionKey(text,
							Constants.F_R_MUSIC_JUST_ONE_PLAY), "播放模式切换成功！");
		}
		// 顺序播放
		else if (text != null
				&& CommonUtil.recognitionKey(text,
						Constants.F_R_MUSIC_ORDER_PLAY) != null) {
			Log.i("SpeechVoiceRecognition", "顺序播放:" + text);
			vdc = new VoiceDataClass(Constants.MAIN_MUSIC_ACTION,
					Constants.F_R_MUSIC_ORDER_PLAY[0],
					CommonUtil.recognitionKey(text,
							Constants.F_R_MUSIC_ORDER_PLAY), "播放模式切换成功！");
		}
		// 随机播放
		else if (text != null
				&& CommonUtil.recognitionKey(text,
						Constants.F_R_MUSIC_RANDOM_PLAY) != null) {
			Log.i("SpeechVoiceRecognition", "随机播放:" + text);
			vdc = new VoiceDataClass(Constants.MAIN_MUSIC_ACTION,
					Constants.F_R_MUSIC_RANDOM_PLAY[0],
					CommonUtil.recognitionKey(text,
							Constants.F_R_MUSIC_RANDOM_PLAY), "播放模式切换成功！");
		}
		// 全部歌曲
		// else if (text != null
		// && CommonUtil.recognitionKey(text,
		// Constants.F_R_MUSIC_ALL_MUSIC_LIST)) {
		// Log.i("SpeechVoiceRecognition", "播放列表:" + text);
		// vdc = new VoiceDataClass(Constants.MAIN_MUSIC_ACTION,
		// Constants.F_R_MUSIC_ALL_MUSIC_LIST[0],
		// Constants.F_R_MUSIC_ALL_MUSIC_LIST[1], "");
		// }
		// 停止播放
		else if (text != null
				&& CommonUtil.recognitionKey(text,
						Constants.F_R_MUSIC_STOP_PLAY) != null) {
			if (HudProjectApplication.isMusicPlaying) {
				Log.i("SpeechVoiceRecognition", "停止播放:" + text);
				vdc = new VoiceDataClass(Constants.MAIN_MUSIC_ACTION,
						Constants.F_R_MUSIC_STOP_PLAY[0],
						CommonUtil.recognitionKey(text,
								Constants.F_R_MUSIC_STOP_PLAY),
						Constants.F_C_STOP_MUSIC);
			} else if (HudProjectApplication.isDoubanplaying) {
				Log.i("SpeechVoiceRecognition", "停止播放:" + text);
				vdc = new VoiceDataClass(Constants.MAIN_DOUBAN_ACTION,
						Constants.F_R_MUSIC_STOP_PLAY[0],
						CommonUtil.recognitionKey(text,
								Constants.F_R_MUSIC_STOP_PLAY), null);
			}

		}
		// 首页OBD
		else if (text != null
				&& text.trim().contains(Constants.F_R_OBD_SHOW[1])) {
			Log.i("SpeechVoiceRecognition", "首页OBD:" + text);
			vdc = new VoiceDataClass(Constants.MAIN_OBD_ACTION,
					Constants.F_R_OBD_SHOW[0], Constants.F_R_OBD_SHOW[1],
					Constants.F_C_OBD_INFO);
		}
		// 首页打电话给某人
		else if (text != null
				&& CommonUtil.recognitionKeySplit(text, Constants.F_R_PHONE_TO) != null) {
			Log.i("SpeechVoiceRecognition", "首页打电话给某人:" + text);
			vdc = new VoiceDataClass(Constants.MAIN_PHONE_ACTION,
					Constants.F_R_PHONE_TO[0], CommonUtil.recognitionKeySplit(
							text, Constants.F_R_PHONE_TO), "");
		}
		// 上翻联系人
		// else if (text != null
		// && CommonUtil.recognitionKey(text, Constants.F_R_PHONE_PAGE_UP)) {
		// Log.i("SpeechVoiceRecognition", "上翻:" + text);
		// vdc = new VoiceDataClass(Constants.MODEL_PHONE_ACTION,
		// Constants.F_R_PHONE_PAGE_UP[0],
		// Constants.F_R_PHONE_PAGE_UP[1], "上翻");
		// }
		// 下翻联系人
		// else if (text != null
		// && CommonUtil.recognitionKey(text,
		// Constants.F_R_PHONE_PAGE_DOWN)) {
		// Log.i("SpeechVoiceRecognition", "上翻:" + text);
		// vdc = new VoiceDataClass(Constants.MODEL_PHONE_ACTION,
		// Constants.F_R_PHONE_PAGE_DOWN[0],
		// Constants.F_R_PHONE_PAGE_DOWN[1], "下翻");
		// }
		// 挂断电话
		else if (text != null
				&& CommonUtil.recognitionKey(text, Constants.F_R_PHONE_HANG_UP) != null) {
			Log.i("SpeechVoiceRecognition", "挂断：" + text);
			vdc = new VoiceDataClass(Constants.MODEL_PHONE_ACTION,
					Constants.F_R_PHONE_HANG_UP[0], CommonUtil.recognitionKey(
							text, Constants.F_R_PHONE_HANG_UP), null);
		}

		// 接听电话
		else if (text != null
				&& CommonUtil.recognitionKey(text, Constants.F_R_PHONE_ANSWER) != null) {
			Log.i("SpeechVoiceRecognition", "接听：" + text);
			vdc = new VoiceDataClass(Constants.MODEL_PHONE_ACTION,
					Constants.F_R_PHONE_ANSWER[0], CommonUtil.recognitionKey(
							text, Constants.F_R_PHONE_ANSWER), null);
		}

		// 读取信息
		else if (text != null
				&& CommonUtil.recognitionKey(text, Constants.F_R_READ_MSG_INFO) != null) {
			vdc = new VoiceDataClass(Constants.MODEL_PHONE_ACTION,
					Constants.F_R_READ_MSG_INFO[0], CommonUtil.recognitionKey(
							text, Constants.F_R_READ_MSG_INFO), null);
		}

		// 忽略信息
		else if (text != null
				&& CommonUtil.recognitionKey(text,
						Constants.F_R_IGNORE_MSG_INFO) != null) {
			vdc = new VoiceDataClass(Constants.MODEL_PHONE_ACTION,
					Constants.F_R_IGNORE_MSG_INFO[0],
					CommonUtil.recognitionKey(text,
							Constants.F_R_IGNORE_MSG_INFO), null);
		}
		// 选择第几条item（属于公共action）
		else if (text != null
				&& CommonUtil.recognitionKeySplit(text,
						Constants.F_R_CHOOSE_ITEM) != null) {
			vdc = new VoiceDataClass(Constants.COMMON_UTIL_ACTION,
					Constants.F_R_CHOOSE_ITEM[0], CommonUtil
							.recognitionKeySplit(text,
									Constants.F_R_CHOOSE_ITEM)
							.subSequence(0, 1).toString(), null);

		}
		// 返回首页
		else if (text != null
				&& CommonUtil.recognitionKey(text, Constants.F_R_RETURN_HOME) != null) {
			Log.i("SpeechVoiceRecognition", "Constants.F_R_RETURN_HOME" + text);
			vdc = new VoiceDataClass(Constants.COMMON_UTIL_ACTION,
					Constants.F_R_RETURN_HOME[0], CommonUtil.recognitionKey(
							text, Constants.F_R_RETURN_HOME), null);

		}
		// 退出应用
		else if (text != null
				&& CommonUtil.recognitionKey(text, Constants.F_R_EXIT_PROJECT) != null) {
			Log.i("SpeechVoiceRecognition", "Constants.F_R_RETURN_HOME" + text);
			vdc = new VoiceDataClass(Constants.MAIN_EXIT_PROJECT_ACTION,
					Constants.F_R_EXIT_PROJECT[0], CommonUtil.recognitionKey(
							text, Constants.F_R_EXIT_PROJECT), null);

		}
		// 缩放地图
		else if (text != null
				&& (text.contains(Constants.F_R_ENLARGE_OR_NARROW_MAP[1]) || text
						.contains(Constants.F_R_ENLARGE_OR_NARROW_MAP[2]))) {
			Log.i("SpeechVoiceRecognition", "Constants.F_R_ENLARGE_MAP" + text);
			vdc = new VoiceDataClass(Constants.COMMON_UTIL_ACTION,
					Constants.F_R_ENLARGE_OR_NARROW_MAP[0], text, null);

		}
		// 进入设置
		else if (text != null
				&& CommonUtil.recognitionKey(text, Constants.F_R_SETTING) != null) {
			Log.i("SpeechVoiceRecognition", "Constants.F_R_SETTING" + text);
			vdc = new VoiceDataClass(Constants.MAIN_SETTING_ACTION,
					Constants.F_R_SETTING[0], Constants.F_R_SETTING[1], null);

		}

		// 行车记录仪
		else if (text != null
				&& CommonUtil.recognitionKey(text, Constants.F_R_RECORD_LAUNCH) != null) {
			Log.i("SpeechVoiceRecognition", "Constants.F_R_RECORD_LAUNCH"
					+ text);
			vdc = new VoiceDataClass(Constants.MAIN_GORECORDER_ACTION,
					Constants.F_R_RECORD_LAUNCH[0], CommonUtil.recognitionKey(
							text, Constants.F_R_RECORD_LAUNCH), null);

		}
		// 开始录像
		else if (text != null
				&& CommonUtil.recognitionKey(text, Constants.F_R_RECORD_START) != null) {
			Log.i("SpeechVoiceRecognition", "Constants.F_R_RECORD_LAUNCH"
					+ text);
			vdc = new VoiceDataClass(Constants.MAIN_RECORDER_ACTION,
					Constants.F_R_RECORD_START[0], CommonUtil.recognitionKey(
							text, Constants.F_R_RECORD_START), null);
		}
		// 结束录像
		else if (text != null
				&& CommonUtil.recognitionKey(text, Constants.F_R_RECORD_END) != null) {
			Log.i("SpeechVoiceRecognition", "Constants.F_R_RECORD_LAUNCH"
					+ text);
			vdc = new VoiceDataClass(Constants.MAIN_RECORDER_ACTION,
					Constants.F_R_RECORD_END[0], CommonUtil.recognitionKey(
							text, Constants.F_R_RECORD_END), null);
		}
		// 打开文件
		// else if (text != null
		// && CommonUtil.recognitionKey(text,
		// Constants.F_R_RECORD_OPENFILE)) {
		// Log.i("SpeechVoiceRecognition", "Constants.F_R_RECORD_LAUNCH"
		// + text);
		// vdc = new VoiceDataClass(Constants.MAIN_RECORDER_ACTION,
		// Constants.F_R_RECORD_OPENFILE[0],
		// Constants.F_R_RECORD_OPENFILE[1], null);
		// }
		// 拍照
		else if (text != null
				&& CommonUtil.recognitionKey(text,
						Constants.F_R_RECORD_MAKEPHOTO) != null) {
			Log.i("SpeechVoiceRecognition", "Constants.F_R_RECORD_MAKEPHOTO"
					+ text);
			vdc = new VoiceDataClass(Constants.MAIN_RECORDER_ACTION,
					Constants.F_R_RECORD_MAKEPHOTO[0],
					CommonUtil.recognitionKey(text,
							Constants.F_R_RECORD_MAKEPHOTO), null);
		}
		// 后台录像
		else if (text != null
				&& CommonUtil.recognitionKey(text,
						Constants.F_R_RECORD_BACKGROUND) != null) {
			Log.i("SpeechVoiceRecognition", "Constants.F_R_RECORD_LAUNCH"
					+ text);
			vdc = new VoiceDataClass(Constants.MAIN_RECORDER_ACTION,
					Constants.F_R_RECORD_BACKGROUND[0],
					CommonUtil.recognitionKey(text,
							Constants.F_R_RECORD_BACKGROUND), null);
		}
		// 返回上一级
		else if (text != null
				&& CommonUtil.recognitionKey(text, Constants.F_R_FILE_BACK) != null) {
			Log.i("SpeechVoiceRecognition", "Constants.F_R_FILE_BACK" + text);
			vdc = new VoiceDataClass(Constants.MAIN_FILEMANAGE_ACTION,
					Constants.F_R_FILE_BACK[0], CommonUtil.recognitionKey(text,
							Constants.F_R_FILE_BACK), null);
		}
		// 返回
		else if (text != null
				&& CommonUtil.recognitionKey(text, Constants.F_R_VIDEO_BACK) != null) {
			Log.i("SpeechVoiceRecognition", "Constants.F_R_RECORD_LAUNCH"
					+ text);
			vdc = new VoiceDataClass(Constants.MAIN_VIDEO_ACTION,
					Constants.F_R_VIDEO_BACK[0], CommonUtil.recognitionKey(
							text, Constants.F_R_VIDEO_BACK), null);
		}
		// 音量管理
		else if (text != null
				&& CommonUtil.recognitionKey(text, Constants.F_R_SETTING_VOICE) != null) {
			Log.i("SpeechVoiceRecognition", "Constants.F_R_SETTING_VOICE"
					+ text);
			vdc = new VoiceDataClass(Constants.MAIN_SETTING_ACTION,
					Constants.F_R_SETTING_VOICE[0], CommonUtil.recognitionKey(
							text, Constants.F_R_SETTING_VOICE), null);
			// 放大音量
		} else if (text != null
				&& CommonUtil.recognitionKey(text,
						Constants.F_R_SETTING_VOICEUP) != null) {
			Log.i("SpeechVoiceRecognition", "Constants.F_R_SETTING_VOICEUP"
					+ text);
			vdc = new VoiceDataClass(Constants.MAIN_SETTING_ACTION,
					Constants.F_R_SETTING_VOICEUP[0],
					CommonUtil.recognitionKey(text,
							Constants.F_R_SETTING_VOICEUP), null);
			// TODO
		}
		// 缩小音量
		else if (text != null
				&& CommonUtil.recognitionKey(text,
						Constants.F_R_SETTING_VOICEDOWN) != null) {
			Log.i("SpeechVoiceRecognition", "Constants.F_R_SETTING_VOICEDOWN"
					+ text);
			vdc = new VoiceDataClass(Constants.MAIN_SETTING_ACTION,
					Constants.F_R_SETTING_VOICEDOWN[0],
					CommonUtil.recognitionKey(text,
							Constants.F_R_SETTING_VOICEDOWN), null);
		}
		// 连接wifi
		else if (text != null
				&& CommonUtil.recognitionKey(text, Constants.F_R_SETTING_WIFI) != null) {
			Log.i("SpeechVoiceRecognition", "Constants.F_R_SETTING_WIFI" + text);
			vdc = new VoiceDataClass(Constants.MAIN_SETTING_ACTION,
					Constants.F_R_SETTING_WIFI[0], CommonUtil.recognitionKey(
							text, Constants.F_R_SETTING_WIFI), null);
		}
		// 打开热点
		else if (text != null
				&& CommonUtil.recognitionKey(text,
						Constants.F_R_SETTING_HOTPOINT) != null) {
			Log.i("SpeechVoiceRecognition", text);
			vdc = new VoiceDataClass(Constants.MAIN_SETTING_ACTION,
					Constants.F_R_SETTING_HOTPOINT[0],
					CommonUtil.recognitionKey(text,
							Constants.F_R_SETTING_HOTPOINT), null);
		}
		// 连接遥控器
		else if (text != null
				&& CommonUtil
						.recognitionKey(text, Constants.F_R_SETTING_REMOTE) != null) {
			Log.i("SpeechVoiceRecognition", "Constants.F_R_SETTING_REMOTE"
					+ text);
			vdc = new VoiceDataClass(Constants.MAIN_SETTING_ACTION,
					Constants.F_R_SETTING_REMOTE[0], CommonUtil.recognitionKey(
							text, Constants.F_R_SETTING_REMOTE), null);
		}
		// 流量监控
		else if (text != null
				&& CommonUtil.recognitionKey(text,
						Constants.F_R_SETTING_TRAFFIC) != null) {
			Log.i("SpeechVoiceRecognition", "Constants.F_R_SETTING_TRAFFIC"
					+ text);
			vdc = new VoiceDataClass(Constants.MAIN_SETTING_ACTION,
					Constants.F_R_SETTING_TRAFFIC[0],
					CommonUtil.recognitionKey(text,
							Constants.F_R_SETTING_TRAFFIC), null);
		}
		// 打开豆瓣FM
		else if (text != null
				&& CommonUtil.recognitionKey(text, Constants.F_R_OPEN_DOUBAN) != null) {
			Log.i("SpeechVoiceRecognition", "Constants.F_R_OPEN_DOUBAN" + text);
			vdc = new VoiceDataClass(Constants.MAIN_DOUBAN_ACTION,
					Constants.F_R_OPEN_DOUBAN[0], CommonUtil.recognitionKey(
							text, Constants.F_R_OPEN_DOUBAN), null);
		}
		// 豆瓣播放下一首
		else if (text != null
				&& CommonUtil.recognitionKey(text,
						Constants.F_R_DOUBAN_NEXT_PLAY) != null) {
			Log.i("SpeechVoiceRecognition", "F_R_DOUBAN_NEXT_PLAY" + text);
			vdc = new VoiceDataClass(Constants.MAIN_DOUBAN_ACTION,
					Constants.F_R_DOUBAN_NEXT_PLAY[0],
					CommonUtil.recognitionKey(text,
							Constants.F_R_DOUBAN_NEXT_PLAY), null);
		}

		Log.i("time", "时间2：" + System.currentTimeMillis());
		return vdc;

	}
}
