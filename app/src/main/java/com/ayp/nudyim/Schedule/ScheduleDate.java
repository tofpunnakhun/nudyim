package com.ayp.nudyim.schedule;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
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
import android.view.Window;
import android.widget.Button;
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

public class ScheduleDate extends Fragment implements View.OnClickListener {


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

    private int color[] = {R.color.colorPink, R.color.colorGreen, R.color.colorRed2, R.color.colorBlue, R.color.colorAccent, R.color.colorBudget};
    private int indexColor;

    private Paint paint = new Paint();
    private int edit_position;
    private String editTopicText;
    private String editHour;
    private String editMin;

    public ScheduleDate() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.schedule_fragment, container, false);
        //Get data
        mDate = getArguments().getString("DATE");
        KEYID = getArguments().getString("KEYID");
        indexColor = 0;
        //init components view
        initView(view);
        return view;
    }

    private void initView(View view) {
        mFloatingActionButton = (FloatingActionButton) view.findViewById(R.id.floating_button);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        mScheduleRecyclerView = (RecyclerView) view.findViewById(R.id.scheduleRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFloatingActionButton.setOnClickListener(this);
        showProgressBar();
        fireBaseAdapter();
//        initSwipe();
    }

    private void showProgressBar() {
        //Check it have chile in table
        mFirebaseDatabaseReference.child(TRIP_CHILD).child(KEYID).child(SCHEDULE_CHILD).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean flag = true;
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    if (postSnapshot.getKey().equals(mDate)) {
                        flag = false;
                    }
                }
                if (flag) {
                    mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void fireBaseAdapter() {
        mFirebaseAdapter = new FirebaseRecyclerAdapter<Schedule, ScheduleViewHolder>(Schedule.class,
                R.layout.item_schedule,
                ScheduleViewHolder.class,
                mFirebaseDatabaseReference.child(TRIP_CHILD).child(KEYID).child(SCHEDULE_CHILD).child(mDate).orderByChild("hour")) {
            @Override
            protected void populateViewHolder(ScheduleViewHolder viewHolder, Schedule model, int position) {
                final DatabaseReference mGetFireBaseReference = getRef(position);
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                viewHolder.timeTextView.setText(model.getTime());
                viewHolder.topicTextView.setText(model.getTopic());
                viewHolder.nameTextView.setText(model.getName());
                editTopicText = model.getTopic();
                editHour = model.getHour();
                editMin = model.getMinute();

                GradientDrawable bgShape = (GradientDrawable) viewHolder.circle.getBackground();
                bgShape.setColor(getResources().getColor(color[indexColor % 6]));
                viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        final Dialog alertDialog = new Dialog(getActivity());
                        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        alertDialog.setContentView(R.layout.dialog_click_item_schedule);

                        // set the custom dialog components - text, image and button
                        Button editButton = (Button) alertDialog.findViewById(R.id.edit_schedule);
                        Button deleteButton = (Button) alertDialog.findViewById(R.id.delete_schedule);
                        deleteButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mGetFireBaseReference.removeValue();
                                alertDialog.cancel();
                            }
                        });

                        editButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.cancel();
                                final Dialog editDialog = new Dialog(getActivity());
                                editDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                editDialog.setContentView(R.layout.dialog_new_schedule);

                                // set the custom dialog components - text, image and button
                                final EditText titleInput = (EditText) editDialog.findViewById(R.id.titleInput);
                                final TimePicker timePicker = (TimePicker) editDialog.findViewById(R.id.timePicker);
                                final TextView okText = (TextView) editDialog.findViewById(R.id.ok_text);
                                final TextView cancelText = (TextView) editDialog.findViewById(R.id.cancel_text);
                                titleInput.setText(editTopicText);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    timePicker.setHour(Integer.valueOf(editHour));
                                    timePicker.setMinute(Integer.valueOf(editMin));
                                }

                                okText.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        int hour = 0;
                                        int min = 0;
                                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                                            hour = timePicker.getHour();
                                            min = timePicker.getMinute();
                                        }
                                        Schedule schedule = new Schedule(titleInput.getText().toString(), FireBaseConnect.getInstance().getUsername(), String.valueOf(hour) + ":" + String.valueOf(min), String.valueOf(hour), String.valueOf(min));
                                        mGetFireBaseReference.setValue(schedule);
                                        editDialog.cancel();
                                    }
                                });

                                cancelText.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        editDialog.cancel();
                                    }
                                });
                                editDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                editDialog.show();
                            }
                        });
                        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        alertDialog.show();
                        return false;
                    }
                });
                indexColor++;
            }
        };
        mScheduleRecyclerView.setLayoutManager(mLinearLayoutManager);
        mScheduleRecyclerView.setAdapter(mFirebaseAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.floating_button:
                // get dialog_new_schedule.xml view
                final Dialog alertDialog = new Dialog(getActivity());
                alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                alertDialog.setContentView(R.layout.dialog_new_schedule);

                // set the custom dialog components - text, image and button
                final EditText titleInput = (EditText) alertDialog.findViewById(R.id.titleInput);
                final TimePicker timePicker = (TimePicker) alertDialog.findViewById(R.id.timePicker);
                final TextView okText = (TextView) alertDialog.findViewById(R.id.ok_text);
                final TextView cancelText = (TextView) alertDialog.findViewById(R.id.cancel_text);

                Calendar c = Calendar.getInstance();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    timePicker.setHour(c.get(Calendar.HOUR_OF_DAY));
                    timePicker.setMinute(c.get(Calendar.MINUTE));
                }

                okText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int hour = 0;
                        int min = 0;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                            hour = timePicker.getHour();
                            min = timePicker.getMinute();
                        }
                        Schedule schedule = new Schedule(titleInput.getText().toString(), FireBaseConnect.getInstance().getUsername(), String.valueOf(hour) + ":" + String.valueOf(min), String.valueOf(hour), String.valueOf(min));
                        mFirebaseDatabaseReference.child(TRIP_CHILD).child(KEYID).child(SCHEDULE_CHILD).child(mDate).push().setValue(schedule);
                        alertDialog.cancel();
                    }
                });

                cancelText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.cancel();
                    }
                });
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDialog.show();
                break;
        }
    }
}
