package com.gymcrm.service;

import com.gymcrm.entity.Trainee;
import com.gymcrm.entity.Training;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TraineeService {
    
    Trainee create(Trainee trainee);
    
    Trainee update(Trainee trainee);
    
    void delete(Long traineeId);
    
    Optional<Trainee> selectById(Long traineeId);

    Optional<Trainee> selectByUsername(String username);

    List<Trainee> selectAll();

    boolean matchCredentials(String username, String password);

    void changePassword(String username, String newPassword);

    Trainee setActiveStatus(String username, boolean active);

    void deleteByUsername(String username);

    List<Training> getTrainingsByCriteria(String traineeUsername,
                                          LocalDate fromDate,
                                          LocalDate toDate,
                                          String trainerName,
                                          String trainingType);
}
