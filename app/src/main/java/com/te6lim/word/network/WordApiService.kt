package com.te6lim.word.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

private const val BASE_URL = "https://random-words5.p.rapidapi.com/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()


private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .build()

interface WordApiService {

    @GET("getMultipleRandom")
    fun getWordAsync(
        @Header("X-RapidAPI-Key") key: String,
        @Header("X-RapidAPI-Host") host: String = "random-words5.p.rapidapi.com",
        @Query("wordLength") length: Int = 5,
        @Query("count") count: Int = 5
    ): Deferred<List<String>>
}

class WordApi(val apiKey: String, val wordApiService: WordApiService) {

    companion object {

        var INSTANCE: WordApi? = null

        fun getInstance(apiKey: String): WordApi {
            var instance = INSTANCE
            if (instance == null) {
                val retrofitService: WordApiService by lazy {
                    retrofit.create(WordApiService::class.java)
                }
                instance = WordApi(apiKey, retrofitService)
                INSTANCE = instance
            }
            return instance
        }
    }
}