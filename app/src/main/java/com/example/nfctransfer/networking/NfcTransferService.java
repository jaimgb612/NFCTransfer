package com.example.nfctransfer.networking;

import com.example.nfctransfer.networking.ApiResponses.AuthResponse;
import com.example.nfctransfer.networking.ApiResponses.Profile.PullProfileResponse;
import com.example.nfctransfer.networking.ApiResponses.RegisterResponse;
import com.example.nfctransfer.networking.ApiResponses.SimpleResponse;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface NfcTransferService {

    String INDEX_USERID = "userid";
    String INDEX_TARGETID = "targetid";
    String INDEX_TOKEN = "token";
    String INDEX_ACCESS_TOKEN = "token";
    String INDEX_CELLPHONE = "cellphone";
    String INDEX_PASSWORD = "password";
    String INDEX_FIRSTNAME = "firstname";
    String INDEX_LASTNAME = "lastname";
    String INDEX_FIELD_TEXT_VALUE = "field_text_value";
    String INDEX_FIELD_SHARED_STATUS = "field_shared_status";
    String INDEX_FIELD_SOCIAL_ID = "field_social_id";

    String PATH_FIELD_NAME = "field_name";

    // AUTHENTICATE USER
    @FormUrlEncoded
    @POST("auth/token")
    Call<AuthResponse> authenticate(@Field(INDEX_CELLPHONE) String cellphone,
                                    @Field(INDEX_PASSWORD) String password);

    // REGISTER USER
    @FormUrlEncoded
    @POST("/register")
    Call<RegisterResponse> register(@Field(INDEX_CELLPHONE) String cellphone,
                                    @Field(INDEX_PASSWORD) String password,
                                    @Field(INDEX_FIRSTNAME) String firstname,
                                    @Field(INDEX_LASTNAME) String lastname);

    // SEND ACTIVATION
    @FormUrlEncoded
    @POST("/send_activation")
    Call<SimpleResponse> sendActivationCode(@Field(INDEX_USERID) String userId);

    // VERIFY ACTIVATION CODE
    @FormUrlEncoded
    @POST("/verify")
    Call<SimpleResponse> verifyAccount(@Field(INDEX_USERID) String userId,
                                       @Field(INDEX_TOKEN) String token);

    // GET SELF PROFILE
    @GET("/api/profile/self")
    Call<PullProfileResponse> pullProfileData(@Query(INDEX_ACCESS_TOKEN) String accessToken);

    // GET TARGET PROFILE
    @GET("/api/profile/{targetid}")
    Call<PullProfileResponse> pullTargetProfileData(@Path(INDEX_TARGETID) String targetId,
                                             @Query(INDEX_ACCESS_TOKEN) String accessToken);

    // ADD PROFILE FIELD
    @FormUrlEncoded
    @POST("/api/profile/field/{field_name}")
    Call<SimpleResponse> addProfileField(@Path(PATH_FIELD_NAME) String fieldName,
                                          @Field(INDEX_ACCESS_TOKEN) String accessToken,
                                          @Field(INDEX_FIELD_TEXT_VALUE) String textValue,
                                          @Field(INDEX_FIELD_SOCIAL_ID) String fieldSocialId);

    // EDIT PROFILE FIELD
    @FormUrlEncoded
    @PUT("/api/profile/field/{field_name}")
    Call<SimpleResponse> editProfileField(@Path(PATH_FIELD_NAME) String fieldName,
                                          @Field(INDEX_ACCESS_TOKEN) String accessToken,
                                          @Field(INDEX_FIELD_TEXT_VALUE) String textValue,
                                          @Field(INDEX_FIELD_SOCIAL_ID) String fieldSocialId,
                                          @Field(INDEX_FIELD_SHARED_STATUS) boolean sharedStatus);

    // DELETE PROFILE FIELD
    @DELETE("/api/profile/field/{field_name}")
    Call<SimpleResponse> deleteProfileField(@Path(PATH_FIELD_NAME) String fieldName,
                                            @Query(INDEX_ACCESS_TOKEN) String accessToken);
}

