package com.sethchhim.epublibdroid_kotlin

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import com.sethchhim.epublibdroid_kotlin.custom.GestureListener

@SuppressLint("SetJavaScriptEnabled")
class EpubReaderView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : EpubReaderViewImpl1_Content(context, attrs, defStyleAttr) {

    init {
        setWebViewSettings()
        setOnTouchListener(object : GestureListener(context) {
            /**
             * A tap is either following a link or asking for the overlay.
             *
             * The gesture detector consumes every touch event -- it has to, or the web view
             * would scroll on its own and fight the paging -- so the web view never sees a tap
             * and never followed a link. Nothing in a book was clickable: not the table of
             * contents, not a footnote, not a cross reference. The page is asked what sits under
             * the finger instead.
             */
            override fun onClick(x: Float, y: Float) {
                super.onClick(x, y)
                findLinkAt(x, y) { href ->
                    when (href) {
                        null -> epubReaderListener.onClickEpubReaderView()
                        else -> epubReaderListener.onLinkClicked(href)
                    }
                }
            }

            override fun onLongPress() {
                super.onLongPress()
                epubReaderListener.onLongPressEpubReaderView()
            }

            override fun onSwipeRight() {
                super.onSwipeRight()
                goToPreviousPage()
            }

            override fun onSwipeLeft() {
                super.onSwipeLeft()
                goToNextPage()
            }

            override fun onSwipeUp() {
                super.onSwipeUp()
                goToNextPage()
            }

            override fun onSwipeDown() {
                super.onSwipeDown()
                goToPreviousPage()
            }
        })
    }

    fun restoreSettings(backgroundColor: String = Settings.DEFAULT_BACKGROUND_COLOR,
                        fontColor: String = Settings.DEFAULT_FONT_COLOR,
                        fontPath: String = Settings.DEFAULT_FONT_PATH,
                        lineHeight: Int = Settings.DEFAULT_LINE_HEIGHT,
                        marginSize: Int = Settings.DEFAULT_MARGIN_SIZE,
                        scrollDuration: Int = Settings.DEFAULT_SCROLL_DURATION,
                        textZoom: Int = Settings.DEFAULT_TEXT_ZOOM) {
        setCustomBackgroundColor(backgroundColor)
        setFontColor(fontColor)
        setFontPath(fontPath)
        setLineHeight(lineHeight)
        setMargin(marginSize)
        setScrollDuration(scrollDuration)
        setTextZoom(textZoom)

        applyTextZoom(Settings.TEXT_ZOOM)
    }

}