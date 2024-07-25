package com.example.bookingparkir

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.bookingparkir.API.ApiService
import com.example.bookingparkir.API.CheckResponse
import com.example.bookingparkir.API.RetrofitClient
import com.example.bookingparkir.API.SignUpRequest
import com.example.bookingparkir.API.UpdateUserRequest
import com.example.bookingparkir.API.UserResponse
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        val editTextName: EditText = findViewById(R.id.signname)
        val editTextEmail: EditText = findViewById(R.id.signmail)
        val editTextTelp: EditText = findViewById(R.id.signnotelp)
        val editTextPassword: EditText = findViewById(R.id.signpass)
        val editTextPassword2: EditText = findViewById(R.id.signpass2)
        val editTextPlate: EditText = findViewById(R.id.signplate)
        val buttonSignUp: Button = findViewById(R.id.signbutton)
        val textViewLogin: TextView = findViewById(R.id.textlogin2)

        val apiService = RetrofitClient.instance.create(ApiService::class.java)

        buttonSignUp.setOnClickListener{
            val name = editTextName.text.toString()
            val email = editTextEmail.text.toString()
            val telp = editTextTelp.text.toString()
            val password = editTextPassword.text.toString()
            val password2 = editTextPassword2.text.toString()
            val platenomor = editTextPlate.text.toString()

            apiService.signUp(SignUpRequest(name, email, telp, password, password2, platenomor)).enqueue(object : Callback<CheckResponse> {
                override fun onResponse(call: Call<CheckResponse>, response: Response<CheckResponse>) {
                    if (response.isSuccessful) {
                        val messageResponse = response.body()
                        val message = messageResponse?.message
                        Toast.makeText(this@SignupActivity, "$message", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        val errorBody = response.errorBody()?.string()
                        errorBody?.let {
                            val errorResponse = Gson().fromJson(it, CheckResponse::class.java)
                            val errorMessage = errorResponse.message
                            Toast.makeText(this@SignupActivity, "$errorMessage", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(call: Call<CheckResponse>, t: Throwable) {
                    Toast.makeText(this@SignupActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })

        }

        textViewLogin.setOnClickListener {
            finish()
        }
    }
}