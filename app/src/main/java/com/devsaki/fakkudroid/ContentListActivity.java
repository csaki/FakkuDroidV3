package com.devsaki.fakkudroid;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.devsaki.fakkudroid.adapters.ContentAdapter;
import com.devsaki.fakkudroid.database.FakkuDroidDB;
import com.devsaki.fakkudroid.database.domains.Content;
import com.devsaki.fakkudroid.database.enums.AttributeType;
import com.devsaki.fakkudroid.util.Constants;
import com.devsaki.fakkudroid.util.ConstantsPreferences;

import java.util.ArrayList;
import java.util.List;


public class ContentListActivity extends ActionBarActivity {

    private static final String TAG = ContentListActivity.class.getName();
    private FakkuDroidDB db;
    private List<Content> contents;
    private static String query = "";
    private SharedPreferences sharedPreferences;
    private int currentPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_list);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
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
        ImageButton btnNext = (ImageButton) findViewById(R.id.btnNext);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qtyPages = Integer.parseInt(sharedPreferences.getString(ConstantsPreferences.PREF_QUANTITY_PER_PAGE_LISTS, ConstantsPreferences.PREF_QUANTITY_PER_PAGE_DEFAULT + ""));
                if (qtyPages <= 0) {
                    Toast.makeText(ContentListActivity.this, R.string.not_limit_per_page, Toast.LENGTH_SHORT).show();
                } else {
                    currentPage++;
                    searchContent();
                }
            }
        });
        ImageButton btnPrevius = (ImageButton) findViewById(R.id.btnPrevius);
        btnPrevius.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qtyPages = Integer.parseInt(sharedPreferences.getString(ConstantsPreferences.PREF_QUANTITY_PER_PAGE_LISTS, ConstantsPreferences.PREF_QUANTITY_PER_PAGE_DEFAULT + ""));
                if(qtyPages<=0){
                    Toast.makeText(ContentListActivity.this, R.string.not_limit_per_page, Toast.LENGTH_SHORT ).show();
                }else{
                    if(currentPage>1){
                        currentPage--;
                        searchContent();
                    }else{
                        Toast.makeText(ContentListActivity.this, R.string.not_limit_per_page, Toast.LENGTH_SHORT ).show();
                    }
                }
            }
        });
        String settingDir = sharedPreferences.getString(Constants.SETTINGS_FAKKUDROID_FOLDER, "");
        if (settingDir.isEmpty()) {
            Intent intent = new Intent(this, SelectFolderActivity.class);
            startActivity(intent);
            finish();
        } else {
            searchContent();
        }
    }

    private void searchContent() {
        int qtyPages = Integer.parseInt(sharedPreferences.getString(ConstantsPreferences.PREF_QUANTITY_PER_PAGE_LISTS, ConstantsPreferences.PREF_QUANTITY_PER_PAGE_DEFAULT + ""));
        if(qtyPages>0){
            contents = (List<Content>) db.selectContentByQuery(query, currentPage, qtyPages);
        }else{
            contents = (List<Content>) db.selectContentByQuery(query);
        }
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

        Button btnPage = (Button) findViewById(R.id.btnPage);
        btnPage.setText("" + currentPage);
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
        searchView.requestFocusFromTouch();
        return true;
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
