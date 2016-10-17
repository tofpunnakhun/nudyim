package com.ayp.nudyim;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ayp.nudyim.friend.ShowAllFriendFragment;
import com.ayp.nudyim.friend.ShowFriendFragment;
import com.ayp.nudyim.trip.ShowTripFragment;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainFragment extends Fragment
        implements GoogleApiClient.OnConnectionFailedListener,
        NavigationView.OnNavigationItemSelectedListener {

    public static final String TAG = "MainFragment";
    private static final String ANONYMOUS = "anonymous";

    // Info on nav drawer
    public CircleImageView mProfileImageView;
    public TextView mNameTextView;
    public TextView mEmailTextView;

    // Information google
    private String mUsername;
    private String mEmail;
    private String mPhotoUrl;

    // manage tab pager
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    public static MainFragment newInstance() {
        Bundle args = new Bundle();
        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity() /* SingleFragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        // Check user login with google
        if (mFirebaseUser != null) {
            Log.d(TAG, "onCreate: ");
            mUsername = mFirebaseUser.getDisplayName();
            mEmail = mFirebaseUser.getEmail();
            // Check photo
            if (mFirebaseUser.getPhotoUrl() != null) {
                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
            }
        } else {
            Log.d(TAG, "onCreate startActivity: ");
            startActivity(new Intent(getActivity(), SignInActivity.class));
            getActivity().finish();
            return;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.activity_main, container, false);
        getActivity().getWindow().setStatusBarColor(Color.TRANSPARENT);

        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) rootView.findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                getActivity(), drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) rootView.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        RelativeLayout content = (RelativeLayout) rootView.findViewById(R.id.main);
        mViewPager = (ViewPager) content.findViewById(R.id.container_pager);
        setupViewPager(mViewPager);
        mTabLayout = (TabLayout) content.findViewById(R.id.tab_layout);
        mTabLayout.setupWithViewPager(mViewPager);


        // Initialize variable for google information
        View headerLayout = navigationView.getHeaderView(0); //0-index header
//       View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);

        mProfileImageView = (CircleImageView) headerLayout.findViewById(R.id.profileImageView);
        mNameTextView = (TextView) headerLayout.findViewById(R.id.nameTextView);
        mEmailTextView = (TextView) headerLayout.findViewById(R.id.emailTextView);

        updateInfoToNav();

        return rootView;
    }

    private void setupViewPager(ViewPager mViewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
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

    private void updateInfoToNav() {
        mNameTextView.setText(mUsername);
        mEmailTextView.setText(mEmail);
        if (mFirebaseUser.getPhotoUrl() != null) {
            Glide.with(MainFragment.this)
                    .load(mFirebaseUser.getPhotoUrl())
                    .into(mProfileImageView);
        } else {
            mProfileImageView
                    .setImageDrawable(ContextCompat
                            .getDrawable(getActivity(), R.drawable.ic_account_circle_black_36dp));
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_logout) {
            mFirebaseAuth.signOut();
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
            mUsername = ANONYMOUS;
            Toast.makeText(getActivity(), "Sign out", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getActivity(), SignInActivity.class));
        }

      //  DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
