package com.xt.kimi_demo

import android.app.Application
import com.xt.endo.EDOExporter

/**
 * Created by cuiminghui on 2018/7/20.
 */
class MainApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        EDOExporter.sharedExporter.initializer(this)
    }

}