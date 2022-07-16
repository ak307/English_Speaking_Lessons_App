package com.akGroup.englishspeakinglessons.model;

public class Message {
    private String messageText;
    private String sentBy;
    private String sentAt;

    public Message(String messageText, String sentBy, String sentAt) {
        this.messageText = messageText;
        this.sentBy = sentBy;
        this.sentAt = sentAt;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getSentBy() {
        return sentBy;
    }

    public void setSentBy(String sentBy) {
        this.sentBy = sentBy;
    }

    public String getSentAt() {
        return sentAt;
    }

    public void setSentAt(String sentAt) {
        this.sentAt = sentAt;
    }
}
