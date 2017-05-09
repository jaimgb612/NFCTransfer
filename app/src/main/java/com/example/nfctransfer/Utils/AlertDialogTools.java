package com.example.nfctransfer.Utils;

import android.content.Context;
import android.text.InputType;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.nfctransfer.R;
import com.example.nfctransfer.data.AProfileDataField;
import com.example.nfctransfer.data.enumerations.ProfileFieldType;

public class AlertDialogTools {

    private Context context;
    private String fieldCompleteName;
    private ProfileFieldType fieldType;

    public AlertDialogTools(Context _context, AProfileDataField field) {
        context = _context;
        fieldType = field.getFieldType();
        fieldCompleteName = field.getFieldDisplayName();
    }

    public String getMessage() {
        return (context.getString(R.string.user_prompt_data) + " " + fieldCompleteName);
    }

    public LinearLayout createAlertDialogLayout() {

        EditText alertDialogEditText = new EditText(context);
        LinearLayout alertDialogLayout = new LinearLayout(context);
        alertDialogEditText.setTextSize(context.getResources().getDimension(R.dimen._7sdp));
        alertDialogEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        alertDialogEditText.setHint(getFieldHint());
        alertDialogLayout.setOrientation(LinearLayout.VERTICAL);
        alertDialogLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(50, 50, 50, 50);
        alertDialogLayout.addView(alertDialogEditText, params);

        return (alertDialogLayout);
    }

    private String getFieldHint(){
        String inputHint = null;

        switch (fieldType) {
            case FIRSTNAME:
                inputHint = context.getString(R.string.firstname_hint);
                break;
            case LASTNAME:
                inputHint = context.getString(R.string.lastname_hint);
                break;
            case CELLPHONE:
                inputHint = context.getString(R.string.cellphone_hint);
                break;
            case EMAIL:
                inputHint = context.getString(R.string.email_hint);
                break;
            case ADDRESS:
                inputHint = context.getString(R.string.address_hint);
                break;
        }
        return (inputHint);
    }
}

