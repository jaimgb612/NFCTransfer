package com.example.nfctransfer.data;

import com.example.nfctransfer.R;
import com.example.nfctransfer.data.enumerations.ProfileFieldType;

public class HomeAddressField extends AProfileDataField {

    public HomeAddressField(String homeAddress) {

        this.type = ProfileFieldType.ADDRESS;
        this.iconResource = R.drawable.map103;
        this.fieldName = "home_address";
        this.fieldDisplayName = "Address";
        this.value = homeAddress;
        this.isDeletable = Deletion.DELETABLE;
    }

    public HomeAddressField() {
        this("");
    }
}
