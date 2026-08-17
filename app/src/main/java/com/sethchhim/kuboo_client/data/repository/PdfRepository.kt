package com.sethchhim.kuboo_client.data.repository

import com.artifex.mupdf.fitz.Document
import com.sethchhim.epublibdroid_kotlin.task.Task_EpubCoverInputStream
import com.sethchhim.kuboo_client.data.model.GlideEpub
import com.sethchhim.kuboo_client.data.model.GlidePdf
import com.sethchhim.kuboo_client.data.task.pdf.Task_GetPdfImageInputStream
import com.sethchhim.kuboo_client.data.task.pdf.Task_GetPdfOutline

class PdfRepository {

    internal lateinit var document: Document

    internal fun initPdf(filePath: String): Document {
        document = Document.openDocument(filePath)
        return document
    }

    //same lock as the page rendering: asking a mupdf document its page count while a page is
    //being drawn on it is the same unsafe concurrent access
    internal fun getPdfPageCount() = synchronized(document) { document.countPages() }

    internal fun getPdfImageInputStream(glidePdf: GlidePdf) = Task_GetPdfImageInputStream(document, glidePdf).liveData

    internal fun getPdfImageInputStreamSingleInstance(glidePdf: GlidePdf) = Task_GetPdfImageInputStream(Document.openDocument(glidePdf.book.filePath), glidePdf).liveData

    internal fun getPdfOutline() = Task_GetPdfOutline(document).liveData

}