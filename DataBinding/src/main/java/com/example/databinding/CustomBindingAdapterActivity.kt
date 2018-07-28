package com.example.databinding

import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.databinding.databinding.ActivityCustomBindingAdapterBinding

/**
 * 自定义绑定具体怎么写可以参考 {@link ViewBindingAdapter}
 * https://blog.csdn.net/qq_22703355/article/details/80804660
 */
class CustomBindingAdapterActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    DataBindingUtil.setContentView<ActivityCustomBindingAdapterBinding>(this, R.layout.activity_custom_binding_adapter)
        .apply {
          user = User("http://idea.lanyus.com", "lastName", false)
        }
  }
}
