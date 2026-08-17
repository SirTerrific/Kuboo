package com.sethchhim.kuboo_client.ui.main.login.browser

import com.sethchhim.kuboo_client.bindView

import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sethchhim.kuboo_client.Extensions.fadeVisible
import com.sethchhim.kuboo_client.Extensions.invisible
import com.sethchhim.kuboo_client.R
import com.sethchhim.kuboo_client.data.ViewModel
import com.sethchhim.kuboo_client.ui.main.MainActivity
import dagger.android.support.DaggerFragment
import javax.inject.Inject

open class LoginBrowserFragmentImpl0_View : DaggerFragment() {

    @Inject lateinit var mainActivity: MainActivity
    @Inject lateinit var viewModel: ViewModel

    val emptyLayout: ConstraintLayout by bindView(R.id.state_empty_constraintLayout)
    val errorLayout: ConstraintLayout by bindView(R.id.state_error_constraintLayout)
    val fab: FloatingActionButton by bindView(R.id.login_layout_browser_floatingActionButton)
    val loginRecyclerView: androidx.recyclerview.widget.RecyclerView by bindView(R.id.login_layout_browser_recyclerView)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.login_layout_browser, container, false)
        return view
    }

    protected fun onFabClicked() = mainActivity.showFragmentLoginEdit(login = null)

    protected fun setStateConnected() {
        loginRecyclerView.fadeVisible()
        emptyLayout.invisible()
        errorLayout.invisible()
    }

    protected fun setStateDisconnected() {
        loginRecyclerView.invisible()
        emptyLayout.invisible()
        errorLayout.fadeVisible()
    }

    protected fun setStateEmpty() {
        loginRecyclerView.invisible()
        emptyLayout.fadeVisible()
        errorLayout.invisible()
    }

}