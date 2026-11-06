package com.example.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Badge implements Serializable {
    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("xpReward")
    private int xpReward;

    @SerializedName("isUnlocked")
    private boolean isUnlocked;

    @SerializedName("unlockedDate")
    private String unlockedDate;

    public Badge() {}

    public Badge(String id, String name, String description, int xpReward, boolean isUnlocked, String unlockedDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.xpReward = xpReward;
        this.isUnlocked = isUnlocked;
        this.unlockedDate = unlockedDate;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getXpReward() { return xpReward; }
    public void setXpReward(int xpReward) { this.xpReward = xpReward; }

    public boolean isUnlocked() { return isUnlocked; }
    public void setUnlocked(boolean unlocked) { isUnlocked = unlocked; }

    public String getUnlockedDate() { return unlockedDate; }
    public void setUnlockedDate(String unlockedDate) { this.unlockedDate = unlockedDate; }
}
