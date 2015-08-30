package com.franguage.admin.test01.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.franguage.admin.test01.R;
import com.franguage.admin.test01.Utils.Img_Path;
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
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by HunJin on 2015-08-27.
 */
public class RoomDetailActivity2 extends ActionBarActivity{
    private TextView mRoomName;
    private TextView mRoomCount;
    private TextView mRoomLocation;
    private TextView mRoomLanguage;
    private TextView[] mRoomMember;
    private TextView mRoomInfo;
    private ImageView mRoomImage;
    private String mRoomImagePath;
    private String mRoomMaker;
    private String user;
    private String mRoomKey;


    ImageView favorite_mark;
    ImageView room_join;

    private Toolbar mToolBar;
    private ImageLoader loader = null;
    boolean is_refresh;

    boolean is_join_check = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_room);

        mToolBar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);

        final ActionBar mActionbar = getSupportActionBar();
        mActionbar.setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_24dp);
        mActionbar.setDisplayHomeAsUpEnabled(true);

        initRoom();
        setRoom();
        getRoom();

        new NetworkCheckUser().execute("");

        ImageView board = (ImageView)findViewById(R.id.room_board_edit);
        board.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(getApplication(),ReplyActivity.class);
                it.putExtra("room_key",mRoomKey);
                startActivity(it);
            }
        });
    }

    private void initRoom() {
        this.mRoomName = (TextView) findViewById(R.id.room_detail_name);
        this.mRoomCount = (TextView) findViewById(R.id.room_member);
        this.mRoomLocation = (TextView) findViewById(R.id.room_location);
        this.mRoomLanguage = (TextView) findViewById(R.id.room_language);
        this.mRoomInfo = (TextView) findViewById(R.id.room_introduce_text);
        this.mRoomImage = (ImageView) findViewById(R.id.backgroundimage);
        loader = ImageLoader.getInstance();
    }

    private void getRoom() {
        mRoomName.setText(getIntent().getStringExtra("name"));
        mRoomCount.setText(getIntent().getStringExtra("count"));
        mRoomLocation.setText(getIntent().getStringExtra("location"));
        mRoomLanguage.setText(getIntent().getStringExtra("language"));
        mRoomInfo.setText(getIntent().getStringExtra("info"));
        mRoomImagePath = getIntent().getStringExtra("image");
        mRoomMaker = getIntent().getStringExtra("create");
        Log.e("room_maker", mRoomMaker);
        user = getIntent().getStringExtra("user");
        Log.e("user",user);
        mRoomKey = getIntent().getStringExtra("room_key");
//        mRoomMember.setText(getIntent().getStringArrayExtra("member"));
        loader.displayImage(Img_Path.IMG_PATH+mRoomImagePath, mRoomImage);

    }

    private void setRoom() {
        if (getIntent().getStringExtra("name") == null) {
            mRoomName.setText("기본 방 이름");
        }
        if (getIntent().getStringExtra("count") == null) {
            mRoomCount.setText("2");
        }
        if (getIntent().getStringExtra("location") == null) {
            mRoomLocation.setText("기본 지역");
        }
        if (getIntent().getStringExtra("info") == null) {
            mRoomInfo.setText("기본 설명");
        }
        if (getIntent().getStringExtra("image") == null) {
            mRoomImage.setImageResource(R.drawable.ic_fail_to_load_image);
        }
    }

    private class NetworkCheckUser extends AsyncTask<String, String, Integer> {

        private  JSONObject jsonObject;
        private ProgressBar pro_bar = (ProgressBar)findViewById(R.id.pro_bar);
        private String err_msg = "Network Error";

        @Override
        protected Integer doInBackground(String... params) {
            return processing();
        }

        protected Integer processing() {
            try {
                HttpClient http_client = new DefaultHttpClient();
                http_client.getParams().setParameter("http.connection.timeout", 7000);
                HttpPost http_post = null;

                ArrayList<NameValuePair> name_value = new ArrayList<>();
                http_post = new HttpPost("http://54.149.51.26/api/check_user_join.php");

                name_value.add(new BasicNameValuePair("room_key", mRoomKey));
                name_value.add(new BasicNameValuePair("user_key", Utils.getAppPreferences(getApplicationContext(), "user_key")));

                UrlEncodedFormEntity entityRequest;

                entityRequest = new UrlEncodedFormEntity(name_value, "UTF-8");
                http_post.setEntity(entityRequest);

                HttpResponse response = http_client.execute(http_post);
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                StringBuilder builder = new StringBuilder();

                for(String line = null; (line = reader.readLine())!=null;) {
                    builder.append(line).append("\n");
                }

                jsonObject = new JSONObject(builder.toString());

                if(jsonObject.getInt("err")>0) {
                    return jsonObject.getInt("err");
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return 0;
        }

        @Override
        protected void onPostExecute(Integer result) {

            pro_bar.setVisibility(View.GONE);
            if(result == 30) {
                // already join -> btn exit
                is_join_check = true;
                // check creater -> delete room
            } else if(result > 0) {
                // network error
            } else {
                // guest -> join btn -> check user number
                is_join_check = false;
            }
        }

        @Override
        protected void onPreExecute() {
            if(!is_refresh) {
                pro_bar.setVisibility(View.VISIBLE);
            }
        }

    }

}
