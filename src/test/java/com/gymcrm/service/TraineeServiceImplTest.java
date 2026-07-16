package com.gymcrm.service;

import com.gymcrm.entity.Trainee;
import com.gymcrm.entity.Training;
import com.gymcrm.entity.User;
import com.gymcrm.repository.TraineeDAO;
import com.gymcrm.repository.TrainingDAO;
import com.gymcrm.service.impl.TraineeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TraineeServiceImplTest {
    private TraineeServiceImpl service;

    @Mock
    private TraineeDAO dao;
    @Mock
    private TrainingDAO trainingDAO;

    @BeforeEach
    void setUp() {
        service = new TraineeServiceImpl();
        service.setTraineeDAO(dao);
        service.setTrainingDAO(trainingDAO);
    }

    @Test
    void testCreateTrainee() {
        Trainee trainee = createTrainee(null, "John", "Doe", null);
        when(dao.selectByUsername(anyString())).thenReturn(Optional.empty());
        when(dao.save(any())).thenAnswer(invocation -> {
            Trainee saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        Trainee result = service.create(trainee);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUser().getUsername()).isEqualTo("John.Doe");
        verify(dao).save(any());
    }

    @Test
    void testCreateNullThrows() {
        assertThatThrownBy(() -> service.create(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testSelectById() {
        Trainee trainee = createTrainee(1L, "John", "Doe", "john.doe");
        when(dao.selectById(1L)).thenReturn(Optional.of(trainee));

        Optional<Trainee> result = service.selectById(1L);

        assertThat(result).isPresent();
    }

    @Test
    void testSelectByIdNullThrows() {
        assertThatThrownBy(() -> service.selectById(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testUpdateTrainee() {
        Trainee trainee = createTrainee(1L, "John", "Doe", "john.doe");
        when(dao.update(trainee)).thenReturn(trainee);

        Trainee result = service.update(trainee);

        assertThat(result).isEqualTo(trainee);
    }

    @Test
    void testDeleteTrainee() {
        service.delete(1L);
        verify(dao).delete(1L);
    }

    @Test
    void testSelectAll() {
        Trainee trainee1 = createTrainee(1L, "John", "Doe", "john.doe");
        Trainee trainee2 = createTrainee(2L, "Jane", "Doe", "jane.doe");
        when(dao.selectAll()).thenReturn(Arrays.asList(trainee1, trainee2));

        List<Trainee> results = service.selectAll();

        assertThat(results).hasSize(2);
        verify(dao).selectAll();
    }

    @Test
    void testUpdateNullThrows() {
        assertThatThrownBy(() -> service.update(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testUpdateNullIdThrows() {
        assertThatThrownBy(() -> service.update(new Trainee()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testUpdateNotFoundThrows() {
        Trainee trainee = createTrainee(999L, "John", "Doe", "john.doe");
        when(dao.update(trainee)).thenReturn(null);

        assertThatThrownBy(() -> service.update(trainee))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void testDeleteNullIdThrows() {
        assertThatThrownBy(() -> service.delete(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testCreateWithDuplicateUsername() {
        Trainee trainee = createTrainee(null, "John", "Doe", null);
        when(dao.selectByUsername("John.Doe")).thenReturn(Optional.of(createTrainee(5L, "John", "Doe", "John.Doe")));
        when(dao.selectByUsername("John.Doe1")).thenReturn(Optional.empty());
        when(dao.save(any())).thenAnswer(invocation -> {
            Trainee saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        Trainee result = service.create(trainee);

        assertThat(result.getUser().getUsername()).isEqualTo("John.Doe1");
    }

    @Test
    void testSelectByIdNotFound() {
        when(dao.selectById(999L)).thenReturn(Optional.empty());

        Optional<Trainee> result = service.selectById(999L);

        assertThat(result).isEmpty();
    }

    @Test
    void testSelectByUsername() {
        Trainee trainee = createTrainee(1L, "John", "Doe", "john.doe");
        when(dao.selectByUsername("john.doe")).thenReturn(Optional.of(trainee));

        assertThat(service.selectByUsername("john.doe")).contains(trainee);
    }

    @Test
    void testSelectByUsernameBlankThrows() {
        assertThatThrownBy(() -> service.selectByUsername(" "))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testMatchCredentials() {
        Trainee trainee = createTrainee(1L, "John", "Doe", "john.doe");
        when(dao.selectByUsername("john.doe")).thenReturn(Optional.of(trainee));

        assertThat(service.matchCredentials("john.doe", "secret")).isTrue();
        assertThat(service.matchCredentials("john.doe", "wrong")).isFalse();
    }

    @Test
    void testChangePassword() {
        Trainee trainee = createTrainee(1L, "John", "Doe", "john.doe");
        when(dao.selectByUsername("john.doe")).thenReturn(Optional.of(trainee));
        when(dao.update(trainee)).thenReturn(trainee);

        service.changePassword("john.doe", "newSecret");

        assertThat(trainee.getUser().getPassword()).isEqualTo("newSecret");
        verify(dao).update(trainee);
    }

    @Test
    void testSetActiveStatus() {
        Trainee trainee = createTrainee(1L, "John", "Doe", "john.doe");
        when(dao.selectByUsername("john.doe")).thenReturn(Optional.of(trainee));
        when(dao.update(trainee)).thenReturn(trainee);

        Trainee updated = service.setActiveStatus("john.doe", false);

        assertThat(updated.getUser().isActive()).isFalse();
    }

    @Test
    void testDeleteByUsername() {
        Trainee trainee = createTrainee(1L, "John", "Doe", "john.doe");
        when(dao.selectByUsername("john.doe")).thenReturn(Optional.of(trainee));

        service.deleteByUsername("john.doe");

        verify(dao).deleteByUsername("john.doe");
    }

    @Test
    void testGetTrainingsByCriteria() {
        Training training = new Training();
        when(trainingDAO.selectTraineeTrainingsByCriteria("john.doe", null, null, null, null))
                .thenReturn(List.of(training));

        List<Training> result = service.getTrainingsByCriteria("john.doe", null, null, null, null);

        assertThat(result).containsExactly(training);
    }

    private Trainee createTrainee(Long id, String firstName, String lastName, String username) {
        Trainee trainee = new Trainee();
        trainee.setId(id);
        trainee.setUser(createUser(firstName, lastName, username));
        return trainee;
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
