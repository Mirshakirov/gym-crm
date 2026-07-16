package com.gymcrm.facade;

import com.gymcrm.entity.Trainee;
import com.gymcrm.entity.Trainer;
import com.gymcrm.entity.Training;
import com.gymcrm.service.TraineeService;
import com.gymcrm.service.TrainerService;
import com.gymcrm.service.TrainingService;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

    public Training addTraining(Training training) {
        return trainingService.create(training);
    }

    public Optional<Training> getTrainingById(Long trainingId) {
        return trainingService.selectById(trainingId);
    }

    public List<Training> getAllTrainings() {
        return trainingService.selectAll();
    }

    public boolean matchTraineeCredentials(String username, String password) {
        return traineeService.matchCredentials(username, password);
    }

    public boolean matchTrainerCredentials(String username, String password) {
        return trainerService.matchCredentials(username, password);
    }

    public Optional<Trainer> getTrainerByUsername(String username) {
        return trainerService.selectByUsername(username);
    }

    public Optional<Trainee> getTraineeByUsername(String username) {
        return traineeService.selectByUsername(username);
    }

    public void changeTraineePassword(String username, String newPassword) {
        traineeService.changePassword(username, newPassword);
    }

    public void changeTrainerPassword(String username, String newPassword) {
        trainerService.changePassword(username, newPassword);
    }

    public Trainee setTraineeActiveStatus(String username, boolean active) {
        return traineeService.setActiveStatus(username, active);
    }

    public Trainer setTrainerActiveStatus(String username, boolean active) {
        return trainerService.setActiveStatus(username, active);
    }

    public void deleteTraineeByUsername(String username) {
        traineeService.deleteByUsername(username);
    }

    public List<Training> getTraineeTrainingsByCriteria(String traineeUsername,
                                                        LocalDate fromDate,
                                                        LocalDate toDate,
                                                        String trainerName,
                                                        String trainingType) {
        return traineeService.getTrainingsByCriteria(
                traineeUsername,
                fromDate,
                toDate,
                trainerName,
                trainingType
        );
    }

    public List<Training> getTrainerTrainingsByCriteria(String trainerUsername,
                                                        LocalDate fromDate,
                                                        LocalDate toDate,
                                                        String traineeName) {
        return trainerService.getTrainingsByCriteria(
                trainerUsername,
                fromDate,
                toDate,
                traineeName
        );
    }

    public List<Trainer> getTrainersNotAssignedToTrainee(String traineeUsername) {
        Trainee trainee = traineeService.selectByUsername(traineeUsername)
                .orElseThrow(() -> new IllegalArgumentException("Trainee not found for username: " + traineeUsername));

        Set<String> assignedTrainerUsernames = trainee.getTrainers().stream()
                .map(trainer -> trainer.getUser().getUsername())
                .collect(Collectors.toSet());

        return trainerService.selectAll().stream()
                .filter(trainer -> !assignedTrainerUsernames.contains(trainer.getUser().getUsername()))
                .toList();
    }

    public Trainee updateTraineeTrainersList(String traineeUsername, List<String> trainerUsernames) {
        if (trainerUsernames == null) {
            throw new IllegalArgumentException("Trainer usernames cannot be null");
        }

        Trainee trainee = traineeService.selectByUsername(traineeUsername)
                .orElseThrow(() -> new IllegalArgumentException("Trainee not found for username: " + traineeUsername));

        Map<String, Trainer> trainerByUsername = trainerService.selectAll().stream()
                .collect(Collectors.toMap(trainer -> trainer.getUser().getUsername(), trainer -> trainer));

        List<Trainer> newAssignedTrainers = new ArrayList<>();
        for (String trainerUsername : trainerUsernames) {
            Trainer trainer = trainerByUsername.get(trainerUsername);
            if (trainer == null) {
                throw new IllegalArgumentException("Trainer not found for username: " + trainerUsername);
            }
            newAssignedTrainers.add(trainer);
        }

        Set<String> newAssignedSet = newAssignedTrainers.stream()
                .map(trainer -> trainer.getUser().getUsername())
                .collect(Collectors.toSet());

        for (Trainer previouslyAssigned : trainee.getTrainers()) {
            if (!newAssignedSet.contains(previouslyAssigned.getUser().getUsername())) {
                previouslyAssigned.getTrainees().removeIf(t -> traineeUsername.equals(t.getUser().getUsername()));
                trainerService.update(previouslyAssigned);
            }
        }

        for (Trainer newlyAssigned : newAssignedTrainers) {
            boolean alreadyAssigned = newlyAssigned.getTrainees().stream()
                    .anyMatch(t -> traineeUsername.equals(t.getUser().getUsername()));
            if (!alreadyAssigned) {
                newlyAssigned.getTrainees().add(trainee);
                trainerService.update(newlyAssigned);
            }
        }

        trainee.setTrainers(newAssignedTrainers);
        return traineeService.update(trainee);
    }
}
