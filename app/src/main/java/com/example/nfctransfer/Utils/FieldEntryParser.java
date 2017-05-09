package com.example.nfctransfer.Utils;

import android.content.Context;

import com.example.nfctransfer.R;
import com.example.nfctransfer.data.enumerations.ProfileFieldType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FieldEntryParser {

    private Pattern pattern;
    private Matcher matcher;
    private String errMsg;
    private Context context;

    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                                              + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static final String TELEPHONE_PATTERN = "^(\\+?1)?[2-9]\\d{2}[2-9](?!11)\\d{6}$";
    private static final String NAME_PATTERN = "^[^0-9@&\"()!_$*€£`+=\\/;?#]{3,30}$";
    private static final String TEXT_PATTERN = "^[^@&\"()!_$*€£`+=\\/;?#]{5,96}$";

    public FieldEntryParser(Context _context) {
        context = _context;
    }

    private boolean parseName(final String value) {
        boolean ret;

        pattern = Pattern.compile(NAME_PATTERN);
        matcher = pattern.matcher(value);
        ret = matcher.matches();
        if (!ret){
            errMsg = context.getString(R.string.err_wrong_name_format);
        }
        return ret;
    }

    private boolean parseEmail(final String value) {
        boolean ret;

        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(value);
        ret = matcher.matches();
        if (!ret){
            errMsg = context.getString(R.string.err_wrong_email_format);
        }
        return ret;
    }

    private boolean parseTelephone(String value) {
        boolean ret;

        pattern = Pattern.compile(TELEPHONE_PATTERN);
        matcher = pattern.matcher(value);
        ret = matcher.matches();
        if (!ret) {
            errMsg = context.getString(R.string.err_wrong_telephone_format);
        }
        return ret;
    }

    private boolean parseText(String value) {
        boolean ret;

        pattern = Pattern.compile(TEXT_PATTERN);
        matcher = pattern.matcher(value);
        ret = matcher.matches();
        if (!ret){
            errMsg = context.getString(R.string.err_wrong_text_format);
        }
        return ret;
    }

    public boolean parse(String value, ProfileFieldType type) {
        if (type == ProfileFieldType.EMAIL) {
            return parseEmail(value);
        }
        else if (type == ProfileFieldType.CELLPHONE) {
            return parseTelephone(value);
        }
        else if (type == ProfileFieldType.FIRSTNAME || type == ProfileFieldType.LASTNAME) {
            return parseName(value);
        }
        else {
            return parseText(value);
        }
    }

    public String getLastErrMsg() {
        return errMsg;
    }
}
