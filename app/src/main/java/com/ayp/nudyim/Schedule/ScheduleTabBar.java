package com.ayp.nudyim.schedule;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ayp.nudyim.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Punnakhun on 10/18/2016.
 */

public class ScheduleTabBar extends Fragment {

    private Toolbar toolbar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private String KEY_CHILD;

    //Date
    SimpleDateFormat mFormat;
    private String mStartDate;
    private String mEndDate;
    Date mStartDateFormatDate;
    Date mEndDateFormatDate;
    long mDiffDays;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_bar_activity, container, false);

        KEY_CHILD = getArguments().getString("KEY_CHILD");
        mStartDate = getArguments().getString("START_DATE");
        mEndDate = getArguments().getString("END_DATE");

        mFormat = new SimpleDateFormat("dd/MM/yyyy");
        mStartDateFormatDate = null;
        mEndDateFormatDate = null;

        try {
            mStartDateFormatDate = mFormat.parse(mStartDate);
            mEndDateFormatDate = mFormat.parse(mEndDate);
            long diff = (mEndDateFormatDate.getTime() - mStartDateFormatDate.getTime());
            mDiffDays = (diff / (24 * 60 * 60 * 1000))+1;
            Log.d("Test","Key = " + KEY_CHILD + ", Start Date = " + mStartDate + ", End Date = " + mEndDate +",Dif = " + String.valueOf(mDiffDays));

        } catch (ParseException e) {
            e.printStackTrace();
        }

        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        setupViewPager(mViewPager);

        mTabLayout = (TabLayout) view.findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);

        return view;
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        for (int i = 0 ; i < mDiffDays ; i++ )
        {
            Calendar cal = Calendar.getInstance();
            cal.setTime(mStartDateFormatDate);
            cal.add(Calendar.DATE, i);
            SimpleDateFormat format2 = new SimpleDateFormat("dd MMM");
            Date date = cal.getTime();
            String newDate = format2.format(date);
            Bundle bundle = new Bundle();
            bundle.putString("DATE", newDate);
            bundle.putString("KEYID", KEY_CHILD);
            ScheduleDate scheduleDate = new ScheduleDate();
            scheduleDate.setArguments(bundle);
            adapter.addFragment(scheduleDate, newDate);
        }

        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
