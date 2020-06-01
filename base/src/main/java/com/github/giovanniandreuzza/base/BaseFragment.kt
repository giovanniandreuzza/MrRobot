package com.github.giovanniandreuzza.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.giovanniandreuzza.androidutility.changeStatusAndNavigationBarIconColor

abstract class BaseFragment : Fragment() {

    abstract fun getLayoutRes(): Int

    abstract fun darkIcons(): Boolean

    abstract fun onInitView()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(getLayoutRes(), container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().changeStatusAndNavigationBarIconColor(darkIcons())
        onInitView()
    }
}
