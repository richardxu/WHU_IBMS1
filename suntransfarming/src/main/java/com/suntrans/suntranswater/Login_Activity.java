package com.suntrans.suntranswater;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import convert.PinyinUtils;
import database.DataCentre;
import database.DbHelper;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class Login_Activity extends AppCompatActivity  {



    // UI references.
    private EditText text_name;
    private EditText text_password;
    private Button btn_login;
    private CheckBox remember;
    private CheckBox auto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        text_name = (EditText) findViewById(R.id.name);    //用户名输入框
        text_password = (EditText) findViewById(R.id.password);    //密码输入框
        remember = (CheckBox) findViewById(R.id.remember);   //是否记住密码
        auto = (CheckBox) findViewById(R.id.auto);    //是否自动登录
        btn_login = (Button) findViewById(R.id.login);   //登录按钮

        btn_login.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(),"点击事件",Toast.LENGTH_SHORT).show();
                if(text_name.getText().toString().equals("")||text_name.getText()==null)
                {
                    Toast.makeText(getApplicationContext(),"账号不能为空！",Toast.LENGTH_SHORT).show();
                }
                else if(text_password.getText().toString().equals("")||text_password.getText()==null)
                {
                    Toast.makeText(getApplicationContext(),"密码不能为空！",Toast.LENGTH_SHORT).show();
                }
                else {
                    btn_login.setClickable(false);    //设置登录按钮不能点击
                    Toast.makeText(getApplicationContext(), "正在登录中。。。", Toast.LENGTH_SHORT).show();
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("Name",text_name.getText().toString());   //获取用户输入的用户名
                    map.put("Password", text_password.getText().toString());   //获取用户输入的密码
                    new LoginTask().execute(new Map[]{map});   //执行登录任务
                }
            }
        });
        //从用户表中查找账号和密码信息
        DbHelper dh1=new DbHelper(Login_Activity.this);
        SQLiteDatabase db = dh1.getWritableDatabase();
        Cursor cursor = db.query(true, "users_tb", new String[]{"NID","Name","Password","IsUsing","Remember","Auto"}, null, null, null, null, null, null, null);
        while(cursor.moveToNext())
        {
            if(cursor.getString(3).equals("1"))
            {
                if(cursor.getString(4).equals("1"))   //如果之前选择记住密码，则账号和密码都显示
                {
                    text_name.setText(cursor.getString(1));
                    text_password.setText(cursor.getString(2));
                    text_password.requestFocus();    //输入密码获取焦点

                }
                else                //如果没有记住密码，只显示账号，用户去输入密码
                {
                    text_name.setText(cursor.getString(1));
                    text_password.requestFocus();    //输入密码获取焦点
                }
                remember.setChecked(cursor.getString(4).equals("1")?true:false);
                auto.setChecked(cursor.getString(5).equals("1")?true:false);
            }

        }

        //    设置通知栏半透明
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }
    }


    /***
     * 异步登录的task，该task只能执行一次，不能多次调用
     * AsyncTask第一个参数是doInBackground接收的参数，第二个为显示进度的参数，第三个参数为DoInBackground返回和onPostExcute传入的参数
     */
    private class LoginTask extends AsyncTask<Map<String,String>,Integer,String>{

        @Override
        protected void onPreExecute(){   //主线程调用

        }

        @Override
        protected String doInBackground(Map<String,String>... params) {    //后台进行，返回的结果传入onPostExecute 。可以使用publishProgress(Progress...)来发布一个或多个进度单位(unitsof progress)
            String result = "-1";
            try{
                result=WebServices.soap.LogIn(params[0].get("Name"),params[0].get("Password"));
            }
            catch(Exception e)
            {
                result="-1";
            }
            return result;
        }

        @Override
        protected void  onProgressUpdate(Integer... Progress) {//在publishProgress(Progress...)调用后由主线程执行

        }

        @Override
        protected void onPostExecute(String result){  //计算结束后调用
            super.onPostExecute(result);
           // Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();
            if(result.equals("0")){   //验证失败
                Toast.makeText(getApplicationContext(),"验证失败！",Toast.LENGTH_SHORT).show();
                btn_login.setClickable(true);
            }
            else if(result.equals("-1"))   //网络错误
            {
                Toast.makeText(getApplicationContext(),"网络错误！",Toast.LENGTH_SHORT).show();
                btn_login.setClickable(true);
            }
            else{     //验证成功
                //先将数据存入数据库，然后根据角色号跳转到主页面
                DbHelper dh1=new DbHelper(Login_Activity.this);
                SQLiteDatabase db = dh1.getWritableDatabase();
                Cursor cursor = db.query("users_tb", new String[]{"Password"},"Name=?", new String[]{text_name.getText().toString()}, null, null, null);
                if(cursor.getCount()<1)   //如果数据库中不存在此用户名，则向用户表中添加这个账号
                {
                    ContentValues cv = new ContentValues();    //内容数组
                    cv.put("Name", text_name.getText().toString());   //用户名
                    cv.put("Password",text_password.getText().toString());   //密码
                    cv.put("IsUsing","1");   //是否正在使用
                    cv.put("Auto",auto.isChecked()?"1":"0");   //是否自动登录
                    cv.put("Remember",remember.isChecked()?"1":"0");   //是否记住密码
                    ContentValues cv1 = new ContentValues();    //内容数组
                    cv1.put("IsUsing","0");   //是否正在使用
                    db.update("users_tb",cv1,null,null);    //更新数据库数据，先把所有的IsUsing置0，然后将正在登录的账号的IsUsing置1
                    long a=db.insert("users_tb", null, cv);    //插入到表中
                }
                else
                {
                    while(cursor.moveToNext())      //如果存在，就更新数据库
                    {
                        ContentValues cv = new ContentValues();    //内容数组
                        cv.put("Password",text_password.getText().toString());   //更新密码
                        cv.put("IsUsing","1");   //是否正在使用
                        cv.put("Auto",auto.isChecked()?"1":"0");   //是否自动登录
                        cv.put("Remember",remember.isChecked()?"1":"0");   //是否记住密码
                        ContentValues cv1 = new ContentValues();    //内容数组
                        cv1.put("IsUsing","0");   //是否正在使用
                        db.update("users_tb",cv1,null,null);    //更新数据库数据，先把所有的IsUsing置0，然后将正在登录的账号的IsUsing置1
                        db.update("users_tb",cv,"Name=?",new String[]{text_name.getText().toString()});   //更新用户表数据
                    }
                }


                Intent intent=new Intent();
                intent.putExtra("UserName",text_name.getText().toString());   //登录名
                intent.putExtra("Name", result);  //用户名
                intent.setClass(Login_Activity.this, All_Activity.class);//设置要跳转的activity
                startActivity(intent);//开始跳转
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);//设置activity切换效果
                finish();

            }

        }

        @Override
        protected void onCancelled(){   //在调用AsyncTask的cancel()方法时调用

        }
    }

    @TargetApi(19)   //屏幕状态栏进行透明化处理
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

}

