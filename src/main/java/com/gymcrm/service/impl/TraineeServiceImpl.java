package com.gymcrm.service.impl;

import com.gymcrm.repository.TraineeDAO;
import com.gymcrm.entity.Trainee;
import com.gymcrm.entity.Training;
import com.gymcrm.repository.TrainingDAO;
import com.gymcrm.service.TraineeService;
import com.gymcrm.util.UserCredentialsGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class TraineeServiceImpl implements TraineeService {
    
    private TraineeDAO traineeDAO;
    private TrainingDAO trainingDAO;

    @Autowired
    public void setTraineeDAO(TraineeDAO traineeDAO) {
        this.traineeDAO = traineeDAO;
    }

    @Autowired
    public void setTrainingDAO(TrainingDAO trainingDAO) {
        this.trainingDAO = trainingDAO;
    }

    @Override
    public Trainee create(Trainee trainee) {
        if (trainee == null) {
            log.error("Attempt to create trainee with null object");
            throw new IllegalArgumentException("Trainee cannot be null");
        }

        log.info("Creating new trainee: {} {}", trainee.getUser().getFirstName(), trainee.getUser().getLastName());

        String username = UserCredentialsGenerator.generateUsername(
                trainee.getUser().getFirstName(),
                trainee.getUser().getLastName(),
                existingUsername -> traineeDAO.selectByUsername(existingUsername).isPresent()
        );
        trainee.getUser().setUsername(username);

        String password = UserCredentialsGenerator.generatePassword();
        trainee.getUser().setPassword(password);

        log.info("Generated credentials - Username: {}, Password: ***", username);

        Trainee created = traineeDAO.save(trainee);
        log.info("Trainee created successfully with ID: {}", created.getId());
        return created;
    }

    @Override
    public Trainee update(Trainee trainee) {
        if (trainee == null) {
            log.error("Attempt to update trainee with null object");
            throw new IllegalArgumentException("Trainee cannot be null");
        }
        if (trainee.getId() == null) {
            log.error("Attempt to update trainee without ID");
            throw new IllegalArgumentException("Trainee ID cannot be null for update");
        }

        log.info("Updating trainee with ID: {}", trainee.getId());
        Trainee updated = traineeDAO.update(trainee);
        if (updated == null) {
            log.warn("Trainee with ID {} not found for update", trainee.getId());
            throw new IllegalArgumentException("Trainee with ID " + trainee.getId() + " not found");
        }
        log.info("Trainee with ID {} updated successfully", trainee.getId());
        return updated;
    }

    @Override
    public void delete(Long traineeId) {
        if (traineeId == null) {
            log.error("Attempt to delete trainee with null ID");
            throw new IllegalArgumentException("Trainee ID cannot be null");
        }

        log.info("Deleting trainee with ID: {}", traineeId);
        traineeDAO.delete(traineeId);
        log.info("Trainee with ID {} deleted successfully", traineeId);
    }

    @Override
    public Optional<Trainee> selectById(Long traineeId) {
        if (traineeId == null) {
            log.error("Attempt to select trainee with null ID");
            throw new IllegalArgumentException("Trainee ID cannot be null");
        }

        log.debug("Selecting trainee by ID: {}", traineeId);
        return traineeDAO.selectById(traineeId);
    }

    @Override
    public List<Trainee> selectAll() {
        log.debug("Selecting all trainees");
        return traineeDAO.selectAll();
    }

    @Override
    public Optional<Trainee> selectByUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or blank");
        }
        return traineeDAO.selectByUsername(username);
    }

    @Override
    public boolean matchCredentials(String username, String password) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or blank");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or blank");
        }
        return traineeDAO.selectByUsername(username)
                .map(trainee -> password.equals(trainee.getUser().getPassword()))
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
        Trainee trainee = traineeDAO.selectByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Trainee not found for username: " + username));
        trainee.getUser().setPassword(newPassword);
        traineeDAO.update(trainee);
    }

    @Override
    public Trainee setActiveStatus(String username, boolean active) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or blank");
        }
        Trainee trainee = traineeDAO.selectByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Trainee not found for username: " + username));
        trainee.getUser().setActive(active);
        return traineeDAO.update(trainee);
    }

    @Override
    public void deleteByUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or blank");
        }
        if (traineeDAO.selectByUsername(username).isEmpty()) {
            throw new IllegalArgumentException("Trainee not found for username: " + username);
        }
        traineeDAO.deleteByUsername(username);
    }

    @Override
    public List<Training> getTrainingsByCriteria(String traineeUsername,
                                                 LocalDate fromDate,
                                                 LocalDate toDate,
                                                 String trainerName,
                                                 String trainingType) {
        if (traineeUsername == null || traineeUsername.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or blank");
        }
        return trainingDAO.selectTraineeTrainingsByCriteria(
                traineeUsername,
                fromDate,
                toDate,
                trainerName,
                trainingType
        );
    }
}
