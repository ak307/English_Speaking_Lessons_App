package com.akGroup.englishspeakinglessons.model;

public class Comment {
    private String docId;
    private String comment;
    private String commentTime;
    private String commentBy_id;
    private String commentBy_profilePic;
    private String commentBy_name;

    public Comment(String docId, String comment,
                   String commentTime, String commentBy_id,
                   String commentBy_profilePic, String commentBy_name) {
        this.docId = docId;
        this.comment = comment;
        this.commentTime = commentTime;
        this.commentBy_id = commentBy_id;
        this.commentBy_profilePic = commentBy_profilePic;
        this.commentBy_name = commentBy_name;
    }

    public Comment(String docId, String comment,
                   String commentTime, String commentBy_id) {
        this.docId = docId;
        this.comment = comment;
        this.commentTime = commentTime;
        this.commentBy_id = commentBy_id;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(String commentTime) {
        this.commentTime = commentTime;
    }

    public String getCommentBy_id() {
        return commentBy_id;
    }

    public void setCommentBy_id(String commentBy_id) {
        this.commentBy_id = commentBy_id;
    }

    public String getCommentBy_profilePic() {
        return commentBy_profilePic;
    }

    public void setCommentBy_profilePic(String commentBy_profilePic) {
        this.commentBy_profilePic = commentBy_profilePic;
    }

    public String getCommentBy_name() {
        return commentBy_name;
    }

    public void setCommentBy_name(String commentBy_name) {
        this.commentBy_name = commentBy_name;
    }
}
