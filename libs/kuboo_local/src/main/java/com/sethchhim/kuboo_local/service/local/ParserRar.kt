package com.sethchhim.kuboo_local.service.local

import com.github.junrar.Archive
import com.github.junrar.exception.RarException
import com.github.junrar.rarfile.FileHeader
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.util.*

class ParserRar : ParserBase(), Parser {

    private val mHeaders = ArrayList<FileHeader>()

    private lateinit var mArchive: Archive

    @Throws(IOException::class)
    override fun parse(file: File) {
        try {
            mArchive = Archive(file)
        } catch (e: RarException) {
            throw IOException("unable to open archive")
        }

        var header = mArchive.nextFileHeader()
        while (header != null) {
            if (!header.isDirectory) {
                val name = getName(header)
                if (isImage(name)) {
                    mHeaders.add(header)
                }
            }

            header = mArchive.nextFileHeader()
        }

        mHeaders.sortBy { getName(it) }
    }

    /** junrar 7+ resolves the unicode and non-unicode header names behind a single accessor. */
    private fun getName(header: FileHeader): String = header.fileName

    override fun numPages() = mHeaders.size

    /**
     * One page at a time, and read to the end before the next one starts.
     *
     * A rar archive is one stateful reader over one file, and the reader draws the page being
     * read alongside its neighbours: three of those calls would land on this archive at once and
     * interleave, which is not an error anywhere -- it is a page that arrives half decoded, or
     * not at all. Measured on a 224 page cbr: the first page drew as a sliver, the second came
     * back as a null bitmap.
     *
     * The page is extracted whole inside the lock rather than handing back a stream that is read
     * later, outside it. Junrar's own stream is piped from a thread it starts, so returning one
     * would put the reading back where the interleaving happened. A page held in memory is what
     * the image loader was going to do with it anyway.
     *
     * This also drops a special case for solid archives that returned the first image of the
     * archive whatever page was asked for.
     */
    @Throws(Exception::class)
    override fun getPage(num: Int): InputStream = synchronized(this) {
        val output = ByteArrayOutputStream()
        mArchive.extractFile(mHeaders[num], output)
        return ByteArrayInputStream(output.toByteArray())
    }

    @Throws(IOException::class)
    override fun destroy() {
        mArchive.close()
    }

}
