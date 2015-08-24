package com.franguage.admin.test01.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.franguage.admin.test01.Item.ListItem;
import com.franguage.admin.test01.R;
import com.franguage.admin.test01.Utils.Holder;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import com.franguage.admin.test01.Utils.Img_Path;
/**
 * Created by Admin on 2015-08-12.
 */
public class ListAdapter extends ArrayAdapter<ListItem> {

    private LayoutInflater mInflater = null;
    private Context mContext = null;
    private ImageLoader loader = null;

    public ListAdapter(Context context, int resource, List<ListItem> objects) {
        super(context, resource, objects);

        mInflater = LayoutInflater.from(context);
        mContext = context;
        loader = ImageLoader.getInstance();
    }


/*    private RoundImageView mRoomImageView;
    private TextView mRoomNameView;
    private TextView mRoomInfoView;
    private TextView mRoomLocationView;
    private TextView mRoomLanguageView;
    private TextView mRoomMemberCountView;

    @Override
    public long getItemId(int position) {
        return position;
    }*/

    public View getView(int position, View convertView, ViewGroup parent) {

        Holder holder;

        if (convertView == null) {
            holder = new Holder();
            convertView = mInflater.inflate(R.layout.fragment_home_item, parent, false);
            holder.mRoomNameView = (TextView) convertView.findViewById(R.id.roomName);
            holder.mRoomInfoView = (TextView) convertView.findViewById(R.id.roomInfo);
            holder.mRoomLanguageView = (TextView) convertView.findViewById(R.id.roomLanguage);
            holder.mRoomLocationView = (TextView) convertView.findViewById(R.id.roomLocation);
            holder.mRoomImageView = (com.franguage.admin.test01.Utils.RoundImageView) convertView.findViewById(R.id.roomImage);
            holder.mRoomMemberView = (TextView)convertView.findViewById(R.id.roomMemberCount);
            convertView.setTag(holder);
        }

        holder = (Holder) convertView.getTag();
        ListItem item = getItem(position);
        holder.mRoomNameView.setText(item.getmRoomName());
        holder.mRoomInfoView.setText(item.getmRoomInfo());
        holder.mRoomLocationView.setText(item.getmRoomLocation());
        holder.mRoomLanguageView.setText(item.getmRoomLanguage());
        holder.mRoomMemberView.setText(item.getmRoomMember());
        loader.displayImage(Img_Path.IMG_PATH + item.getmRoomImage(), holder.mRoomImageView);

        return convertView;
    }
}

/*            mRoomImageView = (RoundImageView) convertView.findViewById(R.id.roomImage);
//            mRoomImageView.setImageResource(mItem.getmRoomImage());
            loader.displayImage(Img_Path.IMG_PATH+mItem.getmRoomImage(),mRoomImageView);

            mRoomNameView = (TextView) convertView.findViewById(R.id.roomName);
            mRoomNameView.setText(mItem.getmRoomName());

            mRoomInfoView = (TextView) convertView.findViewById(R.id.roomInfo);
            mRoomInfoView.setText(mItem.getmRoomInfo());

            mRoomLocationView = (TextView) convertView.findViewById(R.id.roomLocation);
            mRoomLocationView.setText(mItem.getmRoomLocation());

            mRoomLanguageView = (TextView) convertView.findViewById(R.id.roomLanguage);
            mRoomLanguageView.setText(mItem.getmRoomLanguage());

            mRoomMemberCountView = (TextView) convertView.findViewById(R.id.roomMemberCount);
            mRoomMemberCountView.setText(mItem.getmRoomMember());
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(),mItem.getmRoomName(),Toast.LENGTH_SHORT).show();
            }
        });
        return convertView;
    }
/*
    public void add(ListItem mDummy) {
        mList.add(mDummy);
    }

    @Override
    public void onClick(View v) {
//        TODO Auto - getnerated method stub
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }*/

//}
