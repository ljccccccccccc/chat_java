package com.ljccccccccccc.client;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientReceive extends Thread {
    private final JComboBox combobox;
    private final JTextArea textarea;

    Socket socket;
    ObjectOutputStream output;
    ObjectInputStream input;
    JTextField showStatus;

    public ClientReceive(Socket socket, ObjectOutputStream output, ObjectInputStream input, JComboBox combobox, JTextArea textarea, JTextField showStatus) {
        this.socket = socket;
        this.output = output;
        this.input = input;
        this.combobox = combobox;
        this.textarea = textarea;
        this.showStatus = showStatus;
    }

    @Override
    public void run() {
        while (!socket.isClosed()) {
            try {
                String type = (String) input.readObject();

                if (type.equalsIgnoreCase("系统信息")) {
                    String sysmsg = (String) input.readObject();
                    textarea.append("系统信息" + sysmsg);
                } else if (type.equalsIgnoreCase("服务关闭")) {
                    output.close();
                    input.close();
                    socket.close();
                    textarea.append("服务器已关闭！\n");
                    break;
                } else if (type.equalsIgnoreCase("聊天信息")) {
                    String message = (String) input.readObject();
                    textarea.append(message);
                } else if (type.equalsIgnoreCase("用户列表")) {
                    String userlist = (String) input.readObject();
                    String usernames[] = userlist.split("\n");
                    combobox.removeAllItems();

                    int i = 0;
                    combobox.addItem("所有人");
                    while (i < usernames.length) {
                        combobox.addItem(usernames[i]);
                        i++;
                    }
                    combobox.setSelectedIndex(0);
                    showStatus.setText("在线用户 " + usernames.length + " 人");
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println(e);
            }
        }
    }
}
