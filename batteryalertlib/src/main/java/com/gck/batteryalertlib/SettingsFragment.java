package com.gck.batteryalertlib;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.support.v4.app.Fragment;

import com.gck.servicelib.NotificationUtil;


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
        Logger.d(TAG, "onResume");
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);

        setRingtonePreferenceSummary();
        SwitchPreference prefIsEnabled = (SwitchPreference) findPreference(PreferenceUtils.KEY_ENABLE);
        if (prefIsEnabled.isChecked()) {
            BatteryAlertManager.getInstance().enable();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Preference notificationPreference = findPreference(PreferenceUtils.KEY_NOTIFICATION_TONE);
            notificationPreference.setOnPreferenceClickListener(clickListener);

            NotificationManager manager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel notificationChannel = manager.getNotificationChannel(NotificationUtil.CHANNEL_ID);
            Uri sound = notificationChannel.getSound();

            Ringtone ringtone = RingtoneManager.getRingtone(getActivity(), sound);
            String title = ringtone.getTitle(getActivity());

            notificationPreference.setSummary(title);

        }


    }

    private Preference.OnPreferenceClickListener clickListener = new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, getActivity().getPackageName());
            intent.putExtra(Settings.EXTRA_CHANNEL_ID, NotificationUtil.CHANNEL_ID);
            startActivity(intent);
            return true;
        }
    };

    private void setRingtonePreferenceSummary() {
        Preference notificationPref = findPreference(PreferenceUtils.KEY_NOTIFICATION_TONE);
        notificationPref.setSummary(Util.getRingtoneName(getActivity(), PreferenceUtils.getNotificationToneUri(getActivity())));

        Preference pref_notification_frequency = findPreference(PreferenceUtils.KEY_NOTIFICATION_REPEAT_COUNT);
        pref_notification_frequency.setSummary(Util.getFreqencySummery(getActivity()));


    }

    @Override
    public void onPause() {
        super.onPause();
        Logger.d(TAG, "onPause()");
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Logger.d(TAG, "onSharedPreferenceChanged " + key);
        if (key.equalsIgnoreCase(PreferenceUtils.KEY_ENABLE)) {
            boolean enable = sharedPreferences.getBoolean(key, true);
            BatteryAlertManager manager = BatteryAlertManager.getInstance();
            if (enable) {
                manager.enable();
            } else {
                manager.disable();
            }
            return;
        }

        if (key.equalsIgnoreCase(PreferenceUtils.KEY_USB_ENABLE)) {
            /*This block will be entered if the the "Enable Battery Alarm" is enabled.
            So here we just need to tell to BatteryAlertManager that usb preference is changed.*/
            BatteryAlertManager.getInstance().enable();
            return;
        }

        //setRingtonePreferenceSummary();

    }


}
