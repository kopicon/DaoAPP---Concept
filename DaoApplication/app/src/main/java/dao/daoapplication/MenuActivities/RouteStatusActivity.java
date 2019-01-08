package dao.daoapplication.MenuActivities;

import android.app.Dialog;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.okhttp.Route;

import java.util.ArrayList;

import dao.daoapplication.Adapters.PopupArrayAdapter;
import dao.daoapplication.Adapters.RouteStatusListAdapter;
import dao.daoapplication.Entities.Carrier;
import dao.daoapplication.Entities.RoutePackage;
import dao.daoapplication.R;

public class RouteStatusActivity extends AppCompatActivity {

    private Context cContext;

    private ListView lvRouteStatus;
    private EditText txtSearch;
    private ArrayList<RoutePackage> routeStatuses = new ArrayList<>();
    private ArrayList<RoutePackage> filteredrouteStatuses = new ArrayList<>();

    private RouteStatusListAdapter mAdapter;
    private ImageView imgback;
    private RoutePackage cRouteStatus;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRouteRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_status);
        txtSearch = findViewById(R.id.txt_SearchRouteStatus);
        lvRouteStatus = findViewById(R.id.lv_RouteStatus);
        cContext = getApplicationContext();
        mDatabase = FirebaseDatabase.getInstance();
        mRouteRef = mDatabase.getReference("RouteStatuses");

        imgback = findViewById(R.id.ic_back);

        imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RouteStatusActivity.this.finish();
            }
        });
        mRouteRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot DSRP) {
                routeStatuses.clear();
                for(DataSnapshot cRP : DSRP.getChildren()) {
                    cRouteStatus = cRP.getValue(RoutePackage.class);
                    routeStatuses.add(cRouteStatus);
                }
                if(cContext == getApplicationContext()){
                    mAdapter = new RouteStatusListAdapter(RouteStatusActivity.this,R.layout.fragment_route_status_list_item,routeStatuses);
                    lvRouteStatus.setAdapter(mAdapter);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filteredrouteStatuses.clear();
                for(RoutePackage rP : routeStatuses){
                    if(rP.getName().toLowerCase().contains(s)){
                        filteredrouteStatuses.add(rP);
                    }
                }
                mAdapter = new RouteStatusListAdapter(RouteStatusActivity.this,R.layout.fragment_route_status_list_item,filteredrouteStatuses);
                lvRouteStatus.setAdapter(mAdapter);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}
