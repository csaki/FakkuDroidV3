package com.devsaki.fakkudroid;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.devsaki.fakkudroid.util.Constants;
import com.devsaki.fakkudroid.util.Helper;

import net.rdrei.android.dirchooser.DirectoryChooserFragment;

import java.io.File;
import java.io.IOException;


public class SelectFolderActivity extends ActionBarActivity implements
        DirectoryChooserFragment.OnFragmentInteractionListener {

    private DirectoryChooserFragment mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_folder);

        mDialog = DirectoryChooserFragment.newInstance("DialogSample", null);

        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        String settingDir = prefs.getString(Constants.SETTINGS_FAKKUDROID_FOLDER, "");
        if (!settingDir.isEmpty()) {
            EditText editText = (EditText) findViewById(R.id.etFolder);
            editText.setText(settingDir);
        } else {
            selectDefault(null);
        }
    }

    public void explore(View view) {
        mDialog.show(getFragmentManager(), null);
    }

    public void selectDefault(View view) {
        EditText editText = (EditText) findViewById(R.id.etFolder);
        editText.setText(Helper.getDefaultDir("", this).getAbsolutePath());
    }

    public void save(View view) {
        EditText editText = (EditText) findViewById(R.id.etFolder);
        String fakkuFolder = editText.getText().toString();

        //Validation folder
        File file = new File(fakkuFolder);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                Toast.makeText(this, R.string.error_creating_folder, Toast.LENGTH_SHORT).show();
                return;
            }
        }
        File nomedia = new File(fakkuFolder, ".nomedia");
        try {
            nomedia.createNewFile();
        } catch (IOException e) {
        }
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.SETTINGS_FAKKUDROID_FOLDER, fakkuFolder);
        editor.commit();

        if (Helper.getDownloadDir("", this).listFiles().length > 0) {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(R.string.app_name)
                    .setMessage(R.string.detect_contents)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(SelectFolderActivity.this, ImporterActivity.class);
                            startActivity(intent);
                            finish();
                        }

                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }

                    })
                    .show();
        } else {
            Intent intent = new Intent(this, DownloadsActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onSelectDirectory(@NonNull String s) {
        EditText editText = (EditText) findViewById(R.id.etFolder);
        editText.setText(s);
        mDialog.dismiss();
    }

    @Override
    public void onCancelChooser() {
        mDialog.dismiss();
    }
}
