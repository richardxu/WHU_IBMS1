package activities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.ksoap2.serialization.SoapObject;

import convert.Converts;
import views.ZMFragment.MainThread;

import com.suntrans.whu.R;

import Adapter.SectionListAdapter;
import Adapter.TouchListener;
import WebServices.jiexi;
import WebServices.soap;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class StateLog_Activity extends AppCompatActivity {
	private TextView title;    //标题
	private LinearLayout back;     //返回键
	private LinearLayout layout_loading,layout_failure;   //加载中。。；加载失败。
	private LinearLayout layout_time;   //时间区域的linearlayout
	private TextView year,month;   //年和月
	private Button bt1,bt2,bt3;   //选择按钮
	private ListView list;         //列表
	private String RoomID,AccountType,RoomNum;   //宿舍号和账户类型,宿舍号
	private TextView operation_count,off_count;   //操作次数和断电次数
	private int Showing=0;     //正在显示的列表信息，0：表示全部；   -1：表示操作记录；  1：表示断电记录
	private ArrayList<Map<String,String>> datalist_states=new ArrayList<Map<String,String>>();   //存储所有的开关日志
	final private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");   //日期格式
	final private Calendar c = Calendar.getInstance();     //当前时间的日历
	
	private Handler handler1=new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==1)         //代表请求数据成功
            {
	            layout_loading.setVisibility(View.GONE);
	            layout_failure.setVisibility(View.GONE);
	            ListInit();
	            ((SectionListAdapter)list.getAdapter()).notifyDataSetChanged();  //刷新列表显示
            }
            else if(msg.what==0)         //代表请求数据失败
            {
            	layout_loading.setVisibility(View.GONE);
            	layout_failure.setVisibility(View.VISIBLE);
            	//Toast.makeText(getApplication(), msg.obj.toString(), Toast.LENGTH_LONG).show();
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
		requestWindowFeature(Window.FEATURE_NO_TITLE);  //去标题栏
		setContentView(R.layout.statelog);      //设置activity布局文件
		Intent intent=getIntent();
		RoomID=intent.getStringExtra("RoomID");      //获取学号
		AccountType=intent.getStringExtra("AccountType");  //获取账户类型
		RoomNum = intent.getStringExtra("RoomNum");  //获取房间号
		title=(TextView)findViewById(R.id.title);    //标题
		back=(LinearLayout)findViewById(R.id.layout_back);     //返回键
		layout_loading=(LinearLayout)findViewById(R.id.layout_loading);  //加载中
    	layout_failure=(LinearLayout)findViewById(R.id.layout_failure);  //加载失败
		layout_time=(LinearLayout)findViewById(R.id.layout_time);  //时间区域
		year=(TextView)findViewById(R.id.year);   //年
		month=(TextView)findViewById(R.id.month); //月
		operation_count=(TextView)findViewById(R.id.operation_count);  //操作次数
		off_count=(TextView)findViewById(R.id.off_count);           //断电次数
		list=(ListView)findViewById(R.id.list);   //列表
		bt1=(Button)findViewById(R.id.bt1);   //操作记录
		bt2=(Button)findViewById(R.id.bt2);   //全部
		bt3=(Button)findViewById(R.id.bt3);   //断电记录
		layout_time.setOnTouchListener(new TouchListener());
		bt1.setOnTouchListener(new TouchListener());
		bt2.setOnTouchListener(new TouchListener());
		bt3.setOnTouchListener(new TouchListener());
		title.setText(RoomNum + "状态日志");
		year.setText(c.get(Calendar.YEAR)+"年");
		month.setText(String.valueOf(c.get(Calendar.MONTH)+1));
		bt1.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//bt1.getBackground().setColorFilter(0xff00ff00,Mode.SRC);
				bt1.setTextColor(Color.WHITE);    //更改字体颜色
				bt2.setTextColor(Color.GRAY);     
				bt3.setTextColor(Color.GRAY);
				bt1.setBackgroundResource(R.drawable.bg_left_selected); //更改背景颜色				
				bt2.setBackgroundResource(R.drawable.bg_middle);				
				bt3.setBackgroundResource(R.drawable.bg_right);
				Showing=-1;
				ListInit();
				((SectionListAdapter)list.getAdapter()).notifyDataSetChanged();  //刷新显示
			}});
		bt2.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//bt1.getBackground().setColorFilter(0xff00ff00,Mode.SRC);
				bt2.setTextColor(Color.WHITE);
				bt1.setTextColor(Color.GRAY);
				bt3.setTextColor(Color.GRAY);
				bt1.setBackgroundResource(R.drawable.bg_left); //更改背景颜色				
				bt2.setBackgroundResource(R.drawable.bg_middle_selected);				
				bt3.setBackgroundResource(R.drawable.bg_right);
				Showing=0;
				ListInit();
				((SectionListAdapter)list.getAdapter()).notifyDataSetChanged();  //刷新显示
			}});
		bt3.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//bt1.getBackground().setColorFilter(0xff00ff00,Mode.SRC);
				bt3.setTextColor(Color.WHITE);
				bt2.setTextColor(Color.GRAY);
				bt1.setTextColor(Color.GRAY);
				bt1.setBackgroundResource(R.drawable.bg_left); //更改背景颜色				
				bt2.setBackgroundResource(R.drawable.bg_middle);				
				bt3.setBackgroundResource(R.drawable.bg_right_selected);
				Showing=1;
				ListInit();
				((SectionListAdapter)list.getAdapter()).notifyDataSetChanged();  //刷新显示
			}});
		//日期选择监听，弹出dialog，用户选择完后刷新list
		layout_time.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				LayoutInflater factory = LayoutInflater.from(StateLog_Activity.this);  
				final View view = factory.inflate(R.layout.dialog_monthpicker, null); 
				final DatePicker datePicker1=(DatePicker)view.findViewById(R.id.datePicker1);
				datePicker1.init(Integer.valueOf(year.getText().toString().replace("年", "")),Integer.valueOf(month.getText().toString())-1, 1,null);
				try{     //隐藏”日“  ，利用反射
					java.lang.reflect.Field[] f = datePicker1.getClass().getDeclaredFields();
					for (java.lang.reflect.Field field : f) {  //对成员进行反射，获取显示日的控件
						String nam=field.getName().toString();
						if (field.getName().equals("mDayPicker") ||field.getName().equals("mDaySpinner") ) {
							field.setAccessible(true);
							Object dayPicker =field.get(datePicker1);
							((View) dayPicker).setVisibility(View.GONE);
							break;
						}
					}
					f = datePicker1.getClass().getDeclaredFields();
					Object delegate=new Object();
					for (java.lang.reflect.Field field : f) {  //对成员进行反射，获取mDelegate

						String nam=field.getName().toString();
						if (field.getName().equals("mDelegate")  ) {
							field.setAccessible(true);
							delegate =field.get(datePicker1);
							break;
						}
					}
					Class clazz=datePicker1.getClass();
					Class classes[]=clazz.getDeclaredClasses();
					for(Class c:classes){//对成员内部类进行反射
						String a = c.getName();   //内部类的名称，一般是DaePicker$Innerclassname
						if(a.contains("DatePickerSpinnerDelegate"))//获取需要的内部类
						{

							f = c.getDeclaredFields();  //获取内部类的成员
							for (java.lang.reflect.Field field : f) {   //遍历成员，找到显示日的控件
								String name = field.getName();
								if (field.getName().equals("mDayPicker") || field.getName().equals("mDaySpinner")) {
									field.setAccessible(true);
									Object dayPicker = field.get(delegate);
									((View) dayPicker).setVisibility(View.GONE);
									break;
								}
							}
						}
					}
				}

				catch(Exception e){
					String ex=e.toString();
					e.printStackTrace();
				}
				final AlertDialog.Builder builder = new AlertDialog.Builder(StateLog_Activity.this);
			    builder.setTitle("请选择月份："); 	
			    builder.setView(view);
			    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {  
			         public void onClick(DialogInterface dialog, int whichButton) { 
			        	
			        	// Toast.makeText(getApplicationContext(), datePicker1.getYear()+"年"+(datePicker1.getMonth()+1)+"月", Toast.LENGTH_SHORT).show();
			        	 year.setText(datePicker1.getYear()+"年");       //更改显示的 年
			        	 month.setText(String.valueOf(datePicker1.getMonth()+1));   //更改显示的月
			        	 layout_loading.setVisibility(View.VISIBLE);
						 layout_failure.setVisibility(View.GONE);
						 new MainThread().start();    //开始新的刷新线程
			         }  
			     });  
			     builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {  
			         public void onClick(DialogInterface dialog, int whichButton) {  
			 
			         }  
			     });  
			    builder.create().show();
			}});
		//返回键监听
		back.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}});
		layout_failure.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				layout_loading.setVisibility(View.VISIBLE);
				layout_failure.setVisibility(View.GONE);
				new MainThread().start();    //开始新的刷新线程
			}});
		//ListInit();
		new MainThread().start();
		//设置通知栏半透明
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			Converts.setTranslucentStatus(StateLog_Activity.this,true);
		}
	}
	
	public class MainThread extends Thread{
		@Override
		public void run(){
			try{
				String StartTime,EndTime;  
				if(month.getText().toString().equals("12"))
				{
					StartTime=year.getText().toString().replace("年", "")+"-"+month.getText().toString()+"-01 00:00:00";
					EndTime= (Integer.valueOf(year.getText().toString().replace("年", ""))+1)+"-01-01 00:00:00";
				}
				else
				{
					StartTime=year.getText().toString().replace("年", "")+"-"+month.getText().toString()+"-01 00:00:00";
					EndTime=year.getText().toString().replace("年", "")+"-"+(Integer.valueOf(month.getText().toString())+1)+"-01 00:00:00";
				}	
				//获取所有的开关记录
				SoapObject result=soap.Inquiry_States_RoomID(RoomID,AccountType,StartTime,EndTime);
				datalist_states=jiexi.inquiry_states(result);
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
	private void ListInit(){
		int countoperation=0,countoff=0;
		for(int i=0;i<datalist_states.size();i++)
		{
			if(datalist_states.get(i).get("Type").equals("用电操作"))
				countoperation++;
			else
				countoff++;
		}
		operation_count.setText(String.valueOf(countoperation));  //操作次数
		off_count.setText(String.valueOf(countoff));             //断电次数
		SectionListAdapter adapter = new SectionListAdapter(StateLog_Activity.this);  //实例化一个SectionListAdapter
		final ArrayList<Map<String,Object>> data_userinfo=new ArrayList<Map<String,Object>>();
		switch(Showing)
		{
			case -1:     //操作记录
				for(int i=0;i<datalist_states.size();i++)
				{
					if(datalist_states.get(i).get("Type").equals("用电操作"))
					//如果Type是用电操作，则Contents中是数字，2表示打开，3表示关闭
					{
						String gettime=datalist_states.get(i).get("GetTime");
						Map<String,Object> map1=new HashMap<String,Object>();
						map1.put("Name", datalist_states.get(i).get("Contents").equals("2")?"用电开启":"用电关闭");
						map1.put("Value",gettime.substring(0,4)+"年"+gettime.substring(5, 7)+"月"+gettime.substring(8, 10)+"日  "+gettime.substring(11,19));
						data_userinfo.add(map1);
					}
				}
				break;
			case  0:     //全部
				for(int i=0;i<datalist_states.size();i++)
				{
					if(datalist_states.get(i).get("Type").equals("用电操作"))   //如果类型是用电操作
					{
						//如果Type是用电操作，则Contents中是数字，2表示打开，3表示关闭
						String gettime=datalist_states.get(i).get("GetTime");
						Map<String,Object> map1=new HashMap<String,Object>();
						map1.put("Name", datalist_states.get(i).get("Contents").equals("2")?"用电开启":"用电关闭");
						map1.put("Value",gettime.substring(0,4)+"年"+gettime.substring(5, 7)+"月"+gettime.substring(8, 10)+"日  "+gettime.substring(11,19));
						data_userinfo.add(map1);
					}
					else
						//否则，则Contents中是字符串，描述断电时的状态
					{
						String gettime=datalist_states.get(i).get("GetTime");
						Map<String,Object> map1=new HashMap<String,Object>();
						if(datalist_states.get(i).get("Contents").contains("恢复"))
							map1.put("Name", datalist_states.get(i).get("Type")+"恢复");
						else
							map1.put("Name", datalist_states.get(i).get("Type"));
						map1.put("Value",gettime.substring(0,4)+"年"+gettime.substring(5, 7)+"月"+gettime.substring(8, 10)+"日  "+gettime.substring(11,19));
						map1.put("Content", datalist_states.get(i).get("Contents"));
						data_userinfo.add(map1);
					}
				}
				break;
			case  1:      //断电记录
				for(int i=0;i<datalist_states.size();i++)
				{
					if(datalist_states.get(i).get("Type").equals("用电操作"))
					{//如果Type是用电操作，则Contents中是数字，2表示打开，3表示关闭
					}
					else
						//否则，则Contents中是字符串，描述断电时的状态
					{
						String gettime=datalist_states.get(i).get("GetTime");
						Map<String,Object> map1=new HashMap<String,Object>();
						if(datalist_states.get(i).get("Contents").contains("恢复"))
							map1.put("Name", datalist_states.get(i).get("Type")+"恢复");
						else
							map1.put("Name", datalist_states.get(i).get("Type"));
						map1.put("Value",gettime.substring(0,4)+"年"+gettime.substring(5, 7)+"月"+gettime.substring(8, 10)+"日  "+gettime.substring(11,19));
						map1.put("Content", datalist_states.get(i).get("Contents"));
						data_userinfo.add(map1);
					}
				}
				break;
			default:break;
		}
		
		 //添加内容   《=====1=====》账户信息部分
		 adapter.addSection(month.getText().toString()+"月", new BaseAdapter(){
				@Override
				public int getCount() {
					// TODO Auto-generated method stub
					return data_userinfo.size();
				}
				@Override
				public Object getItem(int position) {
					// TODO Auto-generated method stub
					return data_userinfo.get(position);
				}
				@Override
				public long getItemId(int position) {
					// TODO Auto-generated method stub
					return position;
				}
				@Override
				public View getView(final int position, View convertView, ViewGroup parent) {
					// TODO Auto-generated method stub
					if(convertView==null)
						convertView = LayoutInflater.from(getApplication()).inflate(R.layout.liststatelog, null);  
					ImageView image=(ImageView)convertView.findViewById(R.id.image);
					TextView name=(TextView)convertView.findViewById(R.id.name);
					TextView value=(TextView)convertView.findViewById(R.id.value);
					ImageView img_detail=(ImageView)convertView.findViewById(R.id.img_detail);
					final Map<String,Object> map=data_userinfo.get(position);
					Bitmap bmp;
					if(map.get("Name").equals("用电开启"))
					{
						bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_onstate);   
						image.setImageBitmap(bmp);
						img_detail.setVisibility(View.INVISIBLE);
					}
					else if(map.get("Name").equals("用电关闭"))
					{
						bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_offstate);   
						image.setImageBitmap(bmp);
						img_detail.setVisibility(View.INVISIBLE);
					}
					else if(map.get("Name").equals("余额不足"))
					{
						bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_balance);   
						image.setImageBitmap(bmp);
						img_detail.setVisibility(View.INVISIBLE);
					}
					else if(map.get("Name").equals("恶性负载"))
					{
						bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_warning);   
						image.setImageBitmap(bmp);
					}
					else if(map.get("Name").equals("过载"))
					{
						bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_warning);   
						image.setImageBitmap(bmp);
					}
					name.setText(map.get("Name").toString());
					value.setText(map.get("Value").toString());
					convertView.setOnClickListener(new OnClickListener(){
						@Override
						public void onClick(View v) {
							if(map.get("Name").equals("恶性负载")||map.get("Name").equals("过载"))
							{
								Intent intent=new Intent();								
				        		intent.putExtra("Content", map.get("Content").toString());       //账户类型    
				        	    intent.setClass(StateLog_Activity.this, StateDetail_Activity.class);//设置要跳转的activity
								startActivity(intent);//开始跳转	
								overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);//设置activity切换效果
							}
							// TODO Auto-generated method stub
							//Toast.makeText(StateLog_Activity.this, "你点击了第"+position+"行", Toast.LENGTH_SHORT).show();
						}});
					return convertView;
				}});
		 list.setAdapter(adapter);
	}
	
}
