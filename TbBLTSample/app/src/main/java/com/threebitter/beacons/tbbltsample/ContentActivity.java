package com.threebitter.beacons.tbbltsample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
