package com.camoli.findmycoso.api;

import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String BASE_URL = "http://camoli.ns0.it/FMCRestAPI/public/";
    private static RetrofitClient mInstance;
    private Retrofit retrofit;

    private RetrofitClient(){
        retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create())).build();
    }

    public static synchronized RetrofitClient getInstance(){
        if(mInstance == null)
            mInstance = new RetrofitClient();
        return mInstance;
    }

    public FMC_API getApi(){
        return retrofit.create(FMC_API.class);
    }

}
