package com.sethchhim.kuboo_remote.client

import org.junit.Assert.assertEquals
import org.junit.Test

class UbooquitySessionTest {

    /**
     * The expected value is what Ubooquity's own login script produces for this salt and
     * timestamp; getting the two keyed hashes in the wrong order still yields a plausible looking
     * hash, and the server answers a wrong hash by serving the login form again.
     */
    @Test
    fun loginHashMatchesTheServerScript() {
        val hash = ubooquityLoginHash(
                password = "kuboo123",
                salt = "d0809793df2c3be1a77a229781cfe1cdb1a2a",
                time = "1786980775113")

        assertEquals("4b64241b9b24684ce33f365d1965c4e86d3f739eed3984e67e746cd0543a65b9", hash)
    }

}
