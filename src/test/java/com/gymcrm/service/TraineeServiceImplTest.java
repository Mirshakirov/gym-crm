package com.gymcrm.service;

import com.gymcrm.dao.TraineeDAO;
import com.gymcrm.model.Trainee;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeServiceImplTest {
    private TraineeServiceImpl service;
    @Mock
    private TraineeDAO dao;

    @BeforeEach
    void setUp() {
        service = new TraineeServiceImpl();
        service.setTraineeDAO(dao);
    }

    @Test
    void testCreateTrainee() {
        Trainee trainee = new Trainee();
        trainee.setFirstName("John");
        trainee.setLastName("Doe");
        when(dao.selectByUsername(anyString())).thenReturn(Optional.empty());
        when(dao.save(any())).thenAnswer(inv -> {
            Trainee t = inv.getArgument(0);
            t.setUserId(1L);
            return t;
        });
        Trainee result = service.create(trainee);
        assertThat(result.getUserId()).isEqualTo(1L);
        verify(dao).save(any());
    }

    @Test
    void testCreateNullThrows() {
        assertThatThrownBy(() -> service.create(null))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testSelectById() {
        Trainee trainee = new Trainee();
        trainee.setUserId(1L);
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
        Trainee trainee = new Trainee();
        trainee.setUserId(1L);
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
        Trainee trainee1 = new Trainee();
        trainee1.setUserId(1L);
        Trainee trainee2 = new Trainee();
        trainee2.setUserId(2L);
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
        Trainee trainee = new Trainee();
        assertThatThrownBy(() -> service.update(trainee))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testUpdateNotFoundThrows() {
        Trainee trainee = new Trainee();
        trainee.setUserId(999L);
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
        Trainee trainee = new Trainee();
        trainee.setFirstName("John");
        trainee.setLastName("Doe");
        when(dao.selectByUsername("John.Doe")).thenReturn(Optional.of(new Trainee()));
        when(dao.selectByUsername("John.Doe1")).thenReturn(Optional.empty());
        when(dao.save(any())).thenAnswer(inv -> {
            Trainee t = inv.getArgument(0);
            t.setUserId(1L);
            return t;
        });
        Trainee result = service.create(trainee);
        assertThat(result.getUsername()).isEqualTo("John.Doe1");
    }

    @Test
    void testSelectByIdNotFound() {
        when(dao.selectById(999L)).thenReturn(Optional.empty());
        Optional<Trainee> result = service.selectById(999L);
        assertThat(result).isEmpty();
    }
}
