package com.gymcrm.dao;

import com.gymcrm.model.Trainee;

import java.util.List;
import java.util.Optional;

public interface TraineeDAO {
    
    Trainee save(Trainee trainee);
    
    Trainee update(Trainee trainee);
    
    void delete(Long traineeId);
    
    Optional<Trainee> selectById(Long traineeId);
    
    List<Trainee> selectAll();
    
    Optional<Trainee> selectByUsername(String username);
}
