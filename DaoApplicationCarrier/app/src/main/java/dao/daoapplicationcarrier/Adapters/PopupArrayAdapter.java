package dao.daoapplicationcarrier.Adapters;

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


import dao.daoapplicationcarrier.R;

public class PopupArrayAdapter extends ArrayAdapter<String> {

    public Context mContecxt;

    private TextView txtName;
    private LayoutInflater mInflater;
    private int layoutResource;
    private ImageView imgDelete;
    int clickCount = 0;
    private ArrayList<String> toChoose = null;

    public PopupArrayAdapter(@NonNull Context context, int resource, @NonNull ArrayList<String> toChoose) {
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
        txtName = convertView.findViewById(R.id.txt_name);
        String name = getItem(position);
            txtName.setText(name);
            return convertView;

    }
}
