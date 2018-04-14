package com.example.bluetoothble;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.widget.Button;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.jakewharton.rx.ReplayingShare;
import com.polidea.rxandroidble2.RxBleConnection;
import com.polidea.rxandroidble2.RxBleDevice;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.subjects.PublishSubject;
import java.util.Arrays;
import java.util.List;
import timber.log.Timber;

import static com.trello.rxlifecycle2.android.ActivityEvent.PAUSE;

public class CharacteristicOperationExampleActivity extends RxAppCompatActivity {

  public static String EXTRA_MAC_ADDRESS = "address";
  // public static final String EXTRA_CHARACTERISTIC_UUID = "extra_uuid";

  @BindView(R.id.connect) Button connectButton;
  @BindView(R.id.read_output) TextView readOutputView;
  @BindView(R.id.read_hex_output) TextView readHexOutputView;
  @BindView(R.id.write_input) TextView writeInput;
  @BindView(R.id.read) Button readButton;
  @BindView(R.id.write) Button writeButton;
  @BindView(R.id.notify) Button notifyButton;

  private BluetoothGattCharacteristic mReadCharacteristic;
  private BluetoothGattCharacteristic mWriteCharacteristic;
  private BluetoothGattCharacteristic mReadNotifyCharacteristic;
  // private UUID characteristicUuid;
  private PublishSubject<Boolean> disconnectTriggerSubject = PublishSubject.create();
  private Observable<RxBleConnection> connectionObservable;
  private RxBleDevice bleDevice;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_example4);
    ButterKnife.bind(this);
    String macAddress = getIntent().getStringExtra(EXTRA_MAC_ADDRESS);
    // characteristicUuid = (UUID) getIntent().getSerializableExtra(EXTRA_CHARACTERISTIC_UUID);
    bleDevice = SampleApplication.getRxBleClient(this).getBleDevice(macAddress);
    connectionObservable = prepareConnectionObservable();
    //noinspection ConstantConditions
    getSupportActionBar().setSubtitle(getString(R.string.mac_address, macAddress));
  }

  private Observable<RxBleConnection> prepareConnectionObservable() {
    return bleDevice.establishConnection(false)
        .takeUntil(disconnectTriggerSubject)
        .compose(bindUntilEvent(PAUSE))
        .compose(ReplayingShare.instance());
  }

  @OnClick(R.id.connect) public void onConnectToggleClick() {
    if (isConnected()) {
      triggerDisconnect();
    } else {
      connectionObservable.flatMapSingle(RxBleConnection::discoverServices)
          /*.flatMapSingle(
              rxBleDeviceServices -> rxBleDeviceServices.getCharacteristic(characteristicUuid))*/
          .observeOn(AndroidSchedulers.mainThread())
          .doOnSubscribe(disposable -> connectButton.setText(R.string.connecting))
          .subscribe(characteristic -> {
            updateUI(characteristic.getBluetoothGattServices());
            mReadCharacteristic = getReadCharacteristic(characteristic.getBluetoothGattServices());
            mWriteCharacteristic =
                getWriteCharacteristic(characteristic.getBluetoothGattServices());
            mReadNotifyCharacteristic =
                getReadNotifyCharacteristic(characteristic.getBluetoothGattServices());
            Timber.i("Hey, connection has been established!");
          }, this::onConnectionFailure, this::onConnectionFinished);
    }
  }

  @OnClick(R.id.read) public void onReadClick() {
    if (isConnected()) {
      connectionObservable.firstOrError()
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

  @OnClick(R.id.write) public void onWriteClick() {
    if (isConnected()) {
      connectionObservable.firstOrError()
          //.flatMap(rxBleConnection -> rxBleConnection.writeCharacteristic(characteristicUuid, getInputBytes()))
          .flatMap(rxBleConnection -> {
            int time = (int) (System.currentTimeMillis() / 1000);

            String mobile = "";

            byte[] bytes = Cmd.INSTANCE.handCmd(time, mobile);

            return rxBleConnection.writeCharacteristic(mWriteCharacteristic, bytes);
            //return rxBleConnection.writeCharacteristic(mWriteCharacteristic, getInputBytes());
          })
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(bytes -> onWriteSuccess(), this::onWriteFailure);
    }
  }

  @OnClick(R.id.notify) public void onNotifyClick() {
    if (isConnected()) {
      connectionObservable.flatMap(
          rxBleConnection -> rxBleConnection.setupNotification(mReadNotifyCharacteristic))
          //rxBleConnection -> rxBleConnection.setupNotification(characteristicUuid))
          .doOnNext(notificationObservable -> runOnUiThread(this::notificationHasBeenSetUp))
          .flatMap(notificationObservable -> notificationObservable)
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(this::onNotificationReceived, this::onNotificationSetupFailure);
    }
  }

  private boolean isConnected() {
    return bleDevice.getConnectionState() == RxBleConnection.RxBleConnectionState.CONNECTED;
  }

  private void onConnectionFailure(Throwable throwable) {
    //noinspection ConstantConditions
    Timber.e(throwable);
    Snackbar.make(findViewById(R.id.main), "Connection error: " + throwable, Snackbar.LENGTH_LONG)
        .show();
    updateUI(null);
  }

  private void onConnectionFinished() {
    updateUI(null);
  }

  private void onReadFailure(Throwable throwable) {
    //noinspection ConstantConditions
    Timber.e(throwable);
    Snackbar.make(findViewById(R.id.main), "Read error: " + throwable, Snackbar.LENGTH_LONG).show();
  }

  private void onWriteSuccess() {
    //noinspection ConstantConditions
    Snackbar.make(findViewById(R.id.main), "Write success", Snackbar.LENGTH_LONG).show();
  }

  private void onWriteFailure(Throwable throwable) {
    //noinspection ConstantConditions
    Timber.e(throwable);
    Snackbar.make(findViewById(R.id.main), "Write error: " + throwable, Snackbar.LENGTH_LONG)
        .show();
  }

  private void onNotificationReceived(byte[] bytes) {
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
    } else {
      Timber.e("%s:%s", "onNotificationReceived--error", HexString.bytesToHex(bytes));
    }
  }

  private void onNotificationSetupFailure(Throwable throwable) {
    //noinspection ConstantConditions
    Timber.e(throwable);
    Snackbar.make(findViewById(R.id.main), "Notifications error: " + throwable,
        Snackbar.LENGTH_LONG).show();
  }

  private void notificationHasBeenSetUp() {
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
    //readButton.setEnabled(hasProperty(characteristic, BluetoothGattCharacteristic.PROPERTY_READ));
    //writeButton.setEnabled(hasProperty(characteristic, BluetoothGattCharacteristic.PROPERTY_WRITE));
    //notifyButton.setEnabled(hasProperty(characteristic, BluetoothGattCharacteristic.PROPERTY_NOTIFY));
    if (characteristic == null) {
      readButton.setEnabled(false);
      writeButton.setEnabled(false);
      notifyButton.setEnabled(false);
      return;
    }
    readButton.setEnabled(getReadNotifyCharacteristic(characteristic) != null);
    writeButton.setEnabled(getWriteCharacteristic(characteristic) != null);
    notifyButton.setEnabled(getReadNotifyCharacteristic(characteristic) != null);
  }

  private byte[] getInputBytes() {
    return HexString.hexToBytes(writeInput.getText().toString());
  }

  private boolean hasProperty(BluetoothGattCharacteristic characteristic, int property) {
    return characteristic != null && (characteristic.getProperties() & property) > 0;
  }

  private boolean isCharacteristicNotifiable(BluetoothGattCharacteristic characteristic) {
    return hasProperty(characteristic, BluetoothGattCharacteristic.PROPERTY_NOTIFY);
  }

  private boolean isCharacteristicReadable(BluetoothGattCharacteristic characteristic) {
    return hasProperty(characteristic, BluetoothGattCharacteristic.PROPERTY_READ);
  }

  private boolean isCharacteristicWriteable(BluetoothGattCharacteristic characteristic) {
    return hasProperty(characteristic, BluetoothGattCharacteristic.PROPERTY_WRITE
        | BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE);
  }

  private BluetoothGattCharacteristic getReadNotifyCharacteristic(
      List<BluetoothGattService> services) {
    for (BluetoothGattService service : services) {
      for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
        if (isCharacteristicReadable(characteristic) && isCharacteristicNotifiable(
            characteristic)) {
          return characteristic;
        }
      }
    }
    return null;
  }

  private BluetoothGattCharacteristic getWriteNotifyCharacteristic(
      List<BluetoothGattService> services) {
    for (BluetoothGattService service : services) {
      for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
        if (isCharacteristicWriteable(characteristic) && isCharacteristicNotifiable(
            characteristic)) {
          return characteristic;
        }
      }
    }
    return null;
  }

  private BluetoothGattCharacteristic getWriteCharacteristic(List<BluetoothGattService> services) {
    for (BluetoothGattService service : services) {
      for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
        if (isCharacteristicWriteable(characteristic) && !isCharacteristicNotifiable(
            characteristic)) {
          return characteristic;
        }
      }
    }
    return null;
  }

  private BluetoothGattCharacteristic getReadCharacteristic(List<BluetoothGattService> services) {
    for (BluetoothGattService service : services) {
      for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
        if (isCharacteristicReadable(characteristic) && !isCharacteristicNotifiable(
            characteristic)) {
          return characteristic;
        }
      }
    }
    return null;
  }
}
