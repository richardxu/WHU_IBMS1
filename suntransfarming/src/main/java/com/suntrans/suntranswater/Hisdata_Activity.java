package com.suntrans.suntranswater;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.jjoe64.graphview.CustomLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;
import com.jjoe64.graphview.LineGraphView;

import org.ksoap2.serialization.SoapObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import Adapters.SectionListAdapter;
import Adapters.TouchListener;
import WebServices.jiexi;
import WebServices.soap;
import convert.Converts;

public class Hisdata_Activity extends Activity {
	private TextView title,list_title;    //标题，列表标题
	private LinearLayout layout_back;     //返回
	private LinearLayout layout_loading,layout_failure;   //加载中。。；加载失败。
	private Button bt1,bt2,bt3;   //选择按钮
	private PullToRefreshListView mPullToRefreshListView;
	private ListView list;         //列表
	private LinearLayout layout_graph;   //曲线部分 
	private String RoomID,AccountType,Field;  //学号和账户类型和要显示的参数：是电压、电流、功率、功率因数还是用电量
	private ArrayList<Map<String,String>> datalist_hisdata=new ArrayList<Map<String,String>>();  //用电历史数据
	private int Showing=0;     //正在显示的列表信息，0：日；   -1：表示周；  1：表示月
	private Map<String,String> map_field=new HashMap<String,String>();   //Field对应的map，键是中文，值是方法中的英文名
	final private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");   //日期格式
	final private Calendar c = Calendar.getInstance();     //当前时间的日历
	private String Measurement;   //单位
	private String Sid;  //传感器ID
	private String Name;    //参数名称
//	private SectionListAdapter adapter;  //list的适配器SectionListAdapter
	private Handler handler1=new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==1)         //代表请求数据成功
            {
            	ListInit();
	            layout_loading.setVisibility(View.GONE);
	            layout_failure.setVisibility(View.GONE);
	            
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
		setContentView(R.layout.hisdata);      //设置activity布局文件

		Intent intent=getIntent();
		Name=intent.getStringExtra("Name");           //参数名称
		Measurement=intent.getStringExtra("Measurement");   //单位
		Sid=intent.getStringExtra("Sid");  //传感器id
		//实例化控件
		title=(TextView)findViewById(R.id.title);     //标题
//		list_title=(TextView)findViewById(R.id.list_title);     //列表标题
		layout_back=(LinearLayout)findViewById(R.id.layout_back);      //返回键
		layout_graph=(LinearLayout)findViewById(R.id.layout_graph);   //曲线
		mPullToRefreshListView=(PullToRefreshListView)findViewById(R.id.list);
		mPullToRefreshListView.setMode(PullToRefreshBase.Mode.DISABLED);    //设置列表不能下拉和上拉
		list=mPullToRefreshListView.getRefreshableView();      //列表
		bt1=(Button)findViewById(R.id.bt1);    //日
		bt2=(Button)findViewById(R.id.bt2);    //周
		bt3=(Button)findViewById(R.id.bt3);    //年
		layout_loading=(LinearLayout)findViewById(R.id.layout_loading);  //加载中
    	layout_failure=(LinearLayout)findViewById(R.id.layout_failure);  //加载失败

		title.setText(Name);

		//设置触摸事件
		bt1.setOnTouchListener(new TouchListener());
		bt2.setOnTouchListener(new TouchListener());
		bt3.setOnTouchListener(new TouchListener());
		//日
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
				layout_loading.setVisibility(View.VISIBLE);
				layout_failure.setVisibility(View.GONE);
				new MainThread().start();    //开始新的刷新线程
			}});
		//周
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
				layout_loading.setVisibility(View.VISIBLE);
				layout_failure.setVisibility(View.GONE);
				new MainThread().start();    //开始新的刷新线程
			}});
		//月
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
				layout_loading.setVisibility(View.VISIBLE);
				layout_failure.setVisibility(View.GONE);
				new MainThread().start();    //开始新的刷新线程
			}});
		//返回键监听
		layout_back.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
				overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
			}});
		layout_failure.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				layout_loading.setVisibility(View.VISIBLE);
				layout_failure.setVisibility(View.GONE);
				new MainThread().start();    //开始新的刷新线程
			}});
		layout_loading.setVisibility(View.VISIBLE);
		new MainThread().start();
		//ListInit();    //列表初始化
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
		public void run(){
			try{
				//获取所有的开关记录
				switch(Showing)
				{
					case -1:   //日
					{
						String StartTime,EndTime;
						StartTime=dateFormat.format(new Date(new Date().getTime()+3600000-86400000));  //开始时间是现在的时间+1个小时再减去1天，计算ms数
						EndTime=dateFormat.format(new Date(new Date().getTime()+36000000));   //结束的时间就是现在的时间+1个小时
						if(Name.contains("雨量"))  //如果要查询的是用电量，则将时间调整到零点，以查看全天的降雨量
						{
							Calendar c=Calendar.getInstance();
							c.setTime(new Date());
							int hour=c.get(Calendar.HOUR_OF_DAY);   //24小时制的小时
							//如果现在的时间是在当天8点之前，则开始时间是昨天的8点。否则开始时间是今天的8点
							if(hour>8)   //当天8点开始
								StartTime=EndTime.substring(0, 10)+" 08:00:00";
							else    //昨天8点开始
								StartTime=StartTime.substring(0, 10)+" 08:00:00";

							EndTime=EndTime.substring(0,13)+":00:00";
						}
						SoapObject result=soap.Inquiry_HisData(Sid,StartTime,EndTime,"1");
						datalist_hisdata=jiexi.inquiry_hisdata(result);  //获取历史数据
						break;
					}
					case  0:    //周
					{
						String StartTime,EndTime;
						StartTime=dateFormat.format(new Date(new Date().getTime()-604800000));  //开始时间是现在的时间减去7天，计算ms数
						EndTime=dateFormat.format(new Date());   //结束的时间就是现在的时间
						if(Name.contains("雨量"))  //如果要查询的是用电量，则将时间调整到08，以查看全天的降雨量
						{
							StartTime=StartTime.substring(0, 10)+" 08:00:00";
							EndTime=EndTime.substring(0,10)+" 08:00:00";
						}
						SoapObject result=soap.Inquiry_HisData(Sid,StartTime,EndTime,"24");
						datalist_hisdata=jiexi.inquiry_hisdata(result);  //获取历史数据
						break;
					}
					case  1:    //月
					{
						String StartTime,EndTime;
						StartTime=dateFormat.format(new Date(new Date().getTime()-1592000000-1000000000));  //开始时间是现在的时间减去30天，
						EndTime=dateFormat.format(new Date());   //结束的时间就是现在的时间
						if(Name.contains("雨量"))  //如果要查询的是用电量，则将时间调整到零点，以查看全天的用电量
						{
							StartTime=StartTime.substring(0, 10)+" 08:00:00";
							EndTime=EndTime.substring(0,10)+" 08:00:00";
						}
						SoapObject result=soap.Inquiry_HisData(Sid,StartTime,EndTime,"24");
						datalist_hisdata=jiexi.inquiry_hisdata(result);  //获取历史数据
						break;
					}
					default:break;
				}						
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

		 final ArrayList<Map<String,Object>> data_total=new ArrayList<Map<String,Object>>();
		 for(int i=0;i<datalist_hisdata.size();i++)     //将要显示的数据添加到data_total
		 {
			 String gettime=datalist_hisdata.get(i).get("GetTime");
			 Map<String,Object> map=new HashMap<String,Object>();
			 map.put("Name", datalist_hisdata.get(i).get("Value")+datalist_hisdata.get(i).get("Unit"));    //值
//			 if(Name.equals("雨量"))     //如果是显示按天的用电量
//			 {
				 if(Showing==-1)   //日，即每个小时的用电量。显示到时
					 map.put("Value",gettime.substring(0,4)+"年"+gettime.substring(5, 7)+"月"+gettime.substring(8, 10)+"日  "+gettime.substring(11,13)+"点");  //时间
				 else    //周，月，显示到日
					 map.put("Value",gettime.substring(0,4)+"年"+gettime.substring(5, 7)+"月"+gettime.substring(8, 10)+"日  ");  //时间
//			 }
//			 else   //其他，均显示完整的时间
//				 map.put("Value",gettime.substring(0,4)+"年"+gettime.substring(5, 7)+"月"+gettime.substring(8, 10)+"日  "+gettime.substring(11,19));  //时间
			 data_total.add(map);
		 }
		
		 
		 //先清空曲线区域的所有内容
		 layout_graph.removeAllViews();
		 if(datalist_hisdata.size()>0)   //如果有数据，就绘制曲线
		 {
			//添加曲线
			 GraphView graphView;
			 GraphViewData[] data_graph = new GraphViewData[datalist_hisdata.size()];   //构造曲线数组  存放照明数据
				for (int i=0; i<datalist_hisdata.size(); i++) {
					data_graph[i] = new GraphViewData(i, Double.parseDouble(datalist_hisdata.get(i).get("Value")));
				}		
				graphView = new LineGraphView(Hisdata_Activity.this, Name+"("+Measurement+")");  //实例化新的graphView，filed为标题名字
				// add data添加数据，同时定义曲线的属性
				graphView.addSeries(new GraphViewSeries(Name,new GraphViewSeriesStyle(Color.rgb(255, 255, 255), 2),data_graph));
				graphView.setViewPort(0, datalist_hisdata.size()-1);
			    graphView.setScalable(true);     //是否可缩放
			    graphView.setScrollable(true);   //是否可以滚动
				 //自定义横纵坐标显示
				graphView.setCustomLabelFormatter(new CustomLabelFormatter() {
					@Override
					public String formatLabel(double value, boolean isValueX) {
						if (isValueX) {
							if(Showing==-1)   //横坐标单位是小时
							{
								//if((int)value==0||(int)value%3==0||(int)value==datalist_hisdata.size()-1)
									return datalist_hisdata.get((int)value).get("GetTime").substring(11,13).trim()+"点";
								//else
								//	return "";
								
							}
							else if(Showing==1)      //横坐标单位为天
							{			
								//if((int)value==0||(int)value%3==0||(int)value==datalist_hisdata.size()-1)
									return datalist_hisdata.get((int)value).get("GetTime").substring(8,10).trim()+"日";
								//else
								//	return "";
									
													
							}
							else    //横坐标单位为天，7天
							{
								return datalist_hisdata.get((int)value).get("GetTime").substring(8,10).trim()+"日";
							}
						}
						else 
						{
							return Converts.double2String(value);     //保留两位小数
						}
					}
				});		
				graphView.getGraphViewStyle().setTextSize(18);    //设置字体大小
				graphView.getGraphViewStyle().setHorizontalLabelsColor(Color.WHITE);   //设置标题和横坐标文本颜色			
				graphView.getGraphViewStyle().setGridColor(Color.WHITE);         //设置表格颜色
			    if(datalist_hisdata.size()>20) {
					graphView.getGraphViewStyle().setNumHorizontalLabels(datalist_hisdata.size() - Math.abs(Showing) * datalist_hisdata.size() * 2 / 3);     //横坐标刻度线的数量
				}
				else if(datalist_hisdata.size()>10) {
					graphView.getGraphViewStyle().setNumHorizontalLabels(datalist_hisdata.size() - Math.abs(Showing) * datalist_hisdata.size() * 2 / 5);     //横坐标刻度线的数量
				}

				else {
					graphView.getGraphViewStyle().setNumHorizontalLabels(datalist_hisdata.size());     //横坐标刻度线的数量
				}
			    graphView.getGraphViewStyle().setNumVerticalLabels(3);     //纵坐标刻度线的数量
				graphView.getGraphViewStyle().setVerticalLabelsColor(Color.WHITE);      //设置纵坐标文本颜色
				//Toast.makeText(getApplicationContext(),String.valueOf(screenWidth),Toast.LENGTH_SHORT).show(); 
				graphView.getGraphViewStyle().setVerticalLabelsWidth(60);     //Y轴坐标与坐标轴间的距离
				graphView.setBackgroundColor(Color.argb(0x44, 0xff, 0xff, 0xff));    //曲线阴影颜色
				((LineGraphView) graphView).setDrawBackground(true);    //设置曲线下面显示阴影
				// set manual Y axis bounds		
				/*AlphaAnimation animation = new AlphaAnimation(0, 1); 
				animation.setDuration(3000);//设置动画持续时间 				
				animation.setRepeatCount(1);//设置重复次数 
				animation.setFillAfter(true);//动画执行完后是否停留在执行完的状态 
				animation.setStartOffset(0);//执行前的等待时间 
				graphView.startAnimation(animation);    //设置曲线动画*/

//			 graphView.setId(1);    //设置id
				layout_graph.addView(graphView);	
		 }

		SectionListAdapter adapter=new SectionListAdapter(Hisdata_Activity.this);
		adapter.addSection(Name+"("+Measurement+")", new BaseAdapter(){
			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return data_total.size();
			}
			@Override
			public Object getItem(int position) {
				// TODO Auto-generated method stub
				return data_total.get(position);
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
					convertView = LayoutInflater.from(getApplication()).inflate(R.layout.listhisdata_detail, null);
				TextView name=(TextView)convertView.findViewById(R.id.name);
				TextView value=(TextView)convertView.findViewById(R.id.value);
				Map<String,Object> map=data_total.get(position);
				name.setText(map.get("Name").toString());
				value.setText(map.get("Value").toString());
//				convertView.setOnClickListener(new OnClickListener(){
//					@Override
//					public void onClick(View v) {
//						// TODO Auto-generated method stub
//						//Toast.makeText(Hisdata_Activity.this, "你点击了第"+position+"行", Toast.LENGTH_SHORT).show();
//					}});
				return convertView;
			}});
		 //列表添加内容   
		 list.setAdapter(adapter);

	}
}
