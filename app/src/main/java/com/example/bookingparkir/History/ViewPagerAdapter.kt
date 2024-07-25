package com.example.bookingparkir.History

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class ViewPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    // List untuk menyimpan fragment yang akan ditampilkan di dalam ViewPager
    private val fragments: MutableList<Fragment> = ArrayList()
    // List untuk menyimpan judul tab
    private val fragmentTitles: MutableList<String> = ArrayList()

    // Fungsi untuk menambahkan fragment dan judul tab
    fun addFragment(fragment: Fragment, title: String) {
        fragments.add(fragment)
        fragmentTitles.add(title)
    }

    // Mendapatkan fragment berdasarkan posisi
    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    // Mendapatkan jumlah tab
    override fun getCount(): Int {
        return fragments.size
    }

    // Mendapatkan judul tab berdasarkan posisi
    override fun getPageTitle(position: Int): CharSequence? {
        return fragmentTitles[position]
    }
}
