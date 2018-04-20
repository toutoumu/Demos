package com.example.bluetoothble;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.widget.Button;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.example.utils.BluetoothUtils;
import com.example.utils.HexString;
import com.jakewharton.rx.ReplayingShare;
import com.polidea.rxandroidble2.RxBleConnection;
import com.polidea.rxandroidble2.RxBleDevice;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;
import java.util.Arrays;
import java.util.List;
import timber.log.Timber;

import static com.example.utils.BluetoothUtils.getNotifyCharacteristic;
import static com.example.utils.BluetoothUtils.getReadCharacteristic;
import static com.example.utils.BluetoothUtils.getWriteCharacteristic;
import static com.trello.rxlifecycle2.android.ActivityEvent.DESTROY;
import static com.trello.rxlifecycle2.android.ActivityEvent.PAUSE;

/**
 * 关于UUID https://www.cnblogs.com/michaelzero/p/6835642.html
 */
public class CharacteristicOperationExampleActivity extends RxAppCompatActivity {

  /** 蓝牙mac地址 */
  public static String EXTRA_MAC_ADDRESS = "address";

  @BindView(R.id.connect) Button connectButton;
  @BindView(R.id.read_output) TextView readOutputView;
  @BindView(R.id.read_hex_output) TextView readHexOutputView;
  @BindView(R.id.write_input) TextView writeInput;
  @BindView(R.id.read) Button readButton;
  @BindView(R.id.write) Button writeButton;
  @BindView(R.id.notify) Button notifyButton;

  private BluetoothGattCharacteristic mReadCharacteristic; // 读特征值
  private BluetoothGattCharacteristic mWriteCharacteristic; // 写特征值
  private BluetoothGattCharacteristic mNotifyCharacteristic; // 通知特征值
  private Observable<RxBleConnection> connectionObservable;
  private PublishSubject<Boolean> disconnectTriggerSubject = PublishSubject.create();// 用于断开连接
  private RxBleDevice bleDevice; // 蓝牙设备

