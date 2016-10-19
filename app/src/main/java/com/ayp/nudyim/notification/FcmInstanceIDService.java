package com.ayp.nudyim.notification;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by onepi on 10/19/2016.
 */

public class FcmInstanceIDService extends FirebaseInstanceIdService {
     private static final String TAG = "FcmInstanceIDService";
    /**
     * Call if InstanceID token is updated.
     */
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String token) {
        // Do it later, when add custom implement, as I need.
    }

}
