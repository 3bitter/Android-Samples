package com.threebitter.sample.backgounrd_monitoring;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.threebitter.sdk.BeaconManager;
import com.threebitter.sdk.BeaconServiceHelper;
import com.threebitter.sdk.BeaconViewDispatcher;
import com.threebitter.sdk.ui.OnAgreementDialogListener;

public class MainActivity extends BeaconBaseActivity implements OnAgreementDialogListener {

    private Button mEnableButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEnableButton = (Button) findViewById(R.id.enable_button);
        mEnableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!BeaconServiceHelper.isSupportBeacon(MainActivity.this)) {
                    Toast.makeText(MainActivity.this, "この端末はBeaconをサポートしていません。", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!BeaconServiceHelper.isBluetoothEnabled()) {
                    Toast.makeText(MainActivity.this, "BluetoothをONにしてください。", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (BeaconServiceHelper.isAgreement3BitterService(MainActivity.this)) {
                } else {
                    BeaconViewDispatcher.showAgreementDialog(MainActivity.this);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // M以上の場合はパーミッションをリクエスト
        BeaconServiceHelper.requestPermissionIfNeeded(this);
    }

    @Override
    protected void onStop() {
        if (mBeaconManager != null) {
            // モニタリングを停止する
            mBeaconManager.stopMonitoringInitialRegions();
        }
        super.onStop();
    }

    @Override
    public void onDialogSelected(boolean allowed) {
        if (allowed) {
            mBeaconManager = BeaconManager.getInstance(this, true);
            if (mBeaconManager != null) {
                Toast.makeText(this, "規約に同意しました。", Toast.LENGTH_SHORT).show();
                // 同意しないとBeaconManagerのインスタンスが取得できていないので同意後にBindする
                mBeaconManager.bind(this);
                // モニタリングのコールバックを設定
                mBeaconManager.setMonitorNotifier((MyApplication) getApplication());
            }
        } else {
            Toast.makeText(this, "規約に同意が必要です。", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBeaconScannerConnect() {
        super.onBeaconScannerConnect();
        // コネクト完了したらモニタリング開始する
        mBeaconManager.startMonitoringInitialRegions();
    }
}
