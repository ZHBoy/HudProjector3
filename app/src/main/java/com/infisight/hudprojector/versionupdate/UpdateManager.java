package com.infisight.hudprojector.versionupdate;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * 下载更新文件等实现
 * 
 * @author Administrator
 * 
 */
public class UpdateManager {
	private String curVersion;
	private String newVersion;
	private int curVersionCode;
	private int newVersionCode;
	private String updateInfo;
	private UpdateCallback callback;
	private Context ctx;

	private int progress;
	private Boolean hasNewVersion;
	private Boolean canceled;

	// 存放更新APK文件的路径
	public static final String UPDATE_DOWNURL = "http://10.0.0.150:8110/HudProjector.apk";
	// 存放更新APK文件相应的版本说明路径
	public static final String UPDATE_CHECKURL = "http://10.0.0.150:8110/update.txt";
	public static final String UPDATE_APKNAME = "update_test.apk";
	// public static final String UPDATE_VERJSON = "ver.txt";
	public static final String UPDATE_SAVENAME = "updateapk.apk";
	private static final int UPDATE_CHECKCOMPLETED = 1;
	private static final int UPDATE_DOWNLOADING = 2;
	private static final int UPDATE_DOWNLOAD_ERROR = 3;
	private static final int UPDATE_DOWNLOAD_COMPLETED = 4;
	private static final int UPDATE_DOWNLOAD_CANCELED = 5;

	// 从服务器上下载apk存放文件夹
	private String savefolder = Environment.getExternalStorageDirectory()
			.getPath();;

	public UpdateManager(Context context, UpdateCallback updateCallback) {
		ctx = context;
		callback = updateCallback;
		// savefolder = context.getFilesDir();
		canceled = false;
		curVersion = UpdateUtils.getVerName(ctx);
		curVersionCode = UpdateUtils.getVerCode(ctx);
	}

	public String getNewVersionName() {
		return newVersion;
	}

	public String getUpdateInfo() {
		return updateInfo;
	}

	/**
	 * 检查是否有更新
	 */
	public void checkUpdate() {
		hasNewVersion = false;
		new Thread() {
			/**
			 * @by
			 */
			@Override
			public void run() {
				Log.i("@@@@@", ">>>>>>>>>>>>>>>>>>>>>>>>>>>getServerVerCode() ");
				try {

					String verjson = NetHelper.httpStringGet(UPDATE_CHECKURL);
					Log.i("@@@@",
							verjson
									+ "**************************************************");
					JSONArray array = new JSONArray(verjson);

					if (array.length() > 0) {
						JSONObject obj = array.getJSONObject(0);
						try {
							newVersionCode = Integer.parseInt(obj
									.getString("verCode"));
							newVersion = obj.getString("verName");
							updateInfo = "";
							Log.i("newVerCode", newVersionCode + "");
							Log.i("newVerName", newVersion);
							if (newVersionCode > curVersionCode) {
								hasNewVersion = true;
							}
						} catch (Exception e) {
							newVersionCode = -1;
							newVersion = "";
							updateInfo = "";

						}
					}
				} catch (Exception e) {
					Log.e("update", e.getMessage());
				}
				updateHandler.sendEmptyMessage(UPDATE_CHECKCOMPLETED);
			};
		}.start();
	}

	/**
	 * 安装APK
	 */
	public void update() {
		// TODO 需要静默安装
//		boolean slientInstall = slientInstall(new File(savefolder, UPDATE_SAVENAME));
//		Log.i("UpdateManager", slientInstall+"");
		String install = install(savefolder+UPDATE_APKNAME);
		Log.i("update", install);
//		Intent intent = new Intent(Intent.ACTION_VIEW);
//		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		intent.setDataAndType(
//				Uri.fromFile(new File(savefolder, UPDATE_SAVENAME)),
//				"application/vnd.android.package-archive");
//		ctx.startActivity(intent);
	}

	public void cancelDownload() {
		canceled = true;
	}

	public void downloadPackage() {
		new Thread() {
			@Override
			public void run() {
				try {
					URL url = new URL(UPDATE_DOWNURL);

					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					conn.connect();
					int length = conn.getContentLength();
					InputStream is = conn.getInputStream();

					File ApkFile = new File(savefolder, UPDATE_SAVENAME);

					if (ApkFile.exists()) {

						ApkFile.delete();
					}

					FileOutputStream fos = new FileOutputStream(ApkFile);

					int count = 0;
					byte buf[] = new byte[512];

					do {

						int numread = is.read(buf);
						count += numread;
						progress = (int) (((float) count / length) * 100);

						updateHandler.sendMessage(updateHandler
								.obtainMessage(UPDATE_DOWNLOADING));
						if (numread <= 0) {

							updateHandler
									.sendEmptyMessage(UPDATE_DOWNLOAD_COMPLETED);
							break;
						}
						fos.write(buf, 0, numread);
					} while (!canceled);
					if (canceled) {
						updateHandler
								.sendEmptyMessage(UPDATE_DOWNLOAD_CANCELED);
					}
					fos.close();
					is.close();
				} catch (MalformedURLException e) {
					e.printStackTrace();
					updateHandler.sendMessage(updateHandler.obtainMessage(
							UPDATE_DOWNLOAD_ERROR, e.getMessage()));
				} catch (IOException e) {
					e.printStackTrace();
					Log.e("Exception", "IOException");
					Log.e("Exception", e.getMessage());
					updateHandler.sendMessage(updateHandler.obtainMessage(
							UPDATE_DOWNLOAD_ERROR, e.getMessage()));
				}

			}
		}.start();
	}

