package com.example.nfctransfer.data;

import com.example.nfctransfer.R;
import com.example.nfctransfer.data.enumerations.Deletion;
import com.example.nfctransfer.data.enumerations.ProfileEntityType;
import com.example.nfctransfer.data.enumerations.ProfileFieldType;

public class LinkedInField extends AProfileDataField {

    public LinkedInField(String linkedInUserName, String userId, boolean sharedStatus) {

        this.type = ProfileFieldType.LINKEDIN;
        this.entityType = ProfileEntityType.SOCIAL_ACCOUNT;
        this.iconResource = R.drawable.linkedin;
        this.fieldName = "linkedin";
        this.fieldDisplayName = "LinkedIn";
        this.value = linkedInUserName;
        this.socialId = userId;
        this.deletableType = Deletion.ONLY_DELETABLE;
        this.shared = sharedStatus;
    }

    public LinkedInField() {
        this("", "", true);
    }

    public AProfileDataField copy() {
        AProfileDataField c = new LinkedInField();

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
