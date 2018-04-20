package com.example.bluetoothble

import com.example.bluetoothble.MessageUtils.CMD_CONTROL
import com.example.bluetoothble.MessageUtils.CMD_HAND
import com.example.bluetoothble.MessageUtils.intToBytes
import com.example.bluetoothble.MessageUtils.makeMessage
import com.example.bluetoothble.MessageUtils.strToByteArray
import com.example.utils.Strings

object MessageUtils {
  private val HEX_ARRAY = "0123456789ABCDEF".toCharArray()

  const val START: Byte = 0xEE.toByte()
  const val CMD_HAND: Byte = 0x01 //	握手请求 	手机 ->充电桩
  const val CMD_AUTH: Byte = 0x02 //	用户反向鉴权 	手机 ->充电桩
  const val CMD_CHARGE_START: Byte = 0x03 //	开启充电 	手机->充电桩
  const val CMD_CHARGE_STOP: Byte = 0x04 //	结束充电 	手机->充电桩
  const val CMD_HEART: Byte = 0x05 //	遥信及心跳 	充电桩 ->手机
  const val CMD_PRICE_TEMPLATE_UPLOAD: Byte = 0x06 // 计费模版上传	充电桩 ->手机
  const val CMD_PRICE_TEMPLATE_REQUEST: Byte = 0x07 //	请求计费模板 	双向
  const val CMD_FIRMWARE_UPDATE_NOTIFY: Byte = 0x08 //	固件升级开始通知 	手机->充电桩
  const val CMD_FIRMWARE_UPDATE: Byte = 0x09 //	固件下发	手机->充电桩
  const val CMD_CLOSE: Byte = 0x0A //	请求断开连接 	手机->充电桩
  const val CMD_REGISTE: Byte = 0x0B //	设备注册 	充电桩->手机 	只适用于 X6L
  const val CMD_LOGIN: Byte = 0x0C //	设备登录 	充电桩->手机 	只适用于 X6L
  const val CMD_ORDERS_REPORT: Byte = 0x0D // 历史充电订单上报 	充电桩->手机 	只适用于 X6L
  const val CMD_PRICE_TEMPLATE_SEND: Byte = 0x0E // 计费模版下发 手机->充电桩 	只适用于 X6L
  const val CMD_SET_SN: Byte = 0x0F // 设置充电桩编号 手机->充电桩
  const val CMD_SET_ID: Byte = 0x10 // 设置充电桩识别码 手机->充电桩
  const val CMD_SET_GATEWAY: Byte = 0x11 // 设置 2.4G 网关地址 手机->充电桩 X6/X10 适用
  const val CMD_SET_SERVER_IP: Byte = 0x12 // 设置服务器地址 手机->充电桩
  const val CMD_SET_SERVER_PORT: Byte = 0x13 // 设置服务器地址 手机->充电桩
  const val CMD_SET_TERMINAL_NUMBER: Byte = 0x14 // 设置终端编号信息 手机->充电桩 R6 或 R8 适用
  const val CMD_CONTROL: Byte = 0x15 // 远程控制 手机->充电桩

  /**
   * 将int数值转换为占四个字节的byte数组，本方法适用于(低位在前，高位在后)的顺序
   */
  fun intToBytes(value: Int): ByteArray {
    val src = ByteArray(4)
    src[3] = (value shr 24 and 0xFF).toByte()
    src[2] = (value shr 16 and 0xFF).toByte()
    src[1] = (value shr 8 and 0xFF).toByte()
    src[0] = (value and 0xFF).toByte()
    return src
  }

  fun intToBytes2(value: Int): ByteArray {
    val src = ByteArray(2)
    src[1] = (value shr 8 and 0xFF).toByte()
    src[0] = (value and 0xFF).toByte()
    return src
  }

  /**
   * byte数组中取int数值，本方法适用于(低位在前，高位在后)的顺序，和和intToBytes（）配套使用
   *
   * @param src byte数组
   * @param offset 从数组的第offset位开始
   * @return int数值
   */
  fun bytesToInt(
    src: ByteArray,
    offset: Int = 0
  ): Int {
    return (src[offset].toInt() and 0xFF
        or (src[offset + 1].toInt() and 0xFF shl 8)
        or (src[offset + 2].toInt() and 0xFF shl 16)
        or (src[offset + 3].toInt() and 0xFF shl 24))
  }

  fun bytesToInt2(
    src: ByteArray,
    offset: Int = 0
  ): Int {
    return (src[offset].toInt() and 0xFF
        or (src[offset + 1].toInt() and 0xFF shl 8))
  }

  fun bytesToHex(bytes: ByteArray): String {
    val hexChars = CharArray(bytes.size * 2)

    for (j in bytes.indices) {
      bytes[0].toUnsignedInt()
      val v = bytes[j].toInt() and 0xFF
      hexChars[j * 2] = HEX_ARRAY[v.ushr(4)]
      hexChars[j * 2 + 1] = HEX_ARRAY[v and 0x0F]
    }

    return String(hexChars)
  }

  fun hexToBytes(
    hexRepresentation: String,
    length: Int = 0
  ): ByteArray {
    var str = hexRepresentation
    if (length == 0) {
      if (hexRepresentation.length % 2 == 1)
        str = "0" + hexRepresentation
    } else {
      if (hexRepresentation.length > length) {
        throw IllegalArgumentException()
      }
      str = Strings.leftPad(hexRepresentation, length, "0")
    }

    val len = str.length
    val data = ByteArray(len / 2)

    var i = 0
    while (i < len) {
      data[i / 2] = ((Character.digit(str[i], 16) shl 4) + Character.digit(
          str[i + 1], 16
      )).toByte()
      i += 2
    }

    return data
  }

  fun strToByteArray(
    str: String,
    length: Int
  ): ByteArray {
    return str.toByteArray()
        .copyOf(length)
  }

  fun byteArrayToStr(data: ByteArray): String {
    val index: Int = data.indexOfLast { it != 0.toByte() }
    return String(data, 0, index + 1)
  }

  fun makeMessage(
    cmd: Byte,
    data: ByteArray
  ): ByteArray {
    val preData = byteArrayOf(START, cmd, data.size.toByte()) + data
    var value = 0
    preData.forEach({
      value += (it.toInt() and 0xFF)
    })
    return preData + value.toByte()
  }

  @Throws(MessageException::class)
  fun parseMessage(
    byteArray: ByteArray,
    verification: Boolean = false
  ): Message? {
    if (byteArray[0] != START) throw MessageException()
    val cmd = byteArray[1]
    val length = byteArray[2]
    if (byteArray.size != length + 4) return null
    if (verification) {
      val preData = byteArray.copyOfRange(0, byteArray.size - 1)
      var value: Byte = 0
      preData.forEach({
        value = (it + value).toByte()
      })
      if (value != byteArray[byteArray.size - 1]) throw MessageException()
    }
    return Message.parse(cmd, byteArray.copyOfRange(3, byteArray.size - 1))
  }
}

fun Byte.toUnsignedInt(): Int {
  return this.toInt() and 0xFF
}

object Cmd {
  fun handCmd(
    time: Int,
    mobile: String
  ): ByteArray {
    return makeMessage(
        CMD_HAND, intToBytes(time) + strToByteArray(mobile, 12)
    )
  }

  fun controlCmd(
    cmd: Int,
    param: Int = 0
  ): ByteArray {
    return makeMessage(CMD_CONTROL, byteArrayOf(cmd.toByte()) + intToBytes(param))
  }
}