package com.ayp.nudyim.accept;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.ayp.nudyim.SingleFragmentActivity;
import com.google.android.gms.common.ConnectionResult;

/**
 * Created by Chaiwat on 10/21/2016.
 */

public class AcceptFriendActivity extends SingleFragmentActivity {
    @Override
    protected Fragment onCreateFragment() {
        return AcceptFriendFragment.newInstance();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
