package com.gymcrm.dao;

import com.gymcrm.dao.impl.TraineeDAOImpl;
import com.gymcrm.model.Trainee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

class TraineeDAOImplTest {
    private TraineeDAOImpl dao;
    private Map<Long, Trainee> storage;

    @BeforeEach
    void setUp() {
        storage = new HashMap<>();
        dao = new TraineeDAOImpl(storage);
    }

    @Test
    void testSaveTrainee() {
        Trainee trainee = new Trainee();
        trainee.setFirstName("John");
        Trainee saved = dao.save(trainee);
        assertThat(saved.getUserId()).isEqualTo(1L);
        assertThat(storage).hasSize(1);
    }

    @Test
    void testSelectById() {
        Trainee trainee = new Trainee();
        trainee.setFirstName("John");
        dao.save(trainee);
        Optional<Trainee> result = dao.selectById(1L);
        assertThat(result).isPresent();
        assertThat(result.get().getFirstName()).isEqualTo("John");
    }

    @Test
    void testSelectByIdNotFound() {
        Optional<Trainee> result = dao.selectById(999L);
        assertThat(result).isEmpty();
    }

    @Test
    void testSelectAll() {
        dao.save(new Trainee());
        dao.save(new Trainee());
        assertThat(dao.selectAll()).hasSize(2);
    }

    @Test
    void testUpdate() {
        Trainee trainee = new Trainee();
        trainee.setFirstName("John");
        dao.save(trainee);
        trainee.setFirstName("Jane");
        dao.update(trainee);
        assertThat(dao.selectById(1L).get().getFirstName()).isEqualTo("Jane");
    }

    @Test
    void testDelete() {
        dao.save(new Trainee());
        dao.delete(1L);
        assertThat(storage).isEmpty();
    }

    @Test
    void testSelectByUsername() {
        Trainee trainee = new Trainee();
        trainee.setUsername("john.doe");
        dao.save(trainee);
        Optional<Trainee> result = dao.selectByUsername("john.doe");
        assertThat(result).isPresent();
    }
}
