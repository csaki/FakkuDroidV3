package com.devsaki.fakkudroid.components;

import android.app.ListFragment;
import android.content.SharedPreferences;
import android.view.View;

import com.devsaki.fakkudroid.R;
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

    public void showLoading(){
        getView().findViewById(R.id.content_main).setVisibility(View.GONE);
        getView().findViewById(R.id.content_loading).setVisibility(View.VISIBLE);
    }

    public void hideLoading(){
        getView().findViewById(R.id.content_loading).setVisibility(View.GONE);
        getView().findViewById(R.id.content_main).setVisibility(View.VISIBLE);
    }
}
