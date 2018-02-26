package com.threebitter.beacons.tbbltsample.dummy;

import com.threebitter.beacons.tbbltsample.MyContent;
import com.threebitter.beacons.tbbltsample.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ueda on 2016/02/17.
 *
 * This is a dummy content store that maps 2 dummy contents by keycode feature.
 *
 */
public class BRSpecificContnents {

    // Change these two sample values
    private static final String SAMPLE_BEACON_KEY_PREFIX_1 = "0000010";
    private static final String SAMPLE_BEACON_KEY_PREFIX_2 = "0000019";


    public static List<MyContent> getRegionSpecificContentByBeacon(List<String> beaconKeys) {
        List<MyContent> specificContents = new ArrayList<>();
            // Just a simple of simple mapping logic
        MyContent dummy1 = null;
        if (beaconKeys.get(0).startsWith(SAMPLE_BEACON_KEY_PREFIX_1)) {
            dummy1 = new MyContent(6, "領域（レンジA）限定コンテンツ", R.drawable.special1, beaconKeys.get(0));
            specificContents.add(dummy1);
        } else if (beaconKeys.get(0).startsWith(SAMPLE_BEACON_KEY_PREFIX_2)) {
            dummy1 = new MyContent(6, "領域（レンジB）限定コンテンツ", R.drawable.special2, beaconKeys.get(0));
            specificContents.add(dummy1);
        }
        if (beaconKeys.size() > 1) { // Random mapping: up to detected beacon proximity
            if (beaconKeys.get(1).startsWith(SAMPLE_BEACON_KEY_PREFIX_1)) {
                MyContent dummy2 = new MyContent(7, "領域（レンジA）限定コンテンツ", R.drawable.special1, beaconKeys.get(1));
                specificContents.add(dummy2);
            }
        }
        return specificContents;
    }
}
