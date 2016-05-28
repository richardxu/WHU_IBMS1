package activities;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.ksoap2.serialization.SoapObject;

import convert.Converts;
import views.AppManager;
import views.Switch;
import views.Switch.OnSwitchChangedListener;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.suntrans.whu.R;

import database.DataCentre;
import activities.Search_Activity;
import Adapter.SectionListAdapter;
import Adapter.TouchListener;
import WebServices.jiexi;
import WebServices.soap;
import activities.Expense_Activity.MainThread;
import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class All_Activity extends AppCompatActivity {
	private Bitmap bmp=null;   //定义bitmap变量存放图标
	private final String[] status={"正常","恶性负载","电表锁定","等待"};   //电表状态数组
	private String IsShowing="宿舍";   //正在显示的是学生还是宿舍，默认显示宿舍
	private String UserID="";      //管理员账号
	private String Role="";      //角色号，5是楼长，6是宿管。其他管理员可以查看所有楼栋的信息
	private TextView tx_dorm,tx_stu,tx_general;      //标题栏，宿舍、学生和综合三个textview
	private LinearLayout setting,search;
	private Spinner spinner_area,spinner_building,spinner_floor;  //下拉选择
	private LinearLayout layout_loading,layout_failure;   //加载中。。；加载失败。
	private String[] area,building,floor;    //存放供选择的园区、楼栋、楼层的字符串数组
	private PullToRefreshListView mPullRefreshListView;    //下拉列表控件
	private ListView list;   //列表	
	private ArrayList<String> str=new ArrayList<String>();
	private long time=0;
	private ArrayList<Map<String,String>> datalist_room=new ArrayList<Map<String,String>>();   //所有房间
	private ArrayList<Map<String,String>> datalist_stu=new ArrayList<Map<String,String>>();   //所有学生
	private ArrayList<Map<String,String>> datalist_userinfo=new ArrayList<Map<String,String>>(); //管理员信息
	private ArrayList<Map<String,String>> datalist_roomdetail=new ArrayList<Map<String,String>>();//存放开关状态的所有房间
	private ArrayList<Map<String,String>> datalist_general=new ArrayList<Map<String,String>>();//存放综合信息
	private Toast toast;		
	
	private Handler handler1=new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==1)         //代表请求数据成功
            {                      			
            	if(IsShowing.equals("宿舍"))
            		ListInit();	
            	else if(IsShowing.equals("学生"))
            		ListInit1();
            	else if(IsShowing.equals("综合"))  //如果是综合信息页面
            		ListInit2();            	 	
            	layout_loading.setVisibility(View.GONE);
	            layout_failure.setVisibility(View.GONE);	          
	            tx_dorm.setClickable(true);    //设置标题可以点击,并设置点击监听
        		tx_stu.setClickable(true);
        		tx_general.setClickable(true);
        		//search.setClickable(true);
        		tx_dorm.setOnClickListener(new OnClickListener(){   //宿舍标题点击监听
        			@Override
        			public void onClick(View v) {
						mPullRefreshListView.setMode(Mode.PULL_FROM_START);  //允许下拉
        				tx_dorm.setBackgroundResource(R.drawable.tab_left_select);
        				tx_stu.setBackgroundResource(R.drawable.tab_middle_normal); 
        				tx_general.setBackgroundResource(R.drawable.tab_right_normal); 
        				if(IsShowing.equals("宿舍"))   //如果正在显示的就是宿舍页面，那就不进行操作
        				{					
        					//Toast.makeText(getApplication(), "已经是宿舍", Toast.LENGTH_LONG).show();
        				}
        				else         //如果显示的不是宿舍页面，重新初始化列表
        				{
        					if(IsShowing.equals("综合"))
        					{
        						IsShowing="宿舍";
        						layout_loading.setVisibility(View.VISIBLE);
	        		            layout_failure.setVisibility(View.GONE);
	        		            String[] area1=area;
	        		            area=new String[area1.length-1];
	        		            for(int i=0;i<area.length;i++)
	        		            {
	        		            	area[i]=area1[i];
	        		            }
	        		            spinner_area.setAdapter(new ArrayAdapter<String>(All_Activity.this,R.layout.spinner_item,area){
			   			            @Override
			   			            public View getDropDownView(int position, View convertView,ViewGroup parent) {
			   			                View view = getLayoutInflater().inflate(R.layout.spinner_dropdown_item, parent, false); 
			   			                TextView label = (TextView) view.findViewById(R.id.text1);
			   			                label.setText(getItem(position)); 
			   			                return view;
			   			            }
			   			        });
        					}
        					else
        					{
	        					IsShowing="宿舍";
	        					layout_loading.setVisibility(View.VISIBLE);
	        		            layout_failure.setVisibility(View.GONE);
	        					//楼层
	        					int len=datalist_room.size();
	        					TreeMap<String,String> map_floor=new TreeMap<String,String>();
		   			   			for(int i=0;i<len;i++)
		   			   			{
		   			   				 Map<String,String> map=datalist_room.get(i);
		   			   				 if((map.get("Area").equals(spinner_area.getSelectedItem().toString()))&& ((map.get("Building")+map.get("Unit")).equals(spinner_building.getSelectedItem().toString())))//默认显示第一行，所以找园区为第一行的楼栋单元
		   			   				 {
		   			   					  map_floor.put(map.get("Floor")+"层", "");   
		   			   				 }
		   			   			}
		   			   			floor=new String[map_floor.size()+1];
		   			   			floor[0]="所有";
		   			   			int j=1;
		   			   			for(String key : map_floor.keySet())
		   			   			{
		   			   		         floor[j]=key;   //给楼层数组赋值
		   			   				 j++;
		   			   			}
								Arrays.sort(floor);
			   			   		spinner_floor.setAdapter(new ArrayAdapter<String>(All_Activity.this,R.layout.spinner_item,floor){
			   		   	            @Override
			   		   	            public View getDropDownView(int position, View convertView,ViewGroup parent) {
			   		   	                View view = getLayoutInflater().inflate(R.layout.spinner_dropdown_item, parent, false); 
			   		   	                TextView label = (TextView) view.findViewById(R.id.text1);
			   		   	                label.setText(getItem(position)); 
			   		   	                return view;
			   		   	            }
			   		   	        });
        					}
			   			   	layout_loading.setVisibility(View.GONE);
				            layout_failure.setVisibility(View.GONE);
        				}        				
        			}});
        		tx_stu.setOnClickListener(new OnClickListener(){   //学生标题点击监听
        			@Override
        			public void onClick(View v) {
						mPullRefreshListView.setMode(Mode.DISABLED);   //禁止下拉刷新
        				tx_dorm.setBackgroundResource(R.drawable.tab_left_normal);   
        				tx_stu.setBackgroundResource(R.drawable.tab_middle_select);        				
        				tx_general.setBackgroundResource(R.drawable.tab_right_normal);
        				if(IsShowing.equals("学生"))  //如果正在显示的就是学生页面，不进行操作
        				{						
        				}
        				else   //如果显示的不是学生页面，重新初始化列表
        				{
        					//IsShowing="学生";     
	        				layout_loading.setVisibility(View.VISIBLE);
	        		        layout_failure.setVisibility(View.GONE);   
	        				new Thread3().start();
        					
        				}        				
        			}});
        		tx_general.setOnClickListener(new OnClickListener(){   //综合信息标题点击监听
        			@Override
        			public void onClick(View v) {

        				mPullRefreshListView.setMode(Mode.PULL_FROM_START);
        				tx_dorm.setBackgroundResource(R.drawable.tab_left_normal);          	
        				tx_stu.setBackgroundResource(R.drawable.tab_middle_normal);
        				tx_general.setBackgroundResource(R.drawable.tab_right_select);
        				if(IsShowing.equals("综合"))  //如果正在显示的就是综合页面，不进行操作
        				{						
        				}
        				else   //如果显示的不是综合页面，重新初始化列表
        				{
        					IsShowing="综合";   
        					String[] area1=area;
        					area=new String[area1.length+1];
        					for(int i=0;i<area1.length;i++)
        						area[i]=area1[i];
        					area[area1.length]="所有";
        					
        					String[] building1=building;
        					building=new String[building1.length+1];
        					for(int i=0;i<building1.length;i++)
        						building[i]=building1[i];
        					building[building1.length]="所有";
        					
	   			   			floor=new String[2];
	   			   			floor[0]="照明";
	   			   			floor[1]="所有";
		   			   		spinner_area.setAdapter(new ArrayAdapter<String>(All_Activity.this,R.layout.spinner_item,area){
		   			            @Override
		   			            public View getDropDownView(int position, View convertView,ViewGroup parent) {
		   			                View view = getLayoutInflater().inflate(R.layout.spinner_dropdown_item, parent, false); 
		   			                TextView label = (TextView) view.findViewById(R.id.text1);
		   			                label.setText(getItem(position)); 
		   			                return view;
		   			            }
		   			        });
        				}        				
        			}});

            }
            else if(msg.what==-1)
            {
            	SpinnerAdapter();            	
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
    private Handler handler2=new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==1)         //代表请求数据成功
            {          
            	/*Log.i("Time", "列表初始化前==>"+(System.currentTimeMillis()-time));
            	time=System.currentTimeMillis(); */
            	layout_loading.setVisibility(View.GONE);
	            layout_failure.setVisibility(View.GONE);	
            	if(IsShowing.equals("宿舍"))   //如果是宿舍页面
            		ListInit();	
            	else if(IsShowing.equals("学生"))  //如果是学生页面
            		ListInit1();
            	else if(IsShowing.equals("综合"))  //如果是综合信息页面
            		ListInit2();
            }
            else if(msg.what==0)         //代表请求数据失败
            {
            	layout_loading.setVisibility(View.GONE);
            	layout_failure.setVisibility(View.VISIBLE);            	
            }           
        }};
        // 园区，或楼栋单元发生改变，或者从别的页面切换到学生页面，进行初始化
        private Handler handler3=new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(IsShowing.equals("综合"))
                {
                	IsShowing="学生";
                	String[] area1=area;
		            area=new String[area1.length-1];
		            for(int i=0;i<area.length;i++)
		            {
		            	area[i]=area1[i];
		            }
		            spinner_area.setAdapter(new ArrayAdapter<String>(All_Activity.this,R.layout.spinner_item,area){
   			            @Override
   			            public View getDropDownView(int position, View convertView,ViewGroup parent) {
   			                View view = getLayoutInflater().inflate(R.layout.spinner_dropdown_item, parent, false); 
   			                TextView label = (TextView) view.findViewById(R.id.text1);
   			                label.setText(getItem(position)); 
   			                return view;
   			            }
   			        });
                }
                else
                {
                	IsShowing="学生";
	                //楼层
					int len=datalist_stu.size();
					TreeMap<String,String> map_floor=new TreeMap<String,String>();
			   			for(int i=0;i<len;i++)
			   			{
			   				 Map<String,String> map=datalist_stu.get(i);
			   				 if((map.get("Area").equals(spinner_area.getSelectedItem().toString()))&& ((map.get("Building")+map.get("Unit")).equals(spinner_building.getSelectedItem().toString())))//默认显示第一行，所以找园区为第一行的楼栋单元
			   				 {
			   					  map_floor.put(map.get("Floor")+"层", "");   
			   				 }
			   			}
			   			floor=new String[map_floor.size()];
			   			int j=0;
			   			for(String key : map_floor.keySet())
			   			{
			   		         floor[j]=key;   //给楼层数组赋值
			   				 j++;
			   			}
						Arrays.sort(floor);
				   		spinner_floor.setAdapter(new ArrayAdapter<String>(All_Activity.this,R.layout.spinner_item,floor){
			   	            @Override
			   	            public View getDropDownView(int position, View convertView,ViewGroup parent) {
			   	                View view = getLayoutInflater().inflate(R.layout.spinner_dropdown_item, parent, false); 
			   	                TextView label = (TextView) view.findViewById(R.id.text1);
			   	                label.setText(getItem(position)); 
			   	                return view;
			   	            }
			   	        });	   
                }
                layout_loading.setVisibility(View.GONE);
                layout_failure.setVisibility(View.GONE);            	
                           
            }};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		requestWindowFeature(Window.FEATURE_NO_TITLE);  //去标题栏
		setContentView(R.layout.all);      //设置activity布局文件
		AppManager.getAppManager().addActivity(this);
		Intent intent=getIntent();
		UserID=intent.getStringExtra("UserID");      //获取用户ID
		Role=intent.getStringExtra("Role");     //获取用户角色号
		tx_dorm=(TextView)findViewById(R.id.tx_dorm);  //宿舍
		tx_stu=(TextView)findViewById(R.id.tx_stu);     //学生
		tx_general=(TextView)findViewById(R.id.tx_general);  //综合信息
		setting=(LinearLayout)findViewById(R.id.layout_setting);   //设置
		search=(LinearLayout)findViewById(R.id.layout_search);    //搜索
		layout_loading=(LinearLayout)findViewById(R.id.layout_loading);   //加载中。。
    	layout_failure=(LinearLayout)findViewById(R.id.layout_failure);    //加载失败
		spinner_area=(Spinner)findViewById(R.id.spinner_area);  //园区
		spinner_building=(Spinner)findViewById(R.id.spinner_building);  //楼栋
		spinner_floor=(Spinner)findViewById(R.id.spinner_floor);   //楼层
		mPullRefreshListView = (PullToRefreshListView)findViewById(R.id.list);   //下拉列表控件
    	list=mPullRefreshListView.getRefreshableView();   //从下拉列表控件中获取
		mPullRefreshListView.setMode(Mode.PULL_FROM_START);//只有下拉刷新		
		// 列表下拉监听
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

				// Update the LastUpdatedLabel
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel("上次刷新："+label);

				String Area_now=spinner_area.getSelectedItem().toString();   //现在显示的园区
				String Building_now=spinner_building.getSelectedItem().toString();   //现在显示的楼栋
				String AccountType=spinner_floor.getSelectedItem().toString();   //现在显示的账户类型，或者是Floor，看显示的是什么页面了
				// Do work to refresh the list here.
				new GetDataTask().execute(Area_now,Building_now,AccountType);   //执行任务，传入三个参数，分别是园区、楼栋、楼层（账户类型）
					}
		});
		spinner_area.setOnTouchListener(new TouchListener());   //设置触摸监听
		spinner_building.setOnTouchListener(new TouchListener());
		spinner_floor.setOnTouchListener(new TouchListener());
		tx_dorm.setClickable(false);    //先设置宿舍和学生标题不能点击
		tx_stu.setClickable(false);
		tx_general.setClickable(false);
		//search.setClickable(false);		
		/*菜单栏图标点击监听*/
		setting.setOnClickListener(new OnClickListener(){    //设置图标点击监听
			@Override
			public void onClick(View v) {

				Intent intent=new Intent();
				intent.setClass(All_Activity.this, Setting_Activity.class);   //跳转到设置页面
				intent.putExtra("UserID", UserID);   //账号
				intent.putExtra("Role", Role);    //角色号
				startActivity(intent);
				overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);//设置activity切换效果
				
			}			
		});
		search.setOnClickListener(new OnClickListener(){   //设置搜索图标点击监听
			@Override
			public void onClick(View v) {

				Intent intent=new Intent();
				intent.putExtra("Role", Role);  //管理员角色
				intent.putExtra("datalist_userinfo", (Serializable)datalist_userinfo);
				intent.setClass(All_Activity.this,Search_Activity.class);		//跳转到搜索页面		
				startActivity(intent);
				overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);//设置activity切换效果
			}			
		});

		//点击加载失败，重新加载数据
		layout_failure.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {

				layout_loading.setVisibility(View.VISIBLE);
				layout_failure.setVisibility(View.GONE);
				if(IsShowing.equals("综合"))   //综合页面，请求三相电表数据
					new Thread2().start();
				else          //否则，请求宿舍和学生的数据
					new MainThread().start();    //开始新的刷新线程
			}});
		//下拉菜单选中监听,园区
		spinner_area.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				//Log.i("Button", "园区被选择");

				if(Role.equals("5")||Role.equals("6"))
				{}
				else
				{
					//楼栋和楼层数组根据选中园区进行筛选
					 TreeMap<String,String> map_building=new TreeMap<String,String>();
	    			 TreeMap<String,String> map_floor=new TreeMap<String,String>();
	    			 int len=datalist_room.size();
					 //楼栋单元
		   			 for(int i=0;i<len;i++)
		   			 {
		   				 Map<String,String> map=datalist_room.get(i);
		   				 if(map.get("Area").equals(area[position]))   //默认显示第一行，所以找园区为第一行的楼栋单元
		   				 {
		   					 map_building.put(map.get("Building")+map.get("Unit"),"");//有单元号，就存楼栋和单元
		   				 }
		   			 }
		   			 if(IsShowing.equals("综合"))
		   			 {
		   				building=new String[map_building.size()+1];
			   			 int j=0;
			   			 for(String key : map_building.keySet())
			   			 {
			   				 building[j]=key;
			   				 j++;
			   			 }
			   			 building[building.length-1]="所有";
		   			 }
		   			 else
		   			 {
			   			 building=new String[map_building.size()];
			   			 int j=0;
			   			 for(String key : map_building.keySet())
			   			 {
			   				 building[j]=key;
			   				 j++;
			   			 }
		   			 }
		   			 if(area[position].equals("所有"))   //如果园区选择的是“所有”，只能出现在综合页面，building也显示所有
		   				 building=new String[]{"所有"};
		   			 spinner_building.setAdapter(new ArrayAdapter<String>(All_Activity.this,R.layout.spinner_item,building){
		   	            @Override
		   	            public View getDropDownView(int position, View convertView,ViewGroup parent) {
		   	                View view = getLayoutInflater().inflate(R.layout.spinner_dropdown_item, parent, false); 
		   	                TextView label = (TextView) view.findViewById(R.id.text1);
		   	                label.setText(getItem(position)); 
		   	                return view;
		   	            }
		   	        });	 

				}
			} 
			@Override
			public void onNothingSelected(AdapterView<?> parent) {

				
			}});
		//下拉菜单选中监听,楼栋
		spinner_building.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				Log.i("Button", "楼栋被选择");

				
	   			 if(IsShowing.equals("宿舍"))
	   			 {
	   				 //楼层数组根据选中园区和楼栋进行筛选				
	    			 TreeMap<String,String> map_floor=new TreeMap<String,String>();	
	    			 int len=datalist_room.size();
		   			 //楼层
		   			 for(int i=0;i<len;i++)
		   			 {
		   				 Map<String,String> map=datalist_room.get(i);
		   				 if((map.get("Area").equals(spinner_area.getSelectedItem().toString()))&&(map.get("Building")+map.get("Unit")).equals(building[position]))   //
		   				 {
		   						 map_floor.put(map.get("Floor")+"层", "");  
		   				 }
		   			 }
		   			 floor=new String[map_floor.size()+1];
		   			 floor[0]="所有";
		   			 int j=1;
		   			 for(String key : map_floor.keySet())
		   			 {
		   				 floor[j]=key;    //给楼层数组赋值
		   				 j++;
		   			 }

	   			 }
	   			 else if(IsShowing.equals("学生"))
	   			 {
	   				 //楼层数组根据选中园区和楼栋进行筛选				
	    			 TreeMap<String,String> map_floor=new TreeMap<String,String>();	
	    			 int len=datalist_stu.size();
	    			/* time=System.currentTimeMillis();
	     			 Log.i("Time", "初始化楼层前==>"+time);*/
		   			 //楼层
		   			 for(int i=0;i<len;i++)
		   			 {
		   				 Map<String,String> map=datalist_stu.get(i);
		   				 if((map.get("Area").equals(spinner_area.getSelectedItem().toString()))&&(map.get("Building")+map.get("Unit")).equals(building[position]))   //
		   				 {
		   						 map_floor.put(map.get("Floor")+"层", "");  
		   				 }
		   			 }
		   		     floor=new String[map_floor.size()];		   			
		   			 int j=0;
		   			 for(String key : map_floor.keySet())
		   			 {
		   				 floor[j]=key;   //给楼层数组赋值
		   				 j++;
		   			 }

		   			/*time=System.currentTimeMillis();
	    			Log.i("Time", "初始化楼层后==>"+time);*/
	   			 }
	   			 else if (IsShowing.equals("综合")) // 综合信息页面，楼层这一栏换成照明，所有
				 {
						floor = new String[2];
						floor[0] = "照明";
						floor[1]="所有";
				 }
	   			spinner_floor.setAdapter(new ArrayAdapter<String>(All_Activity.this,R.layout.spinner_item,floor){
	   	            @Override
	   	            public View getDropDownView(int position, View convertView,ViewGroup parent) {
	   	                View view = getLayoutInflater().inflate(R.layout.spinner_dropdown_item, parent, false); 
	   	                TextView label = (TextView) view.findViewById(R.id.text1);
	   	                label.setText(getItem(position)); 
	   	                return view;
	   	            }
	   	        });		   			
			} 
			@Override
			public void onNothingSelected(AdapterView<?> parent) {

				
			}});	
		//下拉菜单选中监听,楼层
		spinner_floor.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				//Log.i("Button", "楼层被选择");

				if(IsShowing.equals("宿舍"))    //宿舍页面，重新初始化listview
	   				ListInit();
	   			else if(IsShowing.equals("学生"))  //学生页面，重新初始化listview
	   				ListInit1();
	   			else if(IsShowing.equals("综合"))  //综合页面，新建线程访问三相电表数据，访问成功后重新初始化列表，否则，显示加载失败
	   			{
	   				layout_loading.setVisibility(View.VISIBLE);   
	            	layout_failure.setVisibility(View.GONE);
	            	new Thread2().start();     //此时是重新选择的电表类型，重新请求网络数据
	   			}
			} 
			@Override
			public void onNothingSelected(AdapterView<?> parent) {

				
			}});	
		new MainThread().start();
			//设置通知栏半透明
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			Converts.setTranslucentStatus(All_Activity.this,true);
		}
	}
	
	//自定义线程，访问网络获取数据，并对数据进行解析和处理
    public class MainThread extends Thread{
    	@Override
    	 public void run()
    	 {
    		 SoapObject result=null;
    		 try
    		 {
    			 if(DataCentre.datalist_room.size()==0&&DataCentre.datalist_stu.size()==0)   //如果数据类中没有数据，则重新请求数据
    			 {
	    			 result=soap.Inquiry_Building();
	    			 datalist_room=jiexi.inquiry_building(result);   //获取所有房间			
	    			 result=soap.Inquiry_Student();
	    			 datalist_stu=jiexi.inquiry_student(result);     //获取宿舍所有成员信息
	    			// Log.i("Time", "访问网络");
    			 }
    			 else    //如果有数据，则不需要再次访问服务器获取
    			 {
    				 datalist_room=DataCentre.datalist_room;
    				 datalist_stu=DataCentre.datalist_stu;
    				// Log.i("Time", "不需要访问网络");
    			 }
    			 if(Role.equals("5"))  //如果是楼长或宿管，则查询其所管理的楼栋
    			 {
	    			 result=soap.Inquiry_UserInfo(UserID);
	    			 datalist_userinfo=jiexi.inquiry_staffinfo(result);
    			 }	 
    			 else if(Role.equals("6"))  //如果是楼长或宿管，则查询其所管理的楼栋
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
  //自定义线程，访问房间具体信息，并对数据进行解析和处理
    public class Thread1 extends Thread{
    	@Override
    	 public void run()
    	 {
    		 SoapObject result=null;
    		 try
    		 {
    			 String Area_now=spinner_area.getSelectedItem().toString();   //现在显示的园区
    		  	 String Building_now=spinner_building.getSelectedItem().toString();   //现在显示的楼栋
    		  	 String Unit_now="";   //现在显示的单元    
    		  	 String Area="";    //园区
    		  	 String Building="";    //楼栋
    		  	 String Unit="";       //单元
    		  	 if(Building_now.contains("单元"))   //如果有单元号，则分别获取楼栋号和单元号
    		  	 {
    		  		 int index=Building_now.lastIndexOf("舍");
    		  		 Unit_now=Building_now.substring(index+1, Building_now.length());
    		  		 Building_now=Building_now.substring(0,index+1);
    		  	 }    
    		  	 //long time=System.currentTimeMillis();
  				// Log.i("Time", "访问具体信息前==>"+time);
				 datalist_roomdetail.clear();
    			 result=soap.Inquiry_Building_Detail(Area_now,Building_now,Unit_now);
    			 datalist_roomdetail=jiexi.inquiry_building_detail(result);   //获取楼栋所有房间的具体信息
    			// time=System.currentTimeMillis();
  				//Log.i("Time", "访问具体信息后==>"+time);     //1.4-2.6s
    			 Message msg=new Message();
    			 msg.what=1;    //代表成功   ，列表显示，隐藏掉加载中页面
    			 handler1.sendMessage(msg);
    			 //依次获取所有宿舍的详细信息，此时用户已经可以看见页面，是后台程序进行获取
    			 //datalist_roomdetail=new ArrayList<Map<String,String>>();
    			 ArrayList<Map<String,String>> data=new ArrayList<Map<String,String>>();
    			 for(int k=0;k<area.length;k++)
    			 {
    				 Area=area[k];   //园区
    				 //楼栋单元
    				 Map<String,String> map_building=new HashMap<String,String>();
    				 for(int i=0;i<datalist_room.size();i++)
    				 {
    					 Map<String,String> map=datalist_room.get(i);
    					 if(map.get("Area").equals(area[k]))  
    					 {
    						 map_building.put(map.get("Building")+map.get("Unit"),"");//有单元号，就存楼栋和单元
    					 }
    				 }    				
    				 for(String key : map_building.keySet())
    				 {
    					  Building=key;
    					  if(Building.contains("单元"))   //如果有单元号，则分别获取楼栋号和单元号
    		    		  {
    		    		  	 int index=Building.lastIndexOf("舍");
    		    		 	 Unit=Building.substring(index+1, Building.length());
    		    		 	 Building=Building.substring(0,index+1);
    		    		  }    	
    					  if(Area_now.equals(Area)&&Building_now.equals(key))
    					  {}    //如果数据是第一组数据，已经获取过，就不再获取
    					  else
    					  {
		    		    		 result=soap.Inquiry_Building_Detail(Area,Building,Unit);
		    		    		 data=jiexi.inquiry_building_detail(result);   //获取楼栋所有房间的具体信息
		    		    		 for(int j=0;j<data.size();j++)
		    		    		 {
		    		    			 Map<String,String> map=data.get(j);
		    		    			 datalist_roomdetail.add(map);
		    		    		 }
    					  }
    				 }
    			 }
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
  //自定义线程，访问三相电表当前状态，并对数据进行解析和处理
    public class Thread2 extends Thread{
    	@Override
    	 public void run()
    	 {
    		 SoapObject result=null;
    		 try
    		 {
    			 String Area_now=spinner_area.getSelectedItem().toString();   //现在显示的园区
    		  	 String Building_now=spinner_building.getSelectedItem().toString();   //现在显示的楼栋
    		  	 String AccountType=spinner_floor.getSelectedItem().toString();   //现在显示的账户类型
    		  	 String Unit_now="";   //现在显示的单元        		  	 
    		  	 if(Building_now.contains("单元"))   //如果有单元号，则分别获取楼栋号和单元号
    		  	 {
    		  		 int index=Building_now.lastIndexOf("舍");
    		  		 Unit_now=Building_now.substring(index+1, Building_now.length());
    		  		 Building_now=Building_now.substring(0,index+1);
    		  	 }        		  	   		  	 
    			 result=soap.Inquiry_General(Area_now,Building_now,Unit_now,AccountType);
    			 datalist_general=jiexi.inquiry_general(result);   //获取三相电表当前状态的具体信息
    			
    			 Message msg=new Message();
    			 msg.what=1;    //代表成功   ，列表显示，隐藏掉加载中页面
    			 handler2.sendMessage(msg);
    			
    		 }
    		 catch(Exception e)
    		 {
    			 Message msg=new Message();
    			 msg.what=0;    //代表失败    			 
    			 handler2.sendMessage(msg);
    		 }
    	       
    	 }
    }
  //自定义线程，对学生页面进行初始化处理
    public class Thread3 extends Thread{
    	@Override
    	 public void run()
    	 {    		 
    		 try
    		 {
    			 Message msg=new Message();
    			 msg.what=1;    //
    			 handler3.sendMessage(msg);
    			
    		 }
    		 catch(Exception e)
    		 {
    			 Message msg=new Message();
    			 msg.what=0;    //	 
    			 handler3.sendMessage(msg);
    		 }
    	       
    	 }
    }
    ///下拉刷新处理的函数。
    private class GetDataTask extends AsyncTask<String, Void, String> {
    	// 后台处理部分
    	@Override
    	protected String doInBackground(String... params) {
    		// Simulates a background job.
    		SoapObject result=null;
    		String str="0";    		 
    		try
    		{
				String Area_now=params[0];
				String Building_now=params[1];
				String AccountType=params[2];
    		  	 String Unit_now="";   //现在显示的单元    
    		  	 String Area;
				 String Building;
    		  	 String Unit="";
    		  	 if(Building_now.contains("单元"))   //如果有单元号，则分别获取楼栋号和单元号
    		  	 {
					int index = Building_now.lastIndexOf("舍");
					Unit_now = Building_now.substring(index + 1,Building_now.length());
					Building_now = Building_now.substring(0, index + 1);
    		  	 } 
    		  	 if(IsShowing.equals("综合"))
    		  	 {
					result = soap.Inquiry_General(Area_now, Building_now,Unit_now, AccountType);
					datalist_general = jiexi.inquiry_general(result); // 获取三相电表当前状态的具体信息
    		  	 }
    		  	 if(IsShowing.equals("宿舍"));
    		  	 {
    		  		result=soap.Inquiry_Building();
	    			 datalist_room=jiexi.inquiry_building(result);   //获取所有房间			
	    			 result=soap.Inquiry_Student();
	    			 datalist_stu=jiexi.inquiry_student(result);     //获取宿舍所有成员信息	    	
	    			 new Thread1().start();
    		  	 }
    		  	 if(IsShowing.equals("学生"))
    		  	 {
    		  		result=soap.Inquiry_Building();
	    			 datalist_room=jiexi.inquiry_building(result);   //获取所有房间			
	    			 result=soap.Inquiry_Student();
	    			 datalist_stu=jiexi.inquiry_student(result);     //获取宿舍所有成员信息	
    		  	 }
    		  	 str="1";   //表示请求成功   						 
	   		 }
    		catch (Exception e) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {

					e1.printStackTrace();
				}
				str = "0"; // 表示请求失败
			}
	    		return str;
	    	}

    	//这里是对刷新的响应，可以利用addFirst（）和addLast()函数将新加的内容加到LISTView中
    	//根据AsyncTask的原理，onPostExecute里的result的值就是doInBackground()的返回值
    	@Override
    	protected void onPostExecute(String result) {    		
    		
    		if(result.equals("1"))  //请求数据成功，根据显示的页面重新初始化listview
    		{
    			if(IsShowing.equals("宿舍"))   //如果是宿舍页面
            		ListInit();	
            	else if(IsShowing.equals("学生"))  //如果是学生页面
            		ListInit1();
            	else if(IsShowing.equals("综合"))  //如果是综合信息页面
            		ListInit2();
    		}
    		else            //请求数据失败
    		{
    			Toast.makeText(getApplicationContext(), "加载失败！", Toast.LENGTH_SHORT).show();
    		}
    		// Call onRefreshComplete when the list has been refreshed.
    		mPullRefreshListView.onRefreshComplete();   //表示刷新完成

    		super.onPostExecute(result);//这句是必有的，AsyncTask规定的格式
    	}
    }
    //为Spinner设置适配器，初始化
    private void SpinnerAdapter()
    {
    	 //以下程序是筛选出三个列表显示的字符串数组    			 
		 TreeMap<String,String> map_area=new TreeMap<String,String>();   
		 TreeMap<String,String> map_building=new TreeMap<String,String>();
		 TreeMap<String,String> map_floor=new TreeMap<String,String>();
		 int len=datalist_room.size();
		 if(Role.equals("5")||Role.equals("6"))   //如果是宿管或楼管，则只能选择管辖的楼栋
		 {
			
			 Map<String,String> map1=datalist_userinfo.get(0);
			 String Area=map1.get("Area");
			 String Building=map1.get("Building");
			 String Unit=map1.get("Unit");
			 area=new String[1];
			 area[0]=Area;			
			 
			 if(Unit==null||Unit.equals(""))  //如果没有单元号，可能他就是楼长，也可能是信息学部宿管
			 {
				 if(Role.equals("5"))  //如果是楼长，则本楼栋可能有单元
				 {
					 //楼栋单元
					 for(int i=0;i<len;i++)
					 {
						 Map<String,String> map2=datalist_room.get(i);
						 if(map2.get("Area").equals(area[0])&&map2.get("Building").equals(Building))   //默认显示第一行，所以找园区为第一行的楼栋单元
						 {
							 map_building.put(map2.get("Building")+map2.get("Unit"),"");//有单元号，就存楼栋和单元
						 }
					 }
					 building=new String[map_building.size()];
					 int j=0;
					 for(String key : map_building.keySet())
					 {
						 building[j]=key;
						 j++;
					 }
				 }
				 else   //如果有单元号，则表明是宿管，只能看这个单元
				 {
					 building=new String[1];
					 building[0]=Building;
				 }
			 }		
			 else    //如果宿管信息中有单元号，则确定楼栋单元，管理员是宿管
			 {
				 building=new String[1];
				 building[0]=Building+Unit;
			 }
			 //楼层
			 for(int i=0;i<len;i++)
			 {
				 Map<String,String> map=datalist_room.get(i);
				 if(map.get("Area").equals(area[0]))   //默认显示第一行，所以找园区为第一行的楼栋单元
				 {				 
					 if((map.get("Building")+map.get("Unit")).equals(building[0]))
						 map_floor.put(map.get("Floor")+"层", "");       						
					 
				 }
			 }
			 floor=new String[map_floor.size()+1];    //初始化楼层数组
			 floor[0]="所有";
			 int j=1;
			 for(String key : map_floor.keySet())
			 {
				 floor[j]=key;  // 给楼层数组赋值
				 j++;
			 }
		 }
		 else    //如果是中心管理员，则可以查看所有楼栋
		 {
			 //园区
			 for(int i=0;i<len;i++)
			 {
				 Map<String,String> map=datalist_room.get(i);
				 map_area.put(map.get("Area"), "");
			 }
			 area=new String[map_area.size()];
			 int j=0;
			 for(String key : map_area.keySet())
			 {
				 area[j]=key;   //给园区数组赋值
				 j++;
			 }
			 //楼栋单元
			 for(int i=0;i<len;i++)
			 {
				 Map<String,String> map=datalist_room.get(i);
				 if(map.get("Area").equals(area[0]))   //默认显示第一行，所以找园区为第一行的楼栋单元
				 {
					 map_building.put(map.get("Building")+map.get("Unit"),"");//有单元号，就存楼栋和单元
				 }
			 }
			 building=new String[map_building.size()];
			 j=0;
			 for(String key : map_building.keySet())
			 {
				 building[j]=key;    //给楼栋数组赋值
				 j++;
			 }
			 //楼层
			 for(int i=0;i<len;i++)
			 {
				 Map<String,String> map=datalist_room.get(i);
				 if(map.get("Area").equals(area[0]))   //默认显示第一行，所以找园区为第一行的楼栋单元
				 {
					 if((map.get("Building")+map.get("Unit")).equals(building[0]))
						 map_floor.put(map.get("Floor")+"层", ""); 					       						
					 
				 }
			 }
			 floor=new String[map_floor.size()+1];
			 floor[0]="所有";
			 j=1;
			 for(String key : map_floor.keySet())
			 {
				 floor[j]=key;   //给楼层数组赋值
				 j++;
			 }
			  //对楼层进行排序
		 }
    	spinner_area.setAdapter(new ArrayAdapter<String>(All_Activity.this,R.layout.spinner_item,area){
            @Override
            public View getDropDownView(int position, View convertView,ViewGroup parent) {
                View view = getLayoutInflater().inflate(R.layout.spinner_dropdown_item, parent, false); 
                TextView label = (TextView) view.findViewById(R.id.text1);
                label.setText(getItem(position)); 
                return view;
            }
        });
		spinner_building.setAdapter(new ArrayAdapter<String>(All_Activity.this,R.layout.spinner_item,building){
            @Override
            public View getDropDownView(int position, View convertView,ViewGroup parent) {
                View view = getLayoutInflater().inflate(R.layout.spinner_dropdown_item, parent, false); 
                TextView label = (TextView) view.findViewById(R.id.text1);
                label.setText(getItem(position)); 
                return view;
            }
        });
		spinner_floor.setAdapter(new ArrayAdapter<String>(All_Activity.this,R.layout.spinner_item,floor){
            @Override
            public View getDropDownView(int position, View convertView,ViewGroup parent) {
                View view = getLayoutInflater().inflate(R.layout.spinner_dropdown_item, parent, false); 
                TextView label = (TextView) view.findViewById(R.id.text1);
                label.setText(getItem(position)); 
                return view;
            }
        });
    }
    //为ListView设置适配器，宿舍页面
  	private void ListInit()
  	{
  		String Area=spinner_area.getSelectedItem().toString();   //园区
  		String Building=spinner_building.getSelectedItem().toString();   //楼栋单元
  		String Floor=spinner_floor.getSelectedItem().toString().replace("层", "");  //楼层
  		SectionListAdapter adapter = new SectionListAdapter(All_Activity.this);  //实例化一个SectionListAdapter
		//添加内容   
		final ArrayList<Map<String,String>> data_roominfo=new ArrayList<Map<String,String>>();  //显示列表的内容数组
		int len=datalist_roomdetail.size();
		if(Floor.equals("所有"))
		{
			for(int k=0;k<len;k++)
			{
				Map<String,String> map=datalist_roomdetail.get(k);
				if(map.get("Area").equals(Area)&&(map.get("Building")+map.get("Unit")).equals(Building))
					data_roominfo.add(map);
			}
		}
		else
		{
			for(int k=0;k<len;k++)
			{
				Map<String,String> map=datalist_roomdetail.get(k);
				if(map.get("Area").equals(Area)&&(map.get("Building")+map.get("Unit")).equals(Building)&&map.get("Floor").equals(Floor))
					data_roominfo.add(map);
			}
		}
		adapter.addSection(Area+"-"+Building+"-"+Floor+"层", new BaseAdapter(){
			@Override
			public int getCount() {

				return data_roominfo.size();
			}
			@Override
			public Object getItem(int position) {

				return data_roominfo.get(position);
			}
			@Override
			public long getItemId(int position) {

				return position;
			}
			@Override
			public View getView(final int position, View convertView, ViewGroup parent) {

				if(convertView==null)
					convertView = LayoutInflater.from(getApplication()).inflate(R.layout.listall_dorm, null);  
				final TextView room=(TextView)convertView.findViewById(R.id.room);    //房间号
				final TextView zm_status=(TextView)convertView.findViewById(R.id.zm_status);  //照明电表状态
				final Switch zm_state=(Switch)convertView.findViewById(R.id.zm_state);  //照明开关状态
				final LinearLayout layout_status = (LinearLayout) convertView.findViewById(R.id.layout_status);
				final LinearLayout layout_state = (LinearLayout) convertView.findViewById(R.id.layout_state);

				final Map<String,String> map=data_roominfo.get(position);
				final String RoomType=map.get("RoomType");   //房间类型
				final String RoomNum=map.get("RoomNum");  //房间号
				final String RoomID=map.get("RoomID");  //房间ID
				final String RoomInfo=map.get("Area")+"-"+map.get("Building")+"-"+(map.get("Unit").equals("")?(map.get("RoomNum")):(map.get("Unit")+"-"+map.get("RoomNum")));    //宿舍信息
				room.setText(RoomNum);
				if(RoomType.equals("0")) {   //如果是学生宿舍，就显示开关状态
					zm_status.setText("照明:"+status[Integer.valueOf(map.get("zmStatus")==null?"1":(map.get("zmStatus")))]);  //电表状态
					if ((map.get("zmState")==null?"null":(map.get("zmState"))).equals("1"))  //照明开关
						zm_state.setState(true);
					else
						zm_state.setState(false);
					layout_state.setVisibility(View.VISIBLE);
					layout_status.setVisibility(View.VISIBLE);
				}
				else {   //卫生间或者活动室等
					layout_state.setVisibility(View.GONE);
					layout_status.setVisibility(View.GONE);
				}
				final AlertDialog.Builder builder = new AlertDialog.Builder(All_Activity.this);
				//照明开关状态改变监听，提示是否发送命令
				zm_state.setOnChangeListener(new OnSwitchChangedListener(){
					@Override
					public void onSwitchChange(Switch switchView,
							final boolean isChecked) {

						builder.setCancelable(false);
						 builder.setTitle("确定要"+(isChecked?"打开":"关闭")+map.get("RoomNum")+"照明开关?"); 
						// builder.setMessage("信息1\n信息2");
						 builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {  
						         public void onClick(DialogInterface dialog, int whichButton) {
						        	 new Thread(){    //新建线程发送命令
						        		 @Override
						        		 public void run(){
						        			 try{
						        				 String result=soap.Insert_Order_RoomID(UserID, map.get("RoomID"), "照明", isChecked?"2":"3");
						        				 if(result.equals("1"))   //命令添加成功
						        				 {
						        					 Looper.prepare();  
							        			     Toast.makeText(getApplicationContext(), "命令已发送！", Toast.LENGTH_SHORT).show();  
							        			     Looper.loop(); 
						        				 }
						        				 else{   //命令添加失败
						        					 Looper.prepare();  
							        			     Toast.makeText(getApplicationContext(), "命令发送失败！", Toast.LENGTH_SHORT).show();  
							        			     Looper.loop(); 
						        				 }
						        			 }
						        			 catch(Exception e){   //网络错误
						        				 Looper.prepare();  
						        			     Toast.makeText(getApplicationContext(), "网络错误！", Toast.LENGTH_SHORT).show();  
						        			     Looper.loop(); 
						        			 }
						        		 }
						        	 }.start();
						         }
								 });
						 builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {  
						         public void onClick(DialogInterface dialog, int whichButton) {  
						        	 zm_state.setState(!isChecked);  //将开关恢复原状
						         }  
						     });  
						    builder.create().show();
					}});
				//选项选择监听
				convertView.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {

						if(RoomType.equals("0")||RoomType.equals("1")) {
							Intent intent = new Intent();
							intent.putExtra("UserID", UserID);       //管理员账号
							intent.putExtra("RoomID", RoomID);       //宿舍ID
							intent.putExtra("RoomType", map.get("RoomType"));   //房间类型
							intent.putExtra("Role", Role);       //角色
							intent.putExtra("RoomInfo", RoomInfo);   //房间信息
							intent.setClass(All_Activity.this, Dorm_Activity.class);//设置要跳转的activity
							startActivity(intent);//开始跳转
							overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);//设置activity切换效果
						}
						else{   //三相电房间
							Intent intent = new Intent();
							intent.putExtra("UserID", UserID);       //管理员账号
							intent.putExtra("RoomID", RoomID);       //宿舍ID
							intent.putExtra("RoomType", map.get("RoomType"));   //房间类型
							intent.putExtra("Role", Role);       //角色
							intent.putExtra("RoomInfo", RoomInfo);   //房间信息
							intent.setClass(All_Activity.this, ThreeData_Activity.class);//设置要跳转的activity
							startActivity(intent);//开始跳转
							overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);//设置activity切换效果

						}
						
					}});
				return convertView;
			}});
		list.setAdapter(adapter);
  	}	
  	
  //为ListView设置适配器，学生页面
  	private void ListInit1()
  	{
  		final String Area=spinner_area.getSelectedItem().toString();   //园区
  		final String Building=spinner_building.getSelectedItem().toString();   //楼栋单元
  		String Floor=spinner_floor.getSelectedItem().toString().replace("层", "");  //楼层
  		SectionListAdapter adapter = new SectionListAdapter(All_Activity.this);  //实例化一个SectionListAdapter
		//添加内容   		
		//Map<String,String> map_roomnum=new HashMap<String,String>(); //存放本层的所有房间号，不重复
		TreeSet<String> set=new TreeSet<String>();   //定义hashset集合存放本层的所有房间号，不重复 
		int len = datalist_stu.size();
		// 对楼层进行筛选，选出符合条件的宿舍房间号，存入set
		for (int k = 0; k < len; k++) 
		{
			Map<String, String> map = datalist_stu.get(k);
			if (map.get("Area").equals(Area)&& (map.get("Building") + map.get("Unit")).equals(Building)&& map.get("Floor").equals(Floor))
				set.add(map.get("RoomNum"));
		}
				
		//每一个房间添加一个section		
		for(String key:set)     //所有房间号
		{			
			final ArrayList<Map<String,String>> data_stu=new ArrayList<Map<String,String>>();
			/*int len1=datalist_stu.size();
			for(int k=0;k<len1;k++)
			{
				Map<String,String> map=datalist_stu.get(k);		
				if(map.get("Area").equals(Area)&&(map.get("Building")+map.get("Unit")).equals(Building)&&map.get("RoomNum").equals(key))
					data_stu.add(map);	
			}	*/
			ListIterator it=datalist_stu.listIterator();   //迭代器
			while(it.hasNext())     //开始不指向任何数据，调用next方法后，指向下一个元素同时返回该元素
			{
				Map<String,String> map=(Map<String,String>)it.next();		
				if(map.get("Area").equals(Area)&&(map.get("Building")+map.get("Unit")).equals(Building)&&map.get("RoomNum").equals(key))
					data_stu.add(map);
			}
			adapter.addSection(Area+"-"+Building+"-"+key, new BaseAdapter(){
				@Override
				public int getCount() {

					return data_stu.size();
				}
				@Override
				public Object getItem(int position) {

					return data_stu.get(position);
				}
				@Override
				public long getItemId(int position) {

					return position;
				}
				@Override
				public View getView(final int position, View convertView, ViewGroup parent) {

					if(convertView==null)
						convertView = LayoutInflater.from(getApplication()).inflate(R.layout.listall_stu, null);  
					TextView name=(TextView)convertView.findViewById(R.id.name);
					TextView value=(TextView)convertView.findViewById(R.id.value);				
					final Map<String,String> map=data_stu.get(position);
					name.setText(map.get("SName"));
					value.setText(map.get("Faculty"));
					//选项选择监听
					convertView.setOnClickListener(new OnClickListener(){
						@Override
						public void onClick(View v) {

							Intent intent=new Intent();
							intent.putExtra("Name", map.get("SName"));       //学生姓名
				        	intent.putExtra("RoomID", map.get("RoomID"));       //宿舍ID
				        	intent.putExtra("RoomInfo", Area+"-"+Building+"-"+map.get("RoomNum"));  //宿舍信息
							intent.setClass(All_Activity.this, Stu_Activity.class);//设置要跳转的activity
							startActivity(intent);//开始跳转	
							overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);//设置activity切换效果
							
						}});
					return convertView;
				}});			
		}
		
  		
		list.setAdapter(adapter);
  	}	  	
  
    //为ListView设置适配器，综合信息页面
  	private void ListInit2()
  	{
  		final String Area=spinner_area.getSelectedItem().toString();   //园区
  		final String Building=spinner_building.getSelectedItem().toString();   //楼栋单元
  		final String Floor=spinner_floor.getSelectedItem().toString();  //账户类型
  		SectionListAdapter adapter = new SectionListAdapter(All_Activity.this);  //实例化一个SectionListAdapter
		final ArrayList<Map<String,String>> data_general=new ArrayList<Map<String,String>>();  //存放综合信息内容的数组
  		//添加内容
		if(Area.equals("所有")||Building.equals("所有")||Floor.equals("所有"))
		{
			String title="";
			if(Area.equals("所有")&&Floor.equals("所有"))
				title="所有园区";
			else if(Area.equals("所有"))
				title="所有园区"+Floor;
			else if(Building.equals("所有")&&Floor.equals("所有"))
				title=Area;
			else if(Building.equals("所有"))
				title=Area+"-"+Floor;
			else
				title=Area+"-"+Building;
			
			if(datalist_general.size()>0&&!Floor.equals("热水"))    //热水不显示电费统计
			{
				final ArrayList<Map<String,String>> data_loss=new ArrayList<Map<String,String>>();
				Map<String,String> map_general=datalist_general.get(0); //存放三相电表当前状态信息
				Map<String,String> map1=new HashMap<String,String>();
				map1.put("Name", "本日预存电费");      //名称
				map1.put("Value", map_general.get("PreSaveDay")+"元");  //值
				map1.put("Type", "PreSave");   //类型，跳转到历史数据页面时传递，请求网络数据时用
				data_loss.add(map1);
				Map<String,String> map2=new HashMap<String,String>();
				map2.put("Name", "本月预存电费");      //名称
				map2.put("Value", map_general.get("PreSaveMonth")+"元");  //值
				map2.put("Type", "PreSave");   //类型，跳转到历史数据页面时传递，请求网络数据时用
				data_loss.add(map2);
				Map<String,String> map3=new HashMap<String,String>();
				map3.put("Name", "本年预存电费");      //名称
				map3.put("Value", map_general.get("PreSaveYear")+"元");  //值
				map3.put("Type", "PreSave");   //类型，跳转到历史数据页面时传递，请求网络数据时用
				data_loss.add(map3);
				Map<String,String> map4=new HashMap<String,String>();
				map4.put("Name", "预存电费总计");      //名称
				map4.put("Value", map_general.get("PreSaveSum")+"元");  //值
				map4.put("Type", "PreSave");   //类型，跳转到历史数据页面时传递，请求网络数据时用
				data_loss.add(map4);
				Map<String,String> map5=new HashMap<String,String>();
				map5.put("Name", "本日消费电费");      //名称
				map5.put("Value", map_general.get("ChargeDay")+"元");  //值
				map5.put("Type", "Charge");   //类型，跳转到历史数据页面时传递，请求网络数据时用
				data_loss.add(map5);
				Map<String,String> map6=new HashMap<String,String>();
				map6.put("Name", "本月消费电费");      //名称
				map6.put("Value", map_general.get("ChargeMonth")+"元");  //值
				map6.put("Type", "Charge");   //类型，跳转到历史数据页面时传递，请求网络数据时用
				data_loss.add(map6);
				Map<String,String> map7=new HashMap<String,String>();
				map7.put("Name", "本年消费电费");      //名称
				map7.put("Value", map_general.get("ChargeYear")+"元");  //值
				map7.put("Type", "Charge");   //类型，跳转到历史数据页面时传递，请求网络数据时用
				data_loss.add(map7);
				Map<String,String> map8=new HashMap<String,String>();
				map8.put("Name", "消费电费总计");      //名称
				map8.put("Value", map_general.get("ChargeSum")+"元");  //值
				map8.put("Type", "Charge");   //类型，跳转到历史数据页面时传递，请求网络数据时用
				data_loss.add(map8);
				adapter.addSection(title+"电费统计", new BaseAdapter(){
					@Override
					public int getCount() {

						return data_loss.size();
					}
					@Override
					public Object getItem(int position) {

						return data_loss.get(position);
					}
					@Override
					public long getItemId(int position) {

						return position;
					}
					@Override
					public View getView(final int position, View convertView, ViewGroup parent) {

						if(convertView==null)
							convertView = LayoutInflater.from(getApplication()).inflate(R.layout.listall_fee, null);  
						ImageView image=(ImageView)convertView.findViewById(R.id.image);
						TextView name=(TextView)convertView.findViewById(R.id.name);
						TextView value=(TextView)convertView.findViewById(R.id.value);				
						final Map<String,String> map=data_loss.get(position);				
						name.setText(map.get("Name"));
						value.setText(map.get("Value"));						
						return convertView;
				}});							
			}
		}
		else
		{
			
			if(datalist_general.size()>0)   //用电量信息
			{
				Map<String,String> map_general=datalist_general.get(0); //存放三相电表当前状态信息
				final ArrayList<Map<String,String>> data_elec=new ArrayList<Map<String,String>>();
				
				Map<String,String> map2=new HashMap<String,String>();
				map2.put("Name", "本日已用电量");      //名称
				map2.put("Value", map_general.get("ElecDay")+"度");  //值
				map2.put("Type", "EletricityValue");   //类型，跳转到历史数据页面时传递，请求网络数据时用
				data_elec.add(map2);
				Map<String,String> map3=new HashMap<String,String>();
				map3.put("Name", "本月已用电量");      //名称
				map3.put("Value", map_general.get("ElecMonth")+"度");  //值
				map3.put("Type", "EletricityValue");   //类型，跳转到历史数据页面时传递，请求网络数据时用
				data_elec.add(map3);				
				Map<String,String> map1=new HashMap<String,String>();
				map1.put("Name", "总用电量");      //名称
				map1.put("Value", map_general.get("Electricity")+"度");  //值
				map1.put("Type", "EletricityValue");   //类型，跳转到历史数据页面时传递，请求网络数据时用
				data_elec.add(map1);
				
				adapter.addSection(Area+"-"+Building+"-"+Floor+"用电情况", new BaseAdapter(){
					@Override
					public int getCount() {

						return data_elec.size();
					}
					@Override
					public Object getItem(int position) {

						return data_elec.get(position);
					}
					@Override
					public long getItemId(int position) {

						return position;
					}
					@Override
					public View getView(final int position, View convertView, ViewGroup parent) {
						if(convertView==null)
							convertView = LayoutInflater.from(getApplication()).inflate(R.layout.listall_general, null);  
						ImageView image=(ImageView)convertView.findViewById(R.id.image);
						TextView name=(TextView)convertView.findViewById(R.id.name);
						TextView value=(TextView)convertView.findViewById(R.id.value);
						ImageView detail = (ImageView) convertView.findViewById(R.id.detail);
						detail.setVisibility(View.GONE);
						final Map<String,String> map=data_elec.get(position);				
						name.setText(map.get("Name"));
						value.setText(map.get("Value"));
						switch(position)   //设置每一行得的图标显示
						{
							case 0:     //总用电量
							case 1:     //本日已用电量
							case 2:     //本月已用电量
							{
								bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_elec);  
								break;
							}	
							case 3:     //本日已用补贴
							case 4:     //本月已用补贴						
							{
								bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_subsidy); 
								break;
							}	
							
							default:break;
						}
						image.setImageBitmap(bmp);
						return convertView;
				}});	
			}
			
			if(datalist_general.size()>0&&Floor.equals("照明"))   //补贴使用情况
			{
				Map<String,String> map_general=datalist_general.get(0); //存放三相电表当前状态信息
				final ArrayList<Map<String,String>> data_elec=new ArrayList<Map<String,String>>();	
					Map<String,String> map4=new HashMap<String,String>();
					map4.put("Name", "本日已用补贴");      //名称
					map4.put("Value", map_general.get("SubsidyDay")+"度");  //值
					map4.put("Type", "EletricityValue");   //类型，跳转到历史数据页面时传递，请求网络数据时用
					data_elec.add(map4);
					Map<String,String> map5=new HashMap<String,String>();
					map5.put("Name", "本月已用补贴");      //名称
					map5.put("Value", map_general.get("SubsidyMonth")+"度");  //值
					map5.put("Type", "EletricityValue");   //类型，跳转到历史数据页面时传递，请求网络数据时用
					data_elec.add(map5);
					Map<String,String> map6=new HashMap<String,String>();
					map6.put("Name", "总已用补贴");      //名称
					map6.put("Value", map_general.get("SubsidySum")+"度");  //值
					map6.put("Type", "EletricityValue");   //类型，跳转到历史数据页面时传递，请求网络数据时用
					data_elec.add(map6);
				
							
				adapter.addSection(Area+"-"+Building+"-"+Floor+"补贴使用情况", new BaseAdapter(){
					@Override
					public int getCount() {

						return data_elec.size();
					}
					@Override
					public Object getItem(int position) {

						return data_elec.get(position);
					}
					@Override
					public long getItemId(int position) {

						return position;
					}
					@Override
					public View getView(final int position, View convertView, ViewGroup parent) {

						if(convertView==null)
							convertView = LayoutInflater.from(getApplication()).inflate(R.layout.listall_fee, null);  
						ImageView image=(ImageView)convertView.findViewById(R.id.image);
						TextView name=(TextView)convertView.findViewById(R.id.name);
						TextView value=(TextView)convertView.findViewById(R.id.value);				
						final Map<String,String> map=data_elec.get(position);				
						name.setText(map.get("Name"));
						value.setText(map.get("Value"));					
								bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_subsidy); 
				
						image.setImageBitmap(bmp);

						return convertView;
				}});	
			}
			
			if(datalist_general.size()>0&&!Floor.equals("热水"))    //电费统计，热水不显示电费统计
			{
				final ArrayList<Map<String,String>> data_loss=new ArrayList<Map<String,String>>();
				Map<String,String> map_general=datalist_general.get(0); //存放三相电表当前状态信息
				Map<String,String> map1=new HashMap<String,String>();
				map1.put("Name", "本日预存电费");      //名称
				map1.put("Value", map_general.get("PreSaveDay")+"元");  //值
				map1.put("Type", "PreSave");   //类型，跳转到历史数据页面时传递，请求网络数据时用
				data_loss.add(map1);
				Map<String,String> map2=new HashMap<String,String>();
				map2.put("Name", "本月预存电费");      //名称
				map2.put("Value", map_general.get("PreSaveMonth")+"元");  //值
				map2.put("Type", "PreSave");   //类型，跳转到历史数据页面时传递，请求网络数据时用
				data_loss.add(map2);
				Map<String,String> map3=new HashMap<String,String>();
				map3.put("Name", "本年预存电费");      //名称
				map3.put("Value", map_general.get("PreSaveYear")+"元");  //值
				map3.put("Type", "PreSave");   //类型，跳转到历史数据页面时传递，请求网络数据时用
				data_loss.add(map3);
				Map<String,String> map4=new HashMap<String,String>();
				map4.put("Name", "总预存电费");      //名称
				map4.put("Value", map_general.get("PreSaveSum")+"元");  //值
				map4.put("Type", "PreSave");   //类型，跳转到历史数据页面时传递，请求网络数据时用
				data_loss.add(map4);
				Map<String,String> map5=new HashMap<String,String>();
				map5.put("Name", "本日消费电费");      //名称
				map5.put("Value", map_general.get("ChargeDay")+"元");  //值
				map5.put("Type", "Charge");   //类型，跳转到历史数据页面时传递，请求网络数据时用
				data_loss.add(map5);
				Map<String,String> map6=new HashMap<String,String>();
				map6.put("Name", "本月消费电费");      //名称
				map6.put("Value", map_general.get("ChargeMonth")+"元");  //值
				map6.put("Type", "Charge");   //类型，跳转到历史数据页面时传递，请求网络数据时用
				data_loss.add(map6);
				Map<String,String> map7=new HashMap<String,String>();
				map7.put("Name", "本年消费电费");      //名称
				map7.put("Value", map_general.get("ChargeYear")+"元");  //值
				map7.put("Type", "Charge");   //类型，跳转到历史数据页面时传递，请求网络数据时用
				data_loss.add(map7);
				Map<String,String> map8=new HashMap<String,String>();
				map8.put("Name", "总消费电费");      //名称
				map8.put("Value", map_general.get("ChargeSum")+"元");  //值
				map8.put("Type", "Charge");   //类型，跳转到历史数据页面时传递，请求网络数据时用
				data_loss.add(map8);
				adapter.addSection(Area+"-"+Building+"-"+Floor+"电费统计", new BaseAdapter(){
					@Override
					public int getCount() {

						return data_loss.size();
					}
					@Override
					public Object getItem(int position) {

						return data_loss.get(position);
					}
					@Override
					public long getItemId(int position) {

						return position;
					}
					@Override
					public View getView(final int position, View convertView, ViewGroup parent) {

						if(convertView==null)
							convertView = LayoutInflater.from(getApplication()).inflate(R.layout.listall_fee, null);  
						ImageView image=(ImageView)convertView.findViewById(R.id.image);
						TextView name=(TextView)convertView.findViewById(R.id.name);
						TextView value=(TextView)convertView.findViewById(R.id.value);				
						final Map<String,String> map=data_loss.get(position);				
						name.setText(map.get("Name"));
						value.setText(map.get("Value"));
						/*switch(position)   //设置每一行的图标显示
						{
							case 0:     //损耗量图标
							case 1:     //
							case 2:     //
							{
								bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_elec);  
								break;
							}	
							case 3:     //损耗率图标
							case 4:     //
							case 5:     //
							{
								bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_lossrate); 
								break;
							}	
							default:break;
						}
						image.setImageBitmap(bmp);
						//选项选择监听
						convertView.setOnClickListener(new OnClickListener(){
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								Intent intent=new Intent();
								intent.putExtra("Name", map.get("Name"));       //参数名称
					        	intent.putExtra("Area", Area);       //园区
					        	intent.putExtra("Building", Building);  //楼栋单元   是楼栋+单元
					        	intent.putExtra("AccountType",Floor);   //账户类型
					        	intent.putExtra("Type", map.get("Type"));  //参数英文名称，访问服务器时用到
								intent.setClass(All_Activity.this, ThreeHisData_Activity.class);//设置要跳转的activity,三相用电历史数据页面
								startActivity(intent);//开始跳转	
								overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);//设置activity切换效果
								
							}});*/
						return convertView;
				}});							
			}
		}
