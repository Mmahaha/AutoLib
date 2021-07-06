package xyz.dicky99.autolib;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.Objects;

public class WebActivity extends AppCompatActivity {

    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        webView = findViewById(R.id.web_view);
        webView.setWebViewClient(new MyWebViewClient());
        Intent intent = getIntent();
        String url = "http://kjgl.fzu.edu.cn/remote/static/otherauthIndex?&openid=" + intent.getStringExtra("openid");
        Log.d("openid", Objects.requireNonNull(intent.getStringExtra("openid")));
        webView.setLayerType(View.LAYER_TYPE_HARDWARE,null);
        webView.loadUrl(url);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setUserAgentString("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.83 Safari/537.36");
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setSupportZoom(true);

        webView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    //按返回键操作并且能回退网页
                    if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
                        //后退
                        webView.goBack();
                        return true;
                    }
                }
                return false;
            }
        });
    }

    class MyWebViewClient extends WebViewClient{


        @Override
        public void onPageFinished(WebView view, String url) {
            view.loadUrl("javascript:var con = document.getElementById('smallseats'); con.parentNode.removeChild(con); ");
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            Log.i("url",url);
            Log.i("UA",view.getSettings().getUserAgentString());
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
//            Log.i("url",url);
            if (url.contains("seatId=")){
                String seatId = url.substring(url.indexOf("seatId=")+7,url.indexOf("&date="));
                Log.i("seatid",seatId);
                Intent intent = new Intent();
                intent.putExtra("seatid",seatId);
                setResult(RESULT_OK,intent);
                WebActivity.this.finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            webView.clearHistory();

            ((ViewGroup) webView.getParent()).removeView(webView);
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }

}
