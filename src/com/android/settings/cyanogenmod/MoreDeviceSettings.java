/*
 * Copyright (C) 2013 The CyanogenMod project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.cyanogenmod;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceGroup;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.preference.CheckBoxPreference;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import com.android.settings.hardware.DisplayColor;
import com.android.settings.hardware.DisplayGamma;
import com.android.settings.hardware.VibratorIntensity;

public class MoreDeviceSettings extends SettingsPreferenceFragment {
    private static final String TAG = "MoreDeviceSettings";

    private static final String KEY_SENORS_MOTORS_CATEGORY = "sensors_motors_category";
    private static final String KEY_DISPLAY_CALIBRATION_CATEGORY = "display_calibration_category";
    private static final String KEY_DISPLAY_COLOR = "color_calibration";
    private static final String KEY_DISPLAY_GAMMA = "gamma_tuning";
    private static final String KEY_SCREEN_GESTURE_SETTINGS = "touch_screen_gesture_settings";
    private static final String KEY_DOUBLE_TAP_SLEEP_GESTURE = "double_tap_sleep_gesture";

    private CheckBoxPreference mDTS;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.more_device_settings);
        ContentResolver resolver = getContentResolver();

        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (!VibratorIntensity.isSupported() || vibrator == null || !vibrator.hasVibrator()) {
            removePreference(KEY_SENORS_MOTORS_CATEGORY);
        }

        final PreferenceGroup calibrationCategory =
                (PreferenceGroup) findPreference(KEY_DISPLAY_CALIBRATION_CATEGORY);

        if (!DisplayColor.isSupported() && !DisplayGamma.isSupported()) {
            getPreferenceScreen().removePreference(calibrationCategory);
        } else {
            if (!DisplayColor.isSupported()) {
                calibrationCategory.removePreference(findPreference(KEY_DISPLAY_COLOR));
            }
            if (!DisplayGamma.isSupported()) {
                calibrationCategory.removePreference(findPreference(KEY_DISPLAY_GAMMA));
            }
        }

        Utils.updatePreferenceToSpecificActivityFromMetaDataOrRemove(getActivity(),
                getPreferenceScreen(), KEY_SCREEN_GESTURE_SETTINGS);
                
        mDTS = (CheckBoxPreference) findPreference(KEY_DOUBLE_TAP_SLEEP_GESTURE);
        mDTS.setChecked(Settings.System.getInt(getContentResolver(),
                              Settings.System.DOUBLE_TAP_SLEEP_GESTURE, 0) == 1); 
    }

    @Override
     public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
         ContentResolver cr = getActivity().getContentResolver();
         if (preference == mDTS) {
              Settings.System.putInt(cr, Settings.System.DOUBLE_TAP_SLEEP_GESTURE,
                     mDTS.isChecked() ? 1 : 0);
       } else {
         return super.onPreferenceTreeClick(preferenceScreen, preference);
       }
         return true;

    }
}