package com.example.api;

import com.example.model.ActivityModel;
import com.example.model.AuthResponse;
import com.example.model.Badge;
import com.example.model.CheckIn;
import com.example.model.Community;
import com.example.model.CommunityActivity;
import com.example.model.CommunityMember;
import com.example.model.Invitation;
import com.example.model.InvitationStatusRequest;
import com.example.model.LoginRequest;
import com.example.model.RankingUser;
import com.example.model.RegisterRequest;
import com.example.model.User;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Interface REST para consumo da API do GymBuddy II.
 * Segue as especificações do sistema de gamificação e atividades físicas.
 */
public interface GymBuddyApiService {

    // --- Autenticação e Usuários ---
    @POST("api/auth/login")
    Call<AuthResponse> login(@Body LoginRequest request);

    @POST("api/auth/register")
    Call<AuthResponse> register(@Body RegisterRequest request);

    @GET("api/users/profile")
    Call<User> getUserProfile();

    @PUT("api/users/profile")
    Call<User> updateProfile(@Body User user);

    // --- Atividades Físicas ---
    @GET("api/activities")
    Call<List<ActivityModel>> getActivities();

    @GET("api/activities/{id}")
    Call<ActivityModel> getActivityById(@Path("id") String id);

    @POST("api/activities")
    Call<ActivityModel> createActivity(@Body ActivityModel activity);

    // --- Check-ins com Geolocalização ---
    @POST("api/checkins")
    Call<CheckIn> createCheckIn(@Body CheckIn checkIn);

    @GET("api/checkins/history")
    Call<List<CheckIn>> getCheckInHistory();

    // --- Badges / Conquistas ---
    @GET("api/badges")
    Call<List<Badge>> getBadges();

    @GET("api/badges/user")
    Call<List<Badge>> getUserBadges();

    // --- Comunidades ---
    @GET("api/communities")
    Call<List<Community>> getCommunities();

    @GET("api/communities/{id}")
    Call<Community> getCommunityById(@Path("id") String id);

    @POST("api/communities")
    Call<Community> createCommunity(@Body Community community);

    @PUT("api/communities/{id}")
    Call<Community> updateCommunity(@Path("id") String id, @Body Community community);

    @DELETE("api/communities/{id}")
    Call<Void> deleteCommunity(@Path("id") String id);

    @POST("api/communities/{id}/join")
    Call<Void> joinCommunity(@Path("id") String communityId);

    @POST("api/communities/{id}/leave")
    Call<Void> leaveCommunity(@Path("id") String communityId);

    // --- Membros de Comunidade ---
    @GET("api/communities/{id}/members")
    Call<List<CommunityMember>> getCommunityMembers(@Path("id") String communityId);

    @POST("api/communities/{id}/members")
    Call<CommunityMember> addCommunityMember(@Path("id") String communityId, @Body CommunityMember member);

    @DELETE("api/communities/{id}/members/{userId}")
    Call<Void> removeCommunityMember(@Path("id") String communityId, @Path("userId") String userId);

    // --- Atividades de Comunidade ---
    @GET("api/communities/{id}/activities")
    Call<List<CommunityActivity>> getCommunityActivities(@Path("id") String communityId);

    @GET("api/communities/{id}/activities/{activityId}")
    Call<CommunityActivity> getCommunityActivityById(@Path("id") String communityId, @Path("activityId") String activityId);

    @POST("api/communities/{id}/activities")
    Call<CommunityActivity> createCommunityActivity(@Path("id") String communityId, @Body CommunityActivity activity);

    @PUT("api/communities/{id}/activities/{activityId}")
    Call<CommunityActivity> updateCommunityActivity(@Path("id") String communityId, @Path("activityId") String activityId, @Body CommunityActivity activity);

    @POST("api/communities/{id}/activities/{activityId}/join")
    Call<Void> joinCommunityActivity(@Path("id") String communityId, @Path("activityId") String activityId);

    @POST("api/communities/{id}/activities/{activityId}/leave")
    Call<Void> leaveCommunityActivity(@Path("id") String communityId, @Path("activityId") String activityId);

    // --- Check-ins de Comunidade ---
    @GET("api/communities/{id}/checkins")
    Call<List<CheckIn>> getCommunityCheckIns(@Path("id") String communityId);

    @POST("api/communities/{id}/checkins")
    Call<CheckIn> createCommunityCheckIn(@Path("id") String communityId, @Body CheckIn checkIn);

    // --- Convites ---
    @GET("api/invitations")
    Call<List<Invitation>> getInvitations();

    @GET("api/communities/{id}/invitations")
    Call<List<Invitation>> getInvitationsByCommunity(@Path("id") String communityId);

    @POST("api/invitations")
    Call<Invitation> sendInvitation(@Body Invitation invitation);

    @GET("api/invitations/{id}")
    Call<Invitation> getInvitationById(@Path("id") String invitationId);

    @PUT("api/invitations/{id}")
    Call<Invitation> respondInvitation(@Path("id") String invitationId, @Body InvitationStatusRequest body);

    @POST("api/invitations/{id}/accept")
    Call<Invitation> acceptInvitation(@Path("id") String invitationId);

    @POST("api/invitations/{id}/decline")
    Call<Invitation> declineInvitation(@Path("id") String invitationId);

    @DELETE("api/invitations/{id}")
    Call<Void> cancelInvitation(@Path("id") String invitationId);

    // --- Ranking ---
    @GET("api/ranking")
    Call<List<RankingUser>> getRanking();
}
