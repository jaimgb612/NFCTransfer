package com.example.nfctransfer.data.enumerations;

import java.io.Serializable;

public enum ProfileFieldType implements Serializable {

    FIRSTNAME("firstname"),
    LASTNAME("lastname"),
    CELLPHONE("cellphone"),
    EMAIL("email"),
    ADDRESS("home_address"),
    FACEBOOK("facebook"),
    TWITTER("twitter"),
    LINKEDIN("linkedin");

    private String name;

    ProfileFieldType(String name) {
        this.name = name;
    }

    public String getText() {
        return this.name;
    }

    public static ProfileFieldType fromString(String name) {
        for (ProfileFieldType t : ProfileFieldType.values()) {
            if (t.name.equalsIgnoreCase(name)) {
                return t;
            }
        }
        return null;
    }
}
