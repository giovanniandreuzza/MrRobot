package com.github.giovanniandreuzza.mrrobot.presentation.task1

import androidx.core.view.isVisible
import com.github.giovanniandreuzza.base.BaseFragment
import com.github.giovanniandreuzza.mrrobot.*
import kotlinx.android.synthetic.main.fragment_task.*
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import timber.log.Timber

class Task1Fragment : BaseFragment() {

    private var mission: Mission? = null

    override fun getLayoutRes() = R.layout.fragment_task

    override fun darkIcons() = true

    override fun onInitView() {
        bt_exit.setOnClickListener {
            mission?.stopMission()
            requireActivity().onBackPressed()
        }

        bt_start.setOnClickListener {
            mission?.let {
                runBlocking {
                    async {
                        it.startMission()
                    }
                }.invokeOnCompletion { _ ->
                    Timber.d("FINISH")
                    tv_matrix.text = it.field.getFinalMatrix()
                }
            }
        }

        init()
    }

    private fun init() {
        val ev3 = (requireActivity() as MainActivity).ev3

        if (ev3 != null) {
            mission = Mission(
                MrRobot(ev3 = ev3),
                Field(4, 4, 29.0).apply {
                    startingPoint(Point(4, 1))
                },
                Step.STEP_1
            )
        } else {
            tv_matrix.text = "Non è stato possibile connettersi all'EV3"
        }
    }

}