package com.ayp.nudyim.accept;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Created by Chaiwat on 10/21/2016.
 */

public class AcceptFriendFragment extends Fragment {

    public static AcceptFriendFragment newInstance() {
        Bundle args = new Bundle();
        AcceptFriendFragment fragment = new AcceptFriendFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
