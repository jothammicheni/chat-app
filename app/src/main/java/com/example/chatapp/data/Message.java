package com.example.chatapp.data;

public class Message {
    private String message;
    private String name;

    public Message() {
        // Default constructor required for Firebase Realtime Database
    }

    public Message( String name,String message) {
        this.message = message;
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
