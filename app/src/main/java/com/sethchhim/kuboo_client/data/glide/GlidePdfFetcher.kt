package com.sethchhim.kuboo_client.data.glide

import android.graphics.Bitmap
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.data.DataFetcher
import com.sethchhim.kuboo_client.BaseApplication
import com.sethchhim.kuboo_client.data.ViewModel
import com.sethchhim.kuboo_client.data.model.GlidePdf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class GlidePdfFetcher internal constructor(private val glidePdf: GlidePdf) : DataFetcher<Bitmap> {

    init {
        BaseApplication.appComponent.inject(this)
    }

    @Inject
    lateinit var viewModel: ViewModel

    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in Bitmap>) {
        when (glidePdf.singleInstance) {
            true -> loadSingleInstance(callback)
            false -> loadMultiInstance(callback)
        }
    }

    private fun loadSingleInstance(callback: DataFetcher.DataCallback<in Bitmap>) {
        GlobalScope.launch(Dispatchers.Main) {
            viewModel.getPdfImageSingleInstance(glidePdf).observeForever { result ->
                handleResult(callback, result)
            }
        }
    }

    private fun loadMultiInstance(callback: DataFetcher.DataCallback<in Bitmap>) {
        GlobalScope.launch(Dispatchers.Main) {
            viewModel.getPdfImage(glidePdf).observeForever { result ->
                handleResult(callback, result)
            }
        }
    }

    private fun handleResult(callback: DataFetcher.DataCallback<in Bitmap>, result: Bitmap?) {
        when (result) {
            null -> callback.onLoadFailed(Exception("Failed to render page ${glidePdf.position}"))
            else -> callback.onDataReady(result)
        }
    }

    // The bitmap belongs to Glide once it is handed over: it goes into the bitmap pool through
    // BitmapResource, and recycling it here would pull it out from under whatever is drawing it.
    override fun cleanup() {}

    override fun cancel() {}

    override fun getDataClass() = Bitmap::class.java

    override fun getDataSource() = DataSource.LOCAL

}
