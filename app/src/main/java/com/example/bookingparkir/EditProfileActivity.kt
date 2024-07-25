package com.example.bookingparkir

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import com.example.bookingparkir.API.ApiService
import com.example.bookingparkir.API.CheckResponse
import com.example.bookingparkir.API.RetrofitClient
import com.example.bookingparkir.API.UpdateUserRequest
import com.example.bookingparkir.API.UserRequest
import com.example.bookingparkir.API.UserResponse
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditProfileActivity : AppCompatActivity() {
    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        val sharedPreferences = getSharedPreferences("MySharedPrefs", Context.MODE_PRIVATE)
        val idUser = sharedPreferences?.getString("userId", null)

        val editName: EditText = findViewById(R.id.editName)
        val editMail: EditText = findViewById(R.id.editMail)
        val editTelp: EditText = findViewById(R.id.editTelp)
        val editPlateNumber: EditText = findViewById(R.id.editPlateNumber)
        val btnSave: Button = findViewById(R.id.editButton)
        val btnBack: ImageButton = findViewById(R.id.btnBacktoHome)

        val apiService = RetrofitClient.instance.create(ApiService::class.java)

        apiService.getUserById(UserRequest(id = idUser.toString())).enqueue(object :
            Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful) {
                    val userResponse = response.body()
                    editName.setText(userResponse?.nama ?: "")
                    editMail.setText(userResponse?.email ?: "")
                    editTelp.setText(userResponse?.telp ?: "")
                    editPlateNumber.setText(userResponse?.platenomor ?: "")
                } else {
                    Log.e("API Error", "Error Code: ${response.code()} Error Message: ${response.message()}")
                    Toast.makeText(this@EditProfileActivity, "Gagal mendapatkan data pengguna", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Toast.makeText(this@EditProfileActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })

        btnBack.setOnClickListener(){
            finish()
        }

        btnSave.setOnClickListener(){
            val nama = editName.text.toString()
            val email = editMail.text.toString()
            val telp = editTelp.text.toString()
            val platenomor = editPlateNumber.text.toString()

            apiService.updateUser(UpdateUserRequest(idUser, email, telp, nama, platenomor)).enqueue(object : Callback<CheckResponse> {
                override fun onResponse(call: Call<CheckResponse>, response: Response<CheckResponse>) {
                    if (response.isSuccessful) {
                        val messageResponse = response.body()
                        val message = messageResponse?.message
                        Toast.makeText(this@EditProfileActivity, message, Toast.LENGTH_LONG).show()
                        finish()
                    } else {
                        val errorBody = response.errorBody()?.string()
                        errorBody?.let {
                            val errorResponse = Gson().fromJson(it, CheckResponse::class.java)
                            val errorMessage = errorResponse.message
                            Toast.makeText(this@EditProfileActivity, errorMessage, Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(call: Call<CheckResponse>, t: Throwable) {
                    Toast.makeText(this@EditProfileActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        }
    }
}