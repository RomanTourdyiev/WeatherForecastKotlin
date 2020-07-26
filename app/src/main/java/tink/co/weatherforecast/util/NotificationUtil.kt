package tink.co.weatherforecast.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import tink.co.weatherforecast.R


/**
 * Created by Tourdyiev Roman on 26.07.2020.
 */
private const val CHANNEL_ID = "FCM Channel"
private const val CHANNEL_NAME = "FCM Channel Name"
private const val NOTIFICATION_ID = 5555

object NotificationUtil {

    private fun createNotificationChannelsIfNeeded(context: Context, channelName: String) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }
        createChannel(context, channelName)
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun createChannel(context: Context, channelName: String) {
        val channel =
            NotificationChannel(CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_DEFAULT)
        channel.description = CHANNEL_ID
        channel.enableLights(false)
        channel.enableVibration(false)
        channel.vibrationPattern = longArrayOf(0)
        channel.lightColor = Color.RED
        channel.setSound(null, null)
        channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        channel.importance = NotificationManager.IMPORTANCE_MIN

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    fun showNotification(context: Context, title: String?, content: String?) {
        createNotificationChannelsIfNeeded(context, CHANNEL_NAME)

        val notificationBuilder =
            NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(R.drawable.ic_launcher)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(true)
                .setLargeIcon(
                    getBitmapFromVectorDrawable(context, R.drawable.ic_launcher)
                )


        with(NotificationManagerCompat.from(context)) {
            notify(
                NOTIFICATION_ID,
                notificationBuilder.build()
            )
        }
    }

    fun getBitmapFromVectorDrawable(
        context: Context?,
        drawableId: Int
    ): Bitmap? {
        val drawable = ContextCompat.getDrawable(context!!, drawableId)
        val bitmap = Bitmap.createBitmap(
            drawable!!.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

}