  @SuppressLint("CheckResult") @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_example4);
    ButterKnife.bind(this);
    String macAddress = getIntent().getStringExtra(EXTRA_MAC_ADDRESS);
    getSupportActionBar().setSubtitle(getString(R.string.mac_address, macAddress));

    bleDevice = SampleApplication.getRxBleClient(this).getBleDevice(macAddress);
    // 监听链接状态
    bleDevice.observeConnectionStateChanges()
        .compose(bindUntilEvent(DESTROY))
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(this::onConnectionStateChange);
    connectionObservable = prepareConnectionObservable();
  }

  /**
   * 准备连接对象
   */
  private Observable<RxBleConnection> prepareConnectionObservable() {
    return bleDevice.establishConnection(false)
        .takeUntil(disconnectTriggerSubject)
        .compose(bindUntilEvent(PAUSE))
        .compose(ReplayingShare.instance());
  }

  /**
   * 连接 | 断开连接
   */
  @OnClick(R.id.connect) public void onConnectToggleClick() {
    if (isConnected()) {
      triggerDisconnect();
    } else {
      Disposable subscribe = connectionObservable.flatMapSingle(RxBleConnection::discoverServices)
          //.flatMapSingle(rxBleDeviceServices -> rxBleDeviceServices.getCharacteristic(characteristicUuid))
          .observeOn(AndroidSchedulers.mainThread())
          .doOnSubscribe(disposable -> connectButton.setText(R.string.connecting))
          .subscribe(bleDeviceServices -> {
            List<BluetoothGattService> services = bleDeviceServices.getBluetoothGattServices();

            updateUI(services);
            mReadCharacteristic = getReadCharacteristic(services);
            mWriteCharacteristic = getWriteCharacteristic(services);
            mNotifyCharacteristic = getNotifyCharacteristic(services);

            // 打印服务UUID 以及 特征值UUID
            BluetoothUtils.listService(services);
            Timber.e("读特征值UUID : %s", mReadCharacteristic.getUuid());
            Timber.e("写特征值UUID : %s", mWriteCharacteristic.getUuid());
            Timber.d("通知特征值UUID : %s", mNotifyCharacteristic.getUuid());
          }, this::onConnectionFailure, this::onConnectionFinished);
    }
  }

  /**
   * 读取
   */
  @OnClick(R.id.read) public void onReadClick() {
    if (isConnected()) {
      Disposable subscribe = connectionObservable.firstOrError()
          //.flatMap(rxBleConnection -> rxBleConnection.readCharacteristic(characteristicUuid))
          .flatMap(rxBleConnection -> rxBleConnection.readCharacteristic(mReadCharacteristic))
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(bytes -> {
            readOutputView.setText(new String(bytes));
            readHexOutputView.setText(HexString.bytesToHex(bytes));
            writeInput.setText(HexString.bytesToHex(bytes));
          }, this::onReadFailure);
    }
  }

  /**
   * 写入
   */
  @OnClick(R.id.write) public void onWriteClick() {
    if (isConnected()) {
      Disposable subscribe = connectionObservable.firstOrError()
          .flatMap(rxBleConnection -> {
            int time = (int) (System.currentTimeMillis() / 1000);
            String mobile = "";
            byte[] bytes = Cmd.INSTANCE.handCmd(time, mobile);
            return rxBleConnection.writeCharacteristic(mWriteCharacteristic, bytes);
          })
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(this::onWriteSuccess, this::onWriteFailure);
    }
  }

  /**
   * 注册通知
   */
  @OnClick(R.id.notify) public void onNotifyClick() {
    if (isConnected()) {
      Disposable subscribe = connectionObservable.flatMap(
          rxBleConnection -> rxBleConnection.setupNotification(mNotifyCharacteristic))
          .doOnNext(notificationObservable -> runOnUiThread(this::notificationHasBeenSetUp))
          .flatMap(notificationObservable -> notificationObservable)
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(this::onNotificationReceived, this::onNotificationSetupFailure);
    }
  }

  /**
   * 连接状态改变监听
   *
   * @param rxBleConnectionState
   */
  private void onConnectionStateChange(RxBleConnection.RxBleConnectionState rxBleConnectionState) {
    Timber.e("ConnectionStateChange :%s", rxBleConnectionState);
  }

  /**
   * 是否已经连接
   */
  private boolean isConnected() {
    return bleDevice.getConnectionState() == RxBleConnection.RxBleConnectionState.CONNECTED;
  }

  /**
   * 连接失败
   *
   * @param throwable
   */
  private void onConnectionFailure(Throwable throwable) {
    //noinspection ConstantConditions
    Timber.e("onConnectionFailure");
    Timber.e(throwable);
    Snackbar.make(findViewById(R.id.main), "Connection error: " + throwable, Snackbar.LENGTH_LONG)
        .show();
    updateUI(null);
  }

  /**
   * 连接结束
   */
  private void onConnectionFinished() {
    Timber.e("onConnectionFinished");
    updateUI(null);
  }

  /**
   * 读失败
   *
   * @param throwable
   */
  private void onReadFailure(Throwable throwable) {
    //noinspection ConstantConditions
    Timber.e(throwable);
    Snackbar.make(findViewById(R.id.main), "Read error: " + throwable, Snackbar.LENGTH_LONG).show();
  }

  /**
   * 写成功
   *
   * @param bytes
   */
  private void onWriteSuccess(byte[] bytes) {
    //noinspection ConstantConditions
    Timber.e("WriteSuccess : %s", HexString.bytesToHex(bytes));
    Snackbar.make(findViewById(R.id.main), "Write success", Snackbar.LENGTH_LONG).show();
  }

  /**
   * 写失败
   *
   * @param throwable
   */
  private void onWriteFailure(Throwable throwable) {
    //noinspection ConstantConditions
    Timber.e(throwable);
    Snackbar.make(findViewById(R.id.main), "Write error: " + throwable, Snackbar.LENGTH_LONG)
        .show();
  }

  /**
   * 接受通知
   *
   * @param bytes
   */
  private void onNotificationReceived(byte[] bytes) {
    Timber.e("%s:%s", "onNotificationReceived--data", HexString.bytesToHex(bytes));
    //noinspection ConstantConditions
    if (bytes[0] == (byte) 0xEE) {
      // 协议部分
      Timber.e("%s:%s", "onNotificationReceived--cmd", bytes[1]);
      Timber.e("%s:%s", "onNotificationReceived--length", bytes[2]);

      // 数据部分
      byte[] array = Arrays.copyOfRange(bytes, 3, bytes.length - 1);
      String model = MessageUtils.INSTANCE.byteArrayToStr(Arrays.copyOfRange(array, 0, 4));
      String number = MessageUtils.INSTANCE.bytesToHex(Arrays.copyOfRange(array, 4, 9));
      int version = array[9] & 0xFF;
      int socketCount = array[10] & 0xFF;
      int socketStart = array[11] & 0xFF;

      Snackbar.make(findViewById(R.id.main), "Change: " + HexString.bytesToHex(bytes),
          Snackbar.LENGTH_LONG).show();
    }
  }

  /**
   * 接受通知失败
   *
   * @param throwable
   */
  private void onNotificationSetupFailure(Throwable throwable) {
    Timber.e("NotificationSetupFailure");
    //noinspection ConstantConditions
    Timber.e(throwable);
    Snackbar.make(findViewById(R.id.main), "Notifications error: " + throwable,
        Snackbar.LENGTH_LONG).show();
  }

  private void notificationHasBeenSetUp() {
    Timber.e("notificationHasBeenSetUp");
    //noinspection ConstantConditions
    Snackbar.make(findViewById(R.id.main), "Notifications has been set up", Snackbar.LENGTH_LONG)
        .show();
  }

  /**
   * 触发断开连接
   */
  private void triggerDisconnect() {
    disconnectTriggerSubject.onNext(true);
  }

  /**
   * This method updates the UI to a proper state.
   *
   * @param characteristic a nullable {@link BluetoothGattCharacteristic}. If it is null then UI is
   * assuming a disconnected state.
   */
  private void updateUI(List<BluetoothGattService> characteristic) {
    connectButton.setText(characteristic != null ? R.string.disconnect : R.string.connect);
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
}
