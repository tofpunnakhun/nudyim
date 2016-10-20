package com.ayp.nudyim.schedule;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.ayp.nudyim.R;
import com.ayp.nudyim.database.FireBaseConnect;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

/**
 * Created by Punnakhun on 10/18/2016.
 */

public class ScheduleDate extends Fragment {

    public static class ScheduleViewHolder extends RecyclerView.ViewHolder {
        public TextView timeTextView;
        public TextView topicTextView;
        public TextView nameTextView;
        public RelativeLayout circle;

        public ScheduleViewHolder(View v) {
            super(v);
            timeTextView = (TextView) itemView.findViewById(R.id.time_show);
            topicTextView = (TextView) itemView.findViewById(R.id.topic_schedule);
            nameTextView = (TextView) itemView.findViewById(R.id.name_create);
            circle = (RelativeLayout) itemView.findViewById(R.id.circle);
        }
    }

    public static final String TRIP_CHILD = "trip";
    public static final String SCHEDULE_CHILD = "schedule";
    private String mDate;

    private RecyclerView mScheduleRecyclerView;
    private ProgressBar mProgressBar;
    private LinearLayoutManager mLinearLayoutManager;
    private FloatingActionButton mFloatingActionButton;

    private String KEYID;

    // Firebase instance variables
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<Schedule, ScheduleViewHolder>
            mFirebaseAdapter;

    public ScheduleDate(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.schedule_fragment,container,false);

        //Get data
        mDate = getArguments().getString("DATE");
        KEYID = getArguments().getString("KEYID");

        // components
        mFloatingActionButton = (FloatingActionButton)view.findViewById(R.id.floating_button);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        mScheduleRecyclerView = (RecyclerView) view.findViewById(R.id.scheduleRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());

        // New child entries
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        //Check it have chile in table
        mFirebaseDatabaseReference.child(TRIP_CHILD).child(KEYID).child(SCHEDULE_CHILD).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean flag = true;
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    if (postSnapshot.getKey().equals(mDate)){
                        flag = false;
                    }
                }
                if(flag){
                    mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mFirebaseAdapter = new FirebaseRecyclerAdapter<Schedule, ScheduleViewHolder>(Schedule.class,
                R.layout.item_schedule,
                ScheduleViewHolder.class,
                mFirebaseDatabaseReference.child(TRIP_CHILD).child(KEYID).child(SCHEDULE_CHILD).child(mDate).orderByChild("hour")) {
            @Override
            protected void populateViewHolder(ScheduleViewHolder viewHolder, Schedule model, int position) {
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
//                String time = model.getHour() + ":" + model.getMinute();
                viewHolder.timeTextView.setText(model.getHour());
                viewHolder.topicTextView.setText(model.getTopic());
                viewHolder.nameTextView.setText(model.getName());

                GradientDrawable bgShape = (GradientDrawable)viewHolder.circle.getBackground();
                bgShape.setColor(Color.BLACK);
            }
        };

        //Set RecycleView
        mScheduleRecyclerView.setLayoutManager(mLinearLayoutManager);
        mScheduleRecyclerView.setAdapter(mFirebaseAdapter);

        //Set on Click of Floating Action Button
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get add_schedule.xml view
                LayoutInflater li = LayoutInflater.from(getActivity());
                View addScheduleView = li.inflate(R.layout.add_schedule, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        getActivity());

                // set add_schedule.xml to alertdialog builder
                alertDialogBuilder.setView(addScheduleView);

                final EditText titleInput = (EditText) addScheduleView.findViewById(R.id.titleInput);
                final TimePicker timePicker = (TimePicker) addScheduleView.findViewById(R.id.timePicker);

                Calendar c = Calendar.getInstance();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    timePicker.setHour(c.get(Calendar.HOUR_OF_DAY));
                    timePicker.setMinute(c.get(Calendar.MINUTE));
                }
                // set dialog message
                alertDialogBuilder
                        .setTitle("New Event")
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        int hour = 0;
                                        int min = 0;
                                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                                            hour = timePicker.getHour();
                                            min = timePicker.getMinute();
                                        }
                                        Schedule schedule = new Schedule(titleInput.getText().toString(), FireBaseConnect.getInstance().getUsername(), String.valueOf(hour)+":"+String.valueOf(min), String.valueOf(min));
                                        mFirebaseDatabaseReference.child(TRIP_CHILD).child(KEYID).child(SCHEDULE_CHILD).child(mDate).push().setValue(schedule);
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                });
                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
        return view;
    }
}
