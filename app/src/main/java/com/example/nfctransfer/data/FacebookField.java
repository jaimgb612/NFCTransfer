package com.example.nfctransfer.data;

import com.example.nfctransfer.R;
import com.example.nfctransfer.data.enumerations.Deletion;
import com.example.nfctransfer.data.enumerations.ProfileEntityType;
import com.example.nfctransfer.data.enumerations.ProfileFieldType;

public class FacebookField extends AProfileDataField {

    public FacebookField(String fbUserName, String userId, boolean sharedStatus) {

        this.type = ProfileFieldType.FACEBOOK;
        this.entityType = ProfileEntityType.SOCIAL_ACCOUNT;
        this.iconResource = R.drawable.facebook500;
        this.fieldName = "facebook";
        this.fieldDisplayName = "Facebook";
        this.value = fbUserName;
        this.socialId = userId;
        this.deletableType = Deletion.ONLY_DELETABLE;
        this.shared = sharedStatus;
        this.shared = sharedStatus;
    }

    public FacebookField() {
        this("", "", true);
    }

    public AProfileDataField copy() {
        AProfileDataField c = new FacebookField();

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
