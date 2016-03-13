package com.threebitter.beacons.specialcontentsample.layout.dungeon;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ueda on 2016/01/31.
 */
public class DungeonOverview {

    public static List<Dungeon> dungeons = new ArrayList<Dungeon>();

    public DungeonOverview(){
    };

    static {
        // Add some sample items.
        for (int i = 1; i <= 5; i++) {
            addDungeon(createDefaultDungeon(i));
        }
    }

    public static void addDungeon(Dungeon dungeon) {
        dungeons.add(dungeon);
    }

    private static Dungeon createDefaultDungeon(int position) {
        return new Dungeon(String.valueOf(position), "Dungeon " + position, "default");
    }

    public static SpecialDungeon getSpecialDungeonByBeacon(String  beaconRegionId) {
            //TODO: change to scale model
        return new SpecialDungeon("6", "special1", "Special dungeon No.1", "0000018594");
    }

    public static class Dungeon {
        public final String id;
        public final String dungeonName;
        public final String detail;

        Dungeon(String id, String dungeonName, String detail) {
            this.id = id;
            this.dungeonName = dungeonName;
            this.detail = detail;
        }
    }

    public static class SpecialDungeon extends Dungeon{
        public final String beaconRegionId;

        SpecialDungeon(String id, String dungeonName, String detail, String beaconRegionId) {
            super(id, dungeonName, detail);
            this.beaconRegionId = beaconRegionId;
        }
    }
}
