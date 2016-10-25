package com.ayp.nudyim.accept;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ayp.nudyim.R;
import com.ayp.nudyim.model.User;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Chaiwat on 10/21/2016.
 */

public class AcceptFriendFragment extends Fragment {

    private TextView mNameText;
    private Button mConfirmButton;
    private CircleImageView mCircleImageView;

    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    private String mKeyFriend;
    private String mKeyMyself;

    public static AcceptFriendFragment newInstance(String keyFriend, String keyMyself) {
        Bundle args = new Bundle();
        args.putString("key_friend", keyFriend);
        args.putString("key_myself", keyMyself);
        AcceptFriendFragment fragment = new AcceptFriendFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_accept_friend, container, false);

        mNameText = (TextView) view.findViewById(R.id.name_profile);
        mConfirmButton = (Button) view.findViewById(R.id.button_accept);
        mCircleImageView = (CircleImageView) view.findViewById(R.id.photo_profile);

        mKeyFriend = getArguments().getString("key_friend");
        mKeyMyself = getArguments().getString("key_myself");

        Log.d("test", "uuid friend: " + mKeyFriend);
        Log.d("test", "uuid myself: " + mKeyMyself);
        Log.d("test", "uuid: " + mFirebaseUser.getUid());

        mDatabaseReference.child("user").child(mKeyMyself).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                mNameText.setText(user.getName());
                if (user.getPhotoUrl() == null) {
                    mCircleImageView.setImageDrawable(ContextCompat
                            .getDrawable(getActivity(), R.drawable.ic_account_circle_black_36dp));
                } else {
                    Glide.with(getActivity())
                            .load(user.getPhotoUrl())
                            .into(mCircleImageView);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmFriend();
                getActivity().finish();
            }
        });
        return view;
    }

    private void confirmFriend() {
        mDatabaseReference.child("user").child(mKeyFriend).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User friendInfo = dataSnapshot.getValue(User.class);
                mDatabaseReference.child("user")
                        .child(mKeyMyself)
                        .child("friend")
                        .child(mKeyFriend)
                        .setValue(friendInfo);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mDatabaseReference.child("user").child(mKeyMyself).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User userInfo = dataSnapshot.getValue(User.class);
                mDatabaseReference.child("user")
                        .child(mKeyFriend)
                        .child("friend")
                        .child(mKeyMyself)
                        .setValue(userInfo);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
