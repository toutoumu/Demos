/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.googledemo;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import java.util.List;
import java.util.UUID;
import timber.log.Timber;

import static com.example.googledemo.SampleGattAttributes.HEART_RATE_MEASUREMENT;

/**
 * BLE 设备可以通过该服务 与 Android 的 BLE API 进行互动
 */
public class BluetoothLeService extends Service {

  private BluetoothManager mBluetoothManager;
  private BluetoothAdapter mBluetoothAdapter;
  private BluetoothGatt mBluetoothGatt;
  private String mMacAddress;

  /** 连接状态 */
  private int mConnectionState = STATE_DISCONNECTED;
  private static final int STATE_DISCONNECTED = 0;
  private static final int STATE_CONNECTING = 1;
  private static final int STATE_DISCONNECTING = 3;
  private static final int STATE_CONNECTED = 2;

  /** 扩展数据 */
  public final static String EXTRA_DATA = "a.EXTRA_DATA";
  /** 心率检测特征UUDI */
  public final static UUID UUID_HEART_RATE_MEASUREMENT = UUID.fromString(HEART_RATE_MEASUREMENT);

  /** 蓝牙已连接 */
  public final static String ACTION_GATT_CONNECTED = "a.ACTION_GATT_CONNECTED";
  /** 蓝牙连接中 */
  public final static String ACTION_GATT_CONNECTING = "a.ACTION_GATT_CONNECTING";
  /** 蓝牙断开中 */
  public final static String ACTION_GATT_DISCONNECTING = "a.ACTION_GATT_DISCONNECTING";
  /** 蓝牙已断开 */
  public final static String ACTION_GATT_DISCONNECTED = "a.ACTION_GATT_DISCONNECTED";
  /** 蓝牙连接失败 */
  public final static String ACTION_GATT_CONNECT_FAILURE = "a.ACTION_GATT_CONNECT_FAILURE";

  /** 正在扫描服务 */
  public final static String ACTION_GATT_SERVICES_DISCOVERING =
      "a.ACTION_GATT_SERVICES_DISCOVERING";
  /** 扫描服务成功 */
  public final static String ACTION_GATT_SERVICES_DISCOVERED = "a.ACTION_GATT_SERVICES_DISCOVERED";
  /** 扫描服务失败 */
  public final static String ACTION_GATT_SERVICES_DISCOVER_FAILURE =
      "a.ACTION_GATT_SERVICES_DISCOVER_FAILURE";

  /** 读成功 */
  public final static String ACTION_DATA_READ_SUCCESS = "a.ACTION_DATA_READ_SUCCESS";
  /** 读失败 */
  public final static String ACTION_DATA_READ_FAILURE = "a.ACTION_DATA_READ_FAILURE";
  /** 写成功 */
  public final static String ACTION_DATA_WRITE_SUCCESS = "a.ACTION_DATA_WRITE_SUCCESS";
  /** 写失败 */
  public final static String ACTION_DATA_WRITE_FAILURE = "a.ACTION_DATA_WRITE_FAILURE";
  /** 收到消息通知 */
  public final static String ACTION_DATA_NOTIFY_SUCCESS = "a.ACTION_DATA_NOTIFY_SUCCESS";

  /** 通知开启成功 */
  public final static String ACTION_NOTIFY_OPEN_SUCCESS = "a.ACTION_NOTIFY_OPEN_SUCCESS";
  /** 通知开启失败 */
  public final static String ACTION_NOTIFY_OPEN_FAILURE = "a.ACTION_NOTIFY_OPEN_FAILURE";
  /** 通知关闭失败 */
  public static final String ACTION_NOTIFY_CLOSE_FAILURE = "a.ACTION_NOTIFY_CLOSE_FAILURE";
  /** 通知关闭成功 */
  public static final String ACTION_NOTIFY_CLOSE_SUCCESS = "a.ACTION_NOTIFY_CLOSE_SUCCESS";

  // Implements callback methods for GATT events that the app cares about.  For example,
  // connection change and services discovered.
  private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
    @Override public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
      String intentAction;
      if (status == BluetoothGatt.GATT_SUCCESS) {//连接或断开成功
        if (newState == BluetoothProfile.STATE_CONNECTED) {// 蓝牙已连接
          intentAction = ACTION_GATT_CONNECTED;
          mConnectionState = STATE_CONNECTED;
          broadcastUpdate(intentAction);
          Timber.e("onConnectionStateChange 蓝牙已连接");

          // 蓝牙连接成功后查找服务
          boolean b = mBluetoothGatt.discoverServices();
          Timber.e("onConnectionStateChange 尝试扫描服务:" + (b ? "成功" : "失败"));
          if (b) {// 正在扫描服务
            Timber.e("正在扫描服务");
            intentAction = ACTION_GATT_SERVICES_DISCOVERING;
            broadcastUpdate(intentAction);
          } else {// 开启扫描失败,连接失败
            // TODO: 2018/4/20 是调用断开连接,还是通知连接失败,还是在界面添加扫描服务功能????
            //intentAction = ACTION_GATT_CONNECT_FAILURE;
            //broadcastUpdate(intentAction);
            disconnect();
          }
        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {// 蓝牙已断开
          intentAction = ACTION_GATT_DISCONNECTED;
          mConnectionState = STATE_DISCONNECTED;
          Timber.e("onConnectionStateChange 蓝牙已断开");
          broadcastUpdate(intentAction);
        }
      } else {// 断开或连接失败
        if (newState == BluetoothProfile.STATE_CONNECTED) {
          Timber.e("onConnectionStateChange 蓝牙断开失败" + status);
        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
          Timber.e("onConnectionStateChange 蓝牙连接失败" + status);
        }
        intentAction = ACTION_GATT_CONNECT_FAILURE;
        broadcastUpdate(intentAction);
      }
    }

