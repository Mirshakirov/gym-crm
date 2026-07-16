package com.gymcrm.entity;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

class TrainerTest {
    @Test
    void testTrainerCreation() {
        TrainingType specialization = createTrainingType("Yoga");
        User user = createUser("Mike", "Wilson", "mike.wilson");
        Trainer trainer = new Trainer(1L, specialization, user, new ArrayList<>(), new ArrayList<>());

        assertThat(trainer.getId()).isEqualTo(1L);
        assertThat(trainer.getSpecialization().getTrainingTypeName()).isEqualTo("Yoga");
        assertThat(trainer.getUser().getUsername()).isEqualTo("mike.wilson");
    }

    @Test
    void testTrainerUserAssociation() {
        Trainer trainer = new Trainer();
        trainer.setUser(createUser("Mike", "Wilson", "mike.wilson"));

        assertThat(trainer.getUser().getFirstName()).isEqualTo("Mike");
        assertThat(trainer.getUser().getUsername()).isEqualTo("mike.wilson");
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
