package com.example.nfctransfer.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.nfctransfer.R;
import com.example.nfctransfer.Utils.AlertDialogTools;
import com.example.nfctransfer.Utils.FieldEntryParser;
import com.example.nfctransfer.activities.AddFieldActivity;
import com.example.nfctransfer.activities.MainActivity;
import com.example.nfctransfer.adapters.ProfileDataViewAdapter;
import com.example.nfctransfer.data.AProfileDataField;
import com.example.nfctransfer.data.enumerations.Deletion;
import com.example.nfctransfer.data.enumerations.ProfileEntityType;
import com.example.nfctransfer.data.enumerations.ProfileFieldType;
import com.example.nfctransfer.networking.ApiResponses.PullSelfProfile.ProfileField;
import com.example.nfctransfer.networking.ApiResponses.PullSelfProfile.PullSelfProfileResponse;
import com.example.nfctransfer.networking.ApiResponses.PullSelfProfile.SelfProfile;
import com.example.nfctransfer.networking.HttpCodes;
import com.example.nfctransfer.networking.NfcTransferApi;
import com.example.nfctransfer.networking.Session;
import com.example.nfctransfer.views.VerticalViewPager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    public final static int CONTEXT_MENU_ITEM_DELETE = 0;
    public final static int CONTEXT_MENU_ITEM_EDIT = 1;
    public final static int INTENT_CHOSEN_FIELD_KEY = 1;
    public final static String INTENT_EXTRA_CHOSEN_FIELD_KEY = "extra_chosen_field_key";

    private Context context;
    private Activity activity;

    private VerticalViewPager mViewPager;
    private ProfileDataViewAdapter mAdapter;
    private RecyclerView mProfileView;
    private SwipeRefreshLayout mRefreshLayout;
    private FieldEntryParser mFieldEntryParser;
    private FloatingActionButton mButtonAddField;
    private TextView mUserNameTitle;

    private List<ProfileFieldType> allFields = new ArrayList<>();
    private List<AProfileDataField> profileFields = new ArrayList<>();

    public ProfileFragment() {}

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
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

        mFieldEntryParser = new FieldEntryParser(context);

        mProfileView = (RecyclerView) activity.findViewById(R.id.profile_data_view);
        mButtonAddField = (FloatingActionButton) activity.findViewById(R.id.button_add_field);
        mViewPager = (VerticalViewPager) activity.findViewById(R.id.view_pager);

        mButtonAddField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonAddFieldClicked();
            }
        });
        mButtonAddField.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#2980b9")));

        mAdapter = new ProfileDataViewAdapter(context, profileFields);

        mAdapter.setCreateMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                int position = mAdapter.getPosition();
                AProfileDataField field = profileFields.get(position);

                if (field.isDeletable() == Deletion.ONLY_DELETABLE) {
                    menu.add(Menu.NONE, ProfileFragment.CONTEXT_MENU_ITEM_DELETE, Menu.NONE, "Remove");
                }
                if (field.isDeletable() == Deletion.ONLY_EDITABLE) {
                    menu.add(Menu.NONE, ProfileFragment.CONTEXT_MENU_ITEM_EDIT, Menu.NONE, "Edit");
                }
                if (field.isDeletable() == Deletion.DELETABLE_EDITABLE) {
                    menu.add(Menu.NONE, ProfileFragment.CONTEXT_MENU_ITEM_EDIT, Menu.NONE, "Edit");
                    menu.add(Menu.NONE, ProfileFragment.CONTEXT_MENU_ITEM_DELETE, Menu.NONE, "Remove");
                }
            }
        });

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);

        mProfileView.setLayoutManager(mLayoutManager);

        mProfileView.setAdapter(mAdapter);

        mRefreshLayout = (SwipeRefreshLayout) activity.findViewById(R.id.swipe_refresh);

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshItems();
            }
        });

        mViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mRefreshLayout.setEnabled(false);
                if ((event.getAction() == MotionEvent.ACTION_DOWN) && mViewPager.getCurrentItem() == 0) {
                    refreshItems();
                }
                return false;
            }
        });

        mAdapter.notifyDataSetChanged();

        mUserNameTitle = (TextView ) activity.findViewById(R.id.user_profile_name);

        registerForContextMenu(mProfileView);
    }

    private void showEditContentFailDialog() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getActivity());

        alertDialogBuilder.setTitle(R.string.user_err_edit_content);
        alertDialogBuilder
                .setMessage(R.string.user_err_fail_connection_desc)
                .setCancelable(false)
                .setPositiveButton(R.string.OK, null);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void showInputContentFailDFialog(String errMsg) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getActivity());

        alertDialogBuilder.setTitle(R.string.err_wrong_input_format_title);
        alertDialogBuilder
                .setMessage(errMsg)
                .setCancelable(false)
                .setPositiveButton(R.string.OK, null);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    void refreshItems() {
        mRefreshLayout.setEnabled(true);
        mRefreshLayout.setRefreshing(true);

        pullDataForCurrentUser();
    }

    void onItemsLoadComplete() {
        mRefreshLayout.setRefreshing(false);
    }

    private void onButtonAddFieldClicked() {

        List<ProfileFieldType> fieldsAvailable = new ArrayList<>(allFields);

        for (AProfileDataField field : profileFields) {
            fieldsAvailable.remove(field.getFieldType());
        }

        Intent openAddFieldActivity = new Intent(activity, AddFieldActivity.class);
        openAddFieldActivity.putExtra(MainActivity.KEY_PASS_FIELDS_TO_ADD, (Serializable) fieldsAvailable);

        startActivityForResult(openAddFieldActivity, INTENT_CHOSEN_FIELD_KEY);
    }

    private void onCurrentUserDataPulled(SelfProfile profile) {

        String completeName = profile.getFirstname() + " " + profile.getLastname();
        mUserNameTitle.setText(completeName);

        mAdapter.removeAll();

        for (ProfileField field : profile.getFields()) {

            String fieldName = field.getName();
            String fieldTextValue = field.getTextValue();
            String fieldSocialId = field.getSocialId();
            Boolean sharedStatus = field.getSharedStatus();

            ProfileFieldType fieldType = ProfileFieldType.fromString(fieldName);

            AProfileDataField newField = AProfileDataField.getInstance(fieldType, fieldTextValue, fieldSocialId, sharedStatus);
            mAdapter.addField(newField);
        }
        onItemsLoadComplete();
    }

    private void pullDataForCurrentUser() {
        Call<PullSelfProfileResponse> call;

        call = NfcTransferApi.getInstance().getSelfProfileData(Session.accessToken);
        call.enqueue(new Callback<PullSelfProfileResponse>() {
            @Override
            public void onResponse(Call<PullSelfProfileResponse> call, Response<PullSelfProfileResponse> response) {
                int code = response.code();
                PullSelfProfileResponse result;

                if (code != HttpCodes.OK) {
                    //onRequestFailed();
                    return;
                }

                result = response.body();
                onCurrentUserDataPulled(result.getUser());
            }

            @Override
            public void onFailure(Call<PullSelfProfileResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void addEditableField(final AProfileDataField field) {

        final ProfileFieldType fieldType = field.getFieldType();

        AlertDialogTools alertDialogTools = new AlertDialogTools(getActivity(), field);
        final LinearLayout dialogLayout = alertDialogTools.createAlertDialogLayout();
        final EditText input = (EditText) dialogLayout.getChildAt(0);
        final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.user_new_field)
                .setMessage(alertDialogTools.getMessage())
                .setView(dialogLayout)
                .setPositiveButton(R.string.user_edit_confirm, null)
                .setNegativeButton(R.string.user_edit_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                })
                .create();

        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button okButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String inputValue = input.getText().toString();

                        if (!mFieldEntryParser.parse(inputValue, fieldType)) {
                            showInputContentFailDFialog(mFieldEntryParser.getLastErrMsg());
                        }
                        else {
                            // NETWORK
                            field.setValue(inputValue);
                            mAdapter.addField(field);
                            dialog.dismiss();
                        }
                    }
                });
            }
        });
        dialog.show();
    }

    private void editEditableField(final int position) {
        final AProfileDataField field = profileFields.get(position);
        final ProfileFieldType fieldType = field.getFieldType();

        AlertDialogTools alertDialogTools = new AlertDialogTools(getActivity(), field);
        final LinearLayout dialogLayout = alertDialogTools.createAlertDialogLayout();
        final EditText input = (EditText) dialogLayout.getChildAt(0);
        final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.user_new_field)
                .setMessage(alertDialogTools.getMessage())
                .setView(dialogLayout)
                .setPositiveButton(R.string.user_edit_confirm, null)
                .setNegativeButton(R.string.user_edit_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                })
                .create();

        input.setText(field.getValue());

        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button okButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String inputValue = input.getText().toString();

                        if (!mFieldEntryParser.parse(inputValue, fieldType)) {
                            showInputContentFailDFialog(mFieldEntryParser.getLastErrMsg());
                        }
                        else {
                            // NETWORK
                            field.setValue(inputValue);
                            mAdapter.editField(position);
                            dialog.dismiss();
                        }
                    }
                });
            }
        });
        dialog.show();
    }

    private void onAddField(ProfileFieldType fieldType) {
        AProfileDataField field = AProfileDataField.getEmptyInstance(fieldType);

        if (field.getFieldEntityType() == ProfileEntityType.DATA) {
            addEditableField(field);
        }
        else {

        }
    }

    private void onEditField(int position) {
        editEditableField(position);
    }

    private void onDeleteField(int position) {
        mAdapter.removeField(position);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initComponents();
        pullDataForCurrentUser();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case CONTEXT_MENU_ITEM_DELETE:
                onDeleteField(mAdapter.getPosition());
                break;
            case CONTEXT_MENU_ITEM_EDIT:
                onEditField(mAdapter.getPosition());
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == INTENT_CHOSEN_FIELD_KEY && resultCode == Activity.RESULT_OK) {
            ProfileFieldType chosenFieldType = (ProfileFieldType) data.getSerializableExtra(INTENT_EXTRA_CHOSEN_FIELD_KEY);
            onAddField(chosenFieldType);
        }
    }
}
