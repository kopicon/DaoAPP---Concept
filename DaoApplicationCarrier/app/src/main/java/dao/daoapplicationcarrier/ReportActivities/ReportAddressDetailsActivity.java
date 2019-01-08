package dao.daoapplicationcarrier.ReportActivities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
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
import dao.daoapplicationcarrier.Entities.Addresses;
import dao.daoapplicationcarrier.Entities.UndeliveredNewspaper;
import dao.daoapplicationcarrier.MenuActivity;
import dao.daoapplicationcarrier.R;

public class ReportAddressDetailsActivity extends AppCompatActivity {

    private final static String TAG = "ReportAddressDetailsActivity";

    private Context context;

    private TextView txtPostCode;
    private TextView txtAddress;
    private ListView lvNewspapers;
    private Button btnSendReport;
    private Button btnClearList;
    private ImageView imgBack;

    private FirebaseDatabase mDatabase;
    private DatabaseReference addressRef;
    private Addresses cAddress;

    private ArrayList<String> newspapers = new ArrayList<>();
    private ArrayList<String> selectedNewspapers = new ArrayList<>();
    private PopupArrayAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_address_details);

        txtPostCode = findViewById(R.id.txt_detailsPostCode);
        txtAddress = findViewById(R.id.txt_detailsAddress);
        lvNewspapers = findViewById(R.id.lv_detailsNewspapers);
        btnSendReport = findViewById(R.id.btn_send_addressReport);
        btnClearList = findViewById(R.id.btn_report_clear_list);
        imgBack = findViewById(R.id.ic_back);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReportAddressDetailsActivity.this.finish();
            }
        });
        context = getApplicationContext();
        Intent i = getIntent();
        String address = i.getStringExtra("Address");
        String routePackage = i.getStringExtra("RoutePackage");
        final String route = i.getStringExtra("Route");
        mDatabase = FirebaseDatabase.getInstance();
        addressRef = mDatabase.getReference();
        addressRef.child("Routepackages").child(routePackage).child(route).child(address).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(getApplicationContext() == context) {
                    cAddress = dataSnapshot.getValue(Addresses.class);
                    txtPostCode.setText(cAddress.getPostCode());
                    txtAddress.setText(cAddress.getAddress());
                    newspapers = cAddress.getNewspapers();
                    mAdapter = new PopupArrayAdapter(getApplicationContext(),R.layout.fragment_popup,newspapers);
                    lvNewspapers.setAdapter(mAdapter);
                    lvNewspapers.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                            if(!view.isSelected()) {
                                view.setBackgroundColor(Color.RED);
                                view.setSelected(true);
                                selectedNewspapers.add(newspapers.get(position));
                            }
                            return true;
                        }
                    });
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                toastMassage("Check internet connection");
            }
        });
        btnClearList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNewspapers.clear();
                lvNewspapers.setAdapter(null);
                lvNewspapers.setAdapter(mAdapter);
            }
        });
        btnSendReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!selectedNewspapers.isEmpty()) {
                    UndeliveredNewspaper rAddress = new UndeliveredNewspaper(cAddress.getCity(), cAddress.getAddress(), selectedNewspapers, route, false,false, cAddress.getLat(), cAddress.getLng());
                    addressRef.child("Problems").child("MissingNewspapers").child(cAddress.getAddress()).setValue(rAddress).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            toastMassage("Report Has been Sent!");
                            ReportAddressDetailsActivity.this.finish();
                        }
                    });
                }else { toastMassage("Choose Newspaper with long click!");}
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
    private void toastMassage(String massage){
        Toast.makeText(this,massage,Toast.LENGTH_SHORT).show();
    }
}
