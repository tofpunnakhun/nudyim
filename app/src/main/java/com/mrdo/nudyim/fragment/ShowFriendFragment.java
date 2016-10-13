package com.mrdo.nudyim.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telecom.Call;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mrdo.nudyim.FriendHolder;
import com.mrdo.nudyim.R;
import com.mrdo.nudyim.model.User;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by onepi on 10/5/2016.
 */
public class ShowFriendFragment extends Fragment {
    public static final String TAG = "ShowFriendFragment";

    private DatabaseReference mDatabaseReference;
    private DatabaseReference mFriendReference;
    private ValueEventListener mFriendListener;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    private FriendAdapter mAdapter;
    private RecyclerView mRecycler;

    private FloatingActionButton mAddFriendFAB;
    private EditText mAddFriendEditText;
    private Button mAddButton;

    private static boolean mFlagAdd = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Firebase instance variables
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
    }

    private String getUid() {
        return mFirebaseUser.getUid();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_show_friends, container, false);

        mAddFriendEditText = (EditText) rootView.findViewById(R.id.edit_add_friend);
        mAddFriendEditText.setVisibility(View.INVISIBLE);

//        Drawable originalDrawable = mAddFriendEditText.getBackground();
//        mAddFriendEditText.setBackground(originalDrawable);

        mAddButton = (Button) rootView.findViewById(R.id.button_add_friend);
        mAddButton.setVisibility(View.INVISIBLE);
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAddFriend();
            }
        });

        mAddFriendFAB = (FloatingActionButton) rootView.findViewById(R.id.fab_add_friend);
        mAddFriendFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFlagAdd) {
                    mAddFriendEditText.setVisibility(View.VISIBLE);
                    mAddButton.setVisibility(View.VISIBLE);
                    mFlagAdd = false;
                } else {
                    mAddFriendEditText.setVisibility(View.INVISIBLE);
                    mAddButton.setVisibility(View.INVISIBLE);
                    mFlagAdd = true;
                }
            }
        });

        //[START create database reference]
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFriendReference = FirebaseDatabase.getInstance().getReference()
                .child("user")
                .child(getUid())
                .child("friend");

        mRecycler = (RecyclerView) rootView.findViewById(R.id.show_friend_list);
        mRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecycler.setHasFixedSize(true);

        mAdapter = new FriendAdapter(getActivity(), mFriendReference);
        mRecycler.setAdapter(mAdapter);

        return rootView;
    }

    private void checkAddFriend() {
        final String mEmail = mAddFriendEditText.getText().toString();

        ValueEventListener friendListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot friendSnapshot : dataSnapshot.getChildren()) {
                    User user = friendSnapshot.getValue(User.class);
                    if (user.getEmail().equalsIgnoreCase(mEmail)) {
                        mDatabaseReference.child("user")
                                .child(getUid())
                                .child("friend")
                                .child(friendSnapshot.getKey())
                                .setValue(true);

                        mDatabaseReference.child("user")
                                .child(friendSnapshot.getKey())
                                .child("friend")
                                .child(getUid())
                                .setValue(true);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        mDatabaseReference.child("user").addValueEventListener(friendListener);

        mFriendListener = friendListener;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();

        // Remove post value event listener
        if (mFriendListener != null) {
            mDatabaseReference.removeEventListener(mFriendListener);
        }

        // Clean up comments listener
        mAdapter.cleanupListener();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

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

    private static class FriendAdapter extends RecyclerView.Adapter<FriendViewHolder> {
        private Context mContext;
        private DatabaseReference mReference;
        private ChildEventListener mChildEventListener;

        private List<String> mKeyIds = new ArrayList<>();
        private List<User> mUsers = new ArrayList<>();

        public FriendAdapter(Context context, DatabaseReference ref) {

            mContext = context;
            mReference = ref;
            //Start child event listener recycler
            ChildEventListener childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    // A new friend has been added
                    mKeyIds.add(dataSnapshot.getKey());
                    getFriendFromKey(dataSnapshot.getKey());
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
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.holder_show_friend, parent, false);
            return new FriendViewHolder(view);
        }

        @Override
        public void onBindViewHolder(FriendViewHolder holder, int position) {
            User user = mUsers.get(position);
            holder.mName.setText(user.getName());
            if (user.getPhotoUrl() == null) {
                holder.mPhotoProfileCircleView
                        .setImageDrawable(ContextCompat
                        .getDrawable(mContext, R.drawable.ic_account_circle_black_36dp));
            }else{
                Glide.with(mContext)
                        .load(user.getPhotoUrl())
                        .into(holder.mPhotoProfileCircleView);
            }
        }

        @Override
        public int getItemCount() {
            return mUsers.size();
        }

        public void cleanupListener() {
            if (mChildEventListener != null){
                mReference.removeEventListener(mChildEventListener);
            }
        }
    }
}