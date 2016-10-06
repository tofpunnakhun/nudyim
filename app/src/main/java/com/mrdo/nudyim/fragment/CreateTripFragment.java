package com.mrdo.nudyim.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by onepi on 10/6/2016.
 */

public class CreateTripFragment extends Fragment {

    public static CreateTripFragment newInstance() {
        Bundle args = new Bundle();
        CreateTripFragment fragment = new CreateTripFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return null;
    }
}
