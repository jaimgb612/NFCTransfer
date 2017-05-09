package com.example.nfctransfer.data;

import com.example.nfctransfer.R;
import com.example.nfctransfer.data.enumerations.Deletion;
import com.example.nfctransfer.data.enumerations.ProfileEntityType;
import com.example.nfctransfer.data.enumerations.ProfileFieldType;

public class EmailField extends AProfileDataField {

    public EmailField(String emailAddress, boolean sharedStatus) {

        this.type = ProfileFieldType.EMAIL;
        this.entityType = ProfileEntityType.DATA;
        this.iconResource = R.drawable.email;
        this.fieldName = "email";
        this.fieldDisplayName = "Email";
        this.value = emailAddress;
        this.deletableType = Deletion.DELETABLE_EDITABLE;
        this.shared = sharedStatus;
        this.socialId = null;
    }

    public EmailField() {
        this("", true);
    }

}
