package com.devsaki.fakkudroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.devsaki.fakkudroid.adapters.ContentDownloadManagerAdapter;
import com.devsaki.fakkudroid.database.FakkuDroidDB;
import com.devsaki.fakkudroid.database.domains.Content;
import com.devsaki.fakkudroid.database.enums.AttributeType;
import com.devsaki.fakkudroid.database.enums.Status;
import com.devsaki.fakkudroid.service.DownloadManagerService;

import java.util.ArrayList;
import java.util.List;


public class DownloadManagerActivity extends ActionBarActivity {

    private static final String TAG = DownloadManagerActivity.class.getName();
    private FakkuDroidDB db;
    private List<Content> contents;

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                double percent = bundle.getDouble(DownloadManagerService.INTENT_PERCENT_BROADCAST);
                if(percent>=0){
                    updatePercent(percent);
                }else{
                    update();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_manager);
        db = new FakkuDroidDB(this);
        ImageButton btnDownloads = (ImageButton) findViewById(R.id.btnDownloads);
        btnDownloads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainActivity = new Intent(DownloadManagerActivity.this, ContentListActivity.class);
                startActivity(mainActivity);
            }
        });
        ImageButton btnBrowser = (ImageButton) findViewById(R.id.btnBrowser);
        btnBrowser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DownloadManagerActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        ImageButton btnStart = (ImageButton) findViewById(R.id.btnStart);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.updateContentStatus(Status.DOWNLOADING, Status.PAUSED);
                update();
                Intent intent = new Intent(Intent.ACTION_SYNC, null, DownloadManagerActivity.this, DownloadManagerService.class);
                startService(intent);
            }
        });
        ImageButton btnPause = (ImageButton) findViewById(R.id.btnPause);
        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.updateContentStatus(Status.PAUSED, Status.DOWNLOADING);
                DownloadManagerService.paused = true;
                update();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_download_manager, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        update();
        registerReceiver(receiver, new IntentFilter(DownloadManagerService.NOTIFICATION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, PreferencesActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void resume(Content content){
        content.setStatus(Status.DOWNLOADING);
        db.updateContentStatus(content);
        update();
        if(content.getId()==contents.get(0).getId()){
            Intent intent = new Intent(Intent.ACTION_SYNC, null, DownloadManagerActivity.this, DownloadManagerService.class);
            startService(intent);
        }
    }

    public void pause(Content content){
        content.setStatus(Status.PAUSED);
        db.updateContentStatus(content);
        update();
        if(content.getId()==contents.get(0).getId()){
            DownloadManagerService.paused = true;
        }
    }

    public void cancel(Content content){
        content.setStatus(Status.SAVED);
        db.updateContentStatus(content);
        if(content.getId()==contents.get(0).getId()){
            DownloadManagerService.paused = true;
        }
        contents.remove(content);
    }

    public void updatePercent(double percent){
        if(contents!=null&&!contents.isEmpty()){
            contents.get(0).setPercent(percent);
            ((ArrayAdapter<Content>)getListAdapter()).notifyDataSetChanged();
        }
    }

    public void update(){
        contents = (List<Content>) db.selectContentInDownloadManager();
        if (contents == null) {
            contents = new ArrayList<>();
        }
        ContentDownloadManagerAdapter adapter = new ContentDownloadManagerAdapter(this, contents);
        setListAdapter(adapter);
    }

    private ListView mListView;

    private ListView getListView() {
        if (mListView == null) {
            mListView = (ListView) findViewById(R.id.list);
        }
        return mListView;
    }

    private void setListAdapter(ListAdapter adapter) {
        getListView().setAdapter(adapter);
    }

    private ListAdapter getListAdapter() {
        ListAdapter adapter = getListView().getAdapter();
        if (adapter instanceof HeaderViewListAdapter) {
            return ((HeaderViewListAdapter) adapter).getWrappedAdapter();
        } else {
            return adapter;
        }
    }
}
