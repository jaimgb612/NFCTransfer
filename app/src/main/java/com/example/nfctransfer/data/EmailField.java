package com.example.nfctransfer.data;

import com.example.nfctransfer.R;
import com.example.nfctransfer.data.enumerations.ProfileFieldType;

public class EmailField extends AProfileDataField {

    public EmailField(String emailAddress) {

        this.type = ProfileFieldType.EMAIL;
        this.iconResource = R.drawable.email;
        this.fieldName = "email";
        this.fieldDisplayName = "Email";
        this.value = emailAddress;
        this.isDeletable = Deletion.DELETABLE;
    }

    public EmailField() {
        this("");
    }

}
