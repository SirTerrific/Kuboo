package com.sethchhim.kuboo_client

import com.sethchhim.kuboo_client.Extensions.isSameHostAs
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The server's password is attached to an image request only when the image is on the server.
 * Getting this wrong in the permissive direction hands the password to another site.
 */
class HostMatchTest {

    private val server = "http://192.168.0.1:2202/opds/"

    @Test
    fun matchesTheSameHost() {
        assertTrue("http://192.168.0.1:2202/pagereader/7?page=0".isSameHostAs(server))
        //a different port is the same machine, and Ubooquity serves its pages off the library port
        assertTrue("http://192.168.0.1:2203/admin".isSameHostAs(server))
        assertTrue("HTTP://192.168.0.1:2202/opds/".isSameHostAs(server))
    }

    @Test
    fun refusesAnyOtherHost() {
        assertFalse("http://example.com/cover.jpg".isSameHostAs(server))
        assertFalse("http://192.168.0.11:2202/cover.jpg".isSameHostAs(server))
        assertFalse("http://192.168.0.1.example.com/cover.jpg".isSameHostAs(server))
        assertFalse("not a url".isSameHostAs(server))
        assertFalse("http://192.168.0.1:2202/cover.jpg".isSameHostAs("not a url"))
    }

}
