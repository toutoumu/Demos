package com.example.animation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.TextView;

/**
 * http://blog.csdn.net/cdzz11/article/details/52197732
 */
public class MainActivity1 extends AppCompatActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    getCurrentNetDBM(this);
    findViewById(R.id.text).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        Intent intent = new Intent();
        intent.setClass(MainActivity1.this, VelocityCheckActivity.class);
        startActivity(intent);
      }
    });
  }

  /**
   * 得到当前的手机蜂窝网络信号强度
   * 获取LTE网络和3G/2G网络的信号强度的方式有一点不同，
   * LTE网络强度是通过解析字符串获取的，
   * 3G/2G网络信号强度是通过API接口函数完成的。
   * asu 与 dbm 之间的换算关系是 dbm=-113 + 2*asu
   */
  public void getCurrentNetDBM(Context context) {

    final TelephonyManager tm =
        (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    PhoneStateListener listener = new PhoneStateListener() {
      @Override public void onSignalStrengthsChanged(SignalStrength signalStrength) {
        super.onSignalStrengthsChanged(signalStrength);
        String signalInfo = signalStrength.toString();
        String[] params = signalInfo.split(" ");

        // TODO: 2017/11/22 首先判断是否为 wifi
        if (tm.getNetworkType() == TelephonyManager.NETWORK_TYPE_LTE) {
          //4G网络 最佳范围   >-90dBm 越大越好
          int Itedbm = Integer.parseInt(params[9]);
          setDBM(Itedbm + "");
        } else if (tm.getNetworkType() == TelephonyManager.NETWORK_TYPE_HSDPA
            || tm.getNetworkType() == TelephonyManager.NETWORK_TYPE_HSPA
            || tm.getNetworkType() == TelephonyManager.NETWORK_TYPE_HSUPA
            || tm.getNetworkType() == TelephonyManager.NETWORK_TYPE_UMTS) {
          //3G网络最佳范围  >-90dBm  越大越好  ps:中国移动3G获取不到  返回的无效dbm值是正数（85dbm）
          //在这个范围的已经确定是3G，但不同运营商的3G有不同的获取方法，故在此需做判断 判断运营商与网络类型的工具类在最下方
          String yys = new SIMCardInfo(getApplicationContext()).getProvidersName();//获取当前运营商
          if (yys == "中国移动") {
            setDBM(0 + "");//中国移动3G不可获取，故在此返回0
          } else if (yys == "中国联通") {
            int cdmaDbm = signalStrength.getCdmaDbm();
            setDBM(cdmaDbm + "");
          } else if (yys == "中国电信") {
            int evdoDbm = signalStrength.getEvdoDbm();
            setDBM(evdoDbm + "");
          }
        } else {
          //2G网络最佳范围>-90dBm 越大越好
          int asu = signalStrength.getGsmSignalStrength();
          int dbm = -113 + 2 * asu;
          setDBM(dbm + "");
        }
      }
    };
    //开始监听
    tm.listen(listener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
  }

  private void setDBM(String s) {
    TextView textView = findViewById(R.id.text);
    textView.setText(s);
  }
}
