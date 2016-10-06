package com.mrdo.nudyim.fragment;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.mrdo.nudyim.R;

/**
 * Created by onepi on 10/5/2016.
 */
public class ShowTripFragment extends Fragment {

    private FloatingActionButton mCreateTripFAB;

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
         super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_show_trip, container, false);

        mCreateTripFAB = (FloatingActionButton) rootView.findViewById(R.id.fab_create_trip);
        mCreateTripFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                CreateTripFragment fragment = CreateTripFragment.newInstance();
//                getActivity().getSupportFragmentManager()
//                        .beginTransaction()
//                        .replace(R.id.content_main, fragment)
//                        .commit();
                Toast.makeText(getActivity(), "Create Event.", Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }
}
