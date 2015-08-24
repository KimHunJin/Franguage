package com.franguage.admin.test01.Adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.franguage.admin.test01.R;

/**
 * Created by Admin on 2015-08-19.
 */
public class ViewPagerAdapter extends PagerAdapter {

    Context mContext;
    int[] mImage;
    LayoutInflater mInflater;
    ViewPager mViewPager;

    public ViewPagerAdapter(Context context, int[] image) {
        mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mImage = image;
    }

    @Override
    public int getCount() {
        return mImage.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View mView = null;
        ImageView mImageView = null;
        Button mButton = null;
        if (position == 0) {
            mView = mInflater.inflate(R.layout.viewpager_type_one, null);
            mImageView = (ImageView) mView.findViewById(R.id.img);
            mImageView.setImageResource(mImage[position]);

        } else if (position == 1) {
            mView = mInflater.inflate(R.layout.viewpager_type_one, null);
            mImageView = (ImageView) mView.findViewById(R.id.img);
            mImageView.setImageResource(mImage[position]);

        } else {
            mView = mInflater.inflate(R.layout.viewpager_type_two, null);
            mImageView = (ImageView) mView.findViewById(R.id.img);
            mImageView.setImageResource(mImage[position]);
        }
        ((ViewPager) container).addView(mView);
        return mView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object obj) {
        ((ViewPager) container).removeView((RelativeLayout) obj);
    }
}
