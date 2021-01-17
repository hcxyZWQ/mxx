package cn.edu.hcxy.client;

import javax.swing.*;
import java.io.IOException;
import java.net.*;
import java.sql.*;

public class GetMessageThread extends Thread{
    JComboBox cb;//��Ͽ�
    DatagramSocket ds;//����Ψһ���þ��ǽ��պͷ������ݱ���Javaʹ��DatagramPacket���������ݱ���DatagramSocket���պͷ��͵����ݶ���ͨ��DatagramPacket������ɵġ�
    JTextArea ta;//�����ı���
    public GetMessageThread(ChatThreadWindow cwt)
    {
        this.cb=cwt.cb;
        this.ds=cwt.ds;
        this.ta=cwt.ta;
    }
    public void run()//����xxx������������
    {
        /*
        DatagramPacket(byte[] buf, int length, InetAddress address, int port)
          �������ݱ���������������Ϊ length �İ����͵�ָ�������ϵ�ָ���˿ںš�

        DatagramPacket(byte[] buf, int length)
          ���� DatagramPacket���������ճ���Ϊ length �����ݰ�

        * byte[] getData() ���������ݻ�������
        int getLength() �����ؽ�Ҫ���ͻ���յ������ݵĳ��ȡ�
        InetAddress getAddress() ������ĳ̨������ IP ��ַ�������ݱ���Ҫ�����û��������ǴӸû������յ�*/


        try {
            while(true) {
                byte buff[] = new byte[1024];//�ֽ�����1024����
                DatagramPacket dp=new DatagramPacket(buff,200);
                ds.receive(dp);//���庯��
                String masssgre = new String(buff, 0, dp.getLength());//��ȡ���ݰ��ĳ��ȣ������ֽ������û����ַ�����
                ta.append(masssgre);//��ʵ�Ǵ�����һ���µ���bai�飬�����˳��ȣ�����Ҫ��ӵ��ַ��������Ƶ�����µ�������
                if (masssgre.contains("������������")) {
                    masssgre = masssgre.replace("������������", "");
                    System.out.println("��������Ϣ" + masssgre);
                }

                cb.addItem(masssgre);//����ȡ�����ַ������������б�
                System.out.println("UDP�յ�����Ϣ" + masssgre);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
