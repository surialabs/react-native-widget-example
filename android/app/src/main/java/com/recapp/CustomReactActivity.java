package com.recapp;

import android.os.Bundle;
import android.util.Log;

import com.facebook.react.ReactActivity;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactInstanceManagerBuilder;
import com.facebook.react.ReactRootView;
import com.facebook.react.common.LifecycleState;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.facebook.react.shell.MainReactPackage;

/**
 * Created by kyaroru on 07/04/2017.
 */
public class CustomReactActivity extends ReactActivity implements DefaultHardwareBackBtnHandler {
    private ReactRootView mReactRootView;
    private ReactInstanceManager mReactInstanceManager;
    private String extraModule;
    private Bundle extraBundle;

    private ReactInstanceManagerBuilder getBuilder(){
        return ReactInstanceManager.builder()
                .setApplication(getApplication())
//                .setBundleAssetName("index.android.jsbundle")
//                .setUseDeveloperSupport(false)
                .setBundleAssetName("index.android.bundle")
                .setUseDeveloperSupport(true)
                .setJSMainModuleName("index.android");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mReactRootView = new ReactRootView(this);
        mReactInstanceManager = getBuilder()
                .addPackage(new MainReactPackage())
                .setInitialLifecycleState(LifecycleState.RESUMED)
                .build();
        Bundle bundle = getIntent().getExtras();
        extraModule = bundle.getString("module", "");
        extraBundle = bundle.getBundle("data");

        startReactApplication();
    }

    private void startReactApplication() {
        mReactRootView.startReactApplication(mReactInstanceManager, extraModule, extraBundle);
        setContentView(mReactRootView);
    }
}
