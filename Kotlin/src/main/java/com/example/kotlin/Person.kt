package com.example.kotlin

import java.io.Serializable

// 类似Java POJO
data class Person(var age: Int = 20, var name: String = "jack") : IPerson<String>, Serializable {

  private var address: String? = null

  // 实现接口
  override fun getMessage(): String {
    return "年龄 $age 姓名 $name"
  }

  fun getAddress(): String? {
    return address;
  }

  fun setAddress(address: String) {
    this.address = address;
  }

}
