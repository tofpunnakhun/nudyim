package com.ayp.nudyim;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ayp.nudyim.friend.ShowAllFriendFragment;
import com.ayp.nudyim.friend.ShowFriendFragment;
import com.ayp.nudyim.trip.ShowTripFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class TabFragment extends Fragment {

    public static final String TAG = "TabFragment";
    private static final String ANONYMOUS = "anonymous";

    // manage tab pager
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private String mUserKey;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;


    public static TabFragment newInstance() {
        Bundle args = new Bundle();
        TabFragment fragment = new TabFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize firebase auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.activity_main, container, false);

//        mUserKey = getArguments().getString("KEY_USER");

//        RelativeLayout content = (RelativeLayout) rootView.findViewById(R.id.main);
        mViewPager = (ViewPager) rootView.findViewById(R.id.container_pager);
        setupViewPager(mViewPager);
        mTabLayout = (TabLayout) rootView.findViewById(R.id.tab_layout);
        mTabLayout.setupWithViewPager(mViewPager);
        return rootView;
    }

    private void setupViewPager(ViewPager mViewPager) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());

//        ShowTripFragment showTripFragment = new ShowTripFragment();
//        Bundle bundle = new Bundle();
//        bundle.putString("KEY_USER", mUserKey);
//        showTripFragment.setArguments(bundle);
        adapter.addFragment(new ShowTripFragment(), getString(R.string.title_event));
        adapter.addFragment(new ShowFriendFragment(), getString(R.string.title_friend));
        adapter.addFragment(new ShowAllFriendFragment(), getString(R.string.title_all_user));
        mViewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


//    @Override
//    public void onBackPressed() {
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer((GravityCompat.START));
//        } else {
//            super.onBackPressed();
//        }
//    }

//    @SuppressWarnings("StatementWithEmptyBody")
//    @Override
//    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//        if (item.getItemId() == R.id.nav_logout) {
//            mFirebaseAuth.signOut();
//            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
//            mUsername = ANONYMOUS;
//            Toast.makeText(getActivity(), "Sign out", Toast.LENGTH_SHORT).show();
//            startActivity(new Intent(getActivity(), SignInActivity.class));
//        }
//
//      //  DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
////        drawer.closeDrawer(GravityCompat.START);
//        return true;
//    }
}
