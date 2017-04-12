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

public class AddFieldViewAdapter extends ArrayAdapter<AProfileDataField> {

    private Context context;
    private List<AProfileDataField> fields;

    public AddFieldViewAdapter(Context context, int resource, List<AProfileDataField> objects) {
        super(context, resource, objects);
        this.context = context;
        this.fields = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AProfileDataField field = fields.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.add_field_list_row, parent, false);
        }

        ((TextView)convertView.findViewById(R.id.field_name)).setText(field.getFieldName());
        ((ImageView)convertView.findViewById(R.id.field_icon)).setImageResource(field.getIconResource());
        //NOBUG ((ImageView)convertView.findViewById(R.id.chooseFieldListAppLogo)).setImageResource(R.drawable.snapchat);

        return convertView;
    }

}
