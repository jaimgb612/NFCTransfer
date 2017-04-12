package com.example.nfctransfer.data;

import com.example.nfctransfer.R;
import com.example.nfctransfer.data.enumerations.ProfileFieldType;

public class HomeAddressField extends AProfileDataField {

    public HomeAddressField(String homeAddress) {

        this.type = ProfileFieldType.ADDRESS;
        this.iconResource = R.drawable.map103;
        this.fieldName = "Address";
        this.value = homeAddress;
    }

    public HomeAddressField() {
        this("");
    }
}
