package com.ted.batterychargenotifier

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

class BatteryLevelReceiver : BroadcastReceiver() {

    private val channelId = MainActivity.CHANNEL_ID
    private val notificationTimeoutMs = 5 * 60 * 1000L

    override fun onReceive(context: Context, intent: Intent) {
        if(intent.action == Intent.ACTION_POWER_CONNECTED){
            BatteryMonitorService.start(context)
            sendNotification(context, "开始监控充电电量")
        }
        if(intent.action == Intent.ACTION_POWER_DISCONNECTED){
            BatteryMonitorService.stop(context)
        }
        if (intent.action == Intent.ACTION_BATTERY_CHANGED) {
            val level = intent.getIntExtra(android.os.BatteryManager.EXTRA_LEVEL, -1)
            val scale = intent.getIntExtra(android.os.BatteryManager.EXTRA_SCALE, -1)
            val batteryPct = level / scale.toFloat() * 100

            val settings = context.getSharedPreferences(MainActivity.PREFS_NAME, Context.MODE_PRIVATE)
            val thresholdStr = settings.getString("threshold", "80")
            val message = settings.getString("message", "充电完成80%，请注意")

            thresholdStr?.let {
                try {
                    val threshold = it.toInt()
                    if (batteryPct >= threshold) {
                        sendNotification(context, message ?: "")
                    }
                } catch (_: NumberFormatException) {
                    // Handle non-numeric input
                }
            }
        }
    }

    private fun sendNotification(context: Context, message: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(channelId, context.getString(R.string.channel_name), NotificationManager.IMPORTANCE_DEFAULT)
        notificationManager.createNotificationChannel(channel)

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_battery_alert) // Ensure you have this drawable resource
            .setContentTitle("Battery Level Reached")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setTimeoutAfter(notificationTimeoutMs)

        notificationManager.notify(1, builder.build())
    }
}
