package views;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.ksoap2.serialization.SoapObject;

import com.suntrans.whu.R;

import Adapter.RecyclerViewDivider;
import convert.Converts;
import views.Switch.OnSwitchChangedListener;

import Adapter.SectionListAdapter;
import WebServices.jiexi;
import WebServices.soap;

import activities.Expense_Activity;
import activities.HisData_Activity;

import activities.StateLog_Activity;
import activities.Stu_Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.TextView;
import android.widget.Toast;

public class ZMFragment extends Fragment{
	private Bitmap bmp=null;   //定义bitmap变量存放图标
	private SwipeRefreshLayout refreshlayout;  //下拉控件
	private RecyclerView recyclerview;    //列表
	private long time;  //用来存储刷新时的时间。
	private LinearLayout layout_loading,layout_failure;   //加载中。。；加载失败。
	private String UserID=""; //用户账号
	private String Role="";   //角色号
	private String Permission="无";   //学生是否具有控制权限
	private String RoomID="";    //宿舍RoomID
	private String RoomType="";  //宿舍类型
	private String RoomInfo;  //宿舍详情
	private String RoomNum;  //宿舍号
	private mAdapter adapter;
	private String AccountType="照明";  //账户类型，照明
	private boolean IfRefresh=true;  //标志位，是否需要定时刷新，true表示需要，false表示不需要
	private int IsFinish=1;  //标志位，下拉刷新是否完成，0代表没完成，1代表完成
	private boolean IfRefreshOn=false;   //是否开启了刷新线程
	private SoapObject result=null;
	private final String[] Status_description=new String[]{"正常","恶性负载","电表锁定","等待"};
	private Vector<Map<String,String>> data=new Vector<>();   //页面需要显示的信息
	private ArrayList<Map<String,String>> datalist_userinfo=new ArrayList<Map<String,String>>();  //解析到的学生信息
	private ArrayList<Map<String,String>> datalist_channel=new ArrayList<Map<String,String>>();   //解析到的用电状态
	private ArrayList<Map<String,String>> datalist_room=new ArrayList<Map<String,String>>();   //解析到的宿舍信息
	private Map<String, String> map_channel = new HashMap<>();

