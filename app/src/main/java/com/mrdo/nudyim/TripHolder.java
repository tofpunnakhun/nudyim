package com.mrdo.nudyim;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Chaiwat on 10/11/2016.
 */

public class TripHolder extends RecyclerView.ViewHolder {
    public TextView mTopic;
    public TextView mLocation;
    public CircleImageView mPhofileCircleImageView;
    public TripHolder(View itemView) {
        super(itemView);
        mTopic = (TextView) itemView.findViewById(R.id.topic_trip);
        mLocation = (TextView) itemView.findViewById(R.id.location_trip);
        mPhofileCircleImageView = (CircleImageView) itemView.findViewById(R.id.profile_trip);
    }
}
