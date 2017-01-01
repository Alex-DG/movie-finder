package com.android.alex.moviefinder.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Alex on 27/12/2016.
 */

public class MovieService {

    public static final String BASE_URL = "http://www.omdbapi.com";
    private static Retrofit retrofit = null;


    public static Retrofit getClient() {

        if (retrofit == null) {

            // Interceptor
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            // OkHttpClient
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .connectTimeout(15000, TimeUnit.SECONDS)
                    .readTimeout(15000, TimeUnit.SECONDS)
                    .writeTimeout(15000, TimeUnit.SECONDS)
                    .build();

            // Retrofit
            retrofit = new Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }
}
