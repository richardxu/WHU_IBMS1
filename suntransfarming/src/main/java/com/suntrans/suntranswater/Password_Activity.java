package com.suntrans.suntranswater;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import WebServices.soap;
import views.AppManager;

public class Password_Activity extends Activity {
	private EditText pa1,pa2,pa3;  //旧密码，新密码，确认新密码
	private Button confirm;    //确认修改按钮
	private String result="";//修改结果
	private String UserName;   //用户名和角色号
	private TextView tx_username;  //当前账户
	private LinearLayout layout_back;   //返回键
	private Handler handler1=new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==1)         //代表请求数据成功
            {            	
            	if(result.equals("1"))  //如果修改成功
            	{
            		Toast.makeText(getApplication(), "修改成功，请重新进行身份验证!", Toast.LENGTH_LONG).show();
            		Intent intent=new Intent();
    				intent.setClass(Password_Activity.this, Login_Activity.class);
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
		UserName=intent.getStringExtra("UserName");
		AppManager.getAppManager().addActivity(this);
		tx_username = (TextView) findViewById(R.id.tx_username);
		pa1=(EditText)findViewById(R.id.pa1);
		pa2=(EditText)findViewById(R.id.pa2);
		pa3=(EditText)findViewById(R.id.pa3);		
		confirm=(Button)findViewById(R.id.confirm);
		layout_back=(LinearLayout)findViewById(R.id.layout_back);
		tx_username.setText(UserName);
		layout_back.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
				overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);   //activity切换动画
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
	 public class MainThread extends Thread{
	    	@Override
	    	 public void run()
	    	 {	    		
	    		 try
	    		 {
	    			 result=soap.Update_Password(UserName,pa1.getText().toString(),pa2.getText().toString());
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
