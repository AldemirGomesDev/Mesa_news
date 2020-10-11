package com.aldemir.mesanews.data.api

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkConfig {
    lateinit var context: Context
    private const val BASE_URL = "https://mesa-news-api.herokuapp.com"

    fun <T> provideApi(clazz: Class<T>, context: Context?): T {

        val retrofit = Retrofit
            .Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .client(okhttpClient(context)) // Add our Okhttp client
            .build()

        return retrofit.create(clazz)
    }

    private fun okhttpClient(context: Context?): OkHttpClient {
        return OkHttpClient.Builder()
            .readTimeout(40, TimeUnit.SECONDS)
            .connectTimeout(40, TimeUnit.SECONDS)
            .addInterceptor(AuthInterceptor(context))
            .build()
    }
}