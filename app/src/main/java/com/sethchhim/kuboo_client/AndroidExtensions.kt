package com.sethchhim.kuboo_client

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

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

/**
 * Replacements for Butterknife's @BindView, which was discontinued in 2020 and forced the
 * build to keep resource ids non-final.
 *
 * An activity keeps one content view for its whole life, so looking a view up once is enough;
 * lazy rather than eager so it happens on first use, by which point setContentView has run.
 */
fun <T : View> Activity.bindView(id: Int): Lazy<T> = lazy { findViewById<T>(id) }

/**
 * A fragment outlives its view: leave one and come back and onCreateView runs again on the same
 * instance. Caching the lookup for the life of the fragment therefore handed back the previous,
 * detached view, so work like attaching a RecyclerView adapter landed on a view nobody could
 * see — the list stayed empty until the app was killed and the fragment rebuilt. Butterknife
 * had no such problem because it re-bound every time it was handed a view.
 *
 * The lookup is cached against the view it came from and redone when the fragment's view
 * changes.
 */
fun <T : View> Fragment.bindView(id: Int): ReadOnlyProperty<Fragment, T> = FragmentViewBinding(id)

private class FragmentViewBinding<T : View>(private val id: Int) : ReadOnlyProperty<Fragment, T> {

    private var boundTo: View? = null
    private var view: T? = null

    override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        val root = thisRef.requireView()
        if (root !== boundTo) {
            boundTo = root
            view = root.findViewById(id)
        }
        return view ?: throw IllegalStateException("No view for ${property.name}")
    }

}

val Context.defaultSharedPreferences: SharedPreferences
    get() = PreferenceManager.getDefaultSharedPreferences(this)

var View.backgroundColor: Int
    get() = throw UnsupportedOperationException("backgroundColor is write-only")
    set(value) = setBackgroundColor(value)

var View.backgroundResource: Int
    get() = throw UnsupportedOperationException("backgroundResource is write-only")
    set(value) = setBackgroundResource(value)
