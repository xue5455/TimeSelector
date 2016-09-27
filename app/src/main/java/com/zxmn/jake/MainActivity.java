package com.zxmn.jake;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.zxmn.time.view.WheelView;

public class MainActivity extends AppCompatActivity {

    WheelView wheelView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wheelView = (WheelView) findViewById(R.id.wheel_view);
        wheelView.setAdapter(new MyAdapter(getResources().getDimensionPixelSize(R.dimen.item_height)));
        wheelView.setCurrentPosition(0);
    }
}
