package com.ayp.nudyim;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.MenuItem;

import com.ayp.nudyim.trip.ShowTripFragment;
import com.google.android.gms.common.ConnectionResult;

/**
 * Created by onepi on 10/13/2016.
 */

public class MainActivitySingle extends SingleFragmentActivity {

    @Override
    protected Fragment onCreateFragment() {
        return ShowTripFragment.newInstance();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return super.onNavigationItemSelected(item);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
