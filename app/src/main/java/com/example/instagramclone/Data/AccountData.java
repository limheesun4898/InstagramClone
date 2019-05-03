package com.example.instagramclone.Data;

import com.google.gson.annotations.SerializedName;

public class AccountData {
    @SerializedName("userId")
    public String email;
    @SerializedName("nickName")
    public String nickName;
    @SerializedName("realName")
    public String realName;
    @SerializedName("profile")
    public String profile;

    @SerializedName("id")
    public int id;


    public AccountData() {
    }


    public int getId() { return id; }

    public String getEmail() {
        return email;
    }

    public String getNickName() {
        return nickName;
    }

    public String getRealName() {
        return realName;
    }

    public String getProfile() {
        return profile;
    }

}
