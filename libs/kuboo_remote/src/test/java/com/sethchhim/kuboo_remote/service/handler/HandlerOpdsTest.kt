package com.sethchhim.kuboo_remote.service.handler

import com.sethchhim.kuboo_remote.model.Book
import com.sethchhim.kuboo_remote.model.Login
import com.sethchhim.kuboo_remote.util.resolveLink
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.xml.sax.InputSource
import java.io.StringReader
import javax.xml.parsers.SAXParserFactory

/**
 * Parses feeds captured from a real Ubooquity 3.1.0 server.
 *
 * The parser has to survive two things version 3 introduced: navigation entries whose ids are
 * words rather than numbers, and hrefs written as absolute paths from the server root.
 */
@RunWith(RobolectricTestRunner::class)
class HandlerOpdsTest {

    private val login = Login(server = "http://10.0.2.2:2202/opds-comics/")

    /** Mirrors ParseService: ampersands are escaped before parsing and restored afterwards. */
    private fun parse(xml: String): List<Book> {
        val books = mutableListOf<Book>()
        val prepared = xml.replace("\n", "").replace("\r", "").replace("&", "{ampersand}")
        try {
            SAXParserFactory.newInstance().newSAXParser()
                    .parse(InputSource(StringReader(prepared)), HandlerOpds(login, books))
        } catch (expected: Exception) {
            // the handler aborts the parse once maxResults is reached
        }
        return books
    }

    private val acquisitionFeed = """<?xml version="1.0" encoding="UTF-8"?>
        <feed xmlns="http://www.w3.org/2005/Atom" xmlns:pse="http://vaemendis.net/opds-pse/ns">
            <link type="application/atom+xml" rel="self" href="/opds/comics/3/?displayFiles=true"/>
            <entry>
                <title>Aventure01</title>
                <id>7</id>
                <content type="html">Aventure01&lt;br/&gt;&lt;br/&gt;CBZ (0 MB)</content>
                <link type="image/jpeg" rel="http://opds-spec.org/image/thumbnail" href="/opds/comics/cover/7/cov.jpg"/>
                <link type="application/octet-stream" rel="http://opds-spec.org/acquisition" href="/opds/comics/download/7/Aventure01.cbz"/>
                <link type="image/jpeg" rel="http://vaemendis.net/opds-pse/stream" href="/opds/comicreader/7?page={pageNumber}&amp;width={maxWidth}" pse:count="3"/>
            </entry>
        </feed>"""

    private val navigationFeed = """<?xml version="1.0" encoding="UTF-8"?>
        <feed xmlns="http://www.w3.org/2005/Atom">
            <link type="application/atom+xml" rel="self" href="/opds"/>
            <entry>
                <title>Comics - by folder</title>
                <id>ComicsByFolder</id>
                <content type="html">Comics grouped by folder.</content>
                <link type="application/atom+xml" rel="subsection" href="/opds/comics"/>
            </entry>
            <entry>
                <title>Comics - latest</title>
                <id>ComicsLatest</id>
                <content type="html">Latest comics added to the collection</content>
                <link type="application/atom+xml" rel="subsection" href="/opds/comics?latest=true"/>
            </entry>
        </feed>"""

    @Test
    fun `page count is read from pse count`() {
        assertEquals(3, parse(acquisitionFeed).single().totalPages)
    }

    @Test
    fun `hrefs are kept as the absolute paths the server sent`() {
        val book = parse(acquisitionFeed).single()
        assertEquals("/opds/comics/download/7/Aventure01.cbz", book.linkAcquisition)
        assertEquals("/opds/comics/cover/7/cov.jpg", book.linkThumbnail)
    }

    @Test
    fun `page stream link keeps its placeholders for later substitution`() {
        val pse = parse(acquisitionFeed).single().linkPse
        assertTrue(pse, pse.contains("{pageNumber}"))
        assertTrue(pse, pse.contains("{maxWidth}"))
        assertTrue(pse, pse.startsWith("/opds/comicreader/7"))
    }

    @Test
    fun `escaped markup is stripped from the content shown to the user`() {
        val content = parse(acquisitionFeed).single().content
        assertTrue(content, !content.contains("<br/>"))
        assertTrue(content, !content.contains("&lt;"))
        assertTrue(content, content.contains("Aventure01"))
    }

    @Test
    fun `navigation entries are parsed and keep their subsection links`() {
        val books = parse(navigationFeed)
        assertEquals(2, books.size)
        assertEquals("/opds/comics", books[0].linkSubsection)
        assertEquals("/opds/comics?latest=true", books[1].linkSubsection)
    }

    @Test
    fun `word ids do not abort parsing`() {
        assertEquals(listOf("Comics - by folder", "Comics - latest"), parse(navigationFeed).map { it.title })
    }

    @Test
    fun `resolving a parsed acquisition link produces the url the server serves`() {
        val book = parse(acquisitionFeed).single()
        assertEquals(
                "http://10.0.2.2:2202/opds/comics/download/7/Aventure01.cbz",
                book.getAcquisitionUrl())
    }

    /**
     * The feed advertises /opds/comicreader/, which Ubooquity 3 answers with the catalogue
     * feed rather than a page image. The page url therefore has to point at /pagereader/,
     * which is what the server's own web reader fetches.
     */
    @Test
    fun `page urls point at the endpoint that serves the image`() {
        val book = parse(acquisitionFeed).single()
        assertEquals(
                "http://10.0.2.2:2202/pagereader/7?page=1&width=1080",
                book.server.resolveLink(book.getPse(1080, 1)))
    }

    @Test
    fun `an ubooquity 2 page link is left as the feed gave it`() {
        val book = parse(acquisitionFeed).single().apply {
            linkPse = "/opds-comics/comicreader/7?page={pageNumber}&width={maxWidth}"
        }
        assertEquals(
                "http://10.0.2.2:2202/opds-comics/comicreader/7?page=01&width=1080",
                book.server.resolveLink(book.getPse(1080, 1)))
    }
}
