package dao.daoapplication.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import dao.daoapplication.DetailsActivities.AddressDetailsActivity;
import dao.daoapplication.Entities.Addresses;
import dao.daoapplication.R;

public class AddressListArrayAdapter extends ArrayAdapter<Addresses> {

    public Context mContecxt;

    private TextView Address;
    private TextView txtpopupInfo;
    private ListView lvPopup;
    private TextView txtMassege;
    private ImageButton ibtOpenMaps;
    private LayoutInflater mInflater;
    private int layoutResource;
    private Button btnNewspapers;
    private PopupArrayAdapter mPopupAdapter;
    private String routePackage;
    private String route;

    private ArrayList<String> newsPapers = null;
    private List<Addresses> mAddresses;


public AddressListArrayAdapter(@NonNull Context context,int resource,List<Addresses> addresses,String routePackage,String route){
    super(context,resource,addresses);
    mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    layoutResource = resource;
    this.mContecxt = context;
    this.mAddresses = addresses;
    this.routePackage = routePackage;
    this.route = route;
}

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
    if(convertView == null) {
        mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = mInflater.inflate(layoutResource,null);
    }
    Address = convertView.findViewById(R.id.txt_addressInList);
    btnNewspapers = convertView.findViewById(R.id.btn_newspapersInLIst);
    txtMassege = convertView.findViewById(R.id.txt_massageInList);
    ibtOpenMaps = convertView.findViewById(R.id.ibtn_openMaps);

    int countn = getItem(position).getNewspapers().size();
    newsPapers = getItem(position).getNewspapers();
    String address_ = getItem(position).getAddress();
    String massage = getItem(position).getMassege();
    String Countn1 = Integer.toString(countn);
    Address.setText(address_);
    txtMassege.setText(massage);
    btnNewspapers.setText(Countn1);
    Address.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(getContext(),AddressDetailsActivity.class);
            i.putExtra("Address",getItem(position).getAddress());
            i.putExtra("RoutePackage",routePackage);
            i.putExtra("Route",route);
            getContext().startActivity(i);
        }
    });
    ibtOpenMaps.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(getContext(),AddressDetailsActivity.class);
            i.putExtra("Address",getItem(position).getAddress());
            i.putExtra("RoutePackage",routePackage);
            i.putExtra("Route",route);
            getContext().startActivity(i);
        }
    });
    btnNewspapers.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final Dialog dialog = new Dialog(getContext());
            dialog.setContentView(R.layout.dialog_listview);
            newsPapers = getItem(position).getNewspapers();
            lvPopup = (ListView) dialog.findViewById(R.id.lv_popup);
            txtpopupInfo = dialog.findViewById(R.id.txt_popupInfo);
            txtpopupInfo.setText(getItem(position).getAddress());
            mPopupAdapter = new PopupArrayAdapter(getContext(), R.layout.fragment_popup, newsPapers);
            lvPopup.setAdapter(mPopupAdapter);
            dialog.show();
        }
    });

    return convertView;
    }
}
