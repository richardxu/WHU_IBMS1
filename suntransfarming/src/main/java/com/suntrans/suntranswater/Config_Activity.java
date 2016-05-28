package com.suntrans.suntranswater;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 石奋斗 on 2016/4/22.
 */
public class Config_Activity extends AppCompatActivity{

    private LinearLayout layout_back;
    private RecyclerView recyclerview;  //列表
    private SwipeRefreshLayout refreshlayout;   //下拉控件
    private ArrayList<Map<String, String>> data = new ArrayList<>();   //数据源
    private ArrayList<String> list_name = new ArrayList<>();   //参数名称列表
    Handler handler1=new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            refreshlayout.setRefreshing(false);   //结束加载动作
      }
    };
    @Override
    protected void onCreate(Bundle savedInstatnceState){
        super.onCreate(savedInstatnceState);
        setContentView(R.layout.config);
        Intent intent =getIntent();
        list_name = intent.getStringArrayListExtra("list_name");  //获取传入的参数列表
        layout_back = (LinearLayout) findViewById(R.id.layout_back);    //返回
        refreshlayout = (SwipeRefreshLayout) findViewById(R.id.refreshlayout);   //下拉列表
        recyclerview = (RecyclerView) findViewById(R.id.recyclerview);     //recyclerview
        layout_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);   //设置activity切换动画
            }
        });

        refreshlayout.setColorSchemeResources(R.color.red,R.color.blue,R.color.green,R.color.yellow);   //设置滚动条颜色
        refreshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                       handler1.sendEmptyMessage(1);
                    }
                }).start();
            }
        });

        for(String str:list_name)
        {
            Map<String,String> map=new HashMap<>();
            map.put("Name",str);
            data.add(map);
        }

        //设置布局管理器
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        //设置Adapter
        recyclerview.setAdapter(new mAdapter());
        //添加分割线
//        recyclerview.addItemDecoration(new DividerItemDecoration(
//                getActivity(), DividerItemDecoration.HORIZONTAL_LIST));
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
    //主要的执行顺序：getItemViewType==>onCreateViewHolder==>onBindViewHolder
    class mAdapter extends RecyclerView.Adapter{
        /****
         * 渲染具体的布局，根据viewType选择使用哪种布局
         * @param parent   父容器
         * @param viewType    布局类别，多种布局的情况定义多个viewholder
         * @return
         */
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                   Config_Activity.this).inflate(R.layout.list_config, parent,
                    false));
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
            MyViewHolder viewholder=(MyViewHolder)holder;
            viewholder.tv.setText(data.get(position).get("Name"));
            viewholder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(),"你点击了我！"+String.valueOf(position),Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public int getItemCount()
        {
            return data.size();
        }
        /**
         * 决定元素的布局使用哪种类型
         *
         * @param position 数据源的下标
         * @return 一个int型标志，传递给onCreateViewHolder的第二个参数 */
        @Override
        public int getItemViewType(int position) {
            return 1;
        }

        /**
         * 自定义继承RecyclerView.ViewHolder的viewholder
         */
        class MyViewHolder extends RecyclerView.ViewHolder
        {
            View layout;
            TextView tv;

            public MyViewHolder(View view)
            {
                super(view);
                layout=view;
                tv = (TextView) view.findViewById(R.id.name);

            }
        }

    }

}
