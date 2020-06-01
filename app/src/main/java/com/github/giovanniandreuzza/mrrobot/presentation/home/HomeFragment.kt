package com.github.giovanniandreuzza.mrrobot.presentation.home

import com.github.giovanniandreuzza.androidutility.navigateTo
import com.github.giovanniandreuzza.base.BaseFragment
import com.github.giovanniandreuzza.mrrobot.R
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment: BaseFragment() {

    override fun getLayoutRes() = R.layout.fragment_home

    override fun darkIcons() = true

    override fun onInitView() {
        iv_play_1.setOnClickListener {
            requireView().navigateTo(HomeFragmentDirections.actionHomeFragmentToTask1Fragment())
        }

        iv_play_2.setOnClickListener {
            requireView().navigateTo(HomeFragmentDirections.actionHomeFragmentToTask2Fragment())
        }
    }

}