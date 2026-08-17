package com.sethchhim.kuboo_remote.model

import android.annotation.SuppressLint
import androidx.room.Entity
import androidx.room.PrimaryKey
import android.os.Parcelable
import android.webkit.MimeTypeMap
import android.webkit.URLUtil
import com.sethchhim.kuboo_remote.util.Settings.MAX_PAGE_WIDTH_DEFAULT
import com.sethchhim.kuboo_remote.util.resolveLink
import kotlinx.parcelize.Parcelize
import timber.log.Timber
import java.io.File
import java.net.URL
import java.text.NumberFormat
import java.util.*

@SuppressLint("ParcelCreator")
@Parcelize
@Entity
data class Book(
        @PrimaryKey(autoGenerate = true) override var autoId: Int = 0,
        override var id: Int = 0,
        override var title: String = "",
        override var author: String = "",
        override var content: String = "",
        override var linkAcquisition: String = "",
        override var linkSubsection: String = "",
        override var linkThumbnail: String = "",
        override var linkXmlPath: String = "",
        override var linkPrevious: String = "",
        override var linkNext: String = "",
        override var linkPse: String = "",
        override var currentPage: Int = 0,
        override var totalPages: Int = 0,
        override var server: String = "",
        override var filePath: String = "",
        override var bookMark: String = "",
        override var isFavorite: Boolean = false,
        override var isFinished: Boolean = false,
        override var timeAccessed: Int = 0) : BookData(), Parcelable {

    fun print() {
        Timber.i("======================================")
        Timber.i("autoId: $autoId")
        Timber.i("id: $id")
        Timber.i("title: $title")
        Timber.i("author: $author")
        Timber.i("content: $content")
        Timber.i("linkAcquisition: $linkAcquisition")
        Timber.i("linkSubsection: $linkSubsection")
        Timber.i("linkThumbnail: $linkThumbnail")
        Timber.i("linkXmlPath: $linkXmlPath")
        Timber.i("linkPrevious: $linkPrevious")
        Timber.i("linkNext: $linkNext")
        Timber.i("linkPse: $linkPse")
        Timber.i("currentPage: $currentPage")
        Timber.i("totalPages: $totalPages")
        Timber.i("server: $server")
        Timber.i("bookMark: $bookMark")
        Timber.i("isFavorite: $isFavorite")
        Timber.i("isFinished: $isFinished")
        Timber.i("======================================")
    }

    /**
     * A comic cover is page 0 of the page stream, rendered at the width it is shown at. The
     * server's own cover link is a thumbnail sized for its web ui, so using it left previews
     * and recently viewed looking soft; it is only the fallback now that getPse points at an
     * address the server actually serves.
     */
    fun getPseCover(maxWidth: Int) = when {
        isComic() && linkPse.isNotEmpty() -> getPse(maxWidth, 0)
        else -> linkThumbnail
    }

    fun getPse(maxWidth: Int, index: Int): String {
        val pageReaderId = pageReaderId()
        if (pageReaderId != null) return "/pagereader/$pageReaderId?page=$index&width=$maxWidth"

        var result = linkPse
        result = result.replace("{pageNumber}", index.toMinimumTwoDigits())
        result = result.replace("{maxWidth}", maxWidth.toString())
        return result
    }

    /**
     * Ubooquity 3 advertises its page stream as /opds/comicreader/{id}, but that address
     * answers with the catalogue feed instead of a page image, so reading straight from the
     * server produced blank pages. The address its own web reader fetches does serve the
     * image:
     *
     *     url = "/pagereader/" + documentId + "?page=" + p + "&width=" + IMAGE_MAX_NATIVE_WIDTH
     *
     * Only the /opds/comicreader/ shape is rewritten, which is the unified tree Ubooquity 3
     * introduced; an Ubooquity 2 link is left exactly as the feed gave it.
     */
    private fun pageReaderId(): String? {
        if (!linkPse.contains("/opds/comicreader/")) return null

        val id = linkPse.substringAfter("/opds/comicreader/").substringBefore("?").trim('/')
        return id.ifEmpty { null }
    }

    fun setTimeAccessed() {
        timeAccessed = (Date().time / 1000).toInt()
    }

    fun isEmpty(): Boolean {
        val idEmpty = this.id == 0
        val titleEmpty = this.title.isEmpty()
        val authorEmpty = this.author.isEmpty()
        val contentEmpty = this.content.isEmpty()
        return idEmpty && titleEmpty && authorEmpty && contentEmpty
    }

    fun isEpub() = filePath.endsWith(".epub", ignoreCase = true)
            || linkAcquisition.endsWith(".epub", ignoreCase = true)

    fun isComic() = filePath.endsWith(".cb7", ignoreCase = true)
            || filePath.endsWith(".cba", ignoreCase = true)
            || filePath.endsWith(".cbr", ignoreCase = true)
            || filePath.endsWith(".cbt", ignoreCase = true)
            || filePath.endsWith(".cbz", ignoreCase = true)
            || filePath.endsWith(".zip", ignoreCase = true)
            || filePath.endsWith(".rar", ignoreCase = true)
            || linkAcquisition.endsWith(".cb7", ignoreCase = true)
            || linkAcquisition.endsWith(".cba", ignoreCase = true)
            || linkAcquisition.endsWith(".cbr", ignoreCase = true)
            || linkAcquisition.endsWith(".cbt", ignoreCase = true)
            || linkAcquisition.endsWith(".cbz", ignoreCase = true)
            || linkAcquisition.endsWith(".zip", ignoreCase = true)
            || linkAcquisition.endsWith(".rar", ignoreCase = true)

    fun isPdf() = filePath.endsWith(".pdf", ignoreCase = true)
            || linkAcquisition.endsWith(".pdf", ignoreCase = true)

    fun isRar() = filePath.endsWith("cbr", ignoreCase = true)
            || filePath.endsWith("rar", ignoreCase = true)
            || linkAcquisition.endsWith("cbr", ignoreCase = true)
            || linkAcquisition.endsWith("rar", ignoreCase = true)

    fun isLocal() = filePath.isNotEmpty()

    fun isRemote() = filePath.isEmpty()

    fun isLocalValid() = File(filePath).exists()

    internal fun containsBookmarks(): Boolean {
        return isComic() || isPdf()
    }

    internal fun isSupportedType(): Boolean {
        return isComic() || isEpub() || isPdf()
    }

    fun isBannedFromPreview() = !isComic() && !isEpub()

    fun isBannedFromTransition() = !isComic() && !isEpub()

    fun isBannedFromRecent(): Boolean {
        val lastSegment = this.linkXmlPath.substringBefore("?").trim('/').substringAfterLast("/")
        return lastSegment.equals("all", ignoreCase = true) or
                this.linkXmlPath.contains("latest=true", ignoreCase = true) or
                this.linkXmlPath.contains("?search=true&searchstring=", ignoreCase = true) or
                this.linkXmlPath.contains("all?search=true&displayFiles=true&index=", ignoreCase = true)
    }

    fun getIdString() = try {
        id.toString()
    } catch (e: Exception) {
        Timber.e("Failed to get id! ${e.message}")
        "0"
    }

    fun getXmlId(): Int {
        val withoutQuery = this.linkXmlPath.substringBefore("?")
        val segments = withoutQuery.trim('/').split("/")
        return segments.lastOrNull { it.isNotEmpty() }?.toIntOrNull() ?: 0
    }

    fun getXmlIdString() = try {
        getXmlId().toString()
    } catch (e: Exception) {
        Timber.e("Failed to get xml id! ${e.message}")
        "0"
    }

    internal fun getFormattedContent(): String {
        var result = content
        try {
            if (result.contains("[") && result.contains("]")) {
                //restart text in brackets
                val startBracketIndex = result.indexOf("[")
                val endBracketIndex = result.indexOf("]")
                val s = result.substring(startBracketIndex, endBracketIndex + 2)
                result = result.replace(s, "")
            }
        } catch (ignored: Exception) {
        }

        if (result.contains(" CBZ ")) {
            result = result.replace(" CBZ ", "\n\n" + author + "\n\nCBZ ")
        }
        if (result.contains(" CBR ")) {
            result = result.replace(" CBR ", "\n\n" + author + "\n\nCBR ")
        }
        if (result.contains(" ZIP ")) {
            result = result.replace(" ZIP ", "\n\n" + author + "\n\nZIP ")
        }
        if (result.contains(" RAR ")) {
            result = result.replace(" RAR ", "\n\n" + author + "\n\nRAR ")
        }
        if (result.contains(" EPUB ")) {
            result = result.replace(" EPUB ", "\n\n" + author + "\n\nEPUB ")
        }
        if (result.contains(" PDF ")) {
            result = result.replace(" PDF ", "\n\n" + author + "\n\nPDF ")
        }

        if (result.contains(") ")) {
            //add new line after parenthesis
            result = result.replace(") ", ")\n\n")
        }
        return result
    }


    private fun URL.guessFileName(): String {
        val fileExtension = MimeTypeMap.getFileExtensionFromUrl(this.toString())
        return URLUtil.guessFileName(this.toString(), null, fileExtension)
    }

//    fun setSinglePageNumber(isDualPane: Boolean, currentItem: Int, singlePageUrlList: ArrayList<PageUrl>, widePageUrlList: ArrayList<PageUrl>) {
//        if (isDualPane) {
//            val wideItem = widePageUrlList[currentItem].page0
//
//            singlePageUrlList.forEachIndexed { index, pageUrlItem ->
//                val singleItem = pageUrlItem.page0
//                val isMatch = singleItem == wideItem
//                if (isMatch) {
//                    this.currentPage = index
//                    Timber.i("[READER] Dual pane detected widePage[$currentItem] singlePage[$index]")
//                }
//            }
//        }
//    }
//
//    fun PageUrl.getRealPageNumber(): Int {
//        val url = page0
//        if (!url.isEmpty()) {
//            val startIndex = url.indexOf("=") + 1
//            val endIndex = url.indexOf("&")
//            val pageNumberString = url.substring(startIndex, endIndex)
//            return Integer.parseInt(pageNumberString)
//        } else {
//            return 0
//        }
//    }

    fun isMatch(book: Book) = this.id == book.id && this.getXmlId() == book.getXmlId() && this.server == book.server

    fun isMatchServer(book: Book) = this.server == book.server

    fun isMatchXmlId(book: Book) = this.getXmlId() == book.getXmlId()

    fun isMatchCurrentPage(book: Book) = currentPage == book.currentPage

    fun isMatchPreview(book: Book) = getPreviewUrl() == book.getPreviewUrl()

    fun getAcquisitionUrl() = server.resolveLink(linkAcquisition)

    fun getPreviewUrl() = server.resolveLink(linkThumbnail)

    fun getPreviewUrl(maxWidth: Int) = server.resolveLink(getPseCover(maxWidth))

    fun getPreviewUrl(login: Login, maxWidth: Int) = login.server.resolveLink(getPseCover(maxWidth))

    fun getPreviewUrlMatchingWidthTo(previewUrl: String?): String? {
        if (previewUrl == null) {
            return null
        } else {
            val startIndex = previewUrl.lastIndexOf("=") + 1
            val endIndex = previewUrl.length
            val maxWidth = previewUrl.substring(startIndex, endIndex)

            val maxWidthInt = try {
                maxWidth.toInt()
            } catch (e: Exception) {
                MAX_PAGE_WIDTH_DEFAULT
            }

            return getPreviewUrl(maxWidthInt)
        }
    }

    private fun Int.toMinimumTwoDigits(): String {
        val numberFormat = NumberFormat.getInstance(Locale.US)
        numberFormat.minimumIntegerDigits = 2
        return numberFormat.format(this)
    }

}