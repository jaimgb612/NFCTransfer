package com.example.nfctransfer.socialAPIs;

public class SynchronizableElement {

    private String userId;
    private String completeIdentity;

    public SynchronizableElement() {}

    public SynchronizableElement(String _userId, String _completeIdentity) {
        userId = _userId;
        completeIdentity = _completeIdentity;
    }

    public void setUserId(String _userId) {
        userId = _userId;
    }

    public void setCompleteIdentity(String _completeIdentity) {
        completeIdentity = _completeIdentity;
    }

    public String getUserId(){
        return userId;
    }

    public String getCompleteIdentity() {
        return completeIdentity;
    }
}
