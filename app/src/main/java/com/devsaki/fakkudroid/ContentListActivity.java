package com.devsaki.fakkudroid;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.devsaki.fakkudroid.adapters.ContentAdapter;
import com.devsaki.fakkudroid.database.FakkuDroidDB;
import com.devsaki.fakkudroid.database.domains.Content;
import com.devsaki.fakkudroid.util.Constants;
import com.devsaki.fakkudroid.util.ConstantsPreferences;

import java.util.ArrayList;
import java.util.List;


public class ContentListActivity extends FakkuDroidActivity {

    private static final String TAG = ContentListActivity.class.getName();
    private ContentListFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragment = new ContentListFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
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
                fragment.setQuery(s.trim());
                fragment.searchContent();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                fragment.setQuery(s.trim());
                fragment.searchContent();
                return true;
            }
        });
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
        }else if (id == R.id.action_order_alphabetic) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(ConstantsPreferences.PREF_ORDER_CONTENT_LISTS, ConstantsPreferences.PREF_ORDER_CONTENT_ALPHABETIC);
            editor.commit();
            fragment.searchContent();
            return true;
        }else if (id == R.id.action_order_by_date) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(ConstantsPreferences.PREF_ORDER_CONTENT_LISTS, ConstantsPreferences.PREF_ORDER_CONTENT_BY_DATE);
            editor.commit();
            fragment.searchContent();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class ContentListFragment extends Fragment{
        private static String query = "";
        private int currentPage = 1;
        private List<Content> contents;

        private FakkuDroidActivity getFakkuDroidActivity(){
            return (FakkuDroidActivity) getActivity();
        }

        public void setQuery(String query) {
            ContentListFragment.query = query;
            currentPage = 1;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_content_list, container, false);
            mListView = (ListView) rootView.findViewById(R.id.list);
            btnPage = (Button) rootView.findViewById(R.id.btnPage);

            ImageButton btnBrowser = (ImageButton) rootView.findViewById(R.id.btnBrowser);
            btnBrowser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                }
            });
            ImageButton btnRefresh = (ImageButton) rootView.findViewById(R.id.btnRefresh);
            btnRefresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    searchContent();
                }
            });
            ImageButton btnDownloadManager = (ImageButton) rootView.findViewById(R.id.btnDownloadManager);
            btnDownloadManager.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), DownloadManagerActivity.class);
                    startActivity(intent);
                }
            });
            ImageButton btnNext = (ImageButton) rootView.findViewById(R.id.btnNext);
            btnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int qtyPages = Integer.parseInt(getFakkuDroidActivity().sharedPreferences.getString(ConstantsPreferences.PREF_QUANTITY_PER_PAGE_LISTS, ConstantsPreferences.PREF_QUANTITY_PER_PAGE_DEFAULT + ""));
                    if (qtyPages <= 0) {
                        Toast.makeText(getActivity(), R.string.not_limit_per_page, Toast.LENGTH_SHORT).show();
                    } else {
                        currentPage++;
                        searchContent();
                    }
                }
            });
            ImageButton btnPrevius = (ImageButton) rootView.findViewById(R.id.btnPrevius);
            btnPrevius.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int qtyPages = Integer.parseInt(getFakkuDroidActivity().sharedPreferences.getString(ConstantsPreferences.PREF_QUANTITY_PER_PAGE_LISTS, ConstantsPreferences.PREF_QUANTITY_PER_PAGE_DEFAULT + ""));
                    if (qtyPages <= 0) {
                        Toast.makeText(getActivity(), R.string.not_limit_per_page, Toast.LENGTH_SHORT).show();
                    } else {
                        if (currentPage > 1) {
                            currentPage--;
                            searchContent();
                        } else {
                            Toast.makeText(getActivity(), R.string.not_limit_per_page, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
            String settingDir = getFakkuDroidActivity().sharedPreferences.getString(Constants.SETTINGS_FAKKUDROID_FOLDER, "");
            boolean showMessageSupport = getFakkuDroidActivity().sharedPreferences.getBoolean(ConstantsPreferences.SHOW_MESSAGE_SUPPORT, true);
            if (settingDir.isEmpty()) {
                Intent intent = new Intent(getActivity(), SelectFolderActivity.class);
                startActivity(intent);
                getActivity().finish();
            } else {
                searchContent();
                if (showMessageSupport) {
                    Intent intent = new Intent(getActivity(), MessageSupportActivity.class);
                    startActivity(intent);
                }
            }
            return rootView;
        }

        private void searchContent() {
            int order = getFakkuDroidActivity().sharedPreferences.getInt(ConstantsPreferences.PREF_ORDER_CONTENT_LISTS, ConstantsPreferences.PREF_ORDER_CONTENT_BY_DATE);
            int qtyPages = Integer.parseInt(getFakkuDroidActivity().sharedPreferences.getString(ConstantsPreferences.PREF_QUANTITY_PER_PAGE_LISTS, ConstantsPreferences.PREF_QUANTITY_PER_PAGE_DEFAULT + ""));
            if (qtyPages > 0) {
                contents = (List<Content>) getFakkuDroidActivity().db.selectContentByQuery(query, currentPage, qtyPages, order==ConstantsPreferences.PREF_ORDER_CONTENT_ALPHABETIC);
            } else {
                contents = (List<Content>) getFakkuDroidActivity().db.selectContentByQuery(query, order==ConstantsPreferences.PREF_ORDER_CONTENT_ALPHABETIC);
            }
            if (contents == null) {
                contents = new ArrayList<>();
            }
            if (query.isEmpty())
                getActivity().setTitle(R.string.app_name);
            else
                getActivity().setTitle(getResources().getString(R.string.title_activity_search).replace("@search", query));

            ContentAdapter adapter = new ContentAdapter(getActivity(), contents);
            setListAdapter(adapter);


            btnPage.setText("" + currentPage);
        }

        private ListView mListView;
        private Button btnPage;

        private void setListAdapter(ListAdapter adapter) {
            mListView.setAdapter(adapter);
        }
    }
}
