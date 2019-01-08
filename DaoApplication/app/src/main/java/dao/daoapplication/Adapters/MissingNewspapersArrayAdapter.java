package dao.daoapplication.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import dao.daoapplication.Entities.UndeliveredNewspaper;
import dao.daoapplication.R;

public class MissingNewspapersArrayAdapter extends ArrayAdapter<UndeliveredNewspaper> {
    public Context mContecxt;

    private TextView txtAddress;
    private TextView txtpopupInfo;
    private ListView lvPopup;
    private TextView txtRoute;
    private TextView delivered;
    private Switch sDelivered;
    private Button btnBack;
    private ImageView imgDelivered;
    private Button btnNewspapers;
    private ImageButton ibtOpenMaps;
    private LayoutInflater mInflater;
    private int layoutResource;

    private MissingPopupArrayAdapter mPopupAdapter;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;

    private String Route;
    private ArrayList<String> newsPapers = new ArrayList<>();
    private ArrayList<UndeliveredNewspaper> mAddresses = new ArrayList<>();

    public MissingNewspapersArrayAdapter(@NonNull Context context, int resource, ArrayList<UndeliveredNewspaper> addresses,String route) {
        super(context, resource,addresses);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutResource = resource;
        this.mContecxt = context;
        this.mAddresses = addresses;
        this.Route = route;
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference("Problems").child("MissingNewspapers");
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null) {
            mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(layoutResource,null);
        }
        txtAddress = convertView.findViewById(R.id.txt_missing_newspaper_list_address);
        txtRoute = convertView.findViewById(R.id.txt_missing_newspaper_list_route);
        ibtOpenMaps = convertView.findViewById(R.id.ibtn_missingNewspaper_location);
        btnNewspapers = convertView.findViewById(R.id.btn_missing_newspapers_list);
        delivered = convertView.findViewById(R.id.txt_missingnewpaper_delivered);
        imgDelivered = convertView.findViewById(R.id.img_missingNewspaper_delivered);
        txtAddress.setText(getItem(position).getAddress());
        txtRoute.setText(Route);
        if(getItem(position).isDelivered()){
            delivered.setText("Delivered");
            imgDelivered.setImageResource(R.drawable.ic_tick);
        }else if (!getItem(position).isDelivered()){
            delivered.setText("Undelivered");
            imgDelivered.setImageResource(R.drawable.ic_x);
        }
        int count = getItem(position).getNewspapers().size();
        String Count = Integer.toString(count);
        btnNewspapers.setText(Count);
        btnNewspapers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.dialog_missing_newspapers);
                newsPapers = getItem(position).getNewspapers();
                lvPopup = dialog.findViewById(R.id.lv_popup_missing_newspapers);
                txtpopupInfo = dialog.findViewById(R.id.txt_popup_missingNewspaper_address);
                btnBack = dialog.findViewById(R.id.btn_missingNewspapers_back);
                sDelivered = dialog.findViewById(R.id.s_missingNewspaper_delivered);
                txtpopupInfo.setText(getItem(position).getAddress());
                mPopupAdapter = new MissingPopupArrayAdapter(getContext(), R.layout.fragment_popup, newsPapers);
                lvPopup.setAdapter(mPopupAdapter);
                dialog.show();
                if(getItem(position).isDelivered()){
                    sDelivered.setChecked(true);
                    sDelivered.setText("Delivered");
                }else if(!getItem(position).isDelivered()){
                    sDelivered.setChecked(false);
                    sDelivered.setText("Undelivered");
                }
                btnBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                sDelivered.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked) {
                            sDelivered.setText("Delivered");
                            mRef.child(getItem(position).getAddress()).child("delivered").setValue(true);
                        }else if (!isChecked){
                            sDelivered.setText("Undelivered");
                            mRef.child(getItem(position).getAddress()).child("delivered").setValue(false);
                        }
                    }
                });
            }
        });
        ibtOpenMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                        LatLng posisiabsen = new LatLng(getItem(position).getLat(), getItem(position).getLng());
                        googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_home_red)).position(posisiabsen).title(getItem(position).getAddress()));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(posisiabsen));
                        googleMap.getUiSettings().setZoomControlsEnabled(true);
                        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
                    }
                });

                dialog.show();
            }
        });



        return convertView;
    }
}
