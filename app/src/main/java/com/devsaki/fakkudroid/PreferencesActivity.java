package com.devsaki.fakkudroid;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.devsaki.fakkudroid.util.Constants;

import java.io.File;
import java.io.IOException;

/**
 * Created by DevSaki on 20/05/2015.
 */
public class PreferencesActivity extends PreferenceActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
    }

    public static class MyPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            Preference addNoMediaFile = (Preference) getPreferenceScreen().findPreference("pref_add_no_media_file");
            addNoMediaFile.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                @Override
                public boolean onPreferenceClick(Preference preference) {
                    SharedPreferences prefs = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    String settingDir = prefs.getString(Constants.SETTINGS_FAKKUDROID_FOLDER, "");
                    File nomedia = new File(settingDir, ".nomedia");
                    try {
                        nomedia.createNewFile();
                    } catch (IOException e) {
                    }
                    Toast.makeText(getActivity(), R.string.nomedia_file_created, Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        }
    }
}
