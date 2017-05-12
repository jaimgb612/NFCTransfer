package com.example.nfctransfer.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.nfctransfer.R;
import com.example.nfctransfer.activities.ProfileDisplayer;
import com.example.nfctransfer.adapters.MatchedProfilesAdapter;
import com.example.nfctransfer.networking.ApiResponses.Profile.Profile;
import com.example.nfctransfer.sqLite.MatchedProfilesDB;

import java.util.ArrayList;
import java.util.List;

public class MatchListFragments extends Fragment {

    private TextView sharedFragmentNoShared;
    private ListView listView;
    private MatchedProfilesDB db = null;
    private List<Profile> profiles = null;
    private MatchedProfilesAdapter listAdapter;
    private boolean listVisible;

    private void initComponenents(){
        listVisible = false;
        profiles = new ArrayList<>();
        sharedFragmentNoShared = (TextView) getActivity().findViewById(R.id.shared_fragment_no_shared);
        listView = (ListView) getActivity().findViewById(R.id.match_listview);
        listAdapter = new MatchedProfilesAdapter(getActivity(), 0, profiles);
        listView.setAdapter(listAdapter);
        //setOnRecentlyBeamedUserCallback();
        setListViewItemActions();
    }

    private void setListViewItemActions(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Profile profile = (Profile) adapterView.getItemAtPosition(position);
                Intent intent = new Intent(getActivity(), ProfileDisplayer.class);
                intent.putExtra(ProfileDisplayer.EXTRA_KEY_PROFILE, profile);
                intent.putExtra(ProfileDisplayer.EXTRA_KEY_INSERT_IN_DATABASE, false);
                getActivity().startActivity(intent);
            }
        });
        registerForContextMenu(listView);
    }


    private void updateListView(){
        if (!listVisible) {
            sharedFragmentNoShared.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            listVisible = true;
        }
        listAdapter.notifyDataSetChanged();
    }

    private void getUserList(){
        db.openForRead();
        List<Profile> userTmp = db.getAllSharedUserDatas();
        db.close();

        if (userTmp != null){
            profiles.addAll(userTmp);
            updateListView();
        }
    }

    private void removeSharedUser(Profile profile){
        db.openForWrite();
        db.removeUserWithUserId(profile.getId());
        db.close();

        profiles.remove(profile);
        listAdapter.notifyDataSetChanged();

        if (profiles.isEmpty()){
            sharedFragmentNoShared.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            listVisible = false;
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        menu.setHeaderTitle(R.string.profile_row_menu_title);
        inflater.inflate(R.menu.menu_shared_contact_row, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()) {
            case R.id.menu_profile_delete:
                removeSharedUser(profiles.get(info.position));
                return true;
            default:
                break;
        }
        return false;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_match_list_fragments, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = new MatchedProfilesDB(getContext());

        initComponenents();
        getUserList();
    }
}
