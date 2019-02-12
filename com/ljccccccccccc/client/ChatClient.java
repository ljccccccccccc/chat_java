package com.ljccccccccccc.client;


import com.ljccccccccccc.captureScreen.*;

import com.sun.security.ntlm.Client;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import static com.ljccccccccccc.captureScreen.CaptureScreen.captureScreen;

/**
 *
 * @author ljccccccccccc
 * 客户端主界面
 */
public final class ChatClient extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;

    String ip = "127.0.0.1";  //连接到服务端的ip地址
    int port = 8888;         //连接到服务端的端口号
    String userName = "ljccccccccccc";    //用户名
    int type = 0;      //用户连接标记，其中0表示未连接，1表示已连接

    JComboBox combobox;   //选择发送消息的接受者
    JTextArea messageShow;  //客户端的信息显示
    JScrollPane messageScrollPane; //信息显示的滚动条

    JLabel express, sendToLabel, messageLabel;

    JTextField clientMessage;//客户端消息的发送
    JCheckBox checkbox;//悄悄话
    JComboBox actionlist;//表情选择
    JButton clientMessageButton;//发送消息
    JTextField showStatus;//显示用户连接状态

    Socket socket;
    ObjectOutputStream output;//网络套接字输出流
    ObjectInputStream input;//网络套接字输入流

    ClientReceive recvThread;

    //建立菜单栏
    JMenuBar jMenuBar = new JMenuBar();
    //建立菜单组
    JMenu operateMenu = new JMenu("操作");
    //建立菜单项
    JMenuItem loginItem = new JMenuItem("用户登录");
    JMenuItem logoffItem = new JMenuItem("用户注销");
    JMenuItem exitItem = new JMenuItem("退出");

    JMenu conMenu = new JMenu("设置");
    JMenuItem userItem = new JMenuItem("用户设置");
    JMenuItem connectItem = new JMenuItem("连接设置");

    JMenu helpMenu = new JMenu("帮助");
    JMenuItem helpItem = new JMenuItem("帮助");

    //建立工具栏
    JToolBar toolBar = new JToolBar();

    //建立工具栏中的按钮组件
    JButton loginButton;//用户登录
    JButton logoffButton;//用户注销
    JButton userButton;//用户信息的设置
    JButton connectButton;//连接设置
    JButton exitButton;//退出按钮
    JButton captureScreenButton; //截屏按钮

    //框架的大小
    Dimension faceSize = new Dimension(550, 550);

    JPanel downPanel;
    GridBagLayout girdBag;
    GridBagConstraints girdBagCon;

    public ChatClient() {
        init();//初始化程序

        //添加框架的关闭事件处理
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        //设置框架的大小
        this.setSize(faceSize);

        //设置运行时窗口的位置
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation((int) (screenSize.width - faceSize.getWidth()) / 2,
                (int) (screenSize.height - faceSize.getHeight()) / 2);
        this.setResizable(false);
        this.setTitle("聊天室客户端"); //设置标题

        setVisible(true);

        //为操作菜单栏设置热键'V'
        operateMenu.setMnemonic('O');

    }

    /**
     * 程序初始化函数
     */
    public void init() {

        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //添加菜单栏，对应55行相关介绍
        jMenuBar.add(operateMenu);   //操作（1.2.3）
        operateMenu.add(loginItem);  //1.用户登录
        operateMenu.add(logoffItem); //2.用户注销
        operateMenu.add(exitItem);   //3.退出

        jMenuBar.add(conMenu);       //设置(a.b)
        conMenu.add(userItem);       //a.用户设置
        conMenu.add(connectItem);    //b.连接设置

        jMenuBar.add(helpMenu);      //帮助（I）
        helpMenu.add(helpItem);      //I.帮助

        setJMenuBar(jMenuBar);

        //初始化按钮
        loginButton = new JButton("登录");
        logoffButton = new JButton("注销");
        userButton = new JButton("用户设置");
        connectButton = new JButton("连接设置");
        exitButton = new JButton("退出");
        captureScreenButton = new JButton("全屏截屏");
        JButton  screenShotButton = new JButton("区域截图");

        //当鼠标放上显示信息
        loginButton.setToolTipText("连接到指定的服务器");
        logoffButton.setToolTipText("与服务器断开连接");
        userButton.setToolTipText("设置用户信息");
        connectButton.setToolTipText("设置所要连接到的服务器信息");
        captureScreenButton.setToolTipText("现在只能全屏截下全屏");
        //将按钮添加到工具栏
        toolBar.add(userButton);
        Component add = toolBar.add(connectButton);
        toolBar.addSeparator();//添加分隔栏
        toolBar.add(loginButton);
        toolBar.add(logoffButton);
        toolBar.addSeparator();//添加分隔栏
        toolBar.add(exitButton);
        toolBar.add(captureScreenButton);
        toolBar.add(screenShotButton);

        contentPane.add(toolBar, BorderLayout.NORTH);

        checkbox = new JCheckBox("悄悄话");
        checkbox.setSelected(false);

        actionlist = new JComboBox();
        actionlist.addItem("@/微笑@");
        actionlist.addItem("@/高兴@");
        actionlist.addItem("@/轻轻@");
        actionlist.addItem("@/生气@");
        actionlist.addItem("@/小心@");
        actionlist.addItem("@/静静@");
        actionlist.setSelectedIndex(0);

        //初始时
        loginButton.setEnabled(true);
        logoffButton.setEnabled(false);

        //为菜单栏添加事件监听
        loginItem.addActionListener(this);
        logoffItem.addActionListener(this);
        exitItem.addActionListener(this);
        userItem.addActionListener(this);
        connectItem.addActionListener(this);
        helpItem.addActionListener(this);

        //添加按钮的事件侦听
        loginButton.addActionListener(this);
        logoffButton.addActionListener(this);
        userButton.addActionListener(this);
        connectButton.addActionListener(this);
        exitButton.addActionListener(this);

        /*
         *全屏截屏事件监听处理
         */
        captureScreenButton.addActionListener((ActionEvent e) -> {
            try {
                RandomName filename = new RandomName();
                captureScreen("E:\\projects\\java_chat\\cutImage", filename + ".png");
            } catch (Exception ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        /**
         * 区域截屏监听
         */
        screenShotButton.addActionListener(this);

        combobox = new JComboBox();
        combobox.insertItemAt("所有人", 0);
        combobox.setSelectedIndex(0);

        messageShow = new JTextArea();
        messageShow.setEditable(false);
        //添加滚动条
        messageScrollPane = new JScrollPane(messageShow,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        messageScrollPane.setPreferredSize(new Dimension(400, 400));
        messageScrollPane.revalidate();

        clientMessage = new JTextField(23);
        clientMessage.setEnabled(false);
        clientMessageButton = new JButton();
        clientMessageButton.setText("发送");

        //添加系统消息的事件侦听
        clientMessage.addActionListener(this);
        clientMessageButton.addActionListener(this);

        sendToLabel = new JLabel("发送至:");
        express = new JLabel("         表情:   ");
        messageLabel = new JLabel("发送消息:");
        downPanel = new JPanel();
        girdBag = new GridBagLayout();
        downPanel.setLayout(girdBag);

        girdBagCon = new GridBagConstraints();
        girdBagCon.gridx = 0;
        girdBagCon.gridy = 0;
        girdBagCon.gridwidth = 5;
        girdBagCon.gridheight = 2;
        girdBagCon.ipadx = 5;
        girdBagCon.ipady = 5;
        JLabel none = new JLabel("    ");
        girdBag.setConstraints(none, girdBagCon);
        downPanel.add(none);

        girdBagCon = new GridBagConstraints();
        girdBagCon.gridx = 0;
        girdBagCon.gridy = 2;
        girdBagCon.insets = new Insets(1, 0, 0, 0);
        //girdBagCon.ipadx = 5;
        //girdBagCon.ipady = 5;
        girdBag.setConstraints(sendToLabel, girdBagCon);
        downPanel.add(sendToLabel);

        girdBagCon = new GridBagConstraints();
        girdBagCon.gridx = 1;
        girdBagCon.gridy = 2;
        girdBagCon.anchor = GridBagConstraints.LINE_START;
        girdBag.setConstraints(combobox, girdBagCon);
        downPanel.add(combobox);

        girdBagCon = new GridBagConstraints();
        girdBagCon.gridx = 2;
        girdBagCon.gridy = 2;
        girdBagCon.anchor = GridBagConstraints.LINE_END;
        girdBag.setConstraints(express, girdBagCon);
        downPanel.add(express);

        girdBagCon = new GridBagConstraints();
        girdBagCon.gridx = 3;
        girdBagCon.gridy = 2;
        girdBagCon.anchor = GridBagConstraints.LINE_START;
        //girdBagCon.insets = new Insets(1,0,0,0);
        //girdBagCon.ipadx = 5;
        //girdBagCon.ipady = 5;
        girdBag.setConstraints(actionlist, girdBagCon);
        downPanel.add(actionlist);

        girdBagCon = new GridBagConstraints();
        girdBagCon.gridx = 4;
        girdBagCon.gridy = 2;
        girdBagCon.insets = new Insets(1, 0, 0, 0);
//        girdBagCon.ipadx = 5;
//        girdBagCon.ipady = 5;
        girdBag.setConstraints(checkbox, girdBagCon);
        downPanel.add(checkbox);

        girdBagCon = new GridBagConstraints();
        girdBagCon.gridx = 0;
        girdBagCon.gridy = 3;
        girdBag.setConstraints(messageLabel, girdBagCon);
        downPanel.add(messageLabel);

        girdBagCon = new GridBagConstraints();
        girdBagCon.gridx = 1;
        girdBagCon.gridy = 3;
        girdBagCon.gridwidth = 3;
        girdBagCon.gridheight = 1;
        girdBag.setConstraints(clientMessage, girdBagCon);
        downPanel.add(clientMessage);

        girdBagCon = new GridBagConstraints();
        girdBagCon.gridx = 4;
        girdBagCon.gridy = 3;
        girdBag.setConstraints(clientMessageButton, girdBagCon);
        downPanel.add(clientMessageButton);

        showStatus = new JTextField(35);
        showStatus.setEditable(false);
        girdBagCon = new GridBagConstraints();
        girdBagCon.gridx = 0;
        girdBagCon.gridy = 5;
        girdBagCon.gridwidth = 5;
        girdBag.setConstraints(showStatus, girdBagCon);
        downPanel.add(showStatus);

        contentPane.add(messageScrollPane, BorderLayout.CENTER);
        contentPane.add(downPanel, BorderLayout.SOUTH);

        //关闭程序时的操作
        this.addWindowListener(
                new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        if (type == 1) {
                            DisConnect();
                        }
                        System.exit(0);
                    }
                }
        );
    }

    /**
     * 事件处理
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Object obj = e.getSource();

        if (obj == userItem || obj == userButton) { //用户信息设置
            //调出用户信息设置对话框
            UserConf userConf = new UserConf(this, userName);
            userConf.setVisible(true);
            userName = userConf.userInputName;
        } else if (obj == connectItem || obj == connectButton) { //连接服务端设置
            //调出连接设置对话框
            ConnectConf conConf = new ConnectConf(this, ip, port);
            conConf.setVisible(true);
            ip = conConf.userInputIp;
            port = conConf.userInputPort;
        } else if (obj == loginItem || obj == loginButton) { //登录
            Connect();
        } else if (obj == logoffItem || obj == logoffButton) { //注销
            DisConnect();
            showStatus.setText("");
        } else if (obj == clientMessage || obj == clientMessageButton) { //发送消息
            SendMessage();
            clientMessage.setText("");
        } else if (obj == exitButton || obj == exitItem) { //退出
            int j = JOptionPane.showConfirmDialog(
                    this, "真的要退出吗?", "退出",
                    JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE);

            if (j == JOptionPane.YES_OPTION) {
                if (type == 1) {
                    DisConnect();
                }
                System.exit(0);
            }
        } else if (obj == helpItem) { //菜单栏中的帮助
            //调出帮助对话框
            Help helpDialog = new Help(this);
            helpDialog.setVisible(true);
        }
    }
    /**
     * 连接服务器
     */
    public void Connect() {
        try {
            socket = new Socket(ip, port);
        } catch (Exception e) {
            JOptionPane.showConfirmDialog(
                    this, "不能连接到指定的服务器。\n请确认连接设置是否正确。", "提示",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            output = new ObjectOutputStream(socket.getOutputStream());
            output.flush();

            input = new ObjectInputStream(socket.getInputStream());

            output.writeObject(userName);
            output.flush();

            recvThread = new ClientReceive(socket, output, input, combobox, messageShow, showStatus);
            recvThread.start();

            loginButton.setEnabled(false);
            loginItem.setEnabled(false);
            userButton.setEnabled(false);
            userItem.setEnabled(false);
            connectButton.setEnabled(false);
            connectItem.setEnabled(false);
            logoffButton.setEnabled(true);
            logoffItem.setEnabled(true);
            clientMessage.setEnabled(true);
            messageShow.append("连接服务器 " + ip + ":" + port + " 成功...\n");
            type = 1;//标志位设为已连接
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     *服务器注销
     */
    public void DisConnect() {
        loginButton.setEnabled(true);
        loginItem.setEnabled(true);
        userButton.setEnabled(true);
        userItem.setEnabled(true);
        connectButton.setEnabled(true);
        connectItem.setEnabled(true);
        logoffButton.setEnabled(false);
        logoffItem.setEnabled(false);
        clientMessage.setEnabled(false);

        if (socket.isClosed()) {
            return;
        }

        try {
            output.writeObject("用户下线");
            output.flush();

            input.close();
            output.close();
            socket.close();
            messageShow.append("已经与服务器断开连接...\n");
            type = 0;//标志位设为未连接
        } catch (Exception e) {
            //
        }
    }

    public void SendMessage() {
        String toSomebody = combobox.getSelectedItem().toString();
        String status = "";
        if (checkbox.isSelected()) {
            status = "悄悄话";
        }

        String action = actionlist.getSelectedItem().toString();
        String message = clientMessage.getText();

        if (socket.isClosed()) {
            return;
        }

        try {
            output.writeObject("聊天信息");
            output.flush();
            output.writeObject(toSomebody);
            output.flush();
            output.writeObject(status);
            output.flush();
            output.writeObject(action);
            output.flush();
            output.writeObject(message);
            output.flush();
        } catch (Exception e) {
            //
        }
    }

    public static void main(String[] args) throws UnsupportedLookAndFeelException {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
        }
        ChatClient chatClient = new ChatClient();
    }
}
