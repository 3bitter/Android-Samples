package com.threebitter.beacons.tbbltsample;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.threebitter.beacons.tbbltsample.dummy.BRSpecificContnents;
import com.threebitter.sdk.BeaconConsumer;
import com.threebitter.sdk.BeaconData;
import com.threebitter.sdk.BeaconManager;
import com.threebitter.sdk.BeaconRangeNotifier;
import com.threebitter.sdk.BeaconRegion;
import com.threebitter.sdk.IBeaconManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ContentListActivity extends ListActivity implements BeaconConsumer, BeaconRangeNotifier {

    private static List<MyContent> mDefaultContents;
    private static List<MyContent> mFullContents;

    private IBeaconManager mBeaconManager;

    private final long TIMEOUT_INTERVAL = 15000;

    private ProgressDialog mProgressIndicator;
    private Timer mTimeoutTimer;

    // private Date rangeStartTime; // Just for debug

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_list);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ContentListActivity.this, ContentActivity.class);
                intent.putExtra("content", mFullContents.get(position));
                startActivity(intent);
            }
        });

        // Prepare static content
        mDefaultContents = prepareDefaultContents();
        MyContentArrayAdapter myContentArrayAdapter = new MyContentArrayAdapter(
                ContentListActivity.this,
                R.layout.content_row,
                R.id.contentTitle,
                mDefaultContents
        );
        setListAdapter(myContentArrayAdapter);

        // Build beacon manager
        mBeaconManager = BeaconManager.getInstance(getApplicationContext());
        if (mBeaconManager != null) {
            /* ビーコン電波受信機能の接続 */
            mBeaconManager.bind(this);
            mBeaconManager.addRangeNotifier(this);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    protected void onDestroy() {
        /* ビーコン機能の切り離し */
        if (mBeaconManager != null) {
            mBeaconManager.stopRangingTbBTInitialRegions();
            mBeaconManager.unbind(this);
            mBeaconManager.addRangeNotifier(null);
        }
        super.onDestroy();
    }

    /* ビーコン検出処理の正常系コールバック */
    @Override
    public void didRangeBeacons(@NonNull final List<BeaconData> beaconDataList, @NonNull final BeaconRegion region) {
        Log.d("callback", "didRangeBeacons");
        final List fixedKeyInfoList = mBeaconManager.beaconsTrack(beaconDataList, region);
        if (fixedKeyInfoList != null && !fixedKeyInfoList.isEmpty()) {// finish search
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mProgressIndicator != null) {
                        mProgressIndicator.dismiss();
                        mProgressIndicator = null;
                    }
                    List<MyContent> newContentList = findContentForBeacons(fixedKeyInfoList);
                    mFullContents = new ArrayList<>(mDefaultContents);
                    if (newContentList != null) {
                        mFullContents.addAll(newContentList);
                    }

                    MyContentArrayAdapter myContentArrayAdapter = new MyContentArrayAdapter(
                            ContentListActivity.this,
                            R.layout.content_row,
                            R.id.contentTitle,
                            mFullContents
                    );
                    setListAdapter(myContentArrayAdapter);

                    // Cancel stop timer and stop ranging
                    if(mTimeoutTimer != null) {
                        mTimeoutTimer.cancel();
                        mTimeoutTimer = null;
                    }
                  /*  Date current = new Date(System.currentTimeMillis());
                    long elapsed = current.getTime() - rangeStartTime.getTime();
                    Toast.makeText(ContentListActivity.this, "ビーコン領域限定コンテンツをチェックしました Elapsed: " + elapsed / 1000 + " sec.", Toast.LENGTH_LONG).show();
                    */
                    //　ビーコン検出処理の停止
                    mBeaconManager.stopRangingTbBTInitialRegions();
                }
            });
        }
    }

    private List<MyContent> prepareDefaultContents() {
        List<MyContent> contents = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            MyContent content = new MyContent(i, "標準コンテンツ", R.drawable.android_logo, null);
            contents.add(content);
        }
        return contents;
    }

    /* Get contents for 3bitter beacon region, can be map to each beacon key */
    private List<MyContent> findContentForBeacons(List<BeaconData> fixedBeaconInfoList) {
        Log.d("called", "findContentForRegions");
        List specialContentList = null;
        boolean useBeacon = getSharedPreferences(MainActivity.PREF_FILE_KEY, Context.MODE_PRIVATE).getBoolean("useBeacon", false);
        if (useBeacon) {
            if (fixedBeaconInfoList != null && !fixedBeaconInfoList.isEmpty()) {
                List<String> beaconKeys = new ArrayList<>();
                for (BeaconData foundBeacon : fixedBeaconInfoList) {
                    beaconKeys.add(foundBeacon.getKeycode());
                }
                specialContentList = BRSpecificContnents.getRegionSpecificContentByBeacon(beaconKeys);
            }
        }
        return specialContentList;
    }

    /*
     * ビーコン電波受信機能に接続した場合に呼ばれるコールバック
     */
    @Override
    public void onBeaconScannerConnect() {
        Log.d("callback", "onBeaconScannerConnect");
        if (mBeaconManager != null) {
            final long timeoutTime = System.currentTimeMillis() + TIMEOUT_INTERVAL;
            // タイムアウトした場合にビーコン検出処理をストップするためのタイマー
            mTimeoutTimer = new Timer();
            TimerTask stopRangingTask = new TimerTask() {
                @Override
                public void run() {
                    long current = System.currentTimeMillis();
                    if (current >= timeoutTime) {
                        // Timed out
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (mProgressIndicator != null) {
                                    mProgressIndicator.dismiss();
                                    mProgressIndicator = null;
                                }
                                // ビーコンの検出を停止
                               // Toast.makeText(ContentListActivity.this, "タイムアウトしました", Toast.LENGTH_LONG).show();
                                mBeaconManager.stopRangingTbBTInitialRegions();
                                mTimeoutTimer.cancel();
                                mTimeoutTimer = null;
                            }
                        });
                    }
                }
            };
            // Repeat every sec.
            mTimeoutTimer.schedule(stopRangingTask, 0, 1000);
         //   rangeStartTime = new Date(System.currentTimeMillis());

            // ビーコン検出処理を開始します
            mBeaconManager.startRangingTbBTInitialRegions();
            // Return back to main thread
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mBeaconManager == null) {
                       /* Toast infoToast = Toast.makeText(ContentListActivity.this, "BeaconManager is null", Toast.LENGTH_SHORT);
                        infoToast.show(); */
                        Log.d("state", "BeaconManager not instantiated");
                        return;
                    } else {
                        mProgressIndicator = new ProgressDialog(ContentListActivity.this);
                        mProgressIndicator.setTitle("追加コンテンツを確認しています....");
                        mProgressIndicator.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        mProgressIndicator.setProgress(0);
                        mProgressIndicator.setButton(DialogInterface.BUTTON_NEUTRAL, "CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                mTimeoutTimer.cancel();
                                mTimeoutTimer = null;
                            }
                        });
                        mProgressIndicator.show();
                    }
                }
            });
        }
    }
}
