package com.franguage.admin.test01.Activity;

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
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.franguage.admin.test01.R;
import com.franguage.admin.test01.Utils.RoundImageView;
import com.franguage.admin.test01.Utils.Utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
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

/**
 * Created by Admin on 2015-08-19.
 */
public class ProfileDetailEditActivity extends ActionBarActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_ALBUM = 2;
    private static final int REQUEST_IMAGE_CROP = 3;

    private android.support.v7.app.AlertDialog mDialog;
    private Toolbar mToolBar;

    Bitmap photo;
    private Uri mImageCaptureUri;
    RoundImageView img;
    String land;
    String gender;
    String activity;
    String languageW;
    String languageP;
    String introduce;
    EditText edtInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_profile_edit);

        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_24dp);
        ab.setDisplayHomeAsUpEnabled(true);

        TextView mName = (TextView) findViewById(R.id.profile_detail_user_name);
        mName.setVisibility(View.INVISIBLE);

        initSpinner();

        img = (RoundImageView) findViewById(R.id.profile_detail_user_image);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog = CreateDialog();
                mDialog.show();
            }
        });


        edtInfo = (EditText)findViewById(R.id.profile_detail_user_introduce);


        Button button =(Button)findViewById(R.id.btnConfirm);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                introduce = edtInfo.getText().toString();
                new NetworkSetImage().execute("");
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
        private ProgressDialog dialog;

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
            dialog.cancel();
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
                Toast.makeText(getApplicationContext(),"수정 됨",Toast.LENGTH_SHORT).show();
                finish();

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
            dialog = ProgressDialog.show(ProfileDetailEditActivity.this, "", "사진 업로드중...", true);
            Log.e("dialog","dialog");
        }

        private Integer processing() {
            // TODO Auto-generated method stub
            try {
                Log.e("processing", "processing");
                HttpClient http_client = new DefaultHttpClient();

                // 1. 커넥션 타임아웃 설정.
                http_client.getParams().setParameter("http.connection.timeout", 10000);
                HttpPost http_post = null;

                http_post = new HttpPost("http://54.149.51.26/api/set_profile.php");

                MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
                Log.e("Multi", "Multi");

                reqEntity.addPart("user_key", new StringBody(Utils.getAppPreferences(getApplicationContext(), "user_key"), Charset.defaultCharset()));
                Log.e("string", Utils.getAppPreferences(getApplicationContext(), "user_key"));
                reqEntity.addPart("land", new StringBody(land, Charset.defaultCharset()));
                reqEntity.addPart("sex", new StringBody(gender, Charset.defaultCharset()));
                reqEntity.addPart("activity", new StringBody(activity,Charset.defaultCharset()));
                reqEntity.addPart("languageP", new StringBody(languageP, Charset.defaultCharset()));
                reqEntity.addPart("languageW", new StringBody(languageW, Charset.defaultCharset()));
                reqEntity.addPart("introduce", new StringBody(introduce, Charset.defaultCharset()));

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

    private void setDismiss(Dialog dia) {
        if (dia != null && dia.isShowing())
            dia.dismiss();
    }

    protected void initSpinner() {
        initCountry();
        initGender();
        initLanguage();
        initLocation();

    }

    protected void initCountry() {
        String[] mCountryString = getResources().getStringArray(R.array.spinnerSetCountry);
        final Spinner mCountrySpinner = (Spinner) findViewById(R.id.profile_detail_user_country);
        ArrayAdapter<String> mCountryAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, mCountryString);
        mCountrySpinner.setAdapter(mCountryAdapter);
        mCountrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                land = (String)mCountrySpinner.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    protected void initGender() {
        String[] mGenderString = getResources().getStringArray(R.array.spinnerSetGender);
        final Spinner mGenderSpinner = (Spinner) findViewById(R.id.profile_detail_user_gender);
        ArrayAdapter<String> mGenderAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, mGenderString);
        mGenderSpinner.setAdapter(mGenderAdapter);
        mGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gender = (String)mGenderSpinner.getSelectedItem();
                Log.e("gender",gender);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    protected void initLanguage() {
        String[] mWantLanguageString = getResources().getStringArray(R.array.spinnerSetLanguage);
        final Spinner mWantLanguageSpinner = (Spinner) findViewById(R.id.profile_detail_user_want_language);
        ArrayAdapter<String> mWantLanguageAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, mWantLanguageString);
        mWantLanguageSpinner.setAdapter(mWantLanguageAdapter);
        mWantLanguageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                languageW = (String)mWantLanguageSpinner.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        String[] mOwnLanguageString = getResources().getStringArray(R.array.spinnerSetLanguage);
        final Spinner mOwnLanguageSpinner = (Spinner) findViewById(R.id.profile_detail_user_own_language);
        ArrayAdapter<String> mOwnLanguageAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, mOwnLanguageString);
        mOwnLanguageSpinner.setAdapter(mOwnLanguageAdapter);
        mOwnLanguageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                languageP = (String)mOwnLanguageSpinner.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    protected void initLocation() {
        String[] mLocation = getResources().getStringArray(R.array.spinnerGetSi);
        final Spinner mLocationSpinner = (Spinner)findViewById(R.id.profile_detail_user_location_first);
        ArrayAdapter<String> mLocationAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, mLocation);
        mLocationSpinner.setAdapter(mLocationAdapter);
        mLocationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                activity = (String) mLocationSpinner.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}
