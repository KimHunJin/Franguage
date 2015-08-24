package com.franguage.admin.test01.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.franguage.admin.test01.Item.ListItem;
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

public class RoomDetailActivity extends ActionBarActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_room);

/*        Intent it = getIntent();
        it.getExtras().getString("name");
        it.getExtras().getString("info");
        it.getExtras().getString("Language");
        it.getExtras().getString("count");
        it.getExtras().getString("id");*/
        mToolBar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);

        final ActionBar mActionbar = getSupportActionBar();
        mActionbar.setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_24dp);
        mActionbar.setDisplayHomeAsUpEnabled(true);

        initRoom();
        setRoom();
        getRoom();

        ImageView board = (ImageView)findViewById(R.id.room_board_edit);
        board.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(getApplication(),ReplyActivity.class);
                it.putExtra("room_key",mRoomKey);
                startActivity(it);

            }
        });

        if(mRoomMaker.equals(user)) {
            room_join = (ImageView)findViewById(R.id.ic_room_join_quit);
            room_join.setVisibility(View.INVISIBLE);
            ImageView room_explosive = (ImageView)findViewById(R.id.ic_room_explosive);
            room_explosive.setVisibility(View.VISIBLE);
            room_explosive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(RoomDetailActivity.this);
                    mBuilder.setTitle("경고");
                    mBuilder.setMessage("정말로 삭제하시겠습니까?");
                    mBuilder.setCancelable(false);
                    mBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new NetworkDeleteRoom().execute("");
                            finish();
                        }
                    });
                    mBuilder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog mDialog = mBuilder.create();
                    mDialog.show();
                }
            });
        } else {
            room_join = (ImageView)findViewById(R.id.ic_room_join_quit);
            room_join.setVisibility(View.VISIBLE);
            ImageView room_explosive = (ImageView)findViewById(R.id.ic_room_explosive);
            room_explosive.setVisibility(View.INVISIBLE);

            room_join.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new NetworkCheckJoin().execute("");
                }
            });
        }
        favorite_mark = (ImageView)findViewById(R.id.ic_favorite_mark);
        favorite_mark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new NetworkCheckWish().execute("");
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
        Log.e("room_maker",mRoomMaker);
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

    private class NetworkDeleteRoom extends AsyncTask<String, String, Integer> {

        private String err_msg = "Network error.";
        private ProgressBar pro_bar = (ProgressBar)findViewById(R.id.pro_bar);
        private JSONObject jobject;

        protected void onPreExecute() {

            if(!is_refresh)
            {
                pro_bar.setVisibility(View.VISIBLE);
            }

        }
        @Override
        protected Integer doInBackground(String... params) {
            return processing();
        }

        private Integer processing() {

            try {
                HttpClient http_client = new DefaultHttpClient();
                http_client.getParams().setParameter("http.connection.timeout", 7000);
                HttpPost http_post = null;

                ArrayList<NameValuePair> name_value = new ArrayList<NameValuePair>();
                http_post = new HttpPost("http://54.149.51.26/api/delete_room.php");
//			http_post = new HttpPost(uri);
                name_value.add(new BasicNameValuePair("room_key",mRoomKey));

                UrlEncodedFormEntity entityRequset;

                entityRequset = new UrlEncodedFormEntity(name_value, "UTF-8");
                http_post.setEntity(entityRequset);

                HttpResponse response = http_client.execute(http_post);
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"), 8);
                StringBuilder builder = new StringBuilder();


                for(String line = null; (line=reader.readLine())!=null;)
                {
                    builder.append(line).append("\n");
                }

                jobject = new JSONObject(builder.toString());

                if(jobject.getInt("err")>0)
                {
                    return jobject.getInt("err");
                }

            } catch (UnsupportedEncodingException e){
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

        protected void onPostExecute(Integer result) {

            pro_bar.setVisibility(View.GONE);
            Log.e("result", result + "");
            if(result ==0)
            {
                Toast.makeText(getApplicationContext(),"삭제 완료",Toast.LENGTH_SHORT).show();
                finish();

            }
            else
            {
                Toast toast = Toast.makeText(getApplicationContext(), err_msg, Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    private class NetworkCheckWish extends AsyncTask<String, String, Integer> {

        private String err_msg = "Network error.";
        private ProgressBar pro_bar = (ProgressBar) findViewById(R.id.pro_bar);
        private JSONObject jobject;

        protected void onPreExecute() {

            if(!is_refresh)
            {
                pro_bar.setVisibility(View.VISIBLE);
            }

        }

        @Override
        protected Integer doInBackground(String... params) {
            return processing();
        }

        private Integer processing() {

            try {
                HttpClient http_client = new DefaultHttpClient();
                http_client.getParams().setParameter("http.connection.timeout", 7000);
                HttpPost http_post = null;

                ArrayList<NameValuePair> name_value = new ArrayList<NameValuePair>();
                http_post = new HttpPost("http://54.149.51.26/api/check_wish_list.php");
//			http_post = new HttpPost(uri);
                name_value.add(new BasicNameValuePair("room_key",mRoomKey));
                name_value.add(new BasicNameValuePair("user_key", Utils.getAppPreferences(getApplicationContext(),"user_key")));

                UrlEncodedFormEntity entityRequset;

                entityRequset = new UrlEncodedFormEntity(name_value, "UTF-8");
                http_post.setEntity(entityRequset);

                HttpResponse response = http_client.execute(http_post);
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"), 8);
                StringBuilder builder = new StringBuilder();


                for(String line = null; (line=reader.readLine())!=null;)
                {
                    builder.append(line).append("\n");
                }

                jobject = new JSONObject(builder.toString());

                if(jobject.getInt("err")>0)
                {
                    return jobject.getInt("err");
                }

            } catch (UnsupportedEncodingException e){
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

        protected void onPostExecute(Integer result) {

            pro_bar.setVisibility(View.GONE);
            Log.e("result", result + "");
            if(result == 60 )
            {
                favorite_mark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(RoomDetailActivity.this);
                        mBuilder.setTitle("경고");
                        mBuilder.setMessage("정말로 삭제하시겠습니까?");
                        mBuilder.setCancelable(false);
                        mBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new NetworkDeleteWish().execute("");
                            }
                        });
                        mBuilder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        AlertDialog mDialog = mBuilder.create();
                        mDialog.show();
                    }
                });
            }
            else if(result == 0)
            {
                new NetworkSetWish().execute("");
            }
        }
    }

    private class NetworkDeleteWish extends AsyncTask<String, String, Integer> {

        private String err_msg = "Network error.";
        private ProgressBar pro_bar = (ProgressBar) findViewById(R.id.pro_bar);
        private JSONObject jobject;

        protected void onPreExecute() {

            if(!is_refresh)
            {
                pro_bar.setVisibility(View.VISIBLE);
            }

        }

        @Override
        protected Integer doInBackground(String... params) {
            return processing();
        }

        private Integer processing() {

            try {
                HttpClient http_client = new DefaultHttpClient();
                http_client.getParams().setParameter("http.connection.timeout", 7000);
                HttpPost http_post = null;

                ArrayList<NameValuePair> name_value = new ArrayList<NameValuePair>();
                http_post = new HttpPost("http://54.149.51.26/api/delete_wish_list.php");
//			http_post = new HttpPost(uri);
                name_value.add(new BasicNameValuePair("room_key",mRoomKey));
                name_value.add(new BasicNameValuePair("user_key", Utils.getAppPreferences(getApplicationContext(),"user_key")));

                UrlEncodedFormEntity entityRequset;

                entityRequset = new UrlEncodedFormEntity(name_value, "UTF-8");
                http_post.setEntity(entityRequset);

                HttpResponse response = http_client.execute(http_post);
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"), 8);
                StringBuilder builder = new StringBuilder();


                for(String line = null; (line=reader.readLine())!=null;)
                {
                    builder.append(line).append("\n");
                }

                jobject = new JSONObject(builder.toString());

                if(jobject.getInt("err")>0)
                {
                    return jobject.getInt("err");
                }

            } catch (UnsupportedEncodingException e){
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
            if(result == 0) {
                Toast.makeText(getApplicationContext(),"위시 삭제",Toast.LENGTH_SHORT).show();
            }
            else {
                Toast toast = Toast.makeText(getApplicationContext(), err_msg, Toast.LENGTH_SHORT);
                toast.show();
                return;
            }
        }
    }

    private class NetworkSetWish extends AsyncTask<String, String, Integer> {

        private String err_msg = "Network error.";
        private ProgressBar pro_bar = (ProgressBar) findViewById(R.id.pro_bar);
        private JSONObject jobject;

        protected void onPreExecute() {

            if(!is_refresh)
            {
                pro_bar.setVisibility(View.VISIBLE);
            }

        }

        protected Integer doInBackground(String... params) {

            return processing();
        }

        private Integer processing() {

            try {
                HttpClient  http_client = new DefaultHttpClient();
                http_client.getParams().setParameter("http.connection.timeout", 7000);
                HttpPost http_post = null;

                ArrayList<NameValuePair> name_value = new ArrayList<NameValuePair>();
                http_post = new HttpPost("http://54.149.51.26/api/set_wish_list.php");
//			http_post = new HttpPost(uri);
                name_value.add(new BasicNameValuePair("room_key", mRoomKey ));
                name_value.add(new BasicNameValuePair("user_key", Utils.getAppPreferences(getApplicationContext(),"user_key")));

                UrlEncodedFormEntity entityRequset;

                entityRequset = new UrlEncodedFormEntity(name_value, "UTF-8");
                http_post.setEntity(entityRequset);

                HttpResponse response = http_client.execute(http_post);
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"), 8);
                StringBuilder builder = new StringBuilder();


                for(String line = null; (line=reader.readLine())!=null;)
                {
                    builder.append(line).append("\n");
                }

                jobject = new JSONObject(builder.toString());

                if(jobject.getInt("err")>0)
                {
                    return jobject.getInt("err");
                }

            } catch (UnsupportedEncodingException e){
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

        protected void onPostExecute(Integer result) {
            pro_bar.setVisibility(View.GONE);
            if (result == 0) {
                Toast.makeText(getApplicationContext(), "북마크 저장", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class NetworkCheckJoin extends AsyncTask<String, String, Integer> {

        private String err_msg = "Network error.";
        private ProgressBar pro_bar = (ProgressBar) findViewById(R.id.pro_bar);
        private JSONObject jobject;

        protected void onPreExecute() {

            if(!is_refresh)
            {
                pro_bar.setVisibility(View.VISIBLE);
            }

        }

        @Override
        protected Integer doInBackground(String... params) {
            return processing();
        }

        private Integer processing() {

            try {
                HttpClient http_client = new DefaultHttpClient();
                http_client.getParams().setParameter("http.connection.timeout", 7000);
                HttpPost http_post = null;

                ArrayList<NameValuePair> name_value = new ArrayList<NameValuePair>();
                http_post = new HttpPost("http://54.149.51.26/api/check_user_join.php");
//			http_post = new HttpPost(uri);
                name_value.add(new BasicNameValuePair("room_key",mRoomKey));
                name_value.add(new BasicNameValuePair("user_key", Utils.getAppPreferences(getApplicationContext(),"user_key")));

                UrlEncodedFormEntity entityRequset;

                entityRequset = new UrlEncodedFormEntity(name_value, "UTF-8");
                http_post.setEntity(entityRequset);

                HttpResponse response = http_client.execute(http_post);
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"), 8);
                StringBuilder builder = new StringBuilder();


                for(String line = null; (line=reader.readLine())!=null;)
                {
                    builder.append(line).append("\n");
                }

                jobject = new JSONObject(builder.toString());

                if(jobject.getInt("err")>0)
                {
                    return jobject.getInt("err");
                }

            } catch (UnsupportedEncodingException e){
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

        protected void onPostExecute(Integer result) {

            pro_bar.setVisibility(View.GONE);
            Log.e("result", result + "");
            if(result == 30 )
            {
                room_join.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(RoomDetailActivity.this);
                        mBuilder.setTitle("경고");
                        mBuilder.setMessage("정말로 삭제하시겠습니까?");
                        mBuilder.setCancelable(false);
                        mBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new NetworkExitRoom().execute("");
                            }
                        });
                        mBuilder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        AlertDialog mDialog = mBuilder.create();
                        mDialog.show();
                    }
                });
            }
            else if(result == 0)
            {
                new NetworkSetJoin().execute("");
            }
        }
    }

    private class NetworkSetJoin extends AsyncTask<String, String, Integer> {

        private String err_msg = "Network error.";
        private ProgressBar pro_bar = (ProgressBar) findViewById(R.id.pro_bar);
        private JSONObject jobject;

        protected void onPreExecute() {

            if(!is_refresh)
            {
                pro_bar.setVisibility(View.VISIBLE);
            }

        }

        @Override
        protected Integer doInBackground(String... params) {
            return processing();
        }

        private Integer processing() {

            try {
                HttpClient http_client = new DefaultHttpClient();
                http_client.getParams().setParameter("http.connection.timeout", 7000);
                HttpPost http_post = null;

                ArrayList<NameValuePair> name_value = new ArrayList<NameValuePair>();
                http_post = new HttpPost("http://54.149.51.26/api/join_room.php");
//			http_post = new HttpPost(uri);
                name_value.add(new BasicNameValuePair("room_key",mRoomKey));
                name_value.add(new BasicNameValuePair("user_key", Utils.getAppPreferences(getApplicationContext(),"user_key")));

                UrlEncodedFormEntity entityRequset;

                entityRequset = new UrlEncodedFormEntity(name_value, "UTF-8");
                http_post.setEntity(entityRequset);

                HttpResponse response = http_client.execute(http_post);
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"), 8);
                StringBuilder builder = new StringBuilder();


                for(String line = null; (line=reader.readLine())!=null;)
                {
                    builder.append(line).append("\n");
                }

                jobject = new JSONObject(builder.toString());

                if(jobject.getInt("err")>0)
                {
                    return jobject.getInt("err");
                }

            } catch (UnsupportedEncodingException e){
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

        protected void onPostExecute(Integer result) {

            pro_bar.setVisibility(View.GONE);
            Log.e("result", result + "");
            if(result == 40)
            {
                Toast.makeText(getApplicationContext(),"인원 꽉 참",Toast.LENGTH_SHORT).show();
            }
            else if(result == 0)
            {
                Toast.makeText(getApplicationContext(),"입장",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class NetworkExitRoom extends AsyncTask<String, String, Integer> {

        private String err_msg = "Network error.";
        private ProgressBar pro_bar = (ProgressBar) findViewById(R.id.pro_bar);
        private JSONObject jobject;

        protected void onPreExecute() {

            if(!is_refresh)
            {
                pro_bar.setVisibility(View.VISIBLE);
            }

        }

        @Override
        protected Integer doInBackground(String... params) {
            return processing();
        }

        private Integer processing() {

            try {
                HttpClient http_client = new DefaultHttpClient();
                http_client.getParams().setParameter("http.connection.timeout", 7000);
                HttpPost http_post = null;

                ArrayList<NameValuePair> name_value = new ArrayList<NameValuePair>();
                http_post = new HttpPost("http://54.149.51.26/api/exit_room.php");
//			http_post = new HttpPost(uri);
                name_value.add(new BasicNameValuePair("room_key",mRoomKey));
                name_value.add(new BasicNameValuePair("user_key", Utils.getAppPreferences(getApplicationContext(),"user_key")));

                UrlEncodedFormEntity entityRequset;

                entityRequset = new UrlEncodedFormEntity(name_value, "UTF-8");
                http_post.setEntity(entityRequset);

                HttpResponse response = http_client.execute(http_post);
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"), 8);
                StringBuilder builder = new StringBuilder();


                for(String line = null; (line=reader.readLine())!=null;)
                {
                    builder.append(line).append("\n");
                }

                jobject = new JSONObject(builder.toString());

                if(jobject.getInt("err")>0)
                {
                    return jobject.getInt("err");
                }

            } catch (UnsupportedEncodingException e){
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

        protected void onPostExecute(Integer result) {

            pro_bar.setVisibility(View.GONE);
            Log.e("result", result + "");
            if(result == 0 )
            {
                Toast.makeText(getApplicationContext(),"탈퇴",Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

}
