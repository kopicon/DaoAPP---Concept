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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import dao.daoapplication.Adapters.MissingNewspapersArrayAdapter;
import dao.daoapplication.Entities.Addresses;
import dao.daoapplication.Entities.UndeliveredNewspaper;
import dao.daoapplication.R;

public class NewspapersFragment extends Fragment {

    private static final String TAG = "ProblemNewspaperFragment";

    private ListView lvMissingNewspaper;

    private Context cContext;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private FloatingActionButton fbtnEmpty;
    private Button btnDialogYes;
    private Button btnDialogNo;
    private TextView txtDialog;

    private MissingNewspapersArrayAdapter mAdapter;
    private UndeliveredNewspaper cAddress;

    private ArrayList<UndeliveredNewspaper> addresses = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_problems_newspapers,container,false);
        lvMissingNewspaper = view.findViewById(R.id.lv_missing_newspapers);
        fbtnEmpty = view.findViewById(R.id.fbtn_empty_missing_newspapers);
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference().child("Problems").child("MissingNewspapers");
        cContext= getContext();
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                addresses.clear();
                for (DataSnapshot cA : dataSnapshot.getChildren()){
                    cAddress = cA.getValue(UndeliveredNewspaper.class);
                    addresses.add(cAddress);
                }
                if(cContext == getContext()){
                    mAdapter = new MissingNewspapersArrayAdapter(getContext(),R.layout.fragment_missing_newspapers_list_item,addresses,cAddress.getRoute());
                    lvMissingNewspaper.setAdapter(mAdapter);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        fbtnEmpty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.dialog_are_you_sure);
                btnDialogYes = dialog.findViewById(R.id.btn_dialog_remove_yes);
                btnDialogNo = dialog.findViewById(R.id.btn_dialog_remove_no);
                txtDialog = dialog.findViewById(R.id.txt_dialog_remove_what);
                txtDialog.setText("all the missing newspapers from the system?");
                btnDialogYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    dialog.dismiss();
                                    toastMassage("Missing newspapers have been successfully removed");
                                }
                            }
                        });
                    }
                });
                btnDialogNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        return view;
    }
    private void toastMassage(String massage){
        Toast.makeText(getContext(),massage,Toast.LENGTH_SHORT).show();
    }
}
