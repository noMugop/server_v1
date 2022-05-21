package com.template

import android.util.Log
import androidx.viewbinding.BuildConfig
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit


class ApiFactory() {

    companion object {

        private val LOCK = Any()
        private var apiService: ApiService? = null
        private var retrofit: Retrofit? = null

        fun getInstance(baseURL: String): ApiService {

            apiService?.let { return it }

            synchronized(LOCK) {

                apiService?.let { return it }

                val instance = Retrofit.Builder()
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .baseUrl(baseURL)
                    .client(getOkHttp())
                    .build()
                retrofit = instance
                apiService = instance.create(ApiService::class.java)
                return apiService as ApiService
            }
        }

        private fun getOkHttp(): OkHttpClient {
            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(MyInterceptor())
                .addInterceptor(getLoggingInterceptor())
            return okHttpClient.build()
        }

        class MyInterceptor: Interceptor {
            override fun intercept (chain: Interceptor.Chain): Response {
                val user_agent = System.getProperty("http.agent")
                val request = chain.request()
                val value1 = request.url.toString().replace("%3D", "=")
                val value2 = request.url.toString().replace("%2F", "/")
                var newRequest = request
                    .newBuilder()
                    .url(value1)
                    .url(value2)
                    .header("User-Agent", user_agent as String)
                    .build()

                return chain.proceed(newRequest)
            }
        }

        private fun getLoggingInterceptor(
        ): HttpLoggingInterceptor {
            return HttpLoggingInterceptor(logger = object : HttpLoggingInterceptor.Logger {
                override fun log(message: String) {
                    Log.d("OkHttp", message)
                }
            }).apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
        }
    }
}