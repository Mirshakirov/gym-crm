package com.gymcrm.repository;

import com.gymcrm.entity.Trainee;

import java.util.List;
import java.util.Optional;

public interface TraineeDAO {
    
    Trainee save(Trainee trainee);
    
    Trainee update(Trainee trainee);
    
    void delete(Long traineeId);

    void deleteByUsername(String username);
    
    Optional<Trainee> selectById(Long traineeId);
    
    List<Trainee> selectAll();
    
    Optional<Trainee> selectByUsername(String username);
}
