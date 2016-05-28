package com.suntrans.suntranswater;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.Map;

import WebServices.jiexi;
import WebServices.soap;
import views.AppManager;
import views.Line;

public class Setting_Activity extends Activity {
	private LinearLayout layout_back;     //返回键
	private String UserName="";   //用户账号
	private TextView tx1,tx2,tx3,tx4;   //功能选项

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		requestWindowFeature(Window.FEATURE_NO_TITLE);  //去标题栏
		setContentView(R.layout.setting);      //设置activity布局文件
		Intent intent=getIntent();
		UserName=intent.getStringExtra("UserName");
		layout_back=(LinearLayout) findViewById(R.id.layout_back);
		AppManager.getAppManager().addActivity(this);
		tx1=(TextView)findViewById(R.id.tx1);   //修改密码
		tx2=(TextView)findViewById(R.id.tx2);   //关于
		tx3=(TextView)findViewById(R.id.tx3);   //注销登录
		tx4=(TextView)findViewById(R.id.tx4);	//退出程序
		//返回键监听
		layout_back.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
				overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
			}});	

		//修改密码
		tx1.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method 							
				Intent intent=new Intent();
				intent.putExtra("UserName", UserName);
				intent.setClass(Setting_Activity.this, Password_Activity.class);				
				startActivity(intent);
				overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);//设置activity切换效果
									
			}});

		//关于
		tx2.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method st								
				Intent intent=new Intent();
				intent.setClass(Setting_Activity.this, About_Activity.class);				
				startActivity(intent);
				overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);//设置activity切换效果
				
					
			}});				
		//注销登录
		tx3.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method st							
				Intent intent=new Intent();
				intent.setClass(Setting_Activity.this, Login_Activity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);//设置activity切换效果
				finish();
					
			}});
		//退出程序
		tx4.setOnClickListener(new OnClickListener(){  
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method st
				//关闭所有activity
				AppManager.getAppManager().AppExit(Setting_Activity.this);
			}});
	}
	@Override
	public boolean onKeyDown(int keyCode,KeyEvent event){
		if(keyCode==KeyEvent.KEYCODE_BACK)   //如果按下的是返回键
		{
			finish();
			overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);   //设置activity切换动画
			return true;
		}
		else
			return super.onKeyDown(keyCode, event);
	}

}
