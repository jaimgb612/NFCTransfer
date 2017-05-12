package com.example.nfctransfer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nfctransfer.R;
import com.example.nfctransfer.networking.ApiResponses.Profile.Profile;

import java.util.List;

public class MatchedProfilesAdapter extends ArrayAdapter<Profile> {
    public MatchedProfilesAdapter(Context context, int resource, List<Profile> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        setNotifyOnChange(true);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.users_list_view_row, parent, false);
        }

        Profile profile = getItem(position);

        String completeName = profile.getFirstname() + " " + profile.getLastname();
        ((TextView)convertView.findViewById(R.id.users_list_view_profile_name)).setText(completeName);
        ((ImageView)convertView.findViewById(R.id.users_list_view_profile_pic)).setImageResource(R.drawable.profile_pic_unknow);

        return convertView;
    }
}