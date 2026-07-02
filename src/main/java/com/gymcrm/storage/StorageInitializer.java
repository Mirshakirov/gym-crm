package com.gymcrm.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymcrm.model.Trainee;
import com.gymcrm.model.Trainer;
import com.gymcrm.model.Training;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class StorageInitializer {

    private final ResourceLoader resourceLoader;
    private final ObjectMapper objectMapper;

    private final Map<Long, Trainee> traineeStorage;
    private final Map<Long, Trainer> trainerStorage;
    private final Map<Long, Training> trainingStorage;

    @Value("${storage.trainees.file.path}")
    private String traineesFilePath;

    @Value("${storage.trainers.file.path}")
    private String trainersFilePath;

    @Value("${storage.trainings.file.path}")
    private String trainingsFilePath;

    public StorageInitializer(
            ResourceLoader resourceLoader,
            ObjectMapper objectMapper,
            Map<Long, Trainee> traineeStorage,
            Map<Long, Trainer> trainerStorage,
            Map<Long, Training> trainingStorage) {

        this.resourceLoader = resourceLoader;
        this.objectMapper = objectMapper;
        this.traineeStorage = traineeStorage;
        this.trainerStorage = trainerStorage;
        this.trainingStorage = trainingStorage;
    }

    @PostConstruct
    public void initializeStorages() {
        initializeTraineeStorage();
        initializeTrainerStorage();
        initializeTrainingStorage();
    }

    @PreDestroy
    public void persistStorages() {
        persistTrainees();
        persistTrainers();
        persistTrainings();
    }

    private void persistTrainees() {
        try {
            Resource resource = resourceLoader.getResource(traineesFilePath);

            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(resource.getFile(),
                            traineeStorage.values());

            log.info("Saved {} trainees to {}",
                    traineeStorage.size(),
                    traineesFilePath);

        } catch (IOException e) {
            log.warn("Could not save trainees to {}: {}",
                    traineesFilePath,
                    e.getMessage());
        }
    }

    private void persistTrainers() {
        try {
            Resource resource = resourceLoader.getResource(trainersFilePath);

            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(resource.getFile(),
                            trainerStorage.values());

            log.info("Saved {} trainers to {}",
                    trainerStorage.size(),
                    trainersFilePath);

        } catch (IOException e) {
            log.warn("Could not save trainers: {}", e.getMessage());
        }
    }

    private void persistTrainings() {
        try {
            Resource resource = resourceLoader.getResource(trainingsFilePath);

            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(resource.getFile(),
                            trainingStorage.values());

            log.info("Saved {} trainings to {}",
                    trainingStorage.size(),
                    trainingsFilePath);

        } catch (IOException e) {
            log.warn("Could not save trainings: {}", e.getMessage());
        }
    }

    private void initializeTraineeStorage() {
        try {
            Resource resource = resourceLoader.getResource(traineesFilePath);

            if (resource.exists()) {
                List<Trainee> trainees = objectMapper.readValue(
                        resource.getInputStream(),
                        objectMapper.getTypeFactory()
                                .constructCollectionType(List.class, Trainee.class)
                );

                trainees.forEach(trainee ->
                        traineeStorage.put(trainee.getUserId(), trainee));

                log.info("Loaded {} trainees from {}", trainees.size(), traineesFilePath);
            }
        } catch (IOException e) {
            log.warn("Could not load trainee data from {}: {}",
                    traineesFilePath, e.getMessage());
        }
    }

    private void initializeTrainerStorage() {
        try {
            Resource resource = resourceLoader.getResource(trainersFilePath);

            if (resource.exists()) {
                List<Trainer> trainers = objectMapper.readValue(
                        resource.getInputStream(),
                        objectMapper.getTypeFactory()
                                .constructCollectionType(List.class, Trainer.class)
                );

                trainers.forEach(trainer ->
                        trainerStorage.put(trainer.getUserId(), trainer));

                log.info("Loaded {} trainers from {}", trainers.size(), trainersFilePath);
            }
        } catch (IOException e) {
            log.warn("Could not load trainer data from {}: {}",
                    trainersFilePath, e.getMessage());
        }
    }

    private void initializeTrainingStorage() {
        try {
            Resource resource = resourceLoader.getResource(trainingsFilePath);

            if (resource.exists()) {
                List<Training> trainings = objectMapper.readValue(
                        resource.getInputStream(),
                        objectMapper.getTypeFactory()
                                .constructCollectionType(List.class, Training.class)
                );

                long id = 1;
                for (Training training : trainings) {
                    trainingStorage.put(id++, training);
                }

                log.info("Loaded {} trainings from {}", trainings.size(), trainingsFilePath);
            }
        } catch (IOException e) {
            log.warn("Could not load training data from {}: {}",
                    trainingsFilePath, e.getMessage());
        }
    }
}
