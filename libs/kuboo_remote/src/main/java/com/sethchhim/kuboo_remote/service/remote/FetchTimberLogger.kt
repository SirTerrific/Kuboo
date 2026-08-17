package com.sethchhim.kuboo_remote.service.remote

import com.tonyodev.fetch2core.Logger
import timber.log.Timber

/**
 * Fetch keeps its own log and says nothing by default, so a download that never leaves the
 * queue gives no reason for it. Its output is forwarded here, where a release build keeps
 * warnings and errors, so a report from a device can say what the download library decided.
 *
 * Its debug output is reported as a warning for the same reason: it is the only place that
 * explains a queue that does not advance, and debug is dropped in a release build.
 */
internal class FetchTimberLogger : Logger {

    override var enabled = true

    override fun d(message: String) {
        if (enabled) Timber.w("fetch: $message")
    }

    override fun d(message: String, throwable: Throwable) {
        if (enabled) Timber.w(throwable, "fetch: $message")
    }

    override fun e(message: String) {
        if (enabled) Timber.e("fetch: $message")
    }

    override fun e(message: String, throwable: Throwable) {
        if (enabled) Timber.e(throwable, "fetch: $message")
    }

}
