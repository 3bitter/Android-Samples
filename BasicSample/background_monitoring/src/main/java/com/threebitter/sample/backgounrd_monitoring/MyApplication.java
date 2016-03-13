package com.threebitter.sample.backgounrd_monitoring;

import android.app.Application;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;

import com.threebitter.sdk.BeaconConsumer;
import com.threebitter.sdk.BeaconManager;
import com.threebitter.sdk.BeaconMonitorNotifier;
import com.threebitter.sdk.BeaconRegion;
import com.threebitter.sdk.IBeaconManager;
import com.threebitter.sdk.utils.StartUp;

/**
 * Created by yohei on 2016/01/09.
 */
public class MyApplication extends Application implements BeaconConsumer, BeaconMonitorNotifier {

    @Override
    public void onCreate() {
        super.onCreate();
        StartUp.init(this); // ApplicationがBeaconConsumerを実装している場合自動でbindされます。
        IBeaconManager manager = BeaconManager.getInstance(getApplicationContext());
        if (manager != null) {
            // managerがある場合は、自身をNotifierとして設定する。
            manager.setMonitorNotifier(this);
        }
    }

    @Override
    public void onBeaconScannerConnect() {
        Toast.makeText(this, "Application was Connect.", Toast.LENGTH_SHORT).show();
        final IBeaconManager manager = BeaconManager.getInstance(this);
        if (manager != null && manager.startMonitoringInitialRegions()) {
            Toast.makeText(this, "Start Monitoring.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void didEnter(BeaconRegion beaconRegion) {
        final Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Beacon was entered.")
                .setContentText("Beacon was entered!!!")
                .setContentIntent(PendingIntent.getActivity(this, 1, new Intent(this, MainActivity.class), 0))
                .build();
        NotificationManagerCompat.from(this).notify(12345, notification);
    }

    @Override
    public void didExit(BeaconRegion beaconRegion) {
    }

    @Override
    public void didDetermineStateForRegion(int i, BeaconRegion beaconRegion) {
        final Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("didDetermineState" + Integer.toString(i))
                .setContentText("DetermineState Callback")
                .setContentIntent(PendingIntent.getActivity(this, 1, new Intent(this, MainActivity.class), 0))
                .build();
        NotificationManagerCompat.from(this).notify(12346, notification);
    }

    @Override
    public void didNotFoundTarget(BeaconRegion beaconRegion) {
    }
}
