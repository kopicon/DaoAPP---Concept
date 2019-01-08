package dao.daoapplicationcarrier.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import dao.daoapplicationcarrier.Entities.Addresses;
import dao.daoapplicationcarrier.R;

public class ReportAddressArrayAdapter extends ArrayAdapter<Addresses> {

    public Context mContecxt;

    private TextView txtAddress;
    private TextView txtNewspapers;
    private LayoutInflater mInflater;
    private int layoutResource;

    private List<Addresses> mAddresses = null;
    private ArrayList<String> newsPapers = null;

    private String route;
    public ReportAddressArrayAdapter(@NonNull Context context, int resource, ArrayList<Addresses> addresses) {
        super(context, resource,addresses);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutResource = resource;
        this.mContecxt = context;
        this.mAddresses = addresses;
        this.route = route;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null) {
            mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(layoutResource,null);
        }
        txtAddress = convertView.findViewById(R.id.txt_report_item_address);
        txtNewspapers = convertView.findViewById(R.id.txt_report_item_count);
        int countn = getItem(position).getNewspapers().size();
        String Address = getItem(position).getAddress();
        String Countn1 = Integer.toString(countn);
        txtAddress.setText(Address);
        txtNewspapers.setText(Countn1);

        return convertView;
    }
}
