package com.example.nfctransfer.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.nfctransfer.R;
import com.example.nfctransfer.data.AProfileDataField;
import com.example.nfctransfer.fragments.ProfileFragment;
import com.kyleduo.switchbutton.SwitchButton;

import java.util.List;

public class ProfileDataViewAdapter extends RecyclerView.Adapter<ProfileDataViewAdapter.ViewHolder> {

    private Context context;
    private List<AProfileDataField> fields;
    private int position;

    public ProfileDataViewAdapter(Context _context, List<AProfileDataField> _fields) {

        this.context = _context;
        this.fields = _fields;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_data_field_row, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final AProfileDataField field = fields.get(position);

        holder.fieldIcon.setImageResource(field.getIconResource());
        holder.fieldTitle.setText(field.getFieldName());
        holder.fieldValue.setText(field.getValue());
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

        public ViewHolder(View itemView) {
            super(itemView);

            fieldIcon = (ImageView) itemView.findViewById(R.id.field_icon);
            fieldTitle = (TextView) itemView.findViewById(R.id.field_title);
            fieldValue = (TextView) itemView.findViewById(R.id.field_value);
            shareSwitch = (SwitchButton) itemView.findViewById(R.id.field_share);

            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(Menu.NONE, ProfileFragment.CONTEXT_MENU_ITEM_DELETE, Menu.NONE, "Remove");
            menu.add(Menu.NONE, ProfileFragment.CONTEXT_MENU_ITEM_EDIT, Menu.NONE, "Edit");
        }
    }

    public void removeField(int position) {
        fields.remove(position);
        notifyItemRemoved(position);
    }
}
