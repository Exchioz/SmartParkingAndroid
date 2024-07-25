package com.example.bookingparkir

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPreferences = getSharedPreferences("MySharedPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", null)
        val navView: BottomNavigationView = findViewById(R.id.bottomnav)

        if (userId == null) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        navView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // Navigasi ke HomeFragment
                    val homeFragment = HomeFragment()
                    openFragment(homeFragment)
                    true
                }

                R.id.nav_history -> {
                    // Navigasi ke HistoryFragment
                    val historyFragment = HistoryFragment()
                    openFragment(historyFragment)
                    true
                }

                R.id.nav_account -> {
                    // Navigasi ke AccountFragment
                    val accountFragment = ProfileFragment()
                    openFragment(accountFragment)
                    true
                }

                else -> false
            }
        }
        navView.selectedItemId = R.id.nav_home
    }

    private fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
    }
}