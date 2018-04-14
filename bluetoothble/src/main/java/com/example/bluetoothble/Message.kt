package com.example.bluetoothble

import com.example.bluetoothble.MessageUtils.CMD_CHARGE_STOP
import com.example.bluetoothble.MessageUtils.CMD_CONTROL
import com.example.bluetoothble.MessageUtils.CMD_FIRMWARE_UPDATE
import com.example.bluetoothble.MessageUtils.CMD_FIRMWARE_UPDATE_NOTIFY
import com.example.bluetoothble.MessageUtils.CMD_HAND
import com.example.bluetoothble.MessageUtils.CMD_HEART
import com.example.bluetoothble.MessageUtils.CMD_PRICE_TEMPLATE_UPLOAD
import com.example.bluetoothble.MessageUtils.CMD_SET_ID
import com.example.bluetoothble.MessageUtils.CMD_SET_SERVER_IP
import com.example.bluetoothble.MessageUtils.CMD_SET_SN
import com.example.bluetoothble.MessageUtils.CMD_SET_TERMINAL_NUMBER
import com.example.bluetoothble.MessageUtils.byteArrayToStr
import com.example.bluetoothble.MessageUtils.bytesToInt
import com.example.bluetoothble.MessageUtils.bytesToInt2
import java.io.Serializable

open class Message(var cmd: Byte) : Serializable {

  companion object {
    fun parse(
      cmd: Byte,
      data: ByteArray
    ): Message {
      return when (cmd) {
        CMD_HAND -> Device.parse(data)
        CMD_CHARGE_STOP -> ChargeStop.parse(data)
        CMD_HEART -> Heartbeat.parse(data)
        CMD_PRICE_TEMPLATE_UPLOAD -> PriceTemplateUpload.parse(data)
        CMD_CONTROL -> Control.parse(data)
        CMD_SET_SN -> SetSn.parse(data)
        CMD_SET_ID -> SetID.parse(data)
        CMD_SET_SERVER_IP -> SetServer.parse(data)
        CMD_SET_TERMINAL_NUMBER -> SetTerminalNumber.parse(data)
        CMD_FIRMWARE_UPDATE_NOTIFY -> FirmwareUpdateNotify.parse(data)
        CMD_FIRMWARE_UPDATE -> FirmwareUpdate.parse(data)
        else -> throw MessageException()
      }
    }
  }
}

class MessageException : RuntimeException()

class Device(
  var model: String,
  var sn: String,
  var version: Int,
  var socketCount: Int,
  var socketStart: Int
) : Message(CMD_HAND) {
  companion object {
    fun parse(data: ByteArray): Device {
      val model = byteArrayToStr(data.copyOfRange(0, 4))
      val number = MessageUtils.bytesToHex(data.copyOfRange(4, 9))
      val version = data[9].toUnsignedInt()
      val socketCount = data[10].toUnsignedInt()
      val socketStart = data[11].toUnsignedInt()
      return Device(model, number, version, socketCount, socketStart)
    }
  }

  override fun toString(): String {
    return "Device(model='$model', sn='$sn', version=$version, socketCount=$socketCount, socketStart=$socketStart)"
  }
}

/**
 * 结束订单
 * @param result 0: 成功 1: 失败
 */
class ChargeStop(
  var number: Int,
  var result: Int,
  var time: Int,
  var power: Int
) : Message(CMD_CHARGE_STOP) {
  companion object {
    fun parse(data: ByteArray): ChargeStop {
      val number = data[0].toUnsignedInt()
      val result = data[1].toUnsignedInt()
      val time = bytesToInt(data, 2)
      val power = bytesToInt2(data, 6)
      return ChargeStop(number, result, time, power)
    }
  }

  override fun toString(): String {
    return "ChargeStop(number=$number, result=$result, time=$time, power=$power)"
  }
}

class Heartbeat(
  var simSignal: Int,
  var envTemperature: Int,
  var socketCount: Int,
  var sockets: List<Socket>
) : Message(CMD_HEART) {
  companion object {
    fun parse(data: ByteArray): Heartbeat {
      val simSignal = data[0].toUnsignedInt()
      val envTemperature = data[1].toUnsignedInt() - 50
      val socketCount = data[2].toUnsignedInt()
      val sockets = mutableListOf<Socket>()
      for (index in 3 until 3 + socketCount * 3 step 3) {
        val number = data[index].toUnsignedInt()
        val status = data[index + 1].toUnsignedInt()
        val faultCode = data[index + 2].toUnsignedInt()
        sockets.add(Socket(number, status, faultCode))
      }
      return Heartbeat(simSignal, envTemperature, socketCount, sockets)
    }
  }

  override fun toString(): String {
    return "Heartbeat(simSignal=$simSignal, envTemperature=$envTemperature, socketCount=$socketCount, sockets=$sockets)"
  }
}

