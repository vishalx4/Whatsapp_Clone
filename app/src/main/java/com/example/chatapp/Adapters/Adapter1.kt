package com.example.chatapp.Adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.chatapp.Fragments.Chats
import com.example.chatapp.Fragments.search
import com.example.chatapp.Fragments.settings

class Adapter1(fragmentManager: FragmentManager):FragmentPagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
        when (position){
            0 -> return Chats()
            1 -> return search()
            2 -> return settings()
        }

        return null!!
    }

    override fun getCount(): Int {
        return 3
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when(position) {
            0 -> return "CHATS"
            1 -> return "SEARCH"
            2 -> return "SETTINGS"
        }
        return null
    }
}