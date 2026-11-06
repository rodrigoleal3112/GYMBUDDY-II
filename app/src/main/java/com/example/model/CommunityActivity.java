package com.example.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class CommunityActivity implements Serializable {

    @SerializedName("id")
    private String id;

    @SerializedName("communityId")
    private String communityId;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("category")
    private String category;

    @SerializedName("xpReward")
    private int xpReward;

    @SerializedName("schedule")
    private String schedule;

    @SerializedName("creatorName")
    private String creatorName;

    @SerializedName("participantsCount")
    private int participantsCount;

    @SerializedName("isParticipating")
    private boolean isParticipating;

    public CommunityActivity() {}

    public CommunityActivity(String id, String communityId, String title, String description,
                             String category, int xpReward, String schedule, String creatorName,
                             int participantsCount, boolean isParticipating) {
        this.id = id;
        this.communityId = communityId;
        this.title = title;
        this.description = description;
        this.category = category;
        this.xpReward = xpReward;
        this.schedule = schedule;
        this.creatorName = creatorName;
        this.participantsCount = participantsCount;
        this.isParticipating = isParticipating;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCommunityId() { return communityId; }
    public void setCommunityId(String communityId) { this.communityId = communityId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getXpReward() { return xpReward; }
    public void setXpReward(int xpReward) { this.xpReward = xpReward; }

    public String getSchedule() { return schedule; }
    public void setSchedule(String schedule) { this.schedule = schedule; }

    public String getCreatorName() { return creatorName; }
    public void setCreatorName(String creatorName) { this.creatorName = creatorName; }

    public int getParticipantsCount() { return participantsCount; }
    public void setParticipantsCount(int participantsCount) { this.participantsCount = participantsCount; }

    public boolean isParticipating() { return isParticipating; }
    public void setParticipating(boolean participating) { isParticipating = participating; }
}
