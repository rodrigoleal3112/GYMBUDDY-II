package com.example.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.api.ApiClient;
import com.example.model.ActivityModel;
import com.example.model.CheckIn;
import com.example.model.Community;
import com.example.model.CommunityActivity;
import com.example.model.CommunityMember;
import com.example.model.Invitation;
import com.example.model.RankingUser;
import com.example.model.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AppPreferences {

    private static final String PREF_NAME = "gymbuddy_prefs";
    private static final String KEY_TOKEN = "auth_token";
    private static final String KEY_USER_JSON = "current_user_json";
    private static final String KEY_THEME_MODE = "theme_mode";
    private static final String KEY_API_BASE_URL = "api_base_url";
    private static final String KEY_LOCAL_CHECKINS = "local_checkins";
    private static final String KEY_LOCAL_COMMUNITIES = "local_communities";
    private static final String KEY_LOCAL_INVITATIONS = "local_invitations";
    private static final String KEY_LOCAL_ACTIVITIES = "local_activities";
    private static final String KEY_LOCAL_RANKING = "local_ranking";
    private static final String KEY_RANKING_RESET = "ranking_reset";

    private final SharedPreferences prefs;
    private final Gson gson;

    public AppPreferences(Context context) {
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
    }

    // --- Autenticação ---
    public void saveAuthToken(String token) {
        prefs.edit().putString(KEY_TOKEN, token).apply();
    }

    public String getAuthToken() {
        return prefs.getString(KEY_TOKEN, "");
    }

    public boolean isLoggedIn() {
        return !getAuthToken().isEmpty() && getCurrentUser() != null;
    }

    public void saveCurrentUser(User user) {
        if (user != null) {
            String json = gson.toJson(user);
            prefs.edit().putString(KEY_USER_JSON, json).apply();
        } else {
            prefs.edit().remove(KEY_USER_JSON).apply();
        }
    }

    public User getCurrentUser() {
        String json = prefs.getString(KEY_USER_JSON, null);
        if (json != null) {
            try {
                return gson.fromJson(json, User.class);
            } catch (Exception ignored) {}
        }
        return null;
    }

    public void clearSession() {
        prefs.edit()
                .remove(KEY_TOKEN)
                .remove(KEY_USER_JSON)
                .apply();
    }

    // --- Tema (Claro / Escuro) ---
    public void saveThemeMode(int mode) {
        prefs.edit().putInt(KEY_THEME_MODE, mode).apply();
    }

    public int getThemeMode() {
        return prefs.getInt(KEY_THEME_MODE, ThemeHelper.THEME_SYSTEM);
    }

    // --- URL da API ---
    public void saveApiBaseUrl(String url) {
        prefs.edit().putString(KEY_API_BASE_URL, url).apply();
        ApiClient.resetClient();
    }

    public String getApiBaseUrl() {
        return prefs.getString(KEY_API_BASE_URL, ApiClient.DEFAULT_BASE_URL);
    }

    // --- Cache Local de Check-ins (Persistência Offline & Apresentação) ---
    public void saveCheckIns(List<CheckIn> checkIns) {
        String json = gson.toJson(checkIns);
        prefs.edit().putString(KEY_LOCAL_CHECKINS, json).apply();
    }

    public List<CheckIn> getCheckIns() {
        String json = prefs.getString(KEY_LOCAL_CHECKINS, null);
        if (json != null) {
            try {
                Type type = new TypeToken<List<CheckIn>>(){}.getType();
                return gson.fromJson(json, type);
            } catch (Exception ignored) {}
        }
        return new ArrayList<>();
    }

    public void addCheckIn(CheckIn checkIn) {
        List<CheckIn> list = getCheckIns();
        list.add(0, checkIn);
        saveCheckIns(list);
    }

    // --- Cache Local de Atividades / Exercícios ---
    public void saveActivities(List<ActivityModel> activities) {
        String json = gson.toJson(activities);
        prefs.edit().putString(KEY_LOCAL_ACTIVITIES, json).apply();
    }

    public List<ActivityModel> getActivities() {
        String json = prefs.getString(KEY_LOCAL_ACTIVITIES, null);
        if (json != null) {
            try {
                Type type = new TypeToken<List<ActivityModel>>(){}.getType();
                return gson.fromJson(json, type);
            } catch (Exception ignored) {}
        }
        return new ArrayList<>();
    }

    public void addActivity(ActivityModel activity) {
        List<ActivityModel> list = getActivities();
        list.add(0, activity);
        saveActivities(list);
    }

    public void removeActivity(String activityId) {
        if (activityId == null) return;
        List<ActivityModel> list = getActivities();
        for (int i = 0; i < list.size(); i++) {
            if (activityId.equals(list.get(i).getId())) {
                list.remove(i);
                break;
            }
        }
        saveActivities(list);
    }

    public void removeActivities(List<String> activityIds) {
        if (activityIds == null || activityIds.isEmpty()) return;
        List<ActivityModel> list = getActivities();
        List<ActivityModel> updated = new ArrayList<>();
        for (ActivityModel act : list) {
            if (!activityIds.contains(act.getId()) && !activityIds.contains(act.getName())) {
                updated.add(act);
            }
        }
        saveActivities(updated);
    }

    public void saveCustomRanking(List<RankingUser> ranking) {
        if (ranking != null) {
            String json = gson.toJson(ranking);
            prefs.edit().putString(KEY_LOCAL_RANKING, json).apply();
        } else {
            prefs.edit().remove(KEY_LOCAL_RANKING).apply();
        }
    }

    public List<RankingUser> getCustomRanking() {
        String json = prefs.getString(KEY_LOCAL_RANKING, null);
        if (json != null) {
            try {
                Type type = new TypeToken<List<RankingUser>>(){}.getType();
                return gson.fromJson(json, type);
            } catch (Exception ignored) {}
        }
        return null;
    }

    public boolean isRankingReset() {
        return prefs.getBoolean(KEY_RANKING_RESET, false);
    }

    public void setRankingReset(boolean reset) {
        prefs.edit().putBoolean(KEY_RANKING_RESET, reset).apply();
    }

    // --- Cache Local de Comunidades ---
    public void saveCommunities(List<Community> communities) {
        String json = gson.toJson(communities);
        prefs.edit().putString(KEY_LOCAL_COMMUNITIES, json).apply();
    }

    public List<Community> getCommunities() {
        String json = prefs.getString(KEY_LOCAL_COMMUNITIES, null);
        if (json != null) {
            try {
                Type type = new TypeToken<List<Community>>(){}.getType();
                return gson.fromJson(json, type);
            } catch (Exception ignored) {}
        }
        return new ArrayList<>();
    }

    // --- Cache Local de Convites ---
    public void saveInvitations(List<Invitation> invitations) {
        String json = gson.toJson(invitations);
        prefs.edit().putString(KEY_LOCAL_INVITATIONS, json).apply();
    }

    public List<Invitation> getInvitations() {
        String json = prefs.getString(KEY_LOCAL_INVITATIONS, null);
        if (json != null) {
            try {
                Type type = new TypeToken<List<Invitation>>(){}.getType();
                return gson.fromJson(json, type);
            } catch (Exception ignored) {}
        }
        return new ArrayList<>();
    }

    public void addInvitation(Invitation invitation) {
        List<Invitation> list = getInvitations();
        list.add(0, invitation);
        saveInvitations(list);
    }

    // --- Cache Local de Atividades da Comunidade ---
    public void saveCommunityActivities(String communityId, List<CommunityActivity> activities) {
        String json = gson.toJson(activities);
        prefs.edit().putString("comm_acts_" + communityId, json).apply();
    }

    public List<CommunityActivity> getCommunityActivities(String communityId) {
        String json = prefs.getString("comm_acts_" + communityId, null);
        if (json != null) {
            try {
                Type type = new TypeToken<List<CommunityActivity>>(){}.getType();
                return gson.fromJson(json, type);
            } catch (Exception ignored) {}
        }
        return new ArrayList<>();
    }

    public void addCommunityActivity(CommunityActivity activity) {
        if (activity == null || activity.getCommunityId() == null) return;
        List<CommunityActivity> list = getCommunityActivities(activity.getCommunityId());
        list.add(0, activity);
        saveCommunityActivities(activity.getCommunityId(), list);
    }

    // --- Cache Local de Membros da Comunidade ---
    public void saveCommunityMembers(String communityId, List<CommunityMember> members) {
        String json = gson.toJson(members);
        prefs.edit().putString("comm_members_" + communityId, json).apply();
    }

    public List<CommunityMember> getCommunityMembers(String communityId) {
        String json = prefs.getString("comm_members_" + communityId, null);
        if (json != null) {
            try {
                Type type = new TypeToken<List<CommunityMember>>(){}.getType();
                return gson.fromJson(json, type);
            } catch (Exception ignored) {}
        }
        return new ArrayList<>();
    }

    public void addCommunityMember(CommunityMember member) {
        if (member == null || member.getCommunityId() == null) return;
        List<CommunityMember> list = getCommunityMembers(member.getCommunityId());
        // remove existing if present
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getUserId().equals(member.getUserId())) {
                list.remove(i);
                break;
            }
        }
        list.add(member);
        saveCommunityMembers(member.getCommunityId(), list);
    }

    public void removeCommunity(String communityId) {
        if (communityId == null) return;
        List<Community> list = getCommunities();
        for (int i = 0; i < list.size(); i++) {
            if (communityId.equals(list.get(i).getId())) {
                list.remove(i);
                break;
            }
        }
        saveCommunities(list);
        prefs.edit()
                .remove("comm_acts_" + communityId)
                .remove("comm_members_" + communityId)
                .apply();
    }

    public boolean isOldCommunitiesDeleted() {
        return prefs.getBoolean("old_communities_deleted", false);
    }

    public void setOldCommunitiesDeleted(boolean deleted) {
        prefs.edit().putBoolean("old_communities_deleted", deleted).apply();
    }

    public void deleteOldDefaultCommunities() {
        setOldCommunitiesDeleted(true);
        List<Community> current = getCommunities();
        List<Community> filtered = new ArrayList<>();
        for (Community c : current) {
            if (!"c1".equals(c.getId()) && !"c2".equals(c.getId()) && !"c3".equals(c.getId())
                    && !c.getName().contains("Sedentarismo") && !c.getName().contains("30 Dias")) {
                filtered.add(c);
            }
        }
        saveCommunities(filtered);
    }

    public void updateCommunityMember(CommunityMember member) {
        if (member == null || member.getCommunityId() == null) return;
        List<CommunityMember> list = getCommunityMembers(member.getCommunityId());
        boolean found = false;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId().equals(member.getId()) || 
                (list.get(i).getUserId() != null && list.get(i).getUserId().equals(member.getUserId()))) {
                list.set(i, member);
                found = true;
                break;
            }
        }
        if (!found) {
            list.add(member);
        }
        saveCommunityMembers(member.getCommunityId(), list);
    }

    public void removeCommunityMember(String communityId, String userId) {
        if (communityId == null || userId == null) return;
        List<CommunityMember> list = getCommunityMembers(communityId);
        for (int i = 0; i < list.size(); i++) {
            if (userId.equals(list.get(i).getUserId()) || userId.equals(list.get(i).getId())) {
                list.remove(i);
                break;
            }
        }
        saveCommunityMembers(communityId, list);
    }
}
