package dao.daoapplication.MenuActivities;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.telephony.CarrierConfigManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import dao.daoapplication.Adapters.CarriersListAdapter;
import dao.daoapplication.Adapters.TodaysCarriersAdapter;
import dao.daoapplication.AddCarrierActivity;
import dao.daoapplication.Entities.Carrier;
import dao.daoapplication.Entities.CarrierToday;
import dao.daoapplication.R;

public class CarriersActivity extends AppCompatActivity {
    private static final String TAG = "CarriersActivity";

    private ImageView imgback;
    private FloatingActionButton fbtnAddEmployee;
    private ListView lvCarriers;
    private CheckBox chckTodays;
    private CheckBox chckAllCarriers;
    private EditText txtSearch;

    private ArrayList<Carrier> allcarriers = new ArrayList<>();
    private ArrayList<CarrierToday> todaysCarriers = new ArrayList<>();
    private ArrayList<Carrier> filteredCarriers = new ArrayList<>();
    private ArrayList<CarrierToday> filteredCarrierToday = new ArrayList<>();

    private CarriersListAdapter cListAdapter;
    private TodaysCarriersAdapter mTListAdapter;
    private FirebaseFirestore mFirestore;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private static final int REQUEST_PHONE_CALL = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carriers);
        fbtnAddEmployee = findViewById(R.id.fbtn_addEmployee);
        lvCarriers = findViewById(R.id.lv_Carriers);
        chckAllCarriers = findViewById(R.id.chck_allCarriers);
        chckTodays = findViewById(R.id.chck_todaysCarriers);
        imgback = findViewById(R.id.ic_back);
        txtSearch = findViewById(R.id.txt_SearchCarriers);
        imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CarriersActivity.this.finish();
            }
        });
        fbtnAddEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CarriersActivity.this,AddCarrierActivity.class);
                startActivity(i);
            }
        });

        mFirestore = FirebaseFirestore.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference();
        allcarriers = new ArrayList<>();
        mRef.child("RequestLocation").setValue(true);

        if (ContextCompat.checkSelfPermission(CarriersActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CarriersActivity.this, new String[]{Manifest.permission.CALL_PHONE},REQUEST_PHONE_CALL);
        }
        if (ContextCompat.checkSelfPermission(CarriersActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CarriersActivity.this, new String[]{Manifest.permission.SEND_SMS},REQUEST_PHONE_CALL);
        }
        mFirestore.collection("Carriers").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        DocumentReference cRef = mFirestore.collection("Carriers").document(document.getId());
                        cRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                Carrier cCarrier = documentSnapshot.toObject(Carrier.class);
                                allcarriers.add(cCarrier);
                                cListAdapter = new CarriersListAdapter(CarriersActivity.this, R.layout.fragment_carriers_list_item, allcarriers);
                                lvCarriers.setAdapter(cListAdapter);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                toastMassage("Something went wrong! Check Internet Connection");
                                Log.d(TAG, "onFailure: " + e);
                            }
                        });

                    }
                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: " + e);
            }
        });
        mRef.child("Carriers Today").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                todaysCarriers = new ArrayList<>();
                for (DataSnapshot cC : dataSnapshot.getChildren()) {
                    mRef.child("Carriers Today").child(cC.getKey()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            CarrierToday cC = dataSnapshot.getValue(CarrierToday.class);
                            todaysCarriers.add(cC);
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

        chckAllCarriers.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    fbtnAddEmployee.setVisibility(View.VISIBLE);
                    fbtnAddEmployee.setEnabled(true);
                    chckTodays.setChecked(false);
                    cListAdapter = new CarriersListAdapter(CarriersActivity.this, R.layout.fragment_carriers_list_item, allcarriers);
                    lvCarriers.setAdapter(cListAdapter);
                }
            }
        });
        chckAllCarriers.setChecked(true);
        chckTodays.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    if(!todaysCarriers.isEmpty()) {
                        fbtnAddEmployee.setVisibility(View.INVISIBLE);
                        fbtnAddEmployee.setEnabled(false);
                        chckAllCarriers.setChecked(false);
                        mTListAdapter = new TodaysCarriersAdapter(CarriersActivity.this, R.layout.fragment_todays_carriers_list_item, todaysCarriers);
                        lvCarriers.setAdapter(mTListAdapter);
                    }else{
                        chckTodays.setChecked(false);
                        chckAllCarriers.setChecked(true);
                        toastMassage("No logged in carrier yet");
                    }
                }
            }
        });
        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(chckAllCarriers.isChecked()){
                    filteredCarriers.clear();
                    for(Carrier cC : allcarriers){
                        if(cC.getName().toLowerCase().contains(s.toString().toLowerCase())){
                            filteredCarriers.add(cC);
                        }
                        cListAdapter = new CarriersListAdapter(CarriersActivity.this, R.layout.fragment_carriers_list_item, filteredCarriers);
                        lvCarriers.setAdapter(cListAdapter);
                    }
                }else if (chckTodays.isChecked()){
                    filteredCarrierToday.clear();
                    for(CarrierToday cC : todaysCarriers){
                        if(cC.getName().toLowerCase().contains(s.toString().toLowerCase())){
                            filteredCarrierToday.add(cC);
                        }
                    }
                    mTListAdapter = new TodaysCarriersAdapter(CarriersActivity.this, R.layout.fragment_todays_carriers_list_item, filteredCarrierToday);
                    lvCarriers.setAdapter(mTListAdapter);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    private void toastMassage(String massage){
        Toast.makeText(this,massage,Toast.LENGTH_SHORT).show();
    }
}
