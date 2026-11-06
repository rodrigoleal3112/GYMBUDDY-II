package com.example.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class CommunityMember implements Serializable {

    @SerializedName("id")
    private String id;

    @SerializedName("communityId")
    private String communityId;

    @SerializedName("userId")
    private String userId;

    @SerializedName("userName")
    private String userName;

    @SerializedName("userRole")
    private String userRole; // "Criador", "Moderador", "Membro"

    @SerializedName("level")
    private int level;

    @SerializedName("streakDays")
    private int streakDays;

    @SerializedName("isCurrentUser")
    private boolean isCurrentUser;

    public CommunityMember() {}

    public CommunityMember(String id, String communityId, String userId, String userName,
                           String userRole, int level, int streakDays, boolean isCurrentUser) {
        this.id = id;
        this.communityId = communityId;
        this.userId = userId;
        this.userName = userName;
        this.userRole = userRole;
        this.level = level;
        this.streakDays = streakDays;
        this.isCurrentUser = isCurrentUser;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCommunityId() { return communityId; }
    public void setCommunityId(String communityId) { this.communityId = communityId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUserRole() { return userRole; }
    public void setUserRole(String userRole) { this.userRole = userRole; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public int getStreakDays() { return streakDays; }
    public void setStreakDays(int streakDays) { this.streakDays = streakDays; }

    @SerializedName("isCheckedToday")
    private boolean isCheckedToday;

    @SerializedName("lastCheckInTime")
    private String lastCheckInTime;

    @SerializedName("studentContact")
    private String studentContact;

    public boolean isCurrentUser() { return isCurrentUser; }
    public void setCurrentUser(boolean currentUser) { isCurrentUser = currentUser; }

    public boolean isCheckedToday() { return isCheckedToday; }
    public void setCheckedToday(boolean checkedToday) { isCheckedToday = checkedToday; }

    public String getLastCheckInTime() { return lastCheckInTime; }
    public void setLastCheckInTime(String lastCheckInTime) { this.lastCheckInTime = lastCheckInTime; }

    public String getStudentContact() { return studentContact; }
    public void setStudentContact(String studentContact) { this.studentContact = studentContact; }
}
