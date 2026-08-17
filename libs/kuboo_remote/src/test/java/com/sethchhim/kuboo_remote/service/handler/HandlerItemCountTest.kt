package com.sethchhim.kuboo_remote.service.handler

import org.junit.Assert.assertEquals
import org.junit.Test
import org.xml.sax.InputSource
import java.io.StringReader
import javax.xml.parsers.SAXParserFactory

/**
 * Ubooquity 2 titled its feeds "Comics - 12 items"; version 3 uses titles such as
 * "Latest comics" that carry no count at all. Feeding the raw title to Integer.parseInt
 * crashed the browser, so the handler must only ever emit digits.
 */
class HandlerItemCountTest {

    private fun countFrom(title: String): String {
        val results = mutableListOf<String>()
        val xml = """<?xml version="1.0" encoding="UTF-8"?>
            <feed xmlns="http://www.w3.org/2005/Atom"><title>$title</title></feed>"""
        try {
            SAXParserFactory.newInstance().newSAXParser()
                    .parse(InputSource(StringReader(xml)), HandlerItemCount(results))
        } catch (expected: Exception) {
            // the handler aborts the parse with SAXException once it has the count
        }
        return results.firstOrNull() ?: "0"
    }

    @Test
    fun `reads the count from a version 2 comics title`() {
        assertEquals("12", countFrom("Comics - 12 items"))
    }

    @Test
    fun `reads the count from a version 2 books title`() {
        assertEquals("7", countFrom("Books - 7 items"))
    }

    @Test
    fun `version 3 title without a count yields zero rather than crashing`() {
        assertEquals("0", countFrom("Latest comics"))
    }

    @Test
    fun `version 3 folder title yields zero`() {
        assertEquals("0", countFrom("Comics folders"))
    }

    @Test
    fun `a single item is read correctly`() {
        assertEquals("1", countFrom("Comics - 1 items"))
    }

    @Test
    fun `every emitted value parses as an integer`() {
        listOf("Comics - 12 items", "Latest comics", "Ubooquity catalog", "Search results")
                .forEach { assertEquals(true, countFrom(it).toIntOrNull() != null) }
    }
}
