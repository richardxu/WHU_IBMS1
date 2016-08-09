package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
	private final static String DATABASE_NAME = "BOOKS.db";
	private final static int DATABASE_VERSION = 2;    //数据库的版本号
	private final static String TABLE_NAME = "books_table";
	public final static String BOOK_ID = "book_id";
	public final static String BOOK_NAME = "book_name";
	public final static String BOOK_AUTHOR = "book_author";
	public DbHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub      INTEGER primary key autoincrement自增长
		String sql1 = " create table  users_tb ( NID INTEGER primary key autoincrement,"  //用户名ID 唯一标示通道 ，自增长
				+ "Name TEXT,"   //用户姓名
				+ "Password TEXT,"//用户密码 
				+ "IsUsing TEXT,"//是否正在使用,1表示正在使用，0表示没有在使用 
				+ "Auto TEXT,"   //是否自动登录，1表示自动登录，0表示不自动登录 
				+ "Remember TEXT);" ;  //是否记住密码，1表示记住密码，0表示不记住密码 
		db.execSQL(sql1);    //创建用户表
		
		String sql2 = " create table  room_tb ( RID INTEGER primary key autoincrement,"  //房间ID 唯一标示通道 ，自增长
				+ "RoomID TEXT,"   //房间ID
				+ "Area TEXT,"	//园区
				+ "Building TEXT,"//楼栋 
				+ "Unit TEXT,"   //单元 
				+ "Floor TEXT,"   //楼层
				+ "RoomNum TEXT,"    //宿舍号
				+ "RoomType TEXT);" ;  //宿舍类型
		db.execSQL(sql2);    //创建房间表
		
		String sql3 = " create table  student_tb ( SID INTEGER primary key autoincrement,"  //房间ID 唯一标示通道 ，自增长
				+ "RoomID TEXT,"   //房间ID
				+ "Area TEXT,"	//园区
				+ "Building TEXT,"//楼栋 
				+ "Unit TEXT,"   //单元 
				+ "Floor TEXT,"   //楼层
				+ "RoomNum TEXT,"    //宿舍号
				+ "StudentID TEXT,"    //学号
				+ "SName TEXT,"      //学生名字
				+ "Faculty TEXT,"   //学院
				+ "Role TEXT,"   //角色号 
				+ "Pinyin TEXT,"   //姓名全拼 
				+ "Firstspell TEXT);" ;   //姓名简拼
		db.execSQL(sql3);    //创建学生表	
		
		String sql4 = " create table  search_tb ( SID INTEGER primary key autoincrement,"  //房间ID 唯一标示通道 ，自增长
				+ "Name TEXT);"  ; //搜索内容				
		db.execSQL(sql4);    //创建搜索记录表	
	}

	@Override    //数据库版本更新的时候调用，将version改为更大值，就会触发这个函数
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		if(oldVersion==1)   //如果旧版本数据库版本号是1，那么向学生表中添加Pinyin（姓名全拼）和Firstspell（姓名简拼）两个字段
		{
			String sql1="alter table student_tb add Pinyin TEXT";    //向学生表中添加字段,全拼
			String sql2="alter table student_tb add Firstspell TEXT";    //简拼
			db.execSQL(sql1);
			db.execSQL(sql2);
		}
	}
	//查询操作
	public Cursor select() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query("switch_tb", null, null, null, null, null, null);
		return cursor;
	}
	//增加操作
	public long insert(String bookname,String author)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		/* ContentValues */
		ContentValues cv = new ContentValues();
		cv.put(BOOK_NAME, bookname);
		cv.put(BOOK_AUTHOR, author);
		long row = db.insert(TABLE_NAME, null, cv);
		
		
		return row;
	}
	//删除操作
	public void delete(int id)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		String where = BOOK_ID + " = ?";
		String[] whereValue ={ Integer.toString(id) };
		db.delete(TABLE_NAME, where, whereValue);
	}
	//修改操作
	public void update(int id, String bookname,String author)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		String where = BOOK_ID + " = ?";
		String[] whereValue = { Integer.toString(id) };
		 
		ContentValues cv = new ContentValues();
		cv.put(BOOK_NAME, bookname);
		cv.put(BOOK_AUTHOR, author);
		db.update(TABLE_NAME, cv, where, whereValue);
	}
}
