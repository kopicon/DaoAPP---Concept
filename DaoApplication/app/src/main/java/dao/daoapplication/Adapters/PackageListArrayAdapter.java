package dao.daoapplication.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import dao.daoapplication.Entities.Package;
import dao.daoapplication.R;

public class PackageListArrayAdapter extends ArrayAdapter<Package> {
    private Context mContext;

    private TextView txtRoutePackage;
    private TextView txtRoute;
    private TextView txtName;
    private TextView txtPackageNumber;
    private TextView txtundelivered;
    private ImageView imgDelivered;

    private LayoutInflater mInflater;
    private int layoutResource;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private ArrayList<Package> packages;

    public PackageListArrayAdapter(@NonNull Context context, int resource, ArrayList<Package> packages) {
        super(context, resource,packages);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutResource = resource;
        this.mContext = context;
        this.packages = packages;
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null) {
            mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(layoutResource,null);
        }
        txtName = convertView.findViewById(R.id.txt_packageList_carrierName);
        txtRoute = convertView.findViewById(R.id.txt_packageList_route);
        txtPackageNumber = convertView.findViewById(R.id.txt_packageList_packageNumber);
        txtRoutePackage = convertView.findViewById(R.id.txt_packageList_routepackage);
        txtundelivered = convertView.findViewById(R.id.txt_undelivered);
        imgDelivered = convertView.findViewById(R.id.img_packageList_delivered);
        txtName.setText(getItem(position).getName());
        txtRoutePackage.setText(getItem(position).getRoutePackageNumber());
        txtRoute.setText(getItem(position).getRoute());
        txtPackageNumber.setText(getItem(position).getPackagNumber());

        if(getItem(position).isDelivered()){
            imgDelivered.setImageResource(R.drawable.ic_tick);
            txtundelivered.setText("Delivered");
        }else if(!getItem(position).isDelivered()){
            txtundelivered.setText("Undelivered");
            imgDelivered.setImageResource(R.drawable.ic_x);
        }

        mRef.child("Problems").child("Package").child(txtPackageNumber.getText().toString()).child("delivered").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()== boolean.class) {
                    if (dataSnapshot.getValue(Boolean.class) == true) {
                        imgDelivered.setImageResource(R.drawable.ic_tick);
                        txtundelivered.setText("Delivered");
                    } else if (dataSnapshot.getValue(Boolean.class) == false) {
                        txtundelivered.setText("Undelivered");
                        imgDelivered.setImageResource(R.drawable.ic_x);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





        return convertView;
    }
}
