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
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import dao.daoapplication.DetailsActivities.CarrierDetailsActivity;
import dao.daoapplication.Entities.RoutePackage;
import dao.daoapplication.R;

public class RouteStatusListAdapter extends ArrayAdapter<RoutePackage> {

    public Context mContecxt;

    private TextView txtRoutePackage;
    private TextView txtName;
    private ImageButton ibtnRPDetails;
    private ImageButton ibtnCarrierDetails;
    private ImageView imgFinished;
    private TextView txtUnfinished;

    private TextView txtPopupRoutePackage;
    private TextView txtPopupName;
    private TextView txtPopupFinished;
    private ImageView imgPopupFinished;
    private ListView lvPopupRoutes;
    private Switch sFinished;
    private Button btnBack;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mRouteRef;
    private PopupArrayAdapter mPopupAdapter;

    private ArrayList<RoutePackage> routePackages;
    private ArrayList<String> routes = new ArrayList<>();

    private LayoutInflater mInflater;
    private int layoutResource;

    public RouteStatusListAdapter(@NonNull Context context, int resource, ArrayList<RoutePackage> routePackages){
        super(context,resource,routePackages);
        this.mContecxt = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutResource = resource;
        this.routePackages = routePackages;
        mDatabase = FirebaseDatabase.getInstance();
        mRouteRef = mDatabase.getReference();
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null) {
            mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(layoutResource,null);
        }
        txtRoutePackage = convertView.findViewById(R.id.txt_status_RoutePackage);
        txtName = convertView.findViewById(R.id.txt_status_Name);
        ibtnRPDetails = convertView.findViewById(R.id.ibtn_status_rp_details);
        imgFinished = convertView.findViewById(R.id.img_status_list_finished);
        txtUnfinished = convertView.findViewById(R.id.txt_unfinishedRP);
        ibtnRPDetails.setImageResource(R.drawable.ic_info);
        final String routePackage = getItem(position).getName();
        String name = getItem(position).getWorkerName();
        if(getItem(position).isFinished()){
            imgFinished.setImageResource(R.drawable.ic_tick);
            txtUnfinished.setText("Finished");
        }else{
            imgFinished.setImageResource(R.drawable.ic_x);
            txtUnfinished.setText("Not Finished");
        }

        txtName.setText(name);
        txtRoutePackage.setText(routePackage);
        txtName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(),CarrierDetailsActivity.class);
                i.putExtra("CarrierNumber",getItem(position).getWorkerNumber());
                getContext().startActivity(i);
            }
        });
        ibtnRPDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(getContext());
                RoutePackage cRoutePackage = getItem(position);
                dialog.setContentView(R.layout.dialog_route_status_details);
                txtPopupRoutePackage = dialog.findViewById(R.id.txt_status_details_RoutePackage);
                txtPopupName = dialog.findViewById(R.id.txt_status_details_name);
                txtPopupFinished = dialog.findViewById(R.id.txt_status_details_finished);
                imgFinished = dialog.findViewById(R.id.img_status_details_finished);
                lvPopupRoutes = dialog.findViewById(R.id.lv_status_details_Routes);
                btnBack = dialog.findViewById(R.id.btn_back_routeStatusdeltails_dialog);
                sFinished = dialog.findViewById(R.id.s_finished);
                if(getItem(position).isFinished()){
                    txtPopupFinished.setText("Finished");
                    sFinished.setChecked(true);
                    imgFinished.setImageResource(R.drawable.ic_tick);
                }else if(!getItem(position).isFinished()){
                    txtPopupFinished.setText("Not Finished");
                    sFinished.setChecked(false);
                    imgFinished.setImageResource(R.drawable.ic_x);
                }

                txtPopupRoutePackage.setText(cRoutePackage.getName());
                txtPopupName.setText(cRoutePackage.getWorkerName());
                dialog.show();
                btnBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                mRouteRef.child("Routepackages").child(getItem(position).getName()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot DSRP) {
                        routes.clear();
                        for(DataSnapshot cRP : DSRP.getChildren()){
                            String cR = cRP.getKey();
                            routes.add(cR);
                        }
                        if(mContecxt == getContext()){
                            mPopupAdapter = new PopupArrayAdapter(getContext(),R.layout.fragment_popup,routes);
                            lvPopupRoutes.setAdapter(mPopupAdapter);

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                sFinished.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked){
                            txtPopupFinished.setText("Finished");
                            imgFinished.setImageResource(R.drawable.ic_tick);
                            mRouteRef.child("RouteStatuses").child(getItem(position).getName()).child("finished").setValue(true);
                        }
                        if(!isChecked){
                            txtPopupFinished.setText("Not finished");
                            imgFinished.setImageResource(R.drawable.ic_x);
                            mRouteRef.child("RouteStatuses").child(getItem(position).getName()).child("finished").setValue(false);
                        }
                    }
                });

            }
        });

        return convertView;
    }
}
