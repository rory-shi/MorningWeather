package com.ryan.morningweather;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;


public class MainActivity extends AppCompatActivity {

    ImageButton imgBtn;
    ImageButton imgBtnBlur;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        initView();
        initEvent();
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
                    imgBtn.setTranslationX(-position*(pageWidth/2));
                    imgBtnBlur.setTranslationX(-position*(pageWidth/2));
                } else {
                    page.setAlpha(1);
                }
            }
        });
        initViewPager();
    }

    private void initViewPager() {
        viewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return new TabFragment();
            }

            @Override
            public int getCount() {
                return 3;
            }
        });
    }


}
