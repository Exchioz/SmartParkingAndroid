package com.example.bookingparkir.ListViewPark

import android.content.Context
import com.example.bookingparkir.API.ApiService
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ListView
import android.widget.SearchView
import com.example.bookingparkir.API.PropertyModel
import com.example.bookingparkir.API.RetrofitClient
import com.example.bookingparkir.API.TempatParkir
import com.example.bookingparkir.MainActivity
import com.example.bookingparkir.R
import retrofit2.Call
import retrofit2.Response

class ReserveActivity : AppCompatActivity() {
    private lateinit var listView: ListView
    private lateinit var searchView: SearchView
    private lateinit var propertyList: List<PropertyModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reserve)

        val sharedPreferences = getSharedPreferences("MySharedPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences?.getString("userId", null)
        val btnBack: ImageButton = findViewById(R.id.btnBacktoHome)

        btnBack.setOnClickListener {
            finish()
        }

        //cal api
        listView = findViewById(R.id.listView)
        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        apiService.getTempatParkir().enqueue(object : retrofit2.Callback<List<TempatParkir>> {
            override fun onResponse(call: Call<List<TempatParkir>>, response: Response<List<TempatParkir>>) {
                if (response.isSuccessful) {
                    propertyList = response.body()?.map { tempatParkir ->
                        PropertyModel(
                            tempatParkir.id,
                            tempatParkir.namatempat,
                            tempatParkir.alamat,
                            tempatParkir.harga,
                            tempatParkir.tersedia ?: 0
                        )
                    } ?: emptyList()

                    val propertyListAdapter = PropertyListAdapter(this@ReserveActivity, propertyList)
                    listView.adapter = propertyListAdapter
                } else {
                    Log.e("ReserveActivity", "Response tidak berhasil")
                }
            }


            override fun onFailure(call: Call<List<TempatParkir>>, t: Throwable) {
                Log.e("ReserveActivity", "Gagal mengambil data: ${t.message}")
            }
        })

    }

}