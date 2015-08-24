package com.franguage.admin.test01.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;

import com.franguage.admin.test01.Adapter.ViewPagerAdapter;
import com.franguage.admin.test01.MainActivity;
import com.franguage.admin.test01.R;
import com.viewpagerindicator.CirclePageIndicator;

/**
 * Created by Admin on 2015-08-19.
 */
public class IntroActivity extends Activity {

    ViewPager mViewPager;
    PagerAdapter mAdapter;

    int[] mImage;
    Button[] mButton;
    CirclePageIndicator mIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewpager_main);

        mImage = new int[]{
                R.drawable.viewpager01,
                R.drawable.viewpager02,
                R.drawable.viewpager03
        };

        mButton = new Button[]{
                (Button) findViewById(R.id.btnSkip),
                (Button) findViewById(R.id.btnStart),
                (Button) findViewById(R.id.btnSignUp)
        };

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mAdapter = new ViewPagerAdapter(IntroActivity.this, mImage);

        mViewPager.setAdapter(mAdapter);

        mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
        mIndicator.setViewPager(mViewPager);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSkip:
                Intent iSkip = new Intent(IntroActivity.this, MainActivity.class);
                startActivity(iSkip);
                break;
            case R.id.btnStart:
                Intent iStart = new Intent(IntroActivity.this, LoginActivity.class);
                startActivity(iStart);
                break;
            case R.id.btnSignUp:
                Intent iReg = new Intent(IntroActivity.this, RegisterActivity.class);
                startActivity(iReg);
                break;
        }

    }

    public void onPause() {
        super.onPause();
        finish();
    }
}
