package activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.suntrans.whu.R;

import org.ksoap2.serialization.SoapObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import Adapter.RecyclerViewDivider;
import Adapter.TouchListener;
import WebServices.jiexi;
import WebServices.soap;
import convert.Converts;
import database.DataCentre;
import views.AppManager;
import views.Switch;

/**
 * Created by 石奋斗 on 2016/5/21.
 */
public class AllRoom_Activity extends AppCompatActivity {
    private Bitmap bmp=null;   //定义bitmap变量存放图标
    private final String[] status={"正常","恶性负载","电表锁定","等待"};   //电表状态数组
    private String IsShowing="宿舍";   //正在显示的是学生还是宿舍，默认显示宿舍
    private String UserID="";      //管理员账号
    private String Role="";      //角色号，5是楼长，6是宿管。其他管理员可以查看所有楼栋的信息
    private TextView tx_dorm,tx_stu,tx_general;      //标题栏，宿舍、学生和综合三个textview
    private LinearLayout setting,search;
    private Spinner spinner_area,spinner_building,spinner_floor;  //下拉选择
    private LinearLayout layout_loading,layout_failure;   //加载中。。；加载失败。
    private SwipeRefreshLayout refreshlayout;  //下拉控件
    private RecyclerView recyclerview;    //列表
    private String[] area,building,floor;    //存放供选择的园区、楼栋、楼层的字符串数组
    private SoapObject result1;    //刷新时用到的中间变量
    private ArrayList<Map<String, String>> data1 = new ArrayList<>();   //刷新时用到的中间变量
    private Adapter_Dorm adapter_dorm;  //宿舍适配器
    private Adapter_Stu adapter_stu;    //学生适配器
    private Adapter_General adapter_general;   //综合信息适配器
    private boolean IfRefresh=true;   //是否需要刷新，true表示需要刷新，false表示不需要刷新
    private int IsFinish=0;   //是否完成下拉刷新，1表示完成下拉刷新，0表示未完成下拉刷新
    private boolean IfRefreshOn=false;    //刷新线程是否开启，true表示已经开启，false表示未开启
    private Map<String, String> map_building = new HashMap<>();  //键是园区，值是楼栋（单元）
    private Map<String, String> map_floor_dorm = new HashMap<>();  //键是园区+楼栋(单元)，值是楼层（宿舍页面 ）
    private Map<String, String> map_floor_stu = new HashMap<>();   //键是园区+楼栋（单元），值是楼层（学生页面）

