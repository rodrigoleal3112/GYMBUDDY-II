package com.example.api;

import com.example.model.CheckIn;
import com.example.model.Community;
import com.example.model.CommunityActivity;
import com.example.model.CommunityMember;
import com.example.model.Invitation;
import com.example.model.InvitationStatusRequest;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Interface Retrofit dedicada para endpoints relacionados a Comunidades do GymBuddy II:
 * - Comunidades (CRUD, listar, entrar, sair)
 * - Membros da Comunidade (listar, adicionar, remover)
 * - Atividades de Comunidade (listar, criar, participar, sair)
 * - Check-ins de Comunidade (listar, registrar)
 * - Convites (listar, enviar, aceitar, recusar, cancelar)
 */
public interface CommunityService {

    // ==========================================
    // 1. COMUNIDADES
    // ==========================================

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

    // ==========================================
    // 2. MEMBROS DA COMUNIDADE
    // ==========================================

    @GET("api/communities/{id}/members")
    Call<List<CommunityMember>> getCommunityMembers(@Path("id") String communityId);

    @POST("api/communities/{id}/members")
    Call<CommunityMember> addCommunityMember(@Path("id") String communityId, @Body CommunityMember member);

    @DELETE("api/communities/{id}/members/{userId}")
    Call<Void> removeCommunityMember(@Path("id") String communityId, @Path("userId") String userId);

    // ==========================================
    // 3. ATIVIDADES DE COMUNIDADE
    // ==========================================

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

    // ==========================================
    // 4. CHECK-INS DE COMUNIDADE
    // ==========================================

    @GET("api/communities/{id}/checkins")
    Call<List<CheckIn>> getCommunityCheckIns(@Path("id") String communityId);

    @POST("api/communities/{id}/checkins")
    Call<CheckIn> createCommunityCheckIn(@Path("id") String communityId, @Body CheckIn checkIn);

    // ==========================================
    // 5. CONVITES
    // ==========================================

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
}
