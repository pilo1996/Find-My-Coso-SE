package com.camoli.findmycoso.api;

import com.camoli.findmycoso.activities.DeviceList;
import com.camoli.findmycoso.models.Position;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface FMC_API {

    /*********************************** USERS ***********************************/

    @FormUrlEncoded
    @POST("createuser")
    Call<ResponseBody> createUser(
            @Field("email") String email,
            @Field("nome") String nome,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("userlogin")
    Call<LoginResponse> userlogin(
            @Field("email") String email,
            @Field("password") String password
    );

    @FormUrlEncoded
    @PUT("updateUser/{id}")
    Call<LoginResponse> updateUser(
            @Path("id") int id,
            @Field("nome") String nome,
            @Field("profile_pic") String profile_pic
    );

    @Multipart
    @POST("uploadProfilePic")
    Call<UploadResponse> uploadProfilePic(
        @Part("image") MultipartBody.Part img,
        @Part("userID") RequestBody userID
    );

    @FormUrlEncoded
    @PUT("updateSelectedDevice")
    Call<DefaultResponse> updateSelectedDevice(
            @Field("user_id") int userID,
            @Field("device_id") int deviceID
    );

    /*********************************** LOCATIONS ***********************************/

    @FormUrlEncoded
    @PUT("addPosition")
    Call<PositionResponse> addPosition(
            @Field("device_fk") int device_fk,
            @Field("user_fk") int user_fk,
            @Field("via") String via,
            @Field("latitudine") String latitudine,
            @Field("longitudine") String longitudine,
            @Field("dayTime") String dayTime,
            @Field("dateTime") String dateTime
    );


    @FormUrlEncoded
    @POST("getAllPositionsFromDeviceID")
    Call<PositionListResponse> getAllPositionsFromDeviceID(
            @Field("userid") int userid,
            @Field("deviceid") int deviceid
    );

    @DELETE("deleteSinglePositionByID/{locationID}")
    Call<DefaultResponse> deleteSinglePositionByID(
            @Path("locationID") int locationID
    );

    @FormUrlEncoded
    @POST("deleteAllPositionsByDevice")
    Call<DefaultResponse> deleteAllPositionsByDevice(
            @Field("deviceid") int deviceID,
            @Field("userid") int userID
    );

    /*********************************** DEVICES ***********************************/

    @FormUrlEncoded
    @POST("registerDevice")
    Call<DeviceResponse> registerDevice(
            @Field("nome_device") String nome_device,
            @Field("uuid_device") String uuid_device,
            @Field("ownerid") int ownerid
    );

    @FormUrlEncoded
    @POST("bookmarkDevice")
    Call<DefaultResponse> bookmarkDevice(
            @Field("userID") int userID,
            @Field("deviceID") int deviceID
    );

    //@FormUrlEncoded
    @GET("getAllDevicesRegistered/{userID}")
    Call<DeviceListResponse> getAllDevicesRegistered(
            @Path("userID") int userID
    );

    //@FormUrlEncoded
    @GET("getAllDevicesBookmarked/{userID}")
    Call<DeviceListResponse> getAllDevicesBookmarked(
            @Path("userID") int userID
    );

    //@FormUrlEncoded
    @DELETE("removeBookmarkedDevice/{deviceID, userID}")
    Call<DefaultResponse> removeBookmarkedDevice(
            @Path("deviceID") int deviceID,
            @Path("userID") int userID
    );

    //@FormUrlEncoded
    @DELETE("removeDeviceRegistered/{deviceID}")
    Call<DefaultResponse> removeDeviceRegistered(
            @Path("deviceID") int deviceID
    );




}
