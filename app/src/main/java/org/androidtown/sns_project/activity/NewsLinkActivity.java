package org.androidtown.sns_project.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import org.androidtown.sns_project.R;

import androidx.appcompat.app.AppCompatActivity;

public class NewsLinkActivity extends AppCompatActivity {

    private RelativeLayout loaderLayout_memberinit;
    private WebView mWebView; // 웹뷰 선언
    private WebSettings mWebSettings; //웹뷰세팅
    private static final String TAG = "NewsLinkActivity";// 로그찍을때 태그


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitiy_newslink);
        Log.v(TAG, "OnCreate");

        loaderLayout_memberinit =findViewById(R.id.loaderLayout); // 레이아웃의 로딩 id 연결
        loaderLayout_memberinit.setVisibility(View.VISIBLE); //로딩 화면 보여주기

        // 웹뷰 시작
        mWebView = (WebView) findViewById(R.id.newsWebView);

        mWebView.setWebViewClient(new WebViewClient()); // 클릭시 새창 안뜨게
        mWebSettings = mWebView.getSettings(); //세부 세팅 등록
        mWebSettings.setJavaScriptEnabled(true); // 웹페이지 자바스클비트 허용 여부
        mWebSettings.setSupportMultipleWindows(false); // 새창 띄우기 허용 여부
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(false); // 자바스크립트 새창 띄우기(멀티뷰) 허용 여부
        mWebSettings.setLoadWithOverviewMode(true); // 메타태그 허용 여부
        mWebSettings.setUseWideViewPort(true); // 화면 사이즈 맞추기 허용 여부
        mWebSettings.setSupportZoom(false); // 화면 줌 허용 여부
        mWebSettings.setBuiltInZoomControls(false); // 화면 확대 축소 허용 여부
        mWebSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN); // 컨텐츠 사이즈 맞추기
        mWebSettings.setCacheMode(WebSettings.LOAD_NO_CACHE); // 브라우저 캐시 허용 여부
        mWebSettings.setDomStorageEnabled(true); // 로컬저장소 허용 여부

        Intent intent = new Intent(this.getIntent());
        String url1=intent.getStringExtra("url");
        Log.v(TAG, "url1 :" + url1);

        mWebView.loadUrl(url1); // 웹뷰에 표시할 웹사이트 주소, 웹뷰 시작
        loaderLayout_memberinit.setVisibility(View.GONE); //로딩 화면 보여주기
    }


}
