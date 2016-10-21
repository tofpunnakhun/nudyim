package com.ayp.nudyim.trip;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import com.ayp.nudyim.R;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by onepi on 10/7/2016.
 */

public class DatePickerFragment extends DialogFragment
        implements DialogInterface.OnClickListener {
    public static final String TAG = "DatePickerFragment";

    protected static final String ARGUMENT_DATE = "ARG_DATE";
    protected static final String ARGUMENT_INT = "ARG_INT";

    int requestCode;

    private Calendar mCalendar;
    private DatePicker mDatePicker;

    private Callback mCallback;
    public interface Callback {
        void sendValue(Date date, int request);
    }

    public static DatePickerFragment newInstance(Date date, int request) {
        Bundle args = new Bundle();
        DatePickerFragment fragment = new DatePickerFragment();
        args.putSerializable(ARGUMENT_DATE, date);
        args.putInt(ARGUMENT_INT, request);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCallback = (Callback)getActivity();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Date date = (Date) getArguments().getSerializable(ARGUMENT_DATE);
        requestCode = getArguments().getInt(ARGUMENT_INT);

        mCalendar = Calendar.getInstance();
        mCalendar.setTime(date);
        int year = mCalendar.get(Calendar.YEAR);
        int month = mCalendar.get(Calendar.MONTH);
        int day = mCalendar.get(Calendar.DAY_OF_MONTH);

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_date, null);
        mDatePicker = (DatePicker) v.findViewById(R.id.date_picker);
        mDatePicker.init(year, month, day, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v);
//        builder.setTitle(R.string.date_picker_title);
        builder.setPositiveButton(android.R.string.ok, this);

        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        int day = mDatePicker.getDayOfMonth();
        int month = mDatePicker.getMonth();
        int year = mDatePicker.getYear();

        mCalendar.set(Calendar.YEAR, year);
        mCalendar.set(Calendar.MONTH, month);
        mCalendar.set(Calendar.DAY_OF_MONTH, day);


        Date date = mCalendar.getTime();
        mCallback.sendValue(date, requestCode);
//        sendResult(Activity.RESULT_OK, date);

    }

//    private void sendResult(int resultCode, Date date) {
//        if (getTargetFragment() == null) {
//            return;
//        }
//
//        Intent intent = new Intent();
//        intent.putExtra(EXTRA_DATE, date);
//        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
//    }
}
