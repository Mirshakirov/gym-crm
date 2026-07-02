package com.gymcrm.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.gymcrm.model.Trainee;
import com.gymcrm.model.Trainer;
import com.gymcrm.model.Training;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StorageInitializerTest {

    @Mock
    private ResourceLoader resourceLoader;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ObjectWriter objectWriter;

    @Mock
    private Resource resource;

    @Mock
    private File file;

    private Map<Long, Trainee> traineeStorage;
    private Map<Long, Trainer> trainerStorage;
    private Map<Long, Training> trainingStorage;
    private StorageInitializer storageInitializer;

    @BeforeEach
    void setUp() throws Exception {
        traineeStorage = new HashMap<>();
        trainerStorage = new HashMap<>();
        trainingStorage = new HashMap<>();

        storageInitializer = new StorageInitializer(
                resourceLoader,
                objectMapper,
                traineeStorage,
                trainerStorage,
                trainingStorage
        );

        setFieldValue(storageInitializer, "traineesFilePath", "classpath:trainees.json");
        setFieldValue(storageInitializer, "trainersFilePath", "classpath:trainers.json");
        setFieldValue(storageInitializer, "trainingsFilePath", "classpath:trainings.json");
    }

    private void setFieldValue(Object target, String fieldName, String value) throws Exception {
        Field field = StorageInitializer.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Test
    void testInitializeStoragesWhenResourcesDoNotExist() {
        when(resourceLoader.getResource(anyString())).thenReturn(resource);
        when(resource.exists()).thenReturn(false);

        storageInitializer.initializeStorages();

        verify(resourceLoader, times(3)).getResource(anyString());
        verify(resource, times(3)).exists();
    }

    @Test
    void testInitializeStoragesHandlesIOException() throws IOException {
        when(resourceLoader.getResource(anyString())).thenReturn(resource);
        when(resource.exists()).thenReturn(true);
        when(resource.getInputStream()).thenThrow(new IOException("Read error"));

        storageInitializer.initializeStorages();

        verify(resourceLoader, times(3)).getResource(anyString());
    }

    @Test
    void testPersistStoragesSuccessfully() throws IOException {
        traineeStorage.put(1L, createTrainee(1L));
        trainerStorage.put(2L, createTrainer(2L));
        trainingStorage.put(1L, new Training());

        when(resourceLoader.getResource(anyString())).thenReturn(resource);
        when(resource.getFile()).thenReturn(file);
        when(objectMapper.writerWithDefaultPrettyPrinter()).thenReturn(objectWriter);
        doNothing().when(objectWriter).writeValue(any(File.class), any());

        storageInitializer.persistStorages();

        verify(resourceLoader, times(3)).getResource(anyString());
        verify(objectWriter, times(3)).writeValue(any(File.class), any());
    }

    @Test
    void testPersistStoragesHandlesIOException() throws IOException {
        traineeStorage.put(1L, createTrainee(1L));
        trainerStorage.put(1L, createTrainer(1L));
        trainingStorage.put(1L, new Training());

        when(resourceLoader.getResource(anyString())).thenReturn(resource);
        when(resource.getFile()).thenThrow(new IOException("Write error"));

        storageInitializer.persistStorages();

        verify(resourceLoader, times(3)).getResource(anyString());
    }

    @Test
    void testPersistEmptyStorages() throws IOException {
        when(resourceLoader.getResource(anyString())).thenReturn(resource);
        when(resource.getFile()).thenReturn(file);
        when(objectMapper.writerWithDefaultPrettyPrinter()).thenReturn(objectWriter);
        doNothing().when(objectWriter).writeValue(any(File.class), any());

        storageInitializer.persistStorages();

        verify(objectWriter, times(3)).writeValue(any(File.class), any());
    }

    private Trainee createTrainee(Long userId) {
        Trainee trainee = new Trainee();
        trainee.setUserId(userId);
        trainee.setUsername("testuser" + userId);
        return trainee;
    }

    private Trainer createTrainer(Long userId) {
        Trainer trainer = new Trainer();
        trainer.setUserId(userId);
        trainer.setUsername("testtrainer" + userId);
        return trainer;
    }
}
