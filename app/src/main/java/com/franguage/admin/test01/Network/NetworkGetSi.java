package com.franguage.admin.test01.Network;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by HunJin on 2015-08-16.
 */
public class NetworkGetSi extends AsyncTask<String, String, Integer> {
    private String err_msg = "Network error.";
    private JSONObject jObject;

    ArrayList<String> si_list = new ArrayList<>();

    public ArrayList<String> getNetworkSi() {
        execute("");
        return si_list;
    }


    @Override
    protected Integer doInBackground(String... params) {
        return processing();
    }

    @Override
    protected void onPostExecute(Integer result) {
        // TODO Auto-generated method stub
        Log.i("result",result+"");
        if (result == 0) {

            try {

                for(int i = 0; i < jObject.getInt("cnt"); i++){

                    String si =
                            jObject.getJSONArray("ret")
                                    .getJSONObject(i)
                                    .getString("si");
                    si_list.add(si);

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


    private Integer processing() {
        // TODO Auto-generated method stub
        try {

            HttpClient http_client = new DefaultHttpClient();

            // 1.
            http_client.getParams().setParameter
                    ("http.connection.timeout",
                            7000);
            HttpPost http_post = null;

            List<NameValuePair> name_value = new ArrayList<NameValuePair>();

            http_post = new HttpPost("http://54.149.51.26/api/get_si_list.php");

            UrlEncodedFormEntity entityRequest = new UrlEncodedFormEntity(name_value, "UTF-8");
            http_post.setEntity(entityRequest);

            HttpResponse response = http_client.execute(http_post);

            // 1.
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"), 8);

            StringBuilder builder = new StringBuilder();
            for (String line = null; (line = reader.readLine()) != null;) {
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
