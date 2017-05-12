package com.example.nfctransfer.sqLite;

import org.json.JSONObject;

public class DbUserModel {
    private int id;
    private String userId;
    private String userData;

    public DbUserModel(){}

    public DbUserModel(String userId, JSONObject profileData){
        this.userId = userId;
        this.userData = profileData.toString();
    }

    public DbUserModel(String userId, String profileData){
        this.userId = userId;
        this.userData = profileData;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserData() {
        return userData;
    }

    public void setUserData(String userData) {
        this.userData = userData;
    }

    public void setUserData(JSONObject userData) {
        this.userData = userData.toString();
    }
}
