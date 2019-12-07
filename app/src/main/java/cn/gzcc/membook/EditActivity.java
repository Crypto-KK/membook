package cn.gzcc.membook;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import android.widget.EditText;
import android.widget.TextView;

import android.widget.Toast;

import java.util.Date;

import cn.gzcc.membook.db.MyDB;
import cn.gzcc.membook.entity.Record;
import cn.gzcc.membook.utils.MyFormat;


public class EditActivity extends AppCompatActivity implements View.OnClickListener{

    MyDB myDB;
    private Button btnSave;
    private Button btnBack;
    private TextView amendTime;
    private TextView amendTitle;
    private EditText amendBody;
    private Record record;
    private AlertDialog.Builder dialog;

    private Button btnUpcoming;
    private Button btnNotice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_layout);
        init();

    }

    /*
     * 初始化函数
     */
    void init(){
        myDB = new MyDB(this);
        btnBack = findViewById(R.id.button_back);
        btnSave = findViewById(R.id.button_save);
        amendTitle = findViewById(R.id.amend_title);
        amendBody = findViewById(R.id.amend_body);
        amendTime = findViewById(R.id.amend_title_time);


        btnSave.setOnClickListener(this);
        btnBack.setOnClickListener(this);

        Intent intent = this.getIntent();
        if (intent!=null){
            // 如果能从intent中获取到上一个页面传来的值，则创建该实体类，并渲染到页面中
            record = new Record();

            record.setId(Integer.valueOf(intent.getStringExtra(MyDB.RECORD_ID)));
            record.setTitleName(intent.getStringExtra(MyDB.RECORD_TITLE));
            record.setTextBody(intent.getStringExtra(MyDB.RECORD_BODY));
            record.setLongCreateTime(Long.valueOf(intent.getStringExtra(MyDB.RECORD_TIME_LONG)));

            amendTitle.setText(record.getTitleName());
            String str="";
            if (intent.getStringExtra(MyDB.NOTICE_TIME_LONG)!=null){
                record.setLongNoticeTime(Long.valueOf(intent.getStringExtra(MyDB.NOTICE_TIME_LONG)));
                //  str = "    提醒时间："+record.getNoticeTime();

                //  MyFormat.getTimeStr判断是否去除年份
                str = "    提醒时间："+ MyFormat.getTimeStr(new Date(record.getLongNoticeTime()));
            }
            amendTime.setText(MyFormat.getTimeStr(new Date(record.getLongCreateTime()))+str);
            amendBody.setText(record.getTextBody());
        }
    }

    @Override
    public void onClick(View v) {
        String body;
        body = amendBody.getText().toString();
        switch (v.getId()){
            // 更新按钮
            case R.id.button_save:
                if (updateFunction(body)){
                    intentStart();
                }
                break;
                // 返回按钮
            case R.id.button_back:
                showDialog(body);
                clearDialog();
                break;
            default:
                break;
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //当返回按键被按下
            if (!isShowIng()){
                showDialog(amendBody.getText().toString());
                clearDialog();
            }
        }
        return false;
    }


    /*
     * 返回主界面
     */
    void intentStart(){
        Intent intent = new Intent(EditActivity.this,MainActivity.class);
        startActivity(intent);
        this.finish();
    }

    /*
     * 保存函数
     */
    boolean updateFunction(String body){

        SQLiteDatabase db;
        ContentValues values;

        boolean flag = true;
        if (body.length()>200){
            Toast.makeText(this,"内容过长",Toast.LENGTH_SHORT).show();
            flag = false;
        }
        if(flag){
            db = myDB.getWritableDatabase();
            values = new ContentValues();
            values.put(MyDB.RECORD_BODY,body);
            values.put(MyDB.RECORD_TIME_LONG,new Date(System.currentTimeMillis()).getTime());
            // 执行sql的update语句
            db.update(MyDB.TABLE_NAME_RECORD,values,MyDB.RECORD_ID +"=?",
                    new String[]{record.getId().toString()});
            Toast.makeText(this,"修改成功",Toast.LENGTH_SHORT).show();
            // 关闭db资源
            db.close();
        }
        return flag;
    }

    /*
     * 弹窗函数
     * @param title
     * @param body
     * @param createDate
     */
    void showDialog(final String body){
        dialog = new AlertDialog.Builder(EditActivity.this);
        dialog.setTitle("提示");
        dialog.setMessage("是否保存当前编辑内容");
        // 点击了保存按钮
        dialog.setPositiveButton("保存",
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updateFunction(body);
                intentStart();
                    }
                });

        dialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                intentStart();
                    }
                });
        dialog.show();
    }

    void clearDialog(){
        dialog = null;
    }

    boolean isShowIng(){
        if (dialog!=null){
            return true;
        }else{
            return false;
        }
    }

}
