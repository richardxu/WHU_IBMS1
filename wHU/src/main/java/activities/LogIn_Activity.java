package activities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.suntrans.whu.R;

import convert.PinyinUtils;

import database.DataCentre;
import database.DbHelper;

import WebServices.soap;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class LogIn_Activity extends Activity implements Serializable{
	private Button login;              //登录按钮
	private EditText name;             //账号
	private EditText password;         //密码
	private CheckBox remember,auto;   //记住密码和自动登录
	private String Role="0";   			   //角色号
	private String UserID="";      //账号
	private String Password="";       //密码
	private ArrayList<Map<String,String>> datalist_room=new ArrayList<Map<String,String>>();   //所有房间
	private ArrayList<Map<String,String>> datalist_stu=new ArrayList<Map<String,String>>();   //所有学生

	private Handler handler1=new Handler(){
        public void handleMessage(Message msg) 
        {
            super.handleMessage(msg);
            if(msg.what==1)            //登录成功            	
            { 
            	//先将数据存入数据库，然后根据角色号跳转到主页面
            	DbHelper dh1=new DbHelper(LogIn_Activity.this,"IBMS",null,2);
				SQLiteDatabase db = dh1.getWritableDatabase(); 
				Cursor cursor = db.query("users_tb", new String[]{"Password"},"Name=?", new String[]{name.getText().toString()}, null, null, null);
				if(cursor.getCount()<1)   //如果数据库中不存在此用户名，则向用户表中添加这个账号
				{
					ContentValues cv = new ContentValues();    //内容数组	
					cv.put("Name", name.getText().toString());   //用户名
					cv.put("Password",password.getText().toString());   //密码
					cv.put("IsUsing","1");   //是否正在使用
					cv.put("Auto",auto.isChecked()?"1":"0");   //是否自动登录 
					cv.put("Remember",remember.isChecked()?"1":"0");   //是否记住密码
					ContentValues cv1 = new ContentValues();    //内容数组	
					cv1.put("IsUsing","0");   //是否正在使用
					db.update("users_tb",cv1,null,null);    //更新数据库数据，先把所有的IsUsing置0，然后将正在登录的账号的IsUsing置1
					long a=db.insert("users_tb", null, cv);    //插入到表中					
				}
				else
				{
					while(cursor.moveToNext())      //如果存在，就更新数据库
					{															
						ContentValues cv = new ContentValues();    //内容数组	
						cv.put("Password",password.getText().toString());   //更新密码
						cv.put("IsUsing","1");   //是否正在使用
						cv.put("Auto",auto.isChecked()?"1":"0");   //是否自动登录 
						cv.put("Remember",remember.isChecked()?"1":"0");   //是否记住密码
						ContentValues cv1 = new ContentValues();    //内容数组	
						cv1.put("IsUsing","0");   //是否正在使用
						db.update("users_tb",cv1,null,null);    //更新数据库数据，先把所有的IsUsing置0，然后将正在登录的账号的IsUsing置1
						db.update("users_tb",cv,"Name=?",new String[]{name.getText().toString()});   //更新用户表数据									
					}
				}
	            if(Role.equals("1")||Role.equals("2"))     //用户是学生
	            {	            		    
	            	new Thread(){
	            		public void run(){
	            			DbHelper dh2=new DbHelper(LogIn_Activity.this,"IBMS",null,2);
	        	    		SQLiteDatabase db1 = dh2.getWritableDatabase(); 
			        		long a=0;
			        		Map<String,String> map=new HashMap<String,String>();
			        		db1.beginTransaction();       //手动设置开始事务   ，这样只打开一次数据库，一次性将数据写入,防止多次打开和关闭数据库，节省时间  	
			        		String sql="delete from room_tb; " ;        //清空宿舍表中的数据	      		
			        		db1.execSQL(sql);  
			        		sql=" delete from sqlite_sequence where name='room_tb'; ";  //自增列置0
			        		db1.execSQL(sql);
			        		sql=" delete from student_tb;";   //清空学生表中的数据
			        		db1.execSQL(sql);
			        		sql=" update sqlite_sequence set seq=0 where name='student_tb';";  //自增列置0
			        		db1.execSQL(sql);					        		
			        		db1.setTransactionSuccessful();       //设置事务处理成功，不设置会自动回滚不提交
			    			db1.endTransaction();       //处理完成
			    			db1.close();					    			
	            		}
	            	}.start();
	            	Intent intent=new Intent();
	            	intent.putExtra("UserID",UserID);  //格式需求，不会用到
	            	intent.putExtra("StudentID", UserID);   //学号
	            	intent.putExtra("Role", Role);    //学生角色号，1代表用电账户管理员，2代表普通用户
	            	intent.setClass(LogIn_Activity.this, Dorm_Activity.class);//设置要跳转的activity
	            	startActivity(intent);//开始跳转
	            	overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);//设置activity切换效果
	            	finish();
	            }
	            else if(Role.equals("0"))
	            {
	            	Toast.makeText(getApplicationContext(), "验证失败！", Toast.LENGTH_SHORT).show();
	            	login.setClickable(true);
	            }
	            else    //用户是楼长、宿管、中心管理员等
	            {
	            	if(DataCentre.datalist_room.size()>0&&DataCentre.datalist_stu.size()>0)
	            	{
		            	new Thread(){
		            		public void run(){
		            			DbHelper dh2=new DbHelper(LogIn_Activity.this,"IBMS",null,2);
		        	    		SQLiteDatabase db1 = dh2.getWritableDatabase(); 
				        		long a=0;
				        		Map<String,String> map=new HashMap<String,String>();
				        		db1.beginTransaction();       //手动设置开始事务   ，这样只打开一次数据库，一次性将数据写入,防止多次打开和关闭数据库，节省时间  	
				        		String sql="delete from room_tb; " ;        //清空宿舍表中的数据	      		
				        		db1.execSQL(sql);  
				        		sql=" delete from sqlite_sequence where name='room_tb'; ";  //自增列置0
				        		db1.execSQL(sql);
				        		sql=" delete from student_tb;";   //清空学生表中的数据
				        		db1.execSQL(sql);
				        		sql=" update sqlite_sequence set seq=0 where name='student_tb';";  //自增列置0
				        		db1.execSQL(sql);
				        		for(int k=0;k<DataCentre.datalist_room.size();k++)
				        		{        					
				        			map=DataCentre.datalist_room.get(k);
				        			ContentValues cv = new ContentValues();    //内容数组	
									cv.put("RoomID", map.get("RoomID"));   //宿舍ID
									cv.put("Area",map.get("Area"));   //园区
									cv.put("Building",map.get("Building"));   //楼栋
									cv.put("Unit",map.get("Unit"));   //单元 
									cv.put("Floor",map.get("Floor"));   //楼层
									cv.put("RoomNum",map.get("RoomNum"));   //宿舍号     
									a=db1.insert("room_tb", null, cv);    //插入到宿舍表中	
									
				        		}
				        		for(int k=0;k<DataCentre.datalist_stu.size();k++)
				        		{
				        			map=DataCentre.datalist_stu.get(k);
				        			ContentValues cv = new ContentValues();    //内容数组	
									cv.put("RoomID", map.get("RoomID"));   //宿舍ID
									cv.put("Area",map.get("Area"));   //园区
									cv.put("Building",map.get("Building"));   //楼栋
									cv.put("Unit",map.get("Unit"));   //单元 
									cv.put("Floor",map.get("Floor"));   //楼层
									cv.put("RoomNum",map.get("RoomNum"));   //宿舍号    
									cv.put("StudentID",map.get("StudentID"));   //学生 
									cv.put("SName",map.get("SName"));   //姓名
									cv.put("Faculty",map.get("Faculty"));   //学院
									cv.put("Role", map.get("Role"));    //角色号					
									cv.put("Pinyin", PinyinUtils.getPingYin(map.get("SName")).toLowerCase());    //姓名全拼,小写
									cv.put("Firstspell", PinyinUtils.getFirstSpell(map.get("SName")).toLowerCase());    //姓名简拼,小写
									a=db1.insert("student_tb", null, cv);    //插入到学生表中	
				        		}
				        		db1.setTransactionSuccessful();       //设置事务处理成功，不设置会自动回滚不提交
				    			db1.endTransaction();       //处理完成
				    			db1.close();				    		
		            		}
		            	}.start();
	            	}
	            	Intent intent=new Intent();
	            	intent.putExtra("UserID", UserID);
	            	intent.putExtra("Role", Role);  //角色号
	            	intent.setClass(LogIn_Activity.this, All_Activity.class);//设置要跳转的activity
	            	startActivity(intent);//开始跳转
	            	overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);//设置activity切换效果
	            	finish();
	            }
            }
            else if(msg.what==0)
            {
            	login.setClickable(true);
            	Toast.makeText(getApplicationContext(), "网络错误！", Toast.LENGTH_SHORT).show();
            }
        }
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);     //设置布局文件
		login=(Button)findViewById(R.id.login);
		name=(EditText)findViewById(R.id.name);
		password=(EditText)findViewById(R.id.password);	
		remember=(CheckBox)findViewById(R.id.remember);   //记住密码
		auto=(CheckBox)findViewById(R.id.auto);      //自动登录
		remember.setChecked(true);
		auto.setChecked(true);
		//从用户表中查找账号和密码信息
		DbHelper dh1=new DbHelper(LogIn_Activity.this,"IBMS",null,2);
		SQLiteDatabase db = dh1.getWritableDatabase(); 
		Cursor cursor = db.query(true, "users_tb", new String[]{"NID","Name","Password","IsUsing","Remember","Auto"}, null, null, null, null, null, null, null);
		while(cursor.moveToNext())
		{
			if(cursor.getString(3).equals("1"))
			{
				if(cursor.getString(4).equals("1"))   //如果之前选择记住密码，则账号和密码都显示
				{
					name.setText(cursor.getString(1));					
					password.setText(cursor.getString(2));
					
				} 
				else                //如果没有记住密码，只显示账号，用户去输入密码
				{
					name.setText(cursor.getString(1));
					password.requestFocus();    //输入密码获取焦点
				}
				remember.setChecked(cursor.getString(4).equals("1")?true:false);
				auto.setChecked(cursor.getString(5).equals("1")?true:false);
			}
			
		}
		//为登录按钮绑定点击事件，执行登录操作
		login.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				// TODO Auto-generated method stub
				if(name.getText().toString().equals("")||name.getText()==null)
				{
					Toast.makeText(getApplicationContext(),"账号不能为空！",Toast.LENGTH_SHORT).show();
				}
				else if(password.getText().toString().equals("")||password.getText()==null)
				{
					Toast.makeText(getApplicationContext(),"密码不能为空！",Toast.LENGTH_SHORT).show();
				}
				else
				{
					login.setClickable(false);    //设置登录按钮不能点击
					Toast.makeText(getApplicationContext(),"正在登录中。。。",Toast.LENGTH_SHORT).show();
					new Thread(){     //新建线程访问WebServices接口，进行验证
						@Override
						public void run(){
							try{
								UserID=name.getText().toString();								
								Role=soap.LogIn(name.getText().toString(), password.getText().toString());
								Message msg=new Message();
								msg.what=1;
								handler1.sendMessage(msg);
							}
							catch(Exception e){
								Message msg=new Message();
								msg.what=0;
								handler1.sendMessage(msg);
							}
						}
					}.start();
				}
					
			}});
	}


}
