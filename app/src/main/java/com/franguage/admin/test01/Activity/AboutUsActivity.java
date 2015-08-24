package com.franguage.admin.test01.Activity;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.franguage.admin.test01.R;

public class AboutUsActivity extends ActionBarActivity {

    private Toolbar mToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);

        final ActionBar mActionbar = getSupportActionBar();

        mActionbar.setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_24dp);
        mActionbar.setDisplayHomeAsUpEnabled(true);

        TextView mText = (TextView) findViewById(R.id.aboutus);
        mText.setText("MY LANGUAGE + YOUR LANGUAGE" + "\n\n" + "OUR FRIENDSHIP\n" +
                "\n\"외국인 친구와 함께하는 오프라인 언어교환\"\n" +
                "\n" +
                "\n" +
                "Project Manager" + "\n" + "Yeojin Seo Sangjong Lee\n\n" +
                "Designer" + "\n" + "Hyemin Kim\n\n" +
                "Developer" + "\n" + "Sunwoong Bae Hunjin Kim\n\n" +
                "Special thanks to" + "\n" + "Mento Hyosoo Song\n\n" +
                "\n" +
                "끝까지 다같이 오자고 해서 \n" +
                "여기까지 잘 버틴 우리에게\n" +
                "격려의 셀프 박수를 보내며\n" +
                "멘토님 득남 축하드립니다\n" +
                "\n\n\n" +
                "Copyright ⓒ 2015 Night Owls. All rights reserved.");

    }
}
