package com.gymcrm.repository;

import com.gymcrm.entity.Trainee;
import com.gymcrm.entity.User;
import com.gymcrm.repository.impl.TraineeDAOImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TraineeDAOImplTest {
    private TraineeDAOImpl dao;

    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<Trainee> traineeQuery;

    @BeforeEach
    void setUp() throws Exception {
        dao = new TraineeDAOImpl();
        setField(dao, "entityManager", entityManager);
    }

    @Test
    void testSaveTrainee() {
        Trainee trainee = createTrainee(null, "John", "Doe", "john.doe");
        doAnswer(invocation -> {
            Trainee saved = invocation.getArgument(0);
            saved.setId(1L);
            return null;
        }).when(entityManager).persist(any(Trainee.class));

        Trainee saved = dao.save(trainee);

        assertThat(saved.getId()).isEqualTo(1L);
        verify(entityManager).persist(trainee);
    }

    @Test
    void testSelectById() {
        Trainee trainee = createTrainee(1L, "John", "Doe", "john.doe");
        when(entityManager.find(Trainee.class, 1L)).thenReturn(trainee);

        Optional<Trainee> result = dao.selectById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getUser().getFirstName()).isEqualTo("John");
    }

    @Test
    void testSelectByIdNotFound() {
        when(entityManager.find(Trainee.class, 999L)).thenReturn(null);

        Optional<Trainee> result = dao.selectById(999L);

        assertThat(result).isEmpty();
    }

    @Test
    void testSelectAll() {
        when(entityManager.createQuery("FROM Trainee", Trainee.class)).thenReturn(traineeQuery);
        when(traineeQuery.getResultList()).thenReturn(List.of(
                createTrainee(1L, "John", "Doe", "john.doe"),
                createTrainee(2L, "Jane", "Doe", "jane.doe")
        ));

        assertThat(dao.selectAll()).hasSize(2);
    }

    @Test
    void testUpdate() {
        Trainee trainee = createTrainee(1L, "John", "Doe", "john.doe");
        Trainee updated = createTrainee(1L, "Jane", "Doe", "jane.doe");
        when(entityManager.merge(trainee)).thenReturn(updated);

        Trainee result = dao.update(trainee);

        assertThat(result.getUser().getFirstName()).isEqualTo("Jane");
    }

    @Test
    void testDelete() {
        Trainee trainee = createTrainee(1L, "John", "Doe", "john.doe");
        when(entityManager.find(Trainee.class, 1L)).thenReturn(trainee);

        dao.delete(1L);

        verify(entityManager).remove(trainee);
    }

    @Test
    void testSelectByUsername() {
        Trainee trainee = createTrainee(1L, "John", "Doe", "john.doe");
        when(entityManager.createQuery("FROM Trainee t WHERE t.user.username = :username", Trainee.class)).thenReturn(traineeQuery);
        when(traineeQuery.setParameter("username", "john.doe")).thenReturn(traineeQuery);
        when(traineeQuery.getResultList()).thenReturn(List.of(trainee));

        Optional<Trainee> result = dao.selectByUsername("john.doe");

        assertThat(result).isPresent();
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
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
