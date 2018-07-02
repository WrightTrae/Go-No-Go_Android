package com.wright.android.t_minus.notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.wright.android.t_minus.objects.Manifest;

import java.util.Calendar;

public class AlarmSetter implements Runnable{

        private final Calendar date;
        private final AlarmManager am;
        private final Context context;
        private final Manifest manifest;

        public AlarmSetter(Context context, Calendar date, Manifest _manifest) {
            this.context = context;
            this.am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            manifest = _manifest;
            this.date = date;
        }

        @Override
        public void run() {
            Intent intent = new Intent(context, ShowNotificationService.class);
            intent.putExtra(ShowNotificationService.CHANNEL_ID, true);
            intent.putExtra(ShowNotificationService.MANIFEST_ID, manifest);
            PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);
            am.set(AlarmManager.RTC, date.getTimeInMillis(), pendingIntent);
        }
    }
