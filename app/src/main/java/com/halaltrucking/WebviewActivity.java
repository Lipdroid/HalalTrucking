package com.halaltrucking;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.halaltrucking.utils.CorrectSizeUtil;

public class WebviewActivity extends Activity implements View.OnClickListener {
    Bundle bundle;
    WebView wvContent;
    ImageView mImgButtonBack;
    ProgressBar mpbProgressBar;
    String request_type = null;
    //Variables:
    CorrectSizeUtil mCorrectSize;
    String url = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        String url = getIntent().getExtras().getString("url");

        wvContent = (WebView) findViewById(R.id.wv_campus_work_content);
        mpbProgressBar = (ProgressBar) findViewById(R.id.prgb_campus_work_webview);
        mImgButtonBack = (ImageView) findViewById(R.id.img_left_header);
        mCorrectSize = CorrectSizeUtil.getInstance(this);
        //actions:

        wvContent.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, final int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress < 100) {
                    mpbProgressBar.setProgress(newProgress);
                } else {
                    mpbProgressBar.setVisibility(View.GONE);
                }
            }

        });
        wvContent.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                return super.shouldOverrideUrlLoading(view, url);
            }
        });
        wvContent.getSettings().setJavaScriptEnabled(true);

        wvContent.loadUrl(url);
        mImgButtonBack.setOnClickListener(this);

        mCorrectSize.correctSize();
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.img_left_header) {
            finish();
            overridePendingTransition(R.anim.anim_nothing,
                    R.anim.anim_slide_out_bottom);
        }
    }
}
