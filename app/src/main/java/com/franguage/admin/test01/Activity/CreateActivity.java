package com.franguage.admin.test01.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.franguage.admin.test01.Item.ListItem;
import com.franguage.admin.test01.R;
import com.franguage.admin.test01.Utils.RoundImageView;
import com.franguage.admin.test01.Utils.Utils;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 2015-08-14.
 */
public class CreateActivity extends ActionBarActivity {

    private android.support.v7.widget.Toolbar mToolBar;

    private android.support.v7.app.AlertDialog mDialog;

    private String mRoomName;
    private String mRoomLocationFirst;
    private String mRoomLocationSecond;
    private String mRoomLanguage;
    private String mRoomMaxMember;
    private String mRoomInfo;

    private Spinner mSetMemberSpinner;
    private Spinner mSetLanguageSpinner;
    private Spinner mSetLocationFirst;
    private Spinner mSetLocationSecond;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_ALBUM = 2;
    private static final int REQUEST_IMAGE_CROP = 3;
    private String mCurrentPhotoPath;
    private Uri mContentUri;
    private RoundImageView mImage;

    private ProgressDialog dialog;

    String room_key;

    // test photo and network
    Bitmap photo;
    private Uri mImageCaptureUri;
    ImageView img;
    String user_id;

    ArrayAdapter<String> guAdapter;
    ArrayList<String> si_list = new ArrayList<>();
    ArrayList<String> gu_list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        mToolBar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);

        Intent it = getIntent();
        si_list = it.getExtras().getStringArrayList("si_list");

        final ActionBar mActionbar = getSupportActionBar();

        mActionbar.setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_24dp);
        mActionbar.setDisplayHomeAsUpEnabled(true);


        initRoomValue();
    }

    private void initRoomValue() {
        this.mRoomInfo = null;
        this.mRoomMaxMember = null;
        this.mRoomLanguage = null;
        this.mRoomLocationFirst = null;
        this.mRoomLocationSecond = null;
        this.mRoomName = null;
        initRoomLanguage();
        initRoomMember();
        initRoomLocation();
        initImage();
    }

    private void initImage() {
        img = (ImageView) findViewById(R.id.ic_activity_crate_room_select_room_image);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.ic_activity_crate_room_select_room_image:
                        mDialog = CreateDialog();
                        mDialog.show();
                        break;
                }
            }
        });
    }

    private android.support.v7.app.AlertDialog CreateDialog() {
        final android.support.v7.app.AlertDialog.Builder mAB = new android.support.v7.app.AlertDialog.Builder(this);
        mAB.setTitle("사진선택하기");
        mAB.setCancelable(true);
        mAB.setNeutralButton("사진 촬영", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                doTakePhotoAction();
//                finish();
            }
        })
                .setNegativeButton("앨범에서 가져오기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doTakeAlbumAction();
//                        Intent mIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                        startActivityForResult(mIntent, REQUEST_IMAGE_ALBUM);
                    }
                })
                .setPositiveButton("취소하기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setDismiss(mDialog);
                    }
                });
        return mAB.create();
    }

    private void setDismiss(Dialog dia) {
        if (dia != null && dia.isShowing())
            dia.dismiss();
    }

    private void setRoomInfo() {
        EditText mText = (EditText) findViewById(R.id.activity_create_room_input_info);
        mRoomInfo = mText.getText().toString();

//        if (mText.getText().length() == 0) {
//            Toast.makeText(CreateActivity.this, "방의 이름을 설정해주세요", Toast.LENGTH_SHORT).show();
//        }
    }

    private void setRoomImage() {

    }

    private void setRoomName() {
        EditText mText = (EditText) findViewById(R.id.activity_create_room_input_name);
        mRoomName = mText.getText().toString();
    }

    private void initRoomLocation() {
        mSetLocationFirst = (Spinner) findViewById(R.id.activity_create_room_location_spinner01);
        mSetLocationSecond = (Spinner) findViewById(R.id.activity_create_room_location_spinner02);


//        ArrayList<String> arr = new ArrayList<>();
//        arr = si_list;

        ArrayAdapter<String> siAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item, si_list);
        guAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, gu_list);
        mSetLocationSecond.setAdapter(guAdapter);
        mSetLocationFirst.setAdapter(siAdapter);
        mSetLocationFirst.setPrompt("지역 선택");
        mSetLocationFirst.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mRoomLocationFirst = (String) mSetLocationFirst.getSelectedItem();
                mSetLocationSecond.setVisibility(View.VISIBLE);


                new NetworkGetGu().execute("");
                guAdapter.notifyDataSetChanged();
                mSetLocationSecond.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mRoomLocationSecond = (String) mSetLocationSecond.getSelectedItem();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setRoomLocation() {


/*        Log.e("test","fadfa");
        mSetLocationFirst.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mRoomLocationFirst = (String) mSetLocationFirst.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(CreateActivity.this, "지역 선택", Toast.LENGTH_SHORT).show();
            }
        });*/

    }

    private void initRoomMember() {
        final String[] mMember = getResources().getStringArray(R.array.spinnerSetMaxMember);
        final ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, mMember);

        mSetMemberSpinner = (Spinner) findViewById(R.id.activity_create_room_member_spinner);
        mSetMemberSpinner.setAdapter(mAdapter);
        mSetMemberSpinner.setPrompt("최대 인원을 설정하세요.");
        setRoomMember();
    }

    private void setRoomMember() {
        mSetMemberSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mRoomMaxMember = (String) mSetMemberSpinner.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(CreateActivity.this, "최대 인원을 설정해주셔야 합니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initRoomLanguage() {
        String[] mLanguage = getResources().getStringArray(R.array.spinnerSetLanguage);
        ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, mLanguage);
        mSetLanguageSpinner = (Spinner) findViewById(R.id.activity_create_room_language_spinner);
        mSetLanguageSpinner.setAdapter(mAdapter);
        setRoomLanguage();
    }

    private void setRoomLanguage() {
        mSetLanguageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mRoomLanguage = (String) mSetLanguageSpinner.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(CreateActivity.this, "모임의 언어를 설정해주셔야 합니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    //        확인 버튼
    public void CreateConfirm(View v) {
        setRoomInfo();
        setRoomName();
//        setRoomImage();
//        setRoomMember();
//        setRoomLanguage();
//        setRoomLocation();
        CheckInputValue();

    }

    public void CreateCancel(View v) {
        Toast.makeText(getApplicationContext(), "취소하였습니다.", Toast.LENGTH_SHORT).show();
        finish();
    }

    //    NULL값 체크
    private void CheckInputValue() {
        if (mRoomLanguage == null) {
            AlertDialogCheckValue("모임 언어");
            return;
        }
//        if (mRoomMaxMember == null) {
//            AlertDialogCheckValue("");
//            return;
//        }
        if (mRoomInfo.length() == 0) {
            AlertDialogCheckValue("모임 소개");
            return;
        }
        if (mRoomName.length() == 0) {
            AlertDialogCheckValue("모임 이름");
            return;
        }
        if (mRoomLocationFirst.equals("null")) {
            AlertDialogCheckValue("모임 지역");
            return;
        }
        if (mRoomLocationSecond == null) {
//            AlertDialogCheckValue(mRoomLocationSecond);
//            return;
            mRoomLocationSecond = "";
        }
        AlertDialogInputValue();
    }

    //    유저가 입력한 값 확인
    protected void AlertDialogInputValue() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        mBuilder.setTitle("입력 정보");
        mBuilder.setMessage(mRoomName + "\r\n" + mRoomInfo + "\r\n" + mRoomLanguage + "\r\n" + mRoomLocationFirst + "\r\n" + mRoomLocationSecond + "\r\n" + mRoomMaxMember + "\r\n");
        mBuilder.setCancelable(false);
        mBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                user_id = Utils.getAppPreferences(CreateActivity.this, "user_key");
                new NetworkSetImage().execute("");

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

    private void AlertDialogCheckValue(Object input) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        mBuilder.setTitle("누락 정보");
        mBuilder.setMessage(input + "을(를) 입력해주세요");
        mBuilder.setCancelable(false);
        mBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        mBuilder.show();
    }

    private class NetworkGetGu extends AsyncTask<String, String, Integer> {
        private String err_msg = "Network error.";
        private JSONObject jObject;
        private ProgressDialog dialog_gu;


        @Override
        protected Integer doInBackground(String... params) {
            return processing();
        }

        @Override
        protected void onPostExecute(Integer result) {
            // TODO Auto-generated method stub
            dialog_gu.cancel();

            if (result == 0) {

                try {
//                    si_list = new String[jObject.getInt("cnt")];
                    gu_list.clear();
                    for(int i = 0; i < jObject.getInt("cnt"); i++){
                        String gu = jObject.getJSONArray("ret").getJSONObject(i).getString("gu");

//                        si_list[i] = si;
//                        Log.e("test", si_list[i]);
                        gu_list.add(gu);
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
            Log.e("Pre","onPreExecute");
            dialog_gu = ProgressDialog.show(CreateActivity.this, "", "...", true);
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

                List<NameValuePair> name_value =
                        new ArrayList<NameValuePair>();

                http_post = new HttpPost("http://54.149.51.26/api/get_gu_list.php");

                name_value.add(new BasicNameValuePair("si", mRoomLocationFirst));

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

    /**
     * 카메라 호출 하기
     */
    private void doTakePhotoAction() {
        Log.i("profile", "doTakePhotoAction()");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Crop된 이미지를 저장할 파일의 경로를 생성
        mImageCaptureUri = createSaveCropFile();
        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
                mImageCaptureUri);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    /**
     * 앨범 호출 하기
     */
    private void doTakeAlbumAction() {
        Log.i("profile", "doTakeAlbumAction()");
        // 앨범 호출
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, REQUEST_IMAGE_ALBUM);
    }

    /**
     * Crop된 이미지가 저장될 파일을 만든다.
     *
     * @return Uri
     */
    private Uri createSaveCropFile() {
        Uri uri;
        String url = "tmp_" + String.valueOf(System.currentTimeMillis())
                + ".jpg";
        uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Franguage/",
                url));
        return uri;
    }

    /**
     * Result Code
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("profile", "onActivityResultX");
        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case REQUEST_IMAGE_ALBUM: {
                Log.d("profile", "PICK_FROM_ALBUM");

                // 이후의 처리가 카메라와 같으므로 일단 break없이 진행합니다.
                // 실제 코드에서는 좀더 합리적인 방법을 선택하시기 바랍니다.
                mImageCaptureUri = data.getData();
                File original_file = getImageFile(mImageCaptureUri);

                mImageCaptureUri = createSaveCropFile();
                File cpoy_file = new File(mImageCaptureUri.getPath());

                // SD카드에 저장된 파일을 이미지 Crop을 위해 복사한다.
                copyFile(original_file, cpoy_file);
            }

            case REQUEST_IMAGE_CAPTURE: {
                Log.d("profile", "PICK_FROM_CAMERA");

                // 이미지를 가져온 이후의 리사이즈할 이미지 크기를 결정합니다.
                // 이후에 이미지 크롭 어플리케이션을 호출하게 됩니다.

                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(mImageCaptureUri, "image/*");

                // //// 이부분이 크롭할 사이즈 결정.
                intent.putExtra("outputX", 112);
                intent.putExtra("outputY", 112);
                // intent.putExtra("aspectY", 16);
                intent.putExtra("scale", true);
                intent.putExtra("noFaceDetection", true);

                // ////
                // Crop한 이미지를 저장할 Path
                intent.putExtra("output", mImageCaptureUri);

                // Return Data를 사용하면 번들 용량 제한으로 크기가 큰 이미지는
                // 넘겨 줄 수 없다.
                // intent.putExtra("return-data", true);
                startActivityForResult(intent, REQUEST_IMAGE_CROP);

                break;
            }

            case REQUEST_IMAGE_CROP: {
                Log.w("profile", "CROP_FROM_CAMERA");

                // Crop 된 이미지를 넘겨 받습니다.
                Log.w("profile", "mImageCaptureUri = " + mImageCaptureUri);

                String full_path = mImageCaptureUri.getPath();
                String[] s_path = full_path.split("/");
                // mnt 가 있는 경우다.
                int index = s_path[0].length() + 1;

                String photo_path = full_path.substring(index, full_path.length());

                Log.w("profile", "비트맵 Image path = " + photo_path);

                BitmapFactory.Options options = new BitmapFactory.Options();
                for (options.inSampleSize = 1; options.inSampleSize <= 32; options.inSampleSize++) {
                    try {
                        photo = BitmapFactory.decodeFile(photo_path, options);
                        Log.d("TAG_LOG", "Decoded successfully for sampleSize "
                                + options.inSampleSize);
                        break;
                    } catch (OutOfMemoryError outOfMemoryError) {
                        // If an OutOfMemoryError occurred, we continue with for
                        // loop and next inSampleSize value
                        Log.e("TAG_LOG",
                                "outOfMemoryError while reading file for sampleSize "
                                        + options.inSampleSize
                                        + " retrying with higher value");
                    }
                }

                // photo = BitmapFactory.decodeFile(photo_path);
                img.setImageBitmap(photo);
                // img_phoz.setBackgroundDrawable(new BitmapDrawable(photo));
                // // 네트워크로 보낸다.
                // new NetworkSetImage().execute();

                break;
            }
        }
    }

    /**
     * 선택된 uri의 사진 Path를 가져온다. uri 가 null 경우 마지막에 저장된 사진을 가져온다.
     *
     * @param uri
     * @return
     */
    private File getImageFile(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        if (uri == null) {
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }

        Cursor mCursor = getContentResolver().query(uri, projection, null,
                null, MediaStore.Images.Media.DATE_MODIFIED + " desc");
        if (mCursor == null || mCursor.getCount() < 1) {
            return null; // no cursor or no record
        }
        int column_index = mCursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        mCursor.moveToFirst();

        String path = mCursor.getString(column_index);

        if (mCursor != null) {
            mCursor.close();
            mCursor = null;
        }

        return new File(path);
    }

    /**
     * 파일 복사
     *
     * @param srcFile
     *            : 복사할 File
     * @param destFile
     *            : 복사될 File
     * @return
     */
    public static boolean copyFile(File srcFile, File destFile) {
        boolean result = false;
        try {
            InputStream in = new FileInputStream(srcFile);
            try {
                result = copyToFile(in, destFile);
            } finally {
                in.close();
            }
        } catch (IOException e) {
            result = false;
        }
        return result;
    }

    /**
     * Copy data from a source stream to destFile. Return true if succeed,
     * return false if failed.
     */
    private static boolean copyToFile(InputStream inputStream, File destFile) {
        try {
            OutputStream out = new FileOutputStream(destFile);
            try {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) >= 0) {
                    out.write(buffer, 0, bytesRead);
                }
            } finally {
                out.close();
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private class NetworkSetImage extends AsyncTask<String, String, Integer> {

        private String err_msg = "Network error.";


        JSONObject jObject;

        @Override
        protected Integer doInBackground(String... params) {
            // TODO Auto-generated method stub
            Log.e("Back", "DoInBackground");
            return processing();
        }

        @Override
        protected void onPostExecute(Integer result) {
            // TODO Auto-generated method stub
//            dialog.cancel();
            Log.e("Post","onPostExecute");

            if (result == 100) {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Parsing error.", Toast.LENGTH_SHORT);
                toast.show();
                return;
            } else if (result > 0) {

                if(result == 33) {
                    Toast.makeText(getApplicationContext(), "사진을 등록하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast toast = Toast.makeText(getApplicationContext(), err_msg, Toast.LENGTH_SHORT);
                toast.show();
                return;
            } else if (result == 0) {
                // 넘어가장

                new NetworkGetRoomKey().execute("");
/*                Toast toast = Toast.makeText(getApplicationContext(),
                        "등록되었습니다.", Toast.LENGTH_SHORT);
                toast.show();
                //setResult(1); // 새로고침 하라는 뜻
                finish();*/
            }

        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            Log.e("Pre", "onPreExecute");
            dialog = ProgressDialog.show(CreateActivity.this, "", "사진 업로드중...", true);
        }

        private Integer processing() {
            // TODO Auto-generated method stub
            try {
                Log.e("processing", "processing");
                HttpClient http_client = new DefaultHttpClient();

                // 1. 커넥션 타임아웃 설정.
                http_client.getParams().setParameter("http.connection.timeout", 10000);
                HttpPost http_post = null;

                http_post = new HttpPost("http://54.149.51.26/api/set_room_list.php");

                MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
                Log.e("Multi", "Multi");

                reqEntity.addPart("room_name", new StringBody(mRoomName, Charset.defaultCharset()));
                reqEntity.addPart("create_user_key", new StringBody(user_id, Charset.defaultCharset()));
                reqEntity.addPart("room_introduce", new StringBody(mRoomInfo, Charset.defaultCharset()));
                reqEntity.addPart("room_location", new StringBody(mRoomLocationFirst+mRoomLocationSecond,Charset.defaultCharset()));
                reqEntity.addPart("room_language", new StringBody(mRoomLanguage, Charset.defaultCharset()));
                reqEntity.addPart("room_count", new StringBody(mRoomMaxMember, Charset.defaultCharset()));


                if (photo != null) {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    photo.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                    Log.e("photo",photo.toString());

                    String time = System.currentTimeMillis()+"";
                    byte[] data = bos.toByteArray();
                    ByteArrayBody bab = new ByteArrayBody(data, time +".jpeg");  // need change img name (ex. time.jpeg)
                    reqEntity.addPart("time",new StringBody(time, Charset.defaultCharset()));
                    reqEntity.addPart("img", bab);
                    Log.e("bab",bab.toString());
                    Log.e("photo", data.toString());

                } else {
                    return 33;
                }
                http_post.setEntity(reqEntity);
                HttpResponse response = http_client.execute(http_post);

                // 1. 버퍼로 json 읽기.
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"), 8);
                Log.e("reader","success reader");
                StringBuilder builder = new StringBuilder();
                Log.e("Build","success Build");
                for (String line = null; (line = reader.readLine()) != null;) {
                    builder.append(line).append("\n");
                }

                Log.e("return", "json : " + builder.toString());

                // 2. json 형식으로 만들기.

                jObject = new JSONObject(builder.toString());
                Log.e("err",jObject.getInt("err")+"");

                Log.e("jObject",builder.toString());

                // 2-1. 에러 체크.
                if (jObject.getInt("err") > 0) {

                    Log.e("getErr","err");
                    return jObject.getInt("err");
                }
            } catch (Exception e) {
                // TODO: handle exception
                // 네트워크 에러 처리
                // Log.i("errr", "" + e);
                Log.e("LDS", "Exception : " + e.toString());
                return 100;
            }

            return 0;

        }
    }

    private class NetworkGetRoomKey extends AsyncTask<String, String, Integer> {
        private String err_msg = "Network error.";
//        private ProgressDialog dialog;

        JSONObject jObject;
        protected void onPreExecute() {

//            dialog = ProgressDialog.show(CreateActivity.this, "", "", true);

        }

        protected Integer doInBackground(String... params) {

            return processiong();
        }

        private Integer processiong() {

            try {
                HttpClient  http_client = new DefaultHttpClient();
                http_client.getParams().setParameter("http.connection.timeout", 7000);
                HttpPost http_post = null;

                ArrayList<NameValuePair> name_value = new ArrayList<NameValuePair>();
                http_post = new HttpPost("http://54.149.51.26/api/get_room_key.php");
//			http_post = new HttpPost(uri);
                name_value.add(new BasicNameValuePair("user_key", Utils.getAppPreferences(getApplicationContext(),"user_key")));
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
        protected void onPostExecute(Integer result) {
//            dialog.cancel();

            Log.e("result", result + "");
            if(result == 0)
            {
                try {
                    for(int i=0;i<jObject.getInt("cnt"); i++)
                    {
                        room_key = jObject.getJSONArray("ret").getJSONObject(0).getString("room_key");
                    }

                    new NetworkSetMyAttend().execute("");



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

    private class NetworkSetMyAttend extends  AsyncTask<String, String, Integer> {

        private String err_msg = "Network error.";
//        private ProgressDialog dialog;

        JSONObject jObject;
        protected void onPreExecute() {

//            dialog = ProgressDialog.show(CreateActivity.this, "", "", true);

        }

        @Override
        protected Integer doInBackground(String... params) {
            return processiong();
        }

        private Integer processiong() {

            try {
                HttpClient  http_client = new DefaultHttpClient();
                http_client.getParams().setParameter("http.connection.timeout", 7000);
                HttpPost http_post = null;

                ArrayList<NameValuePair> name_value = new ArrayList<NameValuePair>();
                http_post = new HttpPost("http://54.149.51.26/api/set_my_attend_list.php");
//			http_post = new HttpPost(uri);
                name_value.add(new BasicNameValuePair("user_key", Utils.getAppPreferences(getApplicationContext(),"user_key")));
                name_value.add(new BasicNameValuePair("room_key", room_key));
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
                Log.e("error","err");
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
        protected void onPostExecute(Integer result) {
            dialog.cancel();

            Log.e("result", result + "");
            if(result ==0)
            {
                Toast.makeText(getApplicationContext(),"등록성공",Toast.LENGTH_SHORT).show();
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
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (photo != null) {
            photo.recycle();
            photo = null;
        }
    }


}
