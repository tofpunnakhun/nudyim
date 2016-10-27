package com.ayp.nudyim.detail;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.ayp.nudyim.R;
import com.ayp.nudyim.model.Trip;
import com.ayp.nudyim.model.User;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Punnakhun on 10/25/2016.
 */

public class DetailFragment extends Fragment {

    public static class MemberViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView messengerImageView;

        public MemberViewHolder(View v) {
            super(v);
            messengerImageView = (CircleImageView) itemView.findViewById(R.id.messengerImageView);

        }
        public void bind(String photoURL, Context context) {
            Glide.with(context)
                    .load(photoURL)
                    .into(messengerImageView);
        }
    }

    private String KEY_CHILD;
    private DatabaseReference mFirebaseDatabaseReference;
    private DatabaseReference mFirebaseDatabaseReference2;
    private TextView mTripNameDetail;
    private TextView mTripLocationDetail;
    private TextView mTripDetail;
    private TextView mTripDateDetail;
    private RecyclerView mRecyclerView;
    private CircleImageView addFriendButton;

    private DetailAdaptor mDetailAdapter;
    private List<String> photoUrl = new ArrayList<String>();
    private List<String> memberKey = new ArrayList<String>();

    private List<String> friendUserEmail = new ArrayList<String>();
    private String[] friendEmail;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Firebase instance variables
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_trip,container,false);
        KEY_CHILD = getArguments().getString("KEY_CHILD");

        mTripNameDetail = (TextView) view.findViewById(R.id.trip_name_detail);
        mTripLocationDetail = (TextView) view.findViewById(R.id.trip_location_detail);
        mTripDetail = (TextView) view.findViewById(R.id.trip_detail_detail);
        mTripDateDetail = (TextView) view.findViewById(R.id.trip_date_detail);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.show_trip_member);
        addFriendButton = (CircleImageView) view.findViewById(R.id.buttonImageView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseDatabaseReference.child("trip").child(KEY_CHILD).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                Trip tripLab = dataSnapshot.getValue(Trip.class);
                mTripNameDetail.setText(tripLab.getTopic());
                mTripDetail.setText(tripLab.getDetails());
                mTripLocationDetail.setText(tripLab.getLocation());
                mTripDateDetail.setText(tripLab.getStartdate() + " - " + tripLab.getEnddate());
                for(DataSnapshot post2 : dataSnapshot.child("member").getChildren()){
                    Log.d("Test", post2.getKey());
                    memberKey.add(post2.getKey());
                }

                for (int i = 0; i<memberKey.size(); i++)
                {
                    Log.d("Test", "onCreateView: Size = " + memberKey.size() + " ID = " + memberKey.get(i));
                    mFirebaseDatabaseReference.child("user").child(memberKey.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.d("Test", "onDataChange: dataSnapShot ");
                            User user = dataSnapshot.getValue(User.class);
                            Log.d("Test", "onDataChange: dataSnapShot photoUrl = " + user.getPhotoUrl());
                            photoUrl.add(user.getPhotoUrl());
                            if(mDetailAdapter == null)
                            {
                                Log.d("Test", "onCreateView: mDetailAdapter new mDtail size = " + photoUrl.size());
                                mDetailAdapter = new DetailAdaptor(photoUrl);
                                mRecyclerView.setAdapter(mDetailAdapter);
                            }
                            else
                            {
                                Log.d("Test", "onCreateView: mDetailAdapter2");
                                mDetailAdapter.setPhotoUrl(photoUrl);
                                mDetailAdapter.notifyDataSetChanged();
                            }
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
        mRecyclerView.setAdapter(mDetailAdapter);

        mFirebaseDatabaseReference.child("user").child(mFirebaseUser.getUid()).child("friend").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapShot : dataSnapshot.getChildren()){
                    User user = postSnapShot.getValue(User.class);
                    Log.d("Test", "Keep Email" + user.getEmail());
                    friendUserEmail.add(user.getEmail());
                }
                friendEmail = new String[friendUserEmail.size()];
                for (int i = 0; i< friendUserEmail.size(); i++)
                {
                    friendEmail[i] = friendUserEmail.get(i);
                    Log.d("Test", "onDataChange: Friend email = " + friendUserEmail.get(i));
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        addFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog alertDialog= new Dialog(getActivity());
//                alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                alertDialog.setContentView(R.layout.dialog_add_friend);
                alertDialog.setCancelable(true);

                final TextView okTextLocation = (TextView) alertDialog.findViewById(R.id.ok_text);
                final TextView cancelTextLocation = (TextView) alertDialog.findViewById(R.id.cancel_text);
                final AutoCompleteTextView editText=(AutoCompleteTextView)alertDialog.findViewById(R.id.autocomplete);

                ArrayAdapter adapter = new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1,friendEmail);
                editText.setAdapter(adapter);
                editText.setThreshold(1);

                okTextLocation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        mFirebaseDatabaseReference.child("trip").child(KEY_CHILD).child("member").child(editText.getText().toString()).setValue(true);
                        alertDialog.cancel();
                    }
                });
                cancelTextLocation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.cancel();
                    }
                });

//                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDialog.show();

            }
        });
        return view;
    }

    private class DetailAdaptor extends RecyclerView.Adapter<MemberViewHolder>{
        private List<String> mPhotoUrl;
        private int _viewCreatingCount;

        public DetailAdaptor(List<String> mPhotoUrl){
            this.mPhotoUrl = mPhotoUrl;
        }

        protected void setPhotoUrl(List<String> photoUrl){
            mPhotoUrl = photoUrl;
        }

        @Override
        public MemberViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            _viewCreatingCount++;
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View v = layoutInflater.inflate(R.layout.item_member, parent, false);
            return new MemberViewHolder(v);
        }

        @Override
        public void onBindViewHolder(MemberViewHolder holder, int position) {
            String photoUrl = mPhotoUrl.get(position);
            Log.d("Test", "onBindViewHolder: Photo URL = " + photoUrl);
            holder.bind(photoUrl, getActivity());
        }

        @Override
        public int getItemCount() {
            return mPhotoUrl.size();
        }
    }
}
