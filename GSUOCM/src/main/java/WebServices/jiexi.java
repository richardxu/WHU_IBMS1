package WebServices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.ksoap2.serialization.SoapObject;

public class jiexi {

	//1.用于解析Inqurey_UserInfo请求数据时返回数据的静态方法    
	public static ArrayList<Map<String, String>> inquiry_userinfo(SoapObject result) {
		// TODO Auto-generated constructor stub
		ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();   //要返回的数据集合
		try
		{
			result = (SoapObject)result.getProperty(1); 
			if(result.getPropertyCount()>=1)
				result = (SoapObject)result.getProperty(0);  //有数据  则继续
			else 
				return data;     //无数据直接返回空
			//result = (SoapObject)result.getProperty(1);*/
			for(int i=0;  i< result.getPropertyCount(); i++ ){ 
				SoapObject soap = (SoapObject) result.getProperty(i);
			    Map<String, String> map=new HashMap<String,String>();
			    try{map.put("Role", soap.getProperty("Role").toString());}catch(Exception e){}      //角色号，1表示账户管理员,2表示普通学生用户
				//4表示中心管理员，5表示楼长，6表示宿管，9表示最高管理员，12假期值班组，13信息查看组
				data.add(map);
			}
		
		}
		catch(Exception e){}
		return data;
	}			
	//1.1用于解析Inqurey_UserInfo请求学生信息数据时，角色为学生时的返回数据的静态方法    
	public static ArrayList<Map<String, String>> inquiry_studentinfo(SoapObject result) {
		// TODO Auto-generated constructor stub
		ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();   //要返回的数据集合
		try
		{
			result = (SoapObject)result.getProperty(1); 
			if(result.getPropertyCount()>=1)
				result = (SoapObject)result.getProperty(0);  //有数据  则继续
			else 
				return data;     //无数据直接返回空
			//result = (SoapObject)result.getProperty(1);*/
			for(int i=0;  i< result.getPropertyCount(); i++ ){ 
				SoapObject soap = (SoapObject) result.getProperty(i);
			    Map<String, String> map=new HashMap<String,String>();
				try{map.put("StudentID", soap.getProperty("StudentID").toString());}catch(Exception e){}  //学号,与输入的学号相同
				try{map.put("SName", soap.getProperty("SName").toString());}catch(Exception e){}    //学生姓名
				try{map.put("Role", soap.getProperty("Role").toString());}catch(Exception e){}      //学生角色，1表示账户管理员,2表示普通学生用户
				try{map.put("RoomID", soap.getProperty("RoomID").toString());}catch(Exception e){}  //学生宿舍的RoomID
				try{map.put("ktPreChargeback", soap.getProperty("ktPreChargeback").toString());}catch(Exception e){} //空调余额，单位：元
				try{map.put("ktPreSubsidy", soap.getProperty("ktPreSubsidy").toString());}catch(Exception e){}      //空调剩余补贴，单位：元
				try{map.put("ktPrice", soap.getProperty("ktPrice").toString());}catch(Exception e){}      //空调用电单价,单位：元/度
				try{map.put("zmPreChargeback", soap.getProperty("zmPreChargeback").toString());}catch(Exception e){}  //照明余额，单价：元
				try{map.put("zmPreSubsidy", soap.getProperty("zmPreSubsidy").toString());}catch(Exception e){}        //照明剩余补贴，单价：元
				try{map.put("zmPrice", soap.getProperty("zmPrice").toString());}catch(Exception e){}       //照明用电单价，单位：元/度
				try{map.put("Area", soap.getProperty("Area").toString());}catch(Exception e){}          //宿舍园区
				try{map.put("Building", soap.getProperty("Building").toString());}catch(Exception e){}  //宿舍楼 栋
				try{map.put("Unit", soap.getProperty("Unit").toString().equals("anyType{}")?"":soap.getProperty("Unit").toString());}catch(Exception e){}            //宿舍单元,若没有单元，则返回值为anyType{}
				try{map.put("RoomNum", soap.getProperty("RoomNum").toString());}catch(Exception e){}    //宿舍号
				try{map.put("eleckt", soap.getProperty("eleckt").toString());}catch(Exception e){}     //空调剩余可用电量，单位：度
				try{map.put("eleczm", soap.getProperty("eleczm").toString());}catch(Exception e){}     //照明剩余可用电量，单位：度
				try{map.put("Subsidyzm", soap.getProperty("Subsidyzm").toString());}catch(Exception e){}//照明剩余补贴电量，单位：度
				try{map.put("Subsidykt", soap.getProperty("Subsidykt").toString());}catch(Exception e){}//空调剩余补贴电量，单位：度
				try{map.put("Faculty", soap.getProperty("Faculty").toString());}catch(Exception e){}    //学院
				try{map.put("Professional", soap.getProperty("Professional").toString());}catch(Exception e){} //专业
				
				data.add(map);
			}
		
		}
		catch(Exception e){}
		return data;
	}
	
