package com.gymcrm.model;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class TrainerTest {
    @Test
    void testTrainerCreation() {
        TrainingType type = new TrainingType("Yoga");
        Trainer trainer = new Trainer(1L, type);
        assertThat(trainer.getUserId()).isEqualTo(1L);
        assertThat(trainer.getTrainingType().getTrainingTypeName()).isEqualTo("Yoga");
    }

    @Test
    void testTrainerInheritance() {
        Trainer trainer = new Trainer();
        trainer.setFirstName("Mike");
        trainer.setUsername("mike.wilson");
        assertThat(trainer.getFirstName()).isEqualTo("Mike");
        assertThat(trainer.getUsername()).isEqualTo("mike.wilson");
    }
}
