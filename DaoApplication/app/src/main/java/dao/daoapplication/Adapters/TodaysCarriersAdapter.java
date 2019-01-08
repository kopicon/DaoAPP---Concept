package dao.daoapplication.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import dao.daoapplication.DetailsActivities.CarrierDetailsActivity;
import dao.daoapplication.Entities.CarrierToday;
import dao.daoapplication.R;

public class TodaysCarriersAdapter extends ArrayAdapter<CarrierToday> {

    public Context mContecxt;

    private TextView name;
    private TextView Phone;
    private TextView routePackage;
    private ImageButton ibtnInfo;
    private ImageButton ibtnLocation;

    private double lat;
    private double lng;

    private LayoutInflater mInflater;
    private int layoutResource;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;

    private List<CarrierToday> mCarriers;


    public TodaysCarriersAdapter(@NonNull Context context, int resource, ArrayList<CarrierToday> carriers) {
        super(context, resource, carriers);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutResource = resource;
        this.mContecxt = context;
        this.mCarriers = carriers;
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference();
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null) {
            mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(layoutResource,null);
        }
        name = convertView.findViewById(R.id.txt_todaysCarrierListName);
        routePackage = convertView.findViewById(R.id.txt_todays_CarreiersRoutePackage);
        ibtnInfo = convertView.findViewById(R.id.ibtn_todays_carrierInfo);
        ibtnLocation = convertView.findViewById(R.id.ibtn_todays_carriers_location);
        mRef.child("RequestLocation").setValue(true);
        ibtnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(),CarrierDetailsActivity.class);
                i.putExtra("CarrierNumber",getItem(position).getWorkerNumber());
                getContext().startActivity(i);
            }
        });
        ibtnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRef.child("RequestLocation").setValue(true);
                final Dialog dialog = new Dialog(getContext());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                dialog.setContentView(R.layout.dialog_map);
                MapView mMapView = (MapView) dialog.findViewById(R.id.mapView);
                MapsInitializer.initialize(getContext());
                mMapView.onCreate(dialog.onSaveInstanceState());
                mMapView.onResume();
                mMapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(final GoogleMap googleMap) {
                        mRef.child("Carriers Today").child(getItem(position).getWorkerNumber()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            lat = dataSnapshot.child("lat").getValue(double.class);
                            lng = dataSnapshot.child("lng").getValue(double.class);
                            LatLng posisiabsen = new LatLng(lat,lng);
                            googleMap.addMarker(new MarkerOptions()
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_scooter_green))
                                    .position(posisiabsen)
                                    .title(getItem(position).getName()));
                            googleMap.moveCamera(CameraUpdateFactory.newLatLng(posisiabsen));
                            googleMap.getUiSettings().setZoomControlsEnabled(true);
                            googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
                            googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                                @Override
                                public void onInfoWindowClick(Marker marker) {

                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    }
                });

                dialog.show();
            }
        });
        String name_ = getItem(position).getName();
        String routePackage_ = getItem(position).getRoutePackage();

        name.setText(name_);
        routePackage.setText(routePackage_);

        return convertView;
    }
}
