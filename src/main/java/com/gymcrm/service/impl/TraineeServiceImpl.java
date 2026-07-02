package com.gymcrm.service.impl;

import com.gymcrm.dao.TraineeDAO;
import com.gymcrm.model.Trainee;
import com.gymcrm.service.TraineeService;
import com.gymcrm.util.UserCredentialsGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class TraineeServiceImpl implements TraineeService {
    
    private TraineeDAO traineeDAO;

    @Autowired
    public void setTraineeDAO(TraineeDAO traineeDAO) {
        this.traineeDAO = traineeDAO;
    }

    @Override
    public Trainee create(Trainee trainee) {
        if (trainee == null) {
            log.error("Attempt to create trainee with null object");
            throw new IllegalArgumentException("Trainee cannot be null");
        }

        log.info("Creating new trainee: {} {}", trainee.getFirstName(), trainee.getLastName());

        String username = UserCredentialsGenerator.generateUsername(
                trainee.getFirstName(),
                trainee.getLastName(),
                existingUsername -> traineeDAO.selectByUsername(existingUsername).isPresent()
        );
        trainee.setUsername(username);

        String password = UserCredentialsGenerator.generatePassword();
        trainee.setPassword(password);

        log.info("Generated credentials - Username: {}, Password: ***", username);

        Trainee created = traineeDAO.save(trainee);
        log.info("Trainee created successfully with ID: {}", created.getUserId());
        return created;
    }

    @Override
    public Trainee update(Trainee trainee) {
        if (trainee == null) {
            log.error("Attempt to update trainee with null object");
            throw new IllegalArgumentException("Trainee cannot be null");
        }
        if (trainee.getUserId() == null) {
            log.error("Attempt to update trainee without ID");
            throw new IllegalArgumentException("Trainee ID cannot be null for update");
        }

        log.info("Updating trainee with ID: {}", trainee.getUserId());
        Trainee updated = traineeDAO.update(trainee);
        if (updated == null) {
            log.warn("Trainee with ID {} not found for update", trainee.getUserId());
            throw new IllegalArgumentException("Trainee with ID " + trainee.getUserId() + " not found");
        }
        log.info("Trainee with ID {} updated successfully", trainee.getUserId());
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
}
