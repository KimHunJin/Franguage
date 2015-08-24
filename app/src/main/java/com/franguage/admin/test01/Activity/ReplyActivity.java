package com.franguage.admin.test01.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.franguage.admin.test01.Adapter.DetailAdapter;
import com.franguage.admin.test01.Adapter.ListAdapter;
import com.franguage.admin.test01.Item.DetailItem;
import com.franguage.admin.test01.Item.ListItem;
import com.franguage.admin.test01.R;
import com.franguage.admin.test01.Utils.Utils;

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

public class ReplyActivity extends ActionBarActivity {

    private Toolbar mToolBar;

    Context mContext;
    ArrayList<DetailItem> list;
    SwipeRefreshLayout swipe_refresh;
    int count;
    int offset;
    int row_cnt;
    String room_key;
    boolean is_scroll;
    boolean is_refresh;
    DetailAdapter mAdapter;
    String reply;
    EditText edt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);
        mToolBar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);
        final ActionBar mActionbar = getSupportActionBar();

        Intent it = getIntent();
        room_key = it.getExtras().getString("room_key");
        edt = (EditText)findViewById(R.id.edtInput);
        list = new ArrayList<>();

        Button button = (Button)findViewById(R.id.btnInput);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reply = edt.getText().toString();
                new NetwortSetReply().execute("");
            }
        });

        mAdapter = new DetailAdapter(this, R.layout.activity_detail_room_item, list);

        ListView mList = (ListView) findViewById(R.id.board_list);
        mActionbar.setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_24dp);
        mActionbar.setDisplayHomeAsUpEnabled(true);
        mList.setAdapter(mAdapter);

        mList.setOnScrollListener(new AbsListView.OnScrollListener() {

            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

                if (firstVisibleItem + visibleItemCount == totalItemCount) {
                    if (count != 0 && offset % row_cnt == 0) {
                        if (is_scroll) {
                            is_scroll = false;
                            is_refresh = false;
                            new NetworkGetList().execute("");

                        }

                    }
                }
            }
        });

        swipe_refresh = (SwipeRefreshLayout) findViewById(R.id.board_swipe_refresh_widget);
//		swipe_refresh.setColorSchemeColors(Color.GREEN, Color.RED, Color.BLUE);
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            public void onRefresh() {

                init();
                mAdapter.notifyDataSetChanged();
                new NetworkGetList().execute("");

            }
        });

        new NetworkGetList().execute("");
    }

    public void init()
    {
        count = 0;
        offset = 0;
        row_cnt= 3;
        is_scroll = true;
        is_refresh = true;
        list.clear();
    }





    private class NetworkGetList extends AsyncTask<String, String, Integer>
    {
        private String err_msg = "Network error.";
        private ProgressBar pro_bar = (ProgressBar)findViewById(R.id.board_pro_bar);
        private JSONObject jobject;

        protected void onPreExecute() {

            if(!is_refresh)
            {
                pro_bar.setVisibility(View.VISIBLE);
            }

        }

        protected Integer doInBackground(String... params) {

            return processiong();
        }

        private Integer processiong() {

            try {
                HttpClient http_client = new DefaultHttpClient();
                http_client.getParams().setParameter("http.connection.timeout", 7000);
                HttpPost http_post = null;

                ArrayList<NameValuePair> name_value = new ArrayList<NameValuePair>();
                http_post = new HttpPost("http://54.149.51.26/api/get_reply.php");
//			http_post = new HttpPost(uri);
                name_value.add(new BasicNameValuePair("room_key",room_key));
                name_value.add(new BasicNameValuePair("offset", ""+offset));
                name_value.add(new BasicNameValuePair("row_cnt", ""+row_cnt));

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
            swipe_refresh.setRefreshing(false);
            Log.e("result", result + "");
            if(result ==0)
            {
                try {

                    offset+= jobject.getInt("cnt");
                    count = jobject.getInt("cnt");

                    for(int i=0;i<jobject.getInt("cnt"); i++)
                    {
                        DetailItem item = new DetailItem();
                        item.setComment(jobject.getJSONArray("ret").getJSONObject(i).getString("profile_name"));
                        item.setTime(jobject.getJSONArray("ret").getJSONObject(i).getString("reply_content"));
                        item.setUsername(jobject.getJSONArray("ret").getJSONObject(i).getString("reply_create_date"));
                        item.setImg(jobject.getJSONArray("ret").getJSONObject(i).getString("profile_image"));
                        mAdapter.add(item);
                    }
                    mAdapter.notifyDataSetChanged();
                    is_scroll = true;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                Toast toast = Toast.makeText(getApplicationContext(), err_msg, Toast.LENGTH_SHORT);
                toast.show();
            }
        }


    }

    @Override
    public void onResume() {
        super.onResume();
        init();
        mAdapter.notifyDataSetChanged();
        new NetworkGetList().execute("");
    }


    private class NetwortSetReply extends AsyncTask<String, String, Integer> {

        private String err_msg = "Network error.";
        private ProgressBar pro_bar = (ProgressBar)findViewById(R.id.board_pro_bar);
        private JSONObject jobject;

        protected void onPreExecute() {

            if(!is_refresh)
            {
                pro_bar.setVisibility(View.VISIBLE);
            }

        }

        protected Integer doInBackground(String... params) {

            return processiong();
        }

        private Integer processiong() {

            try {
                HttpClient http_client = new DefaultHttpClient();
                http_client.getParams().setParameter("http.connection.timeout", 7000);
                HttpPost http_post = null;

                ArrayList<NameValuePair> name_value = new ArrayList<NameValuePair>();
                http_post = new HttpPost("http://54.149.51.26/api/set_reply.php");
//			http_post = new HttpPost(uri);
                name_value.add(new BasicNameValuePair("room_key",room_key));
                name_value.add(new BasicNameValuePair("user_key", Utils.getAppPreferences(getApplicationContext(),"user_key")));
                name_value.add(new BasicNameValuePair("reply", reply));

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
            swipe_refresh.setRefreshing(false);
            Log.e("result", result + "");
            if(result ==0)
            {
                Toast.makeText(getApplicationContext(),"등록 성공",Toast.LENGTH_SHORT).show();
                finish();

            }
            else
            {
                Toast toast = Toast.makeText(getApplicationContext(), err_msg, Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_reply, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
