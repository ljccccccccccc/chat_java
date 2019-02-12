package com.ljccccccccccc.server;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;

public class ServerListen extends Thread{

    ServerSocket server;
    JComboBox combobox;
    JTextArea textarea;
    JTextField textfield;
    UserLinkList userLinkList;
    Node client;
    ServerReceive recvThread;

    public boolean isStop;

    public ServerListen(ServerSocket server, JComboBox combobox, JTextArea textarea, JTextField textfield, UserLinkList userLinkList) {
        this.server = server;
        this.combobox = combobox;
        this.textarea = textarea;
        this.textfield = textfield;
        this.userLinkList = userLinkList;
        isStop = false;
    }

    @Override
    public void run () {
        while (!isStop && !server.isClosed()) {
            try{
                client = new Node();
                client.socket = server.accept();
                client.output = new ObjectOutputStream(client.socket.getOutputStream());
                client.output.flush();
                client.input = new ObjectInputStream(client.socket.getInputStream());
                client.username = (String) client.input.readObject();
                combobox.addItem(client.username);
                userLinkList.addUser(client);
                textarea.append("用户" + userLinkList.getCount() + " 人\n");
                recvThread = new ServerReceive(textarea,textfield,combobox,client,userLinkList);
                recvThread.start();
            }catch (IOException | ClassNotFoundException e){}
        }
    }
}











