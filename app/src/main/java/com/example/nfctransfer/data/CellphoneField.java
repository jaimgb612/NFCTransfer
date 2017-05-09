package com.example.nfctransfer.data;

import com.example.nfctransfer.R;
import com.example.nfctransfer.data.enumerations.ProfileFieldType;

public class CellphoneField extends AProfileDataField {

    public CellphoneField(String cellphoneNb) {

        this.type = ProfileFieldType.CELLPHONE;
        this.iconResource = R.drawable.phone370;
        this.fieldName = "cellphone";
        this.fieldDisplayName = "Cellphone";
        this.value = cellphoneNb;
        this.isDeletable = Deletion.NOT_DELETABLE;
    }

    public CellphoneField() {
        this("");
    }


}
