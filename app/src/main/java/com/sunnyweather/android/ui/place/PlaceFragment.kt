package com.sunnyweather.android.ui.place

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.sunnyweather.android.R
import kotlinx.android.synthetic.main.fragment_place.*
import kotlinx.android.synthetic.main.fragment_place.view.*

class PlaceFragment : Fragment() {
    private val viewModel by lazy{ViewModelProviders.of(this).get(PlaceViewModel::class.java)}

    private lateinit var adapter : PlaceAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_place,container,false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager
        adapter =  PlaceAdapter(this,viewModel.placeList)
        recyclerView.adapter = adapter
        searchPlaceEdit.addTextChangedListener{editable ->
            val content = editable.toString()
            if(content.isNotEmpty()){
                viewModel.searchPlaces(content)
                //通过content传入的字符串查询信息，调用ViewModel.searchPlaces并更深入的调用Repository.searchPlaces中的SunnyWeatherNetwork.searchPlaces
                //SunnyWeatherNetwork.searchPlaces返回的是解析好Json的Call类型，其中包含status和places属性(详见PlaceResponse.kt)
                recyclerView.visibility = View.GONE
                byImageView.visibility = View.VISIBLE
                viewModel.placeList.clear()
                adapter.notifyDataSetChanged()
            }
        }
        //当上面的content不为空时，就会执行viewModel.searchPlaces(content)
        //就会导致viewModel.placeLiveData的改变
        //从而使得Observer中的lambda被执行
        //lambda中的result是Repository中searchPlaces中返回的livedata中的result，result中只包含了places(Places是一个collection类型)
        //（为什么是Repository中searchPlaces中返回的livedata？见PlaceViewModel中的注释）
        viewModel.placeLiveData.observe(this, Observer { result ->
            val places = result.getOrNull()
            if(places != null){
                recyclerView.visibility = View.VISIBLE
                byImageView.visibility = View.GONE
                viewModel.placeList.clear()
                viewModel.placeList.addAll(places)  //把places添加到placelist中
                adapter.notifyDataSetChanged()  //更新adapter
            }else{
                Toast.makeText(activity,"未能查询到任何地点",Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
        })
    }
}