package com.sethchhim.kuboo_client.data.glide

import android.graphics.Bitmap
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.ResourceDecoder
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapResource

/**
 * Hands mupdf's own bitmap to Glide as it is.
 *
 * The page used to be compressed to png in memory and decoded straight back, so every page cost
 * three copies of itself: the bitmap mupdf drew, the png bytes, and the bitmap Glide decoded from
 * them. On a magazine page at screen size that is around 30MB for one page.
 */
internal class GlidePdfBitmapDecoder(private val bitmapPool: BitmapPool) : ResourceDecoder<Bitmap, Bitmap> {

    override fun handles(source: Bitmap, options: Options) = true

    override fun decode(source: Bitmap, width: Int, height: Int, options: Options): Resource<Bitmap>? =
            BitmapResource.obtain(source, bitmapPool)

}
