package com.gymcrm.model;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class UserTest {
    @Test
    void testUserCreation() {
        User user = new User("John", "Doe", "john.doe", "pass123", true);
        assertThat(user.getFirstName()).isEqualTo("John");
        assertThat(user.getLastName()).isEqualTo("Doe");
        assertThat(user.getUsername()).isEqualTo("john.doe");
        assertThat(user.isActive()).isTrue();
    }

    @Test
    void testUserSettersGetters() {
        User user = new User();
        user.setFirstName("Jane");
        user.setPassword("secure");
        assertThat(user.getFirstName()).isEqualTo("Jane");
        assertThat(user.getPassword()).isEqualTo("secure");
    }
}
