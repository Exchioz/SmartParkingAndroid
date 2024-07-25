package com.example.bookingparkir

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.bookingparkir.API.ApiService
import com.example.bookingparkir.API.RetrofitClient
import com.example.bookingparkir.API.UserRequest
import com.example.bookingparkir.API.UserResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
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
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences = activity?.getSharedPreferences("MySharedPrefs", Context.MODE_PRIVATE)
        val emailUser = sharedPreferences?.getString("userId", null)
        val apiService = RetrofitClient.instance.create(ApiService::class.java)

        val editProfileLayout: LinearLayout = view.findViewById(R.id.editProfileLayout)
        val changePasswordLayout: LinearLayout = view.findViewById(R.id.changePasswordLayout)
        val logoutLayout: LinearLayout = view.findViewById(R.id.logoutLayout)

        val textViewNama: TextView = view.findViewById(R.id.textViewNama)
        val textViewEmail: TextView = view.findViewById(R.id.textViewEmail)

        // Set click listener for Edit Profile
        editProfileLayout.setOnClickListener {
            val intent = Intent(activity, EditProfileActivity::class.java)
            startActivity(intent)
        }

        // Set click listener for Change Password
        changePasswordLayout.setOnClickListener {
            val intent = Intent(activity, ChangePasswordActivity::class.java)
            startActivity(intent)
        }

        // Set click listener for Logout
        logoutLayout.setOnClickListener {
            val sharedPreferences = requireActivity().getSharedPreferences("MySharedPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("userId", null)
            editor.apply()

            Toast.makeText(requireContext(), "Berhasil logout", Toast.LENGTH_SHORT).show()
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        apiService.getUserById(UserRequest(id = emailUser.toString())).enqueue(object :
            Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful) {
                    val userResponse = response.body()
                    textViewNama.text = userResponse?.nama
                    textViewEmail.text = userResponse?.email

                } else {
                    Log.e("API Error", "Error Code: ${response.code()} Error Message: ${response.message()}")
                    Toast.makeText(requireContext(), "Gagal mendapatkan data pengguna", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}