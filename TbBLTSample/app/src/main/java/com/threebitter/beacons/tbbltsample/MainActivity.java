package com.threebitter.beacons.tbbltsample;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.threebitter.sdk.BeaconManager;
import com.threebitter.sdk.BeaconServiceHelper;
import com.threebitter.sdk.utils.StartUp;

public class MainActivity extends AppCompatActivity {

    public static final String PREF_FILE_KEY = "com.threebitter.beacons.tbbltsample";

    private static Button showContentButton;
    private boolean mWaitingForBluetoothStateChange = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        showContentButton = (Button)findViewById(R.id.showContent);
        showContentButton.setOnClickListener(new View.OnClickListener() {
                                                 @Override
                                                 public void onClick(View v) {
                                                     boolean beaconUsableWithThisDevice = BeaconServiceHelper.isSupportBeacon(getApplicationContext());
                                                     boolean usingBeacon = getSharedPreferences(PREF_FILE_KEY, MODE_PRIVATE).getBoolean("useBeacon", false);
                                                     if (beaconUsableWithThisDevice && !usingBeacon) {
                                                         showConfirmationDialog();
                                                     } else if (usingBeacon) {
                                                         checkBTAvailabilityAndDetermineAction();
                                                     } else { // Beacon can not used
                                                         Intent contentListIntent = new Intent(MainActivity.this, ContentListActivity.class);
                                                         startActivity(contentListIntent);
                                                     }
                                                 }
                                             }
        );

        if (!BeaconServiceHelper.isSupportBeacon(getApplicationContext())) {
            /* Toast infoToast = Toast.makeText(this, "端末またはOSでビーコン使用がサポートされていません", Toast.LENGTH_LONG);
            infoToast.show(); */
            Log.w("Debug", "Beacon Unsupported");
            return; // Cannot use beacon related functionality
        }
        // Preparation for Beacon SDK (Just for foreground mode)
        StartUp.init(getApplication());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mWaitingForBluetoothStateChange) {
            mWaitingForBluetoothStateChange = false;
            checkBTAvailabilityAndDetermineAction();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            disableBeaconFunctionality();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showConfirmationDialog() {
        AlertDialog.Builder notificationDialogBuilder = new AlertDialog.Builder(this)
                .setTitle("お知らせ")
                .setMessage("限定コンテンツ検索機能を有効にしておくと、その場所でしか使用できないコンテンツを参照できるようになります。" +
                        "Bluetoothと位置情報の許可をして、是非この機会に、限定コンテンツの参照ができるようにしてください！")
                .setNegativeButton("無視", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        // Go with no beacon functionality ..
                        showContentListPage();
                    }
                })
                .setPositiveButton("使用する", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        checkBTAvailabilityAndDetermineAction();
                    }
                });
        notificationDialogBuilder.show();
    }


    private void checkBTAvailabilityAndDetermineAction() {
        if (!BeaconServiceHelper.isBluetoothEnabled()) { // Bluetooth is OFF on this device
            Log.i("Debug", "Bluetooth Disabled");
            showBluetoothAlert();
            return;
        }
        // For Location Service Permission, on Android 6.0 〜
        if (!BeaconServiceHelper.allowedPermission(getApplicationContext())) {
            Log.i("Debug", "Permission Not Allowed Yet");
            BeaconServiceHelper.requestPermissionIfNeeded(this);
            return;
        }
        enableBeaconFunctionality();
        /* infoToast = Toast.makeText(MainActivity.this, "ビーコン機能は有効です", Toast.LENGTH_LONG);
        infoToast.show(); */
        showContentListPage();
    }

    private void showBluetoothAlert() {
        AlertDialog.Builder bluetoothAlertBuilder = new AlertDialog.Builder(this)
                .setTitle("確認")
                .setMessage("Bluetoothがオフのようです。" +
                        "Bluetoothをオンにしますか？")
                .setNegativeButton("しない", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mWaitingForBluetoothStateChange = false;
                        dialog.cancel();
                        // Go with no beacon functionality
                        showContentListPage();
                    }
                })
                .setPositiveButton("設定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        mWaitingForBluetoothStateChange = true;
                        // Show settings and wait for resume
                        Intent settingIntent = null;
                        try {
                            settingIntent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                            startActivity(settingIntent);
                        } catch (ActivityNotFoundException e) {
                            settingIntent = new Intent(Settings.ACTION_SETTINGS);
                            startActivity(settingIntent);
                        }
                    }
                });
        bluetoothAlertBuilder.show();
    }

    private void enableBeaconFunctionality() {
        getSharedPreferences(PREF_FILE_KEY, MODE_PRIVATE).edit().putBoolean("useBeacon", true).apply();
        BeaconManager.getInstance(getApplicationContext(), true);
    }

    private void disableBeaconFunctionality() {
        getSharedPreferences(PREF_FILE_KEY, MODE_PRIVATE).edit().putBoolean("useBeacon", false).apply();
        BeaconManager.getInstance(getApplicationContext(), false);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (BeaconServiceHelper.handlePermissionRequestResult(this, requestCode, permissions, grantResults)) {
            if (BeaconServiceHelper.allowedPermission(this)) {
                enableBeaconFunctionality();
                showContentListPage();
            } else {
                showContentListPage();
            }
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void showContentListPage() {
        Intent contentListIntent = new Intent(MainActivity.this, ContentListActivity.class);
        startActivity(contentListIntent);
    }
}
