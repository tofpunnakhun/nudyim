package com.ayp.nudyim;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ayp.nudyim.friend.ShowAllFriendFragment;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by onepi on 10/12/2016.
 */

public abstract class SingleFragmentActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener,
        NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "SingleFragmentActivity";
    private static final String ANONYMOUS = "anonymous";

    @LayoutRes
    protected int getLayoutResId() {
        return R.layout.activity_main_fragment;
    }

    // Info on nav drawer
    public CircleImageView mProfileImageView;
    public TextView mNameTextView;
    public TextView mEmailTextView;

    // Information google
    private String mUsername;
    private String mEmail;
    private String mPhotoUrl;

    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* SingleFragmentActivity */, this /* OnConnectionFailedListener */)
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
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        }

        getWindow().setStatusBarColor(Color.TRANSPARENT);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Initialize variable for google information
        View headerLayout = navigationView.getHeaderView(0); //0-index header
//       View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);

        mProfileImageView = (CircleImageView) headerLayout.findViewById(R.id.profileImageView);
        mNameTextView = (TextView) headerLayout.findViewById(R.id.nameTextView);
        mEmailTextView = (TextView) headerLayout.findViewById(R.id.emailTextView);

        updateInfoToNav();

        Fragment fragment = getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);

        if (fragment == null) {
            fragment = onCreateFragment();

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }

    private void updateInfoToNav() {
        mNameTextView.setText(mUsername);
        mEmailTextView.setText(mEmail);
        if (mFirebaseUser.getPhotoUrl() != null) {
            Glide.with(SingleFragmentActivity.this)
                    .load(mFirebaseUser.getPhotoUrl())
                    .into(mProfileImageView);
        } else {
            mProfileImageView
                    .setImageDrawable(ContextCompat
                            .getDrawable(this, R.drawable.ic_account_circle_black_36dp));
        }
    }

    protected abstract Fragment onCreateFragment();

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_logout) {
            mFirebaseAuth.signOut();
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
            mUsername = ANONYMOUS;
            Toast.makeText(this, "Sign out", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, SignInActivity.class));
        }
        if (item.getItemId() == R.id.nav_show_all_friend) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new ShowAllFriendFragment())
                    .addToBackStack(null)
                    .commit();

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }
}
