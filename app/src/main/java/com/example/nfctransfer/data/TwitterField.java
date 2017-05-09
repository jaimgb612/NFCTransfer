package com.example.nfctransfer.data;

import com.example.nfctransfer.R;
import com.example.nfctransfer.data.enumerations.ProfileFieldType;

public class TwitterField extends AProfileDataField {

    public TwitterField(String twitterUserName, String userId) {

        this.type = ProfileFieldType.TWITTER;
        this.iconResource = R.drawable.twitter;
        this.fieldName = "twitter";
        this.fieldDisplayName = "Twitter";
        this.value = twitterUserName;
        this.userId = userId;
        this.isDeletable = Deletion.DELETABLE;
    }

    public TwitterField() {
        this("", "");
    }
}