	Handler updateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case UPDATE_CHECKCOMPLETED:
				callback.checkUpdateCompleted(hasNewVersion, newVersion);
				break;
			case UPDATE_DOWNLOADING:
				callback.downloadProgressChanged(progress);
				break;
			case UPDATE_DOWNLOAD_ERROR:
				callback.downloadCompleted(false, msg.obj.toString());
				break;
			case UPDATE_DOWNLOAD_COMPLETED:
				callback.downloadCompleted(true, "");
				break;
			case UPDATE_DOWNLOAD_CANCELED:
				callback.downloadCanceled();
			default:
				break;
			}
		}
	};

//	/**
//	 * 静默安装
//	 * 
//	 * @param apkAbsolutePath
//	 *            路径
//	 * @return
//	 */
//	public String install(String apkAbsolutePath) {
//		String[] args = { "pm", "install", "-r", apkAbsolutePath };
//		String result = "";
//		ProcessBuilder processBuilder = new ProcessBuilder(args);
//		Process process = null;
//		InputStream errIs = null;
//		InputStream inIs = null;
//		try {
//			ByteArrayOutputStream baos = new ByteArrayOutputStream();
//			int read = -1;
//			process = processBuilder.start();
//			errIs = process.getErrorStream();
//			while ((read = errIs.read()) != -1) {
//				baos.write(read);
//			}
//			baos.write("/n".getBytes());
//			inIs = process.getInputStream();
//			while ((read = inIs.read()) != -1) {
//				baos.write(read);
//			}
//			byte[] data = baos.toByteArray();
//			result = new String(data);
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			try {
//				if (errIs != null) {
//					errIs.close();
//				}
//				if (inIs != null) {
//					inIs.close();
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			if (process != null) {
//				process.destroy();
//			}
//		}
//		return result;
//
//	}

	/**
	 * 
	 * 静默安装
	 * 
	 * @param file
	 * 
	 * @return
	 */
	public boolean slientInstall(File file) {
		Log.i("UpdateManager", "install");
		boolean result = false;
		Process process = null;
		OutputStream out = null;
		try {
			process = Runtime.getRuntime().exec("su");
			out = process.getOutputStream();
			DataOutputStream dataOutputStream = new DataOutputStream(out);
			dataOutputStream.writeBytes("chmod 777 " + file.getPath() + "\n");
			dataOutputStream
					.writeBytes("LD_LIBRARY_PATH=/vendor/lib:/system/lib pm install -r "
							+
							file.getPath());
			// 提交命令
			dataOutputStream.flush();
			// 关闭流操作
			dataOutputStream.close();
			out.close();
			int value = process.waitFor();
			// 代表成功
			Log.i("UpdateManager", value+"");
			if (value == 0) {
				result = true;
			} else if (value == 1) { // 失败
				result = false;
			} else { // 未知情况
				result = false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			Log.e("UpdateManager", "IOException");
			Log.e("UpdateManager", e.getMessage());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
	public String install(String apkAbsolutePath){
		String[] args = { "pm", "install", "-r", apkAbsolutePath };  
		String result = "";  
		ProcessBuilder processBuilder = new ProcessBuilder(args);  
		Process process = null;  
		InputStream errIs = null;  
		InputStream inIs = null;  
		try {  
		    ByteArrayOutputStream baos = new ByteArrayOutputStream();  
		    int read = -1;  
		    process = processBuilder.start();  
		    errIs = process.getErrorStream();  
		    while ((read = errIs.read()) != -1) {  
		        baos.write(read);  
		    }  
		    baos.write("/n".getBytes());  
		    inIs = process.getInputStream();  
		    while ((read = inIs.read()) != -1) {  
		        baos.write(read);  
		    }  
		    byte[] data = baos.toByteArray();  
		    result = new String(data);  
		} catch (IOException e) {  
		    e.printStackTrace();  
		} catch (Exception e) {  
		    e.printStackTrace();  
		} finally {  
		    try {  
		        if (errIs != null) {  
		            errIs.close();  
		        }  
		        if (inIs != null) {  
		            inIs.close();  
		        }  
		    } catch (IOException e) {  
		        e.printStackTrace();  
		    }  
		    if (process != null) {  
		        process.destroy();  
		    }  
		}  
		return result;  
	}

}
