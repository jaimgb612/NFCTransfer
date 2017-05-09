package com.example.nfctransfer.data;

import com.example.nfctransfer.R;
import com.example.nfctransfer.data.enumerations.ProfileFieldType;

public class LinkedInField extends AProfileDataField {

    public LinkedInField(String linkedInUserName, String userId) {

        this.type = ProfileFieldType.LINKEDIN;
        this.iconResource = R.drawable.linkedin;
        this.fieldName = "linkedin";
        this.fieldDisplayName = "LinkedIn";
        this.value = linkedInUserName;
        this.userId = userId;
        this.isDeletable = Deletion.DELETABLE;
    }

    public LinkedInField() {
        this("", "");
    }
}
