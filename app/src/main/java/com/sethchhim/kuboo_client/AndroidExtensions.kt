package com.sethchhim.kuboo_client

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment

/**
 * Replacements for the handful of Anko helpers this project used. Anko was discontinued in
 * 2019 and was the only reason to keep that dependency, so the helpers live here instead —
 * same call sites, no third-party library.
 */

fun Context.toast(message: CharSequence): Toast =
        Toast.makeText(this, message, Toast.LENGTH_SHORT).apply { show() }

fun Context.longToast(message: CharSequence): Toast =
        Toast.makeText(this, message, Toast.LENGTH_LONG).apply { show() }

fun Fragment.toast(message: CharSequence): Toast? =
        context?.let { Toast.makeText(it, message, Toast.LENGTH_SHORT).apply { show() } }

fun Context.toast(@StringRes messageId: Int): Toast =
        Toast.makeText(this, messageId, Toast.LENGTH_SHORT).apply { show() }

fun Context.longToast(@StringRes messageId: Int): Toast =
        Toast.makeText(this, messageId, Toast.LENGTH_LONG).apply { show() }

fun Fragment.toast(@StringRes messageId: Int): Toast? =
        context?.let { Toast.makeText(it, messageId, Toast.LENGTH_SHORT).apply { show() } }

val Context.layoutInflater: LayoutInflater
    get() = LayoutInflater.from(this)

val Context.defaultSharedPreferences: SharedPreferences
    get() = PreferenceManager.getDefaultSharedPreferences(this)

var View.backgroundColor: Int
    get() = throw UnsupportedOperationException("backgroundColor is write-only")
    set(value) = setBackgroundColor(value)

var View.backgroundResource: Int
    get() = throw UnsupportedOperationException("backgroundResource is write-only")
    set(value) = setBackgroundResource(value)
