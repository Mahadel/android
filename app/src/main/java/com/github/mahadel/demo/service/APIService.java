package com.github.mahadel.demo.service;

import com.github.mahadel.demo.model.About;
import com.github.mahadel.demo.model.AuthenticationInfo;
import com.github.mahadel.demo.model.Category;
import com.github.mahadel.demo.model.ConnectionReceiveItem;
import com.github.mahadel.demo.model.ConnectionRequest;
import com.github.mahadel.demo.model.ResponseMessage;
import com.github.mahadel.demo.model.SearchResult;
import com.github.mahadel.demo.model.UserInfo;
import com.github.mahadel.demo.model.UserSkill;

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

  @GET("user/{uuid}/info")
  Call<UserInfo> getUserInfo(@Path("uuid") String uuid);

  @GET("search/{uuid}")
  Call<List<SearchResult>> search(@Path("uuid") String uuid);

  @POST("user/{uuid}/connection")
  @FormUrlEncoded
  Call<ResponseMessage> requestConnection(@Path("uuid") String userUUIDFrom,
                                          @Field("user_uuid_to") String userUUIDTo,
                                          @Field("learn_skill_uuid_from") String learnSkillUUIDFrom,
                                          @Field("teach_skill_uuid_from") String teachSkillUUIDFrom,
                                          @Field("description") String description);

  @GET("user/{uuid}/connection")
  Call<ConnectionRequest> getUserConnectionRequest(@Path("uuid") String uuid);

  @DELETE("user/{uuid}/connection/{connection_uuid}")
  Call<ResponseMessage> deleteConnectionRequest(@Path("uuid") String uuid,
                                                @Path("connection_uuid") String connectionUUID);

  @PUT("user/{uuid}/connection/{connection_uuid}")
  @FormUrlEncoded
  Call<ConnectionReceiveItem> editConnection(@Path("uuid") String uuid,
                                             @Path("connection_uuid") String connectionUUID,
                                             @Field("is_accept") int isActive);

  @DELETE("user/{uuid}")
  Call<ResponseMessage> deleteUserAccount(@Path("uuid") String uuid);

  @GET("about")
  Call<About> getAbout();
}