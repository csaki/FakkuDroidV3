package com.devsaki.fakkudroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.devsaki.fakkudroid.adapters.ContentDownloadManagerAdapter;
import com.devsaki.fakkudroid.components.FakkuDroidActivity;
import com.devsaki.fakkudroid.components.FakkuDroidFragment;
import com.devsaki.fakkudroid.database.domains.Content;
import com.devsaki.fakkudroid.database.enums.Status;
import com.devsaki.fakkudroid.service.DownloadManagerService;

import java.util.ArrayList;
import java.util.List;


public class DownloadManagerActivity extends FakkuDroidActivity<DownloadManagerActivity.DownloadManagerFragment> {

    private static final String TAG = DownloadManagerActivity.class.getName();

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                double percent = bundle.getDouble(DownloadManagerService.INTENT_PERCENT_BROADCAST);
                if(percent>=0){
                    getFragment().updatePercent(percent);
                }else{
                    getFragment().update();
                }
            }
        }
    };

    @Override
    protected DownloadManagerFragment buildFragment() {
        return new DownloadManagerFragment();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getFragment().update();
        registerReceiver(receiver, new IntentFilter(DownloadManagerService.NOTIFICATION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    public static class DownloadManagerFragment extends FakkuDroidFragment{

        private ListView mListView;
        private List<Content> contents;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_download_manager, container, false);
            mListView = (ListView) rootView.findViewById(R.id.list);

            ImageButton btnStart = (ImageButton)rootView.findViewById(R.id.btnStart);
            btnStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getDB().updateContentStatus(Status.DOWNLOADING, Status.PAUSED);
                    update();
                    Intent intent = new Intent(Intent.ACTION_SYNC, null, getActivity(), DownloadManagerService.class);
                    getActivity().startService(intent);
                }
            });
            ImageButton btnPause = (ImageButton) rootView.findViewById(R.id.btnPause);
            btnPause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getDB().updateContentStatus(Status.PAUSED, Status.DOWNLOADING);
                    DownloadManagerService.paused = true;
                    update();
                }
            });
            return super.onCreateView(inflater, container, savedInstanceState);
        }



        public void resume(Content content){
            content.setStatus(Status.DOWNLOADING);
            getDB().updateContentStatus(content);
            update();
            if(content.getId()==contents.get(0).getId()){
                Intent intent = new Intent(Intent.ACTION_SYNC, null, getActivity(), DownloadManagerService.class);
                getActivity().startService(intent);
            }
        }

        public void pause(Content content){
            content.setStatus(Status.PAUSED);
            getDB().updateContentStatus(content);
            update();
            if(content.getId()==contents.get(0).getId()){
                DownloadManagerService.paused = true;
            }
        }

        public void cancel(Content content){
            content.setStatus(Status.SAVED);
            getDB().updateContentStatus(content);
            if(content.getId()==contents.get(0).getId()){
                DownloadManagerService.paused = true;
            }
            contents.remove(content);
        }

        public void updatePercent(double percent){
            if(contents!=null&&!contents.isEmpty()){
                contents.get(0).setPercent(percent);
                ((ArrayAdapter<Content>)mListView.getAdapter()).notifyDataSetChanged();
            }
        }

        public void update(){
            contents = getDB().selectContentInDownloadManager();
            if (contents == null) {
                contents = new ArrayList<>();
            }
            ContentDownloadManagerAdapter adapter = new ContentDownloadManagerAdapter(getActivity(), contents);
            mListView.setAdapter(adapter);
        }
    }
}
