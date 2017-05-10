package com.example.nfctransfer.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.nfctransfer.R;
import com.example.nfctransfer.fragments.ActionShareFragment;
import com.example.nfctransfer.fragments.MatchListFragments;
import com.example.nfctransfer.fragments.ProfileFragment;
import com.example.nfctransfer.sharedPreferences.Preferences;
import com.example.nfctransfer.views.TabLayoutIconResizer;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;


public class MainActivity extends AppCompatActivity {

    private Context mContext;
    private SmartTabLayout mSmartTabLayout;
    private float mTabLayoutIconSize;

    public static final String KEY_PASS_FIELDS_TO_ADD = "fields_to_add";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initComponents();
    }

    private void prepareTabIcon(ImageView icon) {
        TabLayoutIconResizer.scaleImageview(mContext, icon, (int) mTabLayoutIconSize);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, (int) mTabLayoutIconSize, 1.0f);
        layoutParams.gravity = Gravity.CENTER;
        icon.setLayoutParams(layoutParams);
    }

    private void initComponents() {

        mContext = getApplicationContext();

        final LayoutInflater inflater = LayoutInflater.from(getApplicationContext());

        mTabLayoutIconSize = mContext.getResources().getDimension(R.dimen.activity_main_tab_layout_icon_size);

        mSmartTabLayout = (SmartTabLayout) findViewById(R.id.view_pager_tab);
        mSmartTabLayout.setCustomTabView(new SmartTabLayout.TabProvider() {
            @Override
            public View createTabView(ViewGroup container, int position, PagerAdapter adapter) {

                ImageView icon = (ImageView) inflater.inflate(R.layout.custom_viewpager_item, container, false);

                switch (position) {
                    case 0:
                        icon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.plus));
                        break;
                    case 1:
                        icon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.plus));
                        break;
                    case 2:
                        icon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.plus));
                        break;
                    default:
                        throw new IllegalStateException("Invalid position: " + position);
                }
                prepareTabIcon(icon);
                return icon;
            }
        });


        FragmentPagerItemAdapter horizontalViewPagerAdapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add("A", ActionShareFragment.class)
                .add("B", ProfileFragment.class)
                .add("C", MatchListFragments.class)
                .create());

        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(horizontalViewPagerAdapter);

        mSmartTabLayout.setViewPager(viewPager);
    }

    private void onLogOut() {
        Preferences.getInstance().deleteSavedCredentials(this);
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finishAffinity();
        startActivity(intent);
    }

    private void onOpenSettings() {
        Intent toSettings = new Intent(mContext, SettingsActivity.class);
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
