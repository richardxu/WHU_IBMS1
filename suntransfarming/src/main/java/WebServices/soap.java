package WebServices;

import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class soap {
	//命名空间
	public final static String NAMESPACE="http://www.suntrans.net";
	// WebService地址
	public final static String URL ="https://www.suntrans.net:8017/";
	
	//1.调用Inquiry_UserInfo方法（查询学生用户信息）的soap通讯静态方法   返回一个SoapObject对象
	public static SoapObject Inquiry_UserInfo(String UserID)
	{ 
		//命名空间
		final String NAMESPACE="http://www.suntrans.net/";  
		// WebService地址  
		final String URL ="http://210.42.122.127:8080";
		//方法名
		final String METHOD_NAME ="Inquiry_UserInfo";  
		final String SOAP_ACTION ="http://www.suntrans.net/Inquiry_UserInfo";
		//SoapObject detail; 
		SoapObject rpc =new SoapObject(NAMESPACE, METHOD_NAME);  
		 rpc.addProperty("UserID", UserID);    //添加参数：学号
		 HttpTransportSE ht =new HttpTransportSE(URL,5000);    //设置访问地址，第二个参数是设置超时的毫秒数
		 ht.debug =true;  
		SoapSerializationEnvelope envelope =new SoapSerializationEnvelope(SoapEnvelope.VER11);  
		 envelope.bodyOut = rpc;  
		 envelope.dotNet =true;  
		 envelope.setOutputSoapObject(rpc);  
		  try {
			ht.call(SOAP_ACTION, envelope);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		//  Object result = envelope.bodyIn;  
		 SoapObject result = null;
		 
		try {
				result = (SoapObject)envelope.getResponse();    //获取结果
			} catch (SoapFault e) {
				// TODO Auto-generated catch block
				
			}     //result是服务器返回的数据  形式为SoapObject对象
		return result;   
	}
		
	//2.调用Inquiry_Channel方法   （ 查询用电状态  ）的soap通讯静态方法   返回一个SoapObject对象
	public static SoapObject Inquiry_Channel(String StudentID,String AccountType)
	{ 
		//命名空间
		final String NAMESPACE="http://www.suntrans.net/";  
		// WebService地址  
		final String URL ="http://210.42.122.127:8080";
		//方法名
		final String METHOD_NAME ="Inquiry_Channel";  
		final String SOAP_ACTION ="http://www.suntrans.net/Inquiry_Channel";
		//SoapObject detail; 
		SoapObject rpc =new SoapObject(NAMESPACE, METHOD_NAME);  
		 rpc.addProperty("StudentID", StudentID);    //添加参数
		 rpc.addProperty("AccountType", AccountType);  
		  HttpTransportSE ht =new HttpTransportSE(URL,5000);    //设置访问地址，第二个参数是设置超时的毫秒数
		 ht.debug =true;  
		SoapSerializationEnvelope envelope =new SoapSerializationEnvelope(SoapEnvelope.VER11);  
		 envelope.bodyOut = rpc;  
		 envelope.dotNet =true;  
		 envelope.setOutputSoapObject(rpc);  
		  try {
			ht.call(SOAP_ACTION, envelope);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		//  Object result = envelope.bodyIn;  
		 SoapObject result = null;
		 
		try {
				result = (SoapObject)envelope.getResponse();    //获取结果
			} catch (SoapFault e) {
				// TODO Auto-generated catch block
				
			}     //result是服务器返回的数据  形式为SoapObject对象
		return result;   
	}		
	//2.1调用Inquiry_Channel_RoomID方法   （ 查询用电状态  ）的soap通讯静态方法   返回一个SoapObject对象
		public static SoapObject Inquiry_Channel_RoomID(String RoomID,String AccountType)
		{ 
			//命名空间
			final String NAMESPACE="http://www.suntrans.net/";  
			// WebService地址  
			final String URL ="http://210.42.122.127:8080";
			//方法名
			final String METHOD_NAME ="Inquiry_Channel_RoomID";  
			final String SOAP_ACTION ="http://www.suntrans.net/Inquiry_Channel_RoomID";
			//SoapObject detail; 
			SoapObject rpc =new SoapObject(NAMESPACE, METHOD_NAME);  
			 rpc.addProperty("RoomID", RoomID);    //添加参数
			 rpc.addProperty("AccountType", AccountType);  
			  HttpTransportSE ht =new HttpTransportSE(URL,5000);    //设置访问地址，第二个参数是设置超时的毫秒数
			 ht.debug =true;  
			SoapSerializationEnvelope envelope =new SoapSerializationEnvelope(SoapEnvelope.VER11);  
			 envelope.bodyOut = rpc;  
			 envelope.dotNet =true;  
			 envelope.setOutputSoapObject(rpc);  
			  try {
				ht.call(SOAP_ACTION, envelope);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
			//  Object result = envelope.bodyIn;  
			 SoapObject result = null;
			 
			try {
					result = (SoapObject)envelope.getResponse();    //获取结果
				} catch (SoapFault e) {
					// TODO Auto-generated catch block
					
				}     //result是服务器返回的数据  形式为SoapObject对象
			return result;   
		}		
		
	//3.调用Inquiry_Room_RoomID方法（查询用户宿舍信息）的soap通讯静态方法   返回一个SoapObject对象
	public static SoapObject Inquiry_Room_RoomID(String RoomID)
	{ 
		//命名空间
		final String NAMESPACE="http://www.suntrans.net/";  
		// WebService地址  
		final String URL ="http://210.42.122.127:8080";
		//方法名
		final String METHOD_NAME ="Inquiry_Room_RoomID";  
		final String SOAP_ACTION ="http://www.suntrans.net/Inquiry_Room_RoomID";
		//SoapObject detail; 
		SoapObject rpc =new SoapObject(NAMESPACE, METHOD_NAME);  
		 rpc.addProperty("RoomID", RoomID);    //添加参数
		  HttpTransportSE ht =new HttpTransportSE(URL,5000);    //设置访问地址，第二个参数是设置超时的毫秒数
		 ht.debug =true;  
		 SoapSerializationEnvelope envelope =new SoapSerializationEnvelope(SoapEnvelope.VER11);  
		 envelope.bodyOut = rpc;  
		 envelope.dotNet =true;  
		 envelope.setOutputSoapObject(rpc);  
		  try {
			ht.call(SOAP_ACTION, envelope);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		//  Object result = envelope.bodyIn;  
		 SoapObject result = null;
		 
		try {
				result = (SoapObject)envelope.getResponse();    //获取结果
			} catch (SoapFault e) {
				// TODO Auto-generated catch block
				
			}     //result是服务器返回的数据  形式为SoapObject对象
		return result;   
	}
	
	//4.调用Inquiry_States方法（查询开关状态日志）的soap通讯静态方法   返回一个SoapObject对象
	public static SoapObject Inquiry_States(String StudentID,String AccountType,String StartTime,String EndTime)
	{ 
		//命名空间
		final String NAMESPACE="http://www.suntrans.net/";  
		// WebService地址  
		final String URL ="http://210.42.122.127:8080";
		//方法名
		final String METHOD_NAME ="Inquiry_States";  
		final String SOAP_ACTION ="http://www.suntrans.net/Inquiry_States";
		//SoapObject detail; 
		SoapObject rpc =new SoapObject(NAMESPACE, METHOD_NAME);  
		 rpc.addProperty("StudentID", StudentID);    //学号
		 rpc.addProperty("AccountType", AccountType);    //账户类型
		 rpc.addProperty("StartTime", StartTime);    //添加参数
		 rpc.addProperty("EndTime", EndTime);    //添加参数
		  HttpTransportSE ht =new HttpTransportSE(URL,8000);    //设置访问地址，第二个参数是设置超时的毫秒数
		 ht.debug =true;  
		 SoapSerializationEnvelope envelope =new SoapSerializationEnvelope(SoapEnvelope.VER11);  
		 envelope.bodyOut = rpc;  
		 envelope.dotNet =true;  
		 envelope.setOutputSoapObject(rpc);  
		  try {
			ht.call(SOAP_ACTION, envelope);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		//  Object result = envelope.bodyIn;  
		 SoapObject result = null;
		 
		try {
				result = (SoapObject)envelope.getResponse();    //获取结果
			} catch (SoapFault e) {
				// TODO Auto-generated catch block
				
			}     //result是服务器返回的数据  形式为SoapObject对象
		return result;   
	}		
	
	//4.1 调用Inquiry_States_RoomID方法（查询开关状态日志）的soap通讯静态方法   返回一个SoapObject对象
		public static SoapObject Inquiry_States_RoomID(String RoomID,String AccountType,String StartTime,String EndTime)
		{ 
			//命名空间
			final String NAMESPACE="http://www.suntrans.net/";  
			// WebService地址  
			final String URL ="http://210.42.122.127:8080";
			//方法名
			final String METHOD_NAME ="Inquiry_States_RoomID";  
			final String SOAP_ACTION ="http://www.suntrans.net/Inquiry_States_RoomID";
			//SoapObject detail; 
			SoapObject rpc =new SoapObject(NAMESPACE, METHOD_NAME);  
			 rpc.addProperty("RoomID", RoomID);    //学号
			 rpc.addProperty("AccountType", AccountType);    //账户类型
			 rpc.addProperty("StartTime", StartTime);    //添加参数
			 rpc.addProperty("EndTime", EndTime);    //添加参数
			  HttpTransportSE ht =new HttpTransportSE(URL,8000);    //设置访问地址，第二个参数是设置超时的毫秒数
			 ht.debug =true;  
			 SoapSerializationEnvelope envelope =new SoapSerializationEnvelope(SoapEnvelope.VER11);  
			 envelope.bodyOut = rpc;  
			 envelope.dotNet =true;  
			 envelope.setOutputSoapObject(rpc);  
			  try {
				ht.call(SOAP_ACTION, envelope);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
			//  Object result = envelope.bodyIn;  
			 SoapObject result = null;
			 
			try {
					result = (SoapObject)envelope.getResponse();    //获取结果
				} catch (SoapFault e) {
					// TODO Auto-generated catch block
					
				}     //result是服务器返回的数据  形式为SoapObject对象
			return result;   
		}		
	
	//5.调用Inquiry_Operate方法（查询操作记录）的soap通讯静态方法   返回一个SoapObject对象
	public static SoapObject Inquiry_Operate(String RoomID,String Role,String OperaterID,String StartTime,String EndTime)
	{ 
		//命名空间
		final String NAMESPACE="http://www.suntrans.net/";  
		// WebService地址  
		final String URL ="http://210.42.122.127:8080";
		//方法名
		final String METHOD_NAME ="Inquiry_Operate";  
		final String SOAP_ACTION ="http://www.suntrans.net/Inquiry_Operate";
		//SoapObject detail; 
		SoapObject rpc =new SoapObject(NAMESPACE, METHOD_NAME);  
		 rpc.addProperty("RoomID", RoomID);    //宿舍ID
		 rpc.addProperty("Role", Role);    //角色号
		 rpc.addProperty("OperaterID",OperaterID);  //操作人员ID号
		 rpc.addProperty("StartTime", StartTime);    //添加参数
		 rpc.addProperty("EndTime", EndTime);    //添加参数
		  HttpTransportSE ht =new HttpTransportSE(URL,5000);    //设置访问地址，第二个参数是设置超时的毫秒数
		 ht.debug =true;  
		 SoapSerializationEnvelope envelope =new SoapSerializationEnvelope(SoapEnvelope.VER11);  
		 envelope.bodyOut = rpc;  
		 envelope.dotNet =true;  
		 envelope.setOutputSoapObject(rpc);  
		  try {
			ht.call(SOAP_ACTION, envelope);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		//  Object result = envelope.bodyIn;  
		 SoapObject result = null;
		 
		try {
				result = (SoapObject)envelope.getResponse();    //获取结果
			} catch (SoapFault e) {
				// TODO Auto-generated catch block
				
			}     //result是服务器返回的数据  形式为SoapObject对象
		return result;   
	}			
	
	//6.调用Inquiry_ReCharge方法（查询充值记录）的soap通讯静态方法   返回一个SoapObject对象
	public static SoapObject Inquiry_ReCharge(String StudentID,String AccountType,String StartTime,String EndTime)
	{ 
		//命名空间
		final String NAMESPACE="http://www.suntrans.net/";  
		// WebService地址  
		final String URL ="http://210.42.122.127:8080";
		//方法名
		final String METHOD_NAME ="Inquiry_ReCharge";  
		final String SOAP_ACTION ="http://www.suntrans.net/Inquiry_ReCharge";
		//SoapObject detail; 
		SoapObject rpc =new SoapObject(NAMESPACE, METHOD_NAME);  
		 rpc.addProperty("StudentID", StudentID);    //学号
		 rpc.addProperty("AccountType", AccountType);    //账户类型
		 rpc.addProperty("StartTime", StartTime);    //开始时间
		 rpc.addProperty("EndTime", EndTime);    //结束时间
		  HttpTransportSE ht =new HttpTransportSE(URL,5000);    //设置访问地址，第二个参数是设置超时的毫秒数
		 ht.debug =true;  
		 SoapSerializationEnvelope envelope =new SoapSerializationEnvelope(SoapEnvelope.VER11);  
		 envelope.bodyOut = rpc;  
		 envelope.dotNet =true;  
		 envelope.setOutputSoapObject(rpc);  
		  try {
			ht.call(SOAP_ACTION, envelope);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		//  Object result = envelope.bodyIn;  
		 SoapObject result = null;
		 
		try {
				result = (SoapObject)envelope.getResponse();    //获取结果
			} catch (SoapFault e) {
				// TODO Auto-generated catch block
				
			}     //result是服务器返回的数据  形式为SoapObject对象
		return result;   
	}				
	//6.1 调用Inquiry_ReCharge_RoomID方法（查询充值记录）的soap通讯静态方法   返回一个SoapObject对象
		public static SoapObject Inquiry_ReCharge_RoomID(String RoomID,String AccountType,String StartTime,String EndTime)
		{ 
			//命名空间
			final String NAMESPACE="http://www.suntrans.net/";  
			// WebService地址  
			final String URL ="http://210.42.122.127:8080";
			//方法名
			final String METHOD_NAME ="Inquiry_ReCharge_RoomID";  
			final String SOAP_ACTION ="http://www.suntrans.net/Inquiry_ReCharge_RoomID";
			//SoapObject detail; 
			SoapObject rpc =new SoapObject(NAMESPACE, METHOD_NAME);  
			 rpc.addProperty("RoomID", RoomID);    //学号
			 rpc.addProperty("AccountType", AccountType);    //账户类型
			 rpc.addProperty("StartTime", StartTime);    //开始时间
			 rpc.addProperty("EndTime", EndTime);    //结束时间
			  HttpTransportSE ht =new HttpTransportSE(URL,10000);    //设置访问地址，第二个参数是设置超时的毫秒数
			 ht.debug =true;  
			 SoapSerializationEnvelope envelope =new SoapSerializationEnvelope(SoapEnvelope.VER11);  
			 envelope.bodyOut = rpc;  
			 envelope.dotNet =true;  
			 envelope.setOutputSoapObject(rpc);  
			  try {
				ht.call(SOAP_ACTION, envelope);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
			//  Object result = envelope.bodyIn;  
			 SoapObject result = null;
			 
			try {
					result = (SoapObject)envelope.getResponse();    //获取结果
				} catch (SoapFault e) {
					// TODO Auto-generated catch block
					
				}     //result是服务器返回的数据  形式为SoapObject对象
			return result;   
		}				
	
	//7.调用Inquiry_Chargeback方法（查询消费列表）的soap通讯静态方法   返回一个SoapObject对象
	public static SoapObject Inquiry_Chargeback(String StudentID,String AccountType,String Freq,String StartTime,String EndTime)
	{ 
		//命名空间
		final String NAMESPACE="http://www.suntrans.net/";  
		// WebService地址  
		final String URL ="http://210.42.122.127:8080";
		//方法名
		final String METHOD_NAME ="Inquiry_Chargeback";  
		final String SOAP_ACTION ="http://www.suntrans.net/Inquiry_Chargeback";
		//SoapObject detail; 
		SoapObject rpc =new SoapObject(NAMESPACE, METHOD_NAME);  
		 rpc.addProperty("StudentID", StudentID);    //学号
		 rpc.addProperty("AccountType", AccountType);    //账户类型
		 rpc.addProperty("Freq", Freq);    //查询频率，小时
		 rpc.addProperty("StartTime", StartTime);    //开始时间
		 rpc.addProperty("EndTime", EndTime);    //结束时间
		  HttpTransportSE ht =new HttpTransportSE(URL,5000);    //设置访问地址，第二个参数是设置超时的毫秒数
		 ht.debug =true;  
		 SoapSerializationEnvelope envelope =new SoapSerializationEnvelope(SoapEnvelope.VER11);  
		 envelope.bodyOut = rpc;  
		 envelope.dotNet =true;  
		 envelope.setOutputSoapObject(rpc);  
		  try {
			ht.call(SOAP_ACTION, envelope);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		//  Object result = envelope.bodyIn;  
		 SoapObject result = null;
		 
		try {
				result = (SoapObject)envelope.getResponse();    //获取结果
			} catch (SoapFault e) {
				// TODO Auto-generated catch block
				
			}     //result是服务器返回的数据  形式为SoapObject对象
		return result;   
	}		
	//7.1 调用Inquiry_Chargeback_RoomID方法（查询消费列表）的soap通讯静态方法   返回一个SoapObject对象
		public static SoapObject Inquiry_Chargeback_RoomID(String RoomID,String AccountType,String Freq,String StartTime,String EndTime)
		{ 
			//命名空间
			final String NAMESPACE="http://www.suntrans.net/";  
			// WebService地址  
			final String URL ="http://210.42.122.127:8080";
			//方法名
			final String METHOD_NAME ="Inquiry_Chargeback_RoomID";  
			final String SOAP_ACTION ="http://www.suntrans.net/Inquiry_Chargeback_RoomID";
			//SoapObject detail; 
			SoapObject rpc =new SoapObject(NAMESPACE, METHOD_NAME);  
			 rpc.addProperty("RoomID", RoomID);    //学号
			 rpc.addProperty("AccountType", AccountType);    //账户类型
			 rpc.addProperty("Freq", Freq);    //查询频率，小时
			 rpc.addProperty("StartTime", StartTime);    //开始时间
			 rpc.addProperty("EndTime", EndTime);    //结束时间
			  HttpTransportSE ht =new HttpTransportSE(URL,10000);    //设置访问地址，第二个参数是设置超时的毫秒数
			 ht.debug =true;  
			 SoapSerializationEnvelope envelope =new SoapSerializationEnvelope(SoapEnvelope.VER11);  
			 envelope.bodyOut = rpc;  
			 envelope.dotNet =true;  
			 envelope.setOutputSoapObject(rpc);  
			  try {
				ht.call(SOAP_ACTION, envelope);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
			//  Object result = envelope.bodyIn;  
			 SoapObject result = null;
			 
			try {
					result = (SoapObject)envelope.getResponse();    //获取结果
				} catch (SoapFault e) {
					// TODO Auto-generated catch block
					
				}     //result是服务器返回的数据  形式为SoapObject对象
			return result;   
		}		
	
	//8.调用Inquiry_WindFreq方法（查询风向频率）的soap通讯静态方法   返回一个SoapObject对象
	public static SoapObject Inquiry_WindFreq(String Sid,String StartTime,String EndTime)	{

		//方法名
		final String METHOD_NAME ="Inquiry_BillMonth";  
		final String SOAP_ACTION ="http://www.suntrans.net/Inquiry_BillMonth";
		//SoapObject detail; 
		SoapObject rpc =new SoapObject(NAMESPACE, METHOD_NAME);  
		 rpc.addProperty("Sid", Sid);    //传感器id
		 rpc.addProperty("StartTime", StartTime);    //开始时间
		 rpc.addProperty("EndTime", EndTime);    //结束时间

		  HttpTransportSE ht =new HttpTransportSE(URL,5000);    //设置访问地址，第二个参数是设置超时的毫秒数
		 ht.debug =true;  
		 SoapSerializationEnvelope envelope =new SoapSerializationEnvelope(SoapEnvelope.VER11);  
		 envelope.bodyOut = rpc;  
		 envelope.dotNet =true;  
		 envelope.setOutputSoapObject(rpc);  
		  try {
			ht.call(SOAP_ACTION, envelope);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		//  Object result = envelope.bodyIn;  
		 SoapObject result = null;
		 
		try {
				result = (SoapObject)envelope.getResponse();    //获取结果
			} catch (SoapFault e) {
				// TODO Auto-generated catch block
				
			}     //result是服务器返回的数据  形式为SoapObject对象
		return result;   
	}	
	//9. 调用Inquiry_Sensors方法（查询所有传感器信息）的soap通讯静态方法   返回一个SoapObject对象
		public static SoapObject Inquiry_Sensors()
		{ 

			//方法名
			final String METHOD_NAME ="Inquiry_Sensors";
			final String SOAP_ACTION ="http://www.suntrans.net/Inquiry_Sensors";
			//SoapObject detail; 
			SoapObject rpc =new SoapObject(NAMESPACE, METHOD_NAME);
			  HttpTransportSE ht =new HttpTransportSE(URL,5000);    //设置访问地址，第二个参数是设置超时的毫秒数
			 ht.debug =true;  
			 SoapSerializationEnvelope envelope =new SoapSerializationEnvelope(SoapEnvelope.VER11);  
			 envelope.bodyOut = rpc;  
			 envelope.dotNet =true;  
			 envelope.setOutputSoapObject(rpc);  
			  try {
				ht.call(SOAP_ACTION, envelope);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
			//  Object result = envelope.bodyIn;  
			 SoapObject result = null;
			 
			try {
					result = (SoapObject)envelope.getResponse();    //获取结果
				} catch (SoapFault e) {
					// TODO Auto-generated catch block
					
				}     //result是服务器返回的数据  形式为SoapObject对象
			return result;   
		}


		//10.调用Inquiry_HisData方法（查看历史参数）的soap通讯静态方法   返回一个SoapObject对象
		public static SoapObject Inquiry_HisData(String Sid,String StartTime,String EndTime,String Freq)
		{
			//方法名
			final String METHOD_NAME ="Inquiry_HisData";
			final String SOAP_ACTION ="http://www.suntrans.net/Inquiry_HisData";
			//SoapObject detail; 
			SoapObject rpc =new SoapObject(NAMESPACE, METHOD_NAME);  			
			  HttpTransportSE ht =new HttpTransportSE(URL,5000);    //设置访问地址，第二个参数是设置超时的毫秒数
			rpc.addProperty("Sid",Sid);
			rpc.addProperty("StartTime", StartTime);
			rpc.addProperty("EndTime",EndTime);
			rpc.addProperty("Freq",Freq);
			 ht.debug =true;  
			 SoapSerializationEnvelope envelope =new SoapSerializationEnvelope(SoapEnvelope.VER11);  
			 envelope.bodyOut = rpc;  
			 envelope.dotNet =true;  
			 envelope.setOutputSoapObject(rpc);  
			  try {
				ht.call(SOAP_ACTION, envelope);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
			//  Object result = envelope.bodyIn;  
			 SoapObject result = null;
			 
			try {
					result = (SoapObject)envelope.getResponse();    //获取结果
				} catch (SoapFault e) {
					// TODO Auto-generated catch block
					
				}     //result是服务器返回的数据  形式为SoapObject对象
			return result;   
		}
		
		//11.调用Inquiry_RealData方法（查询站点的实时参数）的soap通讯静态方法   返回一个SoapObject对象
		public static SoapObject Inquiry_RealData(String addr)
		{
			//方法名
			final String METHOD_NAME ="Inquiry_RealData";
			final String SOAP_ACTION ="http://www.suntrans.net/Inquiry_RealData";
			//SoapObject detail; 
			SoapObject rpc =new SoapObject(NAMESPACE, METHOD_NAME);  			
			HttpTransportSE ht =new HttpTransportSE(URL,5000);    //设置访问地址，第二个参数是设置超时的毫秒数
			rpc.addProperty("Addr",addr);
			ht.debug =true;
			 SoapSerializationEnvelope envelope =new SoapSerializationEnvelope(SoapEnvelope.VER11);  
			 envelope.bodyOut = rpc;  
			 envelope.dotNet =true;  
			 envelope.setOutputSoapObject(rpc);  
			  try {
				ht.call(SOAP_ACTION, envelope);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
			//  Object result = envelope.bodyIn;  
			 SoapObject result = null;
			 
			try {
					result = (SoapObject)envelope.getResponse();    //获取结果
				} catch (SoapFault e) {
					// TODO Auto-generated catch block
					
				}     //result是服务器返回的数据  形式为SoapObject对象
			return result;   
		}			

		//12.调用Inquiry_Sites方法（查询所有站点）的soap通讯静态方法   返回一个SoapObject对象
		public static SoapObject Inquiry_Sites()
		{
			//方法名
			final String METHOD_NAME ="Inquiry_Sites";
			final String SOAP_ACTION ="http://www.suntrans.net/Inquiry_Sites";
			//SoapObject detail; 
			SoapObject rpc =new SoapObject(NAMESPACE, METHOD_NAME);  			
			  HttpTransportSE ht =new HttpTransportSE(URL,5000);    //设置访问地址，第二个参数是设置超时的毫秒数
			 ht.debug =true;  
			 SoapSerializationEnvelope envelope =new SoapSerializationEnvelope(SoapEnvelope.VER11);
			 envelope.bodyOut = rpc;  
			 envelope.dotNet =true;  
			 envelope.setOutputSoapObject(rpc);  
			  try {
				ht.call(SOAP_ACTION, envelope);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
			//  Object result = envelope.bodyIn;  
			 SoapObject result = null;
			 
			try {
					result = (SoapObject)envelope.getResponse();    //获取结果
				} catch (SoapFault e) {
					// TODO Auto-generated catch block
					
				}     //result是服务器返回的数据  形式为SoapObject对象
			return result;   
		}	
		
		//13.调用LogIn方法（登陆）的soap通讯静态方法   返回一个字符串，存放用户角色
		public static String LogIn(String UserID,String Password)
		{
			//方法名
			final String METHOD_NAME ="LogIn";  
			final String SOAP_ACTION ="http://www.suntrans.net/LogIn";
			//SoapObject detail; 
			SoapObject rpc =new SoapObject(NAMESPACE, METHOD_NAME);  			
			  HttpTransportSE ht =new HttpTransportSE(URL,5000);    //设置访问地址，第二个参数是设置超时的毫秒数
			 ht.debug =true;  
			 SoapSerializationEnvelope envelope =new SoapSerializationEnvelope(SoapEnvelope.VER11);  
			 rpc.addProperty("UserName", UserID);    //账号
			 rpc.addProperty("Password", Password);    //密码			
			 envelope.bodyOut = rpc;  
			 envelope.dotNet =true;  
			 envelope.setOutputSoapObject(rpc);
			Log.i("webservice",rpc.toString());
			try {
				ht.call(SOAP_ACTION, envelope);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
			  Object result = "0";
				 
				try {
					result = envelope.getResponse();
				} catch (SoapFault e) {
					// TODO Auto-generated catch block
					
				}      //result是服务器返回的数据  形式为SoapObject对象
			return result.toString();   //返回result 
		}				
		//14.调用Update_Password方法（修改密码）的soap通讯静态方法   返回一个字符串，存放修改结果
		public static String Update_Password(String UserName,String Password_Old,String Password_New)
		{
			//方法名
			final String METHOD_NAME ="Update_Password";
			final String SOAP_ACTION ="http://www.suntrans.net/Update_Password";
			//SoapObject detail; 
			SoapObject rpc =new SoapObject(NAMESPACE, METHOD_NAME);  			
			  HttpTransportSE ht =new HttpTransportSE(URL,5000);    //设置访问地址，第二个参数是设置超时的毫秒数
			 ht.debug =true;  
			 SoapSerializationEnvelope envelope =new SoapSerializationEnvelope(SoapEnvelope.VER11);  
			 rpc.addProperty("UserName", UserName);    //账号
			 rpc.addProperty("Password_Old", Password_Old);    //密码
			 rpc.addProperty("Password_New", Password_New);    //密码

			 envelope.bodyOut = rpc;  
			 envelope.dotNet =true;  
			 envelope.setOutputSoapObject(rpc);  
			  try {
				ht.call(SOAP_ACTION, envelope);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			  Object result = null;
				 
				try {
					result = envelope.getResponse();
				} catch (SoapFault e) {
					// TODO Auto-generated catch block
					
				}      //result是服务器返回的数据  形式为SoapObject对象
			return result.toString();   //返回result 
		}			
		//15.调用Update_PhoneNum方法（修改手机号）的soap通讯静态方法   返回一个字符串，存放修改结果
		public static String Update_PhoneNum(String UserID,String PhoneNum,String Role)
		{ 
			//命名空间
			final String NAMESPACE="http://www.suntrans.net/";  
			// WebService地址  
			final String URL ="http://210.42.122.127:8080";
			//方法名
			final String METHOD_NAME ="Update_PhoneNum";  
			final String SOAP_ACTION ="http://www.suntrans.net/Update_PhoneNum";
			//SoapObject detail; 
			SoapObject rpc =new SoapObject(NAMESPACE, METHOD_NAME);  			
			  HttpTransportSE ht =new HttpTransportSE(URL,5000);    //设置访问地址，第二个参数是设置超时的毫秒数
			 ht.debug =true;  
			 SoapSerializationEnvelope envelope =new SoapSerializationEnvelope(SoapEnvelope.VER11);  
			 rpc.addProperty("UserID", UserID);    //账号
			 rpc.addProperty("PhoneNum", PhoneNum);    //手机号		
			 rpc.addProperty("Role", Role);    //角色号
			 envelope.bodyOut = rpc;  
			 envelope.dotNet =true;  
			 envelope.setOutputSoapObject(rpc);  
			  try {
				ht.call(SOAP_ACTION, envelope);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
			  Object result = null;
				 
				try {
					result = envelope.getResponse();
				} catch (SoapFault e) {
					// TODO Auto-generated catch block
					
				}      //result是服务器返回的数据  形式为SoapObject对象
			return result.toString();   //返回result 
		}			
		//16.调用Update_PhoneNum方法（插入控制命令）2表示打开，3表示关闭    的soap通讯静态方法   返回一个字符串，存放修改结果
		public static String Insert_Order_RoomID(String UserID,String RoomID,String AccountType,String OrderID)
		{ 
			//命名空间
			final String NAMESPACE="http://www.suntrans.net/";  
			// WebService地址  
			final String URL ="http://210.42.122.127:8080";
			//方法名
			final String METHOD_NAME ="Insert_Order_RoomID";  
			final String SOAP_ACTION ="http://www.suntrans.net/Insert_Order_RoomID";
			//SoapObject detail; 
			SoapObject rpc =new SoapObject(NAMESPACE, METHOD_NAME);  			
			  HttpTransportSE ht =new HttpTransportSE(URL,5000);    //设置访问地址，第二个参数是设置超时的毫秒数
			 ht.debug =true;  
			 SoapSerializationEnvelope envelope =new SoapSerializationEnvelope(SoapEnvelope.VER11);  
			 rpc.addProperty("UserID", UserID);    //账号
			 rpc.addProperty("RoomID", RoomID);    //房间ID		
			 rpc.addProperty("AccountType", AccountType);    //账户类型
			 rpc.addProperty("OrderID",OrderID);  //命令，2表示打开，3表示关闭
			 envelope.bodyOut = rpc;  
			 envelope.dotNet =true;  
			 envelope.setOutputSoapObject(rpc);  
			  try {
				ht.call(SOAP_ACTION, envelope);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
			  Object result = null;
				 
				try {
					result = envelope.getResponse();
				} catch (SoapFault e) {
					// TODO Auto-generated catch block
					
				}      //result是服务器返回的数据  形式为SoapObject对象
			return result.toString();   //返回result 
		}				
		//17.调用Inquiry_Aplication方法(查看学生申请)    的soap通讯静态方法   返回一个soapobject对象，存放修改结果
		public static SoapObject Inquiry_Application(String Count,String Area,String Building,String Unit)
		{ 
			//命名空间
			final String NAMESPACE="http://www.suntrans.net/";  
			// WebService地址  
			final String URL ="http://210.42.122.127:8080";
			//方法名
			final String METHOD_NAME ="Inquiry_Application";  
			final String SOAP_ACTION ="http://www.suntrans.net/Inquiry_Application";
			//SoapObject detail; 
			SoapObject rpc =new SoapObject(NAMESPACE, METHOD_NAME);  			
			  HttpTransportSE ht =new HttpTransportSE(URL,5000);    //设置访问地址，第二个参数是设置超时的毫秒数
			 ht.debug =true;  
			 SoapSerializationEnvelope envelope =new SoapSerializationEnvelope(SoapEnvelope.VER11);  
			 rpc.addProperty("Count", Count);    //数量
			 rpc.addProperty("Area", Area);    //园区		
			 rpc.addProperty("Building", Building);    //楼栋
			 rpc.addProperty("Unit",Unit);  //单元
			 envelope.bodyOut = rpc;  
			 envelope.dotNet =true;  
			 envelope.setOutputSoapObject(rpc);  
			  try {
				ht.call(SOAP_ACTION, envelope);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
			  SoapObject result = null;
				 
				try {
					result = (SoapObject)envelope.getResponse();
				} catch (SoapFault e) {
					// TODO Auto-generated catch block
					
				}      //result是服务器返回的数据  形式为SoapObject对象
			return result;   //返回result 
		}		
		
		//18.调用Check_Aplication方法(审核学生申请)    的soap通讯静态方法   返回一个字符串，存放修改结果，1代表修改成功，0代表修改失败
		public static String Check_Application(String AID,String IsPassed,String UserID)
		{ 
			//命名空间
			final String NAMESPACE="http://www.suntrans.net/";  
			// WebService地址  
			final String URL ="http://210.42.122.127:8080";
			//方法名
			final String METHOD_NAME ="Inquiry_Application";  
			final String SOAP_ACTION ="http://www.suntrans.net/Inquiry_Application";
			//SoapObject detail; 
			SoapObject rpc =new SoapObject(NAMESPACE, METHOD_NAME);  			
			  HttpTransportSE ht =new HttpTransportSE(URL,5000);    //设置访问地址，第二个参数是设置超时的毫秒数
			 ht.debug =true;  
			 SoapSerializationEnvelope envelope =new SoapSerializationEnvelope(SoapEnvelope.VER11);  
			 rpc.addProperty("AID", AID);    //申请ID
			 rpc.addProperty("IsPassed", IsPassed);    //是否通过，1代表通过，0代表不通过		
			 rpc.addProperty("UserID", UserID);    //账号			
			 envelope.bodyOut = rpc;  
			 envelope.dotNet =true;  
			 envelope.setOutputSoapObject(rpc);  
			  try {
				ht.call(SOAP_ACTION, envelope);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
			  Object result = null;
				 
				try {
					result = envelope.getResponse();
				} catch (SoapFault e) {
					// TODO Auto-generated catch block
					
				}      //result是服务器返回的数据  形式为SoapObject对象
			return result.toString();   //返回result 
		}			
		
		//19.调用Inquiry_HisAplication方法(查看历史学生申请)    的soap通讯静态方法   返回一个soapobject对象，存放修改结果
		public static SoapObject Inquiry_HisApplication(String Count,String Area,String Building,String Unit)
		{ 
			//命名空间
			final String NAMESPACE="http://www.suntrans.net/";  
			// WebService地址  
			final String URL ="http://210.42.122.127:8080";
			//方法名
			final String METHOD_NAME ="Inquiry_HisApplication";  
			final String SOAP_ACTION ="http://www.suntrans.net/Inquiry_HisApplication";
			//SoapObject detail; 
			SoapObject rpc =new SoapObject(NAMESPACE, METHOD_NAME);  			
			  HttpTransportSE ht =new HttpTransportSE(URL,5000);    //设置访问地址，第二个参数是设置超时的毫秒数
			 ht.debug =true;  
			 SoapSerializationEnvelope envelope =new SoapSerializationEnvelope(SoapEnvelope.VER11);  
			 rpc.addProperty("Count", Count);    //数量
			 rpc.addProperty("Area", Area);    //园区		
			 rpc.addProperty("Building", Building);    //楼栋
			 rpc.addProperty("Unit",Unit);  //单元
			 envelope.bodyOut = rpc;  
			 envelope.dotNet =true;  
			 envelope.setOutputSoapObject(rpc);  
			  try {
				ht.call(SOAP_ACTION, envelope);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
			  SoapObject result = null;
				 
				try {
					result = (SoapObject)envelope.getResponse();
				} catch (SoapFault e) {
					// TODO Auto-generated catch block
					
				}      //result是服务器返回的数据  形式为SoapObject对象
			return result;   //返回result 
		}					
		//20.调用Insert_Aplication方法(插入学生申请)    的soap通讯静态方法   返回一个字符串，存放修改结果，1代表修改成功，0代表修改失败
		public static String Insert_Application(String UserID,String Application)
		{ 
			//命名空间
			final String NAMESPACE="http://www.suntrans.net/";  
			// WebService地址  
			final String URL ="http://210.42.122.127:8080";
			//方法名
			final String METHOD_NAME ="Insert_Application";  
			final String SOAP_ACTION ="http://www.suntrans.net/Insert_Application";
			//SoapObject detail; 
			SoapObject rpc =new SoapObject(NAMESPACE, METHOD_NAME);  			
			  HttpTransportSE ht =new HttpTransportSE(URL,5000);    //设置访问地址，第二个参数是设置超时的毫秒数
			 ht.debug =true;  
			 SoapSerializationEnvelope envelope =new SoapSerializationEnvelope(SoapEnvelope.VER11);  			 
			 rpc.addProperty("UserID", UserID);    //账号			
			 rpc.addProperty("Application",Application);  //申请理由
			 envelope.bodyOut = rpc;  
			 envelope.dotNet =true;  
			 envelope.setOutputSoapObject(rpc);  
			  try {
				ht.call(SOAP_ACTION, envelope);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
			  Object result = null;
				 
				try {
					result = envelope.getResponse();
				} catch (SoapFault e) {
					// TODO Auto-generated catch block
					
				}      //result是服务器返回的数据  形式为SoapObject对象
			return result.toString();   //返回result 
		}			
				
		//21.调用Inquiry_Aplication_StudentID方法(查看学生申请)    的soap通讯静态方法   返回一个soapobject对象，存放修改结果
		public static SoapObject Inquiry_Application_StudentID(String StudentID)
		{ 
			//命名空间
			final String NAMESPACE="http://www.suntrans.net/";  
			// WebService地址  
			final String URL ="http://210.42.122.127:8080";
			//方法名
			final String METHOD_NAME ="Inquiry_Application_StudentID";  
			final String SOAP_ACTION ="http://www.suntrans.net/Inquiry_Application_StudentID";
			//SoapObject detail; 
			SoapObject rpc =new SoapObject(NAMESPACE, METHOD_NAME);  			
			  HttpTransportSE ht =new HttpTransportSE(URL,5000);    //设置访问地址，第二个参数是设置超时的毫秒数
			 ht.debug =true;  
			 SoapSerializationEnvelope envelope =new SoapSerializationEnvelope(SoapEnvelope.VER11);  
			 rpc.addProperty("StudentID", StudentID);    //学号		
			 envelope.bodyOut = rpc;  
			 envelope.dotNet =true;  
			 envelope.setOutputSoapObject(rpc);  
			  try {
				ht.call(SOAP_ACTION, envelope);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
			  SoapObject result = null;
				 
				try {
					result = (SoapObject)envelope.getResponse();
				} catch (SoapFault e) {
					// TODO Auto-generated catch block
					
				}      //result是服务器返回的数据  形式为SoapObject对象
			return result;   //返回result 
		}					
		
		//22.调用Inquiry_Version方法(查看最新版本号)    的soap通讯静态方法   返回一个soapobject对象，存放信息
		public static SoapObject Inquiry_Version()
		{ 
			//命名空间
			final String NAMESPACE="http://www.suntrans.net/";  
			// WebService地址  
			final String URL ="http://210.42.122.127:8080";
			//方法名
			final String METHOD_NAME ="Inquiry_Version";  
			final String SOAP_ACTION ="http://www.suntrans.net/Inquiry_Version";
			//SoapObject detail; 
			SoapObject rpc =new SoapObject(NAMESPACE, METHOD_NAME);  			
			  HttpTransportSE ht =new HttpTransportSE(URL,5000);    //设置访问地址，第二个参数是设置超时的毫秒数
			 ht.debug =true;  
			 SoapSerializationEnvelope envelope =new SoapSerializationEnvelope(SoapEnvelope.VER11);
			 rpc.addProperty("Type", "Android");    //类型，Android	
			 envelope.bodyOut = rpc;  
			 envelope.dotNet =true;  
			 envelope.setOutputSoapObject(rpc);  
			  try {
				ht.call(SOAP_ACTION, envelope);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
			  SoapObject result = null;
				 
				try {
					result = (SoapObject)envelope.getResponse();
				} catch (SoapFault e) {
					// TODO Auto-generated catch block
					
				}      //result是服务器返回的数据  形式为SoapObject对象
			return result;   //返回result 
		}					
		//23.调用Inquiry_Threephase_R方法(查看三相电表当前用电状态)    的soap通讯静态方法   返回一个soapobject对象，存放信息
		public static SoapObject Inquiry_Threephase_R(String Area,String Building,String Unit,String AccountType)
		{ 
			//命名空间
			final String NAMESPACE="http://www.suntrans.net/";  
			// WebService地址  
			final String URL ="http://210.42.122.127:8080";
			//方法名
			final String METHOD_NAME ="Inquiry_Threephase_R";  
			final String SOAP_ACTION ="http://www.suntrans.net/Inquiry_Threephase_R";
			//SoapObject detail; 
			SoapObject rpc =new SoapObject(NAMESPACE, METHOD_NAME);  			
			  HttpTransportSE ht =new HttpTransportSE(URL,5000);    //设置访问地址，第二个参数是设置超时的毫秒数
			 ht.debug =true;  
			 SoapSerializationEnvelope envelope =new SoapSerializationEnvelope(SoapEnvelope.VER11);  
			
			 rpc.addProperty("Area", Area);    //园区	
			 rpc.addProperty("Building", Building);    //楼栋
			 rpc.addProperty("Unit", Unit);    //单元
			 rpc.addProperty("AccountType", AccountType);    //账户类型
			 envelope.bodyOut = rpc;  
			 envelope.dotNet =true;  
			 envelope.setOutputSoapObject(rpc);  
			  try {
				ht.call(SOAP_ACTION, envelope);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
			  SoapObject result = null;
				 
				try {
					result = (SoapObject)envelope.getResponse();
				} catch (SoapFault e) {
					// TODO Auto-generated catch block
					
				}      //result是服务器返回的数据  形式为SoapObject对象
			return result;   //返回result 
		}	

		//24.调用Inquiry_Threephase_H方法(查看三相电表历史用电状态)    的soap通讯静态方法   返回一个soapobject对象，存放信息
		public static SoapObject Inquiry_Threephase_H(String Area,String Building,String Unit,String AccountType,String Field,String Freq,String StartTime,String EndTime)
		{ 
			//命名空间
			final String NAMESPACE="http://www.suntrans.net/";  
			// WebService地址  
			final String URL ="http://210.42.122.127:8080";
			//方法名
			final String METHOD_NAME ="Inquiry_Threephase_H";  
			final String SOAP_ACTION ="http://www.suntrans.net/Inquiry_Threephase_H";
			//SoapObject detail; 
			SoapObject rpc =new SoapObject(NAMESPACE, METHOD_NAME);  			
			  HttpTransportSE ht =new HttpTransportSE(URL,5000);    //设置访问地址，第二个参数是设置超时的毫秒数
			 ht.debug =true;  
			 SoapSerializationEnvelope envelope =new SoapSerializationEnvelope(SoapEnvelope.VER11);  
			
			 rpc.addProperty("Area", Area);    //园区	
			 rpc.addProperty("Building", Building);    //楼栋
			 rpc.addProperty("Unit", Unit);    //单元
			 rpc.addProperty("AccountType", AccountType);    //账户类型
			 rpc.addProperty("Field", Field);    //查询内容VolA,VolB,VolC,IA,IB,IC,ActivePower,ReactivePower,PowerRateC,EletricityValue,Eletricity
			 rpc.addProperty("Freq", Freq);    //查询间隔小时数
			 rpc.addProperty("StartTime", StartTime);    //开始时间
			 rpc.addProperty("EndTime", EndTime);    //结束时间
			 envelope.bodyOut = rpc;  
			 envelope.dotNet =true;  
			 envelope.setOutputSoapObject(rpc);  
			  try {
				ht.call(SOAP_ACTION, envelope);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
			  SoapObject result = null;
				 
				try {
					result = (SoapObject)envelope.getResponse();
				} catch (SoapFault e) {
					// TODO Auto-generated catch block
					
				}      //result是服务器返回的数据  形式为SoapObject对象
			return result;   //返回result 
		}					
}
	 
