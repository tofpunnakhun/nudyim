package com.mrdo.nudyim;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mrdo.nudyim.fragment.CreateTripFragment;
import com.mrdo.nudyim.fragment.ShowFriendFragment;
import com.mrdo.nudyim.fragment.ShowTripFragment;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener, NavigationView.OnNavigationItemSelectedListener{
    private static final String TAG = "MainActivity";
    private static final String ANONYMOUS = "anonymous";
    //private TextView mSignOut;

    // Info on nav drawer
    public CircleImageView mProfileImageView;
    public TextView mNameTextView;
    public TextView mEmailTextView;

    // Information google
    private String mUsername;
    private String mEmail;
    private String mPhotoUrl;

    private FloatingActionButton mCreatedTripButton;

    // manage tab pager
    private Toolbar toolbar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        /* Navigation bar*/
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        /* End Navigation bar*/

        /* Create the adapter that will return a fragment for each section*/
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        RelativeLayout content = (RelativeLayout) findViewById(R.id.main);
        mViewPager = (ViewPager) content.findViewById(R.id.container_pager);
        setupViewPager(mViewPager);
        mTabLayout =(TabLayout)content.findViewById(R.id.tab_layout);
        mTabLayout.setupWithViewPager(mViewPager);
        /* End new pager tab*/

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        // Check user login with google
        if (mFirebaseUser != null) {
            Log.d(TAG, "User has exist");
            mUsername = mFirebaseUser.getDisplayName();
            mEmail = mFirebaseUser.getEmail();
            // Check photo
            if (mFirebaseUser.getPhotoUrl() != null) {
                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
            }
        } else {
            Log.d(TAG, "User isn't exist");
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        }

//        CoordinatorLayout appBar = (CoordinatorLayout) findViewById(R.id.app_bar);
//        mCreatedTripButton = (FloatingActionButton)appBar.findViewById(R.id.fab_created);
//        mCreatedTripButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Snackbar.make(v, "Create Trip!!", Snackbar.LENGTH_SHORT)
//                        .setAction("Action", null).show();
//            }
//        });

        // Initialize variable for google information
        View headerLayout = navigationView.getHeaderView(0); //0-index header
//       View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);

        mProfileImageView = (CircleImageView) headerLayout.findViewById(R.id.profileImageView);
        mNameTextView = (TextView) headerLayout.findViewById(R.id.nameTextView);
        mEmailTextView = (TextView) headerLayout.findViewById(R.id.emailTextView);

        updateInfoToNav();
    }

    private void setupViewPager(ViewPager mViewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ShowTripFragment(), getString(R.string.title_event));
        adapter.addFragment(new ShowFriendFragment(), getString(R.string.title_friend));
        mViewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter{
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

        public void addFragment(Fragment fragment, String title){
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
            Glide.with(MainActivity.this)
                    .load(mFirebaseUser.getPhotoUrl())
                    .into(mProfileImageView);
        } else {
            mProfileImageView
                    .setImageDrawable(ContextCompat
                            .getDrawable(MainActivity.this, R.drawable.ic_account_circle_black_36dp));
        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer((GravityCompat.START));
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_logout) {
            mFirebaseAuth.signOut();
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
            mUsername = ANONYMOUS;
            Toast.makeText(MainActivity.this, "Sign out", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, SignInActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
