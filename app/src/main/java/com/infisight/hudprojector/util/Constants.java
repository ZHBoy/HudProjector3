package com.infisight.hudprojector.util;

import com.infisight.hudprojector.R;

public class Constants {

	// 消息类型：数据，命令，安装包 等
	public static final int TYPE_AUDIO = 0x00301;// 音频命令
	public static final int TYPE_TEXT = 0x00302;// 文本命令
	public static final int TYPE_CMD = 0x00303;// 命令命令
	public static final int TYPE_IMG = 0x00304;// 图片命令
	public static final int TYPE_STREAM = 0x00305;// 流媒体命令
	public static final int TYPE_RETURNIFO = 0x00307;// 服务器返回信息

	// 首页图标的显示与隐藏(具体名称)0:打招呼 1:听 2：思考 3：识别不出来 4：休息
	public final static int MAIN_ICON_RUI_STATE = 0x10001;// 小瑞状态
	public final static int MAIN_ICON_MUSIC_STATE = 0x10002;// 音乐图标状态
	public final static int MAIN_ICON_OBD_STATE = 0x10003;// OBD状态
	public final static int MAIN_ICON_NAIV_STATE = 0x10004;// 导航状态

	// 打电话模块command
	public final static int MODEL_PHONE_COM = 0x02001;// 打电话模块
	public final static int MODEL_PHONE_NAME_COM = 0x02002;// 说联系人
	public final static int MODEL_PHONE_ITEM_CHOOSE_COM = 0x02003;// 选择联系人
	public final static int MODEL_PHONE_HANG_UP_COM = 0x02004;// 挂断
	public final static int MODEL_PHONE_ANSWER_COM = 0x02005;// 接听
	// 打电话模块action
	public final static String MODEL_PHONE_ACTION = "MODEL_PHONE_ACTION";// 打电话模块action
	public final static String MODEL_PHONE_NAME_ACTION = "MODEL_PHONE_NAME_ACTION";// 说联系人action
	public final static String MODEL_PHONE_ITEM_CHOOSE_ACTION = "MODEL_PHONE_ITEM_CHOOSE_ACTION";// 选择联系人action
	public final static String MODEL_PHONE_HANG_UP_ACTION = "MODEL_PHONE_HANG_UP_ACTION";// 挂断
	// 流量发送
	public final static int MODEL_TRAFFIC_COM = 0x03001;
	public final static int MODEL_TRAFFIC_GPRS_COM = 0x03002;
	public final static int MODEL_TRAFFIC_WIFI_COM = 0x03003;
	public static final int MODEL_TRAFFIC_GPRS_P_COM = 0x03004;
	public static final int MODEL_TRAFFIC_WIFI_P_COM = 0x03005;
	public final static String MODEL_TRAFFIC_ACTION = "MODEL_TRAFFIC_ACTION";
	// 导航位置收藏发送
	public final static String MODEL_NAVI_ACTION = "MODEL_NAVI_ACTION";
	public final static int MODEL_NAVI_COM = 0x04001;
	public final static int MODEL_NAVI_LOCATION_COM = 0x04002;
	// wifi信息SSID,PWD发送
	public final static String MODEL_WIFIINFO_ACTION = "MODEL_WIFIINFO_ACTION";
	public final static int MODEL_WIFIINFO_COM = 0x05001;
	public final static int MODEL_WIFIINFO_INFO_COM = 0x05002;
	public final static int MODEL_WIFIINFO_BACK_COM = 0x05003;// hud从手机端获得wifi信息后回送手机

	// 公共模块
	public final static int MODEL_COMMON_COM = 0x06001;// 此模块标示
	public final static int MODEL_CLEAR_CAR_INFO = 0x06002;// 清除车主信息

	/**
	 * 地理位置的信息数据
	 */
	public static String LOCATION_INFO = "LOCATION_INFO";
	public static String LOCATIONDATA = "LOCATIONDATA";
	public static String LATITUDE = "LATITUDE";
	public static String LONGITUDE = "LONGITUDE";
	public static String CITYNAME = "CITYNAME";
	public static String ADDR = "ADDR";
	public static String SPEED = "SPEED";

	// 与Socket相关的值
	public static int SERVERPORT = 8988;
	// 与Socket详细相关的常量
	public static String MSG_SEND = "SEND_MSG";
	public static String MSG_NEW = "NEW_MSG";
	public static String MSG_CMD = "MSG_CMD";
	public static String MSG_PARAM = "MSG_PARAM";
	// 设置首页菜单相关的配置
	public static String MENU_INFO = "MENU_INFO";
	public static String MENUDATA = "MENUDATA";

	/**
	 * 控制面板上的操作
	 */
	public static final String GESTURE_TOUP = "0x00001"; // 向上
	public static final String GESTURE_TODOWN = "0x00002";// 向下
	public static final String GESTURE_TOLEFT = "0x00003";// 向左
	public static final String GESTURE_TORIGHT = "0x00004";// 向右
	public static final String GESTURE_DOUBLECLICK = "0x00005";// 双击，暂时不用
	public static final String GESTURE_SINGLETAP = "0x00006";// 单击
	public static final String GETSTURE_LONGPRESS = "0x00007";// 长按
	public static final String GETSTURE_BACK = "0x00008";// 返回操作

