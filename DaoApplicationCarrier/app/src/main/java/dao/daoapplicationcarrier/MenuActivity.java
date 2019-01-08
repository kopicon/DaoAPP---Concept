package dao.daoapplicationcarrier;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import dao.daoapplicationcarrier.Entities.Carrier;
import dao.daoapplicationcarrier.Entities.DischargedScooter;
import dao.daoapplicationcarrier.ReportActivities.ReportNewspaperActivity;
import dao.daoapplicationcarrier.ReportActivities.ReportPackageActivity;
import dao.daoapplicationcarrier.ReportActivities.ReportScooterActivity;

public class MenuActivity extends AppCompatActivity {
    private static final String TAG = "MenuActivity";

    private Button btnRoutelistMap;
    private Button btnReportProblem;
    private Button btnWorkDone;
    private Button btnLogOut;
    private Button btnChangePin;

    private Button btnChangePinOK;
    private TextView txtChangePin;
    private TextView txtChangePassword;


    private Button btnDoneReportNewspaper;
    private Button btnDoneReportPackage;
    private Button btnDoneReportScooter;
    private Button btnDone;
    private Button btnDoneDone;

    private Button btnReportNewspaper;
    private Button btnReportDischargedScooter;
    private Button btnReportScooter;
    private Button btnReportPackage;

    private Button btnFinnishedYes;
    private Button btnFinnishedNo;

    private Button btnLogoutYes;
    private Button btnLogoutNo;

    private SharedPreferences carrierPref;
    private Carrier cCarrier;
    private Carrier nCarrier;

    private SharedPreferences locationPref;
    private SharedPreferences.Editor lEditor;
    private SharedPreferences.Editor CEditor;

    private String deCryptedPin;
    private String enCryptedPin;
    private String routePackage;
    private String carrierNumber;
    private String carrierName;

    private double cLat;
    private double clng;

    private FusedLocationProviderClient mFusedLocationProviderClient;

    private FirebaseFirestore mFirestore;
    public static boolean mLocationPermissionGranted = false;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        btnRoutelistMap = findViewById(R.id.btn_RouteListMap);
        btnReportProblem = findViewById(R.id.btn_reprotProblem);
        btnWorkDone = findViewById(R.id.btn_workComplete);
        btnLogOut = findViewById(R.id.btn_logout);
        btnChangePin = findViewById(R.id.btn_changePassword);

        locationPref = getSharedPreferences("Location", MODE_PRIVATE);
        lEditor = locationPref.edit();

        mFirestore = FirebaseFirestore.getInstance();
        getDeviceLocation();


        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference();

