package com.infisight.hudprojector.service;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.infisight.hudprojector.common.HudProjectApplication;
import com.infisight.hudprojector.data.AppControlActionClass;
import com.infisight.hudprojector.data.DataMoreLevelClass;
import com.infisight.hudprojector.data.DataTextMessageClass;
import com.infisight.hudprojector.data.PMessage;
import com.infisight.hudprojector.data.PasswordInfo;
import com.infisight.hudprojector.data.WifiInformation;
import com.infisight.hudprojector.kdxfspeech.SpeechVoiceRecognition;
import com.infisight.hudprojector.util.CommonUtil;
import com.infisight.hudprojector.util.Constants;
import com.infisight.hudprojector.util.Utils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.widget.Toast;

/**
 * 消息接收中心，依据接收的消息发送到相应的界面，现阶段采用单向的输出模式，以后再考虑加入双向的Socket通讯模式
 * 
 * @author Administrator
 * 
 */
public class MsgServer implements Runnable {
	public static final String TAG = "MsgServer";
	public static final int SERVERPORT = 8988;
	Context mContext;
	ServerSocket serverSocket = null;
	Socket socket;
	static Vector usrList = new Vector(1, 1);

	public MsgServer(Context context) {
		mContext = context;
		try {
			serverSocket = new ServerSocket(SERVERPORT);
		} catch (IOException ioe) {
			System.out.println("Can't set up server socket." + ioe);
		}
		System.out.println("server start ...");
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			while (true) {
				Log.i(TAG, "正在等待连接.....");
				socket = serverSocket.accept();
				socket.setSoTimeout(4000);
				Log.i(TAG, "已连接.....");
				usrThread ut = new usrThread(socket, mContext);
				new Thread(ut).start();
				usrList.addElement(ut);
				Log.d(TAG, "createGroup:onSuccess");
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "等待连接出现异常");
		}
	}

}

class usrThread implements Runnable {
	Socket socket;
	Context mContext;
	ObjectInputStream osFromClient;
	ObjectOutputStream osToClient;
	// thPut tp;
	// thGet tg;
	// static int msgCount = 0;
	// static Vector msgBox = new Vector(1, 1);// 动态数组
	List<PMessage> msgLsts = new ArrayList<PMessage>();
	// int localCount = 0;
	SpeechVoiceRecognition svr = null;

	// 读取消息，发送给手机端
	public synchronized void writeMsg(PMessage msg) {
		// 在这里实现对消息的接收
		// msgBox.addElement(msg);
		msgLsts.add(msg);
		// msgCount++;
		Log.d("usrThread", "writeMsg(PMessage msg)");
	}

	// 写入消息，接收手机端信息
	public synchronized PMessage readMsg() {
		// PMessage msg = (PMessage) (msgBox.elementAt(localCount));// 放到这里面
		PMessage msg = msgLsts.get(0);
		// localCount++;
		return msg;
	}

	// 写入消息
	public synchronized void readMsg(PMessage pm) {
		Gson gson = new Gson();
		int cmd = pm.command;
		switch (cmd) {
		case Constants.TYPE_AUDIO:
			break;
		case Constants.TYPE_CMD:
			try {
				String content = pm.message;
				Log.d("readMsg", content);
				DataTextMessageClass dtmc = gson.fromJson(content,
						DataTextMessageClass.class);
				processBroadcast(dtmc.getCmd(), dtmc.getContent());
			} catch (Exception e) {
				Log.d("usrThread", "e.Message:" + e.getMessage());
			}
			break;
		case Constants.TYPE_IMG:
			break;
		case Constants.TYPE_STREAM:
			break;
		case Constants.TYPE_TEXT:
			try {
				String content = pm.message;
				Log.d("usrThread", content);
				DataTextMessageClass dtmc = gson.fromJson(content,
						DataTextMessageClass.class);
				processBroadcast(dtmc.getCmd(), dtmc.getContent());
				Log.d("usrThread", dtmc.getContent() + "-=" + dtmc.getCmd());

			} catch (Exception e) {
				Log.d("usrThread", "e.Message:" + e.getMessage());
			}
			break;
		case Constants.TYPE_RETURNIFO:
			try {
				String content = pm.message;
				DataTextMessageClass dtmc = gson.fromJson(content,
						DataTextMessageClass.class);
				if (dtmc.getCmd().equals(Constants.RETURNWIFIDIRECTSTATE)) {// 判断wifi-direct是否连接上
					// Log.d("MsgServer", "e.Message:------里面");
					// PMessage pm2 = new PMessage();
					// pm2.message = Constants.WIFIDIRECTSTATE;
					// writeMsg(pm2);
					processBroadcast(dtmc.getCmd(), null);
				}
			} catch (Exception e) {
				Log.d("usrThread", "e.Message:" + e.getMessage());
			}
			break;
		}

	}

