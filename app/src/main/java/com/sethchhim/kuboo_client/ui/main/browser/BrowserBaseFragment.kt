package com.sethchhim.kuboo_client.ui.main.browser

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sethchhim.kuboo_client.R
import com.sethchhim.kuboo_client.ui.main.browser.handler.PaginationHandler

open class BrowserBaseFragment : BrowserBaseFragmentImpl3_Path() {

    protected var isCustomImplementation = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.browser_layout_base, container, false)

    // onBindViews reaches the fragment's views, which only exist once onCreateView has
    // returned. Butterknife could run inside onCreateView because it was handed the
    // inflated view directly; a lookup through the fragment cannot.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBindViews(view)
    }

    override fun onPause() {
        super.onPause()
        disableSelection(isCustomImplementation)
    }

    override fun onResume() {
        super.onResume()
        enableSelection(isCustomImplementation)
        handleNeededAdapterUpdate()
    }

    override fun onDestroy() {
        super.onDestroy()
        mainActivity.hideMenuItemSearch()
        mainActivity.hideMenuItemHttps()
        mainActivity.hideMenuItemBrowserLayout()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        setContentSpanCount(newConfig.orientation, contentRecyclerView.contentType)
    }

    protected open fun onBindViews(view: View) {
        setPath()
        setPagination()
        paginationHandler = PaginationHandler(this, view)
    }

}