package com.example.demo

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.delete_aiqiyi
import kotlinx.android.synthetic.main.activity_main.delete_dongfangtoutiao
import kotlinx.android.synthetic.main.activity_main.delete_hahashipin
import kotlinx.android.synthetic.main.activity_main.delete_haokan
import kotlinx.android.synthetic.main.activity_main.delete_jinritoutiao
import kotlinx.android.synthetic.main.activity_main.delete_jukandian
//import kotlinx.android.synthetic.main.activity_main.delete_qutoutiao
import kotlinx.android.synthetic.main.activity_main.delete_repeat
import kotlinx.android.synthetic.main.activity_main.delete_zhongqingkandian
import kotlinx.android.synthetic.main.activity_main.runBtn
import kotlinx.android.synthetic.main.activity_main.shutdown
import kotlinx.android.synthetic.main.activity_main.times_aiqiyi
import kotlinx.android.synthetic.main.activity_main.times_dongfangtoutiao
import kotlinx.android.synthetic.main.activity_main.times_hahashipin
import kotlinx.android.synthetic.main.activity_main.times_haokan
import kotlinx.android.synthetic.main.activity_main.times_jinritoutiao
import kotlinx.android.synthetic.main.activity_main.times_jukandian
//import kotlinx.android.synthetic.main.activity_main.times_qutoutiao
import kotlinx.android.synthetic.main.activity_main.times_repeat
import kotlinx.android.synthetic.main.activity_main.times_zhongqingkandian
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {

  var aiqiyi = 0
  var haokan = 0
  var jinritoutiao = 0
  var dongfangtoutiao = 0
  var jukandian = 0
//  var qutoutiao = 0
  var zhongqingkandian = 0
  var hahashipin = 0

  var repeat = 0

  companion object {
    var REQUEST_PERMISSION_CAMERA_CODE = 233
    private val TAG = MainActivity::class.java.name!!
    const val PACKAGE = "com.example.demo"
    const val CLAZS_NAME = "AppTest"
    const val METHOD = "testAllDevice"
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    // 点击选中所有文本
    val listener = View.OnFocusChangeListener { it: View?, b: Boolean ->
      if (!b) return@OnFocusChangeListener
      it as EditText
      if (it.text.isNotEmpty()) {
        it.setSelection(it.text.toString().length)
      }
      it.selectAll()
    }
    times_aiqiyi.onFocusChangeListener = listener
    times_haokan.onFocusChangeListener = listener
    times_jinritoutiao.onFocusChangeListener = listener
    times_dongfangtoutiao.onFocusChangeListener = listener
//    times_qutoutiao.onFocusChangeListener = listener
    times_zhongqingkandian.onFocusChangeListener = listener
    times_hahashipin.onFocusChangeListener = listener
    times_jukandian.onFocusChangeListener = listener
    times_repeat.onFocusChangeListener = listener

    delete_aiqiyi.setOnClickListener { times_aiqiyi.setText("0") }
    delete_haokan.setOnClickListener { times_haokan.setText("0") }
    delete_jinritoutiao.setOnClickListener { times_jinritoutiao.setText("0") }
    delete_dongfangtoutiao.setOnClickListener { times_dongfangtoutiao.setText("0") }
//    delete_qutoutiao.setOnClickListener { times_qutoutiao.setText("0") }
    delete_zhongqingkandian.setOnClickListener { times_zhongqingkandian.setText("0") }
    delete_hahashipin.setOnClickListener { times_hahashipin.setText("0") }
    delete_repeat.setOnClickListener { times_repeat.setText("1") }
    delete_jukandian.setOnClickListener { times_jukandian.setText("0") }

    // 开始执行
    runBtn.setOnClickListener {
      //判断当前系统是否高于或等于6.0
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
       /* if (!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
          //如果用户勾选了不再提醒，则返回false
          //给予用户提醒，比如Toast或者对话框，让用户去系统设置-应用管理里把相关权限打开
          Toast.makeText(this, "勾选了不再提示", Toast.LENGTH_LONG)
              .show()
          return@setOnClickListener
        }*/

        //当前系统大于等于6.0
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
          //具有拍照权限，直接调用相机
          //具体调用代码
          doStart()
        } else {
          //不具有拍照权限，需要进行权限申请
          ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_PERMISSION_CAMERA_CODE)
        }
      } else {//当前系统小于6.0，直接调用拍照
        doStart()
      }
    }

    // 生成一个文件用于停止操作
    shutdown.setOnClickListener {
      //判断当前系统是否高于或等于6.0
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        /*if (!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
          //如果用户勾选了不再提醒，则返回false
          //给予用户提醒，比如Toast或者对话框，让用户去系统设置-应用管理里把相关权限打开
          Toast.makeText(this, "勾选了不再提示", Toast.LENGTH_LONG)
              .show()
          return@setOnClickListener
        }*/

        //当前系统大于等于6.0
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
          //具有拍照权限，直接调用相机
          //具体调用代码
          doEnd()
        } else {
          //不具有拍照权限，需要进行权限申请
          ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_PERMISSION_CAMERA_CODE)
        }
      } else { //当前系统小于6.0，直接调用拍照
        doEnd()
      }
    }
  }

  private fun doEnd() {
    val directory = File(Environment.getExternalStorageDirectory(), File.separator + "aaaaaa" + File.separator)
    if (!directory.exists()) {
      directory.mkdirs()
    }
    val file = File(directory, "shutdown.txt")
    if (!file.exists()) {
      val outputStream = FileOutputStream(file)
      outputStream.write(12)
      outputStream.flush()
      outputStream.close()
    }
  }

  private fun doStart() {
    if (ShellUtils.checkRootPermission()) {
      Log.e(TAG, "拥有Root权限")
      Toast.makeText(this, "拥有Root权限", Toast.LENGTH_SHORT)
          .show()
      // 删除停止标识文件,让程序顺利运行
      val directory = File(Environment.getExternalStorageDirectory(), File.separator + "aaaaaa" + File.separator)
      val file = File(directory, "shutdown.txt")
      if (file.exists()) {
        file.delete()
      }
      this.runMyUiautomator()
    } else {
      Log.e(TAG, "没有有Root权限")
      Toast.makeText(this, "没有有Root权限", Toast.LENGTH_LONG)
          .show()
    }
  }

  /**
   * 点击按钮对应的方法
   *
   */
  private fun runMyUiautomator() {
    aiqiyi = times_aiqiyi.text.toString()
        .toInt()
    haokan = times_haokan.text.toString()
        .toInt()
    jinritoutiao = times_jinritoutiao.text.toString()
        .toInt()
    dongfangtoutiao = times_dongfangtoutiao.text.toString()
        .toInt()
    jukandian = times_jukandian.text.toString()
        .toInt()
//    qutoutiao = times_qutoutiao.text.toString()
//        .toInt()
    zhongqingkandian = times_zhongqingkandian.text.toString()
        .toInt()
    hahashipin = times_hahashipin.text.toString()
        .toInt()
    repeat = times_repeat.text.toString()
        .toInt()
    UiautomatorThread().start()
  }

  /**
   * 运行uiautomator是个费时的操作，不应该放在主线程，因此另起一个线程运行
   */
  internal inner class UiautomatorThread : Thread() {
    override fun run() {
      // 传递的参数
      val param = mutableMapOf<String, Int>()
      param["aiqiyi"] = aiqiyi
      param["haokan"] = haokan
      param["jinritoutiao"] = jinritoutiao
      param["dongfangtoutiao"] = dongfangtoutiao
      param["jukandian"] = jukandian
//      param["qutoutiao"] = qutoutiao
      param["zhongqingkandian"] = zhongqingkandian
      param["hahashipin"] = hahashipin
      param["repeat"] = repeat

      // 组装成命令
      val command = generateCommand(PACKAGE, CLAZS_NAME, METHOD, param)
      val execCommand = ShellUtils.execCommand(command, true)
      Log.e(TAG, command)
      Log.e(TAG, execCommand.errorMsg + execCommand.successMsg + execCommand.result)
    }

    /**
     * 生成命令
     *
     * @param pkgName 包名
     * @param clsName 类名
     * @param mtdName 方法名
     * @return
     */
    private fun generateCommand(pkgName: String, clsName: String, mtdName: String, param: Map<String, Int>): String {
      val p = generateParameter(param)
      val command =
        "am instrument -w -r -e debug false $p -e class $pkgName.$clsName#$mtdName $pkgName.debug.test/android.support.test.runner.AndroidJUnitRunner"
      //"am instrument --user 0 -w -r -e debug false $p -e class $pkgName.$clsName#$mtdName $pkgName.debug.test/android.support.test.runner.AndroidJUnitRunner"
      Log.e("生成的命令: ", command)
      return command
    }

    /**
     * 生成参数
     */
    private fun generateParameter(param: Map<String, Int>): String {
      val stringBuilder = StringBuilder()
      for (entry in param) {
        stringBuilder.append(" -e ${entry.key} ${entry.value} ")
      }
      return stringBuilder.toString()
    }
  }

  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    if (requestCode == REQUEST_PERMISSION_CAMERA_CODE) {
      if (grantResults.isNotEmpty()) {
        val cameraResult = grantResults[0]//相机权限
        val cameraGranted = cameraResult == PackageManager.PERMISSION_GRANTED//拍照权限
        if (cameraGranted) {
          //具有拍照权限，调用相机
        } else {
          //不具有相关权限，给予用户提醒，比如Toast或者对话框，让用户去系统设置-应用管理里把相关权限开启
        }
      }
    }
  }
}
