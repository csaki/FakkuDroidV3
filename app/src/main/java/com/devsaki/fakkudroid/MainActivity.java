package com.devsaki.fakkudroid;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.devsaki.fakkudroid.database.domains.Content;
import com.devsaki.fakkudroid.parser.FakkuParser;
import com.melnykov.fab.FloatingActionButton;

import java.net.MalformedURLException;
import java.net.URL;


public class MainActivity extends ActionBarActivity {

    private final String FAKKU_URL = "https://www.fakku.net";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WebView webview = (WebView) findViewById(R.id.wbMain);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebViewClient(new CustomWebViewClient());
        webview.addJavascriptInterface(new FakkuLoadListener(), "HTMLOUT");
        webview.loadUrl(FAKKU_URL);
        FloatingActionButton fabDownload = (FloatingActionButton) findViewById(R.id.fabDownload);
        fabDownload.hide(true);
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(event.getAction() == KeyEvent.ACTION_DOWN){
            switch(keyCode)
            {
                case KeyEvent.KEYCODE_BACK:
                    WebView webview = (WebView) findViewById(R.id.wbMain);
                    if(webview.canGoBack()){
                        webview.goBack();
                    }else{
                        finish();
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    class CustomWebViewClient extends WebViewClient {

        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {
            try {
                URL u = new URL(url);
                if(u.getHost().equals("www.fakku.net")){
                    view.loadUrl(url);
                    return false;
                }else{
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                    return true;
                }
            } catch (MalformedURLException e) {}
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon)
        {
            FloatingActionButton fabDownload = (FloatingActionButton) findViewById(R.id.fabDownload);
            fabDownload.hide();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if(url.startsWith("https://www.fakku.net/manga/")||url.startsWith("https://www.fakku.net/doujinshi/")) {
                view.loadUrl("javascript:window.HTMLOUT.processHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
            }
        }
    }

    class  FakkuLoadListener{

        @JavascriptInterface
        public void processHTML(String html)
        {
            Content content = FakkuParser.parseContent(html);

            FloatingActionButton fabDownload = (FloatingActionButton) findViewById(R.id.fabDownload);
            fabDownload.show();
        }
    }
}
