package com.sethchhim.kuboo_remote.service.remote

import android.content.BroadcastReceiver
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Handler

/**
 * Fetch 2.3.6 predates Android 14, which rejects registerReceiver unless the caller
 * says whether the receiver is exported. Fetch registers through the context it is
 * handed, so the flag is supplied here instead of dropping the app's target sdk.
 *
 * Fetch keeps context.applicationContext rather than the context itself, so
 * getApplicationContext has to return this wrapper for the override to be reached.
 *
 * The receivers only listen for Fetch's own in-process broadcasts, so they are not
 * exported.
 */
internal class ReceiverFlagContext(base: Context) : ContextWrapper(base.applicationContext) {

    override fun getApplicationContext(): Context = this

    override fun registerReceiver(receiver: BroadcastReceiver?, filter: IntentFilter): Intent? =
            registerReceiver(receiver, filter, null, null)

    override fun registerReceiver(receiver: BroadcastReceiver?, filter: IntentFilter, flags: Int): Intent? =
            super.registerReceiver(receiver, filter, flags or notExportedFlag())

    override fun registerReceiver(receiver: BroadcastReceiver?, filter: IntentFilter, broadcastPermission: String?, scheduler: Handler?): Intent? =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                super.registerReceiver(receiver, filter, broadcastPermission, scheduler, notExportedFlag())
            } else {
                super.registerReceiver(receiver, filter, broadcastPermission, scheduler)
            }

    override fun registerReceiver(receiver: BroadcastReceiver?, filter: IntentFilter, broadcastPermission: String?, scheduler: Handler?, flags: Int): Intent? =
            super.registerReceiver(receiver, filter, broadcastPermission, scheduler, flags or notExportedFlag())

    private fun notExportedFlag() =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Context.RECEIVER_NOT_EXPORTED else 0

}
