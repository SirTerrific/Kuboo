package com.sethchhim.kuboo_remote.service.handler

import com.sethchhim.kuboo_remote.model.OpdsEntity
import com.sethchhim.kuboo_remote.util.Settings.isDebugItemCountHandler
import org.xml.sax.Attributes
import org.xml.sax.SAXException
import org.xml.sax.helpers.DefaultHandler
import timber.log.Timber

class HandlerItemCount(private val itemCountList: MutableList<String>) : DefaultHandler() {

    private companion object {
        val ITEM_COUNT_REGEX = Regex("""(\d+)\s+items""", RegexOption.IGNORE_CASE)
    }

    private val mEntity = OpdsEntity()
    private var itemCount = false

    @Throws(SAXException::class)
    override fun startElement(uri: String, localName: String, qName: String?, attributes: Attributes) {
        if (isDebugItemCountHandler) Timber.d("Start Element: $qName")
        if (qName != null) {
            if (qName.equals("TITLE", ignoreCase = true)) {
                itemCount = true
            }
        }
    }

    @Throws(SAXException::class)
    override fun endElement(uri: String, localName: String, qName: String) {
        if (isDebugItemCountHandler) Timber.d("End Element: $qName")

        if (qName.equals("TITLE", ignoreCase = true)) {
            if (itemCount) {
                // Ubooquity 2 titled feeds "Comics - N items"; version 3 uses titles such as
                // "Latest comics" with no count at all. Emit "0" when no number is present so
                // callers never have to parse arbitrary text.
                val result = ITEM_COUNT_REGEX.find(mEntity.ItemCount)?.groupValues?.get(1) ?: "0"
                itemCountList.add(result)
                throw SAXException()
            }
        }
    }

    @Throws(SAXException::class)
    override fun characters(ch: CharArray, start: Int, length: Int) {
        if (itemCount) {
            mEntity.ItemCount = String(ch, start, length)
            if (isDebugItemCountHandler) Timber.d("Found item count: ${mEntity.ItemCount}")
        }
    }

}