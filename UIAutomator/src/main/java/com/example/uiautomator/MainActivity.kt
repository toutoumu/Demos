package com.example.uiautomator

import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.runBtn
import kotlinx.android.synthetic.main.activity_main.shutdown
import kotlinx.android.synthetic.main.activity_main.times_aiqiyi
import kotlinx.android.synthetic.main.activity_main.times_dongfangtoutiao
import kotlinx.android.synthetic.main.activity_main.times_jinritoutiao
import kotlinx.android.synthetic.main.activity_main.times_jukandian
import kotlinx.android.synthetic.main.activity_main.times_qutoutiao
import java.io.File
import java.io.FileOutputStream
import java.util.Arrays
import java.util.Collections

class MainActivity : AppCompatActivity() {

  var aiqiyi = 0
  var jinritoutiao = 0
  var dongfangtoutiao = 0
  var jukandian = 0
  var qutoutiao = 0

  companion object {
    private val TAG = MainActivity::class.java.name!!
    const val PACKAGE = "com.example.uiautomator"
    const val CLAZS_NAME = "AppTest"
    const val METHOD = "allTest"
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    // 点击选中所有文本
    times_aiqiyi.setOnClickListener { times_aiqiyi.selectAll() }
    times_jinritoutiao.setOnClickListener { times_jinritoutiao.selectAll() }
    times_dongfangtoutiao.setOnClickListener { times_dongfangtoutiao.selectAll() }
    times_qutoutiao.setOnClickListener { times_qutoutiao.selectAll() }
    times_jukandian.setOnClickListener { times_jukandian.selectAll() }

    // 开始执行
    runBtn.setOnClickListener {
      if (ShellUtils.checkRootPermission()) {
        /*Toast.makeText(this, "拥有Root权限", Toast.LENGTH_SHORT)
            .show()*/
        Log.e(TAG, "拥有Root权限")
        val directory = Environment.getExternalStorageDirectory()
        val file = File(directory, "shutdown.txt")
        if (file.exists()) {
          file.delete()
        }
        this.runMyUiautomator()
      } else {
        Log.e(TAG, "没有有Root权限")
        /*Toast.makeText(this, "没有有Root权限", Toast.LENGTH_LONG)
            .show()*/
      }
    }

    // 生成一个文件用于停止操作
    shutdown.setOnClickListener {
      val directory = Environment.getExternalStorageDirectory()
      val file = File(directory, "shutdown.txt")
      if (!file.exists()) {
        val outputStream = FileOutputStream(file)
        outputStream.write(12)
        outputStream.flush()
        outputStream.close()
      }
    }
  }

  /**
   * 点击按钮对应的方法
   *
   */
  private fun runMyUiautomator() {
    Log.i(TAG, "runMyUiautomator: ")
    aiqiyi = times_aiqiyi.text.toString()
        .toInt()
    jinritoutiao = times_jinritoutiao.text.toString()
        .toInt()
    dongfangtoutiao = times_dongfangtoutiao.text.toString()
        .toInt()
    jukandian = times_jukandian.text.toString()
        .toInt()
    qutoutiao = times_qutoutiao.text.toString()
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
      param["jinritoutiao"] = jinritoutiao
      param["dongfangtoutiao"] = dongfangtoutiao
      param["jukandian"] = jukandian
      param["qutoutiao"] = qutoutiao

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
}
