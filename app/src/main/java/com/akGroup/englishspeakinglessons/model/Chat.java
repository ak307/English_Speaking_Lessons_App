package com.akGroup.englishspeakinglessons.model;

public class Chat {
    private String sender;
    private String receiver;
    private String message;
    private String rentAt;


    public Chat(String sender, String receiver, String message, String rentAt) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.rentAt = rentAt;
    }

    public Chat() {
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRentAt() {
        return rentAt;
    }

    public void setRentAt(String rentAt) {
        this.rentAt = rentAt;
    }
}
