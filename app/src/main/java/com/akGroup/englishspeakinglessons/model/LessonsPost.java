package com.akGroup.englishspeakinglessons.model;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Map;

public class LessonsPost {
    private String postedDateAndTime;
    private String description;
    private String profilePicUrl;
    private String profileName;
    private String videoUrl;
    private Long handShakeCount;
    private String postId;
    private ArrayList<String> likedUserList = new ArrayList<>();
    private ArrayList<Map<String, Object>> commentsList = new ArrayList<>();
    private Bitmap thumbnail;



    public LessonsPost(String postedDateAndTime, String description,
                       String profilePicUrl, String profileName,
                       String videoUrl, Long handShakeCount,
                       String postId, ArrayList<String> likedUserList) {
        this.postedDateAndTime = postedDateAndTime;
        this.description = description;
        this.profilePicUrl = profilePicUrl;
        this.profileName = profileName;
        this.videoUrl = videoUrl;
        this.handShakeCount = handShakeCount;
        this.postId = postId;
        this.likedUserList = likedUserList;
    }


    public LessonsPost(String postedDateAndTime, String description,
                       String profilePicUrl, String profileName,
                       String videoUrl, Long handShakeCount,
                       String postId, ArrayList<String> likedUserList,
                       ArrayList<Map<String, Object>> commentsList) {
        this.postedDateAndTime = postedDateAndTime;
        this.description = description;
        this.profilePicUrl = profilePicUrl;
        this.profileName = profileName;
        this.videoUrl = videoUrl;
        this.handShakeCount = handShakeCount;
        this.postId = postId;
        this.likedUserList = likedUserList;
        this.commentsList = commentsList;
    }



    public String getPostedDateAndTime() {
        return postedDateAndTime;
    }

    public void setPostedDateAndTime(String postedDateAndTime) {
        this.postedDateAndTime = postedDateAndTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public Long getHandShakeCount() {
        return handShakeCount;
    }

    public void setHandShakeCount(Long handShakeCount) {
        this.handShakeCount = handShakeCount;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public ArrayList<String> getLikedUserList() {
        return likedUserList;
    }

    public void setLikedUserList(ArrayList<String> likedUserList) {
        this.likedUserList = likedUserList;
    }

    public ArrayList<Map<String, Object>> getCommentsList() {
        return commentsList;
    }

    public void setCommentsList(ArrayList<Map<String, Object>> commentsList) {
        this.commentsList = commentsList;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Bitmap thumbnail) {
        this.thumbnail = thumbnail;
    }
}
