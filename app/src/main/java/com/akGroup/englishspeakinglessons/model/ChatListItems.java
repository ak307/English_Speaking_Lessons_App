package com.akGroup.englishspeakinglessons.model;

import java.util.ArrayList;

public class ChatListItems {
    private String chatId;
    private String lastMessage;
    private String lastMessageCreatedAt;
    private String friendName;
    private String friendProfileUrl;
    private String friendId;
    private String friendToken;


    public ChatListItems(String chatId, String lastMessage, String lastMessageCreatedAt, String friendName, String friendProfileUrl, String friendId, String friendToken) {
        this.chatId = chatId;
        this.lastMessage = lastMessage;
        this.lastMessageCreatedAt = lastMessageCreatedAt;
        this.friendName = friendName;
        this.friendProfileUrl = friendProfileUrl;
        this.friendId = friendId;
        this.friendToken = friendToken;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getLastMessageCreatedAt() {
        return lastMessageCreatedAt;
    }

    public void setLastMessageCreatedAt(String lastMessageCreatedAt) {
        this.lastMessageCreatedAt = lastMessageCreatedAt;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public String getFriendProfileUrl() {
        return friendProfileUrl;
    }

    public void setFriendProfileUrl(String friendProfileUrl) {
        this.friendProfileUrl = friendProfileUrl;
    }

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public String getFriendToken() {
        return friendToken;
    }

    public void setFriendToken(String friendToken) {
        this.friendToken = friendToken;
    }
}
