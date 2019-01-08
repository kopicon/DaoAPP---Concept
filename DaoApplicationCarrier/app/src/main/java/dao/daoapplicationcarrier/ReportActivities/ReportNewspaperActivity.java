package dao.daoapplicationcarrier.ReportActivities;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import dao.daoapplicationcarrier.Adapters.PopupArrayAdapter;
import dao.daoapplicationcarrier.Adapters.ReportAddressArrayAdapter;
import dao.daoapplicationcarrier.Entities.Addresses;
import dao.daoapplicationcarrier.R;

public class ReportNewspaperActivity extends AppCompatActivity {

    private ListView lvAddresses;
    private ListView lvPopup;
    private ImageButton ibtnRouteList;
    private TextView txtRoute;
    private TextView popupInfo;
    private ImageView imgBack;

    private PopupArrayAdapter mPopupAdapter;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;

    private SharedPreferences carrierPref;
    private String routePackage;
    private ArrayList<String> routes = new ArrayList<>();
    private ArrayList<Addresses> addresses = new ArrayList<>();
    private ArrayList<Addresses> filteredAddresses = new ArrayList<>();


    private ReportAddressArrayAdapter mAddressAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_newspaper);
        lvAddresses = findViewById(R.id.lv_report_addressList);
        ibtnRouteList = findViewById(R.id.ibtn_report_route_list);
        txtRoute = findViewById(R.id.txt_report_newspaperRoute);
        imgBack = findViewById(R.id.ic_back);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReportNewspaperActivity.this.finish();
            }
        });
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference();

        carrierPref = getSharedPreferences("Carrier",MODE_PRIVATE);
        routePackage = carrierPref.getString("RoutePackage","");

        mRef.child("Routepackages").child(routePackage).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(routes != null){
                    routes.clear();
                }
                for (DataSnapshot dsRoutes : dataSnapshot.getChildren()){
                    routes.add(dsRoutes.getKey());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        ibtnRouteList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dRoute = new Dialog(ReportNewspaperActivity.this);
                dRoute.setContentView(R.layout.dialog_listview);
                lvPopup = dRoute.findViewById(R.id.lv_popup);
                popupInfo = dRoute.findViewById(R.id.txt_popupInfo);
                popupInfo.setText("Choose Route");
                mPopupAdapter = new PopupArrayAdapter(ReportNewspaperActivity.this,R.layout.fragment_popup,routes);
                lvPopup.setAdapter(mPopupAdapter);
                dRoute.show();
                lvPopup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String cRoute = routes.get(position);
                        txtRoute.setText(cRoute);
                        addresses.clear();
                        filteredAddresses.clear();
                        final ArrayList<Integer> ids = new ArrayList<>();
                        mRef.child("Routepackages").child(routePackage).child(cRoute).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot DSAddresses) {
                                for(DataSnapshot child :DSAddresses.getChildren()){
                                    Addresses cAddress = child.getValue(Addresses.class);
                                    addresses.add(cAddress);
                                    ids.add(cAddress.getId());
                                }
                                Collections.sort(ids);
                                for (int i = 1;i <= ids.size();i++) {
                                    for (Addresses cA: addresses) {
                                        if(cA.getId()==i){
                                            filteredAddresses.add(cA);
                                        }
                                    }
                                }
                                lvAddresses.setAdapter(mAddressAdapter);
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        mAddressAdapter = new ReportAddressArrayAdapter(ReportNewspaperActivity.this,R.layout.fragment_report_address_list_item,filteredAddresses);
                        dRoute.dismiss();

                    }
                });
                lvAddresses.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent i = new Intent(ReportNewspaperActivity.this,ReportAddressDetailsActivity.class);
                        i.putExtra("RoutePackage",routePackage);
                        i.putExtra("Route",txtRoute.getText().toString());
                        i.putExtra("Address",filteredAddresses.get(position).getAddress());
                        startActivity(i);
                        ReportNewspaperActivity.this.finish();
                    }
                });
            }
        });

    }
}
