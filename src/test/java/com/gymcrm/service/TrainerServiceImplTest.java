package com.gymcrm.service;

import com.gymcrm.dao.TrainerDAO;
import com.gymcrm.model.Trainer;
import com.gymcrm.model.TrainingType;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerServiceImplTest {
    private TrainerServiceImpl service;
    @Mock
    private TrainerDAO dao;

    @BeforeEach
    void setUp() {
        service = new TrainerServiceImpl();
        service.setTrainerDAO(dao);
    }

    @Test
    void testCreateTrainer() {
        Trainer trainer = new Trainer();
        trainer.setFirstName("Mike");
        trainer.setLastName("Wilson");
        when(dao.selectByUsername(anyString())).thenReturn(Optional.empty());
        when(dao.save(any())).thenAnswer(inv -> {
            Trainer t = inv.getArgument(0);
            t.setUserId(1L);
            return t;
        });
        Trainer result = service.create(trainer);
        assertThat(result.getUserId()).isEqualTo(1L);
        verify(dao).save(any());
    }

    @Test
    void testSelectById() {
        Trainer trainer = new Trainer();
        trainer.setUserId(1L);
        when(dao.selectById(1L)).thenReturn(Optional.of(trainer));
        Optional<Trainer> result = service.selectById(1L);
        assertThat(result).isPresent();
    }

    @Test
    void testUpdateTrainer() {
        Trainer trainer = new Trainer();
        trainer.setUserId(1L);
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
        Trainer trainer1 = new Trainer();
        trainer1.setUserId(1L);
        Trainer trainer2 = new Trainer();
        trainer2.setUserId(2L);
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
        Trainer trainer = new Trainer();
        assertThatThrownBy(() -> service.update(trainer))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testUpdateNotFoundThrows() {
        Trainer trainer = new Trainer();
        trainer.setUserId(999L);
        when(dao.update(trainer)).thenReturn(null);
        assertThatThrownBy(() -> service.update(trainer))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("not found");
    }

    @Test
    void testCreateWithDuplicateUsername() {
        Trainer trainer = new Trainer();
        trainer.setFirstName("Mike");
        trainer.setLastName("Wilson");
        when(dao.selectByUsername("Mike.Wilson")).thenReturn(Optional.of(new Trainer()));
        when(dao.selectByUsername("Mike.Wilson1")).thenReturn(Optional.empty());
        when(dao.save(any())).thenAnswer(inv -> {
            Trainer t = inv.getArgument(0);
            t.setUserId(1L);
            return t;
        });
        Trainer result = service.create(trainer);
        assertThat(result.getUsername()).isEqualTo("Mike.Wilson1");
    }

    @Test
    void testSelectByIdNotFound() {
        when(dao.selectById(999L)).thenReturn(Optional.empty());
        Optional<Trainer> result = service.selectById(999L);
        assertThat(result).isEmpty();
    }
}
