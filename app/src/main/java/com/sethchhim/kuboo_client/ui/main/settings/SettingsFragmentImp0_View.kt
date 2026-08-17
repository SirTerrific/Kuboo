package com.sethchhim.kuboo_client.ui.main.settings

import android.content.Context
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.sethchhim.kuboo_client.BaseApplication
import com.sethchhim.kuboo_client.R
import com.sethchhim.kuboo_client.data.ViewModel
import com.sethchhim.kuboo_client.ui.main.MainActivity
import com.sethchhim.kuboo_client.util.DialogUtil
import com.sethchhim.kuboo_client.util.SharedPrefsHelper
import com.sethchhim.kuboo_client.util.SystemUtil
import com.sethchhim.kuboo_remote.KubooRemote
import javax.inject.Inject


open class SettingsFragmentImp0_View : PreferenceFragmentCompat() {

    init {
        BaseApplication.appComponent.inject(this)
    }

    @Inject lateinit var dialogUtil: DialogUtil
    @Inject lateinit var kubooRemote: KubooRemote
    @Inject lateinit var sharedPrefsHelper: SharedPrefsHelper
    @Inject lateinit var systemUtil: SystemUtil
    @Inject lateinit var viewModel: ViewModel

    protected lateinit var mainActivity: MainActivity

    protected lateinit var aboutVersionPreference: Preference
    protected lateinit var browserFavoritePreference: SwitchPreferenceCompat
    protected lateinit var browserMarkFinishedPreference: SwitchPreferenceCompat

    protected lateinit var downloadFinishedNotification: SwitchPreferenceCompat
    protected lateinit var downloadSavePath: Preference
    protected lateinit var downloadTrackingLimit: Preference
    protected lateinit var downloadTrackingInterval: Preference
    protected lateinit var downloadTrackingHideFinished: SwitchPreferenceCompat
    protected lateinit var homeLayoutPreference: Preference
    protected lateinit var serverLoginPreference: Preference
    protected lateinit var systemOrientationPreference: Preference
    protected lateinit var systemThemePreference: Preference
    protected lateinit var systemVolumePageTurnPreference: SwitchPreferenceCompat
    protected lateinit var systemDisableCellularPreference: SwitchPreferenceCompat
    protected lateinit var systemAllowSelfSignedPreference: SwitchPreferenceCompat
    protected lateinit var systemKeepScreenOn: SwitchPreferenceCompat
    protected lateinit var systemStartTab: Preference
    protected lateinit var advancedPreference: Preference

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings)
        aboutVersionPreference = findPreference<Preference>("settings_about_version")!!
        browserFavoritePreference = findPreference<SwitchPreferenceCompat>("settings_browser_favorite")!!
        browserMarkFinishedPreference = findPreference<SwitchPreferenceCompat>("settings_browser_mark_finished")!!
        downloadFinishedNotification = findPreference<SwitchPreferenceCompat>("settings_download_show_finished_notification")!!
        downloadSavePath = findPreference<Preference>("settings_download_save_path")!!
        downloadTrackingLimit = findPreference<Preference>("settings_download_tracking_limit")!!
        downloadTrackingInterval = findPreference<Preference>("settings_download_tracking_interval")!!
        downloadTrackingHideFinished = findPreference<SwitchPreferenceCompat>("settings_download_tracking_hide_finished")!!
        homeLayoutPreference = findPreference<Preference>("settings_home_layout")!!
        serverLoginPreference = findPreference<Preference>("settings_server_login")!!
        systemOrientationPreference = findPreference<Preference>("settings_system_orientation")!!
        systemThemePreference = findPreference<Preference>("settings_system_theme")!!
        systemKeepScreenOn = findPreference<SwitchPreferenceCompat>("settings_keep_screen_on")!!
        systemVolumePageTurnPreference = findPreference<SwitchPreferenceCompat>("settings_volume_page_turn")!!
        systemDisableCellularPreference = findPreference<SwitchPreferenceCompat>("settings_disable_cellular")!!
        systemAllowSelfSignedPreference = findPreference<SwitchPreferenceCompat>("settings_allow_self_signed")!!
        systemStartTab = findPreference<Preference>("settings_start_tab")!!
        advancedPreference = findPreference<Preference>("settings_advanced")!!
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    internal fun scrollToTop() = scrollToPreference(serverLoginPreference)

}