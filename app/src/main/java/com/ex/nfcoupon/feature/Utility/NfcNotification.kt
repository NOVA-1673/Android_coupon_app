package com.ex.nfcoupon.feature.Utility

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.ex.nfcoupon.R

object NfcNotification {
    private const val CHANNEL_ID = "nfcoupon_channel"

    fun ensureChannel(ctx: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val ch = NotificationChannel(
                CHANNEL_ID,
                "NFCoupon",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            nm.createNotificationChannel(ch)
        }
    }

    fun show(ctx: Context, title: String, text: String) {
        ensureChannel(ctx)
        val nm = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val n = NotificationCompat.Builder(ctx, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // 아이콘 프로젝트에 맞게
            .setContentTitle(title)
            .setContentText(text)
            .setAutoCancel(true)
            .build()

        nm.notify((System.currentTimeMillis() % Int.MAX_VALUE).toInt(), n)
    }
}