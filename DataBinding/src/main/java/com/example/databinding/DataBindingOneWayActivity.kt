package com.example.databinding

import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.databinding.databinding.ActivityDataBindingOneWayBinding
import java.util.Random

class DataBindingOneWayActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val binding = DataBindingUtil.setContentView<ActivityDataBindingOneWayBinding>(this, R.layout.activity_data_binding_one_way)
        .apply {
          user = User("firstName", "lastName", Random().nextBoolean())
        }

    // 10秒后修改绑定的用户信息
    Handler().postDelayed({
      binding.user = User("aaa", "bbb", Random().nextBoolean())
    }, 10000)
  }
}
