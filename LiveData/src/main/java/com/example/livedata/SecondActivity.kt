package com.example.livedata

import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View

/**
 * 演示同一个Activity里面多个Fragment数据同步
 */
class SecondActivity : AppCompatActivity() {
  private lateinit var mNameViewModel: NameViewModel
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_second)

    supportFragmentManager.beginTransaction()
        .add(R.id.container, FirstFragment.getInstance())
        .add(R.id.container1, SecondFragment.getInstance())
        .commit()

    mNameViewModel = ViewModelProviders.of(this)
        .get(NameViewModel::class.java)
    findViewById<View>(R.id.change).setOnClickListener {
      mNameViewModel.currentName.postValue("SecondActivity中设置Name")

    }
  }
}