	private Handler handler1=new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==1)         //代表请求数据成功
            {
				adapter.notifyDataSetChanged();   //刷新列表显示
	            layout_loading.setVisibility(View.GONE);
	            layout_failure.setVisibility(View.GONE);
				if(IfRefreshOn==false) {
					new RefreshThread().start();   //开始刷新线程
				}

            }
            else if(msg.what==0)         //代表请求数据失败
            {
            	layout_loading.setVisibility(View.GONE);
            	layout_failure.setVisibility(View.VISIBLE);
				Log.i("Order", "错误：" + msg.obj.toString());
				//Toast.makeText(getActivity(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
            }
            else if(msg.what==2)         //代表开始请求
            {
            	layout_loading.setVisibility(View.VISIBLE);
            	layout_failure.setVisibility(View.GONE);
            }
        }};
    @Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  
            Bundle savedInstanceState) {
    	Bundle bundle=getArguments();
    	UserID=bundle.getString("UserID");
    	RoomID =bundle.getString("RoomID");
		RoomType = bundle.getString("RoomType");
		Role =bundle.getString("Role");
		RoomInfo = bundle.getString("RoomInfo");
		String[] str=RoomInfo.split("-");
		RoomNum=str[str.length-1];   //房间号
    	View view = inflater.inflate(R.layout.zmfragment, null);
		refreshlayout = (SwipeRefreshLayout) view.findViewById(R.id.refreshlayout);  //下拉控件
		recyclerview = (RecyclerView) view.findViewById(R.id.recyclerview);  //列表控件
		refreshlayout.setSize(SwipeRefreshLayout.LARGE);  //设置大小
		refreshlayout.setColorSchemeResources(R.color.bg_action,R.color.green,R.color.yellow);   //设置滚动条颜色
		refreshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				IsFinish=0;   //开始刷新
				new GetDataTask().execute();   //执行任务
			}
		});

    	layout_loading=(LinearLayout)view.findViewById(R.id.layout_loading);   //加载中。。
    	layout_failure=(LinearLayout)view.findViewById(R.id.layout_failure);   //加载失败。。。
	    layout_failure.setOnClickListener(new OnClickListener(){    //加载失败点击监听，重新访问网络
			@Override
			public void onClick(View v) {
				layout_loading.setVisibility(View.VISIBLE);
				layout_failure.setVisibility(View.GONE);
				new MainThread().start();    //开始新的刷新线程
			}});
    	MainThread mainThread=new MainThread();
    	mainThread.start();
		//设置布局管理器
		recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
		//添加分割线，自定义RecyclerViewDivider继承RecyclerView.ItemDecoration
		Context context=getActivity().getApplicationContext();
		recyclerview.addItemDecoration(new RecyclerViewDivider(getActivity(),LinearLayoutManager.VERTICAL,
				Converts.dip2px(context,1f),R.color.gray));  //不知道为什么，此处修改分割线的颜色无效
		//设置Adapter适配器
		adapter=new mAdapter();
		recyclerview.setAdapter(adapter);

    	return view;
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
	public void onDestroy(){   //父activity销毁时执行
		super.onDestroy();
		IfRefresh=false;  //停止刷新
	}

	//对从服务器读取到的数据进行解析，并存放到data中
	private void DataInit(){
		data.clear();  //清除原有数据
		if(RoomType.equals("0")) {

			Map<String, String> map1 = new HashMap<String, String>();
			map1.put("Name", "账户余额");
			map1.put("Value", map_channel.get("PreChargeback")==null?"null":  (map_channel.get("PreChargeback")+ "元"));
			map1.put("Type","1");  //布局类型，1
			map1.put("Title","账户信息");    //小标题，如果不显示小标题，则此项为-1
			map1.put("Icon",String.valueOf(R.drawable.ic_balance));   //图标
			data.add(map1);
			Map<String, String> map2 = new HashMap<String, String>();
			map2.put("Name", "剩余补贴");
			map2.put("Value", map_channel.get("PreSubsidy")==null?"null":( map_channel.get("PreSubsidy") + "度"));
			map2.put("Type","1");  //布局类型，1
			map2.put("Title", "-1");
			map2.put("Icon", String.valueOf(R.drawable.ic_subsidy));
			data.add(map2);
			//前2个是账户信息

			Map<String,String> map5=new HashMap<String,String>();
			map5.put("Name", "开关状态");
			map5.put("Value",map_channel.get("State")==null?"0":(map_channel.get("State")));
			map5.put("Type","2");
			map5.put("Title", "用电状态");
			map5.put("Icon", String.valueOf(R.drawable.ic_onstate));
			data.add(map5);
			Map<String,String> map6=new HashMap<String,String>();
			map6.put("Name", "状态描述");
			map6.put("Value",Status_description[Integer.valueOf(map_channel.get("Status")==null?"0":(map_channel.get("Status")))]);
			map6.put("Type", "1");
			map6.put("Title", "-1");
			map6.put("Icon", String.valueOf(R.drawable.ic_detailinfo));
			data.add(map6);      //这两个是用电状态
		}
		else if(RoomType.equals("1")){
			Map<String,String> map5=new HashMap<String,String>();
			map5.put("Name", "开关状态");
			map5.put("Value",map_channel.get("State")==null?"0":(map_channel.get("State")));
			map5.put("Type","2");
			map5.put("Title", "用电状态");
			map5.put("Icon", String.valueOf(R.drawable.ic_onstate));
			data.add(map5);
			Map<String,String> map6=new HashMap<String,String>();
			map6.put("Name", "状态描述");
			map6.put("Value",Status_description[Integer.valueOf(map_channel.get("Status")==null?"0":(map_channel.get("Status")))]);
			map6.put("Type", "1");
			map6.put("Title", "-1");
			map6.put("Icon", String.valueOf(R.drawable.ic_detailinfo));
			data.add(map6);      //这两个是用电状态
		}
		Map<String, String> map9 = new HashMap<>();
		map9.put("Name","宿舍详情");
		map9.put("Value",RoomInfo);
		map9.put("Type","3");
		map9.put("Title", "宿舍详情");
		data.add(map9);   //这个是宿舍详情


		Map<String,String> map7=new HashMap<String,String>();
		map7.put("Name", "电压");
		map7.put("Value",map_channel.get("U")==null?"null":(map_channel.get("U")+"V"));
		map7.put("Type","1");
		map7.put("Title", "电量信息");
		map7.put("Icon", String.valueOf(R.drawable.ic_voltage));
		data.add(map7);
		Map<String,String> map8=new HashMap<String,String>();
		map8.put("Name", "电流");
		map8.put("Value",map_channel.get("I")==null?"null":(map_channel.get("I")+"A"));
		map8.put("Type","1");
		map8.put("Title", "-1");
		map8.put("Icon", String.valueOf(R.drawable.ic_current));
		data.add(map8);
		Map<String,String> map10=new HashMap<String,String>();
		map10.put("Name", "功率");
		map10.put("Value",map_channel.get("Power")==null?"null":(map_channel.get("Power")+"W"));
		map10.put("Type","1");
		map10.put("Title", "-1");
		map10.put("Icon", String.valueOf(R.drawable.ic_power));
		data.add(map10);
		Map<String,String> map11=new HashMap<String,String>();
		map11.put("Name", "功率因数");
		map11.put("Value",map_channel.get("PowerRate")==null?"null":(map_channel.get("PowerRate")));
		map11.put("Type","1");
		map11.put("Title", "-1");
		map11.put("Icon", String.valueOf(R.drawable.ic_powerrate));
		data.add(map11);
		Map<String,String> map12=new HashMap<String,String>();
		map12.put("Name", "本日已用电量");
		map12.put("Value",map_channel.get("ElecDay")==null?"null":(map_channel.get("ElecDay")+"度"));
		map12.put("Type","1");
		map12.put("Title", "-1");
		map12.put("Icon", String.valueOf(R.drawable.ic_elec));
		data.add(map12);
		Map<String,String> map3=new HashMap<String,String>();
		map3.put("Name", "本月已用电量");
		map3.put("Value",map_channel.get("ElecMonth")==null?"null":(map_channel.get("ElecMonth")+"度"));
		map3.put("Type","1");
		map3.put("Title", "-1");
		map3.put("Icon", String.valueOf(R.drawable.ic_elec));
		data.add(map3);
		Map<String,String> map4=new HashMap<String,String>();
		map4.put("Name", "总用电量");
		map4.put("Value",map_channel.get("Eletricity")==null?"null":(map_channel.get("Eletricity")+"度"));
		map4.put("Type","1");
		map4.put("Title", "-1");
		map4.put("Icon", String.valueOf(R.drawable.ic_elec));
		data.add(map4); //这7个是电量信息

		if(RoomType.equals("0")&&datalist_room.size()>0) {
			for (int k = 0; k < datalist_room.size(); k++) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("Name", datalist_room.get(k).get("SName"));   //学生姓名
				map.put("Value", datalist_room.get(k).get("Faculty"));   //学生学院
				map.put("Type", "4");
				if(k==0)
					map.put("Title","学生信息");
				else
					map.put("Title", "-1");
				data.add(map);
			}    //这里面是学生信息
		}

	}
    //自定义刷新线程，访问网络获取数据
    public class MainThread extends Thread{
    	@Override
    	 public void run()
    	 {
    		 SoapObject result=null;
    		 try
    		 {
				 if(RoomType.equals("0")) {   //如果是学生宿舍，就查询学生信息，如果没有学生，就不显示账户和成员列表
					 result = soap.Inquiry_Room_RoomID(RoomID);
					 datalist_room = jiexi.inquiry_room_roomid(result);     //获取宿舍所有成员信息
				 }

				 result=soap.Inquiry_Channel_RoomID(RoomID, AccountType);
				 datalist_channel=jiexi.inquiry_channel(result);    //获取电表状态
				 map_channel=datalist_channel.get(0);

				 DataInit();

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
    //自定义刷新线程，定时对页面数据进行刷新
	private class RefreshThread extends Thread{
		@Override
		public void run(){
			IfRefreshOn=true;
			while(IfRefresh) {
				try {
					Thread.sleep(2000);   //延时2s
				} catch (Exception e) {
					e.printStackTrace();
				}
				if(IsFinish==1) {
					try {
					/*if(RoomType.equals("0")) {   //如果是学生宿舍，就查询学生信息，如果没有学生，就不显示账户和成员列表
						result = soap.Inquiry_Room_RoomID(RoomID);
						datalist_room = jiexi.inquiry_room_roomid(result);     //获取宿舍所有成员信息
					}
*/
						Log.i("Thread","zm开始刷新");
						result = soap.Inquiry_Channel_RoomID(RoomID, AccountType);
						datalist_channel = jiexi.inquiry_channel(result);    //获取电表状态
						map_channel = datalist_channel.get(0);
						DataInit();

						Message msg = new Message();
						msg.what = 1;    //代表成功
						handler1.sendMessage(msg);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}
			IfRefreshOn=false;   //关闭了刷新线程
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
			 if(RoomType.equals("0")) {   //如果是学生宿舍，就查询学生信息，如果没有学生，就不显示账户和成员列表
				 result = soap.Inquiry_Room_RoomID(RoomID);
				 datalist_room = jiexi.inquiry_room_roomid(result);     //获取宿舍所有成员信息
			 }

			 result=soap.Inquiry_Channel_RoomID(RoomID, AccountType);
			 datalist_channel=jiexi.inquiry_channel(result);    //获取电表状态
			 map_channel=datalist_channel.get(0);

		     DataInit();   //数据处理

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
    			Toast.makeText(getActivity().getApplicationContext(),"刷新失败！",Toast.LENGTH_SHORT).show();
    		}
			IsFinish=1;
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
					getActivity()).inflate(R.layout.listmain_userinfo, parent,false));
			switch(viewType){
				case 1:
					holder= new viewHolder1(LayoutInflater.from(
							getActivity()).inflate(R.layout.listmain_userinfo, parent,false));
					break;
				case 2:
					holder= new viewHolder2(LayoutInflater.from(
							getActivity()).inflate(R.layout.listmain_state, parent,false));
					break;
				case 3:
					holder= new viewHolder3(LayoutInflater.from(
							getActivity()).inflate(R.layout.listmain_dorm, parent,false));
					break;
				case 4:
					holder= new viewHolder4(LayoutInflater.from(
							getActivity()).inflate(R.layout.listmain_staffinfo, parent,false));
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
				bmp = BitmapFactory.decodeResource(getActivity().getResources(),Integer.valueOf(map.get("Icon")));
				viewholder.image.setImageBitmap(bmp);
				final String Name = map.get("Name");
				viewholder.name.setText(map.get("Name"));
				viewholder.value.setText(map.get("Value"));
				if(RoomType.equals("0")&&datalist_room.size()>0)   //如果是学生宿舍，并且宿舍有人，才可以看账户和用电量的历史记录
				{
					viewholder.detail.setVisibility(View.VISIBLE);
				}
				else{
					if(Name.contains("用电量"))
						viewholder.detail.setVisibility(View.GONE);
					else
						viewholder.detail.setVisibility(View.VISIBLE);
				}

				if(!map.get("Title").equals("-1")){
					viewholder.title.setText(map.get("Title"));   //设置标题
					viewholder.title.setVisibility(View.VISIBLE);  //显示标题
				}
				else
					viewholder.title.setVisibility(View.GONE);
				viewholder.layout.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {   //根据点击的内容选择需要跳转的页面

						if(Name.equals("状态描述")){   //跳转到状态日志页面
							Intent intent=new Intent();
							intent.putExtra("RoomID",RoomID);       //房间id
							intent.putExtra("AccountType", "照明");       //账户类型
							intent.putExtra("RoomNum",RoomNum);   //房间号
							intent.setClass(getActivity(), StateLog_Activity.class);//设置要跳转的activity
							getActivity().startActivity(intent);//开始跳转
							getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
						}
						else if(Name.equals("账户余额")||Name.equals("剩余补贴")){   //资费记录页面
							Intent intent=new Intent();
							intent.putExtra("AccountType", "照明");       //账户类型
							intent.putExtra("RoomID", RoomID);       //宿舍ID
							intent.putExtra("RoomNum", RoomNum);   //房间号
							intent.setClass(getActivity(), Expense_Activity.class);//设置要跳转的activity
							getActivity().startActivity(intent);//开始跳转
							getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);//设置activity切换效果

						}
						else{
							if(datalist_room.size()==0&&Name.contains("用电量")){
								return;
							}
							else {
								Intent intent = new Intent();
								intent.putExtra("RoomID", RoomID);       //学号
								intent.putExtra("AccountType", "照明");       //账户类型    //学生姓名
								intent.putExtra("Field", Name);    //查看类型，电压，电流。。。
								intent.putExtra("RoomNum", RoomNum);   //房间号
								intent.setClass(getActivity(), HisData_Activity.class);//设置要跳转的activity
								getActivity().startActivity(intent);//开始跳转
								getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
							}
						}
					}
				});
			}
			else if(holder instanceof viewHolder2){
				final viewHolder2 viewholder = (viewHolder2) holder;
				Map<String, String> map = data.get(position);
				bmp = BitmapFactory.decodeResource(getActivity().getResources(),Integer.valueOf(map.get("Icon")));
				viewholder.image.setImageBitmap(bmp);
				viewholder.name.setText(map.get("Name"));
				viewholder.state.setState(map.get("Value").equals("1"));   //State=1则代表打开
				if(Role.equals("3")){
					viewholder.state.setEnabled(false);   //如果角色号是3，则不允许控制
				}
				if(!map.get("Title").equals("-1")){
					viewholder.title.setText(map.get("Title"));   //设置标题
					viewholder.title.setVisibility(View.VISIBLE);  //显示标题
				}
				else
					viewholder.title.setVisibility(View.GONE);
				final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
				viewholder.state.setOnChangeListener(new OnSwitchChangedListener(){
					@Override
					public void onSwitchChange(Switch switchView,final boolean isChecked) {   //设置开关点击监听
						if(Role.equals("1")||Role.equals("2"))  //如果是学生，则只允许打开
						{
							if(isChecked)    //如果是要打开
							{
								new Thread(){
									public void run(){
										try{
											String succ=soap.Insert_Order(UserID,"照明","2");   //发送打开命令
											if(succ.equals("1")){
												Looper.prepare();
												Toast.makeText(getActivity().getApplicationContext(), "命令已发送！", Toast.LENGTH_SHORT).show();
												Looper.loop();
											}
											else{
												Looper.prepare();
												Toast.makeText(getActivity().getApplicationContext(), "当前不能进行此操作！", Toast.LENGTH_SHORT).show();
												Looper.loop();
											}
										}
										catch (Exception e){
											Looper.prepare();
											Toast.makeText(getActivity(),"网络错误",Toast.LENGTH_SHORT).show();
											Looper.loop();
										}
									}
								}.start();
							}
							else   //如果是要关闭
							{
								new Thread(){
									public void run(){
										try{
											String succ=soap.Insert_Order(UserID,"照明","3");   //发送关闭命令
											if(succ.equals("1")){
												Looper.prepare();
												Toast.makeText(getActivity().getApplicationContext(), "命令已发送！", Toast.LENGTH_SHORT).show();
												Looper.loop();
											}
											else{
												Looper.prepare();
												Toast.makeText(getActivity().getApplicationContext(), "当前不能进行此操作！", Toast.LENGTH_SHORT).show();
												Looper.loop();
											}
										}
										catch (Exception e){
											Looper.prepare();
											Toast.makeText(getActivity(),"网络错误",Toast.LENGTH_SHORT).show();
											Looper.loop();
										}
									}
								}.start();
							}
						}
						else //如果有权限，或者用户是管理员，则提示是否进行控制
						{
							builder.setCancelable(false);
							String[] str = RoomInfo.split("-");
							String RoomNum = str[str.length - 1];
							builder.setTitle("确定要" + (isChecked ? "打开" : "关闭") + RoomNum + "照明开关?");
							// builder.setMessage("信息1\n信息2");
							builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int whichButton) {
									new Thread() {    //新建线程发送命令
										@Override
										public void run() {
											try {
												String result = soap.Insert_Order_RoomID(UserID, RoomID, "照明", isChecked ? "2" : "3");
												if (result.equals("1"))   //命令添加成功
												{
													Looper.prepare();
													Toast.makeText(getActivity().getApplicationContext(), "命令已发送！", Toast.LENGTH_SHORT).show();
													Looper.loop();
												} else {   //命令添加失败
													Looper.prepare();
													Toast.makeText(getActivity().getApplicationContext(), "命令发送失败！", Toast.LENGTH_SHORT).show();
													Looper.loop();
												}
											} catch (Exception e) {   //网络错误
												Looper.prepare();
												Toast.makeText(getActivity().getApplicationContext(), "网络错误！", Toast.LENGTH_SHORT).show();
												Looper.loop();
											}
										}
									}.start();
								}
							});
							builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int whichButton) {
									viewholder.state.setState(!isChecked);  //将开关恢复原状
								}
							});
							builder.create().show();
						}
					}});
				viewholder.layout.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {   //跳转到状态日志页面
						Intent intent=new Intent();
						intent.putExtra("RoomID",RoomID);       //房间id
						intent.putExtra("AccountType", "照明");       //账户类型
						intent.putExtra("RoomNum", RoomNum);   //房间号
						intent.setClass(getActivity(), StateLog_Activity.class);//设置要跳转的activity
						getActivity().startActivity(intent);//开始跳转
						getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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
				viewholder.layout.setOnClickListener(new OnClickListener() {   //点击宿舍详情，不进行页面跳转
					@Override
					public void onClick(View v) {

					}
				});
			}
			else if(holder instanceof viewHolder4){
				viewHolder4 viewholder = (viewHolder4) holder;
				Map<String, String> map = data.get(position);
				final String Name=map.get("Name");  //学生姓名
				viewholder.name.setText(Name);
				viewholder.value.setText(map.get("Value"));
				if(!map.get("Title").equals("-1")){
					viewholder.title.setText(map.get("Title"));   //设置标题
					viewholder.title.setVisibility(View.VISIBLE);  //显示标题
				}
				else
					viewholder.title.setVisibility(View.GONE);
				viewholder.layout.setOnClickListener(new OnClickListener() {   //跳转到学生页面
					@Override
					public void onClick(View v) {
						Intent intent=new Intent();
						intent.putExtra("RoomID",RoomID);       //宿舍RoomID
						intent.putExtra("Name",Name);         //学生姓名
						intent.putExtra("RoomInfo",RoomInfo);    //宿舍信息
						intent.putExtra("RoomNum", RoomNum);   //房间号
						intent.setClass(getActivity(), Stu_Activity.class);//设置要跳转的activity
						getActivity().startActivity(intent);//开始跳转
						getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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
		 *     布局2使用R.layout.listmain_state,
		 *     布局3使用R.layout.listmain_dorm
		 *     布局4使用R.layout.listmain_staffinfo
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
		* 布局类型2对应的ViewHolder，R.layout.listmain_state
		*/
		class viewHolder2 extends RecyclerView.ViewHolder
		{
			LinearLayout layout;    //内容布局（除去标题）
			TextView title;   //分区小标题
			ImageView image;
			TextView name;
			Switch state;  //开关状态

			public viewHolder2(View view)
			{
				super(view);
				layout=(LinearLayout)view.findViewById(R.id.layout1);
				title = (TextView) view.findViewById(R.id.list_header_title);
				image=(ImageView)view.findViewById(R.id.image);
				name = (TextView) view.findViewById(R.id.name);
				state = (Switch) view.findViewById(R.id.state);
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
		/*
		* 布局类型4对应的ViewHolder，R.layout.listmain_staffinfo
		*/
		class viewHolder4 extends RecyclerView.ViewHolder
		{
			LinearLayout layout;    //内容布局（除去标题）
			TextView title;   //分区小标题
			TextView name;     //学生姓名
			TextView value;  //学院

			public viewHolder4(View view)
			{
				super(view);
				layout=(LinearLayout)view.findViewById(R.id.layout1);
				title = (TextView) view.findViewById(R.id.list_header_title);
				name = (TextView) view.findViewById(R.id.name);
				value = (TextView) view.findViewById(R.id.value);
			}
		}

	}


	
}
