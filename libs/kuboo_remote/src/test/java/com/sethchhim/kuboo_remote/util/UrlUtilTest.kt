package com.sethchhim.kuboo_remote.util

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Link resolution is what makes the app work against both Ubooquity versions.
 *
 * Version 3 emits feed hrefs as absolute paths from the server root, while the app itself
 * builds relative paths. Getting this wrong is easy to miss because version 3 answers any
 * unknown OPDS path with HTTP 200 and its root navigation feed instead of an error.
 */
class UrlUtilTest {

    private val comicsServer = "http://192.168.0.1:2202/opds-comics/"
    private val booksServer = "http://192.168.0.1:2202/opds-books/"

    @Test
    fun `absolute feed href resolves against the server root, dropping the configured suffix`() {
        assertEquals(
                "http://192.168.0.1:2202/opds/comics/download/4/Comic.cbz",
                comicsServer.resolveLink("/opds/comics/download/4/Comic.cbz"))
    }

    @Test
    fun `absolute href resolves the same way whichever section was configured`() {
        val href = "/opds/comics/cover/9/cov.jpg"
        assertEquals(comicsServer.resolveLink(href), booksServer.resolveLink(href))
    }

    @Test
    fun `relative path resolves against the configured address, not the root`() {
        assertEquals(
                "http://192.168.0.1:2202/opds-comics/all?groupByFolder=true",
                comicsServer.resolveLink("all?groupByFolder=true"))
    }

    @Test
    fun `query-only link keeps the configured path`() {
        assertEquals(
                "http://192.168.0.1:2202/opds-comics/?latest=true",
                comicsServer.resolveLink("?latest=true"))
    }

    @Test
    fun `search link built by the app keeps its query intact`() {
        assertEquals(
                "http://192.168.0.1:2202/opds-comics/?search=true&searchstring=Tintin",
                comicsServer.resolveLink("?search=true&searchstring=Tintin"))
    }

    @Test
    fun `an already absolute url is returned as is`() {
        val absolute = "http://elsewhere:2202/opds/comics"
        assertEquals(absolute, comicsServer.resolveLink(absolute))
    }

    @Test
    fun `a server address without a trailing slash still resolves absolute hrefs`() {
        assertEquals(
                "http://192.168.0.1:2202/opds/comics",
                "http://192.168.0.1:2202".resolveLink("/opds/comics"))
    }

    @Test
    fun `https and non default ports are preserved`() {
        assertEquals(
                "https://books.example.com:8443/opds/comics/1/",
                "https://books.example.com:8443/opds-comics/".resolveLink("/opds/comics/1/"))
    }

    @Test
    fun `a malformed server address falls back to concatenation instead of throwing`() {
        assertEquals("not a url/opds", "not a url".resolveLink("/opds"))
    }
}
