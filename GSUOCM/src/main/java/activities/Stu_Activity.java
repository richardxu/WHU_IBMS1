package activities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.ksoap2.serialization.SoapObject;

import convert.Converts;
import views.ZMFragment.MainThread;

import Adapter.SectionListAdapter;
import WebServices.jiexi;
import WebServices.soap;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.suntrans.whu.R;

public class Stu_Activity extends AppCompatActivity {
	private TextView title;    //标题
	private Bitmap bmp;  //定义bitmap存放图标
	private LinearLayout back;     //返回键
	private ListView list;    //列表控件
	private LinearLayout layout_loading,layout_failure;   //加载中。。；加载失败。
	private String RoomID;    //宿舍ID
	private String SName;   //学生姓名
	private String RoomInfo;  //宿舍信息
	private ArrayList<Map<String,String>> datalist_room=new ArrayList<Map<String,String>>();
	private Map<String,String> map_student=new HashMap<String,String>();   //当前页面显示学生的信息
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
		setContentView(R.layout.stu);      //设置activity布局文件
		Intent intent=getIntent();
		SName=intent.getStringExtra("Name");   //获取学生姓名
		RoomID=intent.getStringExtra("RoomID");//获取宿舍ID
		RoomInfo=intent.getStringExtra("RoomInfo");  //获取宿舍信息
		title=(TextView)findViewById(R.id.title);     //标题
		list=(ListView)findViewById(R.id.list);     //信息列表
		back=(LinearLayout)findViewById(R.id.layout_back);      //返回键
		layout_loading=(LinearLayout)findViewById(R.id.layout_loading);  //加载中
    	layout_failure=(LinearLayout)findViewById(R.id.layout_failure);  //加载失败
		title.setText(SName);   //设置标题显示
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
	   MainThread mainThread=new MainThread();
	   mainThread.start();
		//设置通知栏半透明
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			Converts.setTranslucentStatus(Stu_Activity.this,true);
		}
		//ListInit();
	}
	
	public class MainThread extends Thread{
		@Override
		public void run(){
			try{
				SoapObject result=soap.Inquiry_Room_RoomID(RoomID);
				datalist_room=jiexi.inquiry_room_roomid(result);
				for(int i=0;i<datalist_room.size();i++)      //获取正在显示的学生的信息，存放在map_student中
				{
					if(datalist_room.get(i).get("SName").equals(SName))
						map_student=datalist_room.get(i);
				}
			//	Collections.sort(datalist_room, comparator)
				Message msg=new Message();
				msg.what=1;    //代表成功
				handler1.sendMessage(msg);
			}
			catch(Exception e)
			{
	   			 Message msg=new Message();
	   			 msg.what=0;    //代表失败
	   			 handler1.sendMessage(msg);
			}
		}
	}
	 //为ListView设置适配器，list初始化函数
	private void ListInit()
	{
			 SectionListAdapter adapter = new SectionListAdapter(this);  //实例化一个SectionListAdapter
			 final ArrayList<Map<String,Object>> data_stu=new ArrayList<Map<String,Object>>();
			 Map<String,Object> map1=new HashMap<String,Object>();
			 map1.put("Name", "学号");
			 map1.put("Value",map_student.get("StudentID"));
			 data_stu.add(map1);
			 Map<String,Object> map2=new HashMap<String,Object>();
			 map2.put("Name", "院系");
			 map2.put("Value",map_student.get("Faculty"));
			 data_stu.add(map2);
			 Map<String,Object> map3=new HashMap<String,Object>();
			 map3.put("Name", "专业");
			 map3.put("Value",map_student.get("Professional"));
			 data_stu.add(map3);
			 Map<String,Object> map4=new HashMap<String,Object>();
			 map4.put("Name", "电话");
			 map4.put("Value",map_student.get("PhoneNum").equals("anyType{}")?("无"):(map_student.get("PhoneNum")));
			 data_stu.add(map4);
			/* Map<String,Object> map5=new HashMap<String,Object>();
			 map5.put("Name", "说明");
			 map5.put("Value",map_student.get("YesorNo").equals("是")?("用电账户管理员"):("寝室成员"));
			 data_stu.add(map5);*/
			 //添加内容   《=====1=====》学生详情部分
			 adapter.addSection("学生信息", new BaseAdapter(){
					@Override
					public int getCount() {
						// TODO Auto-generated method stub
						return data_stu.size();
					}
					@Override
					public Object getItem(int position) {
						// TODO Auto-generated method stub
						return data_stu.get(position);
					}
					@Override
					public long getItemId(int position) {
						// TODO Auto-generated method stub
						return position;
					}
					@Override
					public View getView(final int position, View convertView, ViewGroup parent) {
						// TODO Auto-generated method stub
						convertView = LayoutInflater.from(Stu_Activity.this.getApplication()).inflate(R.layout.liststu_stu, null);  
						ImageView image=(ImageView)convertView.findViewById(R.id.image);
						TextView name=(TextView)convertView.findViewById(R.id.name);
						TextView value=(TextView)convertView.findViewById(R.id.value);
						switch(position)
						{
							case 0:     //学号图标
							{
								bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_userid);   
								image.setImageBitmap(bmp);
								break;
							}	
							case 1:     //院系图标
							{
								bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_faculty);   
								image.setImageBitmap(bmp);
								break;
							}	
							case 2:     //专业图标
							{
								bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_professional);   
								image.setImageBitmap(bmp);
								break;
							}	
							case 3:     //电话图标
							{
								bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_phone);   
								image.setImageBitmap(bmp);
								break;
							}								
							default:break;
						
						}
						Map<String,Object> map=data_stu.get(position);
						name.setText(map.get("Name")==null?"null":map.get("Name").toString());
						value.setText(map.get("Value")==null?"null":map.get("Value").toString());						
						return convertView;
					}});
			 final ArrayList<Map<String,Object>> data_dorm=new ArrayList<Map<String,Object>>();
			 Map<String,Object> map6=new HashMap<String,Object>();
			 map6.put("Name", RoomInfo);
			 data_dorm.add(map6);
			 //添加内容   《=====1=====》宿舍详情部分
			 adapter.addSection("宿舍信息", new BaseAdapter(){
					@Override
					public int getCount() {
						// TODO Auto-generated method stub
						return data_dorm.size();
					}
					@Override
					public Object getItem(int position) {
						// TODO Auto-generated method stub
						return data_dorm.get(position);
					}
					@Override
					public long getItemId(int position) {
						// TODO Auto-generated method stub
						return position;
					}
					@Override
					public View getView(final int position, View convertView, ViewGroup parent) {
						// TODO Auto-generated method stub
						convertView = LayoutInflater.from(Stu_Activity.this.getApplication()).inflate(R.layout.listmain_dorm, null);  
						TextView name=(TextView)convertView.findViewById(R.id.name);
						Map<String,Object> map=data_dorm.get(position);
						name.setText(map.get("Name")==null?"null":map.get("Name").toString());
						return convertView;
					}});
			 final ArrayList<Map<String,Object>> data_staffinfo=new ArrayList<Map<String,Object>>();
			 for(int i=0;i<datalist_room.size();i++)
			 {
				 if(!datalist_room.get(i).get("SName").equals(SName))
				 {
					 Map<String,Object> map=new HashMap<String,Object>();
					 map.put("Name", datalist_room.get(i).get("SName"));
					 map.put("Value",datalist_room.get(i).get("Faculty"));
					 data_staffinfo.add(map);
				 }
			 }
			 //添加内容   《=====3=====》人员信息部分
			 adapter.addSection("人员信息", new BaseAdapter(){
					@Override
					public int getCount() {
						// TODO Auto-generated method stub
						return data_staffinfo.size();
					}
					@Override
					public Object getItem(int position) {
						// TODO Auto-generated method stub
						return data_staffinfo.get(position);
					}
					@Override
					public long getItemId(int position) {
						// TODO Auto-generated method stub
						return position;
					}
					@Override
					public View getView(final int position, View convertView, ViewGroup parent) {
						// TODO Auto-generated method stub
						convertView = LayoutInflater.from(Stu_Activity.this.getApplication()).inflate(R.layout.listmain_staffinfo, null);  
						TextView name=(TextView)convertView.findViewById(R.id.name);
						TextView value=(TextView)convertView.findViewById(R.id.value);
						final Map<String,Object> map=data_staffinfo.get(position);
						name.setText(map.get("Name")==null?"null":map.get("Name").toString());
						value.setText(map.get("Value")==null?"null":map.get("Value").toString());
						convertView.setOnClickListener(new OnClickListener(){
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								//Toast.makeText(getActivity(), "你点击了第"+position+"行", Toast.LENGTH_SHORT).show();
								Intent intent=new Intent();
				        		intent.putExtra("Name",map.get("Name").toString());             //学生姓名
				        		intent.putExtra("RoomID", RoomID);     //宿舍ID
				        		intent.putExtra("RoomInfo", RoomInfo); //宿舍信息
								intent.setClass(Stu_Activity.this, Stu_Activity.class);//设置要跳转的activity
								startActivity(intent);//开始跳转
								finish();
								overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
							}});
						return convertView;
					}});
			 list.setAdapter(adapter);
		}
}
