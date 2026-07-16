package com.gymcrm.repository;

import com.gymcrm.entity.Trainer;
import com.gymcrm.entity.TrainingType;
import com.gymcrm.entity.User;
import com.gymcrm.repository.impl.TrainerDAOImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerDAOImplTest {
    private TrainerDAOImpl dao;

    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<Trainer> trainerQuery;

    @BeforeEach
    void setUp() throws Exception {
        dao = new TrainerDAOImpl();
        setField(dao, "entityManager", entityManager);
    }

    @Test
    void testSaveTrainer() {
        Trainer trainer = createTrainer(null, "Mike", "Wilson", "mike.wilson");
        doAnswer(invocation -> {
            Trainer saved = invocation.getArgument(0);
            saved.setId(1L);
            return null;
        }).when(entityManager).persist(any(Trainer.class));

        Trainer saved = dao.save(trainer);

        assertThat(saved.getId()).isEqualTo(1L);
    }

    @Test
    void testSelectById() {
        Trainer trainer = createTrainer(1L, "Mike", "Wilson", "mike.wilson");
        when(entityManager.find(Trainer.class, 1L)).thenReturn(trainer);

        Optional<Trainer> result = dao.selectById(1L);

        assertThat(result).isPresent();
    }

    @Test
    void testSelectAll() {
        when(entityManager.createQuery("FROM Trainer", Trainer.class)).thenReturn(trainerQuery);
        when(trainerQuery.getResultList()).thenReturn(List.of(
                createTrainer(1L, "Mike", "Wilson", "mike.wilson"),
                createTrainer(2L, "Jane", "Smith", "jane.smith")
        ));

        assertThat(dao.selectAll()).hasSize(2);
    }

    @Test
    void testUpdate() {
        Trainer trainer = createTrainer(1L, "Mike", "Wilson", "mike.wilson");
        Trainer updated = createTrainer(1L, "Michael", "Wilson", "michael.wilson");
        when(entityManager.merge(trainer)).thenReturn(updated);

        Trainer result = dao.update(trainer);

        assertThat(result.getUser().getFirstName()).isEqualTo("Michael");
    }

    @Test
    void testSelectByUsername() {
        Trainer trainer = createTrainer(1L, "Mike", "Wilson", "mike.wilson");
        when(entityManager.createQuery("FROM Trainer t WHERE t.user.username = :username", Trainer.class)).thenReturn(trainerQuery);
        when(trainerQuery.setParameter("username", "mike.wilson")).thenReturn(trainerQuery);
        when(trainerQuery.getResultList()).thenReturn(List.of(trainer));

        Optional<Trainer> result = dao.selectByUsername("mike.wilson");

        assertThat(result).isPresent();
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    private Trainer createTrainer(Long id, String firstName, String lastName, String username) {
        Trainer trainer = new Trainer();
        trainer.setId(id);
        trainer.setSpecialization(createTrainingType("Yoga"));
        trainer.setUser(createUser(firstName, lastName, username));
        return trainer;
    }

    private TrainingType createTrainingType(String name) {
        TrainingType trainingType = new TrainingType();
        trainingType.setTrainingTypeName(name);
        return trainingType;
    }

    private User createUser(String firstName, String lastName, String username) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setPassword("secret");
        user.setActive(true);
        return user;
    }
}
