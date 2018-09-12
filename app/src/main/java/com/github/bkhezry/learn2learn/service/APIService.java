package com.github.bkhezry.learn2learn.service;

import com.github.bkhezry.learn2learn.model.AuthenticationInfo;
import com.github.bkhezry.learn2learn.model.ResponseMessage;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface APIService {

  @POST("token")
  @FormUrlEncoded
  Call<AuthenticationInfo> storeUser(
      @Field("token") String token);

  @PUT("user/{uuid}")
  @FormUrlEncoded
  Call<ResponseMessage> updateUser(
      @Path("uuid") String uuid,
      @Field("first_name") String firstName,
      @Field("last_name") String lastName,
      @Field("gender") int gender);
}