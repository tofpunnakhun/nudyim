package com.mrdo.nudyim.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mrdo.nudyim.CreateTripActivity;
import com.mrdo.nudyim.R;
import com.mrdo.nudyim.TripHolder;
import com.mrdo.nudyim.model.Trip;

/**
 * Created by onepi on 10/5/2016.
 */
public class ShowTripFragment extends Fragment {

    private FloatingActionButton mCreateTripFAB;

    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    private FirebaseRecyclerAdapter<Trip, TripHolder> mAdapter;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize firebase auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_show_trip, container, false);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.show_trip_list);
        mRecyclerView.setHasFixedSize(true);

        mCreateTripFAB = (FloatingActionButton) rootView.findViewById(R.id.fab_create_trip);
        mCreateTripFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                RelativeLayout content = (RelativeLayout) getActivity().findViewById(R.id.main);
//                CreateTripActivity fragment = CreateTripActivity.newInstance();
//                getActivity().getSupportFragmentManager()
//                        .beginTransaction()
//                        .replace(R.id.fragment_container, fragment) //content.getID()
//                        .addToBackStack(null)
//                        .commit();
                startActivity(new Intent(getActivity(), CreateTripActivity.class));
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Set up Layout Manager, reverse layout
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setReverseLayout(true);
        mLinearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        // Set up FirebaseRecycleAdapter with the Query
        mAdapter = new FirebaseRecyclerAdapter<Trip, TripHolder>(
                Trip.class,
                R.layout.holder_show_trip,
                TripHolder.class,
                mDatabaseReference.child("trip")) {
            @Override
            protected void populateViewHolder(TripHolder viewHolder, Trip model, int position) {
                viewHolder.mTopic.setText(model.getTopic());
                viewHolder.mLocation.setText(model.getLocation());
                if (mFirebaseUser.getPhotoUrl() == null) {
                    viewHolder.mPhofileCircleImageView
                            .setImageDrawable(ContextCompat
                                    .getDrawable(getActivity(), R.drawable.ic_account_circle_black_36dp));
                }else{
                    Glide.with(getActivity())
                            .load(mFirebaseUser.getPhotoUrl())
                            .into(viewHolder.mPhofileCircleImageView);
                }
            }
        };
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
