package com.sunnyweather.android.logic

import androidx.lifecycle.liveData
import com.sunnyweather.android.SunnyWeatherApplication
import com.sunnyweather.android.logic.model.Place
import com.sunnyweather.android.logic.network.SunnyWeatherNetwork
import kotlinx.coroutines.Dispatchers
import java.lang.Exception

object Repository {

    fun searchPlaces(query : String) = liveData(Dispatchers.IO) {
        val result = try{
            val placeResponse = SunnyWeatherNetwork.searchPlaces(query)
            //SunnyWeatherNetwork.searchPlaces返回的是解析好Json的Call类型，其中包含status和places属性(详见PlaceResponse.kt)
            if(placeResponse.status == "ok"){ //通过status的属性值判断请求属否成功
                val places = placeResponse.places
                Result.success(places)//将places打包进Result，通过Result的构造函数，将places赋值给Result中的泛型变量value
            }else{
                Result.failure(RuntimeException("response status is ${placeResponse.status}"))
            }
        }catch (e :Exception){
            Result.failure<List<Place>>(e)
        }
        emit(result) //发送出result
    }
}