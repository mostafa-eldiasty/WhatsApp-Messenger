package com.example.whatsappmessenger.Classes;

public class Chat {
    String contactName;
    String contactNumber;
    String messageContent;
    String messageTime;
    String messageType;

    public Chat(String contactName,String contactNumber, String messageContent, String messageTime, String messageType) {
        this.contactName = contactName;
        this.contactNumber = contactNumber;
        this.messageContent = messageContent;
        this.messageTime = messageTime;
        this.messageType = messageType;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public String getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(String messageTime) {
        this.messageTime = messageTime;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }
}
