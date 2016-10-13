package com.mrdo.nudyim;

import android.support.v4.app.Fragment;

/**
 * Created by onepi on 10/13/2016.
 */

public class MainActivitySingle extends SingleFragmentActivity {
    @Override
    protected Fragment onCreateFragment() {
        return MainFragment.newInstance();
    }
}