	// 控制面板上的按钮操作，这个不一定能用得着
	public static final String GESTURE_INFO = "0x00101";// 手势操作
	public static final String OPER_NAV = "0x00103";// 导航指令
	public static final String OPER_BACK = "0x00201";// 返回操作
	// 短信
	public static final String SMS_NEW = "0x00401";
	// 电话
	public static final String TELEPHONY_NEW = "0x00402";
	public static final String TELEPHONY_OFFHOOK = "0x00501";// 接听
	public static final String TELEPHONY_IDLE = "0x00502";// 挂断
	// 程序内部用的导航命令
	public static final String NAV_START = "0x00601";
	public static final String NAV_END = "0x00602";
	public static final String NAV_HUD_DATA = "0x00603";// 手机端发来的导航实时状态数据，用于更新HUD界面
	public static final String OBD_HUD_DATA = "0x00604";// 手机端发来的OBD实时数据，用于更新HUD界面
	// 消息分发的方向，内部消息分发机制
	public static final String NAV_MSG = "0x00701";
	public static final String HUD_MSG = "0x00702";
	public static final String MSG_MSG = "0x00703";
	public static final String PHONE_MSG = "0x00704";
	// 语音导航相关的指令集
	public static final String V_NAV_CMD = "0x10201";// 导航
	public static final String V_CALL_CMD = "0x10202";// 电话
	public static final String V_STARTAPP_CMD = "0x10203";// 开启应用
	public static final String V_VIDEO_CMD = "0x10204";// 开启应用
	public static final String V_PHOTO_CMD = "0x10205";// 开启应用

	// 解析命令指示
	public static final String K_SPLITS = "0x10101";// 终点分割字符集
	public static final String K_MAP_ORPERCMD_ENLARGE = "0x10102";// 放大地图操作字符集
	public static final String K_MAP_ORPERCMD_SMALLERE = "0x10103";// 缩小地图操作指令集
	public static final String K_MAP_ORPERCMD_UP = "0x10104";// 上移地图
	public static final String K_MAP_ORPERCMD_DOWN = "0x10105";// 下移地图
	public static final String K_MAP_ORPERCMD_LEFT = "0x10106";// 左移地图
	public static final String K_MAP_ORPERCMD_RIGHT = "0x10107";// 右移地图
	public static final String K_MAP_NEARBY_ADDR_SPLITS = "0x10108";// 附近的地点
	public static final String K_MAP_NEARBY_WEATHER_SPLITS = "0x10109";// 天气
	public static final String K_V_NOCLEARHEAR = "0x10110";// 没有获得语音输入的解析结果
	public static final String K_V_NOFIND_ADDR = "0x10111";// 没有找到终点
	public static final String K_MAP_LSTVIEW_DIRECT_UP = "0x10112";// 联想词listView上一条
	public static final String K_MAP_LSTVIEW_DIRECT_DOWN = "0x10113";// 联想词listView下一条
	public static final String K_MAP_LSTVIEW_DIRECT_AUTO2 = "0x10114";// 联想词listView选中指定条目
	public static final String K_MAP_LSTVIEW_DIRECT_AUTO = "0x10115";// 联想词listView选中指定条目，配合使用
	public static final String K_MAP_LSTVIEW_DIRECT_CHOOSE = "0x10116";// 设为终点
	public static final String K_MAP_NAI_START = "0x10117";// 开始导航
	public static final String K_MAP_NAI_END = "0x10118";// 结束导航

	/**
	 * 通用参数设置
	 */
	public static String STOPCONNECT = "STOPCONNECT";
	public static String RETURNWIFIDIRECTSTATE = "RETURNWIFIDIRECTSTATE";// 发送服务器，wifi-direct返回状态