	//1.2用于解析Inqurey_UserInfo请求用户信息数据时，角色为管理员时的返回数据的静态方法    
	public static ArrayList<Map<String, String>> inquiry_staffinfo(SoapObject result) {
		// TODO Auto-generated constructor stub
		ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();   //要返回的数据集合
		try
		{
			result = (SoapObject)result.getProperty(1); 
			if(result.getPropertyCount()>=1)
				result = (SoapObject)result.getProperty(0);  //有数据  则继续
			else 
				return data;     //无数据直接返回空
			
			for(int i=0;  i< result.getPropertyCount(); i++ ){ 
				SoapObject soap = (SoapObject) result.getProperty(i);
			    Map<String, String> map=new HashMap<String,String>();
				try{map.put("StaffID", soap.getProperty("StaffID").toString());}catch(Exception e){}  //职工号
				try{map.put("SName", soap.getProperty("SName").toString());}catch(Exception e){}    //职工姓名
				try{map.put("Role", soap.getProperty("Role").toString());}catch(Exception e){}      //职工角色，4表示中心管理员，5表示楼长，6表示宿管，9表示最高管理员，12假期值班组，13信息查看组
				try{map.put("Area", soap.getProperty("Area").toString());}catch(Exception e){} //园区
				try{map.put("Building", soap.getProperty("Building").toString());}catch(Exception e){}      //楼栋
				try{map.put("Unit", soap.getProperty("Unit").toString().replace("anyType{}", ""));}catch(Exception e){}      //单元
				try{map.put("Licensor", soap.getProperty("Licensor").toString());}catch(Exception e){}  //未知
				try{map.put("RoleName", soap.getProperty("RoleName").toString());}catch(Exception e){}        //角色名
				try{map.put("LName", soap.getProperty("LName").toString());}catch(Exception e){}       //未知
								
				data.add(map);
			}
		
		}
		catch(Exception e){}
		return data;
	}
	//1.3用于解析Inqurey_UserInfo请求用户信息数据时，角色为宿管时的返回数据的静态方法    
	public static ArrayList<Map<String, String>> inquiry_boomerinfo(SoapObject result) {
		// TODO Auto-generated constructor stub
		ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();   //要返回的数据集合
		try
		{
			result = (SoapObject)result.getProperty(1); 
			if(result.getPropertyCount()>=1)
				result = (SoapObject)result.getProperty(0);  //有数据  则继续
			else 
				return data;     //无数据直接返回空
			
			for(int i=0;  i< result.getPropertyCount(); i++ ){ 
				SoapObject soap = (SoapObject) result.getProperty(i);
			    Map<String, String> map=new HashMap<String,String>();
				try{map.put("UserID", soap.getProperty("UserID").toString());}catch(Exception e){}  //职工号
				try{map.put("BName", soap.getProperty("BName").toString());}catch(Exception e){}    //职工姓名
				try{map.put("Role", soap.getProperty("Role").toString());}catch(Exception e){}      //职工角色，4表示中心管理员，5表示楼长，6表示宿管，9表示最高管理员，12假期值班组，13信息查看组
				try{map.put("Area", soap.getProperty("Area").toString());}catch(Exception e){} //园区
				try{map.put("Building", soap.getProperty("Building").toString());}catch(Exception e){}      //楼栋
				try{map.put("Unit",soap.getProperty("Unit").toString().equals("anyType{}")?"":soap.getProperty("Unit").toString());}catch(Exception e){}      //单元
				try{map.put("StaffID", soap.getProperty("StaffID").toString());}catch(Exception e){}  				
								
				data.add(map);
			}
		
		}
		catch(Exception e){}
		return data;
	}		
		
