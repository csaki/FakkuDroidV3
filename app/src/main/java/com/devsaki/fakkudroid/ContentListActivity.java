package com.devsaki.fakkudroid;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import com.devsaki.fakkudroid.adapters.ContentAdapter;
import com.devsaki.fakkudroid.database.FakkuDroidDB;
import com.devsaki.fakkudroid.database.domains.Content;
import com.devsaki.fakkudroid.database.enums.AttributeType;

import java.util.ArrayList;
import java.util.List;


public class ContentListActivity extends ActionBarActivity {

    private static final String TAG = ContentListActivity.class.getName();
    private FakkuDroidDB db;
    private List<Content> contents;
    private static String query = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_list);

        db = new FakkuDroidDB(this);
        ImageButton btnBrowser = (ImageButton) findViewById(R.id.btnBrowser);
        btnBrowser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContentListActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        ImageButton btnRefresh = (ImageButton) findViewById(R.id.btnRefresh);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchContent();
            }
        });
        ImageButton btnDownloadManager = (ImageButton) findViewById(R.id.btnDownloadManager);
        btnDownloadManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContentListActivity.this, DownloadManagerActivity.class);
                startActivity(intent);
            }
        });
        searchContent();
    }

    private void searchContent() {
        contents = (List<Content>) db.selectContentByQuery(query);
        if (contents != null) {
            for (Content content : contents) {
                content.setArtists(db.selectAttributesByContentId(content.getId(), AttributeType.ARTIST));
                content.setSerie(db.selectAttributeByContentId(content.getId(), AttributeType.SERIE));
                content.setTags(db.selectAttributesByContentId(content.getId(), AttributeType.TAG));
                content.setImageFiles(db.selectImageFilesByContentId(content.getId()));
            }
        }else{
            contents = new ArrayList<>();
        }
        ContentAdapter adapter = new ContentAdapter(this, contents);
        setListAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_content_list, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                query = s;
                searchContent();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                query = s;
                searchContent();
                return true;
            }
        });
        return true;
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
