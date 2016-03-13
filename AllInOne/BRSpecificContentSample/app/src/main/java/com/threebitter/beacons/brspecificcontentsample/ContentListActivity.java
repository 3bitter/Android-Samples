package com.threebitter.beacons.brspecificcontentsample;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.threebitter.beacons.brspecificcontentsample.dummy.BRSpecificContnents;
import com.threebitter.sdk.BeaconConsumer;
import com.threebitter.sdk.BeaconData;
import com.threebitter.sdk.BeaconManager;
import com.threebitter.sdk.BeaconRangeNotifier;
import com.threebitter.sdk.BeaconRegion;
import com.threebitter.sdk.IBeaconManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import java.util.Date;

public class ContentListActivity extends ListActivity implements BeaconConsumer, BeaconRangeNotifier {

    private static List<MyContent> mDefaultContents;
    private static List<MyContent> mFullContents;

    private static final String PREF_FILE_KEY = "com.threebitter.beacons.specificcontentsample";

    private IBeaconManager mBeaconManager;
    private int rangedCount = 0;

    private final int MAX_RANGE_COUNT = 3;
    private final long TIMEOUT_INTERVAL = 15000;
    private List<BeaconData> mCumulativeBeaconList;

    private ProgressDialog mProgressIndicator;
    private Timer mTimeoutTimer;

    private Date rangeStartTime;

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
        /*ArrayAdapter<MyContent> myContentArrayAdapter = new ArrayAdapter<MyContent>(
                ContentListActivity.this,
                android.R.layout.simple_list_item_1,
                mDefaultContents
        ); */
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
            mBeaconManager.bind(this);
            mBeaconManager.setRangeNotifier(this);
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
        if (mBeaconManager != null) {
            mBeaconManager.stopRangingInitialRegions();
            mBeaconManager.unbind(this);
            mBeaconManager.setRangeNotifier(null);
        }
        super.onDestroy();
    }

    @Override
    public void didRangeBeacons(@NonNull final List<BeaconData> beaconDataList, @NonNull final BeaconRegion region) {
        Log.d("called", "didRange!!!!!");
        rangedCount++;
        if (rangedCount <= MAX_RANGE_COUNT) {
            if (mCumulativeBeaconList == null) {
                mCumulativeBeaconList = new ArrayList<BeaconData>();
            }
            if (!beaconDataList.isEmpty()) {
                Log.d("state", "Not empty !!!!");
                for (BeaconData beaconData : beaconDataList) {
                    boolean alreadyStored = false;
                    String keycode = beaconData.getKeycode();
                    if (!mCumulativeBeaconList.isEmpty()) {
                        for (BeaconData stored : mCumulativeBeaconList) {
                            if (keycode.equals(stored.getKeycode())) {
                                alreadyStored = true;
                                break;
                            }
                        }
                    }
                    if (!alreadyStored) {
                        mCumulativeBeaconList.add(beaconData);
                    }
                }
            }
        } else { // finish search
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mProgressIndicator != null) {
                        mProgressIndicator.dismiss();
                        mProgressIndicator = null;
                    }
                    List<MyContent> newContentList = findContentForBeaconRegions();
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

                    // Stop ranging and clear data
                    mTimeoutTimer.cancel();
                    mTimeoutTimer = null;
                    Date current = new Date(System.currentTimeMillis());
                    long elapsed = current.getTime() - rangeStartTime.getTime();
                    Toast.makeText(ContentListActivity.this, "ビーコン領域限定コンテンツをチェックしました Elapsed: " + elapsed / 1000 + " sec.", Toast.LENGTH_LONG).show();

                    mBeaconManager.stopRangingInitialRegions();
                    mCumulativeBeaconList = null;
                    rangedCount = 0;
                }
            });
        }
    }

    private List<MyContent> prepareDefaultContents() {
        List<MyContent> contents = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            MyContent content = new MyContent(i, "標準コンテンツ", R.drawable.d1, null);
            contents.add(content);
        }
        return contents;
    }

    private List<MyContent> findContentForBeaconRegions() {
        Log.d("called", "findContentForRegions");
        List specialContentList = null;
        boolean useBeacon = getSharedPreferences(PREF_FILE_KEY, Context.MODE_PRIVATE).getBoolean("useBeacon", false);
        if (useBeacon) {
            List<BeaconData> foundBeaconDataList = mCumulativeBeaconList;
            if (foundBeaconDataList != null && !foundBeaconDataList.isEmpty()) {
                Log.d("status", "found !!");
                List<String> beaconKeys = new ArrayList<>();
                for (BeaconData foundBeacon : foundBeaconDataList) {
                    beaconKeys.add(foundBeacon.getKeycode());
                }
                specialContentList = BRSpecificContnents.getRegionSpecificContentByBeacon(beaconKeys);
            }
        }
        return specialContentList;
    }

    @Override
    public void onBeaconScannerConnect() {
        if (mBeaconManager != null) {
            final long timeoutTime = System.currentTimeMillis() + TIMEOUT_INTERVAL;
            mTimeoutTimer = new Timer();
            TimerTask stopRangingTask = new TimerTask() {
                @Override
                public void run() {
                    long current = System.currentTimeMillis();
                    Log.d("status", "diff(the rest for timer) " + Long.toString(timeoutTime - current));
                    if (current >= timeoutTime) {
                        Log.d("status", "Stop !!");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (mProgressIndicator != null) {
                                    mProgressIndicator.dismiss();
                                    mProgressIndicator = null;
                                }
                                // Stop ranging and clear data
                                Toast.makeText(ContentListActivity.this, "タイムアウトしました", Toast.LENGTH_LONG).show();
                                mBeaconManager.stopRangingInitialRegions();
                                mCumulativeBeaconList = null;
                                rangedCount = 0;
                                mTimeoutTimer.cancel();
                                mTimeoutTimer = null;
                            }
                        });
                    }
                }
            };
            // Repeat every sec.
            mTimeoutTimer.schedule(stopRangingTask, 0, 1000);
            rangeStartTime = new Date(System.currentTimeMillis());

            mBeaconManager.startRangingInitialRegions();
            // Return back to main thread
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mBeaconManager == null) {
                        Toast infoToast = Toast.makeText(ContentListActivity.this, "BeaconManager is null", Toast.LENGTH_SHORT);
                        infoToast.show();
                        Log.d("state", "BeaconManager not instantiated");
                        return;
                    } else {
                        mProgressIndicator = new ProgressDialog(ContentListActivity.this);
                        mProgressIndicator.setTitle("コンテンツを確認しています....");
                        mProgressIndicator.setProgressStyle(ProgressDialog.STYLE_SPINNER);
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
