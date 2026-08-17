package com.sethchhim.kuboo_local.service.local

import com.github.junrar.Archive
import com.github.junrar.exception.RarException
import com.github.junrar.rarfile.FileHeader
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.util.*

class ParserRar : ParserBase(), Parser {

    private val mHeaders = ArrayList<FileHeader>()

    private lateinit var mArchive: Archive
    private var mSolidFileExtracted = false

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

    @Throws(Exception::class)
    override fun getPage(num: Int): InputStream {
        if (mArchive.mainHeader.isSolid) {
            // solid archives require special treatment
            synchronized(this) {
                if (!mSolidFileExtracted) {
                    for (h in mArchive.fileHeaders) {
                        if (!h.isDirectory && isImage(getName(h))) {
                            return mArchive.getInputStream(h)
                        }
                    }
                    mSolidFileExtracted = true
                }
            }
        }
        return mArchive.getInputStream(mHeaders[num])
    }


    @Throws(IOException::class)
    override fun destroy() {
        mArchive.close()
    }

}