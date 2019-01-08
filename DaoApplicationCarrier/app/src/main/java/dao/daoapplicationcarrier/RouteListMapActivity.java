package dao.daoapplicationcarrier;

import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import dao.daoapplicationcarrier.Adapters.SectionPageAdapter;
import dao.daoapplicationcarrier.Fragments.AddressListFragment;
import dao.daoapplicationcarrier.Fragments.RouteMapsFragment;

public class RouteListMapActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private SectionPageAdapter mSectionPageAdapter;
    private ImageView imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_list_map);
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

    }
    private void setupViewPager(ViewPager viewPager){
        mSectionPageAdapter = new SectionPageAdapter(getSupportFragmentManager());
        mSectionPageAdapter.addFragment(new AddressListFragment(),"List");
        mSectionPageAdapter.addFragment(new RouteMapsFragment(),"Maps");
        viewPager.setAdapter(mSectionPageAdapter);
    }
}

