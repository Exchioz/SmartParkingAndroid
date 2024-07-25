package com.example.bookingparkir.History

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.example.bookingparkir.API.ApiService
import com.example.bookingparkir.API.OngoingReservationsResponse
import com.example.bookingparkir.API.RetrofitClient
import com.example.bookingparkir.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [UserOngoingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UserOngoingFragment : Fragment() {
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

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_user_ongoing, container, false)

        val sharedPreferences = activity?.getSharedPreferences("MySharedPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences?.getString("userId", null)
        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        val listviewongoing: ListView = view.findViewById(R.id.listViewOngoing)
        val textViewEmptyList: TextView = view.findViewById(R.id.textViewEmptyList)

        apiService.getOngoingReservations(userId).enqueue(object : Callback<OngoingReservationsResponse> {
            override fun onResponse(call: Call<OngoingReservationsResponse>, response: Response<OngoingReservationsResponse>) {
                if (response.isSuccessful) {
                    val ongoingReservationsResponse = response.body()
                    val ongoingReservations = ongoingReservationsResponse?.ongoing_reservations

                    textViewEmptyList.visibility = View.GONE
                    listviewongoing.visibility = View.VISIBLE

                    val adapter = OngoingReservationsAdapter(
                        requireContext(),
                        ongoingReservations ?: emptyList()
                    )
                    listviewongoing.adapter = adapter
                } else {
                    textViewEmptyList.visibility = View.VISIBLE
                    listviewongoing.visibility = View.GONE
                }
            }

            override fun onFailure(call: Call<OngoingReservationsResponse>, t: Throwable) {
                // Tangani kesalahan jika gagal melakukan panggilan ke API
                Toast.makeText(
                    requireContext(),
                    "Gagal melakukan panggilan ke server",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment UserOngoingFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UserOngoingFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}