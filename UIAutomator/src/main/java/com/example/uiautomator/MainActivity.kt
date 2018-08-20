package com.example.uiautomator

import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.delete_aiqiyi
import kotlinx.android.synthetic.main.activity_main.delete_dongfangtoutiao
import kotlinx.android.synthetic.main.activity_main.delete_haokan
import kotlinx.android.synthetic.main.activity_main.delete_jinritoutiao
import kotlinx.android.synthetic.main.activity_main.delete_jukandian
import kotlinx.android.synthetic.main.activity_main.delete_qutoutiao
import kotlinx.android.synthetic.main.activity_main.delete_repeat
import kotlinx.android.synthetic.main.activity_main.runBtn
import kotlinx.android.synthetic.main.activity_main.shutdown
import kotlinx.android.synthetic.main.activity_main.times_aiqiyi
import kotlinx.android.synthetic.main.activity_main.times_dongfangtoutiao
import kotlinx.android.synthetic.main.activity_main.times_haokan
import kotlinx.android.synthetic.main.activity_main.times_jinritoutiao
import kotlinx.android.synthetic.main.activity_main.times_jukandian
import kotlinx.android.synthetic.main.activity_main.times_qutoutiao
import kotlinx.android.synthetic.main.activity_main.times_repeat
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {

  var aiqiyi = 0
  var haokan = 0
  var jinritoutiao = 0
  var dongfangtoutiao = 0
  var jukandian = 0
  var qutoutiao = 0

  var repeat = 0

  companion object {
    private val TAG = MainActivity::class.java.name!!
    const val PACKAGE = "com.example.uiautomator"
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
    times_qutoutiao.onFocusChangeListener = listener
    times_jukandian.onFocusChangeListener = listener
    times_repeat.onFocusChangeListener = listener

    delete_aiqiyi.setOnClickListener { times_aiqiyi.setText("0") }
    delete_haokan.setOnClickListener { times_haokan.setText("0") }
    delete_jinritoutiao.setOnClickListener { times_jinritoutiao.setText("0") }
    delete_dongfangtoutiao.setOnClickListener { times_dongfangtoutiao.setText("0") }
    delete_qutoutiao.setOnClickListener { times_qutoutiao.setText("0") }
    delete_repeat.setOnClickListener { times_repeat.setText("1") }
    delete_jukandian.setOnClickListener { times_jukandian.setText("0") }

    // 开始执行
    runBtn.setOnClickListener {
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

    // 生成一个文件用于停止操作
    shutdown.setOnClickListener {
      val directory = File(Environment.getExternalStorageDirectory(), File.separator + "aaaaaa" + File.separator)
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
    qutoutiao = times_qutoutiao.text.toString()
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
      param["qutoutiao"] = qutoutiao
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
}
