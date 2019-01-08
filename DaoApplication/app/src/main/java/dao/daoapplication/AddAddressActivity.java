package dao.daoapplication;


import android.app.Dialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dao.daoapplication.Adapters.PopupArrayAdapter;
import dao.daoapplication.Entities.Addresses;
import dao.daoapplication.MenuActivities.RouteListMapActivity;
import dao.daoapplication.MenuActivities.RouteStatusActivity;

public class AddAddressActivity extends AppCompatActivity {

    private static final String TAG = "AddAddressActivity";

    private EditText txtRoutePackage;
    private EditText txtRoute;
    private EditText txtCity;
    private EditText txtAddress;
    private ListView lvNewspapers;
    private TextView txtId;
    private EditText txtMassege;
    private ImageButton ibtnCities;
    private ImageButton ibtnRoutePackages;
    private ImageButton ibtnRoutes;
    private ImageButton ibtnnewRP;
    private ImageButton ibtnnewR;
    private Button btnAdd;
    private Button btnSave;
    private TextView txtpopupInfo;

    private ImageView imgBack;

    private ListView lvPopup;
    private PopupArrayAdapter mPopupAdapter;

    private ArrayList<String> AllNewspapers = new ArrayList<>();
    private ArrayList<String> Newspapers = new ArrayList<>();
    private ArrayList<String> Routes = new ArrayList<>();
    private ArrayList<String> RoutePackages = new ArrayList<>();
    private ArrayList<String> Cities = new ArrayList<>();
    private ArrayList<Addresses> addresses = new ArrayList<>();

    private double lat = 0;
    private double lng = 0;
    private String PostCode;

    private FirebaseFirestore mFirestore;
    private FirebaseDatabase mDatabase;