	/**
	 * 
	 * 定义了机动点显示图标的对应关系，如maneuverName 为R.drawable.nsdk_drawable_rg_ic_turn_back 的
	 * turn_back字段，具体图标请用户自行设计。
	 */
	public static final int[] gTurnIconID = {
			R.drawable.nsdk_drawable_rg_ic_turn_back, /** < 无效值 */
			R.drawable.nsdk_drawable_rg_ic_turn_front, /** < 直行 */
			R.drawable.nsdk_drawable_rg_ic_turn_right_front, /** < 右前方转弯 */
			R.drawable.nsdk_drawable_rg_ic_turn_right, /** < 右转 */
			R.drawable.nsdk_drawable_rg_ic_turn_right_back, /** < 右后方转弯 */
			R.drawable.nsdk_drawable_rg_ic_turn_back, /** < 掉头 */
			R.drawable.nsdk_drawable_rg_ic_turn_left_back, /** < 左后方转弯 */
			R.drawable.nsdk_drawable_rg_ic_turn_left, /** < 左转 */
			R.drawable.nsdk_drawable_rg_ic_turn_left_front, /** < 左前方转弯 */
			R.drawable.nsdk_drawable_rg_ic_turn_ring, /** < 环岛 */
			R.drawable.nsdk_drawable_rg_ic_turn_ring_out, /** < 环岛出口 */
			R.drawable.nsdk_drawable_rg_ic_turn_left_side, /**
			 * < 普通/JCT/SAPA二分歧
			 * 靠左
			 */
			R.drawable.nsdk_drawable_rg_ic_turn_right_side, /**
			 * < 普通/JCT/SAPA二分歧
			 * 靠右
			 */
			R.drawable.nsdk_drawable_rg_ic_turn_left_side_main, /** < 左侧走本线 */
			R.drawable.nsdk_drawable_rg_ic_turn_branch_left_straight, /**
			 * <
			 * 靠最左走本线
			 */
			R.drawable.nsdk_drawable_rg_ic_turn_right_side_main, /** < 右侧走本线 */
			R.drawable.nsdk_drawable_rg_ic_turn_branch_right_straight, /**
			 * <
			 * 靠最右走本线
			 */
			R.drawable.nsdk_drawable_rg_ic_turn_branch_center, /** < 中间走本线 */
			R.drawable.nsdk_drawable_rg_ic_turn_left_side_ic, /** < IC二分歧左侧走IC */
			R.drawable.nsdk_drawable_rg_ic_turn_right_side_ic, /** < IC二分歧右侧走IC */
			R.drawable.nsdk_drawable_rg_ic_turn_branch_left, /**
			 * < 普通三分歧/JCT/SAPA
			 * 靠最左
			 */
			R.drawable.nsdk_drawable_rg_ic_turn_branch_right, /**
			 * <
			 * 普通三分歧/JCT/SAPA 靠最右
			 */
			R.drawable.nsdk_drawable_rg_ic_turn_branch_center, /**
			 * <
			 * 普通三分歧/JCT/SAPA 靠中间
			 */
			R.drawable.nsdk_drawable_rg_ic_turn_start, /** < 起始地 */
			R.drawable.nsdk_drawable_rg_ic_turn_dest, /** < 目的地 */
			R.drawable.nsdk_drawable_rg_ic_turn_via_1, /** < 途径点1 */
			R.drawable.nsdk_drawable_rg_ic_turn_via_1, /** < 途径点2 */
			R.drawable.nsdk_drawable_rg_ic_turn_via_1, /** < 途径点3 */
			R.drawable.nsdk_drawable_rg_ic_turn_via_1, /** < 途径点4 */
			R.drawable.nsdk_drawable_rg_ic_turn_inferry, /** < 进入渡口 */
			R.drawable.nsdk_drawable_rg_ic_turn_inferry, /** < 脱出渡口 */
			R.drawable.nsdk_drawable_rg_ic_turn_tollgate, /** < 收费站 */
			R.drawable.nsdk_drawable_rg_ic_turn_left_side_main, /**
			 * <
			 * IC二分歧左侧直行走IC
			 */
			R.drawable.nsdk_drawable_rg_ic_turn_right_side_main, /**
			 * <
			 * IC二分歧右侧直行走IC
			 */
			R.drawable.nsdk_drawable_rg_ic_turn_left_side_main, /**
			 * <
			 * 普通/JCT/SAPA二分歧左侧 直行
			 */
			R.drawable.nsdk_drawable_rg_ic_turn_right_side_main, /**
			 * <
			 * 普通/JCT/SAPA二分歧右侧 直行
			 */
			R.drawable.nsdk_drawable_rg_ic_turn_branch_left_straight, /**
			 * <
			 * 普通/JCT/SAPA三分歧左侧 直行
			 */
			R.drawable.nsdk_drawable_rg_ic_turn_branch_center, /**
			 * <
			 * 普通/JCT/SAPA三分歧中央 直行
			 */
			R.drawable.nsdk_drawable_rg_ic_turn_branch_right_straight, /**
			 * <
			 * 普通/JCT/SAPA三分歧右侧 直行
			 */
			R.drawable.nsdk_drawable_rg_ic_turn_branch_left, /** < IC三分歧左侧走IC */
			R.drawable.nsdk_drawable_rg_ic_turn_branch_center, /** < IC三分歧中央走IC */
			R.drawable.nsdk_drawable_rg_ic_turn_branch_right, /** < IC三分歧右侧走IC */
			R.drawable.nsdk_drawable_rg_ic_turn_branch_left_straight, /**
			 * <
			 * IC三分歧左侧直行
			 */
			R.drawable.nsdk_drawable_rg_ic_turn_branch_center, /** < IC三分歧中间直行 */
			R.drawable.nsdk_drawable_rg_ic_turn_branch_right_straight, /**
			 * <
			 * IC三分歧右侧直行
			 */
			R.drawable.nsdk_drawable_rg_ic_turn_left_side_main, /** < 八方向靠左直行 */
			R.drawable.nsdk_drawable_rg_ic_turn_right_side_main, /** < 八方向靠右直行 */
			R.drawable.nsdk_drawable_rg_ic_turn_branch_left_straight, /**
			 * <
			 * 八方向靠最左侧直行
			 */
			R.drawable.nsdk_drawable_rg_ic_turn_branch_center, /** < 八方向沿中间直行 */
			R.drawable.nsdk_drawable_rg_ic_turn_branch_right_straight, /**
			 * <
			 * 八方向靠最右侧直行
			 */
			R.drawable.nsdk_drawable_rg_ic_turn_left_2branch_left, /**
			 * <
			 * 八方向左转+随后靠左
			 */
			R.drawable.nsdk_drawable_rg_ic_turn_left_2branch_right, /**
			 * <
			 * 八方向左转+随后靠右
			 */
			R.drawable.nsdk_drawable_rg_ic_turn_left_3branch_left, /**
			 * <
			 * 八方向左转+随后靠最左
			 */
			R.drawable.nsdk_drawable_rg_ic_turn_left_3branch_middle, /**
			 * <
			 * 八方向左转+随后沿中间
			 */
			R.drawable.nsdk_drawable_rg_ic_turn_left_3branch_right, /**
			 * <
			 * 八方向左转+随后靠最右
			 */
			R.drawable.nsdk_drawable_rg_ic_turn_right_2branch_left, /**
			 * <
			 * 八方向右转+随后靠左
			 */
			R.drawable.nsdk_drawable_rg_ic_turn_right_2branch_right, /**
			 * <
			 * 八方向右转+随后靠右
			 */
			R.drawable.nsdk_drawable_rg_ic_turn_right_3branch_left, /**
			 * <
			 * 八方向右转+随后靠最左
			 */
			R.drawable.nsdk_drawable_rg_ic_turn_right_3branch_middle, /**
			 * <
			 * 八方向右转+随后沿中间
			 */
			R.drawable.nsdk_drawable_rg_ic_turn_right_3branch_right, /**
			 * <
			 * 八方向右转+随后靠最右
			 */
			R.drawable.nsdk_drawable_rg_ic_turn_lf_2branch_left, /** < 八方向左前方靠左侧 */
			R.drawable.nsdk_drawable_rg_ic_turn_lf_2branch_right, /** < 八方向左前方靠右侧 */
			R.drawable.nsdk_drawable_rg_ic_turn_rf_2branch_left, /** < 八方向右前方靠左侧 */
			R.drawable.nsdk_drawable_rg_ic_turn_rf_2branch_right /** < 八方向右前方靠右侧 */
	};
	/**
	 * 定音手势的方向，来依据手势的功能
	 */
	// public static int HandleDirection{
	//
	// };

