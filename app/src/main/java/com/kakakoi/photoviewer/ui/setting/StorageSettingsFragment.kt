package com.kakakoi.photoviewer.ui.setting

import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.InputType
import android.text.TextUtils
import android.text.method.DigitsKeyListener
import android.text.method.PasswordTransformationMethod
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat
import com.kakakoi.photoviewer.R


class StorageSettingsFragment : PreferenceFragmentCompat() {

    val KEY_SMB_IP = "smb_ip"
    val KEY_SMB_PASS = "smb_pass"
    val KEY_SYNC = "sync"
    val KEY_SMB_USER = "smb_user"
    val KEY_SMB_DIR = "smb_dir"

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        val numberPreference: EditTextPreference? = findPreference(KEY_SMB_IP)

        numberPreference?.setOnBindEditTextListener { editText ->
            editText.keyListener = DigitsKeyListener.getInstance("0123456789.")
        }

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