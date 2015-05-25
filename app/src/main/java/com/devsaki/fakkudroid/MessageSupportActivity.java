package com.devsaki.fakkudroid;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import com.devsaki.fakkudroid.util.ConstantsPreferences;

public class MessageSupportActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_support);

        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        prefs.edit().putBoolean(ConstantsPreferences.SHOW_MESSAGE_SUPPORT, false).commit();

        WebView wvHelp = (WebView) findViewById(R.id.wbSupport);
        wvHelp.loadDataWithBaseURL(null, getResources().getString(R.string.help_to_fakkudroid),"text/html", "utf-8",null);
    }

    public void close(View view) {
        finish();
    }
}
