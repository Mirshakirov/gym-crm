package com.gymcrm.repository;

import com.gymcrm.entity.Trainee;
import com.gymcrm.entity.Trainer;
import com.gymcrm.entity.Training;
import com.gymcrm.entity.TrainingType;
import com.gymcrm.entity.User;
import com.gymcrm.repository.impl.TrainingDAOImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingDAOImplTest {
    private TrainingDAOImpl dao;

    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<Training> trainingQuery;

    @BeforeEach
    void setUp() throws Exception {
        dao = new TrainingDAOImpl();
        setField(dao, "entityManager", entityManager);
    }

    @Test
    void testSaveTraining() {
        Training training = createTraining(null, 1L, 1L, "Yoga");
        doAnswer(invocation -> {
            Training saved = invocation.getArgument(0);
            saved.setId(1L);
            return null;
        }).when(entityManager).persist(any(Training.class));

        Training saved = dao.save(training);

        assertThat(saved.getId()).isEqualTo(1L);
    }

    @Test
    void testSelectById() {
        Training training = createTraining(1L, 1L, 1L, "Yoga");
        when(entityManager.find(Training.class, 1L)).thenReturn(training);

        Optional<Training> result = dao.selectById(1L);

        assertThat(result).isPresent();
    }

    @Test
    void testSelectAll() {
        when(entityManager.createQuery("FROM Training", Training.class)).thenReturn(trainingQuery);
        when(trainingQuery.getResultList()).thenReturn(List.of(
                createTraining(1L, 1L, 1L, "T1"),
                createTraining(2L, 2L, 2L, "T2")
        ));

        assertThat(dao.selectAll()).hasSize(2);
    }

    @Test
    void testSelectByTraineeId() {
        when(entityManager.createQuery("FROM Training t WHERE t.trainee.id = :traineeId", Training.class)).thenReturn(trainingQuery);
        when(trainingQuery.setParameter("traineeId", 1L)).thenReturn(trainingQuery);
        when(trainingQuery.getResultList()).thenReturn(List.of(
                createTraining(1L, 1L, 1L, "T1"),
                createTraining(2L, 1L, 2L, "T2")
        ));

        assertThat(dao.selectByTraineeId(1L)).hasSize(2);
    }

    @Test
    void testSelectByTrainerId() {
        when(entityManager.createQuery("FROM Training t WHERE t.trainer.id = :trainerId", Training.class)).thenReturn(trainingQuery);
        when(trainingQuery.setParameter("trainerId", 1L)).thenReturn(trainingQuery);
        when(trainingQuery.getResultList()).thenReturn(List.of(
                createTraining(1L, 1L, 1L, "T1"),
                createTraining(2L, 2L, 1L, "T2")
        ));

        assertThat(dao.selectByTrainerId(1L)).hasSize(2);
    }

    @Test
    void testSelectTraineeTrainingsByCriteria() {
        when(entityManager.createQuery(
                contains("SELECT t FROM Training t WHERE t.trainee.user.username = :traineeUsername"),
                eq(Training.class))
        ).thenReturn(trainingQuery);
        when(trainingQuery.setParameter(anyString(), any())).thenReturn(trainingQuery);
        when(trainingQuery.getResultList()).thenReturn(List.of(createTraining(1L, 1L, 1L, "T1")));

        List<Training> result = dao.selectTraineeTrainingsByCriteria(
                "john.doe",
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 12, 31),
                "Mike",
                "Yoga"
        );

        assertThat(result).hasSize(1);
        verify(trainingQuery).setParameter("traineeUsername", "john.doe");
        verify(trainingQuery).setParameter("fromDate", LocalDate.of(2024, 1, 1));
        verify(trainingQuery).setParameter("toDate", LocalDate.of(2024, 12, 31));
        verify(trainingQuery).setParameter("trainerName", "%mike%");
        verify(trainingQuery).setParameter("trainingType", "yoga");
    }

    @Test
    void testSelectTrainerTrainingsByCriteria() {
        when(entityManager.createQuery(
                contains("SELECT t FROM Training t WHERE t.trainer.user.username = :trainerUsername"),
                eq(Training.class))
        ).thenReturn(trainingQuery);
        when(trainingQuery.setParameter(anyString(), any())).thenReturn(trainingQuery);
        when(trainingQuery.getResultList()).thenReturn(List.of(createTraining(1L, 1L, 1L, "T1")));

        List<Training> result = dao.selectTrainerTrainingsByCriteria(
                "mike.wilson",
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 12, 31),
                "John"
        );

        assertThat(result).hasSize(1);
        verify(trainingQuery).setParameter("trainerUsername", "mike.wilson");
        verify(trainingQuery).setParameter("fromDate", LocalDate.of(2024, 1, 1));
        verify(trainingQuery).setParameter("toDate", LocalDate.of(2024, 12, 31));
        verify(trainingQuery).setParameter("traineeName", "%john%");
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    private Training createTraining(Long id, Long traineeId, Long trainerId, String trainingName) {
        Training training = new Training();
        training.setId(id);
        training.setTrainee(createTrainee(traineeId, "John", "Doe", "john.doe"));
        training.setTrainer(createTrainer(trainerId, "Mike", "Wilson", "mike.wilson"));
        training.setTrainingName(trainingName);
        training.setTrainingType(createTrainingType("Yoga"));
        training.setTrainingDate(LocalDate.now());
        training.setTrainingDuration(60);
        return training;
    }

    private Trainee createTrainee(Long id, String firstName, String lastName, String username) {
        Trainee trainee = new Trainee();
        trainee.setId(id);
        trainee.setUser(createUser(firstName, lastName, username));
        return trainee;
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
