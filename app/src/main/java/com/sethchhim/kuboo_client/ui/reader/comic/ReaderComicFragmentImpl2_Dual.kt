package com.sethchhim.kuboo_client.ui.reader.comic

import com.sethchhim.kuboo_client.bindView

import android.graphics.Bitmap
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.github.ybq.android.spinkit.SpinKitView
import com.sethchhim.kuboo_client.Constants
import com.sethchhim.kuboo_client.Constants.ARG_BOOK
import com.sethchhim.kuboo_client.Constants.ARG_LOCAL
import com.sethchhim.kuboo_client.Constants.ARG_POSITION
import com.sethchhim.kuboo_client.Extensions.disable
import com.sethchhim.kuboo_client.Extensions.dismissDelayed
import com.sethchhim.kuboo_client.Extensions.enable
import com.sethchhim.kuboo_client.Extensions.fadeGone
import com.sethchhim.kuboo_client.Extensions.fadeVisible
import com.sethchhim.kuboo_client.R
import com.sethchhim.kuboo_client.data.model.Dimension
import com.sethchhim.kuboo_client.data.model.GlideLocal
import com.sethchhim.kuboo_client.ui.reader.comic.custom.ReaderPageImageView
import com.sethchhim.kuboo_remote.model.Book

class ReaderComicFragmentImpl2_Dual : ReaderComicFragment() {

    val imageView1: ReaderPageImageView by bindView(R.id.reader_item_comic_single_panel1_readerPageImageView)
    val spinKitView1: SpinKitView by bindView(R.id.reader_item_comic_single_panel1_spinKitView)
    val swipeRefreshLayout1: androidx.swiperefreshlayout.widget.SwipeRefreshLayout by bindView(R.id.reader_item_fail_swipeRefreshLayout1)
    val failConstraintLayout1: ConstraintLayout by bindView(R.id.reader_item_fail_constraintLayout1)
    val failTextView1: TextView by bindView(R.id.reader_item_fail_textView1)

    val imageView2: ReaderPageImageView by bindView(R.id.reader_item_comic_dual_panel2_readerPageImageView)
    val spinKitView2: SpinKitView by bindView(R.id.reader_item_comic_dual_panel2_spinKitView)
    val swipeRefreshLayout2: androidx.swiperefreshlayout.widget.SwipeRefreshLayout by bindView(R.id.reader_item_fail_swipeRefreshLayout2)
    val failConstraintLayout2: ConstraintLayout by bindView(R.id.reader_item_fail_constraintLayout2)
    val failTextView2: TextView by bindView(R.id.reader_item_fail_textView2)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.reader_layout_comic_dual_content, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val page1 = getPage1()
        val page1Int = getPage1ToInt()
        val page2 = getPage2()
        val page2Int = getPage2ToInt()
        val isPage1Single = page1 == Constants.KEY_SINGLE
        val isPage2Single = page2 == Constants.KEY_SINGLE

        spinKitView1.setVisibilityToPip()
        spinKitView2.setVisibilityToPip()
        imageView1.setScaleToPip(singlePane = isPage2Single)
        imageView2.setScaleToPip(singlePane = isPage2Single)
        if (!isPage1Single) {
            imageView1.loadImage(when (isLocal) {
                true -> GlideLocal(book, page1Int)
                false -> page1
            }, getRequestListener1())
            swipeRefreshLayout1.setColorSchemeResources(R.color.lightColorAccent)
            swipeRefreshLayout1.setOnRefreshListener {
                imageView1.loadImage(when (isLocal) {
                    true -> GlideLocal(book, page1Int)
                    false -> page1
                }, getRequestListener1())
            }
        }

        if (!isPage2Single) {
            imageView1.navigationButtonType = 1
            imageView2.navigationButtonType = 2
            imageView2.loadImage(when (isLocal) {
                true -> GlideLocal(book, page2Int)
                false -> page2
            }, getRequestListener2())
            swipeRefreshLayout2.setColorSchemeResources(R.color.lightColorAccent)
            swipeRefreshLayout2.setOnRefreshListener {
                imageView2.loadImage(when (isLocal) {
                    true -> GlideLocal(book, page2Int)
                    false -> page2
                }, getRequestListener2())
            }
        }
    }

    private fun onLoadImage1Success() {
        viewModel.setReaderDimension(position, Dimension(readerComicActivity.contentFrameLayout.width, readerComicActivity.contentFrameLayout.height))
        spinKitView1.fadeGone()
        imageView1.fadeVisible()

        swipeRefreshLayout1.dismissDelayed()
        swipeRefreshLayout1.disable()
        failConstraintLayout2.fadeGone()
    }

    private fun onLoadImage1Fail(message: String?) {
        viewModel.setReaderDimension(position, Dimension(readerComicActivity.contentFrameLayout.width, readerComicActivity.contentFrameLayout.height))
        spinKitView1.fadeGone()
        imageView1.fadeVisible()

        val swipeText = "Swipe down to refresh!"
        val reasonText = message?.let { it } ?: "Failed to load image!"
        failTextView1.text = "$swipeText\n$reasonText"
        failConstraintLayout1.fadeVisible()
        swipeRefreshLayout1.dismissDelayed()
        swipeRefreshLayout1.enable()
    }

    private fun onLoadImage2Success() {
        spinKitView2.fadeGone()
        imageView2.fadeVisible()

        swipeRefreshLayout2.dismissDelayed()
        swipeRefreshLayout2.disable()
        failConstraintLayout2.fadeGone()
    }

    private fun onLoadImage2Fail(message: String?) {
        spinKitView2.fadeGone()
        imageView2.fadeVisible()

        val swipeText = "Swipe down to refresh!"
        val reasonText = message?.let { it } ?: "Failed to load image!"
        failTextView2.text = "$swipeText\n$reasonText"
        failConstraintLayout2.fadeVisible()
        swipeRefreshLayout2.dismissDelayed()
        swipeRefreshLayout2.enable()
    }

    private fun getRequestListener1() = object : RequestListener<Bitmap> {
        override fun onResourceReady(resource: Bitmap, model: Any, target: Target<Bitmap>?, dataSource: DataSource, isFirstResource: Boolean): Boolean {
            onLoadImage1Success()
            return false
        }

        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>, isFirstResource: Boolean): Boolean {
            onLoadImage1Fail(e?.message)
            return false
        }
    }

    private fun getRequestListener2() = object : RequestListener<Bitmap> {
        override fun onResourceReady(resource: Bitmap, model: Any, target: Target<Bitmap>?, dataSource: DataSource, isFirstResource: Boolean): Boolean {
            onLoadImage2Success()
            return false
        }

        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>, isFirstResource: Boolean): Boolean {
            onLoadImage2Fail(e?.message)
            return false
        }
    }

    companion object {
        fun newInstance(book: Book, isLocal: Boolean, position: Int): ReaderComicFragmentImpl2_Dual {
            return ReaderComicFragmentImpl2_Dual().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_BOOK, book)
                    putBoolean(ARG_LOCAL, isLocal)
                    putInt(ARG_POSITION, position)
                }
            }
        }
    }

}