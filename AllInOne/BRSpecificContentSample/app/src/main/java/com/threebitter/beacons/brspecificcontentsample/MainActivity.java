package com.threebitter.beacons.brspecificcontentsample;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
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
import com.threebitter.sdk.BeaconViewDispatcher;
import com.threebitter.sdk.IBeaconManager;
import com.threebitter.sdk.utils.StartUp;

public class MainActivity extends AppCompatActivity {

    private static final String PREF_FILE_KEY = "com.threebitter.beacons.specificcontentsample";

    private static Button showContentButton;
    private static Button disableButton;

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
                                                   Toast infoToast = null;
                                                   boolean useBeacon = getSharedPreferences(PREF_FILE_KEY, MODE_PRIVATE).getBoolean("useBeacon", false);
                                                   if (useBeacon) {
                                                       infoToast = Toast.makeText(MainActivity.this, "ビーコン機能は有効です", Toast.LENGTH_LONG);
                                                       infoToast.show();
                                                   } else {
                                                       infoToast = Toast.makeText(MainActivity.this, "ビーコン機能は無効です", Toast.LENGTH_LONG);
                                                       infoToast.show();
                                                   }
                                                   Intent contentListIntent = new Intent(MainActivity.this, ContentListActivity.class);
                                                    startActivity(contentListIntent);
                                               }
                                           }
        );
        disableButton = (Button)findViewById(R.id.stopBT);
        disableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableBeaconFunctionality();
                disableButton.setEnabled(false);
            }
        });

        // Preparation for Beacon SDK
        StartUp.init(getApplication());
        boolean useBeacon = getSharedPreferences(PREF_FILE_KEY, MODE_PRIVATE).getBoolean("useBeacon", false);
        if (!useBeacon) {
            showConfirmationDialog();
            disableButton.setEnabled(false);
        } else {
            disableButton.setEnabled(true);
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
                        findViewById(R.id.stopBT).setEnabled(false);
                        dialog.cancel();
                    }
                })
                .setPositiveButton("使用する", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        checkBTAvailability();
                    }
                });
        notificationDialogBuilder.show();
    }

    private void checkBTAvailability() {
        Toast infoToast = null;
        if (!BeaconServiceHelper.isBluetoothEnabled()) {
            infoToast = Toast.makeText(this, "Bluetoothをオンにしてください", Toast.LENGTH_LONG);
            infoToast.show();
            Log.i("Debug", "Bluetooth Disabled");
            return;
        }
        if (!BeaconServiceHelper.isSupportBeacon(getApplicationContext())) {
            infoToast = Toast.makeText(this, "ビーコン使用がサポートされていません", Toast.LENGTH_LONG);
            infoToast.show();
            Log.w("Debug", "Beacon Unsupported");
            return;
        }
        if (!BeaconServiceHelper.allowedPermission(getApplicationContext())) {
            infoToast = Toast.makeText(MainActivity.this, "まだ許可が得られていません", Toast.LENGTH_SHORT);
            infoToast.show();
            Log.i("Debug", "Permission Not Allowed Yet");
            BeaconServiceHelper.requestPermissionIfNeeded(this);
            return;
/*        } else if (!BeaconServiceHelper.isAgreement3BitterService(getApplicationContext())) {
            // Show agreement activity
            BeaconViewDispatcher.showAgreementDialog(this);
            return; */
        }
        enableBeaconFunctionality();
        infoToast = Toast.makeText(MainActivity.this, "ビーコン機能は有効です", Toast.LENGTH_LONG);
        infoToast.show();
        Intent contentListIntent = new Intent(MainActivity.this, ContentListActivity.class);
        startActivity(contentListIntent);
    }
    private void enableBeaconFunctionality() {
        getSharedPreferences(PREF_FILE_KEY, MODE_PRIVATE).edit().putBoolean("useBeacon", true).apply();
        BeaconManager.getInstance(getApplicationContext(), true);
        disableButton.setEnabled(true);
    }

    private void disableBeaconFunctionality() {
        getSharedPreferences(PREF_FILE_KEY, MODE_PRIVATE).edit().putBoolean("useBeacon", false).apply();
        BeaconManager.getInstance(getApplicationContext(), false);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (BeaconServiceHelper.handlePermissionRequestResult(this, requestCode, permissions, grantResults)) {
            if (BeaconServiceHelper.allowedPermission(this)) {
                Toast.makeText(this, "使用許可設定されました", Toast.LENGTH_SHORT).show();
            } else {
                BeaconServiceHelper.requestPermissionIfNeeded(this);
            }
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
