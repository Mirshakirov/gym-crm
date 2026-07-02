package com.gymcrm.dao.impl;

import com.gymcrm.dao.TrainingDAO;
import com.gymcrm.model.Training;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class TrainingDAOImpl implements TrainingDAO {
    
    private final Map<Long, Training> trainingStorage;

    public TrainingDAOImpl(Map<Long, Training> trainingStorage) {
        this.trainingStorage = trainingStorage;
    }

    @Override
    public Training save(Training training) {
        long newId = trainingStorage.keySet().stream().max(Long::compare).orElse(0L) + 1;
        trainingStorage.put(newId, training);
        return training;
    }

    @Override
    public Optional<Training> selectById(Long trainingId) {
        return Optional.ofNullable(trainingStorage.get(trainingId));
    }

    @Override
    public List<Training> selectAll() {
        return new ArrayList<>(trainingStorage.values());
    }

    @Override
    public List<Training> selectByTraineeId(Long traineeId) {
        return trainingStorage.values().stream()
                .filter(training -> training.getTraineeId() != null && training.getTraineeId().equals(traineeId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Training> selectByTrainerId(Long trainerId) {
        return trainingStorage.values().stream()
                .filter(training -> training.getTrainerId() != null && training.getTrainerId().equals(trainerId))
                .collect(Collectors.toList());
    }
}
