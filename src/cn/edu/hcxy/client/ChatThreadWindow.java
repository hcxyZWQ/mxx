package cn.edu.hcxy.client;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.*;
import java.sql.*;

/**
 * 聊天线程
 */
public class ChatThreadWindow {
    private String name;//当前页面的用户名
    JComboBox cb;
    private JFrame f;
    JTextArea ta;
    private JTextField tf;
    private static int total;// 在线人数统计
    DatagramSocket ds;

    public ChatThreadWindow(String name, DatagramSocket ds) {
        this.ds = ds;
        this.name=name;
        /*
         * 设置聊天室窗口界面
         */
        f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(600, 400);
        f.setTitle("聊天室" + " - " + name + "     当前在线人数:" + ++total);
        f.setLocation(300, 200);
        ta = new JTextArea();
        JScrollPane sp = new JScrollPane(ta);
        ta.setEditable(false);
        tf = new JTextField();
        cb = new JComboBox();
        cb.addItem("All");
        JButton jb = new JButton("私聊窗口");
        JPanel pl = new JPanel(new BorderLayout());
        pl.add(cb);
        pl.add(jb, BorderLayout.WEST);
        JPanel p = new JPanel(new BorderLayout());
        p.add(pl, BorderLayout.WEST);
        p.add(tf);
        f.getContentPane().add(p, BorderLayout.SOUTH);
        f.getContentPane().add(sp);
        f.setVisible(true);
        GetMessageThread getMessageThread = new GetMessageThread(this);//由于传的参数过多，可直接用this
        getMessageThread.start();

        /*
        提示XXX进入聊天室
         */
        showXXXIntoChatRoom();
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
                String username=rs.getString("USERNAME");//获取sql的用户名
                String ip = rs.getString("IP");
                int port = rs.getInt("PORT");
                System.out.println(ip);
                System.out.println(port);
                byte[] ipB = new byte[4];

                String ips[] = ip.split("\\.");
                for (int i = 0; i < ips.length; i++) {
                    ipB[i] = (byte)Integer.parseInt(ips[i]);
                }
                if(!username.equals(name))//如果输入的姓名与获取到的不一致  则向她发送信息
                {
                    String message = name+"进入了聊天室";
                    byte[] m = message.getBytes();
                    DatagramPacket dp = new DatagramPacket(m, m.length);
                    dp.setAddress(InetAddress.getByAddress(ipB));
                    dp.setPort(port);
                    DatagramSocket ds = new DatagramSocket();
                    ds.send(dp);//投递
                }else if(username.equals(name))//如果输入的姓名与获取到的不一致  则向她发送信息
                {
                    String message = username+"正在聊天室";
                    byte[] m = message.getBytes();
                    DatagramPacket dp = new DatagramPacket(m, m.length);
                    dp.setAddress(InetAddress.getByAddress(ipB));
                    dp.setPort(port);

                    DatagramSocket ds = new DatagramSocket();
                    ds.send(dp);//投递
                }
            }
        } catch (SQLException | UnknownHostException | SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}