package com.ayp.nudyim.friend;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.ayp.nudyim.R;

import de.hdodenhof.circleimageview.CircleImageView;
/**
 * Created by Chaiwat on 10/5/2016.
 */
public class FriendHolder extends RecyclerView.ViewHolder {
    public TextView mName;
    public CircleImageView mPhotoProfileCircleView;
    public FriendHolder(View itemView) {
        super(itemView);
            mName = (TextView) itemView.findViewById(R.id.name_profile);
            mPhotoProfileCircleView = (CircleImageView)itemView.findViewById(R.id.friend_profile);
    }
}
