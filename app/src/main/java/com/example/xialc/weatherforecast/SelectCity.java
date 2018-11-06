package com.example.xialc.weatherforecast;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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
    private EditText mSearchCityEt;
    private List<City> mCityList;
    private CityDB mCityDB;

    //ArrayList<Map<String, Object>> mData = new ArrayList<Map<String, Object>>();
    //ArrayList<Map<String, Object>> mDataSub = new ArrayList<Map<String, Object>>();
    ArrayList<String> mCityName = new ArrayList<String>();
    ArrayList<String> mCityCode = new ArrayList<String>();
    ArrayList<String> mCityNameSub = new ArrayList<String>();
    ArrayList<String> mCityCodeSub = new ArrayList<String>();

    ArrayAdapter<String> adapter;


    private String[] cityName;
    private String[] cityCode;
    private String[] cityName1;
    private String[] cityCode1;
    private String selectCityCode;
    private static final String TAG = "abc";

    Handler myhandler = new Handler();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_city);

        mCityNameTv = findViewById(R.id.title_name);
        mSearchCityEt = findViewById(R.id.search_edit);
        mBackBtn = findViewById(R.id.title_back);
        mlistView = findViewById(R.id.list_view);
        mBackBtn.setOnClickListener(this);

        //获取城市名称和code信息
        getMyCityData();

        set_mSearchCityEt_TextChanged();//设置mSearchCityEt搜索框的文本改变时监听器

        //给listview装载数据
        adapter = new ArrayAdapter<String>(
                SelectCity.this,R.layout.item,mCityName);

        mlistView.setAdapter(adapter);
        mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(SelectCity.this, "你单击了："+ mCityName.get(position),
                        Toast.LENGTH_SHORT).show();
                selectCityCode = mCityCode.get(position);
                mCityNameTv.setText("当前城市：" + mCityName.get(position));
                //Log.d(TAG,selectCityCode);
            }
        });
    }

    public void set_mSearchCityEt_TextChanged()
    {
        mSearchCityEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                myhandler.post(eChanged);
            }
        });
    }

    Runnable eChanged = new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            String data = mSearchCityEt.getText().toString();
            mCityName.clear();//先要清空，不然会叠加
            mCityCode.clear();
            getmDataSub(mCityName,mCityCode, data);//获取更新数据
            adapter.notifyDataSetChanged();//更新
        }
    };

    private void getmDataSub(ArrayList<String> mCityNames,ArrayList<String> mCityCodes, String data)
    {
        int length = mCityNameSub.size();
        for(int i = 0; i < length; ++i){
            if(mCityNameSub.get(i).contains(data) ){
                Log.d(TAG,"包括"+mCityNameSub.get(i).toString());
                mCityNames.add(mCityNameSub.get(i));
                mCityCodes.add(mCityCodeSub.get(i));
            }
        }
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //后退按钮点击事件
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

    //从数据库中读取信息
    private void getMyCityData(){
        mCityDB = openCityDB();
        mCityList = new ArrayList<City>();
        cityName1 = new  String[2587];
        cityCode1 = new  String[2587];
        mCityList = mCityDB.getAllCity();

        int i=0;
        for (City city : mCityList) {
            cityName1[i] = city.getCity();
            cityCode1[i] = city.getNumber();
            mCityName.add(cityName1[i]);
            mCityCode.add(cityCode1[i]);
            mCityNameSub.add(cityName1[i]);
            mCityCodeSub.add(cityCode1[i]);
            i++;
        }
     }

    public List<City> getCityList() {
        return mCityList;
    }

    private CityDB openCityDB() {
        String path = "/data"
                + Environment.getDataDirectory().getAbsolutePath()
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
