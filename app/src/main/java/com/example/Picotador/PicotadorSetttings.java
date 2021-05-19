package com.example.Picotador;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

public class PicotadorSetttings extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

    }
}
