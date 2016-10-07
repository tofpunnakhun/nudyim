package com.mrdo.nudyim.fragment;

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
import android.widget.TextView;
import android.widget.Toast;

import com.mrdo.nudyim.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by onepi on 10/6/2016.
 */

public class CreateTripFragment extends Fragment {

    private static final int REQUEST_DATE = 9000;
    private static final String DIALOG_DATE = "DIALOG_DATE";
    private TextView mStartDate;
    private TextView mEndDate;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_create_trip, container, false);

        mStartDate = (TextView) rootView.findViewById(R.id.start_date_view);
        mStartDate.setText(toShortDate(new Date()));
        mStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                DatePickerFragment datePickerFragment =
                        DatePickerFragment.newInstance(new Date());
                datePickerFragment.setTargetFragment(CreateTripFragment.this, REQUEST_DATE);
                datePickerFragment.show(fm, DIALOG_DATE);
            }
        });

        mEndDate = (TextView) rootView.findViewById(R.id.end_date_view);
        mEndDate.setText(toShortDate(new Date()));
        mEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                DatePickerFragment datePickerFragment =
                        DatePickerFragment.newInstance(new Date());
                datePickerFragment.setTargetFragment(CreateTripFragment.this, REQUEST_DATE);
                datePickerFragment.show(fm, DIALOG_DATE);
            }
        });

        return rootView;
    }

    public static String toShortDate(Date date) {
        return new SimpleDateFormat("MMM d, yyyy").format(date);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_created:
                Toast.makeText(getActivity(), "Created", Toast.LENGTH_SHORT).show();
            default: return super.onOptionsItemSelected(item);
        }
    }
}
