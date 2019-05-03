package com.example.instagramclone.Data;

import com.google.gson.JsonObject;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;


public interface ApiInterface {
    public static final String API_URI = "";

    //유저 회원가입
    @FormUrlEncoded
    @POST("users")
    Call<JsonObject> signUp(@Field("userId")String userId,
                            @Field("userPw")String userPw,
                            @Field("realName")String realName,
                            @Field("nickName")String nickName);

    //유저 로그인
    @FormUrlEncoded
    @POST("users/login")
    Call<JsonObject> getlogin(@Field("userId")String email,
                                       @Field("userPw")String password);

    //유저 account
    @FormUrlEncoded
    @POST("users/my")
    Call<JsonObject>getAccount(@Field("token") String token);

    //게시물 올리기 이미지
    @Multipart
    @POST("feeds")
    Call<JsonObject> FeedInsert(@Part("token") RequestBody token,
                                 @Part MultipartBody.Part userfile,
                                 @Part("feedContents") RequestBody edit_feed);


}
