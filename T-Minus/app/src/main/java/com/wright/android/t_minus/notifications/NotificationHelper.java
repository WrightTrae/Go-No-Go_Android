package com.wright.android.t_minus.notifications;

import com.wright.android.t_minus.objects.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import java.util.Calendar;


public class NotificationHelper {

    private NotifService mBoundService;
    public static final String SPOIL_ITEM_ARG = "SPOIL_ITEM_ARG";
    private final Context mContext;
    private boolean mIsBound;

    public NotificationHelper(Context context) {
        mContext = context;
    }

    public void setBindService() {
        Intent intent = new Intent(mContext, NotifService.class);
        mContext.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    private final ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mBoundService = ((NotifService.ServiceBinder) service).getService();
        }

        public void onServiceDisconnected(ComponentName className) {
            mBoundService = null;
        }
    };

    public void setAlarmForNotification(Calendar c, Manifest manifest){
        mBoundService.setAlarm(c, manifest);
    }

    public void unbindService() {
        if (mIsBound) {
            mContext.unbindService(mConnection);
            mIsBound = false;
        }
    }
}
