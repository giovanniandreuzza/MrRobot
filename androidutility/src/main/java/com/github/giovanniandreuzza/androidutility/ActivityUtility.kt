package com.github.giovanniandreuzza.androidutility

import android.app.Activity
import android.app.Dialog
import android.content.res.Resources
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlin.math.roundToInt

fun Activity.hideStatusBar() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
    }
}

fun Fragment.getColor(colorRes: Int): Int = ContextCompat.getColor(requireContext(), colorRes)

fun View.getNavBarSize(): Int {
    val resourceId: Int = resources.getIdentifier("navigation_bar_height", "dimen", "android")
    return if (resourceId > 0) {
        resources.getDimensionPixelSize(resourceId)
    } else 0
}

fun View.getStatusBarHeight(): Int {
    val resourceId: Int = resources.getIdentifier("status_bar_height", "dimen", "android")
    return if (resourceId > 0) {
        resources.getDimensionPixelSize(resourceId)
    } else 0
}

fun View.setMargins(
    marginLeft: Int = this.marginLeft,
    marginTop: Int = this.marginTop,
    marginRight: Int = this.marginRight,
    marginBottom: Int = this.marginBottom
) {
    if (layoutParams is ViewGroup.MarginLayoutParams) {
        val p = layoutParams as ViewGroup.MarginLayoutParams
        p.setMargins(marginLeft, marginTop, marginRight, marginBottom)
        requestLayout()
    }
}

fun Activity.changeStatusAndNavigationBarIconColor(dark: Boolean) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val decor = window.decorView
        if (dark) {
            decor.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        } else {
            decor.systemUiVisibility = 0
        }
    }
}

fun Dialog?.changeStatusAndNavigationBarIconColor(dark: Boolean) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val decor = this?.window?.decorView
        if (dark) {
            decor?.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        } else {
            decor?.systemUiVisibility = 0
        }
    }
}

/***
 * get result of a savedStateHandle operation (get destination result)
 * @param key the key for retrieve the livedata value
 */
fun <T> Fragment.getNavigationResult(key: String = "result") =
    findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<T?>(key)

/***
 * set result of a savedStateHandle operation (set destination result)
 * @param key the key to set the livedata value
 */
fun <T> Fragment.setNavigationResult(key: String = "result", value: T?) {
    findNavController().previousBackStackEntry?.savedStateHandle?.set(key, value)
}

val Int.dpToPx: Int
    get() = (this * Resources.getSystem().displayMetrics.density).roundToInt()

val Int.pxToDp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).roundToInt()