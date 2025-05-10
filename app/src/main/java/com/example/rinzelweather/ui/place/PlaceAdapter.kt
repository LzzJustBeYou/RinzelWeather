package com.example.rinzelweather.ui.place

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rinzelweather.R
import com.example.rinzelweather.logic.model.Place
import com.example.rinzelweather.ui.werther.WeatherActivity

class PlaceAdapter(private val fragment: PlaceFragment, private val placeList: List<Place>) :
    RecyclerView.Adapter<PlaceAdapter.ViewHolder>() {

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val placeName: TextView = view.findViewById(R.id.place_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.place_item, parent, false)
        val holder = ViewHolder(view)
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val place = placeList[position]
        holder.placeName.text = place.name

        holder.itemView.setOnClickListener {
            // 处理点击事件，可以保存选中的地点并跳转到天气界面
            val intent = Intent(fragment.requireContext(), WeatherActivity::class.java).apply {
                putExtra("location_lng", place.location.longitude.toString())
                putExtra("location_lat", place.location.latitude.toString())
                putExtra("place_name", place.name)
            }
            fragment.startActivity(intent)
            fragment.activity?.finish()
        }
    }

    override fun getItemCount() = placeList.size
}