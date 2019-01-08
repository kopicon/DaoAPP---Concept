package dao.daoapplication.DetailsActivities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import dao.daoapplication.Adapters.PopupArrayAdapter;
import dao.daoapplication.Entities.Addresses;
import dao.daoapplication.MenuActivities.CarriersActivity;
import dao.daoapplication.R;

public class AddressDetailsActivity extends AppCompatActivity {

    private final static String TAG = "AddressDetailsActivity";

    private Context context;

    private TextView txtPostCode;
    private TextView txtAddress;
    private ListView lvNewspapers;
    private Button btnSeeOnMap;
    private ImageView imgBack;

    private FirebaseDatabase mDatabase;
    private DatabaseReference addressRef;
    private Addresses cAddress;

    private ArrayList<String> newspapers;
    private PopupArrayAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_details);

        txtPostCode = findViewById(R.id.txt_detailsPostCode);
        txtAddress = findViewById(R.id.txt_detailsAddress);
        lvNewspapers = findViewById(R.id.lv_detailsNewspapers);
        btnSeeOnMap = findViewById(R.id.btn_seeOnMap);

        imgBack = findViewById(R.id.ic_back);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddressDetailsActivity.this.finish();
            }
        });
        context = getApplicationContext();
        Intent i = getIntent();
        String address = i.getStringExtra("Address");
        String routePackage = i.getStringExtra("RoutePackage");
        String route = i.getStringExtra("Route");
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
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        btnSeeOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(AddressDetailsActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                dialog.setContentView(R.layout.dialog_map);
                MapView mMapView = (MapView) dialog.findViewById(R.id.mapView);
                MapsInitializer.initialize(AddressDetailsActivity.this);
                mMapView.onCreate(dialog.onSaveInstanceState());
                mMapView.onResume();
                mMapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(final GoogleMap googleMap) {
                        LatLng posisiabsen = new LatLng(cAddress.getLat(),cAddress.getLng());
                        googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_home)).position(posisiabsen).title(cAddress.getAddress()));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(posisiabsen));
                        googleMap.getUiSettings().setZoomControlsEnabled(true);
                        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
                    }
                });
                dialog.show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
