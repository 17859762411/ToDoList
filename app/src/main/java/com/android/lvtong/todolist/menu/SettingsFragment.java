package com.android.lvtong.todolist.menu;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.android.lvtong.todolist.R;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.pref_visualizer);
    }
}
