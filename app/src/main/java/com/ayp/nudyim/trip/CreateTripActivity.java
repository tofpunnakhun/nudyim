package com.ayp.nudyim.trip;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.ayp.nudyim.R;
import com.ayp.nudyim.model.User;
import com.ayp.nudyim.model.UserTrip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ayp.nudyim.model.Trip;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

/**
 * Created by onepi on 10/6/2016.
 */

public class CreateTripActivity extends AppCompatActivity implements DatePickerFragment.Callback{

    private static final int REQUEST_START_DATE = 9000;
    private static final int REQUEST_END_DATE = 9009;

    private static final String AUTH_KEY = "key=AIzaSyBZTgjZdeldRh5RoAjnx8o2pa2Qk23fU7U";

    private static final String DIALOG_DATE = "DIALOG_DATE";
    private static final int REQUEST_CODE = 1189;
    private static final String TAG = "CreateTripActivity";

    private EditText mTopicEditText;
    private EditText mDetailEditText;
    private EditText mLocationEditText;
    private TextView mStartDateTextView;
    private TextView mEndDateTextView;
    private TextView mInviteFriendTextView;

    private List<String> mFriendLists = new ArrayList<>();

    public static final String EMPTY_STRING = "";

    private String mStartDateStr;
    private String mEndDateStr;


    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabaseReference;
    private String mEmail;
    private String mUserKey;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_create_trip);

        // Initialize firebase auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mEmail = mFirebaseUser.getEmail();

        getSupportActionBar().setTitle(EMPTY_STRING);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_cancel);

//        Toolbar tb = (Toolbar)findViewById(R.id.toolBar);
//        setSupportActionBar(tb);
        //getActionBar().setDisplayShowTitleEnabled(false);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        mTopicEditText = (EditText) findViewById(R.id.topic_trip);
        mLocationEditText = (EditText) findViewById(R.id.location_trip);
        mDetailEditText = (EditText) findViewById(R.id.detail_trip);

        mInviteFriendTextView = (TextView) findViewById(R.id.invite_trip);
        mInviteFriendTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CreateTripActivity.this, InviteFriendActivity.class);
                startActivityForResult(i, REQUEST_CODE);
            }
        });

        mStartDateTextView = (TextView) findViewById(R.id.start_date_view);
        mStartDateTextView.setText(toShortDate(new Date()));
        mStartDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                DatePickerFragment datePickerFragment =
                        DatePickerFragment.newInstance(new Date(), REQUEST_START_DATE);
//                datePickerFragment.setTargetFragment(CreateTripActivity.this, REQUEST_START_DATE);
                datePickerFragment.show(fm, DIALOG_DATE);
            }
        });

        mEndDateTextView = (TextView) findViewById(R.id.end_date_view);
        mEndDateTextView.setText(toShortDate(new Date()));
        mEndDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                DatePickerFragment datePickerFragment =
                        DatePickerFragment.newInstance(new Date(), REQUEST_END_DATE);
