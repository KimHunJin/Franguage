package com.franguage.admin.test01;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.franguage.admin.test01.Activity.LoginActivity;
import com.franguage.admin.test01.Fragments.BookMarkFragment;
import com.franguage.admin.test01.Fragments.HomeFramgment;

import com.franguage.admin.test01.Fragments.MyListFragment;
import com.franguage.admin.test01.Activity.ProfileDetailActivity;
import com.franguage.admin.test01.Fragments.SearchFragment;
import com.franguage.admin.test01.Fragments.SettingFragment;
import com.franguage.admin.test01.Network.NetworkGetSi;
import com.franguage.admin.test01.Utils.Img_Path;
import com.franguage.admin.test01.Utils.RoundImageView;
import com.franguage.admin.test01.Utils.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawer;
    private Toolbar mToolBar;
    private NavigationView mNavigationView;
    private long mBackPress;
    private static final int mInterval = 2000;
    private ImageLoader loader = null;

//    private ActionBarDrawerToggle mToggle;
    String user_key;
    String name, profile_land, profile_location_activity, profile_email ,profile_possible_language, profile_sex, profile_want_language, profile_introduce, img_path;
    TextView user_name, user_country, user_location,  user_language, user_email;
    RoundImageView img;

    Fragment mFragment = null;
    Class mFragmentClass = null;
    ArrayList<String> si_list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        si_list = new NetworkGetSi().getNetworkSi();
        loader = ImageLoader.getInstance();

        mFragmentClass = HomeFramgment.class;
        change();


        if(Utils.getAppPreferences(MainActivity.this, "is_auto").equals("1")) {
            user_key = Utils.getAppPreferences(MainActivity.this, "user_key");
        }

        img = (RoundImageView)findViewById(R.id.profile_image);
        user_name = (TextView)findViewById(R.id.user_name);
        user_country = (TextView)findViewById(R.id.user_country_t);
        user_location = (TextView)findViewById(R.id.user_location_t);
        user_language = (TextView)findViewById(R.id.user_language_t);
        user_email = (TextView)findViewById(R.id.user_email_t);

//        Replace ActionBar
        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);

//        Find Drawer View
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);

