package cn.edu.hcxy.client;

import javax.swing.*;
import java.io.IOException;
import java.net.*;
import java.sql.*;

public class GetMessageThread extends Thread{
    JComboBox cb;//组合框
    DatagramSocket ds;//它的唯一作用就是接收和发送数据报，Java使用DatagramPacket来代表数据报，DatagramSocket接收和发送的数据都是通过DatagramPacket对象完成的。
    JTextArea ta;//创造文本域
    public GetMessageThread(ChatThreadWindow cwt)
    {
        this.cb=cwt.cb;
        this.ds=cwt.ds;
        this.ta=cwt.ta;
    }
    public void run()//发起xxx进入了聊天室
    {
        /*
        DatagramPacket(byte[] buf, int length, InetAddress address, int port)
          构造数据报包，用来将长度为 length 的包发送到指定主机上的指定端口号。

        DatagramPacket(byte[] buf, int length)
          构造 DatagramPacket，用来接收长度为 length 的数据包

        * byte[] getData() ：返回数据缓冲区。
        int getLength() ：返回将要发送或接收到的数据的长度。
        InetAddress getAddress() ：返回某台机器的 IP 地址，此数据报将要发往该机器或者是从该机器接收到*/


        try {
            while(true) {
                byte buff[] = new byte[1024];//字节数字1024长度
                DatagramPacket dp=new DatagramPacket(buff,200);
                ds.receive(dp);//缓冲函数
                String masssgre = new String(buff, 0, dp.getLength());//读取数据包的长度，并将字节数组置换成字符串，
                ta.append(masssgre);//其实是创建了一个新的数bai组，扩大了长度，将需要添加的字符串给复制到这个新的数组中
                if (masssgre.contains("进入了聊天室")) {
                    masssgre = masssgre.replace("进入了聊天室", "");
                    System.out.println("处理后的消息" + masssgre);
                }else if (masssgre.contains("正在聊天室")) {
                    masssgre = masssgre.replace("正在聊天室", "");
                    System.out.println("处理后的消息" + masssgre);
                }

                cb.addItem(masssgre);//将截取到的字符串放入下拉列表
                System.out.println("UDP收到的消息" + masssgre);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
