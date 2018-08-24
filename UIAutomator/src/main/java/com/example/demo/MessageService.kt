package com.example.demo

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.widget.Toast

class MessageService : Service() {

  private var handler = Handler()

  private var stub: IMessageServer.Stub = object : IMessageServer.Stub() {

    override fun toast(message: String?) {
      handler.post {
        Toast.makeText(this@MessageService, message, Toast.LENGTH_SHORT)
            .show()
      }
    }
  }

  override fun onBind(intent: Intent): IBinder {
    return stub
  }
}
