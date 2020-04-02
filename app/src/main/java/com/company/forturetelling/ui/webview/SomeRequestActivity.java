package com.company.forturetelling.ui.webview;

import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.company.forturetelling.R;
import com.company.forturetelling.base.BaseActivity;
import com.just.agentweb.AgentWeb;
import com.just.agentweb.DefaultWebClient;
import com.just.agentweb.WebChromeClient;
import com.just.agentweb.WebViewClient;
import com.yun.common.utils.LogUtils;

/**
 * Created by Lovelin on 2020/4/2
 * <p>
 * Describe:第一次登入需要用户协议界面
 */
public class SomeRequestActivity extends BaseActivity {
    private String userUrl = "http://testbazi.zgszfy.com/api/bs/yc.html";  //用户协议
    private String privacyUrl = "http://jdyapi.jxsccm.com//api/index/yc.html";  //隐私条款
    protected AgentWeb mAgentWeb;
    private RelativeLayout linear_all_some;
    private String typeUrl;
    private String title;

    @Override
    public int getContentViewId() {
        return R.layout.activity_some_request;
    }

    @Override
    public void init() {
        initView();
    }

    private void initView() {
        setTitleBarVisibility(View.VISIBLE);
        setTitleLeftBtnVisibility(View.VISIBLE);
        typeUrl = getIntent().getStringExtra("typeUrl");

        LogUtils.e("typeUrl====================="+typeUrl);
        if("1".equalsIgnoreCase(typeUrl)) {
            title ="用户协议";
        }else{
            title ="隐私条款";
        }
        setTitleName(title + "");
        linear_all_some = findViewById(R.id.linear_all_some);
        goRead(typeUrl);
    }


    public void goRead(String typeUrl) {
        mAgentWeb = AgentWeb.with(this)
                .setAgentWebParent(linear_all_some, new LinearLayout.LayoutParams(-1, -1))
                .useDefaultIndicator()
                .setWebChromeClient(mWebChromeClient)
                .setWebViewClient(mWebViewClient)
                .setMainFrameErrorView(R.layout.agentweb_error_page, -1)
                .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
                .setWebLayout(new WebLayout(this))
                .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.ASK)//打开其他应用时，弹窗咨询用户是否前往其他应用
                .interceptUnkownUrl() //拦截找不到相关页面的Scheme
                .createAgentWeb()
                .ready()
                .go(getUrl(typeUrl));
    }

    private String getUrl(String stype) {
        if("1".equalsIgnoreCase(stype)) {
            return userUrl;
        }else{
            return privacyUrl;
        }
    }

    private com.just.agentweb.WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return super.shouldOverrideUrlLoading(view, request);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            //do you  work
            Log.i("Info", "BaseWebActivity onPageStarted");
        }
    };
    private com.just.agentweb.WebChromeClient mWebChromeClient = new WebChromeClient() {
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
//            if (mTitleTextView != null) {
//                mTitleTextView.setText(title);
//            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (mAgentWeb.handleKeyEvent(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

//    @Override
//    protected void onPause() {
//        mAgentWeb.getWebLifeCycle().onPause();
//        super.onPause();
//
//    }
//
//    @Override
//    protected void onResume() {
//        mAgentWeb.getWebLifeCycle().onResume();
//        super.onResume();
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.i("Info", "onResult:" + requestCode + " onResult:" + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //mAgentWeb.destroy();
        mAgentWeb.getWebLifeCycle().onDestroy();
    }
}
