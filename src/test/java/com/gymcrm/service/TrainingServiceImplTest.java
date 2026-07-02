package com.gymcrm.service;

import com.gymcrm.dao.TrainingDAO;
import com.gymcrm.model.Training;
import com.gymcrm.model.TrainingType;
import com.gymcrm.service.impl.TrainingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingServiceImplTest {
    private TrainingServiceImpl service;
    @Mock
    private TrainingDAO dao;

    @BeforeEach
    void setUp() {
        service = new TrainingServiceImpl();
        service.setTrainingDAO(dao);
    }

    @Test
    void testCreateTraining() {
        Training training = new Training(1L, 1L, "Yoga", new TrainingType("Yoga"), LocalDate.now(), 60);
        when(dao.save(any())).thenReturn(training);
        Training result = service.create(training);
        assertThat(result).isNotNull();
        verify(dao).save(any());
    }

    @Test
    void testCreateNullThrows() {
        assertThatThrownBy(() -> service.create(null))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testCreateNullTraineeIdThrows() {
        Training training = new Training();
        training.setTrainerId(1L);
        assertThatThrownBy(() -> service.create(training))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testCreateNullTrainerIdThrows() {
        Training training = new Training();
        training.setTraineeId(1L);
        assertThatThrownBy(() -> service.create(training))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testSelectById() {
        Training training = new Training(1L, 1L, "Yoga", new TrainingType("Yoga"), LocalDate.now(), 60);
        when(dao.selectById(1L)).thenReturn(Optional.of(training));
        Optional<Training> result = service.selectById(1L);
        assertThat(result).isPresent();
    }

    @Test
    void testSelectByIdNullThrows() {
        assertThatThrownBy(() -> service.selectById(null))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
