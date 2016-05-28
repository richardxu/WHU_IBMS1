package com.suntrans.suntranswater;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Map;

import WebServices.soap;
import convert.Converts;
import database.DbHelper;


public class Welcome_Activity extends Activity {
	private String UserID="";
	private int flag=0;   //为0表示正常打开，为1表示从其他应用传入
	private String str="";   //存储参数信息
	private long time;
	private LinearLayout layout1,layout2;
//	ArrayList<Map<String,String>> datalist_userinfo=new ArrayList<Map<String,String>>();
	private ArrayList<Map<String,String>> datalist_room=new ArrayList<Map<String,String>>();   //所有房间
	private ArrayList<Map<String,String>> datalist_stu=new ArrayList<Map<String,String>>();   //所有学生
    Handler handler2=new Handler(){   //从其他平台打开的本应用，用于判断传递过来的用户是否属于系统，根据角色号跳转到相应页面
        public void handleMessage(Message msg) 
        {
            super.handleMessage(msg);
            if(msg.what==1)    //验证成功
            {
				Intent intent=new Intent();
				intent.putExtra("Name", UserID);   //学号
				intent.setClass(Welcome_Activity.this, All_Activity.class);//设置要跳转的activity
				startActivity(intent);//开始跳转
				finish();
				overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);//设置activity切换效果

            }
            else if(msg.what==0)    //验证失败
            {
            	Intent intent=new Intent();   //跳转到登录页面                        
                intent.setClass(Welcome_Activity.this, Login_Activity.class);
                startActivity(intent);
                finish();
				overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);//设置activity切换效果
            	Toast.makeText(getApplicationContext(), "网络错误！", Toast.LENGTH_LONG).show();
            }
        }
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.welcome);      //设置activity布局文件
		new MainThread().start();
		//    设置通知栏半透明
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			Converts.setTranslucentStatus(Welcome_Activity.this,true);
		}
	}
	

	 public class MainThread extends Thread{
		   	@Override
		   	 public void run()
		   	 {
		   		DbHelper dh1=new DbHelper(Welcome_Activity.this);
	    		SQLiteDatabase db = dh1.getWritableDatabase(); 
	    		//查询是否有自动登录的账号
	    		final Cursor cursor = db.query("users_tb", new String[]{"NID","Name","Password"}, "IsUsing=? and Auto=?", new String[]{"1","1"}, null, null, null);
	        	if(cursor.getCount()<1)    //如果没有，直接跳转到登录页面
	        	{
					db.close();
	        		Intent intent=new Intent();   //跳转到登录页面                        
	                intent.setClass(Welcome_Activity.this, Login_Activity.class);
	                startActivity(intent);
					finish();
					overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);//设置activity切换效果

	        	}
	        	else     //如果有的话，调用登录接口函数进行验证，验证成功则跳转到主页面，否则进入登录页面
	        	{    	
	        		try {
						String UserID = "";
						String Password = "";
						while (cursor.moveToNext()) {
							UserID = cursor.getString(1);     //从数据库中获取用户账号
							Password = cursor.getString(2);  //从数据库中获取用户密码
						}
						db.close();
						String Name = soap.LogIn(UserID, Password);
						time = System.currentTimeMillis() - time;
						Log.i("Time", "时间3==>" + time);
						if (Name.equals("0"))   //如果验证失败
						{
							Intent intent = new Intent();   //跳转到登录页面
							intent.setClass(Welcome_Activity.this, Login_Activity.class);
							startActivity(intent);
							overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);//设置activity切换效果
							finish();
						}
						else //验证成功
						{
							Intent intent = new Intent();
							intent.putExtra("UserName",UserID);  //登录名
							intent.putExtra("Name", Name);   //用户真实姓名
							intent.setClass(Welcome_Activity.this, All_Activity.class);//设置要跳转的activity
							startActivity(intent);//开始跳转
							overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);//设置activity切换效果
							finish();
						}
					}
					catch(Exception e)
					{
						
						Intent intent=new Intent();   //跳转到登录页面                        
	                    intent.setClass(Welcome_Activity.this, Login_Activity.class);
	                    startActivity(intent);
	                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);//设置activity切换效果
	                    finish(); 
					}
	        		        	           
	        	}
		   	 }
		 }

}
