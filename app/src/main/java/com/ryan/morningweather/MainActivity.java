package com.ryan.morningweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private ImageButton imgBtn;
    private ImageButton imgBtnBlur;
    private ViewPager viewPager;
    private PagerAdapter myPagerAdapter;
    private ArrayList<Fragment> fragments;
    private ArrayList<String> cityLists;

    private int cityCount;

    public static final String CITY_COUNT = "CITY_COUNT";
    public static final String CITY_NAME = "CITY_NAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        initEvent();
    }



    private void initData() {
        SharedPreferences prefs = getSharedPreferences("CITY_DATA",MODE_PRIVATE);

        if (this.getIntent()!=null) {
            Intent intent = this.getIntent();
            String cityName = intent.getStringExtra(CITY_NAME);
            SharedPreferences.Editor editor = prefs.edit();
            int count = prefs.getInt(CITY_COUNT,0);
            editor.putInt(CITY_COUNT,++count);
            editor.putString(count + "_CITY", cityName);
            editor.commit();
        }
        if (fragments == null) {
            fragments = new ArrayList<>();
        }
        if (cityLists == null) {
            cityLists = new ArrayList<>();
        }
        cityCount = prefs.getInt(CITY_COUNT,0);
        for (int i=0;i<cityCount;i++) {
            String cityName = prefs.getString(i+"_CITY","BEIJING");
            cityLists.add(cityName.toLowerCase());
        }


        for (String city :cityLists) {
            Fragment tab = new TabFragment();

            Bundle bundle = new Bundle();
            bundle.putString("cityName",city);
            tab.setArguments(bundle);
            fragments.add(tab);
        }
    }


    private void initView() {
        viewPager = (ViewPager) findViewById(R.id.view_pager);
    }

    private void initEvent() {
        viewPager.setPageTransformer(false, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View page, float position) {
                int pageWidth = page.getWidth();
                if (position <= -1) {
                    page.setAlpha(1);
                } else if (position <= 1) {
                    imgBtn = (ImageButton) page.findViewById(R.id.img_btn);
                    imgBtnBlur = (ImageButton) page.findViewById(R.id.img_blur);
                    imgBtn.setTranslationX(-position * (pageWidth / 2));
                    imgBtnBlur.setTranslationX(-position * (pageWidth / 2));
                } else {
                    page.setAlpha(1);
                }
            }
        });
        initViewPager();
    }



    private void initViewPager() {
        myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager(),fragments);
        viewPager.setAdapter(myPagerAdapter);
    }


}
