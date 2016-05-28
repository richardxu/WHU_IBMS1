package com.example;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.ksoap2.transport.HttpsTransportSE;
import org.kxml2.kdom.Element;
import org.kxml2.kdom.Node;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

/**
 * Created by 石奋斗 on 2016/4/18.
 */
public class WebServices {
    public static void main(String[] args){
        long time=new Date().getTime();
        System.out.println(time);
        //命名空间
        final String NAMESPACE="http://www.suntrans.net/";
        // WebService地址
        final String URL ="http://192.168.0.111:8008/";
        //方法名
        final String METHOD_NAME ="Inquiry_Building";
        final String SOAP_ACTION ="http://www.suntrans.net/Inquiry_Building";
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
        }
        catch(Exception e){
            e.printStackTrace();
        }
        SoapObject result = null;
        try{
            result=(SoapObject)envelope.getResponse();

        }
        catch(Exception e){}

        System.out.println(result.toString());

        ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();
        try{
            result = (SoapObject)result.getProperty(1);
            if(result.getPropertyCount()>=1)
                result = (SoapObject)result.getProperty(0);  //有数据  则继续

            for(int i=0;  i< result.getPropertyCount(); i++ ){
                SoapObject soap = (SoapObject) result.getProperty(i);
                Map<String, String> map=new HashMap<String,String>();
                if(!soap.hasProperty("RoomType"))
                    System.out.println(soap.hasProperty("RoomID")+"没有roomtype");
//                try{map.put("GetTime", soap.getProperty("GetTime").toString());}catch(Exception e){}   //更新时间
//                try{map.put("RoomID", soap.getProperty("RoomID").toString());}catch(Exception e){}     //房间RoomID
//                try{map.put("Status", soap.getProperty("Status").toString());}catch(Exception e){}     //电表状态，0：正常；  1：恶性负载；  2：电表锁定；  3：等待
//                try{map.put("State", soap.getProperty("State").toString());}catch(Exception e){}       //开关状态，0：关；   1：开
//                try{map.put("U", soap.getProperty("U").toString());}catch(Exception e){}           //当前电压   ，单位：V
//                try{map.put("I", soap.getProperty("I").toString());}catch(Exception e){}           //当前电流   ，  单位：A
//                try{map.put("Power", soap.getProperty("Power").toString());}catch(Exception e){}   //当前功率   ，单位：W
//                try{map.put("PowerRate", soap.getProperty("PowerRate").toString());}catch(Exception e){} //当前功率因数
//                try{map.put("Eletricity", soap.getProperty("Eletricity").toString());}catch(Exception e){}   //当前总共用电量
//                try{map.put("PreChargeback", soap.getProperty("PreChargeback").toString());}catch(Exception e){}   //账户剩余金额
//                try{map.put("PreSubsidy", soap.getProperty("PreSubsidy").toString());}catch(Exception e){}   //剩余补贴，单位：元
//                try{map.put("AccountStatus", soap.getProperty("AccountStatus").toString());}catch(Exception e){}   //账户状态，字符串，有“正常”，“余额不足”，“冻结”三个状态
//                try{map.put("AccountType", soap.getProperty("AccountType").toString());}catch(Exception e){}   //账户类型，与输入相同
//                try{map.put("ElecMonth", soap.getProperty("ElecMonth").toString());}catch(Exception e){}   //本月总用电量
//                try{map.put("ElecDay", soap.getProperty("ElecDay").toString());}catch(Exception e){}   //本日总用电量

                data.add(map);

            }
            System.out.println(result.getPropertyCount());

        }
        catch(Exception e){
            e.printStackTrace();
        }
        time=new Date().getTime();
        System.out.println(time);
    }


}
