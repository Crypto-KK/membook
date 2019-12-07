package cn.gzcc.membook.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v4.app.NotificationCompat;


import cn.gzcc.membook.R;
import cn.gzcc.membook.db.MyDB;
import cn.gzcc.membook.utils.NotificationID;

import static android.content.Context.NOTIFICATION_SERVICE;


public class NoticeReceiver extends BroadcastReceiver {
    // 广播提醒  使用通知栏
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if ("ACTION_NEW_REMIND".equals(action)){

            // 通知栏提醒！
            showNotificationIcon(context,intent);
        }
    }
    public static void showNotificationIcon(Context context,Intent intent) {
        String title = intent.getStringExtra(MyDB.RECORD_TITLE);
        String body = intent.getStringExtra(MyDB.RECORD_BODY);
        Integer id = intent.getIntExtra(MyDB.RECORD_ID,NotificationID.getId());
        Notification.Builder builder = new Notification.Builder(context);
        builder.setAutoCancel(true);//点击后消失
        builder.setSmallIcon(R.mipmap.baseline_alarm_black_36);//设置通知栏消息标题的图标
        // 使用MediaPlayer类播放铃声
        MediaPlayer mMediaPlayer;
        mMediaPlayer=MediaPlayer.create(context, R.raw.dingdong);
        mMediaPlayer.start();

        builder.setContentText(body);//通知内容
        builder.setContentTitle(title);//通知标题
        Notification notification = builder.build();
        NotificationManager manager =
                (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        assert manager != null;
        manager.notify(id,notification);
    }
}
