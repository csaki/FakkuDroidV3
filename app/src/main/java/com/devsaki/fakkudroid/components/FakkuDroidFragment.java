package com.devsaki.fakkudroid.components;

import android.app.ListFragment;
import android.content.SharedPreferences;

import com.devsaki.fakkudroid.database.FakkuDroidDB;

/**
 * Created by neko on 06/06/2015.
 */

public abstract class FakkuDroidFragment extends ListFragment {

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
