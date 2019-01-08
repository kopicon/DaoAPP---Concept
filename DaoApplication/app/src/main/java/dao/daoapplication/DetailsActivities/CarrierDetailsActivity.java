package dao.daoapplication.DetailsActivities;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import dao.daoapplication.AddCarrierActivity;
import dao.daoapplication.Entities.Carrier;
import dao.daoapplication.MenuActivities.CarriersActivity;
import dao.daoapplication.R;

public class CarrierDetailsActivity extends AppCompatActivity {

    private TextView txtWorkerNumber;
    private TextView txtName;
    private TextView txtRoutePackage;
    private TextView txtPhone;
    private TextView txtEmail;
    private TextView txtCity;
    private TextView txtAddress;

    private ImageView imgBack;

    private ImageButton ibtnCall;
    private ImageButton ibtnMassage;
    private ImageButton ibtnEmail;

    private Button btnEditDetails;
    private Button btnDeleteCarrier;

    private TextView txtRemoveWhat;
    private Button btnRemoveYes;
    private Button btnRemoveNo;

    private Carrier cCarrier;

    private FirebaseFirestore mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrier_details);
        txtName = findViewById(R.id.txt_carrierDetailsName);
        txtWorkerNumber = findViewById(R.id.txt_carrierDetailsNumber);
        txtRoutePackage = findViewById(R.id.txt_carrierDetailRP);
        txtPhone = findViewById(R.id.txt_carrierDetailsPhone);
        txtEmail = findViewById(R.id.txt_carrierDetailsemail);
        txtCity = findViewById(R.id.txt_carrierDetailsCity);
        txtAddress = findViewById(R.id.txt_carrierDetailAddress);
        btnEditDetails = findViewById(R.id.btn_edit_carrier);
        btnDeleteCarrier = findViewById(R.id.btn_delete_carrier);
        imgBack = findViewById(R.id.ic_back);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CarrierDetailsActivity.this.finish();
            }
        });

        ibtnCall = findViewById(R.id.ic_call);
        ibtnEmail = findViewById(R.id.ic_email);
        ibtnMassage = findViewById(R.id.ic_messeg);

        mDatabase = FirebaseFirestore.getInstance();

        Intent i = getIntent();
        final String workerNumber = i.getStringExtra("CarrierNumber");

        mDatabase.collection("Carriers").document(workerNumber).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                cCarrier = documentSnapshot.toObject(Carrier.class);
                txtName.setText(cCarrier.getName());
                txtWorkerNumber.setText(cCarrier.getWorkerNumber());
                txtRoutePackage.setText(cCarrier.getRoutePackage());
                txtPhone.setText(cCarrier.getPhone());
                txtEmail.setText(cCarrier.getEmail());
                txtCity.setText(cCarrier.getCity());
                txtAddress.setText(cCarrier.getAddress());

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
        btnEditDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CarrierDetailsActivity.this,AddCarrierActivity.class);
                i.putExtra("CarrierNumber",workerNumber);
                startActivity(i);
            }
        });
        btnDeleteCarrier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(CarrierDetailsActivity.this);
                dialog.setContentView(R.layout.dialog_are_you_sure);
                txtRemoveWhat = dialog.findViewById(R.id.txt_dialog_remove_what);
                btnRemoveNo = dialog.findViewById(R.id.btn_dialog_remove_no);
                btnRemoveYes = dialog.findViewById(R.id.btn_dialog_remove_yes);
                txtRemoveWhat.setText(cCarrier.getName()+" From the system");
                btnRemoveYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDatabase.collection("Carriers").document(workerNumber).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Intent i = new Intent(CarrierDetailsActivity.this,CarriersActivity.class);
                                CarrierDetailsActivity.this.finish();
                                startActivity(i);
                                toastMassage(cCarrier.getName()+" Successfully removed from the system");
                            }
                        });
                    }
                });
                btnRemoveNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            dialog.show();
            }
        });
        ibtnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCallIntent(cCarrier.getPhone());
            }
        });
        ibtnMassage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSMSIntent(cCarrier.getPhone());
            }
        });
        ibtnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startEmailIntent(cCarrier.getEmail());
            }
        });
    }
    private void startCallIntent(String phonNumeber){
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phonNumeber));
        startActivity(intent);
    }
    private void startSMSIntent(String phonNumeber){
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + phonNumeber));
        startActivity(intent);
    }
    private void startEmailIntent(String emailAddress){
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto",emailAddress, null));
        startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }
    private void toastMassage(String massage){
        Toast.makeText(this,massage,Toast.LENGTH_SHORT).show();
    }
}
