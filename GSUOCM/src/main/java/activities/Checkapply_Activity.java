package activities;

import java.util.ArrayList;
import java.util.Map;

import org.ksoap2.serialization.SoapObject;

import views.AppManager;

import com.suntrans.whu.R;

import WebServices.jiexi;
import WebServices.soap;
import activities.All_Activity.MainThread;
import activities.All_Activity.Thread1;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class Checkapply_Activity extends Activity {
	private ImageView back;   //返回键
	private ListView list;  //申请内容列表
	private String UserID;  //管理员账号
	private String Role;   //管理员角色号
	private LinearLayout layout_loading,layout_failure;   //加载中。。；加载失败。
	private ArrayList<Map<String,String>> datalist_application=new ArrayList<Map<String,String>>();  //申请列表内容
	private ArrayList<Map<String,String>> datalist_userinfo=new ArrayList<Map<String,String>>();  //用户信息
	private Handler handler1=new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==1)         //代表请求数据成功
            {          
            	ListInit();  //初始化列表
            	layout_loading.setVisibility(View.GONE);
            	layout_failure.setVisibility(View.GONE);	                  
            }
            else if(msg.what==-1)          
            {            	         	
            	new Thread1().start();
            }
            else if(msg.what==0)         //代表请求数据失败
            {
            	layout_loading.setVisibility(View.GONE);
            	layout_failure.setVisibility(View.VISIBLE);
            	//Toast.makeText(getActivity(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
            }
            else if(msg.what==2)         //代表开始请求
            {
            	layout_loading.setVisibility(View.VISIBLE);
            	layout_failure.setVisibility(View.GONE);
            }
        }};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.apply_check);     //设置布局文件
		Intent intent=getIntent();
		UserID=intent.getStringExtra("UserID");  //获取管理员账号
		Role=intent.getStringExtra("Role");    //获取管理员角色
		AppManager.getAppManager().addActivity(this);
		list=(ListView)findViewById(R.id.list);			
		back=(ImageView)findViewById(R.id.back);		
		layout_loading=(LinearLayout)findViewById(R.id.layout_loading);   //加载中。。
    	layout_failure=(LinearLayout)findViewById(R.id.layout_failure);    //加载失败
		back.setOnClickListener(new OnClickListener(){	
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}			
		});
		//点击加载失败，重新加载数据
		layout_failure.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				layout_loading.setVisibility(View.VISIBLE);
				layout_failure.setVisibility(View.GONE);
				new MainThread().start();    //开始新的刷新线程
			}});				
		
	}
	public class MainThread extends Thread{
		public void run(){
			SoapObject result=new SoapObject();
			try
	   		 {
				 if(Role.equals("5"))  //如果是楼长，则只查询其所管理的楼栋，先查询楼长信息
				 {
	    			 result=soap.Inquiry_UserInfo(UserID);
	    			 datalist_userinfo=jiexi.inquiry_staffinfo(result);
				 }	 
				 else if(Role.equals("6"))  //如果是宿管，则只查询其所管理的楼栋，先查询宿管信息
				 {
	    			 result=soap.Inquiry_UserInfo(UserID);
	    			 datalist_userinfo=jiexi.inquiry_boomerinfo(result);
				 }	
				 Message msg=new Message();
    			 msg.what=-1;    //代表成功，但是没有获取房间开关状态，所以不进行ListInit
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
	public class Thread1 extends Thread{
		public void run(){
			SoapObject result=new SoapObject();
			try
	   		 {
				 if(Role.equals("5"))  //如果是楼长，获取这个楼栋的学生申请
				 {
	    			 result=soap.Inquiry_Application("50", datalist_userinfo.get(0).get("Area"), datalist_userinfo.get(0).get("Building"), "");
	    			 datalist_application=jiexi.inquiry_application(result);
				 }	 
				 else if(Role.equals("6"))  //如果是宿管，获取这个单元（楼栋）的学生申请
				 {
	    			 result=soap.Inquiry_Application("50", datalist_userinfo.get(0).get("Area"), datalist_userinfo.get(0).get("Building"), datalist_userinfo.get(0).get("Unit").replace("anyType{}", ""));
	    			 datalist_application=jiexi.inquiry_application(result);
				 }	
				 else
				 {
					 result=soap.Inquiry_Application("50", "", "", "");   //获取全部学生的申请
					 datalist_application=jiexi.inquiry_application(result);
				 }
				 Message msg=new Message();
    			 msg.what=1;    //代表成功，但是没有获取房间开关状态，所以不进行ListInit
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
	public void ListInit(){
		
	}
	
}
