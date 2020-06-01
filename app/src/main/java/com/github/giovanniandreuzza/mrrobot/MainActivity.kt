package com.github.giovanniandreuzza.mrrobot

import android.bluetooth.BluetoothAdapter
import com.github.giovanniandreuzza.androidutility.hideStatusBar
import com.github.giovanniandreuzza.base.BaseActivity
import com.github.giovanniandreuzza.legoandroid.comm.Channel
import com.github.giovanniandreuzza.legoandroid.ev3.EV3
import com.github.giovanniandreuzza.rxbluetoothserial.RxBluetoothDevice
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class MainActivity : BaseActivity() {

    var ev3: EV3? = null

    override fun getLayoutRes() = R.layout.activity_main

    override fun getContainerId() = R.id.activity_container

    override fun setupViews() {
        hideStatusBar()

        BluetoothAdapter.getDefaultAdapter().bondedDevices.forEach {
            if (it.name.contains("EV3")) {
                RxBluetoothDevice(it).connect(
                    onConnected = { rxBluetoothConnection ->
                        Timber.d("Connected")

                        Single.create<Unit> {
                            val ev3 = EV3(object : Channel {
                                override fun send(data: ByteArray) {
                                    rxBluetoothConnection.send(data)
                                }

                                override suspend fun receive(): Byte {
                                    return rxBluetoothConnection.listen()
                                }

                                override fun close() {
                                    rxBluetoothConnection.close()
                                }
                            })

                            this.ev3 = ev3
                        }.subscribeOn(Schedulers.io())
                            .observeOn(Schedulers.io())
                            .subscribe()
                    },
                    onFailure = { error ->
                        Timber.d("Connection error")
                        Timber.d("Error -> ${error.message}")
                    }
                )
            }
        }

//        bt_start.setOnClickListener {
//            mission?.startMission()
//        }
//
//        bt_stop.setOnClickListener {
//            mission?.stopMission()
//        }
    }

}
