package com.example.rinzelweather.ui.place

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rinzelweather.MainActivity
import com.example.rinzelweather.R
import com.example.rinzelweather.ui.werther.WeatherActivity
import kotlinx.coroutines.launch

class PlaceFragment: Fragment() {
    val viewModel by lazy { ViewModelProvider(this).get(PlaceViewModel::class.java) }
    private lateinit var adapter: PlaceAdapter
    private lateinit var searchPlaceEdit: EditText
    private lateinit var bgImageView: ImageView
    private lateinit var recyclerView: androidx.recyclerview.widget.RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 初始化视图组件
        recyclerView = view.findViewById(R.id.recycler_view)
        searchPlaceEdit = view.findViewById(R.id.search_place_edit)
        bgImageView = view.findViewById(R.id.bg_image_view)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_palce, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (activity is MainActivity && viewModel.isPlaceSaved()) {
            val intent = Intent(context, WeatherActivity::class.java)
            startActivity(intent)
            activity?.finish()
            return
        }

        // 配置RecyclerView
        val layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager
        adapter = PlaceAdapter(this, viewModel.filterPlaces.value)
        recyclerView.adapter = adapter
        
        searchPlaceEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            
            override fun afterTextChanged(editable: Editable?) {
                val content = editable.toString()
                if (content.isNotEmpty()) {
                    viewModel.updateSearchQuery(content)
                } else {
                    recyclerView.visibility = View.GONE
                    bgImageView.visibility = View.VISIBLE
                    viewModel.updateSearchQuery("")
                }
            }
        })
        
        lifecycleScope.launch {
            // 观察筛选后的地点列表
            viewModel.filterPlaces.collect { places ->
                if (places.isNotEmpty() && searchPlaceEdit.text.isNotEmpty()) {
                    recyclerView.visibility = View.VISIBLE
                    bgImageView.visibility = View.GONE
                    adapter = PlaceAdapter(this@PlaceFragment, places)
                    recyclerView.adapter = adapter
                } else if (searchPlaceEdit.text.isEmpty()) {
                    recyclerView.visibility = View.GONE
                    bgImageView.visibility = View.VISIBLE
                }
            }
        }
        
        lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                // 可以添加加载指示器
            }
        }
    }
}