package com.android.settings.AOSPAL;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.UserHandle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.preference.PreferenceCategory;
import android.preference.Preference.OnPreferenceChangeListener;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;

import com.android.internal.util.paranoid.DeviceUtils;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class SystemSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String TAG = "SystemSettings";
    private static final String SYSTEM_SETTINGS = "system_settings";

    private static final String KEY_DUAL_PANEL = "force_dualpanel";
    private static final String KEY_REVERSE_DEFAULT_APP_PICKER = "reverse_default_app_picker";
    private static final String RECENT_MENU_CLEAR_ALL = "recent_menu_clear_all";
    private static final String RECENT_MENU_CLEAR_ALL_LOCATION = "recent_menu_clear_all_location";
    private static final String TELO_RADIO_SETTINGS = "telo_radio_settings";

    private ListPreference mRecentClearAllPosition;
    private CheckBoxPreference mRecentClearAll;
    private CheckBoxPreference mDualPanel;
    private CheckBoxPreference mReverseDefaultAppPicker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.system_settings);

        final ContentResolver resolver = getActivity().getContentResolver();
        PreferenceScreen prefs = getPreferenceScreen();

        PreferenceScreen systemSettings = (PreferenceScreen) findPreference(SYSTEM_SETTINGS);

        if (!DeviceUtils.isPhone(getActivity())) {
            systemSettings.removePreference(findPreference(TELO_RADIO_SETTINGS));
        }

        mDualPanel = (CheckBoxPreference) findPreference(KEY_DUAL_PANEL);
        mDualPanel.setChecked(Settings.System.getBoolean(getContentResolver(), Settings.System.FORCE_DUAL_PANEL, false));

        mReverseDefaultAppPicker = (CheckBoxPreference) findPreference(KEY_REVERSE_DEFAULT_APP_PICKER);
        mReverseDefaultAppPicker.setChecked(Settings.System.getInt(getContentResolver(),
                    Settings.System.REVERSE_DEFAULT_APP_PICKER, 0) != 0);

        mRecentClearAll = (CheckBoxPreference) prefs.findPreference(RECENT_MENU_CLEAR_ALL);
        mRecentClearAll.setChecked(Settings.System.getInt(getContentResolver(),
            Settings.System.SHOW_CLEAR_RECENTS_BUTTON, 1) == 1);
        mRecentClearAll.setOnPreferenceChangeListener(this);
        mRecentClearAllPosition = (ListPreference) prefs.findPreference(RECENT_MENU_CLEAR_ALL_LOCATION);
        String recentClearAllPosition = Settings.System.getString(getContentResolver(), Settings.System.CLEAR_RECENTS_BUTTON_LOCATION);
        if (recentClearAllPosition != null) {
             mRecentClearAllPosition.setValue(recentClearAllPosition);
        }
        mRecentClearAllPosition.setOnPreferenceChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mRecentClearAll) {
            Settings.System.putInt(getContentResolver(), Settings.System.SHOW_CLEAR_RECENTS_BUTTON,
                    (Boolean) newValue ? 1 : 0);
            return true;
        } else if (preference == mRecentClearAllPosition) {
            Settings.System.putString(getContentResolver(), Settings.System.CLEAR_RECENTS_BUTTON_LOCATION, (String) newValue);
            return true;
        } else {
        return false;
        }
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mDualPanel) {
            Settings.System.putBoolean(getContentResolver(), Settings.System.FORCE_DUAL_PANEL, ((CheckBoxPreference) preference).isChecked());
        } else if (preference == mReverseDefaultAppPicker) {
            Settings.System.putInt(getContentResolver(), Settings.System.REVERSE_DEFAULT_APP_PICKER,
                    mReverseDefaultAppPicker.isChecked() ? 1 : 0);
        }else {
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }
        return true;
    }
}
