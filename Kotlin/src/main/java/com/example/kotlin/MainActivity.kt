package com.example.kotlin

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

  private var age: Int? = null
  private var mText: TextView? = null;
  private val mData: List<String> = ArrayList();

  // 扩展方法
  fun Activity.toast(message: String? = "") {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
  }

  // 类似单例模式
  object Status {
    private val success: Int = 1
    public fun say(): Int {
      System.out.println(success)
      return success
    }
  }

  //常量定义
  private val PAGE_SIZE = 20

  companion object {
    private val ITEM_TYPE_DATA = 1
    private val ITEM_TYPE_HEADER = 2
    private val ITEM_TYPE_FOOTER = 3
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    mData.forEach { s ->
      toast(s)
    }

    for (s: String in mData) {
      toast(s)
    }

    // 常量使用
    Companion.ITEM_TYPE_DATA

    // 点击事件
    text?.setOnClickListener {
      var age: Int = 23
      var name: String = "name"
    }

    text.setOnClickListener({
      toast("hahah")
      vars(1, 2, 3, 4, 5)  // 输出12345
    })

    text?.setOnClickListener(View.OnClickListener { v ->
      (v as TextView).text = "hah"

    })

    text.setOnClickListener({ v ->
      (v as TextView).text = "点击了哦"
      toast("hahah")
    });

    text.setOnClickListener(object : View.OnClickListener {

      internal var age = 23

      private fun getAge() {
        return
      }

      override fun onClick(v: View?) {
        (v as TextView).text = "点击了哦"
        toast("hahah")
      }

    })

    val p: Person = Person(12, "liubin")
    p.setAddress("dizhi")
    toast(p.getMessage())
    toast(p.getAddress())
  }

  // 可变参数
  fun vars(vararg v: Int) {
    for (vt in v) {
      print(vt)
    }
  }


}
