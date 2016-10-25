package com.ayp.nudyim.budget;

/**
 * Created by Punnakhun on 10/18/2016.
 */

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.ayp.nudyim.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Punnakhun on 10/12/2016.
 */
public class BudgetFragment extends Fragment {

    public static class IncomeViewHolder extends RecyclerView.ViewHolder {
        public TextView detailTextView;
        public TextView incomeTextView;

        public IncomeViewHolder(View v) {
            super(v);
            detailTextView = (TextView) itemView.findViewById(R.id.detail_income);
            incomeTextView = (TextView) itemView.findViewById(R.id.income_amount);
        }
    }

    public static class ExpendViewHolder extends RecyclerView.ViewHolder {
        public TextView detailTextView;
        public TextView expendTextView;


        public ExpendViewHolder(View v) {
            super(v);
            detailTextView = (TextView) itemView.findViewById(R.id.detail_income);
            expendTextView = (TextView) itemView.findViewById(R.id.expend_amount);
        }
    }

    public static final String TRIP_CHILD = "trip";
    public static final String BUDGET_CHILD = "budget";
    public static final String INCOME_CHILD = "income";
    public static final String EXPEND_CHILD = "expend";

    private String KEY_CHILD;
    private int mBalance;

    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mFirebaseDatabaseReference;

    private RecyclerView mIncomeRecyclerView;
    private RecyclerView mExpendRecyclerView;
    private LinearLayoutManager mIncomeLinearLayoutManager;
    private LinearLayoutManager mExpendLinearLayoutManager;
    private FloatingActionButton mFloatingActionButton;
    private TextView mBalanceTextView;


    private FirebaseRecyclerAdapter<Income, IncomeViewHolder>
            mIncomeFirebaseAdapter;
    private FirebaseRecyclerAdapter<Expend, ExpendViewHolder>
            mExpendFirebaseAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.budget_fragment,container,false);

        KEY_CHILD = getArguments().getString("KEY_CHILD");
        mBalance = 0;

        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        mIncomeRecyclerView = (RecyclerView) view.findViewById(R.id.budgetIncomeRecyclerView);
        mIncomeLinearLayoutManager = new LinearLayoutManager(getActivity());
        mIncomeRecyclerView.setLayoutManager(mIncomeLinearLayoutManager);
        mExpendRecyclerView = (RecyclerView) view.findViewById(R.id.budgetOutcomeRecyclerView);
        mExpendLinearLayoutManager = new LinearLayoutManager(getActivity());
        mExpendRecyclerView.setLayoutManager(mExpendLinearLayoutManager);

        mBalanceTextView = (TextView) view.findViewById(R.id.wallet_balance);
        mFloatingActionButton = (FloatingActionButton) view.findViewById(R.id.floating_button) ;

        // New child entries
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
//        mFirebaseDatabaseReference2 = FirebaseDatabase.getInstance().getReference();

        mIncomeFirebaseAdapter = new FirebaseRecyclerAdapter<Income, IncomeViewHolder>(Income.class,
                R.layout.item_income,
                IncomeViewHolder.class,
                mFirebaseDatabaseReference.child(TRIP_CHILD).child(KEY_CHILD).child(BUDGET_CHILD).child(INCOME_CHILD)) {
            @Override
            protected void populateViewHolder(IncomeViewHolder viewHolder, Income model, int position) {
                viewHolder.detailTextView.setText(model.getDetail());
                viewHolder.incomeTextView.setText("+THB"+String.valueOf(model.getIncome()));
                mBalance = mBalance + model.getIncome();
                mBalanceTextView.setText(String.valueOf(mBalance));

            }
        };

        mExpendFirebaseAdapter = new FirebaseRecyclerAdapter<Expend, ExpendViewHolder>(Expend.class,
                R.layout.item_expend,
                ExpendViewHolder.class,
                mFirebaseDatabaseReference.child(TRIP_CHILD).child(KEY_CHILD).child(BUDGET_CHILD).child(EXPEND_CHILD)) {
            @Override
            protected void populateViewHolder(ExpendViewHolder viewHolder, Expend model, int position) {
                viewHolder.detailTextView.setText(model.getDetail());
                viewHolder.expendTextView.setText("-THB"+String.valueOf(model.getExpend()));
                mBalance = mBalance - model.getExpend();
                mBalanceTextView.setText(String.valueOf(mBalance));
            }
        };

        //Set RecycleView
        mIncomeRecyclerView.setAdapter(mIncomeFirebaseAdapter);
        mExpendRecyclerView.setAdapter(mExpendFirebaseAdapter);

        //Set on Click of Floating Action Button
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get dialog_budget.xml view
                LayoutInflater li = LayoutInflater.from(getActivity());
                View addBudgetView = li.inflate(R.layout.dialog_budget, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        getActivity());

                // set dialog_budget.xml to alertdialog builder
                alertDialogBuilder.setView(addBudgetView);
                final EditText titleInput = (EditText) addBudgetView.findViewById(R.id.input_title);
                final EditText incomeInput = (EditText) addBudgetView.findViewById(R.id.input_income);
                final RadioButton incomesRadioButton = (RadioButton) addBudgetView.findViewById(R.id.radio_income);
                final RadioButton expendRadioButton = (RadioButton) addBudgetView.findViewById(R.id.radio_expenses);
                incomesRadioButton.setChecked(true);
                // set dialog message
                alertDialogBuilder
                        .setTitle("New budget")
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        if(incomesRadioButton.isChecked()){
                                            Income income = new Income(titleInput.getText().toString(), Integer.valueOf(incomeInput.getText().toString()));
                                            mFirebaseDatabaseReference.child(TRIP_CHILD).child(KEY_CHILD).child(BUDGET_CHILD).child(INCOME_CHILD).push().setValue(income);
                                        }
                                        else if(expendRadioButton.isChecked()){
                                            Expend expend = new Expend(titleInput.getText().toString(), Integer.valueOf(incomeInput.getText().toString()));
                                            mFirebaseDatabaseReference.child(TRIP_CHILD).child(KEY_CHILD).child(BUDGET_CHILD).child(EXPEND_CHILD).push().setValue(expend);
                                        }
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

