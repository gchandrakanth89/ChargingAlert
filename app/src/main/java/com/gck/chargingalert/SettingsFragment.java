package com.gck.chargingalert;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.gck.batteryalertlib.BatteryAlertManager;
import com.gck.batteryalertlib.PreferenceUtils;
import com.gck.batteryalertlib.Util;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {


    private static final String TAG = SettingsFragment.class.getSimpleName();

    public SettingsFragment() {
    }


    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);

        setPreferenceSummary();
    }

    private void setPreferenceSummary() {
        Preference notificationPref = findPreference("key_notification");
        notificationPref.setSummary(Util.getRingtoneName(getActivity(), PreferenceUtils.getNotificationToneUri(getActivity())));

        Preference pref_notification_frequency = findPreference("pref_notification_frequency");
        pref_notification_frequency.setSummary(Util.getFreqencySummery(getActivity()));


    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equalsIgnoreCase("enable")) {
            boolean enable = sharedPreferences.getBoolean(key, true);
            BatteryAlertManager manager = BatteryAlertManager.getInstance();
            if (enable) {
                manager.enable();
            } else {
                manager.disable();
            }
        }
        setPreferenceSummary();

    }


}
