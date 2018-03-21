package com.shawon.yousuf.retrofitdemo.rest;


import com.shawon.yousuf.retrofitdemo.BuildConfig;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// Created by user on 8/19/2016.
/**
 *
 */

public class ApiClient {

    public static final String BASE_URL = BuildConfig.API_BASE_URL;
    private static Retrofit retrofit = null;


    public static Retrofit getClient() {
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

}
