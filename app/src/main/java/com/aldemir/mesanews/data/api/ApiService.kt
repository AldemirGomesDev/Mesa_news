package com.aldemir.mesanews.data.api

import com.aldemir.mesanews.data.api.model.*
import com.aldemir.mesanews.ui.register.domain.Post
import io.reactivex.Single
import retrofit2.http.*

interface ApiService {

    @POST("/v1/client/auth/signin")
    suspend fun sinIn(
        @Body requestLogin: RequestLogin
    ): ResponseLogin

    @POST("/v1/client/auth/signup")
    suspend fun sinUp(
        @Body requestRegister: RequestRegister
    ): ResponseLogin

    @GET("/v1/client/news")
    suspend fun getAllNews(
        @Query("current_page") current_page: String,
        @Query("per_page") per_page: String,
        @Query("published_at") published_at: String
    ): ResponseNew

    @GET("/v1/client/news/highlights")
    suspend fun getAllNewsHighlights(): ResponseNewHighlights

    @GET("posts")
    fun getPosts(): Single<List<Post>>
}