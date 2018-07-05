package com.example.livedata

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast

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
  }

  private fun AppCompatActivity.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT)
        .show()
  }
}
