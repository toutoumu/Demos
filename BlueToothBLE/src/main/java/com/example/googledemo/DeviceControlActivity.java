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

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.example.bluetoothble.R;
import com.example.utils.Cmd;
import com.example.utils.HexString;
import java.util.List;
import java.util.Objects;
import timber.log.Timber;

import static com.example.utils.BluetoothUtils.getNotifyCharacteristic;
import static com.example.utils.BluetoothUtils.getReadCharacteristic;
import static com.example.utils.BluetoothUtils.getWriteCharacteristic;

/**
 * For a given BLE device, this Activity provides the user interface to connect, display data,
 * and display GATT services and characteristics supported by the device.  The Activity
 * communicates with {@code BluetoothLeService}, which in turn interacts with the
 * Bluetooth LE API.
 */
public class DeviceControlActivity extends Activity {

  public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
  public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

  @BindView(R.id.connect) Button connectButton;
  @BindView(R.id.read) Button readButton;
  @BindView(R.id.write) Button writeButton;
  @BindView(R.id.notify) Button notifyButton;

  @BindView(R.id.connection_state) TextView mConnectionState;
  @BindView(R.id.data_value) TextView mDataField;
  @BindView(R.id.device_address) TextView mMacAddress;

  private BluetoothGattCharacteristic mReadCharacteristic; // 读特征值
  private BluetoothGattCharacteristic mWriteCharacteristic; // 写特征值
  private BluetoothGattCharacteristic mNotifyCharacteristic; // 通知特征值

  private String mDeviceAddress;
  private BluetoothLeService mBluetoothLeService;
  private boolean mConnected = false;//是否连接
  private boolean mNotifyEnabled = false;//通知是否开启

  // Code to manage Service lifecycle.
  private final ServiceConnection mServiceConnection = new ServiceConnection() {

    @Override public void onServiceConnected(ComponentName componentName, IBinder service) {
      mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
      if (!mBluetoothLeService.initialize()) {
        Timber.e("Unable to initialize Bluetooth");
        finish();
      }
      // Automatically connects to the device upon successful start-up initialization.
      mBluetoothLeService.connect(mDeviceAddress);
    }

    @Override public void onServiceDisconnected(ComponentName componentName) {
      mBluetoothLeService = null;
    }
  };

