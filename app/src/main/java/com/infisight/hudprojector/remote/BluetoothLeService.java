package com.infisight.hudprojector.remote;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.infisight.hudprojector.MainActivity;
import com.infisight.hudprojector.data.BleConnectDevice;
import com.infisight.hudprojector.util.Constants;
import com.infisight.hudprojector.util.SaveUtils;

@SuppressLint("NewApi")
public class BluetoothLeService extends Service {
	private String TAG = "BluetoothLeService";
	private BluetoothManager mBluetoothManager;
	private BluetoothAdapter mBluetoothAdapter;
	// private String mBluetoothDeviceAddress;
	// private BluetoothGatt mBluetoothGatt;
	private HashMap<String, BluetoothGatt> mGattMap = new HashMap<String, BluetoothGatt>();// 根据address存放Gatt的集合
	private ArrayList<String> addressList;// 存放已连接过的地址
	private int mConnectionState = STATE_DISCONNECTED;

	// 当前状态标志
	private static final int STATE_DISCONNECTED = 0;
	private static final int STATE_CONNECTING = 1;
	private static final int STATE_CONNECTED = 2;

	public final static String ACTION_GATT_CONNECTED = "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
	public final static String ACTION_GATT_DISCONNECTED = "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
	public final static String ACTION_GATT_SERVICES_DISCOVERED = "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
	public final static String ACTION_DATA_AVAILABLE = "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
	public final static String EXTRA_DATA = "com.example.bluetooth.le.EXTRA_DATA";
	private static final String SP_NAME = "address";

	private final IBinder mBinder = new LocalBinder();
	boolean mScanning = false;
	SharedPreferences prefs = null;
	private BluetoothGattCharacteristic mNotifyCharacteristic;
	private BluetoothGattCharacteristic mWriteCharacteristic;
	boolean isNotificationSetted = false; // 是否已经设置了 通知模式
	// 判断是否已经有连接过
	HashMap<String, Boolean> hmIsConnected = new HashMap<String, Boolean>(); // 判断是否已经连接
	// 判断是否连接
	List<BleConnectDevice> lstNeedConnectDevices = new ArrayList<BleConnectDevice>();
	// 判断第一次启动服务的时候，是否需要扫描
	List<String> lstNeedConnectAddress = new ArrayList<String>();
	boolean isFirstRun = true;
	// 开启一个线程，不断地检测连接上次连接的那个设备
	Handler handler = new Handler();

	public class LocalBinder extends Binder {
		BluetoothLeService getService() {
			return BluetoothLeService.this;
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefs.edit().putBoolean(Constants.REMOTE_CONNECT_STATUS, false)
				.commit();
		addressList = SaveUtils.loadArray(this, SP_NAME);
		GetDeviceInfo();
		registerReceiver(mRemoteDataUpdateReceiver,
				makeRemoteDataUpdateIntentFilter());
	}

	public boolean initialize() {
		// 获取蓝牙管理
		if (mBluetoothManager == null) {
			mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
			if (mBluetoothManager == null) {
				return false;
			}
		}
		// 获取适配器
		mBluetoothAdapter = mBluetoothManager.getAdapter();
		if (mBluetoothAdapter == null) {
			return false;
		}
		return true;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "onStartCommand:flags=" + flags + ",startId=" + startId);
		if (mBluetoothManager == null) {
			if (!initialize()) {
				// Log.i(TAG, "Unable to initialize Bluetooth");
			}
		}
		if (isFirstRun) {
			try {
				handler.postDelayed(runnable, 2000);
			} catch (Exception e) {
			}
		} else {
			String address = getSharedPreferences(Constants.REMOTE_DEVICE_INFO,
					0).getString(Constants.REMOTE_CURR_ADDR, "0");
			// if (mBluetoothGatt == null || !mBluetoothGatt.connect()) {
			// if (mGattMap.keySet().contains(address)) {
			//
			// } else {
			connect(address);
			// }
			// }
		}
		isFirstRun = false;
		return START_STICKY;
	}

