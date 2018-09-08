package uiautomatorcase.example.apple.test

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.canvas
import kotlinx.android.synthetic.main.activity_main.checkAPP
import kotlinx.android.synthetic.main.activity_main.checkAPP1
import kotlinx.android.synthetic.main.activity_main.message

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)



    checkAPP.setOnClickListener {
      if (isAvilible(this, "com.lswl.qfq")) {
        message.text = "安装了HAHA视频"
      } else {
        message.text = "没有HAHA视频"
      }
    }

    checkAPP1.setOnClickListener {
      if (isAvilible(this, "com.jifen.qukan")) {
        message.text = "安装了趣头条"
      } else {
        message.text = "没有趣头条"
      }
    }
  }

  /**
   * 检查手机上是否安装了指定的软件
   * @param context
   * @param packageName
   * @return
   */
  fun isAvilible(context: Context, packageName: String): Boolean {
    val packageManager = context.packageManager
    val packageInfos = packageManager.getInstalledPackages(0)
    val packageNames = ArrayList<String>()

    if (packageInfos != null) {
      for (i in packageInfos!!.indices) {
        val packName = packageInfos!!.get(i)
            .packageName
        packageNames.add(packName)
      }
    }
    // 判断packageNames中是否有目标程序的包名，有TRUE，没有FALSE
    return packageNames.contains(packageName)
  }
}
