package com.akGroup.englishspeakinglessons.model;

public class PracticeConversationSaved {
    private String conversationWith;
    private String savedConversationUrl;
    private String docID;

    public PracticeConversationSaved(String conversationWith, String savedConversationUrl, String docID) {
        this.conversationWith = conversationWith;
        this.savedConversationUrl = savedConversationUrl;
        this.docID = docID;
    }

    public String getConversationWith() {
        return conversationWith;
    }

    public void setConversationWith(String conversationWith) {
        this.conversationWith = conversationWith;
    }

    public String getSavedConversationUrl() {
        return savedConversationUrl;
    }

    public void setSavedConversationUrl(String savedConversationUrl) {
        this.savedConversationUrl = savedConversationUrl;
    }

    public String getDocID() {
        return docID;
    }

    public void setDocID(String docID) {
        this.docID = docID;
    }
}
