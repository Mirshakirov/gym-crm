package com.gymcrm.dao;

import com.gymcrm.dao.impl.TrainerDAOImpl;
import com.gymcrm.model.Trainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

class TrainerDAOImplTest {
    private TrainerDAOImpl dao;
    private Map<Long, Trainer> storage;

    @BeforeEach
    void setUp() {
        storage = new HashMap<>();
        dao = new TrainerDAOImpl(storage);
    }

    @Test
    void testSaveTrainer() {
        Trainer trainer = new Trainer();
        trainer.setFirstName("Mike");
        Trainer saved = dao.save(trainer);
        assertThat(saved.getUserId()).isEqualTo(1L);
    }

    @Test
    void testSelectById() {
        Trainer trainer = new Trainer();
        trainer.setFirstName("Mike");
        dao.save(trainer);
        Optional<Trainer> result = dao.selectById(1L);
        assertThat(result).isPresent();
    }

    @Test
    void testSelectAll() {
        dao.save(new Trainer());
        dao.save(new Trainer());
        assertThat(dao.selectAll()).hasSize(2);
    }

    @Test
    void testUpdate() {
        Trainer trainer = new Trainer();
        trainer.setFirstName("Mike");
        dao.save(trainer);
        trainer.setFirstName("Michael");
        dao.update(trainer);
        assertThat(dao.selectById(1L).get().getFirstName()).isEqualTo("Michael");
    }

    @Test
    void testSelectByUsername() {
        Trainer trainer = new Trainer();
        trainer.setUsername("mike.wilson");
        dao.save(trainer);
        Optional<Trainer> result = dao.selectByUsername("mike.wilson");
        assertThat(result).isPresent();
    }
}
