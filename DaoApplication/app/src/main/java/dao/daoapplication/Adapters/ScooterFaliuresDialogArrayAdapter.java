package dao.daoapplication.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import dao.daoapplication.R;

public class ScooterFaliuresDialogArrayAdapter extends ArrayAdapter<String> {

    public Context mContecxt;

    private TextView txtName;
    private ImageView imgFaliure;
    private LayoutInflater mInflater;
    private int layoutResource;
    private ArrayList<String> toChoose;

    public ScooterFaliuresDialogArrayAdapter(@NonNull Context context, int resource, @NonNull ArrayList<String> toChoose) {
        super(context, resource, toChoose);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutResource = resource;
        this.mContecxt = context;
        this.toChoose = toChoose;

    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null) {
            mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(layoutResource,null);
        }
        txtName = convertView.findViewById(R.id.txt_dialog_scooterFaliure_reason);
        imgFaliure = convertView.findViewById(R.id.ic_dialog_scooterFaliure_reason);
        if (getItem(position).equals("Low Battery Capacity")){
            imgFaliure.setImageResource(R.drawable.ic_battery_red);
        }else if(getItem(position).equals("Problem with the lights")){
            imgFaliure.setImageResource(R.drawable.ic_lights_red);
        }else if (getItem(position).equals("Steering or Break Problems")|| getItem(position).equals("Flat Tire")){
            imgFaliure.setImageResource(R.drawable.ic_scooter_red);
        }else if (getItem(position).equals("Problems with the Key")){
            imgFaliure.setImageResource(R.drawable.ic_key_red);
        }

            String name = getItem(position);
            txtName.setText(name);

        return convertView;
    }
}
