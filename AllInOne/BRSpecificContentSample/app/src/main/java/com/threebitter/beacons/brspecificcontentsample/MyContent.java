package com.threebitter.beacons.brspecificcontentsample;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ueda on 2016/02/18.
 */
public class MyContent implements Parcelable {
    private int contentID;
    private String contentTitle;
    private int contentIcon;
    private String mappedBeaconCode;

    public MyContent(int id, String title, int iconResource, String beaconKey) {
        this.contentID = id;
        this.contentTitle = title;
        this.contentIcon = iconResource;
        if (beaconKey != null) {
            this.mappedBeaconCode = new String(beaconKey);
        }
    }

    public int getContentID() {
        return contentID;
    }

    public String getContentTitle() {
        return contentTitle;
    }

    public int getContentIcon() {
        return contentIcon;
    }

    public String toString() {
        return "[" + contentID + "]" + contentTitle;
    }

    public String getMappedBeaconCode() {
        return mappedBeaconCode;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(contentID);
        dest.writeString(contentTitle);
        dest.writeInt(contentIcon);
        dest.writeString(mappedBeaconCode);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator<MyContent>() {
        public MyContent createFromParcel(Parcel source) {
            return new MyContent(source);
        }

        public MyContent[] newArray(int size) {
            return new MyContent[size];
        }
    };

    private MyContent(Parcel source) {
        this.contentID = source.readInt();
        this.contentTitle = source.readString();
        this.contentIcon = source.readInt();
        this.mappedBeaconCode = source.readString();
    }
}
