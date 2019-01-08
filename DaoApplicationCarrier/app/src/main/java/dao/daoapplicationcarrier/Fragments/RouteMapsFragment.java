package dao.daoapplicationcarrier.Fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import dao.daoapplicationcarrier.Adapters.PopupArrayAdapter;
import dao.daoapplicationcarrier.AddressDetailsActivity;
import dao.daoapplicationcarrier.Entities.Addresses;
import dao.daoapplicationcarrier.R;

import static android.content.Context.MODE_PRIVATE;


public class RouteMapsFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = "RouteMapsFragment";

    private TextView txtRoute;
    private ImageButton ibtnRoute;
    private ListView lvPopup;
    private TextView popupInfo;

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private Context cContext;
    private ArrayList<String> Routes = new ArrayList<>();
    private ArrayList<String> AllRoutes = new ArrayList<>();
    private ArrayList<String> RoutePackages = new ArrayList<>();
    private ArrayList<Addresses> routeAddresses = new ArrayList<>();
    private ArrayList<Addresses> filteredRouteAddresses = new ArrayList<>();
    private ArrayList<Addresses> AllAddresses = new ArrayList<>();
    private ArrayList<LatLng> allCordinates = new ArrayList<>();

    private PopupArrayAdapter mPopupAdapter;

    private FirebaseDatabase mDatabase;
    private DatabaseReference addressesRef;

    private boolean mLocationPermissionGranted;
    private static final float DEFAULT_ZOOM = 15;

    private SharedPreferences locationPref;
    private SharedPreferences.Editor editor;

    private Addresses cAddress;
    private SupportMapFragment mMapFrag;

    private SharedPreferences carrierPref;

    private String routePackage;
    private String carrierNumber;

    public RouteMapsFragment(){

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_route_map,container,false);
        ibtnRoute = view.findViewById(R.id.ibtn_MapRoutesList);
        txtRoute = view.findViewById(R.id.txt_MapSearchRoute);

        mDatabase = FirebaseDatabase.getInstance();
        addressesRef = mDatabase.getReference();
        cContext = getContext();

        carrierPref = getContext().getSharedPreferences("Carrier",MODE_PRIVATE);
        routePackage = carrierPref.getString("RoutePackage","");
        carrierNumber = carrierPref.getString("CarrierNumber","");

        locationPref = getContext().getSharedPreferences("Location",Context.MODE_PRIVATE);
        mLocationPermissionGranted = locationPref.getBoolean("LocationPermission",false);
        addressesRef.child("Routepackages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot DSRP) {
                for (DataSnapshot child:  DSRP.getChildren()){
                    String cRP = child.getKey();
                    RoutePackages.add(cRP);
                    addressesRef.child("RoutePackages").child(cRP).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot DSR) {
                            for (DataSnapshot child : DSR.getChildren()){
                                String cR = child.getKey();
                                AllRoutes.add(cR);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                for(String cRoute : AllRoutes){
                    addressesRef.child("RoutePackages").child(cRoute).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot DSA) {
                            for(DataSnapshot cA : DSA.getChildren()){
                                Addresses CA = cA.getValue(Addresses.class);
                                AllAddresses.add(CA);
                            }
                            moveCamera(new LatLng(55.475123, 8.460829),30);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                toastMassage("Check Internet Connection");
            }
        });


        ibtnRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.dialog_listview);
                dialog.setTitle("Choose Route");
                Routes.clear();
                lvPopup = (ListView) dialog.findViewById(R.id.lv_popup);
                popupInfo = dialog.findViewById(R.id.txt_popupInfo);
                popupInfo.setText("Choose Route");
                addressesRef.child("Routepackages").child(routePackage).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot DSRoutes) {
                        for (DataSnapshot child : DSRoutes.getChildren()) {
                            String cRoute = child.getKey();
                            Routes.add(cRoute);
                        }
                        if(getContext() == cContext)
                            lvPopup.setAdapter(mPopupAdapter);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                mPopupAdapter = new PopupArrayAdapter(getContext(), R.layout.fragment_popup, Routes);
                dialog.show();
                lvPopup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String cRoute = Routes.get(position);
                        txtRoute.setText(cRoute);
                        routeAddresses.clear();
                        filteredRouteAddresses.clear();
                        mMap.clear();
                        final ArrayList<Integer> ids = new ArrayList<>();
                        addressesRef.child("Routepackages").child(routePackage).child(txtRoute.getText().toString()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot DSAddresses) {
                                for(DataSnapshot child :DSAddresses.getChildren()){
                                    Addresses cAddress = child.getValue(Addresses.class);
                                    routeAddresses.add(cAddress);
                                    ids.add(cAddress.getId());
                                }
                                Collections.sort(ids);
                                for (int i = 1;i <= ids.size();i++) {
                                    for (Addresses cA: routeAddresses) {
                                        if(cA.getId()==i){
                                            filteredRouteAddresses.add(cA);
                                        }
                                    }
                                }
                                createMarkers(filteredRouteAddresses);
                                drawLineOnMap(filteredRouteAddresses);
                                cAddress = filteredRouteAddresses.get(0);
                                moveCamera(new LatLng(cAddress.getLat(),cAddress.getLng()),DEFAULT_ZOOM);

                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        dialog.dismiss();
                    }
                });
            }
        });
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        Log.d("MyMap", "onResume");
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {

        if (mMap == null) {

            Log.d("MyMap", "setUpMapIfNeeded");

            mMapFrag = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_route);
            mMapFrag.getMapAsync(this);
        }
    }
    private void drawLineOnMap(ArrayList<Addresses> addresses){
        allCordinates.clear();
    for(Addresses cA:addresses){
        LatLng latLng = new LatLng(cA.getLat(),cA.getLng());
        allCordinates.add(latLng);
    }
    mMap.addPolyline(new PolylineOptions()
            .addAll(allCordinates)
            .width(5)
            .color(Color.RED));
}
    private void createMarkers(ArrayList<Addresses> addresses){
        for(Addresses cA :addresses){
            double cLAt = cA.getLat();
            double cLng = cA.getLng();
            mMap.addMarker(new MarkerOptions().position(new LatLng(cLAt,cLng))
                    .title(cA.getAddress())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_home)))
                    .setTag(cA.getAddress());
        }
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent i = new Intent(getContext(),AddressDetailsActivity.class);
                i.putExtra("Address",marker.getTitle());
                i.putExtra("RoutePackage",routePackage);
                i.putExtra("Route",txtRoute.getText().toString());
                startActivity(i);
            }
        });
    }
    private void getDeviceLocation(){
        mFusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(getContext());
        try {
            if (mLocationPermissionGranted){
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()){
                            Log.d(TAG, "onComplete: Found Location");
                            Location currentLocation = (Location)task.getResult();
                            moveCamera(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()),DEFAULT_ZOOM);
                        }else{
                            Log.d(TAG, "onComplete: current location is null");
                            toastMassage("Unable to track Location");
                        }
                    }
                });
            }
        }catch(SecurityException e){
            Log.e(TAG, "getDeviceLocation: Security Exeption"+ e.getMessage());
        }

    }

    private void toastMassage(String massage){
        Toast.makeText(getContext(),massage,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("MyMap", "Map is ready");
        mMap =  googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        getDeviceLocation();
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED
                &&ActivityCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_COARSE_LOCATION) !=PackageManager.PERMISSION_GRANTED){
            return;
        }

        mMap.setMyLocationEnabled(true);
        try {

        } catch (Exception e) {
            Log.e(TAG, "onMapReady: error ", e);
        }
    }
    private void moveCamera(LatLng latLng, float zoom){
        Log.d(TAG, "moveCamera: moving the camera to lat:"+latLng.latitude+"lng:"+latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));
    }
}

