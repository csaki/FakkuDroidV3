package com.devsaki.fakkudroid;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.devsaki.fakkudroid.database.FakkuDroidDB;
import com.devsaki.fakkudroid.database.domains.Content;
import com.devsaki.fakkudroid.database.domains.ImageFile;
import com.devsaki.fakkudroid.database.enums.AttributeType;
import com.devsaki.fakkudroid.database.enums.Status;
import com.devsaki.fakkudroid.parser.FakkuParser;
import com.devsaki.fakkudroid.service.DownloadManagerService;
import com.devsaki.fakkudroid.util.Constants;
import com.devsaki.fakkudroid.util.Helper;
import com.melnykov.fab.FloatingActionButton;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = MainActivity.class.getName();

    public static final String INTENT_URL = "url";

    private FakkuDroidDB db;
    private Content currentContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final WebView webview = (WebView) findViewById(R.id.wbMain);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebViewClient(new CustomWebViewClient());
        webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                ProgressBar pb = (ProgressBar) findViewById(R.id.pbMain);
                pb.setProgress(newProgress);
            }
        });
        webview.addJavascriptInterface(new FakkuLoadListener(), "HTMLOUT");
        String intentVar = getIntent().getStringExtra(INTENT_URL);
        webview.loadUrl(intentVar==null? Constants.FAKKU_URL:intentVar);

        FloatingActionButton fabDownload = (FloatingActionButton) findViewById(R.id.fabDownload);
        fabDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadContent();
            }
        });
        fabDownload.hide(true);

        db = new FakkuDroidDB(MainActivity.this);

        FloatingActionButton fabDownloads = (FloatingActionButton) findViewById(R.id.fabDownloads);
        fabDownloads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainActivity = new Intent(MainActivity.this, ContentListActivity.class);
                startActivity(mainActivity);
            }
        });
        FloatingActionButton fabRefresh = (FloatingActionButton) findViewById(R.id.fabRefresh);
        fabRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webview.reload();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    private void downloadContent() {
        currentContent = db.selectContentById(currentContent.getId());
        if (Status.DOWNLOADED == currentContent.getStatus()) {
            Toast.makeText(this, R.string.already_downloaded, Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, R.string.in_queue, Toast.LENGTH_SHORT).show();
        currentContent.setDownloadDate(new Date().getTime());
        currentContent.setStatus(Status.DOWNLOADING);

        db.updateContentStatus(currentContent);
        Intent intent = new Intent(Intent.ACTION_SYNC, null, this, DownloadManagerService.class);
        startService(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    WebView webview = (WebView) findViewById(R.id.wbMain);
                    if (webview.canGoBack()) {
                        webview.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    class CustomWebViewClient extends WebViewClient {

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            try {
                URL u = new URL(url);
                if (u.getHost().equals("www.fakku.net")) {
                    view.loadUrl(url);
                    return false;
                } else {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                    return true;
                }
            } catch (MalformedURLException e) {
            }
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            FloatingActionButton fabDownload = (FloatingActionButton) findViewById(R.id.fabDownload);
            fabDownload.hide();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            URI uri = null;
            try {
                uri = new URI(url);
            } catch (URISyntaxException e) {
                Log.e(TAG, "Error reading current url form webview", e);
            }

            if (uri!=null) {
                String[] paths = uri.getPath().split("/");
                if(paths.length>=3){
                    if(paths[1].equals("doujinshi")||paths[1].equals("manga")){
                        if(paths.length==3||!paths[3].equals("read")){
                            view.loadUrl("javascript:window.HTMLOUT.processHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
                        }
                    }
                }
            }
        }
    }

    class FakkuLoadListener {

        @JavascriptInterface
        public void processHTML(String html) {
            if(html==null)
                return;
            Content content = FakkuParser.parseContent(html);
            if (content == null) {
                return;
            }
            Content contentbd = db.selectContentById(content.getUrl().hashCode());
            if (contentbd == null) {
                Log.i(TAG, "Saving content : " + content.getUrl());
                try {
                    content.setCoverImageUrl("http://" + content.getCoverImageUrl().substring(2));
                    db.insertContent(content);
                } catch (Exception e) {
                    Log.e(TAG, "Saving content", e);
                    return;
                }
            }else if(contentbd.getStatus()==Status.MIGRATED){
                content.setStatus(Status.DOWNLOADED);
                db.insertContent(content);
                //Save JSON file
                try {
                    File dir = Helper.getDownloadDir(content.getFakkuId(), MainActivity.this);
                    Helper.saveJson(content, dir);
                } catch (IOException e) {
                    Log.e(TAG, "Error Save JSON " + content.getTitle(), e);
                }
            } else {
                content.setStatus(contentbd.getStatus());
            }
            if (content.isDownloadable()&&content.getStatus()!=Status.DOWNLOADED) {
                currentContent = content;
                FloatingActionButton fabDownload = (FloatingActionButton) findViewById(R.id.fabDownload);
                fabDownload.show();
                WebView webview = (WebView) findViewById(R.id.wbMain);
                webview.stopLoading();
            }else {
                FloatingActionButton fabDownload = (FloatingActionButton) findViewById(R.id.fabDownload);
                fabDownload.hide();
            }
        }
    }
}
