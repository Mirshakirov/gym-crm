package com.gymcrm.repository.impl;

import com.gymcrm.repository.TrainingDAO;
import com.gymcrm.entity.Training;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class TrainingDAOImpl implements TrainingDAO {
    
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public Training save(Training training) {
        entityManager.persist(training);
        log.info("Saved training with id: {}", training.getId());
        return training;
    }

    @Override
    public Optional<Training> selectById(Long trainingId) {
        Training training = entityManager.find(Training.class, trainingId);
        return Optional.ofNullable(training);
    }

    @Override
    public List<Training> selectAll() {
        return entityManager.createQuery("FROM Training", Training.class).getResultList();
    }

    @Override
    public List<Training> selectByTraineeId(Long traineeId) {
        return entityManager.createQuery(
                "FROM Training t WHERE t.trainee.id = :traineeId",
                Training.class)
                .setParameter("traineeId", traineeId)
                .getResultList();
    }

    @Override
    public List<Training> selectByTrainerId(Long trainerId) {
        return entityManager.createQuery(
                "FROM Training t WHERE t.trainer.id = :trainerId",
                Training.class)
                .setParameter("trainerId", trainerId)
                .getResultList();
    }

    @Override
    public List<Training> selectTraineeTrainingsByCriteria(String traineeUsername,
                                                           LocalDate fromDate,
                                                           LocalDate toDate,
                                                           String trainerName,
                                                           String trainingType) {
        StringBuilder queryBuilder = new StringBuilder(
                "SELECT t FROM Training t WHERE t.trainee.user.username = :traineeUsername");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("traineeUsername", traineeUsername);

        if (fromDate != null) {
            queryBuilder.append(" AND t.trainingDate >= :fromDate");
            parameters.put("fromDate", fromDate);
        }
        if (toDate != null) {
            queryBuilder.append(" AND t.trainingDate <= :toDate");
            parameters.put("toDate", toDate);
        }
        if (trainerName != null && !trainerName.isBlank()) {
            queryBuilder.append(" AND LOWER(CONCAT(t.trainer.user.firstName, ' ', t.trainer.user.lastName)) LIKE :trainerName");
            parameters.put("trainerName", "%" + trainerName.toLowerCase() + "%");
        }
        if (trainingType != null && !trainingType.isBlank()) {
            queryBuilder.append(" AND LOWER(t.trainingType.trainingTypeName) = :trainingType");
            parameters.put("trainingType", trainingType.toLowerCase());
        }

        TypedQuery<Training> query = entityManager.createQuery(queryBuilder.toString(), Training.class);
        parameters.forEach(query::setParameter);
        return query.getResultList();
    }

    @Override
    public List<Training> selectTrainerTrainingsByCriteria(String trainerUsername,
                                                           LocalDate fromDate,
                                                           LocalDate toDate,
                                                           String traineeName) {
        StringBuilder queryBuilder = new StringBuilder(
                "SELECT t FROM Training t WHERE t.trainer.user.username = :trainerUsername");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("trainerUsername", trainerUsername);

        if (fromDate != null) {
            queryBuilder.append(" AND t.trainingDate >= :fromDate");
            parameters.put("fromDate", fromDate);
        }
        if (toDate != null) {
            queryBuilder.append(" AND t.trainingDate <= :toDate");
            parameters.put("toDate", toDate);
        }
        if (traineeName != null && !traineeName.isBlank()) {
            queryBuilder.append(" AND LOWER(CONCAT(t.trainee.user.firstName, ' ', t.trainee.user.lastName)) LIKE :traineeName");
            parameters.put("traineeName", "%" + traineeName.toLowerCase() + "%");
        }

        TypedQuery<Training> query = entityManager.createQuery(queryBuilder.toString(), Training.class);
        parameters.forEach(query::setParameter);
        return query.getResultList();
    }
}