    private ArrayList<Map<String,String>> datalist_room=new ArrayList<Map<String,String>>();   //所有房间
    private ArrayList<Map<String,String>> datalist_stu=new ArrayList<Map<String,String>>();   //所有学生
    private ArrayList<Map<String,String>> datalist_userinfo=new ArrayList<Map<String,String>>(); //管理员信息
    private ArrayList<Map<String,String>> datalist_roomdetail=new ArrayList<Map<String,String>>();//存放开关状态的所有房间
    private ArrayList<Map<String,String>> datalist_roomdetail_refresh=new ArrayList<Map<String,String>>();//刷新时使用的存放开关状态的所有房间，刷新完毕后赋值给datalist_roomdetail
    private ArrayList<Map<String,String>> datalist_general=new ArrayList<Map<String,String>>();//存放综合信息
    private Vector<Map<String,String>> data_dorm=new Vector<>();   //单个页面显示的房间信息
    private Vector<Map<String, String>> data_dorm1 = new Vector<>();
    private ArrayList<Map<String,String>> data_stu=new ArrayList<>();   //单个页面显示的学生信息
    private ArrayList<Map<String,String>> data_general=new ArrayList<>();   //显示的综合信息
    private Handler handler1=new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==1)         //代表请求数据成功
            {
                ViewInit();   //显示页面，为recyclerview设置适配器
                layout_loading.setVisibility(View.GONE);
                layout_failure.setVisibility(View.GONE);
                tx_dorm.setClickable(true);    //设置标题可以点击,并设置点击监听
                tx_stu.setClickable(true);
                tx_general.setClickable(true);
                //search.setClickable(true);
                tx_dorm.setOnClickListener(new View.OnClickListener(){   //宿舍标题点击监听
                    @Override
                    public void onClick(View v) {
                        refreshlayout.setEnabled(true);    //允许下拉刷新
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
                                if(!(Role.equals("5")||Role.equals("6"))) {
                                    String[] area1 = area;
                                    area = new String[area1.length - 1];
                                    for (int i = 0; i < area.length; i++) {
                                        area[i] = area1[i];
                                    }
                                }
                                spinner_area.setAdapter(new ArrayAdapter<String>(AllRoom_Activity.this,R.layout.spinner_item,area){
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
                                //楼层
                                floor = map_floor_dorm.get(spinner_area.getSelectedItem().toString() + spinner_building.getSelectedItem().toString()).split(";");
                                spinner_floor.setAdapter(new ArrayAdapter<String>(AllRoom_Activity.this,R.layout.spinner_item,floor){
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
                tx_stu.setOnClickListener(new View.OnClickListener(){   //学生标题点击监听
                    @Override
                    public void onClick(View v) {
                        refreshlayout.setEnabled(false);   //禁止下拉刷新
                        tx_dorm.setBackgroundResource(R.drawable.tab_left_normal);
                        tx_stu.setBackgroundResource(R.drawable.tab_middle_select);
                        tx_general.setBackgroundResource(R.drawable.tab_right_normal);
                        if(IsShowing.equals("学生"))  //如果正在显示的就是学生页面，不进行操作
                        {
                        }
                        else   //如果显示的不是学生页面，重新初始化列表
                        {
                            if(IsShowing.equals("综合"))
                            {
                                IsShowing="学生";
                                if(!(Role.equals("5")||Role.equals("6"))) {
                                    String[] area1 = area;
                                    area = new String[area1.length - 1];
                                    for (int i = 0; i < area.length; i++) {
                                        area[i] = area1[i];
                                    }
                                }
                                spinner_area.setAdapter(new ArrayAdapter<String>(AllRoom_Activity.this,R.layout.spinner_item,area){
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
                                floor = map_floor_stu.get(spinner_area.getSelectedItem().toString() + spinner_building.getSelectedItem().toString()).split(";");
                                spinner_floor.setAdapter(new ArrayAdapter<String>(AllRoom_Activity.this,R.layout.spinner_item,floor){
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
                tx_general.setOnClickListener(new View.OnClickListener(){   //综合信息标题点击监听
                    @Override
                    public void onClick(View v) {

                        refreshlayout.setEnabled(true);    //允许下拉
                        tx_dorm.setBackgroundResource(R.drawable.tab_left_normal);
                        tx_stu.setBackgroundResource(R.drawable.tab_middle_normal);
                        tx_general.setBackgroundResource(R.drawable.tab_right_select);
                        if(IsShowing.equals("综合"))  //如果正在显示的就是综合页面，不进行操作
                        {
                        }
                        else   //如果显示的不是综合页面，重新初始化列表
                        {
                            IsShowing="综合";
                            if(Role.equals("5")||Role.equals("6")){}  //如果是宿管，只需要把最右边的下拉菜单改成账户类型
                            else {   //如果是中心管理员，则显示所有楼栋和所有园区
                                String[] area1 = area;
                                area = new String[area1.length + 1];
                                for (int i = 0; i < area1.length; i++)
                                    area[i] = area1[i];
                                area[area1.length] = "所有";

                                String[] building1 = building;
                                building = new String[building1.length + 1];
                                for (int i = 0; i < building1.length; i++)
                                    building[i] = building1[i];
                                building[building1.length] = "所有";
                            }
                            floor=new String[2];
                            floor[0]="照明";
                            floor[1]="所有";
                            spinner_area.setAdapter(new ArrayAdapter<String>(AllRoom_Activity.this,R.layout.spinner_item,area){
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
                IfRefresh=true;
                if(IfRefreshOn==false) {
                    new RefreshThread().start();   //开始刷新线程，定时刷新数据
                }
            }
            else if(msg.what==-1)
            {
                SpinnerAdapter();  //初始化下拉菜单，确定一共有几个园区，每个园区对应几个楼栋，对应几层楼
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
        }
    };
    private Handler handler2=new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==1)         //代表请求数据成功
            {          
            	/*Log.i("Time", "列表初始化前==>"+(System.currentTimeMillis()-time));
            	time=System.currentTimeMillis(); */
                layout_loading.setVisibility(View.GONE);
                layout_failure.setVisibility(View.GONE);
                ViewInit();
            }
            else if(msg.what==0)         //代表请求数据失败
            {
                layout_loading.setVisibility(View.GONE);
                layout_failure.setVisibility(View.VISIBLE);
            }
        }
    };
    private Handler handler3=new Handler(){
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            adapter_dorm.notifyDataSetChanged();
        }
    };
  /*  // 园区，或楼栋单元发生改变，或者从别的页面切换到学生页面，进行初始化
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
                spinner_area.setAdapter(new ArrayAdapter<String>(AllRoom_Activity.this,R.layout.spinner_item,area){
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
                spinner_floor.setAdapter(new ArrayAdapter<String>(AllRoom_Activity.this,R.layout.spinner_item,floor){
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
    };*/
    
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.allroom);
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
        refreshlayout = (SwipeRefreshLayout) findViewById(R.id.refreshlayout);  //下拉控件
        recyclerview = (RecyclerView) findViewById(R.id.recyclerview);   //列表控件
        spinner_area.setOnTouchListener(new TouchListener());   //设置触摸监听
        spinner_building.setOnTouchListener(new TouchListener());
        spinner_floor.setOnTouchListener(new TouchListener());
        tx_dorm.setClickable(false);    //先设置宿舍和学生标题不能点击
        tx_stu.setClickable(false);
        tx_general.setClickable(false);
        //search.setClickable(false);
		/*菜单栏图标点击监听*/
        setting.setOnClickListener(new View.OnClickListener(){    //设置图标点击监听
            @Override
            public void onClick(View v) {

                Intent intent=new Intent();
                intent.setClass(AllRoom_Activity.this, Setting_Activity.class);   //跳转到设置页面
                intent.putExtra("UserID", UserID);   //账号
                intent.putExtra("Role", Role);    //角色号
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);//设置activity切换效果

            }
        });
        search.setOnClickListener(new View.OnClickListener(){   //设置搜索图标点击监听
            @Override
            public void onClick(View v) {

                Intent intent=new Intent();
                intent.putExtra("UserID", UserID);   //管理员账号
                intent.putExtra("Role", Role);  //管理员角色
                intent.putExtra("datalist_userinfo", (Serializable)datalist_userinfo);
                intent.setClass(AllRoom_Activity.this,Search_Activity.class);		//跳转到搜索页面
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);//设置activity切换效果
            }
        });
        //点击加载失败，重新加载数据
        layout_failure.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                layout_loading.setVisibility(View.VISIBLE);
                layout_failure.setVisibility(View.GONE);
                if(IsShowing.equals("综合"))   //综合页面，请求三相电表数据
                    new Thread2().start();
                else          //否则，请求宿舍和学生的数据
                    new MainThread().start();    //开始新的刷新线程
            }});
        //		    设置通知栏半透明
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Converts.setTranslucentStatus(AllRoom_Activity.this,true);
        }
        /****配置下拉控件****/
        refreshlayout.setSize(SwipeRefreshLayout.LARGE);  //设置大小
        refreshlayout.setColorSchemeResources(R.color.bg_action,R.color.green,R.color.yellow);   //设置滚动条颜色
        refreshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                IsFinish=0;   //表示开始刷新
                String Area_now=spinner_area.getSelectedItem().toString();   //现在显示的园区
                String Building_now=spinner_building.getSelectedItem().toString();   //现在显示的楼栋
                String AccountType=spinner_floor.getSelectedItem().toString();   //现在显示的账户类型，或者是Floor，看显示的是什么页面了
                new GetDataTask().execute(Area_now,Building_now,AccountType);   //执行任务
            }
        });

        /***配置列表控件*****/
        //设置布局管理器
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        //添加分割线，自定义RecyclerViewDivider继承RecyclerView.ItemDecoration
        Context context=getApplicationContext();
        recyclerview.addItemDecoration(new RecyclerViewDivider(this,LinearLayoutManager.VERTICAL,
                Converts.dip2px(context,1f),R.color.gray));  //不知道为什么，此处修改分割线的颜色无效
        //设置Adapter适配器
        adapter_dorm=new Adapter_Dorm();
        adapter_stu=new Adapter_Stu();
        adapter_general=new Adapter_General();
        recyclerview.setAdapter(adapter_dorm);

        //下拉菜单选中监听,园区
        spinner_area.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                //Log.i("Button", "园区被选择");

                if(Role.equals("5")||Role.equals("6"))
                {
                    spinner_building.setAdapter(new ArrayAdapter<String>(AllRoom_Activity.this,R.layout.spinner_item,building){
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
                    if(area[position].equals("所有"))   //如果园区选择的是“所有”，只能出现在综合页面，building也显示所有
                        building=new String[]{"所有"};
                    else {
                        String[] temp_building=map_building.get(area[position]).split(";");
                        if (IsShowing.equals("综合")) {
                            building = new String[temp_building.length + 1];
                            int j = 0;
                            for (j = 0; j < temp_building.length; j++) {
                                building[j] = temp_building[j];
                                j++;
                            }
                            building[building.length - 1] = "所有";
                        } else {
                            building = temp_building.clone();
                        }
                    }

                    spinner_building.setAdapter(new ArrayAdapter<String>(AllRoom_Activity.this,R.layout.spinner_item,building){
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
        spinner_building.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Log.i("Button", "楼栋被选择");


                if(IsShowing.equals("宿舍"))
                {
                    floor = map_floor_dorm.get(spinner_area.getSelectedItem().toString() + building[position]).split(";");

                }
                else if(IsShowing.equals("学生"))
                {
                    floor = map_floor_stu.get(spinner_area.getSelectedItem().toString() + building[position]).split(";");

                }
                else if (IsShowing.equals("综合")) // 综合信息页面，楼层这一栏换成照明，所有
                {
                    floor = new String[2];
                    floor[0] = "照明";
                    floor[1]="所有";
                }
                spinner_floor.setAdapter(new ArrayAdapter<String>(AllRoom_Activity.this,R.layout.spinner_item,floor){
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
        spinner_floor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                //Log.i("Button", "楼层被选择");

                if(IsShowing.equals("综合"))  //综合页面，新建线程访问三相电表数据，访问成功后重新初始化列表，否则，显示加载失败
                {
                    layout_loading.setVisibility(View.VISIBLE);
                    layout_failure.setVisibility(View.GONE);
                    new Thread2().start();     //此时是重新选择的电表类型，重新请求网络数据
                }
                else{
                    ViewInit();
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {


            }});
        new MainThread().start();   //开始新线程查询宿舍和学生的具体信息
        try {
            String IPAddr = Converts.getLocalIPAddress();
            Log.i("IPAddr", IPAddr);

        }
        catch(Exception e){}
    }

    @Override
    public void onPause(){
        super.onPause();
        IfRefresh=false;
    }
    @Override
    public void onResume(){
        super.onResume();
        IfRefresh=true;
        if(IfRefreshOn==false){   //如果刷新线程未开启，则开启刷新线程
            new RefreshThread().start();
        }
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        IfRefresh=false;  //停止刷新

    }

    //自定义线程，访问网络获取数据，并对数据进行解析和处理
    public class MainThread extends Thread{
        @Override
        public void run()
        {
            SoapObject result=null;
            try
            {

                if (DataCentre.datalist_room.size() == 0 && DataCentre.datalist_stu.size() == 0)   //如果数据类中没有数据，则重新请求数据
                {
                    result = soap.Inquiry_Building();
                    datalist_room = jiexi.inquiry_building(result);   //获取所有房间
                    result = soap.Inquiry_Student();
                    datalist_stu = jiexi.inquiry_student(result);     //获取宿舍所有成员信息
                    // Log.i("Time", "访问网络");
                }

                else    //如果有数据，则不需要再次访问服务器获取
                {
                    datalist_room=DataCentre.datalist_room;
                    datalist_stu=DataCentre.datalist_stu;
                    // Log.i("Time", "不需要访问网络");
                }
                if(Role.equals("5"))  //如果是楼长，则查询其所管理的楼栋
                {
                    result=soap.Inquiry_UserInfo(UserID);
                    datalist_userinfo=jiexi.inquiry_staffinfo(result);
                }
                else if(Role.equals("6"))  //如果是宿管，则查询其所管理的楼栋(或者包括单元)
                {
                    result=soap.Inquiry_UserInfo(UserID);
                    datalist_userinfo=jiexi.inquiry_boomerinfo(result);
                }

                Message msg=new Message();
                msg.what=-1;    //代表成功，但是没有获取房间开关状态，所以初始化下拉菜单显示，但是先不显示详细信息
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
                    int index;
                    if(Building_now.contains("舍"))
                        index=Building_now.lastIndexOf("舍");
                    else
                        index=Building_now.lastIndexOf("楼");
                    Unit_now=Building_now.substring(index+1, Building_now.length());
                    Building_now=Building_now.substring(0,index+1);
                }
                //long time=System.currentTimeMillis();
                // Log.i("Time", "访问具体信息前==>"+time);    		  	 
                result=soap.Inquiry_Building_Detail(Area_now,Building_now,Unit_now);
                datalist_roomdetail=jiexi.inquiry_building_detail(result);   //获取楼栋所有房间的具体信息
                // time=System.currentTimeMillis();
                //Log.i("Time", "访问具体信息后==>"+time);     //1.4-2.6s
                Message msg=new Message();
                msg.what=1;    //代表成功   ，列表显示，隐藏掉加载中页面
                handler1.sendMessage(msg);
                //依次获取所有宿舍的详细信息，此时用户已经可以看见页面，是后台程序进行获取
                //datalist_roomdetail=new ArrayList<Map<String,String>>();
                for(String key:map_building.keySet()){
                    String[] temp_building=map_building.get(key).split(";");
                    for(int i=0;i<temp_building.length;i++){
                        if (!(key.equals(Area_now) && temp_building[i].equals(Building_now))) {
                            SoapObject result1=soap.Inquiry_Building_Detail(key,temp_building[i],"");
                            ArrayList<Map<String,String>> data1=jiexi.inquiry_building_detail(result1);   //获取本楼栋所有房间的具体信息
                            datalist_roomdetail.addAll(data1);
                        }

                    }
                }
                IsFinish=1;
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
                    int index;
                    if(Building_now.contains("舍"))
                        index=Building_now.lastIndexOf("舍");
                    else
                        index=Building_now.lastIndexOf("楼");
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
    //自定义刷新线程，定时刷新房间开关状态
    private class RefreshThread extends Thread{
        @Override
        public void run(){
            IfRefreshOn=true;  //表示刷新线程已经开启
            try{
                Thread.sleep(2000);
            }
            catch(Exception e){
                e.printStackTrace();
            }
            while(IfRefresh){
                try{
                    Thread.sleep(2000);
                }
                catch(Exception e){
                    e.printStackTrace();
                }
                if(IsFinish==1){
                    Log.i("Thread","all开始刷新");
                    try {
                        datalist_roomdetail_refresh.clear();
                        for(String key:map_building.keySet()) {   //所有园区
                            String[] temp_building = map_building.get(key).split(";");
                            for (int i = 0; i < temp_building.length; i++) {       //所有楼栋单元

                                result1 = soap.Inquiry_Building_Detail(key, temp_building[i], "");
                                data1 = jiexi.inquiry_building_detail(result1);   //获取本楼栋所有房间的具体信息
                                datalist_roomdetail_refresh.addAll(data1);

                            }
                        }
                        synchronized (datalist_roomdetail){  //同步datalist_roomdetail数据
                           datalist_roomdetail=(ArrayList<Map<String,String>>) datalist_roomdetail_refresh.clone();
                            if(IsShowing.equals("宿舍")) {
                                    data_dorm1.clear();
                                    int len = datalist_roomdetail.size();
                                    if (spinner_floor.getSelectedItem().toString().equals("所有")) {
                                        for (int k = 0; k < len; k++) {
                                            Map<String, String> map = datalist_roomdetail.get(k);
                                            if (map.get("Area").equals(spinner_area.getSelectedItem().toString()) && (map.get("Building") + map.get("Unit")).equals(spinner_building.getSelectedItem().toString()))
                                                data_dorm1.add(map);
                                        }
                                    } else {
                                        for (int k = 0; k < len; k++) {
                                            Map<String, String> map = datalist_roomdetail.get(k);
                                            if (map.get("Area").equals(spinner_area.getSelectedItem().toString()) && (map.get("Building") + map.get("Unit")).equals(spinner_building.getSelectedItem().toString()) && (map.get("Floor") + ("层")).equals(spinner_floor.getSelectedItem().toString()))
                                                data_dorm1.add(map);
                                        }
                                    }
                                data_dorm=(Vector<Map<String,String>>) data_dorm1.clone();
                                handler3.sendEmptyMessage(1);
                            }


                        }

                    }
                    catch(Exception e){
                        Log.i("Thread", e.toString());
                    }
                }
            }
            IfRefreshOn=false;   //表示刷新线程已经关闭

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
                final String Area_now=params[0];   //现在显示的园区
                final String Building_now=params[1];   //现在显示的楼栋+单元
                String AccountType=params[2];    //现在显示的楼层（账户类型）
                String Unit_now="";   //现在显示的单元
//                if(Building_now.contains("单元"))   //如果有单元号，则分别获取楼栋号和单元号
//                {
//                    int index;
//                    if(Building_now.contains("舍"))
//                        index=Building_now.lastIndexOf("舍");
//                    else
//                        index=Building_now.lastIndexOf("楼");
//                    Unit_now = Building_now.substring(index + 1,Building_now.length());
//                    Building_now = Building_now.substring(0, index + 1);
//                }
                if(IsShowing.equals("综合"))
                {
                    result = soap.Inquiry_General(Area_now, Building_now,Unit_now, AccountType);
                    datalist_general = jiexi.inquiry_general(result); // 获取三相电表当前状态的具体信息
                    IsFinish=1;  //表示已经完成刷新
                }
                if(IsShowing.equals("宿舍"));
                {
//                    result=soap.Inquiry_Building();
//                    datalist_room=jiexi.inquiry_building(result);   //获取所有房间
//                    result=soap.Inquiry_Student();
//                    datalist_stu=jiexi.inquiry_student(result);     //获取宿舍所有成员信息
//                    new Thread1().start();
                    result=soap.Inquiry_Building_Detail(Area_now,Building_now,Unit_now);
                    datalist_roomdetail=jiexi.inquiry_building_detail(result);   //获取本楼栋所有房间的具体信息
                    new Thread(){
                        @Override
                        public void run(){
                            for(String key:map_building.keySet()){
                                String[] temp_building=map_building.get(key).split(";");
                                for(int i=0;i<temp_building.length;i++){
                                    if (!(key.equals(Area_now) && temp_building[i].equals(Building_now))) {
                                        SoapObject result1=soap.Inquiry_Building_Detail(key,temp_building[i],"");
                                        ArrayList<Map<String,String>> data1=jiexi.inquiry_building_detail(result1);   //获取本楼栋所有房间的具体信息
                                        datalist_roomdetail.addAll(data1);
                                    }

                                }
                            }
                            IsFinish=1;   //完成刷新
                        }
                    }.start();

                }
//                if(IsShowing.equals("学生"))
//                {
//                    result=soap.Inquiry_Building();
//                    datalist_room=jiexi.inquiry_building(result);   //获取所有房间
//                    result=soap.Inquiry_Student();
//                    datalist_stu=jiexi.inquiry_student(result);     //获取宿舍所有成员信息
//                }
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
                ViewInit();   //页面显示
            }
            else            //请求数据失败
            {
                Toast.makeText(getApplicationContext(), "刷新失败！", Toast.LENGTH_SHORT).show();
            }
            // Call onRefreshComplete when the list has been refreshed.
            refreshlayout.setRefreshing(false);   //结束加载动作

            super.onPostExecute(result);//这句是必有的，AsyncTask规定的格式
        }
    }


    //为Spinner设置适配器，初始化
    private void SpinnerAdapter()
    {
        //以下程序是筛选出三个列表显示的字符串数组    			 
        TreeMap<String,String> map_area=new TreeMap<String,String>();
        TreeMap<String,String> map_building1=new TreeMap<String,String>();
        TreeMap<String,String> map_floor=new TreeMap<String,String>();
        int len=datalist_room.size();   //房间数据长度
        int len_stu=datalist_stu.size();    //学生数据长度
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
                    //先选出对应的楼栋单元

                    map_building1.clear();
                    for(int i=0;i<len;i++)
                    {
                        Map<String,String> map=datalist_room.get(i);
                        if(map.get("Area").equals(area[0]))   //默认显示第一行，所以找园区为第一行的楼栋单元
                        {
                            map_building1.put(map.get("Building")+map.get("Unit"),"");//有单元号，就存楼栋和单元
                        }
                    }

                    String str_building="";
                    for(String key : map_building1.keySet())
                    {
                        str_building+=key+";";    //给楼栋数组赋值
                    }
                    map_building.put(area[0],str_building);
                    building = map_building.get(area[0]).split(";");   //当前园区的所有楼栋

                }
                else   //如果是宿管，只能看这个单元
                {
                    building=new String[1];
                    building[0]=Building+Unit;
                    map_building.put(area[0], building[0]);
                }
            }
            else    //如果宿管信息中有单元号，则确定楼栋单元，管理员是宿管
            {
                building=new String[1];
                building[0]=Building+Unit;
                map_building.put(area[0], building[0]);
            }
            for(int y=0;y<building.length;y++){
                map_floor.clear();
                for(int i=0;i<len;i++)
                {
                    Map<String,String> map=datalist_room.get(i);
                    if(map.get("Area").equals(area[0]))   //默认显示第一行，所以找园区为第一行的楼栋单元
                    {
                        if((map.get("Building")+map.get("Unit")).equals(building[y]))
                            map_floor.put(map.get("Floor"), "");
                    }
                }
                int[] floor_old=new int[map_floor.size()];  //楼号
                int k=0;
                String str_floor;   //存放所有的楼层号，用分号隔开，例如：“1层，2层，3层。。。。”
                for(String key : map_floor.keySet())
                {
                    floor_old[k]=Integer.parseInt(key);  // 给数字楼层数组赋值
                    k++;
                }
                Arrays.sort(floor_old);   //对其排序

                str_floor="所有;";
                for(k=0;k<floor_old.length;k++)
                {
                    str_floor+=floor_old[k]+"层;";    //给楼栋数组赋值
                }
                map_floor_dorm.put(area[0] + building[y], str_floor);  //宿舍园区楼栋与楼层的对应关系
                map_floor.clear();
                for(int i=0;i<len_stu;i++)
                {
                    Map<String,String> map=datalist_stu.get(i);
                    if(map.get("Area").equals(area[0]))   //默认显示第一行，所以找园区为第一行的楼栋单元
                    {
                        if((map.get("Building")+map.get("Unit")).equals(building[y]))
                            map_floor.put(map.get("Floor"), "");
                    }
                }
                floor_old=new int[map_floor.size()];  //楼号
                k=0;
                for(String key : map_floor.keySet())
                {
                    floor_old[k]=Integer.parseInt(key);  // 给数字楼层数组赋值
                    k++;
                }
                Arrays.sort(floor_old);   //对其排序

                str_floor="";
                for(k=0;k<floor_old.length;k++)
                {
                    str_floor+=floor_old[k]+"层;";    //给楼栋数组赋值
                }
                map_floor_stu.put(area[0] + building[y], str_floor);  //学生园区楼栋与楼层的对应关系
            }


            floor = map_floor_dorm.get(area[0] + building[0]).split(";");
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
            //先选出对应的楼栋单元
            for(int x=0;x<area.length;x++){
                map_building1.clear();
                for(int i=0;i<len;i++)
                {
                    Map<String,String> map=datalist_room.get(i);
                    if(map.get("Area").equals(area[x]))   //默认显示第一行，所以找园区为第一行的楼栋单元
                    {
                        map_building1.put(map.get("Building")+map.get("Unit"),"");//有单元号，就存楼栋和单元
                    }
                }

                String str_building="";
                for(String key : map_building1.keySet())
                {
                    str_building+=key+";";    //给楼栋数组赋值
                }
                map_building.put(area[x],str_building);
            }
            building = map_building.get(area[0]).split(";");   //当前园区的所有楼栋
            //选出每个楼栋对应的楼层，分为学生和宿舍两种楼层
            String str_floor="";
            for(int x=0;x<area.length;x++){
                String[] temp_building=map_building.get(area[x]).split(";");  //该园区的所有楼栋
                for(int y=0;y<temp_building.length;y++){
                    map_floor.clear();
                    for(int i=0;i<len;i++)
                    {
                        Map<String,String> map=datalist_room.get(i);
                        if(map.get("Area").equals(area[x]))   //默认显示第一行，所以找园区为第一行的楼栋单元
                        {
                            if((map.get("Building")+map.get("Unit")).equals(temp_building[y]))
                                map_floor.put(map.get("Floor"), "");
                        }
                    }
                    int[] floor_old=new int[map_floor.size()];  //楼号
                    int k=0;
                    for(String key : map_floor.keySet())
                    {
                        floor_old[k]=Integer.parseInt(key);  // 给数字楼层数组赋值
                        k++;
                    }
                    Arrays.sort(floor_old);   //对其排序

                    str_floor="所有;";
                    for(k=0;k<floor_old.length;k++)
                    {
                        str_floor+=floor_old[k]+"层;";    //给楼栋数组赋值
                    }
                    map_floor_dorm.put(area[x] + temp_building[y], str_floor);  //宿舍园区楼栋与楼层的对应关系
                    map_floor.clear();
                    for(int i=0;i<len_stu;i++)
                    {
                        Map<String,String> map=datalist_stu.get(i);
                        if(map.get("Area").equals(area[x]))   //默认显示第一行，所以找园区为第一行的楼栋单元
                        {
                            if((map.get("Building")+map.get("Unit")).equals(temp_building[y]))
                                map_floor.put(map.get("Floor"), "");
                        }
                    }
                    floor_old=new int[map_floor.size()];  //楼号
                    k=0;
                    for(String key : map_floor.keySet())
                    {
                        floor_old[k]=Integer.parseInt(key);  // 给数字楼层数组赋值
                        k++;
                    }
                    Arrays.sort(floor_old);   //对其排序

                    str_floor="";
                    for(k=0;k<floor_old.length;k++)
                    {
                        str_floor+=floor_old[k]+"层;";    //给楼栋数组赋值
                    }
                    map_floor_stu.put(area[x] + temp_building[y], str_floor);  //学生园区楼栋与楼层的对应关系
                }
            }
            floor = map_floor_dorm.get(area[0] + building[0]).split(";");

        }
        spinner_area.setAdapter(new ArrayAdapter<String>(AllRoom_Activity.this,R.layout.spinner_item,area){
            @Override
            public View getDropDownView(int position, View convertView,ViewGroup parent) {
                View view = getLayoutInflater().inflate(R.layout.spinner_dropdown_item, parent, false);
                TextView label = (TextView) view.findViewById(R.id.text1);
                label.setText(getItem(position));
                return view;
            }
        });
        spinner_building.setAdapter(new ArrayAdapter<String>(AllRoom_Activity.this,R.layout.spinner_item,building){
            @Override
            public View getDropDownView(int position, View convertView,ViewGroup parent) {
                View view = getLayoutInflater().inflate(R.layout.spinner_dropdown_item, parent, false);
                TextView label = (TextView) view.findViewById(R.id.text1);
                label.setText(getItem(position));
                return view;
            }
        });
        spinner_floor.setAdapter(new ArrayAdapter<String>(AllRoom_Activity.this,R.layout.spinner_item,floor){
            @Override
            public View getDropDownView(int position, View convertView,ViewGroup parent) {
                View view = getLayoutInflater().inflate(R.layout.spinner_dropdown_item, parent, false);
                TextView label = (TextView) view.findViewById(R.id.text1);
                label.setText(getItem(position));
                return view;
            }
        });
    }

    /***
     * 将数据按照下拉列表选中的选项进行筛选
     * 当前层的宿舍信息存放在data_dorm中，当前层的学生信息存放在data_stu中
     * 对datalist_general进行处理，加上Icon，并将中文name与value对应好
     * 然后根据需要显示的界面，为recyclerview设置相应适配器
     */
    private void ViewInit() {
        final String Area = spinner_area.getSelectedItem().toString();   //园区
        final String Building = spinner_building.getSelectedItem().toString();   //楼栋单元
        final String Floor = spinner_floor.getSelectedItem().toString().replace("层", "");  //楼层
        if (IsShowing.equals("宿舍")) {//如果是宿舍页面
            data_dorm.clear();
            int len = datalist_roomdetail.size();
            if (Floor.equals("所有")) {
                for (int k = 0; k < len; k++) {
                    Map<String, String> map = datalist_roomdetail.get(k);
                    if (map.get("Area").equals(Area) && (map.get("Building") + map.get("Unit")).equals(Building))
                        data_dorm.add(map);
                }
            } else {
                for (int k = 0; k < len; k++) {
                    Map<String, String> map = datalist_roomdetail.get(k);
                    if (map.get("Area").equals(Area) && (map.get("Building") + map.get("Unit")).equals(Building) && map.get("Floor").equals(Floor))
                        data_dorm.add(map);
                }
            }
            recyclerview.setAdapter(adapter_dorm);
            adapter_dorm.notifyDataSetChanged();
        } else if (IsShowing.equals("学生")) {  //学生页面
            data_stu.clear();
            int len = datalist_stu.size();
            // 对楼层进行筛选，选出符合条件的宿舍房间号，存入set
            for (int k = 0; k < len; k++) {
                Map<String, String> map = datalist_stu.get(k);
                if (map.get("Area").equals(Area) && (map.get("Building") + map.get("Unit")).equals(Building) && map.get("Floor").equals(Floor))
                    data_stu.add(map);
            }
            recyclerview.setAdapter(adapter_stu);
            adapter_stu.notifyDataSetChanged();
        } else {   //综合页面
            if (Area.equals("所有") || Building.equals("所有") || Floor.equals("所有")) {
                String title = "";
                if (Area.equals("所有") && Floor.equals("所有"))
                    title = "所有园区";
                else if (Area.equals("所有"))
                    title = "所有园区" + Floor;
                else if (Building.equals("所有") && Floor.equals("所有"))
                    title = Area;
                else if (Building.equals("所有"))
                    title = Area + "-" + Floor;
                else
                    title = Area + "-" + Building;
                data_general.clear();
                if (datalist_general.size() > 0 && !Floor.equals("热水"))    //热水不显示电费统计
                {
                    Map<String, String> map_general = datalist_general.get(0); //存放三相电表当前状态信息
                    Map<String, String> map1 = new HashMap<String, String>();
                    map1.put("Name", "本日预存电费");      //名称
                    map1.put("Value", map_general.get("PreSaveDay") + "元");  //值
                    map1.put("Type", "PreSave");   //类型，跳转到历史数据页面时传递，请求网络数据时用
                    map1.put("Title", title + "电费统计");
                    map1.put("Icon", String.valueOf(R.drawable.ic_balance));
                    data_general.add(map1);
                    Map<String, String> map2 = new HashMap<String, String>();
                    map2.put("Name", "本月预存电费");      //名称
                    map2.put("Value", map_general.get("PreSaveMonth") + "元");  //值
                    map2.put("Type", "PreSave");   //类型，跳转到历史数据页面时传递，请求网络数据时用
                    map2.put("Title", "-1");
                    map2.put("Icon", String.valueOf(R.drawable.ic_balance));
                    data_general.add(map2);
                    Map<String, String> map3 = new HashMap<String, String>();
                    map3.put("Name", "本年预存电费");      //名称
                    map3.put("Value", map_general.get("PreSaveYear") + "元");  //值
                    map3.put("Type", "PreSave");   //类型，跳转到历史数据页面时传递，请求网络数据时用
                    map3.put("Title", "-1");
                    map3.put("Icon", String.valueOf(R.drawable.ic_balance));
                    data_general.add(map3);
                    Map<String, String> map4 = new HashMap<String, String>();
                    map4.put("Name", "预存电费总计");      //名称
                    map4.put("Value", map_general.get("PreSaveSum") + "元");  //值
                    map4.put("Type", "PreSave");   //类型，跳转到历史数据页面时传递，请求网络数据时用
                    map4.put("Title", "-1");
                    map4.put("Icon", String.valueOf(R.drawable.ic_balance));
                    data_general.add(map4);
                    Map<String, String> map5 = new HashMap<String, String>();
                    map5.put("Name", "本日消费电费");      //名称
                    map5.put("Value", map_general.get("ChargeDay") + "元");  //值
                    map5.put("Type", "Charge");   //类型，跳转到历史数据页面时传递，请求网络数据时用
                    map5.put("Title", "-1");
                    map5.put("Icon", String.valueOf(R.drawable.ic_balance));
                    data_general.add(map5);
                    Map<String, String> map6 = new HashMap<String, String>();
                    map6.put("Name", "本月消费电费");      //名称
                    map6.put("Value", map_general.get("ChargeMonth") + "元");  //值
                    map6.put("Type", "Charge");   //类型，跳转到历史数据页面时传递，请求网络数据时用
                    map6.put("Title", "-1");
                    map6.put("Icon", String.valueOf(R.drawable.ic_balance));
                    data_general.add(map6);
                    Map<String, String> map7 = new HashMap<String, String>();
                    map7.put("Name", "本年消费电费");      //名称
                    map7.put("Value", map_general.get("ChargeYear") + "元");  //值
                    map7.put("Type", "Charge");   //类型，跳转到历史数据页面时传递，请求网络数据时用
                    map7.put("Title", "-1");
                    map7.put("Icon", String.valueOf(R.drawable.ic_balance));
                    data_general.add(map7);
                    Map<String, String> map8 = new HashMap<String, String>();
                    map8.put("Name", "消费电费总计");      //名称
                    map8.put("Value", map_general.get("ChargeSum") + "元");  //值
                    map8.put("Type", "Charge");   //类型，跳转到历史数据页面时传递，请求网络数据时用
                    map8.put("Title", "-1");
                    map8.put("Icon", String.valueOf(R.drawable.ic_balance));
                    data_general.add(map8);
                }
            } else {
                data_general.clear();
                if (datalist_general.size() > 0)   //用电量信息
                {
                    Map<String, String> map_general = datalist_general.get(0); //存放三相电表当前状态信息
                    Map<String, String> map2 = new HashMap<String, String>();
                    map2.put("Name", "本日已用电量");      //名称
                    map2.put("Value", map_general.get("ElecDay") + "度");  //值
                    map2.put("Type", "EletricityValue");   //类型，跳转到历史数据页面时传递，请求网络数据时用
                    map2.put("Title", Area + "-" + Building + "-" + Floor+"用电情况");
                    map2.put("Icon", String.valueOf(R.drawable.ic_elec));
                    data_general.add(map2);
                    Map<String, String> map3 = new HashMap<String, String>();
                    map3.put("Name", "本月已用电量");      //名称
                    map3.put("Value", map_general.get("ElecMonth") + "度");  //值
                    map3.put("Type", "EletricityValue");   //类型，跳转到历史数据页面时传递，请求网络数据时用
                    map3.put("Title", "-1");
                    map3.put("Icon", String.valueOf(R.drawable.ic_elec));
                    data_general.add(map3);
                    Map<String, String> map1 = new HashMap<String, String>();
                    map1.put("Name", "总用电量");      //名称
                    map1.put("Value", map_general.get("Electricity") + "度");  //值
                    map1.put("Type", "EletricityValue");   //类型，跳转到历史数据页面时传递，请求网络数据时用
                    map1.put("Title", "-1");
                    map1.put("Icon", String.valueOf(R.drawable.ic_elec));
                    data_general.add(map1);
                }

                if (datalist_general.size() > 0 && Floor.equals("照明"))   //补贴使用情况
                {
                    Map<String, String> map_general = datalist_general.get(0); //存放三相电表当前状态信息
                    Map<String, String> map4 = new HashMap<String, String>();
                    map4.put("Name", "本日已用补贴");      //名称
                    map4.put("Value", map_general.get("SubsidyDay") + "度");  //值
                    map4.put("Type", "EletricityValue");   //类型，跳转到历史数据页面时传递，请求网络数据时用
                    map4.put("Title", Area + "-" + Building + "-" + Floor + "补贴使用情况");
                    map4.put("Icon", String.valueOf(R.drawable.ic_subsidy));
                    data_general.add(map4);
                    Map<String, String> map5 = new HashMap<String, String>();
                    map5.put("Name", "本月已用补贴");      //名称
                    map5.put("Value", map_general.get("SubsidyMonth") + "度");  //值
                    map5.put("Type", "EletricityValue");   //类型，跳转到历史数据页面时传递，请求网络数据时用
                    map5.put("Title", "-1");
                    map5.put("Icon", String.valueOf(R.drawable.ic_subsidy));
                    data_general.add(map5);
                    Map<String, String> map6 = new HashMap<String, String>();
                    map6.put("Name", "总已用补贴");      //名称
                    map6.put("Value", map_general.get("SubsidySum") + "度");  //值
                    map6.put("Type", "EletricityValue");   //类型，跳转到历史数据页面时传递，请求网络数据时用
                    map6.put("Title", "-1");
                    map6.put("Icon", String.valueOf(R.drawable.ic_subsidy));
                    data_general.add(map6);

                }

                if (datalist_general.size() > 0 && !Floor.equals("热水"))    //电费统计，热水不显示电费统计
                {
                    Map<String, String> map_general = datalist_general.get(0); //存放三相电表当前状态信息
                    Map<String, String> map1 = new HashMap<String, String>();
                    map1.put("Name", "本日预存电费");      //名称
                    map1.put("Value", map_general.get("PreSaveDay") + "元");  //值
                    map1.put("Type", "PreSave");   //类型，跳转到历史数据页面时传递，请求网络数据时用
                    map1.put("Title", Area + "-" + Building + "-" + Floor + "电费统计");
                    map1.put("Icon", String.valueOf(R.drawable.ic_balance));
                    data_general.add(map1);
                    Map<String, String> map2 = new HashMap<String, String>();
                    map2.put("Name", "本月预存电费");      //名称
                    map2.put("Value", map_general.get("PreSaveMonth") + "元");  //值
                    map2.put("Type", "PreSave");   //类型，跳转到历史数据页面时传递，请求网络数据时用
                    map2.put("Title", "-1");
                    map2.put("Icon", String.valueOf(R.drawable.ic_balance));
                    data_general.add(map2);
                    Map<String, String> map3 = new HashMap<String, String>();
                    map3.put("Name", "本年预存电费");      //名称
                    map3.put("Value", map_general.get("PreSaveYear") + "元");  //值
                    map3.put("Type", "PreSave");   //类型，跳转到历史数据页面时传递，请求网络数据时用
                    map3.put("Title", "-1");
                    map3.put("Icon", String.valueOf(R.drawable.ic_balance));
                    data_general.add(map3);
                    Map<String, String> map4 = new HashMap<String, String>();
                    map4.put("Name", "预存电费总计");      //名称
                    map4.put("Value", map_general.get("PreSaveSum") + "元");  //值
                    map4.put("Type", "PreSave");   //类型，跳转到历史数据页面时传递，请求网络数据时用
                    map4.put("Title", "-1");
                    map4.put("Icon", String.valueOf(R.drawable.ic_balance));
                    data_general.add(map4);
                    Map<String, String> map5 = new HashMap<String, String>();
                    map5.put("Name", "本日消费电费");      //名称
                    map5.put("Value", map_general.get("ChargeDay") + "元");  //值
                    map5.put("Type", "Charge");   //类型，跳转到历史数据页面时传递，请求网络数据时用
                    map5.put("Title", "-1");
                    map5.put("Icon", String.valueOf(R.drawable.ic_balance));
                    data_general.add(map5);
                    Map<String, String> map6 = new HashMap<String, String>();
                    map6.put("Name", "本月消费电费");      //名称
                    map6.put("Value", map_general.get("ChargeMonth") + "元");  //值
                    map6.put("Type", "Charge");   //类型，跳转到历史数据页面时传递，请求网络数据时用
                    map6.put("Title", "-1");
                    map6.put("Icon", String.valueOf(R.drawable.ic_balance));
                    data_general.add(map6);
                    Map<String, String> map7 = new HashMap<String, String>();
                    map7.put("Name", "本年消费电费");      //名称
                    map7.put("Value", map_general.get("ChargeYear") + "元");  //值
                    map7.put("Type", "Charge");   //类型，跳转到历史数据页面时传递，请求网络数据时用
                    map7.put("Title", "-1");
                    map7.put("Icon", String.valueOf(R.drawable.ic_balance));
                    data_general.add(map7);
                    Map<String, String> map8 = new HashMap<String, String>();
                    map8.put("Name", "消费电费总计");      //名称
                    map8.put("Value", map_general.get("ChargeSum") + "元");  //值
                    map8.put("Type", "Charge");   //类型，跳转到历史数据页面时传递，请求网络数据时用
                    map8.put("Title", "-1");
                    map8.put("Icon", String.valueOf(R.drawable.ic_balance));
                    data_general.add(map8);
                }
            }
            recyclerview.setAdapter(adapter_general);
            adapter_general.notifyDataSetChanged();
        }
    }

    /**宿舍页面
     * RecyclerView适配器
     **自定义Recyclerview的适配器,主要的执行顺序：getItemViewType==>onCreateViewHolder==>onBindViewHolder
     */
    class Adapter_Dorm extends RecyclerView.Adapter{
        /****
         * 渲染具体的布局
         * @param parent   父容器
         * @param viewType    布局类别，多种布局的情况定义多个viewholder
         * @return
         */
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            RecyclerView.ViewHolder holder= new viewHolder1(LayoutInflater.from(
                    AllRoom_Activity.this).inflate(R.layout.listall_dorm, parent,false));

            return holder;
        }

        /***
         * 绑定数据
         * @param holder   绑定哪个holder，用if(holder instanceof mViewHolder1)来判断类型，再绑定数据
         * @param position
         */
        @Override
        public void onBindViewHolder( RecyclerView.ViewHolder holder,final int position)
        {

                final viewHolder1 viewholder = (viewHolder1) holder;
                final Map<String,String> map=data_dorm.get(position);
                final String RoomType=map.get("RoomType");   //房间类型
                final String RoomNum=map.get("RoomNum");  //房间号
                final String RoomID=map.get("RoomID");  //房间ID
                final String Floor=map.get("Floor");  //楼层
                final String RoomInfo=map.get("Area")+"-"+map.get("Building")+"-"+(map.get("Unit").equals("")?(map.get("RoomNum")):(map.get("Unit")+"-"+map.get("RoomNum")));    //宿舍信息
                viewholder.room.setText(RoomNum);
                if(position==0){   //如果是第一行，则显示房间小标题
                    viewholder.title.setVisibility(View.VISIBLE);
                    viewholder.title.setText(map.get("Area")+"-"+map.get("Building")+"-"+map.get("Floor")+"层");
                }    //如果是新房间，也显示房间小标题
                else if(!data_dorm.get(position-1).get("Floor").equals(Floor)){
                    viewholder.title.setVisibility(View.VISIBLE);
                    viewholder.title.setText(map.get("Area")+"-"+map.get("Building")+"-"+map.get("Floor")+"层");
                }
                else{
                    viewholder.title.setVisibility(View.GONE);
                }
                if(RoomType.equals("0")||RoomType.equals("1")) {   //如果是学生宿舍，或者库房等，就显示开关状态和账户状态
                    viewholder.zm_status.setText("照明:"+status[Integer.valueOf(map.get("zmStatus")==null?"1":(map.get("zmStatus")))]);  //电表状态
                    if ((map.get("zmState")==null?"null":(map.get("zmState"))).equals("1"))  //照明开关
                        viewholder.zm_state.setState(true);
                    else
                        viewholder.zm_state.setState(false);
                    viewholder.layout_state.setVisibility(View.VISIBLE);
                    viewholder.layout_status.setVisibility(View.VISIBLE);
                    if(Role.equals("3")){
                        viewholder.zm_state.setEnabled(false);    //如果角色号是3，则为系统观察员，不允许控制

                    }
                }
                else {   //卫生间或者活动室等
                    viewholder.layout_state.setVisibility(View.GONE);
                    viewholder.layout_status.setVisibility(View.GONE);
                }
                final AlertDialog.Builder builder = new AlertDialog.Builder(AllRoom_Activity.this);
                //照明开关状态改变监听，提示是否发送命令
                viewholder.zm_state.setOnChangeListener(new Switch.OnSwitchChangedListener(){
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
                                viewholder.zm_state.setState(!isChecked);  //将开关恢复原状
                            }
                        });
                        builder.create().show();
                    }});
                //选项选择监听
                viewholder.layout.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {

                        if(RoomType.equals("0")||RoomType.equals("1")||RoomType.equals("2")) {
                            Intent intent = new Intent();
                            intent.putExtra("UserID", UserID);       //管理员账号
                            intent.putExtra("RoomID", RoomID);       //宿舍ID
                            intent.putExtra("RoomType", map.get("RoomType"));   //房间类型
                            intent.putExtra("Role", Role);       //角色
                            intent.putExtra("RoomInfo", RoomInfo);   //房间信息
                            intent.setClass(AllRoom_Activity.this, Dorm_Activity.class);//设置要跳转的activity
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
                            intent.setClass(AllRoom_Activity.this, ThreeData_Activity.class);//设置要跳转的activity
                            startActivity(intent);//开始跳转
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);//设置activity切换效果

                        }

                    }});

        }

        @Override
        public int getItemCount()
        {
            return data_dorm.size();
        }
        /**
         * 决定元素的布局使用哪种类型
                * @param position 数据源的下标
         * @return 一个int型标志，传递给onCreateViewHolder的第二个参数 */
        @Override
        public int getItemViewType(int position) {
            return 1;
        }

        /**
         * 自定义继承RecyclerView.ViewHolder的viewholder
         * 布局类型1对应的ViewHolder，R.layout.listmain_userinfo
         */
        class viewHolder1 extends RecyclerView.ViewHolder
        {
            TextView title;   //分区小标题
            LinearLayout layout;   //内容布局（除去小标题）
            LinearLayout layout_status;    //电表状态布局
            LinearLayout layout_state;  //开关状态布局
            TextView room;   //房间号
            ImageView image;    //房间图标
            TextView zm_status;    //电表状态
            Switch zm_state;     //开关状态
            ImageView detail;  //详细信息按钮

            public viewHolder1(View view)
            {
                super(view);
                layout = (LinearLayout) view.findViewById(R.id.layout1);
                layout_status=(LinearLayout)view.findViewById(R.id.layout_status);
                layout_state = (LinearLayout) view.findViewById(R.id.layout_state);
                title = (TextView) view.findViewById(R.id.list_header_title);
                image=(ImageView)view.findViewById(R.id.image);
                room = (TextView) view.findViewById(R.id.room);
                zm_status = (TextView) view.findViewById(R.id.zm_status);
                zm_state = (Switch) view.findViewById(R.id.zm_state);
                detail = (ImageView) view.findViewById(R.id.detail);
            }
        }

    }

    /**学生页面
     * RecyclerView适配器
     **自定义Recyclerview的适配器,主要的执行顺序：getItemViewType==>onCreateViewHolder==>onBindViewHolder
     */
    class Adapter_Stu extends RecyclerView.Adapter{
        /****
         * 渲染具体的布局
         * @param parent   父容器
         * @param viewType    布局类别，多种布局的情况定义多个viewholder
         * @return
         */
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            RecyclerView.ViewHolder holder= new viewHolder1(LayoutInflater.from(
                    AllRoom_Activity.this).inflate(R.layout.listall_stu, parent,false));

            return holder;
        }

        /***
         * 绑定数据
         * @param holder   绑定哪个holder，用if(holder instanceof mViewHolder1)来判断类型，再绑定数据
         * @param position
         */
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder,final int position)
        {

            viewHolder1 viewholder = (viewHolder1) holder;
            final Map<String, String> map = data_stu.get(position);
            final String Area=map.get("Area");
            final String Building = map.get("Building")+map.get("Unit");
            final String RoomNum = map.get("RoomNum");
            viewholder.name.setText(map.get("SName"));
            viewholder.value.setText(map.get("Faculty"));
            if(position==0){   //如果是第一行，则显示房间小标题
                viewholder.title.setVisibility(View.VISIBLE);
                viewholder.title.setText(Area+"-"+Building+"-"+RoomNum);
            }    //如果是新房间，也显示房间小标题
            else if(!data_stu.get(position-1).get("RoomNum").equals(RoomNum)){
                viewholder.title.setVisibility(View.VISIBLE);
                viewholder.title.setText(Area+"-"+Building+"-"+RoomNum);
            }
            else{
                viewholder.title.setVisibility(View.GONE);
            }

            //选项选择监听
            viewholder.layout.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {

                    Intent intent=new Intent();
                    intent.putExtra("Name", map.get("SName"));       //学生姓名
                    intent.putExtra("RoomID", map.get("RoomID"));       //宿舍ID
                    intent.putExtra("RoomInfo", Area+"-"+Building+"-"+map.get("RoomNum"));  //宿舍信息
                    intent.setClass(AllRoom_Activity.this, Stu_Activity.class);//设置要跳转的activity
                    startActivity(intent);//开始跳转
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);//设置activity切换效果

                }});
        }

        @Override
        public int getItemCount()
        {
            return data_stu.size();
        }
        /***
         * 决定元素的布局使用哪种类型

         * @param position 数据源的下标
         * @return 一个int型标志，传递给onCreateViewHolder的第二个参数 */
        @Override
        public int getItemViewType(int position) {
            return 1;
        }

        /**
         * 自定义继承RecyclerView.ViewHolder的viewholder
         * 布局类型1对应的ViewHolder，R.layout.listmain_userinfo
         */
        class viewHolder1 extends RecyclerView.ViewHolder
        {
            TextView title;   //分区小标题
            LinearLayout layout;   //内容布局（除去小标题）
            ImageView image;    //房间图标
            TextView name;    //姓名
            TextView value;     //学院
            ImageView detail;  //详细信息按钮

            public viewHolder1(View view)
            {
                super(view);
                layout = (LinearLayout) view.findViewById(R.id.layout1);
                title = (TextView) view.findViewById(R.id.list_header_title);
                image=(ImageView)view.findViewById(R.id.image);
                name = (TextView) view.findViewById(R.id.name);
                value = (TextView) view.findViewById(R.id.value);
                detail = (ImageView) view.findViewById(R.id.detail);
            }
        }

    }

    /**综合页面
     * RecyclerView适配器
     **自定义Recyclerview的适配器,主要的执行顺序：getItemViewType==>onCreateViewHolder==>onBindViewHolder
     */
    class Adapter_General extends RecyclerView.Adapter{
        /****
         * 渲染具体的布局
         * @param parent   父容器
         * @param viewType    布局类别，多种布局的情况定义多个viewholder
         * @return
         */
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            RecyclerView.ViewHolder holder= new viewHolder1(LayoutInflater.from(
                    AllRoom_Activity.this).inflate(R.layout.listall_general, parent,false));

            return holder;
        }

        /***
         * 绑定数据
         * @param holder   绑定哪个holder，用if(holder instanceof mViewHolder1)来判断类型，再绑定数据
         * @param position
         */
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder,final int position)
        {

            viewHolder1 viewholder = (viewHolder1) holder;
            Map<String, String> map = data_general.get(position);
            bmp = BitmapFactory.decodeResource(getResources(),Integer.valueOf(map.get("Icon")));
            viewholder.image.setImageBitmap(bmp);
            final String Name = map.get("Name");
            viewholder.name.setText(Name);   //参数名
            viewholder.value.setText(map.get("Value"));   //参数值

            if(!map.get("Title").equals("-1")){
                viewholder.title.setText(map.get("Title"));   //设置标题
                viewholder.title.setVisibility(View.VISIBLE);  //显示标题
            }
            else
                viewholder.title.setVisibility(View.GONE);


        }

        @Override
        public int getItemCount()
        {
            return data_general.size();
        }
        /**
         * 决定元素的布局使用哪种类型
         *在本activity中，布局1使用R.layout.listmain_userinfo，

         *     布局3使用R.layout.listmain_dorm

         * @param position 数据源的下标
         * @return 一个int型标志，传递给onCreateViewHolder的第二个参数 */
        @Override
        public int getItemViewType(int position) {
            return 1;
        }

        /**
         * 自定义继承RecyclerView.ViewHolder的viewholder
         * 布局类型1对应的ViewHolder，R.layout.listmain_userinfo
         */
        class viewHolder1 extends RecyclerView.ViewHolder
        {
            TextView title;   //分区小标题
            LinearLayout layout;   //内容布局（除去小标题）
            ImageView image;    //图标
            TextView name;    //参数名称
            TextView value;     //参数值
            ImageView detail;  //详细信息按钮

            public viewHolder1(View view)
            {
                super(view);
                layout = (LinearLayout) view.findViewById(R.id.layout1);
                title = (TextView) view.findViewById(R.id.list_header_title);
                image=(ImageView)view.findViewById(R.id.image);
                name = (TextView) view.findViewById(R.id.name);
                value = (TextView) view.findViewById(R.id.value);
                detail = (ImageView) view.findViewById(R.id.detail);
            }
        }


    }
}
