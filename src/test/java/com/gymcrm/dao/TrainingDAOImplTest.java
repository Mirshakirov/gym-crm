package com.gymcrm.dao;

import com.gymcrm.dao.impl.TrainingDAOImpl;
import com.gymcrm.model.Training;
import com.gymcrm.model.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

class TrainingDAOImplTest {
    private TrainingDAOImpl dao;
    private Map<Long, Training> storage;

    @BeforeEach
    void setUp() {
        storage = new HashMap<>();
        dao = new TrainingDAOImpl(storage);
    }

    @Test
    void testSaveTraining() {
        Training training = new Training(1L, 1L, "Yoga", new TrainingType("Yoga"), LocalDate.now(), 60);
        dao.save(training);
        assertThat(storage).hasSize(1);
    }

    @Test
    void testSelectById() {
        Training training = new Training(1L, 1L, "Yoga", new TrainingType("Yoga"), LocalDate.now(), 60);
        dao.save(training);
        Optional<Training> result = dao.selectById(1L);
        assertThat(result).isPresent();
    }

    @Test
    void testSelectAll() {
        dao.save(new Training(1L, 1L, "T1", new TrainingType("Yoga"), LocalDate.now(), 60));
        dao.save(new Training(2L, 2L, "T2", new TrainingType("Yoga"), LocalDate.now(), 60));
        assertThat(dao.selectAll()).hasSize(2);
    }

    @Test
    void testSelectByTraineeId() {
        dao.save(new Training(1L, 1L, "T1", new TrainingType("Yoga"), LocalDate.now(), 60));
        dao.save(new Training(1L, 2L, "T2", new TrainingType("Yoga"), LocalDate.now(), 60));
        assertThat(dao.selectByTraineeId(1L)).hasSize(2);
    }

    @Test
    void testSelectByTrainerId() {
        dao.save(new Training(1L, 1L, "T1", new TrainingType("Yoga"), LocalDate.now(), 60));
        dao.save(new Training(2L, 1L, "T2", new TrainingType("Yoga"), LocalDate.now(), 60));
        assertThat(dao.selectByTrainerId(1L)).hasSize(2);
    }
}
