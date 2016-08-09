package activities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

import org.ksoap2.serialization.SoapObject;

import convert.Converts;
import views.AppManager;
import views.KTFragment;
import views.ZMFragment;
import views.ZoomOutPageTransformer;
import WebServices.jiexi;
import WebServices.soap;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.suntrans.whu.R;

public class Dorm_Activity extends AppCompatActivity implements Serializable{
	private TextView tx_recharge;   //充值缴费
	private String UserID=""; // 管理员账号
	private String StudentID="";   //学号
	private String Role="";    //角色号
	private String RoomType="";   //房间类型
	private String RoomID="";  //房间ID
	private String RoomInfo="";   //房间详细信息
	private LinearLayout back;   //返回键，默认不显示，如果是管理员页面跳转过来，则显示
	private LinearLayout setting;  //设置键，如果用户是学生，就显示设置键，否则显示返回键
	private TextView tx_zm;      //标题栏，照明和空调两个textview
	private ViewPager vPager;
	private ZMFragment page_zm;
	private KTFragment page_kt;
	private ArrayList<Map<String,String>> datalist_userinfo=new ArrayList<Map<String,String>>();  //解析到的学生信息
	private Handler handler1=new Handler(){
        public void handleMessage(Message msg) 
        {
            super.handleMessage(msg);
            if(msg.obj!=null)
            {
	            String roomnum=msg.obj.toString();
	            tx_zm.setText(roomnum+"照明");
            }
        }
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dorm);      //设置activity布局文件
		AppManager.getAppManager().addActivity(this);
		Intent intent=getIntent();
		UserID=intent.getStringExtra("UserID");  //用户id
		StudentID=UserID;   //如果当前用户是学生，则在打开设置页面时会用到StudentID
		RoomID=intent.getStringExtra("RoomID");    //房间ID
		Role=intent.getStringExtra("Role");   //用户角色号
		RoomType=intent.getStringExtra("RoomType");  //房间类型:0学生宿舍
		RoomInfo = intent.getStringExtra("RoomInfo"); ///房间详情
		tx_zm=(TextView)findViewById(R.id.tx_zm);   //照明
		tx_recharge=(TextView)findViewById(R.id.tx_recharge);     //充值缴费
		back=(LinearLayout)findViewById(R.id.layout_back);     //返回键
		setting=(LinearLayout)findViewById(R.id.layout_setting);    //设置键
		vPager=(ViewPager)findViewById(R.id.vPager);
		vPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));    //设置适配器
		vPager.setPageTransformer(true, new ZoomOutPageTransformer());    //设置viewpager切换动画
		String[] str=RoomInfo.split("-");
		tx_zm.setText(str[str.length-1]+"照明");   //设置标题，房间号+照明
		//tx_zm.setOnClickListener(new MyOnClickListener(0));
		if(Role.equals("1")||Role.equals("2"))    //如果用户是学生，则显示设置按钮
		{
			back.setVisibility(View.GONE);
			setting.setVisibility(View.VISIBLE);
		}
		else               //如果用户是管理员，则显示返回按钮
		{
			back.setVisibility(View.VISIBLE);
			setting.setVisibility(View.GONE);
		}
		setting.setOnClickListener(new OnClickListener(){    //设置图标点击监听
			@Override
			public void onClick(View v) {
				Intent intent=new Intent();
				intent.setClass(Dorm_Activity.this, Setting_Activity.class);
				intent.putExtra("UserID", StudentID);   //账号
				intent.putExtra("Role", Role);    //角色号
				startActivity(intent);
				overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);//设置activity切换效果
				
			}			
		});
		//充值缴费
		tx_recharge.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				new Thread(){
					@Override
					public void run(){
						Intent intent = new Intent();
						SoapObject result = soap.Inquiry_Version("ReCharge");

						ArrayList<Map<String,String>> data= jiexi.inquiry_version(result);
						String sUrl=data.get(0).get("URL");	   //获取充值地址
						intent.setData(Uri.parse(sUrl));
						intent.setAction(Intent.ACTION_VIEW);
						//intent.setClassName("com.android.browser","com.android.browser.BrowserActivity");  //设置打开的浏览器
						//注释掉上面一行，则选择系统默认浏览器打开
						startActivity(intent);
					}
				}.start();


			}});
		back.setOnClickListener(new OnClickListener(){   //返回键监听
			@Override
			public void onClick(View v) {
				finish();
			}});
		//		    设置通知栏半透明
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			Converts.setTranslucentStatus(Dorm_Activity.this,true);
		}

	}
	@Override
	protected void onResume()    //重写onResume方法 
	{
		super.onResume();
		//Log.i("WHU","调用了onResume");
	}
	//viewpager适配器
	public class MyPagerAdapter extends FragmentPagerAdapter {     //viewpager适配器

				public MyPagerAdapter(FragmentManager fm) {
					super(fm);
				}

				private final String[] titles = { "照明"};

				@Override
				public CharSequence getPageTitle(int position) {
					return titles[position];    //获得与标题号对应的标题名
				}

				@Override
				public int getCount() {
					return titles.length;     //一共有几个头标
				}

				@Override
				public Fragment getItem(int position) {
					switch (position) {
					case 0:          //第一个fragment
						if (page_zm == null) {
							page_zm = new ZMFragment();
						}				
						Bundle bundle = new Bundle();
						bundle.putString("UserID", UserID);
						bundle.putString("RoomID", RoomID);
						bundle.putString("RoomType", RoomType);
						bundle.putString("Role", Role);   //当前用户的角色，可以是管理员或学生
						bundle.putString("RoomInfo",RoomInfo);  //房间详细信息
						page_zm.setArguments(bundle);
						return page_zm;
					default:
						return null;
					}
				}
				@Override  
				public int getItemPosition(Object object) {  
				    return POSITION_NONE;  
				}  
				
			}
	    
	    
	  /*  *//**
	     * 页卡切换监听
	*//*
	    public class MyOnPageChangeListener implements OnPageChangeListener {
	        @Override
	        public void onPageSelected(int arg0) {
	        	 switch(arg0)   //根据页面滑到哪一页，设置标题两个textview的背景颜色
	        	 {
	        	 	case 0:tx_zm.setBackgroundResource(R.drawable.tab_left_select);
	        	 			tx_kt.setBackgroundResource(R.drawable.tab_right_normal);
	        	 			break;
	        	 	case 1:tx_zm.setBackgroundResource(R.drawable.tab_left_normal);
		 					tx_kt.setBackgroundResource(R.drawable.tab_right_select);
		 					break;
	        	 	default:break;
	        	 }
	        }
	        @Override
	        public void onPageScrolled(int arg0, float arg1, int arg2) {
	        }

	        @Override
	        public void onPageScrollStateChanged(int arg0) {
	        }
	    }*/
}