    /**
     * discoverServices() 回调
     */
    @Override public void onServicesDiscovered(BluetoothGatt gatt, int status) {
      if (status == BluetoothGatt.GATT_SUCCESS) { // 扫描成功
        broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
        Timber.e("onServicesDiscovered 扫描成功");
      } else {//扫描失败
        broadcastUpdate(ACTION_GATT_SERVICES_DISCOVER_FAILURE);
        Timber.e("onServicesDiscovered received: " + status);
        disconnect();// todo 扫描失败是断开连接,还是在界面添加扫描服务功能 ????
      }
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic,
        int status) {
      if (status == BluetoothGatt.GATT_SUCCESS) {
        Timber.e("onCharacteristicRead 成功: ");
        broadcastUpdate(ACTION_DATA_READ_SUCCESS, characteristic);
      } else {
        Timber.e("onCharacteristicRead 失败: " + status);
        broadcastUpdate(ACTION_DATA_READ_FAILURE, characteristic);
      }
    }

    @Override public void onCharacteristicWrite(BluetoothGatt gatt,
        BluetoothGattCharacteristic characteristic, int status) {
      super.onCharacteristicWrite(gatt, characteristic, status);
      if (status == BluetoothGatt.GATT_SUCCESS) {
        Timber.e("onCharacteristicWrite 成功: ");
        broadcastUpdate(ACTION_DATA_WRITE_SUCCESS, characteristic);
      } else {
        Timber.e("onCharacteristicWrite 失败: " + status);
        broadcastUpdate(ACTION_DATA_WRITE_FAILURE, characteristic);
      }
    }

