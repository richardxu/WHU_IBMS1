package views;


import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.ksoap2.serialization.SoapObject;

import com.suntrans.whu.R;

import pulltofresh.PullDownElasticImp;
import pulltofresh.PullDownScrollView;
import pulltofresh.PullDownScrollView.RefreshListener;
import views.Switch.OnSwitchChangedListener;

import Adapter.SectionListAdapter;
import WebServices.jiexi;
import WebServices.soap;
import activities.All_Activity;
import activities.Expense_Activity;
import activities.HisData_Activity;
import activities.Inquiryapply_Activity;
import activities.Insertapply_Activity;
import activities.StateLog_Activity;
import activities.Stu_Activity;
import android.app.AlertDialog;
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
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
public class ZMFragment extends Fragment{
	private Bitmap bmp=null;   //定义bitmap变量存放图标
	private PullToRefreshListView mPullRefreshListView;    //下拉列表控件
	private ListView list;    //列表
	//private PullDownScrollView mPullDownScrollView;  //下拉组件
	private long time;  //用来存储刷新时的时间。
	private LinearLayout layout_loading,layout_failure;   //加载中。。；加载失败。
	private String UserID=""; //用户账号
	private String StudentID="";   //学号
	private String Role="";   //角色号
	private String Permission="无";   //学生是否具有控制权限
	private String RoomID="";    //宿舍RoomID
	private String AccountType="照明";  //账户类型，照明
	private int IsFinish=0;  //标志位，刷新是否完成，0代表没完成，1代表完成
	private final String[] Status_description=new String[]{"正常","恶性负责","电表锁定","等待"};
	private ArrayList<Map<String,String>> datalist_userinfo=new ArrayList<Map<String,String>>();  //解析到的学生信息
	private ArrayList<Map<String,String>> datalist_channel=new ArrayList<Map<String,String>>();   //解析到的用电状态
	private ArrayList<Map<String,String>> datalist_room=new ArrayList<Map<String,String>>();   //解析到的宿舍信息
	private ArrayList<Map<String,String>> datalist_application=new ArrayList<Map<String,String>>();   //解析到的学生正在进行的申请
	private Map<String,String> map_userinfo,map_channel;
	// TODO Auto-generated constructor stub
	private Handler handler1=new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==1)         //代表请求数据成功
            {
	           
	            ListInit();
	            layout_loading.setVisibility(View.GONE);
	            layout_failure.setVisibility(View.GONE);
	            //((SectionListAdapter)list.getAdapter()).notifyDataSetChanged();  //刷新列表显示
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  
            Bundle savedInstanceState) {
    	Bundle bundle=getArguments();
    	UserID=bundle.getString("UserID");
    	StudentID =bundle.getString("StudentID");
    	Role =bundle.getString("Role");
    	View view = inflater.inflate(R.layout.zmfragment, null);
    	mPullRefreshListView = (PullToRefreshListView)view.findViewById(R.id.list);
    	list=mPullRefreshListView.getRefreshableView();   //从下拉列表控件中获取
		mPullRefreshListView.setMode(Mode.PULL_FROM_START);//只有下拉刷新
		
		// 列表下拉监听
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				String label = DateUtils.formatDateTime(getActivity().getApplicationContext(), System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

				// Update the LastUpdatedLabel
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel("上次刷新："+label);

				// Do work to refresh the list here.
						new GetDataTask().execute();   //执行任务
					}
				});    	
    	layout_loading=(LinearLayout)view.findViewById(R.id.layout_loading);   //加载中。。
    	layout_failure=(LinearLayout)view.findViewById(R.id.layout_failure);   //加载失败。。。
	    layout_failure.setOnClickListener(new OnClickListener(){    //加载失败点击监听，重新访问网络
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				layout_loading.setVisibility(View.VISIBLE);
				layout_failure.setVisibility(View.GONE);
				new MainThread().start();    //开始新的刷新线程
			}});
    	MainThread mainThread=new MainThread();
    	mainThread.start();
    	//ListInit();
    	return view;
    }	
    
    //自定义刷新线程，访问网络获取数据
    public class MainThread extends Thread{
    	@Override
    	 public void run()
    	 {
    		 SoapObject result=null;
    		 try
    		 {
    			 result=soap.Inquiry_UserInfo(StudentID);
    			 datalist_userinfo=jiexi.inquiry_studentinfo(result);   //获取用户信息
    			 RoomID=datalist_userinfo.get(0).get("RoomID");   //获取RoomID
    			 result=soap.Inquiry_Room_RoomID(RoomID);
    			 datalist_room=jiexi.inquiry_room_roomid(result);     //获取宿舍所有成员信息
    			 result=soap.Inquiry_Channel(StudentID, AccountType);
    			 datalist_channel=jiexi.inquiry_channel(result);    //获取电表状态
    			 result=soap.Inquiry_Application_StudentID(StudentID);
    			 datalist_application=jiexi.inquiry_application(result);//获取学生正在进行的申请
    			 map_userinfo=datalist_userinfo.get(0);
    			 map_channel=datalist_channel.get(0);   
    			 for(int k=0;k<datalist_room.size();k++)
    			 {
    				 Map<String,String> map=datalist_room.get(k);
    				 if(map.get("StudentID").equals(StudentID))
    					 Permission=map.get("Permission");
    			 }
    			 IsFinish=1;   //加载完成
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
  //自定义刷新线程，访问网络获取数据
    public class Thread1 extends Thread{
    	@Override
    	 public void run()
    	 {
    		 SoapObject result=null;
    		 try
    		 {
    			 result=soap.Inquiry_Application_StudentID(StudentID);
    			 datalist_application=jiexi.inquiry_application(result);//获取学生正在进行的申请
    			 
    		 }
    		 catch(Exception e)
    		 {
    			
    		 }
    	       
    	 }
    }
    
    ///下拉刷新处理的函数。
    private class GetDataTask extends AsyncTask<Void, Void, String> {
    	// 后台处理部分
    	@Override
    	protected String doInBackground(Void... params) {
    		// Simulates a background job.
    		SoapObject result=null;
    		String str="0";
   		 try
   		 {
   			 result=soap.Inquiry_UserInfo(StudentID);
   			 datalist_userinfo=jiexi.inquiry_studentinfo(result);   //获取用户信息
   			 RoomID=datalist_userinfo.get(0).get("RoomID");   //获取RoomID
   			 result=soap.Inquiry_Room_RoomID(RoomID);
   			 datalist_room=jiexi.inquiry_room_roomid(result);     //获取宿舍所有成员信息
   			 result=soap.Inquiry_Channel(StudentID, AccountType);
   			 datalist_channel=jiexi.inquiry_channel(result);    //获取电表状态   			
   			 map_userinfo=datalist_userinfo.get(0);
   			 map_channel=datalist_channel.get(0);   
   			 str="1";   //表示请求成功
   						 
   		 }
   		 catch(Exception e)
   		 {
   			 try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
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
    		
    		if(result.equals("1"))  //请求成功
    			ListInit();
    		else
    		{
    			Toast.makeText(getActivity().getApplicationContext(),"加载失败！",Toast.LENGTH_SHORT).show();
    		}
    		// Call onRefreshComplete when the list has been refreshed.
    		mPullRefreshListView.onRefreshComplete();   //表示刷新完成

    		super.onPostExecute(result);//这句是必有的，AsyncTask规定的格式
    	}
    }
    //为ListView设置适配器，list初始化函数
	private void ListInit()
	{
		 SectionListAdapter adapter = new SectionListAdapter(getActivity());  //实例化一个SectionListAdapter
		 //添加内容   《=====1=====》账户信息部分
		 final ArrayList<Map<String,Object>> data_userinfo=new ArrayList<Map<String,Object>>();
		 Map<String,Object> map1=new HashMap<String,Object>();
		 map1.put("Name", "账户余额");
		 map1.put("Value",map_userinfo.get("zmPreChargeback")+"元");
		 data_userinfo.add(map1);
		 Map<String,Object> map2=new HashMap<String,Object>();
		 map2.put("Name", "剩余补贴");
		 map2.put("Value",map_userinfo.get("Subsidyzm")+"度");
		 data_userinfo.add(map2);
		 Map<String,Object> map20=new HashMap<String,Object>();
		 map20.put("Name", "本月已用电量");
		 map20.put("Value",map_channel.get("ElecMonth")+"度");
		 data_userinfo.add(map20);
		 Map<String,Object> map3=new HashMap<String,Object>();
		 map3.put("Name", "总用电量");
		 map3.put("Value",map_channel.get("Eletricity")+"度");
		 data_userinfo.add(map3);
		 adapter.addSection("账户信息", new BaseAdapter(){
				@Override
				public int getCount() {
					// TODO Auto-generated method stub
					return data_userinfo.size();
				}
				@Override
				public Object getItem(int position) {
					// TODO Auto-generated method stub
					return data_userinfo.get(position);
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
						convertView = LayoutInflater.from(getActivity().getApplication()).inflate(R.layout.listmain_userinfo, null);  
					ImageView image=(ImageView)convertView.findViewById(R.id.image);  //图标
					TextView name=(TextView)convertView.findViewById(R.id.name);
					TextView value=(TextView)convertView.findViewById(R.id.value);
					final Map<String,Object> map=data_userinfo.get(position);
					name.setText(map.get("Name")==null?"null":map.get("Name").toString());
					value.setText(map.get("Value")==null?"null":map.get("Value").toString());					
					switch(position)
					{
						case 0:     //账户余额图标
						{
							bmp = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.ic_balance);   
							image.setImageBitmap(bmp);
							break;
						}	
						case 1:     //剩余补贴图标
						{
							bmp = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.ic_subsidy);   
							image.setImageBitmap(bmp);
							break;
						}	
						case 2:     //本月用电量图标
						{
							bmp = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.ic_elec);   
							image.setImageBitmap(bmp);
							break;
						}	
						case 3:     //累计用电量图标
						{
							bmp = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.ic_elec);   
							image.setImageBitmap(bmp);
							break;
						}	
						default:break;
					}
					convertView.setOnClickListener(new OnClickListener(){
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							if(position<2)
							{
								Intent intent=new Intent();
				        		intent.putExtra("AccountType", "照明");       //账户类型    
				        		intent.putExtra("RoomID", RoomID);       //宿舍ID    
								intent.setClass(getActivity(), Expense_Activity.class);//设置要跳转的activity
								getActivity().startActivity(intent);//开始跳转	
								getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);//设置activity切换效果
							}
							else
							{
								Intent intent=new Intent();
				        		//intent.putExtra("Name",map.get("Name").toString()); 
								intent.putExtra("RoomID",RoomID);       //学号
				        		intent.putExtra("AccountType", "照明");       //账户类型    //学生姓名
								intent.putExtra("Field", map.get("Name").toString());    //查看类型，电压，电流。。。
				        		intent.setClass(getActivity(), HisData_Activity.class);//设置要跳转的activity
								getActivity().startActivity(intent);//开始跳转	
								getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
							}
						}});
					return convertView;
				}});
		 
		 //添加内容   《=====2=====》用电状态部分
		 final ArrayList<Map<String,Object>> data_state=new ArrayList<Map<String,Object>>();
		 Map<String,Object> map4=new HashMap<String,Object>();
		 map4.put("Name", "开关状态");
		 map4.put("Value",map_channel.get("State"));
		 data_state.add(map4);
		 Map<String,Object> map5=new HashMap<String,Object>();
		 map5.put("Name", "状态描述");
		 map5.put("Value",map_channel.get("Status"));
		 data_state.add(map5);
		 adapter.addSection("用电状态", new BaseAdapter(){
				@Override
				public int getCount() {
					// TODO Auto-generated method stub
					return data_state.size();
				}
				@Override
				public Object getItem(int position) {
					// TODO Auto-generated method stub
					return data_state.get(position);
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
						convertView = LayoutInflater.from(getActivity().getApplication()).inflate(R.layout.listmain_state, null);  
					ImageView image=(ImageView)convertView.findViewById(R.id.image);
					TextView name=(TextView)convertView.findViewById(R.id.name);
					TextView value=(TextView)convertView.findViewById(R.id.value);
					final views.Switch state=(Switch)convertView.findViewById(R.id.state);
					final Map<String,Object> map=data_state.get(position);					
					switch(position)
					{
						case 0:     //开关状态图标
						{
							bmp = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.ic_onstate);   
							image.setImageBitmap(bmp);
							break;
						}	
						case 1:     //状态描述图标
						{
							bmp = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.ic_detailinfo);   
							image.setImageBitmap(bmp);
							break;
						}	
						default:break;
					}
					name.setText(map.get("Name").toString());
					value.setText(Status_description[Integer.parseInt(map.get("Value").toString())]);
					if(position==0)
					{
						state.setVisibility(View.VISIBLE);
						if(Role.equals("1")||Role.equals("2"))
							state.setEnabled(false);  
						else
							state.setEnabled(true);
						state.setState(map.get("Value").toString().equals("1")?true:false);
						value.setVisibility(View.GONE);
						//开关状态改变监听
						state.setOnChangeListener(new OnSwitchChangedListener(){
							@Override
							public void onSwitchChange(Switch switchView,
									final boolean isChecked) {
								// TODO Auto-generated method stub
								final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()); 
								builder.setCancelable(false);
								if(Permission.equals("无")&&(Role.equals("1")||Role.equals("2")))  //如果是学生，且无控制权限
								{
									new Thread1().start();
									builder.setTitle("您没有控制权限，是否申请获取控制权限？"); 							  
									builder.setPositiveButton("是", new DialogInterface.OnClickListener() {  
									         public void onClick(DialogInterface dialog, int whichButton) {
									        	 if(datalist_application.size()>0)   //如果有正在进行的申请
									        	 {
									        		 builder.setTitle("您有待审核的申请！"); 	
									        		 //跳转到查看申请的页面
									        		 builder.setPositiveButton("查看", new DialogInterface.OnClickListener() {  
												         public void onClick(DialogInterface dialog, int whichButton) {
												        	  Intent intent=new Intent();
												        	  intent.putExtra("data", (Serializable)datalist_application);
												        	  intent.setClass(getActivity(), Inquiryapply_Activity.class);
												        	  getActivity().startActivity(intent);//开始跳转
												        	  getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);//设置activity切换效果
												        	  
												         }
														 });
									        		 builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {  
												         public void onClick(DialogInterface dialog, int whichButton) {  
												        	 
												         }  
												     });  
									        		 builder.create().show();
									        	 }
									        	 else//跳转到权限申请的页面
									        	 {
									        		  Intent intent=new Intent();
										        	  intent.putExtra("StudentID", StudentID);   //学号
										        	  intent.setClass(getActivity(), Insertapply_Activity.class);
										        	  getActivity().startActivity(intent);//开始跳转
										        	  getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);//设置activity切换效果
										        	  
									        	 }
									        	 state.setState(!isChecked);  //将开关恢复原状
									         }
											 });
									  builder.setNegativeButton("否", new DialogInterface.OnClickListener() {  
									         public void onClick(DialogInterface dialog, int whichButton) {  
									        	 state.setState(!isChecked);  //将开关恢复原状
									         }  
									     });  
									  builder.create().show();
								}
								else //如果有权限，或者用户是管理员，则提示是否进行控制
								{							
									builder.setCancelable(false);
									builder.setTitle("确定要"+(isChecked?"打开":"关闭")+"照明开关?"); 							  
									builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {  
									         public void onClick(DialogInterface dialog, int whichButton) {
									        	 new Thread(){    //新建线程发送命令
									        		 @Override
									        		 public void run(){
									        			 try{
									        				 String result=soap.Insert_Order_RoomID(UserID, RoomID, "照明", isChecked?"2":"3");
									        				 if(result.equals("1"))   //命令添加成功
									        				 {
									        					 Looper.prepare();  
										        			     Toast.makeText(getActivity(), "命令已发送！", Toast.LENGTH_SHORT).show();  
										        			     Looper.loop(); 
									        				 }
									        				 else{   //命令添加失败
									        					 Looper.prepare();  
										        			     Toast.makeText(getActivity(), "命令发送失败！", Toast.LENGTH_SHORT).show();  
										        			     Looper.loop(); 
									        				 }
									        			 }
									        			 catch(Exception e){   //网络错误
									        				 Looper.prepare();  
									        			     Toast.makeText(getActivity(), "网络错误！", Toast.LENGTH_SHORT).show();  
									        			     Looper.loop(); 
									        			 }
									        		 }
									        	 }.start();
									         }
											 });
									  builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {  
									         public void onClick(DialogInterface dialog, int whichButton) {  
									        	 state.setState(!isChecked);  //将开关恢复原状
									         }  
									     });  
									  builder.create().show();
								}								
							}});
						
					}
					convertView.setOnClickListener(new OnClickListener(){
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
						//	if(position==0)
						//	{
								Intent intent=new Intent();
								intent.putExtra("RoomID",RoomID);       //学号
				        		intent.putExtra("AccountType", "照明");       //账户类型    
								intent.setClass(getActivity(), StateLog_Activity.class);//设置要跳转的activity
								getActivity().startActivity(intent);//开始跳转
								getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
						//	}
							//else
							//	Toast.makeText(getActivity(), "你点击了第"+position+"行", Toast.LENGTH_SHORT).show();
						}});
					return convertView;
				}});
		
		//添加内容   《=====3=====》宿舍详情部分
		 final ArrayList<Map<String,Object>> data_dorm=new ArrayList<Map<String,Object>>();
		 Map<String,Object> map10=new HashMap<String,Object>();
		 map10.put("Name", map_userinfo.get("Area")+"-"+map_userinfo.get("Building")+"-"+(map_userinfo.get("Unit").equals("anyType{}")?(map_userinfo.get("RoomNum")):(map_userinfo.get("Unit")+"-"+map_userinfo.get("RoomNum"))));
		 data_dorm.add(map10);
		 adapter.addSection("宿舍详情", new BaseAdapter(){
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
					if(convertView==null)
						convertView = LayoutInflater.from(getActivity().getApplication()).inflate(R.layout.listmain_dorm, null);  
					TextView name=(TextView)convertView.findViewById(R.id.name);
					Map<String,Object> map=data_dorm.get(position);
					name.setText(map.get("Name").toString());
					convertView.setOnClickListener(new OnClickListener(){
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							//Toast.makeText(getActivity(), "你点击了第"+position+"行", Toast.LENGTH_SHORT).show();
						}});
					return convertView;
				}});
		 
		//添加内容   《=====4=====》电量信息部分
		 final ArrayList<Map<String,Object>> data_elec=new ArrayList<Map<String,Object>>();
		 Map<String,Object> map15=new HashMap<String,Object>();
		 map15.put("Name", "电压");
		 map15.put("Value",map_channel.get("U")+"V");
		 data_elec.add(map15);
		 Map<String,Object> map16=new HashMap<String,Object>();
		 map16.put("Name", "电流");
		 map16.put("Value",map_channel.get("I")+"A");
		 data_elec.add(map16);
		 Map<String,Object> map17=new HashMap<String,Object>();
		 map17.put("Name", "功率");
		 map17.put("Value",map_channel.get("Power")+"W");
		 data_elec.add(map17);
		 Map<String,Object> map18=new HashMap<String,Object>();
		 map18.put("Name", "功率因数");
		 map18.put("Value",map_channel.get("PowerRate"));
		 data_elec.add(map18);
		 Map<String,Object> map19=new HashMap<String,Object>();
		 map19.put("Name", "本日已用电量");
		 map19.put("Value",map_channel.get("ElecDay")+"度");
		 data_elec.add(map19);
		 adapter.addSection("电量信息", new BaseAdapter(){
				@Override
				public int getCount() {
					// TODO Auto-generated method stub
					return data_elec.size();
				}
				@Override
				public Object getItem(int position) {
					// TODO Auto-generated method stub
					return data_elec.get(position);
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
						convertView = LayoutInflater.from(getActivity().getApplication()).inflate(R.layout.listmain_state, null);  
					ImageView image=(ImageView)convertView.findViewById(R.id.image);   //图标
					TextView name=(TextView)convertView.findViewById(R.id.name);   //电压、电流、功率。功率因数等等
					TextView value=(TextView)convertView.findViewById(R.id.value);    //值
					switch(position)
					{
						case 0:     //电压图标
						{
							bmp = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.ic_voltage);   
							image.setImageBitmap(bmp);
							break;
						}	
						case 1:     //电流图标
						{
							bmp = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.ic_current);   
							image.setImageBitmap(bmp);
							break;
						}	
						case 2:     //功率图标
						{
							bmp = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.ic_power);   
							image.setImageBitmap(bmp);
							break;
						}	
						case 3:     //功率因数图标
						{
							bmp = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.ic_powerrate);   
							image.setImageBitmap(bmp);
							break;
						}	
						case 4:     //用电量图标
						{
							bmp = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.ic_elec);   
							image.setImageBitmap(bmp);
							break;
						}	
						default:break;
					
					}
					final Map<String,Object> map=data_elec.get(position);
					name.setText(map.get("Name").toString());
					value.setText(map.get("Value").toString());
					convertView.setOnClickListener(new OnClickListener(){
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
								Intent intent=new Intent();
				        		//intent.putExtra("Name",map.get("Name").toString()); 
								intent.putExtra("RoomID",RoomID);       //学号
				        		intent.putExtra("AccountType", "照明");       //账户类型    //学生姓名
								intent.putExtra("Field", map.get("Name").toString());    //查看类型，电压，电流。。。
				        		intent.setClass(getActivity(), HisData_Activity.class);//设置要跳转的activity
								getActivity().startActivity(intent);//开始跳转	
								getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
							
						}});
					return convertView;
				}});
		 
		 //添加内容   《=====5=====》人员信息部分
		 final ArrayList<Map<String,Object>> data_staffinfo=new ArrayList<Map<String,Object>>();
		 for(int k=0;k<datalist_room.size();k++)
		 {
			 Map<String,Object> map=new HashMap<String,Object>();
			 map.put("Name", datalist_room.get(k).get("SName"));
			 map.put("Value",datalist_room.get(k).get("Faculty"));
			 data_staffinfo.add(map);
		 }
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
					if(convertView==null)
						convertView = LayoutInflater.from(getActivity().getApplication()).inflate(R.layout.listmain_staffinfo, null);  
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
							intent.putExtra("RoomID",RoomID);       //宿舍RoomID
			        		intent.putExtra("Name",map.get("Name").toString());         //学生姓名
			        		intent.putExtra("RoomInfo", map_userinfo.get("Area")+"-"+map_userinfo.get("Building")+"-"+(map_userinfo.get("Unit").equals("anyType{}")?(map_userinfo.get("RoomNum")):(map_userinfo.get("Unit")+"-"+map_userinfo.get("RoomNum"))));    //宿舍信息
							intent.setClass(getActivity(), Stu_Activity.class);//设置要跳转的activity
							getActivity().startActivity(intent);//开始跳转
							getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
						}});
					return convertView;
				}});
		 list.setAdapter(adapter);    //为listview设置适配器
		 
	}


	
}
