package com.example.photogallery.network

import android.util.Log
import com.example.photogallery.model.FlickrResponse
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface FlickrApi {

    @GET("services/rest/?method=flickr.interestingness.getList")
    suspend fun getInterestingness(): FlickrResponse

    @GET("services/rest/?method=flickr.photos.search")
    suspend fun getPhotosBySearchText(@Query("text") query: String): FlickrResponse

    companion object {

        private const val TAG = "FlickrApi"
        private const val FLICKER_API_KEY = "152a8a273ed1cb8988cef8f1e38583dc"
        private const val FLICKER_BASE_URL = "https://api.flickr.com/"

        private var FLICKER_API: FlickrApi? = null

        fun get(): FlickrApi {
            Log.d(TAG, "FlickerApi instance requested")
            if (FLICKER_API == null) synchronized(this) {
                val okHttpClient = OkHttpClient.Builder()
                    .addInterceptor(FlickerInterceptor())
                    .build()

                val retrofit = Retrofit.Builder()
                    .baseUrl(FLICKER_BASE_URL)
                    .addConverterFactory(MoshiConverterFactory.create())
                    .client(okHttpClient)
                    .build()

                FLICKER_API = retrofit.create(FlickrApi::class.java)
                Log.d(TAG, "FlickerApi instance created")
            }
            return FLICKER_API!!
        }
    }

    private class FlickerInterceptor : Interceptor {

        override fun intercept(chain: Interceptor.Chain): Response {
            val originalRequest = chain.request()
            val url: HttpUrl = originalRequest.url.newBuilder()
                .addQueryParameter("api_key", FLICKER_API_KEY)
                .addQueryParameter("format", "json")
                .addQueryParameter("nojsoncallback", "1")
                .addQueryParameter("extras", "url_s")
                .addQueryParameter("safesearch", "3")
                .build()

            Log.d(TAG, url.toString())
            val newRequest = originalRequest.newBuilder().url(url).build()

            return chain.proceed(newRequest)
        }

    }

}