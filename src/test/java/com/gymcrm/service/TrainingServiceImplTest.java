package com.gymcrm.service;

import com.gymcrm.entity.Trainee;
import com.gymcrm.entity.Trainer;
import com.gymcrm.entity.Training;
import com.gymcrm.entity.TrainingType;
import com.gymcrm.entity.User;
import com.gymcrm.repository.TrainingDAO;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        Training training = createTraining(1L, 1L, 2L, "Yoga");
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
    void testCreateNullTraineeThrows() {
        Training training = new Training();
        training.setTrainer(createTrainer(1L, "Mike", "Wilson", "mike.wilson"));

        assertThatThrownBy(() -> service.create(training))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Trainee");
    }

    @Test
    void testCreateNullTrainerThrows() {
        Training training = new Training();
        training.setTrainee(createTrainee(1L, "John", "Doe", "john.doe"));

        assertThatThrownBy(() -> service.create(training))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Trainer");
    }

    @Test
    void testSelectById() {
        Training training = createTraining(1L, 1L, 2L, "Yoga");
        when(dao.selectById(1L)).thenReturn(Optional.of(training));

        Optional<Training> result = service.selectById(1L);

        assertThat(result).isPresent();
    }

    @Test
    void testSelectByIdNullThrows() {
        assertThatThrownBy(() -> service.selectById(null))
                .isInstanceOf(IllegalArgumentException.class);
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
