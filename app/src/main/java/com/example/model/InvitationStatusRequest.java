package com.example.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class InvitationStatusRequest implements Serializable {

    @SerializedName("status")
    private String status;

    public InvitationStatusRequest() {}

    public InvitationStatusRequest(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
