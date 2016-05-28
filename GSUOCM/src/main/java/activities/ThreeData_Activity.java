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
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.suntrans.whu.R;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Adapter.RecyclerViewDivider;
import WebServices.jiexi;
import WebServices.soap;
import convert.Converts;
import views.AppManager;
import views.Switch;
import views.ZoomOutPageTransformer;

/**
 * Created by 石奋斗 on 2016/5/19.
 */
public class ThreeData_Activity extends AppCompatActivity {
    private Bitmap bmp=null;   //定义bitmap变量存放图标
    private SwipeRefreshLayout refreshlayout;  //下拉控件
    private RecyclerView recyclerview;    //列表
    private long time;  //用来存储刷新时的时间。
    private LinearLayout back;  //返回键
    private TextView tx_title;  //标题
    private LinearLayout layout_loading,layout_failure;   //加载中和正在加载
    private String UserID=""; // 管理员账号
    private String Role="";    //角色号
    private String RoomType="";   //房间类型
    private String RoomID="";  //房间ID
    private String RoomInfo="";   //房间详细信息
    private String RoomNum = "";    //房间号
    private String AccountType="照明";   //账户类型，默认为照明
    private mAdapter adapter;   //列表适配器
    private ArrayList<Map<String,String>> data=new ArrayList<Map<String,String>>();  //三相电表用电信息
    private Handler handler1=new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==1)         //代表请求数据成功
            {

                adapter.notifyDataSetChanged();   //刷新列表显示
                layout_loading.setVisibility(View.GONE);
                layout_failure.setVisibility(View.GONE);

            }
            else if(msg.what==0)         //代表请求数据失败
            {
                layout_loading.setVisibility(View.GONE);
                layout_failure.setVisibility(View.VISIBLE);
                Log.i("Order", msg.obj.toString());
                //Toast.makeText(getActivity(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
            }
            else if(msg.what==2)         //代表开始请求
            {
                layout_loading.setVisibility(View.VISIBLE);
                layout_failure.setVisibility(View.GONE);
            }
        }};
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.threedata);      //设置activity布局文件
        AppManager.getAppManager().addActivity(this);
        Intent intent = getIntent();
        UserID = intent.getStringExtra("UserID");  //用户id
        RoomID = intent.getStringExtra("RoomID");    //房间ID
        Role = intent.getStringExtra("Role");   //用户角色号
        RoomType = intent.getStringExtra("RoomType");  //房间类型:0学生宿舍
        RoomInfo = intent.getStringExtra("RoomInfo"); ///房间详情
        tx_title = (TextView) findViewById(R.id.tx_title);   //标题
        back = (LinearLayout) findViewById(R.id.layout_back);     //返回键
        layout_failure = (LinearLayout) findViewById(R.id.layout_failure);   //加载失败
        layout_loading = (LinearLayout) findViewById(R.id.layout_loading);  //正在加载
        refreshlayout=(SwipeRefreshLayout)findViewById(R.id.refreshlayout);   //下拉控件
        recyclerview = (RecyclerView) findViewById(R.id.recyclerview);   //列表控件
        String[] str = RoomInfo.split("-");
        RoomNum=str[str.length - 1];
        tx_title.setText(RoomNum);   //设置标题，房间号
        back.setOnClickListener(new View.OnClickListener(){   //返回键监听
            @Override
            public void onClick(View v) {
                finish();
            }});
        layout_failure.setOnClickListener(new View.OnClickListener(){    //加载失败点击监听，重新访问网络
            @Override
            public void onClick(View v) {
                layout_loading.setVisibility(View.VISIBLE);
                layout_failure.setVisibility(View.GONE);
                new MainThread().start();    //开始新的刷新线程
            }});
        new MainThread().start();
        //		    设置通知栏半透明
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Converts.setTranslucentStatus(ThreeData_Activity.this,true);
        }
        /****配置下拉控件****/
        refreshlayout.setSize(SwipeRefreshLayout.LARGE);  //设置大小
        refreshlayout.setColorSchemeResources(R.color.bg_action,R.color.green,R.color.yellow);   //设置滚动条颜色
        refreshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new GetDataTask().execute();   //执行任务
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
        adapter=new mAdapter();
        recyclerview.setAdapter(adapter);
    }

    //自定义刷新线程，访问网络获取数据
    public class MainThread extends Thread{
        @Override
        public void run()
        {
            SoapObject result;
            try
            {
                result=soap.Inquiry_Threephase_R(RoomID, AccountType);
                data=jiexi.inquiry_threephase_r(result);    //获取三相电表用电状态
                Map<String,String> map=data.get(0);
                data.clear();  //清除原有数据

                Map<String, String> map9 = new HashMap<>();
                map9.put("Name","宿舍详情");
                map9.put("Value",RoomInfo);
                map9.put("Type","3");
                map9.put("Title", "宿舍详情");
                data.add(map9);   //这个是宿舍详情

                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("Name", "A相电压");
                map1.put("Value", map.get("VolA")==null?"null":(map.get("VolA")+ "V") );
                map1.put("Type","1");  //布局类型，1
                map1.put("Title","电量信息");    //小标题，如果不显示小标题，则此项为-1
                map1.put("Icon",String.valueOf(R.drawable.ic_voltage));   //图标
                data.add(map1);
                Map<String, String> map2 = new HashMap<String, String>();
                map2.put("Name", "B相电压");
                map2.put("Value", map.get("VolB")==null?"null":(  map.get("VolB")+ "V"));
                map2.put("Type","1");  //布局类型，1
                map2.put("Title", "-1");
                map2.put("Icon", String.valueOf(R.drawable.ic_voltage));
                data.add(map2);

                Map<String,String> map5=new HashMap<String,String>();
                map5.put("Name", "C相电压");
                map5.put("Value",map.get("VolC")==null?"null":(map.get("VolC")+"V"));
                map5.put("Type","2");
                map5.put("Title", "-1");
                map5.put("Icon", String.valueOf(R.drawable.ic_voltage));
                data.add(map5);
                Map<String,String> map6=new HashMap<String,String>();
                map6.put("Name", "A相电流");
                map6.put("Value",map.get("IA")==null?"null":(map.get("IA")+"A"));
                map6.put("Type", "1");
                map6.put("Title", "-1");
                map6.put("Icon", String.valueOf(R.drawable.ic_current));
                data.add(map6);
                Map<String,String> map7=new HashMap<String,String>();
                map7.put("Name", "B相电流");
                map7.put("Value",map.get("IB")==null?"null":(map.get("IB")+"A"));
                map7.put("Type","1");
                map7.put("Title", "-1");
                map7.put("Icon", String.valueOf(R.drawable.ic_current));
                data.add(map7);
                Map<String,String> map8=new HashMap<String,String>();
                map8.put("Name", "C相电流");
                map8.put("Value",map.get("IC")==null?"null":(map.get("IC")+"A"));
                map8.put("Type","1");
                map8.put("Title", "-1");
                map8.put("Icon", String.valueOf(R.drawable.ic_current));
                data.add(map8);
                Map<String,String> map10=new HashMap<String,String>();
                map10.put("Name", "有功功率");
                map10.put("Value",map.get("ActivePower")==null?"null":(map.get("ActivePower")+"kW"));
                map10.put("Type","1");
                map10.put("Title", "-1");
                map10.put("Icon", String.valueOf(R.drawable.ic_power));
                data.add(map10);
                Map<String,String> map11=new HashMap<String,String>();
                map11.put("Name", "无功功率");
                map11.put("Value",map.get("ReactivePower")==null?"null":(map.get("ReactivePower")+"kW"));
                map11.put("Type","1");
                map11.put("Title", "-1");
                map11.put("Icon", String.valueOf(R.drawable.ic_power));
                data.add(map11);

                Map<String,String> map20=new HashMap<String,String>();
                map20.put("Name", "功率因数");
                map20.put("Value",map.get("PowerRateC")==null?"null":(map.get("PowerRateC")+" "));
                map20.put("Type","1");
                map20.put("Title", "-1");
                map20.put("Icon", String.valueOf(R.drawable.ic_powerrate));
                data.add(map20);
                Map<String,String> map12=new HashMap<String,String>();
                map12.put("Name", "本日已用电量");
                map12.put("Value",map.get("ElecDay")==null?"null":(map.get("ElecDay")+"度"));
                map12.put("Type","1");
                map12.put("Title", "-1");
                map12.put("Icon", String.valueOf(R.drawable.ic_elec));
                data.add(map12);
                Map<String,String> map3=new HashMap<String,String>();
                map3.put("Name", "本月已用电量");
                map3.put("Value",map.get("ElecMonth")==null?"null":(map.get("ElecMonth")+"度"));
                map3.put("Type","1");
                map3.put("Title", "-1");
                map3.put("Icon", String.valueOf(R.drawable.ic_elec));
                data.add(map3);
                Map<String,String> map4=new HashMap<String,String>();
                map4.put("Name", "总用电量");
                map4.put("Value",map.get("Eletricity")==null?"null":(map.get("Eletricity")+"度"));
                map4.put("Type","1");
                map4.put("Title", "-1");
                map4.put("Icon", String.valueOf(R.drawable.ic_elec));
                data.add(map4); //这些是电量信息

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

    ///下拉刷新处理的函数。
    private class GetDataTask extends AsyncTask<Void, Void, String> {
        // 后台处理部分
        @Override
        protected String doInBackground(Void... params) {
            // Simulates a background job.
            SoapObject result;
            String str;
            try
            {
                try {
                    Thread.sleep(1200);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                result=soap.Inquiry_Threephase_R(RoomID, AccountType);
                data=jiexi.inquiry_threephase_r(result);    //获取三相电表用电状态
                Map<String,String> map=data.get(0);
                data.clear();  //清除原有数据

                Map<String, String> map9 = new HashMap<>();
                map9.put("Name","宿舍详情");
                map9.put("Value",RoomInfo);
                map9.put("Type","3");
                map9.put("Title", "宿舍详情");
                data.add(map9);   //这个是宿舍详情

                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("Name", "A相电压");
                map1.put("Value", map.get("VolA")==null?"null":(map.get("VolA")+ "V") );
                map1.put("Type","1");  //布局类型，1
                map1.put("Title","电量信息");    //小标题，如果不显示小标题，则此项为-1
                map1.put("Icon",String.valueOf(R.drawable.ic_voltage));   //图标
                data.add(map1);
                Map<String, String> map2 = new HashMap<String, String>();
                map2.put("Name", "B相电压");
                map2.put("Value", map.get("VolB")==null?"null":(  map.get("VolB")+ "V"));
                map2.put("Type","1");  //布局类型，1
                map2.put("Title", "-1");
                map2.put("Icon", String.valueOf(R.drawable.ic_voltage));
                data.add(map2);

                Map<String,String> map5=new HashMap<String,String>();
                map5.put("Name", "C相电压");
                map5.put("Value",map.get("VolC")==null?"null":(map.get("VolC")+"V"));
                map5.put("Type","2");
                map5.put("Title", "-1");
                map5.put("Icon", String.valueOf(R.drawable.ic_voltage));
                data.add(map5);
                Map<String,String> map6=new HashMap<String,String>();
                map6.put("Name", "A相电流");
                map6.put("Value",map.get("IA")==null?"null":(map.get("IA")+"A"));
                map6.put("Type", "1");
                map6.put("Title", "-1");
                map6.put("Icon", String.valueOf(R.drawable.ic_current));
                data.add(map6);
                Map<String,String> map7=new HashMap<String,String>();
                map7.put("Name", "B相电流");
                map7.put("Value",map.get("IB")==null?"null":(map.get("IB")+"A"));
                map7.put("Type","1");
                map7.put("Title", "-1");
                map7.put("Icon", String.valueOf(R.drawable.ic_current));
                data.add(map7);
                Map<String,String> map8=new HashMap<String,String>();
                map8.put("Name", "C相电流");
                map8.put("Value",map.get("IC")==null?"null":(map.get("IC")+"A"));
                map8.put("Type","1");
                map8.put("Title", "-1");
                map8.put("Icon", String.valueOf(R.drawable.ic_current));
                data.add(map8);
                Map<String,String> map10=new HashMap<String,String>();
                map10.put("Name", "有功功率");
                map10.put("Value",map.get("ActivePower")==null?"null":(map.get("ActivePower")+"kW"));
                map10.put("Type","1");
                map10.put("Title", "-1");
                map10.put("Icon", String.valueOf(R.drawable.ic_power));
                data.add(map10);
                Map<String,String> map11=new HashMap<String,String>();
                map11.put("Name", "无功功率");
                map11.put("Value",map.get("ReactivePower")==null?"null":(map.get("ReactivePower")+"kW"));
                map11.put("Type","1");
                map11.put("Title", "-1");
                map11.put("Icon", String.valueOf(R.drawable.ic_power));
                data.add(map11);

                Map<String,String> map20=new HashMap<String,String>();
                map20.put("Name", "功率因数");
                map20.put("Value",map.get("PowerRateC")==null?"null":(map.get("PowerRateC")+" "));
                map20.put("Type","1");
                map20.put("Title", "-1");
                map20.put("Icon", String.valueOf(R.drawable.ic_powerrate));
                data.add(map20);
                Map<String,String> map12=new HashMap<String,String>();
                map12.put("Name", "本日已用电量");
                map12.put("Value",map.get("ElecDay")==null?"null":(map.get("ElecDay")+"度"));
                map12.put("Type","1");
                map12.put("Title", "-1");
                map12.put("Icon", String.valueOf(R.drawable.ic_elec));
                data.add(map12);
                Map<String,String> map3=new HashMap<String,String>();
                map3.put("Name", "本月已用电量");
                map3.put("Value",map.get("ElecMonth")==null?"null":(map.get("ElecMonth")+"度"));
                map3.put("Type","1");
                map3.put("Title", "-1");
                map3.put("Icon", String.valueOf(R.drawable.ic_elec));
                data.add(map3);
                Map<String,String> map4=new HashMap<String,String>();
                map4.put("Name", "总用电量");
                map4.put("Value",map.get("Eletricity")==null?"null":(map.get("Eletricity")+"度"));
                map4.put("Type","1");
                map4.put("Title", "-1");
                map4.put("Icon", String.valueOf(R.drawable.ic_elec));
                data.add(map4); //这些是电量信息
                str="1";   //表示请求成功

            }
            catch(Exception e)
            {
                try {
                    Thread.sleep(1200);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                str="0";   //表示请求失败
            }
            return str;
        }

        //这里是对刷新的响应，可以利用addFirst（）和addLast()函数将新加的内容加到LISTView中
        //根据AsyncTask的原理，onPostExecute里的result的值就是doInBackground()的返回值
        @Override
        protected void onPostExecute(String result) {
            refreshlayout.setRefreshing(false);   //结束加载动作
            if(result.equals("1"))  //请求成功
            {
                adapter.notifyDataSetChanged();  //请求成功则刷新列表显示
            }
            else
            {
                Toast.makeText(getApplicationContext(),"加载失败！",Toast.LENGTH_SHORT).show();
            }

            super.onPostExecute(result);//这句是必有的，AsyncTask规定的格式
        }
    }
    /**
     * RecyclerView适配器
     **自定义Recyclerview的适配器,主要的执行顺序：getItemViewType==>onCreateViewHolder==>onBindViewHolder
     */
    class mAdapter extends RecyclerView.Adapter{
        /****
         * 渲染具体的布局，根据viewType选择使用哪种布局
         * @param parent   父容器
         * @param viewType    布局类别，多种布局的情况定义多个viewholder
         * @return
         */
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            RecyclerView.ViewHolder holder= new viewHolder1(LayoutInflater.from(
                   ThreeData_Activity.this).inflate(R.layout.listmain_userinfo, parent,false));
            switch(viewType){
                case 1:
                    holder= new viewHolder1(LayoutInflater.from(
                            ThreeData_Activity.this).inflate(R.layout.listmain_userinfo, parent,false));
                    break;
                case 3:
                    holder= new viewHolder3(LayoutInflater.from(
                            ThreeData_Activity.this).inflate(R.layout.listmain_dorm, parent,false));
                    break;
                default:break;

            }
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
            //判断holder是哪个类，从而确定是哪种布局
            /////布局1
            if(holder instanceof viewHolder1) {
                viewHolder1 viewholder = (viewHolder1) holder;
                Map<String, String> map = data.get(position);
                bmp = BitmapFactory.decodeResource(getResources(),Integer.valueOf(map.get("Icon")));
                viewholder.image.setImageBitmap(bmp);
                final String Name = map.get("Name");
                viewholder.name.setText(Name);
                viewholder.value.setText(map.get("Value"));

                if(Name.contains("用电量"))
                    viewholder.detail.setVisibility(View.GONE);
                else
                    viewholder.detail.setVisibility(View.VISIBLE);


                if(!map.get("Title").equals("-1")){
                    viewholder.title.setText(map.get("Title"));   //设置标题
                    viewholder.title.setVisibility(View.VISIBLE);  //显示标题
                }
                else
                    viewholder.title.setVisibility(View.GONE);
                viewholder.layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {   //根据点击的内容选择需要跳转的页面


                            if(Name.contains("用电量")){
                                return;
                            }
                            else {
                                Intent intent = new Intent();
                                intent.putExtra("RoomID", RoomID);       //宿舍ID
                                intent.putExtra("AccountType", "照明");       //账户类型    //学生姓名
                                intent.putExtra("Field", Name);    //查看类型，电压，电流。。。
                                intent.putExtra("RoomNum", RoomNum);   //房间号
                                intent.setClass(ThreeData_Activity.this, ThreeHisData_Activity.class);//设置要跳转的activity
                                startActivity(intent);//开始跳转
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            }
                        }

                });
            }

            else if(holder instanceof viewHolder3){
                viewHolder3 viewholder = (viewHolder3) holder;
                Map<String, String> map = data.get(position);
                viewholder.name.setText(map.get("Value"));
                if(!map.get("Title").equals("-1")){
                    viewholder.title.setText(map.get("Title"));   //设置标题
                    viewholder.title.setVisibility(View.VISIBLE);  //显示标题
                }
                else
                    viewholder.title.setVisibility(View.GONE);
                viewholder.layout.setOnClickListener(new View.OnClickListener() {   //点击宿舍详情，不进行页面跳转
                    @Override
                    public void onClick(View v) {

                    }
                });
            }

        }

        @Override
        public int getItemCount()
        {
            return data.size();
        }
        /**
         * 决定元素的布局使用哪种类型
         *在本activity中，布局1使用R.layout.listmain_userinfo，

         *     布局3使用R.layout.listmain_dorm

         * @param position 数据源的下标
         * @return 一个int型标志，传递给onCreateViewHolder的第二个参数 */
        @Override
        public int getItemViewType(int position) {
            return Integer.valueOf(data.get(position).get("Type"));
        }

        /**
         * 自定义继承RecyclerView.ViewHolder的viewholder
         * 布局类型1对应的ViewHolder，R.layout.listmain_userinfo
         */
        class viewHolder1 extends RecyclerView.ViewHolder
        {
            LinearLayout layout;    //内容布局（除去标题）
            TextView title;   //分区小标题
            ImageView image;    //图标
            TextView name;    //左侧名称
            TextView value;     //右侧值
            ImageView detail;  //详细信息按钮

            public viewHolder1(View view)
            {
                super(view);
                layout=(LinearLayout)view.findViewById(R.id.layout1);
                title = (TextView) view.findViewById(R.id.list_header_title);
                image=(ImageView)view.findViewById(R.id.image);
                name = (TextView) view.findViewById(R.id.name);
                value = (TextView) view.findViewById(R.id.value);
                detail = (ImageView) view.findViewById(R.id.detail);
            }
        }
        /*
        * 布局类型3对应的ViewHolder，R.layout.listmain_dorm
        */
        class viewHolder3 extends RecyclerView.ViewHolder
        {
            LinearLayout layout;    //内容布局（除去标题）
            TextView title;   //分区小标题
            TextView name;  //房间详情

            public viewHolder3(View view)
            {
                super(view);
                layout=(LinearLayout)view.findViewById(R.id.layout1);
                title = (TextView) view.findViewById(R.id.list_header_title);
                name = (TextView) view.findViewById(R.id.name);
            }
        }

    }

}