  /**
   * 蓝牙回调之后都通过广播发送到应用进行处理
   */
  private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
    @Override public void onReceive(Context context, Intent intent) {
      final String action = intent.getAction();
      switch (action) {
        case BluetoothLeService.ACTION_GATT_CONNECT_FAILURE: {// 蓝牙连接失败
          mConnected = false;
          mNotifyEnabled = false;
          connectButton.setEnabled(true);
          connectButton.setText(R.string.connect);
          updateConnectionState(R.string.disconnected);
          invalidateOptionsMenu();
          mDataField.setText(R.string.no_data);
          updateUI(null);
          break;
        }
        case BluetoothLeService.ACTION_GATT_CONNECTING: {// 蓝牙连接中
          mConnected = false;
          mNotifyEnabled = false;
          connectButton.setEnabled(false);
          connectButton.setText(R.string.connecting);
          updateConnectionState(R.string.connecting);
          mDataField.setText(R.string.no_data);
          updateUI(null);
          break;
        }
        case BluetoothLeService.ACTION_GATT_DISCONNECTING: {// 断开连接中
          connectButton.setEnabled(false);
          connectButton.setText(R.string.disconnecting);
          updateConnectionState(R.string.disconnecting);
          break;
        }
        case BluetoothLeService.ACTION_GATT_CONNECTED: {// 蓝牙已经连接
          mConnected = true;
          connectButton.setEnabled(true);
          connectButton.setText(R.string.disconnect);
          updateConnectionState(R.string.connected);
          invalidateOptionsMenu();
          break;
        }
        case BluetoothLeService.ACTION_GATT_DISCONNECTED: {// 蓝牙已断开
          mConnected = false;
          mNotifyEnabled = false;
          connectButton.setEnabled(true);
          connectButton.setText(R.string.connect);
          updateConnectionState(R.string.disconnected);
          invalidateOptionsMenu();
          mDataField.setText(R.string.no_data);
          updateUI(null);
          break;
        }
        case BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERING: {//连接后,扫描服务中
          break;
        }
        case BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED: {//连接后,服务扫描成功
          List<BluetoothGattService> services = mBluetoothLeService.getSupportedGattServices();
          updateUI(services);
          if (services == null || services.size() == 0) return;
          mReadCharacteristic = getReadCharacteristic(services);
          mWriteCharacteristic = getWriteCharacteristic(services);
          mNotifyCharacteristic = getNotifyCharacteristic(services);

          // 打印服务UUID 以及 特征值UUID
          // BluetoothUtils.listService(services);
          if (mReadCharacteristic != null) {
            Timber.e("读特征值UUID : %s", mReadCharacteristic.getUuid());
          }
          if (mWriteCharacteristic != null) {
            Timber.e("写特征值UUID : %s", mWriteCharacteristic.getUuid());
          }
          if (mNotifyCharacteristic != null) {
            mBluetoothLeService.setCharacteristicNotification(mNotifyCharacteristic, true);
            Timber.d("通知特征值UUID : %s", mNotifyCharacteristic.getUuid());
          }
          break;
        }
        case BluetoothLeService.ACTION_GATT_SERVICES_DISCOVER_FAILURE: {//连接后,服务扫描失败
          break;
        }
        case BluetoothLeService.ACTION_NOTIFY_OPEN_SUCCESS: {//开启通知成功
          mNotifyEnabled = true;
          break;
        }
        case BluetoothLeService.ACTION_NOTIFY_OPEN_FAILURE: {//开启通知失败
          break;
        }
        case BluetoothLeService.ACTION_NOTIFY_CLOSE_SUCCESS: {//关闭通知成功
          mNotifyEnabled = false;
          break;
        }
        case BluetoothLeService.ACTION_NOTIFY_CLOSE_FAILURE: {//关闭通知失败
          break;
        }
        case BluetoothLeService.ACTION_DATA_READ_SUCCESS://写成功
        case BluetoothLeService.ACTION_DATA_READ_FAILURE://写失败
        case BluetoothLeService.ACTION_DATA_WRITE_SUCCESS://读成功
        case BluetoothLeService.ACTION_DATA_WRITE_FAILURE://读失败
        case BluetoothLeService.ACTION_DATA_NOTIFY_SUCCESS:// 接收到数据
        {
          byte[] data = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
          if (data != null && data.length > 0) {
            Timber.e(action + ": " + HexString.bytesToHex(data));
            mDataField.setText(HexString.bytesToHex(data));
          } else {
            mDataField.setText(R.string.no_data);
            Timber.e(action);
          }
          break;
        }
      }
    }
  };

  /**
   * This method updates the UI to a proper state.
   *
   * @param characteristic a nullable {@link BluetoothGattCharacteristic}. If it is null then UI is
   * assuming a disconnected state.
   */
  private void updateUI(List<BluetoothGattService> characteristic) {
    if (characteristic == null) {
      readButton.setEnabled(false);
      writeButton.setEnabled(false);
      notifyButton.setEnabled(false);
      return;
    }
    readButton.setEnabled(getReadCharacteristic(characteristic) != null);
    writeButton.setEnabled(getWriteCharacteristic(characteristic) != null);
    notifyButton.setEnabled(getNotifyCharacteristic(characteristic) != null);
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.gatt_services_characteristics);
    ButterKnife.bind(this);

    final Intent intent = getIntent();
    String mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
    mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

    // Sets up UI references.
    mMacAddress.setText(mDeviceAddress);

    Objects.requireNonNull(getActionBar()).setTitle(mDeviceName);
    getActionBar().setDisplayHomeAsUpEnabled(true);

    // 绑定服务
    Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
    bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
  }

  @Override protected void onResume() {
    super.onResume();
    // 注册广播
    registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    if (mBluetoothLeService != null) {
      final boolean result = mBluetoothLeService.connect(mDeviceAddress);
      Timber.e("Connect request result=" + result);
    }
  }

  @Override protected void onPause() {
    super.onPause();
    // 取消注册广播
    unregisterReceiver(mGattUpdateReceiver);
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    unbindService(mServiceConnection);
    mBluetoothLeService = null;
  }

  /**
   * 连接 | 断开连接
   */
  @OnClick(R.id.connect) public void onConnectToggleClick() {
    if (isConnected()) {
      mBluetoothLeService.disconnect();
    } else {
      mBluetoothLeService.connect(mDeviceAddress);
    }
  }

  /**
   * 读取
   */
  @OnClick(R.id.read) public void onReadClick() {
    if (isConnected()) {
      mBluetoothLeService.readCharacteristic(mReadCharacteristic);
    }
  }

  /**
   * 写入
   */
  @OnClick(R.id.write) public void onWriteClick() {
    if (isConnected()) {
      int time = (int) (System.currentTimeMillis() / 1000);
      String mobile = "";
      byte[] bytes = Cmd.INSTANCE.handCmd(time, mobile);
      mBluetoothLeService.writeCharacteristic(mWriteCharacteristic, bytes);
    }
  }

  /**
   * 注册通知
   */
  @OnClick(R.id.notify) public void onNotifyClick() {
    if (isConnected()) {
      mBluetoothLeService.setCharacteristicNotification(mNotifyCharacteristic, !mNotifyEnabled);
    }
  }

  /**
   * 是否已经连接
   */
  private boolean isConnected() {
    return mConnected;
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.gatt_services, menu);
    menu.findItem(R.id.menu_connect).setVisible(false);
    menu.findItem(R.id.menu_disconnect).setVisible(false);
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        onBackPressed();
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  private void updateConnectionState(final int resourceId) {
    runOnUiThread(() -> mConnectionState.setText(resourceId));
  }

  /**
   * 接收哪些广播
   *
   * @return {@link IntentFilter}
   */
  private static IntentFilter makeGattUpdateIntentFilter() {
    final IntentFilter intentFilter = new IntentFilter();
    // 蓝牙连接
    intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTING);//蓝牙连接中
    intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);//蓝牙已连接
    intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTING);//蓝牙断开中
    intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);//蓝牙已断开
    intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECT_FAILURE);//蓝牙连接失败
    // 扫描服务
    intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERING);//正在扫描成功
    intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);//扫描成功
    intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVER_FAILURE);//扫描失败
    // 读写,通知
    intentFilter.addAction(BluetoothLeService.ACTION_DATA_READ_SUCCESS);//读取成功
    intentFilter.addAction(BluetoothLeService.ACTION_DATA_READ_FAILURE);//读取失败
    intentFilter.addAction(BluetoothLeService.ACTION_DATA_WRITE_SUCCESS);//写入成功
    intentFilter.addAction(BluetoothLeService.ACTION_DATA_WRITE_FAILURE);//写入失败
    intentFilter.addAction(BluetoothLeService.ACTION_DATA_NOTIFY_SUCCESS);//收到通知
    // 通知开关
    intentFilter.addAction(BluetoothLeService.ACTION_NOTIFY_OPEN_SUCCESS);//通知开启成功
    intentFilter.addAction(BluetoothLeService.ACTION_NOTIFY_OPEN_FAILURE);//通知开启失败
    intentFilter.addAction(BluetoothLeService.ACTION_NOTIFY_CLOSE_SUCCESS);//通知关闭失败
    intentFilter.addAction(BluetoothLeService.ACTION_NOTIFY_CLOSE_FAILURE);//通知关闭失败
    return intentFilter;
  }
}
