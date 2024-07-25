package com.example.bookingparkir.ListViewPark

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.example.bookingparkir.API.ApiService
import com.example.bookingparkir.API.CheckResponse
import com.example.bookingparkir.API.Reservasi
import com.example.bookingparkir.API.RetrofitClient
import com.example.bookingparkir.API.TempatParkirDetail
import com.example.bookingparkir.MainActivity
import com.example.bookingparkir.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailReserveActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_reserve)
        val idTempatParkir = intent.getIntExtra("idTempatParkir", -1)

        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        val sharedPreferences = getSharedPreferences("MySharedPrefs", MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", null)

        val nama: TextView = findViewById(R.id.namatempat)
        val alamat: TextView = findViewById(R.id.alamat)
        val harga: TextView = findViewById(R.id.harga)
        val kapasitas: TextView = findViewById(R.id.kapasitas)
        val tersedia: TextView = findViewById(R.id.tersedia)

        val btnBacktoHome: ImageButton = findViewById(R.id.btnBacktoHome)
        val btnBookingNow: Button = findViewById(R.id.reservebutton)

        val mapView: MapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.onResume() // needed to get the map to display immediately

        //get data from api
        apiService.getDetailTempatParkir(idTempatParkir).enqueue(object :
            Callback<TempatParkirDetail> {
            override fun onResponse(call: Call<TempatParkirDetail>, response: Response<TempatParkirDetail>) {
                if (response.isSuccessful) {
                    val detail = response.body()
                    detail?.let {
                        nama.text = it.namatempat
                        alamat.text = it.alamat
                        val long = it.longg
                        val lat = it.lat
                        harga.text = it.harga.toString()
                        kapasitas.text = it.kapasitas.toString()
                        tersedia.text = it.tersedia.toString()

                        mapView.getMapAsync { googleMap ->
                            val latLng = LatLng(lat, long) // Example coordinates
                            googleMap.addMarker(MarkerOptions().position(latLng).title(it.namatempat))
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14f))
                        }
                    }
                } else {
                    Log.e("DetailReserveActivity", "Error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<TempatParkirDetail>, t: Throwable) {
                Log.e("DetailReserveActivity", "API Call Failed", t)
            }
        })

        //button action
        btnBookingNow.setOnClickListener {
            if (!userId.isNullOrEmpty()) {
                attemptBooking(idTempatParkir, userId)
            } else {
                // Handle error, user id tidak ditemukan
            }
        }

        btnBacktoHome.setOnClickListener{
            finish()
        }

    }

    private fun attemptBooking(idTempatParkir: Int, userId: String?) {
        val reservasiData = Reservasi(
            id = 0,
            user_id = userId,
            parkir_id = idTempatParkir
        )
        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        apiService.bookReservation(reservasiData).enqueue(object : Callback<CheckResponse> {
            override fun onResponse(call: Call<CheckResponse>, response: Response<CheckResponse>) {
                if (response.isSuccessful) {
                    val messageResponse = response.body()
                    val message = messageResponse?.message
                    Toast.makeText(this@DetailReserveActivity, message, Toast.LENGTH_SHORT).show()

                    val intentMainActivity = Intent(this@DetailReserveActivity, MainActivity::class.java)
                    intentMainActivity.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    intentMainActivity.putExtra("fragmentToLoad", "home")
                    startActivity(intentMainActivity)
                    finish()
                } else {
                    val errorBody = response.errorBody()?.string()
                    errorBody?.let {
                        val errorResponse = Gson().fromJson(it, CheckResponse::class.java)
                        val errorMessage = errorResponse.message
                        Toast.makeText(this@DetailReserveActivity, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<CheckResponse>, t: Throwable) {
                Toast.makeText(this@DetailReserveActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}