        carrierPref = getSharedPreferences("Carrier",MODE_PRIVATE);
        routePackage = carrierPref.getString("RoutePackage","");
        carrierNumber = carrierPref.getString("CarrierNumber","");
        carrierName = carrierPref.getString("CarrierName","");
        requestLocationFromManager();
        btnRoutelistMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MenuActivity.this,RouteListMapActivity.class);
                startActivity(i);
            }
        });

        btnReportProblem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDeviceLocation();
                final Dialog dialog = new Dialog(MenuActivity.this);
                dialog.setContentView(R.layout.dialog_report_problem);
                btnReportNewspaper = dialog.findViewById(R.id.btn_report_Newspaper);
                btnReportDischargedScooter = dialog.findViewById(R.id.btn_report_DischargedScooter);
                btnReportScooter = dialog.findViewById(R.id.btn_report_Scooter);
                btnReportPackage = dialog.findViewById(R.id.btn_report_Package);
                dialog.show();
                btnReportNewspaper.setOnClickListener(new View.OnClickListener()
                {@Override
                public void onClick(View v) {
                    Intent i = new Intent(MenuActivity.this,ReportNewspaperActivity.class);
                    startActivity(i);
                }
                });

                btnReportDischargedScooter.setOnClickListener(new View.OnClickListener()
                {@Override
                public void onClick(View v) {
                    dialog.setContentView(R.layout.dialog_finnished);
                    btnFinnishedYes = dialog.findViewById(R.id.btn_finnishedYes);
                    btnFinnishedNo = dialog.findViewById(R.id.btn_finnishedNo);
                    btnFinnishedYes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DischargedScooter cScooter = new DischargedScooter(routePackage,carrierName,true,false,cLat,clng);
                            mRef.child("Problems").child("DischargedScooters").child(routePackage).setValue(cScooter);
                            toastMassage("Report has been sent! We are on the way");
                            dialog.dismiss();
                        }
                    });
                    btnFinnishedNo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DischargedScooter cScooter = new DischargedScooter(routePackage,carrierName,false,false,cLat,clng);
                            mRef.child("Problems").child("DischargedScooters").child(routePackage).setValue(cScooter);
                            toastMassage("Report has been sent! We are on the way");
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
                });

                btnReportScooter.setOnClickListener(new View.OnClickListener()
                {@Override
                public void onClick(View v) {
                    Intent i = new Intent(MenuActivity.this,ReportScooterActivity.class);
                    startActivity(i);
                    }
                });

                btnReportPackage.setOnClickListener(new View.OnClickListener()
                {@Override
                public void onClick(View v) {
                    Intent i = new Intent(MenuActivity.this,ReportPackageActivity.class);
                    startActivity(i);
                    }
                });
            }
        });

        btnWorkDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(MenuActivity.this);
                dialog.setContentView(R.layout.dialog_work_done);
                btnDoneReportNewspaper = dialog.findViewById(R.id.btn_report_Newspaper);
                btnDoneReportPackage = dialog.findViewById(R.id.btn_report_Package);
                btnDoneReportScooter = dialog.findViewById(R.id.btn_done_reportScooter);
                btnDone = dialog.findViewById(R.id.btn_done);
                btnDoneReportNewspaper.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(MenuActivity.this,ReportNewspaperActivity.class);
                        startActivity(i);
                    }
                });
                btnDoneReportPackage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(MenuActivity.this,ReportPackageActivity.class);
                        startActivity(i);
                    }
                });
                btnDoneReportScooter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(MenuActivity.this,ReportScooterActivity.class);
                        startActivity(i);
                    }
                });
                btnDone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mRef.child("RouteStatuses").child(routePackage).child("finished").setValue(true);
                        toastMassage("Great Job!");
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(MenuActivity.this);
                dialog.setContentView(R.layout.dialog_are_you_sure);
                btnLogoutYes = dialog.findViewById(R.id.btn_dialog_logout_yes);
                btnLogoutNo = dialog.findViewById(R.id.btn_dialog_logout_no);
                btnLogoutYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mRef.child("Carriers Today").child(carrierNumber).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                android.os.Process.killProcess(android.os.Process.myPid());
                            }
                        });
                    }
                });
                btnLogoutNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
        btnChangePin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(MenuActivity.this);
                dialog.setContentView(R.layout.dialog_change_pin);
                btnChangePinOK = dialog.findViewById(R.id.btn_chnagePinOK);
                txtChangePin = dialog.findViewById(R.id.txtv_changePin);
                txtChangePassword = dialog.findViewById(R.id.txt_change_password);
                txtChangePin.setText("Enter Old Password");
                final String carrierNumber = carrierPref.getString("CarrierNumber","");
                mFirestore.collection("Carriers").document(carrierNumber).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        cCarrier = documentSnapshot.toObject(Carrier.class);
                    }
                });
                btnChangePinOK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                           deCryptedPin = AESCrypt.decrypt(cCarrier.getPin());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        final String pwO = txtChangePassword.getText().toString();
                        if (deCryptedPin.equals(pwO)){
                            dialog.dismiss();
                            final Dialog dialog1 = new Dialog(MenuActivity.this);
                            dialog1.setContentView(R.layout.dialog_change_pin);
                            btnChangePinOK = dialog1.findViewById(R.id.btn_chnagePinOK);
                            txtChangePin = dialog1.findViewById(R.id.txtv_changePin);
                            txtChangePassword = dialog1.findViewById(R.id.txt_change_password);
                            txtChangePin.setText("Enter New Password");
                            btnChangePinOK.setText("Save");
                            dialog1.show();
                            btnChangePinOK.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    final String pwN = txtChangePassword.getText().toString();
                                    try {
                                        enCryptedPin = AESCrypt.encrypt(pwN);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    nCarrier = new Carrier(cCarrier.getWorkerNumber(),cCarrier.getName(),cCarrier.getEmail(),cCarrier.getPhone(),cCarrier.getCity(),cCarrier.getAddress(),cCarrier.getRoutePackage(),enCryptedPin);
                                    mFirestore.collection("Carriers").document(carrierNumber).set(nCarrier).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            dialog1.dismiss();
                                            toastMassage("Password has been changed");
                                        }
                                    });
                                }
                            });
                        }else {
                            toastMassage("Wrong Password!");
                        }
                    }
                });
                dialog.show();
            }
        });

    }
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    private void requestLocationFromManager(){
        getDeviceLocation();
        mRef.child("RequestLocation").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue(boolean.class)== true){
                    mRef.child("Carriers Today").child(carrierNumber).child("lat").setValue(cLat);
                    mRef.child("Carriers Today").child(carrierNumber).child("lng").setValue(clng);
                    mRef.child("RequestLocation").setValue(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getDeviceLocation(){
        mFusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);
            try {
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()){
                            Log.d(TAG, "onComplete: Found Location");
                            Location currentLocation = (Location)task.getResult();
                            cLat = currentLocation.getLatitude();
                            clng = currentLocation.getLongitude();
                        }else{
                            Log.d(TAG, "onComplete: current location is null");
                            toastMassage("Unable to track Location");
                        }
                    }
                });
        }catch(SecurityException e){
            Log.e(TAG, "getDeviceLocation: Security Exeption"+ e.getMessage());
        }
    }

    private void toastMassage(String massage){
        Toast.makeText(this,massage,Toast.LENGTH_SHORT).show();
    }

}
