package activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.ksoap2.serialization.SoapObject;

import WebServices.jiexi;
import WebServices.soap;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.suntrans.whu.R;

import convert.PinyinUtils;

import database.DataCentre;
import database.DbHelper;

public class Main_Activity extends Activity {
	private String UserID="";
	private int flag=0;   //为0表示正常打开，为1表示从其他应用传入
	private String publickey="0wdjkjkdj";
	private String str="";   //存储参数信息
	private long time;
	private String Role;   //用户角色号
	private boolean IfNeedUpdate=true;   //是否需要更新数据库,true表示需要更新数据库，false表示不需要更新数据库
	private LinearLayout layout1,layout2;
	ArrayList<Map<String,String>> datalist_userinfo=new ArrayList<Map<String,String>>();
	private ArrayList<Map<String,String>> datalist_room=new ArrayList<Map<String,String>>();   //所有房间
	private ArrayList<Map<String,String>> datalist_stu=new ArrayList<Map<String,String>>();   //所有学生
	private Handler handler1=new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==1)         //代表请求数据成功,存入到数据库
            {            
            	time=System.currentTimeMillis();
    			Log.i("Time", "时间2==>"+time);
    			//DataCentre.datalist_room=datalist_room;
    			//DataCentre.datalist_stu=datalist_stu;
    			
    			if(flag==0||UserID==null)   //如果不是从平台打开的程序，或者密钥不对，判断是否自动登录，是则跳转到主页面，否则跳转到登录页面
    			{
    				new Thread1().start();
    			}
    			else   //如果是从平台打开的本应用，新建线程查询角色信息，若属于系统，跳转到主页面，否则，提示不属于本系统
    			{
	    			new Thread(){    //新建线程验证用户信息
	    				@Override
	    				public void run(){
	    					try{
	    						//查询用户信息
	    						SoapObject result=soap.Inquiry_UserInfo(UserID);
	    						datalist_userinfo=jiexi.inquiry_userinfo(result);					
	    						Message msg=new Message();
	    						msg.what=1;
	    						handler2.sendMessage(msg);
	    					}
	    					catch(Exception e)
	    					{
	    						Message msg=new Message();
	    						msg.what=0;
	    						msg.obj=e.toString();
	    						handler2.sendMessage(msg);
	    					}
	    				}
	    			}.start();
    			}
            }           
            else if(msg.what==0)         //代表请求数据失败
            {
            	if(flag==0||UserID==null)   //如果不是从平台打开的程序，或者密钥不对，自动登录
    			{
            		new Thread1().start();
    			}
    			else    //如果是从其他应用打开的本程序
    			{
	    			new Thread(){    //新建线程验证用户信息
	    				@Override
	    				public void run(){
	    					try{
	    						//查询用户信息
	    						SoapObject result=soap.Inquiry_UserInfo(UserID);
	    						datalist_userinfo=jiexi.inquiry_userinfo(result);					
	    						Message msg=new Message();
	    						msg.what=1;
	    						handler2.sendMessage(msg);
	    					}
	    					catch(Exception e)
	    					{
	    						Message msg=new Message();
	    						msg.what=0;
	    						msg.obj=e.toString();
	    						handler2.sendMessage(msg);
	    					}
	    				}
	    			}.start();
    			}
            }          
        }};
	private Handler handler2=new Handler(){   //从其他平台打开的本应用，用于判断传递过来的用户是否属于系统，根据角色号跳转到相应页面
        public void handleMessage(Message msg) 
        {
            super.handleMessage(msg);
            if(msg.what==1)
            {

            	if(datalist_userinfo.get(0).get("Role")!=null)
            	{
					Role=datalist_userinfo.get(0).get("Role");
					switchpage();   //页面跳转
            	}
            	else{    
            		layout2.setVisibility(View.VISIBLE);   //显示您不属于本系统
            		layout1.setVisibility(View.GONE);   
            	}
            }
            else if(msg.what==0)    //获取用户信息失败
            {
            	Intent intent=new Intent();   //跳转到登录页面                        
                intent.setClass(Main_Activity.this, LogIn_Activity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);//设置activity切换效果
                finish();
            	Toast.makeText(getApplicationContext(), "网络错误！", Toast.LENGTH_LONG).show();
            }
        }
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.main);      //设置activity布局文件
		layout1=(LinearLayout)findViewById(R.id.layout1);
		layout2=(LinearLayout)findViewById(R.id.layout2);
		time=System.currentTimeMillis();
		Log.i("Time", "时间1==>"+time);
		Intent intent=getIntent();		
		if(intent==null)   //此处的intent不是空的
		{
			str="intent=null";
			flag=0;
		}
		else{			
			Bundle bundle=intent.getBundleExtra("bundle");   //先获取bundle
			if(bundle==null)     //如果bundle是空，表示本应用不是其他应用打开的
			{
				flag=0;
				str="bundle=null";	
				//Log.i("IB", intent.toString());
			}
			else           //如果bundle不是空，表示本应用是通过其他应用打开的
			{
				flag=1;          
				str=bundle.getString("basecode");   //获取编码后的参数字符串
				try{
					byte[] bytecode= Base64.decode(str,Base64.DEFAULT);
					String str1=new String(bytecode);
					str1=str1.replace("\"", "").replace("{", "").replace("}", "");
					String userName=str1.split(",")[0].split(":")[1];   //学号
					String from=str1.split(",")[1].split(":")[1];    //唤醒程序来源
					String time=str1.split(",")[2].split(":")[1];     //时间
					String key=str1.split(",")[3].split(":")[1];       //key
					String encryptkey=convert.Converts.md5(time+publickey+userName);
					if(encryptkey.equals(key))
						UserID=userName;
					else
					{
						UserID=null;
						Toast.makeText(getApplicationContext(), "密钥不正确!", Toast.LENGTH_LONG).show();
					}
				}
				catch(Exception e)
				{
					UserID="";
					Toast.makeText(getApplicationContext(), "解析出错", Toast.LENGTH_LONG).show();
				}
				//Toast.makeText(getApplicationContext(), str1, Toast.LENGTH_LONG).show();
			}
			//Toast.makeText(getApplicationContext(), "标示"+flag, Toast.LENGTH_LONG).show();
		}
		new MainThread().start();		
	}
	
	public class MainThread extends Thread{
    	@Override
    	 public void run()
    	 {
    		 SoapObject result=null;
    		 try
    		 {

				 DbHelper dh = new DbHelper(Main_Activity.this,"IBMS",null,2);
				 SQLiteDatabase db = dh.getWritableDatabase();
				 Cursor cursor = db.query("room_tb", new String[]{"RoomID","Area","Building"}, null, null, null, null, null);
				 if(cursor.getCount()<1)    //如果没有宿舍，则请求所有宿舍数据
				 {
					 IfNeedUpdate=true;
					 result = soap.Inquiry_Building();
					 DataCentre.datalist_room = jiexi.inquiry_building(result);   //获取所有房间
					 result = soap.Inquiry_Student();
					 DataCentre.datalist_stu = jiexi.inquiry_student(result);     //获取宿舍所有成员信息
				 	 db.close();
				 }
				 else{
					 SharedPreferences sp=getSharedPreferences("UpdateTime", MODE_PRIVATE);   //文件名称和文件类型
					 long lasttime=sp.getLong("GetTime",0);   //获取最后一次更新的时间
					 time=System.currentTimeMillis();   //现在的毫秒数
					 if(time-lasttime>24*60*60*1000){   //如果距离现在超过24个小时，则重新请求所有学生和宿舍信息
						 result = soap.Inquiry_Building();
						 DataCentre.datalist_room = jiexi.inquiry_building(result);   //获取所有房间
						 result = soap.Inquiry_Student();
						 DataCentre.datalist_stu = jiexi.inquiry_student(result);     //获取宿舍所有成员信息
					 	IfNeedUpdate=true;
						 db.close();
					 }
					 else {   //不需要更新数据，从数据库读取学生和宿舍数据
						 IfNeedUpdate = false;
						 DataCentre.datalist_room = new ArrayList<>();
						 DataCentre.datalist_stu=new ArrayList<>();
						 cursor = db.query("room_tb", new String[]{"RoomID", "Area", "Building", "Unit", "Floor", "RoomNum", "RoomType"}, null, null, null, null, null);
						 while (cursor.moveToNext()){
							 Map<String, String> map = new HashMap<>();
							 map.put("RoomID", cursor.getString(0));
							 map.put("Area", cursor.getString(1));
							 map.put("Building", cursor.getString(2));
							 map.put("Unit", cursor.getString(3));
							 map.put("Floor",cursor.getString(4));
							 map.put("RoomNum",cursor.getString(5));
							 map.put("RoomType",cursor.getString(6));
							 DataCentre. datalist_room.add(map);

						 }
						 cursor = db.query("student_tb", new String[]{"RoomID", "Area", "Building", "Unit", "Floor", "RoomNum", "StudentID","SName","Faculty","Role"}, null, null, null, null, null);
					 	 while(cursor.moveToNext()){
							 Map<String, String> map = new HashMap<>();
							 map.put("RoomID", cursor.getString(0));
							 map.put("Area", cursor.getString(1));
							 map.put("Building", cursor.getString(2));
							 map.put("Unit", cursor.getString(3));
							 map.put("Floor",cursor.getString(4));
							 map.put("RoomNum",cursor.getString(5));
							 map.put("StudentID",cursor.getString(6));
							 map.put("SName",cursor.getString(7));
							 map.put("Faculty", cursor.getString(8));
							 map.put("Role", cursor.getString(9));
							 DataCentre.datalist_stu.add(map);
						 }
					 }
				 }

//				     time = System.currentTimeMillis();
//					 Log.i("Time", "访问数据前==>" + time);
//					 result = soap.Inquiry_Building();
//					 datalist_room = jiexi.inquiry_building(result);   //获取所有房间
//					 time = System.currentTimeMillis();
//					 Log.i("Time", "访问数据中==>" + time);    //用0.7s
//					 result = soap.Inquiry_Student();
//					 datalist_stu = jiexi.inquiry_student(result);     //获取宿舍所有成员信息
//					 time = System.currentTimeMillis();
//					 Log.i("Time", "访问数据后==>" + time);   //用2.3s
					 Message msg = new Message();
					 msg.what = 1;    //代表成功
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
	 public class Thread1 extends Thread{
		   	@Override
		   	 public void run()
		   	 {
		   		DbHelper dh1=new DbHelper(Main_Activity.this,"IBMS",null,2);
	    		SQLiteDatabase db = dh1.getWritableDatabase(); 
	    		//查询是否有自动登录的账号
	    		final Cursor cursor = db.query("users_tb", new String[]{"NID","Name","Password"}, "IsUsing=? and Auto=?", new String[]{"1","1"}, null, null, null);
	        	if(cursor.getCount()<1)    //如果没有，直接跳转到登录页面
	        	{
	        		Intent intent=new Intent();   //跳转到登录页面                        
	                intent.setClass(Main_Activity.this, LogIn_Activity.class);
	                startActivity(intent);
	                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);//设置activity切换效果
	                finish();     
	        	}
	        	else     //如果有的话，调用登录接口函数进行验证，验证成功则跳转到主页面，否则进入登录页面
	        	{    	
	        		try{
//						final String UserID="";
						String Password="";
	            		while(cursor.moveToNext())
	            		{
	            			UserID=cursor.getString(1);     //从数据库中获取用户账号
	            			Password=cursor.getString(2);  //从数据库中获取用户密码
	            		}          
						Role=soap.LogIn(UserID, Password);
						time=System.currentTimeMillis()-time;
		    			Log.i("Time", "时间3==>"+time);
						switchpage();   //页面跳转

					}
					catch(Exception e)
					{
						
						Intent intent=new Intent();   //跳转到登录页面                        
	                    intent.setClass(Main_Activity.this, LogIn_Activity.class);
	                    startActivity(intent);
	                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);//设置activity切换效果
	                    finish(); 
					}
	        		        	           
	        	}
		   	 }
		 }

	//自定义页面跳转函数，先对数据进行处理，再进行页面跳转
	private void switchpage() {
		if (Role.equals("0"))   //如果验证失败
		{
			Intent intent = new Intent();   //跳转到登录页面
			intent.setClass(Main_Activity.this, LogIn_Activity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);//设置activity切换效果
			finish();
		} else if (Role.equals("1") || Role.equals("2"))   //如果是学生，跳转到宿舍页面
		{
			new Thread() {
				public void run() {
					DbHelper dh2 = new DbHelper(Main_Activity.this, "IBMS", null, 2);
					SQLiteDatabase db1 = dh2.getWritableDatabase();
					long a = 0;
					Map<String, String> map = new HashMap<String, String>();
					db1.beginTransaction();       //手动设置开始事务   ，这样只打开一次数据库，一次性将数据写入,防止多次打开和关闭数据库，节省时间
					String sql = "delete from room_tb; ";        //清空宿舍表中的数据
					db1.execSQL(sql);
					sql = " delete from sqlite_sequence where name='room_tb'; ";  //自增列置0
					db1.execSQL(sql);
					sql = " delete from student_tb;";   //清空学生表中的数据
					db1.execSQL(sql);
					sql = " update sqlite_sequence set seq=0 where name='student_tb';";  //自增列置0
					db1.execSQL(sql);
					db1.setTransactionSuccessful();       //设置事务处理成功，不设置会自动回滚不提交
					db1.endTransaction();       //处理完成
					db1.close();
				}
			}.start();
			new Thread() {
				@Override
				public void run() {
					SoapObject result = soap.Inquiry_UserInfo(UserID);
					ArrayList<Map<String, String>> data = jiexi.inquiry_studentinfo(result);
					Map<String, String> map = data.get(0);
					String RoomID = map.get("RoomID");
					String RoomInfo = map.get("Area") + "-" + map.get("Building") + "-" + (map.get("Unit").equals("") ? (map.get("RoomNum")) : (map.get("Unit") + "-" + map.get("RoomNum")));    //宿舍信息
					String Role = map.get("Role");  //角色号
					Intent intent = new Intent();
					intent.putExtra("UserID", UserID);  //学号
					intent.putExtra("RoomInfo", RoomInfo);  //宿舍详情
					intent.putExtra("RoomID", RoomID);      //宿舍ID
					intent.putExtra("RoomType", "0");   //房间类型
					intent.putExtra("Role", Role);    //学生角色号，1代表用电账户管理员，2代表普通用户
					intent.setClass(Main_Activity.this, Dorm_Activity.class);//设置要跳转的activity
					startActivity(intent);//开始跳转
					overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);//设置activity切换效果
					finish();
				}
			}.start();  //查询出学生的信息，并传入到宿舍页面
		} else      //如果是管理员，跳转到管理员页面,并将数据写入数据库中
		{
			if (DataCentre.datalist_room.size() > 0 && DataCentre.datalist_stu.size() > 0&&IfNeedUpdate) {
				new Thread() {
					public void run() {
						DbHelper dh2 = new DbHelper(Main_Activity.this, "IBMS", null, 2);
						SQLiteDatabase db1 = dh2.getWritableDatabase();
						long a = 0;
						Map<String, String> map = new HashMap<String, String>();
						db1.beginTransaction();       //手动设置开始事务   ，这样只打开一次数据库，一次性将数据写入,防止多次打开和关闭数据库，节省时间
						String sql = "delete from room_tb; ";        //清空宿舍表中的数据
						db1.execSQL(sql);
						sql = " delete from sqlite_sequence where name='room_tb'; ";  //自增列置0
						db1.execSQL(sql);
						sql = " delete from student_tb;";   //清空学生表中的数据
						db1.execSQL(sql);
						sql = " update sqlite_sequence set seq=0 where name='student_tb';";  //自增列置0
						db1.execSQL(sql);
						for (int k = 0; k < DataCentre.datalist_room.size(); k++) {
							map = DataCentre.datalist_room.get(k);
							ContentValues cv = new ContentValues();    //内容数组
							cv.put("RoomID", map.get("RoomID"));   //宿舍ID
							cv.put("Area", map.get("Area"));   //园区
							cv.put("Building", map.get("Building"));   //楼栋
							cv.put("Unit", map.get("Unit"));   //单元
							cv.put("Floor", map.get("Floor"));   //楼层
							cv.put("RoomNum", map.get("RoomNum"));   //宿舍号
							cv.put("RoomType",map.get("RoomType"));   //房间类型
							a = db1.insert("room_tb", null, cv);    //插入到宿舍表中

						}

						for (int k = 0; k < DataCentre.datalist_stu.size(); k++) {
							map = DataCentre.datalist_stu.get(k);
							ContentValues cv = new ContentValues();    //内容数组
							cv.put("RoomID", map.get("RoomID"));   //宿舍ID
							cv.put("Area", map.get("Area"));   //园区
							cv.put("Building", map.get("Building"));   //楼栋
							cv.put("Unit", map.get("Unit"));   //单元
							cv.put("Floor", map.get("Floor"));   //楼层
							cv.put("RoomNum", map.get("RoomNum"));   //宿舍号
							cv.put("StudentID", map.get("StudentID"));   //学生
							cv.put("SName", map.get("SName"));   //姓名
							cv.put("Faculty", map.get("Faculty"));   //学院
							cv.put("Role", map.get("Role"));    //角色号
							cv.put("Pinyin", PinyinUtils.getPingYin(map.get("SName")).toLowerCase());    //姓名全拼,小写
							cv.put("Firstspell", PinyinUtils.getFirstSpell(map.get("SName")).toLowerCase());    //姓名简拼,小写
							a = db1.insert("student_tb", null, cv);    //插入到学生表中
						}
						db1.setTransactionSuccessful();       //设置事务处理成功，不设置会自动回滚不提交
						db1.endTransaction();       //处理完成
						db1.close();
						time=System.currentTimeMillis();  //1970年1月1日到现在的毫秒数
						SharedPreferences sp =getSharedPreferences("UpdateTime", MODE_PRIVATE);
						SharedPreferences.Editor editor=sp.edit();
						editor.putLong("GetTime", time);
						editor.commit();
						Log.i("Time", "更新完数据库==>" + time);
					}
				}.start();
			}
			Intent intent = new Intent();
			intent.putExtra("UserID", UserID);
			intent.putExtra("Role", Role);  //角色号
			intent.setClass(Main_Activity.this, AllRoom_Activity.class);//设置要跳转的activity
			startActivity(intent);//开始跳转
			overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);//设置activity切换效果
			finish();
		}

	}
}
