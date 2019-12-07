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

import cn.gzcc.membook.R;
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
        //  分钟级改变，触发
        if (Intent.ACTION_TIME_TICK.equals(intent.getAction())) {
            Long temp = System.currentTimeMillis();
            Log.i(TAG, "onReceive: 提醒~~~~" + temp);
//            MediaPlayer mMediaPlayer;
//            mMediaPlayer=MediaPlayer.create(this, R.raw);
//            mMediaPlayer.start();
            List<Record> recordList = getData(context);
            Intent newIntent;
            for (Record record : recordList) {
                // 遍历每一个记录来发送广播
                newIntent = new Intent();
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
        // 获取所有数据库的数据
        List<Record> recordList = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date(System.currentTimeMillis());
        try {
            String param = String.valueOf(dateFormat.parse(dateFormat.format(date)).getTime());
            String[] strings = new String[]{param};
            myDB = new MyDB(context);
            SQLiteDatabase db = myDB.getReadableDatabase();

            String sql = "select * from " + MyDB.TABLE_NAME_RECORD + " where " +
                    MyDB.NOTICE_TIME_LONG + "=?";
            Cursor cursor = db.rawQuery(sql, strings);

            if (cursor.moveToFirst()) {
                Record record;
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
//                    Log.i(TAG, "getData: ~~~~~数据库参数" + record.getLongNoticeTime());
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
