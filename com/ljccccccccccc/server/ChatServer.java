package com.ljccccccccccc.server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.ServerSocket;

public final class ChatServer extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;
    public static int port = 8888;
    ServerSocket serverSocket;
    JComboBox combobox;
    JTextArea messageShow;
    JScrollPane messageScrollPane;
    JTextField showStatus;
    JLabel sendToLabel,messageLabel;
    JTextField sysMessage;
    JButton sysMessageButton;
    UserLinkList userLinkList;
    JMenuBar jMenuBar = new JMenuBar();
    JMenu serviceMenu = new JMenu("服务");
    JMenuItem portItem = new JMenuItem("端口设置");
    JMenuItem startItem = new JMenuItem("开启服务");
    JMenuItem stopItem = new JMenuItem("停止服务");
    JMenuItem exitItem = new JMenuItem("退出服务");

    JMenu helpMenu = new JMenu("帮助");
    JMenuItem helpItem = new JMenuItem("帮助菜单");

    JToolBar toolBar = new JToolBar();
    JButton portSet;
    JButton startServer;
    JButton stopServer;
    JButton exitButton;
    JButton uploadButton;

    Dimension faceSize = new Dimension(550,550);
    ServerListen listenThread;
    JPanel downPanel;
    GridBagLayout gridBag;
    GridBagConstraints gridBagCon;


    public ChatServer () {
        init();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setSize(faceSize);
        //获取屏幕尺寸对象
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation((int) (screenSize.width - faceSize.getWidth()) / 2 , (int) (screenSize.height - faceSize.getHeight()) / 2);
        this.setResizable(false);
        this.setTitle("聊天室服务器端");
        setVisible(true);
    }

    //初始化
    public void init () {
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        serviceMenu.add(startItem);
        serviceMenu.add(stopItem);
        serviceMenu.add(exitItem);
        jMenuBar.add(serviceMenu);
        helpMenu.add(helpItem);
        JMenu add = jMenuBar.add(helpMenu);
        setJMenuBar(jMenuBar);
        portSet = new JButton("端口设置");
        startServer = new JButton("启动服务");
        stopServer = new JButton("停止服务");
        exitButton = new JButton("退出");
        toolBar.add(portSet);
        toolBar.addSeparator();
        toolBar.add(startServer);
        toolBar.add(stopServer);
        toolBar.addSeparator();
        toolBar.add(exitButton);
        contentPane.add(toolBar, BorderLayout.NORTH);
        stopServer.setEnabled(false);
        stopItem.setEnabled(false);
        portItem.addActionListener(this);
        startItem.addActionListener(this);
        stopItem.addActionListener(this);
        exitItem.addActionListener(this);
        helpItem.addActionListener(this);
        portSet.addActionListener(this);
        startServer.addActionListener(this);
        stopServer.addActionListener(this);
        exitButton.addActionListener(this);
        combobox = new JComboBox();
        combobox.insertItemAt("所有人", 0);
        combobox.setSelectedIndex(0);
        messageShow = new JTextArea();
        messageShow.setEditable(false);
        messageScrollPane = new JScrollPane(messageShow, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        messageScrollPane.setPreferredSize(new Dimension(400, 400));
        messageScrollPane.revalidate();
        showStatus = new JTextField(25);
        showStatus.setEditable(false);
        sysMessage = new JTextField(25);
        sysMessage.setEnabled(false);
        sysMessageButton = new JButton();
        sysMessageButton.setText("发送");
        sysMessage.addActionListener(this);
        sysMessageButton.addActionListener(this);
        sendToLabel = new JLabel("发送至:");
        messageLabel = new JLabel("发送消息:");
        downPanel = new JPanel();
        gridBag = new GridBagLayout();
        downPanel.setLayout(gridBag);
        gridBagCon = new GridBagConstraints();
        gridBagCon.gridx = 0;
        gridBagCon.gridy = 0;
        gridBagCon.gridwidth = 3;
        gridBagCon.gridheight = 2;
        gridBagCon.ipadx = 5;
        gridBagCon.ipady = 5;
        JLabel none = new JLabel("    ");
        gridBag.setConstraints(none, gridBagCon);
        downPanel.add(none);
        gridBagCon = new GridBagConstraints();
        gridBagCon.gridx = 0;
        gridBagCon.gridy = 2;
        gridBagCon.insets = new Insets(1, 0, 0, 0);
        gridBagCon.ipadx = 5;
        gridBagCon.ipady = 5;
        gridBag.setConstraints(sendToLabel, gridBagCon);
        downPanel.add(sendToLabel);
        gridBagCon = new GridBagConstraints();
        gridBagCon.gridx = 1;
        gridBagCon.gridy = 2;
        gridBagCon.anchor = GridBagConstraints.LINE_START;
        gridBag.setConstraints(combobox, gridBagCon);
        downPanel.add(combobox);
        gridBagCon = new GridBagConstraints();
        gridBagCon.gridx = 0;
        gridBagCon.gridy = 3;
        gridBag.setConstraints(messageLabel, gridBagCon);
        downPanel.add(messageLabel);
        gridBagCon = new GridBagConstraints();
        gridBagCon.gridx = 1;
        gridBagCon.gridy = 3;
        gridBag.setConstraints(sysMessage, gridBagCon);
        downPanel.add(sysMessage);
        gridBagCon = new GridBagConstraints();
        gridBagCon.gridx = 2;
        gridBagCon.gridy = 3;
        gridBag.setConstraints(sysMessageButton, gridBagCon);
        downPanel.add(sysMessageButton);
        gridBagCon = new GridBagConstraints();
        gridBagCon.gridx = 0;
        gridBagCon.gridy = 4;
        gridBagCon.gridwidth = 3;
        gridBag.setConstraints(showStatus, gridBagCon);
        downPanel.add(showStatus);
        contentPane.add(messageScrollPane, BorderLayout.CENTER);
        contentPane.add(downPanel, BorderLayout.SOUTH);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                stopService();
                System.exit(0);
            }
         });
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        Object obj = e.getSource();
        if (obj == startServer || obj == startItem) {
            startService();
        }else if (obj == stopItem || obj == stopServer ) {
            int j = JOptionPane.showConfirmDialog(this,"确认停止服务吗?","停止",JOptionPane.YES_OPTION,JOptionPane.QUESTION_MESSAGE);
            if (JOptionPane.YES_OPTION == j) {
                stopService();
            }
        }else if (obj == portSet || obj == portItem){
            PortConf portConf = new PortConf(this);
            portConf.setVisible(true);
        }else if (obj == exitButton || obj == exitItem) {
            int k = JOptionPane.showConfirmDialog(this,"真的要退出吗?","退出",JOptionPane.YES_OPTION,JOptionPane.QUESTION_MESSAGE);
            if(k == JOptionPane.YES_OPTION) {
                stopService();
                System.exit(0);
            }
        }else if (obj == helpItem) {
            Help helpDialog = new Help(this);
            helpDialog.setVisible(true);
        }else if (obj == sysMessage || obj == sysMessageButton) {
            sendSystemMessage();
        }
    }
    public void startService () {
        try{
            serverSocket = new ServerSocket(port,10);
            messageShow.append("服务端已在"+port+"端口监听...\n");
            startServer.setEnabled(false);
            startItem.setEnabled(false);
            portSet.setEnabled(false);
            portItem.setEnabled(false);
            stopServer.setEnabled(true);
            stopItem.setEnabled(true);
            sysMessage.setEnabled(true);

        } catch (Exception e) {
        }
        userLinkList = new UserLinkList();
        listenThread = new ServerListen(serverSocket,combobox,messageShow,showStatus,userLinkList);
        listenThread.start();
    }

    public void stopService () {
        try {
            sendStopToAll();
            listenThread.isStop = true;
            serverSocket.close();
            int count = userLinkList.getCount();
            int i = 0 ;
            while (i < count) {
                Node node = userLinkList.findUser(i);
                node.input.close();
                node.output.close();
                node.socket.close();
                i++;
            }
            stopServer.setEnabled(false);
            stopItem.setEnabled(false);
            startServer.setEnabled(true);
            startItem.setEnabled(true);
            portSet.setEnabled(true);
            portItem.setEnabled(true);
            sysMessage.setEnabled(true);
            messageShow.append("服务端已关闭!\n");
            combobox.removeAllItems();
            combobox.addItem("所有人");
        } catch (Exception e) {
        }
    }

    public void sendStopToAll () {
        int count = userLinkList.getCount();
        int i = 0;
        while( i < count) {
            Node node = userLinkList.findUser(i);
            if(node == null) {
                i++;
                continue;
            }
            try {
                node.output.writeObject("服务关闭");
                node.output.flush();
            }catch(Exception e) {
            }
            i++;
        }
    }

    public void sendMsgToAll (String msg) {
        int count = userLinkList.getCount();
        int i = 0;
        while(i < count){
            Node node = userLinkList.findUser(i);
            if(node == null) {
                i++;
                continue;
            }
            try {
                node.output.writeObject("系统信息");
                node.output.flush();
                node.output.writeObject(msg);
                node.output.flush();
            }catch (Exception e) {
            }
            i++;
        }
        sysMessage.setText("");
    }


    public void sendSystemMessage() {
        String toSomebody = combobox.getSelectedItem().toString();
        String message = sysMessage.getText() + "\n" ;
        messageShow.append(message);
        if(toSomebody.equalsIgnoreCase("所有人")) {
            sendMsgToAll(message);
        }else {
            Node node;
            node = userLinkList.findUser(toSomebody);
            try {
                node.output.writeObject("系统信息");
                node.output.flush();
                node.output.writeObject(message);
                node.output.flush();
            }catch (Exception e){}
            sysMessage.setText("");
        }
    }

    public static void main (String[] args){
        try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e ){
            System.out.println(e+"error occur in ChatServer.class - void main !");
        }
        ChatServer chatServer = new ChatServer();
    }
}
























