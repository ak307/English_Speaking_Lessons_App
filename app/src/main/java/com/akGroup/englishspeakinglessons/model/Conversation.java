package com.akGroup.englishspeakinglessons.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Conversation implements Parcelable {
    private String title;
    private String imageUrl;
    private String fullConversationText;
    private String firstPersonConversationText;
    private String secondPersonConversationText;
    private String fullConversationUrl;
    private String firstPersonConversationUrl;
    private String secondPersonConversationUrl;
    private String conversationBy;
    private String firstPersonConversationBy;
    private String secondPersonConversationBy;
    private String conversationID;
    private String level;

    public Conversation(String title, String imageUrl,
                        String fullConversationText,
                        String firstPersonConversationText,
                        String secondPersonConversationText,
                        String fullConversationUrl,
                        String firstPersonConversationUrl,
                        String secondPersonConversationUrl,
                        String conversationBy, String firstPersonConversationBy,
                        String secondPersonConversationBy, String conversationID,
                        String level) {
        this.title = title;
        this.imageUrl = imageUrl;
        this.fullConversationText = fullConversationText;
        this.firstPersonConversationText = firstPersonConversationText;
        this.secondPersonConversationText = secondPersonConversationText;
        this.fullConversationUrl = fullConversationUrl;
        this.firstPersonConversationUrl = firstPersonConversationUrl;
        this.secondPersonConversationUrl = secondPersonConversationUrl;
        this.conversationBy = conversationBy;
        this.firstPersonConversationBy = firstPersonConversationBy;
        this.secondPersonConversationBy = secondPersonConversationBy;
        this.conversationID = conversationID;
        this.level = level;
    }

    protected Conversation(Parcel in) {
        title = in.readString();
        imageUrl = in.readString();
        fullConversationText = in.readString();
        firstPersonConversationText = in.readString();
        secondPersonConversationText = in.readString();
        fullConversationUrl = in.readString();
        firstPersonConversationUrl = in.readString();
        secondPersonConversationUrl = in.readString();
        conversationBy = in.readString();
        firstPersonConversationBy = in.readString();
        secondPersonConversationBy = in.readString();
        conversationID = in.readString();
    }

    public static final Creator<Conversation> CREATOR = new Creator<Conversation>() {
        @Override
        public Conversation createFromParcel(Parcel in) {
            return new Conversation(in);
        }

        @Override
        public Conversation[] newArray(int size) {
            return new Conversation[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getFullConversationText() {
        return fullConversationText;
    }

    public void setFullConversationText(String fullConversationText) {
        this.fullConversationText = fullConversationText;
    }

    public String getFirstPersonConversationText() {
        return firstPersonConversationText;
    }

    public void setFirstPersonConversationText(String firstPersonConversationText) {
        this.firstPersonConversationText = firstPersonConversationText;
    }

    public String getSecondPersonConversationText() {
        return secondPersonConversationText;
    }

    public void setSecondPersonConversationText(String secondPersonConversationText) {
        this.secondPersonConversationText = secondPersonConversationText;
    }

    public String getFullConversationUrl() {
        return fullConversationUrl;
    }

    public void setFullConversationUrl(String fullConversationUrl) {
        this.fullConversationUrl = fullConversationUrl;
    }

    public String getFirstPersonConversationUrl() {
        return firstPersonConversationUrl;
    }

    public void setFirstPersonConversationUrl(String firstPersonConversationUrl) {
        this.firstPersonConversationUrl = firstPersonConversationUrl;
    }

    public String getSecondPersonConversationUrl() {
        return secondPersonConversationUrl;
    }

    public void setSecondPersonConversationUrl(String secondPersonConversationUrl) {
        this.secondPersonConversationUrl = secondPersonConversationUrl;
    }

    public String getConversationBy() {
        return conversationBy;
    }

    public void setConversationBy(String conversationBy) {
        this.conversationBy = conversationBy;
    }

    public String getFirstPersonConversationBy() {
        return firstPersonConversationBy;
    }

    public void setFirstPersonConversationBy(String firstPersonConversationBy) {
        this.firstPersonConversationBy = firstPersonConversationBy;
    }

    public String getSecondPersonConversationBy() {
        return secondPersonConversationBy;
    }

    public void setSecondPersonConversationBy(String secondPersonConversationBy) {
        this.secondPersonConversationBy = secondPersonConversationBy;
    }

    public String getConversationID() {
        return conversationID;
    }

    public void setConversationID(String conversationID) {
        this.conversationID = conversationID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(imageUrl);
        dest.writeString(fullConversationText);
        dest.writeString(firstPersonConversationText);
        dest.writeString(secondPersonConversationText);
        dest.writeString(fullConversationUrl);
        dest.writeString(firstPersonConversationUrl);
        dest.writeString(secondPersonConversationUrl);
        dest.writeString(conversationBy);
        dest.writeString(firstPersonConversationBy);
        dest.writeString(secondPersonConversationBy);
        dest.writeString(conversationID);
    }
}
