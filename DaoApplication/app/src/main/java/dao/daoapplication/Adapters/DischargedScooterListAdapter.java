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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import dao.daoapplication.Entities.DischargedScooter;
import dao.daoapplication.R;

public class DischargedScooterListAdapter extends ArrayAdapter<DischargedScooter> {
    private Context mContext;

    private TextView txtCarrierName;
    private TextView txtRoutePackageNumber;
    private TextView txtFinished;
    private ImageView imgfinished;
    private ImageButton ibtnLocation;

    private LayoutInflater mInflater;
    private int layoutResource;

    private ArrayList<DischargedScooter> scooters = new ArrayList<>();

    public DischargedScooterListAdapter(@NonNull Context context, int resource, ArrayList<DischargedScooter> scooters) {
        super(context, resource, scooters);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutResource = resource;
        this.mContext = context;
        this.scooters = scooters;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null) {
            mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(layoutResource,null);
        }
        txtCarrierName = convertView.findViewById(R.id.txt_scooter_list_carrierName);
        txtRoutePackageNumber = convertView.findViewById(R.id.txt_scooter_list_routePackage);
        txtFinished = convertView.findViewById(R.id.txt_scooter_finished);
        imgfinished = convertView.findViewById(R.id.img_scooter_finished_list);
        ibtnLocation = convertView.findViewById(R.id.ibtn_scooter_location);
        txtCarrierName.setText(getItem(position).getCarrierName());
        txtRoutePackageNumber.setText(getItem(position).getRoutePackage());
        if(getItem(position).isFinished()){
            txtFinished.setText("Finished");
            imgfinished.setImageResource(R.drawable.ic_tick);
        }else if(!getItem(position).isFinished()){
            txtFinished.setText("Not Finished");
            imgfinished.setImageResource(R.drawable.ic_x);
        }
        ibtnLocation.setOnClickListener(new View.OnClickListener() {
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
                        googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_scooter_red)).position(posisiabsen).title(getItem(position).getCarrierName()));
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
