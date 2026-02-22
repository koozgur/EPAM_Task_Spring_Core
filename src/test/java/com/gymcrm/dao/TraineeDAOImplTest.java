package com.gymcrm.dao;

import com.gymcrm.model.Trainee;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeDAOImplTest {

    @Mock
    EntityManager entityManager;

    @InjectMocks
    TraineeDAOImpl traineeDAO;

    @Test
    @DisplayName("create: delegates to entityManager.persist")
    void create_delegatesToPersist() {
        Trainee trainee = new Trainee();
        traineeDAO.create(trainee);
        verify(entityManager).persist(trainee);
    }

    @Test
    @DisplayName("update: delegates to entityManager.merge")
    void update_delegatesToMerge() {
        Trainee trainee = new Trainee();
        Trainee merged = new Trainee();
        when(entityManager.merge(trainee)).thenReturn(merged);

        assertThat(traineeDAO.update(trainee)).isSameAs(merged);
    }

    @Test
    @DisplayName("delete: no-op when id is null")
    void delete_noopWhenIdIsNull() {
        traineeDAO.delete(null);
        verifyNoInteractions(entityManager);
    }

    @Test
    @DisplayName("delete: no-op when trainee not found")
    void delete_noopWhenNotFound() {
        when(entityManager.find(Trainee.class, 99L)).thenReturn(null);
        traineeDAO.delete(99L);
        verify(entityManager, never()).remove(any());
    }

    @Test
    @DisplayName("delete: removes entity when found")
    void delete_removesWhenFound() {
        Trainee trainee = new Trainee();
        when(entityManager.find(Trainee.class, 1L)).thenReturn(trainee);
        traineeDAO.delete(1L);
        verify(entityManager).remove(trainee);
    }

    @Test
    @DisplayName("findById: returns empty without querying when id is null")
    void findById_returnsEmptyWhenIdIsNull() {
        assertThat(traineeDAO.findById(null)).isEmpty();
        verifyNoInteractions(entityManager);
    }

    @Test
    @DisplayName("findById: returns present when entity found")
    @SuppressWarnings("unchecked")
    void findById_returnsPresentWhenFound() {
        Trainee trainee = new Trainee();
        TypedQuery<Trainee> query = mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(Trainee.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getResultStream()).thenReturn(Stream.of(trainee));

        assertThat(traineeDAO.findById(1L)).contains(trainee);
    }

    @Test
    @DisplayName("findByUsername: returns empty without querying when username is null")
    void findByUsername_returnsEmptyWhenUsernameIsNull() {
        assertThat(traineeDAO.findByUsername(null)).isEmpty();
        verifyNoInteractions(entityManager);
    }

    @Test
    @DisplayName("findByUsername: returns present when entity found")
    @SuppressWarnings("unchecked")
    void findByUsername_returnsPresentWhenFound() {
        Trainee trainee = new Trainee();
        TypedQuery<Trainee> query = mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(Trainee.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getResultStream()).thenReturn(Stream.of(trainee));

        assertThat(traineeDAO.findByUsername("john.doe")).contains(trainee);
    }
}
