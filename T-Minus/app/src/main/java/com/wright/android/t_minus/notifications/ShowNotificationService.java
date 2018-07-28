package com.wright.android.t_minus.notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.wright.android.t_minus.objects.Manifest;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.wright.android.t_minus.MainTabbedActivity;
import com.wright.android.t_minus.R;

public class ShowNotificationService extends Service {
    public static final String CHANNEL_ID = "com.wright.android.t_minus.Notification.CHANNEL_ID";
    public static final String MANIFEST_ID = "MANIFEST_ID";
    private Manifest manifest;

    public class ServiceBinder extends Binder {
        ShowNotificationService getService() {
            return ShowNotificationService.this;
        }
    }

    private static final int NOTIFICATION = 123;
    private NotificationManager mNM;

    @Override
    public void onCreate() {
        Log.i("NotifyService", "onCreate()");
        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);
        if (intent.hasExtra(MANIFEST_ID)) {
            manifest = (Manifest) intent.getSerializableExtra(MANIFEST_ID);
        }
        if(intent.getBooleanExtra(CHANNEL_ID, true)){
            showNotification();
        }
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private final IBinder mBinder = new ServiceBinder();

    private void showNotification() {
        int icon = R.drawable.logo_outline;

        NotificationCompat.Builder b = new NotificationCompat.Builder(this, CHANNEL_ID);
        b.setSmallIcon(icon);
        b.setContentTitle("Launch is Go");
        b.setContentText("A rocket is going to take off soon");
        b.setPriority(NotificationManager.IMPORTANCE_HIGH);
        Intent i = new Intent(this, MainTabbedActivity.class);
        //i.putExtra(ManifestDetailsActivity.ARG_MANIFEST, manifest);
        PendingIntent pi = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
        b.addAction(android.R.drawable.sym_def_app_icon, "View", pi);

        mNM.notify(NOTIFICATION, b.build());

        stopSelf();

    }
}
