package com.android.settings.AOSPAL;

import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.preference.PreferenceCategory;
import android.preference.Preference.OnPreferenceChangeListener;

import com.android.internal.util.paranoid.DeviceUtils;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class SystemSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String SYSTEM_SETTINGS = "system_settings";

    private static final String KEY_REVERSE_DEFAULT_APP_PICKER = "reverse_default_app_picker";
    private static final String TELO_RADIO_SETTINGS = "telo_radio_settings";
    private static final String RECENT_MENU_CLEAR_ALL_LOCATION = "recent_menu_clear_all_location";
    private static final String INACCURATE_PROXIMITY_SENSOR = "inaccurate_proximity_sensor";
    private static final String GENERAL_CATEGORY = "general_category";

    private CheckBoxPreference mReverseDefaultAppPicker;
    private ListPreference mRecentClearAllPosition;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.system_settings);

        final ContentResolver resolver = getActivity().getContentResolver();
        PreferenceScreen prefs = getPreferenceScreen();

        PreferenceScreen systemSettings = (PreferenceScreen) findPreference(SYSTEM_SETTINGS);
        PreferenceCategory generalCategory = (PreferenceCategory) findPreference(GENERAL_CATEGORY);

        mReverseDefaultAppPicker = (CheckBoxPreference) findPreference(KEY_REVERSE_DEFAULT_APP_PICKER);
        mReverseDefaultAppPicker.setChecked(Settings.System.getInt(getContentResolver(),
                    Settings.System.REVERSE_DEFAULT_APP_PICKER, 0) != 0);

        mRecentClearAllPosition = (ListPreference) prefs.findPreference(RECENT_MENU_CLEAR_ALL_LOCATION);
        String recentClearAllPosition = Settings.System.getString(resolver, Settings.System.CLEAR_RECENTS_BUTTON_LOCATION);
        if (recentClearAllPosition != null) {
             mRecentClearAllPosition.setValue(recentClearAllPosition);
        }
        mRecentClearAllPosition.setOnPreferenceChangeListener(this);

        boolean hasProximitySensor = getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_PROXIMITY);
        boolean hasTelephony = getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY);
        if (!hasTelephony) {
            systemSettings.removePreference(findPreference(TELO_RADIO_SETTINGS));
        } else if (!hasProximitySensor) {
            generalCategory.removePreference(findPreference(INACCURATE_PROXIMITY_SENSOR));
        }
        updateClearAllButtonPositionOptions();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateClearAllButtonPositionOptions();
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mRecentClearAllPosition) {
            String value = (String) objValue;
            Settings.System.putString(resolver, Settings.System.CLEAR_RECENTS_BUTTON_LOCATION, value);
        } else {
            return false;
        }
        return true;
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mReverseDefaultAppPicker) {
            Settings.System.putInt(getContentResolver(), Settings.System.REVERSE_DEFAULT_APP_PICKER,
                    mReverseDefaultAppPicker.isChecked() ? 1 : 0);
        }else {
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }
        return true;
    }

    public void updateClearAllButtonPositionOptions() {
        boolean alternativeClearRecentsAllEnabled = Settings.System.getInt(getActivity().getContentResolver(),
               Settings.System.ALTERNATIVE_RECENTS_CLEAR_ALL, 0) == 1;
        if (alternativeClearRecentsAllEnabled) {
            mRecentClearAllPosition.setEnabled(true);
            mRecentClearAllPosition.setSummary(R.string.recent_clear_all_button_location_summary);
        } else {
            mRecentClearAllPosition.setEnabled(false);
            mRecentClearAllPosition.setSummary(R.string.recent_clear_all_disabled);
        }
    }
}
