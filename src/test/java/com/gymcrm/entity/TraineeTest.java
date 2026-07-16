package com.gymcrm.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

class TraineeTest {
    @Test
    void testTraineeCreation() {
        LocalDate dob = LocalDate.of(1990, 5, 15);
        User user = createUser("John", "Doe", "john.doe");
        Trainee trainee = new Trainee(1L, dob, "123 Main St", user, new ArrayList<>(), new ArrayList<>());

        assertThat(trainee.getId()).isEqualTo(1L);
        assertThat(trainee.getDateOfBirth()).isEqualTo(dob);
        assertThat(trainee.getAddress()).isEqualTo("123 Main St");
        assertThat(trainee.getUser().getUsername()).isEqualTo("john.doe");
    }

    @Test
    void testTraineeUserAssociation() {
        Trainee trainee = new Trainee();
        trainee.setUser(createUser("John", "Doe", "john.doe"));

        assertThat(trainee.getUser().getFirstName()).isEqualTo("John");
        assertThat(trainee.getUser().getUsername()).isEqualTo("john.doe");
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
