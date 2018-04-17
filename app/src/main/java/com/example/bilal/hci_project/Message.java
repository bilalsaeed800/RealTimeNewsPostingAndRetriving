package com.example.bilal.hci_project;

import java.io.Serializable;

/**
 * Created by Dr Ishfaque on 10-Dec-17.
 */

public class Message implements Serializable{
    private String category;
    private String expiryDate;
    private String messageText;


    private String sender;

    public Message(String category, String expiryDate, String messageText,String sender) {
        this.category = category;
        this.expiryDate = expiryDate;
        this.messageText = messageText;
        this.sender = sender;
    }
    public Message(){}


    public String getSender() {
        return sender;
    }

    public String getCategory() {
        return category;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public String getMessageText() {
        return messageText;
    }
}