	// 遥控器模块
	public final static String CONTROL_MAIN_KEY = "CONTROL_MAIN_KEY";// 主页遥控控制
	// 主从连接控制
	public final static String AppControlAuthorityAction = "AppControlAuthorityAction";// 主从连接最外层action
	// 密码上传hud
	public final static String AppControlPasswordAction = "AppControlPasswordAction";// 手机端与hud端密码的通讯
	// 导航偏好上传hud
	public final static String AppControlNaviPreferAction = "AppControlNaviPreferAction";// 手机HUD
																							// 导航偏好通讯
	// WIFI密码（wifi名称和密码hashmap）上传HUD
	public final static String AppControlWifiPasswordAction = "AppControlWifiPasswordAction";// 手机端与HUD端wifi连接信息通讯
	// 需要hud回传wifi密码的信号（无实际数据）
	public final static String AppControlNeedWifiAction = "AppControlNeedWifiAction";
	/*
	 * 语音导航参数设置
	 */
	public final static String CONNECT_VOICE_MODULE = "CONNECT_VOICE_MODULE";// 语音关联各个模块（通过广播）
	public final static String CONNECT_VOICE_MODULE_VALUE = "CONNECT_VOICE_MODULE_VALUE";// 语音关联各个模块（通过广播），具体内容
	public final static String CONNECT_VOICE_MODULE_COMMAND = "CONNECT_VOICE_MODULE_COMMAND";// 语音关联各个模块（通过广播），具体指令标示

	// 首页模块action
	public final static String MAIN_NAVI_ACTION = "MAIN_NAVI_ACTION";// 首页导航ACTION
	public final static String MAIN_HUD_ACTION = "MAIN_HUD_ACTION";// 首页HUD的ACTION
	public final static String MAIN_MUSIC_ACTION = "MAIN_MUSIC_ACTION";// 首页音乐ACTION
	public final static String MAIN_DOUBAN_ACTION = "MAIN_DOUBAN_ACTION";// 首页豆瓣ACTION
	public final static String MAIN_PHONE_ACTION = "MAIN_PHONE_ACTION";// 首页电话ACTION
	public final static String MAIN_OBD_ACTION = "MAIN_OBD_ACTION";// 首页OBD的ACTION
	public final static String COMMON_UTIL_ACTION = "COMMON_UTIL_ACTION";// 公共ACTION（比如：选择第几条）
	public final static String MAIN_EXIT_PROJECT_ACTION = "MAIN_EXIT_PROJECT_ACTION";// 退出应用ACTION
	public final static String MAIN_SETTING_ACTION = "MAIN_SETTING_ACTION";// 首页设置的ACTION
	public static final String MAIN_GORECORDER_ACTION = "MAIN_GORECORDER_ACTION";
	public final static String MAIN_RECORDER_ACTION = "MAIN_RECORDER_ACTION";// 首页行车记录仪的ACTION
	public final static String MAIN_FILEMANAGE_ACTION = "MAIN_FILEMANAGE_ACTION";// 文件管理的ACTION
	public final static String MAIN_VIDEO_ACTION = "MAIN_VIDEO_ACTION";// 视频播放ACTION
	public final static String FRAG_TO_MAIN_ACTION = "FRAG_TO_MAIN_ACTION";// 需要现在fragment中处理然后交给mainactivity转发的action。比如：开始导航、查看全览图等
	public final static String MAIN_ICON_STATE_ACTION = "MAIN_ICON_STATE_ACTION";// 界面标题栏图标ACTION
	public final static String NEW_NAVI_PLAN_ACTION = "NEW_NAVI_PLAN_ACTION";// 手机端导航ACTION
	public final static String CERTAIN_ACTION = "CERTAIN_ACTION";// dialog弹出框点击操作action
	public final static String HUD_TITLE_SHOW_ACTION = "HUD_TITLE_SHOW_ACTION";// 控制顶部条显示或隐藏

	public final static String HUD_MENU_SHOW_ACTION = "HUD_MENU_SHOW_ACTION";// 控制HUD菜单键
	public final static String MAIN_KEY_SHOW_ACTION = "MAIN_KEY_SHOW_ACTION";// 控制提示词的显示隐藏
	public final static String MUSIC_AUDIOFOCUS_ACTION = "MUSIC_AUDIOFOCUS_ACTION";// 控制音乐图标在hud界面的显示和隐藏
	public final static String CONNECTIVITY_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";// 用于控制界面图标的显示、隐藏
	public final static String ON_VOLUME_CHANGED_ACTION = "onVolumeChanged";
	public final static String CONTROL_DIALOG_TO_NAVI = "CONTROL_DIALOG_TO_NAVI";// 控制dialog的显示和隐藏，导航去哪哪哪