	//2.用于解析Inquery_Channel_RoomID返回数据的静态方法
	public static ArrayList<Map<String, String>> inquiry_channel(SoapObject result) {
		// TODO Auto-generated constructor stub
		ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();
		try{
			result = (SoapObject)result.getProperty(1); 
			if(result.getPropertyCount()>=1)
				result = (SoapObject)result.getProperty(0);  //有数据  则继续
			else 
				return data;     //无数据直接返回空
		
			for(int i=0;  i< result.getPropertyCount(); i++ ){
				SoapObject soap = (SoapObject) result.getProperty(i);
			    Map<String, String> map=new HashMap<String,String>();
				try{map.put("GetTime", soap.getProperty("GetTime").toString());}catch(Exception e){}   //更新时间
				try{map.put("RoomID", soap.getProperty("RoomID").toString());}catch(Exception e){}     //房间RoomID
				try{map.put("Status", soap.getProperty("Status").toString());}catch(Exception e){}     //电表状态，0：正常；  1：恶性负载；  2：电表锁定；  3：等待
				try{map.put("State", soap.getProperty("State").toString());}catch(Exception e){}       //开关状态，0：关；   1：开
				try{map.put("U", soap.getProperty("U").toString());}catch(Exception e){}           //当前电压   ，单位：V
				try{map.put("I", soap.getProperty("I").toString());}catch(Exception e){}           //当前电流   ，  单位：A
				try{map.put("Power", soap.getProperty("Power").toString());}catch(Exception e){}   //当前功率   ，单位：W
				try{map.put("PowerRate", soap.getProperty("PowerRate").toString());}catch(Exception e){} //当前功率因数
				try{map.put("Eletricity", soap.getProperty("Eletricity").toString());}catch(Exception e){}   //当前总共用电量
				try{map.put("PreChargeback", soap.getProperty("PreChargeback").toString());}catch(Exception e){}   //账户剩余金额
				try{map.put("PreSubsidy", soap.getProperty("PreSubsidy").toString());}catch(Exception e){}   //剩余补贴，单位：元
				try{map.put("AccountStatus", soap.getProperty("AccountStatus").toString());}catch(Exception e){}   //账户状态，字符串，有“正常”，“余额不足”，“冻结”三个状态
				try{map.put("AccountType", soap.getProperty("AccountType").toString());}catch(Exception e){}   //账户类型，与输入相同
				try{map.put("ElecMonth", soap.getProperty("ElecMonth").toString());}catch(Exception e){}   //本月总用电量
				try{map.put("ElecDay", soap.getProperty("ElecDay").toString());}catch(Exception e){}   //本日总用电量
				
				data.add(map);
			
			}
		}
		catch(Exception e){}
		return data;
	}
	
	//3.用于解析Inqurey_Room_RoomID返回数据的静态方法    
	public static ArrayList<Map<String, String>> inquiry_room_roomid(SoapObject result) {
		// TODO Auto-generated constructor stub
		ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();
		try
		{
			result = (SoapObject)result.getProperty(1); 
			if(result.getPropertyCount()>=1)
				result = (SoapObject)result.getProperty(0);  //有数据  则继续
			else 
				return data;     //无数据直接返回空
			
			for(int i=0;  i< result.getPropertyCount(); i++ ){
				SoapObject soap = (SoapObject) result.getProperty(i);
			    Map<String, String> map=new HashMap<String,String>();
				try{map.put("StudentID", soap.getProperty("StudentID").toString());}catch(Exception e){}    //学号
				try{map.put("SName", soap.getProperty("SName").toString());}catch(Exception e){}       //姓名
				try{map.put("Faculty", soap.getProperty("Faculty").toString());}catch(Exception e){}	   //学院
				try{map.put("Professional", soap.getProperty("Professional").toString());}catch(Exception e){}  //专业
				try{map.put("YesorNo", soap.getProperty("YesorNo").toString());}catch(Exception e){}    //是否为账户管理员，“是”:是账户管理员； “否”：普通学生用户
				try{map.put("Permission", soap.getProperty("Permission").toString());}catch(Exception e){}	 //是否有控制权限，“有”：有控制权限；  “无”：没有控制权限
				try{map.put("PhoneNum", soap.getProperty("PhoneNum").toString());}catch(Exception e){}    //电话，存在则返回电话号码，不存在则返回"anyType{}"
							
				data.add(map);
			
			}
		}
		catch(Exception e){}
		return data;
	}
	
	//4.用于解析Inqurey_States返回数据的静态方法    
	public static ArrayList<Map<String, String>> inquiry_states(SoapObject result) {
		// TODO Auto-generated constructor stub
		ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();
		try
		{
			result = (SoapObject)result.getProperty(1); 
			if(result.getPropertyCount()>=1)
				result = (SoapObject)result.getProperty(0);  //有数据  则继续
			else 
				return data;     //无数据直接返回空
			
			for(int i=0;  i< result.getPropertyCount(); i++ ){
				SoapObject soap = (SoapObject) result.getProperty(i);
			    Map<String, String> map=new HashMap<String,String>();
				try{map.put("GetTime", soap.getProperty("GetTime").toString());}catch(Exception e){}    //时间
				try{map.put("Type", soap.getProperty("Type").toString());}catch(Exception e){}       //类型：余额不足，恶性负载，过载，用电操作
				try{map.put("Contents", soap.getProperty("Contents").toString());}catch(Exception e){}	   //内容描述
				try{map.put("RoomID", soap.getProperty("RoomID").toString());}catch(Exception e){}  //宿舍ID
				try{map.put("AccountType", soap.getProperty("AccountType").toString());}catch(Exception e){}    //账户类型：照明或空调
							
				data.add(map);
			
			}
		}
		catch(Exception e){}
		return data;
	}
	
