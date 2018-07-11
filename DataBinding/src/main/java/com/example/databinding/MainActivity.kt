package com.example.databinding

import android.content.Intent
import android.content.res.Resources
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import com.example.databinding.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

    binding.user = ObservableUser()/*("abc", "ddd")*/.apply {
      firstName = "firstName"
      lastName = "lastName"
      isAdult = false
      padding = 100
    }

    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 33f, Resources.getSystem().displayMetrics)
    binding.handlers = MyHandlers()

    binding.ddd.setOnClickListener {
      binding.user!!.isAdult = true
      val intent = Intent(this, DynamicActivity::class.java)
      startActivity(intent)
    }
  }

  // todo DataBinding如何使用kotlin的静态方法
  companion object BindingFun {
    fun Converte(para: String?): String {
      if (para != null) {
        return para.toUpperCase()
      }
      return ""
    }
  }
}
