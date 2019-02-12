package com.ljccccccccccc.server;

public class UserLinkList {
    Node root;
    Node pointer;
    private int count;
    public UserLinkList () {
        root = new Node();
        root.next = null;
        pointer = null;
        count = 0;
    }
    public void addUser (Node n) {
        pointer = root;
        while(pointer.next != null){
            pointer = pointer.next;
        }
        pointer.next = n;
        n.next = null;
        count ++;
    }

    public void delUser (Node n){
        pointer = root;
        while (pointer.next != null) {
            if(pointer.next == n){
                pointer.next = n.next;
                count--;
                break;
            }
            pointer = pointer.next;
        }
    }

    public int getCount () {
        return count;
    }

    public Node findUser (String username) {
        if(count == 0){
            return null;
        }
        pointer = root;
        while (pointer.next != null) {
            pointer = pointer.next;
            if (pointer.username.equalsIgnoreCase(username)) {
                return pointer;
            }
        }
        return null;
    }

    public Node findUser (int index) {
        if (count == 0) {
            return null;
        }
        if(index < 0) {
            return null;
        }
        //初始化
        pointer = root;
        int i = 0;
        while (i < index + 1) {
            if(pointer.next != null){
                pointer = pointer.next;
            }else {
                return null;
            }
            i++;
        }
        return pointer;
    }
}
