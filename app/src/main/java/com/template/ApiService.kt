package com.template

import com.google.gson.JsonObject
import com.squareup.okhttp.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*
import java.net.URL
import java.util.*
import kotlin.jvm.Throws

interface ApiService {

    @GET("/")
    suspend fun getURL(
        @Query("packageid") template: String = "com.template",
        @Query("usserid") uuid: String,
        @Query("getz") timezone: String,
        @Query("getr") getr: String = "utm_source=google-play",
        @Query("utm_medium") organic: String = "organic"
    ): String
}