package com.ayp.nudyim.trip;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.ayp.nudyim.R;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ayp.nudyim.model.User;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Chaiwat on 10/12/2016.
 */

public class InviteFriendActivity extends AppCompatActivity {

    private static final String TAG = "InviteFriendActivity" ;
    public static final String INVITE_VALUE = "INVITE_VALUE";
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    private DatabaseReference mDatabaseReference;

    private FirebaseRecyclerAdapter<User, InviteFriendHolder> mAdapter;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;

    private List<String> inviteCheckboxList;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.done_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                Log.d(TAG, "inviteCheckboxList: "+inviteCheckboxList);
                Intent intent = new Intent();
                intent.putStringArrayListExtra(INVITE_VALUE, (ArrayList<String>) inviteCheckboxList);
                setResult(RESULT_OK, intent);
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setReverseLayout(true);
        mLinearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        inviteCheckboxList = new ArrayList<>();

        mAdapter = new FirebaseRecyclerAdapter<User, InviteFriendHolder>(
                User.class,
                R.layout.holder_invite_friend,
                InviteFriendHolder.class,
                mDatabaseReference.child("user").child(getUid()).child("friend")) {
            @Override
            protected void populateViewHolder(InviteFriendHolder viewHolder, User model, int position) {

                final DatabaseReference ref = getRef(position);

                viewHolder.mName.setText(model.getName());
                if (model.getPhotoUrl() == null) {
                    viewHolder.mProfileCircleImageView
                            .setImageDrawable(ContextCompat
                                    .getDrawable(InviteFriendActivity.this, R.drawable.ic_account_circle_black_36dp));
                } else {
                    Glide.with(InviteFriendActivity.this)
                            .load(model.getPhotoUrl())
                            .into(viewHolder.mProfileCircleImageView);
                }
                viewHolder.mInviteCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            inviteCheckboxList.add(ref.getKey());
                            Log.d(TAG, "onCheckedChanged add: "+inviteCheckboxList);
                        }else{
                            //isChecked = false
                            inviteCheckboxList.remove(ref.getKey());
                            Log.d(TAG, "onCheckedChanged remove: "+inviteCheckboxList);
                        }
                    }
                });
            }
        };
        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private String getUid() {
        return mFirebaseUser.getUid();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_invite_friend);

        getSupportActionBar().setTitle(CreateTripActivity.EMPTY_STRING);
//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_cancel);

        mFirebaseAuth = mFirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        mRecyclerView = (RecyclerView) findViewById(R.id.invite_friend_list);
        mRecyclerView.setHasFixedSize(true);

    }

    private static class InviteFriendHolder extends RecyclerView.ViewHolder {
        public TextView mName;
        public CircleImageView mProfileCircleImageView;
        public AppCompatCheckBox mInviteCheckBox;

        public InviteFriendHolder(View itemView) {
            super(itemView);
            mName = (TextView) itemView.findViewById(R.id.name_profile);
            mProfileCircleImageView = (CircleImageView) itemView.findViewById(R.id.friend_profile);
            mInviteCheckBox = (AppCompatCheckBox) itemView.findViewById(R.id.checkbox_invite);
        }
    }

   /* private static class InviteFriendAdapter extends RecyclerView.Adapter<InviteFriendHolder> {

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
            View view = inflater.inflate(R.layout.holder_invite_friend,parent,false);
            return new InviteFriendHolder(view);
        }

        @Override
        public void onBindViewHolder(InviteFriendHolder holder, final int position) {

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

            holder.mInviteCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked){
                          mKeyId.get(position);
                    }
                }
            });
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
    }*/
}
