package com.example.xialc.weatherforecast;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;

public class testDB extends Activity {

    private TextView tv_postion;
    private LocationClient mLocationClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_layout);
        tv_postion = findViewById(R.id.tv_postion);
        initLocation();
    }

    private void initLocation() {
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                StringBuilder  currentPosition =  new StringBuilder();
                currentPosition.append("维度：").append(bdLocation.getLatitude()).append("\n");
                currentPosition.append("经度：").append(bdLocation.getLongitude()).append("\n");
                currentPosition.append("定位方式：");
                Log.e("tag","当前的定位方式="+bdLocation.getLocType());

                if(bdLocation.getLocType() == BDLocation.TypeGpsLocation){
                    currentPosition.append("GPS");
                }else if(bdLocation.getLocType() == BDLocation.TypeNetWorkLocation){
                    currentPosition.append("网络");
                }
                tv_postion.setText(currentPosition);
            }
        });
        mLocationClient.start();
    }
}
