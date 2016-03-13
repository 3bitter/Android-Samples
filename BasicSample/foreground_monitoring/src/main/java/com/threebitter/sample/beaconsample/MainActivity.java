package com.threebitter.sample.beaconsample;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.threebitter.sdk.BeaconManager;
import com.threebitter.sdk.BeaconMonitorNotifier;
import com.threebitter.sdk.BeaconRegion;
import com.threebitter.sdk.BeaconServiceHelper;
import com.threebitter.sdk.BeaconViewDispatcher;
import com.threebitter.sdk.ui.OnAgreementDialogListener;

public class MainActivity extends BeaconBaseActivity implements BeaconMonitorNotifier, OnAgreementDialogListener {

    private Button mEnableButton;
    private Button mMonitoringButton;

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
                    mMonitoringButton.setEnabled(true);
                } else {
                    BeaconViewDispatcher.showAgreementDialog(MainActivity.this);
                }
            }
        });
        mMonitoringButton = (Button) findViewById(R.id.monitoring);
        mMonitoringButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mIsConnected) {
                    return;
                }
                if (mBeaconManager.startMonitoringInitialRegions()) {
                    Toast.makeText(v.getContext(), "モニタリング開始します。", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(v.getContext(), "モニタリング開始に失敗しました", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 利用規約に同意している場合
        if (BeaconServiceHelper.isAgreement3BitterService(this)) {
            mMonitoringButton.setEnabled(true);
        }
        if (mBeaconManager != null) {
            mBeaconManager.setMonitorNotifier(this);
        }
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (BeaconServiceHelper.handlePermissionRequestResult(this, requestCode, permissions, grantResults)) {
            if (BeaconServiceHelper.allowedPermission(this)) {
                // パーミッション取得
                Toast.makeText(this, "Beacon使用の許可を取得できました。", Toast.LENGTH_SHORT).show();
            }
        } else {
            // リクエスト拒否
            // 今回は再度パーミッションを取得する。
            BeaconServiceHelper.requestPermissionIfNeeded(this);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    public void onBeaconScannerConnect() {
        super.onBeaconScannerConnect();
        Toast.makeText(this, "接続しました。", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void didEnter(BeaconRegion beaconRegion) {

    }

    @Override
    public void didExit(BeaconRegion beaconRegion) {

    }

    @Override
    public void didDetermineStateForRegion(int i, BeaconRegion beaconRegion) {
        final String status;
        switch (i) {
            case BeaconMonitorNotifier.INSIDE:
                status = "INSIDE";
                break;
            case BeaconMonitorNotifier.OUTSIDE:
                status = "OUTSIDE";
                break;
            case BeaconMonitorNotifier.UNKNOWN:
            default:
                status = "UNKNOWN";
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "didDetermineStateForRegion:" + status, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void didNotFoundTarget(BeaconRegion beaconRegion) {
        Toast.makeText(this, "一定時間検索しましたがビーコンが見つかりませんでした。", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDialogSelected(boolean b) {
        if (b) {
            mBeaconManager = BeaconManager.getInstance(this, true);
            if (mBeaconManager != null) {
                Toast.makeText(this, "規約に同意しました。", Toast.LENGTH_SHORT).show();
                // 同意しないとBeaconManagerのインスタンスが取得できていないので同意後にBindする
                mBeaconManager.bind(this);
                // モニタリングのコールバックを設定
                mBeaconManager.setMonitorNotifier(this);
                mMonitoringButton.setEnabled(true);
            }
        } else {
            Toast.makeText(this, "規約に同意が必要です。", Toast.LENGTH_SHORT).show();
        }
    }
}
