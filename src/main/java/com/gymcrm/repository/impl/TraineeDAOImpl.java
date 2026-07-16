package com.gymcrm.repository.impl;

import com.gymcrm.repository.TraineeDAO;
import com.gymcrm.entity.Trainee;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class TraineeDAOImpl implements TraineeDAO {
    
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public Trainee save(Trainee trainee) {
        entityManager.persist(trainee);
        log.info("Saved trainee with id: {}", trainee.getId());
        return trainee;
    }

    @Override
    @Transactional
    public Trainee update(Trainee trainee) {
        Trainee merged = entityManager.merge(trainee);
        log.info("Updated trainee with id: {}", trainee.getId());
        return merged;
    }

    @Override
    @Transactional
    public void delete(Long traineeId) {
        Trainee trainee = entityManager.find(Trainee.class, traineeId);
        if (trainee != null) {
            entityManager.remove(trainee);
            log.info("Deleted trainee with id: {}", traineeId);
        }
    }

    @Override
    @Transactional
    public void deleteByUsername(String username) {
        selectByUsername(username).ifPresent(trainee -> {
            entityManager.remove(entityManager.contains(trainee) ? trainee : entityManager.merge(trainee));
            log.info("Deleted trainee with username: {}", username);
        });
    }

    @Override
    public Optional<Trainee> selectById(Long traineeId) {
        Trainee trainee = entityManager.find(Trainee.class, traineeId);
        return Optional.ofNullable(trainee);
    }

    @Override
    public List<Trainee> selectAll() {
        return entityManager.createQuery("FROM Trainee", Trainee.class).getResultList();
    }

    @Override
    public Optional<Trainee> selectByUsername(String username) {
        return entityManager.createQuery(
                "FROM Trainee t WHERE t.user.username = :username",
                Trainee.class)
                .setParameter("username", username)
                .getResultList()
                .stream()
                .findFirst();
    }
}