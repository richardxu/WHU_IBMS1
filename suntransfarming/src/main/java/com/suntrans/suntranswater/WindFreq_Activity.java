package com.suntrans.suntranswater;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;

/**
 * Created by 石奋斗 on 2016/5/8.
 */
public class WindFreq_Activity extends AppCompatActivity {
    private views.RadarChart01View radarview;   //雷达图
    private LinearLayout layout;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.windfreq);


    }

}