	public boolean connect(final String address) {
		// 确认已经获取到适配器并且地址不为空
		if (mBluetoothAdapter == null || address == null || address.equals("0")) {
			// Log.i(TAG,
			// "BluetoothAdapter not initialized or unspecified address.");
			return false;
		}

		final BluetoothDevice device = mBluetoothAdapter
				.getRemoteDevice(address);
		// 通过设备地址获取设备
		if (device == null) {
			Log.e(TAG, "Device not found.  Unable to connect.");
			return false;
		}
		// Log.i(TAG, "device.connectGatt(this, false, mGattCallback)");
		if (mGattMap.keySet().contains(address)) {
			BluetoothGatt mBluetoothGatt = mGattMap.get(address);
			if (mBluetoothGatt != null) {
				// close(mBluetoothGatt);
				disconnect(mBluetoothGatt);
			}
		}
		BluetoothGatt mBluetoothGatt = device.connectGatt(this, false,
				mGattCallback);
		mGattMap.put(address, mBluetoothGatt);
		addressList.add(address);
		mConnectionState = STATE_CONNECTING;

		return true;
	}

	public void disconnect(BluetoothGatt mBluetoothGatt) {
		// Log.i("LIFECYCLE", "disconnect");
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.e(TAG, "BluetoothAdapter not initialized disconnect");
			return;
		}
		mBluetoothGatt.disconnect();
		// 断开连接
	}

	private void broadcastUpdate(final String action) {
		final Intent intent = new Intent(action);
		sendBroadcast(intent);
	}

	// 从特征中分析返回的数据并表示为action后进行广播
	private void broadcastUpdate(final String action, BluetoothGatt gatt,
			final BluetoothGattCharacteristic characteristic) {
		// final Intent intent = new Intent(action);
		final byte[] data = characteristic.getValue();
		// Log.i("broadcastUpdate", characteristic.getValue() + "");
		if (data != null && data.length > 0) {
			final StringBuilder stringBuilder = new StringBuilder(data.length);
			for (byte byteChar : data)
				stringBuilder.append(String.format("%02X ", byteChar));
			Log.d(TAG,
					"addr:" + gatt.getDevice().getAddress() + ",name:"
							+ gatt.getDevice().getName() + ",data:"
							+ stringBuilder.toString());
			if (stringBuilder.toString().trim().equals("01")) {
				// 上
				ProcessBroadcast(Constants.GESTURE_INFO, Constants.GESTURE_TOUP);
				Log.i(TAG, "keys01");

			} else if (stringBuilder.toString().trim().equals("02")) {
				// 确定
				ProcessBroadcast(Constants.GESTURE_INFO,
						Constants.GESTURE_SINGLETAP);
				Log.i(TAG, "keys02");
			} else if (stringBuilder.toString().trim().equals("04")) {
				// 唤醒
				MainActivity.svr.controlToWake(true);
				Log.i(TAG, "keys04");
			} else if (stringBuilder.toString().trim().equals("08")) {
				// 返回
				ProcessBroadcast(Constants.GESTURE_INFO,
						Constants.GETSTURE_BACK);
				Log.i(TAG, "keys08");
			} else if (stringBuilder.toString().trim().equals("10")) {
				// 下
				ProcessBroadcast(Constants.GESTURE_INFO,
						Constants.GESTURE_TODOWN);
				Log.i(TAG, "keys10");

			}
			// intent.putExtra(EXTRA_DATA, new String(data) + "\n" +
			// stringBuilder.toString());
		}
	}

	private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status,
				int newState) {
			// 连接状态发生变更
			String intentAction;
			Log.i(TAG, "BluetoothProfile.STATE_CONNECTED:"
					+ BluetoothProfile.STATE_CONNECTED
					+ ",BluetoothProfile.STATE_DISCONNECTED:"
					+ BluetoothProfile.STATE_DISCONNECTED + ",newState:"
					+ newState);
			if (newState == BluetoothProfile.STATE_CONNECTED) {
				// 取消重新连接过程
				// SharedPreferences spOBDDevice =
				// BluetoothLeService.this.getSharedPreferences(Constants.REMOTEDEVICEINFO,0);
				// spOBDDevice.edit().putString(Constants.REMOTECURRADDR,gatt.getDevice().getAddress())
				// .putString(Constants.REMOTECURNAME,gatt.getDevice().getName())
				// .commit();
				hmIsConnected.put(gatt.getDevice().getAddress(), true);
				// try{
				// handler.removeCallbacks(runnable);
				// }catch(Exception e)
				// {}
				SaveUtils.saveArray(BluetoothLeService.this, SP_NAME,
						addressList);
				intentAction = ACTION_GATT_CONNECTED;
				mConnectionState = STATE_CONNECTED;
				prefs.edit().putBoolean(Constants.REMOTE_CONNECT_STATUS, true)
						.commit();
				broadcastUpdate(intentAction);
				// Attempts to discover services after successful connection.
				Log.i(TAG,
						"Attempting to start service discovery:"
								+ gatt.discoverServices());
				SaveDeviceInfo(gatt.getDevice());

			} else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
				// 开启线程，不断时的试图连接设备
				hmIsConnected.put(gatt.getDevice().getAddress(), false);
				// try{
				// handler.postDelayed(runnable, 2000);
				// }catch(Exception e )
				// {}
				intentAction = ACTION_GATT_DISCONNECTED;
				mConnectionState = STATE_DISCONNECTED;
				prefs.edit().putBoolean(Constants.REMOTE_CONNECT_STATUS, false)
						.commit();
				close(gatt);
				// Log.i(TAG, "Disconnected from GATT server.");
				// getSharedPreferences(Constants.REMOTEDEVICEINFO, 0).edit()
				// .clear().commit();// 清除连接信息
				broadcastUpdate(intentAction);
//				connect(gatt.getDevice().getAddress());
				// close();
				// stopSelf();// 终止自己
			}
		}

		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			// 发现服务的广播
			if (status == BluetoothGatt.GATT_SUCCESS) {
				// Log.i(TAG,
				// "onServicesDiscovered received: displayGattServices");
				displayGattServices(getSupportedGattServices(gatt), gatt);
				broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
			} else {
				Log.i(TAG, "onServicesDiscovered received: " + status);
			}
		}

		@Override
		public void onCharacteristicRead(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			Log.i(TAG, "onCharacteristicRead");
			if (status == BluetoothGatt.GATT_SUCCESS) {
				broadcastUpdate(ACTION_DATA_AVAILABLE, gatt, characteristic);
			}
		}

		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic) {
			Log.i(TAG, "onCharacteristicChanged");
			// Log.i(TAG, gatt.getDevice().getAddress());
			broadcastUpdate(ACTION_DATA_AVAILABLE, gatt, characteristic);
		}

		@Override
		public void onCharacteristicWrite(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {

		}
	};

	// //////////////////////////////////////////////////////////////////
	// public void readCharacteristic(BluetoothGattCharacteristic
	// characteristic) {
	// if (mBluetoothAdapter == null || mBluetoothGatt == null) {
	// Log.w(TAG, "readCharacteristic BluetoothAdapter not initialized");
	// return;
	// }
	// mBluetoothGatt.readCharacteristic(characteristic);
	// }

	// public void writeCharacteristic(BluetoothGattCharacteristic
	// characteristic) {
	// if (mBluetoothAdapter == null || mBluetoothGatt == null) {
	// Log.w(TAG, "writeCharacteristic BluetoothAdapter not initialized");
	// return;
	// }
	// mBluetoothGatt.writeCharacteristic(characteristic);
	// }

	public void setCharacteristicNotification(
			BluetoothGattCharacteristic characteristic, boolean enabled,
			BluetoothGatt mBluetoothGatt) {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG,
					"setCharacteristicNotification BluetoothAdapter not initialized");
			return;
		}
		String uuid = characteristic.getUuid().toString();
		if (uuid.contains("fff1"))// 这个是针对遥控器设备
		{
			mBluetoothGatt.setCharacteristicNotification(characteristic,
					enabled);
			// Measurement.这个需要替换为Notification的的Characteristic
			BluetoothGattDescriptor descriptor = characteristic
					.getDescriptor(UUID
							.fromString(Constants.CLIENT_CHARACTERISTIC_CONFIG));
			descriptor
					.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
			mBluetoothGatt.writeDescriptor(descriptor);
		}
		// 接下来是对OBD设备的内容
	}

	// 获取支持的Service列表
	public List<BluetoothGattService> getSupportedGattServices(
			BluetoothGatt mBluetoothGatt) {
		if (mBluetoothGatt == null) {
			Log.e("getSupportedGattServices", "gatt is null");
			return null;
		}
		return mBluetoothGatt.getServices();
	}

	// 向遥控器发送数据
	private final String LIST_NAME = "NAME";
	private final String LIST_UUID = "UUID";
	private static HashMap<String, String> attributes = new HashMap(); // 遥控器的字段属性
	static {
		attributes.put("0000fff2-0000-1000-8000-00805f9b34fb",
				"Device OBDCYX Service");
		attributes.put("0000fff1-0000-1000-8000-00805f9b34fb",
				"Device OBDCYX String");
	}

	public static String lookup(String uuid, String defaultName) {
		String name = attributes.get(uuid);
		return name == null ? defaultName : name;
	}

	/**
	 * 设置相关的属性 在这个里面兼容OBD 和 遥控器的蓝牙设备连接功能，根据不同的设备，对设备返回的数据进行处理。
	 * 
	 * @param gattServices
	 */
	private void displayGattServices(List<BluetoothGattService> gattServices,
			BluetoothGatt gatt) {
		if (gattServices == null)
			return;
		String uuid = null;
		String unknownServiceString = "未知服务";
		String unknownCharaString = "未知特征";
		// Loops through available GATT Services.
		for (BluetoothGattService gattService : gattServices) {
			HashMap<String, String> currentServiceData = new HashMap<String, String>();
			uuid = gattService.getUuid().toString();
			currentServiceData.put(LIST_NAME,
					lookup(uuid, unknownServiceString));
			currentServiceData.put(LIST_UUID, uuid);
			List<BluetoothGattCharacteristic> gattCharacteristics = gattService
					.getCharacteristics();
			Log.d(TAG, "gattService.uuid:" + gattService.getUuid().toString());
			// Loops through available Characteristics.
			for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
				HashMap<String, String> currentCharaData = new HashMap<String, String>();
				uuid = gattCharacteristic.getUuid().toString();
				currentCharaData.put(LIST_NAME,
						lookup(uuid, unknownCharaString));
				currentCharaData.put(LIST_UUID, uuid);
				if (uuid.contains("fff1"))// 得确定那个是回掉等待的
				{
					Log.d(TAG, "BluetoothGattCharacteristic.uuid:"
							+ gattCharacteristic.getUuid().toString());
					BluetoothGattCharacteristic mTempNotifyCharacteristic = gattCharacteristic;
					setCharacteristicNotification(mTempNotifyCharacteristic,
							true, gatt);
				}
			}
		}

	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}

	public void close(BluetoothGatt mBluetoothGatt) {
		if (mBluetoothGatt == null) {
			return;
		}
		mBluetoothGatt.close();
		mBluetoothGatt = null;
	}

	@Override
	public void onDestroy() {
		getSharedPreferences(Constants.REMOTE_DEVICE_INFO, 0).edit().clear()
				.commit();// 清除连接信息
		unregisterReceiver(mRemoteDataUpdateReceiver);
		super.onDestroy();
	}

	/**
	 * 从外部发送过来的指令或者数据
	 */
	private final BroadcastReceiver mRemoteDataUpdateReceiver = new BroadcastReceiver() {// 需要将其他的内容加这个广播操作里面。
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (Constants.REMOTE_ACTION_DISCONNECT_GATT.equals(action))// 电话来时操作
			{
				// disconnect();
			} else if (Constants.REMOTE_ACTION_SENDDATA.equals(action))// 电话来时操作
			{
				String command = intent.getStringExtra(Constants.REMOTE_DATA);// 发送数据
				// if(!command.equals(""))
				// sendString(command+"\r");
			}
			if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
				BluetoothManager bluetoothManager = (BluetoothManager) BluetoothLeService.this
						.getSystemService(Context.BLUETOOTH_SERVICE);
				mBluetoothAdapter = bluetoothManager.getAdapter();
				if (!mBluetoothAdapter.isEnabled()) {
					Log.i(TAG, "结束蓝牙服务");
					onDestroy();
				}
			}
		}
	};

	/**
	 * 从外部发过来的命令 过滤
	 * 
	 * @return
	 */
	private static IntentFilter makeRemoteDataUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Constants.REMOTE_ACTION_DISCONNECT_GATT);
		intentFilter.addAction(Constants.REMOTE_ACTION_SENDDATA);
		intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		return intentFilter;
	}

	/**
	 * 启动连接线程
	 */
	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			// 在这里启动连接，如果连接失败，就再重新试图连接
			// Log.i(TAG, "try connect runnable");
			try {
				mBluetoothAdapter.stopLeScan(mLeScanCallback);
				mBluetoothAdapter.startLeScan(mLeScanCallback);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e(TAG,
						"第493行报错了mBluetoothAdapter.stopLeScan(mLeScanCallback);");
			}
			// 获取保存的设备数据，然后进行判断是否需要连接
			// String address =
			// getSharedPreferences(Constants.REMOTEDEVICEINFO,0).getString(Constants.REMOTECURRADDR,
			// "");
			// if(!address.equals(""))
			// connect(address);
			// else
			// handler.removeCallbacks(runnable);
			handler.postDelayed(this, 10000);
		}

	};

	/**
	 * 将新消息广播出去
	 */
	private void ProcessBroadcast(String newCmd, String newMsg) {
		String stringIntentAction = newCmd;
		Intent intentAction = new Intent(stringIntentAction);
		intentAction.putExtra(Constants.MSG_CMD, newCmd);
		intentAction.putExtra(Constants.MSG_PARAM, newMsg);
		this.sendBroadcast(intentAction);
	}

	// 设备查找回调
	BluetoothAdapter.LeScanCallback mLeScanCallback = new LeScanCallback() {

		@Override
		public void onLeScan(final BluetoothDevice device, int rssi,
				byte[] scanRecord) {
			// Log.d(TAG, "mLeScanCallback onLeScan");
			// Log.i(TAG, device.getAddress());
			// if (hmIsConnected.keySet().contains(device.getAddress())
			// && hmIsConnected.get(device.getAddress()).equals(false)) {
			// Log.i("address", addressList + "");
			if (addressList.contains(device.getAddress())) {
				close(mGattMap.get(device.getAddress()));
				Log.i(TAG, "Remote is reConnet");
				BluetoothGatt mBluetoothGatt = device.connectGatt(
						BluetoothLeService.this, false, mGattCallback);
				mGattMap.put(device.getAddress(), mBluetoothGatt);
			}else if ("KeyCtrl".equals(device.getName())) {
				Log.i(TAG, "connect KeyCtrl");
				close(mGattMap.get(device.getAddress()));
				BluetoothGatt mBluetoothGatt = device.connectGatt(
						BluetoothLeService.this, false, mGattCallback);
				mGattMap.put(device.getAddress(), mBluetoothGatt);
			}
		}
	};

	/**
	 * 保存设备信息
	 */
	private void SaveDeviceInfo(BluetoothDevice bludtoothdevice) {
		List<BleConnectDevice> lstConnectDevices = new ArrayList<BleConnectDevice>();
		String connectedinfo = prefs.getString(
				Constants.REMOTE_CONNECTED_DEVICES, "");
		Gson gson = new Gson();
		if (!connectedinfo.equals("")) {

			try {
				lstConnectDevices = gson.fromJson(connectedinfo,
						new TypeToken<List<BleConnectDevice>>() {
						}.getType());
			} catch (Exception e) {
			}
		}
		if (!lstNeedConnectAddress.contains(bludtoothdevice.getAddress())) {
			lstNeedConnectAddress.add(bludtoothdevice.getAddress());
			BleConnectDevice bledevice = new BleConnectDevice();
			bledevice.setAddress(bludtoothdevice.getAddress());
			bledevice.setName(bludtoothdevice.getName());
			lstConnectDevices.add(bledevice);
			try {
				String newaddr = gson.toJson(lstConnectDevices);
				prefs.edit()
						.putString(Constants.REMOTE_CONNECTED_DEVICES, newaddr)
						.commit();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 获取设备信息 lstNeedConnectDevices
	 */
	private void GetDeviceInfo() {
		String connectedinfo = prefs.getString(
				Constants.REMOTE_CONNECTED_DEVICES, "");
		if (!connectedinfo.equals("")) {
			Gson gson = new Gson();
			try {
				lstNeedConnectDevices = gson.fromJson(connectedinfo,
						new TypeToken<List<BleConnectDevice>>() {
						}.getType());
				for (BleConnectDevice bleconnectdevice : lstNeedConnectDevices) {
					if (!lstNeedConnectAddress.contains(bleconnectdevice
							.getAddress()))
						lstNeedConnectAddress
								.add(bleconnectdevice.getAddress());
				}
			} catch (Exception e) {
			}
		}
	}

}
