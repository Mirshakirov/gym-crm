package com.gymcrm;

import com.gymcrm.facade.GymFacade;
import com.gymcrm.model.Trainee;
import com.gymcrm.model.Trainer;
import com.gymcrm.model.Training;
import com.gymcrm.model.TrainingType;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class ConsoleMenu {

    private final GymFacade gymFacade;
    private final Scanner scanner;

    public ConsoleMenu(GymFacade gymFacade) {
        this.gymFacade = gymFacade;
        this.scanner = new Scanner(System.in);
    }

    public void start() {

        boolean running = true;

        while (running) {

            printMenu();

            String option = scanner.nextLine();

            switch (option) {

                case "1" -> showAllTrainees();

                case "2" -> showAllTrainers();

                case "3" -> showAllTrainings();

                case "4" -> findTraineeById();

                case "5" -> createTrainee();

                case "6" -> createTraining();

                case "7" -> deleteTrainee();

                case "0" -> {
                    running = false;
                    System.out.println("Goodbye!");
                }

                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void printMenu() {

        System.out.println("""
                
                ===============================
                    GYM CRM MANAGEMENT SYSTEM
                ===============================
                
                1. Show all trainees
                2. Show all trainers
                3. Show all trainings
                4. Find trainee by ID
                5. Create trainee
                6. Create training
                7. Delete trainee
                
                0. Exit
                
                ===============================
                """);

        System.out.print("Choose option: ");
    }

    private void showAllTrainees() {

        List<Trainee> trainees = gymFacade.getAllTrainees();

        if (trainees.isEmpty()) {
            System.out.println("No trainees found.");
            return;
        }

        trainees.forEach(t ->
                System.out.printf(
                        "ID: %d | %s %s | Username: %s%n",
                        t.getUserId(),
                        t.getFirstName(),
                        t.getLastName(),
                        t.getUsername()));
    }

    private void showAllTrainers() {

        List<Trainer> trainers = gymFacade.getAllTrainers();

        if (trainers.isEmpty()) {
            System.out.println("No trainers found.");
            return;
        }

        trainers.forEach(t ->
                System.out.printf(
                        "ID: %d | %s %s | Type: %s%n",
                        t.getUserId(),
                        t.getFirstName(),
                        t.getLastName(),
                        t.getTrainingType().getTrainingTypeName()));
    }

    private void showAllTrainings() {

        List<Training> trainings = gymFacade.getAllTrainings();

        if (trainings.isEmpty()) {
            System.out.println("No trainings found.");
            return;
        }

        trainings.forEach(t ->
                System.out.printf(
                        "%s | %s | %d min%n",
                        t.getTrainingName(),
                        t.getTrainingDate(),
                        t.getTrainingDuration()));
    }

    private void findTraineeById() {

        Long id = readLong("Enter trainee ID: ");

        Optional<Trainee> trainee = gymFacade.getTraineeById(id);

        if (trainee.isPresent()) {

            Trainee t = trainee.get();

            System.out.println("\n===== Trainee =====");
            System.out.println("ID: " + t.getUserId());
            System.out.println("Name: " + t.getFirstName() + " " + t.getLastName());
            System.out.println("Username: " + t.getUsername());
            System.out.println("Address: " + t.getAddress());
            System.out.println("Birth date: " + t.getDateOfBirth());
            System.out.println("Active: " + t.isActive());

        } else {

            System.out.println("Trainee not found.");
        }
    }

    private Long readLong(String message) {

        while (true) {

            try {

                System.out.print(message);
                return Long.parseLong(scanner.nextLine());

            } catch (NumberFormatException e) {

                System.out.println("Please enter a valid number.");
            }
        }
    }

    private Integer readInt(String message) {

        while (true) {

            try {

                System.out.print(message);
                return Integer.parseInt(scanner.nextLine());

            } catch (NumberFormatException e) {

                System.out.println("Please enter a valid number.");
            }
        }
    }

    private LocalDate readDate(String message) {

        while (true) {

            try {

                System.out.print(message);
                return LocalDate.parse(scanner.nextLine());

            } catch (Exception e) {

                System.out.println("Use format yyyy-MM-dd");
            }
        }
    }
    private void createTrainee() {

        Trainee trainee = new Trainee();

        System.out.print("First name: ");
        trainee.setFirstName(scanner.nextLine());

        System.out.print("Last name: ");
        trainee.setLastName(scanner.nextLine());

        trainee.setActive(true);

        trainee.setDateOfBirth(
                readDate("Date of birth (yyyy-MM-dd): "));

        System.out.print("Address: ");
        trainee.setAddress(scanner.nextLine());

        Trainee created = gymFacade.createTrainee(trainee);

        System.out.println();
        System.out.println("✓ Trainee created successfully!");
        System.out.println("Generated ID: " + created.getUserId());
    }

    private void createTraining() {

        Training training = new Training();

        training.setTraineeId(
                readLong("Trainee ID: "));

        training.setTrainerId(
                readLong("Trainer ID: "));

        System.out.print("Training name: ");
        training.setTrainingName(scanner.nextLine());

        System.out.print("Training type: ");
        training.setTrainingType(
                new TrainingType(scanner.nextLine()));

        training.setTrainingDate(
                readDate("Training date (yyyy-MM-dd): "));

        training.setTrainingDuration(
                readInt("Duration (minutes): "));

        gymFacade.createTraining(training);

        System.out.println();
        System.out.println("✓ Training created successfully!");
    }

    private void deleteTrainee() {

        Long id = readLong("Enter trainee ID to delete: ");

        Optional<Trainee> trainee = gymFacade.getTraineeById(id);

        if (trainee.isEmpty()) {
            System.out.println("Trainee not found.");
            return;
        }

        gymFacade.deleteTrainee(id);

        System.out.println("✓ Trainee deleted successfully.");
    }
}