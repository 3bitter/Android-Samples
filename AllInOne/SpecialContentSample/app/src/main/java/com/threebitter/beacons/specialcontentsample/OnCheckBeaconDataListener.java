package com.threebitter.beacons.specialcontentsample;

import com.threebitter.sdk.BeaconData;

import java.util.List;

/**
 * Created by ueda on 2016/02/10.
 */
public interface OnCheckBeaconDataListener {
    void onFinishBeaconRegionCheck(List<BeaconData> beaconDataList);
}
