package com.gymcrm.service;

import com.gymcrm.entity.Trainer;
import com.gymcrm.entity.Training;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TrainerService {
    
    Trainer create(Trainer trainer);
    
    Trainer update(Trainer trainer);
    
    Optional<Trainer> selectById(Long trainerId);

    Optional<Trainer> selectByUsername(String username);

    List<Trainer> selectAll();

    boolean matchCredentials(String username, String password);

    void changePassword(String username, String newPassword);

    Trainer setActiveStatus(String username, boolean active);

    List<Training> getTrainingsByCriteria(String trainerUsername,
                                          LocalDate fromDate,
                                          LocalDate toDate,
                                          String traineeName);
}
