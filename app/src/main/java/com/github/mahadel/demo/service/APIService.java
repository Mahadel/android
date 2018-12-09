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

  /**
   * @param token String token of google login
   * @return instance of {@link AuthenticationInfo} from server
   */
  @POST("token")
  @FormUrlEncoded
  Call<AuthenticationInfo> storeUser(
      @Field("token") String token);

  /**
   * @param uuid      String uuid of user
   * @param firstName String first name
   * @param lastName  String last name
   * @param gender    Int gender
   * @return instance of {@link ResponseMessage} from server
   */
  @PUT("user/{uuid}")
  @FormUrlEncoded
  Call<ResponseMessage> updateUser(
      @Path("uuid") String uuid,
      @Field("first_name") String firstName,
      @Field("last_name") String lastName,
      @Field("gender") int gender);


  /**
   * @return List of {@link Category} from server
   */
  @GET("category")
  Call<List<Category>> getCategories();

  /**
   * @param uuid String uuid of user
   * @return List of {@link UserSkill} of user from server
   */
  @GET("user/{uuid}/skill")
  Call<List<UserSkill>> getUserSkills(@Path("uuid") String uuid);

  /**
   * @param uuid        String uuid of user
   * @param skillUUID   String uuid of skill
   * @param description String description of user skill
   * @param skillType   Int skill type of user skill
   * @return Instance of {@link UserSkill} that saved in server
   */
  @POST("user/{uuid}/skill")
  @FormUrlEncoded
  Call<UserSkill> addUserSkill(@Path("uuid") String uuid,
                               @Field("skill_uuid") String skillUUID,
                               @Field("description") String description,
                               @Field("skill_type") int skillType);

  /**
   * @param uuid          String uuid of user
   * @param userSkillUUID String uuid of userSkill
   * @param description   String description of userSkill
   * @return Instance of {@link UserSkill} that saved in server
   */
  @PUT("user/{uuid}/skill")
  @FormUrlEncoded
  Call<UserSkill> editUserSkill(@Path("uuid") String uuid,
                                @Field("uuid") String userSkillUUID,
                                @Field("description") String description);

  /**
   * @param uuid          String uuid of user
   * @param userSkillUUID String uuid of userSkill
   * @return Instance of {@link ResponseMessage} response from server
   */
  @DELETE("user/{uuid}/skill/{user_skill_uuid}")
  Call<ResponseMessage> deleteUserSkill(@Path("uuid") String uuid,
                                        @Path("user_skill_uuid") String userSkillUUID);

  /**
   * @param uuid String uuid of user
   * @return Instance of {@link UserInfo}
   */
  @GET("user/{uuid}/info")
  Call<UserInfo> getUserInfo(@Path("uuid") String uuid);

  /**
   * @param uuid String uuid of user
   * @return List of {@link SearchResult} that match with search
   */
  @GET("search/{uuid}")
  Call<List<SearchResult>> search(@Path("uuid") String uuid);

  /**
   * @param userUUIDFrom       String uuid of current user
   * @param userUUIDTo         String uuid of receiver user
   * @param learnSkillUUIDFrom String uuid of skill want to learn
   * @param teachSkillUUIDFrom String uuid of skill
   * @param description        String description of request connection
   * @return Instance of {@link ResponseMessage} response from server
   */
  @POST("user/{uuid}/connection")
  @FormUrlEncoded
  Call<ResponseMessage> requestConnection(@Path("uuid") String userUUIDFrom,
                                          @Field("user_uuid_to") String userUUIDTo,
                                          @Field("learn_skill_uuid_from") String learnSkillUUIDFrom,
                                          @Field("teach_skill_uuid_from") String teachSkillUUIDFrom,
                                          @Field("description") String description);

  /**
   * @param uuid String uuid of user
   * @return Instance of {@link ConnectionRequest} that hold list of send & receive requests.
   */
  @GET("user/{uuid}/connection")
  Call<ConnectionRequest> getUserConnectionRequest(@Path("uuid") String uuid);

  /**
   * @param uuid           String uuid of user
   * @param connectionUUID String uuid of connection request
   * @return Instance {@link ResponseMessage} response from server
   */
  @DELETE("user/{uuid}/connection/{connection_uuid}")
  Call<ResponseMessage> deleteConnectionRequest(@Path("uuid") String uuid,
                                                @Path("connection_uuid") String connectionUUID);

  /**
   * @param uuid           String uuid of user
   * @param connectionUUID String uuid of connection request
   * @param isAccept       Int status of connection
   * @return Instance of {@link ConnectionReceiveItem}
   */
  @PUT("user/{uuid}/connection/{connection_uuid}")
  @FormUrlEncoded
  Call<ConnectionReceiveItem> editConnection(@Path("uuid") String uuid,
                                             @Path("connection_uuid") String connectionUUID,
                                             @Field("is_accept") int isAccept);

  /**
   * @param uuid String uuid of user
   * @return Instance of {@link ResponseMessage} response from server
   */
  @DELETE("user/{uuid}")
  Call<ResponseMessage> deleteUserAccount(@Path("uuid") String uuid);

  /**
   * @return Instance of {@link About} from server
   */
  @GET("about")
  Call<About> getAbout();

  /**
   * @param uuid       String uuid of user
   * @param firebaseId String firebase id token
   * @return Instance of {@link ResponseMessage} response from server
   */
  @PUT("user/{uuid}/firebase")
  @FormUrlEncoded
  Call<ResponseMessage> setFirebaseId(@Path("uuid") String uuid,
                                      @Field("firebase_id") String firebaseId);
}