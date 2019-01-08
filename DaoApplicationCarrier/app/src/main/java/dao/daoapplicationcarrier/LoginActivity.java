    package dao.daoapplicationcarrier;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import dao.daoapplicationcarrier.Entities.Carrier;
import dao.daoapplicationcarrier.Entities.CarrierToday;
import dao.daoapplicationcarrier.Entities.RoutePackage;

import static dao.daoapplicationcarrier.MenuActivity.mLocationPermissionGranted;

    public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private TextView txtCarrierNumber;
    private TextView txtPin;
    private CheckBox chkboxRememberMe;
    private Button btnLogin;

    private SharedPreferences loginPref;

    private FirebaseFirestore mFirestore;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseReference;

    private Carrier cCarrier;
    private CarrierToday bCarrier;
    private RoutePackage cRoutepackge;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private double cLat = 0;
    private double clng = 0;

    private String deCryptedPin;
    private String carrierNumber;

    public static boolean mLocationPermissionGranted = false;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        txtCarrierNumber = findViewById(R.id.txt_carrierNumberLogin);
        txtPin = findViewById(R.id.txt_pinLogin);
        btnLogin = findViewById(R.id.btn_login);
        chkboxRememberMe = findViewById(R.id.chkbox_rememberMe);
        loginPref = getSharedPreferences("Carrier",MODE_PRIVATE);
        final SharedPreferences.Editor editor = loginPref.edit();
        final boolean rememberMe = loginPref.getBoolean("RememberMe",false);

        if(rememberMe){
            chkboxRememberMe.setChecked(true);
            carrierNumber = loginPref.getString("CarrierNumber","");
            txtCarrierNumber.setText(carrierNumber);
            txtPin.setSelected(true);
        }

        mFirestore = FirebaseFirestore.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference();



        getLocationPermission();
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String carrierNumber = txtCarrierNumber.getText().toString();
                final String Pin = txtPin.getText().toString();
                getDeviceLocation();
                mFirestore.collection("Carriers").document(carrierNumber).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        cCarrier = documentSnapshot.toObject(Carrier.class);
                        try {
                            deCryptedPin = AESCrypt.decrypt(cCarrier.getPin());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (deCryptedPin.equals(Pin)){
                            bCarrier = new CarrierToday(cCarrier.getWorkerNumber(), cCarrier.getName(), cCarrier.getRoutePackage(), cLat, clng);
                        mDatabaseReference.child("Carriers Today").child(carrierNumber).setValue(bCarrier);
                        cRoutepackge = new RoutePackage(cCarrier.getRoutePackage(), cCarrier.getWorkerNumber(), cCarrier.getName(), false,false);
                        mDatabaseReference.child("RouteStatuses").child(cCarrier.getRoutePackage()).setValue(cRoutepackge);
                        editor.putString("CarrierNumber",cCarrier.getWorkerNumber());
                        editor.putString("RoutePackage",cCarrier.getRoutePackage());
                        editor.putString("CarrierName",cCarrier.getName());
                        if(chkboxRememberMe.isChecked()) {
                            editor.putBoolean("RememberMe", true);
                        }else if (!chkboxRememberMe.isChecked()){
                            editor.putBoolean("RememberMe", false);
                        }
                        editor.apply();
                        Intent i = new Intent(LoginActivity.this, MenuActivity.class);
                        startActivity(i);
                        LoginActivity.this.finish();
                    }else{
                            toastMassage("Something is wrong, try again");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: "+e);
                        toastMassage("Something Went Wrong");
                    }
                });
            }
        });
    }
        private void getLocationPermission() {
            Log.d(TAG, "getLocationPermission: Getting LocationPermission");
            String[] permission = {android.Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION};

            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    getDeviceLocation();
                }else{
                    ActivityCompat.requestPermissions(this, permission, LOCATION_PERMISSION_REQUEST_CODE);
                }
            }else {
                ActivityCompat.requestPermissions(this, permission, LOCATION_PERMISSION_REQUEST_CODE);
            }

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
