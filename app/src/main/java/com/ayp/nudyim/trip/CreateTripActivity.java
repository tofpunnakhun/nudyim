package com.ayp.nudyim.trip;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

/**
 * Created by onepi on 10/6/2016.
 */

public class CreateTripActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_START_DATE = 9000;
    private static final int REQUEST_END_DATE = 9009;

    private static final String AUTH_KEY = "key=AIzaSyBZTgjZdeldRh5RoAjnx8o2pa2Qk23fU7U";

    private static final String DIALOG_DATE = "DIALOG_DATE";
    private static final int REQUEST_CODE = 1189;
    private static final String TAG = "CreateTripActivity";

    private List<String> mFriendLists = new ArrayList<>();

    public static final String EMPTY_STRING = "";

    private String mStartDateStr;
    private String mEndDateStr;

    Date sDate = new Date();
    Date cDate = new Date();
    Date eDate = new Date();

    Date sDateKeep = new Date();
    Date eDateKeep = new Date();

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabaseReference;
    private String mEmail;
    private String mUserKey;

    //declare object in layout xml
    private EditText mTopicEditText;
    private RelativeLayout mDetailLayout;
    private RelativeLayout mInviteLayout;
    private RelativeLayout mLocationLayout;
    private RelativeLayout mDateLayout;

    private String mDetail;
    private String mLocation;

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

        mDetail = "";
        mLocation = "";

//        Toolbar tb = (Toolbar)findViewById(R.id.toolBar);
//        setSupportActionBar(tb);
        //getActionBar().setDisplayShowTitleEnabled(false);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        mTopicEditText = (EditText) findViewById(R.id.topic_trip);
        mDetailLayout = (RelativeLayout) findViewById(R.id.detail_layout);
        mDateLayout = (RelativeLayout) findViewById(R.id.date_layout);
        mLocationLayout = (RelativeLayout) findViewById(R.id.location_layout);
        mInviteLayout = (RelativeLayout) findViewById(R.id.invite_layout);

        //Set On Click Listener
        mDetailLayout.setOnClickListener(this);
        mDateLayout.setOnClickListener(this);
        mLocationLayout.setOnClickListener(this);
        mInviteLayout.setOnClickListener(this);

        mDatabaseReference.child("user").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    User userLab = postSnapshot.getValue(User.class);
                    if (userLab.getEmail().equals(mEmail)) {
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
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                mFriendLists = data.getStringArrayListExtra(InviteFriendActivity.INVITE_VALUE);
                Log.d(TAG, "onActivityResult List: " + mFriendLists);
            }
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
                if (mStartDateStr == null || mEndDateStr == null){
                    Toast.makeText(this, "Please fill up your date", Toast.LENGTH_SHORT).show();
                    return false;
                }

                if (!sDate.after(cDate)) {
                    Toast.makeText(this, "Please make sure your start date is more than current date", Toast.LENGTH_SHORT).show();
                    return false;
                }
                if (!eDate.after(sDate)) {
                    Toast.makeText(this, "Please make sure your end date is more that start date", Toast.LENGTH_SHORT).show();
                    return false;
                }

                //TODO
                Trip trip = bindTrip();
                String key = mDatabaseReference.child("trip").push().getKey();
                mDatabaseReference.child("trip").child(key).setValue(trip);
                inviteFriendToDb(key);

                // notification
                sendWithOtherThread("tokens");

                finish();
