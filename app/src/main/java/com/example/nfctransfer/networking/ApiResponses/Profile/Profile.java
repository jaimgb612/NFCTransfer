package com.example.nfctransfer.networking.ApiResponses.Profile;

import com.example.nfctransfer.networking.ApiResponses.Profile.ProfileField;

import java.io.Serializable;
import java.util.List;

public class Profile implements Serializable {

    private String _id;
    private String firstname;
    private String lastname;
    private List<ProfileField> fields;

    public String getId() {
        return _id;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public List<ProfileField> getFields() {
        return fields;
    }
}
