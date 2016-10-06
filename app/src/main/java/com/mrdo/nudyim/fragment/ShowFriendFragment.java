package com.mrdo.nudyim.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mrdo.nudyim.FriendHolder;
import com.mrdo.nudyim.R;
import com.mrdo.nudyim.model.User;

/**
 * Created by onepi on 10/5/2016.
 */
public class ShowFriendFragment extends Fragment {
    private static final String TAG = "ShowFriendFragment";

    private DatabaseReference mDatabaseReference;

    private FirebaseRecyclerAdapter<User, FriendHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mLinearLayoutManager;

    private FloatingActionButton mAddFriendFAB;
    private EditText mAddFriendEditText;
    private Button mAddButton;

    private  static boolean mFlagAdd = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_show_friends, container, false);

        mAddFriendEditText = (EditText) rootView.findViewById(R.id.edit_add_friend);
        mAddFriendEditText.setVisibility(View.INVISIBLE);

        mAddButton = (Button) rootView.findViewById(R.id.button_add_friend);
        mAddButton.setVisibility(View.INVISIBLE);
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "I will make it", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            }
        });

        mAddFriendFAB = (FloatingActionButton) rootView.findViewById(R.id.fab_add_friend);
        mAddFriendFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mFlagAdd) {
                    mAddFriendEditText.setVisibility(View.VISIBLE);
                    mAddButton.setVisibility(View.VISIBLE);
                    mFlagAdd = false;
                }else{
                    mAddFriendEditText.setVisibility(View.INVISIBLE);
                    mAddButton.setVisibility(View.INVISIBLE);
                    mFlagAdd = true;
                }
            }
        });

        //[START create database reference]
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        mRecycler = (RecyclerView) rootView.findViewById(R.id.show_friend_list);
        mRecycler.setHasFixedSize(true);

        return rootView;
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