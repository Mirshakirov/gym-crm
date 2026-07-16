package com.gymcrm.service;

import com.gymcrm.entity.Trainer;
import com.gymcrm.entity.Training;
import com.gymcrm.entity.TrainingType;
import com.gymcrm.entity.User;
import com.gymcrm.repository.TrainerDAO;
import com.gymcrm.repository.TrainingDAO;
import com.gymcrm.service.impl.TrainerServiceImpl;
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
class TrainerServiceImplTest {
    private TrainerServiceImpl service;

    @Mock
    private TrainerDAO dao;
    @Mock
    private TrainingDAO trainingDAO;

    @BeforeEach
    void setUp() {
        service = new TrainerServiceImpl();
        service.setTrainerDAO(dao);
        service.setTrainingDAO(trainingDAO);
    }

    @Test
    void testCreateTrainer() {
        Trainer trainer = createTrainer(null, "Mike", "Wilson", null);
        when(dao.selectByUsername(anyString())).thenReturn(Optional.empty());
        when(dao.save(any())).thenAnswer(invocation -> {
            Trainer saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        Trainer result = service.create(trainer);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUser().getUsername()).isEqualTo("Mike.Wilson");
        verify(dao).save(any());
    }

    @Test
    void testSelectById() {
        Trainer trainer = createTrainer(1L, "Mike", "Wilson", "mike.wilson");
        when(dao.selectById(1L)).thenReturn(Optional.of(trainer));

        Optional<Trainer> result = service.selectById(1L);

        assertThat(result).isPresent();
    }

    @Test
    void testUpdateTrainer() {
        Trainer trainer = createTrainer(1L, "Mike", "Wilson", "mike.wilson");
        when(dao.update(trainer)).thenReturn(trainer);

        Trainer result = service.update(trainer);

        assertThat(result).isEqualTo(trainer);
    }

    @Test
    void testSelectByIdNullThrows() {
        assertThatThrownBy(() -> service.selectById(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testCreateNullThrows() {
        assertThatThrownBy(() -> service.create(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testSelectAll() {
        Trainer trainer1 = createTrainer(1L, "Mike", "Wilson", "mike.wilson");
        Trainer trainer2 = createTrainer(2L, "Jane", "Smith", "jane.smith");
        when(dao.selectAll()).thenReturn(Arrays.asList(trainer1, trainer2));

        List<Trainer> results = service.selectAll();

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
        assertThatThrownBy(() -> service.update(new Trainer()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testUpdateNotFoundThrows() {
        Trainer trainer = createTrainer(999L, "Mike", "Wilson", "mike.wilson");
        when(dao.update(trainer)).thenReturn(null);

        assertThatThrownBy(() -> service.update(trainer))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void testCreateWithDuplicateUsername() {
        Trainer trainer = createTrainer(null, "Mike", "Wilson", null);
        when(dao.selectByUsername("Mike.Wilson")).thenReturn(Optional.of(createTrainer(5L, "Mike", "Wilson", "Mike.Wilson")));
        when(dao.selectByUsername("Mike.Wilson1")).thenReturn(Optional.empty());
        when(dao.save(any())).thenAnswer(invocation -> {
            Trainer saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        Trainer result = service.create(trainer);

        assertThat(result.getUser().getUsername()).isEqualTo("Mike.Wilson1");
    }

    @Test
    void testSelectByIdNotFound() {
        when(dao.selectById(999L)).thenReturn(Optional.empty());

        Optional<Trainer> result = service.selectById(999L);

        assertThat(result).isEmpty();
    }

    @Test
    void testSelectByUsername() {
        Trainer trainer = createTrainer(1L, "Mike", "Wilson", "mike.wilson");
        when(dao.selectByUsername("mike.wilson")).thenReturn(Optional.of(trainer));

        assertThat(service.selectByUsername("mike.wilson")).contains(trainer);
    }

    @Test
    void testSelectByUsernameBlankThrows() {
        assertThatThrownBy(() -> service.selectByUsername(" "))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testMatchCredentials() {
        Trainer trainer = createTrainer(1L, "Mike", "Wilson", "mike.wilson");
        when(dao.selectByUsername("mike.wilson")).thenReturn(Optional.of(trainer));

        assertThat(service.matchCredentials("mike.wilson", "secret")).isTrue();
        assertThat(service.matchCredentials("mike.wilson", "wrong")).isFalse();
    }

    @Test
    void testChangePassword() {
        Trainer trainer = createTrainer(1L, "Mike", "Wilson", "mike.wilson");
        when(dao.selectByUsername("mike.wilson")).thenReturn(Optional.of(trainer));
        when(dao.update(trainer)).thenReturn(trainer);

        service.changePassword("mike.wilson", "newSecret");

        assertThat(trainer.getUser().getPassword()).isEqualTo("newSecret");
        verify(dao).update(trainer);
    }

    @Test
    void testSetActiveStatus() {
        Trainer trainer = createTrainer(1L, "Mike", "Wilson", "mike.wilson");
        when(dao.selectByUsername("mike.wilson")).thenReturn(Optional.of(trainer));
        when(dao.update(trainer)).thenReturn(trainer);

        Trainer updated = service.setActiveStatus("mike.wilson", false);

        assertThat(updated.getUser().isActive()).isFalse();
    }

    @Test
    void testGetTrainingsByCriteria() {
        Training training = new Training();
        when(trainingDAO.selectTrainerTrainingsByCriteria("mike.wilson", null, null, null))
                .thenReturn(List.of(training));

        List<Training> result = service.getTrainingsByCriteria("mike.wilson", null, null, null);

        assertThat(result).containsExactly(training);
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
