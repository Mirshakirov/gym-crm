package com.gymcrm.repository;

import com.gymcrm.entity.Trainer;

import java.util.List;
import java.util.Optional;

public interface TrainerDAO {
    
    Trainer save(Trainer trainer);
    
    Trainer update(Trainer trainer);
    
    Optional<Trainer> selectById(Long trainerId);
    
    List<Trainer> selectAll();
    
    Optional<Trainer> selectByUsername(String username);
}
