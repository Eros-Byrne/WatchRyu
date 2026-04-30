package com.example.mob_dev_portfolio.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.mob_dev_portfolio.model.AnimeStatus

/**
 * Adapter for the ViewPager2 in MainActivity.
 * Handles the logic for showing different fragments based on the selected tab.
 */
class MainPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 7

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> AiringFragment()
            1 -> StatsFragment()
            2 -> AnimeListFragment.newInstance(AnimeStatus.WATCHING)
            3 -> AnimeListFragment.newInstance(AnimeStatus.COMPLETED)
            4 -> AnimeListFragment.newInstance(AnimeStatus.ON_HOLD)
            5 -> AnimeListFragment.newInstance(AnimeStatus.DROPPED)
            6 -> AnimeListFragment.newInstance(AnimeStatus.PLAN_TO_WATCH)
            else -> AiringFragment()
        }
    }
}
