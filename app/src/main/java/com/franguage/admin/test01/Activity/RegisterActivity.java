package com.franguage.admin.test01.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.franguage.admin.test01.R;
import com.franguage.admin.test01.Utils.Utils;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
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

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    TextView txtLogo;
    private CallbackManager callbackManager;
    private LoginButton loginButtonR;
    private Button btnRegist;
    private EditText edtName, edtEmailR, edtPwdR, edtPwdC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_register);
        callbackManager = CallbackManager.Factory.create();
        loginButtonR = (LoginButton)findViewById(R.id.login_buttonR);
        loginButtonR.setReadPermissions("public_profile", "user_friends");
        loginButtonR.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
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

        txtLogo = (TextView)findViewById(R.id.txtLogoR);
        Typeface typeface = Typeface.createFromAsset(getAssets(),"fonts/Dynalight-Regular.ttf");
        txtLogo.setTypeface(typeface);

        edtName = (EditText)findViewById(R.id.edtName);
        edtEmailR = (EditText)findViewById(R.id.edtEmailR);
        edtPwdR = (EditText)findViewById(R.id.edtPwdR);
        edtPwdC = (EditText)findViewById(R.id.edtPwdC);

        btnRegist = (Button)findViewById(R.id.btnRegist);
        btnRegist.setOnClickListener(this);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRegist : {
                if(edtEmailR.getText().toString().trim().length() == 0 || edtName.getText().toString().trim().length() == 0) {
                    Toast.makeText(getApplication(), "이름과 이메일을 확인하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(edtPwdR.getText().toString().trim().equals(edtPwdC.getText().toString().trim()) == false) {
                    Toast.makeText(getApplication(),"비밀번호를 확인하세요.",Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    onSignUp();
                }
            }
        }
    }
    protected void onSignUp() {
        new NetworkRegister().execute("");
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub

        Intent it = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(it);
        finish();

    }


    private class NetworkRegister extends AsyncTask<String, String, Integer> {

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
                    int user_id = jObject.getInt("user_id");
                    Utils.setAppPreferences(RegisterActivity.this, "user_id", user_id + "");

                    // 메인 액티비티로 이동
                    Intent it = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(it);
                    finish();

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }else if(result == 3){
                Toast.makeText(RegisterActivity.this, "이미 회원 아이디가 존재합니다.", Toast.LENGTH_SHORT).show();

            }else {
                Toast.makeText(RegisterActivity.this, "서버에 문제가 있습니다.", Toast.LENGTH_SHORT).show();
            }



        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            pro_dialog =
                    ProgressDialog.show(
                            RegisterActivity.this,
                            "",
                            "회원가입 중...",
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

                http_post = new HttpPost("http://54.149.51.26/api/register.php");

                name_value.add(new BasicNameValuePair("name", edtName.getText().toString().trim()));
                name_value.add(new BasicNameValuePair("email", edtEmailR.getText().toString().trim()));
                name_value.add(new BasicNameValuePair("passwd", edtPwdR.getText().toString().trim()));


                UrlEncodedFormEntity entityRequest = new UrlEncodedFormEntity(name_value, "UTF-8");
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


}
