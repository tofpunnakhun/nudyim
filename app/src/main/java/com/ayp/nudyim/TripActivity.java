package com.ayp.nudyim;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.ayp.nudyim.trip.TripFragment;
import com.google.android.gms.common.ConnectionResult;

/**
 * Created by Punnakhun on 10/18/2016.
 */

public class TripActivity extends SingleFragmentActivity{

    @Override
    protected Fragment onCreateFragment() {
        Intent intent = getIntent();
        String Key = intent.getStringExtra("KEY_CHILD");
        return TripFragment.newInstance(Key);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
