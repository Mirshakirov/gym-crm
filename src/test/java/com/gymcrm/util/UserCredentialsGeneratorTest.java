package com.gymcrm.util;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserCredentialsGeneratorTest {
    @Test
    void testGenerateUsernameBasic() {
        String username = UserCredentialsGenerator.generateUsername("John", "Doe", u -> false);
        assertThat(username).isEqualTo("John.Doe");
    }

    @Test
    void testGenerateUsernameWithDuplicate() {
        String username = UserCredentialsGenerator.generateUsername(
            "John", "Doe", 
            u -> u.equals("John.Doe")
        );
        assertThat(username).isEqualTo("John.Doe1");
    }

    @Test
    void testGenerateUsernameNullThrows() {
        assertThatThrownBy(() -> UserCredentialsGenerator.generateUsername(null, "Doe", u -> false))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testGeneratePasswordLength() {
        String password = UserCredentialsGenerator.generatePassword();
        assertThat(password).hasSize(10);
    }

    @Test
    void testGeneratePasswordNotEmpty() {
        String password = UserCredentialsGenerator.generatePassword();
        assertThat(password).isNotEmpty();
    }
}
