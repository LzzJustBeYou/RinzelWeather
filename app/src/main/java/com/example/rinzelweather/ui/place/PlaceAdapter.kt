package com.example.rinzelweather.ui.place

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.drawerlayout.widget.DrawerLayout
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
        holder.itemView.setOnClickListener {
            val position = holder.adapterPosition
            val place = placeList[position]
            Log.d("xxx", "place is: $place")
            fragment.viewModel.savePlace(place)
            val activity = fragment.activity
            if (activity is WeatherActivity) {
                activity.findViewById<DrawerLayout>(R.id.drawer_layout).closeDrawers()
                activity.viewModel.reloadWeather()
            } else {
                val intent = Intent(parent.context, WeatherActivity::class.java)
                fragment.startActivity(intent)
                activity?.finish()
            }
        }
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val place = placeList[position]
        holder.placeName.text = place.name
    }

    override fun getItemCount() = placeList.size
}