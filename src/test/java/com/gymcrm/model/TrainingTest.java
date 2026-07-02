package com.gymcrm.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.assertj.core.api.Assertions.assertThat;

class TrainingTest {
    @Test
    void testTrainingCreation() {
        TrainingType type = new TrainingType("Yoga");
        LocalDate date = LocalDate.now();
        Training training = new Training(1L, 1L, "Morning Yoga", type, date, 60);
        assertThat(training.getTraineeId()).isEqualTo(1L);
        assertThat(training.getTrainerId()).isEqualTo(1L);
        assertThat(training.getTrainingName()).isEqualTo("Morning Yoga");
        assertThat(training.getTrainingDuration()).isEqualTo(60);
    }

    @Test
    void testTrainingSettersGetters() {
        Training training = new Training();
        training.setTraineeId(2L);
        training.setTrainerId(2L);
        assertThat(training.getTraineeId()).isEqualTo(2L);
        assertThat(training.getTrainerId()).isEqualTo(2L);
    }
}
