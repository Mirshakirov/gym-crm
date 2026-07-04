package com.gymcrm.facade;

import com.gymcrm.model.Trainee;
import com.gymcrm.model.Trainer;
import com.gymcrm.model.Training;
import com.gymcrm.service.TraineeService;
import com.gymcrm.service.TrainerService;
import com.gymcrm.service.TrainingService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class GymFacade {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    public GymFacade(TraineeService traineeService, TrainerService trainerService, TrainingService trainingService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
    }

    public Trainee createTrainee(Trainee trainee) {
        return traineeService.create(trainee);
    }

    public Trainee updateTrainee(Trainee trainee) {
        return traineeService.update(trainee);
    }

    public void deleteTrainee(Long traineeId) {
        traineeService.delete(traineeId);
    }

    public Optional<Trainee> getTraineeById(Long traineeId) {
        return traineeService.selectById(traineeId);
    }

    public List<Trainee> getAllTrainees() {
        return traineeService.selectAll();
    }

    public Trainer createTrainer(Trainer trainer) {
        return trainerService.create(trainer);
    }

    public Trainer updateTrainer(Trainer trainer) {
        return trainerService.update(trainer);
    }

    public Optional<Trainer> getTrainerById(Long trainerId) {
        return trainerService.selectById(trainerId);
    }

    public List<Trainer> getAllTrainers() {
        return trainerService.selectAll();
    }

    public Training createTraining(Training training) {
        return trainingService.create(training);
    }

    public Optional<Training> getTrainingById(Long trainingId) {
        return trainingService.selectById(trainingId);
    }

    public List<Training> getAllTrainings() {
        return trainingService.selectAll();
    }
}
