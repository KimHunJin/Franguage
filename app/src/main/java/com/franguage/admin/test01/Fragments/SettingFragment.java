package com.franguage.admin.test01.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.franguage.admin.test01.Activity.AboutUsActivity;
import com.franguage.admin.test01.MainActivity;
import com.franguage.admin.test01.R;
import com.franguage.admin.test01.Utils.Utils;

/**
 * Created by Admin on 2015-08-12.
 */
public class SettingFragment extends Fragment implements View.OnClickListener {

    private View mView;
    private Switch mSwitchButton;
//    private ImageView mPolicy;
//    private ImageView mAboutUs;
//    private ImageView mLogOut;

    private RelativeLayout mPolicy;

    private RelativeLayout mAboutUs;
    private RelativeLayout mLogOut;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_setting, container, false);
        mSwitchButton = (Switch) mView.findViewById(R.id.activity_setting_switch);
//        mPolicy = (ImageView) mView.findViewById(R.id.activity_setting_policy);
//        mAboutUs = (ImageView) mView.findViewById(R.id.ic_activity_setting_about);
//        mLogOut = (ImageView) mView.findViewById(R.id.ic_activity_setting_logout);
//        mSwitchButton = (RelativeLayout) mView.findViewById(R.id.layout00);
        mPolicy = (RelativeLayout) mView.findViewById(R.id.layout01);
        mAboutUs = (RelativeLayout) mView.findViewById(R.id.layout02);
        mLogOut = (RelativeLayout) mView.findViewById(R.id.layout03);
        SetOnClick();
        return mView;
    }

    private void SetOnClick() {
        mSwitchButton.setOnClickListener(this);
        mPolicy.setOnClickListener(this);
        mAboutUs.setOnClickListener(this);
        mLogOut.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_setting_switch:
                Toast.makeText(getActivity(), "Switch", Toast.LENGTH_SHORT).show();
                break;
            case R.id.layout01:
                Toast.makeText(getActivity(), "Policy", Toast.LENGTH_SHORT).show();
                break;
            case R.id.layout02:
                Intent mIntent = new Intent(getActivity(),AboutUsActivity.class);
                startActivity(mIntent);
                break;
            case R.id.layout03:
                Utils.setAppPreferences(getActivity(), "is_auto", "0");
                Toast.makeText(getActivity(), "Success Logout", Toast.LENGTH_SHORT).show();

                Intent it = new Intent(getActivity(), MainActivity.class);
                startActivity(it);

                getActivity().finish();
                break;
        }
    }

}
