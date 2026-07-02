package com.gymcrm.service;

import com.gymcrm.model.Trainee;

import java.util.List;
import java.util.Optional;

public interface TraineeService {
    
    Trainee create(Trainee trainee);
    
    Trainee update(Trainee trainee);
    
    void delete(Long traineeId);
    
    Optional<Trainee> selectById(Long traineeId);
    
    List<Trainee> selectAll();
}
