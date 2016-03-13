package com.threebitter.beacons.brspecificcontentsample;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ContentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        MyContent targetContent = intent.getParcelableExtra("content");

        TextView titleView = (TextView) findViewById(R.id.contentDetailTitle);
        TextView messageView = (TextView) findViewById(R.id.contentDetail);
        ImageView imageView = (ImageView) findViewById(R.id.contentDetailImage);
        String message = "標準のコンテンツです";
        if (targetContent != null) {
            titleView.setText(targetContent.getContentTitle());
            if (targetContent.getMappedBeaconCode() != null) {
                message = "ビーコン [" + targetContent.getMappedBeaconCode() +
                        "] の領域内なので取得されたコンテンツです";
            }
            imageView.setImageResource(targetContent.getContentIcon());
        } else {
            message = "対象が見つかりません";
            titleView.setText(message);
            imageView.setImageResource(0);
        }
        messageView.setText(message);
    }

}
