package com.ayp.nudyim.accept;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.ayp.nudyim.SingleFragmentActivity;
import com.google.android.gms.common.ConnectionResult;

/**
 * Created by Chaiwat on 10/21/2016.
 */

public class AcceptFriendActivity extends SingleFragmentActivity {
    @Override
    protected Fragment onCreateFragment() {

        Bundle bundle = getIntent().getExtras();
        String keyFriend = bundle.getString("key_friend");
        String keyMyself = bundle.getString("key_myself");

        return AcceptFriendFragment.newInstance(keyFriend, keyMyself);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
