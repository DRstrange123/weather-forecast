package com.example.xialc.weatherforecast;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

public class testDB extends Activity {

    private TextView tv_postion;
    private LocationClient mLocationClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_layout);
        tv_postion = findViewById(R.id.tv_postion);
        initLocation();
        requestLocation();
    }

    private void initLocation() {
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                StringBuilder  currentPosition =  new StringBuilder();
                currentPosition.append("省：").append(bdLocation.getProvince()).append("\n");
                currentPosition.append("市：").append(bdLocation.getCity()).append("\n");
                currentPosition.append("区：").append(bdLocation.getDistrict()).append("\n");
                String city = bdLocation.getCity();
                Toast.makeText(testDB.this,city,Toast.LENGTH_LONG).show();
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
        //mLocationClient.start();
    }

    private void requestLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setIsNeedAddress(true);
        option.setAddrType("all");
        mLocationClient.setLocOption(option);
        mLocationClient.start();

    }
}
