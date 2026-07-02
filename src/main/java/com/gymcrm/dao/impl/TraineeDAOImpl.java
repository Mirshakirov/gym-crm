package com.gymcrm.dao.impl;

import com.gymcrm.dao.TraineeDAO;
import com.gymcrm.model.Trainee;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class TraineeDAOImpl implements TraineeDAO {
    
    private final Map<Long, Trainee> traineeStorage;

    public TraineeDAOImpl(Map<Long, Trainee> traineeStorage) {
        this.traineeStorage = traineeStorage;
    }

    @Override
    public Trainee save(Trainee trainee) {
        long newId = traineeStorage.keySet().stream().max(Long::compare).orElse(0L) + 1;
        trainee.setUserId(newId);
        traineeStorage.put(trainee.getUserId(), trainee);
        return trainee;
    }

    @Override
    public Trainee update(Trainee trainee) {
        if (traineeStorage.containsKey(trainee.getUserId())) {
            traineeStorage.put(trainee.getUserId(), trainee);
            return trainee;
        }
        return null;
    }

    @Override
    public void delete(Long traineeId) {
        traineeStorage.remove(traineeId);
    }

    @Override
    public Optional<Trainee> selectById(Long traineeId) {
        return Optional.ofNullable(traineeStorage.get(traineeId));
    }

    @Override
    public List<Trainee> selectAll() {
        return new ArrayList<>(traineeStorage.values());
    }

    @Override
    public Optional<Trainee> selectByUsername(String username) {
        return traineeStorage.values().stream()
                .filter(trainee -> trainee.getUsername() != null && trainee.getUsername().equals(username))
                .findFirst();
    }
}
