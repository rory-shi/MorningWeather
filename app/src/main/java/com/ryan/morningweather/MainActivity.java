package com.ryan.morningweather;

import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    ImageButton imgBtn;
    TextView timeTv;
    TextView dateTv;
    TextView weatherTv;
    TextView temperatureTv;
    TextView provinceTv;
    TextView cityTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

        initEvent();
    }

    private void initEvent() {
        initBackground();

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
                        PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit();
                editor.putString("weather_data", response.toString());
                editor.putString("qlty", qlty);
                editor.putString("weather", weather);
                editor.putString("temperature", temperature);
                editor.commit();

                runOnUiThread(new Runnable() {
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

    private void initTime() {
        Date curDate = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM月dd日");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        String date = dateFormat.format(curDate);
        String time = timeFormat.format(curDate);
        SharedPreferences.Editor editor =
                PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit();
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



    private void showWeather() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
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


    private void initView() {
        imgBtn = (ImageButton) findViewById(R.id.img_btn);
        timeTv = (TextView) findViewById(R.id.tv_time);
        dateTv = (TextView) findViewById(R.id.tv_date);
        weatherTv = (TextView) findViewById(R.id.tv_weather);
        temperatureTv = (TextView) findViewById(R.id.tv_temperature);
        provinceTv = (TextView) findViewById(R.id.tv_location_big);
        cityTv = (TextView) findViewById(R.id.tv_location_small);
    }
}
