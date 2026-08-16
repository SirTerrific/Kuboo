package com.sethchhim.kuboo_remote.util

import timber.log.Timber
import java.net.URL

/**
 * Resolves a link against the configured server address using standard URI resolution.
 *
 * Two shapes have to work:
 *  - absolute paths taken from OPDS feed hrefs ("/opds/comics/download/4/x.cbz").
 *    Ubooquity 3 emits these from the server root, so they must NOT be appended to
 *    the "/opds-comics/" suffix the user configured, or the server silently serves
 *    its root navigation feed instead of the requested resource.
 *  - relative paths built by the app ("all?groupByFolder=true", "?latest=true"),
 *    which are relative to the configured address.
 *
 * java.net.URL handles both exactly as RFC 3986 specifies.
 */
fun String.resolveLink(link: String): String = try {
    URL(URL(this), link).toString()
} catch (e: Exception) {
    Timber.e("Failed to resolve link[$link] against server[$this]: ${e.message}")
    this + link
}
