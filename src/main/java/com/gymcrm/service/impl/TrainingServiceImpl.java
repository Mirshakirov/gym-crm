package com.gymcrm.service.impl;

import com.gymcrm.repository.TrainingDAO;
import com.gymcrm.entity.Training;
import com.gymcrm.service.TrainingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TrainingServiceImpl implements TrainingService {
    
    private TrainingDAO trainingDAO;

    @Autowired
    public void setTrainingDAO(TrainingDAO trainingDAO) {
        this.trainingDAO = trainingDAO;
    }

    @Override
    public Training create(Training training) {
        if (training == null) {
            throw new IllegalArgumentException("Training cannot be null");
        }
        if (training.getTrainee() == null) {
            throw new IllegalArgumentException("Trainee cannot be null");
        }
        if (training.getTrainer() == null) {
            throw new IllegalArgumentException("Trainer cannot be null");
        }
        return trainingDAO.save(training);
    }

    @Override
    public Optional<Training> selectById(Long trainingId) {
        if (trainingId == null) {
            throw new IllegalArgumentException("Training ID cannot be null");
        }
        return trainingDAO.selectById(trainingId);
    }

    @Override
    public List<Training> selectAll() {
        return trainingDAO.selectAll();
    }
}
