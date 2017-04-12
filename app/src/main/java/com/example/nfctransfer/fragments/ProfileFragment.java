package com.example.nfctransfer.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.nfctransfer.R;
import com.example.nfctransfer.activities.AddFieldActivity;
import com.example.nfctransfer.activities.MainActivity;
import com.example.nfctransfer.adapters.ProfileDataViewAdapter;
import com.example.nfctransfer.data.AProfileDataField;
import com.example.nfctransfer.data.CellphoneField;
import com.example.nfctransfer.data.EmailField;
import com.example.nfctransfer.data.HomeAddressField;
import com.example.nfctransfer.data.enumerations.ProfileFieldType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class ProfileFragment extends Fragment {

    public final static int CONTEXT_MENU_ITEM_DELETE = 0;
    public final static int CONTEXT_MENU_ITEM_EDIT = 1;

    private Context context;
    private Activity activity;

    private ProfileDataViewAdapter mAdapter;
    private RecyclerView mProfileView;
    private FloatingActionButton mButtonAddField;

    private List<ProfileFieldType> allFields = new ArrayList<>();
    private List<AProfileDataField> profile = new ArrayList<>();

    public ProfileFragment() {
        // Required empty public constructor
    }

    private void buildFieldsModel() {
        allFields.add(ProfileFieldType.CELLPHONE);
        allFields.add(ProfileFieldType.EMAIL);
        allFields.add(ProfileFieldType.ADDRESS);
        allFields.add(ProfileFieldType.FACEBOOK);
        allFields.add(ProfileFieldType.TWITTER);
        allFields.add(ProfileFieldType.LINKEDIN);
    }


    private void initComponents() {

        activity = getActivity();
        context = activity.getApplicationContext();

        buildFieldsModel();

        profile.add(new CellphoneField("424 384 7402"));
        profile.add(new EmailField("dummy@dummy.com"));
        profile.add(new HomeAddressField("5050 E Garford"));

        mProfileView = (RecyclerView) activity.findViewById(R.id.profile_data_view);
        mButtonAddField = (FloatingActionButton) activity.findViewById(R.id.button_add_field);

        mButtonAddField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonAddFieldClicked();
            }
        });

        mAdapter = new ProfileDataViewAdapter(context, profile);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);

        mProfileView.setLayoutManager(mLayoutManager);

        mProfileView.setAdapter(mAdapter);

        mAdapter.notifyDataSetChanged();

        registerForContextMenu(mProfileView);
    }

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    private void onButtonAddFieldClicked() {

        List<ProfileFieldType> fieldsAvailable = allFields;

        for (AProfileDataField field : profile) {
            fieldsAvailable.remove(field.getFieldType());
        }

        Intent openAddFieldActivity = new Intent(activity, AddFieldActivity.class);
        openAddFieldActivity.putExtra(MainActivity.KEY_PASS_FIELDS_TO_ADD, (Serializable) fieldsAvailable);

        startActivityForResult(openAddFieldActivity, 0);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initComponents();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int position = -1;
        try {
            position = mAdapter.getPosition();
        } catch (Exception e) {
            return super.onContextItemSelected(item);
        }
        switch (item.getItemId()) {
            case CONTEXT_MENU_ITEM_DELETE:
                Log.e("TAG", "Delete pressed " + mAdapter.getPosition());
                break;
            case CONTEXT_MENU_ITEM_EDIT:
                Log.e("TAG", "Edit pressed " + mAdapter.getPosition());
                break;
        }
        return super.onContextItemSelected(item);
    }
}
