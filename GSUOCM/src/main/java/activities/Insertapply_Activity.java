package activities;

import java.util.ArrayList;
import java.util.Map;

import views.AppManager;

import com.suntrans.whu.R;

import Adapter.SectionListAdapter;
import Adapter.TouchListener;
import WebServices.soap;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Insertapply_Activity extends Activity {
	private ImageView back;   //返回键
	private EditText apply;  //申请内容
	private Button submit;  //提交申请按钮
	private String StudentID="";   //学号
	private Handler handler1=new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==1)         //代表提交成功
            {
            	Toast.makeText(getApplication(), "提交成功!", Toast.LENGTH_SHORT).show();
            	finish();
            }
            else if(msg.what==0)         //代表提交失败
            {
            	Toast.makeText(getApplication(), "提交失败!", Toast.LENGTH_SHORT).show();
            	submit.setClickable(true);
            }
         
        }};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.apply_insert);     //设置布局文件
		Intent intent=getIntent();
		StudentID=intent.getStringExtra("StudentID");		 //获取传入的学号
		AppManager.getAppManager().addActivity(this);		
		apply=(EditText)findViewById(R.id.apply);     //申请内容
		back=(ImageView)findViewById(R.id.back);	 //返回按钮
		submit=(Button)findViewById(R.id.submit);  //提交按钮
		submit.setOnTouchListener(new TouchListener());  //设置触摸效果
		//返回按键点击监听
		back.setOnClickListener(new OnClickListener(){	
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}			
		});
		//提交按钮点击监听
		submit.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				submit.setClickable(false);  //设置按钮不能点击
				final String application=apply.getText().toString();
				if(application.equals(""))    //如果申请内容为空，提示不能为空，不进行提交
				{
					Toast.makeText(getApplication(), "申请理由不能为空!", Toast.LENGTH_SHORT).show();
					submit.setClickable(true);
				}
				else   //进行提交
				{
					new Thread(){
						@Override
						public void run(){
							String result=soap.Insert_Application(StudentID, application);
							if(result.equals("1"))
							{
								Message msg=new Message();
								msg.what=1;
								handler1.sendMessage(msg);
							}
							else
							{
								Message msg=new Message();
								msg.what=1;
								handler1.sendMessage(msg);
							}
						}
					}.start();
				}
			}});
	}
}
