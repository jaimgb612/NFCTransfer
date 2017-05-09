package com.example.nfctransfer.data;

import com.example.nfctransfer.R;
import com.example.nfctransfer.data.enumerations.Deletion;
import com.example.nfctransfer.data.enumerations.ProfileEntityType;
import com.example.nfctransfer.data.enumerations.ProfileFieldType;

public class CellphoneField extends AProfileDataField {

    public CellphoneField(String cellphoneNb, boolean sharedStatus) {

        this.type = ProfileFieldType.CELLPHONE;
        this.entityType = ProfileEntityType.DATA;
        this.iconResource = R.drawable.phone370;
        this.fieldName = "cellphone";
        this.fieldDisplayName = "Cellphone";
        this.value = cellphoneNb;
        this.deletableType = Deletion.ONLY_EDITABLE;
        this.shared = sharedStatus;
        this.socialId = null;
    }

    public CellphoneField() {
        this("", true);
    }


}
