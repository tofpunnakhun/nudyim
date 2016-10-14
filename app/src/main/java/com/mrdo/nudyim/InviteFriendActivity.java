package com.mrdo.nudyim;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mrdo.nudyim.model.User;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Chaiwat on 10/12/2016.
 */

public class InviteFriendActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    private DatabaseReference mDatabaseReference;
    private DatabaseReference mFriendReference;

    private RecyclerView mRecyclerView;
    private InviteFriendAdapter mInviteFriendAdapter;

    @Override
    protected void onStop() {
        super.onStop();
        mInviteFriendAdapter.cleanupListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.done_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_done:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_invite_friend);

        getSupportActionBar().setTitle(CreateTripActivity.EMPTY_STRING);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_cancel);

        mFirebaseAuth = mFirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFriendReference = FirebaseDatabase.getInstance().getReference()
                .child("user")
                .child(mFirebaseUser.getUid())
                .child("friend");

        mRecyclerView = (RecyclerView) findViewById(R.id.invite_friend_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);

        mInviteFriendAdapter = new InviteFriendAdapter(this, mFriendReference);
        mRecyclerView.setAdapter(mInviteFriendAdapter);
    }

    private static class InviteFriendHolder extends RecyclerView.ViewHolder {
        public TextView mName;
        public CircleImageView mProfileCircleImageView;

        public InviteFriendHolder(View itemView) {
            super(itemView);

            mName = (TextView) itemView.findViewById(R.id.name_profile);
            mProfileCircleImageView = (CircleImageView) itemView.findViewById(R.id.friend_profile);
        }
    }

    private static class InviteFriendAdapter extends RecyclerView.Adapter<InviteFriendHolder> {

        private static final String TAG = "InviteFriendAdapter";

        private Context mContext;
        private DatabaseReference mReference;
        private ChildEventListener mChildEventListener;

        private List<String> mKeyId = new ArrayList<>();
        private List<User> mUsers = new ArrayList<>();

        public InviteFriendAdapter(Context context, DatabaseReference ref) {
            mContext = context;
            mReference = ref;

            ChildEventListener childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    mKeyId.add(dataSnapshot.getKey());
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
                    mUsers.add(user);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "getFriendFromKey:onCancelled", databaseError.toException());
                    Toast.makeText(mContext, "Failed to load user.", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public InviteFriendHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.holder_show_friend,parent,false);
            return new InviteFriendHolder(view);
        }

        @Override
        public void onBindViewHolder(InviteFriendHolder holder, int position) {
            User user = mUsers.get(position);
            holder.mName.setText(user.getName());
            if (user.getPhotoUrl() == null) {
                holder.mProfileCircleImageView
                        .setImageDrawable(ContextCompat
                                .getDrawable(mContext, R.drawable.ic_account_circle_black_36dp));
            }else{
                Glide.with(mContext)
                        .load(user.getPhotoUrl())
                        .into(holder.mProfileCircleImageView);
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
