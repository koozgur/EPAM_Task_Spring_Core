package com.gymcrm.dao;

import com.gymcrm.model.Trainer;
import com.gymcrm.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerDAOImplTest {

    @Mock
    EntityManager entityManager;

    @InjectMocks
    TrainerDAOImpl trainerDAO;

    @Test
    @DisplayName("create: delegates to entityManager.persist")
    void create_delegatesToPersist() {
        Trainer trainer = new Trainer();
        User user = new User();
        trainer.setUser(user);
        trainerDAO.create(trainer);
        verify(entityManager).persist(trainer);
    }

    @Test
    @DisplayName("update: delegates to entityManager.merge")
    void update_delegatesToMerge() {
        Trainer trainer = new Trainer();
        Trainer merged = new Trainer();
        when(entityManager.merge(trainer)).thenReturn(merged);

        assertThat(trainerDAO.update(trainer)).isSameAs(merged);
    }

    @Test
    @DisplayName("findById: returns empty without querying when id is null")
    void findById_returnsEmptyWhenIdIsNull() {
        assertThat(trainerDAO.findById(null)).isEmpty();
        verifyNoInteractions(entityManager);
    }

    @Test
    @DisplayName("findById: returns present when entity found")
    @SuppressWarnings("unchecked")
    void findById_returnsPresentWhenFound() {
        Trainer trainer = new Trainer();
        TypedQuery<Trainer> query = mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(Trainer.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getResultStream()).thenReturn(Stream.of(trainer));

        assertThat(trainerDAO.findById(1L)).contains(trainer);
    }

    @Test
    @DisplayName("findByUsername: returns empty without querying when username is null")
    void findByUsername_returnsEmptyWhenUsernameIsNull() {
        assertThat(trainerDAO.findByUsername(null)).isEmpty();
        verifyNoInteractions(entityManager);
    }

    @Test
    @DisplayName("findByUsername: returns present when entity found")
    @SuppressWarnings("unchecked")
    void findByUsername_returnsPresentWhenFound() {
        Trainer trainer = new Trainer();
        TypedQuery<Trainer> query = mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(Trainer.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getResultStream()).thenReturn(Stream.of(trainer));

        assertThat(trainerDAO.findByUsername("mike.coach")).contains(trainer);
    }

    @Test
    @DisplayName("findUnassignedTrainers: returns empty list when username is null")
    @SuppressWarnings("unchecked")
    void findUnassignedTrainers_returnsEmptyListWhenUsernameIsNull() {
        List<Trainer> result = trainerDAO.findUnassignedTrainersByTraineeUsername(null);
        assertThat(result).isEmpty();
        verifyNoInteractions(entityManager);
    }

    @Test
    @DisplayName("findUnassignedTrainers: returns trainers when found")
    @SuppressWarnings("unchecked")
    void findUnassignedTrainers_returnedFromQuery() {
        Trainer t1 = new Trainer();
        TypedQuery<Trainer> query = mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(Trainer.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(t1));

        List<Trainer> result = trainerDAO.findUnassignedTrainersByTraineeUsername("john.doe");
        assertThat(result).containsExactly(t1);
    }
}
