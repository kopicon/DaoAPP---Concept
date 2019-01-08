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
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import dao.daoapplication.Entities.Scooter;
import dao.daoapplication.R;

public class ScooterFailuresListAdapter extends ArrayAdapter<Scooter> {
    private Context mContext;

    private TextView txtCarrierName;
    private TextView txtScooterNumber;
    private ImageView icBattery;
    private ImageView icLights;
    private ImageView icScooter;
    private ImageView icKey;

    private LayoutInflater mInflater;
    private int layoutResource;

    private ArrayList<Scooter> scooters;
    private ArrayList<String> failures = new ArrayList<>();

    public ScooterFailuresListAdapter(@NonNull Context context, int resource, ArrayList<Scooter> scooters) {
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
        txtCarrierName = convertView.findViewById(R.id.txt_failures_carrierName);
        txtScooterNumber = convertView.findViewById(R.id.txt_failures_scooterNumber);
        icBattery = convertView.findViewById(R.id.ic_failures_1);
        icLights = convertView.findViewById(R.id.ic_failures_2);
        icScooter = convertView.findViewById(R.id.ic_failures_3);
        icKey = convertView.findViewById(R.id.ic_failures_5);
        icKey.setVisibility(View.INVISIBLE);
        icScooter.setVisibility(View.INVISIBLE);
        icLights.setVisibility(View.INVISIBLE);
        icBattery.setVisibility(View.INVISIBLE);
        if (getItem(position)!=null) {
            failures = getItem(position).getFaliures();
            for (String F : failures) {
                if (F.equals("Low Battery Capacity")) {
                    icBattery.setVisibility(View.VISIBLE);
                } else if (F.equals("Problem with the lights")) {
                    icLights.setVisibility(View.VISIBLE);
                } else if (F.equals("Steering or Break Problems") || F.equals("Flat Tire")) {
                    icScooter.setVisibility(View.VISIBLE);
                } else if (F.equals("Problems with the Key")) {
                    icKey.setVisibility(View.VISIBLE);
                }
            }
            txtCarrierName.setText(getItem(position).getCarrierName());
            txtScooterNumber.setText(getItem(position).getScooterNumber());
        }
        return convertView;
    }
}
