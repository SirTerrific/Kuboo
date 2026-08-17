package com.sethchhim.kuboo_client.ui.about

import com.sethchhim.kuboo_client.bindView

import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import com.sethchhim.kuboo_client.R
import com.sethchhim.kuboo_client.ui.about.adapter.AboutPagerAdapter
import com.sethchhim.kuboo_client.ui.base.BaseActivity
import me.relex.circleindicator.CircleIndicator

class AboutActivity : BaseActivity() {

    val aboutViewPager: androidx.viewpager.widget.ViewPager by bindView(R.id.about_content_viewPager)
    val aboutViewPagerIndicator: CircleIndicator by bindView(R.id.about_content_circleIndicator)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        forceOrientationSetting()
        setFullScreen()
        setContentView(R.layout.about_content)

        aboutViewPager.adapter = AboutPagerAdapter(this)
        aboutViewPagerIndicator.setViewPager(aboutViewPager)
    }

}