package activities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

import org.ksoap2.serialization.SoapObject;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import com.suntrans.whu.R;

public class Dorm_Activity extends FragmentActivity implements Serializable{
	private String UserID=""; // 管理员账号
	private String StudentID="";   //学号
	private String Role="";    //角色号
	private ImageView back;   //返回键，默认不显示，如果是管理员页面跳转过来，则显示
	private ImageView setting;  //设置键，如果用户是学生，就显示设置键，否则显示返回键
	private TextView tx_zm,tx_kt;      //标题栏，照明和空调两个textview
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
				tx_kt.setText(roomnum+"空调");
            }
        }
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		requestWindowFeature(Window.FEATURE_NO_TITLE);  //去标题栏
		setContentView(R.layout.dorm);      //设置activity布局文件
		AppManager.getAppManager().addActivity(this);
		Intent intent=getIntent();
		UserID=intent.getStringExtra("UserID");
		StudentID=intent.getStringExtra("StudentID");
		Role=intent.getStringExtra("Role");
		tx_zm=(TextView)findViewById(R.id.tx_zm);   //照明
		tx_kt=(TextView)findViewById(R.id.tx_kt);   //空调
		back=(ImageView)findViewById(R.id.back);     //返回键
		setting=(ImageView)findViewById(R.id.setting);    //设置键
		vPager=(ViewPager)findViewById(R.id.vPager);
		vPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));    //设置适配器
		vPager.setPageTransformer(true, new ZoomOutPageTransformer());    //设置viewpager切换动画
		vPager.setOnPageChangeListener(new MyOnPageChangeListener());   //设置页面切换监听
		tx_zm.setOnClickListener(new MyOnClickListener(0));
		tx_kt.setOnClickListener(new MyOnClickListener(1));
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
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				intent.setClass(Dorm_Activity.this, Setting_Activity.class);
				intent.putExtra("UserID", StudentID);   //账号
				intent.putExtra("Role", Role);    //角色号
				startActivity(intent);
				overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);//设置activity切换效果
				
			}			
		});
		back.setOnClickListener(new OnClickListener(){   //返回键监听
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}});
		new Thread(){
			@Override
			public void run(){
				try{
					SoapObject result=soap.Inquiry_UserInfo(StudentID);
					datalist_userinfo=jiexi.inquiry_studentinfo(result);
					Message msg=new Message();
					msg.obj=datalist_userinfo.get(0).get("RoomNum");
					handler1.sendMessage(msg);
				}
				catch(Exception e){}
			}
		}.start();
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

				private final String[] titles = { "照明", "空调"};

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
						bundle.putString("StudentID", StudentID);
						bundle.putString("Role", Role);   //当前用户的角色，可以是管理员或学生
						page_zm.setArguments(bundle);
						return page_zm;
					case 1:          //第二个fragment
						if (page_kt == null) {
							page_kt =new KTFragment();
						}
						Bundle bundle1 = new Bundle();
						bundle1.putString("UserID", UserID);
						bundle1.putString("StudentID", StudentID);
						bundle1.putString("Role", Role);   //当前用户的角色，可以是管理员或学生
						page_kt.setArguments(bundle1);
						return page_kt;
					default:
						return null;
					}
				}
				@Override  
				public int getItemPosition(Object object) {  
				    return POSITION_NONE;  
				}  
				
			}
	    /**
	     * 头标点击监听
	*/
	    public class MyOnClickListener implements View.OnClickListener {
	        private int index = 0;

	        public MyOnClickListener(int i) {
	            index = i;
	        }

	        @Override
	        public void onClick(View v) {
	        	switch(v.getId())   //判断按下的按钮id，设置标题两个textview的背景颜色
	        	{
	        		case R.id.tx_zm:tx_zm.setBackgroundResource(R.drawable.tab_left_select);
	        						tx_kt.setBackgroundResource(R.drawable.tab_right_normal);
	        						break;
	        		case R.id.tx_kt:tx_zm.setBackgroundResource(R.drawable.tab_left_normal);
									tx_kt.setBackgroundResource(R.drawable.tab_right_select);
									break;
	        		default:break;
	        	}
	            vPager.setCurrentItem(index);   //根据头标选择的内容  对viewpager进行页面切换
	        }
	    };
	    
	    
	    /**
	     * 页卡切换监听
	*/
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
	    }
}
