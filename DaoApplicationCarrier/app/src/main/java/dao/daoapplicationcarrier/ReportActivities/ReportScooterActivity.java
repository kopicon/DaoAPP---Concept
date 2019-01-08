package dao.daoapplicationcarrier.ReportActivities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import dao.daoapplicationcarrier.Entities.Scooter;
import dao.daoapplicationcarrier.MenuActivity;
import dao.daoapplicationcarrier.R;

public class ReportScooterActivity extends AppCompatActivity {

    private EditText txtScooterNumber;
    private CheckBox chckLights;
    private CheckBox chckBattery;
    private CheckBox chckBreakSteernig;
    private CheckBox chckTire;
    private CheckBox chckKey;
    private Button btnReportScooter;
    private int reasons;
    private ImageView imgBack;

    private String carrierName;
    private ArrayList<String> failures = new ArrayList<>();

    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;

    private SharedPreferences carrierPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_scooter);

        txtScooterNumber = findViewById(R.id.txt_report_scooterNumber);
        chckLights = findViewById(R.id.chck_reportLights);
        chckBattery = findViewById(R.id.chck_report_lowBattery);
        chckBreakSteernig = findViewById(R.id.chck_reportBreakSteering);
        chckTire = findViewById(R.id.chck_reportFlatTire);
        chckKey = findViewById(R.id.chck_key);
        btnReportScooter = findViewById(R.id.btn_reportScooter);

        carrierPref = getSharedPreferences("Carrier",MODE_PRIVATE);
        carrierName = carrierPref.getString("CarrierName","");
        imgBack = findViewById(R.id.ic_back);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReportScooterActivity.this.finish();
            }
        });
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference();
        btnReportScooter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chckLights.isChecked()){
                    failures.add("Problem with the lights");
                    reasons++;
                }
                if(chckBattery.isChecked()){
                    failures.add("Low Battery Capacity");
                    reasons++;
                }
                if(chckBreakSteernig.isChecked()){
                    failures.add("Steering or Break Problems");
                    reasons++;
                }
                if(chckTire.isChecked()){
                    failures.add("Flat Tire");
                    reasons++;
                }
                if(chckKey.isChecked()){
                    failures.add("Problems with the Key");
                    reasons++;
                }
                if(!txtScooterNumber.getText().toString().isEmpty()&& reasons>0) {
                    Scooter cScooter = new Scooter(carrierName,txtScooterNumber.getText().toString(), failures,false);
                    mRef.child("Problems").child("Scooter").child(txtScooterNumber.getText().toString()).setValue(cScooter).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            toastMassage("Report Has been sent, thank you");
                            ReportScooterActivity.this.finish();
                        }
                    });
                }
                else {toastMassage("Fill the number of the scooter and at least one problem");}
            }
        });
    }
    private void toastMassage(String massage){
        Toast.makeText(this,massage,Toast.LENGTH_SHORT).show();
    }
}
