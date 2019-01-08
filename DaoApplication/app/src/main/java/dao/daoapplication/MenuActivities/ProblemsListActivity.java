package dao.daoapplication.MenuActivities;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import dao.daoapplication.Adapters.SectionPageAdapter;
import dao.daoapplication.Fragments.NewspapersFragment;
import dao.daoapplication.Fragments.PackagesFragment;
import dao.daoapplication.Fragments.ScootersFragment;
import dao.daoapplication.R;

public class ProblemsListActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private SectionPageAdapter mSectionPageAdapter;
    private ImageView imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problems_list);
        imgBack = findViewById(R.id.ic_back);
        mViewPager = (ViewPager)findViewById(R.id.container);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProblemsListActivity.this.finish();
            }
        });

        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        Intent i = getIntent();
        if (i.hasExtra("Problem")){
            String problemType = i.getStringExtra("Problem");
            if(problemType.equals("Scooter")){
                mViewPager.setCurrentItem(2);
            }else if(problemType.equals("MissingNewspaper")){
                mViewPager.setCurrentItem(0);
            }else if(problemType.equals("Package")){
                mViewPager.setCurrentItem(1);
            }
        }
    }
    private void setupViewPager(ViewPager viewPager){
        mSectionPageAdapter = new SectionPageAdapter(getSupportFragmentManager());
        mSectionPageAdapter.addFragment(new NewspapersFragment(),"Newspapers");
        mSectionPageAdapter.addFragment(new PackagesFragment(),"Package");
        mSectionPageAdapter.addFragment(new ScootersFragment(),"Scooters");
        viewPager.setAdapter(mSectionPageAdapter);
    }
}