	public usrThread(final Socket s, Context mcontext) {
		socket = s;
		mContext = mcontext;
		svr = SpeechVoiceRecognition.getInstance(mContext);
		try {
			Log.d("MsgServer", "mNewMsgReceiver   usrThread");
			osToClient = new ObjectOutputStream(socket.getOutputStream());
			osFromClient = new ObjectInputStream(new BufferedInputStream(
					socket.getInputStream()));
			mContext.registerReceiver(mMobileMsgReceiver,
					makeNewMsgIntentFilter());
			// 读取客户端消息
			new Thread(new Runnable() {
				PMessage msg;
				Gson gson = new Gson();

				public void run() {
					while (true) {
						try {
							// Object obj = osFromClient.readObject();
							// msg = (PMessage) obj;
							String str = (String) osFromClient.readObject();

							// 表明是车主
							if (str != null) {
								// 说明第一次有车主连接
								if (HudProjectApplication.carSocket == null) {
									HudProjectApplication.carSocket = s;
								}
								// 说明之前已有车主
								else {
									// 此client不是之前的车主，则替换，并发送消息到手机端，清空车主信息
									if (!HudProjectApplication.carSocket
											.equals(s)) {
										HudProjectApplication.carBeforeSocket = HudProjectApplication.carSocket;// 前任车主
										HudProjectApplication.carSocket = s;// 当前车主
										Log.d("MsgServer", s.toString());
									}
								}
								msg = gson.fromJson(str, PMessage.class);
								readMsg(msg);
							}

						} catch (Exception e) {
							e.printStackTrace();
							// Log.i("MsgServer", "出现异常了");
						}
					}
				}
			}).start();
			// 发送HUD消息到客户端
			new Thread(new Runnable() {
				// PMessage msg;
				Gson gson = new Gson();

				public void run() {
					while (true) {

						// if (localCount < msgCount) {
						// msg = readMsg();
						// Log.d("TAG",
						// msgLsts.toString()+msgLsts.size()+"111111");

						// 表示前任车主
						if (HudProjectApplication.carBeforeSocket != null
								&& s.equals(HudProjectApplication.carBeforeSocket)) {
							DataMoreLevelClass dmlc = new DataMoreLevelClass();

							dmlc.setCommand(Constants.MODEL_CLEAR_CAR_INFO);// 清楚车主信息的标示
							dmlc.setContent(null);

							String carStr = gson.toJson(dmlc);
							PMessage pm = new PMessage();
							pm.command = Constants.MODEL_COMMON_COM;// 在手机端进行识别,公共标示
							pm.message = carStr;
							pm.fromname = "a";
							pm.toname = "b";
							try {
								osToClient.writeObject(gson.toJson(pm));
								osToClient.flush();
								osToClient.reset();
							} catch (SocketTimeoutException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								Log.d("MsgServer", "尚未和手机端进行连接");
								Toast.makeText(mContext, "尚未和手机端进行连接", 0)
										.show();
							} catch (Exception e) {
								// TODO: handle exception
								Log.d("MsgServer", "Exception222222");
								Toast.makeText(mContext, "尚未和手机端进行连接", 0)
										.show();
							}
							HudProjectApplication.carBeforeSocket = null;
						}
						// 表明是车主
						else if (msgLsts.size() != 0) {
							String str = gson.toJson(readMsg());
							Log.d("MsgServer", str + "--发送hud消息");
							try {
								osToClient.writeObject(str);
								osToClient.flush();
								osToClient.reset();
							} catch (SocketTimeoutException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								Log.d("MsgServer", "尚未和手机端进行连接");
								Toast.makeText(mContext, "尚未和手机端进行连接", 0)
										.show();
							} catch (Exception e) {
								// TODO: handle exception
								Log.d("MsgServer", "Exception222222");
								Toast.makeText(mContext, "尚未和手机端进行连接", 0)
										.show();
							}
							msgLsts.clear();
						}
						// }

					}
				}
			}).start();
			// tp = new thPut();
			// tp.start();
			// tg = new thGet();
			// tg.start();
		} catch (Exception e) {
			System.out.println("usrThread init error! " + e);
		}
	}

