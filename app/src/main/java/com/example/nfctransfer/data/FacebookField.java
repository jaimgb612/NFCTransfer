package com.example.nfctransfer.data;

import com.example.nfctransfer.R;
import com.example.nfctransfer.data.enumerations.ProfileFieldType;

public class FacebookField extends AProfileDataField {

    public FacebookField(String fbUserName, String userId) {

        this.type = ProfileFieldType.FACEBOOK;
        this.iconResource = R.drawable.facebook500;
        this.fieldName = "Facebook";
        this.value = fbUserName;
        this.userId = userId;
    }

    public FacebookField() {
        this("", "");
    }
}
