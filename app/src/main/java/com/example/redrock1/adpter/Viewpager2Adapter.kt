package com.example.redrock1.adpter


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.redrock1.BackInterface



class Viewpager2Adapter(val fragments:ArrayList<BackInterface>,activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun createFragment(position: Int): Fragment {
        return fragments[position].back()
    }

    override fun getItemCount(): Int {
        return fragments.size
    }
}
