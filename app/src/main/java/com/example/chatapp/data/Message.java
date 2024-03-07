package com.example.chatapp.data;

public class Message {
    private String message;
    private String sendername;
    private String recepientname;

    public Message() {
    }


    public Message(String message, String sendername, String recepientname) {
        this.message = message;
        this.sendername = sendername;
        this.recepientname = recepientname;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSendername() {
        return sendername;
    }

    public void setSendername(String sendername) {
        this.sendername = sendername;
    }

    public String getRecepientname() {
        return recepientname;
    }

    public void setRecepientname(String recepientname) {
        this.recepientname = recepientname;
    }
}
