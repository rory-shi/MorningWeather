package com.ryan.morningweather;

import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    Button showBtn;
    EditText cityText;
    TextView weatherText;
    String cityName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

        initEvent();
    }

    private void initEvent() {
        showBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cityName = cityText.getText().toString();
                Toast.makeText(MainActivity.this,cityName,Toast.LENGTH_LONG).show();
                sendRequestWithHttpClient(new HttpCallbackListener() {
                    @Override
                    public void onFinish(String response) {
                        SharedPreferences.Editor editor =
                                PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit();
                        editor.putString("weather_data",response.toString());
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
        });
    }

    private void showWeather() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        cityText.setText(prefs.getString("weather_data",""));
    }

    private void sendRequestWithHttpClient(final HttpCallbackListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL("http://apis.baidu.com/heweather/weather/free?" + "city=" + cityName);
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
        showBtn = (Button) findViewById(R.id.btn_click_show);
        cityText = (EditText) findViewById(R.id.et_weather_name);
        weatherText = (TextView) findViewById(R.id.tv_show_weather);
    }
}
