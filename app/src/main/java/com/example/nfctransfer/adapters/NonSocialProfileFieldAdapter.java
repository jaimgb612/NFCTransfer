package com.example.nfctransfer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nfctransfer.R;
import com.example.nfctransfer.data.AProfileDataField;

import java.util.List;

public class NonSocialProfileFieldAdapter extends ArrayAdapter<AProfileDataField> {

    private static final int DATA_MAX_SIZE = 22;

    public NonSocialProfileFieldAdapter(Context context, int resource, List<AProfileDataField> objects) {
        super(context, resource, objects);
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
        AProfileDataField field = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.profile_showcase_non_social_row, parent, false);
        }

        TextView title = (TextView) convertView.findViewById(R.id.profile_showcase_non_social_title);
        TextView data= (TextView) convertView.findViewById(R.id.profile_showcase_non_social_data);
        ImageView icon = (ImageView) convertView.findViewById(R.id.profile_showcase_non_social_icon);

        title.setText(field.getFieldDisplayName());
        data.setText(cutData(field.getValue()));
        icon.setImageResource(field.getIconResource());

        return convertView;
    }
}
