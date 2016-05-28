package com.suntrans.suntranswater;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshAdapterViewBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.ksoap2.serialization.SoapObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.zip.Inflater;

import Adapters.SectionListAdapter;
import WebServices.jiexi;
import WebServices.soap;
import convert.Converts;
import views.AppManager;

/**
 * Created by 石奋斗 on 2016/4/18.
 */
public class All_Activity extends AppCompatActivity {
    private LinearLayout layout_setting;   //设置按钮
    private LinearLayout layout_failure;   //加载失败
    private LinearLayout layout_loading;   //正在加载
    private PullToRefreshListView mPullToRefreshListView;   //下拉刷新
    private ListView list;
    private ArrayList<Map<String,String>> data=new ArrayList<Map<String,String>>();   //存放所有站点的数据
    private ArrayList<String> data_time = new ArrayList<>();  //存放当前时间
    private SectionListAdapter adapter;
    private String UserName;  //用户登录名
    private Handler handler1=new Handler(){
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            if(msg.what==1)   //请求数据成功
            {
                ArrayList<Map<String,String>> data1=(ArrayList<Map<String,String>>) msg.obj;
                layout_failure.setVisibility(View.GONE);
                layout_loading.setVisibility(View.GONE);
                data=new ArrayList<Map<String, String>>(data1);
                adapter.notifyDataSetChanged();
            }
            else if(msg.what==0)    //请求数据失败
            {
                layout_failure.setVisibility(View.VISIBLE);
                layout_loading.setVisibility(View.GONE);
            }
            else{    //下拉刷新请求数据失败
                Toast.makeText(getApplicationContext(),"刷新失败！",Toast.LENGTH_SHORT).show();
            }


        }
    };
    @Override
    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all);
        AppManager.getAppManager().addActivity(this);
        Intent intent=getIntent();
        UserName = intent.getStringExtra("UserName");  //用户登录名
        layout_setting=(LinearLayout)findViewById(R.id.layout_setting);
        layout_failure = (LinearLayout) findViewById(R.id.layout_failure);
        layout_loading = (LinearLayout) findViewById(R.id.layout_loading);
        mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.list);
        list=mPullToRefreshListView.getRefreshableView();
        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);   //设置只有下拉
        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {  //设置下拉刷新要进行的操作
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                new RefreshTask().execute();
            }
        });
        //获取系统当前时间
        SimpleDateFormat sDateFormat    =   new SimpleDateFormat("yyyy年MM月dd日 HH:mm");   //hh为小写是12小时制，为大写HH时时24小时制
        String    date    =    sDateFormat.format(new    java.util.Date());
        data_time.clear();
        data_time.add(date);
        adapter = new SectionListAdapter(getApplicationContext());
        adapter.addSection("",new Adapter_time());
        adapter.addSection("站点列表",new Adapter_sites());
        list.setAdapter(adapter);
        layout_failure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_loading.setVisibility(View.VISIBLE);
                layout_failure.setVisibility(View.GONE);
                new MainThread().start();
            }
        });
        layout_setting.setOnClickListener(new View.OnClickListener() {   //进入设置页面
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("UserName",UserName);
                intent.setClass(All_Activity.this, Setting_Activity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);//设置activity切换效果
            }
        });
        layout_loading.setVisibility(View.VISIBLE);
        new MainThread().start();
        //    设置通知栏半透明
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Converts.setTranslucentStatus(All_Activity.this,true);
        }
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

    //下拉刷新请求数据的任务
    class RefreshTask extends AsyncTask<Void,Void,String> {
        @Override
        protected String doInBackground(Void... params) {    //后台进行，返回的结果传入onPostExecute 。可以使用publishProgress(Progress...)来发布一个或多个进度单位(unitsof progress)

            try{
                Thread.sleep(800);
            }
            catch(Exception e){}
            try{
                SoapObject result=soap.Inquiry_Sites();
                ArrayList<Map<String,String>> data1= jiexi.inquiry_realdata(result);
                Message msg=new Message();
                msg.what=1;   //刷新成功
                msg.obj=data1;
                handler1.sendMessage(msg);
            }
            catch(Exception e){
                Message msg=new Message();
                msg.what=-1;    //刷新失败
                handler1.sendMessage(msg);
            }

            return "1";
        }

        @Override
        protected void onPostExecute(String result) {  //计算结束后调用
            super.onPostExecute(result);
            mPullToRefreshListView.onRefreshComplete();
        }
    }


    private class MainThread extends Thread{
        @Override
        public void run(){
            try{
                SoapObject result = soap.Inquiry_Sites();
                ArrayList<Map<String,String>> data1= jiexi.inquiry_sites(result);
                Message msg=new Message();
                msg.what=1;  //表示请求成功
                msg.obj=data1;
                handler1.sendMessage(msg);
            }
            catch(Exception e)
            {
                Message msg=new Message();
                msg.what=0;  //表示请求失败
                handler1.sendMessage(msg);
            }
        }
    }

    public class Adapter_time extends BaseAdapter{
        @Override
        public int getCount() {
            return data_time.size();
        }

        @Override
        public Object getItem(int position) {
            return data_time.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView==null)
                convertView= LayoutInflater.from(getApplication()).inflate(R.layout.list_time, null);
            TextView tx_time=(TextView)convertView.findViewById(R.id.tx_time);
            tx_time.setText(data_time.get(position));
            return convertView;
        }
    }

    public class Adapter_sites extends BaseAdapter{

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView==null)
                convertView= LayoutInflater.from(getApplication()).inflate(R.layout.list_all, null);
            TextView site_name=(TextView)convertView.findViewById(R.id.site_name);
            final String name=data.get(position).get("Name");  //站点名称
            final String addr=data.get(position).get("Addr");    //站点地址
            site_name.setText(name);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra("Addr", addr);
                    intent.putExtra("Name", name);
                    intent.setClass(All_Activity.this, Realdata_Activity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);//设置activity切换效果

                }
            });
            return convertView;
        }
    }
}
