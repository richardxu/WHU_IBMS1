package activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import convert.Converts;
import views.InputBox;
import views.Switch;
import views.Switch.OnSwitchChangedListener;
import Adapter.SectionListAdapter;
import Adapter.TouchListener;
import WebServices.soap;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.suntrans.whu.R;

import database.DbHelper;

public class Search_Activity extends Activity {
	private InputBox inputbox;    //搜索输入框
	private TextView cancel;  //取消
	private ListView list;   //列表
	private Spinner spinner;  //下拉菜单
	private LinearLayout layout1,layout2;  //无搜索结果。加载中...
	private String[] search_item={"所有","宿舍号","学号","姓名"};
	private String Role;       //角色号
	private String UserID;  //管理员账号
	private ArrayList<Map<String,String>> datalist_userinfo=new ArrayList<Map<String,String>>();  //管理者信息
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		requestWindowFeature(Window.FEATURE_NO_TITLE);  //去标题栏
		setContentView(R.layout.search);      //设置activity布局文件
		Intent intent=getIntent();
		Role=intent.getStringExtra("Role");
		UserID = intent.getStringExtra("UserID");
		datalist_userinfo=(ArrayList<Map<String, String>>)intent.getSerializableExtra("datalist_userinfo");
		inputbox=(InputBox)findViewById(R.id.inputbox);  //输入框
		cancel=(TextView)findViewById(R.id.cancel);  //取消
		list=(ListView)findViewById(R.id.list);    //显示搜索结果的列表
		spinner=(Spinner)findViewById(R.id.spinner);    //查询条件的下拉菜单
		layout1=(LinearLayout)findViewById(R.id.layout1);    //显示无搜索结果
		layout2=(LinearLayout)findViewById(R.id.layout2);    //显示加载中...
		spinner.setOnTouchListener(new TouchListener());  //是指点击效果
		cancel.setOnTouchListener(new TouchListener());  //设置点击效果	
		cancel.setOnClickListener(new OnClickListener(){  //设置点击监听  ,关闭当前页面
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}});
		//为下拉菜单设置适配器
		spinner.setAdapter(new ArrayAdapter<String>(Search_Activity.this,R.layout.spinner_item,search_item){
            @Override
            public View getDropDownView(int position, View convertView,ViewGroup parent) {
                View view = getLayoutInflater().inflate(R.layout.spinner_dropdown_item, parent, false); 
                TextView label = (TextView) view.findViewById(R.id.text1);
                label.setText(getItem(position)); 
                return view;
            }
        });
		//为下拉菜单设置选中监听
		spinner.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				//layout1.setVisibility(View.GONE);
				//layout2.setVisibility(View.VISIBLE);
				ListInit();    //更新列表显示搜索结果
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}});
		inputbox.addTextChangedListener(new TextWatcher(){  //设置输入框文字改变监听
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub				
			}
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub				
			}
			@Override
			public void afterTextChanged(Editable s) {   //字体改变后
				// TODO Auto-generated method stub
				layout1.setVisibility(View.GONE);
				layout2.setVisibility(View.VISIBLE);
				ListInit();    //更新列表显示搜索结果
				//Toast.makeText(getApplication(), inputbox.getText().toString(), Toast.LENGTH_SHORT).show();
			}});
		layout1.setVisibility(View.GONE);
		layout2.setVisibility(View.GONE);
		Init();         //初始化显示内容
		//设置通知栏半透明
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			Converts.setTranslucentStatus(Search_Activity.this,true);
		}
	}
	
	public void Init(){    //初始化函数,可以对所有宿舍和学生进行搜索
		
		DbHelper dh1=new DbHelper(Search_Activity.this,"IBMS",null,2);
		final SQLiteDatabase db = dh1.getReadableDatabase();
		Cursor cursor = db.query(true,"search_tb", new String[]{"Name"},null, null, null, null, "SID desc", null);
		//Toast.makeText(getApplication(), cursor.getCount()+"tiao", Toast.LENGTH_LONG).show();
		if(cursor.getCount()<1)//如果没有最近搜索记录，就显示“无搜索结果”
		{
			layout1.setVisibility(View.VISIBLE);
			layout2.setVisibility(View.GONE);
			//Toast.makeText(getApplication(), cursor.getCount()+"tiao", Toast.LENGTH_LONG).show();
		}
		else   //否则，就显示最近搜索记录
		{			
			SectionListAdapter adapter = new SectionListAdapter(Search_Activity.this);  //实例化一个SectionListAdapter
			final ArrayList<Map<String,String>> data=new ArrayList<Map<String,String>>();	
			int count=0;
			while(cursor.moveToNext())   //遍历查询出最近的记录
			{
				Map<String,String> map=new HashMap<String,String>();
				map.put("Name", cursor.getString(0));
				data.add(map);
				count++;
				if(count>=20)
					break;
			}			
			Map<String,String> map1=new HashMap<String,String>();
			map1.put("Name", "清空搜索记录");
			data.add(map1);
			adapter.addSection("最近搜索", new BaseAdapter(){
					@Override
					public int getCount() {
						// TODO Auto-generated 
						return data.size();
					}
					@Override
					public Object getItem(int position) {
						// TODO Auto-generated method 
						return data.get(position);
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
							convertView = LayoutInflater.from(getApplication()).inflate(R.layout.listsearch_record, null);  
						TextView name=(TextView)convertView.findViewById(R.id.name);	
						final Map<String,String> map=data.get(position);			
						name.setText(map.get("Name"));
						if(position==data.size()-1)   //清空搜索历史，格式设置
						{
							name.setTextColor(Color.BLUE);  //设置字体颜色
							name.setTextSize(16);           //设置字体大小
						}
						//选项选择监听
						convertView.setOnClickListener(new OnClickListener(){
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								if(position==data.size()-1)   //如果点击的是清空搜索记录
								{
									final AlertDialog.Builder builder = new AlertDialog.Builder(Search_Activity.this); 
									 builder.setTitle("确定要清空搜索记录?"); 							  
									 builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {  
									         public void onClick(DialogInterface dialog, int whichButton) {
									        	String sql="delete from search_tb; " ;        //清空搜索表	
									        	db.execSQL(sql);  
									        	sql=" delete from sqlite_sequence where name='search_tb'; ";  //自增列置0
									        	db.execSQL(sql);
									        	Init();
									         }
											 });
									 builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {  
									         public void onClick(DialogInterface dialog, int whichButton) {  
									        	
									         }  
									     });  
									    builder.create().show();
								}
								else     //点击浏览历史中的一项，将搜索栏内容设置为这一项
									inputbox.setText(map.get("Name"));			
							}});
						return convertView;
					}});
				list.setAdapter(adapter);
				layout1.setVisibility(View.GONE);
			    layout2.setVisibility(View.GONE);
			}
		   
	  }//初始化结束
	
      public void ListInit(){   //列表初始化，显示搜索结果
    	  DbHelper dh1=new DbHelper(Search_Activity.this,"IBMS",null,2);
  		  final SQLiteDatabase db = dh1.getReadableDatabase();
  		  Cursor cursor;
  		  String content=inputbox.getText().toString();   //要搜索的内容
  		  SectionListAdapter adapter = new SectionListAdapter(Search_Activity.this);  //实例化一个SectionListAdapter
  		  final ArrayList<Map<String,String>> data_room=new ArrayList<Map<String,String>>();   //数据源  	，搜索结果的房间
  		  final ArrayList<Map<String,String>> data_stu=new ArrayList<Map<String,String>>();   //数据源  	，搜索结果的学生
 		     	 
  		  if(content.equals(""))   //如果搜索框内容为空，则显示无结果
  		  {
  		    	layout1.setVisibility(View.GONE);
  				layout2.setVisibility(View.GONE);
  				Init();
  		  }
  		  else   //否则根据搜索文字进行搜索和显示
  		  {
	  		  switch(spinner.getSelectedItemPosition())   //根据下拉菜单选中项进行查询和显示
	    	  {
	    	  	case 0:   //所有
	    	  	{
	    	  		if(Role.equals("5"))      //楼长   ,先从房间表里选
	    	  			cursor=db.query("room_tb", new String[]{"RoomID","Area","Building","Unit","RoomNum","RoomType"}, "RoomNum like ? and Area=? and Building=?", new String[]{"%"+content+"%",datalist_userinfo.get(0).get("Area"),datalist_userinfo.get(0).get("Building")}, null, null, null);
	    	  		else if(Role.equals("6"))    //宿管
	    	  			cursor=db.query("room_tb", new String[]{"RoomID","Area","Building","Unit","RoomNum","RoomType"}, "RoomNum like ? and Area=? and Building=? and Unit=?", new String[]{"%"+content+"%",datalist_userinfo.get(0).get("Area"),datalist_userinfo.get(0).get("Building"),datalist_userinfo.get(0).get("Unit").replace("anyType{}", "")}, null, null, null);
	    	  		else	      //中心管理员或其他可以查看全校宿舍的管理员	  			
	    	  			cursor=db.query("room_tb", new String[]{"RoomID","Area","Building","Unit","RoomNum","RoomType"}, "RoomNum like ?", new String[]{"%"+content+"%"}, null, null, null);
	    	  		if(cursor.getCount()>=1)
	    	  		{
	    	  			while(cursor.moveToNext())
	    	  			{
	    	  				Map<String,String> map=new HashMap<String,String>();
		    	  			map.put("Type", "Room");     //类型，是房间
		    	  			map.put("RoomID", cursor.getString(0));   
	 	    	  			map.put("Name",cursor.getString(4));   //房间号
	 	    	  			map.put("StudentID", "");
							map.put("RoomType", cursor.getString(5));  //房间类型
							map.put("RoomInfo",cursor.getString(1)+"-"+cursor.getString(2)+cursor.getString(3));
		    	  			data_room.add(map);
	    	  			}
	    	  		}
	    	  		if(Role.equals("5"))      //楼长  ，再从学生表里选
	    	  			cursor=db.query("student_tb", new String[]{"RoomID","Area","Building","Unit","RoomNum","StudentID","SName"}, "(SName like ? or Pinyin like ? or Firstspell like ? or StudentID like ?) and Area=? and Building=?", new String[]{"%"+content+"%","%"+content+"%","%"+content+"%","%"+content+"%",datalist_userinfo.get(0).get("Area"),datalist_userinfo.get(0).get("Building")}, null, null, null);
	    	  		else if(Role.equals("6"))    //宿管
	    	  			cursor=db.query("student_tb", new String[]{"RoomID","Area","Building","Unit","RoomNum","StudentID","SName"}, "(SName like ? or Pinyin like ? or Firstspell like ? or StudentID like ?) and Area=? and Building=? and Unit=?", new String[]{"%"+content+"%","%"+content+"%","%"+content+"%","%"+content+"%",datalist_userinfo.get(0).get("Area"),datalist_userinfo.get(0).get("Building"),datalist_userinfo.get(0).get("Unit").replace("anyType{}", "")}, null, null, null);
	    	  	    else	      //中心管理员或其他可以查看全校宿舍的管理员
	    	  			cursor=db.query("student_tb", new String[]{"RoomID","Area","Building","Unit","RoomNum","StudentID","SName"}, "SName like ? or Pinyin like ? or Firstspell like ? or StudentID like ?", new String[]{"%"+content+"%","%"+content+"%","%"+content+"%","%"+content+"%"}, null, null, null);
	    	  		if(cursor.getCount()>=1)
	    	  		{
	    	  			while(cursor.moveToNext())
	    	  			{
	    	  				Map<String,String> map=new HashMap<String,String>();
		    	  			map.put("Type", "Stu");     //类型，是学生
		    	  			map.put("RoomID", cursor.getString(0));   
	 	    	  			map.put("Name",cursor.getString(6));   //学生姓名
	 	    	  			map.put("StudentID", cursor.getString(5));  //学号
		    	  			map.put("RoomInfo",cursor.getString(1)+"-"+cursor.getString(2)+cursor.getString(3)+"-"+cursor.getString(4));
		    	  			data_stu.add(map);
	    	  			}
	    	  		}
	    	  		break;
	    	  	}
	    	  	case 1:    //宿舍号
	    	  	{
	    	  		if(Role.equals("5"))      //楼长
	    	  			cursor=db.query("room_tb", new String[]{"RoomID","Area","Building","Unit","RoomNum","RoomType"}, "RoomNum like ? and Area=? and Building=?", new String[]{"%"+content+"%",datalist_userinfo.get(0).get("Area"),datalist_userinfo.get(0).get("Building")}, null, null, null);
	    	  		else if(Role.equals("6"))    //宿管
	    	  			cursor=db.query("room_tb", new String[]{"RoomID","Area","Building","Unit","RoomNum","RoomType"}, "RoomNum like ? and Area=? and Building=? and Unit=?", new String[]{"%"+content+"%",datalist_userinfo.get(0).get("Area"),datalist_userinfo.get(0).get("Building"),datalist_userinfo.get(0).get("Unit").replace("anyType{}", "")}, null, null, null);
	    	  		else	      //中心管理员或其他可以查看全校宿舍的管理员	  			
	    	  			cursor=db.query("room_tb", new String[]{"RoomID","Area","Building","Unit","RoomNum","RoomType"}, "RoomNum like ?", new String[]{"%"+content+"%"}, null, null, null);
	    	  		if(cursor.getCount()>=1)
	    	  		{
	    	  			while(cursor.moveToNext())
	    	  			{
	    	  				Map<String,String> map=new HashMap<String,String>();
		    	  			map.put("Type", "Room");     //类型，是房间
		    	  			map.put("RoomID", cursor.getString(0));   
	 	    	  			map.put("Name",cursor.getString(4));   //房间号
	 	    	  			map.put("StudentID", "");
							map.put("RoomType",cursor.getString(5));   //房间类型
		    	  			map.put("RoomInfo",cursor.getString(1)+"-"+cursor.getString(2)+cursor.getString(3));
		    	  			data_room.add(map);
	    	  			}
	    	  		}
	    	  		break;
	    	  	}
	    	  	case 2:    //学号
	    	  	{
	    	  		if(Role.equals("5"))      //楼长
	    	  			cursor=db.query("student_tb", new String[]{"RoomID","Area","Building","Unit","RoomNum","StudentID","SName"}, " StudentID like ? and Area=? and Building=?", new String[]{"%"+content+"%",datalist_userinfo.get(0).get("Area"),datalist_userinfo.get(0).get("Building")}, null, null, null);
	    	  		else if(Role.equals("6"))    //宿管
	    	  			cursor=db.query("student_tb", new String[]{"RoomID","Area","Building","Unit","RoomNum","StudentID","SName"}, " StudentID like ? and Area=? and Building=? and Unit=?", new String[]{"%"+content+"%",datalist_userinfo.get(0).get("Area"),datalist_userinfo.get(0).get("Building"),datalist_userinfo.get(0).get("Unit").replace("anyType{}", "")}, null, null, null);
	    	  	    else	      //中心管理员或其他可以查看全校宿舍的管理员
	    	  			cursor=db.query("student_tb", new String[]{"RoomID","Area","Building","Unit","RoomNum","StudentID","SName"}, " StudentID like ?", new String[]{"%"+content+"%"}, null, null, null);
	    	  		if(cursor.getCount()>=1)
	    	  		{
	    	  			while(cursor.moveToNext())
	    	  			{
	    	  				Map<String,String> map=new HashMap<String,String>();
		    	  			map.put("Type", "Stu");     //类型，是学生
		    	  			map.put("RoomID", cursor.getString(0));   
	 	    	  			map.put("Name",cursor.getString(6));   //学生姓名
	 	    	  			map.put("StudentID", cursor.getString(5));  //学号
		    	  			map.put("RoomInfo",cursor.getString(1)+"-"+cursor.getString(2)+cursor.getString(3)+"-"+cursor.getString(4));
		    	  			data_stu.add(map);
	    	  			}
	    	  		}
	    	  		break;
	    	  	}
	    	  	case 3:     //姓名
	    	  	{
	    	  		if(Role.equals("5"))      //楼长
	    	  			cursor=db.query("student_tb", new String[]{"RoomID","Area","Building","Unit","RoomNum","StudentID","SName"}, "(SName like ? or Pinyin like ? or Firstspell like ?) and Area=? and Building=?", new String[]{"%"+content+"%","%"+content+"%","%"+content+"%",datalist_userinfo.get(0).get("Area"),datalist_userinfo.get(0).get("Building")}, null, null, null);
	    	  		else if(Role.equals("6"))    //宿管
	    	  			cursor=db.query("student_tb", new String[]{"RoomID","Area","Building","Unit","RoomNum","StudentID","SName"}, "(SName like ? or Pinyin like ? or Firstspell like ?) and Area=? and Building=? and Unit=?", new String[]{"%"+content+"%","%"+content+"%","%"+content+"%",datalist_userinfo.get(0).get("Area"),datalist_userinfo.get(0).get("Building"),datalist_userinfo.get(0).get("Unit").replace("anyType{}", "")}, null, null, null);
	    	  	    else	      //中心管理员或其他可以查看全校宿舍的管理员
	    	  			cursor=db.query("student_tb", new String[]{"RoomID","Area","Building","Unit","RoomNum","StudentID","SName"}, "SName like ? or Pinyin like ? or Firstspell like ? ", new String[]{"%"+content+"%","%"+content+"%","%"+content+"%",}, null, null, null);
	    	  		if(cursor.getCount()>=1)
	    	  		{
	    	  			while(cursor.moveToNext())
	    	  			{
	    	  				Map<String,String> map=new HashMap<String,String>();
		    	  			map.put("Type", "Stu");     //类型，是学生
		    	  			map.put("RoomID", cursor.getString(0));   
	 	    	  			map.put("Name",cursor.getString(6));   //学生姓名
	 	    	  			map.put("StudentID", cursor.getString(5));  //学号
		    	  			map.put("RoomInfo",cursor.getString(1)+"-"+cursor.getString(2)+cursor.getString(3)+"-"+cursor.getString(4));
		    	  			data_stu.add(map);
	    	  			}
	    	  		}
	    	  		break;
	    	  	}
	    	  	default:break;
	    	  }
	  		  if(data_room.size()>0)
	  			  adapter.addSection("房间", new BaseAdapter(){
					@Override
					public int getCount() {
						// TODO Auto-generated 
						return data_room.size();
					}
					@Override
					public Object getItem(int position) {
						// TODO Auto-generated method 
						return data_room.get(position);
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
							convertView = LayoutInflater.from(getApplication()).inflate(R.layout.listsearch_dorm, null);  
						TextView name=(TextView)convertView.findViewById(R.id.name);
						TextView studentid=(TextView)convertView.findViewById(R.id.studentid);
						TextView roominfo=(TextView)convertView.findViewById(R.id.roominfo);
						final Map<String,String> map=data_room.get(position);			
						name.setText(map.get("Name"));
						studentid.setText(map.get("StudentID"));
						roominfo.setText(map.get("RoomInfo"));
						String RoomType = map.get("RoomType");
						//选项选择监听
						convertView.setOnClickListener(new OnClickListener(){
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								//Cursor cursor=db.query("student_tb", new String[]{"StudentID"}, "RoomID=?", new String[]{map.get("RoomID")}, null, null, null);
//								if(cursor.getCount()>0)//如果学号存在就跳转
//									while(cursor.moveToNext())
//									{
										DbHelper dh2=new DbHelper(Search_Activity.this,"IBMS",null,2);
										SQLiteDatabase db1 = dh2.getReadableDatabase();
										ContentValues cv=new ContentValues();
										cv.put("Name", inputbox.getText().toString());
										long a=db1.insert("search_tb", null, cv);
										db1.close();
										Intent intent=new Intent();				        		
						        		//intent.putExtra("StudentID", cursor.getString(0));       //学号
						        		intent.putExtra("Role", Role);       //角色号
										intent.putExtra("RoomInfo",map.get("RoomInfo")+"-"+map.get("Name"));
										intent.putExtra("RoomType", map.get("RoomType"));
										intent.putExtra("RoomID", map.get("RoomID"));
										intent.putExtra("UserID", UserID);
										intent.setClass(Search_Activity.this, Dorm_Activity.class);//设置要跳转的activity
										startActivity(intent);//开始跳转	
										overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);//设置activity切换效果	
										finish();
									//	break;
									//}
							}});
						return convertView;
					}});
	  		  if(data_stu.size()>0)
	  			  adapter.addSection("学生", new BaseAdapter(){
					@Override
					public int getCount() {
						// TODO Auto-generated 
						return data_stu.size();
					}
					@Override
					public Object getItem(int position) {
						// TODO Auto-generated method 
						return data_stu.get(position);
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
							convertView = LayoutInflater.from(getApplication()).inflate(R.layout.listsearch_stu, null);  
						TextView name=(TextView)convertView.findViewById(R.id.name);
						TextView studentid=(TextView)convertView.findViewById(R.id.studentid);
						TextView roominfo=(TextView)convertView.findViewById(R.id.roominfo);
						final Map<String,String> map=data_stu.get(position);			
						name.setText(map.get("Name"));
						studentid.setText(map.get("StudentID"));
						roominfo.setText(map.get("RoomInfo"));
						//选项选择监听
						convertView.setOnClickListener(new OnClickListener(){
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								DbHelper dh2=new DbHelper(Search_Activity.this,"IBMS",null,2);
								SQLiteDatabase db1 = dh2.getReadableDatabase();
								ContentValues cv=new ContentValues();
								cv.put("Name", inputbox.getText().toString());
								long a=db1.insert("search_tb", null, cv);
								db1.close();
								Intent intent=new Intent();
								intent.putExtra("Name", map.get("Name"));       //学生姓名
					        	intent.putExtra("RoomID", map.get("RoomID"));       //宿舍ID
					        	intent.putExtra("RoomInfo", map.get("RoomInfo"));   //宿舍信息
								intent.setClass(Search_Activity.this, Stu_Activity.class);//设置要跳转的activity
								startActivity(intent);//开始跳转	
								overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);//设置activity切换效果	
								finish();
							}});
						return convertView;
					}});
	  		    if(data_room.size()==0&&data_stu.size()==0)  //如果没有搜索结果，就显示搜索记录
	  		    {
	  		    	Init();
	  		    }
	  		    else     //如果有搜索结果，就显示搜索结果列表
	  		    {
	  		    	list.setAdapter(adapter);		
	  		    }
				layout1.setVisibility(View.GONE);
				layout2.setVisibility(View.GONE);
				if(data_room.size()==0&&data_stu.size()==0&&(!(inputbox.getText().toString().equals(""))))   //如果学生和宿舍两个列表中都没有数据，就显示无搜索结果
				{
					layout1.setVisibility(View.VISIBLE);
					layout2.setVisibility(View.GONE);
				}
  		  }
    	 db.close();
      }
}
