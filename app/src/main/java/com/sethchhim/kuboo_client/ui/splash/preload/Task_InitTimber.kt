package com.sethchhim.kuboo_client.ui.splash.preload

import android.util.Log
import com.sethchhim.kuboo_client.BuildConfig
import com.sethchhim.kuboo_client.ui.splash.SplashActivity
import timber.log.Timber

class Task_InitTimber(splashActivity: SplashActivity) : Task_PreloadBase(splashActivity) {

    override fun doPreload() {
        Timber.plant(when (BuildConfig.DEBUG) {
            true -> Timber.DebugTree()
            false -> ReleaseTree()
        })
        onFinished(javaClass.simpleName)
    }

    /**
     * A release build used to plant nothing, so a problem reported from a phone left no trace
     * of the app in logcat and there was nothing to diagnose from. Warnings and errors are
     * kept; the debug chatter, which prints server addresses and titles, stays out.
     */
    private class ReleaseTree : Timber.DebugTree() {
        override fun isLoggable(tag: String?, priority: Int) = priority >= Log.WARN
    }

}
