package com.example.nfctransfer.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
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
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.nfctransfer.R;
import com.example.nfctransfer.utils.AlertDialogTools;
import com.example.nfctransfer.utils.FieldEntryParser;
import com.example.nfctransfer.activities.AddFieldActivity;
import com.example.nfctransfer.activities.MainActivity;
import com.example.nfctransfer.adapters.ProfileDataViewAdapter;
import com.example.nfctransfer.data.AProfileDataField;
import com.example.nfctransfer.data.enumerations.Deletion;
import com.example.nfctransfer.data.enumerations.ProfileEntityType;
import com.example.nfctransfer.data.enumerations.ProfileFieldType;
import com.example.nfctransfer.networking.ApiResponses.Profile.ProfileField;
import com.example.nfctransfer.networking.ApiResponses.Profile.PullProfileResponse;
import com.example.nfctransfer.networking.ApiResponses.Profile.Profile;
import com.example.nfctransfer.networking.ApiResponses.SimpleResponse;
import com.example.nfctransfer.networking.HttpCodes;
import com.example.nfctransfer.networking.NfcTransferApi;
import com.example.nfctransfer.networking.Session;
import com.example.nfctransfer.socialAPIs.ASocialAPI;
import com.example.nfctransfer.socialAPIs.FacebookAPI;
import com.example.nfctransfer.socialAPIs.LinkedinAPI;
import com.example.nfctransfer.socialAPIs.SynchronizableElement;
import com.example.nfctransfer.socialAPIs.TwitterAPI;
import com.example.nfctransfer.websockets.SocketConnectionWatcher;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment implements SocketConnectionWatcher {

    public final static int CONTEXT_MENU_ITEM_DELETE = 0;
    public final static int CONTEXT_MENU_ITEM_EDIT = 1;
    public final static int INTENT_CHOSEN_FIELD_KEY = 1;
    public final static String INTENT_EXTRA_CHOSEN_FIELD_KEY = "extra_chosen_field_key";

    private Context context;
    private Activity activity;

    private ProfileDataViewAdapter mAdapter;
    private RecyclerView mProfileView;
    private SwipeRefreshLayout mRefreshLayout;
    private FieldEntryParser mFieldEntryParser;
    private FloatingActionButton mButtonAddField;
    private TextView mUserNameTitle;

    private List<ProfileFieldType> allFields = new ArrayList<>();
    private List<AProfileDataField> profileFields = new ArrayList<>();

    private enum ApiTaskType {
        CREATE,
        UPDATE,
        DELETE,
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
        mProfileView.setNestedScrollingEnabled(false);
        mButtonAddField = (FloatingActionButton) activity.findViewById(R.id.button_add_field);

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

        mAdapter.notifyDataSetChanged();

        mAdapter.setSharedStatusListener(new ProfileDataViewAdapter.SharedStatusChangedListener() {
            @Override
            public void onSharedStatusChanged(AProfileDataField field, AProfileDataField backupField, int position) {
                requestEditField(field, backupField, position);
            }
        });

        mUserNameTitle = (TextView ) activity.findViewById(R.id.user_profile_name);

        registerForContextMenu(mProfileView);
    }

    private void showErrorDialog(int titleRes, String errMsg) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getActivity());

        alertDialogBuilder.setTitle(titleRes);
        alertDialogBuilder
                .setMessage(errMsg)
                .setCancelable(false)
                .setPositiveButton(R.string.OK, null);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void showErrorDialog(int titleRes, int msgRes) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getActivity());

        alertDialogBuilder.setTitle(titleRes);
        alertDialogBuilder
                .setMessage(msgRes)
                .setCancelable(false)
                .setPositiveButton(R.string.OK, null);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void showSocialFieldSyncFailDialog(ProfileFieldType fieldType) {
        Resources resources = getResources();
        String errMsg;

        switch (fieldType) {
            case FACEBOOK:
                errMsg = resources.getString(R.string.err_facebook_sync_fail_msg);
                break;
            case LINKEDIN:
                errMsg = resources.getString(R.string.err_linkedin_sync_fail_msg);
                break;
            case TWITTER:
                errMsg = resources.getString(R.string.err_twitter_sync_fail_msg);
                break;
            default:
                errMsg = null;
                break;
        }

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getActivity());

        alertDialogBuilder.setTitle(R.string.oops);
        alertDialogBuilder
                .setMessage(errMsg)
                .setPositiveButton(R.string.OK, null);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void showSocialFieldSyncRequireAppInstall(ProfileFieldType fieldType) {
        Resources resources = getResources();
        String errMsg;

        switch (fieldType) {
            case TWITTER:
                errMsg = resources.getString(R.string.err_twitter_not_installed);
                break;
            default:
                errMsg = null;
                break;
        }

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getActivity());

        alertDialogBuilder.setTitle(R.string.oops);
        alertDialogBuilder
                .setMessage(errMsg)
                .setPositiveButton(R.string.OK, null);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void rollbackOnRequestFail(AProfileDataField field, ApiTaskType taskType, int position) {

        switch (taskType) {
            case CREATE:
                mAdapter.removeField(position);
                break;
            case UPDATE:
                mAdapter.editFieldAtPosition(field, position);
                break;
            case DELETE:
                mAdapter.addFieldAtPosition(field, position);
                break;
        }

        showErrorDialog(R.string.user_err_edit_content, R.string.user_err_fail_connection_desc);
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

    private void pullDataForCurrentUser() {
        Call<PullProfileResponse> call;

        call = NfcTransferApi.getInstance().getSelfProfileData(Session.accessToken);
        call.enqueue(new Callback<PullProfileResponse>() {
            @Override
            public void onResponse(Call<PullProfileResponse> call, Response<PullProfileResponse> response) {
                int code = response.code();
                PullProfileResponse result;

                if (code != HttpCodes.OK) {
                    return;
                }
                result = response.body();
                onCurrentUserDataPulled(result.getUser());
            }

            @Override
            public void onFailure(Call<PullProfileResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void onCurrentUserDataPulled(Profile profile) {

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
        onItemsLoadComplete();
    }

    private void addSocialField(ProfileFieldType fieldType, SynchronizableElement se) {

        String textValue = se.getCompleteIdentity();
        String socialId = se.getUserId();

        AProfileDataField field = AProfileDataField.getInstance(fieldType, textValue, socialId, true);

        mAdapter.addField(field);

        requestAddField(field, profileFields.size() - 1);
    }

    private void onAddField(ProfileFieldType fieldType) {
        AProfileDataField field = AProfileDataField.getEmptyInstance(fieldType);

        if (field.getFieldEntityType() == ProfileEntityType.DATA) {
            addEditableField(field);
        }
        else {
            switch (fieldType) {
                case FACEBOOK:
                    synchronizeWithFacebookApi();
                    break;
                case LINKEDIN:
                    synchronizeWithLinkedinApi();
                    break;
                case TWITTER:
                    synchronizeWithTwitterApi();
                    break;
            }
        }
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
                            showErrorDialog(R.string.err_wrong_input_format_title, mFieldEntryParser.getLastErrMsg());
                        }
                        else {
                            field.setValue(inputValue);
                            mAdapter.addField(field);
                            requestAddField(field, profileFields.size() - 1);
                            dialog.dismiss();
                        }
                    }
                });
            }
        });
        dialog.show();
    }

    private void requestAddField(final AProfileDataField field, final int insertedAtPosition) {

        Call<SimpleResponse> call;
        call = NfcTransferApi.getInstance().addProfileField(Session.accessToken,
                field.getFieldName(), field.getValue(), field.getSocialId());

        call.enqueue(new Callback<SimpleResponse>() {
            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                int code = response.code();

                if (code != HttpCodes.CREATED) {
                    rollbackOnRequestFail(field, ApiTaskType.CREATE, insertedAtPosition);
                }
            }

            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {
                t.printStackTrace();
                rollbackOnRequestFail(field, ApiTaskType.CREATE, insertedAtPosition);
            }
        });
    }

    private void onEditField(final int position) {
        final AProfileDataField originalField = profileFields.get(position);
        final AProfileDataField backupField = originalField.copy();
        final ProfileFieldType fieldType = originalField.getFieldType();

        AlertDialogTools alertDialogTools = new AlertDialogTools(getActivity(), originalField);
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

        input.setText(originalField.getValue());

        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                final Button okButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String inputValue = input.getText().toString();

                        if (!mFieldEntryParser.parse(inputValue, fieldType)) {
                            showErrorDialog(R.string.err_wrong_input_format_title, mFieldEntryParser.getLastErrMsg());
                        }
                        else {
                            originalField.setValue(inputValue);
                            mAdapter.editField(position);
                            requestEditField(originalField, backupField, position);
                            dialog.dismiss();
                        }
                    }
                });
            }
        });
        dialog.show();
    }

    private void requestEditField(final AProfileDataField field, final AProfileDataField backupField, final int atPosition) {

        Call<SimpleResponse> call;
        call = NfcTransferApi.getInstance().editProfileField(Session.accessToken,
                field.getFieldName(), field.getValue(), field.getSocialId(), field.isShared());

        call.enqueue(new Callback<SimpleResponse>() {
            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                int code = response.code();

                if (code != HttpCodes.OK) {
                    rollbackOnRequestFail(backupField, ApiTaskType.UPDATE, atPosition);
                }
            }

            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {
                t.printStackTrace();
                rollbackOnRequestFail(backupField, ApiTaskType.UPDATE, atPosition);
            }
        });
    }

    private void onDeleteField(int position) {
        AProfileDataField field = profileFields.get(position);
        mAdapter.removeField(position);
        requestDeleteField(field, position);
    }

    private void requestDeleteField(final AProfileDataField field, final int atPosition) {

        Call<SimpleResponse> call;
        call = NfcTransferApi.getInstance().deleteProfileField(Session.accessToken, field.getFieldName());

        call.enqueue(new Callback<SimpleResponse>() {
            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                int code = response.code();

                if (code != HttpCodes.OK) {
                    rollbackOnRequestFail(field, ApiTaskType.DELETE, atPosition);
                }
            }

            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {
                t.printStackTrace();
                rollbackOnRequestFail(field, ApiTaskType.DELETE, atPosition);
            }
        });
    }

    private void synchronizeWithFacebookApi() {

        FacebookAPI.getInstance().synchronizeWithSocialApi(context, getActivity(), new ASocialAPI.ApiSyncCallBack() {
            @Override
            public void synchronizationResult(boolean status) {
                if (status) {
                    FacebookAPI.getInstance().getProfileData(context, getActivity(), new ASocialAPI.ApiProfileSyncCallBack() {
                        @Override
                        public void onProfileSynchronized(SynchronizableElement element) {
                            if (element == null) {
                                showSocialFieldSyncFailDialog(ProfileFieldType.FACEBOOK);
                            } else {
                                addSocialField(ProfileFieldType.FACEBOOK, element);
                            }
                        }
                    });
                } else {
                    showSocialFieldSyncFailDialog(ProfileFieldType.FACEBOOK);
                }
            }
        });
    }

    private void synchronizeWithTwitterApi() {

        TwitterAPI.getInstance().synchronizeWithSocialApi(context, getActivity(), new ASocialAPI.ApiSyncCallBack() {
            @Override
            public void synchronizationResult(boolean status) {
                if (status) {
                    TwitterAPI.getInstance().getProfileData(context, getActivity(), new ASocialAPI.ApiProfileSyncCallBack() {
                        @Override
                        public void onProfileSynchronized(SynchronizableElement element) {
                            if (element == null) {
                                showSocialFieldSyncFailDialog(ProfileFieldType.TWITTER);
                            } else {
                                addSocialField(ProfileFieldType.TWITTER, element);
                            }
                        }
                    });
                }
                else {
                    if (!TwitterAPI.getInstance().isApplicationInstalled(context)) {
                        showSocialFieldSyncRequireAppInstall(ProfileFieldType.TWITTER);
                    }
                    else {
                        showSocialFieldSyncFailDialog(ProfileFieldType.TWITTER);
                    }
                }
            }
        });
    }

    private void synchronizeWithLinkedinApi() {

        LinkedinAPI.getInstance().synchronizeWithSocialApi(context, getActivity(), new ASocialAPI.ApiSyncCallBack() {
            @Override
            public void synchronizationResult(boolean status) {
                if (status) {
                    LinkedinAPI.getInstance().getProfileData(context, getActivity(), new ASocialAPI.ApiProfileSyncCallBack() {
                        @Override
                        public void onProfileSynchronized(SynchronizableElement element) {
                            if (element == null) {
                                showSocialFieldSyncFailDialog(ProfileFieldType.LINKEDIN);
                            } else {
                                addSocialField(ProfileFieldType.LINKEDIN, element);
                            }
                        }
                    });
                } else {
                    showSocialFieldSyncFailDialog(ProfileFieldType.LINKEDIN);
                }
            }
        });
    }

    @Override
    public void onSocketConnected() {

    }

    @Override
    public void onSocketDisconnected() {

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
