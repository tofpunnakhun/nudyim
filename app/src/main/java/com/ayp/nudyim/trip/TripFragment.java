package com.ayp.nudyim.trip;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ayp.nudyim.R;
import com.ayp.nudyim.SignInActivity;
import com.ayp.nudyim.budget.BudgetFragment;
import com.ayp.nudyim.chat.ChatFragment;
import com.ayp.nudyim.detail.DetailFragment;
import com.ayp.nudyim.model.Trip;
import com.ayp.nudyim.photo.PhotoGallery;
import com.ayp.nudyim.schedule.ScheduleTabBar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Punnakhun on 10/18/2016.
 */

public class TripFragment extends Fragment implements View.OnClickListener{

    private static final String TRIP_CHILD = "trip";
    private static final String TAG = "TripFragment";
    private static String KEY_CHILD;
    private SharedPreferences mSharedPreferences;

    //declare object in layout xml
    private RelativeLayout mTripPageLayout;
    private RelativeLayout mDetailLayout;
    private RelativeLayout mScheduleLayout;
    private RelativeLayout mBudgetLayout;
    private RelativeLayout mChatLayout;
    private RelativeLayout mPhotoLayout;
    private ProgressBar mProgressBar;

    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mFirebaseDatabaseReference;

    private Trip tripLab;
    private String mUsername;

    public static TripFragment newInstance(String key) {
        Bundle args = new Bundle();
        KEY_CHILD = key;
        TripFragment fragment = new TripFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_trip_fragment,container,false);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mTripPageLayout = (RelativeLayout) view.findViewById(R.id.layout_trip_page);
        mDetailLayout = (RelativeLayout) view.findViewById(R.id.detail_layout);
        mScheduleLayout = (RelativeLayout) view.findViewById(R.id.schedule_layout);
        mBudgetLayout = (RelativeLayout) view.findViewById(R.id.budget_layout);
        mChatLayout = (RelativeLayout) view.findViewById(R.id.chat_layout);
        mPhotoLayout = (RelativeLayout) view.findViewById(R.id.picture_layout);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar) ;

        //Set On Click Listener
        mDetailLayout.setOnClickListener(this);
        mScheduleLayout.setOnClickListener(this);
        mBudgetLayout.setOnClickListener(this);
        mChatLayout.setOnClickListener(this);
        mPhotoLayout.setOnClickListener(this);

        // Set default username is anonymous.
        mUsername = "ANONYMOUS";
        connectDatabase();
        return view;
    }

    public void connectDatabase(){
        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(getActivity(), SignInActivity.class));
            getActivity().finish();
        } else {
            mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
            mFirebaseDatabaseReference.child(TRIP_CHILD).child(KEY_CHILD).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
//                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    tripLab = dataSnapshot.getValue(Trip.class);
                    mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                    mTripPageLayout.setVisibility(RelativeLayout.VISIBLE);
//                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.detail_layout :
                DetailFragment detailFragment = new DetailFragment();
                Bundle bundleDetail = new Bundle();
                bundleDetail.putString("KEY_CHILD", KEY_CHILD);
                detailFragment.setArguments(bundleDetail);
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, detailFragment)
                        .addToBackStack(null)
                        .commit();
                break;
            case R.id.schedule_layout :
                ScheduleTabBar scheduleTabBar = new ScheduleTabBar();
                Bundle bundleSchedule = new Bundle();
                bundleSchedule.putString("KEY_CHILD", KEY_CHILD);
                bundleSchedule.putString("START_DATE", tripLab.getStartdate());
                bundleSchedule.putString("END_DATE", tripLab.getEnddate());
                scheduleTabBar.setArguments(bundleSchedule);
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, scheduleTabBar).addToBackStack(null).commit();
                break;
            case R.id.budget_layout :
                BudgetFragment budgetFragment = new BudgetFragment();
                Bundle bundleBudget = new Bundle();
                bundleBudget.putString("KEY_CHILD", KEY_CHILD);
                budgetFragment.setArguments(bundleBudget);
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, budgetFragment)
                        .addToBackStack(null)
                        .commit();
                break;
            case R.id.chat_layout :
                ChatFragment chatFragment = new ChatFragment();
                Bundle bundleChat = new Bundle();
                bundleChat.putString("KEY_CHILD", KEY_CHILD);
                chatFragment.setArguments(bundleChat);
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, chatFragment)
                        .addToBackStack(null)
                        .commit();
                break;
            case R.id.picture_layout :
                PhotoGallery photoGallery = new PhotoGallery();
                Bundle bundlePicture = new Bundle();
                bundlePicture.putString("KEY_CHILD", KEY_CHILD);
                photoGallery.setArguments(bundlePicture);
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, photoGallery)
                        .addToBackStack(null)
                        .commit();
                break;
        }
    }
}
