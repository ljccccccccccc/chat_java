package com.ljccccccccccc.server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;

public class PortConf extends JDialog {

    private static final long serialVersionUID = 1L;
    JPanel panelPort = new JPanel();
    JButton save = new JButton();
    JButton cancel = new JButton();

    public static JLabel DLGINFO = new JLabel("默认端口号为:8888");

    JPanel panelSave = new JPanel();
    JLabel message = new JLabel();

    public static JTextField portNumber;

    public PortConf (JFrame frame) {
        super (frame,true);
        try{
            jbInit();
        }catch (Exception e){}

        //设置运行位置,居中对话框
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(
                (int) (screenSize.width - 400 )/2 ,
                (int) (screenSize.height - 600)/2
        );
        this.setResizable(false);
    }

    private void jbInit () throws Exception {
        this.setSize(new Dimension(300,120));
        this.setTitle("端口设置");
        message.setText("请输入监听的端口号:");
        portNumber = new JTextField(10);
        portNumber.setText(""+ChatServer.port);
        save.setText("保存");
        cancel.setText("取消");

        panelPort.setLayout(new FlowLayout());
        panelPort.add(message);
        panelPort.add(portNumber);

        panelSave.add(new Label("              "));
        panelSave.add(save);
        panelSave.add(cancel);
        panelSave.add(new Label("              "));

        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(panelPort,BorderLayout.NORTH);
        contentPane.add(DLGINFO,BorderLayout.CENTER);
        contentPane.add(panelSave,BorderLayout.SOUTH);

        //保存按钮的事件处理
        save.addActionListener((ActionEvent a ) -> {
            int savePort;
            try {
                savePort = Integer.parseInt(PortConf.portNumber.getText());

                if(savePort < 1 || savePort > 65535) {
                    PortConf.DLGINFO.setText("监听端口必须在0-65535之间!");
                    PortConf.portNumber.setText(" ");
                    return ;
                }
                ChatServer.port = savePort;
                dispose();
            }catch (Exception e) {
                PortConf.DLGINFO.setText("错误的端口号,请重新填写!");
                PortConf.portNumber.setText("");
            }
        });

        //关闭对话框
        this.addWindowListener(
                new WindowAdapter() {
                    public void windowClosing(Exception e) {
                        DLGINFO.setText("默认端口号为8888");
                    }
                }
        );

        //取消按钮的事件处理
        cancel.addActionListener((ActionEvent e) -> {
            DLGINFO.setText("默认端口号为:8888");
            dispose();
        });
    }
}





