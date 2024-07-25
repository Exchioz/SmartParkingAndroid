package com.example.bookingparkir

import android.content.Context
import com.example.bookingparkir.API.ApiService
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.bookingparkir.API.CancelReservasiResponse
import com.example.bookingparkir.API.CheckResponse
import com.example.bookingparkir.API.ExpiredTimeResponse
import com.example.bookingparkir.API.PaymentRequest
import com.example.bookingparkir.API.PaymentResponse
import com.example.bookingparkir.API.ReservasiResponse
import com.example.bookingparkir.API.RetrofitClient
import com.example.bookingparkir.API.UserRequest
import com.example.bookingparkir.API.UserResponse
import com.example.bookingparkir.ListViewPark.ReserveActivity
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // SharedPreferences dari Context untuk mengambil email pengguna
        val sharedPreferences = activity?.getSharedPreferences("MySharedPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences?.getString("userId", null)

        // Inisialisasi komponen UI
        val textViewNama: TextView = view.findViewById(R.id.textViewNama)
        val textViewPlateNomor: TextView = view.findViewById(R.id.textViewPlat)
        val textViewSaldo: TextView = view.findViewById(R.id.textViewSaldo)
        val btnReserve: Button = view.findViewById(R.id.btnReserve)
        val textnote: TextView = view.findViewById(R.id.textnote)
        val textnote2: TextView = view.findViewById(R.id.textnote2)

        // Inisialisasi layanan Retrofit
        val apiService = RetrofitClient.instance.create(ApiService::class.java)

        apiService.getStatusReservasi(userId).enqueue(object : Callback<ReservasiResponse> {
            override fun onResponse(call: Call<ReservasiResponse>, response: Response<ReservasiResponse>) {
                if (response.isSuccessful) {
                    val reservasiResponse = response.body()

                    // Panggil API untuk mendapatkan waktu kadaluwarsa
                    apiService.getExpiredTime(userId).enqueue(object : Callback<ExpiredTimeResponse> {
                        override fun onResponse(call: Call<ExpiredTimeResponse>, response: Response<ExpiredTimeResponse>) {
                            if (response.isSuccessful) {
                                val expiredTimeResponse = response.body()
                                val waktuKadaluwarsa = expiredTimeResponse?.waktu_akhir

                                // Lakukan sesuatu dengan waktu kadaluwarsa yang diterima
                                // Misalnya, tampilkan waktu kadaluwarsa di textnote2
                                if (reservasiResponse?.status == "Pending") {
                                    btnReserve.tag = "cancel_book"
                                    btnReserve.text = "Cancel Reserve"
                                    textnote.text = "Be careful on the way!"
                                    textnote2.text = "automatically canceled in \n $waktuKadaluwarsa"
                                } else if (reservasiResponse?.status == "Active") {
                                    btnReserve.tag = "payment"
                                    textnote.text = "Have a good time!"
                                    btnReserve.text = "Pay Now"
                                } else if (reservasiResponse?.status == "Checkout") {
                                    btnReserve.tag = "cancel_checkout"
                                    textnote.text = "See you again!"
                                    textnote2.text = "automatically canceled in \n $waktuKadaluwarsa"
                                    btnReserve.text = "Waiting"
                                    btnReserve.isEnabled = false
                                    btnReserve.alpha = 0.5f
                                } else {
                                    btnReserve.tag = "reserve"
                                    btnReserve.isEnabled = true
                                    btnReserve.alpha = 1f
                                }
                            } else {
                                Log.e("API Error", "Error Code: ${response.code()} Error Message: ${response.message()}")
                            }
                        }

                        override fun onFailure(call: Call<ExpiredTimeResponse>, t: Throwable) {
                            Log.e("API Error", "Failed to get expired time: ${t.message}", t)
                        }
                    })
                } else {
                    btnReserve.tag = "reserve"
                    btnReserve.isEnabled = true
                    btnReserve.alpha = 1f
                    //Log.e("API Error", "Error Code: ${response.code()} Error Message: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ReservasiResponse>, t: Throwable) {
                // Tangani kesalahan jaringan atau kesalahan lainnya
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })


        // Panggilan API untuk mendapatkan data pengguna
        apiService.getUserById(UserRequest(id = userId.toString())).enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful) {
                    val userResponse = response.body()
                    textViewNama.text = userResponse?.nama
                    textViewPlateNomor.text = userResponse?.platenomor
                    textViewSaldo.text = userResponse?.saldo.toString()

                } else {
                    Log.e("API Error", "Error Code: ${response.code()} Error Message: ${response.message()}")
                    Toast.makeText(requireContext(), "Gagal mendapatkan data pengguna", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })

        // Mengatur button Top Up
        view.findViewById<Button>(R.id.btnTopUp).setOnClickListener {
            val intent = Intent(activity, TopupActivity::class.java)
            startActivity(intent)
        }

        // Mengatur button Reserve
        btnReserve.setOnClickListener {
            if (btnReserve.tag == "reserve") {
                val intent = Intent(activity, ReserveActivity::class.java)
                startActivity(intent)
            } else if (btnReserve.tag == "cancel_book"){
                apiService.cancelReservasi(userId).enqueue(object : Callback<CancelReservasiResponse> {
                    override fun onResponse(call: Call<CancelReservasiResponse>, response: Response<CancelReservasiResponse>) {
                        if (response.isSuccessful) {
                            val cancelReservasiResponse = response.body()
                            val message = cancelReservasiResponse?.message

                            val fragmentManager = requireActivity().supportFragmentManager
                            val transaction = fragmentManager.beginTransaction()
                            transaction.replace(R.id.fragment_container, HomeFragment())
                            transaction.commit()
                            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                        } else {
                            val errorBody = response.errorBody()?.string()
                            errorBody?.let {
                                val errorResponse = Gson().fromJson(it, CheckResponse::class.java)
                                val errorMessage = errorResponse.message
                                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                    override fun onFailure(call: Call<CancelReservasiResponse>, t: Throwable) {
                        Log.e("CancelReservasi", "Failed to cancel reservation. Error: ${t.message}", t)
                    }
                })
            } else if (btnReserve.tag == "payment") {
                apiService.payment(PaymentRequest(userId)).enqueue(object : Callback<CheckResponse> {
                    override fun onResponse(call: Call<CheckResponse>, response: Response<CheckResponse>) {
                        if (response.isSuccessful) {
                            val paymentResponse = response.body()
                            val message = paymentResponse?.message

                            val fragmentManager = requireActivity().supportFragmentManager
                            val transaction = fragmentManager.beginTransaction()
                            transaction.replace(R.id.fragment_container, HomeFragment())
                            transaction.commit()
                            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                        } else {
                            val errorBody = response.errorBody()?.string()
                            errorBody?.let {
                                val errorResponse = Gson().fromJson(it, CheckResponse::class.java)
                                val errorMessage = errorResponse.message
                                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                    override fun onFailure(call: Call<CheckResponse>, t: Throwable) {
                        // Tangani kesalahan jaringan atau kesalahan lainnya
                    }
                })
            } else if (btnReserve.tag == "cancel_checkout"){
                //actioncancelcheckout
            }
        }
    }




    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}