	//5.用于解析Inqurey_States返回数据的静态方法    
	public static ArrayList<Map<String, String>> inquiry_operate(SoapObject result) {
		// TODO Auto-generated constructor stub
		ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();
		try
		{
			result = (SoapObject)result.getProperty(1); 
			if(result.getPropertyCount()>=1)
				result = (SoapObject)result.getProperty(0);  //有数据  则继续
			else 
				return data;     //无数据直接返回空
			
			for(int i=0;  i< result.getPropertyCount(); i++ ){
				SoapObject soap = (SoapObject) result.getProperty(i);
			    Map<String, String> map=new HashMap<String,String>();
			    try{map.put("ID", soap.getProperty("ID").toString());}catch(Exception e){}     //ID
				try{map.put("GetTime", soap.getProperty("GetTime").toString());}catch(Exception e){}    //时间
				try{map.put("UserID", soap.getProperty("UserID").toString());}catch(Exception e){}       //操作者ID
				try{map.put("Contents", soap.getProperty("Contents").toString());}catch(Exception e){}	   //内容描述
				try{map.put("IP", soap.getProperty("IP").toString());}catch(Exception e){}     //操作者IP
				try{map.put("Role", soap.getProperty("Role").toString());}catch(Exception e){}     //角色号
				try{map.put("OType", soap.getProperty("OType").toString());}catch(Exception e){}   //操作，2：打开；  3：关闭
				try{map.put("RoomID", soap.getProperty("RoomID").toString());}catch(Exception e){}  //宿舍ID
				try{map.put("AccountType", soap.getProperty("AccountType").toString());}catch(Exception e){}    //账户类型：照明或空调
							
				data.add(map);
			
			}
		}catch(Exception e){}
		return data;
	}
	
	//6.用于解析Inqurey_ReCharge返回数据的静态方法    
	public static ArrayList<Map<String, String>> inquiry_recharge(SoapObject result) {
		// TODO Auto-generated constructor stub
		ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();
		try
		{
			result = (SoapObject)result.getProperty(1); 
			if(result.getPropertyCount()>=1)
				result = (SoapObject)result.getProperty(0);  //有数据  则继续
			else 
				return data;     //无数据直接返回空
			
			for(int i=0;  i< result.getPropertyCount(); i++ ){
				SoapObject soap = (SoapObject) result.getProperty(i);
			    Map<String, String> map=new HashMap<String,String>();
			    try{map.put("RoomID", soap.getProperty("RoomID").toString());}catch(Exception e){}  //宿舍ID
			    try{map.put("Area", soap.getProperty("Area").toString());}catch(Exception e){}          //宿舍园区
				try{map.put("Building", soap.getProperty("Building").toString());}catch(Exception e){}  //宿舍楼 栋
				try{map.put("Unit", soap.getProperty("Unit").toString().equals("anyType{}")?"":soap.getProperty("Unit").toString());}catch(Exception e){}            //宿舍单元
				try{map.put("Floor", soap.getProperty("Floor").toString());}catch(Exception e){}      //楼层
				try{map.put("RoomNum", soap.getProperty("RoomNum").toString());}catch(Exception e){}      //楼层
				try{map.put("AccountType", soap.getProperty("AccountType").toString());}catch(Exception e){}    //账户类型：照明或空调
				try{map.put("GetTime", soap.getProperty("GetTime").toString());}catch(Exception e){}    //时间
				try{map.put("StudentID", soap.getProperty("StudentID").toString());}catch(Exception e){}       //充值者ID
				try{map.put("Recharge", soap.getProperty("Recharge").toString());}catch(Exception e){}	   //充值金额
				try{map.put("SName", soap.getProperty("SName").toString());}catch(Exception e){}     //充值者姓名
				try{map.put("Balance", soap.getProperty("Balance").toString());}catch(Exception e){}   //充值后余额		
							
				data.add(map);
			
			}
		}
		catch(Exception e){}
		return data;
	}			
	
	//7.用于解析Inqurey_ReCharge_StudentID返回数据的静态方法    
	public static ArrayList<Map<String, String>> inquiry_chargeback(SoapObject result) {
		// TODO Auto-generated constructor stub
		ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();
		try{
			result = (SoapObject)result.getProperty(1); 
			if(result.getPropertyCount()>=1)
				result = (SoapObject)result.getProperty(0);  //有数据  则继续
			else 
				return data;     //无数据直接返回空
			
			for(int i=0;  i< result.getPropertyCount(); i++ ){
				SoapObject soap = (SoapObject) result.getProperty(i);
			    Map<String, String> map=new HashMap<String,String>();
			    try{map.put("GetTime", soap.getProperty("GetTime").toString());}catch(Exception e){}  //时间
			    try{map.put("Chargeback", soap.getProperty("Chargeback").toString());}catch(Exception e){}          //钱数
				try{map.put("Eletricity", soap.getProperty("Eletricity").toString());}catch(Exception e){}  //度数
				
				data.add(map);
			
			}
		}
		catch(Exception e){}
		return data;
	}			
	