    private DatabaseReference addressesRef;
    private DatabaseReference newspapersRef;
    private DatabaseReference citiesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);

        txtRoutePackage = findViewById(R.id.txt_addRoutePackageNumber);
        txtRoute = findViewById(R.id.txt_addRouteNumber);
        txtCity = findViewById(R.id.txt_addCity);
        txtAddress = findViewById(R.id.txt_addAddress);
        lvNewspapers = findViewById(R.id.lv_addNewspapers);
        btnAdd = findViewById(R.id.btn_addNewspaper);
        btnSave = findViewById(R.id.btn_saveAddress);
        txtId = findViewById(R.id.txt_id);
        txtMassege = findViewById(R.id.txt_massege);
        ibtnRoutePackages = findViewById(R.id.ibtn_routepackageListAdd);
        ibtnRoutes = findViewById(R.id.ibtn_routeListAdd);
        ibtnCities = findViewById(R.id.ibtn_cityList);
        imgBack = findViewById(R.id.ic_back);
        ibtnnewRP = findViewById(R.id.ibtn_newRP);
        ibtnnewR = findViewById(R.id.ibtn_newR);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddAddressActivity.this.finish();
            }
        });

        txtRoutePackage.setEnabled(false);
        txtRoute.setEnabled(false);
        ibtnnewRP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtRoutePackage.setEnabled(true);
                txtRoute.setEnabled(true);
            }
        });
        ibtnnewR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtRoute.setEnabled(true);
            }
        });
        ibtnRoutes.setEnabled(false);
        txtId.setText("1");
        mFirestore = FirebaseFirestore.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        addressesRef = mDatabase.getReference();
        citiesRef = mDatabase.getReference("Cities");
        newspapersRef = mDatabase.getReference("Newspaper");

        newspapersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot DSNewspapers) {
                for (DataSnapshot child:  DSNewspapers.getChildren()){
                    String cNewspaper = child.getValue(String.class);
                    AllNewspapers.add(cNewspaper);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                toastMassage("Check Internet Connection");
            }
        });
        citiesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot DSCities) {
                for (DataSnapshot child:  DSCities.getChildren()){
                    String cCity = child.getValue(String.class);
                    Cities.add(cCity);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        addressesRef.child("Routepackages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot DSRP) {
                for (DataSnapshot child:  DSRP.getChildren()){
                    String cRP = child.getKey();
                    RoutePackages.add(cRP);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                toastMassage("Check Internet Connection");
            }
        });


        ibtnRoutePackages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Routes.clear();
                final Dialog dialog = new Dialog(AddAddressActivity.this);
                dialog.setContentView(R.layout.dialog_listview);
                dialog.setTitle("Choose RoutePackage");
                lvPopup = (ListView) dialog.findViewById(R.id.lv_popup);
                txtpopupInfo = dialog.findViewById(R.id.txt_popupInfo);
                mPopupAdapter = new PopupArrayAdapter(AddAddressActivity.this, R.layout.fragment_popup, RoutePackages);
                lvPopup.setAdapter(mPopupAdapter);
                txtpopupInfo.setText("Choose Routepackage");
                dialog.show();

                lvPopup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        final String cRP = RoutePackages.get(position);
                        addressesRef.child("Routepackages").child(cRP).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot DSRoutes) {
                                for (DataSnapshot child : DSRoutes.getChildren()) {
                                    String cRoute = child.getKey();
                                    Routes.add(cRoute);
                                    }
                                    ibtnRoutes.setEnabled(true);
                                txtRoutePackage.setText(cRP);
                                dialog.dismiss();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    toastMassage("Check Internet Connection");
                                }
                                });

                        }
                        });
                }
                });
        ibtnRoutes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(AddAddressActivity.this);
                dialog.setContentView(R.layout.dialog_listview);
                dialog.setTitle("Choose Route");
                lvPopup = (ListView) dialog.findViewById(R.id.lv_popup);
                txtpopupInfo = dialog.findViewById(R.id.txt_popupInfo);
                mPopupAdapter = new PopupArrayAdapter(AddAddressActivity.this, R.layout.fragment_popup, Routes);
                lvPopup.setAdapter(mPopupAdapter);
                txtpopupInfo.setText("Choose Route");
                dialog.show();
                lvPopup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String cRoute = Routes.get(position);
                        addressesRef.child("Routepackages").child(txtRoutePackage.getText().toString()).child(cRoute).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                addresses.clear();
                                for (DataSnapshot cA :dataSnapshot.getChildren()){
                                    Addresses cAddress = cA.getValue(Addresses.class);
                                    addresses.add(cAddress);
                                }
                                int count = addresses.size()+1;
                                String Count = Integer.toString(count);
                                txtId.setText(Count);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        txtRoute.setText(cRoute);
                        lvPopup.setAdapter(null);
                        dialog.dismiss();
                    }
                });
            }
        });
        ibtnCities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(AddAddressActivity.this);
                dialog.setContentView(R.layout.dialog_listview);
                lvPopup = (ListView) dialog.findViewById(R.id.lv_popup);
                txtpopupInfo = dialog.findViewById(R.id.txt_popupInfo);
                mPopupAdapter = new PopupArrayAdapter(AddAddressActivity.this, R.layout.fragment_popup, Cities);
                lvPopup.setAdapter(mPopupAdapter);
                txtpopupInfo.setText("Choose City");
                dialog.show();
                lvPopup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String cCity = Cities.get(position);
                        txtCity.setText(cCity);
                        lvPopup.setAdapter(null);
                        dialog.dismiss();
                    }
                });
            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(AddAddressActivity.this);
                dialog.setContentView(R.layout.dialog_listview);
                dialog.setTitle("Choose Newspaper");
                lvPopup = (ListView) dialog.findViewById(R.id.lv_popup);
                txtpopupInfo = dialog.findViewById(R.id.txt_popupInfo);
                mPopupAdapter = new PopupArrayAdapter(AddAddressActivity.this, R.layout.fragment_popup, AllNewspapers);
                lvPopup.setAdapter(mPopupAdapter);
                txtpopupInfo.setText("Choose Newspaper");
                dialog.show();
                lvPopup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String aNewspaper = AllNewspapers.get(position);
                        Newspapers.add(aNewspaper);
                        mPopupAdapter = new PopupArrayAdapter(AddAddressActivity.this, R.layout.fragment_popup_newspapers, Newspapers);
                        lvNewspapers.setAdapter(mPopupAdapter);
                        lvPopup.setAdapter(null);
                        dialog.dismiss();
                    }
                });
            }
        });

                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String RoutePackage = txtRoutePackage.getText().toString();
                        String Route = txtRoute.getText().toString();
                        String City = txtCity.getText().toString();
                        final String Address = txtAddress.getText().toString();
                        String id = txtId.getText().toString();
                        String massege = txtMassege.getText().toString();
                        Integer Id = Integer.valueOf(id);
                        geoLocate();

                        if (!RoutePackage.isEmpty() && !Route.isEmpty() && !City.isEmpty() && !Address.isEmpty() && !id.isEmpty() && lat != 0 && lng != 0 && Newspapers.size()>0) {
                            Addresses cAddress = new Addresses(PostCode, City, Address, Newspapers, lat, lng, massege, Id);
                            addressesRef.child("Routepackages").child(RoutePackage).child(Route).child(Address).setValue(cAddress);
                            Intent i = new Intent(AddAddressActivity.this,RouteListMapActivity.class);
                            startActivity(i);
                            AddAddressActivity.this.finish();

                        } else {
                            toastMassage("Fill all the fields");
                        }
                    }
                });

            }
    public void geoLocate() {
        Log.d(TAG, "geoLocate: geoLocating");
        String searchString = (txtCity.getText().toString()+" "+txtAddress.getText().toString());
        Geocoder geocoder = new Geocoder(this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchString, 1);
        } catch (IOException e) {
            Log.e(TAG, "geoLocate: IOException" + e.getMessage());
        }
        if (list.size() == 1) {
            android.location.Address address = list.get(0);
            Log.d(TAG, "geoLocate: Found a location " + address.toString());

             lat = address.getLatitude();
             lng = address.getLongitude();
             PostCode = address.getPostalCode();
            }
        }

    private void toastMassage(String massage){
        Toast.makeText(this,massage,Toast.LENGTH_SHORT).show();
    }
}


