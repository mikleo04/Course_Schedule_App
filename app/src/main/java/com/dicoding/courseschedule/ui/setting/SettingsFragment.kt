package com.dicoding.courseschedule.ui.setting

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.dicoding.courseschedule.R
import com.dicoding.courseschedule.notification.DailyReminder

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        //TODO 10 : Update theme based on value in ListPreference
        findPreference<ListPreference>(getString(R.string.pref_key_dark))?.setOnPreferenceChangeListener { preference, newValueAccept ->
            if(newValueAccept.equals("on")){
                updateTheme(AppCompatDelegate.MODE_NIGHT_YES)
            }else if(newValueAccept.equals("off")){
                updateTheme(AppCompatDelegate.MODE_NIGHT_NO)
            }else{
                updateTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
            true
        }

        //TODO 11 : Schedule and cancel notification in DailyReminder based on SwitchPreference
        findPreference<SwitchPreference>(getString(R.string.pref_key_notify))?.setOnPreferenceChangeListener { preference, newValueAccept ->
            if (newValueAccept.equals(true)){
                DailyReminder().dailyReminder(requireContext())
            }else{
                DailyReminder().cancelAlarm(requireContext())
            }
            true
        }
    }

    private fun updateTheme(nightMode: Int): Boolean {
        AppCompatDelegate.setDefaultNightMode(nightMode)
        requireActivity().recreate()
        return true
    }
}