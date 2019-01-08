package dao.daoapplication.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import dao.daoapplication.Adapters.PackageListArrayAdapter;
import dao.daoapplication.Adapters.PopupArrayAdapter;
import dao.daoapplication.Entities.Package;
import dao.daoapplication.R;

public class PackagesFragment extends Fragment {
    private static final String TAG = "ProblemPackageFragment";
    private Context mContext;

    private ListView lvPackages;
    private TextView txtPopupPackageNumb;
    private TextView txtPopupName;
    private TextView txtPopupRoute;
    private Switch sDelivered;
    private ListView lvPopup;
    private Button btnBack;
    private Button btnDeletePackage;

    private Button btnRemoveYes;
    private Button btnRemoveNo;
    private TextView txtRemoveWhat;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private ArrayList<Package> packages = new ArrayList<>();
    private PackageListArrayAdapter mAdapter;
    private PopupArrayAdapter mPopupAdapter;
    private String cPackageNumber;
    private Package cPackage;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_problems_packages,container,false);
        lvPackages = view.findViewById(R.id.lv_package_problem);
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference();
        mContext = getContext();
        mRef.child("Problems").child("Package").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                packages.clear();
                if(mContext == getContext()){
                for (DataSnapshot cPN : dataSnapshot.getChildren()) {
                    cPackageNumber = cPN.getKey();
                    mRef.child("Problems").child("Package").child(cPackageNumber).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            cPackage = dataSnapshot.getValue(Package.class);
                            packages.add(cPackage);
                            mAdapter = new PackageListArrayAdapter(mContext, R.layout.fragment_package_list, packages);
                            lvPackages.setAdapter(mAdapter);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                lvPackages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                        final Dialog dialog = new Dialog(getContext());
                        dialog.setContentView(R.layout.dialog_package_details);
                        txtPopupPackageNumb = dialog.findViewById(R.id.txt_popup_packageNumber);
                        txtPopupName = dialog.findViewById(R.id.txt_popup_packages_Name);
                        txtPopupRoute = dialog.findViewById(R.id.txt_popup_packages_Route);
                        lvPopup = dialog.findViewById(R.id.lv_popup_packages_reasons);
                        sDelivered = dialog.findViewById(R.id.s__package_delivered);
                        btnBack = dialog.findViewById(R.id.btn_packagesDialog_back);
                        btnDeletePackage = dialog.findViewById(R.id.btn_delete_package);
                        txtPopupPackageNumb.setText(packages.get(position).getPackagNumber());
                        txtPopupName.setText(packages.get(position).getName());
                        txtPopupRoute.setText(packages.get(position).getRoute());
                        mPopupAdapter = new PopupArrayAdapter(getContext(),R.layout.fragment_popup,packages.get(position).getReasons());
                        lvPopup.setAdapter(mPopupAdapter);
                        dialog.show();
                        if(packages.get(position).isDelivered()){
                            sDelivered.setChecked(true);
                            sDelivered.setText("Delivered");
                        }
                        sDelivered.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if(isChecked) {
                                    sDelivered.setText("Delivered");
                                    mRef.child("Problems").child("Package").child(packages.get(position).getPackagNumber()).child("delivered").setValue(true);
                                }else if(!isChecked){
                                    sDelivered.setText("Undelivered");
                                    mRef.child("Problems").child("Package").child(packages.get(position).getPackagNumber()).child("delivered").setValue(false);
                                }
                            }
                        });
                        btnDeletePackage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.setContentView(R.layout.dialog_are_you_sure);
                                btnRemoveYes = dialog.findViewById(R.id.btn_dialog_remove_yes);
                                btnRemoveNo = dialog.findViewById(R.id.btn_dialog_remove_no);
                                txtRemoveWhat = dialog.findViewById(R.id.txt_dialog_remove_what);
                                txtRemoveWhat.setText(packages.get(position).getPackagNumber()+" Package?");
                                btnRemoveYes.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mRef.child("Problems").child("Package").child(packages.get(position).getPackagNumber()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                dialog.dismiss();
                                            }
                                        });
                                    }
                                });
                               btnRemoveNo.setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View v) {
                                       dialog.dismiss();
                                   }
                               });
                            }
                        });
                        btnBack.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                    }
                });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }
}
