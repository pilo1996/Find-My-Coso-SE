package com.camoli.findmycoso.api;

import com.camoli.findmycoso.activities.DeviceList;
import com.camoli.findmycoso.models.Position;

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
            @Field("device_fk") String via,
            @Field("device_fk") double latitudine,
            @Field("device_fk") double longitudine,
            @Field("device_fk") String dayTime,
            @Field("device_fk") String dateTime
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

    @DELETE("deleteAllPositionsByDevice/{deviceID, userID}")
    Call<DefaultResponse> deleteAllPositionsByDevice(
            @Path("deviceID") int deviceID,
            @Path("userID") int userID
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

    @FormUrlEncoded
    @POST("getAllDevicesRegistered/{userID}")
    Call<DeviceListResponse> getAllDevicesRegistered(
            @Path("userID") int userID
    );

    @FormUrlEncoded
    @POST("getAllDevicesBookmarked/{userID}")
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
