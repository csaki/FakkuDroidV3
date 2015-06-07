package com.devsaki.fakkudroid.components;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Build;

import com.devsaki.fakkudroid.database.FakkuDroidDB;

/**
 * Created by neko on 06/06/2015.
 */

public abstract class FakkuDroidFragment extends Fragment {

    protected SharedPreferences getSharedPreferences(){
        return ((FakkuDroidActivity) getActivity()).getSharedPreferences();
    }

    protected FakkuDroidDB getDB(){
        return ((FakkuDroidActivity) getActivity()).getDB();
    }

    protected FakkuDroidActivity getFakkuDroidActivity(){
        return (FakkuDroidActivity) getActivity();
    }
}
