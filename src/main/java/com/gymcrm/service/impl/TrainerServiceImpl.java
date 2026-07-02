package com.gymcrm.service.impl;

import com.gymcrm.dao.TrainerDAO;
import com.gymcrm.model.Trainer;
import com.gymcrm.service.TrainerService;
import com.gymcrm.util.UserCredentialsGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class TrainerServiceImpl implements TrainerService {
    
    private TrainerDAO trainerDAO;

    @Autowired
    public void setTrainerDAO(TrainerDAO trainerDAO) {
        this.trainerDAO = trainerDAO;
    }

    @Override
    public Trainer create(Trainer trainer) {
        if (trainer == null) {
            log.error("Attempt to create trainer with null object");
            throw new IllegalArgumentException("Trainer cannot be null");
        }

        log.info("Creating new trainer: {} {}", trainer.getFirstName(), trainer.getLastName());

        // Generate username
        String username = UserCredentialsGenerator.generateUsername(
                trainer.getFirstName(),
                trainer.getLastName(),
                existingUsername -> trainerDAO.selectByUsername(existingUsername).isPresent()
        );
        trainer.setUsername(username);

        // Generate password
        String password = UserCredentialsGenerator.generatePassword();
        trainer.setPassword(password);

        log.info("Generated credentials - Username: {}, Password: ***", username);

        Trainer created = trainerDAO.save(trainer);
        log.info("Trainer created successfully with ID: {}", created.getUserId());
        return created;
    }

    @Override
    public Trainer update(Trainer trainer) {
        if (trainer == null) {
            log.error("Attempt to update trainer with null object");
            throw new IllegalArgumentException("Trainer cannot be null");
        }
        if (trainer.getUserId() == null) {
            log.error("Attempt to update trainer without ID");
            throw new IllegalArgumentException("Trainer ID cannot be null for update");
        }

        log.info("Updating trainer with ID: {}", trainer.getUserId());
        Trainer updated = trainerDAO.update(trainer);
        if (updated == null) {
            log.warn("Trainer with ID {} not found for update", trainer.getUserId());
            throw new IllegalArgumentException("Trainer with ID " + trainer.getUserId() + " not found");
        }
        log.info("Trainer with ID {} updated successfully", trainer.getUserId());
        return updated;
    }

    @Override
    public Optional<Trainer> selectById(Long trainerId) {
        if (trainerId == null) {
            log.error("Attempt to select trainer with null ID");
            throw new IllegalArgumentException("Trainer ID cannot be null");
        }

        log.debug("Selecting trainer by ID: {}", trainerId);
        return trainerDAO.selectById(trainerId);
    }

    @Override
    public List<Trainer> selectAll() {
        log.debug("Selecting all trainers");
        return trainerDAO.selectAll();
    }
}
