package com.sethchhim.kuboo_client.ui.preview

import com.sethchhim.kuboo_client.bindView

import android.annotation.SuppressLint
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.sethchhim.kuboo_client.R
import com.sethchhim.kuboo_client.data.enum.Source
import com.sethchhim.kuboo_client.data.model.ReadData
import com.sethchhim.kuboo_client.ui.base.BaseActivity
import com.sethchhim.kuboo_client.ui.base.custom.OnLoadCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("Registered")
open class PreviewActivityImpl0_View : BaseActivity(), OnLoadCallback {

    val fab: FloatingActionButton by bindView(R.id.preview_layout_base_floatingActionButton)
    val imageView: ImageView by bindView(R.id.preview_layout_base_imageView)
    val textView: TextView by bindView(R.id.preview_layout_base_textView)

    override fun onFinishLoad() = fab.show()

    protected fun onClickedFab() {
        GlobalScope.launch(Dispatchers.Main) {
            fab.hide()
            delay(300)
            try {
                startReader(ReadData(book = currentBook, onLoadCallback = this@PreviewActivityImpl0_View, sharedElement = imageView, source = Source.PREVIEW))
            } catch (e: RuntimeException) {
                e.printStackTrace()
            }
        }
    }

}