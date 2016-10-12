package com.mrdo.nudyim.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mrdo.nudyim.InviteFriendFragment;
import com.mrdo.nudyim.R;
import com.mrdo.nudyim.model.Trip;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by onepi on 10/6/2016.
 */

public class CreateTripFragment extends Fragment {

    private static final int REQUEST_START_DATE = 9000;
    private static final int REQUEST_END_DATE = 9009;
    private static final String DIALOG_DATE = "DIALOG_DATE";

    private EditText mTopicEditText;
    private EditText mDetailEditText;
    private EditText mLocationEditText;
    private TextView mStartDateTextView;
    private TextView mEndDateTextView;
    private TextView mInviteFriendEditView;

    private String mStartDateStr;
    private String mEndDateStr;

    private DatabaseReference mDatabaseReference;

    public static CreateTripFragment newInstance() {
        Bundle args = new Bundle();
        CreateTripFragment fragment = new CreateTripFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_create_trip, container, false);

        mTopicEditText = (EditText) rootView.findViewById(R.id.topic_trip);
        mLocationEditText = (EditText) rootView.findViewById(R.id.location_trip);
        mDetailEditText = (EditText) rootView.findViewById(R.id.detail_trip);

        mInviteFriendEditView = (TextView) rootView.findViewById(R.id.invite_trip);
        mInviteFriendEditView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Fuck you man", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity(), InviteFriendFragment.class));
            }
        });

        mStartDateTextView = (TextView) rootView.findViewById(R.id.start_date_view);
        mStartDateTextView.setText(toShortDate(new Date()));
        mStartDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                DatePickerFragment datePickerFragment =
                        DatePickerFragment.newInstance(new Date());
                datePickerFragment.setTargetFragment(CreateTripFragment.this, REQUEST_START_DATE);
                datePickerFragment.show(fm, DIALOG_DATE);
            }
        });

        mEndDateTextView = (TextView) rootView.findViewById(R.id.end_date_view);
        mEndDateTextView.setText(toShortDate(new Date()));
        mEndDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                DatePickerFragment datePickerFragment =
                        DatePickerFragment.newInstance(new Date());
                datePickerFragment.setTargetFragment(CreateTripFragment.this, REQUEST_END_DATE);
                datePickerFragment.show(fm, DIALOG_DATE);
            }
        });

        return rootView;
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
        if (requestCode == REQUEST_START_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mStartDateStr = toDbDate(date);
            mStartDateTextView.setText(mStartDateStr);
        }
        if (requestCode == REQUEST_END_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mEndDateStr = toDbDate(date);
            mEndDateTextView.setText(mEndDateStr);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_created:
                Trip trip = bindTrip();
                mDatabaseReference.child("trip").push().setValue(trip);
                getFragmentManager().popBackStack();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private Trip bindTrip() {
        String topic = mTopicEditText.getText().toString();
        String startDate = mStartDateStr;
        String endDate = mEndDateStr;
        String location = mLocationEditText.getText().toString();
        String details = mDetailEditText.getText().toString();

        Trip trip = new Trip(topic, startDate, endDate, location, details);
        return trip;
    }
}
