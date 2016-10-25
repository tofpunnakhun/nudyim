package com.ayp.nudyim.friend;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.ayp.nudyim.trip.CreateTripActivity;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ayp.nudyim.R;
import com.ayp.nudyim.model.User;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by onepi on 10/5/2016.
 */
public class ShowFriendFragment extends Fragment {

    public static final String TAG = "ShowFriendFragment";
    private static final String AUTH_KEY = "key=AIzaSyBZTgjZdeldRh5RoAjnx8o2pa2Qk23fU7U";

    private DatabaseReference mDatabaseReference;
    private ValueEventListener mFriendListener;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    private FirebaseRecyclerAdapter<User, FriendViewHolder> mAdapter;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;

    private FloatingActionButton mAddFriendFAB;
    private EditText mAddEditText;

    private boolean flag = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Firebase instance variables
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        //[START create database reference]
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
    }

    private String getUid() {
        return mFirebaseUser.getUid();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_show_friends, container, false);

        mAddFriendFAB = (FloatingActionButton) rootView.findViewById(R.id.fab_add_friend);
        mAddFriendFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setTitle("Add Friends");
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_edittext, null);
                mAddEditText = (EditText) view.findViewById(R.id.add_editText);
                alertDialog.setView(view);
                alertDialog.setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                checkAddFriend();
                            }
                        });

                alertDialog.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialog.show();
            }
        });

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.show_friend_list);
        mRecyclerView.setHasFixedSize(true);

        return rootView;
    }

    private void sendWithOtherThread(final String token, final String keyFriend) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                pushNotification(token, keyFriend);
            }
        }).start();
    }

    private void pushNotification(String token, String keyFriend) {

        /**
         * The registration token may change when:

         The app deletes Instance ID
         The app is restored on a new device
         The user uninstalls/reinstall the app
         The user clears app data.

         */
        Log.d(TAG, "Token: " + FirebaseInstanceId.getInstance().getToken());

        JSONObject jPayload = new JSONObject();
        JSONObject jNotification = new JSONObject();
        JSONObject jData = new JSONObject();

        try {
            jNotification.put("title", "Friend Request");
            jNotification.put("body", "Accept your request :D");
            jNotification.put("sound", "default");
            jNotification.put("badge", "1");
            jNotification.put("content_available", "1");
            jNotification.put("click_action", "OPEN_ACCEPT_FRIEND");

            jData.put("picture_url", "https://fbi.dek-d.com/27/0378/7611/118707044");
            jData.put("key_friend", keyFriend);
            jData.put("key_myself", getUid());

            jPayload.put("to", token);
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

    private void checkAddFriend() {
        final View rootView = getActivity().findViewById(R.id.fragment_container);
        final String mEmail = mAddEditText.getText().toString();

//        ValueEventListener friendListener = new ValueEventListener() {
        mDatabaseReference.child("user").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (final DataSnapshot friendSnapshot : dataSnapshot.getChildren()) {
                    User user = friendSnapshot.getValue(User.class);

                    if (user.getEmail().equals(mEmail)) {

                        mDatabaseReference.child("user").child(getUid()).child("friend").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                for (DataSnapshot checkSnapshot : dataSnapshot.getChildren()) {
                                    if (friendSnapshot.getKey().equals(checkSnapshot.getKey())) {
                                        Snackbar.make(rootView, "This email account already been exist", Snackbar.LENGTH_LONG).show();
                                        flag = false;
                                    }
                                }

                                if (flag) {
                                    mDatabaseReference.child("user").child(friendSnapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            User userToken = dataSnapshot.getValue(User.class);
                                            sendWithOtherThread(userToken.getToken(), friendSnapshot.getKey());
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    } else {
                        Snackbar.make(rootView, "The email account does not exist.", Snackbar.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });
//        mDatabaseReference.child("user").addValueEventListener(friendListener);
//        mFriendListener = friendListener;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        // Remove post value event listener
//        if (mFriendListener != null) {
//            mDatabaseReference.removeEventListener(mFriendListener);
//        }
        // Clean up comments listener
//        mAdapter.cleanupListener();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setReverseLayout(true);
        mLinearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        mAdapter = new FirebaseRecyclerAdapter<User, FriendViewHolder>(
                User.class,
                R.layout.holder_show_friend,
                FriendViewHolder.class,
                mDatabaseReference.child("user").child(getUid()).child("friend")) {
            @Override
            protected void populateViewHolder(FriendViewHolder viewHolder, User model, int position) {
                viewHolder.mName.setText(model.getName());
                if (model.getPhotoUrl() == null) {
                    viewHolder.mPhotoProfileCircleView
                            .setImageDrawable(ContextCompat
                                    .getDrawable(getActivity(), R.drawable.ic_account_circle_black_36dp));
                } else {
                    Glide.with(getActivity())
                            .load(model.getPhotoUrl())
                            .into(viewHolder.mPhotoProfileCircleView);
                }

            }
        };
        mRecyclerView.setAdapter(mAdapter);
    }

    private static class FriendViewHolder extends RecyclerView.ViewHolder {

        public TextView mName;
        public CircleImageView mPhotoProfileCircleView;

        public FriendViewHolder(View itemView) {
            super(itemView);

            mName = (TextView) itemView.findViewById(R.id.name_profile);
            mPhotoProfileCircleView = (CircleImageView) itemView.findViewById(R.id.friend_profile);
        }
    }

   /* private static class FriendAdapter extends RecyclerView.Adapter<FriendViewHolder> {
        private Context mContext;
        private DatabaseReference mReference;
        private ChildEventListener mChildEventListener;

        private List<String> mKeyIds = new ArrayList<>();
        private List<User> mUsers = new ArrayList<>();
        private List<User> mFriendInfo = new ArrayList<>();

        public FriendAdapter(Context context, DatabaseReference ref, List<User> friendInfo) {

            mFriendInfo = friendInfo;
            mContext = context;
            mReference = ref;

            Log.d(TAG, "FriendAdapter: " + mFriendInfo);
            //Start child event listener recycler
            ChildEventListener childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    // A new friend has been added
                    mKeyIds.add(dataSnapshot.getKey());
                    getFriendFromKey(dataSnapshot.getKey());
                    notifyItemInserted(mFriendInfo.size() - 1);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "postComments:onCancelled", databaseError.toException());
                    Toast.makeText(mContext, "Failed to load comments.",
                            Toast.LENGTH_SHORT).show();
                }
            };
            ref.addChildEventListener(childEventListener);

            mChildEventListener = childEventListener;

        }

        private void getFriendFromKey(String key) {
            DatabaseReference dbr = FirebaseDatabase.getInstance().getReference()
                    .child("user")
                    .child(key);
            dbr.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    // Add user to mUser list
                    mUsers.add(user);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "getFriendFromKey:onCancelled", databaseError.toException());
                    Toast.makeText(mContext, "Failed to load user.",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }


        @Override
        public FriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Log.d(TAG, "size of user:2 " + mFriendInfo.size());
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.holder_show_friend, parent, false);
            return new FriendViewHolder(view);
        }

        @Override
        public void onBindViewHolder(FriendViewHolder holder, int position) {
            Log.d(TAG, "size of user3: " + mFriendInfo.size());
            User user = mFriendInfo.get(position);
            holder.mName.setText(user.getName());
            if (user.getPhotoUrl() == null) {
                holder.mPhotoProfileCircleView
                        .setImageDrawable(ContextCompat
                                .getDrawable(mContext, R.drawable.ic_account_circle_black_36dp));
            } else {
                Glide.with(mContext)
                        .load(user.getPhotoUrl())
                        .into(holder.mPhotoProfileCircleView);
            }
        }

        @Override
        public int getItemCount() {
            Log.d(TAG, "size of user4: " + mFriendInfo.size());
            return mFriendInfo.size();
        }

        public void cleanupListener() {
            if (mChildEventListener != null) {
                mReference.removeEventListener(mChildEventListener);
            }
        }
    }*/
}