	//8.用于解析Inqurey_BillMonth返回数据的静态方法    
	public static ArrayList<Map<String, String>> inquiry_billmonth(SoapObject result) {
		// TODO Auto-generated constructor stub
		ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();
		try{
			result = (SoapObject)result.getProperty(1); 
			if(result.getPropertyCount()>=1)
				result = (SoapObject)result.getProperty(0);  //有数据  则继续
			else 
				return data;     //无数据直接返回空
			
			for(int i=0;  i< result.getPropertyCount(); i++ ){
				SoapObject soap = (SoapObject) result.getProperty(i);
			    Map<String, String> map=new HashMap<String,String>();
			    try{map.put("InDegree", soap.getProperty("InDegree").toString());}catch(Exception e){}  //充值总度数
			    try{map.put("InRMB", soap.getProperty("InRMB").toString());}catch(Exception e){}          //充值总金额
				try{map.put("OutDegree", soap.getProperty("OutDegree").toString());}catch(Exception e){}  //消费总度数
				try{map.put("OutRMB", soap.getProperty("OutRMB").toString());}catch(Exception e){}     //消费总金额
				try{map.put("AccountType", soap.getProperty("AccountType").toString());}catch(Exception e){}  //账户类型
				try{map.put("RoomID", soap.getProperty("RoomID").toString());}catch(Exception e){}      //宿舍ID
				
				data.add(map);
			
			}
		}
		catch(Exception e){}
		return data;
	}				
			
	//9.用于解析Inqurey_HisData返回数据的静态方法    
	public static ArrayList<Map<String, String>> inquiry_hisdata(SoapObject result) {
		// TODO Auto-generated constructor stub
		ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();
		try{
			result = (SoapObject)result.getProperty(1); 
			if(result.getPropertyCount()>=1)
				result = (SoapObject)result.getProperty(0);  //有数据  则继续
			else 
				return data;     //无数据直接返回空
			
			for(int i=0;  i< result.getPropertyCount(); i++ ){
				SoapObject soap = (SoapObject) result.getProperty(i);
			    Map<String, String> map=new HashMap<String,String>();
			    try{map.put("GetTime", soap.getProperty("GetTime").toString());}catch(Exception e){}  //时间
			    try{map.put("Value", soap.getProperty("Value").toString());}catch(Exception e){}      //数值
				data.add(map);
			
			}
		}
		catch(Exception e){}
		return data;
	}	
	
	//10.用于解析Inqurey_Building返回数据的静态方法  		
	public static ArrayList<Map<String, String>> inquiry_building(SoapObject result) {
		// TODO Auto-generated constructor stub
		ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();
		try{
			result = (SoapObject)result.getProperty(1); 
			if(result.getPropertyCount()>=1)
				result = (SoapObject)result.getProperty(0);  //有数据  则继续
			else 
				return data;     //无数据直接返回空
			
			for(int i=0;  i< result.getPropertyCount(); i++ ){
				SoapObject soap = (SoapObject) result.getProperty(i);
			    Map<String, String> map=new HashMap<String,String>();
			    try{map.put("RoomID", soap.getProperty("RoomID").toString());}catch(Exception e){}  //宿舍ID
			    try{map.put("Area", soap.getProperty("Area").toString());}catch(Exception e){}      //园区
			    try{map.put("Building", soap.getProperty("Building").toString());}catch(Exception e){}  //楼栋
			    try{map.put("Unit", soap.getProperty("Unit").toString().equals("anyType{}")?"":soap.getProperty("Unit").toString());}catch(Exception e){}      //单元
			    try{map.put("Floor", soap.getProperty("Floor").toString());}catch(Exception e){}  //楼层
			    try{map.put("RoomNum", soap.getProperty("RoomNum").toString());}catch(Exception e){}      //房间号
				try{map.put("RoomType", soap.getProperty("RoomType").toString());}catch(Exception e){}      //房间类型
				data.add(map);
			
			}
		}
		catch(Exception e){}
		return data;
	}		
	
	//11.用于解析Inqurey_Student返回数据的静态方法  		
	public static ArrayList<Map<String, String>> inquiry_student(SoapObject result) {
		// TODO Auto-generated constructor stub
		ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();
		try{
			result = (SoapObject)result.getProperty(1); 
			if(result.getPropertyCount()>=1)
				result = (SoapObject)result.getProperty(0);  //有数据  则继续
			else 
				return data;     //无数据直接返回空
			
			for(int i=0;  i< result.getPropertyCount(); i++ ){
				SoapObject soap = (SoapObject) result.getProperty(i);
			    Map<String, String> map=new HashMap<String,String>();
			    try{map.put("RoomID", soap.getProperty("RoomID").toString());}catch(Exception e){}  //宿舍ID
			    try{map.put("Area", soap.getProperty("Area").toString());}catch(Exception e){}      //园区
			    try{map.put("Building", soap.getProperty("Building").toString());}catch(Exception e){}  //楼栋
			    try{map.put("Unit",soap.getProperty("Unit").toString().equals("anyType{}")?"":soap.getProperty("Unit").toString());}catch(Exception e){}      //单元
			    try{map.put("Floor", soap.getProperty("Floor").toString());}catch(Exception e){}  //楼层
			    try{map.put("RoomNum", soap.getProperty("RoomNum").toString());}catch(Exception e){}      //房间号
			    try{map.put("StudentID", soap.getProperty("StudentID").toString());}catch(Exception e){}  //学号
			    try{map.put("SName", soap.getProperty("SName").toString());}catch(Exception e){}      //姓名
			    try{map.put("Faculty", soap.getProperty("Faculty").toString());}catch(Exception e){}  //学院
			    try{map.put("Role", soap.getProperty("Role").toString());}catch(Exception e){}      //角色号
			    
				data.add(map);
			
			}
		}
		catch(Exception e){}
		return data;
	}		
	
