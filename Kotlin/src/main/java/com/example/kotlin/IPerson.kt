package com.example.kotlin

// 接口定义
interface IPerson<out T> {
  fun getMessage(): T

}