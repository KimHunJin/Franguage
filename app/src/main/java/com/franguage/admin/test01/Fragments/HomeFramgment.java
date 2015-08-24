package com.franguage.admin.test01.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.franguage.admin.test01.Activity.CreateActivity;
import com.franguage.admin.test01.Activity.RoomDetailActivity;
import com.franguage.admin.test01.Adapter.ListAdapter;
import com.franguage.admin.test01.Item.ListItem;
import com.franguage.admin.test01.Network.NetworkGetSi;
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

/**
 * Created by Admin on 2015-08-12.
 */
public class HomeFramgment extends Fragment {

    Context mContext;
    ArrayList<String> si_list = new ArrayList<>();
    ArrayList<ListItem> list;
    SwipeRefreshLayout swipe_refresh;
    int count;
    int offset;
    int row_cnt;
    int row_position;
    boolean is_scroll;

    boolean is_refresh;
    View mView;
    ListAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        si_list = new NetworkGetSi().getNetworkSi();

//        mAdapter = new ListAdapter();

        list = new ArrayList<>();

        mAdapter = new ListAdapter(getActivity(), R.layout.fragment_home_item, list);

        mView = inflater.inflate(R.layout.fragment_home, container, false);

        mContext = mView.getContext();

        ListView mList = (ListView) mView.findViewById(R.id.home_list);

        mList.setAdapter(mAdapter);
        new NetworkGetList().execute("");

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

        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
/*                ListItem item = list.get(position);
                Intent it = new Intent(getActivity(), RoomDetailActivity.class);
                it.putExtra("name", item.getmRoomName());
                it.putExtra("info", item.getmRoomInfo());
                it.putExtra("Language", item.getmRoomLanguage());
                it.putExtra("count", item.getmRoomMember());
                it.putExtra("id", item.getmRoomMaker());
                Toast.makeText(getActivity(),item.getmRoomMaker(),Toast.LENGTH_SHORT).show();*/

                ListItem item = (ListItem)parent.getItemAtPosition(position);
                Bundle extras = new Bundle();

                extras.putString("room_key", item.getmRoomKey());
                extras.putString("name", item.getmRoomName());
                extras.putString("count", item.getmRoomMember());
                extras.putString("location", item.getmRoomLocation());
                extras.putString("language", item.getmRoomLanguage());
                extras.putString("image", item.getmRoomImage());
                extras.putString("info", item.getmRoomInfo());
                extras.putString("create", item.getmRoomMaker());
                extras.putString("user", Utils.getAppPreferences(getActivity(),"user_key"));

                Intent mIntent = new Intent(getActivity(), RoomDetailActivity.class);
                mIntent.putExtras(extras);
                startActivity(mIntent);
            }
        });

/*
        // detail room
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListItem item = list.get(position);
                Intent it = new Intent(HomeFramgment.this, DetailRoomFragment.class);
                it.putExtra("name", item.getmRoomName());
                it.putExtra("info", item.getmRoomInfo());
                it.putExtra("Language", item.getmRoomLanguage());
                it.putExtra("count", item.getmRoomMember());
                it.putExtra("id", item.getmRoomMaker());
                startActivityForResult(it, 0);
                row_position = position;
            }
        });*/

        swipe_refresh = (SwipeRefreshLayout)mView.findViewById(R.id.swipe_refresh_widget);
//		swipe_refresh.setColorSchemeColors(Color.GREEN, Color.RED, Color.BLUE);
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            public void onRefresh() {

                init();
                mAdapter.notifyDataSetChanged();
                new NetworkGetList().execute("");

            }
        });

        FloatingActionButton mButton = (FloatingActionButton) mView.findViewById(R.id.btnFAB);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Utils.getAppPreferences(getActivity(),"is_auto").equals("1")) {
                    Intent mIntent = new Intent(mContext, CreateActivity.class);
                    mIntent.putExtra("si_list", si_list);
                    startActivity(mIntent);
                }
                else {
                Toast.makeText(getActivity(),"로그인하세요",Toast.LENGTH_SHORT).show();
                }

            }
        });



/*        mAdapter.add(new ListItem(R.drawable.profile, "Test01", "Test01입니다", "3", "서대문구", "영어", false));
        mAdapter.add(new ListItem(R.drawable.profile2, "Test02", "Test02입니다", "4", "양천구", "중국어", false));
        mAdapter.add(new ListItem(R.drawable.profile3, "Test03", "Test03입니다", "6", "성동구", "스페인어", false));
        mAdapter.add(new ListItem(R.drawable.profile4, "Test04", "Test04입니다", "8", "강북구", "러시아어", false));
        mAdapter.add(new ListItem(R.drawable.profile2, "Test05", "Test05입니다", "4", "강남구", "일본어", false));
        mAdapter.add(new ListItem(R.drawable.profile3, "Test06", "Test06입니다", "5", "강서구", "한국어", false));*/

        return mView;
    }

/*
// test
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 0 && resultCode == RESULT_OK )
        {
            ListItem item = list.get(row_position);
            item.setReply_count(data.getStringExtra("reply_count"));
            adapter.notifyDataSetChanged();

        }

    }
*/

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
        private ProgressBar pro_bar = (ProgressBar)mView.findViewById(R.id.pro_bar);
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
                http_post = new HttpPost("http://54.149.51.26/api/get_room_list.php");
//			http_post = new HttpPost(uri);
                name_value.add(new BasicNameValuePair("offset", "" + offset));
                name_value.add(new BasicNameValuePair("row_cnt", "" + row_cnt));

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
            if(result == 0)
            {
                try {

                    offset += jobject.getInt("cnt");
                    count = jobject.getInt("cnt");

                    for(int i=0;i<jobject.getInt("cnt"); i++)
                    {
                        ListItem item = new ListItem();
                        item.setmRoomKey(jobject.getJSONArray("ret").getJSONObject(i).getString("room_key"));
                        item.setmRoomName(jobject.getJSONArray("ret").getJSONObject(i).getString("room_name"));
                        item.setmRoomInfo(jobject.getJSONArray("ret").getJSONObject(i).getString("room_introduce"));
                        item.setmRoomLocation(jobject.getJSONArray("ret").getJSONObject(i).getString("room_location"));
                        item.setmRoomLanguage(jobject.getJSONArray("ret").getJSONObject(i).getString("room_language"));
                        item.setmRoomMaker(jobject.getJSONArray("ret").getJSONObject(i).getString("create_user_key"));
                        item.setmRoomImage(jobject.getJSONArray("ret").getJSONObject(i).getString("create_room_img_o"));
                        item.setmRoomMember(jobject.getJSONArray("ret").getJSONObject(i).getString("room_user_count"));
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
                Toast toast = Toast.makeText(getActivity(), err_msg, Toast.LENGTH_SHORT);
                toast.show();
                return;
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

    //    public void CreateBtnClick(View v) {
//        Intent mIntent = new Intent(getActivity(),CreateActivity.class);
//        getActivity().startActivity(mIntent);
//    }
}
