package dao.daoapplication.Adapters;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import dao.daoapplication.DetailsActivities.CarrierDetailsActivity;
import dao.daoapplication.Entities.Carrier;
import dao.daoapplication.MenuActivities.CarriersActivity;
import dao.daoapplication.R;


public class CarriersListAdapter extends ArrayAdapter<Carrier> {

    public Context mContecxt;

    private TextView name;
    private TextView Phone;
    private TextView routePackage;
    private ImageButton ibtnCall;
    private ImageButton ibtnMassage;
    private ImageButton ibtnCarrierInfo;
    private LayoutInflater mInflater;
    private int layoutResource;

    private List<Carrier> mCarriers;

    public CarriersListAdapter(@NonNull Context context, int resource, List<Carrier> carriers) {
        super(context,resource,carriers);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutResource = resource;
        this.mContecxt = context;
        this.mCarriers = carriers;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if(convertView == null) {
            mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(layoutResource,null);
        }
        name = convertView.findViewById(R.id.txt_carrierDetailsName);
        Phone = convertView.findViewById(R.id.txt_Phone);
        routePackage = convertView.findViewById(R.id.txt_CarreiersRoutePackage);
        ibtnCarrierInfo = convertView.findViewById(R.id.ibtn_carrierInfo);
        ibtnCall = convertView.findViewById(R.id.ibtn_carrierCall);
        ibtnMassage = convertView.findViewById(R.id.ibtn_carrierMassege);
        ibtnCarrierInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(),CarrierDetailsActivity.class);
                i.putExtra("CarrierNumber",getItem(position).getWorkerNumber());
                getContext().startActivity(i);
            }
        });
        ibtnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCallIntent(getItem(position).getPhone());
            }
        });
        ibtnMassage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSMSIntent(getItem(position).getPhone());
            }
        });
        String name_ = getItem(position).getName();
        String phonr_ = getItem(position).getPhone();
        String routePackage_ = getItem(position).getRoutePackage();

        name.setText(name_);
        Phone.setText(phonr_);
        routePackage.setText(routePackage_);
        return convertView;
    }
    private void startCallIntent(String phonNumeber){
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phonNumeber));
        getContext().startActivity(intent);
    }
    private void startSMSIntent(String phonNumeber){
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + phonNumeber));
        getContext().startActivity(intent);
    }

}
