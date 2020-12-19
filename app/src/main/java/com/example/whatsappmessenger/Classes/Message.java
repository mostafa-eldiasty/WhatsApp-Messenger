package com.example.whatsappmessenger.Classes;

public class Message {
    String content;
    String sender;
    String time;
    String status;
    String type;

    public Message(String content, String sender, String time, String status, String type) {
        this.content = content;
        this.sender = sender;
        this.time = time;
        this.status = status;
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
