package com.kakakoi.photoviewer.ui.setting

import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.InputType
import android.text.TextUtils
import android.text.method.DigitsKeyListener
import android.text.method.PasswordTransformationMethod
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.snackbar.Snackbar
import com.kakakoi.photoviewer.R
import com.kakakoi.photoviewer.lib.EventObserver


class StorageSettingsFragment : PreferenceFragmentCompat() {

    companion object {
        const val KEY_SMB_IP = "smb_ip"
        const val KEY_SMB_PASS = "smb_pass"
        const val KEY_SYNC = "sync"
        const val KEY_SMB_USER = "smb_user"
        const val KEY_SMB_DIR = "smb_dir"
        const val TAG = "StorageSettingsFragment"
        const val KEY_ADD_NAS = "add_nas_button"
    }

    private val viewModel: StorageSettingsViewModel by viewModels()
    private var progressSnackbar: Snackbar? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        // navigation main settings
        viewModel.onTransit.observe(this, EventObserver {
            when(it) {
                viewModel.TRANSIT_SUCCESS -> findNavController().navigate(R.id.main_settings_fragment)
                viewModel.TRANSIT_FAILURE ->
                    Snackbar.make(
                        requireActivity().findViewById(android.R.id.content),
                        getString(R.string.smb_connection_failure),
                        Snackbar.LENGTH_LONG
                    ).show()
            }
        })

        // smb connect progress view
        viewModel.onProgress.observe(this, Observer {
            if(it) {
                progressSnackbar = Snackbar.make(
                    requireActivity().findViewById(android.R.id.content),
                    getString(R.string.smb_connection_progress),
                    Snackbar.LENGTH_INDEFINITE
                )
                progressSnackbar?.show()
            } else {
                progressSnackbar?.dismiss()
            }
        })

        // add button
        val button: Preference? = findPreference(KEY_ADD_NAS)
        button?.setOnPreferenceClickListener {
            viewModel.addStorage()
            true
        }

        // ip edit text
        val numberPreference: EditTextPreference? = findPreference(KEY_SMB_IP)
        numberPreference?.setOnBindEditTextListener { editText ->
            editText.keyListener = DigitsKeyListener.getInstance("0123456789.")
        }

        // pass edit text
        val passPreference: EditTextPreference? = findPreference(KEY_SMB_PASS)
        passPreference?.setOnBindEditTextListener { editText ->
            editText.inputType =
                InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_CLASS_TEXT
            editText.transformationMethod = PasswordTransformationMethod.getInstance()
            editText.selectAll()
            editText.filters = arrayOf<InputFilter>(LengthFilter(99))
        }
    }

    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
    }

    override fun onPause() {
        super.onPause()
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
    }

    /**
     * auto prefix dir path
     */
    private val listener =
        OnSharedPreferenceChangeListener { sharedPreferences, key ->
            if (key == KEY_SMB_DIR) {
                val newValue = sharedPreferences.getString(KEY_SMB_DIR, "")
                val prefixExp = "^(\\w)"
                val prefixRep = "/$1"
                val suffixExp = "(\\w)$"
                val suffixRep = "$1/"
                if (!TextUtils.isEmpty(newValue)) {
                    val afterStr = newValue!!.trim { it <= ' ' }
                        .replaceFirst(prefixExp.toRegex(), prefixRep)
                        .replaceFirst(suffixExp.toRegex(), suffixRep)
                    sharedPreferences.edit().putString(KEY_SMB_DIR, afterStr).apply()
                }
            }
        }
}