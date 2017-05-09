package com.example.nfctransfer.data;

import com.example.nfctransfer.data.enumerations.ProfileFieldType;

public abstract class AProfileDataField {

    public enum Deletion {
        DELETABLE,
        NOT_DELETABLE
    }

    protected String fieldDisplayName;

    protected String fieldName;

    protected String value;

    protected Integer iconResource;

    protected String userId;

    protected ProfileFieldType type;

    protected Deletion isDeletable;

    private boolean shared;

    public AProfileDataField() {
        this.isDeletable = Deletion.DELETABLE;
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public String getFielDisplayName() {
        return this.fieldDisplayName;
    }

    public String getValue() {
        return this.value;
    }

    public Integer getIconResource() {
        return this.iconResource;
    }

    public ProfileFieldType getFieldType() { return this.type; }

    public boolean isShared() {
        return this.shared;
    }

    public void setShared(boolean shared) {
        this.shared = shared;
    }

    public Deletion isDeletable() {
        return this.isDeletable;
    }

    public void setDeletable(Deletion deletable) {
        this.isDeletable = deletable;
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

    public static AProfileDataField getInstance(ProfileFieldType type, String textValue, String socialId) {
        switch (type) {
            case CELLPHONE:
                return new CellphoneField(textValue);
            case EMAIL:
                return new EmailField(textValue);
            case ADDRESS:
                return new HomeAddressField(textValue);
            case FACEBOOK:
                return new FacebookField(textValue, socialId);
            case TWITTER:
                return new TwitterField(textValue, socialId);
            case LINKEDIN:
                return new LinkedInField(textValue, socialId);
        }
        return null;
    }
}
