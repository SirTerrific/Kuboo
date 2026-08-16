package com.sethchhim.kuboo_client.util

class FileUtil {

    internal fun isZip(filename: String) = filename.lowercase().matches(".*\\.(zip|cbz)$".toRegex())

    internal fun isRar(filename: String) = filename.lowercase().matches(".*\\.(rar|cbr)$".toRegex())

    internal fun isTarball(filename: String) = filename.lowercase().matches(".*\\.(cbt)$".toRegex())

    internal fun isSevenZ(filename: String) = filename.lowercase().matches(".*\\.(cb7|7z)$".toRegex())

    internal fun isArchive(filename: String) = isZip(filename) || isRar(filename) || isTarball(filename) || isSevenZ(filename)

}