package com.gymcrm.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.assertj.core.api.Assertions.assertThat;

class TraineeTest {
    @Test
    void testTraineeCreation() {
        LocalDate dob = LocalDate.of(1990, 5, 15);
        Trainee trainee = new Trainee(1L, dob, "123 Main St");
        assertThat(trainee.getUserId()).isEqualTo(1L);
        assertThat(trainee.getDateOfBirth()).isEqualTo(dob);
        assertThat(trainee.getAddress()).isEqualTo("123 Main St");
    }

    @Test
    void testTraineeInheritance() {
        Trainee trainee = new Trainee();
        trainee.setFirstName("John");
        trainee.setUsername("john.doe");
        assertThat(trainee.getFirstName()).isEqualTo("John");
        assertThat(trainee.getUsername()).isEqualTo("john.doe");
    }
}
