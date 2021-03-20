package com.kakakoi.photoviewer.ui.setting

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.kakakoi.photoviewer.R

class StorageSettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}