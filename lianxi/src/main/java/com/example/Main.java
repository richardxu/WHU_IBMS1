package com.example;

import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args){
//        System.out.println("Hellow World");
//        Scanner sc=new Scanner(System.in);
//        int a=sc.nextInt();
//        System.out.println("�������������"+a);
     /*   Calendar c=Calendar.getInstance();
        c.setTime(new Date());
        int hour=c.get(Calendar.HOUR_OF_DAY);   //24Сʱ�Ƶ�Сʱ��
        int day = c.get(Calendar.DAY_OF_MONTH);
        System.out.println(hour);
        System.out.println(day);*/
//        ArrayList<Map<String, String>> data = new ArrayList<>();
//        Map<String, String> map1 = new HashMap<>();
//        map1.put("Name", "��ѹ");
//        data.add(map1);
//        Map<String, String> map2 = new HashMap<>();
//        map2.put("Name", "����");
//        data.add(map2);
//        Map<String, String> map3 = new HashMap<>();
//        map3.put("Name", "��ˮλ");
//        data.add(map3);
//        Map<String, String> map4 = new HashMap<>();
//        map4.put("Name", "Ͷ��ʽˮλ");
//        data.add(map4);
//
////        System.out.println(map1.get("Value"));
//        for (int i=0;i<10;i++)
//        new Thread(){
//            @Override
//            public void run(){
//                for(int i=0;i<8;i++) {
//                    try {
//                        final InetAddress serverAddr = InetAddress.getByName("192.168.0.111");
//                        Socket client = new Socket(serverAddr, 8009);
//                    } catch (Exception e) {
//                        System.out.println(e.toString());
//                    }
//
//                }
//            }
//        }.start();
//
//        Scanner sc=new Scanner(System.in);
//        int a =sc.nextInt();
  /*      ArrayList<Map<String, String>> data1 = new ArrayList<Map<String, String>>();
        Map<String,String> map0=new HashMap<>();
        for(Map<String,String> map:data){
            if(!map.get("Name").equals("��ѹ"))
                data1.add(new HashMap<>(map));
            else
                map0=new HashMap<>(map);
//            System.out.println(map.get("Name"));
        }
        data1.add(map0);

         for(Map<String,String> map:data1){
            System.out.println(map.get("Name"));
        }
*/

        for(int i=63;i<10000;i=i+63){
            if(i%7==0&&i%9==0&i%5==1&&i%6==3&&i%8==1)
                System.out.println(i);
        }
    }
}
