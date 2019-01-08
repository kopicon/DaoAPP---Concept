package dao.daoapplication.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import dao.daoapplication.Adapters.DischargedScooterListAdapter;
import dao.daoapplication.Adapters.ScooterFailuresListAdapter;
import dao.daoapplication.Adapters.ScooterFaliuresDialogArrayAdapter;
import dao.daoapplication.Entities.DischargedScooter;
import dao.daoapplication.Entities.Scooter;
import dao.daoapplication.R;

public class ScootersFragment extends Fragment {

    private static final String TAG = "ProblemScooterFragment";

    private Context mContext;

    private ListView lvScooters;
    private CheckBox chckDischarges;
    private CheckBox chckFailures;
    private FloatingActionButton fbtnEmptyDS;


    private Button btnDialogYes;
    private Button btnDialogNo;
    private TextView txtDialog;
    private DischargedScooter cDScooter;
    private Scooter cScooter;

    private TextView txtDialogScooterNumber;
    private TextView txtDialogCarriersName;
    private ListView lvDialogFaliures;
    private Button btnback;
    private Button btnProblemSolved;

    private ArrayList<DischargedScooter> dischargedScooters = new ArrayList<>();
    private ArrayList<Scooter> failuresScooter = new ArrayList<>();
    private DischargedScooterListAdapter mDischargedAdapter;
    private ScooterFailuresListAdapter mFailuresAdapter;
    private ScooterFaliuresDialogArrayAdapter mReasonsAdapter;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_problems_scooters,container,false);
        lvScooters = view.findViewById(R.id.lv_reported_scooters);
        chckDischarges = view.findViewById(R.id.chck_Discharges);
        chckFailures = view.findViewById(R.id.chck_Failures);
        fbtnEmptyDS = view.findViewById(R.id.fbtn_empty_discharged_scooters);
        mDatabase = FirebaseDatabase.getInstance();
        mContext = getContext();
        mRef = mDatabase.getReference();
        chckDischarges.setChecked(true);
        chckFailures.setChecked(false);
        fbtnEmptyDS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.dialog_are_you_sure);
                btnDialogYes = dialog.findViewById(R.id.btn_dialog_remove_yes);
                btnDialogNo = dialog.findViewById(R.id.btn_dialog_remove_no);
                txtDialog = dialog.findViewById(R.id.txt_dialog_remove_what);
                txtDialog.setText("all discharged scooters from the system?");
                btnDialogYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mRef.child("Problems").child("DischargedScooters").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    dialog.dismiss();
                                    toastMassage("Discharged scooters have been successfully removed");
                                }
                            }
                        });
                    }
                });
                btnDialogNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        mRef.child("Problems").child("Scooter").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                failuresScooter.clear();
                for (DataSnapshot cS : dataSnapshot.getChildren()) {
                    mRef.child("Problems").child("Scooter").child(cS.getKey()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            cScooter = dataSnapshot.getValue(Scooter.class);
                            failuresScooter.add(cScooter);
                            if (mContext == getContext()) {
                                lvScooters.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                                        final Dialog dialog = new Dialog(getContext());
                                        dialog.setContentView(R.layout.dialog_scooter_failures);
                                        txtDialogCarriersName = dialog.findViewById(R.id.txt_dialog_carrierName_faliures);
                                        txtDialogScooterNumber = dialog.findViewById(R.id.txt_dialog_scooterNumber_faliures);
                                        lvDialogFaliures = dialog.findViewById(R.id.lv_dialog_faliures);
                                        btnback = dialog.findViewById(R.id.btn_dialogScooter_faliures_back);
                                        btnProblemSolved = dialog.findViewById(R.id.btn_Scooterproblems_solved);
                                        mReasonsAdapter = new ScooterFaliuresDialogArrayAdapter(getContext(), R.layout.fragment_scooter_faliures_dialog_list_item, failuresScooter.get(position).getFaliures());
                                        lvDialogFaliures.setAdapter(mReasonsAdapter);
                                        txtDialogCarriersName.setText(failuresScooter.get(position).getCarrierName());
                                        txtDialogScooterNumber.setText(failuresScooter.get(position).getScooterNumber());
                                        btnProblemSolved.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                mRef.child("Problems").child("Scooter").child(failuresScooter.get(position).getScooterNumber()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        toastMassage("Faliures has been deleted");
                                                        dialog.dismiss();
                                                        if(failuresScooter.isEmpty()){
                                                            chckDischarges.setChecked(true);
                                                        }
                                                    }
                                                });
                                            }
                                        });
                                        btnback.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                dialog.dismiss();
                                            }
                                        });
                                        dialog.show();
                                    }
                                });
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

        mRef.child("Problems").child("DischargedScooters").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dischargedScooters.clear();
                for (DataSnapshot cS: dataSnapshot.getChildren()){
                    mRef.child("Problems").child("DischargedScooters").child(cS.getKey()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            cDScooter = dataSnapshot.getValue(DischargedScooter.class);
                            dischargedScooters.add(cDScooter);
                            if(mContext == getContext()) {
                                mDischargedAdapter = new DischargedScooterListAdapter(getContext(), R.layout.fragment_dischargedscooter_list_item, dischargedScooters);
                                lvScooters.setAdapter(mDischargedAdapter);
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

        chckFailures.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (!failuresScooter.isEmpty()) {
                        lvScooters.setAdapter(null);
                        chckDischarges.setChecked(false);
                        mFailuresAdapter = new ScooterFailuresListAdapter(getContext(), R.layout.fragment_scooter_failures_list_item, failuresScooter);
                        lvScooters.setAdapter(mFailuresAdapter);
                    }else {
                        chckFailures.setChecked(false);
                        toastMassage("No Faliures to show yet");
                    }
                }
//
            }
        });

        chckDischarges.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    if(!dischargedScooters.isEmpty()) {
                        lvScooters.setAdapter(null);
                        chckFailures.setChecked(false);
                        mDischargedAdapter = new DischargedScooterListAdapter(getContext(), R.layout.fragment_dischargedscooter_list_item, dischargedScooters);
                        lvScooters.setAdapter(mDischargedAdapter);
                    }else{
                        toastMassage("No Discharges to show yet");
                    }
                }
            }
        });
        return view;
    }

    private void toastMassage(String massage){
        Toast.makeText(getContext(),massage,Toast.LENGTH_SHORT).show();
    }
}