//                getFragmentManager().popBackStack();
            default:
                return super.onOptionsItemSelected(item);        }
    }

    private void inviteFriendToDb(String key) {
        for (int i = 0; i < mFriendLists.size(); i++) {
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

        mDatabaseReference.child("trip")
                .child(key)
                .child("member")
                .child(mFirebaseUser.getUid())
                .setValue(true);

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
            jNotification.put("content_available", "1");
            jNotification.put("click_action", "OPEN_DETAIL_ACTIVITY");

            jData.put("picture_url", "https://fbi.dek-d.com/27/0378/7611/118707044");

            switch (typeOfToken) {
                case "tokens":
                    JSONArray ja = new JSONArray();
                    ja.put("fltNMjkbOFE:APA91bGWbIq0iIyNfih4BbFtz8cr3joFF1RCM7mceWULIOX6_aZgxCMKA0x1YOJzS-R7h-vFXsvND3h0VPQkRnn65v5cwd813oUrfNkyetMaWmwXn5LXmjW6rDG0qyZtyhG36WriMob3");
                    ja.put("c0J2ls-3pms:APA91bEs3nqKHiiC4nGLy4e0UUo3eJ7nBCGdgVohZdg23mPUQHUaUoDdWYr0pplfRxryNo5bAUcb-0w3Rcpn5TXkqLzuHgPE8Gk1vM00Ds7K-3kw_22jeCp_LSGCz9XlVHFJE0Z6AM6R");
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
                    Log.d(TAG, "" + resp);
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
        String location = mLocation;
        String details = mDetail;
        String photoUrl = mFirebaseUser.getPhotoUrl().toString();

        Trip trip = new Trip(topic, startDate, endDate, location, details, photoUrl);
        return trip;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.detail_layout :
                final Dialog alertDialogDetail = new Dialog(this);
                alertDialogDetail.requestWindowFeature(Window.FEATURE_NO_TITLE);
                alertDialogDetail.setContentView(R.layout.trip_input_detail);

                // set the custom dialog components - text, image and button
                final EditText detailTrip = (EditText) alertDialogDetail.findViewById(R.id.detail_input);
                final TextView okText = (TextView) alertDialogDetail.findViewById(R.id.ok_text);
                final TextView cancelText = (TextView) alertDialogDetail.findViewById(R.id.cancel_text);

                if (!mDetail.equals(""))
                {
                    detailTrip.setText(mDetail);
                }

                okText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDetail = detailTrip.getText().toString();
                        alertDialogDetail.cancel();
                    }
                });
                cancelText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialogDetail.cancel();
                    }
                });
                alertDialogDetail.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDialogDetail.show();
                break;
            case R.id.location_layout :
                final Dialog alertDialogLocation = new Dialog(this);
                alertDialogLocation.requestWindowFeature(Window.FEATURE_NO_TITLE);
                alertDialogLocation.setContentView(R.layout.trip_input_location);

                // set the custom dialog components - text, image and button
                final EditText locationTrip = (EditText) alertDialogLocation.findViewById(R.id.location_input);
                final TextView okTextLocation = (TextView) alertDialogLocation.findViewById(R.id.ok_text);
                final TextView cancelTextLocation = (TextView) alertDialogLocation.findViewById(R.id.cancel_text);

                if (!mLocation.equals(""))
                {
                    locationTrip.setText(mDetail);
                }

                okTextLocation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mLocation = locationTrip.getText().toString();
                        alertDialogLocation.cancel();
                    }
                });
                cancelTextLocation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialogLocation.cancel();
                    }
                });
                alertDialogLocation.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDialogLocation.show();

                break;
            case R.id.date_layout :
                final Dialog alertDialogDate = new Dialog(this);
                alertDialogDate.requestWindowFeature(Window.FEATURE_NO_TITLE);
                alertDialogDate.setContentView(R.layout.trip_input_date);

                // set the custom dialog components - text, image and button
                final TextView startDateTextView = (TextView) alertDialogDate.findViewById(R.id.start_date_view);
                final TextView endDateTextView = (TextView) alertDialogDate.findViewById(R.id.end_date_view);
                final TextView okTextDate = (TextView) alertDialogDate.findViewById(R.id.ok_text);
                final TextView cancelTextDate= (TextView) alertDialogDate.findViewById(R.id.cancel_text);

                if(mStartDateStr == null)
                {
                    startDateTextView.setText(toShortDate(new Date()));
                }
                else
                {
                    startDateTextView.setText(mStartDateStr);
                }

                final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        Calendar myCalendar = Calendar.getInstance();
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        Date date = myCalendar.getTime();
                        sDateKeep = date;
                        startDateTextView.setText(toDbDate(date));
                    }
                };
                startDateTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar myCalendar = Calendar.getInstance();
                        new DatePickerDialog(CreateTripActivity.this, date, myCalendar
                                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                    }
                });

                if(mEndDateStr == null)
                {
                    endDateTextView.setText(toShortDate(new Date()));
                }
                else
                {
                    endDateTextView.setText(mEndDateStr);
                }

                final DatePickerDialog.OnDateSetListener dateEnd = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        Calendar myCalendarEnd = Calendar.getInstance();
                        myCalendarEnd.set(Calendar.YEAR, year);
                        myCalendarEnd.set(Calendar.MONTH, monthOfYear);
                        myCalendarEnd.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        Date date = myCalendarEnd.getTime();
                        eDateKeep = date;
                        endDateTextView.setText(toDbDate(date));
                    }
                };
                endDateTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar myCalendarEnd = Calendar.getInstance();
                        new DatePickerDialog(CreateTripActivity.this, dateEnd, myCalendarEnd
                                .get(Calendar.YEAR), myCalendarEnd.get(Calendar.MONTH),
                                myCalendarEnd.get(Calendar.DAY_OF_MONTH)).show();
                    }
                });

                okTextDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sDate = sDateKeep;
                        mStartDateStr = toDbDate(sDate);
                        eDate = eDateKeep;
                        mEndDateStr = toDbDate(eDate);
                        alertDialogDate.cancel();
                    }
                });
                cancelTextDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialogDate.cancel();
                    }
                });
                alertDialogDate.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDialogDate.show();
                break;
            case R.id.invite_layout :
                Intent i = new Intent(CreateTripActivity.this, InviteFriendActivity.class);
                startActivityForResult(i, REQUEST_CODE);
                break;
        }
    }
}
