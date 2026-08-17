package com.sethchhim.kuboo_client.data.task.pdf

import androidx.lifecycle.MutableLiveData
import android.graphics.Bitmap
import com.artifex.mupdf.fitz.Context
import com.artifex.mupdf.fitz.Document
import com.artifex.mupdf.fitz.android.AndroidDrawDevice
import com.sethchhim.kuboo_client.data.model.GlidePdf
import com.sethchhim.kuboo_client.data.task.base.Task_LocalBase
import timber.log.Timber
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream


open class Task_GetPdfImageInputStream(private val document: Document, private val glidePdf: GlidePdf) : Task_LocalBase() {

    private companion object {
        const val SHRINK_STORE_TO_PERCENT = 50
    }

    internal val liveData = MutableLiveData<InputStream>()

    init {
        executors.diskIO.execute {
            try {
                // One page at a time on this document. A mupdf document is not thread safe, and
                // the reader renders the page being read alongside its neighbours, so two draws
                // on the same document overlap as a matter of course -- which shows up as a
                // corrupt or truncated image rather than as an error.
                //
                // The page is released as soon as it is drawn. It holds native memory that java
                // garbage collection does not account for, so on a long document read quickly
                // the leak is what runs the device out of memory, and the failure surfaces from
                // inside the image loader as "Mark has been invalidated".
                val bitmap = synchronized(document) {
                    val page = document.loadPage(glidePdf.position)
                    try {
                        AndroidDrawDevice.drawPage(page, AndroidDrawDevice.fitPage(page, glidePdf.width, glidePdf.height))
                    } finally {
                        page.destroy()
                        // mupdf keeps decoded images and fonts in a native store that java never
                        // sees. Reading a magazine, whose every page is a full page photograph,
                        // fills it: measured on a 830 page pdf, the native heap reached 276MB
                        // after thirty pages and stayed there. Halving the store after each page
                        // keeps the cache useful for the neighbours without letting it grow.
                        Context.shrinkStore(SHRINK_STORE_TO_PERCENT)
                    }
                }

                when (bitmap) {
                    null -> {
                        Timber.e("Failed to draw pdf page ${glidePdf.position}")
                        executors.mainThread.execute { liveData.value = null }
                    }
                    else -> {
                        val byteArrayOutputStream = ByteArrayOutputStream(bitmap.byteCount / 4)
                        bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, byteArrayOutputStream)
                        val bytes = byteArrayOutputStream.toByteArray()
                        byteArrayOutputStream.close()
                        bitmap.recycle()
                        val byteArrayInputStream = ByteArrayInputStream(bytes)
                        executors.mainThread.execute { liveData.value = byteArrayInputStream }
                    }
                }
            } catch (e: Throwable) {
                //an out of memory error on a large page is an Error, not an Exception
                Timber.e("Failed to render pdf page ${glidePdf.position}: $e")
                executors.mainThread.execute { liveData.value = null }
            }
        }
    }

}

