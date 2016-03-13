package com.threebitter.beacons.brspecificcontentsample.dummy;

import com.threebitter.beacons.brspecificcontentsample.MyContent;
import com.threebitter.beacons.brspecificcontentsample.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ueda on 2016/02/17.
 */
public class BRSpecificContnents {
        public static List<MyContent> getRegionSpecificContentByBeacon(List<String> beaconKeys) {
            List<MyContent> specificContents = new ArrayList<>();
            // Just a simple of simple mapping logic
            MyContent dummy1 = null;
            if (beaconKeys.get(0).startsWith("0000018")) {
                 dummy1 = new MyContent(6, "領域（レンジA）限定コンテンツ", R.drawable.special1, beaconKeys.get(0));
                specificContents.add(dummy1);
            } else if (beaconKeys.get(0).startsWith("0000019")) {
                dummy1 = new MyContent(6, "領域（レンジB）限定コンテンツ", R.drawable.special2, beaconKeys.get(0));
                specificContents.add(dummy1);
            }
            if (beaconKeys.size() > 1) { // Random mapping: up to detected beacon proximity
                if (beaconKeys.get(1).startsWith("0000018")) {
                    MyContent dummy2 = new MyContent(7, "領域（レンジA）限定コンテンツ", R.drawable.special1, beaconKeys.get(1));
                    specificContents.add(dummy2);
                }
            }
            return specificContents;
        }
}
