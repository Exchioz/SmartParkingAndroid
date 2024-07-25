package com.example.bookingparkir

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.bookingparkir.API.ApiService
import com.example.bookingparkir.API.RetrofitClient
import com.example.bookingparkir.API.TopupRequest
import com.example.bookingparkir.API.TopupResponse
import com.example.bookingparkir.API.UserRequest
import com.example.bookingparkir.API.UserResponse
import com.example.bookingparkir.databinding.ActivityTopupBinding
import com.midtrans.sdk.corekit.callback.TransactionFinishedCallback
import com.midtrans.sdk.corekit.core.MidtransSDK
import com.midtrans.sdk.corekit.core.TransactionRequest
import com.midtrans.sdk.corekit.core.themes.CustomColorTheme
import com.midtrans.sdk.corekit.models.CustomerDetails
import com.midtrans.sdk.corekit.models.ItemDetails
import com.midtrans.sdk.corekit.models.snap.TransactionResult
import com.midtrans.sdk.uikit.SdkUIFlowBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TopupActivity : AppCompatActivity(), TransactionFinishedCallback {
    private lateinit var binding: ActivityTopupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTopupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initMidtransSDK()

        binding.btnTopUp.setOnClickListener { goToPayment() }
        binding.btn10k.setOnClickListener {
            binding.editTextHarga.setText("10000")
        }
        binding.btn25k.setOnClickListener {
            binding.editTextHarga.setText("25000")
        }
        binding.btn50k.setOnClickListener {
            binding.editTextHarga.setText("50000")
        }
        binding.btn100k.setOnClickListener {
            binding.editTextHarga.setText("100000")
        }
        binding.btn300k.setOnClickListener {
            binding.editTextHarga.setText("300000")
        }
        binding.btn500k.setOnClickListener {
            binding.editTextHarga.setText("500000")
        }
        binding.btnBacktoHome.setOnClickListener{
            finish()
        }
    }

    private fun initMidtransSDK() {
        val sdkUIFlowBuilder: SdkUIFlowBuilder = SdkUIFlowBuilder.init()
            .setClientKey("SB-Mid-client-AmrpDBOya3Hx-Uns") // client_key
            .setContext(this)
            .setTransactionFinishedCallback(this)
            .setMerchantBaseUrl("https://midtrans-carpark.000webhostapp.com/midtrans.php/") //URL Server
            .enableLog(true)
            .setLanguage("id")
        sdkUIFlowBuilder.buildSDK()
    }

    private fun goToPayment() {
        val hargaText = binding.editTextHarga.text.toString()
        if (hargaText.isNotEmpty()) {
            val harga = hargaText.toDouble()

            if (harga >= 10000) {

                val sharedPreferences = getSharedPreferences("MySharedPrefs", Context.MODE_PRIVATE)
                val userId = sharedPreferences?.getString("userId", null)
                val apiService = RetrofitClient.instance.create(ApiService::class.java)

                apiService.getUserById(UserRequest(id = userId.toString())).enqueue(object :
                    Callback<UserResponse> {
                    override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                        if (response.isSuccessful) {
                            val userResponse = response.body()
                            userResponse?.let {
                                val transactionRequest = TransactionRequest("CarPark" + System.currentTimeMillis().toShort(), harga)
                                val detail = ItemDetails("topup-${userId}", harga, 1, "Top Up Saldo")
                                val itemDetails = ArrayList<ItemDetails>()
                                itemDetails.add(detail)

                                val customerDetails = CustomerDetails()
                                customerDetails.firstName = userResponse.nama
                                customerDetails.lastName = ""
                                customerDetails.email = userResponse.email
                                customerDetails.phone = userResponse.telp //userResponse.phone

                                transactionRequest.customerDetails = customerDetails
                                transactionRequest.itemDetails = itemDetails

                                MidtransSDK.getInstance().transactionRequest = transactionRequest
                                MidtransSDK.getInstance().startPaymentUiFlow(this@TopupActivity)
                            } ?: run {
                                Toast.makeText(this@TopupActivity, "Gagal mendapatkan data pengguna", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Log.e("API Error", "Error Code: ${response.code()} Error Message: ${response.message()}")
                            Toast.makeText(this@TopupActivity, "Gagal mendapatkan data pengguna", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                        Toast.makeText(this@TopupActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                Toast.makeText(this, "Minimun Top Up Rp 10.000", Toast.LENGTH_SHORT).show()
            }
        }  else {
            Toast.makeText(this, "Enter the balance first", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onTransactionFinished(result: TransactionResult) {
        val transactionId = result.response.transactionId

        // Mengambil userId dari SharedPreferences
        val sharedPreferences = getSharedPreferences("MySharedPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences?.getString("userId", null)

        val status = result.status
        if (result.response != null) {
            when (result.status) {
                TransactionResult.STATUS_SUCCESS -> {
                    // Lakukan pembaruan ke database karena transaksi sukses
                    updateDatabase(transactionId, userId, status)
                }
                TransactionResult.STATUS_PENDING -> {
                    // Transaksi masih tertunda, tidak ada pembaruan database yang dilakukan
                    updateDatabase(transactionId, userId, status)
                }
                TransactionResult.STATUS_FAILED -> {
                    // Lakukan pembaruan ke database karena transaksi gagal
                    updateDatabase(transactionId, userId, status)
                }
            }
        } else if (result.isTransactionCanceled) {
            Toast.makeText(this, "Transaction Canceled", Toast.LENGTH_LONG).show()
        } else {
            if (result.status.equals(TransactionResult.STATUS_INVALID, true)) {
                Toast.makeText(this, "Transaction Invalid", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Transaction Finished with failure.", Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun updateDatabase(transactionId: String, userId: String?, status: String){

        val jumlah = binding.editTextHarga.text.toString().toInt()
        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        val topupRequest = TopupRequest(transactionId, userId, jumlah, status)
        Log.e("TopupActivity", "toop up: ${topupRequest}")

        apiService.topUp(topupRequest).enqueue(object : Callback<TopupResponse> {
            override fun onResponse(call: Call<TopupResponse>, response: Response<TopupResponse>) {
                if (response.isSuccessful) {
                    val topupResponse = response.body()
                    if (topupResponse != null) {
                        // Top up successful, perform appropriate action, such as showing a message to the user
                        val intentMainActivity = Intent(this@TopupActivity, MainActivity::class.java)
                        intentMainActivity.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        intentMainActivity.putExtra("fragmentToLoad", "home")
                        startActivity(intentMainActivity)
                    } else {
                        // Top up failed, show error message to the user
                        Log.e("TopupActivity", "Failed to top up: ${topupResponse?.message}")
                        Toast.makeText(this@TopupActivity, "Failed to top up1", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Request failed, show error message to the user
                    Log.e("TopupActivity", "Failed to top up: ${response.message()}")
                    Toast.makeText(this@TopupActivity, "Failed to top up2", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<TopupResponse>, t: Throwable) {
                // Handle network errors, show error message to the user
                Toast.makeText(this@TopupActivity, "Network error", Toast.LENGTH_SHORT).show()
            }
        })
    }
}