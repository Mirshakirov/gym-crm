package com.gymcrm.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class TrainingTest {
    @Test
    void testTrainingCreation() {
        Training training = createTraining(1L, 1L, 2L, "Morning Yoga");

        assertThat(training.getTrainee().getId()).isEqualTo(1L);
        assertThat(training.getTrainer().getId()).isEqualTo(2L);
        assertThat(training.getTrainingName()).isEqualTo("Morning Yoga");
        assertThat(training.getTrainingDuration()).isEqualTo(60);
    }

    @Test
    void testTrainingSettersGetters() {
        Training training = new Training();
        training.setTrainee(createTrainee(2L, "Jane", "Doe", "jane.doe"));
        training.setTrainer(createTrainer(3L, "Mike", "Wilson", "mike.wilson"));

        assertThat(training.getTrainee().getId()).isEqualTo(2L);
        assertThat(training.getTrainer().getId()).isEqualTo(3L);
    }

    private Training createTraining(Long id, Long traineeId, Long trainerId, String name) {
        Training training = new Training();
        training.setId(id);
        training.setTrainee(createTrainee(traineeId, "John", "Doe", "john.doe"));
        training.setTrainer(createTrainer(trainerId, "Mike", "Wilson", "mike.wilson"));
        training.setTrainingName(name);
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