	public final static String DOUBAN_SETUPREADY_ACTION = "DOUBAN_SETUPREADY_ACTION";// 豆瓣音乐播放准备完成标识
	public final static String DOUBAN_NEXTPLAY_ACTION = "DOUBAN_NEXTPLAY_ACTION";// 豆瓣音乐播放下一首
	public final static String DOUBAN_NEXTREADY_ACTION = "DOUBAN_NEXTREADY_ACTION";// 豆瓣音乐播放下一首准备完成

	// 导航到附近的地点参数
	public final static String NAIV_TO_SEARCH_POI_LAT = "NAIV_TO_SEARCH_POI_LAT";// 终点坐标

	// 设置Action
	public final static String MODEL_SETTING_ACTION = "MODEL_SETTING_ACTION";

	// MainActivity中接受到语音指令
	public final static String MAIN_VOICE = "MAIN_VOICE";// 最外层语音指令标志
	public final static String MAIN_VOICE_GNAVI = "MAIN_VOICE_GNAVI";// 语音导航指令标志

	// 遥控器相关的连接设备
	public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
	public final static String REMOTE_DEVICE_INFO = "REMOTEDEVICEINFO";
	public final static String REMOTE_CONNECTED_DEVICES = "REMOTECONNECTEDDEVICES";// 存储已经连接的设备列表
	public final static String REMOTE_CURR_ADDR = "REMOTECURRADDR";// 当前连接设备的地址
	public final static String REMOTE_CUR_NAME = "REMOTECURNAME";// 当前选择的遥控器设备名称
	public final static String REMOTE_CONNECT_STATUS = "REMOTE_CONNECT_STATUS";// 当前选择的遥控器连接状态

	// 遥控器的通知回调属性
	public final static String REMOTE_ACTION_DISCONNECT_GATT = "REMOTE_ACTION_DISCONNECT_GATT";// 当前连接设备的地址
	public final static String REMOTE_ACTION_SENDDATA = "REMOTE_ACTION_SENDDATA";// 当前连接设备的地址
	public final static String REMOTE_DATA = "REMOTE_DATA";
	public final static String ACTION_GATT_CONNECTED = "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
	public final static String ACTION_GATT_DISCONNECTED = "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
	public final static String ACTION_GATT_SERVICES_DISCOVERED = "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
	public final static String ACTION_DATA_AVAILABLE = "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
	public final static String ACTION_HOTSPOT_AVAILABLE = "ACTION_HOTSPOT_AVAILABLE";// 热点可用
	public final static String ACTION_HOTSPOT_UNAVAILABLE = "ACTION_HOTSPOT_UNAVAILABLE";// 热点不可用
	public final static String ACTION_INTERNET_AVAILABLE = "ACTION_INTERNET_AVAILABLE";// 当前连接设备的地址
	public final static String ACTION_INTERNET_UNAVAILABLE = "ACTION_INTERNET_UNAVAILABLE";// 当前连接设备的地址

	// 全程语音指令集合(语音识别:以数组的格式定义，第一个参数为这个这个指令的名称，其他的即具体的指令)
	public static final String[] F_R_CHOOSE_ITEM = { "F_CHOOSE_ITEM", "选择第",
			"第", "选第" };// 选择第几个条目
	public static final String[] F_R_START_NAVI = { "F_R_START_NAVI", "开始导航" };// 选择第几个条目
	// 音乐模块
	public static final String[] F_R_MUSIC_ON = { "F_R_MUSIC_ON", "打开音乐" };// 放首歌
	// public static final String[] F_R_MUSIC_PAGE_UP = { "F_R_MUSIC_PAGE_UP",
	// "上一页", "上瘾", "上映", "上页", "上一任", "上演" };// 上一页

	// public static final String[] F_R_MUSIC_PAGE_DOWN = {
	// "F_R_MUSIC_PAGE_DOWN",
	// "下一页", "下页", "夏夜", "夏燕儿", "小鱼儿", "下雨", "下雨了" };// 下一页
	// public static final String[] F_R_MUSIC_ALL_MUSIC_LIST = {
	// "F_R_MUSIC_ALL_MUSIC_LIST", "全部歌曲", "音乐列表", "播放列表" };// 全部歌曲

	public static final String[] F_R_MUSIC_CONTINUE_PLAY = {
			"F_R_MUSIC_CONTINUE_PLAY", "继续播放" };// 继续播放

	public static final String[] F_R_MUSIC_MUSIC_PLAY = {
			"F_R_MUSIC_MUSIC_PLAY", "播放音乐" };// 其他界面播放音乐

	// public static final String[] F_R_MUSIC_PAUSE_PLAY = {
	// "F_R_MUSIC_PAUSE_PLAY", "暂停播放" };// 暂停播放

	public static final String[] F_R_MUSIC_RANDOM_PLAY = {
			"F_R_MUSIC_RANDOM_PLAY", "随机播放" };// 随机播放

	public static final String[] F_R_MUSIC_ORDER_PLAY = {
			"F_R_MUSIC_ORDER_PLAY", "顺序播放" };// 顺序播放

	public static final String[] F_R_MUSIC_JUST_ONE_PLAY = {
			"F_R_MUSIC_JUST_ONE_PLAY", "单曲循环" };// 单曲循环

