package com.gymcrm.repository;

import com.gymcrm.entity.Training;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TrainingDAO {
    
    Training save(Training training);
    
    Optional<Training> selectById(Long trainingId);
    
    List<Training> selectAll();
    
    List<Training> selectByTraineeId(Long traineeId);
    
    List<Training> selectByTrainerId(Long trainerId);

    List<Training> selectTraineeTrainingsByCriteria(String traineeUsername,
                                                    LocalDate fromDate,
                                                    LocalDate toDate,
                                                    String trainerName,
                                                    String trainingType);

    List<Training> selectTrainerTrainingsByCriteria(String trainerUsername,
                                                    LocalDate fromDate,
                                                    LocalDate toDate,
                                                    String traineeName);
}
