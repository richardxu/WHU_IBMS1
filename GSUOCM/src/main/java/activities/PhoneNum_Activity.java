package activities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

public class PhoneNum_Activity extends Activity {
	private EditText phone;  //新手机号
	private TextView userid;  //用户账号
	private Button confirm;    //确认修改按钮
	private String result="";//修改结果
	private String UserID,Role;   //用户名和角色号
	private LinearLayout back;   //返回键
	private Handler handler1=new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==1)         //代表请求数据成功
            {            	
            	if(result.equals("1"))  //如果修改成功
            	{
            		Toast.makeText(getApplication(), "修改成功!", Toast.LENGTH_LONG).show();
            		confirm.setClickable(true);
    				
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
		setContentView(R.layout.phonenum);     //设置布局文件
		Intent intent=getIntent();
		UserID=intent.getStringExtra("UserID");
		Role=intent.getStringExtra("Role");		
		AppManager.getAppManager().addActivity(this);
		phone=(EditText)findViewById(R.id.number);
		userid=(TextView)findViewById(R.id.userid);		
		confirm=(Button)findViewById(R.id.confirm);
		back=(LinearLayout)findViewById(R.id.layout_back);
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
				String new_phone=phone.getText().toString();				
				if(isMobileNO(new_phone)==false)   //如果手机号格式不对
				{				
					Toast.makeText(getApplicationContext(),"手机号输入格式不正确！",Toast.LENGTH_SHORT).show();
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
			Converts.setTranslucentStatus(PhoneNum_Activity.this,true);
		}
	}

	 public class MainThread extends Thread{
	    	@Override
	    	 public void run()
	    	 {	    		
	    		 try
	    		 {
	    			 result=soap.Update_PhoneNum(UserID,phone.getText().toString(), Role);	 //修改手机号		
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
	/* 是否是合法的手机号
		* 
		* @param context
		* @return
		*/
		public static boolean isMobileNO(String mobiles) {
			Pattern p = Pattern
					.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
			Matcher m = p.matcher(mobiles);
			return m.matches();
		}

}
