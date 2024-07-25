package com.example.bookingparkir

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bookingparkir.API.ApiService
import com.example.bookingparkir.API.CheckResponse
import com.example.bookingparkir.API.LoginRequest
import com.example.bookingparkir.API.LoginResponse
import com.example.bookingparkir.API.RetrofitClient
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Membuat instance dari RetrofitClient
        val apiService = RetrofitClient.instance.create(ApiService::class.java)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        // Inisialisasi komponen UI
        val editTextEmail: EditText = findViewById(R.id.logemail)
        val editTextPassword: EditText = findViewById(R.id.logpassword)
        val buttonLogin: Button = findViewById(R.id.logbutton)
        val textViewSignUp: TextView = findViewById(R.id.textsignup2)

        val sharedPreferences = getSharedPreferences("MySharedPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", null)

        if (userId != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        // Pengaturan listener untuk tombol login
        buttonLogin.setOnClickListener {
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()
            val loginRequest = LoginRequest(email, password)

            // Memanggil API login
            apiService.login(loginRequest).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.isSuccessful) {
                        val messageResponse = response.body()
                        val message = messageResponse?.message
                        val sharedPreferences = getSharedPreferences("MySharedPrefs", MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putString("userId", messageResponse?.id.toString())
                        editor.apply()

                        Toast.makeText(this@LoginActivity, message, Toast.LENGTH_SHORT).show()
                        val intentMainActivity = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intentMainActivity)
                        finish()
                    } else {
                        val errorBody = response.errorBody()?.string()
                        errorBody?.let {
                            val errorResponse = Gson().fromJson(it, CheckResponse::class.java)
                            val errorMessage = errorResponse.message
                            Toast.makeText(this@LoginActivity, errorMessage, Toast.LENGTH_SHORT).show()
                        }

                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Toast.makeText(this@LoginActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }

        textViewSignUp.setOnClickListener {
            val intentSignupActivity = Intent(this, SignupActivity::class.java)
            startActivity(intentSignupActivity)
        }
    }
}
