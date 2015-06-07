package com.devsaki.fakkudroid;

import android.app.SearchManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Toast;

import com.devsaki.fakkudroid.adapters.ContentAdapter;
import com.devsaki.fakkudroid.components.FakkuDroidActivity;
import com.devsaki.fakkudroid.components.FakkuDroidFragment;
import com.devsaki.fakkudroid.database.domains.Content;
import com.devsaki.fakkudroid.util.ConstantsPreferences;

import java.util.ArrayList;
import java.util.List;


public class ContentListActivity extends FakkuDroidActivity<ContentListActivity.ContentListFragment> {

    private static final String TAG = ContentListActivity.class.getName();

    @Override
    protected ContentListFragment buildFragment() {
        return new ContentListFragment();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_content_list, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView =
                (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(true);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return true;
            }
        });
        return true;
    }

    public static class ContentListFragment extends FakkuDroidFragment{
        private static String url = "";
        private int currentPage = 1;
        private List<Content> contents;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_content_list, container, false);
            btnPage = (Button) rootView.findViewById(R.id.btnPage);

            ImageButton btnRefresh = (ImageButton) rootView.findViewById(R.id.btnRefresh);
            btnRefresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadContent();
                }
            });
            ImageButton btnNext = (ImageButton) rootView.findViewById(R.id.btnNext);
            btnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentPage++;
                    loadContent();
                }
            });
            ImageButton btnPrevius = (ImageButton) rootView.findViewById(R.id.btnPrevius);
            btnPrevius.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentPage > 1) {
                        currentPage--;
                        loadContent();
                    } else {
                        Toast.makeText(getActivity(), R.string.not_previus_page, Toast.LENGTH_SHORT).show();
                    }
                }
            });
            return rootView;
        }

        private void loadContent() {


            btnPage.setText("" + currentPage);
        }
        private Button btnPage;
    }

    private class LoadContentTask extends AsyncTask<String, Integer, List<Content>>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<Content> doInBackground(String... params) {
            return null;
        }

        @Override
        protected void onPostExecute(List<Content> contents) {
            super.onPostExecute(contents);
            ContentAdapter adapter = new ContentAdapter(ContentListActivity.this, contents);
            getFragment().setListAdapter(adapter);
        }
    }
}
