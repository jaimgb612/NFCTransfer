package com.example.nfctransfer.networking.ApiResponses.PullSelfProfile;

import java.io.Serializable;

public class ProfileField implements Serializable {
    private String name;

    private String text_value;

    private String social_id;

    private Boolean shared_status;

    public String getName() {
        return name;
    }

    public String getTextValue() {
        return text_value;
    }

    public String getSocialId() {
        return social_id;
    }

    public Boolean getSharedStatus() {
        return shared_status;
    }
}
