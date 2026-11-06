package com.example.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class CheckIn implements Serializable {
    @SerializedName("id")
    private String id;

    @SerializedName("userId")
    private String userId;

    @SerializedName("userName")
    private String userName;

    @SerializedName("activityId")
    private String activityId;

    @SerializedName("activityName")
    private String activityName;

    @SerializedName("xpEarned")
    private int xpEarned;

    @SerializedName("timestamp")
    private String timestamp;

    @SerializedName("latitude")
    private double latitude;

    @SerializedName("longitude")
    private double longitude;

    @SerializedName("locationName")
    private String locationName;

    @SerializedName("locationType")
    private String locationType;

    @SerializedName("accuracyMeters")
    private float accuracyMeters;

    @SerializedName("status")
    private String status;

    @SerializedName("communityId")
    private String communityId;

    @SerializedName("communityName")
    private String communityName;

    public CheckIn() {}

    public CheckIn(String id, String userId, String userName, String activityId, String activityName, 
                   int xpEarned, String timestamp, double latitude, double longitude, 
                   String locationName, String locationType, float accuracyMeters, String status) {
        this(id, userId, userName, activityId, activityName, xpEarned, timestamp, latitude, longitude,
                locationName, locationType, accuracyMeters, status, null, null);
    }

    public CheckIn(String id, String userId, String userName, String activityId, String activityName, 
                   int xpEarned, String timestamp, double latitude, double longitude, 
                   String locationName, String locationType, float accuracyMeters, String status,
                   String communityId, String communityName) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.activityId = activityId;
        this.activityName = activityName;
        this.xpEarned = xpEarned;
        this.timestamp = timestamp;
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationName = locationName;
        this.locationType = locationType;
        this.accuracyMeters = accuracyMeters;
        this.status = status;
        this.communityId = communityId;
        this.communityName = communityName;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getActivityId() { return activityId; }
    public void setActivityId(String activityId) { this.activityId = activityId; }

    public String getActivityName() { return activityName; }
    public void setActivityName(String activityName) { this.activityName = activityName; }

    public int getXpEarned() { return xpEarned; }
    public void setXpEarned(int xpEarned) { this.xpEarned = xpEarned; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public String getLocationName() { return locationName; }
    public void setLocationName(String locationName) { this.locationName = locationName; }

    public String getLocationType() { return locationType; }
    public void setLocationType(String locationType) { this.locationType = locationType; }

    public float getAccuracyMeters() { return accuracyMeters; }
    public void setAccuracyMeters(float accuracyMeters) { this.accuracyMeters = accuracyMeters; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCommunityId() { return communityId; }
    public void setCommunityId(String communityId) { this.communityId = communityId; }

    public String getCommunityName() { return communityName; }
    public void setCommunityName(String communityName) { this.communityName = communityName; }
}
