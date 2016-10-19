package com.ayp.nudyim.trip;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ayp.nudyim.TripActivity;
import com.ayp.nudyim.model.User;
import com.ayp.nudyim.model.UserTrip;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ayp.nudyim.trip.CreateTripActivity;
import com.ayp.nudyim.R;
import com.ayp.nudyim.trip.TripHolder;
import com.ayp.nudyim.model.Trip;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by onepi on 10/5/2016.
 */
public class ShowTripFragment extends Fragment {

    private FloatingActionButton mCreateTripFAB;

    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private String mUserKey;
    private String mEmail;
    private String  mUUID;

    private FirebaseRecyclerAdapter<UserTrip, TripHolder> mAdapter;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;

    private DatabaseReference mFirebaseDatabaseReference;

    String mTopic;
    String mLocation;
    String mPicture;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize firebase auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mEmail = mFirebaseUser.getEmail();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_show_trip, container, false);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.show_trip_list);
        mRecyclerView.setHasFixedSize(true);

        mUUID = mFirebaseUser.getUid();
        Log.d("Test", "Key user = " + mUUID);

//        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
//        mFirebaseDatabaseReference.child("user").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                    User userLab = postSnapshot.getValue(User.class);
//                    if (userLab.getEmail().equals(mEmail))
//                    {
//                        mUserKey = postSnapshot.getKey();
//                        Log.d("Test", "Key user = " + mUserKey);
//                    }
//                }
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//            }
//        });

//         Set up Layout Manager, reverse layout
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setReverseLayout(true);
        mLinearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        //Set up FirebaseRecycleAdapter with the Query
        mAdapter = new FirebaseRecyclerAdapter<UserTrip, TripHolder>(
                UserTrip.class,
                R.layout.holder_show_trip,
                TripHolder.class,
                    mDatabaseReference.child("user").child(mUUID).child("trip")) {
            @Override
            protected void populateViewHolder(final TripHolder viewHolder, UserTrip model, int position) {
                final DatabaseReference ref = getRef(position);

                mDatabaseReference.child("trip").child(model.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Trip trip = dataSnapshot.getValue(Trip.class);
                        mTopic = trip.getTopic();
                        mLocation = trip.getLocation();
                        mPicture = trip.getPhotoUrl();
                        viewHolder.mTopic.setText(mTopic);
                        viewHolder.mLocation.setText(mLocation);
                        viewHolder.mTopic.setText(mTopic);
                        viewHolder.mLocation.setText(mLocation);
                        if (mPicture == null) {
                            viewHolder.mPhofileCircleImageView
                                    .setImageDrawable(ContextCompat
                                            .getDrawable(getActivity(), R.drawable.ic_account_circle_black_36dp));
                        }else{
                            Glide.with(getActivity())
                                    .load(mPicture)
                                    .into(viewHolder.mPhofileCircleImageView);
                        }

                        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getActivity(), TripActivity.class);
                                intent.putExtra("KEY_CHILD", ref.getKey());
                                getActivity().startActivity(intent);
//                        Toast.makeText(getActivity(), ref.getKey(), Toast.LENGTH_SHORT).show();
                            }
                        });

                        Log.d("Test", "Detail = :" +  trip.getDetails());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };
        mRecyclerView.setAdapter(mAdapter);


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
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
