package com.gymcrm.service;

import com.gymcrm.entity.Training;

import java.util.List;
import java.util.Optional;

public interface TrainingService {
    
    Training create(Training training);
    
    Optional<Training> selectById(Long trainingId);
    
    List<Training> selectAll();
}
