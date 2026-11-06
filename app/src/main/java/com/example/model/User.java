package com.example.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class User implements Serializable {
    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName("level")
    private int level;

    @SerializedName("xp")
    private int xp;

    @SerializedName("xpNextLevel")
    private int xpNextLevel;

    @SerializedName("streakDays")
    private int streakDays;

    @SerializedName("totalCheckins")
    private int totalCheckins;

    @SerializedName("badgesCount")
    private int badgesCount;

    public User() {}

    public User(String id, String name, String email, int level, int xp, int xpNextLevel, int streakDays, int totalCheckins, int badgesCount) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.level = level;
        this.xp = xp;
        this.xpNextLevel = xpNextLevel;
        this.streakDays = streakDays;
        this.totalCheckins = totalCheckins;
        this.badgesCount = badgesCount;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public int getXp() { return xp; }
    public void setXp(int xp) { this.xp = xp; }

    public int getXpNextLevel() { return xpNextLevel; }
    public void setXpNextLevel(int xpNextLevel) { this.xpNextLevel = xpNextLevel; }

    public int getStreakDays() { return streakDays; }
    public void setStreakDays(int streakDays) { this.streakDays = streakDays; }

    public int getTotalCheckins() { return totalCheckins; }
    public void setTotalCheckins(int totalCheckins) { this.totalCheckins = totalCheckins; }

    public int getBadgesCount() { return badgesCount; }
    public void setBadgesCount(int badgesCount) { this.badgesCount = badgesCount; }
}
