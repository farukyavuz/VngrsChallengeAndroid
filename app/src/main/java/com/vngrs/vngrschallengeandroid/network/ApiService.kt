package com.vngrs.vngrschallengeandroid.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.experimental.CoroutineCallAdapterFactory
import com.vngrs.vngrschallengeandroid.common.BASE_URL
import com.vngrs.vngrschallengeandroid.common.OAUTH2_TOKEN_SUFFIX
import com.vngrs.vngrschallengeandroid.common.SEARCH_SUFFIX
import com.vngrs.vngrschallengeandroid.model.SearchResponseModel
import com.vngrs.vngrschallengeandroid.model.TokenResponseModel
import kotlinx.coroutines.experimental.Deferred
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST(OAUTH2_TOKEN_SUFFIX)
    fun getToken(
        @Header("Authorization") authorization: String,
        @Field("grant_type") grantType: String
    ): Deferred<TokenResponseModel?>


    @GET(SEARCH_SUFFIX)
    fun getTweetList(
        @Header("Authorization") authorization: String,
        @Query("q") queryText: String
    ): Call<SearchResponseModel>


    companion object {
        val instance: ApiService by lazy {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .baseUrl(BASE_URL)
                .build()
            retrofit.create(ApiService::class.java)
        }

    }
}