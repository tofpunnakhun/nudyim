package com.mrdo.nudyim;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mrdo.nudyim.fragment.DatePickerFragment;
import com.mrdo.nudyim.model.Trip;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by onepi on 10/6/2016.
 */

public class CreateTripActivity extends AppCompatActivity implements DatePickerFragment.Callback{

    private static final int REQUEST_START_DATE = 9000;
    private static final int REQUEST_END_DATE = 9009;

    private static final String DIALOG_DATE = "DIALOG_DATE";
    private static final int REQUEST_CODE = 1189;
    private static final String TAG = "CreateTripActivity";

    private EditText mTopicEditText;
    private EditText mDetailEditText;
    private EditText mLocationEditText;
    private TextView mStartDateTextView;
    private TextView mEndDateTextView;
    private TextView mInviteFriendTextView;

    private List<String> mFriendLists = new ArrayList<>();

    public static final String EMPTY_STRING = "";

    private String mStartDateStr;
    private String mEndDateStr;


    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabaseReference;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_create_trip);

        // Initialize firebase auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        getSupportActionBar().setTitle(EMPTY_STRING);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_cancel);

//        Toolbar tb = (Toolbar)findViewById(R.id.toolBar);
//        setSupportActionBar(tb);
        //getActionBar().setDisplayShowTitleEnabled(false);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        mTopicEditText = (EditText) findViewById(R.id.topic_trip);
        mLocationEditText = (EditText) findViewById(R.id.location_trip);
        mDetailEditText = (EditText) findViewById(R.id.detail_trip);

        mInviteFriendTextView = (TextView) findViewById(R.id.invite_trip);
        mInviteFriendTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CreateTripActivity.this, InviteFriendActivity.class);
                startActivityForResult(i, REQUEST_CODE);
            }
        });

        mStartDateTextView = (TextView) findViewById(R.id.start_date_view);
        mStartDateTextView.setText(toShortDate(new Date()));
        mStartDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                DatePickerFragment datePickerFragment =
                        DatePickerFragment.newInstance(new Date(), REQUEST_START_DATE);
//                datePickerFragment.setTargetFragment(CreateTripActivity.this, REQUEST_START_DATE);
                datePickerFragment.show(fm, DIALOG_DATE);
            }
        });

        mEndDateTextView = (TextView) findViewById(R.id.end_date_view);
        mEndDateTextView.setText(toShortDate(new Date()));
        mEndDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                DatePickerFragment datePickerFragment =
                        DatePickerFragment.newInstance(new Date(), REQUEST_END_DATE);
//                datePickerFragment.setTargetFragment(CreateTripActivity.this, REQUEST_END_DATE);
                datePickerFragment.show(fm, DIALOG_DATE);
            }
        });
        }

    public static String toShortDate(Date date) {
        return new SimpleDateFormat("MMM d, yyyy").format(date);
    }

    public static String toDbDate(Date date) {
        return new SimpleDateFormat("dd/MM/yyyy").format(date);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE){
            if (resultCode == RESULT_OK){
                mFriendLists = data.getStringArrayListExtra(InviteFriendActivity.INVITE_VALUE);
                Log.d(TAG, "onActivityResult List: "+mFriendLists);
            }
        }
    }

    @Override
    public void sendValue(Date date, int requestCode) {
        if (requestCode == REQUEST_START_DATE){
            mStartDateStr = toDbDate(date);
            mStartDateTextView.setText(mStartDateStr);
        }
        if (requestCode == REQUEST_END_DATE){
            mEndDateStr = toDbDate(date);
            mEndDateTextView.setText(mEndDateStr);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cancel_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_created:
                Trip trip = bindTrip();
                String key = mDatabaseReference.child("trip").push().getKey();
                mDatabaseReference.child("trip").child(key).setValue(trip);
                inviteFriendToDb(key);
                finish();
//                getFragmentManager().popBackStack();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void inviteFriendToDb(String key) {
        for (int i =0;i < mFriendLists.size();i++){
            mDatabaseReference
                    .child("trip")
                    .child(key)
                    .child("friend")
                    .child(mFriendLists.get(i))
                    .setValue(true);
        }
    }

    private Trip bindTrip() {
        String topic = mTopicEditText.getText().toString();
        String startDate = mStartDateStr;
        String endDate = mEndDateStr;
        String location = mLocationEditText.getText().toString();
        String details = mDetailEditText.getText().toString();
        String photoUrl = mFirebaseUser.getPhotoUrl().toString();

        Trip trip = new Trip(topic, startDate, endDate, location, details, photoUrl);
        return trip;
    }
}
