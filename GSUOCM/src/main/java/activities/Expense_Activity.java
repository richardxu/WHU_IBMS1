package activities;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.ksoap2.serialization.SoapObject;


import com.suntrans.whu.R;


import Adapter.SectionListAdapter;
import Adapter.TouchListener;
import WebServices.jiexi;
import WebServices.soap;
import activities.StateLog_Activity.MainThread;
import convert.Converts;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources.Theme;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Expense_Activity extends AppCompatActivity {
	private TextView title;    //标题
	private TextView expend,recharge;   //消费总计和充值总计
	private LinearLayout layout_loading,layout_failure;   //加载中。。；加载失败。
	private LinearLayout back;     //返回键
	private TextView year,month;   //年和月
	private LinearLayout layout_time;   //时间区域的linearlayout
	private Button bt1,bt2,bt3;   //选择按钮
	private ListView list;         //列表
	private String RoomID,AccountType,RoomNum;  //学号和账户类型和房间号
	private int Showing=0;     //正在显示的列表信息，0：表示全部；   -1：表示操作记录；  1：表示断电记录
	private ArrayList<Map<String,String>> datalist_recharge=new ArrayList<Map<String,String>>();   //存储充值记录
	private ArrayList<Map<String,String>> datalist_expend=new ArrayList<Map<String,String>>();   //存储消费记录
	private ArrayList<Map<String,String>> datalist_billmonth=new ArrayList<Map<String,String>>();   //存储本月消费充值总计
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
            }
            else if(msg.what==0)         //代表请求数据失败
            {
            	layout_loading.setVisibility(View.GONE);
            	layout_failure.setVisibility(View.VISIBLE);
            	Toast.makeText(getApplication(), "请求失败，请点击刷新重试！", Toast.LENGTH_LONG).show();
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
		setContentView(R.layout.expense);      //设置activity布局文件
		Intent intent=getIntent();	
		RoomID=intent.getStringExtra("RoomID");
		AccountType=intent.getStringExtra("AccountType");
		RoomNum = intent.getStringExtra("RoomNum");
		//实例化控件
		title=(TextView)findViewById(R.id.title);     //标题

		back=(LinearLayout)findViewById(R.id.layout_back);     //返回键
		layout_time=(LinearLayout)findViewById(R.id.layout_time);   //时间选择区域
		layout_loading=(LinearLayout)findViewById(R.id.layout_loading);  //加载中
    	layout_failure=(LinearLayout)findViewById(R.id.layout_failure);  //加载失败
		year=(TextView)findViewById(R.id.year);  //年
		month=(TextView)findViewById(R.id.month);//月
		expend=(TextView)findViewById(R.id.expend);//本月消费总计
		recharge=(TextView)findViewById(R.id.recharge);//本月充值总计
		list=(ListView)findViewById(R.id.list);     //列表
		bt1=(Button)findViewById(R.id.bt1);
		bt2=(Button)findViewById(R.id.bt2);
		bt3=(Button)findViewById(R.id.bt3);
		year.setText(c.get(Calendar.YEAR)+"年");
		month.setText(String.valueOf(c.get(Calendar.MONTH)+1));

		title.setText(RoomNum+"资费记录");
		//设置触摸事件
		layout_time.setOnTouchListener(new TouchListener());
		bt1.setOnTouchListener(new TouchListener());
		bt2.setOnTouchListener(new TouchListener());
		bt3.setOnTouchListener(new TouchListener());

		//用电记录
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
			}});
		//全部
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
			}});
		//充值记录
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
			}});
		//日期选择监听，弹出dialog，用户选择完后刷新list
		layout_time.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				LayoutInflater factory = LayoutInflater.from(Expense_Activity.this);  
				final View view = factory.inflate(R.layout.dialog_monthpicker, null); 
				final DatePicker datePicker1=(DatePicker)view.findViewById(R.id.datePicker1);

				//datePicker1.setMinDate();
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
					e.printStackTrace();
				}
				final AlertDialog.Builder builder = new AlertDialog.Builder(Expense_Activity.this);
			    builder.setTitle("请选择月份："); 	
			    builder.setView(view);
			    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {  
			         public void onClick(DialogInterface dialog, int whichButton) { 
			        	
			        	 //Toast.makeText(getApplicationContext(), datePicker1.getYear()+"年"+(datePicker1.getMonth()+1)+"月", Toast.LENGTH_SHORT).show();
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
		new MainThread().start();
		//设置通知栏半透明
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			Converts.setTranslucentStatus(Expense_Activity.this,true);
		}
		//ListInit();    //列表初始化
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
				SoapObject result=soap.Inquiry_ReCharge_RoomID(RoomID,AccountType,StartTime,EndTime);
				datalist_recharge=jiexi.inquiry_recharge(result);  //获取充值列表
				result=soap.Inquiry_Chargeback_RoomID(RoomID,AccountType,"24",StartTime,EndTime);
				datalist_expend=jiexi.inquiry_chargeback(result);     //获取消费列表
				result=soap.Inquiry_BillMonth_RoomID(RoomID, AccountType, year.getText().toString().replace("年", ""), month.getText().toString());
				datalist_billmonth=jiexi.inquiry_billmonth(result);    //获取消费与充值总计
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
		 Map<String,String> map_billmonth=datalist_billmonth.get(0);
		 expend.setText(map_billmonth.get("OutRMB")+"元");
		 recharge.setText(map_billmonth.get("InRMB")+"元");
		 SectionListAdapter adapter = new SectionListAdapter(Expense_Activity.this);  //实例化一个SectionListAdapter
		 final ArrayList<Map<String,Object>> data_detail=new ArrayList<Map<String,Object>>();
		 switch(Showing)
		 {
			 case -1:  //用电记录
			 {
				 for(int i=0;i<datalist_expend.size();i++)
				 {
					 String gettime=datalist_expend.get(i).get("GetTime");
					 Map<String,Object> map=new HashMap<String,Object>();
					 map.put("Fee", "-"+datalist_expend.get(i).get("Chargeback")+"元");
					 map.put("Event","宿舍用电"+datalist_expend.get(i).get("Eletricity")+"度");
					 map.put("Time", gettime.substring(0,4)+"年"+gettime.substring(5, 7)+"月"+gettime.substring(8, 10)+"日  ");
					 data_detail.add(map);
				 }
				 break;
			 }
				 
			 case  0:   //全部
			 {
				 for(int i=0;i<datalist_recharge.size();i++)
				 {
					 String gettime=datalist_recharge.get(i).get("GetTime");
					 //判断充值年月是否为本月和充值类型
					 if((datalist_recharge.get(i).get("AccountType").equals(AccountType))&&(year.getText().toString().replace("年", "").equals(gettime.substring(0,4)))&&(Integer.valueOf(month.getText().toString())==Integer.valueOf(gettime.substring(5, 7))) )
					 {
						 Map<String,Object> map=new HashMap<String,Object>();
						 map.put("Fee", "+"+datalist_recharge.get(i).get("Recharge")+"元");
						 map.put("Event",datalist_recharge.get(i).get("SName")+"充值");
						 map.put("Time", gettime.substring(0,4)+"年"+gettime.substring(5, 7)+"月"+gettime.substring(8, 10)+"日  "+gettime.substring(11,19));
						 data_detail.add(map);
					 }
				 }
				 for(int i=0;i<datalist_expend.size();i++)
				 {
					 String gettime=datalist_expend.get(i).get("GetTime");
					 Map<String,Object> map=new HashMap<String,Object>();
					 map.put("Fee", "-"+datalist_expend.get(i).get("Chargeback")+"元");
					 map.put("Event","宿舍用电"+datalist_expend.get(i).get("Eletricity")+"度");
					 map.put("Time", gettime.substring(0,4)+"年"+gettime.substring(5, 7)+"月"+gettime.substring(8, 10)+"日  ");
					 data_detail.add(map);
				 }
				 break;
			 }
			 case  1:   //充值记录
			 {
				 for(int i=0;i<datalist_recharge.size();i++)
				 {
					 String gettime=datalist_recharge.get(i).get("GetTime");
					 //判断充值年月是否为本月，和充值类型
					 if((datalist_recharge.get(i).get("AccountType").equals(AccountType))&&(year.getText().toString().replace("年", "").equals(gettime.substring(0,4)))&&(Integer.valueOf(month.getText().toString())==Integer.valueOf(gettime.substring(5, 7))) )
					 {
						 Map<String,Object> map=new HashMap<String,Object>();
						 map.put("Fee", "+"+datalist_recharge.get(i).get("Recharge")+"元");
						 map.put("Event",datalist_recharge.get(i).get("SName")+"充值");
						 map.put("Time", gettime.substring(0,4)+"年"+gettime.substring(5, 7)+"月"+gettime.substring(8, 10)+"日  "+gettime.substring(11,19));
						 data_detail.add(map);
					 }
				 }
				 break;
			 }
		 }
		 //为list添加适配器
		 adapter.addSection(month.getText().toString()+"月",new BaseAdapter(){
				@Override
				public int getCount() {
					// TODO Auto-generated method stub
					return data_detail.size();
				}
				@Override
				public Object getItem(int position) {
					// TODO Auto-generated method stub
					return data_detail.get(position);
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
						convertView = LayoutInflater.from(getApplication()).inflate(R.layout.listexpense_detail, null);  
					TextView tx_fee=(TextView)convertView.findViewById(R.id.tx_fee);
					TextView tx_event=(TextView)convertView.findViewById(R.id.tx_event);
					TextView tx_time=(TextView)convertView.findViewById(R.id.tx_time);
					Map<String,Object> map=data_detail.get(position);
					tx_fee.setText(map.get("Fee").toString());
					tx_event.setText(map.get("Event").toString());
					tx_time.setText(map.get("Time").toString());
					convertView.setOnClickListener(new OnClickListener(){
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							//Toast.makeText(Expense_Activity.this, "你点击了第"+position+"行", Toast.LENGTH_SHORT).show();
						}});
					return convertView;
				}});
		 list.setAdapter(adapter);
		 
	}
}
