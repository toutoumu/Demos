package com.example.phone.state;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import timber.log.Timber;

public class PhoneStateActivity extends AppCompatActivity {
  public static final int NP_CELL_INFO_UPDATE = 1001;
  public Handler mMainHandler;
  // for current
  public PhoneGeneralInfo phoneGeneralInfo;
  public CellGeneralInfo serverCellInfo;
  TelephonyManager phoneManager;
  private PhoneInfoThread phoneInfoThread;
  //for history
  private List<CellGeneralInfo> HistoryServerCellList;
  private MyPhoneStateListener myPhoneStateListener;

  void InitProcessThread() {
    mMainHandler = new Handler() {
      @Override public void handleMessage(Message msg) {
        if (msg.what == NP_CELL_INFO_UPDATE) {
          Timber.e("DeviceId:" + phoneGeneralInfo.deviceId);
          Timber.e("RatType:" + phoneGeneralInfo.ratType);
          Timber.e("Mnc:" + phoneGeneralInfo.mnc);
          Timber.e("Mcc:" + phoneGeneralInfo.mcc);
          Timber.e("Operator:" + phoneGeneralInfo.operaterName);
          Timber.e("Sdk:" + phoneGeneralInfo.sdk);
          Timber.e("Imsi:" + phoneGeneralInfo.Imsi);
          Timber.e("SN:" + phoneGeneralInfo.serialNumber);
          Timber.e("Model:" + phoneGeneralInfo.phoneModel);
          Timber.e("Version:" + phoneGeneralInfo.deviceSoftwareVersion);
          Timber.e("History cells list(" + HistoryServerCellList.size() + ")");
        }
        super.handleMessage(msg);
      }
    };

    phoneInfoThread = new PhoneInfoThread(PhoneStateActivity.this);
    phoneInfoThread.start();
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    serverCellInfo = new CellGeneralInfo();
    phoneGeneralInfo = new PhoneGeneralInfo();
    myPhoneStateListener = new MyPhoneStateListener();
    phoneManager = (TelephonyManager) this.getSystemService(this.TELEPHONY_SERVICE);
    phoneManager.listen(myPhoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    //
    HistoryServerCellList = new ArrayList<CellGeneralInfo>();
    LinearLayoutManager historylayoutManager = new LinearLayoutManager(this);
    historylayoutManager.setOrientation(OrientationHelper.VERTICAL);

    InitProcessThread();
  }

  public void updateServerCellView() {
    Timber.e("Rat:" + serverCellInfo.type);

    Timber.e("Tac:" + serverCellInfo.tac);

    Timber.e("Ci:" + serverCellInfo.CId);

    Timber.e("Pci:" + serverCellInfo.pci);

    Timber.e("Rsrp:" + serverCellInfo.rsrp);

    Timber.e("Rsrq:" + serverCellInfo.rsrq);

    Timber.e("Sinr:" + serverCellInfo.sinr);

    Timber.e("cqi:" + serverCellInfo.cqi);

    Timber.e("type:" + serverCellInfo.getInfoType);
  }

  class PhoneGeneralInfo {
    public String serialNumber;
    public String operaterName;
    public String operaterId;
    public String deviceId;
    public String deviceSoftwareVersion;
    public String Imsi;
    public String Imei;
    public int mnc;
    public int mcc;
    public int ratType = TelephonyManager.NETWORK_TYPE_UNKNOWN;
    public int phoneDatastate;
    public String phoneModel;
    public int sdk;
  }

  class CellGeneralInfo implements Cloneable {
    public int type;
    public int CId;
    public int lac;
    public int tac;
    public int psc;
    public int pci;
    public int RatType = TelephonyManager.NETWORK_TYPE_UNKNOWN;
    public int rsrp;
    public int rsrq;
    public int sinr;
    public int rssi;
    public int cqi;
    public int asulevel;
    public int getInfoType;
    public String time;

    @Override

    public Object clone() {
      CellGeneralInfo cellinfo = null;
      try {
        cellinfo = (CellGeneralInfo) super.clone();
      } catch (CloneNotSupportedException e) {
        e.printStackTrace();
      }
      return cellinfo;
    }
  }

  class MyPhoneStateListener extends PhoneStateListener {
    @Override public void onSignalStrengthsChanged(SignalStrength signalStrength) {
      super.onSignalStrengthsChanged(signalStrength);
      getPhoneGeneralInfo();
      getServerCellInfo();
      if (phoneGeneralInfo.ratType == TelephonyManager.NETWORK_TYPE_LTE) {
        try {
          serverCellInfo.rssi = (Integer) signalStrength.getClass()
              .getMethod("getLteSignalStrength")
              .invoke(signalStrength);
          serverCellInfo.rsrp =
              (Integer) signalStrength.getClass().getMethod("getLteRsrp").invoke(signalStrength);
          serverCellInfo.rsrq =
              (Integer) signalStrength.getClass().getMethod("getLteRsrq").invoke(signalStrength);
          serverCellInfo.sinr =
              (Integer) signalStrength.getClass().getMethod("getLteRssnr").invoke(signalStrength);
          serverCellInfo.cqi =
              (Integer) signalStrength.getClass().getMethod("getLteCqi").invoke(signalStrength);
        } catch (Exception e) {
          e.printStackTrace();
          return;
        }
      } else if (phoneGeneralInfo.ratType == TelephonyManager.NETWORK_TYPE_GSM) {
        try {
          serverCellInfo.rssi = signalStrength.getGsmSignalStrength();
          serverCellInfo.rsrp =
              (Integer) signalStrength.getClass().getMethod("getGsmDbm").invoke(signalStrength);
          serverCellInfo.asulevel =
              (Integer) signalStrength.getClass().getMethod("getAsuLevel").invoke(signalStrength);
        } catch (Exception e) {
          e.printStackTrace();
          return;
        }
      } else if (phoneGeneralInfo.ratType == TelephonyManager.NETWORK_TYPE_TD_SCDMA) {
        try {
          serverCellInfo.rssi = (Integer) signalStrength.getClass()
              .getMethod("getTdScdmaLevel")
              .invoke(signalStrength);
          serverCellInfo.rsrp =
              (Integer) signalStrength.getClass().getMethod("getTdScdmaDbm").invoke(signalStrength);
          serverCellInfo.asulevel =
              (Integer) signalStrength.getClass().getMethod("getAsuLevel").invoke(signalStrength);
        } catch (Exception e) {
          e.printStackTrace();
          return;
        }
      }
      Date now = new Date();
      SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss");
      serverCellInfo.time = formatter.format(now);
      updateHistoryCellList(serverCellInfo);
      updateServerCellView();
    }

    public void getPhoneGeneralInfo() {
      phoneGeneralInfo.operaterName = phoneManager.getNetworkOperatorName();
      phoneGeneralInfo.operaterId = phoneManager.getNetworkOperator();
      phoneGeneralInfo.mnc = Integer.parseInt(phoneGeneralInfo.operaterId.substring(0, 3));
      phoneGeneralInfo.mcc = Integer.parseInt(phoneGeneralInfo.operaterId.substring(3));
      phoneGeneralInfo.phoneDatastate = phoneManager.getDataState();
      phoneGeneralInfo.deviceId = phoneManager.getDeviceId();
      phoneGeneralInfo.Imei = phoneManager.getSimSerialNumber();
      phoneGeneralInfo.Imsi = phoneManager.getSubscriberId();
      phoneGeneralInfo.serialNumber = phoneManager.getSimSerialNumber();
      phoneGeneralInfo.deviceSoftwareVersion = android.os.Build.VERSION.RELEASE;
      phoneGeneralInfo.phoneModel = android.os.Build.MODEL;
      phoneGeneralInfo.ratType = phoneManager.getNetworkType();
      phoneGeneralInfo.sdk = android.os.Build.VERSION.SDK_INT;
    }

    public void getServerCellInfo() {
      try {
        List<CellInfo> allCellinfo;
        allCellinfo = phoneManager.getAllCellInfo();
        if (allCellinfo != null) {
          CellInfo cellInfo = allCellinfo.get(0);
          serverCellInfo.getInfoType = 1;
          if (cellInfo instanceof CellInfoGsm) {
            CellInfoGsm cellInfoGsm = (CellInfoGsm) cellInfo;
            serverCellInfo.CId = cellInfoGsm.getCellIdentity().getCid();
            serverCellInfo.rsrp = cellInfoGsm.getCellSignalStrength().getDbm();
            serverCellInfo.asulevel = cellInfoGsm.getCellSignalStrength().getAsuLevel();
            serverCellInfo.lac = cellInfoGsm.getCellIdentity().getLac();
            serverCellInfo.RatType = TelephonyManager.NETWORK_TYPE_GSM;
          } else if (cellInfo instanceof CellInfoWcdma) {
            CellInfoWcdma cellInfoWcdma = (CellInfoWcdma) cellInfo;
            serverCellInfo.CId = cellInfoWcdma.getCellIdentity().getCid();
            serverCellInfo.psc = cellInfoWcdma.getCellIdentity().getPsc();
            serverCellInfo.lac = cellInfoWcdma.getCellIdentity().getLac();
            serverCellInfo.rsrp = cellInfoWcdma.getCellSignalStrength().getDbm();
            serverCellInfo.asulevel = cellInfoWcdma.getCellSignalStrength().getAsuLevel();
            serverCellInfo.RatType = TelephonyManager.NETWORK_TYPE_UMTS;
          } else if (cellInfo instanceof CellInfoLte) {
            CellInfoLte cellInfoLte = (CellInfoLte) cellInfo;
            serverCellInfo.CId = cellInfoLte.getCellIdentity().getCi();
            serverCellInfo.pci = cellInfoLte.getCellIdentity().getPci();
            serverCellInfo.tac = cellInfoLte.getCellIdentity().getTac();
            serverCellInfo.rsrp = cellInfoLte.getCellSignalStrength().getDbm();
            serverCellInfo.asulevel = cellInfoLte.getCellSignalStrength().getAsuLevel();
            serverCellInfo.RatType = TelephonyManager.NETWORK_TYPE_LTE;
          }
        } else
        //for older devices
        {
          getServerCellInfoOnOlderDevices();
        }
      } catch (Exception e) {
        getServerCellInfoOnOlderDevices();
      }
    }

    void getServerCellInfoOnOlderDevices() {
      GsmCellLocation location = (GsmCellLocation) phoneManager.getCellLocation();
      serverCellInfo.getInfoType = 0;
      serverCellInfo.CId = location.getCid();
      serverCellInfo.tac = location.getLac();
      serverCellInfo.psc = location.getPsc();
      serverCellInfo.type = phoneGeneralInfo.ratType;
    }

    void updateHistoryCellList(CellGeneralInfo serverinfo) {
      CellGeneralInfo newcellInfo = (CellGeneralInfo) serverinfo.clone();
      HistoryServerCellList.add(newcellInfo);
    }
  }

  class PhoneInfoThread extends Thread {
    public int timecount;
    private Context context;

    public PhoneInfoThread(Context context) {
      this.context = context;
      timecount = 0;
    }

    public void run() {
      while (true) {
        try {
          timecount++;
          Message message = new Message();
          message.what = NP_CELL_INFO_UPDATE;
          Bundle bundle = new Bundle();
          bundle.putString("UPDATE", "UPDATE_TIME");
          message.setData(bundle);
          mMainHandler.sendMessage(message);
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  }
}  