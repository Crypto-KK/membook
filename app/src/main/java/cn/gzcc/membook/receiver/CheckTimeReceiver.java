package cn.gzcc.membook.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import cn.gzcc.membook.db.MyDB;
import cn.gzcc.membook.entity.Record;


/**
 * create_by Android Studio
 *
 * @author gzcc
 * @package_name cn.gzcc.membook.receiver
 * @description 检查时间变化的广播接收，同时发送提醒广播
 */
public class CheckTimeReceiver extends BroadcastReceiver {

    private final static String TAG = "CheckTimeReceiver";
    MyDB myDB;

    @Override
    public void onReceive(Context context, Intent intent) {
        //  每分钟都触发一遍该方法
        if (Intent.ACTION_TIME_TICK.equals(intent.getAction())) {
            Long temp = System.currentTimeMillis();
            Log.i(TAG, "每分钟触发一次该监听方法" + temp);
            List<Record> recordList = getData(context);
            Intent newIntent;
            for (Record record : recordList) {
                // 遍历需要提醒的备忘录并发送广播
                newIntent = new Intent();
                // 标记action为ACTION_TIME_TICK
                newIntent.setAction("ACTION_NEW_REMIND");
                newIntent.putExtra(MyDB.RECORD_TITLE, record.getTitleName().trim());
                newIntent.putExtra(MyDB.RECORD_BODY, record.getTextBody().trim());
                newIntent.putExtra(MyDB.RECORD_TIME_LONG, record.getLongCreateTime());
                newIntent.putExtra(MyDB.RECORD_ID, record.getId());
                newIntent.putExtra(MyDB.NOTICE_TIME_LONG, record.getLongNoticeTime());
                context.sendBroadcast(newIntent);
            }
        }
    }

    private List<Record> getData(Context context) {
        // 获取当前时间需要提醒的备忘录列表
        List<Record> recordList = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        // 获取当前系统的时间戳
        Date date = new Date(System.currentTimeMillis());
        try {
            // 将当前时间转换为字符串型 如：1575717600000
            String param = String.valueOf(dateFormat.parse(dateFormat.format(date)).getTime());
            // 将参数变为数组 如：[1575717600000]
            String[] strings = new String[]{param};
            myDB = new MyDB(context);
            // 获取可读的资源
            SQLiteDatabase db = myDB.getReadableDatabase();


            String sql = "select * from " + MyDB.TABLE_NAME_RECORD + " where " +
                    MyDB.NOTICE_TIME_LONG + "=?";
            // 相当于 select * from record where notice_time_long=1575717600000
            Cursor cursor = db.rawQuery(sql, strings);
            // 将游标移到开始
            if (cursor.moveToFirst()) {
                Record record;
                // 如果还有下一条记录，不断取出放到实体类中
                while (!cursor.isAfterLast()) {
                    record = new Record();
                    record.setId(
                            Integer.valueOf(cursor.getString(cursor.getColumnIndex(MyDB.RECORD_ID))));
                    record.setTitleName(
                            cursor.getString(cursor.getColumnIndex(MyDB.RECORD_TITLE))
                    );
                    record.setTextBody(
                            cursor.getString(cursor.getColumnIndex(MyDB.RECORD_BODY))
                    );
                    Long longCreateTime = cursor.getLong(cursor.getColumnIndex(MyDB.RECORD_TIME_LONG));
                    record.setLongCreateTime(longCreateTime);
                    Long longNoticeTime = cursor.getLong(cursor.getColumnIndex(MyDB.NOTICE_TIME_LONG));
                    record.setLongNoticeTime(longNoticeTime);
                    recordList.add(record);
                    cursor.moveToNext();
                    // 游标移到下一条记录
                }
            }
            cursor.close();
            db.close();
            return recordList;
        } catch (ParseException e) {
            Log.i(TAG, "getData: 日期错误！");
            return null;
        }
    }

}
