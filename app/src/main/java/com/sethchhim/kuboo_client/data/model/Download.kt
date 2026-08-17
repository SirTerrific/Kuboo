package com.sethchhim.kuboo_client.data.model

import android.annotation.SuppressLint
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import android.os.Parcelable
import com.sethchhim.kuboo_remote.model.BookData
import com.sethchhim.kuboo_remote.util.resolveLink
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
@SuppressLint("ParcelCreator")
data class Download(
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
        override var timeAccessed: Int = 0,
        @Ignore var ignored: String? = null) : BookData(), Parcelable {

    // Matches how the download was queued, which resolves the link against the server address
    // rather than pasting the two together: an Ubooquity 3 address ends in /opds/ and pasting
    // produced /opds//opds/..., so no download ever matched its entry here.
    fun getAcquisitionUrl() = server.resolveLink(linkAcquisition)

    fun getXmlId(): Int {
        val withoutQuery = this.linkXmlPath.substringBefore("?")
        val segments = withoutQuery.trim('/').split("/")
        return segments.lastOrNull { it.isNotEmpty() }?.toIntOrNull() ?: 0
    }

}