	//12.用于解析Inqurey_Building_Detail返回数据的静态方法  		
	public static ArrayList<Map<String, String>> inquiry_building_detail(SoapObject result) {
		// TODO Auto-generated constructor stub
		ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();
		try{
			result = (SoapObject)result.getProperty(1); 
			if(result.getPropertyCount()>=1)
				result = (SoapObject)result.getProperty(0);  //有数据  则继续
			else 
				return data;     //无数据直接返回空
			
			for(int i=0;  i< result.getPropertyCount(); i++ ){
				SoapObject soap = (SoapObject) result.getProperty(i);
			    Map<String, String> map=new HashMap<String,String>();
			    try{map.put("RoomID", soap.getProperty("RoomID").toString());}catch(Exception e){}  //宿舍ID
			    try{map.put("Area", soap.getProperty("Area").toString());}catch(Exception e){}      //园区
			    try{map.put("Building", soap.getProperty("Building").toString());}catch(Exception e){}  //楼栋
			    try{map.put("Unit", soap.getProperty("Unit").toString().equals("anyType{}")?"":soap.getProperty("Unit").toString());}catch(Exception e){}      //单元
			    try{map.put("Floor", soap.getProperty("Floor").toString());}catch(Exception e){}  //楼层
			    try{map.put("RoomNum", soap.getProperty("RoomNum").toString());}catch(Exception e){}      //房间号
				try{map.put("RoomType", soap.getProperty("RoomType").toString());}catch(Exception e){}      //房间类型
			    try{map.put("zmStatus", soap.getProperty("zmStatus").toString());}catch(Exception e){}  //照明电表状态
			    try{map.put("zmState", soap.getProperty("zmState").toString());}catch(Exception e){}      //照明开关状态

				data.add(map);
			
			}
		}
		catch(Exception e){}
		return data;
	}	

	//17.用于解析Inqurey_Application返回数据的静态方法  		
	public static ArrayList<Map<String, String>> inquiry_application(SoapObject result) {
		// TODO Auto-generated constructor stub
		ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();
		try{
			result = (SoapObject)result.getProperty(1); 
			if(result.getPropertyCount()>=1)
				result = (SoapObject)result.getProperty(0);  //有数据  则继续
			else 
				return data;     //无数据直接返回空
			
			for(int i=0;  i< result.getPropertyCount(); i++ ){
				SoapObject soap = (SoapObject) result.getProperty(i);
			    Map<String, String> map=new HashMap<String,String>();
			    try{map.put("AID", soap.getProperty("AID").toString());}catch(Exception e){}  //申请ID
			    try{map.put("UserID", soap.getProperty("UserID").toString());}catch(Exception e){}  //申请人ID
			    try{map.put("Application", soap.getProperty("Application").toString());}catch(Exception e){}      //申请内容
			    try{map.put("HasTaken", soap.getProperty("HasTaken").toString());}catch(Exception e){}  //是否被审核，都是false
			    try{map.put("GetTime", soap.getProperty("GetTime").toString());}catch(Exception e){}      //申请提交时间
			    try{map.put("Area", soap.getProperty("Area").toString());}catch(Exception e){}      //园区
			    try{map.put("Building", soap.getProperty("Building").toString());}catch(Exception e){}  //楼栋
			    try{map.put("Unit", soap.getProperty("Unit").toString().equals("anyType{}")?"":soap.getProperty("Unit").toString());}catch(Exception e){}      //单元
			    try{map.put("RoomNum", soap.getProperty("RoomNum").toString());}catch(Exception e){}      //房间号
			    
				data.add(map);
			
			}
		}
		catch(Exception e){}
		return data;
	}			
	//19.用于解析Inqurey_HisApplication返回数据的静态方法  		
	public static ArrayList<Map<String, String>> inquiry_hisapplication(SoapObject result) {
		// TODO Auto-generated constructor stub
		ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();
		try{
			result = (SoapObject)result.getProperty(1); 
			if(result.getPropertyCount()>=1)
				result = (SoapObject)result.getProperty(0);  //有数据  则继续
			else 
				return data;     //无数据直接返回空
			
			for(int i=0;  i< result.getPropertyCount(); i++ ){
				SoapObject soap = (SoapObject) result.getProperty(i);
			    Map<String, String> map=new HashMap<String,String>();
			    try{map.put("AID", soap.getProperty("AID").toString());}catch(Exception e){}  //申请ID
			    try{map.put("UserID", soap.getProperty("UserID").toString());}catch(Exception e){}  //申请人ID
			    try{map.put("CheckID", soap.getProperty("CheckID").toString());}catch(Exception e){}  //审核人ID
			    try{map.put("IsPassed", soap.getProperty("IsPassed").toString());}catch(Exception e){}  //1表示通过，0表示不通过
			    try{map.put("CheckTime", soap.getProperty("CheckTime").toString());}catch(Exception e){}  //审核时间
			    try{map.put("Application", soap.getProperty("Application").toString());}catch(Exception e){}      //申请内容
			    try{map.put("GetTime", soap.getProperty("GetTime").toString());}catch(Exception e){}      //申请提交时间
			    try{map.put("Area", soap.getProperty("Area").toString());}catch(Exception e){}      //园区
			    try{map.put("Building", soap.getProperty("Building").toString());}catch(Exception e){}  //楼栋
			    try{map.put("Unit",soap.getProperty("Unit").toString().equals("anyType{}")?"":soap.getProperty("Unit").toString());}catch(Exception e){}      //单元
			    try{map.put("RoomNum", soap.getProperty("RoomNum").toString());}catch(Exception e){}      //房间号
			    
				data.add(map);
			
			}
		}
		catch(Exception e){}
		return data;
	}					
	
