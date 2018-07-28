package com.example.databinding

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.example.databinding.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        .apply {
          handlers = this@MainActivity
        }
  }

  fun onClickPara(para: Int) {
    Toast.makeText(this, para.toString(), Toast.LENGTH_SHORT)
        .show()
  }

  fun onClickOneWay(view: View) {
    startActivity(Intent(this, DataBindingOneWayActivity::class.java))
  }

  fun onClickTwoWay(view: View) {
    startActivity(Intent(this, DataBindingTwoWayActivity::class.java))
  }

  fun onClickCustomBinding(view: View) {
    startActivity(Intent(this, CustomBindingAdapterActivity::class.java))
  }

  fun onClickObservableFiled(view: View) {
    startActivity(Intent(this, ObservableFiledActivity::class.java))
  }

  fun onClickRecycler(view: View) {
    startActivity(Intent(this, AdapterBindingActivity::class.java))
  }
}
