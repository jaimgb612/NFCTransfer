package com.example.nfctransfer.data;

import com.example.nfctransfer.R;
import com.example.nfctransfer.data.enumerations.ProfileFieldType;

public class EmailField extends AProfileDataField {

    public EmailField(String emailAddress) {

        this.type = ProfileFieldType.EMAIL;
        this.iconResource = R.drawable.email;
        this.fieldName = "Email";
        this.value = emailAddress;
    }

    public EmailField() {
        this("");
    }

}
