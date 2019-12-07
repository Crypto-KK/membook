package cn.gzcc.membook.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * create_by Android Studio
 *
 * @author gzcc
 * @package_name cn.gzcc.membook.db
 * @description 对SQLite的操作类进行进一步封装
 */
public class MyDB extends SQLiteOpenHelper {
    public final static String TABLE_NAME_RECORD = "record";

    public final static String RECORD_ID = "_id";
    public final static String RECORD_TITLE = "title_name";
    public final static String RECORD_BODY = "text_body";
    public final static String RECORD_TIME_LONG ="create_time_long";
    public final static String NOTICE_TIME_LONG ="notice_time_long";



    public MyDB(Context context) {
        super(context, "test.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 执行插入的sql
        db.execSQL("CREATE TABLE "+TABLE_NAME_RECORD+" ("+RECORD_ID+" INTEGER PRIMARY KEY AUTOINCREMENT," +
                RECORD_TITLE+" VARCHAR(30)," +
                RECORD_BODY+" TEXT," +
                RECORD_TIME_LONG+" LONG NOT NULL," +
                NOTICE_TIME_LONG+" LONG)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}