//        Set Menu Icon
        final ActionBar mActionbar = getSupportActionBar();
        mActionbar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        mActionbar.setDisplayHomeAsUpEnabled(true);

        mNavigationView = (NavigationView) findViewById(R.id.nvView);
        initDrawerContent(mNavigationView);
        initToggle();
        new NetworkGetProfile().execute("");
    }

    private ActionBarDrawerToggle initToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, mToolBar, R.string.drawer_open, R.string.drawer_close);
    }

    private void initDrawerContent(NavigationView nView) {
        nView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                SelectDrawerItem(menuItem);
                return true;
            }
        });
    }

    private void SelectDrawerItem(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home_fragment:
                mFragmentClass = HomeFramgment.class;
                break;
            case R.id.mylist_fragment:
                if(Utils.getAppPreferences(MainActivity.this,"is_auto").equals("1")) {
                    mFragmentClass = MyListFragment.class;
                } else {
                    Toast.makeText(MainActivity.this,"Please Login",Toast.LENGTH_SHORT).show();
                    return;
                }
                break;
            case R.id.search_fragment:
                mFragmentClass = SearchFragment.class;
                break;
            case R.id.bookmark_fragment:
                if(Utils.getAppPreferences(MainActivity.this,"is_auto").equals("1")) {
                    mFragmentClass = BookMarkFragment.class;
                } else {

                    Toast.makeText(MainActivity.this,"Please Login",Toast.LENGTH_SHORT).show();
                    return;
                }
                break;
            case R.id.setting_fragment:
                mFragmentClass = SettingFragment.class;
                break;
            case R.id.login :  // need change
                Sign_In_Out();
                break;

        }
        change();

        item.setChecked(true);
        setTitle(item.getTitle());
        mDrawer.closeDrawers();
    }

    private void Sign_In_Out() {
        if (Utils.getAppPreferences(MainActivity.this, "is_auto").equals("1")) {
            Toast.makeText(getApplicationContext(),"Always Login",Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void change() {
        try {
            mFragment = (Fragment)mFragmentClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        FragmentManager mFM = MainActivity.this.getSupportFragmentManager();
        mFM.beginTransaction().replace(R.id.flContent, mFragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

/*    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
//        mToggle.syncState();
    }*/

    public void ProfileImageClick(View v) {
        if(Utils.getAppPreferences(MainActivity.this,"is_auto").equals("1")) {
            Toast.makeText(getApplicationContext(), "ProfileImage Click", Toast.LENGTH_SHORT).show();
            Intent mIntent = new Intent(MainActivity.this, ProfileDetailActivity.class);
            mIntent.putExtra("name", name);
            mIntent.putExtra("profile_land", profile_land);
            mIntent.putExtra("profile_location_activity", profile_location_activity);
            mIntent.putExtra("profile_email", profile_email);
            mIntent.putExtra("profile_possible_language", profile_possible_language);
            mIntent.putExtra("profile_sex", profile_sex);
            mIntent.putExtra("profile_introduce", profile_introduce);
            mIntent.putExtra("profile_img", img_path);
            startActivity(mIntent);
        }
        else {
            Toast.makeText(getApplicationContext(), "Please Login", Toast.LENGTH_SHORT).show();
        }
    }

    private class NetworkGetProfile extends AsyncTask<String, String, Integer> {

        private String err_msg = "Network error.";

        private JSONObject jObject;

        @Override
        protected Integer doInBackground(String... params) {
            // TODO Auto-generated method stub

            return processing();
        }

        @Override
        protected void onPostExecute(Integer result) {
            // TODO Auto-generated method stub
            if (result == 0) {

                try {
                    if(Utils.getAppPreferences(MainActivity.this, "is_auto").equals("1")) {
                        name = jObject.getJSONArray("ret").getJSONObject(0).getString("profile_name");
                        profile_land = jObject.getJSONArray("ret").getJSONObject(0).getString("profile_land");
                        profile_location_activity = jObject.getJSONArray("ret").getJSONObject(0).getString("profile_location_activity");
                        profile_email = jObject.getJSONArray("ret").getJSONObject(0).getString("profile_email");
                        profile_possible_language = jObject.getJSONArray("ret").getJSONObject(0).getString("profile_possible_language");
                        profile_sex = jObject.getJSONArray("ret").getJSONObject(0).getString("profile_sex");
                        profile_want_language = jObject.getJSONArray("ret").getJSONObject(0).getString("profile_want_language");
                        profile_introduce = jObject.getJSONArray("ret").getJSONObject(0).getString("profile_introduce");
                        img_path = Img_Path.IMG_PATH + jObject.getJSONArray("ret").getJSONObject(0).getString("profile_image");
                        loader.displayImage(img_path, img);

                        user_name.setText(name);
                        user_country.setText(profile_land);
                        user_location.setText(profile_location_activity);
                        user_email.setText(profile_email);
                        user_language.setText(profile_possible_language);
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return;
            }else if(result > 0){
                // 토스트
            }
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
        }

        private Integer processing() {
            // TODO Auto-generated method stub
            try {

                HttpClient http_client = new DefaultHttpClient();

                // 1.
                http_client.getParams().setParameter("http.connection.timeout", 7000);
                HttpPost http_post = null;

                List<NameValuePair> name_value = new ArrayList<NameValuePair>();

                http_post = new HttpPost("http://54.149.51.26/api/get_profile.php");

                name_value.add(new BasicNameValuePair("user_key", user_key));

                UrlEncodedFormEntity entityRequest = new UrlEncodedFormEntity(name_value, "UTF-8");
                http_post.setEntity(entityRequest);

                HttpResponse response = http_client.execute(http_post);

                // 1.
                BufferedReader reader = new BufferedReader(

                        new InputStreamReader(response.getEntity().getContent(), "UTF-8"), 8);

                StringBuilder builder = new StringBuilder();
                for (String line = null;
                     (line = reader.readLine()) != null;) {
                    builder.append(line).append("\n");
                }
                // 2. json
                jObject = new JSONObject(builder.toString());
                // 2-1.
                if (jObject.getInt("err") > 0) {
                    return jObject.getInt("err");
                }
            } catch (Exception e) {
                Log.i("err", e.toString());
                return 100;
            }
            return 0;

        }
    }
    public void onBackPressed() {
        if (mBackPress + mInterval > System.currentTimeMillis()) {
            super.onBackPressed();
            return;
        } else {
            Toast.makeText(MainActivity.this, "Press Back Again To Exit", Toast.LENGTH_SHORT).show();
        }
        mBackPress = System.currentTimeMillis();
    }

    @Override
    protected void onResume() {
        super.onResume();
        new NetworkGetProfile().execute("");
    }
}
