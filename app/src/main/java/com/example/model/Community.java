package com.example.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Community implements Serializable {
    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("creatorName")
    private String creatorName;

    @SerializedName("memberCount")
    private int memberCount;

    @SerializedName("status")
    private String status;

    @SerializedName("isMember")
    private boolean isMember;

    public Community() {}

    public Community(String id, String name, String description, String creatorName, int memberCount, String status, boolean isMember) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.creatorName = creatorName;
        this.memberCount = memberCount;
        this.status = status;
        this.isMember = isMember;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCreatorName() { return creatorName; }
    public void setCreatorName(String creatorName) { this.creatorName = creatorName; }

    public int getMemberCount() { return memberCount; }
    public void setMemberCount(int memberCount) { this.memberCount = memberCount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isMember() { return isMember; }
    public void setMember(boolean member) { isMember = member; }
}
