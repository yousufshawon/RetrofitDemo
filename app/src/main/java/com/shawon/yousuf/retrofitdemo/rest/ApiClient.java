package com.shawon.yousuf.retrofitdemo.rest;


import android.util.Log;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.shawon.yousuf.retrofitdemo.App;
import com.shawon.yousuf.retrofitdemo.BuildConfig;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// Created by user on 8/19/2016.
/**
 *
 */

public class ApiClient {

    public static final String BASE_URL = BuildConfig.API_BASE_URL;
    private static Retrofit retrofit = null;

    private static final String CACHE_CONTROL = "Cache-Control";


    private static String TAG = ApiClient.class.getSimpleName();


    public static Retrofit getClient() {

        if (retrofit==null) {

            OkHttpClient okHttpClient = getOkHttpClient();

            Picasso picasso = new Picasso.Builder(App.getInstance())
                    .downloader(new OkHttp3Downloader(okHttpClient))
                    .build();

            Log.i(TAG, "Picasso hashCode: " + picasso.hashCode());

            Picasso.setSingletonInstance(picasso);


            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }





    private static OkHttpClient getOkHttpClient(){

        return new OkHttpClient.Builder()
                .addInterceptor(provideLoginInterceptor())
                .addInterceptor(provideOfflineCacheInterceptor())
                .addNetworkInterceptor(provideCacheInterceptor())
                .cache(provideCache())
                .build();
    }

    private static Interceptor provideLoginInterceptor(){

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);

        return loggingInterceptor;
    }



    private static Cache provideCache(){
        Cache cache = null;


        try {

            cache = new Cache(new File(App.getInstance().getCacheDir(), "http-cache"),
                    10* 1024 * 1024);  // 10 MB
        }catch (Exception e){
            Log.e(TAG, e.getMessage());
        }

        return cache;
    }



    private static Interceptor provideCacheInterceptor(){
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {

                Response response = chain.proceed(chain.request());

                CacheControl cacheControl = new CacheControl.Builder()
                        .maxAge(2, TimeUnit.MINUTES)
                        .build();

                return response.newBuilder()
                        .header(CACHE_CONTROL, cacheControl.toString())
                        .build();
            }
        };
    }


    private static Interceptor provideOfflineCacheInterceptor(){

        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {

                Request request = chain.request();

                if (!App.hasNetwork()) {

                    CacheControl cacheControl = new CacheControl.Builder()
                            .maxStale(7, TimeUnit.DAYS)
                            .build();

                    request = request.newBuilder()
                            .cacheControl(cacheControl)
                            .build();

                }

                return chain.proceed(request);

            }
        };

    }


}
