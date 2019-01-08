package dao.daoapplicationcarrier.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import dao.daoapplicationcarrier.Adapters.AddressListArrayAdapter;
import dao.daoapplicationcarrier.Adapters.PopupArrayAdapter;
import dao.daoapplicationcarrier.Entities.Addresses;
import dao.daoapplicationcarrier.R;

import static android.content.Context.MODE_PRIVATE;

public class AddressListFragment extends Fragment {

    private static final String TAG = "AddressListFragment";


    private ImageButton ibtnRoutes;
    private ListView lvAddresses;
    private ListView lvPopup;
    private TextView txtRoute;
    private TextView popupInfo;

    private Context cContext;

    private ArrayList<String> Routes = new ArrayList<>();
    private ArrayList<String> RoutePackages = new ArrayList<>();
    private ArrayList<Addresses> allAddress = new ArrayList<>();
    private ArrayList<Addresses> filteredAddresses = new ArrayList<>();

    private PopupArrayAdapter mPopupAdapter;
    private AddressListArrayAdapter mAddressAdapter;
    private SharedPreferences carrierPref;

    private String routePackage;
    private String carrierNumber;

    private FirebaseDatabase mDatabase;
    private DatabaseReference addressesRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_route_list,container,false);

        ibtnRoutes = view.findViewById(R.id.ibtn_routesList);
        txtRoute = view.findViewById(R.id.txt_SearchRoute);
        lvAddresses = view.findViewById(R.id.lv_RouteList);

        mDatabase = FirebaseDatabase.getInstance();
        addressesRef = mDatabase.getReference();

        carrierPref = getContext().getSharedPreferences("Carrier",MODE_PRIVATE);
        routePackage = carrierPref.getString("RoutePackage","");
        carrierNumber = carrierPref.getString("CarrierNumber","");

        cContext = getContext();
        addressesRef.child("Routepackages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot DSRP) {
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

        ibtnRoutes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.dialog_listview);
                dialog.setTitle("Choose Route");
                Routes.clear();
                lvPopup =  dialog.findViewById(R.id.lv_popup);
                popupInfo = dialog.findViewById(R.id.txt_popupInfo);
                popupInfo.setText("Choose Route");
                addressesRef.child("Routepackages").child(routePackage).addValueEventListener(new ValueEventListener() {
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
                        addressesRef.child("Routepackages").child(routePackage).child(txtRoute.getText().toString()).addValueEventListener(new ValueEventListener() {
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
                                }
                                lvAddresses.setAdapter(mAddressAdapter);
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        mAddressAdapter = new AddressListArrayAdapter(getContext(),R.layout.fragment_route_list_item,filteredAddresses,routePackage,txtRoute.getText().toString());
                        dialog.dismiss();
                    }
                });
            }
        });
        return view;
    }
    private void toastMassage(String massage){
        Toast.makeText(getContext(),massage,Toast.LENGTH_SHORT).show();
    }
}
