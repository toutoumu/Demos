package com.example.livedata

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast

/**
 * LiveData的优点
 * 没有内存泄漏：因为 Observer 被绑定到它们自己的 Lifecycle 对象上，所以，当它们的 Lifecycle 被销毁时，它们能自动的被清理。
 *             不会因为 activity 停止而崩溃：如果 Observer 的 Lifecycle 处于闲置状态（例如：activity 在后台时），它们不会收到变更事件。

 * 始终保持数据最新：如果 Lifecycle 重新启动（例如：activity 从后台返回到启动状态）将会收到最新的位置数据（除非还没有）。

 * 正确处理配置更改：如果 activity 或 fragment 由于配置更改（如：设备旋转）重新创建，将会立即收到最新的有效位置数据。

 * 资源共享：可以只保留一个 MyLocationListener 实例，只连接系统服务一次，并且能够正确的支持应用程序中的所有观察者。
 */
class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    supportFragmentManager.beginTransaction()
        .replace(R.id.container, LiveDataFragment.getInstance())
        .commit()

    // 位置改变监听
    LocationLiveData.get(this)
        .observe(this, Observer { toast("ddd") })

    // 添加监听
    lifecycle.addObserver(MyObserve())

    findViewById<View>(R.id.jump).setOnClickListener {
      startActivity(Intent().apply {
        setClass(applicationContext, SecondActivity::class.java)
      })
    }
  }

  private fun AppCompatActivity.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT)
        .show()
  }
}
