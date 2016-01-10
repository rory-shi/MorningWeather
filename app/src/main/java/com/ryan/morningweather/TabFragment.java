package com.ryan.morningweather;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by rory9 on 2016/1/10.
 */
public class TabFragment extends Fragment {

    ImageButton imgBtn;
    TextView timeTv;
    TextView dateTv;
    TextView weatherTv;
    TextView temperatureTv;
    TextView provinceTv;
    TextView cityTv;

    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();

    public TabFragment(){
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab,container,false);
        initView(view);
        initEvent();
        return view;

    }

    private void initEvent() {
        initBackground();
        initLocation();
        initData();
    }

    private void initData() {
        sendRequestWithHttpClient(new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                String qlty = getQltyFromJson(response);
                String weather = getWeatherFromJson(response);
                String temperature = getTemperatureFromJson(response);
                SharedPreferences.Editor editor =
                        PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                editor.putString("weather_data", response.toString());
                editor.putString("qlty", qlty);
                editor.putString("weather", weather);
                editor.putString("temperature", temperature);
                editor.commit();

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showWeather();
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });

    }


    private void showWeather() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String weather = prefs.getString("weather","请等待");
        String temperature = prefs.getString("temperature","请等待");
        String date = prefs.getString("date","请等待");
        String time = prefs.getString("time","请等待");
        weatherTv.setText(weather);
        temperatureTv.setText(temperature + " °");
        dateTv.setText(date);
        timeTv.setText(time);

        setDefaultBackground(weather);
    }

    private void setDefaultBackground(String weather) {
        switch (weather) {
            case "雾":
                imgBtn.setAlpha(0.1f);
                break;
            case "霾":
                imgBtn.setAlpha(0.1f);
                break;
            case "扬沙":
                imgBtn.setAlpha(0.1f);
                break;
            case "沙尘暴":
                imgBtn.setAlpha(0.1f);
                break;
            case "强沙尘暴":
                imgBtn.setAlpha(0.1f);
                break;
            case "火山灰":
                imgBtn.setAlpha(0.1f);
            default:
                break;
        }
    }

    private void sendRequestWithHttpClient(final HttpCallbackListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                initTime();
                HttpURLConnection connection = null;
                try {
                    URL url = new URL("http://apis.baidu.com/heweather/weather/free?" + "city=" + "hangzhou");
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("apikey","832d7ef40bf399309c9946d515867e80");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    InputStream in = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in,"UTF-8"));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                        response.append("\r\n");
                    }
                    if (listener != null) {
                        listener.onFinish(response.toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    listener.onError(e);
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();

    }

    private void initTime() {
        Date curDate = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM月dd日");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        String date = dateFormat.format(curDate);
        String time = timeFormat.format(curDate);
        SharedPreferences.Editor editor =
                PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
        editor.putString("date", date);
        editor.putString("time", time);
        editor.commit();
    }


    private String getTemperatureFromJson(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather data service 3.0");
            JSONObject objectData = jsonArray.getJSONObject(0);
            JSONObject now = objectData.getJSONObject("now");
            return now.getString("tmp");
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        }
    }

    private String getQltyFromJson(String jsonData) {
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather data service 3.0");
            JSONObject objectData = jsonArray.getJSONObject(0);
            JSONObject objectAqi = objectData.getJSONObject("aqi");
            JSONObject objectCity = objectAqi.getJSONObject("city");
            String qlty = objectCity.getString("qlty");
            return qlty;
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        }

    }

    private String getWeatherFromJson(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather data service 3.0");
            JSONObject objectData = jsonArray.getJSONObject(0);
            JSONObject now = objectData.getJSONObject("now");
            JSONObject cond = now.getJSONObject("cond");
            return  cond.getString("txt");
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        }
    }

    private void initLocation() {
        mLocationClient = new LocationClient(getActivity().getApplicationContext());
        mLocationClient.registerLocationListener(myListener);

        setLocationOption();
        mLocationClient.start();
        mLocationClient.stop();
    }

    private void setLocationOption() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(false);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }

    private void initView(View view) {
        imgBtn = (ImageButton) view.findViewById(R.id.img_btn);
        timeTv = (TextView) view.findViewById(R.id.tv_time);
        dateTv = (TextView) view.findViewById(R.id.tv_date);
        weatherTv = (TextView) view.findViewById(R.id.tv_weather);
        temperatureTv = (TextView) view.findViewById(R.id.tv_temperature);
        provinceTv = (TextView) view.findViewById(R.id.tv_location_big);
        cityTv = (TextView) view.findViewById(R.id.tv_location_small);
    }

    private void initBackground() {
        imgBtn.setOnTouchListener(new View.OnTouchListener() {
            private float mLastY;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:{
                        mLastY = event.getY();
                        break;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        float y = event.getY();
                        float alphaDelta = (y - mLastY) / 1000;
                        float alpha = imgBtn.getAlpha() + alphaDelta;
                        if (alpha > 1.0) {
                            alpha = 1.0f;
                        } else if (alpha < 0.0) {
                            alpha = 0.0f;
                        }imgBtn.setAlpha(alpha);
                    }
                    default:
                        break;
                }
                return true;
            }
        });
    }
}
