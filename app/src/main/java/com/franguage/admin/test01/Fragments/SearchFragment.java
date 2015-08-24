package com.franguage.admin.test01.Fragments;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.franguage.admin.test01.Activity.SearchResultActivity;
import com.franguage.admin.test01.MainActivity;
import com.franguage.admin.test01.Network.NetworkGetGu;
import com.franguage.admin.test01.Network.NetworkGetSi;
import com.franguage.admin.test01.R;

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
public class SearchFragment extends Fragment {

    private View mView;
    private Context mContext;
    private Spinner mSpinnerLocationFirst;
    private Spinner mSpinnerLocationSecond;
    private Spinner mSpinnerMember;
    private Spinner mSpinnerLanguage;
//    ArrayList<String> gu_list = new ArrayList<>();

    static ArrayAdapter<String> mLocationFirstAdapter;
    ArrayAdapter<String> mLocationSecondAdapter;

    private String mLocationFirst;
    private String mSearchLanguage;
    private String mCount;
//    private String mLocationnSecond;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        String[] si = getResources().getStringArray(R.array.spinnerGetSi);

        mView = inflater.inflate(R.layout.fragment_search, container, false);
        mContext = mView.getContext();

        mSpinnerLocationFirst = (Spinner) mView.findViewById(R.id.search_location_si);
//        mSpinnerLocationSecond = (Spinner) mView.findViewById(R.id.search_location_gu);



//        mLocationSecondAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, gu_list);
//        mSpinnerLocationSecond.setAdapter(mLocationSecondAdapter);
        mLocationFirstAdapter = new ArrayAdapter<String>(mContext,android.R.layout.simple_spinner_dropdown_item, si);
        mSpinnerLocationFirst.setAdapter(mLocationFirstAdapter);
        mSpinnerLocationFirst.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mLocationFirst = (String) mSpinnerLocationFirst.getSelectedItem();
//                gu_list = new NetworkGetGu().getNetworkGu("  " + mLocationFirst);
//                mLocationSecondAdapter.notifyDataSetChanged();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mSpinnerLanguage = (Spinner) mView.findViewById(R.id.search_want_language_spinner);
        final String[] mLanguage = getResources().getStringArray(R.array.spinnerSetLanguage);
        final ArrayAdapter<String> mLanguageAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item, mLanguage);
        mSpinnerLanguage.setAdapter(mLanguageAdapter);
        mSpinnerLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSearchLanguage = (String)mSpinnerLanguage.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mSpinnerMember = (Spinner) mView.findViewById(R.id.search_pick_member);
        final String[] mMember = getResources().getStringArray(R.array.spinnerSetMaxMember);
        ArrayAdapter<String> mMemberAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item, mMember);
        mSpinnerMember.setAdapter(mMemberAdapter);
        mSpinnerMember.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCount = (String)mSpinnerMember.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        Button btnSearch = (Button)mView.findViewById(R.id.search_confirm);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
                public void onClick(View v) {

                Intent it = new Intent(getActivity(), SearchResultActivity.class);
                it.putExtra("location",mLocationFirst);
                it.putExtra("language",mSearchLanguage);
                it.putExtra("count",mCount);
                startActivity(it);

            }
        });


        return mView;
    }
}
