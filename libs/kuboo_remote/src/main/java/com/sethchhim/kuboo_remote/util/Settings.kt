package com.sethchhim.kuboo_remote.util

object Settings {

    internal const val isDebugOkHttp = false
    internal const val isDebugPaginationHandler = false
    internal const val isDebugItemCountHandler = false

    internal const val CONNECTION_TIMEOUT = 15000

    // Applies between reads, not to the whole transfer. 15 seconds was tight enough that a
    // large feed or a drive spinning up could abort a request that was going to succeed,
    // which showed as an empty browser or a spinner that never finished.
    internal const val READ_TIMEOUT = 60000

    internal const val CACHE_SIZE = 20 //megabytes

    internal const val MAX_PAGE_WIDTH_DEFAULT = 500

    /**
     * Whether to accept an https certificate that does not validate.
     *
     * Off is the honest default: with it on, nothing distinguishes your server from anyone on
     * the network answering in its place, and the password goes to whoever answers. It exists
     * because a Ubooquity at home is usually behind a certificate it signed itself, and turning
     * this on knowingly is a different thing from having it always on without being told.
     */
    var ALLOW_SELF_SIGNED_CERTIFICATES = false

}