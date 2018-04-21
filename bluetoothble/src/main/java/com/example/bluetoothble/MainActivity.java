package com.example.bluetoothble;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.example.googledemo.DeviceScanActivity;
import com.polidea.rxandroidble2.RxBleClient;
import com.polidea.rxandroidble2.exceptions.BleScanException;
import com.polidea.rxandroidble2.scan.ScanFilter;
import com.polidea.rxandroidble2.scan.ScanResult;
import com.polidea.rxandroidble2.scan.ScanSettings;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static com.example.bluetoothble.BluetoothControlActivity.EXTRA_MAC_ADDRESS;

public class MainActivity extends RxAppCompatActivity {

  @BindView(R.id.scan_toggle_btn) Button scanToggleButton;
  @BindView(R.id.scan_results) RecyclerView recyclerView;
  private RxBleClient rxBleClient;
  private Disposable scanDisposable;
  private ScanResultsAdapter resultsAdapter;
  private int REQUEST_ENABLE_BT = 33;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);
    rxBleClient = App.getRxBleClient(this);
    requestBT();
    configureResultList();
  }

  @OnClick(R.id.demo_google) public void onStartGoogleDemo() {
    Intent intent = new Intent(this, DeviceScanActivity.class);
    startActivity(intent);
  }

  @OnClick(R.id.scan_toggle_btn) public void onScanToggleClick() {

    if (isScanning()) {
      scanDisposable.dispose();
    } else {
      scanDisposable = rxBleClient.scanBleDevices(
          new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
              .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
              .build(), new ScanFilter.Builder()//.setDeviceName("1010999999")
              // .setDeviceAddress("B4:99:4C:34:DC:8B")
              // add custom filters if needed
              .build()).filter(result -> {
        return result.getBleDevice().getName() != null;
        //return "1010999999".equals(result.getBleDevice().getName());
      }).take(5)
          .observeOn(AndroidSchedulers.mainThread())
          .doFinally(this::dispose)
          .subscribe(resultsAdapter::addScanResult, this::onScanFailure);
    }

    updateButtonUIState();
  }

  private void handleBleScanException(BleScanException bleScanException) {
    final String text;

    switch (bleScanException.getReason()) {
      case BleScanException.BLUETOOTH_NOT_AVAILABLE:
        text = "Bluetooth is not available";
        break;
      case BleScanException.BLUETOOTH_DISABLED:
        text = "Enable bluetooth and try again";
        break;
      case BleScanException.LOCATION_PERMISSION_MISSING:
        text = "On Android 6.0 location permission is required. Implement Runtime Permissions";
        break;
      case BleScanException.LOCATION_SERVICES_DISABLED:
        text = "Location services needs to be enabled on Android 6.0";
        break;
      case BleScanException.SCAN_FAILED_ALREADY_STARTED:
        text = "Scan with the same filters is already started";
        break;
      case BleScanException.SCAN_FAILED_APPLICATION_REGISTRATION_FAILED:
        text = "Failed to register application for bluetooth scan";
        break;
      case BleScanException.SCAN_FAILED_FEATURE_UNSUPPORTED:
        text = "Scan with specified parameters is not supported";
        break;
      case BleScanException.SCAN_FAILED_INTERNAL_ERROR:
        text = "Scan failed due to internal error";
        break;
      case BleScanException.SCAN_FAILED_OUT_OF_HARDWARE_RESOURCES:
        text = "Scan cannot start due to limited hardware resources";
        break;
      case BleScanException.UNDOCUMENTED_SCAN_THROTTLE:
        text = String.format(Locale.getDefault(),
            "Android 7+ does not allow more scans. Try in %d seconds",
            secondsTill(bleScanException.getRetryDateSuggestion()));
        break;
      case BleScanException.UNKNOWN_ERROR_CODE:
      case BleScanException.BLUETOOTH_CANNOT_START:
      default:
        text = "Unable to start scanning";
        break;
    }
    Log.w("EXCEPTION", text, bleScanException);
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
  }

  private long secondsTill(Date retryDateSuggestion) {
    return TimeUnit.MILLISECONDS.toSeconds(
        retryDateSuggestion.getTime() - System.currentTimeMillis());
  }

  @Override public void onPause() {
    super.onPause();

    if (isScanning()) {
      /*
       * Stop scanning in onPause callback. You can use rxlifecycle for convenience. Examples are provided later.
       */
      scanDisposable.dispose();
    }
  }

  private void configureResultList() {
    recyclerView.setHasFixedSize(true);
    LinearLayoutManager recyclerLayoutManager = new LinearLayoutManager(this);
    recyclerView.setLayoutManager(recyclerLayoutManager);
    resultsAdapter = new ScanResultsAdapter();
    recyclerView.setAdapter(resultsAdapter);
    resultsAdapter.setOnAdapterItemClickListener(view -> {
      final int childAdapterPosition = recyclerView.getChildAdapterPosition(view);
      final ScanResult itemAtPosition = resultsAdapter.getItemAtPosition(childAdapterPosition);
      onAdapterItemClick(itemAtPosition);
    });
  }

  private boolean isScanning() {
    return scanDisposable != null;
  }

  private void onAdapterItemClick(ScanResult scanResults) {
    final String macAddress = scanResults.getBleDevice().getMacAddress();
    final Intent intent = new Intent(this, BluetoothControlActivity.class);
    intent.putExtra(EXTRA_MAC_ADDRESS, macAddress);
    startActivity(intent);
  }

  private void onScanFailure(Throwable throwable) {

    if (throwable instanceof BleScanException) {
      handleBleScanException((BleScanException) throwable);
    }
  }

  private void dispose() {
    scanDisposable = null;
    // resultsAdapter.clearScanResults();
    updateButtonUIState();
  }

  private void updateButtonUIState() {
    scanToggleButton.setText(isScanning() ? R.string.stop_scan : R.string.start_scan);
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == REQUEST_ENABLE_BT && resultCode != Activity.RESULT_OK) {
      requestBT();
      return;
    }
    super.onActivityResult(requestCode, resultCode, data);
  }

  private void requestBT() {
    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
  }
}
