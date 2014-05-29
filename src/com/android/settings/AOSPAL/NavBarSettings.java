package com.android.settings.AOSPAL;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.UserHandle;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.preference.PreferenceCategory;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.SeekBarPreference;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.Window;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.internal.util.paranoid.DeviceUtils;

import java.io.File;
import java.io.IOException;
import java.lang.CharSequence;
import java.lang.String;

import org.cyanogenmod.hardware.KeyDisabler;

public class NavBarSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String NAVBAR_SETTINGS = "navbar_settings";
    private static final String KEY_NAVIGATION_BAR_HEIGHT = "navigation_bar_height";
    private static final String NAVIGATION_BAR_CATEGORY = "navigation_bar";
    private static final String NAVIGATION_BAR_LEFT = "navigation_bar_left";
    private static final String NAVIGATION_BAR_INFO = "navigation_bar_info";
    private static final String ENABLE_NAVIGATION_BAR = "enable_nav_bar";
    private static final String CATEGORY_NAVBAR_GENERAL = "category_navbar_general";
    private static final String CATEGORY_NAVBAR_CONTROLS = "category_navbar_controls";

    private SeekBarPreference mNavigationBarHeight;
    private CheckBoxPreference mEnableNavigationBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.navbar_settings);

        mNavigationBarHeight = (SeekBarPreference) findPreference(KEY_NAVIGATION_BAR_HEIGHT);
        mNavigationBarHeight.setProgress((int)(Settings.System.getFloat(getContentResolver(),
                    Settings.System.NAVIGATION_BAR_HEIGHT, 1f) * 100));
        mNavigationBarHeight.setTitle(getResources().getText(R.string.navigation_bar_height) + " " + mNavigationBarHeight.getProgress() + "%");
        mNavigationBarHeight.setOnPreferenceChangeListener(this);

        boolean hasNavBarByDefault = getResources().getBoolean(
                com.android.internal.R.bool.config_showNavigationBar);
        boolean enableNavigationBar = Settings.System.getInt(getContentResolver(),
                Settings.System.DEV_FORCE_SHOW_NAVBAR, hasNavBarByDefault ? 1 : 0) == 1;
        mEnableNavigationBar = (CheckBoxPreference) findPreference(ENABLE_NAVIGATION_BAR);
        mEnableNavigationBar.setChecked(enableNavigationBar);
        mEnableNavigationBar.setOnPreferenceChangeListener(this);

        PreferenceScreen navbarSettings = (PreferenceScreen) findPreference(NAVBAR_SETTINGS);

        PreferenceCategory generalCategory = (PreferenceCategory) findPreference(CATEGORY_NAVBAR_GENERAL);

        if (!DeviceUtils.isPhone(getActivity())) {
            navbarSettings.removePreference(findPreference(CATEGORY_NAVBAR_CONTROLS));
        } else  if (KeyDisabler.isSupported() || hasNavBarByDefault == true) {
            generalCategory.removePreference(mEnableNavigationBar);
        } else if (!KeyDisabler.isSupported() || hasNavBarByDefault == true) {
            navbarSettings.removePreference(findPreference(NAVIGATION_BAR_INFO));
        }

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        final String key = preference.getKey();

        if (preference == mNavigationBarHeight) {
            Settings.System.putFloat(getActivity().getContentResolver(),
                    Settings.System.NAVIGATION_BAR_HEIGHT, (Integer)newValue / 100f);
            mNavigationBarHeight.setTitle(getResources().getText(R.string.navigation_bar_height) + " " + (Integer)newValue + "%");
        } else if (preference == mEnableNavigationBar) {
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.DEV_FORCE_SHOW_NAVBAR,
                    ((Boolean) newValue) ? 1 : 0);
            mNavigationBarHeight.setEnabled((Boolean)newValue);
        } else {
            return false;
        }
        return true;
    }
}
