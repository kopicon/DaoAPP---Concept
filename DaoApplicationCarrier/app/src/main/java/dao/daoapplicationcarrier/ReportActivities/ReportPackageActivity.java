package dao.daoapplicationcarrier.ReportActivities;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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

import dao.daoapplicationcarrier.Adapters.PopupArrayAdapter;
import dao.daoapplicationcarrier.Entities.Package;
import dao.daoapplicationcarrier.R;

public class ReportPackageActivity extends AppCompatActivity {

    private EditText txtPackageNumber;
    private ImageButton ibtnRoutes;
    private CheckBox chckMissingPackage;
    private CheckBox chckNoKey;
    private CheckBox chckTooBig;
    private TextView txtRoute;
    private Button btnReportPackage;

    private ListView lvPopup;
    private TextView popupInfo;

    private PopupArrayAdapter mPopupAdapter;

    private ImageView imgBack;

    private SharedPreferences carrierPref;
    private String routePackage;
    private String carrierName;

    private ArrayList<String> routes = new ArrayList<>();
    private ArrayList<String> reasons = new ArrayList<>();

    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private int Reasons = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_package);
        txtPackageNumber = findViewById(R.id.txt_report_package);
        txtRoute = findViewById(R.id.txt_report_packageRoute);
        chckMissingPackage = findViewById(R.id.chck_MissingPackage);
        chckNoKey = findViewById(R.id.chck_NoKey);
        chckTooBig = findViewById(R.id.chckTooBig);
        ibtnRoutes = findViewById(R.id.ibtn_report_PackageRoutes);
        btnReportPackage = findViewById(R.id.btn_reportPackage);
        imgBack = findViewById(R.id.ic_back);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReportPackageActivity.this.finish();
            }
        });
        carrierPref = getSharedPreferences("Carrier",MODE_PRIVATE);
        routePackage = carrierPref.getString("RoutePackage","");
        carrierName = carrierPref.getString("CarrierName","");


        chckMissingPackage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    chckNoKey.setEnabled(false);
                    chckTooBig.setEnabled(false);
                }if(!isChecked){
                    chckNoKey.setEnabled(true);
                    chckTooBig.setEnabled(true);
                }
            }
        });

        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference();
        mRef.child("Routepackages").child(routePackage).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot cR : dataSnapshot.getChildren()){
                    routes.add(cR.getKey());
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                toastMassage("Check Internet Connenction");
            }
        });
        ibtnRoutes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dRoute = new Dialog(ReportPackageActivity.this);
                dRoute.setContentView(R.layout.dialog_listview);
                lvPopup = dRoute.findViewById(R.id.lv_popup);
                popupInfo = dRoute.findViewById(R.id.txt_popupInfo);
                popupInfo.setText("Choose Route");
                mPopupAdapter = new PopupArrayAdapter(ReportPackageActivity.this,R.layout.fragment_popup,routes);
                lvPopup.setAdapter(mPopupAdapter);
                dRoute.show();
                lvPopup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String cRoute = routes.get(position);
                        txtRoute.setText(cRoute);
                        dRoute.dismiss();

                    }
                });
            }
        });
        btnReportPackage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chckMissingPackage.isChecked()){
                    reasons.add("Missing Package");
                    Reasons++;
                }
                if(chckNoKey.isChecked()){
                    reasons.add("No Key");
                    Reasons++;
                }
                if(chckTooBig.isChecked()){
                    reasons.add("Too big");
                    Reasons++;
                }
                if(!txtPackageNumber.getText().toString().isEmpty() && !txtRoute.getText().toString().equals("Route")&& Reasons>0) {
                    Package cPackage = new Package(carrierName, routePackage, txtRoute.getText().toString(), txtPackageNumber.getText().toString(), reasons, false,false);
                    mRef.child("Problems").child("Package").child(txtPackageNumber.getText().toString()).setValue(cPackage).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            toastMassage("Report has been sent");
                            ReportPackageActivity.this.finish();
                        }
                    });
                }else {
                    toastMassage("Fill all Information!");
                }
            }
        });

    }
    private void toastMassage(String massage){
        Toast.makeText(this,massage,Toast.LENGTH_SHORT).show();
    }
}
