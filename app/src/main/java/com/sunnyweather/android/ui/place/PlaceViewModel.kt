package com.sunnyweather.android.ui.place

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.sunnyweather.android.logic.Repository
import com.sunnyweather.android.logic.model.Place

class PlaceViewModel:ViewModel(){
    private val searchLiveData = MutableLiveData<String>()

    val placeList = ArrayList<Place>()

    val placeLiveData = Transformations.switchMap(searchLiveData){query ->
        Repository.searchPlaces(query)
        //通过switchMap监控searchLiveData的变化，一旦变化，便调用lambda表达式
        //lambda会返回一个livedata，但这个livedata不是viewmodel中的，是repository中的，所以不能被监听
        //switchmap将这个不能监听的livadata转换为可监听的placeLiveData，因为这个是在viewmodel中定义的
        //所以实际上placeLiveData就是可监听版的 Repository.searchPlaces返回的livedata
    }

    fun searchPlaces(query : String){
        searchLiveData.value = query
    }
}