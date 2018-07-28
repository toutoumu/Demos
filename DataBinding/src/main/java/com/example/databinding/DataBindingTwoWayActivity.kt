package com.example.databinding

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.example.databinding.databinding.ActivityDataBindingTwoWayBinding

class DataBindingTwoWayActivity : AppCompatActivity() {

  private lateinit var bind: ActivityDataBindingTwoWayBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    bind = DataBindingUtil.setContentView<ActivityDataBindingTwoWayBinding>(this, R.layout.activity_data_binding_two_way)
        .apply {
          handles = this@DataBindingTwoWayActivity
          user = ObservableUser().apply {
            firstName = "firstName"
            lastName = "lastName"
            isAdult = false
            padding = 100
          }
        }
  }

  fun onChangeAdult(view: View) {
    bind.user?.isAdult = !bind.user?.isAdult!!
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
