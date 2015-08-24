package com.franguage.admin.test01.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.franguage.admin.test01.Item.DetailItem;
import com.franguage.admin.test01.Item.ListItem;
import com.franguage.admin.test01.R;
import com.franguage.admin.test01.Utils.DetailHolder;
import com.franguage.admin.test01.Utils.Holder;
import com.franguage.admin.test01.Utils.Img_Path;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HunJin on 2015-08-22.
 */
public class DetailAdapter extends ArrayAdapter<DetailItem> {
    private LayoutInflater mInflater = null;
    private Context mContext = null;
    private ImageLoader loader = null;

    public DetailAdapter(Context context, int resource, ArrayList<DetailItem> object) {
        super(context, resource, object);
        mInflater = LayoutInflater.from(context);
        mContext = context;
        loader = ImageLoader.getInstance();
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        DetailHolder holder;

        if (convertView == null) {
            holder = new DetailHolder();
            convertView = mInflater.inflate(R.layout.activity_detail_room_item, parent, false);
            holder.Dcontent = (TextView) convertView.findViewById(R.id.board_user_comments);
            holder.Dtime = (TextView) convertView.findViewById(R.id.board_timestamp);
            holder.Duser = (TextView) convertView.findViewById(R.id.board_user_name);
            holder.imageView = (com.franguage.admin.test01.Utils.RoundImageView) convertView.findViewById(R.id.board_user_icon);
             convertView.setTag(holder);
        }

        holder = (DetailHolder) convertView.getTag();
        DetailItem item = getItem(position);
        holder.Dcontent.setText(item.getComment());
        holder.Dtime.setText(item.getTime());
        holder.Duser.setText(item.getUsername());
        loader.displayImage(Img_Path.IMG_PATH + item.getImg(), holder.imageView);

        return convertView;
    }
}