	public static final String[] F_R_MUSIC_LAST_MUSIC_PLAY = {
			"F_R_MUSIC_LAST_MUSIC_PLAY", "上一首" };// 上一首

	public static final String[] F_R_MUSIC_NEXT_MUSIC_PLAY = {
			"F_R_MUSIC_NEXT_MUSIC_PLAY", "下一首" };// 下一首

	public static final String[] F_R_MUSIC_STOP_PLAY = { "F_R_MUSIC_STOP_PLAY",
			"停止播放", "停止吧" };
	// public static final String[] F_R_SEARCH_CONTENT = { "F_R_SEARCH_CONTENT",
	// "搜索" };

	// 电话模块
	public static final String[] F_R_PHONE_TO = { "F_R_PHONE_TO", "打电话给" };// 首页，电话
	// public static final String[] F_R_PHONE_PAGE_UP = { "F_R_PHONE_PAGE_UP",
	// "往上翻" };// 上翻
	//
	// public static final String[] F_R_PHONE_PAGE_DOWN = {
	// "F_R_PHONE_PAGE_DOWN",
	// "往下翻" };// 下翻
	public static final String[] F_R_PHONE_HANG_UP = { "F_R_PHONE_HANG_UP",
			"挂断" };// 挂断电话
	public static final String[] F_R_PHONE_ANSWER = { "F_R_PHONE_ANSWER", "接听" };// 接听电话

	public static final String[] F_R_READ_MSG_INFO = { "F_R_READ_MSG_INFO",
			"读取" };// 读取

	public static final String[] F_R_IGNORE_MSG_INFO = { "F_R_IGNORE_MSG_INFO",
			"忽略" };// 忽略
	// obd模块
	public static final String[] F_R_OBD_SHOW = { "F_R_OBD_SHOW", "打开汽车obd" };// 打开obd
	// 导航模块
	public static final String[] F_R_NAV_GOTO = { "F_R_NAV_GOTO", "导航去", "导航到",
			"早上去", "早上到", "导航区", "我要去", "我要到" };// 导航要去的地址
	public static final String[] F_R_OPEN_NAVI = { "F_R_OPEN_NAVI", "打开导航",
			"打开的", "打开灯" };// 打开导航
	public static final String[] F_R_NAVI_ROUTE_PLAN = { "F_R_NAVI_ROUTE_PLAN",
			"避免拥堵", "不走高速", "避免收费" };// 返回导航
	public static final String[] F_R_NAVI_ROUTE_PLAN_All_SEE = {
			"F_R_NAVI_ROUTE_PLAN_All_SEE", "全览图", "全脸涂", "路线总览", "路线总揽",
			"路径总览", "总路线", "路线总揽", "路径总揽" };// 返回导航
	public static final String[] F_R_STOP_NAVI = { "F_R_STOP_NAVI", "退出导航",
			"推出导航" };// 退出导航

	public static final String[] F_R_PAUSE_NAVI = { "F_R_PAUSE_NAVI", "暂停",
			"暂停倒", "暂停导航", "暂停的" };// 暂停导航

	public static final String[] F_R_RESUME_NAVI = { "F_R_RESUME_NAVI", "继续导航",
			"继续道", "继续的", "继续倒" };// 继续导航

	public static final String[] F_R_IS_TRUE_STOP_NAVI = {
			"F_R_IS_TRUE_STOP_NAVI", "确定" };// 退出导航确定
	public static final String[] F_R_POSITION_SHOW = { "F_R_POSITION_SHOW",
			"附近的" };// 退出导航确定

	public static final String[] F_R_NOT_IS_TRUE_STOP_NAVI = {
			"F_R_NOT_IS_TRUE_STOP_NAVI", "取消" };// 退出导航取消
	public static final String[] F_R_RETURN_HOME = { "F_R_RETURN_HOME", "首页",
			"返回首页", "打开首页", "返回主页", "打开主页" };// 返回首页
	public static final String[] F_R_EXIT_PROJECT = { "F_R_EXIT_PROJECT",
			"退出应用" };// 退出应用
	public static final String[] F_R_ENLARGE_OR_NARROW_MAP = {
			"F_R_ENLARGE_OR_NARROW_MAP", "放大地图", "缩小地图" };// 放大地图
	public static final String[] F_R_SETTING = { "F_R_SETTING", "打开设置" };// 导航要去的地址
	public static final String[] F_R_SAVE_MY_LOCATION = {
			"F_R_SAVE_MY_LOCATION", "收藏位置" };// 唤醒词，用于识别过程中
	public static final String[] F_R_WAKE_NAME = { "祥瑞", "小芮", "小瑞", "小蕊",
			"小睿", "小锐", "小徽", "小丽" };// 唤醒词，用于识别过程中
	// 行车记录仪
	public static final String[] F_R_RECORD_LAUNCH = { "F_R_RECORD_LAUNCH",
			"记录仪", "机理" };// 打开行车记录仪
	public static final String[] F_R_RECORD_START = { "F_R_RECORD_START",
			"开始录像" };// 开始录像
	public static final String[] F_R_RECORD_END = { "F_R_RECORD_END", "结束录像",
			"停止录像" };// 结束录像
	public static final String[] F_R_RECORD_BACKGROUND = {
			"F_R_RECORD_BACKGROUND", "后台录像" };// 后台录像
	public static final String[] F_R_RECORD_MAKEPHOTO = {
			"F_R_RECORD_MAKEPHOTO", "拍照" };
	public static final String[] F_R_VIDEO_BACK = { "F_R_VIDEO_BACK", "返回" };// 从播放页面返回
	public static final String[] F_R_FILE_BACK = { "F_R_FILE_BACK", "返回上一级" };
	public static final String[] F_R_SETTING_WIFI = { "F_R_SETTING_WIFI",
			"连接wifi" };
	public static final String[] F_R_SETTING_HOTPOINT = {
			"F_R_SETTING_HOTPOINT", "打开热点" };
	public static final String[] F_R_SETTING_REMOTE = { "F_R_SETTING_REMOTE",
			"遥控器" };
	public static final String[] F_R_SETTING_TRAFFIC = { "F_R_SETTING_TRAFFIC",
			"流量监控" };
	public static final String[] F_R_SETTING_VOICE = { "F_R_SETTING_VOICE",
			"音量管理" };
	public static final String[] F_R_SETTING_VOICEUP = { "F_R_SETTING_VOICEUP",
			"提高音量", "放大音量", "增加音量" };
	public static final String[] F_R_SETTING_VOICEDOWN = {
			"F_R_SETTING_VOICEDOWN", "降低音量", "减小音量" };

