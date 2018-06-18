package ru.merkulyevsasha.news.presentation.webview;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.merkulyevsasha.news.R;
import ru.merkulyevsasha.news.pojos.Article;

public class WebViewActivity extends AppCompatActivity {

    public static final String KEY_LINK = "ru.merkulyevsasha.news.key_link";
    public static final String KEY_TITLE = "ru.merkulyevsasha.news.key_title";

    @BindView(R.id.webView) public WebView mWebView;
    @BindView(R.id.fab) public FloatingActionButton mFab;
    @BindView(R.id.toolbar) public Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        final String url = intent.getStringExtra(KEY_LINK);
        final String title = intent.getStringExtra(KEY_TITLE);
        setTitle(title);

        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.loadUrl(url);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, url);
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, getString(R.string.share_using)));
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if(mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    public static void startActivity(Context context, String title, Article item) {
        Intent link_intent = new Intent(context, WebViewActivity.class);
        link_intent.putExtra(KEY_LINK, item.getLink());
        link_intent.putExtra(KEY_TITLE, title);
        context.startActivity(link_intent);
    }

    private class MyWebViewClient extends WebViewClient
    {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {
            view.loadUrl(url);
            return true;
        }
    }
}
