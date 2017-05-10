package com.example.nfctransfer.data;

import com.example.nfctransfer.R;
import com.example.nfctransfer.data.enumerations.Deletion;
import com.example.nfctransfer.data.enumerations.ProfileEntityType;
import com.example.nfctransfer.data.enumerations.ProfileFieldType;

public class HomeAddressField extends AProfileDataField {

    public HomeAddressField(String homeAddress, boolean sharedStatus) {

        this.type = ProfileFieldType.ADDRESS;
        this.entityType = ProfileEntityType.DATA;
        this.iconResource = R.drawable.map103;
        this.fieldName = "home_address";
        this.fieldDisplayName = "Address";
        this.value = homeAddress;
        this.deletableType = Deletion.DELETABLE_EDITABLE;
        this.shared = sharedStatus;
        this.socialId = null;
    }

    public HomeAddressField() {
        this("", true);
    }

    public AProfileDataField copy() {
        AProfileDataField c = new HomeAddressField();

        c.type = this.type;
        c.entityType = this.entityType;
        c.iconResource = this.iconResource;
        c.fieldName = this.fieldName;
        c.fieldDisplayName = this.fieldDisplayName;
        c.value = this.value;
        c.deletableType = this.deletableType;
        c.shared = this.shared;
        c.socialId = this.socialId;

        return c;
    }
}
