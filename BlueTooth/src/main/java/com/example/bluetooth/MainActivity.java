package com.example.bluetooth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
  public static final int REQUEST_BT_ENABLE_CODE = 200;
  public static final String BT_UUID = "00001101-0000-1000-8000-00805F9B34FB";//uuid

  private BluetoothAdapter mBluetoothAdapter;//蓝牙适配器
  private BlueToothStateReceiver mReceiver;//广播接收器
  private ConnectThread mConnectThread; //客户端线程
  private AcceptThread mAcceptThread; //服务端线程

  private RecyclerView mRecyclerView;
  private RvAdapter mRvAdapter;

  private RecyclerView mMessageView;
  private static MsgAdapter mMessageAdapter;

  private EditText inputEt;

  private static Handler mHandler = new Handler() {
    @Override public void dispatchMessage(Message msg) {
      mMessageAdapter.addMessage((String) msg.obj);
    }
  };

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    //这是我是为了6.0以上的设备能搜索到结果，动态申请了位置权限。但是没有处理结果，因为我测试肯定点同意~- -
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      requestPermissions(new String[] {
          Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
      }, 1001);
    }
    initUI();
    registerRec();
  }

  private void initUI() {
    findViewById(R.id.open).setOnClickListener(this);
    findViewById(R.id.close).setOnClickListener(this);
    findViewById(R.id.start).setOnClickListener(this);
    findViewById(R.id.stop).setOnClickListener(this);
    findViewById(R.id.send).setOnClickListener(this);

    inputEt = findViewById(R.id.input);

    mRecyclerView = findViewById(R.id.devices);
    mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    mRvAdapter = new RvAdapter(this);
    mRecyclerView.setAdapter(mRvAdapter);
    mRvAdapter.setOnItemClickListener(new RvAdapter.OnItemClickListener() {
      @Override public void onClick(BluetoothDevice device) {
        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
      }
    });

    mMessageView = findViewById(R.id.msglist);
    mMessageView.setLayoutManager(new LinearLayoutManager(this));
    mMessageAdapter = new MsgAdapter(this);
    mMessageView.setAdapter(mMessageAdapter);
  }

  private void openBT() {
    if (mBluetoothAdapter == null) {
      mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }
    //1.设备不支持蓝牙，结束应用
    if (mBluetoothAdapter == null) {
      finish();
      return;
    }
    //2.判断蓝牙是否打开
    if (!mBluetoothAdapter.enable()) {
      //没打开请求打开
      Intent btEnable = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
      startActivityForResult(btEnable, REQUEST_BT_ENABLE_CODE);
    }
  }

  private void registerRec() {
    //3.注册蓝牙广播
    mReceiver = new BlueToothStateReceiver();
    IntentFilter filter = new IntentFilter();
    filter.addAction(BluetoothDevice.ACTION_FOUND);//搜多到蓝牙
    filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);//搜索结束
    registerReceiver(mReceiver, filter);
  }

  @Override protected void onDestroy() {
    if (mReceiver != null) {
      unregisterReceiver(mReceiver);
    }
    super.onDestroy();
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == REQUEST_BT_ENABLE_CODE) {
      if (resultCode == RESULT_OK) {
        //用户允许打开蓝牙
        mMessageAdapter.addMessage("用户同意打开蓝牙");
      } else if (resultCode == RESULT_CANCELED) {
        //用户取消打开蓝牙
        mMessageAdapter.addMessage("用户拒绝打开蓝牙");
      }
    }
    super.onActivityResult(requestCode, resultCode, data);
  }

  @Override public void onClick(View view) {
    switch (view.getId()) {
      case R.id.open:
        openBT();
        mMessageAdapter.addMessage("打开蓝牙");
        if (mAcceptThread == null && mBluetoothAdapter != null) {
          mAcceptThread = new AcceptThread();
          mAcceptThread.start();
          mMessageAdapter.addMessage("启动服务线程");
        }
        break;
      case R.id.close:
        mBluetoothAdapter.disable();
        break;
      case R.id.start:
        if (mBluetoothAdapter != null) {
          mRvAdapter.clearDevices();//开始搜索前清空上一次的列表
          mBluetoothAdapter.startDiscovery();
          mMessageAdapter.addMessage("开始搜索蓝牙");
        } else {
          openBT();
          if (mBluetoothAdapter != null) {
            mRvAdapter.clearDevices();//开始搜索前清空上一次的列表
            mBluetoothAdapter.startDiscovery();
            mMessageAdapter.addMessage("开始搜索蓝牙");
          }
        }
        break;
      case R.id.stop:
        if (mBluetoothAdapter != null && mBluetoothAdapter.isDiscovering()) {
          mBluetoothAdapter.cancelDiscovery();
        }
        break;
      case R.id.send:
        /**
         * 如何区分我是使用服务端的socket还是使用客户端的socket发送消息
         *  1. 在单一环境下，手机一般作为客户端，外围设备是服务器。所以手机完全可以不用创建服务器，不存在这个问题。
         *  2. 假如是两台手机用来聊天，可分别充当服务器和客户端，那就是发起连接方（即点击设备列表连接）作为客户端。
         *  3. 假如我链接了别人，另一个人又连接了我，那我怎么区分？那你写两个界面啊~你要回复其他客户端发来的消息就用服务器的socket
         *  否则就用客户端的。我这里偷懒了，一但我主动连接别人，相当于我就关闭服务端了，不给别人连我了。
         *  4. 那那些蓝牙对战游戏都怎么区分的？你发现蓝牙对战需要一个人先创建房间没？那个人就是服务端，其他都是客户端，没这个问题。
         */
        String msg = inputEt.getText().toString();
        if (TextUtils.isEmpty(msg)) {
          Toast.makeText(this, "消息为空", Toast.LENGTH_SHORT).show();
          return;
        }
        if (mConnectThread != null) {//证明我主动去链接别人了
          mConnectThread.write(msg);
        } else if (mAcceptThread != null) {
          mAcceptThread.write(msg);
        }
        mMessageAdapter.addMessage("发送消息：" + msg);
        break;
    }
  }

  class BlueToothStateReceiver extends BroadcastReceiver {
    @Override public void onReceive(Context context, Intent intent) {
      Toast.makeText(MainActivity.this, "触发广播", Toast.LENGTH_SHORT).show();
      String action = intent.getAction();
      switch (action) {
        case BluetoothDevice.ACTION_FOUND:
          BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
          Toast.makeText(MainActivity.this, "找到设备" + device.getName(), Toast.LENGTH_SHORT).show();
          if (mRvAdapter != null) {
            mRvAdapter.addDevice(device);
          }
          break;
        case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
          mMessageAdapter.addMessage("搜索结束");
          break;
      }
    }
  }

  class ConnectThread extends Thread {
    private BluetoothDevice mDevice;
    private BluetoothSocket mSocket;
    private InputStream btIs;
    private OutputStream btOs;
    private boolean canRecv;
    private PrintWriter writer;

    public ConnectThread(BluetoothDevice device) {
      mDevice = device;
      canRecv = true;
    }

    @Override public void run() {
      if (mDevice != null) {
        try {
          //获取套接字
          BluetoothSocket temp =
              mDevice.createInsecureRfcommSocketToServiceRecord(UUID.fromString(BT_UUID));
          //mDevice.createRfcommSocketToServiceRecord(UUID.fromString(BT_UUID));//sdk 2.3以下使用
          mSocket = temp;
          //发起连接请求
          if (mSocket != null) {
            mSocket.connect();
          }
          sendHandlerMsg("连接 " + mDevice.getName() + "成功！");
          //获取输入输出流
          btIs = mSocket.getInputStream();
          btOs = mSocket.getOutputStream();

          //通讯-接收消息
          BufferedReader reader = new BufferedReader(new InputStreamReader(btIs, "UTF-8"));
          String content = null;
          while (canRecv) {
            content = reader.readLine();
            sendHandlerMsg("收到消息：" + content);
          }
        } catch (IOException e) {
          e.printStackTrace();
          sendHandlerMsg("错误：" + e.getMessage());
        } finally {
          try {
            if (mSocket != null) {
              mSocket.close();
            }
            //btIs.close();//两个输出流都依赖socket，关闭socket即可
            //btOs.close();
          } catch (IOException e) {
            e.printStackTrace();
            sendHandlerMsg("错误：" + e.getMessage());
          }
        }
      }
    }

    private void sendHandlerMsg(String content) {
      Message msg = mHandler.obtainMessage();
      msg.what = 1001;
      msg.obj = content;
      mHandler.sendMessage(msg);
    }

    public void write(String msg) {
      if (btOs != null) {
        try {
          if (writer == null) {
            writer = new PrintWriter(new OutputStreamWriter(btOs, "UTF-8"), true);
          }
          writer.println(msg);
        } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
          writer.close();
          sendHandlerMsg("错误：" + e.getMessage());
        }
      }
    }
  }

  class AcceptThread extends Thread {
    private BluetoothServerSocket mServerSocket;
    private BluetoothSocket mSocket;
    private InputStream btIs;
    private OutputStream btOs;
    private PrintWriter writer;
    private boolean canAccept;
    private boolean canRecv;

    public AcceptThread() {
      canAccept = true;
      canRecv = true;
    }

    @Override public void run() {
      try {
        //获取套接字
        BluetoothServerSocket temp =
            mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord("TEST",
                UUID.fromString(BT_UUID));
        mServerSocket = temp;
        //监听连接请求 -- 作为测试，只允许连接一个设备
        if (mServerSocket != null) {
          // while (canAccept) {
          mSocket = mServerSocket.accept();
          sendHandlerMsg("有客户端连接");
          // }
        }
        //获取输入输出流
        btIs = mSocket.getInputStream();
        btOs = mSocket.getOutputStream();
        //通讯-接收消息
        BufferedReader reader = new BufferedReader(new InputStreamReader(btIs, "UTF-8"));
        String content = null;
        while (canRecv) {
          content = reader.readLine();
          sendHandlerMsg("收到消息：" + content);
        }
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        try {
          if (mSocket != null) {
            mSocket.close();
          }
          // btIs.close();//两个输出流都依赖socket，关闭socket即可
          // btOs.close();
        } catch (IOException e) {
          e.printStackTrace();
          sendHandlerMsg("错误：" + e.getMessage());
        }
      }
    }

    private void sendHandlerMsg(String content) {
      Message msg = mHandler.obtainMessage();
      msg.what = 1001;
      msg.obj = content;
      mHandler.sendMessage(msg);
    }

    public void write(String msg) {
      if (btOs != null) {
        try {
          if (writer == null) {
            writer = new PrintWriter(new OutputStreamWriter(btOs, "UTF-8"), true);
          }
          writer.println(msg);
        } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
          writer.close();
          sendHandlerMsg("错误：" + e.getMessage());
        }
      }
    }
  }
}
