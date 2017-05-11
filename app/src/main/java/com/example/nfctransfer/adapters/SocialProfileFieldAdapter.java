package com.example.nfctransfer.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nfctransfer.R;
import com.example.nfctransfer.data.AProfileDataField;
import com.example.nfctransfer.socialAPIs.SocialApiProfileViewer.SyncProfileViewer;

import java.util.List;

public class SocialProfileFieldAdapter extends ArrayAdapter<AProfileDataField> {

    private static final int DATA_MAX_SIZE = 22;
    private Context context;
    private SyncProfileViewer pv;

    public SocialProfileFieldAdapter(Context _context, Activity _activity, int resource, List<AProfileDataField> objects){
        super(_context, resource, objects);
        this.context = _context;
        pv = new SyncProfileViewer(context, _activity);
    }

    private String cutData(String data) {
        if (data.length() > DATA_MAX_SIZE) {
            data = data.substring(0, DATA_MAX_SIZE) + "...";
        }
        return (data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        setNotifyOnChange(true);
        final AProfileDataField field = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.profile_showcase_social_row, parent, false);
        }

        TextView identity = (TextView) convertView.findViewById(R.id.profile_showcase_social_identity);
        ImageView icon = (ImageView) convertView.findViewById(R.id.profile_showcase_social_icon);
        ImageButton button = (ImageButton) convertView.findViewById(R.id.profile_showcase_social_button);

        identity.setText(cutData(field.getValue()));
        icon.setImageResource(field.getIconResource());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pv.viewProfile(field);
            }
        });
        return convertView;
    }
}
