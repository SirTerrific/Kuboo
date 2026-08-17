package com.sethchhim.kuboo_client.ui.main.browser

import com.sethchhim.kuboo_client.bindView

import android.content.res.Configuration
import androidx.constraintlayout.widget.ConstraintLayout
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.ProgressBar
import com.daimajia.numberprogressbar.NumberProgressBar
import com.sethchhim.kuboo_client.Extensions.dismissDelayed
import com.sethchhim.kuboo_client.Extensions.fadeVisible
import com.sethchhim.kuboo_client.Extensions.identify
import com.sethchhim.kuboo_client.Extensions.invisible
import com.sethchhim.kuboo_client.Extensions.visible
import com.sethchhim.kuboo_client.R
import com.sethchhim.kuboo_client.data.AppDatabaseDao
import com.sethchhim.kuboo_client.data.ViewModel
import com.sethchhim.kuboo_client.ui.main.MainActivity
import com.sethchhim.kuboo_client.ui.main.browser.adapter.BrowserContentAdapter
import com.sethchhim.kuboo_client.ui.main.browser.custom.BrowserContentRecyclerView
import com.sethchhim.kuboo_client.ui.main.browser.custom.BrowserContentSwipeRefreshLayout
import com.sethchhim.kuboo_client.ui.main.browser.custom.BrowserContentType
import com.sethchhim.kuboo_client.ui.main.browser.custom.BrowserPathRecyclerView
import com.sethchhim.kuboo_client.ui.main.downloads.custom.DownloadsTabLayout
import com.sethchhim.kuboo_client.util.DialogUtil
import com.sethchhim.kuboo_client.util.SystemUtil
import com.sethchhim.kuboo_remote.model.Book
import dagger.android.support.DaggerFragment
import timber.log.Timber
import javax.inject.Inject

open class BrowserBaseFragmentImpl0_View : DaggerFragment() {

    @Inject lateinit var appDatabaseDao: AppDatabaseDao
    @Inject lateinit var dialogUtil: DialogUtil
    @Inject lateinit var mainActivity: MainActivity
    @Inject lateinit var systemUtil: SystemUtil
    @Inject lateinit var viewModel: ViewModel

    val paginationLayout: LinearLayout by bindView(R.id.browser_layout_pagination_linearLayout)
    val contentLoading: ProgressBar by bindView(R.id.browser_layout_content_spinKitView)
    val contentRecyclerView: BrowserContentRecyclerView by bindView(R.id.browser_layout_content_browserContentRecyclerView)
    val contentSwipeRefreshLayout: BrowserContentSwipeRefreshLayout by bindView(R.id.browser_layout_content_swipeRefreshLayout)
    val downloadsNumberProgressBar: NumberProgressBar by bindView(R.id.browser_layout_download_numberProgressBar)
    val downloadsTabLayout: DownloadsTabLayout by bindView(R.id.browser_layout_downloads_tabLayout)
    val pathRecyclerView: BrowserPathRecyclerView by bindView(R.id.browser_layout_path_recyclerView)
    val emptyLayout: ConstraintLayout by bindView(R.id.state_empty_constraintLayout)
    val errorLayout: ConstraintLayout by bindView(R.id.state_error_constraintLayout)

    protected fun resetRecyclerView() {
        viewModel.clearContentList()
        (contentRecyclerView.adapter as? BrowserContentAdapter)?.let {
            it.resetAllGlide()
            it.notifyDataSetChanged()
        }
    }

    protected fun saveRecyclerViewState() {
        val book = viewModel.getCurrentBook()
        if (book != null) {
            val state = contentRecyclerView.layoutManager?.onSaveInstanceState()
            if (state != null) {
                Timber.i("saveRecyclerViewState: title[${book.title}] linkSubsection[${book.linkSubsection}] state[${state.identify()}]")
                viewModel.saveRecyclerViewState(book, state)
            }
        }
    }

    protected fun loadRecyclerViewState(book: Book) {
        val state = viewModel.loadRecyclerViewState(book)
        if (state != null) {
            Timber.i("loadRecyclerViewState: title[${book.title}] linkSubsection[${book.linkSubsection}] state[${state.identify()}]")
            contentRecyclerView.layoutManager?.onRestoreInstanceState(state)
        }
    }

    protected fun setContentSpanCount(orientation: Int, browserContentType: BrowserContentType) {
        val isHiDpi = mainActivity.isHiDpi()
        when (orientation) {
            Configuration.ORIENTATION_PORTRAIT -> when (isHiDpi) {
                true -> contentRecyclerView.setSpanCountPortraitHiDpi(browserContentType)
                false -> contentRecyclerView.setSpanCountPortrait(browserContentType)
            }
            Configuration.ORIENTATION_LANDSCAPE -> when (isHiDpi) {
                true -> contentRecyclerView.setSpanCountLandscapeHiDpi(browserContentType)
                false -> contentRecyclerView.setSpanCountLandscape(browserContentType)
            }
        }
    }

    protected fun setStateConnected() {
        contentLoading.invisible()
        contentRecyclerView.visible()
        contentSwipeRefreshLayout.dismissDelayed()
        emptyLayout.invisible()
        errorLayout.invisible()
    }

    protected fun setStateDisconnected() {
        contentLoading.invisible()
        contentRecyclerView.invisible()
        contentSwipeRefreshLayout.dismissDelayed()
        emptyLayout.invisible()
        errorLayout.fadeVisible()
    }

    protected fun setStateEmpty() {
        contentLoading.invisible()
        contentRecyclerView.invisible()
        contentSwipeRefreshLayout.dismissDelayed()
        emptyLayout.fadeVisible()
        errorLayout.invisible()
    }

    protected fun setStateLoading() {
        contentLoading.fadeVisible()
        contentRecyclerView.invisible()
        contentSwipeRefreshLayout.dismissDelayed()
        emptyLayout.invisible()
        errorLayout.invisible()
    }

    internal open fun onSwipeRefresh() {
        //override in child fragments
    }

}