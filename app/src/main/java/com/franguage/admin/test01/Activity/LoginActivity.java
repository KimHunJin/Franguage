package com.franguage.admin.test01.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.franguage.admin.test01.MainActivity;
import com.franguage.admin.test01.R;
import com.franguage.admin.test01.Utils.Utils;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

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

/**
 * Created by Admin on 2015-08-12.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView txtReist, txtLogoL;
    private CallbackManager callbackManager;
    private LoginButton loginButtonL;
    private EditText edtEmailL, edtPwdL;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        callbackManager = CallbackManager.Factory.create();
        loginButtonL = (LoginButton)findViewById(R.id.login_buttonL);
        loginButtonL.setReadPermissions("public_profile");


        loginButtonL.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.i("TAG", "FacebookCallback(result = " + loginResult.getAccessToken());

            }

            @Override
            public void onCancel() {
                Log.i("TAG", "Facebook");
            }

            @Override
            public void onError(FacebookException e) {
                Log.e("TAG", "ERROR");
            }

        });

/*        txtLogoL = (TextView)findViewById(R.id.txtLogoL);
        Typeface typeface = Typeface.createFromAsset(getAssets(),"fonts/Dynalight-Regular.ttf");
        txtLogoL.setTypeface(typeface);*/

        String text =  "계정이 없으신가요?  가입하기";
        txtReist = (TextView)findViewById(R.id.txtRegist);
        SpannableString spanString = new SpannableString(text);
        spanString.setSpan(new UnderlineSpan(), 0, spanString.length(), 0);
        txtReist.setText(spanString);
        txtReist.setOnClickListener(this);

        edtEmailL = (EditText)findViewById(R.id.edtEmailL);
        edtPwdL = (EditText)findViewById(R.id.edtPwdS);
        btnLogin = (Button)findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);


    }



    protected void onLogin() {
        new NetworkLogin().execute("");
    }

    private class NetworkLogin extends AsyncTask<String, String, Integer> {

        private String err_msg = "Network error.";
        private ProgressDialog pro_dialog;
        private JSONObject jObject;

        @Override
        protected Integer doInBackground(String... params) {
            // TODO Auto-generated method stub

            return processing();
        }

        @Override
        protected void onPostExecute(Integer result) {
            // TODO Auto-generated method stub

            pro_dialog.cancel();

            // 에러 체크해서 해당 액션 수행

            Log.e("error",result.toString());

            if(result == 0){

                try {
                    int user_id = jObject.getInt("user_key");
                    // 자동 로그인 선택되었을때, 이 유저 아이디를 저장해 놓자
                    Utils.setAppPreferences(LoginActivity.this, "user_key", user_id + "");

                    Utils.setAppPreferences(LoginActivity.this, "is_auto", "1");


                    // 메인 액티비티로 이동
                    Intent it = new Intent(LoginActivity.this,
                            MainActivity.class);
                    startActivity(it);
                    finish();

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }else if(result == 3){
                Toast.makeText(LoginActivity.this, "아이디가 없습니다.", Toast.LENGTH_SHORT).show();

            }else if(result == 4){
                Toast.makeText(LoginActivity.this, "비번 틀렸습니다.", Toast.LENGTH_SHORT).show();

            }else {
                Toast.makeText(LoginActivity.this, "서버에 문제가 있습니다.", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            pro_dialog =
                    ProgressDialog.show(
                            LoginActivity.this,
                            "",
                            "로그인 중...",
                            true);
        }

        private Integer processing() {
            // TODO Auto-generated method stub
            try {

                HttpClient http_client = new DefaultHttpClient();

                // 1.
                http_client.getParams().setParameter("http.connection.timeout",
                        7000);
                HttpPost http_post = null;

                List<NameValuePair> name_value = new ArrayList<NameValuePair>();

                http_post = new HttpPost("http://54.149.51.26/api/login.php");

                name_value.add(new BasicNameValuePair("email", edtEmailL.getText().toString().trim()));
                name_value.add(new BasicNameValuePair("passwd",edtPwdL.getText().toString().trim()));

                UrlEncodedFormEntity entityRequest = new UrlEncodedFormEntity(
                        name_value, "UTF-8");
                http_post.setEntity(entityRequest);

                HttpResponse response = http_client.execute(http_post);

                // 1.
                BufferedReader reader = new BufferedReader(

                        new InputStreamReader(response.getEntity().getContent(),
                                "UTF-8"), 8);

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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
//        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin : {
                if(edtEmailL.getText().toString().trim().length() == 0) {
                    Toast.makeText(getApplication(),"Check your Email",Toast.LENGTH_SHORT).show();
                    return;
                } else if(edtPwdL.getText().toString().trim().length() == 0) {
                    Toast.makeText(getApplication(),"Check your Passwd", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    onLogin();
                }
                break;
            }
            case R.id.txtRegist : {
                Intent it = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(it);
                finish();
                break;
            }
        }
    }
}