//		if(datalist_general.size()>0)    //电气参数
//		{
//			Map<String,String> map_general=datalist_general.get(0); //存放三相电表当前状态信息
//			Map<String,String> map1=new HashMap<String,String>();
//			map1.put("Name", "A相电压");      //名称
//			map1.put("Value", map_general.get("VolA")+"V");  //值
//			map1.put("Type", "VolA");   //类型，跳转到历史数据页面时传递，请求网络数据时用
//			data_general.add(map1);
//			Map<String,String> map2=new HashMap<String,String>();
//			map2.put("Name", "B相电压");      //名称
//			map2.put("Value", map_general.get("VolB")+"V");  //值
//			map2.put("Type", "VolB");   //类型，跳转到历史数据页面时传递，请求网络数据时用
//			data_general.add(map2);
//			Map<String,String> map3=new HashMap<String,String>();
//			map3.put("Name", "C相电压");      //名称
//			map3.put("Value", map_general.get("VolC")+"V");  //值
//			map3.put("Type", "VolC");   //类型，跳转到历史数据页面时传递，请求网络数据时用
//			data_general.add(map3);
//			Map<String,String> map4=new HashMap<String,String>();
//			map4.put("Name", "A相电流");      //名称
//			map4.put("Value", map_general.get("IA")+"A");  //值
//			map4.put("Type", "IA");   //类型，跳转到历史数据页面时传递，请求网络数据时用
//			data_general.add(map4);
//			Map<String,String> map5=new HashMap<String,String>();
//			map5.put("Name", "B相电流");      //名称
//			map5.put("Value", map_general.get("IB")+"A");  //值
//			map5.put("Type", "IB");   //类型，跳转到历史数据页面时传递，请求网络数据时用
//			data_general.add(map5);
//			Map<String,String> map6=new HashMap<String,String>();
//			map6.put("Name", "C相电流");      //名称
//			map6.put("Value", map_general.get("IC")+"A");  //值
//			map6.put("Type", "IC");   //类型，跳转到历史数据页面时传递，请求网络数据时用
//			data_general.add(map6);
//			Map<String,String> map7=new HashMap<String,String>();
//			map7.put("Name", "有功功率");      //名称
//			map7.put("Value", map_general.get("ActivePower")+"kW");  //值
//			map7.put("Type", "ActivePower");   //类型，跳转到历史数据页面时传递，请求网络数据时用
//			data_general.add(map7);
//			Map<String,String> map8=new HashMap<String,String>();
//			map8.put("Name", "无功功率");      //名称
//			map8.put("Value", map_general.get("ReactivePower")+"kW");  //值
//			map8.put("Type", "ReactivePower");   //类型，跳转到历史数据页面时传递，请求网络数据时用
//			data_general.add(map8);
//
//			adapter.addSection(Area+"-"+Building+"-"+Floor+"电气参数", new BaseAdapter(){
//				@Override
//				public int getCount() {
//
//					return data_general.size();
//				}
//				@Override
//				public Object getItem(int position) {
//
//					return data_general.get(position);
//				}
//				@Override
//				public long getItemId(int position) {
//
//					return position;
//				}
//				@Override
//				public View getView(final int position, View convertView, ViewGroup parent) {
//
//					if(convertView==null)
//						convertView = LayoutInflater.from(getApplication()).inflate(R.layout.listall_general, null);
//					ImageView image=(ImageView)convertView.findViewById(R.id.image);
//					TextView name=(TextView)convertView.findViewById(R.id.name);
//					TextView value=(TextView)convertView.findViewById(R.id.value);
//					final Map<String,String> map=data_general.get(position);
//					name.setText(map.get("Name"));
//					value.setText(map.get("Value"));
//					switch(position)   //设置每一行得的图标显示
//					{
//						case 0:     //A相电压图标
//						case 1:     //B相电压
//						case 2:     //C相电压
//						{
//							bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_voltage);
//							break;
//						}
//						case 3:     //A相电流图标
//						case 4:     //B相电流
//						case 5:     //C相电流
//						{
//							bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_current);
//							break;
//						}
//						case 6:    //有功功率图标
//						case 7:    //无功功率
//						{
//							bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_power);
//							break;
//						}
//						/*case 8:
//						{
//							bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_powerrate);
//							break;
//						}*/
//						/*case 8:
//						case 9:
//						case 10:
//						{
//							bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_elec);
//							break;
//						}*/
//						default:break;
//					}
//					image.setImageBitmap(bmp);
//					//选项选择监听
//					convertView.setOnClickListener(new OnClickListener(){
//						@Override
//						public void onClick(View v) {
//
//							Intent intent=new Intent();
//							intent.putExtra("Name", map.get("Name"));       //参数名称
//				        	intent.putExtra("Area", Area);       //园区
//				        	intent.putExtra("Building", Building);  //楼栋单元   是楼栋+单元
//				        	intent.putExtra("AccountType",Floor);   //账户类型
//				        	intent.putExtra("Type", map.get("Type"));  //参数英文名称，访问服务器时用到
//							intent.setClass(All_Activity.this, ThreeHisData_Activity.class);//设置要跳转的activity,三相用电历史数据页面
//							startActivity(intent);//开始跳转
//							overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);//设置activity切换效果
//
//						}});
//					return convertView;
//			}});
//		}

		list.setAdapter(adapter);
  	}	  	


}
