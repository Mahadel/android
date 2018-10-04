package com.github.bkhezry.learn2learn.service;

import com.github.bkhezry.learn2learn.model.AuthenticationInfo;
import com.github.bkhezry.learn2learn.model.Category;
import com.github.bkhezry.learn2learn.model.ResponseMessage;
import com.github.bkhezry.learn2learn.model.UserSkill;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
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


  @GET("category")
  Call<List<Category>> getCategories();

  @GET("user/{uuid}/skill")
  Call<List<UserSkill>> getUserSkills(@Path("uuid") String uuid);

  @POST("user/{uuid}/skill")
  @FormUrlEncoded
  Call<UserSkill> addUserSkill(@Path("uuid") String uuid,
                               @Field("skill_uuid") String skillUUID,
                               @Field("description") String description,
                               @Field("skill_type") int skillType);

  @PUT("user/{uuid}/skill")
  @FormUrlEncoded
  Call<UserSkill> editUserSkill(@Path("uuid") String uuid,
                                @Field("uuid") String userSkillUUID,
                                @Field("description") String description);

  @DELETE("user/{uuid}/skill/{user_skill_uuid}")
  Call<ResponseMessage> deleteUserSkill(@Path("uuid") String uuid,
                                        @Path("user_skill_uuid") String userSkillUUID);
}
