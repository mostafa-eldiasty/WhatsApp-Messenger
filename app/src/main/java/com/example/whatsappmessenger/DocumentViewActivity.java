package com.example.whatsappmessenger;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DocumentViewActivity extends AppCompatActivity {

    String documentURL;
    WebView  webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_view);

        Intent intent = getIntent();
        documentURL = intent.getStringExtra("documentURL");

        webView = findViewById(R.id.wv);
        webView.getSettings().setJavaScriptEnabled(true);
//        webView.getSettings().setAllowContentAccess(true);
//        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient());
    }

    @Override
    protected void onStart() {
        super.onStart();
        webView.loadUrl(documentURL + "&url=https://www.adobe.com/devnet/acrobat/pdfs/pdf_open_parameters.pdf");
    }
}