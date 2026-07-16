package com.gymcrm.repository.impl;

import com.gymcrm.repository.TrainerDAO;
import com.gymcrm.entity.Trainer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class TrainerDAOImpl implements TrainerDAO {
    
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public Trainer save(Trainer trainer) {
        entityManager.persist(trainer);
        log.info("Saved trainer with id: {}", trainer.getId());
        return trainer;
    }

    @Override
    @Transactional
    public Trainer update(Trainer trainer) {
        Trainer merged = entityManager.merge(trainer);
        log.info("Updated trainer with id: {}", trainer.getId());
        return merged;
    }

    @Override
    public Optional<Trainer> selectById(Long trainerId) {
        Trainer trainer = entityManager.find(Trainer.class, trainerId);
        return Optional.ofNullable(trainer);
    }

    @Override
    public List<Trainer> selectAll() {
        return entityManager.createQuery("FROM Trainer", Trainer.class).getResultList();
    }

    @Override
    public Optional<Trainer> selectByUsername(String username) {
        return entityManager.createQuery(
                "FROM Trainer t WHERE t.user.username = :username",
                Trainer.class)
                .setParameter("username", username)
                .getResultList()
                .stream()
                .findFirst();
    }
}
