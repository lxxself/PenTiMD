package com.lxxself.pentimd.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.lxxself.pentimd.R;
import com.socks.library.KLog;
import com.umeng.analytics.MobclickAgent;

import static com.lxxself.pentimd.util.ShareUtil.shareTO;

public class WebViewActivity extends AppCompatActivity {

    private com.lxxself.pentimd.ProgressBarWebView webview;
    private String url;
    private String title;
    private SharedPreferences sp;
    private boolean nightMode = false;

    @Override
    protected void onStart() {
        super.onStart();
        if (nightMode!=sp.getBoolean("nightMode", false)) {
            recreate();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = getSharedPreferences("Setting", MODE_PRIVATE);
        if (sp.getBoolean("nightMode", false)) {
            setTheme(R.style.NightTheme);
            nightMode = true;
        } else {
            setTheme(R.style.AppTheme);
        }
        setContentView(R.layout.activity_web_view);


        Intent getIntent = getIntent();
        if (getIntent != null) {
            url = getIntent.getStringExtra("url");
            title = getIntent.getStringExtra("title");
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webview.destroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
    private void init() {
        KLog.d(url);
        url = url.replace("https", "http");
        webview = (com.lxxself.pentimd.ProgressBarWebView) findViewById(R.id.webview);
        final String js = "javascript:window.onload=function(){ " +
                "var ps = document.getElementsByTagName(\"p\"); \n" +
                "for (var i=0;i<ps.length;i++)\n" +
                "{\n" +
                "ps[i].style.color = \"#DFDFDF\"\n" +
                "};"+
                "document.bgColor='#000000';" +
                "" +
                "}";
        webview.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                return true;
            }



            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (nightMode == true) {
                    view.loadUrl(js);
                    KLog.d(js);
                }
            }
        });
        webview.loadUrl(url);
        webview.clearCache(true);
        WebSettings webSettings = webview.getSettings();

        SharedPreferences sp = getSharedPreferences("Setting", MODE_PRIVATE);
        webSettings.setJavaScriptEnabled(sp.getBoolean("JS", true));

        KLog.d("zoom", webSettings.getTextZoom() + "");
        KLog.d("size", webSettings.getTextSize() + "");
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_web, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_share:
                shareThis();
                break;
            case R.id.action_refresh:
                webview.clearCache(true);
                webview.reload();
                break;
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingActivity.class);
                startActivity(intent);
                break;
            case R.id.action_out:
                Intent out = new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.parse(url);
                out.setData(uri);
                startActivity(out);
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    private void shareThis() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TITLE, title);
        intent.putExtra(Intent.EXTRA_TEXT, title+" —— "+url);
        startActivity(Intent.createChooser(intent, "分享到"));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webview.canGoBack()) {
                webview.goBack();
                return true;
            } else {
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