//                datePickerFragment.setTargetFragment(CreateTripActivity.this, REQUEST_END_DATE);
                datePickerFragment.show(fm, DIALOG_DATE);
            }
        });

        mDatabaseReference.child("user").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    User userLab = postSnapshot.getValue(User.class);
                    if (userLab.getEmail().equals(mEmail))
                    {
                        mUserKey = postSnapshot.getKey();
                        Log.d("Test", "Key user = " + mUserKey);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public static String toShortDate(Date date) {
        return new SimpleDateFormat("MMM d, yyyy").format(date);
    }

    public static String toDbDate(Date date) {
        return new SimpleDateFormat("dd/MM/yyyy").format(date);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: ");
        if (requestCode == REQUEST_CODE){
            Log.d(TAG, "onActivityResult:1212 ");
            Log.d(TAG, "resultCode " + resultCode );
            if (resultCode == RESULT_OK){
                mFriendLists = data.getStringArrayListExtra(InviteFriendActivity.INVITE_VALUE);
                Log.d(TAG, "onActivityResult List: "+mFriendLists);
            }
        }
    }

    @Override
    public void sendValue(Date date, int requestCode) {
        if (requestCode == REQUEST_START_DATE){
            mStartDateStr = toDbDate(date);
            mStartDateTextView.setText(mStartDateStr);
        }
        if (requestCode == REQUEST_END_DATE){
            mEndDateStr = toDbDate(date);
            mEndDateTextView.setText(mEndDateStr);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cancel_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_created:
                Trip trip = bindTrip();
                String key = mDatabaseReference.child("trip").push().getKey();
                mDatabaseReference.child("trip").child(key).setValue(trip);
                inviteFriendToDb(key);

                // notification
                sendWithOtherThread("token");

                finish();
//                getFragmentManager().popBackStack();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void inviteFriendToDb(String key) {
        for (int i =0;i < mFriendLists.size();i++){
            mDatabaseReference.child("trip")
                    .child(key)
                    .child("member")
                    .child(mFriendLists.get(i))
                    .setValue(true);

            UserTrip usertrip = new UserTrip(key);
            mDatabaseReference
                    .child("user")
                    .child(mFriendLists.get(i))
                    .child("trip")
                    .child(key)
                    .setValue(usertrip);
        }

        UserTrip userTrip = new UserTrip(key);
        mDatabaseReference
                .child("user")
                .child(mUserKey)
                .child("trip")
                .child(key)
                .setValue(userTrip);
    }

    private void sendWithOtherThread(final String typeOfToken) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                pushNotification(typeOfToken);
            }
        }).start();
    }

    private void pushNotification(String typeOfToken) {
        Log.d(TAG, "Token: " + FirebaseInstanceId.getInstance().getToken());
        JSONObject jPayload = new JSONObject();
        JSONObject jNotification = new JSONObject();
        JSONObject jData = new JSONObject();

        try {
            jNotification.put("title", "Hey It's me, Mr.Do");
            jNotification.put("body", "I was just create trip, Can you see that?");
            jNotification.put("sound", "default");
            jNotification.put("badge", "1");
//            jNotification.put("click_action", "OPEN_ACTIVITY");

            jData.put("picture_url", "https://fbi.dek-d.com/27/0378/7611/118707044");

            switch (typeOfToken){
                case "tokens":
                    JSONArray ja = new JSONArray();
                    ja.put("c5pBXXsuCN0:APA91bH8nLMt084KpzMrmSWRS2SnKZudyNjtFVxLRG7VFEFk_RgOm-Q5EQr_oOcLbVcCjFH6vIXIyWhST1jdhR8WMatujccY5uy1TE0hkppW_TSnSBiUsH_tRReutEgsmIMmq8fexTmL");
                    ja.put(FirebaseInstanceId.getInstance().getToken());
                    jPayload.put("registration_ids", ja);
                    break;
                default:
                    jPayload.put("to", FirebaseInstanceId.getInstance().getToken());
            }

            jPayload.put("priority", "high");
            jPayload.put("notification", jNotification);
            jPayload.put("data", jData);

            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", AUTH_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoInput(true);

            // send FCM message content.
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(jPayload.toString().getBytes());

            InputStream inputStream = conn.getInputStream();
            final String resp = convertStreamToString(inputStream);

            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, ""+resp);
                }
            });


        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    private String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next().replace(",", ",\n") : "";
    }

    private Trip bindTrip() {
        String topic = mTopicEditText.getText().toString();
        String startDate = mStartDateStr;
        String endDate = mEndDateStr;
        String location = mLocationEditText.getText().toString();
        String details = mDetailEditText.getText().toString();
        String photoUrl = mFirebaseUser.getPhotoUrl().toString();

        Trip trip = new Trip(topic, startDate, endDate, location, details, photoUrl);
        return trip;
    }
}
