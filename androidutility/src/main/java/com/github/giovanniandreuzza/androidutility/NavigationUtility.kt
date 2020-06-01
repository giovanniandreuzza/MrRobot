package com.github.giovanniandreuzza.androidutility

import android.util.Log
import android.view.View
import androidx.navigation.NavDirections
import androidx.navigation.Navigation

fun View.navigateTo(navDirection: NavDirections) {
    try {
        Navigation.findNavController(this).navigate(navDirection)
    } catch (e: IllegalArgumentException) {
        Log.e("NAV", e.message ?: "error")
    }
}