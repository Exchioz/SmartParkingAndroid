package com.example.bookingparkir

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import com.example.bookingparkir.API.ApiService
import com.example.bookingparkir.API.ChangePasswordRequest
import com.example.bookingparkir.API.CheckResponse
import com.example.bookingparkir.API.RetrofitClient
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePasswordActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        val sharedPreferences = getSharedPreferences("MySharedPrefs", Context.MODE_PRIVATE)
        val idUser = sharedPreferences?.getString("userId", null)

        val editOldPassword: EditText = findViewById(R.id.editOldPassword)
        val editNewPassword: EditText = findViewById(R.id.editNewPassword)
        val ediConfirmPassword: EditText = findViewById(R.id.editConfirmPassword)

        val btnUpdate: Button = findViewById(R.id.editButton)
        val btnBack: ImageButton = findViewById(R.id.btnBacktoHome)

        val apiService = RetrofitClient.instance.create(ApiService::class.java)

        btnUpdate.setOnClickListener {
            val oldPassword = editOldPassword.text.toString().trim()
            val newPassword = editNewPassword.text.toString().trim()
            val confirmPassword = ediConfirmPassword.text.toString().trim()

            apiService.changePassword(ChangePasswordRequest(idUser, oldPassword, newPassword, confirmPassword)).enqueue(object : Callback<CheckResponse> {
                override fun onResponse(call: Call<CheckResponse>, response: Response<CheckResponse>) {
                    if (response.isSuccessful) {
                        val messageResponse = response.body()
                        val message = messageResponse?.message
                        Toast.makeText(this@ChangePasswordActivity, message, Toast.LENGTH_SHORT).show()
                        finish()
                    } else{
                        val errorBody = response.errorBody()?.string()
                        errorBody?.let {
                            val errorResponse = Gson().fromJson(it, CheckResponse::class.java)
                            val errorMessage = errorResponse.message
                            Toast.makeText(this@ChangePasswordActivity, errorMessage, Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(call: Call<CheckResponse>, t: Throwable) {
                    Toast.makeText(this@ChangePasswordActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }

        btnBack.setOnClickListener {
            finish()
        }

    }
}