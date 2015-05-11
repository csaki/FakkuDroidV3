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
import com.melnykov.fab.FloatingActionButton;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = MainActivity.class.getName();
    private final String FAKKU_URL = "https://www.fakku.net";

    private FakkuDroidDB db;
    private Content currentContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WebView webview = (WebView) findViewById(R.id.wbMain);
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
        webview.loadUrl(FAKKU_URL);

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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void downloadContent() {

        if (Status.DOWNLOADING == currentContent.getStatus() && DownloadManagerService.isStarted()) {
            Toast.makeText(this, R.string.starting_download_manager, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Intent.ACTION_SYNC, null, this, DownloadManagerService.class);
            startService(intent);
            return;
        }
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
            if (url.startsWith("https://www.fakku.net/manga/") || url.startsWith("https://www.fakku.net/doujinshi/")) {
                view.loadUrl("javascript:window.HTMLOUT.processHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
            }
        }
    }

    class FakkuLoadListener {

        @JavascriptInterface
        public void processHTML(String html) {
            Content content = FakkuParser.parseContent(html);
            if (content == null) {
                return;
            }
            Content contentbd = db.selectContentById(content.getUrl().hashCode());
            if (contentbd == null) {
                contentbd = content;
                Log.i(TAG, "Saving content : " + content.getUrl());
                try {
                    content.setImageFiles(new ArrayList<ImageFile>(content.getQtyPages()));
                    String urlCdn = "http://" + content.getCoverImageUrl().substring(2, content.getCoverImageUrl().lastIndexOf("/thumbs/")) + "/images/";
                    content.setCoverImageUrl("http://" + content.getCoverImageUrl().substring(2));
                    for (int i = 1; i <= content.getQtyPages(); i++) {
                        String name = String.format("%03d", i) + ".jpg";
                        ImageFile imageFile = new ImageFile();
                        imageFile.setUrl(urlCdn + name);
                        imageFile.setOrder(i);
                        imageFile.setStatus(Status.SAVED);
                        imageFile.setName(name);
                        content.getImageFiles().add(imageFile);
                    }

                    db.insertContent(content);
                } catch (Exception e) {
                    Log.e(TAG, "Saving content", e);
                    return;
                }
            }
            if (content.isDownloadable()) {
                currentContent = contentbd;
                FloatingActionButton fabDownload = (FloatingActionButton) findViewById(R.id.fabDownload);
                fabDownload.show();
            }
        }
    }
}
