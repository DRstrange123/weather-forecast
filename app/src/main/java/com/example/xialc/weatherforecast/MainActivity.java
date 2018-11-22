package com.example.xialc.weatherforecast;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.xialc.app.MyApplication;
import com.example.xialc.bean.City;
import com.example.xialc.bean.TodayWeather;
import com.example.xialc.db.CityDB;
import com.example.xialc.util.NetUtil;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener,ViewPager.OnPageChangeListener{
    private static final int UPDATE_TODAY_WEATHER = 1;

    private ImageView mUpdateBtn;

    private ImageView mCitySelect;

    private int pm25value;

    private TextView cityTv, timeTv, humidityTv, weekTv, pmDataTv, pmQualityTv,
            temperatureTv, climateTv, windTv, city_name_Tv;
    private ImageView weatherImg, pmImg, locImg;

    private ProgressBar progressBar;

    private LocationClient mLocationClient;

    private CityDB mCityDB;

    private List<City> mCityList;

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case UPDATE_TODAY_WEATHER:
                    updateTodayWeather((TodayWeather) msg.obj);
                    break;
                default:
                    break;
            }
        }
    };

    //六天天⽓气信息展示
    //显示两个展示⻚页
    private ViewPagerAdapter vpAdapter;
    private ViewPager vp;
    private List<View> views;
    //为引导⻚页增加⼩小圆点
    private ImageView[] dots; //存放⼩小圆点的集合
    private int[] ids = {R.id.iv1,R.id.iv2};
    private TextView
            week_today,temperature,climate,wind,week_today1,temperature1,climate1,wind1
            ,week_today2,temperature2,climate2,wind2;
    private TextView
            week_today3,temperature3,climate3,wind3,week_today4,temperature4,climate4,wind4,week_today5,temperature5,climate5,wind5;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_info);

        progressBar = findViewById(R.id.title_update_progress);
        mUpdateBtn = findViewById(R.id.title_update_btn);
        mUpdateBtn.setOnClickListener(this);
        locImg = findViewById(R.id.title_location);
        locImg.setOnClickListener(this);

        if(NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE){
            Log.d("myWeather","网络ok");
            Toast.makeText(MainActivity.this,"网络ok！", Toast.LENGTH_LONG).show();
        } else {
            Log.d("myWeather","网络挂了");
            Toast.makeText(MainActivity.this,"网络挂了！",Toast.LENGTH_LONG).show();
        }

        mCitySelect = findViewById(R.id.title_city_manager);
        mCitySelect.setOnClickListener(this);

        //初始化两个滑动⻚页⾯面
        initViews();
        //⼩小圆点初始化
        initDots();
        //初始化界⾯面控件
        initView();
    }

    void initView(){
        city_name_Tv =  findViewById(R.id.title_city_name);
        cityTv =  findViewById(R.id.city);
        timeTv =  findViewById(R.id.time);
        humidityTv =  findViewById(R.id.humidity);
        weekTv =  findViewById(R.id.week_today);
        pmDataTv =  findViewById(R.id.pm_data);
        pmQualityTv =  findViewById(R.id.pm2_5_quality);
        pmImg =  findViewById(R.id.pm2_5_img);
        temperatureTv = findViewById(R.id.temperature);
        climateTv = findViewById(R.id.climate);
        windTv =  findViewById(R.id.wind);
        weatherImg =  findViewById(R.id.weather_img);

        city_name_Tv.setText("N/A");
        cityTv.setText("N/A");
        timeTv.setText("N/A");
        humidityTv.setText("N/A");
        pmDataTv.setText("N/A");
        pmQualityTv.setText("N/A");
        weekTv.setText("N/A");
        temperatureTv.setText("N/A");
        climateTv.setText("N/A");
        windTv.setText("N/A");

        //六天天气预报页面初始化
        //
        week_today = views.get(0).findViewById(R.id.week_today);
        temperature = views.get(0).findViewById(R.id.temperature);
        climate = views.get(0).findViewById(R.id.climate);
        wind = views.get(0).findViewById(R.id.wind);
        //
        week_today1 = views.get(0).findViewById(R.id.week_today1);
        temperature1 = views.get(0).findViewById(R.id.temperature1);
        climate1 = views.get(0).findViewById(R.id.climate1);
        wind1 = views.get(0).findViewById(R.id.wind1);
        //
        week_today2 = views.get(0).findViewById(R.id.week_today2);
        temperature2 = views.get(0).findViewById(R.id.temperature2);
        climate2 = views.get(0).findViewById(R.id.climate2);
        wind2 = views.get(0).findViewById(R.id.wind2);
        //
        week_today3 = views.get(1).findViewById(R.id.week_today);
        temperature3 = views.get(1).findViewById(R.id.temperature);
        climate3 = views.get(1).findViewById(R.id.climate);
        wind3 = views.get(1).findViewById(R.id.wind);
        //
        week_today4 = views.get(1).findViewById(R.id.week_today1);
        temperature4 = views.get(1).findViewById(R.id.temperature1);
        climate4 = views.get(1).findViewById(R.id.climate1);
        wind4 = views.get(1).findViewById(R.id.wind1);
        //
        week_today5 = views.get(1).findViewById(R.id.week_today2);
        temperature5 = views.get(1).findViewById(R.id.temperature2);
        climate5 = views.get(1).findViewById(R.id.climate2);
        wind5 = views.get(1).findViewById(R.id.wind2);

        //设置初值
        week_today.setText("N/A");
        week_today1.setText("N/A");
        week_today2.setText("N/A");
        week_today3.setText("N/A");
        week_today4.setText("N/A");
        week_today5.setText("N/A");
        temperature.setText("N/A");
        temperature1.setText("N/A");
        temperature2.setText("N/A");
        temperature3.setText("N/A");
        temperature4.setText("N/A");
        temperature5.setText("N/A");
        climate.setText("N/A");
        climate1.setText("N/A");
        climate2.setText("N/A");
        climate3.setText("N/A");
        climate4.setText("N/A");
        climate5.setText("N/A");
        wind.setText("N/A");
        wind1.setText("N/A");
        wind2.setText("N/A");
        wind3.setText("N/A");
        wind4.setText("N/A");
        wind5.setText("N/A");

    }
    //初始化小圆点
    void initDots() {
        dots = new ImageView[views.size()];
        for (int i = 0; i < views.size(); i++) {
            dots[i] =  findViewById(ids[i]);
        }
    }

    //六天天气信息展示
    private void initViews(){
        LayoutInflater inflater = LayoutInflater.from(this);
        views = new ArrayList<View>();
        views.add(inflater.inflate(R.layout.sixday1,null));
        views.add(inflater.inflate(R.layout.sixday2,null));
        vpAdapter = new ViewPagerAdapter(views,this);
        vp =  findViewById(R.id.viewpager);
        vp.setAdapter(vpAdapter);
        //为pageviewer配置监听事件
        vp.setOnPageChangeListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int
            positionOffsetPixels) {
    }
    @Override
    public void onPageSelected(int position) {
        for (int a = 0;a<ids.length;a++){
            if(a==position){
                dots[a].setImageResource(R.drawable.page_indicator_focused);
            }else {
                dots[a].setImageResource(R.drawable.page_indicator_unfocused);
            }
        }
    }
    @Override
    public void onPageScrollStateChanged(int state) {
    }

    public void onClick(View view){
        if (view.getId() == R.id.title_city_manager){
            Intent i = new Intent(this, SelectCity.class);
            //startActivity(i);
            startActivityForResult(i,1);
        }

        if (view.getId() == R.id.title_update_btn){

            progressBar.setVisibility(View.VISIBLE);
            mUpdateBtn.setVisibility(View.INVISIBLE);

            SharedPreferences sharedPreferences = getSharedPreferences("config",MODE_PRIVATE);
            String cityCode = sharedPreferences.getString("main_city_code","101010100");
            Log.d("myWeather",cityCode);

            if(NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE){
                Log.d("myWeather","网络ok");
                queryWeatherCode(cityCode);
            } else {
                Log.d("myWeather","网络挂了");
                Toast.makeText(MainActivity.this,"网络挂了！",Toast.LENGTH_LONG).show();
            }

        }

        if (view.getId() == R.id.title_location){
           // Intent intent = new Intent(MainActivity.this,testDB.class);
           // MainActivity.this.startActivity(intent);
            progressBar.setVisibility(View.VISIBLE);
            mUpdateBtn.setVisibility(View.INVISIBLE);

            mLocationClient = new LocationClient(getApplicationContext());
            mLocationClient.registerLocationListener(new BDLocationListener() {
                @Override
                public void onReceiveLocation(BDLocation bdLocation) {
                    String city = bdLocation.getCity();
                    String cityname = city.replace("市","");
                    Log.d("loccity",cityname);

                    mCityDB = openCityDB();
                    mCityList = mCityDB.getAllCity();

                    for (City mcity : mCityList) {
                        if (mcity.getCity().equals(cityname)){
                            String cityCode = mcity.getNumber();
                            Log.d("loccity",cityCode);
                            queryWeatherCode(cityCode);
                            break;
                        }
                    }
                }
            });
            LocationClientOption option = new LocationClientOption();
            option.setIsNeedAddress(true);
            option.setAddrType("all");
            mLocationClient.setLocOption(option);
            mLocationClient.start();
        }

    }

    private CityDB openCityDB() {
        String path = "/data"
                + Environment.getDataDirectory().getAbsolutePath()
                + File.separator + getPackageName()
                + File.separator + "databases1"
                + File.separator
                + CityDB.CITY_DB_NAME;
        File db = new File(path);
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String newCityCode= data.getStringExtra("cityCode");
            Log.d("myWeather", "选择的城市代码为"+newCityCode);
            if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
                Log.d("myWeather", "网络OK");
                queryWeatherCode(newCityCode);
            } else {
                Log.d("myWeather", "网络挂了");
                Toast.makeText(MainActivity.this, "网络挂了！", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void queryWeatherCode(String cityCode) {
        final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" + cityCode;
        Log.d("myWeather", address);
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection con=null;
                TodayWeather todayWeather = null;
                try{
                    URL url = new URL(address);
                    con = (HttpURLConnection)url.openConnection();
                    con.setRequestMethod("GET");
                    con.setConnectTimeout(8000);
                    con.setReadTimeout(8000);
                    InputStream in = con.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String str;
                    while((str=reader.readLine()) != null){
                        response.append(str);
                        Log.d("myWeather", str);
                    }
                    String responseStr = response.toString();
                    Log.d("myWeather", responseStr);

                    todayWeather = parseXML(responseStr);
                    if (todayWeather != null){
                        Log.d("myWeather",todayWeather.toString());

                        Message msg = new Message();
                        msg.what = UPDATE_TODAY_WEATHER;
                        msg.obj=todayWeather;
                        mHandler.sendMessage(msg);
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    if(con != null){
                        con.disconnect();
                    }
                }
            }
        }).start();
    }

    private TodayWeather parseXML(String xmldata){
        TodayWeather todayWeather = null;
        int fengxiangCount = 0;
        int fenglliCount = 0;
        int dateCount = 0;
        int highCount = 0;
        int lowCount = 0;
        int typeCount = 0;
        try {
            XmlPullParserFactory fac = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = fac.newPullParser();
            xmlPullParser.setInput(new StringReader(xmldata));
            int evenType = xmlPullParser.getEventType();
            Log.d("myWeather","parseXML");
            while (evenType != XmlPullParser.END_DOCUMENT){
                switch (evenType){
                    //判断当前事件是否为文档开始事件
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    //判断当前事件是否为标签元素开始事件
                    case XmlPullParser.START_TAG:
                        if(xmlPullParser.getName().equals("resp")){
                            todayWeather= new TodayWeather();
                        }
                        if (todayWeather != null) {
                            if (xmlPullParser.getName().equals("city")) {
                                evenType = xmlPullParser.next();
                                todayWeather.setCity(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("updatetime")) {
                                evenType = xmlPullParser.next();
                                todayWeather.setUpdatetime(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("shidu")) {
                                evenType = xmlPullParser.next();
                                todayWeather.setShidu(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("wendu")) {
                                evenType = xmlPullParser.next();
                                todayWeather.setWendu(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("pm25")) {
                                evenType = xmlPullParser.next();
                                todayWeather.setPm25(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("quality")) {
                                evenType = xmlPullParser.next();
                                todayWeather.setQuality(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("fengxiang") && fengxiangCount == 0) {
                                evenType = xmlPullParser.next();
                                todayWeather.setFengxiang(xmlPullParser.getText());
                                fengxiangCount++;
                            } else if (xmlPullParser.getName().equals("fengli") && fenglliCount == 0) {
                                evenType = xmlPullParser.next();
                                todayWeather.setFengli(xmlPullParser.getText());
                                todayWeather.setWind(xmlPullParser.getText());
                                fenglliCount++;
                            } else if (xmlPullParser.getName().equals("fengli") && fenglliCount == 1) {
                                evenType = xmlPullParser.next();
                                todayWeather.setWind1(xmlPullParser.getText());
                                fenglliCount++;
                            } else if (xmlPullParser.getName().equals("fengli") && fenglliCount == 2) {
                                evenType = xmlPullParser.next();
                                todayWeather.setWind2(xmlPullParser.getText());
                                fenglliCount++;
                            } else if (xmlPullParser.getName().equals("fengli") && fenglliCount == 3) {
                                evenType = xmlPullParser.next();
                                todayWeather.setWind3(xmlPullParser.getText());
                                fenglliCount++;
                            } else if (xmlPullParser.getName().equals("fengli") && fenglliCount == 4) {
                                evenType = xmlPullParser.next();
                                todayWeather.setWind4(xmlPullParser.getText());
                                fenglliCount++;
                            } else if (xmlPullParser.getName().equals("fengli") && fenglliCount == 5) {
                                evenType = xmlPullParser.next();
                                todayWeather.setWind5(xmlPullParser.getText());
                                fenglliCount++;
                            } else if (xmlPullParser.getName().equals("fengli") && fenglliCount == 0) {
                                evenType = xmlPullParser.next();
                                todayWeather.setFengli(xmlPullParser.getText());
                                fenglliCount++;
                            } else if (xmlPullParser.getName().equals("date") && dateCount == 0) {
                                evenType = xmlPullParser.next();
                                todayWeather.setDate(xmlPullParser.getText());
                                todayWeather.setWeek_today(xmlPullParser.getText());
                                dateCount++;
                            } else if (xmlPullParser.getName().equals("date") && dateCount == 1) {
                                evenType = xmlPullParser.next();
                                todayWeather.setWeek_today1(xmlPullParser.getText());
                                dateCount++;
                            } else if (xmlPullParser.getName().equals("date") && dateCount == 2) {
                                evenType = xmlPullParser.next();
                                todayWeather.setWeek_today2(xmlPullParser.getText());
                                dateCount++;
                            } else if (xmlPullParser.getName().equals("date") && dateCount == 3) {
                                evenType = xmlPullParser.next();
                                todayWeather.setWeek_today3(xmlPullParser.getText());
                                dateCount++;
                            } else if (xmlPullParser.getName().equals("date") && dateCount == 4) {
                                evenType = xmlPullParser.next();
                                todayWeather.setWeek_today4(xmlPullParser.getText());
                                dateCount++;
                            } else if (xmlPullParser.getName().equals("date") && dateCount == 5) {
                                evenType = xmlPullParser.next();
                                todayWeather.setWeek_today5(xmlPullParser.getText());
                                dateCount++;
                            } else if (xmlPullParser.getName().equals("high") && highCount == 0) {
                                evenType = xmlPullParser.next();
                                todayWeather.setHigh(xmlPullParser.getText());
                                todayWeather.setTemperatureH(xmlPullParser.getText());
                                highCount++;
                            } else if (xmlPullParser.getName().equals("high") && highCount == 1) {
                                evenType = xmlPullParser.next();
                                todayWeather.setTemperatureH1(xmlPullParser.getText());
                                highCount++;
                            } else if (xmlPullParser.getName().equals("high") && highCount == 2) {
                                evenType = xmlPullParser.next();
                                todayWeather.setTemperatureH2(xmlPullParser.getText());
                                highCount++;
                            } else if (xmlPullParser.getName().equals("high") && highCount == 3) {
                                evenType = xmlPullParser.next();
                                todayWeather.setTemperatureH3(xmlPullParser.getText());
                                highCount++;
                            } else if (xmlPullParser.getName().equals("high") && highCount == 4) {
                                evenType = xmlPullParser.next();
                                todayWeather.setTemperatureH4(xmlPullParser.getText());
                                highCount++;
                            } else if (xmlPullParser.getName().equals("high") && highCount == 5) {
                                evenType = xmlPullParser.next();
                                todayWeather.setTemperatureH5(xmlPullParser.getText());
                                highCount++;
                            } else if (xmlPullParser.getName().equals("low") && lowCount == 0) {
                                evenType = xmlPullParser.next();
                                todayWeather.setLow(xmlPullParser.getText());
                                todayWeather.setTemperatureL(xmlPullParser.getText());
                                lowCount++;
                            } else if (xmlPullParser.getName().equals("low") && lowCount == 1) {
                                evenType = xmlPullParser.next();
                                todayWeather.setTemperatureL1(xmlPullParser.getText());
                                lowCount++;
                            } else if (xmlPullParser.getName().equals("low") && lowCount == 2) {
                                evenType = xmlPullParser.next();
                                todayWeather.setTemperatureL2(xmlPullParser.getText());
                                lowCount++;
                            } else if (xmlPullParser.getName().equals("low") && lowCount == 3) {
                                evenType = xmlPullParser.next();
                                todayWeather.setTemperatureL3(xmlPullParser.getText());
                                lowCount++;
                            } else if (xmlPullParser.getName().equals("low") && lowCount == 4) {
                                evenType = xmlPullParser.next();
                                todayWeather.setTemperatureL4(xmlPullParser.getText());
                                lowCount++;
                            } else if (xmlPullParser.getName().equals("low") && lowCount == 5) {
                                evenType = xmlPullParser.next();
                                todayWeather.setTemperatureL5(xmlPullParser.getText());
                                lowCount++;
                            } else if (xmlPullParser.getName().equals("type") && typeCount == 0) {
                                evenType = xmlPullParser.next();
                                todayWeather.setType(xmlPullParser.getText());
                                todayWeather.setClimate(xmlPullParser.getText());
                                typeCount++;
                            } else if (xmlPullParser.getName().equals("type") && typeCount == 1) {
                                evenType = xmlPullParser.next();
                                todayWeather.setClimate1(xmlPullParser.getText());
                                typeCount++;
                            } else if (xmlPullParser.getName().equals("type") && typeCount == 2) {
                                evenType = xmlPullParser.next();
                                todayWeather.setClimate2(xmlPullParser.getText());
                                typeCount++;
                            } else if (xmlPullParser.getName().equals("type") && typeCount == 3) {
                                evenType = xmlPullParser.next();
                                todayWeather.setClimate3(xmlPullParser.getText());
                                typeCount++;
                            } else if (xmlPullParser.getName().equals("type") && typeCount == 4) {
                                evenType = xmlPullParser.next();
                                todayWeather.setClimate4(xmlPullParser.getText());
                                typeCount++;
                            } else if (xmlPullParser.getName().equals("type") && typeCount == 5) {
                                evenType = xmlPullParser.next();
                                todayWeather.setClimate5(xmlPullParser.getText());
                                typeCount++;
                            }
                        }
                        break;
                //判断当前事件是否为标签元素结束事件
                    case XmlPullParser.END_TAG:
                        break;
                }
                //进入下一个元素并触发相应事件
                evenType = xmlPullParser.next();
            }
        }catch (XmlPullParserException e ){
            e.printStackTrace();
        }catch ( IOException e){
            e.printStackTrace();
        }
        return todayWeather;
    }

    void updateTodayWeather(TodayWeather todayWeather){
        city_name_Tv.setText(todayWeather.getCity()+"天气");
        cityTv.setText(todayWeather.getCity());
        timeTv.setText(todayWeather.getUpdatetime()+ "发布");
        humidityTv.setText("湿度："+todayWeather.getShidu());
        pmDataTv.setText(todayWeather.getPm25());
        pmQualityTv.setText(todayWeather.getQuality());
        weekTv.setText(todayWeather.getDate());
        temperatureTv.setText(todayWeather.getLow().replace("低温","")+"~"+
                todayWeather.getHigh().replace("高温",""));
        climateTv.setText(todayWeather.getType());
        windTv.setText("风力:"+todayWeather.getFengli());
        if (todayWeather.getType().equals("暴雪")){weatherImg.setImageResource(R.drawable.biz_plugin_weather_baoxue);}
        if (todayWeather.getType().equals("暴雨")){weatherImg.setImageResource(R.drawable.biz_plugin_weather_baoyu);}
        if (todayWeather.getType().equals("大暴雨")){weatherImg.setImageResource(R.drawable.biz_plugin_weather_dabaoyu);}
        if (todayWeather.getType().equals("大雪")){weatherImg.setImageResource(R.drawable.biz_plugin_weather_daxue);}
        if (todayWeather.getType().equals("大雨")){weatherImg.setImageResource(R.drawable.biz_plugin_weather_dayu);}
        if (todayWeather.getType().equals("多云")){weatherImg.setImageResource(R.drawable.biz_plugin_weather_duoyun);}
        if (todayWeather.getType().equals("雷阵雨")){weatherImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyu);}
        if (todayWeather.getType().equals("雷阵雨冰雹")){weatherImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyubingbao);}
        if (todayWeather.getType().equals("晴")){weatherImg.setImageResource(R.drawable.biz_plugin_weather_qing);}
        if (todayWeather.getType().equals("沙尘暴")){weatherImg.setImageResource(R.drawable.biz_plugin_weather_shachenbao);}
        if (todayWeather.getType().equals("特大暴雨")){weatherImg.setImageResource(R.drawable.biz_plugin_weather_tedabaoyu);}
        if (todayWeather.getType().equals("雾")){weatherImg.setImageResource(R.drawable.biz_plugin_weather_wu);}
        if (todayWeather.getType().equals("小雪")){weatherImg.setImageResource(R.drawable.biz_plugin_weather_xiaoxue);}
        if (todayWeather.getType().equals("小雨")){weatherImg.setImageResource(R.drawable.biz_plugin_weather_xiaoyu);}
        if (todayWeather.getType().equals("阴")){weatherImg.setImageResource(R.drawable.biz_plugin_weather_yin);}
        if (todayWeather.getType().equals("雨夹雪")){weatherImg.setImageResource(R.drawable.biz_plugin_weather_yujiaxue);}
        if (todayWeather.getType().equals("阵雪")){weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhenxue);}
        if (todayWeather.getType().equals("阵雨")){weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhenyu);}
        if (todayWeather.getType().equals("中雪")){weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhongxue);}
        if (todayWeather.getType().equals("中雨")){weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhongyu);}

        if(todayWeather.getPm25()!=null) {
            pm25value = Integer.parseInt(todayWeather.getPm25());
            if (pm25value >= 0 && pm25value <= 50) {
                pmImg.setImageResource(R.drawable.biz_plugin_weather_0_50);
            } else if (pm25value <= 100) {
                pmImg.setImageResource(R.drawable.biz_plugin_weather_51_100);
            } else if (pm25value <= 150) {
                pmImg.setImageResource(R.drawable.biz_plugin_weather_101_150);
            } else if (pm25value <= 200) {
                pmImg.setImageResource(R.drawable.biz_plugin_weather_151_200);
            } else if (pm25value <= 300) {
                pmImg.setImageResource(R.drawable.biz_plugin_weather_201_300);
            } else {
                pmImg.setImageResource(R.drawable.biz_plugin_weather_greater_300);
            }
        }else {
            pmImg.setImageResource(R.drawable.biz_plugin_weather_0_50);
        }
        //新增六天天气信息
        wind.setText(todayWeather.getWind());
        wind1.setText(todayWeather.getWind1());
        wind2.setText(todayWeather.getWind2());
        wind3.setText(todayWeather.getWind3());
        wind4.setText(todayWeather.getWind4());
        wind5.setText(todayWeather.getWind());
        climate.setText(todayWeather.getClimate());
        climate1.setText(todayWeather.getClimate1());
        climate2.setText(todayWeather.getClimate2());
        climate3.setText(todayWeather.getClimate3());
        climate4.setText(todayWeather.getClimate4());
        climate5.setText(todayWeather.getClimate());
        week_today.setText(todayWeather.getWeek_today());
        week_today1.setText(todayWeather.getWeek_today1());
        week_today2.setText(todayWeather.getWeek_today2());
        week_today3.setText(todayWeather.getWeek_today3());
        week_today4.setText(todayWeather.getWeek_today4());
        week_today5.setText(todayWeather.getWeek_today());
        temperature.setText(todayWeather.getTemperatureL().replace("低温","")+"~"+ todayWeather.getTemperatureH().replace("高温",""));
        temperature1.setText(todayWeather.getTemperatureL1().replace("低温","")+"~"+ todayWeather.getTemperatureH1().replace("高温",""));
        temperature2.setText(todayWeather.getTemperatureL2().replace("低温","")+"~"+ todayWeather.getTemperatureH2().replace("高温",""));
        temperature3.setText(todayWeather.getTemperatureL3().replace("低温","")+"~"+ todayWeather.getTemperatureH3().replace("高温",""));
        temperature4.setText(todayWeather.getTemperatureL4().replace("低温","")+"~"+ todayWeather.getTemperatureH4().replace("高温",""));
        temperature5.setText(todayWeather.getTemperatureL().replace("低温","")+"~"+ todayWeather.getTemperatureH().replace("高温",""));

        Toast.makeText(MainActivity.this,"更新成功！",Toast.LENGTH_SHORT).show();
        progressBar.setVisibility(View.INVISIBLE);
        mUpdateBtn.setVisibility(View.VISIBLE);
    }

}
