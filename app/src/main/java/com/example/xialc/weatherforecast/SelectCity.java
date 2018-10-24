package com.example.xialc.weatherforecast;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xialc.bean.City;
import com.example.xialc.db.CityDB;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SelectCity extends Activity implements View.OnClickListener{

    private ImageView mBackBtn;
    private ListView mlistView;
    private TextView mCityNameTv;
    private String[] data;
    // private String[] data = {"第一组","第二组","第三组","第四组","第五组","第六组"};
    private String[] cityName1;
    private String[]  cityCode1;
    private String[] cityCode;
    private String selectCityCode;


    private static final String TAG = "abc";
    private CityDB mCityDB;
    private List<City> mCityList;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.select_city);

        mCityNameTv = findViewById(R.id.title_name);
        mBackBtn = findViewById(R.id.title_back);
        mBackBtn.setOnClickListener(this);

        data = getMyCityData(1);
        cityCode = getMyCityData(0);

        mlistView = findViewById(R.id.list_view);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                SelectCity.this,R.layout.item,data);
        mlistView.setAdapter(adapter);
        mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(SelectCity.this, "你单击了："+ data[position],
                        Toast.LENGTH_SHORT).show();
                selectCityCode = cityCode[position];
                mCityNameTv.setText("当前城市：" + data[position]);
                //Log.d(TAG,selectCityCode);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_back:
                Intent i = new Intent();
                i.putExtra("cityCode",selectCityCode);
                //i.putExtra("cityCode","101160101");
                setResult(RESULT_OK,i);
                finish();
                break;
            default:
                break;
        }
    }

    private String[] getMyCityData(int flag){
        mCityDB = openCityDB();
        mCityList = new ArrayList<City>();
        cityName1 = new  String[2587];
        cityCode1 = new  String[2587];
        mCityList = mCityDB.getAllCity();

        int i=0;
        for (City city : mCityList) {
            cityName1[i] = city.getCity();
            cityCode1[i] = city.getNumber();
            i++;
            //Log.d(TAG,cityCode+":"+cityName);
        }
        Log.d(TAG,"i="+i);

        if (flag==1){return cityName1;}
        if (flag==0){ return cityCode1;}
        return null;
     }

    public List<City> getCityList() {
        return mCityList;
    }

    private CityDB openCityDB() {
        String path = "/data"
                + Environment.getDataDirectory().getAbsolutePath
                ()
                + File.separator + getPackageName()
                + File.separator + "databases1"
                + File.separator
                + CityDB.CITY_DB_NAME;
        File db = new File(path);
        Log.d(TAG,path);
        if (!db.exists()) {

            String pathfolder = "/data"
                    + Environment.getDataDirectory().getAbsolutePath()
                    + File.separator + getPackageName()
                    + File.separator + "databases1"
                    + File.separator;
            File dirFirstFolder = new File(pathfolder);
            if(!dirFirstFolder.exists()){
                dirFirstFolder.mkdirs();
            }
            try {
                InputStream is = getAssets().open("city.db");
                FileOutputStream fos = new FileOutputStream(db);
                int len = -1;
                byte[] buffer = new byte[1024];
                while ((len = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                    fos.flush();
                }
                fos.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(0);
            }
        }
        return new CityDB(this, path);
    }
}
