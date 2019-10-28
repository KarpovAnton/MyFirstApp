package com.socializer.vacuum.commons;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;

import com.socializer.vacuum.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class AuthenticationDialog extends Dialog {

    private final String redirect_uri;
    private final String request_url;
    private AuthInstListener listener;

    @BindView(R.id.webView)
    WebView webView;

    public AuthenticationDialog(@NonNull Context context, AuthInstListener listener) {
        super(context);
        this.listener = listener;
        this.redirect_uri = context.getResources().getString(R.string.inst_redirect_url);

        this.request_url = context.getResources().getString(R.string.inst_base_url)
                + context.getResources().getString(R.string.inst_client_id) +
                "&redirect_uri=" + redirect_uri +
                "&response_type=token&scope=user_profile&response_type=code";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.auth_dialog);
        ButterKnife.bind(this);

        WebSettings settings = webView.getSettings();
        /* JavaScript must be enabled if you want it to work, obviously */
        settings.setJavaScriptEnabled(true);
        /* Register a new JavaScript interface called HTMLOUT */
        webView.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");

        webView.setWebViewClient(webViewClient);
        webView.loadUrl(request_url);
    }

    WebViewClient webViewClient = new WebViewClient() {

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            webView.scrollTo(0, 350);
            if (url.startsWith(redirect_uri)) {
                /* This call inject JavaScript into the page which just finished loading. */
                webView.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
                AuthenticationDialog.this.dismiss();
            }
        }
    };

    /* An instance of this class will be registered as a JavaScript interface */
    class MyJavaScriptInterface
    {
        @JavascriptInterface
        @SuppressWarnings("unused")
        public void processHTML(String html)
        {
            String access_token = html.substring(html.indexOf("access_token") + 15, html.indexOf("\",\"user_id"));

            String userId = html.substring(html.indexOf("user_id") + 9, html.indexOf(",\"username"));

            String username = html.substring(html.indexOf("username") + 11, html.lastIndexOf("\"}"));
            listener.onInstTokenReceived(username, userId, access_token);
        }
    }


    public interface AuthInstListener {
        void onInstTokenReceived(String username, String userId, String auth_token);
    }
}
