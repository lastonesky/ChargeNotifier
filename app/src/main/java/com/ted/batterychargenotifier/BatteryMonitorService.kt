package com.ted.batterychargenotifier

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

class BatteryMonitorService : Service() {
    private var batteryReceiver: BroadcastReceiver? = null
    private var notified = false

    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()
        startForeground(SERVICE_NOTIFICATION_ID, buildServiceNotification())

        batteryReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action != Intent.ACTION_BATTERY_CHANGED || notified) return

                val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                if (level < 0 || scale <= 0) return

                val batteryPct = level / scale.toFloat() * 100f

                val settings = getSharedPreferences(MainActivity.PREFS_NAME, Context.MODE_PRIVATE)
                val threshold = settings.getString("threshold", "80")?.toIntOrNull() ?: 80
                val message = settings.getString("message", "请注意电量") ?: ""

                if (batteryPct >= threshold) {
                    notified = true
                    sendAlertNotification(message)
                    stopSelf()
                }
            }
        }

        registerReceiver(batteryReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        batteryReceiver?.let {
            unregisterReceiver(it)
        }
        batteryReceiver = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun buildServiceNotification(): Notification {
        val launchIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            launchIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, MainActivity.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_battery_alert)
            .setContentTitle("正在监控充电电量")
            .setContentText("达到阈值后将发送提醒")
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun sendAlertNotification(message: String) {
        val builder = NotificationCompat.Builder(this, MainActivity.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_battery_alert)
            .setContentTitle("电量已达到阈值")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(ALERT_NOTIFICATION_ID, builder.build())
    }

    private fun createNotificationChannel() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            MainActivity.CHANNEL_ID,
            getString(R.string.channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = getString(R.string.channel_description)
        }
        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        private const val SERVICE_NOTIFICATION_ID = 1001
        private const val ALERT_NOTIFICATION_ID = 1

        fun start(context: Context) {
            val intent = Intent(context, BatteryMonitorService::class.java)
            ContextCompat.startForegroundService(context, intent)
        }

        fun stop(context: Context) {
            val intent = Intent(context, BatteryMonitorService::class.java)
            context.stopService(intent)
        }
    }
}
