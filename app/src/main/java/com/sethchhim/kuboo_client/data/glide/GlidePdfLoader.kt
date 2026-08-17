package com.sethchhim.kuboo_client.data.glide

import android.graphics.Bitmap
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import com.bumptech.glide.signature.ObjectKey
import com.sethchhim.kuboo_client.data.model.GlidePdf

internal class GlidePdfLoader : ModelLoader<GlidePdf, Bitmap> {

    override fun handles(model: GlidePdf): Boolean {
        return true
    }

    override fun buildLoadData(model: GlidePdf, width: Int, height: Int, options: Options): ModelLoader.LoadData<Bitmap>? {
        val key = ObjectKey("${model.book.filePath}:${model.position}")
        return ModelLoader.LoadData(key, GlidePdfFetcher(model))
    }

    internal class Factory : ModelLoaderFactory<GlidePdf, Bitmap> {

        override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<GlidePdf, Bitmap> {
            return GlidePdfLoader()
        }

        override fun teardown() {
            // Do nothing, this instance doesn't own the client.
        }
    }

}