/**
 * @param number 插座编号 1~12
 * @param status 充电口状态:0 空闲;1 占用;2 故障;3 离线; Int.MAX_VALUE 已开启(通过DFI开启端口, 客户端记录)
 * @param faultCode 1~255 当充电口状态为 2 故障时有效;1:状态异常;2:计量芯片通信故障
 */
class Socket(
  var number: Int,
  var status: Int,
  var faultCode: Int
) : Serializable {
  override fun toString(): String {
    return "Socket(number=$number, status=$status, faultCode=$faultCode)"
  }
}

class PriceTemplateUpload(
  var number: Int,
  var templateNumber: Int,
  var model: Int,
  var count: Int,
  var priceTemplates: List<PriceTemplate>
) : Message(CMD_PRICE_TEMPLATE_UPLOAD) {
  companion object {
    fun parse(data: ByteArray): PriceTemplateUpload {
      val number = data[0].toUnsignedInt()
      val templateNumber = bytesToInt(data, 1)
      val model = data[5].toUnsignedInt()
      val count = data[6].toUnsignedInt()
      val priceTemplates = arrayListOf<PriceTemplate>()
      for (index in 7 until 7 + count * 8 step 8) {
        val minPower = bytesToInt2(data, index)
        val maxPower = bytesToInt2(data, index + 2)
        val price = bytesToInt2(data, index + 4)
        val time = bytesToInt2(data, index + 6)
        priceTemplates.add(PriceTemplate(minPower, maxPower, price, time))
      }
      return PriceTemplateUpload(number, templateNumber, model, count, priceTemplates)
    }
  }

  override fun toString(): String {
    return "PriceTemplateUpload(number=$number, templateNumber=$templateNumber, model=$model, count=$count, priceTemplates=$priceTemplates)"
  }
}

class PriceTemplate(
  var minPower: Int,
  var maxPower: Int,
  var price: Int,
  var time: Int
) : Serializable {
  override fun toString(): String {
    return "PriceTemplate(minPower=$minPower, maxPower=$maxPower, price=$price, time=$time)"
  }
}

/**
 * 远程控制应答
 * @param result 0: 成功 1: 失败
 */
class Control(
  var command: Int,
  var param: Int,
  var result: Int
) : Message(CMD_CONTROL) {
  companion object {
    fun parse(data: ByteArray): Control {
      val command = data[0].toUnsignedInt()
      val param = bytesToInt(data, 1)
      val result = data[5].toUnsignedInt()
      return Control(command, param, result)
    }
  }

  override fun toString(): String {
    return "Control(command=$command, param=$param, result=$result)"
  }
}

/**
 * @param result 0: 成功 1: 失败
 */
class SetSn(
  var result: Int
) : Message(CMD_SET_SN) {
  companion object {
    fun parse(data: ByteArray): SetSn {
      val result = data[0].toUnsignedInt()
      return SetSn(result)
    }
  }

  override fun toString(): String {
    return "SetSn(result=$result)"
  }
}

/**
 * @param result 0: 成功 1: 失败
 */
class SetID(
  var result: Int
) : Message(CMD_SET_ID) {
  companion object {
    fun parse(data: ByteArray): SetID {
      val result = data[0].toUnsignedInt()
      return SetID(result)
    }
  }

  override fun toString(): String {
    return "SetID(result=$result)"
  }
}

/**
 * @param result 0: 成功 1: 失败
 */
class SetServer(
  var result: Int
) : Message(CMD_SET_SERVER_IP) {
  companion object {
    fun parse(data: ByteArray): SetServer {
      val result = data[0].toUnsignedInt()
      return SetServer(result)
    }
  }

  override fun toString(): String {
    return "SetServer(result=$result)"
  }
}

/**
 * @param result 0: 成功 1: 失败
 */
class SetTerminalNumber(
  var result: Int
) : Message(CMD_SET_TERMINAL_NUMBER) {
  companion object {
    fun parse(data: ByteArray): SetTerminalNumber {
      val result = data[0].toUnsignedInt()
      return SetTerminalNumber(result)
    }
  }

  override fun toString(): String {
    return "SetTerminalNumber(result=$result)"
  }
}

/**
 * @param result 0: 成功 1: 失败
 */
class FirmwareUpdateNotify(
  var result: Int
) : Message(CMD_FIRMWARE_UPDATE_NOTIFY) {
  companion object {
    fun parse(data: ByteArray): FirmwareUpdateNotify {
      val result = data[0].toUnsignedInt()
      return FirmwareUpdateNotify(result)
    }
  }

  override fun toString(): String {
    return "FirmwareUpdateNotify(result=$result)"
  }
}

/**
 * @param result 0: 成功 1: 失败
 */
class FirmwareUpdate(
  var result: Int
) : Message(CMD_FIRMWARE_UPDATE) {
  companion object {
    fun parse(data: ByteArray): FirmwareUpdate {
      val result = data[0].toUnsignedInt()
      return FirmwareUpdate(result)
    }
  }

  override fun toString(): String {
    return "FirmwareUpdate(result=$result)"
  }
}