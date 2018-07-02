package com.wright.android.t_minus.notifications;

import com.wright.android.t_minus.objects.Manifest;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.util.Calendar;

public class NotifService extends Service {

    public class ServiceBinder extends Binder {
        NotifService getService() {
            return NotifService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private final IBinder mBinder = new ServiceBinder();

    public void setAlarm(Calendar c, Manifest manifest) {
        new AlarmSetter(this, c, manifest).run();
    }
}
