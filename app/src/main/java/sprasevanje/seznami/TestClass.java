package sprasevanje.seznami;

import android.app.Notification;
import android.app.NotificationManager;
import android.nfc.Tag;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;



public class TestClass extends FirebaseMessagingService {
    ArrayList<String> seznami = new ArrayList<>();
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d("jabuk", "notification");
        if (!seznami.contains(remoteMessage.getNotification().getTitle())){
            seznami.add(remoteMessage.getNotification().getTitle());
        }
        NotificationCompat.Builder builder = new  NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(remoteMessage.getNotification().getTitle())
                .setContentText(remoteMessage.getNotification().getBody())
                .setDefaults(Notification.DEFAULT_SOUND);
        NotificationManager manager = (NotificationManager)     getSystemService(NOTIFICATION_SERVICE);
        manager.notify(seznami.indexOf(remoteMessage.getNotification().getTitle()), builder.build());
    }

    }

