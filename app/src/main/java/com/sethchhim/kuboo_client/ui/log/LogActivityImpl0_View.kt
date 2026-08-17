package com.sethchhim.kuboo_client.ui.log

import com.sethchhim.kuboo_client.bindView

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import android.widget.CheckBox
import android.widget.TextView
import com.sethchhim.kuboo_client.R
import com.sethchhim.kuboo_client.ui.base.BaseActivity

@SuppressLint("Registered")
open class LogActivityImpl0_View : BaseActivity() {

    val recyclerView: androidx.recyclerview.widget.RecyclerView by bindView(R.id.log_layout_base_recyclerView)
    val textView: TextView by bindView(R.id.log_layout_base_textView)
    val checkBoxUi: CheckBox by bindView(R.id.log_layout_base_checkBox1)
    val checkBoxLocal: CheckBox by bindView(R.id.log_layout_base_checkBox2)
    val checkBoxNetwork: CheckBox by bindView(R.id.log_layout_base_checkBox3)
    val checkBoxError: CheckBox by bindView(R.id.log_layout_base_checkBox4)

    protected fun initUi() {
        setContentView(R.layout.log_layout_base)

        checkBoxUi.isChecked = true
        checkBoxLocal.isChecked = true
        checkBoxNetwork.isChecked = true
        checkBoxError.isChecked = false
    }

}