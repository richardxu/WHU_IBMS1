package com.suntrans.suntranswater;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import Adapters.SectionListAdapter;
import WebServices.jiexi;
import WebServices.soap;
import convert.Converts;

/**
 * Created by 石奋斗 on 2016/4/19.
 */
public class Realdata_Activity extends AppCompatActivity {

    private LinearLayout layout_back;
    private LinearLayout layout_setting;
    private LinearLayout layout_failure;
    private LinearLayout layout_loading;
    private TextView title;
    private String addr;
    private String name;
    private PullToRefreshListView mPullToRefreshListView;
    private ListView list;
    private ArrayList<Map<String,String>> data=new ArrayList<Map<String,String>>();
    private ArrayList<String> list_name = new ArrayList<>();   //所有参数名称列表
    private SectionListAdapter adapter;
    //自定义比较器，用于实现arraylist的排序
    Comparator comp=new Comparator(){
        public int compare(Object O1,Object O2){
            Map<String,String> map1=(Map<String,String>)O1;
            Map<String,String> map2=(Map<String,String>)O2;
            return map1.get("Name").compareTo(map2.get("Name"));
        }
    };
//    private Adapter adapter;
    private Handler handler1=new Handler(){
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            if(msg.what==1)   //请求数据成功
            {
                ArrayList<Map<String,String>> data1=(ArrayList<Map<String,String>>) msg.obj;
                layout_failure.setVisibility(View.GONE);
                layout_loading.setVisibility(View.GONE);
                data=new ArrayList<Map<String, String>>();
//                Collections.sort(data,comp);   //对数组进行排序
                //以下的步骤是要将电压放在最后显示
                Map<String,String> map0=new HashMap<String,String>();
                for(Map<String,String> map:data1)
                {
                    if(!map.get("Name").equals("电压"))
                        data.add(new HashMap<String, String>(map));
                    else
                        map0 = new HashMap<>(map);
                }
                if(map0.containsKey("Name"))
                    data.add(map0);
                for(Map<String,String> map:data){
                    list_name.add(map.get("Name"));
                }
                adapter.notifyDataSetChanged();
            }
            else if(msg.what==0)   //请求数据失败
            {
                layout_failure.setVisibility(View.VISIBLE);
                layout_loading.setVisibility(View.GONE);
            }
            else
            {
                Toast.makeText(getApplicationContext(),"刷新失败",Toast.LENGTH_SHORT).show();
            }


        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.realdata);
        layout_back = (LinearLayout) findViewById(R.id.layout_back);   //返回键
        layout_setting = (LinearLayout) findViewById(R.id.layout_setting);   //配置键
        layout_failure = (LinearLayout) findViewById(R.id.layout_failure);   //加载失败
        layout_loading = (LinearLayout) findViewById(R.id.layout_loading);  //正在加载
        title=(TextView)findViewById(R.id.title);     //标题
        mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.list);
        list=mPullToRefreshListView.getRefreshableView();
        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);    //设置模式为下拉
        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {   //设置下拉刷新时的动作
            @Override
            public void onRefresh(final PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                // Update the LastUpdatedLabel
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel("上次刷新："+label);
                new RefreshTask().execute();

            }
        });
        Intent intent=getIntent();
        addr = intent.getStringExtra("Addr");
        name = intent.getStringExtra("Name");
        title.setText(name);   //设置标题
        layout_back.setOnClickListener(new View.OnClickListener() {   //返回
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
            }
        });
        layout_setting.setOnClickListener(new View.OnClickListener() {    //设置
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("list_name", list_name);
                intent.setClass(Realdata_Activity.this, Config_Activity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);

            }
        });
        layout_failure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_loading.setVisibility(View.VISIBLE);
                layout_failure.setVisibility(View.GONE);
                new MainThread().start();
            }
        });
        adapter = new SectionListAdapter(Realdata_Activity.this);
        adapter.addSection("在线参数",new Adapter());
        list.setAdapter(adapter);
        layout_loading.setVisibility(View.VISIBLE);
        new MainThread().start();   //新建刷新数据的线程
        //    设置通知栏半透明
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Converts.setTranslucentStatus(Realdata_Activity.this,true);
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

    //请求数据的线程
    class MainThread extends Thread{
        @Override
        public void run(){
            try{

                SoapObject result=soap.Inquiry_RealData(addr);
                ArrayList<Map<String,String>> data1= jiexi.inquiry_realdata(result);
                Message msg=new Message();
                msg.what=1;
                msg.obj=data1;
                handler1.sendMessage(msg);
            }
            catch(Exception e){
                Message msg=new Message();
                msg.what=0;
                handler1.sendMessage(msg);
            }

        }
    }

    //下拉刷新请求数据的任务
    class RefreshTask extends AsyncTask<Void,Void,String>{
        @Override
        protected String doInBackground(Void... params) {    //后台进行，返回的结果传入onPostExecute 。可以使用publishProgress(Progress...)来发布一个或多个进度单位(unitsof progress)

            try{
                Thread.sleep(800);
            }
            catch(Exception e){}
            try{
                SoapObject result=soap.Inquiry_RealData(addr);
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

    //ListView的自定义适配器
    public class Adapter extends BaseAdapter {

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
                convertView= LayoutInflater.from(getApplication()).inflate(R.layout.list_realdata, null);
            TextView tx_name=(TextView)convertView.findViewById(R.id.name);
            TextView tx_value = (TextView) convertView.findViewById(R.id.value);
            Map<String,String> map=data.get(position);
            final String name=map.get("Name");
            final String value=map.get("Value")+map.get("Unit");  //参数值+单位
            final String sid=map.get("Sid");
            final String measurement = map.get("Unit");   //单位
            tx_name.setText(name);
            tx_value.setText(value);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra("Sid", sid);   //传感器id
                    intent.putExtra("Name",name);      //参数名称
                    intent.putExtra("Measurement",measurement);   //单位
                    intent.setClass(Realdata_Activity.this, Hisdata_Activity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);//设置activity切换效果

                }
            });
            return convertView;
        }
    }
}
