package cn.gzcc.membook;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v7.app.AlertDialog;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.gzcc.membook.db.MyDB;
import cn.gzcc.membook.entity.Record;
import cn.gzcc.membook.service.AlarmService;
import cn.gzcc.membook.utils.ServiceUtils;

import static cn.gzcc.membook.utils.MyFormat.*;


public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        AdapterView.OnItemClickListener,AdapterView.OnItemLongClickListener{
    // 实现item点击事件、item长按点击事件、点击监听事件
    private final static String TAG = "MainActivity";

    MyDB myDB; // 自定义DB
    private ListView myListView;// 列表
    private Button createButton; // 创建按钮
    private MyBaseAdapter myBaseAdapter; // 适配器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        init();
    }

    //初始化控件
    private void init(){
        createButton = findViewById(R.id.createButton);
        createButton.setOnClickListener(this);

        myListView = findViewById(R.id.list_view);

        List<Record> recordList = new ArrayList<>();
        myDB = new MyDB(this);
        SQLiteDatabase db = myDB.getReadableDatabase();
        Cursor cursor = db.query(MyDB.TABLE_NAME_RECORD,null,
                null,null,null,
                null,MyDB.NOTICE_TIME_LONG+","+MyDB.RECORD_TIME_LONG+" DESC");
        // 按照提醒时间、创建时间以降序排列
        if(cursor.moveToFirst()){
            // 数据库游标置为起点
            Record record;
            while (!cursor.isAfterLast()){
                // 不断从数据库中遍历出记录存入实体类中
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
                // 将数据库日期格式改为Long
                Long longNoticeTime = cursor.getLong(cursor.getColumnIndex(MyDB.NOTICE_TIME_LONG));
                if (longNoticeTime!=0){
                    // 将数据库日期格式改为Long
                    record.setLongNoticeTime(longNoticeTime);
                }
                recordList.add(record);
                cursor.moveToNext();
            }
        }
        // 关闭数据库和游标资源
        cursor.close();
        db.close();
        // 创建一个Adapter的实例
        myBaseAdapter = new MyBaseAdapter(this,recordList,R.layout.list_item);
        myListView.setAdapter(myBaseAdapter);
        // 设置点击监听和长按点击监听
        myListView.setOnItemClickListener(this);
        myListView.setOnItemLongClickListener(this);
        // 启动提醒服务
        startAlarmService();
    }

    // 启动提醒服务
    private void startAlarmService(){
        // 如果服务未运行，则创建一个intent
        if (!ServiceUtils.isServiceRunning(MainActivity.this,
                "cn.gzcc.membook.service.AlarmService")){
            Intent intent = new Intent(MainActivity.this, AlarmService.class);
            startService(intent);
        }
    }

    @Override
    public void onClick(View v) {
        // 按钮监听绑定
        switch (v.getId()){
            case R.id.createButton:
                // 跳转到添加页面
                Intent intent = new Intent(MainActivity.this, NewActivity.class);
                startActivity(intent);
                // 结束当前页面
                MainActivity.this.finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // 点击记录的监听事件
        Intent intent = new Intent(MainActivity.this, EditActivity.class);
        // 通过listView的位置信息获取当前记录的实体类
        Record record = (Record) myListView.getItemAtPosition(position);
        intent.putExtra(MyDB.RECORD_TITLE,record.getTitleName().trim());
        intent.putExtra(MyDB.RECORD_BODY,record.getTextBody().trim());

        intent.putExtra(MyDB.RECORD_ID,record.getId().toString().trim());
        intent.putExtra(MyDB.RECORD_TIME_LONG,record.getLongCreateTime().toString().trim());
        // 如果存在提醒时间
        if (record.getLongNoticeTime()!=null) {
            // 存入提醒时间到extra中
            intent.putExtra(MyDB.NOTICE_TIME_LONG,record.getLongNoticeTime().toString().trim());
        }
        // 进入编辑页面
        this.startActivity(intent);
        MainActivity.this.finish();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        // 长按点击监听
        Record record = (Record) myListView.getItemAtPosition(position);
        // 弹出对话框
        showDialog(record,position);
        return true;
    }

    /*
     * 删除操作询问框
     * @param record
     * @param position
     */
    void showDialog(final Record record,final int position){
        // 通过工厂类创建dialog对象
        final AlertDialog.Builder dialog =
                new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle("是否删除？");
        // 获取笔记的内容
        String titleName = record.getTitleName();
        // 如果笔记的内容大于10字符则使用...
        dialog.setMessage(
                titleName.length() > 10 ? titleName.substring(0,11) + "..." : titleName);
        dialog.setPositiveButton("删除",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SQLiteDatabase db = myDB.getWritableDatabase();
                        // 删除对应的记录
                        db.delete(MyDB.TABLE_NAME_RECORD,
                            MyDB.RECORD_ID +"=?",
                            new String[]{String.valueOf(record.getId())});
                        db.close();
                        // 删除列表上的记录
                        myBaseAdapter.removeItem(position);
                        myListView.post(new Runnable() {
                            @Override
                            public void run() {
                                // 通知数据更新
                                myBaseAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                });
        // 对话框设置取消按钮
        dialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        dialog.show();
    }

    /*
     * ListView里的组件包装类
     */
    class ViewHolder{
        TextView titleView;
        TextView bodyView;
        TextView timeView;
    }

    /*
     * ListView展示的适配器类
     */
    class MyBaseAdapter extends BaseAdapter{
        private List<Record> recordList;//数据集合
        private Context context;
        private int layoutId;

        private MyBaseAdapter(Context context, List<Record> recordList, int layoutId){
            this.context = context;
            this.recordList = recordList;
            this.layoutId = layoutId;
        }

        @Override
        public int getCount() {
            // 获取数量
            if (recordList!=null&&recordList.size()>0)
                return recordList.size();
            else
                return 0;
        }

        @Override
        public Object getItem(int position) {
            // 获取对应的实体类
            if (recordList!=null&&recordList.size()>0)
                return recordList.get(position);
            else
                return null;
        }

        public void removeItem(int position){
            // 删除对应的实体类
            this.recordList.remove(position);
        }

        @Override
        public long getItemId(int position) {
            // 获取item的id
            return position;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                // 绑定list_item自定义列表框
                convertView = LayoutInflater.from(
                        getApplicationContext()).inflate(R.layout.list_item, parent,
                        false);
                viewHolder  = new ViewHolder();
                // 绑定自定义item的布局
                viewHolder.titleView = convertView.findViewById(R.id.list_item_title);
                viewHolder.bodyView = convertView.findViewById(R.id.list_item_body);
                viewHolder.timeView = convertView.findViewById(R.id.list_item_time);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            // 通过位置参数来获取record实体类
            Record record = recordList.get(position);
            String tile = record.getTitleName();
            // 将每条item的数据渲染到列表框上
            // 标题＞5则用...代替
            viewHolder.titleView.setText((tile.length()>5?tile.substring(0,6)+"...":tile));
            // 将数据库日期格式改为Long
            //  viewHolder.titleView.setText(tile);
            String body = record.getTextBody();
            // 内容>13则用...代替
            viewHolder.bodyView.setText(body.length()>13?body.substring(0,12)+"...":body);
            //  将数据库日期格式改为Long
            // 将日期转换成yyyy-MM-dd HH:mm格式
            Date date = new Date(record.getLongCreateTime());
            String str = getTimeStr(date);
            if (record.getLongNoticeTime()!=null){
                Date d = new Date(record.getLongNoticeTime());
                str = str + "  🔔";
            }
            viewHolder.timeView.setText(str);
            return convertView;
        }
    }

}

