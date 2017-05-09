package com.example.nfctransfer.data;

import com.example.nfctransfer.data.enumerations.Deletion;
import com.example.nfctransfer.data.enumerations.ProfileEntityType;
import com.example.nfctransfer.data.enumerations.ProfileFieldType;

public abstract class AProfileDataField {

    protected String fieldDisplayName;

    protected String fieldName;

    protected String value;

    protected Integer iconResource;

    protected String userId;

    protected ProfileFieldType type;

    protected ProfileEntityType entityType;

    protected Deletion deletableType;

    protected boolean shared;

    public AProfileDataField() {}

    public String getFieldName() {
        return this.fieldName;
    }

    public String getFieldDisplayName() {
        return this.fieldDisplayName;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getIconResource() {
        return this.iconResource;
    }

    public ProfileFieldType getFieldType() { return this.type; }

    public ProfileEntityType getFieldEntityType() { return this.entityType; }

    public boolean isShared() {
        return this.shared;
    }

    public void setShared(boolean shared) {
        this.shared = shared;
    }

    public Deletion isDeletable() {
        return this.deletableType;
    }

    public void setDeletableType(Deletion deletable) {
        this.deletableType = deletable;
    }

    public static AProfileDataField getEmptyInstance(ProfileFieldType type) {
        switch (type) {
            case CELLPHONE:
                return new CellphoneField();
            case EMAIL:
                return new EmailField();
            case ADDRESS:
                return new HomeAddressField();
            case FACEBOOK:
                return new FacebookField();
            case TWITTER:
                return new TwitterField();
            case LINKEDIN:
                return new LinkedInField();
        }
        return null;
    }

    public static AProfileDataField getInstance(ProfileFieldType type, String textValue, String socialId, boolean sharedStatus) {
        switch (type) {
            case CELLPHONE:
                return new CellphoneField(textValue, sharedStatus);
            case EMAIL:
                return new EmailField(textValue, sharedStatus);
            case ADDRESS:
                return new HomeAddressField(textValue, sharedStatus);
            case FACEBOOK:
                return new FacebookField(textValue, socialId, sharedStatus);
            case TWITTER:
                return new TwitterField(textValue, socialId, sharedStatus);
            case LINKEDIN:
                return new LinkedInField(textValue, socialId, sharedStatus);
        }
        return null;
    }
}
