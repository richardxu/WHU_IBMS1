package activities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;

import org.ksoap2.serialization.SoapObject;

import convert.Converts;
import views.AppManager;

import com.suntrans.whu.R;


import Adapter.TouchListener;
import WebServices.jiexi;
import WebServices.soap;
import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Password_Activity extends Activity {
	private EditText pa1,pa2,pa3;  //旧密码，新密码，确认新密码
	private Button confirm;    //确认修改按钮
	private String result="";//修改结果
	private String UserID,Role,PhoneNum;   //用户名和角色号
	private TextView userid;//当前用户
	private LinearLayout back;   //返回键
	private Handler handler1=new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==1)         //代表请求数据成功
            {            	
            	if(result.equals("1"))  //如果修改成功
            	{
            		Toast.makeText(getApplication(), "修改成功，请重新进行身份验证!", Toast.LENGTH_LONG).show();
            		Intent intent=new Intent();
    				intent.setClass(Password_Activity.this, LogIn_Activity.class);
    				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
    				startActivity(intent);
    				overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);//设置activity切换效果
    				finish();
            	}
            	else
            	{
            		Toast.makeText(getApplication(), "修改失败!", Toast.LENGTH_LONG).show();;
            		confirm.setClickable(true);
            	}
            }           
            else if(msg.what==0)         //代表请求数据失败
            {
            	Toast.makeText(getApplication(), "网络错误!", Toast.LENGTH_LONG).show();
            	confirm.setClickable(true);
            }
          
        }};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.password);     //设置布局文件
		Intent intent=getIntent();
		UserID=intent.getStringExtra("UserID");
		Role=intent.getStringExtra("Role");
		AppManager.getAppManager().addActivity(this);
		pa1=(EditText)findViewById(R.id.pa1);
		pa2=(EditText)findViewById(R.id.pa2);
		pa3=(EditText)findViewById(R.id.pa3);		
		confirm=(Button)findViewById(R.id.confirm);
		back=(LinearLayout)findViewById(R.id.layout_back);
		userid = (TextView) findViewById(R.id.userid);
		userid.setText(UserID);
		back.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
			
		});
		confirm.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String p1=pa1.getText().toString();
				String p2=pa2.getText().toString();
				String p3=pa3.getText().toString();
				if(p1.equals("")||p2.equals("")||p3.equals(""))   //如果输入为空
				{				
					Toast.makeText(getApplicationContext(),"输入不能为空！",Toast.LENGTH_SHORT).show();
				}
				else if(!p2.equals(p3))    //如果两次填写的新密码不一样
				{
					Toast.makeText(getApplicationContext(),"两次输入的新密码不一致！",Toast.LENGTH_SHORT).show();
				}
				else
				{
					new MainThread().start();
					confirm.setClickable(false);
				}
			}
			
		});
		//设置通知栏半透明
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			Converts.setTranslucentStatus(Password_Activity.this,true);
		}
	}

	 public class MainThread extends Thread{
	    	@Override
	    	 public void run()
	    	 {	    		
	    		 try
	    		 {
	    			 result=soap.Update_PWD(UserID,pa1.getText().toString(),pa2.getText().toString(), Role);			
	    			 Message msg=new Message();
	    			 msg.what=1;    //代表成功
	    			 handler1.sendMessage(msg);
	    		 }
	    		 catch(Exception e)
	    		 {
	    			 Message msg=new Message();
	    			 msg.what=0;    //代表失败
	    			 msg.obj=e.toString();
	    			 handler1.sendMessage(msg);
	    		 }
	    	       
	    	 }
	    }
		

}
