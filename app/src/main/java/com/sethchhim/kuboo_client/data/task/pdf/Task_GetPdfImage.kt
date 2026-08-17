package com.sethchhim.kuboo_client.data.task.pdf

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import com.artifex.mupdf.fitz.Context
import com.artifex.mupdf.fitz.Document
import com.artifex.mupdf.fitz.android.AndroidDrawDevice
import com.sethchhim.kuboo_client.data.model.GlidePdf
import com.sethchhim.kuboo_client.data.task.base.Task_LocalBase
import timber.log.Timber


open class Task_GetPdfImage(private val document: Document, private val glidePdf: GlidePdf) : Task_LocalBase() {

    private companion object {
        const val SHRINK_STORE_TO_PERCENT = 50
    }

    internal val liveData = MutableLiveData<Bitmap>()

    init {
        executors.diskIO.execute {
            try {
                // One page at a time on this document. A mupdf document is not thread safe, and
                // the reader renders the page being read alongside its neighbours, so two draws
                // on the same document overlap as a matter of course -- which shows up as a
                // corrupt or truncated image rather than as an error.
                //
                // The page is released as soon as it is drawn, and mupdf's own store of decoded
                // images and fonts is halved: both hold native memory that java garbage
                // collection does not account for, and on a long document read quickly that is
                // what runs the device out of memory.
                val bitmap = synchronized(document) {
                    val page = document.loadPage(glidePdf.position)
                    try {
                        AndroidDrawDevice.drawPage(page, AndroidDrawDevice.fitPage(page, glidePdf.width, glidePdf.height))
                    } finally {
                        page.destroy()
                        Context.shrinkStore(SHRINK_STORE_TO_PERCENT)
                    }
                }

                if (bitmap == null) Timber.e("Failed to draw pdf page ${glidePdf.position}")
                executors.mainThread.execute { liveData.value = bitmap }
            } catch (e: Throwable) {
                //an out of memory error on a large page is an Error, not an Exception
                Timber.e("Failed to render pdf page ${glidePdf.position}: $e")
                executors.mainThread.execute { liveData.value = null }
            }
        }
    }

}
