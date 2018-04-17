package com.example.bilal.hci_project;

import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Dr Ishfaque on 10-Dec-17.
 */

public class InterestArea implements Serializable {
    private String Uid;
    private String Name;
    private ArrayList<Message> messagesArrayList;

    public InterestArea(String uid) {
        Uid = uid;
        messagesArrayList = new ArrayList<Message>();
    }

    public InterestArea(String uid, String name) {
        Uid = uid;
        Name = name;
        messagesArrayList = new ArrayList<Message>();
    }

    public void setMessagesArrayList(ArrayList<Message> messagesArrayList) {
        this.messagesArrayList = messagesArrayList;
    }
    public void addMessage(Message message)
    {

        this.messagesArrayList.add(message);
    }
    public Message getMessage(int index)
    {
        if(messagesArrayList.size()!=0)
        {
            return this.messagesArrayList.get(index);
        }
        else return null;

    }

    public void setName(String name) {
        Name = name;
    }

    public String getUid() {

        return Uid;
    }

    public String getName() {
        return Name;
    }

    public ArrayList<Message> getMessagesArrayList() {
        if(messagesArrayList.size()!=0)
        {
            return this.messagesArrayList;
        }
        else return null;
    }
}
