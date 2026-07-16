package com.gymcrm.service.impl;

import com.gymcrm.repository.TrainerDAO;
import com.gymcrm.entity.Trainer;
import com.gymcrm.entity.Training;
import com.gymcrm.repository.TrainingDAO;
import com.gymcrm.service.TrainerService;
import com.gymcrm.util.UserCredentialsGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class TrainerServiceImpl implements TrainerService {
    
    private TrainerDAO trainerDAO;
    private TrainingDAO trainingDAO;

    @Autowired
    public void setTrainerDAO(TrainerDAO trainerDAO) {
        this.trainerDAO = trainerDAO;
    }

    @Autowired
    public void setTrainingDAO(TrainingDAO trainingDAO) {
        this.trainingDAO = trainingDAO;
    }

    @Override
    public Trainer create(Trainer trainer) {
        if (trainer == null) {
            log.error("Attempt to create trainer with null object");
            throw new IllegalArgumentException("Trainer cannot be null");
        }

        log.info("Creating new trainer: {} {}", trainer.getUser().getFirstName(), trainer.getUser().getLastName());

        String username = UserCredentialsGenerator.generateUsername(
                trainer.getUser().getFirstName(),
                trainer.getUser().getLastName(),
                existingUsername -> trainerDAO.selectByUsername(existingUsername).isPresent()
        );
        trainer.getUser().setUsername(username);

        String password = UserCredentialsGenerator.generatePassword();
        trainer.getUser().setPassword(password);

        log.info("Generated credentials - Username: {}, Password: ***", username);

        Trainer created = trainerDAO.save(trainer);
        log.info("Trainer created successfully with ID: {}", created.getId());
        return created;
    }

    @Override
    public Trainer update(Trainer trainer) {
        if (trainer == null) {
            log.error("Attempt to update trainer with null object");
            throw new IllegalArgumentException("Trainer cannot be null");
        }
        if (trainer.getId() == null) {
            log.error("Attempt to update trainer without ID");
            throw new IllegalArgumentException("Trainer ID cannot be null for update");
        }

        log.info("Updating trainer with ID: {}", trainer.getId());
        Trainer updated = trainerDAO.update(trainer);
        if (updated == null) {
            log.warn("Trainer with ID {} not found for update", trainer.getId());
            throw new IllegalArgumentException("Trainer with ID " + trainer.getId() + " not found");
        }
        log.info("Trainer with ID {} updated successfully", trainer.getId());
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

    @Override
    public Optional<Trainer> selectByUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or blank");
        }
        return trainerDAO.selectByUsername(username);
    }

    @Override
    public boolean matchCredentials(String username, String password) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or blank");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or blank");
        }
        return trainerDAO.selectByUsername(username)
                .map(trainer -> password.equals(trainer.getUser().getPassword()))
                .orElse(false);
    }

    @Override
    public void changePassword(String username, String newPassword) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or blank");
        }
        if (newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or blank");
        }
        Trainer trainer = trainerDAO.selectByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Trainer not found for username: " + username));
        trainer.getUser().setPassword(newPassword);
        trainerDAO.update(trainer);
    }

    @Override
    public Trainer setActiveStatus(String username, boolean active) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or blank");
        }
        Trainer trainer = trainerDAO.selectByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Trainer not found for username: " + username));
        trainer.getUser().setActive(active);
        return trainerDAO.update(trainer);
    }

    @Override
    public List<Training> getTrainingsByCriteria(String trainerUsername,
                                                 LocalDate fromDate,
                                                 LocalDate toDate,
                                                 String traineeName) {
        if (trainerUsername == null || trainerUsername.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or blank");
        }
        return trainingDAO.selectTrainerTrainingsByCriteria(
                trainerUsername,
                fromDate,
                toDate,
                traineeName
        );
    }
}
