package dao.daoapplication.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import dao.daoapplication.Adapters.AddressListArrayAdapter;
import dao.daoapplication.Adapters.PopupArrayAdapter;
import dao.daoapplication.Entities.Addresses;
import dao.daoapplication.MenuActivities.RouteListMapActivity;
import dao.daoapplication.R;

public class AddressListFragment extends Fragment {

    private static final String TAG = "AddressListFragment";


    private ImageButton ibtnRoutePackages;
    private ImageButton ibtnRoutes;
    private ListView lvAddresses;
    private ListView lvPopup;
    private TextView txtRoutePackage;
    private TextView txtRoute;
    private EditText txtChangePosition;
    private Button btnSave;
    private TextView popupInfo;

    private FloatingActionButton fbtnDeleteRoute;
    private Button btnDialogYes;
    private Button btnDialogNo;
    private TextView txtDialog;

    private Context cContext;

    private ArrayList<String> Routes = new ArrayList<>();
    private ArrayList<String> RoutePackages = new ArrayList<>();
    private ArrayList<Addresses> allAddress = new ArrayList<>();
    private ArrayList<Addresses> filteredAddresses = new ArrayList<>();

    private PopupArrayAdapter mPopupAdapter;
    private AddressListArrayAdapter mAddressAdapter;

    private FirebaseDatabase mDatabase;
    private DatabaseReference addressesRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_route_list,container,false);
        ibtnRoutePackages = view.findViewById(R.id.ibtn_routePackagesList);
        ibtnRoutes = view.findViewById(R.id.ibtn_routesList);
        txtRoutePackage = view.findViewById(R.id.txt_SearchRoutePackage);
        txtRoute = view.findViewById(R.id.txt_SearchRoute);
        lvAddresses = view.findViewById(R.id.lv_RouteList);
        fbtnDeleteRoute = view.findViewById(R.id.fbtn_delete_Route);
        mDatabase = FirebaseDatabase.getInstance();
        addressesRef = mDatabase.getReference();
        fbtnDeleteRoute.setVisibility(View.INVISIBLE);
        ibtnRoutes.setEnabled(false);
        cContext = getContext();
        addressesRef.child("Routepackages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot DSRP) {
                RoutePackages.clear();
                for (DataSnapshot child:  DSRP.getChildren()){
                    String cRP = child.getKey();
                    RoutePackages.add(cRP);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                toastMassage("Check Internet Connection");
            }
        });
        fbtnDeleteRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.dialog_are_you_sure);
                btnDialogYes = dialog.findViewById(R.id.btn_dialog_remove_yes);
                btnDialogNo = dialog.findViewById(R.id.btn_dialog_remove_no);
                txtDialog = dialog.findViewById(R.id.txt_dialog_remove_what);
                txtDialog.setText(txtRoute.getText().toString());
                btnDialogNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                btnDialogYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addressesRef.child("Routepackages").child(txtRoutePackage.getText().toString()).child(txtRoute.getText().toString()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                toastMassage(txtRoute.getText().toString()+" Successfully removed");
                                Routes.clear();
                                txtRoutePackage.setText("");
                                txtRoute.setText("");
                                lvAddresses.setAdapter(null);
                                dialog.dismiss();
                                fbtnDeleteRoute.setVisibility(View.INVISIBLE);
                            }
                        });
                    }
                });
                dialog.show();
            }
        });
        ibtnRoutePackages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.dialog_listview);
                dialog.setTitle("Choose RoutePackage");
                lvPopup = (ListView) dialog.findViewById(R.id.lv_popup);
                popupInfo = dialog.findViewById(R.id.txt_popupInfo);
                popupInfo.setText("Choose Routepackage");
                mPopupAdapter = new PopupArrayAdapter(getContext(), R.layout.fragment_popup, RoutePackages);
                lvPopup.setAdapter(mPopupAdapter);
                dialog.show();
                Routes.clear();

                lvPopup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        final String cRP = RoutePackages.get(position);
                        txtRoutePackage.setText(cRP);
                        ibtnRoutes.setEnabled(true);
                        dialog.dismiss();


                    }
                });
            }
        });

        ibtnRoutes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.dialog_listview);
                dialog.setTitle("Choose Route");
                Routes.clear();
                lvPopup = (ListView) dialog.findViewById(R.id.lv_popup);
                popupInfo = dialog.findViewById(R.id.txt_popupInfo);
                popupInfo.setText("Choose Route");
                addressesRef.child("Routepackages").child(txtRoutePackage.getText().toString()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot DSRoutes) {
                        for (DataSnapshot child : DSRoutes.getChildren()) {
                            String cRoute = child.getKey();
                            Routes.add(cRoute);
                        }
                        if(getContext() == cContext)
                        lvPopup.setAdapter(mPopupAdapter);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                mPopupAdapter = new PopupArrayAdapter(getContext(), R.layout.fragment_popup, Routes);
                dialog.show();
                lvPopup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String cRoute = Routes.get(position);
                        txtRoute.setText(cRoute);
                        allAddress.clear();
                        filteredAddresses.clear();
                        final ArrayList<Integer> ids = new ArrayList<>();
                        addressesRef.child("Routepackages").child(txtRoutePackage.getText().toString()).child(txtRoute.getText().toString()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot DSAddresses) {
                                for(DataSnapshot child :DSAddresses.getChildren()){
                                    Addresses cAddress = child.getValue(Addresses.class);
                                    allAddress.add(cAddress);
                                    ids.add(cAddress.getId());
                                }
                                Collections.sort(ids);
                                for (int i = 1;i <= ids.size();i++) {
                                    for (Addresses cA: allAddress) {
                                        if(cA.getId()==i){
                                            filteredAddresses.add(cA);
                                        }
                                    }
                                }if(getContext() == cContext) {
                                    mAddressAdapter = new AddressListArrayAdapter(getContext(), R.layout.fragment_route_list_item, filteredAddresses, txtRoutePackage.getText().toString(), txtRoute.getText().toString());
                                    lvAddresses.setAdapter(mAddressAdapter);
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        fbtnDeleteRoute.setVisibility(View.VISIBLE);
                        dialog.dismiss();
                    }
                });
            }
        });
        lvAddresses.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.popup_change_queue);
                txtChangePosition = dialog.findViewById(R.id.txt_changePosition);
                btnSave = dialog.findViewById(R.id.btn_save_position);
                dialog.show();
                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String nP = txtChangePosition.getText().toString();
                        int newPos = Integer.parseInt(nP);
                        if(position<newPos){
                            int after = position+1;
                            for (int i = after;i< newPos;i++){
                                Addresses eAddresses = filteredAddresses.get(i);
                                eAddresses.setId(position-1);
                                addressesRef.child("RoutePackages").child(txtRoutePackage.getText().toString()).child(txtRoute.getText().toString()).child(eAddresses.getAddress()).setValue(eAddresses);

                            }
                            for(int i = newPos;i<=filteredAddresses.size();i++){
                                Addresses eAddresses = filteredAddresses.get(i);
                                eAddresses.setId(newPos+1);
                                addressesRef.child("RoutePackages").child(txtRoutePackage.getText().toString()).child(txtRoute.getText().toString()).child(eAddresses.getAddress()).setValue(eAddresses).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        toastMassage("Queue has bee changed");
                                    }
                                });

                            }
                        }
                        dialog.dismiss();
                    }
                });

                return false;
            }
        });
        return view;
    }
    private void toastMassage(String massage){
        Toast.makeText(getContext(),massage,Toast.LENGTH_SHORT).show();
    }
}
