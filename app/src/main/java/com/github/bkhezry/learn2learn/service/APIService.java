package com.github.bkhezry.learn2learn.service;

import com.github.bkhezry.learn2learn.model.AuthenticationInfo;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface APIService {

  @POST("token")
  @FormUrlEncoded
  Call<AuthenticationInfo> storeUser(@Field("token") String token);


}
