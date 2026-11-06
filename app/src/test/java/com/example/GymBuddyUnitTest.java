package com.example;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.example.model.ActivityModel;
import com.example.model.Badge;
import com.example.model.CheckIn;
import com.example.model.Community;
import com.example.model.Invitation;
import com.example.model.RankingUser;
import com.example.model.User;

import org.junit.Test;

public class GymBuddyUnitTest {

    @Test
    public void testUserGamificationLevelCalculation() {
        User user = new User("u1", "Aluno Teste", "teste@gymbuddy.com", 1, 0, 100, 0, 0, 0);
        assertEquals(1, user.getLevel());

        // Simulando ganho de 150 XP
        user.setXp(user.getXp() + 150);
        int newLevel = 1 + (user.getXp() / 100);
        user.setLevel(newLevel);

        assertEquals(2, user.getLevel());
        assertEquals(150, user.getXp());
    }

    @Test
    public void testActivityCreation() {
        ActivityModel act = new ActivityModel(
                "act_cardio_1",
                "Caminhada",
                "Cardio leve para iniciantes",
                "CARDIO",
                40,
                "3x por semana",
                true
        );
        assertEquals("CARDIO", act.getCategory());
        assertEquals(40, act.getXpReward());
        assertTrue(act.isActive());
    }

    @Test
    public void testCheckInGeolocationIntegrity() {
        CheckIn checkIn = new CheckIn(
                "chk_1",
                "u1",
                "Aluno Teste",
                "act_cardio_1",
                "Caminhada",
                40,
                "25/09/2026 10:00",
                -23.5505,
                -46.6333,
                "Parque Municipal",
                "PARQUE",
                8.0f,
                "VERIFIED"
        );

        assertNotNull(checkIn.getTimestamp());
        assertEquals(-23.5505, checkIn.getLatitude(), 0.0001);
        assertEquals(-46.6333, checkIn.getLongitude(), 0.0001);
        assertEquals("PARQUE", checkIn.getLocationType());
        assertEquals("VERIFIED", checkIn.getStatus());
    }

    @Test
    public void testInvitationStatusFlow() {
        Invitation inv = new Invitation(
                "inv_1",
                "c1",
                "Grupo Sedentarismo Zero",
                "Prof. Ricardo",
                "teste@gymbuddy.com",
                Invitation.STATUS_PENDING,
                "25/09/2026",
                false
        );

        assertEquals(Invitation.STATUS_PENDING, inv.getStatus());
        inv.setStatus(Invitation.STATUS_ACCEPTED);
        assertEquals(Invitation.STATUS_ACCEPTED, inv.getStatus());
    }

    @Test
    public void testActivityConfirmationAndXpGeneration() {
        User user = new User("u_practitioner", "Praticante", "praticante@gymbuddy.com", 1, 50, 100, 1, 0, 0);
        ActivityModel supino = new ActivityModel("act_supino", "Supino Reto (Peitoral)", "Fortalecimento peitoral e braços", "BRAÇO", 50, "3x por semana", 3, 10, "20 kg", true);

        // Simulando a confirmação da atividade com OK
        int initialXp = user.getXp();
        int initialLevel = user.getLevel();
        int earnedXp = supino.getXpReward();

        user.setXp(initialXp + earnedXp);
        user.setLevel(1 + (user.getXp() / 100));
        user.setTotalCheckins(user.getTotalCheckins() + 1);

        assertEquals(100, user.getXp());
        assertEquals(2, user.getLevel()); // Subiu para o nível 2!
        assertEquals(1, user.getTotalCheckins());
    }

    @Test
    public void testActivityDeletion() {
        ActivityModel supino = new ActivityModel("act_supino", "Supino Reto (Peitoral)", "Peitoral e braços", "BRAÇO", 50, "3x por semana", 3, 10, "20 kg", true);
        java.util.List<ActivityModel> list = new java.util.ArrayList<>();
        list.add(supino);
        assertEquals(1, list.size());

        // Simulando a exclusão do treino
        list.removeIf(a -> a.getId().equals("act_supino"));
        assertEquals(0, list.size());
    }

    @Test
    public void testGroupDeletion() {
        Community comm = new Community("c_treino", "Turma da Manhã", "Grupo rápido para treino do dia", "Prof. Rodrigo", 5, "ATIVO", true);
        java.util.List<Community> list = new java.util.ArrayList<>();
        list.add(comm);
        assertEquals(1, list.size());

        // Simulando a exclusão do grupo criado apenas na hora do treino
        list.removeIf(c -> c.getId().equals("c_treino"));
        assertEquals(0, list.size());
    }

    @Test
    public void testRankingResetAndCompetition() {
        User user = new User("u1", "Rodrigo", "rodrigo@gymbuddy.com", 3, 280, 300, 5, 4, 2);
        RankingUser rUser = new RankingUser(1, "u1", user.getName(), user.getLevel(), user.getXp(), user.getStreakDays(), true);
        assertEquals(280, rUser.getXp());

        // Limpar ranking zerando atividades e XP para disputa competitiva
        user.setXp(0);
        user.setLevel(1);
        user.setTotalCheckins(0);
        rUser.setXp(0);
        rUser.setLevel(1);

        assertEquals(0, user.getXp());
        assertEquals(1, user.getLevel());
        assertEquals(0, rUser.getXp());

        // Nova atividade realizada após zerar ranking assume a liderança
        rUser.setXp(rUser.getXp() + 50);
        assertEquals(50, rUser.getXp());
    }
}
