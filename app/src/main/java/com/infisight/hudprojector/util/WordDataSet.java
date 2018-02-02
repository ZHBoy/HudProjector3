package com.infisight.hudprojector.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.R.integer;

import com.amap.api.navi.model.AMapNaviGuide;
import com.infisight.hudprojector.R;

/**
 * 数据集
 * 
 * @author Administrator
 * 
 */
public class WordDataSet {
	/**
	 * 语音指令分析相关
	 */
	public static HashMap<String, String[]> hmVoiceNav = new HashMap<String, String[]>() {
		{
			put(Constants.K_SPLITS, new String[] { "到", "去", "回" });
			put(Constants.K_MAP_ORPERCMD_ENLARGE, new String[] { "放大", "放大地图",
					"大地图" });
			put(Constants.K_MAP_ORPERCMD_SMALLERE, new String[] { "缩小", "缩小地图",
					"小地图" });
			put(Constants.K_MAP_ORPERCMD_UP, new String[] { "地图向上" });
			put(Constants.K_MAP_ORPERCMD_DOWN, new String[] { "地图向下" });
			put(Constants.K_MAP_ORPERCMD_LEFT, new String[] { "地图向左", });
			put(Constants.K_MAP_ORPERCMD_RIGHT, new String[] { "地图向右" });
			put(Constants.K_MAP_NEARBY_ADDR_SPLITS, new String[] { "附近", "周围",
					"周边" });
			put(Constants.K_MAP_LSTVIEW_DIRECT_UP, new String[] { "向上" });
			put(Constants.K_MAP_LSTVIEW_DIRECT_DOWN, new String[] { "向下" });
			put(Constants.K_MAP_NEARBY_WEATHER_SPLITS, new String[] { "天气" });
			put(Constants.K_MAP_LSTVIEW_DIRECT_AUTO2, new String[] { "一", "二",
					"三", "四", "五", "六", "七", "八", "九", "十" });
			put(Constants.K_MAP_LSTVIEW_DIRECT_AUTO, new String[] { "第" });
			put(Constants.K_MAP_LSTVIEW_DIRECT_CHOOSE, new String[] { "选择",
					"选中", "设为终点" });
			put(Constants.K_MAP_NAI_START, new String[] { "开始导航" });
			put(Constants.K_MAP_NAI_END, new String[] { "结束导航" });

		}
	};
	/**
	 * 语音反馈句子
	 */
	public static HashMap<String, String> arrFeedBack = new HashMap<String, String>() {
		{
			put(Constants.K_V_NOCLEARHEAR, "没有听懂您在说什么");
			put(Constants.K_V_NOFIND_ADDR, "没有找到设置的终点");
		}
	};
	/**
	 * HUD诱导图文字对应的图标
	 */
	public static HashMap<Integer, Integer> hmHudInductionIcons = new HashMap<Integer, Integer>() {
		{
			put(AMapNaviGuide.IconTypeArrivedDestination,
					R.drawable.nsdk_drawable_rg_ic_turn_dest);
			put(AMapNaviGuide.IconTypeArrivedTollGate,
					R.drawable.nsdk_drawable_rg_ic_turn_tollgate);
			put(AMapNaviGuide.IconTypeArrivedTunnel,
					R.drawable.nsdk_drawable_rg_ic_tunnel);
			put(AMapNaviGuide.IconTypeArrivedWayPoint,
					R.drawable.nsdk_drawable_rg_ic_turn_via_point);
			put(AMapNaviGuide.IconTypeCrosswalk,
					R.drawable.nsdk_drawable_rg_ic_crosswalk);
			put(AMapNaviGuide.IconTypeDefault,
					R.drawable.nsdk_drawable_rg_ic_typedefault);
			// put(AMapNaviGuide.IconTypeDiagonallyOpposite,
			// R.drawable.nsdk_drawable_rg_ic_diagonallyopposite);
			put(AMapNaviGuide.IconTypeEnterRoundabout,
					R.drawable.nsdk_drawable_rg_ic_turn_ring);
			// put(AMapNaviGuide.IconTypeKeepLeft,
			// R.drawable.nsdk_drawable_rg_ic_turn_left_side_main);
			// put(AMapNaviGuide.IconTypeKeepRight,
			// R.drawable.nsdk_drawable_rg_ic_turn_right_side_main);
			put(AMapNaviGuide.IconTypeLeft,
					R.drawable.nsdk_drawable_rg_ic_turn_left);
			put(AMapNaviGuide.IconTypeLeftAndAround,
					R.drawable.nsdk_drawable_rg_ic_turn_back);
			put(AMapNaviGuide.IconTypeLeftBack,
					R.drawable.nsdk_drawable_rg_ic_turn_left_back);
			put(AMapNaviGuide.IconTypeLeftFront,
					R.drawable.nsdk_drawable_rg_ic_turn_left_front);
			put(AMapNaviGuide.IconTypeOutRoundabout,
					R.drawable.nsdk_drawable_rg_ic_turn_ring_out);
			put(AMapNaviGuide.IconTypeOverpass,
					R.drawable.nsdk_drawable_rg_ic_overpass);
			put(AMapNaviGuide.IconTypeRight,
					R.drawable.nsdk_drawable_rg_ic_turn_right);
			put(AMapNaviGuide.IconTypeRightBack,
					R.drawable.nsdk_drawable_rg_ic_turn_right_back);
			put(AMapNaviGuide.IconTypeRightFront,
					R.drawable.nsdk_drawable_rg_ic_turn_right_front);
			put(AMapNaviGuide.IconTypeSquare,
					R.drawable.nsdk_drawable_rg_ic_square);
			put(AMapNaviGuide.IconTypeStraight,
					R.drawable.nsdk_drawable_rg_ic_turn_front);
			put(AMapNaviGuide.IconTypeUnderpass,
					R.drawable.nsdk_drawable_rg_ic_underpass);

		}
	};
	/**
	 * 路径规划选项
	 */
	public static List<Integer> rightIconsLsts = new ArrayList<Integer>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		{
			add(18);// IconTypeKeepRight导航段转向图标类型：靠右图标（数值：18）
			add(6);// IconTypeLeftBack导航段转向图标类型：左后方图标（数值：6）
			add(3);// IconTypeRight导航段转向图标类型：右转图标（数值：3
			add(7);// IconTypeRightBack 导航段转向图标类型：右后方图标（数值：7）
			add(5);// IconTypeRightFront导航段转向图标类型：右前方图标（数值：5）
		}
	};
	/**
	 * 路径规划选项
	 */
	public static HashMap<String, Integer> routePlans = new HashMap<String, Integer>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		{
			put("避免拥堵", 4);// 设置为默认路线
			put("不走高速", 3);
			put("避免收费", 1);

		}
	};

	/**
	 * 路径规划选项
	 */
	public static List<String> routePlansLsts = new ArrayList<String>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		{
			add("避免拥堵");// 设置为默认路线
			add("不走高速");
			add("避免收费");

		}
	};

	/**
	 * 语音指令分析相关
	 */
	public static HashMap<String, String[]> mainKeyShow = new HashMap<String, String[]>() {
		{
			put("HudFragment", new String[] { "退出导航", "导航去/到", "播放音乐", "附近的银行",
					"收藏位置", "返回首页" });
			put("HomePJFragment", new String[] { "打开导航", "打开音乐", "导航去.. ",
					"打电话给" });
			put("GNaviFragment0", new String[] { "导航去/到", "返回首页" });
			put("GNaviFragment1", new String[] { "第几个/条", "导航去/到", "返回首页" });
			put("GNaviFragment2", new String[] { "导航去/到", "避免拥堵", "开始导航",
					"返回首页" });
			put("MusicFragment", new String[] { "下一首", "停止播放", "继续播放", "随机播放" });
			put("PhoneFragment", new String[] { "打电话给", "挂断电话" });
			put("SettingFragment", new String[] { "音量管理", "连接wifi", "打开热点",
					"连接遥控器", "流量监控" });
			put("RecorderFragment", new String[] { "开始录像", "后台录像", "开始拍照",
					"结束录像" });
			put("DoubanFragment", new String[] { "下一首", "停止播放", "继续播放" });
			put("TrafficFragment", new String[] { "返回首页" });
		}
	};
}
