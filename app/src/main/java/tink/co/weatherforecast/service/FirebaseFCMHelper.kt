package tink.co.weatherforecast.service

import android.content.Intent
import android.util.Config
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import tink.co.weatherforecast.util.NotificationUtil

/**
 * Created by Tourdyiev Roman on 26.07.2020.
 */
class FirebaseFCMHelper : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        remoteMessage.notification?.let{
            NotificationUtil.showNotification(
                applicationContext,
                remoteMessage.notification?.title,
                remoteMessage.notification?.body
            )
        }
    }
}
