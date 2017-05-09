package com.example.nfctransfer.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.nfctransfer.R;
import com.example.nfctransfer.data.AProfileDataField;
import com.example.nfctransfer.data.enumerations.Deletion;
import com.example.nfctransfer.fragments.ProfileFragment;
import com.example.nfctransfer.networking.ApiResponses.PullSelfProfile.ProfileField;
import com.kyleduo.switchbutton.SwitchButton;

import java.util.List;

public class ProfileDataViewAdapter extends RecyclerView.Adapter<ProfileDataViewAdapter.ViewHolder> {

    private Context context;
    private List<AProfileDataField> fields;
    private int position;
    private View.OnCreateContextMenuListener listener;

    private static final int DATA_MAX_SIZE = 22;

    public ProfileDataViewAdapter(Context _context, List<AProfileDataField> _fields) {
        this.context = _context;
        this.fields = _fields;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_data_field_row, parent, false);
        return new ViewHolder(v, listener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final AProfileDataField field = fields.get(position);

        holder.fieldIcon.setImageResource(field.getIconResource());
        holder.fieldTitle.setText(field.getFieldDisplayName());
        holder.fieldValue.setText(cutData(field.getValue()));
        holder.shareSwitch.setChecked(field.isShared());
        holder.shareSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                field.setShared(isChecked);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setPosition(position);
                return false;
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AProfileDataField field = fields.get(position);
                boolean inversedShare = !field.isShared();

                field.setShared(inversedShare);
                SwitchButton button = (SwitchButton) v.findViewById(R.id.field_share);
                button.setChecked(inversedShare);
            }
        });
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        holder.itemView.setOnLongClickListener(null);
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        return fields.size();
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        ImageView fieldIcon;
        TextView fieldTitle;
        TextView fieldValue;
        SwitchButton shareSwitch;
        View.OnCreateContextMenuListener listener;

        public ViewHolder(View itemView, View.OnCreateContextMenuListener _listener) {
            super(itemView);

            listener = _listener;
            fieldIcon = (ImageView) itemView.findViewById(R.id.field_icon);
            fieldTitle = (TextView) itemView.findViewById(R.id.field_title);
            fieldValue = (TextView) itemView.findViewById(R.id.field_value);
            shareSwitch = (SwitchButton) itemView.findViewById(R.id.field_share);

            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            listener.onCreateContextMenu(menu, v, menuInfo);
        }
    }

    public void setCreateMenuListener(View.OnCreateContextMenuListener listener) {
        this.listener = listener;
    }

    public void removeAll() {
        if (!fields.isEmpty()) {
            int numberItems = fields.size();
            fields.clear();
            notifyItemRangeRemoved(0, numberItems);
        }
    }

    public void addField(AProfileDataField field) {
        fields.add(field);
        notifyItemInserted(fields.size() - 1);
    }

    public void addFieldAtPosition(AProfileDataField field, int position) {
        fields.add(position, field);
        notifyItemInserted(position);
    }

    public void editField(int position) {
        notifyItemChanged(position);
    }

    public void removeField(int position) {
        fields.remove(position);
        notifyItemRemoved(position);
    }

    private String cutData(String data) {
        if (data.length() > DATA_MAX_SIZE) {
            data = data.substring(0, DATA_MAX_SIZE) + "...";
        }
        if (data == null){
            return "";
        }
        return (data);
    }
}
