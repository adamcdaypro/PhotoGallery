package com.example.photogallery.network

import com.example.photogallery.model.FlickrResponse
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

interface FlickrApi {

    @GET(
        "services/rest/?method=flickr.interestingness.getList" +
                "&api_key=$FLICKER_API_KEY" +
                "&extras=url_s" +
                "&format=json&nojsoncallback=1"
    )
    suspend fun getInterestingness(): FlickrResponse

    companion object {

        private const val FLICKER_API_KEY = "152a8a273ed1cb8988cef8f1e38583dc"
        private const val FLICKER_BASE_URL = "https://api.flickr.com/"

        fun create(): FlickrApi {
            val retrofit = Retrofit.Builder()
                .baseUrl(FLICKER_BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()

            return retrofit.create(FlickrApi::class.java)
        }

    }
}