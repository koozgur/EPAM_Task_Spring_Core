package com.gymcrm.dao;

import com.gymcrm.model.Training;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingDAOImplTest {

    @Mock
    EntityManager entityManager;

    @InjectMocks
    TrainingDAOImpl trainingDAO;

    @Test
    @DisplayName("create: delegates to entityManager.persist")
    void create_delegatesToPersist() {
        Training training = new Training();
        trainingDAO.create(training);
        verify(entityManager).persist(training);
    }

    @Test
    @DisplayName("findById: returns empty without querying when id is null")
    void findById_returnsEmptyWhenIdIsNull() {
        assertThat(trainingDAO.findById(null)).isEmpty();
        verifyNoInteractions(entityManager);
    }

    @Test
    @DisplayName("findById: returns present when entity found")
    void findById_returnsPresentWhenFound() {
        Training training = new Training();
        when(entityManager.find(Training.class, 1L)).thenReturn(training);

        assertThat(trainingDAO.findById(1L)).contains(training);
    }

    @Test
    @DisplayName("findByTraineeId: returns empty list when id is null")
    void findByTraineeId_returnsEmptyListWhenIdIsNull() {
        assertThat(trainingDAO.findByTraineeId(null)).isEmpty();
        verifyNoInteractions(entityManager);
    }

    @Test
    @DisplayName("findByTrainerId: returns empty list when id is null")
    void findByTrainerId_returnsEmptyListWhenIdIsNull() {
        assertThat(trainingDAO.findByTrainerId(null)).isEmpty();
        verifyNoInteractions(entityManager);
    }
}


