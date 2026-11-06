package com.example.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class RankingUser implements Serializable {
    @SerializedName("position")
    private int position;

    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("level")
    private int level;

    @SerializedName("xp")
    private int xp;

    @SerializedName("streakDays")
    private int streakDays;

    @SerializedName("isCurrentUser")
    private boolean isCurrentUser;

    public RankingUser() {}

    public RankingUser(int position, String id, String name, int level, int xp, int streakDays, boolean isCurrentUser) {
        this.position = position;
        this.id = id;
        this.name = name;
        this.level = level;
        this.xp = xp;
        this.streakDays = streakDays;
        this.isCurrentUser = isCurrentUser;
    }

    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public int getXp() { return xp; }
    public void setXp(int xp) { this.xp = xp; }

    public int getStreakDays() { return streakDays; }
    public void setStreakDays(int streakDays) { this.streakDays = streakDays; }

    public boolean isCurrentUser() { return isCurrentUser; }
    public void setCurrentUser(boolean currentUser) { isCurrentUser = currentUser; }
}
