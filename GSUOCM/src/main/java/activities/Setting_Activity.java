package activities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

import org.ksoap2.serialization.SoapObject;

import convert.Converts;
import views.AppManager;

import com.suntrans.whu.R;

import Adapter.SectionListAdapter;
import WebServices.jiexi;
import WebServices.soap;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Setting_Activity extends Activity {
	private LinearLayout back;     //返回键
	private String UserID="";   //用户账号
	private String Role="";     //用户角色号
	private TextView tx0,tx1,tx2,tx3,tx4;   //功能选项
	private LinearLayout update;   //更新版本选项
	private TextView version;  //当前版本号
	private ImageView img_new;  //显示有新版本的图标 
	private int versioncode=1000;     //当前版本
	private String versionname="";   //版本号	
	private ArrayList<Map<String,String>> datalist_version=new ArrayList<Map<String,String>>(); //存放最新版本信息
	private Handler handler1=new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==1)         //代表请求数据成功
            {
				if(datalist_version.size()>0) {
					Map<String, String> map = datalist_version.get(0);
					if (Integer.valueOf(map.get("VersionCode")) > versioncode)   //如果最新版本号大于现在的版本
						img_new.setVisibility(View.VISIBLE);
				}
	            update.setClickable(true);
            }
            else if(msg.what==0)         //代表请求数据失败
            {
            	update.setClickable(true);
            }           
        }};
        private Handler handler2=new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what==1)         //代表请求数据成功
                {
					if(datalist_version.size()>0) {
						if (Integer.valueOf(datalist_version.get(0).get("VersionCode")) > versioncode)   //如果最新版本号大于现在的版本
						{
							final AlertDialog.Builder builder = new AlertDialog.Builder(Setting_Activity.this);
							builder.setTitle("更新" + datalist_version.get(0).get("VersionName"));
							builder.setMessage(datalist_version.get(0).get("Description"));
							builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int whichButton) {
									Intent intent = new Intent();
									String sUrl = datalist_version.get(0).get("URL");
									intent.setData(Uri.parse(sUrl));
									intent.setAction(Intent.ACTION_VIEW);
									//intent.setClassName("com.android.browser","com.android.browser.BrowserActivity");  //设置打开的浏览器
									//注释掉上面一行，则选择系统默认浏览器打开
									startActivity(intent);
								}
							});
							builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int whichButton) {
								}
							});
							builder.create().show();
							update.setClickable(true);
						}
						else   //否则，提示当前已经是最新版本，不需要更新
						{
							Toast.makeText(getApplication(), "当前应用已经是最新版本", Toast.LENGTH_SHORT).show();
							update.setClickable(true);
						}
					}
					else   //否则，提示当前已经是最新版本，不需要更新
					{
						Toast.makeText(getApplication(), "当前应用已经是最新版本", Toast.LENGTH_SHORT).show();
						update.setClickable(true);
					}	           
                }
                else if(msg.what==0)         //代表请求数据失败
                {
                	update.setClickable(true);
                }           
            }};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		requestWindowFeature(Window.FEATURE_NO_TITLE);  //去标题栏
		setContentView(R.layout.setting);      //设置activity布局文件
		Intent intent=getIntent();
		UserID=intent.getStringExtra("UserID");
		Role=intent.getStringExtra("Role");
		back=(LinearLayout)findViewById(R.id.layout_back);
		AppManager.getAppManager().addActivity(this);
		tx0=(TextView)findViewById(R.id.tx0);   //修改手机号
		tx1=(TextView)findViewById(R.id.tx1);   //修改密码
		tx2=(TextView)findViewById(R.id.tx2);   //关于
		tx3=(TextView)findViewById(R.id.tx3);   //注销登录
		tx4=(TextView)findViewById(R.id.tx4);	//退出程序
		version=(TextView)findViewById(R.id.version);	 //当前版本号
		img_new=(ImageView)findViewById(R.id.img_new);   //”新“字图标
		update=(LinearLayout)findViewById(R.id.update);    //更新
		PackageManager pm = getApplicationContext().getPackageManager();//context为当前Activity上下文 
		PackageInfo pi;
		try {
			pi = pm.getPackageInfo(getApplicationContext().getPackageName(), 0);
			versionname = pi.versionName;
			versioncode = pi.versionCode;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		version.setText("当前版本v"+versionname);   //设置当前版本信息的显示
		//返回键监听
		back.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}});	
		//修改手机号
		tx0.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method 
				//关闭所有activity				
				Intent intent=new Intent();
				intent.putExtra("UserID", UserID);
				intent.putExtra("Role", Role);
				intent.setClass(Setting_Activity.this, PhoneNum_Activity.class);				
				startActivity(intent);
				overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);//设置activity切换效果
									
			}});					
		//修改密码
		tx1.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method 							
				Intent intent=new Intent();
				intent.putExtra("UserID", UserID);
				intent.putExtra("Role", Role);
				intent.setClass(Setting_Activity.this, Password_Activity.class);				
				startActivity(intent);
				overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);//设置activity切换效果
									
			}});
		//版本更新
		update.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method 
				update.setClickable(false);	
				if(datalist_version.size()>0)   //如果已经获取到了新版本的信息
				{
					if(Integer.valueOf(datalist_version.get(0).get("VersionCode"))>versioncode)   //如果最新版本号大于现在的版本
					{
						final AlertDialog.Builder builder = new AlertDialog.Builder(Setting_Activity.this);
						builder.setTitle("更新"+datalist_version.get(0).get("VersionName")); 
						builder.setMessage(datalist_version.get(0).get("Description"));
						builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {  
						         public void onClick(DialogInterface dialog, int whichButton) {
						        	 Intent intent = new Intent();
						        	 String sUrl=datalist_version.get(0).get("URL");							
						        	 intent.setData(Uri.parse(sUrl));
									 intent.setAction(Intent.ACTION_VIEW);
									 //intent.setClassName("com.android.browser","com.android.browser.BrowserActivity");  //设置打开的浏览器 
									 //注释掉上面一行，则选择系统默认浏览器打开
									 startActivity(intent);									
						         	}
								 });
						  builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {  
						         public void onClick(DialogInterface dialog, int whichButton) {  						        	
						         }  
						     });  
						  builder.create().show();
						  update.setClickable(true);
					}
					else   //否则，提示当前已经是最新版本，不需要更新
					{
						Toast.makeText(getApplication(), "当前应用已经是最新版本", Toast.LENGTH_SHORT).show();
						update.setClickable(true);
					}
				}
				else   //如果没有，重新获取信息
				{
					new Thread(){
						public void run(){    //访问最新版本信息
							try{
								SoapObject result=soap.Inquiry_Version("Android");
								datalist_version=jiexi.inquiry_version(result);
								Message msg=new Message();
								msg.what=1;   //成功
								handler2.sendMessage(msg);
							}
							catch(Exception e)
							{
								Message msg=new Message();
								msg.what=0;   //失败
								handler2.sendMessage(msg);
							}
						}
					}.start();
				}
				
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
				intent.setClass(Setting_Activity.this, LogIn_Activity.class);
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
		new MainThread().start();
		//设置通知栏半透明
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			Converts.setTranslucentStatus(Setting_Activity.this,true);
		}
	}
	public class MainThread extends Thread{
		public void run(){    //访问最新版本信息
			try{
				SoapObject result=soap.Inquiry_Version("Android");
				datalist_version=jiexi.inquiry_version(result);
				Message msg=new Message();
				msg.what=1;   //成功
				handler1.sendMessage(msg);
			}
			catch(Exception e)
			{
				Message msg=new Message();
				msg.what=0;   //失败
				handler1.sendMessage(msg);
			}
		}
	}
}
