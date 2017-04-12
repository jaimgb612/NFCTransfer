package com.example.nfctransfer.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.example.nfctransfer.R;
import com.example.nfctransfer.adapters.AddFieldViewAdapter;
import com.example.nfctransfer.data.AProfileDataField;
import com.example.nfctransfer.data.enumerations.ProfileFieldType;

import java.util.ArrayList;
import java.util.List;

public class AddFieldActivity extends AppCompatActivity {

    private List<AProfileDataField> mFieldsToDisplay;

    private AddFieldViewAdapter mAdapter;

    private ListView mListView;

    public void buildFieldsToDisplay(List<ProfileFieldType> fields) {
        mFieldsToDisplay = new ArrayList<>();

        for (ProfileFieldType field : fields) {
            mFieldsToDisplay.add(AProfileDataField.getEmptyInstance(field));
        }
    }

    public void initCompontents() {
        Intent from = getIntent();

        List<ProfileFieldType> activeFieldsOnProfile = (List<ProfileFieldType>) from.getSerializableExtra(MainActivity.KEY_PASS_FIELDS_TO_ADD);

        buildFieldsToDisplay(activeFieldsOnProfile);

        mListView = (ListView) findViewById(R.id.listview);

        mAdapter = new AddFieldViewAdapter(getApplicationContext(), 0, mFieldsToDisplay);

        mListView.setAdapter(mAdapter);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_field);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initCompontents();
    }
}