    @Override public void onCharacteristicChanged(BluetoothGatt gatt,
        BluetoothGattCharacteristic characteristic) {
      broadcastUpdate(ACTION_DATA_NOTIFY_SUCCESS, characteristic);
      Timber.e("onCharacteristicChanged: 收到通知");
    }
  };

  private void broadcastUpdate(final String action) {
    final Intent intent = new Intent(action);
    sendBroadcast(intent);
  }

  private void broadcastUpdate(final String action,
      final BluetoothGattCharacteristic characteristic) {
    final Intent intent = new Intent(action);

    // This is special handling for the Heart Rate Measurement profile.  Data parsing is
    // carried out as per profile specifications:
    // http://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.heart_rate_measurement.xml
    // 蓝牙心率处理
    if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
      int flag = characteristic.getProperties();
      int format;
      if ((flag & 0x01) != 0) {
        format = BluetoothGattCharacteristic.FORMAT_UINT16;
        Timber.e("Heart rate format UINT16.");
      } else {
        format = BluetoothGattCharacteristic.FORMAT_UINT8;
        Timber.e("Heart rate format UINT8.");
      }
      final int heartRate = characteristic.getIntValue(format, 1);
      Timber.e(String.format("Received heart rate: %d", heartRate));
      intent.putExtra(EXTRA_DATA, String.valueOf(heartRate));
    } else {
      intent.putExtra(EXTRA_DATA, characteristic.getValue());
    }
    sendBroadcast(intent);
  }

  public class LocalBinder extends Binder {
    BluetoothLeService getService() {
      return BluetoothLeService.this;
    }
  }

  @Override public IBinder onBind(Intent intent) {
    return mBinder;
  }

  @Override public boolean onUnbind(Intent intent) {
    // After using a given device, you should make sure that BluetoothGatt.close() is called
    // such that resources are cleaned up properly.  In this particular example, close() is
    // invoked when the UI is disconnected from the Service.
    close();
    return super.onUnbind(intent);
  }

  private final IBinder mBinder = new LocalBinder();

  /**
   * Initializes a reference to the local Bluetooth adapter.
   *
   * @return Return true if the initialization is successful.
   */
  public boolean initialize() {
    // For API level 18 and above, get a reference to BluetoothAdapter through
    // BluetoothManager.
    if (mBluetoothManager == null) {
      mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
      if (mBluetoothManager == null) {
        Timber.e("Unable to initialize BluetoothManager.");
        return false;
      }
    }

    mBluetoothAdapter = mBluetoothManager.getAdapter();
    if (mBluetoothAdapter == null) {
      Timber.e("Unable to obtain a BluetoothAdapter.");
      return false;
    }

    return true;
  }

  /**
   * Connects to the GATT server hosted on the Bluetooth LE device.
   *
   * @param address The device address of the destination device.
   * @return Return true if the connection is initiated successfully. The connection result
   * is reported asynchronously through the
   * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
   * callback.
   */
  public boolean connect(final String address) {
    if (mBluetoothAdapter == null || address == null) {
      Timber.e("BluetoothAdapter not initialized or unspecified address.");
      return false;
    }

    // Previously connected device.  Try to reconnect.
    if (mMacAddress != null && address.equals(mMacAddress) && mBluetoothGatt != null) {
      Timber.e("Trying to use an existing mBluetoothGatt for connection.");
      if (mBluetoothGatt.connect()) {
        mConnectionState = STATE_CONNECTING;
        broadcastUpdate(ACTION_GATT_CONNECTING);
        return true;
      } else {
        mConnectionState = STATE_DISCONNECTED;
        broadcastUpdate(ACTION_GATT_CONNECT_FAILURE);
        Timber.e("连接失败");
        return false;
      }
    }

    final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
    if (device == null) {
      mConnectionState = STATE_DISCONNECTED;
      broadcastUpdate(ACTION_GATT_CONNECT_FAILURE);
      Timber.e("设备未找到,连接失败.");
      return false;
    }
    // We want to directly connect to the device, so we are setting the autoConnect
    // parameter to false.
    mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
    Timber.e("Trying to create a new connection.");
    mMacAddress = address;
    mConnectionState = STATE_CONNECTING;
    broadcastUpdate(ACTION_GATT_CONNECTING);
    return true;
  }

  /**
   * Disconnects an existing connection or cancel a pending connection. The disconnection result
   * is reported asynchronously through the
   * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
   * callback.
   */
  public void disconnect() {
    if (mBluetoothAdapter == null || mBluetoothGatt == null) {
      Timber.e("BluetoothAdapter not initialized");
      return;
    }
    mBluetoothGatt.disconnect();
    mConnectionState = STATE_DISCONNECTING;
    broadcastUpdate(ACTION_GATT_DISCONNECTING);
    Timber.e("disconnect 断开蓝牙");
  }

  /**
   * After using a given BLE device, the app must call this method to ensure resources are
   * released properly.
   */
  public void close() {
    if (mBluetoothGatt == null) {
      return;
    }
    mBluetoothGatt.close();
    mBluetoothGatt = null;
    Timber.e("close 关闭蓝牙");
  }

  /**
   * 读取
   * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
   * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
   * callback.
   *
   * @param characteristic The characteristic to read from.
   */
  public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
    if (mBluetoothAdapter == null || mBluetoothGatt == null) {
      Timber.e("BluetoothAdapter not initialized");
      return;
    }
    if (!mBluetoothGatt.readCharacteristic(characteristic)) {
      Timber.e("readCharacteristic 读取失败");
      broadcastUpdate(ACTION_DATA_READ_FAILURE, characteristic);
    }
  }

  /**
   * 写入
   *
   * @param characteristic
   * @param data
   */
  public void writeCharacteristic(BluetoothGattCharacteristic characteristic, byte[] data) {
    if (mBluetoothAdapter == null || mBluetoothGatt == null) {
      Timber.e("BluetoothAdapter not initialized");
      return;
    }
    characteristic.setValue(data);
    if (!mBluetoothGatt.writeCharacteristic(characteristic)) {
      Timber.e("writeCharacteristic 写入失败");
      broadcastUpdate(ACTION_DATA_WRITE_FAILURE, characteristic);
    }
  }

  /**
   * 通知开启或关闭
   * Enables or disables notification on a give characteristic.
   *
   * @param characteristic Characteristic to act on.
   * @param enabled If true, enable notification.  False otherwise.
   */
  public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
      boolean enabled) {
    if (mBluetoothAdapter == null || mBluetoothGatt == null) {
      Timber.e("BluetoothAdapter未初始化");
      return;
    }
    boolean b = mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
    if (enabled) {
      broadcastUpdate(b ? ACTION_NOTIFY_OPEN_SUCCESS : ACTION_NOTIFY_OPEN_FAILURE);
      Timber.e("setCharacteristicNotification 通知开启" + (b ? "成功 : " : "失败 : "));
    } else {
      broadcastUpdate(b ? ACTION_NOTIFY_CLOSE_SUCCESS : ACTION_NOTIFY_CLOSE_FAILURE);
      Timber.e("setCharacteristicNotification 通知关闭" + (b ? "成功 : " : "失败 : "));
    }
    // 心率测量 This is specific to Heart Rate Measurement.
    if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
      BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
          UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
      descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
      mBluetoothGatt.writeDescriptor(descriptor);
    }
  }

  /**
   * Retrieves a list of supported GATT services on the connected device. This should be
   * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
   *
   * @return A {@code List} of supported services.
   */
  public List<BluetoothGattService> getSupportedGattServices() {
    if (mBluetoothGatt == null) return null;
    return mBluetoothGatt.getServices();
  }
}
