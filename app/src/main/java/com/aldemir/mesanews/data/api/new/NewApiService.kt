package com.aldemir.mesanews.data.api.new

import com.aldemir.mesanews.data.model.ResponseNew
import retrofit2.http.GET
import retrofit2.http.Path

interface NewApiService {

    @GET("users/{current_page}")
    suspend fun getAllNews(
        @Path("current_page") current_page: String,
        @Path("per_page") per_page: String
    ): List<ResponseNew>
}