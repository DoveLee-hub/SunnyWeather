package com.sunnyweather.android.logic.network

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Query
import java.lang.RuntimeException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object SunnyWeatherNetwork {
    private val placeService = ServiceCreator.create<PlaceService>()

    suspend fun searchPlaces(query: String) = placeService.searchPlaces(query).await()
    //SunnyWeatherNetwork.searchPlaces返回的是解析好Json的Call类型，其中包含status和places属性(详见PlaceResponse.kt)

    private suspend fun <T> Call<T>.await() :T{
        return suspendCoroutine { continuation ->
            enqueue(object :Callback<T>{
                override fun onFailure(call: Call<T>, t: Throwable) {
                    continuation.resumeWithException(t)
                }

                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val body = response.body()
                    if(body!=null) continuation.resume(body)
                    else continuation.resumeWithException(RuntimeException("Response body is null"))
                }
            })
        }
    }
}