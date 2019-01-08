package dao.daoapplication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import dao.daoapplication.Entities.Carrier;
import dao.daoapplication.MenuActivities.CarriersActivity;

public class AddCarrierActivity extends AppCompatActivity {

    private static final String TAG = "AddCarrierActivity";

    private Button btnSaveCarrier;
    private EditText txtWorkerNumber;
    private EditText txtName;
    private EditText txtPhone;
    private EditText txtEmail;
    private EditText txtCity;
    private EditText txtAddress;
    private EditText txtRoutePackage;
    private ImageView imgBack;

    private Carrier cCarrier;

    private FirebaseFirestore mFirestore;
    private String encryptedPin;
    private String carrierNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_carrier);

        btnSaveCarrier = findViewById(R.id.btn_saveEmplyee);
        txtWorkerNumber = findViewById(R.id.txt_addWorkerNumber);
        txtName = findViewById(R.id.txt_addWorkerName);
        txtPhone = findViewById(R.id.txt_addPhone);
        txtEmail = findViewById(R.id.txt_addEmail);
        txtCity = findViewById(R.id.txt_carrierCity);
        txtAddress = findViewById(R.id.txt_carrierAddress);
        txtRoutePackage = findViewById(R.id.txt_addCarrierRoutepackage);
        imgBack = findViewById(R.id.ic_back);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddCarrierActivity.this.finish();
            }
        });
        mFirestore = FirebaseFirestore.getInstance();
        final Intent i = getIntent();
        if(i.hasExtra("CarrierNumber")){
            carrierNumber = i.getStringExtra("CarrierNumber");
            mFirestore.collection("Carriers").document(carrierNumber).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    cCarrier = documentSnapshot.toObject(Carrier.class);
                    txtName.setText(cCarrier.getName());
                    txtWorkerNumber.setText(cCarrier.getWorkerNumber());
                    txtPhone.setText(cCarrier.getPhone());
                    txtEmail.setText(cCarrier.getEmail());
                    txtCity.setText(cCarrier.getCity());
                    txtAddress.setText(cCarrier.getAddress());
                    txtRoutePackage.setText(cCarrier.getRoutePackage());
                }
            });
        }
        btnSaveCarrier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String WorkerNumber = txtWorkerNumber.getText().toString();
                final String Name = txtName.getText().toString();
                String Phone = txtPhone.getText().toString();
                String Email = txtEmail.getText().toString();
                String City = txtCity.getText().toString();
                String Address = txtAddress.getText().toString();
                String RoutePAckage = txtRoutePackage.getText().toString();
                try {
                   encryptedPin = AESCrypt.encrypt("2105");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (!WorkerNumber.isEmpty()||!Phone.isEmpty()||!Email.isEmpty()||!RoutePAckage.isEmpty()||!City.isEmpty()||!Address.isEmpty()){
                    Carrier carrier = new Carrier(WorkerNumber,Name,Email,Phone,City,Address,RoutePAckage,encryptedPin);
                    mFirestore.collection("Carriers").document(WorkerNumber).set(carrier).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            toastMassage(Name +" Successfully added as a Carrier");
                            Intent i = new Intent(AddCarrierActivity.this,CarriersActivity.class);
                            startActivity(i);
                            AddCarrierActivity.this.finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            toastMassage("Something Went wrong! Check internet Connection");
                        }
                    });
                }
                else{
                    toastMassage("Fill all the Informations");
                }
            }
        });
    }
    private void toastMassage(String massage){
        Toast.makeText(this,massage,Toast.LENGTH_SHORT).show();
    }
}
