package com.sethchhim.kuboo_client.ui.reader.base

import com.sethchhim.kuboo_client.bindView

import android.annotation.SuppressLint
import android.app.PictureInPictureParams
import android.os.Build
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Guideline
import androidx.appcompat.widget.Toolbar
import android.util.Rational
import android.view.View
import android.widget.*
import com.sethchhim.kuboo_client.R
import com.sethchhim.kuboo_client.Settings
import com.sethchhim.kuboo_client.data.model.ReadData
import com.sethchhim.kuboo_client.ui.base.BaseActivity
import com.sethchhim.kuboo_client.ui.reader.comic.custom.ReaderPreviewImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.sethchhim.kuboo_client.toast
import timber.log.Timber


@SuppressLint("Registered")
open class ReaderBaseActivityImpl0_View : BaseActivity() {

    val constraintLayout: ConstraintLayout by bindView(R.id.reader_layout_base_constraintLayout)
    val progressBar: ProgressBar by bindView(R.id.reader_layout_base_content_progressBar)
    val contentFrameLayout: FrameLayout by bindView(R.id.reader_layout_base_content_frameLayout)
    val overlayLayout: ConstraintLayout by bindView(R.id.reader_layout_base_overlay_constraintLayout)
    val guidelineHorizontal: Guideline by bindView(R.id.reader_layout_base_overlay_guideLine1)
    val guidelineVertical: Guideline by bindView(R.id.reader_layout_comic_dual_content_guideLine)
    val overlayImageView: ImageView by bindView(R.id.reader_layout_base_overlay_imageView)
    val overlayChapterButton: Button by bindView(R.id.reader_layout_base_overlay_button)
    val overlayTextLayout: LinearLayout by bindView(R.id.reader_layout_base_overlay_linearLayout)
    val overlaySeekBar: SeekBar by bindView(R.id.reader_layout_base_overlay_seekBar)
    val overlayTextView1: TextView by bindView(R.id.reader_layout_base_overlay_textView1)
    val overlayPageNumberTextView: TextView by bindView(R.id.reader_layout_base_overlay_textView3)
    val overlayTotalPagesTextView: TextView by bindView(R.id.reader_layout_base_overlay_textView5)
    val previewImageView: ReaderPreviewImageView by bindView(R.id.reader_layout_base_preview_readerPreviewImageView)
    val toolbar: Toolbar by bindView(R.id.reader_layout_base_toolBar)

    //long enough for slide_in_right to land, and no longer
    private val EXIT_TRANSITION_DURATION = 300L

    internal var isLocal = false
    internal var isInPipMode = false
    protected var isBackStackLost = false

    internal var pipPosition = 0
    protected var pipWidth = 0
    protected var pipHeight = 0

    protected open fun showEnterTransition() {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                delay(1200)
                previewImageView.slideOut()
                onEnterTransitionFinished()
                delay(300)
                previewImageView.isAnimatingTransition = false
            } catch (e: RuntimeException) {
                e.printStackTrace()
            }
        }
    }

    protected fun showNewIntentTransition() {
        if (!previewImageView.isAnimatingTransition) {
            GlobalScope.launch(Dispatchers.Main) {
                try {
                    previewImageView.isAnimatingTransition = true
                    delay(300)
                    previewImageView.slideIn()
                    delay(800)
                    startReader(ReadData(book = nextBook, bookmarksEnabled = false, sharedElement = previewImageView, source = source))
                } catch (e: Exception) {
                    Timber.e(e)
                }
            }
        }
    }

    /**
     * Leaving is never refused and never made to wait long.
     *
     * This used to sit out 300ms, slide the preview in, then sit out another 800ms before
     * finishing -- and if a transition was already running it returned without doing anything at
     * all, so the press that asked to leave was simply dropped. A book that would not close on
     * the first or second press is that.
     */
    private fun showExitTransition() {
        if (previewImageView.isAnimatingTransition) {
            super.onBackPressed()
            return
        }

        GlobalScope.launch(Dispatchers.Main) {
            try {
                previewImageView.isAnimatingTransition = true
                showStatusBar()
                previewImageView.slideIn()
                delay(EXIT_TRANSITION_DURATION)
            } catch (e: Exception) {
                Timber.e(e)
            }
            super@ReaderBaseActivityImpl0_View.onBackPressed()
        }
    }

    protected fun exitActivity() {
        val isBannedFromTransition = currentBook.isBannedFromTransition()
        when (isBannedFromTransition) {
            true -> finish()
            false -> showExitTransition()
        }
    }

    protected fun startPictureInPictureMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val pipParams = PictureInPictureParams.Builder().apply {
                val aspectRatio = Rational(pipWidth, pipHeight)
                setAspectRatio(aspectRatio)
            }.build()
            enterPictureInPictureMode(pipParams)
        } else {
            toast(getString(R.string.reader_pip_requires_android_oreo_or_above))
        }
    }

    protected fun forceOrientation() = when (Settings.DUAL_PANE) {
        true -> forceOrientationLandscape()
        false -> forceOrientationSetting()
    }

    protected fun View.setLayoutDirection() {
        layoutDirection = when (Settings.RTL) {
            true -> SeekBar.LAYOUT_DIRECTION_RTL
            false -> SeekBar.LAYOUT_DIRECTION_LTR
        }
    }

    protected fun hideReaderToolbar() = supportActionBar?.hide()

    open fun onSwipeOutOfBoundsStart() {
        //override in children
    }

    open fun onSwipeOutOfBoundsEnd() {
        //override in children
    }

    open fun onEnterTransitionFinished() {
        //override in children
    }

}