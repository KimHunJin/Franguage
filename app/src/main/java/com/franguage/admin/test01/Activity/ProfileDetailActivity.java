package com.franguage.admin.test01.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.internal.widget.ActionBarContextView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.franguage.admin.test01.R;
import com.franguage.admin.test01.Utils.RoundImageView;
import com.franguage.admin.test01.Utils.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by Admin on 2015-08-15.
 */
public class ProfileDetailActivity extends ActionBarActivity {

    private Toolbar mToolBar;
    private ImageLoader loader = null;
    String user_key;
    TextView user_name, country_user, gender_user, location_user, email_user, language_user, intro_user;
    RoundImageView user_img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_profile);
        loader = ImageLoader.getInstance();

        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);

        Intent it = getIntent();
        String name = it.getStringExtra("name");
        String profile_land = it.getStringExtra("profile_land");
        String profile_sex = it.getStringExtra("profile_sex");
        String profile_location_activity = it.getStringExtra("profile_location_activity");
        String profile_email = it.getStringExtra("profile_email");
        String profile_possible_language = it.getStringExtra("profile_possible_language");
        String profile_introduce = it.getStringExtra("profile_introduce");
        String img_path = it.getStringExtra("profile_img");

        user_key = Utils.getAppPreferences(ProfileDetailActivity.this, "user_key");

        user_name = (TextView)findViewById(R.id.profile_detail_user_name);
        country_user = (TextView)findViewById(R.id.profile_detail_user_countury);
        gender_user = (TextView)findViewById(R.id.profile_detail_user_gender);
        location_user = (TextView)findViewById(R.id.profile_detail_user_location);
        email_user = (TextView)findViewById(R.id.profile_detail_user_email);
        language_user = (TextView)findViewById(R.id.profile_detail_user_language);
        intro_user = (TextView)findViewById(R.id.profile_detail_user_introduce);
        user_img = (RoundImageView)findViewById(R.id.profile_detail_user_image);

        user_name.setText(name);
        country_user.setText(profile_land);
        gender_user.setText(profile_sex);
        location_user.setText(profile_location_activity);
        email_user.setText(profile_email);
        language_user.setText(profile_possible_language);
        intro_user.setText(profile_introduce);
        loader.displayImage(img_path, user_img);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_24dp);
        ab.setDisplayHomeAsUpEnabled(true);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mInflater = getMenuInflater();
        mInflater.inflate(R.menu.activity_edit_profile, menu);
        MenuItem menuItem = menu.findItem(R.id.action_edit);
        menuItem.collapseActionView();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                Intent mIntent = new Intent(ProfileDetailActivity.this, ProfileDetailEditActivity.class);
                startActivity(mIntent);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
