package com.github.giovanniandreuzza.base

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment

abstract class BaseActivity : AppCompatActivity() {

    abstract fun setupViews()

    abstract fun getLayoutRes(): Int

    abstract fun getContainerId(): Int

    override fun onStart() {
        super.onStart()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            window.findViewById<View>(getContainerId()).fitsSystemWindows = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(getLayoutRes())
        setupViews()
    }

    override fun onBackPressed() {
        var fragmentIntercept = false

        supportFragmentManager.fragments.forEach { navHost ->
            if (navHost is NavHostFragment) {
                navHost.childFragmentManager.fragments.forEach {
                    if (it.isVisible && it is OnBackPressedCallback) {
                        it.onBackPressed()
                        fragmentIntercept = true
                    }
                }
            }
        }

        if (!fragmentIntercept) {
            super.onBackPressed()
        }
    }

}