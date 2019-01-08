package dao.daoapplication.MenuActivities;

import android.app.Dialog;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import dao.daoapplication.Adapters.SectionPageAdapter;
import dao.daoapplication.AddAddressActivity;
import dao.daoapplication.Fragments.AddressListFragment;
import dao.daoapplication.Fragments.RouteMapsFragment;
import dao.daoapplication.R;

public class RouteListMapActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private SectionPageAdapter mSectionPageAdapter;
    private FloatingActionButton fbtnEdit;

    private ImageView imgBack;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_list_map);

        fbtnEdit = findViewById(R.id.fbtn_edit);
        imgBack = findViewById(R.id.ic_back);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RouteListMapActivity.this.finish();
            }
        });

        mViewPager = (ViewPager)findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        fbtnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RouteListMapActivity.this,AddAddressActivity.class);
                startActivity(i);
                RouteListMapActivity.this.finish();
            }
        });
    }
    private void setupViewPager(ViewPager viewPager){
        mSectionPageAdapter = new SectionPageAdapter(getSupportFragmentManager());
        mSectionPageAdapter.addFragment(new AddressListFragment(),"List");
        mSectionPageAdapter.addFragment(new RouteMapsFragment(),"Maps");
        viewPager.setAdapter(mSectionPageAdapter);
    }

}
