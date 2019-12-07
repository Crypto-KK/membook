package cn.gzcc.membook.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import cn.gzcc.membook.receiver.CheckTimeReceiver;
import cn.gzcc.membook.receiver.NoticeReceiver;


public class AlarmService extends Service {

    private final static String TAG = "AlarmService";

    private NoticeReceiver noticeReceiver;
    private CheckTimeReceiver timeReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate: 服务创建！");
        timeReceiver = new CheckTimeReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(timeReceiver,filter);
        noticeReceiver = new NoticeReceiver();
        filter = new IntentFilter();
        filter.addAction("ACTION_NEW_REMIND");
        registerReceiver(noticeReceiver,filter);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        flags = START_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: 服务销毁！");
        unregisterReceiver(timeReceiver);
    }
}

