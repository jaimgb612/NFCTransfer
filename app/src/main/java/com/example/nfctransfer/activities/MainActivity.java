package com.example.nfctransfer.activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.nfctransfer.R;
import com.example.nfctransfer.adapters.MainPagerAdapter;
import com.example.nfctransfer.fragments.ActionShareFragment;
import com.example.nfctransfer.fragments.ProfileFragment;
import com.example.nfctransfer.views.VerticalViewPager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private MainPagerAdapter mPagerAdapter;
    private VerticalViewPager mViewPager;
    private List<Fragment> mFragments;

    public static final String KEY_PASS_FIELDS_TO_ADD = "fields_to_add";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initComponents();
    }

    public void initComponents() {

        mFragments = new ArrayList<>();

        mFragments.add(ProfileFragment.newInstance());
        mFragments.add(ActionShareFragment.newInstance());

        mPagerAdapter = new MainPagerAdapter(getSupportFragmentManager(), mFragments);

        mViewPager = (VerticalViewPager) findViewById(R.id.view_pager);
        mViewPager.setAdapter(mPagerAdapter);

    }
}
