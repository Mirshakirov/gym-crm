package com.gymcrm.service;

import com.gymcrm.model.Trainer;

import java.util.List;
import java.util.Optional;

public interface TrainerService {
    
    Trainer create(Trainer trainer);
    
    Trainer update(Trainer trainer);
    
    Optional<Trainer> selectById(Long trainerId);
    
    List<Trainer> selectAll();
}