	// 全程语音指令集合(语音合成)
	public static final String F_C_FOUND_RIGHT_ADDR = "请选择";// 导航的目的地
	public static final String F_C_FOUND_RIGHT_ROUTE_PLAN = "请选择具体线路！";// 导航的目的地
	public static final String F_C_INPUT_RIGHT_ADDR = "请说您要去的地方";// 请输入确切目的地
	public static final String F_C_DEFEAT_LOCATION = "定位失败，您网络不太好";// 定位失败
	public static final String F_C_READY_NAVI = "，是否开始 导航？";// 导航的目的地
	public static final String F_C_RIGHT_TIME = "请选择正确条目";// 请选择正确条目
	public static final String F_C_NOT_UNDERSTAND = "未能识别你的操作请重试";// 没有识别操作
	public static final String F_C_NOT_FOUND_RIGHT_ADDR = "输入目的地有误，请重试";// 导航目的地输入有误
	public static final String F_C_NOT_FOUND_TIP_ADDR = "没有找到相关地点，请再说一次。";// 没有找到相关地点，请再说一次。

	public static final String F_C_FIND_LOC_DEFEAT = "终点搜索失败，请检查网络";// 没有找到相关地点，请再说一次。

	public static final String F_C_START_NAVI = "导航开始";// 导航开始
	public static final String F_C_RETURN_HOME = "返回首页";// 返回首页
	public static final String F_C_TASK_IS_OK = "操作完成，请继续";// (公共操作结束提示)
	public static final String F_C_NAVI_NOT_START = "导航还没有开始，请先选择目的地。";
	public static final String F_C_SAVED_AGO = "您之前收藏过当前位置！";// 返回首页
	public static final String F_C_PAUSE_NAVI = "导航暂停成功";// 暂停导航
	public static final String F_C_RESUME_NAVI = "导航开始";// 继续导航

	public static final String F_C_PHONE_SPECIFIC_PERSON = "请选择具体联系人";// 打电话给谁谁谁
	public static final String F_C_PHONE_TO = "正在发起呼叫";// 正在发起呼叫
	public static final String F_C_PHONE_HANG_UP = "通话已结束";// 通话已结束
	public static final String F_C_PHONE_NO_PERSON = "没找到相应联系人";// 没找到相应体联系人

	public static final String F_C_OPEN_MUSIC = "开始播放歌曲";// 小瑞已经帮你打开音乐播放器
	public static final String F_C_STOP_MUSIC = "音乐已停止";// 小瑞已经帮你打开音乐播放器

	public static final String F_C_OBD_INFO = "obd信息";// obd信息
	public static final String F_C_CONTINUE_NAVI = "继续导航";// 继续导航
	public static final String F_C_STOP_CURRENT_NAVI = "开启新的导航吗";// 是否结束当前导航
	public static final String F_C_STOP_NAVI = "结束导航吗";// 退出本次导航
	public static final String F_C_NAVI_NOT_ON = "导航还没开始";// 退出本次导航
	public static final String F_C_RUI_GO_BACK = "，，需要的时候，请再次唤醒！";// 一会见
	public static final String F_C_RUI_TIP_SHOW = "没有听懂您说话";// 第一次自动停止语音识别时，提示词
	public static final String F_C_WHAT_YOU_DO = "请吩咐!";
	public static final String F_C_WHAT_YOU_DO_WAKE = "请说!";
	public static final String F_C_NET_ERROR = "您的网络不好，请检查后重试！";
	public static final String F_C_WHAT_YOU_CHOOSE_ROUTE_PLAN = "你所选路线是，";
	public static final String F_C_OUT_ITIMS = "你选择的数据超出范围，请确认";
	public static final String F_C_DETIAL_LOCATION = "未找到具体地点请重试";
	public static final String F_C_DEVICE_NO_CONNECT = "当前设备已经与手机端断连，请连接后重试。";
	public static final String F_C_NAVI_GPS_ERROR = "定位失败，请检查您的gps后重试";
	public static final String handle_info_command = "handle_info_command";// hud顶部条信息(标识)
	public static final String handle_info_content = "handle_info_content";// hud顶部条信息(标识)
	public static final String handle_answer = "handle_answer";// hud顶部条:处理接听操作
	public static final String handle_hang_up = "handle_hang_up";// hud顶部条信息：处理挂断操作
	public static final String F_C_NAVI_ROUTE_ERROR = "导航准备失败，请检查您的网络后重试！";// hud导航准备失败
	public static final String F_C_MIAT_ERROR = "语音识别出现错误，请检查您的您的网络连接";// hud导航准备失败
	public static final String F_C_OUT_TIME_SEARCH = "请求超时，请检查网络";// 请求超时
	public static final String F_C_NOT_CONNECT_PHONE = "您尚未和手机端连接";
	public static final String F_C_CAN_NOT_PHONE = "您正处于通话中，无法再次创建通话";
	public static final String F_C_DISCONNECTED = "当前处于无网络状态，请检查！";
	public static final String F_C_UNBECOMING_TYPE = "您当前3g网络信号较差";