	//22.用于解析Inqurey_Version返回数据的静态方法  		
	public static ArrayList<Map<String, String>> inquiry_version(SoapObject result) {
		// TODO Auto-generated constructor stub
		ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();
		try{
			result = (SoapObject)result.getProperty(1); 
			if(result.getPropertyCount()>=1)
				result = (SoapObject)result.getProperty(0);  //有数据  则继续
			else 
				return data;     //无数据直接返回空
			
			for(int i=0;  i< result.getPropertyCount(); i++ ){
				SoapObject soap = (SoapObject) result.getProperty(i);
			    Map<String, String> map=new HashMap<String,String>();
			    try{map.put("Type", soap.getProperty("Type").toString());}catch(Exception e){}  //类型
			    try{map.put("VersionCode", soap.getProperty("VersionCode").toString());}catch(Exception e){}  //版本更新次数，用来检查是否需要更新
			    try{map.put("VersionName", soap.getProperty("VersionName").toString());}catch(Exception e){}  //版本号
			    try{map.put("Description", soap.getProperty("Description").toString());}catch(Exception e){}  //版本更新内容说明
			    try{map.put("GetTime", soap.getProperty("GetTime").toString());}catch(Exception e){}  //更新时间
			    try{map.put("URL", soap.getProperty("URL").toString());}catch(Exception e){}      //apk下载地址
			   
				data.add(map);
			
			}
		}
		catch(Exception e){}
		return data;
	}		

	//23.用于解析Inqurey_Threephase_R返回数据的静态方法  		
	public static ArrayList<Map<String, String>> inquiry_threephase_r(SoapObject result) {
		// TODO Auto-generated constructor stub
		ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();
		try{
			result = (SoapObject)result.getProperty(1); 
			if(result.getPropertyCount()>=1)
				result = (SoapObject)result.getProperty(0);  //有数据  则继续
			else 
				return data;     //无数据直接返回空
			
			for(int i=0;  i< result.getPropertyCount(); i++ ){
				SoapObject soap = (SoapObject) result.getProperty(i);
			    Map<String, String> map=new HashMap<String,String>();
			    try{map.put("GetTime", soap.getProperty("GetTime").toString());}catch(Exception e){}  //时间
			    try{map.put("VolA", soap.getProperty("VolA").toString());}catch(Exception e){}  //A相电压
			    try{map.put("VolB", soap.getProperty("VolB").toString());}catch(Exception e){}  //B相电压
			    try{map.put("VolC", soap.getProperty("VolC").toString());}catch(Exception e){}  //C相电压
			    try{map.put("IA", soap.getProperty("IA").toString());}catch(Exception e){}  //A相电流
			    try{map.put("IB", soap.getProperty("IB").toString());}catch(Exception e){}      //B相电流
			    try{map.put("IC", soap.getProperty("IC").toString());}catch(Exception e){}  //C相电流
			    try{map.put("ActivePower", soap.getProperty("ActivePower").toString());}catch(Exception e){}  //有功功率
			    try{map.put("ReactivePower", soap.getProperty("ReactivePower").toString());}catch(Exception e){}  //无功功率
			    try{map.put("PowerRateC", soap.getProperty("PowerRateC").toString());}catch(Exception e){}  //功率因数
			    try{map.put("Eletricity", soap.getProperty("Electricity").toString());}catch(Exception e){}  //总用电量，电表读数
			    try{map.put("ElecDay", soap.getProperty("ElecDay").toString());}catch(Exception e){}      //当日已用用电量
			    try{map.put("ElecMonth", soap.getProperty("ElecMonth").toString());}catch(Exception e){}      //当月已用用电量

//				try{map.put("PreSaveDay", soap.getProperty("PreSaveDay").toString());}catch(Exception e){}      //当日总预存电费
//			    try{map.put("PreSaveMonth", soap.getProperty("PreSaveMonth").toString());}catch(Exception e){}      //当月总预存电费
//			    try{map.put("PreSaveYear", soap.getProperty("PreSaveYear").toString());}catch(Exception e){}      //当年总预存电费
//			    try{map.put("PreSaveSum", soap.getProperty("PreSaveSum").toString());}catch(Exception e){}      //总预存电费
//			    try{map.put("ChargeDay", soap.getProperty("ChargeDay").toString());}catch(Exception e){}      //当日总消费电费
//			    try{map.put("ChargeMonth", soap.getProperty("ChargeMonth").toString());}catch(Exception e){}      //当月总消费电费
//			    try{map.put("ChargeYear", soap.getProperty("ChargeYear").toString());}catch(Exception e){}      //当年总消费电费
//			    try{map.put("ChargeSum", soap.getProperty("ChargeSum").toString());}catch(Exception e){}      //总消费电费
//			    try{map.put("SubsidyDay", soap.getProperty("SubsidyDay").toString());}catch(Exception e){}      //本日已用补贴
//			    try{map.put("SubsidyMonth", soap.getProperty("SubsidyMonth").toString());}catch(Exception e){}      //本月已用补贴
//			    try{map.put("SubsidySum", soap.getProperty("SubsidySum").toString());}catch(Exception e){}      //总已用补贴
			    data.add(map);
			
			}
		}
		catch(Exception e){}
		return data;
	}		
	
