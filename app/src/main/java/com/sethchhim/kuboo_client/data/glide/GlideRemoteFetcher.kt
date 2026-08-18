package com.sethchhim.kuboo_client.data.glide

import android.accounts.NetworkErrorException
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.data.DataFetcher
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.util.ContentLengthInputStream
import com.bumptech.glide.util.Synthetic
import com.google.gson.Gson
import com.sethchhim.kuboo_client.BaseApplication
import com.sethchhim.kuboo_client.Extensions.toGlideUrl
import com.sethchhim.kuboo_client.Settings
import com.sethchhim.kuboo_client.data.ViewModel
import com.sethchhim.kuboo_client.util.SystemUtil
import okhttp3.Call
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import timber.log.Timber
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject

class GlideRemoteFetcher internal constructor(private val client: Call.Factory, private val url: GlideUrl) : DataFetcher<InputStream> {

    private companion object {
        /** Kuboo server serves image metadata here; thumbnails come back as images directly. */
        const val KUBOO_IMAGE_META_PATH = "/cache/images/"
    }

    init {
        BaseApplication.appComponent.inject(this)
    }

    @Inject lateinit var systemUtil: SystemUtil
    @Inject lateinit var viewModel: ViewModel

    @Synthetic private var stream: InputStream? = null
    @Synthetic private var responseBody: ResponseBody? = null
    @Volatile private var call: Call? = null

    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>) {
        if (systemUtil.isNetworkAllowed()) {
            //basic authentication
            val activeLogin = viewModel.getActiveLogin()
            val stringUrl = url.toStringUrl()
            val authGlideUrl = stringUrl.toGlideUrl(activeLogin)

            //request
            val requestBuilder = Request.Builder().url(authGlideUrl.toStringUrl())
            for ((key, value) in authGlideUrl.headers) {
                requestBuilder.addHeader(key, value)
            }
            val request = requestBuilder.build()

            //call
            call = client.newCall(request)
            call!!.enqueue(object : okhttp3.Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onLoadFailed(e)
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    responseBody = response.body
                    if (response.isSuccessful && responseBody != null) {
                        // A Kuboo server answers its image endpoint with JSON metadata that
                        // points at the real file, while Ubooquity returns the image itself.
                        // Decide from the url actually requested: deriving it from the
                        // configured server address misreads an Ubooquity 3 instance set up
                        // with its natural "/opds/" address, and the image is then parsed
                        // as JSON.
                        when (url.toStringUrl().contains(KUBOO_IMAGE_META_PATH)) {
                            true -> onResponseKubooImageMeta()
                            false -> handleResponse()
                        }
                    }
                }

                private fun onResponseKubooImageMeta() {
                    try {
                        val contentLength = responseBody!!.contentLength()
                        val inputStream = ContentLengthInputStream.obtain(responseBody!!.byteStream(), contentLength)
                        val inputAsString = inputStream.use {
                            it.bufferedReader().use { it.readText() }
                        }
                        val jsonObject = Gson().fromJson(inputAsString, ImageMeta::class.java)
                        val kubooUrl = viewModel.getActiveServer() + jsonObject.fileName
                        loadDataKuboo(kubooUrl)
                    } catch (e: Exception) {
                        callback.onLoadFailed(e)
                    }
                }

                private fun loadDataKuboo(kubooUrl: String) {
                    val authGlideUrl2 = kubooUrl.toGlideUrl(activeLogin)
                    val requestBuilder2 = Request.Builder().url(authGlideUrl2.toStringUrl())
                    for ((key, value) in authGlideUrl.headers) {
                        requestBuilder2.addHeader(key, value)
                    }
                    val request2 = requestBuilder2.build()
                    call = client.newCall(request2)
                    call!!.enqueue(object : okhttp3.Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            callback.onLoadFailed(e)
                        }

                        @Throws(IOException::class)
                        override fun onResponse(call: Call, response: Response) {
                            responseBody = response.body
                            if (response.isSuccessful && responseBody != null) {
                                handleResponse()
                            }
                        }
                    })
                }

                private fun handleResponse() {
                    val contentLength = responseBody!!.contentLength()
                    stream = ContentLengthInputStream.obtain(responseBody!!.byteStream(), contentLength)
                    callback.onDataReady(stream)
                }

            })
        } else {
            val message = "Network is not allowed! disableCellular[${Settings.DISABLE_CELLULAR}] isNetworkAllowed[${systemUtil.isNetworkAllowed()}]"
            Timber.w(message)
            throw (NetworkErrorException(message))
        }
    }

    override fun cleanup() {
        try {
            call?.cancel()
        } catch (e: IOException) {
        }
    }

    override fun cancel() {
        try {
            call?.cancel()
        } catch (e: IOException) {
        }
    }

    override fun getDataClass() = InputStream::class.java

    override fun getDataSource() = DataSource.REMOTE

}

data class ImageMeta(val fileName: String, val width: Int, val height: Int, val isWide: Boolean)