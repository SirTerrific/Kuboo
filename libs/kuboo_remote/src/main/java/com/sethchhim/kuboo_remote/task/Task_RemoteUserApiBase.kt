package com.sethchhim.kuboo_remote.task

import com.sethchhim.kuboo_remote.KubooRemote
import com.sethchhim.kuboo_remote.model.Book
import com.sethchhim.kuboo_remote.model.Login
import com.sethchhim.kuboo_remote.util.resolveLink

open class Task_RemoteUserApiBase(val kubooRemote: KubooRemote, val login: Login, val book: Book) {

    protected val okHttpHelper = kubooRemote.okHttpHelper
    protected val stringUrl = getRemoteBookmarkUrl(book)

    private fun getRemoteBookmarkUrl(book: Book): String {
        val stringBookmarkRequest = if (book.isEpub()) {
            "/user-api/bookmark?isBook=true&docId=${book.id}"
        } else {
            "/user-api/bookmark?isBook=false&docId=${book.id}"
        }
        return book.server.resolveLink(stringBookmarkRequest)
    }

}