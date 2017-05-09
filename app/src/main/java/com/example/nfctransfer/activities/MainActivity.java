package com.example.nfctransfer.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.nfctransfer.R;
import com.example.nfctransfer.adapters.MainPagerAdapter;
import com.example.nfctransfer.fragments.ActionShareFragment;
import com.example.nfctransfer.fragments.ProfileFragment;
import com.example.nfctransfer.sharedPreferences.Preferences;
import com.example.nfctransfer.views.VerticalViewPager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Context context;
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

    private void initComponents() {

        context = getApplicationContext();

        mFragments = new ArrayList<>();

        mFragments.add(ProfileFragment.newInstance());
        mFragments.add(ActionShareFragment.newInstance());

        mPagerAdapter = new MainPagerAdapter(getSupportFragmentManager(), mFragments);

        mViewPager = (VerticalViewPager) findViewById(R.id.view_pager);
        mViewPager.setAdapter(mPagerAdapter);

    }

    private void onLogOut() {
        Preferences.getInstance().deleteSavedCredentials(this);
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finishAffinity();
        startActivity(intent);
    }

    private void onOpenSettings() {
        Intent toSettings = new Intent(context, SettingsActivity.class);
        startActivity(toSettings);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.option_settings:
                onOpenSettings();
                return true;
            case R.id.option_logout:
                onLogOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
