package cn.edu.hcxy.client;

import cn.edu.hcxy.util.MD5;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

public class LoginThread extends Thread {
    private JFrame loginf;

    private JTextField t;
    public void run() {
        /*
         * 设置登录界面
         */
        loginf = new JFrame();
        loginf.setResizable(false);
        loginf.setLocation(300, 200);
        loginf.setSize(400, 150);
        loginf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginf.setTitle("聊天室" + " - 登录");

        t = new JTextField("Version " + "1.1.0" + "        By liwei");
        t.setHorizontalAlignment(JTextField.CENTER);
        t.setEditable(false);
        loginf.getContentPane().add(t, BorderLayout.SOUTH);

        JPanel loginp = new JPanel(new GridLayout(3, 2));
        loginf.getContentPane().add(loginp);

        JTextField t1 = new JTextField("username:");
        t1.setHorizontalAlignment(JTextField.CENTER);
        t1.setEditable(false);
        loginp.add(t1);

        final JTextField loginname = new JTextField("liwei");
        loginname.setHorizontalAlignment(JTextField.CENTER);
        loginp.add(loginname);

        JTextField t2 = new JTextField("password:");
        t2.setHorizontalAlignment(JTextField.CENTER);
        t2.setEditable(false);
        loginp.add(t2);

        final JTextField loginPassword = new JTextField("lw1234");
        loginPassword.setHorizontalAlignment(JTextField.CENTER);
        loginp.add(loginPassword);
        /*
         * 监听退出按钮(匿名内部类)
         */
        JButton b1 = new JButton("Exit");
        loginp.add(b1);
        b1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        final JButton b2 = new JButton("Sign in");
        loginp.add(b2);

        loginf.setVisible(true);

        /**
         * 监听器,监听"登录"Button的点击和TextField的回车
         */
        class ButtonListener implements ActionListener {
            private Socket s;

            public void actionPerformed(ActionEvent e) {
                String username=loginname.getText();
                String password=loginPassword.getText();
                String sql="";
                try {
                    String url = "jdbc:oracle:thin:@localhost:1521:orcl";
                    String username_db = "opts";
                    String password_db = "opts1234";
                    Connection conn = null;
                    conn = DriverManager.getConnection(url, username_db, password_db);
                    sql = "select password from users where username=?";

                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1,username);
                    ResultSet rs = pstmt.executeQuery();
                    if (rs.next())
                    {
                        String oldPassword=rs.getString("PASSWORD");
                        if (MD5.checkpassword(password,oldPassword))
                        {

                            int port=1688;
                            DatagramSocket ds = null;
                            InetAddress ip4 = InetAddress.getLocalHost();
                            System.out.println(ip4.getHostAddress());
                            while(true)
                            {
                                try {
                                    ds=new DatagramSocket(port);
                                    break;
                                } catch (IOException ex) {
                                    port++;

                                }
                            }

                            sql="UPDATE users set IP=?,port=? ,status=? where USERNAME=?";


                            pstmt=conn.prepareStatement(sql);

                            pstmt.setString(1,ip4.getHostAddress());
                            pstmt.setInt(2,port);
                            pstmt.setString(3,"online");
                            pstmt.setString(4,username);
                            pstmt.executeUpdate();
                            loginf.setVisible(false);

                            ChatThreadWindow chatThreadWindow=new ChatThreadWindow(username,ds);
                        }
                        else
                        {
                            System.out.println("no");
                        }
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                } catch (NoSuchAlgorithmException ex) {
                    ex.printStackTrace();
                } catch (UnsupportedEncodingException ex) {
                    ex.printStackTrace();
                } catch (UnknownHostException ex) {
                    ex.printStackTrace();
                }

				/*
				1、根据用户去数据库把加密后的密码拿到
				SELECT password FROM users WHERE username='liwei';
				2、把登录界面输入的密码和数据库里加密后的进行比对（调用MD5类的checkpassword方法）
				 */
            }
        }
        ButtonListener bl = new ButtonListener();
        b2.addActionListener(bl);
        loginname.addActionListener(bl);
        loginPassword.addActionListener(bl);
    }
}
