package com.example.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Invitation implements Serializable {
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_ACCEPTED = "ACCEPTED";
    public static final String STATUS_DECLINED = "DECLINED";
    public static final String STATUS_CANCELLED = "CANCELLED";

    @SerializedName("id")
    private String id;

    @SerializedName("communityId")
    private String communityId;

    @SerializedName("communityName")
    private String communityName;

    @SerializedName("senderName")
    private String senderName;

    @SerializedName("recipientEmail")
    private String recipientEmail;

    @SerializedName("status")
    private String status;

    @SerializedName("date")
    private String date;

    @SerializedName("canCancel")
    private boolean canCancel;

    public Invitation() {}

    public Invitation(String id, String communityId, String communityName, String senderName, 
                      String recipientEmail, String status, String date, boolean canCancel) {
        this.id = id;
        this.communityId = communityId;
        this.communityName = communityName;
        this.senderName = senderName;
        this.recipientEmail = recipientEmail;
        this.status = status;
        this.date = date;
        this.canCancel = canCancel;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCommunityId() { return communityId; }
    public void setCommunityId(String communityId) { this.communityId = communityId; }

    public String getCommunityName() { return communityName; }
    public void setCommunityName(String communityName) { this.communityName = communityName; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public String getRecipientEmail() { return recipientEmail; }
    public void setRecipientEmail(String recipientEmail) { this.recipientEmail = recipientEmail; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public boolean canCancel() { return canCancel; }
    public void setCanCancel(boolean canCancel) { this.canCancel = canCancel; }
}
