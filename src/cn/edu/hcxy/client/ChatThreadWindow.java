package cn.edu.hcxy.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.*;
import java.sql.*;

/**
 * �����߳�
 */
public class ChatThreadWindow {
    private String name;//��ǰҳ����û���
    JComboBox cb;
    private JFrame f;
    JTextArea ta;
    private JTextField tf;
    private static int total;// ��������ͳ��
    DatagramSocket ds;

    public ChatThreadWindow(String name, DatagramSocket ds) {
        this.ds = ds;
        this.name=name;
        /*
         * ���������Ҵ��ڽ���
         */
        f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(600, 400);
        f.setTitle("������" + " - " + name + "     ��ǰ��������:" + ++total);
        f.setLocation(300, 200);
        ta = new JTextArea();
        JScrollPane sp = new JScrollPane(ta);
        ta.setEditable(false);
        tf = new JTextField();
        cb = new JComboBox();
        cb.addItem("All");
        JButton jb = new JButton("˽�Ĵ���");
        JPanel pl = new JPanel(new BorderLayout());
        pl.add(cb);
        pl.add(jb, BorderLayout.WEST);
        JPanel p = new JPanel(new BorderLayout());
        p.add(pl, BorderLayout.WEST);
        p.add(tf);
        f.getContentPane().add(p, BorderLayout.SOUTH);
        f.getContentPane().add(sp);
        f.setVisible(true);
        GetMessageThread getMessageThread = new GetMessageThread(this);//���ڴ��Ĳ������࣬��ֱ����this
        getMessageThread.start();

        /*
        ��ʾXXX����������
         */
        showXXXIntoChatRoom();

        showXXXInChatRoom();

        tf.addKeyListener(
                new KeyListener() {
                    @Override
                    public void keyTyped(KeyEvent e) {

                    }

                    @Override
                    public void keyPressed(KeyEvent e) {
                        if (e.getKeyCode()==KeyEvent.VK_ENTER)
                        {

                           String massageTo= (String) cb.getSelectedItem();//��ȡ��Ӧ��ѡ��������
                            if("All".equals(massageTo))
                            {
                                //����Ⱥ����Ϣ��
                                sendAll();
                            }
                            else {
                                //����˽����Ϣ
                                privateSend();
                            }
                        }
                    }

                    @Override
                    public void keyReleased(KeyEvent e) {

                    }
                }
        );
    }
    public void sendAll() {//����Ⱥ����Ϣ
        String url = "jdbc:oracle:thin:@localhost:1521:orcl";
        String username_db = "opts";
        String password_db = "opts1234";
        PreparedStatement pstmt = null;
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, username_db, password_db);
            String sql = "SELECT username,ip,port FROM users WHERE status='online'";
            pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String username=rs.getString("USERNAME");//��ȡsql���û���
                String ip = rs.getString("IP");
                int port = rs.getInt("PORT");
                System.out.println(ip);
                System.out.println(port);
                byte[] ipB = new byte[4];

                String ips[] = ip.split("\\.");
                for (int i = 0; i < ips.length; i++) {
                    ipB[i] = (byte)Integer.parseInt(ips[i]);
                }
                if(!username.equals(name))//���������������ȡ���Ĳ�һ��  ������������Ϣ
                {
                    String message = tf.getText();//��ȡҪ���͵�Ⱥ����Ϣ
                    byte[] m = message.getBytes();
                    DatagramPacket dp = new DatagramPacket(m, m.length);
                    dp.setAddress(InetAddress.getByAddress(ipB));
                    dp.setPort(port);
                    DatagramSocket ds = new DatagramSocket();
                    ds.send(dp);//Ͷ��
                    System.out.println("\n");
                }
            }
        } catch (SQLException | UnknownHostException | SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void privateSend() {//����Ⱥ����Ϣ
        String url = "jdbc:oracle:thin:@localhost:1521:orcl";
        String username_db = "opts";
        String password_db = "opts1234";
        PreparedStatement pstmt = null;
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, username_db, password_db);
            String sql = "SELECT username,ip,port FROM users WHERE status='online' AND USERNAME=?";
            pstmt = conn.prepareStatement(sql);
            String massageTo= (String) cb.getSelectedItem();
            pstmt.setString(1,massageTo);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String username=rs.getString("USERNAME");//��ȡsql���û���
                String ip = rs.getString("IP");
                int port = rs.getInt("PORT");
                System.out.println(ip);
                System.out.println(port);
                byte[] ipB = new byte[4];

                String ips[] = ip.split("\\.");
                for (int i = 0; i < ips.length; i++) {
                    ipB[i] = (byte)Integer.parseInt(ips[i]);
                }
                if(!username.equals(name))
                {
                    String message = tf.getText();//��ȡҪ���͵�Ⱥ����Ϣ
                    byte[] m = message.getBytes();
                    DatagramPacket dp = new DatagramPacket(m, m.length);
                    dp.setAddress(InetAddress.getByAddress(ipB));
                    dp.setPort(port);
                    DatagramSocket ds = new DatagramSocket();
                    ds.send(dp);//Ͷ��
                    System.out.println("\n");
                }
            }
        } catch (SQLException | UnknownHostException | SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void showXXXIntoChatRoom() {
        String url = "jdbc:oracle:thin:@localhost:1521:orcl";
        String username_db = "opts";
        String password_db = "opts1234";
        PreparedStatement pstmt = null;
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, username_db, password_db);
            String sql = "SELECT username,ip,port FROM users WHERE status='online'";
            pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String username=rs.getString("USERNAME");//��ȡsql���û���
                String ip = rs.getString("IP");
                int port = rs.getInt("PORT");
                System.out.println(ip);
                System.out.println(port);
                byte[] ipB = new byte[4];

                String ips[] = ip.split("\\.");
                for (int i = 0; i < ips.length; i++) {
                    ipB[i] = (byte)Integer.parseInt(ips[i]);
                }
                if(!username.equals(name))//���������������ȡ���Ĳ�һ��  ������������Ϣ
                {
                    String message = name+"������������";
                    byte[] m = message.getBytes();
                    DatagramPacket dp = new DatagramPacket(m, m.length);
                    dp.setAddress(InetAddress.getByAddress(ipB));
                    dp.setPort(port);
                    DatagramSocket ds = new DatagramSocket();
                    ds.send(dp);//Ͷ��
                }
            }
        } catch (SQLException | UnknownHostException | SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void showXXXInChatRoom() {
        String url = "jdbc:oracle:thin:@localhost:1521:orcl";
        String username_db = "opts";
        String password_db = "opts1234";
        PreparedStatement pstmt = null;
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, username_db, password_db);
            String sql = "SELECT username,ip,port FROM users WHERE status='online' AND username!=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1,name);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String username=rs.getString("USERNAME");//��ȡsql���û���
                String message = username+"����������";
                ta.append(message);
                cb.addItem(message);
                }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}