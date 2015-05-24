package com.devsaki.fakkudroid;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.devsaki.fakkudroid.database.FakkuDroidDB;
import com.devsaki.fakkudroid.database.domains.Attribute;
import com.devsaki.fakkudroid.database.domains.Content;
import com.devsaki.fakkudroid.database.enums.AttributeType;
import com.devsaki.fakkudroid.util.AndroidHelper;
import com.devsaki.fakkudroid.util.Constants;
import com.devsaki.fakkudroid.util.Helper;
import com.devsaki.fakkudroid.v2.bean.DoujinBean;
import com.devsaki.fakkudroid.v2.bean.URLBean;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ImporterActivity extends ActionBarActivity {

    private static final String TAG = ImporterActivity.class.getName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_importer);

        AndroidHelper.executeAsyncTask(new ImporterAsyncTask());
    }

    class ImporterAsyncTask extends AsyncTask<Integer,String,List<Content>>{

        private File downloadDir;
        private int currentPercent;
        private DonutProgress donutProgress;
        private TextView tvCurrentStatus;
        private FakkuDroidDB fakkuDroidDB;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            downloadDir = Helper.getDownloadDir("", ImporterActivity.this);

            donutProgress = (DonutProgress) findViewById(R.id.donut_progress);
            tvCurrentStatus = (TextView) findViewById(R.id.tvCurrentStatus);
            fakkuDroidDB = new FakkuDroidDB(ImporterActivity.this);
        }

        @Override
        protected void onPostExecute(List<Content> contents) {
            fakkuDroidDB.insertContents(contents.toArray(new Content[contents.size()]));
            Intent intent = new Intent(ImporterActivity.this, ContentListActivity.class);
            startActivity(intent);
            finish();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            donutProgress.setProgress(currentPercent);
            tvCurrentStatus.setText(values[0]);
        }

        @Override
        protected List<Content> doInBackground(Integer... params) {
            List<Content> contents = null;
            File[] files = downloadDir.listFiles();
            int processeds = 0;
            if(files.length>0){
                contents = new ArrayList<>();
                for(File file : files){
                    processeds++;
                    currentPercent = (int) (processeds*100.0/files.length);
                    if(file.isDirectory()){
                        publishProgress(file.getName());
                        File json = new File(file, Constants.JSON_FILE_NAME);
                        if(json.exists()){
                            try {
                                Content content = new Gson().fromJson(Helper.readTextFile(json), Content.class);
                                if(content.getStatus() != com.devsaki.fakkudroid.database.enums.Status.DOWNLOADED)
                                    content.setStatus(com.devsaki.fakkudroid.database.enums.Status.MIGRATED);
                                contents.add(content);
                            } catch (IOException e) {
                                Log.e(TAG, "Reading json file", e);
                            }
                        }else{
                            json = new File(file, Constants.JSON_FILE_NAME_V2);
                            if(json.exists()){
                                try {
                                    DoujinBean doujinBean = new Gson().fromJson(Helper.readTextFile(json), DoujinBean.class);
                                    Content content = new Content();
                                    content.setUrl(doujinBean.getId());
                                    content.setHtmlDescription(doujinBean.getDescription());
                                    content.setTitle(doujinBean.getTitle());
                                    content.setSerie(from(doujinBean.getSerie(), AttributeType.SERIE));
                                    Attribute artist = from(doujinBean.getArtist(), AttributeType.ARTIST);
                                    List<Attribute> artists = null;
                                    if(artist!=null){
                                        artists = new ArrayList<>(1);
                                        artists.add(artist);
                                    }
                                    content.setCoverImageUrl(doujinBean.getUrlImageTitle());
                                    content.setQtyPages(doujinBean.getQtyPages());
                                    content.setArtists(artists);
                                    Attribute translator = from(doujinBean.getTranslator(), AttributeType.TRANSLATOR);
                                    List<Attribute> translators = null;
                                    if(translator!=null){
                                        translators = new ArrayList<>(1);
                                        translators.add(translator);
                                    }
                                    content.setTranslators(translators);
                                    content.setTags(from(doujinBean.getLstTags(), AttributeType.TAG));
                                    content.setLanguage(from(doujinBean.getLanguage(), AttributeType.LANGUAGE));

                                    content.setStatus(com.devsaki.fakkudroid.database.enums.Status.MIGRATED);
                                    content.setDownloadDate(new Date().getTime());
                                    contents.add(content);
                                } catch (IOException e) {
                                    Log.e(TAG, "Reading json file v2", e);
                                }
                            }
                        }
                    }
                }
            }
            return contents;
        }

        private List<Attribute> from(List<URLBean> urlBeans, AttributeType type){
            List<Attribute> attributes = null;
            if(urlBeans==null)
                return null;
            if(urlBeans.size()>0){
                attributes = new ArrayList<>();
                for (URLBean urlBean : urlBeans){
                    Attribute attribute = from(urlBean, type);
                    if(attribute!=null)
                        attributes.add(attribute);
                }
            }
            return attributes;
        }

        private Attribute from(URLBean urlBean, AttributeType type){
            if(urlBean == null){
                return null;
            }
            try{
                Attribute attribute = new Attribute();
                attribute.setName(urlBean.getDescription());
                attribute.setUrl(urlBean.getId());
                attribute.setType(type);
                return attribute;
            }catch (Exception ex){
                Log.e(TAG, "Parsing urlBean to attribute" ,ex);
                return null;
            }
        }
    }
}