	// 豆瓣FM
	public static final String[] F_R_OPEN_DOUBAN = { "F_R_OPEN_DOUBAN", "打开豆瓣" };

	// public static final String[] F_R_DOUBAN_DELETE_SONG = {
	// "F_R_DOUBAN_DELETE_SONG", "删除" };// 禁止播放此首歌
	// public static final String[] F_R_DOUBAN_SAVE_SONG = {
	// "F_R_DOUBAN_SAVE_SONG", "收藏" };// 收藏歌曲
	public static final String[] F_R_DOUBAN_NEXT_PLAY = {
			"F_R_DOUBAN_NEXT_PLAY", "下一首" };// 下一首
	public static final String[] F_R_DOUBAN_CHANNEL_CHOOSE = {
			"F_R_DOUBAN_CHANNEL_CHOOSE", "语言", "年代", "流派", "特别推荐", "品牌", "艺术家",
			"上升最快", "热门兆赫", "试试这些" };// 根据不同类别选择频道
	public static final String[] F_R_DOUBAN_STOP_PLAY = {
			"F_R_DOUBAN_STOP_PLAY", "停止播放" };
	// public static final String[] F_R_DOUBAN_CHANNEL_CHOOSE = {
	// "F_R_DOUBAN_CHANNEL_CHOOSE", "语言", "年代", "流派", "特别推荐", "品牌", "艺术家",
	// "上升最快", "热门兆赫", "试试这些" };// 根据不同类别选择频道
	// public static final String[] F_R_DOUBAN_STOP_PLAY = {
	// "F_R_DOUBAN_STOP_PLAY", "停止播放" };
	// public static final String[] F_R_DOUBAN_START_PLAY = {
	// "F_R_DOUBAN_START_PLAY", "继续播放" };

	// sharedPreferences常量池
	public static final String FILE_PASSWORD = "password";// 密码管理
	public static final String KEY_WIFINAME = "wifiName";// 热点名
	public static final String KEY_WIFIPASSWORD = "wifiPassword";// 热点密码
	public static final String KEY_FTPNAME = "ftpName";
	public static final String KEY_FTPPASSWORD = "ftpPassword";
	public static final String FILE_NAVIPREFER = "naviprefer";// 导航偏好
	public static final String KEY_NAVIPREFER = "naviprefer";
	public static final String FILE_HUDWIFI = "hudwifi";// WIFI信息保存
	public static final String KEY_WIFIINFO = "wifiinfo";// WIFI信息保存
	public static final String FILE_APPCONTROLDEVICE = "AppControlDevice";
	public static final String KEY_DEVICEID = "deviceid";
	public static final String FILE_HS_INFO = "hs_info";// 热点状态
	public static final String KEY_HSINFO = "hsInfo";
	public static final String KEY_LAST_OPENED_CHANNEL = "last_opened_channel";// 最后次打开的频道
	public static final String KEY_LAST_OPENED_CHANNEL_NAME = "last_opened_channel_name";// 最后次打开的频道名
	public static final String KEY_LAST_CHLS_QUERY_TIME = "KEY_LAST_CHLS_QUERY_TIME";
	public static final String FILE_NAVI_CONTINUE = "navi_continue";
	public static final String KEY_CONTINUE_ZOOM = "continue_zoom";
	public static final String KEY_CONTINUE_NAVIPATH = "continue_navipath";
	public static final String FILE_BG_MUSIC_INFO = "bg_music_info";// 音乐播放状态
	public static final String FILE_SAVE_LOCATION_INFO = "SAVE_LOCATION_INFO";// 保存的收藏地点
	public static final String FILE_HUD_CONTIUE_NAVI = "HUD_CONTIUE_NAVI";
	public static final String KEY_CONTIUE_NAVI_PLAN = "contiue_navi_plan";
	public static final String KEY_CONTIUE_NAVI_ADDRESS = "contiue_navi_address";
	public static final String FILE_SP_IS_PHONE = "sp_is_phone";
	public static final String KEY_RING_STATE = "ring_state";
	public static final String FILE_STARTNAVIINFO = "StartNaviInfo";
	public static final String FILE_MSGCONGIGXML = "MsgCongigXml";
	public static final String FILE_VOICEMANAGER = "VoiceManager";// 音量管理
	public static final String KEY_MANAGER_VOICE_SPEAK = "currSpeakVoice";
	public static final String KEY_MANAGER_VOICE_MUSIC = "currMusicVoice";
	public static final String FILE_PLAN_ALL_SEE = "plan_all_see";
	public static final String KEY_NAVIPATH = "navipath";
	public static final String FILE_DATA = "Data";
	public static final String FILE_LOCATIONLIST = "locationlist";// 收藏的位置信息list
	public static final String KEY_LOCATIONLIST = "locationlist";
	public static final String KEY_CURRENTMUSIC = "currentMusic";
	public static final String KEY_MUSICPLAYING = "musicPlaying";
	public static final String KEY_MSGINFOS = "msgInfos";

}