	//24.用于解析Inqurey_Threephase_H返回数据的静态方法  		
	public static ArrayList<Map<String, String>> inquiry_threephase_h(SoapObject result) {
		// TODO Auto-generated constructor stub
		ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();
		try{
			result = (SoapObject)result.getProperty(1); 
			if(result.getPropertyCount()>=1)
				result = (SoapObject)result.getProperty(0);  //有数据  则继续
			else 
				return data;     //无数据直接返回空
			
			for(int i=0;  i< result.getPropertyCount(); i++ ){
				SoapObject soap = (SoapObject) result.getProperty(i);
			    Map<String, String> map=new HashMap<String,String>();
			    try{map.put("GetTime", soap.getProperty("GetTime").toString());}catch(Exception e){}  //时间
			    try{map.put("Value", soap.getProperty("Value").toString());}catch(Exception e){}  //值
			    
				data.add(map);
			
			}
		}
		catch(Exception e){}
		return data;
	}
	//25.用于解析Inqurey_General返回数据的静态方法
	public static ArrayList<Map<String, String>> inquiry_general(SoapObject result) {
		// TODO Auto-generated constructor stub
		ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();
		try{
			result = (SoapObject)result.getProperty(1);
			if(result.getPropertyCount()>=1)
				result = (SoapObject)result.getProperty(0);  //有数据  则继续
			else
				return data;     //无数据直接返回空

			for(int i=0;  i< result.getPropertyCount(); i++ ){
				SoapObject soap = (SoapObject) result.getProperty(i);
				Map<String, String> map=new HashMap<String,String>();
				try{map.put("Electricity", soap.getProperty("Electricity").toString());}catch(Exception e){}  //总用电量，电表读数
				try{map.put("ElecDay", soap.getProperty("ElecDay").toString());}catch(Exception e){}      //当日已用用电量
				try{map.put("ElecMonth", soap.getProperty("ElecMonth").toString());}catch(Exception e){}      //当月已用用电量
				try{map.put("PreSaveDay", soap.getProperty("PreSaveDay").toString());}catch(Exception e){}      //当日总预存电费
			    try{map.put("PreSaveMonth", soap.getProperty("PreSaveMonth").toString());}catch(Exception e){}      //当月总预存电费
			    try{map.put("PreSaveYear", soap.getProperty("PreSaveYear").toString());}catch(Exception e){}      //当年总预存电费
			    try{map.put("PreSaveSum", soap.getProperty("PreSaveSum").toString());}catch(Exception e){}      //总预存电费
			    try{map.put("ChargeDay", soap.getProperty("ChargeDay").toString());}catch(Exception e){}      //当日总消费电费
			    try{map.put("ChargeMonth", soap.getProperty("ChargeMonth").toString());}catch(Exception e){}      //当月总消费电费
			    try{map.put("ChargeYear", soap.getProperty("ChargeYear").toString());}catch(Exception e){}      //当年总消费电费
			    try{map.put("ChargeSum", soap.getProperty("ChargeSum").toString());}catch(Exception e){}      //总消费电费
			    try{map.put("SubsidyDay", soap.getProperty("SubsidyDay").toString());}catch(Exception e){}      //本日已用补贴
			    try{map.put("SubsidyMonth", soap.getProperty("SubsidyMonth").toString());}catch(Exception e){}      //本月已用补贴
			    try{map.put("SubsidySum", soap.getProperty("SubsidySum").toString());}catch(Exception e){}      //总已用补贴


				data.add(map);

			}
		}
		catch(Exception e){}
		return data;
	}

}
