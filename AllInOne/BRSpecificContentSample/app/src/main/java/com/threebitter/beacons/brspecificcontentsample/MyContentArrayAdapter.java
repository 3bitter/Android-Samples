package com.threebitter.beacons.brspecificcontentsample;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ueda on 2016/02/18.
 */
public class MyContentArrayAdapter extends ArrayAdapter {

    private static LayoutInflater inflater;

    private int resourceID;
    private int textViewId;
    private List<MyContent> contents;

    public MyContentArrayAdapter(Context context, int resource, int textViewResourceId, List<MyContent> objects) {
        super(context, resource, textViewResourceId, objects);
        this.resourceID = resource;
        this.textViewId = textViewResourceId;
        this.contents = objects;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView != null) {
            return convertView;
        }
        View contentTitleView = inflater.inflate(R.layout.content_row, null);
        MyContent content = contents.get(position);
        ImageView iconView = (ImageView)contentTitleView.findViewById(R.id.contentIcon);
        iconView.setImageResource(content.getContentIcon());
        TextView titleView = (TextView)contentTitleView.findViewById(R.id.contentTitle);
        titleView.setText(content.getContentTitle());
        TextView descriptionView = (TextView)contentTitleView.findViewById(R.id.contentDesc);
        descriptionView.setText(content.getMappedBeaconCode());
        return contentTitleView;
    }
}
