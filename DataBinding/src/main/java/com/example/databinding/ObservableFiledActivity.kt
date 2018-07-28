package com.example.databinding

import android.databinding.DataBindingUtil
import android.databinding.ObservableArrayMap
import android.databinding.ObservableField
import android.databinding.ObservableInt
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.databinding.databinding.ActivityObservableFiledBinding
import java.util.Random

/**
 * ObservableXXX 类型数据的使用
 */
class ObservableFiledActivity : AppCompatActivity() {
  private val observableInt = ObservableInt()
  private val observableField = ObservableField<String>()
  private val observableArrayMap: ObservableArrayMap<String, String> = ObservableArrayMap()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    DataBindingUtil.setContentView<ActivityObservableFiledBinding>(this, R.layout.activity_observable_filed)
        .apply {
          intField = observableInt
          stringField = observableField
          mapField = observableArrayMap
        }
  }

  fun onChange(view: View) {
    observableInt.set(Random().nextInt())
    observableField.set("observableField" + Random().nextInt())
    observableArrayMap["name"] = "observableArrayMap" + Random().nextInt()
  }
}
