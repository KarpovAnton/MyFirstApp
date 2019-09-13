package com.socializer.vacuum.commons;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;

import com.socializer.vacuum.R;

import butterknife.BindView;

public class AuthenticationDialog extends Dialog {

    private final String redirect_url;
    private final String request_url;
    private AuthInstListener listener;

    @BindView(R.id.webView)
    WebView webView;

    public AuthenticationDialog(@NonNull Context context, AuthInstListener listener) {
        super(context);
        this.listener = listener;
        this.redirect_url = context.getResources().getString(R.string.inst_redirect_url);
        this.request_url = context.getResources().getString(R.string.inst_base_url) +
                "oauth/authorize/?client_id=" +
                context.getResources().getString(R.string.inst_client_id) +
                "&redirect_uri=" + redirect_url +
                "&response_type=token&display=touch&scope=public_content";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.auth_dialog);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(request_url);
        webView.setWebViewClient(webViewClient);
    }

    WebViewClient webViewClient = new WebViewClient() {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith(redirect_url)) {
                AuthenticationDialog.this.dismiss();
                return true;
            }
            return false;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (url.contains("access_token=")) {
                Uri uri = Uri.EMPTY.parse(url);
                String access_token = uri.getEncodedFragment();
                access_token = access_token.substring(access_token.lastIndexOf("=") + 1);
                listener.onInstTokenReceived(access_token);
            }
        }
    };

    public interface AuthInstListener {
        void onInstTokenReceived(String auth_token);
    }
}
