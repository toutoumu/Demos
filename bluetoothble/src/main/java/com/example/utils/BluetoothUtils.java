package com.example.utils;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import java.util.List;
import timber.log.Timber;

import static android.bluetooth.BluetoothGattCharacteristic.PROPERTY_BROADCAST;
import static android.bluetooth.BluetoothGattCharacteristic.PROPERTY_EXTENDED_PROPS;
import static android.bluetooth.BluetoothGattCharacteristic.PROPERTY_INDICATE;
import static android.bluetooth.BluetoothGattCharacteristic.PROPERTY_NOTIFY;
import static android.bluetooth.BluetoothGattCharacteristic.PROPERTY_READ;
import static android.bluetooth.BluetoothGattCharacteristic.PROPERTY_SIGNED_WRITE;
import static android.bluetooth.BluetoothGattCharacteristic.PROPERTY_WRITE;
import static android.bluetooth.BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE;

public class BluetoothUtils {
  public static boolean hasProperty(BluetoothGattCharacteristic characteristic, int property) {
    return characteristic != null && (characteristic.getProperties() & property) > 0;
  }

  /**
   * 是否可通知
   */
  private static boolean isCharacteristicNotifiable(BluetoothGattCharacteristic characteristic) {
    return hasProperty(characteristic, PROPERTY_NOTIFY);
  }

  /**
   * 是否可读
   */
  private static boolean isCharacteristicReadable(BluetoothGattCharacteristic characteristic) {
    return hasProperty(characteristic, PROPERTY_READ);
  }

  /**
   * 是否可写(带响应,不带响应)
   */
  private static boolean isCharacteristicWriteable(BluetoothGattCharacteristic characteristic) {
    return hasProperty(characteristic, PROPERTY_WRITE | PROPERTY_WRITE_NO_RESPONSE);
  }

  /**
   * 获取带通知,读属性的特征值
   */
  private static BluetoothGattCharacteristic getReadNotifyCharacteristic(List<BluetoothGattService> services) {
    for (BluetoothGattService service : services) {
      for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
        if (isCharacteristicReadable(characteristic) && isCharacteristicNotifiable(characteristic)) {
          return characteristic;
        }
      }
    }
    return null;
  }

  /**
   * 获取带通知,写属性的特征值
   */
  private static BluetoothGattCharacteristic getWriteNotifyCharacteristic(List<BluetoothGattService> services) {
    for (BluetoothGattService service : services) {
      for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
        if (isCharacteristicWriteable(characteristic) && isCharacteristicNotifiable(characteristic)) {
          return characteristic;
        }
      }
    }
    return null;
  }

  /**
   * 获取带写属性的特征值(不带通知属性)
   */
  public static BluetoothGattCharacteristic getWriteCharacteristic(List<BluetoothGattService> services) {
    for (BluetoothGattService service : services) {
      for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
        if (isCharacteristicWriteable(characteristic) && !isCharacteristicNotifiable(characteristic)) {
          return characteristic;
        }
      }
    }
    return null;
  }

  /**
   * 获取带读属性的特征值(不带通知属性)
   */
  public static BluetoothGattCharacteristic getReadCharacteristic(List<BluetoothGattService> services) {
    for (BluetoothGattService service : services) {
      for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
        if (isCharacteristicReadable(characteristic) && !isCharacteristicNotifiable(characteristic)) {
          return characteristic;
        }
      }
    }
    return null;
  }

  /**
   * 获取带通知属性的特征值
   */
  public static BluetoothGattCharacteristic getNotifyCharacteristic(List<BluetoothGattService> services) {
    BluetoothGattCharacteristic temp = null;
    for (BluetoothGattService service : services) {
      for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
        if (isCharacteristicNotifiable(
          characteristic) /*&& characteristic.getUuid().toString().equals("0000fff1-0000-1000-8000-00805f9b34fb")*/) {
          temp = characteristic;
        }
      }
    }
    return temp;
  }

  /**
   * 打印服务以及对应的特征值拥有的属性
   *
   * @param services {@link List<BluetoothGattService>}
   */
  public static void listService(List<BluetoothGattService> services) {
    for (BluetoothGattService service : services) {
      Timber.e("####服务UUID : %s", service.getUuid());
      for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
        String message = "";
        boolean b;
        b = hasProperty(characteristic, PROPERTY_BROADCAST);
        if (b) message += " PROPERTY_BROADCAST";
        b = hasProperty(characteristic, PROPERTY_READ);
        if (b) message += " PROPERTY_READ";
        b = hasProperty(characteristic, PROPERTY_WRITE);
        if (b) message += " PROPERTY_WRITE";
        b = hasProperty(characteristic, PROPERTY_NOTIFY);
        if (b) message += " PROPERTY_NOTIFY";
        b = hasProperty(characteristic, PROPERTY_WRITE_NO_RESPONSE);
        if (b) message += " PROPERTY_WRITE_NO_RESPONSE";
        b = hasProperty(characteristic, PROPERTY_INDICATE);
        if (b) message += " PROPERTY_INDICATE";
        b = hasProperty(characteristic, PROPERTY_SIGNED_WRITE);
        if (b) message += " PROPERTY_SIGNED_WRITE";
        b = hasProperty(characteristic, PROPERTY_EXTENDED_PROPS);
        if (b) message += " PROPERTY_EXTENDED_PROPS";
        Timber.e("        特征值UUID : %s, %s", characteristic.getUuid(), message);
      }
    }
  }
}