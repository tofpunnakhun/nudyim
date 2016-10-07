package com.mrdo.nudyim.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mrdo.nudyim.FriendHolder;
import com.mrdo.nudyim.R;
import com.mrdo.nudyim.model.User;

/**
 * Created by onepi on 10/5/2016.
 */
public class ShowAllFriendFragment extends Fragment {
    private static final String TAG = "ShowAllFriendFragment";

    private DatabaseReference mDatabaseReference;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    private FirebaseRecyclerAdapter<User, FriendHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mLinearLayoutManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Firebase instance variables
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_show_all_friend, container, false);

        //[START create database reference]
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        mRecycler = (RecyclerView) rootView.findViewById(R.id.show_all_friend_list);
        mRecycler.setHasFixedSize(true);

        return rootView;
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Set up Layout Manager, reverse layout
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setReverseLayout(true);
        mLinearLayoutManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mLinearLayoutManager);

        // Set up FirebaseRecycleAdapter with the Query
        mAdapter = new FirebaseRecyclerAdapter<User, FriendHolder>(
                User.class,
                R.layout.holder_show_friend,
                FriendHolder.class,
                mDatabaseReference.child("user")) {
            @Override
            protected void populateViewHolder(FriendHolder viewHolder, User model, int position) {

                viewHolder.mName.setText(model.name);
                if (model.photoUrl == null) {
                    viewHolder.mPhotoProfileCircleView
                            .setImageDrawable(ContextCompat
                                    .getDrawable(getActivity(), R.drawable.ic_account_circle_black_36dp));
                } else {
                    Glide.with(getActivity())
                            .load(model.photoUrl)
                            .into(viewHolder.mPhotoProfileCircleView);
                }
            }
        };
        mRecycler.setAdapter(mAdapter);
    }
}