package com.threebitter.sample.backgounrd_monitoring;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.threebitter.sdk.BeaconConsumer;
import com.threebitter.sdk.BeaconManager;
import com.threebitter.sdk.IBeaconManager;


/**
 * Created by yohei on 2016/01/02.
 */
public class BeaconBaseActivity extends AppCompatActivity implements BeaconConsumer {

    protected boolean mIsConnected = false;
    protected IBeaconManager mBeaconManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBeaconManager = BeaconManager.getInstance(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mBeaconManager != null) {
            mBeaconManager.bind(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBeaconManager != null) {
            mBeaconManager.unbind(this);
        }
    }

    @Override
    public void onBeaconScannerConnect() {
        mIsConnected = true;
    }
}
