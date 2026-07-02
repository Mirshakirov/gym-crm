package com.gymcrm.dao.impl;

import com.gymcrm.dao.TrainerDAO;
import com.gymcrm.model.Trainer;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class TrainerDAOImpl implements TrainerDAO {
    
    private final Map<Long, Trainer> trainerStorage;

    public TrainerDAOImpl(Map<Long, Trainer> trainerStorage) {
        this.trainerStorage = trainerStorage;
    }

    @Override
    public Trainer save(Trainer trainer) {
        long newId = trainerStorage.keySet().stream().max(Long::compare).orElse(0L) + 1;
        trainer.setUserId(newId);
        trainerStorage.put(trainer.getUserId(), trainer);
        return trainer;
    }

    @Override
    public Trainer update(Trainer trainer) {
        if (trainerStorage.containsKey(trainer.getUserId())) {
            trainerStorage.put(trainer.getUserId(), trainer);
            return trainer;
        }
        return null;
    }

    @Override
    public Optional<Trainer> selectById(Long trainerId) {
        return Optional.ofNullable(trainerStorage.get(trainerId));
    }

    @Override
    public List<Trainer> selectAll() {
        return new ArrayList<>(trainerStorage.values());
    }

    @Override
    public Optional<Trainer> selectByUsername(String username) {
        return trainerStorage.values().stream()
                .filter(trainer -> trainer.getUsername() != null && trainer.getUsername().equals(username))
                .findFirst();
    }
}
