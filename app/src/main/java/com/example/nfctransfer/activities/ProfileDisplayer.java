package com.example.nfctransfer.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.nfctransfer.R;
import com.example.nfctransfer.adapters.NonSocialProfileFieldAdapter;
import com.example.nfctransfer.adapters.SocialProfileFieldAdapter;
import com.example.nfctransfer.data.AProfileDataField;
import com.example.nfctransfer.data.enumerations.ProfileEntityType;
import com.example.nfctransfer.data.enumerations.ProfileFieldType;
import com.example.nfctransfer.networking.ApiResponses.Profile.ProfileField;
import com.example.nfctransfer.networking.ApiResponses.Profile.Profile;
import com.example.nfctransfer.sqLite.DbUserModel;
import com.example.nfctransfer.sqLite.MatchedProfilesDB;
import com.example.nfctransfer.utils.ContactAdder;
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class ProfileDisplayer extends AppCompatActivity {

    public static final String EXTRA_KEY_PROFILE = "extra_key_profile";
    public final static String EXTRA_KEY_INSERT_IN_DATABASE = "extra_key_insert_database";
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    private ExpandableHeightListView mSocialFieldsListView;
    private ExpandableHeightListView mNonSocialFieldsListView;
    private List<AProfileDataField> mSocialFields;
    private List<AProfileDataField> mNonSocialFields;
    private RelativeLayout mSocialFieldsLayout;
    private RelativeLayout mNotSocialFieldsLayout;
    private ContactAdder mContactAdder;

    private String completeName = null;
    private String cellphoneNumber = null;
    private String emailAddr = null;
    private boolean hasToDbCreate;

    private void init() {
        mNonSocialFieldsListView = (ExpandableHeightListView) findViewById(R.id.profile_showcase_not_social_listview);
        mNotSocialFieldsLayout = (RelativeLayout) findViewById(R.id.profile_showcase_not_social_layout);
        mNonSocialFields = new ArrayList<>();

        mSocialFieldsListView = (ExpandableHeightListView) findViewById(R.id.profile_showcase_social_listview);
        mSocialFieldsLayout = (RelativeLayout) findViewById(R.id.profile_showcase_social_layout);
        mSocialFields = new ArrayList<>();

        mContactAdder = new ContactAdder(getApplicationContext());
    }

    private void handleAddContactLayout() {
        RelativeLayout contactAddLayout = (RelativeLayout) findViewById(R.id.profile_showcase_contact_add_layout);
        contactAddLayout.setVisibility(View.VISIBLE);
        findViewById(R.id.profile_showcase_contact_add_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProfileToContacts();
            }
        });
    }

    private void addProfileToContacts() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            mContactAdder.add(completeName, cellphoneNumber, emailAddr);
        }
    }

    private void setData() {
        Intent intent = getIntent();
        boolean displayNonSocialPart = false;
        boolean displaySocialPart = false;

        Profile profile = (Profile) intent.getSerializableExtra(EXTRA_KEY_PROFILE);

        completeName = profile.getFirstname() + " " + profile.getLastname();

        TextView nameTextView = (TextView) findViewById(R.id.profile_showcase_header_name);
        nameTextView.setText(completeName);

        for (ProfileField field : profile.getFields()) {

            String fieldName = field.getName();
            String fieldTextValue = field.getTextValue();
            String fieldSocialId = field.getSocialId();
            Boolean sharedStatus = field.getSharedStatus();

            ProfileFieldType fieldType = ProfileFieldType.fromString(fieldName);
            AProfileDataField newField = AProfileDataField.getInstance(fieldType, fieldTextValue, fieldSocialId, sharedStatus);
            ProfileEntityType entityType = newField.getFieldEntityType();

            if (fieldType == ProfileFieldType.CELLPHONE) {
                cellphoneNumber = newField.getValue();
            }

            if (fieldType == ProfileFieldType.EMAIL) {
                emailAddr = newField.getValue();
            }

            if (entityType == ProfileEntityType.DATA) {
                mNonSocialFields.add(newField);
                displayNonSocialPart = true;
            }
            else {
                mSocialFields.add(newField);
                displaySocialPart = true;
            }
        }

        if (displayNonSocialPart) {

            mNotSocialFieldsLayout.setVisibility(View.VISIBLE);
            NonSocialProfileFieldAdapter adapter = new NonSocialProfileFieldAdapter(this, 0, mNonSocialFields);
            mNonSocialFieldsListView.setAdapter(adapter);
            mNonSocialFieldsListView.setExpanded(true);
            handleAddContactLayout();
        }

        if (displaySocialPart) {
            mSocialFieldsLayout.setVisibility(View.VISIBLE);
            SocialProfileFieldAdapter adapter = new SocialProfileFieldAdapter(this, this, 0, mSocialFields);
            mSocialFieldsListView.setAdapter(adapter);
            mSocialFieldsListView.setExpanded(true);
        }

        boolean hasToDbCreate = intent.getBooleanExtra(ProfileDisplayer.EXTRA_KEY_INSERT_IN_DATABASE, false);

        if (hasToDbCreate) {
            storeBeamedProfileToDatabase(profile);
        }
    }

    private void storeBeamedProfileToDatabase(Profile profile){

        MatchedProfilesDB db = new MatchedProfilesDB(this);

        Gson gson = new Gson();
        DbUserModel model = new DbUserModel(profile.getId(), gson.toJson(profile));

        db.openForWrite();
        db.insertUser(model);
        db.close();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_displayer);

        init();
        setData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile_displayer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_ok:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //LISessionManager.getInstance(getApplicationContext()).onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                addProfileToContacts();
            }
        }
    }
}