	// // 读取客户端消息
	// class thPut extends Thread {
	// PMessage msg;
	//
	// public void run() {
	// try {
	// while (true) {
	// try {
	// Object obj = osFromClient.readObject();
	// msg = (PMessage) obj;
	// readMsg(msg);
	// } catch (Exception e) {
	// }
	// }
	// } catch (Exception e) {
	// System.out.println("Receive error" + e.getMessage());
	// }
	// }
	// }
	//
	// // 发送HUD消息到客户端
	// class thGet extends Thread {
	// // Declare the currnet message get from readMsg method
	// PMessage msg = null;
	//
	// public void run() {
	// try {
	// while (true) {
	//
	// if (localCount < msgCount) {
	// msg = readMsg();
	// osToClient.writeObject(msg);
	// osToClient.flush();
	// osToClient.reset();
	// localCount++;
	// }
	//
	// }
	//
	// } catch (Exception e) {
	// System.out.println("cant write msg to client" + e);
	//
	// }
	// }
	//
	// }

	/**
	 * 新消息过滤器
	 * 
	 * @return
	 */
	private IntentFilter makeNewMsgIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Constants.MSG_SEND);
		intentFilter.addAction(Constants.AppControlPasswordAction);
		intentFilter.addAction(Constants.AppControlNaviPreferAction);
		intentFilter.addAction(Constants.AppControlWifiPasswordAction);
		intentFilter.addAction(Constants.AppControlNeedWifiAction);
		return intentFilter;
	}

	/**
	 * 广播接收器,这个应该是SendMsg
	 */
	public BroadcastReceiver mMobileMsgReceiver = new BroadcastReceiver() {
		SharedPreferences sharedPreferences;

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			Gson gson = new Gson();
			Log.d("MsgServer", "已经接收到手机端的消息。。。。。。。。。");
			try {
				final String action = intent.getAction();
				if (action != null && Constants.MSG_SEND.equals(action)) {
					String strnmsg = intent.getStringExtra("NMESSAGE");
					Log.d("MsgServer", "strnmsg--------------" + strnmsg);
					if (!strnmsg.equals("")) {
						PMessage nmsg = gson.fromJson(strnmsg, PMessage.class);
						writeMsg(nmsg);
						Log.d("MsgServer", "数据已经发送到Mobile");
					}

				} else if (action != null
						&& Constants.AppControlPasswordAction.equals(action)) {

					String strnmsg = intent.getStringExtra(Constants.MSG_PARAM);
					Log.i("Test", strnmsg);
					if (strnmsg != null) {
						PasswordInfo passwordInfo = gson.fromJson(strnmsg,
								PasswordInfo.class);
						sharedPreferences = context.getSharedPreferences(
								Constants.FILE_PASSWORD, Activity.MODE_PRIVATE);
						Editor editor = sharedPreferences.edit();
						editor.putString(Constants.KEY_WIFINAME,
								passwordInfo.getWifiName());
						editor.putString(Constants.KEY_WIFIPASSWORD,
								passwordInfo.getWifiPassword());
						editor.putString(Constants.KEY_FTPNAME,
								passwordInfo.getFtpName());
						editor.putString(Constants.KEY_FTPPASSWORD,
								passwordInfo.getFtpPassword());
						editor.commit();
					}
				}
				// 主从设备导航偏好传递
				else if (action != null
						&& Constants.AppControlNaviPreferAction.equals(action)) {
					Log.i("MsgServer", "AppControlNaviPreferAction");
					String str = intent.getStringExtra(Constants.MSG_PARAM);
					Log.i("MsgServer", str);
					if (str != null) {
						sharedPreferences = context.getSharedPreferences(
								Constants.FILE_NAVIPREFER,
								Activity.MODE_PRIVATE);
						Editor editor = sharedPreferences.edit();
						editor.putString(Constants.KEY_NAVIPREFER, str);
						editor.commit();
					}
				}
				// 主从设备wifi连接信息传递
				else if (action != null
						&& Constants.AppControlWifiPasswordAction
								.equals(action)) {
					Log.i("MsgServer", "AppControlWifiPasswordAction");
					String str = intent.getStringExtra(Constants.MSG_PARAM);
					Log.i("MsgServer", str);
					WifiInformation wifiinfo = null;
					String wifiinfoMapStr;
					HashMap<String, String> wifiinfoMap = new HashMap<String, String>();
					if (null != str && "" != str) {
						wifiinfo = gson.fromJson(str, WifiInformation.class);
					}
					try {
						wifiinfoMapStr = (String) Utils.get(mContext,
								Constants.FILE_HUDWIFI, Constants.KEY_WIFIINFO,
								"");
					} catch (Exception e) {
						wifiinfoMapStr = "";
					}
					if (wifiinfoMapStr != null && wifiinfoMapStr != "") {
						wifiinfoMap = gson.fromJson(wifiinfoMapStr,
								new TypeToken<HashMap<String, String>>() {
								}.getType());
					}
					wifiinfoMap.put(wifiinfo.getWifiName(),
							wifiinfo.getWifiPassword());
					Utils.put(mContext, Constants.FILE_HUDWIFI,
							Constants.KEY_WIFIINFO, gson.toJson(wifiinfoMap));
				}
				// hud需求wifi信息回传信号
				else if (action != null
						&& Constants.AppControlNeedWifiAction.equals(action)) {
					Log.i("MsgServer", "AppControlNeedWifiAction");
					String str = intent.getStringExtra(Constants.MSG_PARAM);
					Log.i("MsgServer", str);
					if (str.equals("giveMeWifi")) {
						Log.i("MsgServer", str);
						// 考虑到有可能有两个不同的手机，数据回发给手机端
						String hudwifiInfoMapStr = (String) Utils.get(mContext,
								Constants.FILE_HUDWIFI, Constants.KEY_WIFIINFO,
								"");
						// 发送的是一个已转成字符串的hashMap
						if (hudwifiInfoMapStr != null
								&& hudwifiInfoMapStr != "") {
							CommonUtil.sendMsg(
									Constants.MODEL_WIFIINFO_BACK_COM,
									hudwifiInfoMapStr, mContext,
									Constants.MODEL_WIFIINFO_COM);
						}

					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	/**
	 * 将新消息广播出去
	 */
	private void processBroadcast(String newCmd, String newMsg) {
		String stringIntentAction = newCmd;
		Intent intentAction = new Intent(stringIntentAction);
		intentAction.putExtra(Constants.MSG_CMD, newCmd);
		intentAction.putExtra(Constants.MSG_PARAM, newMsg);
		mContext.sendBroadcast(intentAction);
	}

	@Override
	public void run() {

	}
}
