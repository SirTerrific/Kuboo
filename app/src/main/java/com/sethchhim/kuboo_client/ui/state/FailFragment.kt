package com.sethchhim.kuboo_client.ui.state

import com.sethchhim.kuboo_client.bindView

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.sethchhim.kuboo_client.Constants.ARG_RESPONSE
import com.sethchhim.kuboo_client.Extensions.visible
import com.sethchhim.kuboo_client.R
import com.sethchhim.kuboo_client.ui.main.MainActivity
import com.sethchhim.kuboo_client.util.DialogUtil
import com.sethchhim.kuboo_remote.model.Response
import dagger.android.support.DaggerFragment
import com.sethchhim.kuboo_client.toast
import javax.inject.Inject

class FailFragment : DaggerFragment() {

    @Inject lateinit var dialogUtil: DialogUtil
    @Inject lateinit var mainActivity: MainActivity

    val reasonTextView: TextView by bindView(R.id.home_layout_fail_textView2)
    val retryTextView: TextView by bindView(R.id.home_layout_fail_textView3)

    private var response: Response? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            response = getParcelable(ARG_RESPONSE)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.home_layout_fail, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        response?.let { setFailTexts(it) }
    }

    private fun setFailTexts(response: Response) {
        reasonTextView.text = "${response.code}: ${response.message}"
        retryTextView.setOnClickListener { toast("Test") }

        reasonTextView.visible()
        retryTextView.visible()
    }

    companion object {
        fun newInstance(response: Response?) = FailFragment().apply {
            arguments = Bundle().apply { putParcelable(ARG_RESPONSE, response) }
        }
    }

}