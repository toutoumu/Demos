package com.example.animation;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.widget.TextView;

/**
 * 代码来源于
 * http://blog.csdn.net/u011394071/article/details/53198672
 */
public class MainActivity extends AppCompatActivity {

  private static final int NETWORKTYPE_WIFI = 0;
  private static final int NETWORKTYPE_4G = 1;
  private static final int NETWORKTYPE_2G = 2;
  private static final int NETWORKTYPE_NONE = 3;
  public TextView mTextView;
  public TelephonyManager mTelephonyManager;
  public PhoneStatListener mListener;

  /**
   * 网络信号强度监听
   *
   * @param savedInstanceState
   */
  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    mTextView = (TextView) findViewById(R.id.text);
    //获取telephonyManager
    mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
    //开始监听
    mListener = new PhoneStatListener();
    //监听信号强度
    mTelephonyManager.listen(mListener, PhoneStatListener.LISTEN_SIGNAL_STRENGTHS);
  }

  @Override protected void onResume() {
    super.onResume();
    mTelephonyManager.listen(mListener, PhoneStatListener.LISTEN_SIGNAL_STRENGTHS);
  }

  @Override protected void onPause() {
    super.onPause();
    //用户不在当前页面时，停止监听
    mTelephonyManager.listen(mListener, PhoneStatListener.LISTEN_NONE);
  }

  private class PhoneStatListener extends PhoneStateListener {
    //获取信号强度

    @Override public void onSignalStrengthsChanged(SignalStrength signalStrength) {
      super.onSignalStrengthsChanged(signalStrength);
      //获取网络信号强度
      //获取0-4的5种信号级别，越大信号越好,但是api23开始才能用
      //            int level = signalStrength.getLevel();
      int gsmSignalStrength = signalStrength.getGsmSignalStrength();
      //获取网络类型
      int netWorkType = getNetWorkType(MainActivity.this);
      switch (netWorkType) {
        case NETWORKTYPE_WIFI:
          mTextView.setText("当前网络为wifi,信号强度为：" + gsmSignalStrength);
          break;
        case NETWORKTYPE_2G:
          mTextView.setText("当前网络为2G移动网络,信号强度为：" + gsmSignalStrength);
          break;
        case NETWORKTYPE_4G:
          mTextView.setText("当前网络为4G移动网络,信号强度为：" + gsmSignalStrength);
          break;
        case NETWORKTYPE_NONE:
          mTextView.setText("当前没有网络,信号强度为：" + gsmSignalStrength);
          break;
        case -1:
          mTextView.setText("当前网络错误,信号强度为：" + gsmSignalStrength);
          break;
      }
    }
  }

  public static int getNetWorkType(Context context) {
    int mNetWorkType = -1;
    ConnectivityManager manager =
        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo networkInfo = manager.getActiveNetworkInfo();
    if (networkInfo != null && networkInfo.isConnected()) {
      String type = networkInfo.getTypeName();
      if (type.equalsIgnoreCase("WIFI")) {
        mNetWorkType = NETWORKTYPE_WIFI;
      } else if (type.equalsIgnoreCase("MOBILE")) {
        return isFastMobileNetwork(context) ? NETWORKTYPE_4G : NETWORKTYPE_2G;
      }
    } else {
      mNetWorkType = NETWORKTYPE_NONE;//没有网络
    }
    return mNetWorkType;
  }

  /** 判断网络类型 */
  private static boolean isFastMobileNetwork(Context context) {
    TelephonyManager telephonyManager =
        (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    if (telephonyManager.getNetworkType() == TelephonyManager.NETWORK_TYPE_LTE) {
      //这里只简单区分两种类型网络，认为4G网络为快速，但最终还需要参考信号值
      return true;
    }
    return false;
  }
}