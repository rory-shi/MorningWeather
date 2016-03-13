package com.ryan.morningweather;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by rory9 on 2016/2/7.
 */
public class AddCityActivity extends Activity{
    EditText etCityName;
    Button btnCheckEnter;
    String cityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_city);

        etCityName = (EditText) findViewById(R.id.et_new_city_name);
        btnCheckEnter = (Button) findViewById(R.id.btn_check_enter);

        btnCheckEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cityName = etCityName.getText().toString();

                Toast.makeText(AddCityActivity.this,"Adding city name..." + cityName,Toast.LENGTH_LONG).show();

                Intent intent = new Intent(AddCityActivity.this,MainActivity.class);
                intent.putExtra("CITY_NAME",cityName);
                startActivity(intent);
            }
        });
    }
}
