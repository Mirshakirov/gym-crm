package com.gymcrm.dao;

import com.gymcrm.model.Training;

import java.util.List;
import java.util.Optional;

public interface TrainingDAO {
    
    Training save(Training training);
    
    Optional<Training> selectById(Long trainingId);
    
    List<Training> selectAll();
    
    List<Training> selectByTraineeId(Long traineeId);
    
    List<Training> selectByTrainerId(Long trainerId);
}
