package com.gymcrm.dao;

import com.gymcrm.model.TrainingType;
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
class TrainingTypeDAOImplTest {

    @Mock
    EntityManager entityManager;

    @InjectMocks
    TrainingTypeDAOImpl trainingTypeDAO;

    @Test
    @DisplayName("findById: returns empty without querying when id is null")
    void findById_returnsEmptyWhenIdIsNull() {
        assertThat(trainingTypeDAO.findById(null)).isEmpty();
        verifyNoInteractions(entityManager);
    }

    @Test
    @DisplayName("findById: delegates to entityManager.find and wraps result")
    void findById_delegatesToEntityManagerFind() {
        TrainingType type = new TrainingType("Yoga");
        when(entityManager.find(TrainingType.class, 1L)).thenReturn(type);

        assertThat(trainingTypeDAO.findById(1L)).contains(type);
    }

    @Test
    @DisplayName("findById: returns empty when entityManager.find returns null")
    void findById_returnsEmptyWhenNotFound() {
        when(entityManager.find(TrainingType.class, 99L)).thenReturn(null);

        assertThat(trainingTypeDAO.findById(99L)).isEmpty();
    }

    @Test
    @DisplayName("findByName: returns empty without querying when name is null")
    void findByName_returnsEmptyWhenNameIsNull() {
        assertThat(trainingTypeDAO.findByName(null)).isEmpty();
        verifyNoInteractions(entityManager);
    }

    @Test
    @DisplayName("findByName: returns present when entity found")
    @SuppressWarnings("unchecked")
    void findByName_returnsPresentWhenFound() {
        TrainingType type = new TrainingType("Cardio");
        TypedQuery<TrainingType> query = mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(TrainingType.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getResultStream()).thenReturn(Stream.of(type));

        assertThat(trainingTypeDAO.findByName("Cardio")).contains(type);
    }
}

