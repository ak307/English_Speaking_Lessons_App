package com.akGroup.englishspeakinglessons.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class User implements Parcelable {
    private String uid;
    private String userName;
    private String email;
    private String profileImgUrl;
    private String phone;
    private String status;
    private ArrayList<String> friendsArraylist;
    private String token;


    public User() {
    }

    public User(String uid, String userName,
                String email, String profileImgUrl,
                String phone) {
        this.uid = uid;
        this.userName = userName;
        this.email = email;
        this.profileImgUrl = profileImgUrl;
        this.phone = phone;
    }

    public User(String uid, String userName,
                String email, String profileImgUrl,
                String phone, String status) {
        this.uid = uid;
        this.userName = userName;
        this.email = email;
        this.profileImgUrl = profileImgUrl;
        this.phone = phone;
        this.status = status;
    }

    public User(String uid, String userName,
                String email, String profileImgUrl,
                String phone, String status,
                ArrayList<String> friendsArraylist) {
        this.uid = uid;
        this.userName = userName;
        this.email = email;
        this.profileImgUrl = profileImgUrl;
        this.phone = phone;
        this.status = status;
        this.friendsArraylist = friendsArraylist;
    }


    public User(String uid, String userName, String email,
                String profileImgUrl, String phone, String status,
                String token) {
        this.uid = uid;
        this.userName = userName;
        this.email = email;
        this.profileImgUrl = profileImgUrl;
        this.phone = phone;
        this.status = status;
        this.token = token;
    }


    public User(String uid, String userName, String email,
                String profileImgUrl, String phone, String status,
                ArrayList<String> friendsArraylist, String token) {
        this.uid = uid;
        this.userName = userName;
        this.email = email;
        this.profileImgUrl = profileImgUrl;
        this.phone = phone;
        this.status = status;
        this.friendsArraylist = friendsArraylist;
        this.token = token;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileImgUrl() {
        return profileImgUrl;
    }

    public void setProfileImgUrl(String profileImgUrl) {
        this.profileImgUrl = profileImgUrl;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<String> getFriendsArraylist() {
        return friendsArraylist;
    }

    public void setFriendsArraylist(ArrayList<String> friendsArraylist) {
        this.friendsArraylist = friendsArraylist;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


    protected User(Parcel in) {
        uid = in.readString();
        userName = in.readString();
        email = in.readString();
        profileImgUrl = in.readString();
        phone = in.readString();
        status = in.readString();
        friendsArraylist = in.createStringArrayList();
        token = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeString(userName);
        dest.writeString(email);
        dest.writeString(profileImgUrl);
        dest.writeString(phone);
        dest.writeString(status);
        dest.writeArray(friendsArraylist.toArray());
        dest.writeString(token);
    }
}
