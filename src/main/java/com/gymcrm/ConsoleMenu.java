package com.gymcrm;

import com.gymcrm.entity.Trainee;
import com.gymcrm.entity.Trainer;
import com.gymcrm.entity.Training;
import com.gymcrm.entity.TrainingType;
import com.gymcrm.entity.User;
import com.gymcrm.facade.GymFacade;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class ConsoleMenu {

    private final GymFacade gymFacade;
    private final Scanner scanner;
    private String authenticatedUsername;
    private String authenticatedRole;

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
                case "1" -> executeProtected(this::showAllTrainees);
                case "2" -> executeProtected(this::showAllTrainers);
                case "3" -> executeProtected(this::showAllTrainings);
                case "4" -> execute(this::createTraineeProfile);
                case "5" -> execute(this::createTrainerProfile);
                case "6" -> execute(this::matchTraineeCredentials);
                case "7" -> execute(this::matchTrainerCredentials);
                case "8" -> executeProtected(this::selectTrainerByUsername);
                case "9" -> executeProtected(this::selectTraineeByUsername);
                case "10" -> executeProtected(this::changeTraineePassword);
                case "11" -> executeProtected(this::changeTrainerPassword);
                case "12" -> executeProtected(this::updateTrainerProfile);
                case "13" -> executeProtected(this::updateTraineeProfile);
                case "14" -> executeProtected(this::setTraineeActiveStatus);
                case "15" -> executeProtected(this::setTrainerActiveStatus);
                case "16" -> executeProtected(this::deleteTraineeByUsername);
                case "17" -> executeProtected(this::getTraineeTrainingsByCriteria);
                case "18" -> executeProtected(this::getTrainerTrainingsByCriteria);
                case "19" -> executeProtected(this::addTraining);
                case "20" -> executeProtected(this::getTrainersNotAssignedToTrainee);
                case "21" -> executeProtected(this::updateTraineeTrainersList);
                case "22" -> executeProtected(this::findTraineeById);
                case "0" -> {
                    running = false;
                    System.out.println("Goodbye!");
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void printMenu() {
        String authState = isAuthenticated()
                ? String.format("Authenticated as %s '%s'", authenticatedRole, authenticatedUsername)
                : "Not authenticated";
        System.out.println("""
                
                ==========================================
                    GYM CRM MANAGEMENT SYSTEM
                ==========================================
                %s
                
                1. Show all trainees
                2. Show all trainers
                3. Show all trainings
                4. Create trainee profile
                5. Create trainer profile
                6. Authenticate as trainee
                7. Authenticate as trainer
                8. Select trainer profile by username
                9. Select trainee profile by username
                10. Trainee password change
                11. Trainer password change
                12. Update trainer profile
                13. Update trainee profile
                14. Activate/De-activate trainee
                15. Activate/De-activate trainer
                16. Delete trainee profile by username
                17. Get trainee trainings by criteria
                18. Get trainer trainings by criteria
                19. Add training
                20. Get unassigned trainers for trainee
                21. Update trainee's trainers list
                22. Find trainee by ID
                
                0. Exit
                
                ==========================================
                """.formatted(authState));

        System.out.print("Choose option: ");
    }

    private void execute(MenuAction action) {
        try {
            action.run();
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void executeProtected(MenuAction action) {
        if (!isAuthenticated()) {
            System.out.println("Authentication required. Use option 6 or 7 first.");
            return;
        }
        execute(action);
    }

    private boolean isAuthenticated() {
        return authenticatedUsername != null && authenticatedRole != null;
    }

    private void showAllTrainees() {
        List<Trainee> trainees = gymFacade.getAllTrainees();
        if (trainees.isEmpty()) {
            System.out.println("No trainees found.");
            return;
        }

        trainees.forEach(this::printTraineeProfile);
    }

    private void showAllTrainers() {
        List<Trainer> trainers = gymFacade.getAllTrainers();
        if (trainers.isEmpty()) {
            System.out.println("No trainers found.");
            return;
        }

        trainers.forEach(this::printTrainerProfile);
    }

    private void showAllTrainings() {
        printTrainings(gymFacade.getAllTrainings());
    }

    private void createTraineeProfile() {
        Trainee trainee = new Trainee();
        trainee.setUser(new User());

        trainee.getUser().setFirstName(readRequired("First name: "));
        trainee.getUser().setLastName(readRequired("Last name: "));
        trainee.getUser().setActive(true);
        trainee.setDateOfBirth(readDate("Date of birth (yyyy-MM-dd): "));
        trainee.setAddress(readOptional("Address: "));

        Trainee created = gymFacade.createTrainee(trainee);
        System.out.printf("✓ Trainee created. Username: %s%n", created.getUser().getUsername());
    }

    private void createTrainerProfile() {
        Trainer trainer = new Trainer();
        trainer.setUser(new User());

        trainer.getUser().setFirstName(readRequired("First name: "));
        trainer.getUser().setLastName(readRequired("Last name: "));
        trainer.getUser().setActive(true);

        TrainingType specialization = new TrainingType();
        specialization.setTrainingTypeName(readRequired("Specialization (training type): "));
        trainer.setSpecialization(specialization);

        Trainer created = gymFacade.createTrainer(trainer);
        System.out.printf("✓ Trainer created. Username: %s%n", created.getUser().getUsername());
    }

    private void matchTraineeCredentials() {
        String username = readRequired("Trainee username: ");
        String password = readRequired("Password: ");
        boolean matched = gymFacade.matchTraineeCredentials(username, password);
        if (matched) {
            authenticatedUsername = username;
            authenticatedRole = "trainee";
            System.out.println("✓ Authenticated as trainee.");
            return;
        }
        authenticatedUsername = null;
        authenticatedRole = null;
        System.out.println("✗ Credentials do not match.");
    }

    private void matchTrainerCredentials() {
        String username = readRequired("Trainer username: ");
        String password = readRequired("Password: ");
        boolean matched = gymFacade.matchTrainerCredentials(username, password);
        if (matched) {
            authenticatedUsername = username;
            authenticatedRole = "trainer";
            System.out.println("✓ Authenticated as trainer.");
            return;
        }
        authenticatedUsername = null;
        authenticatedRole = null;
        System.out.println("✗ Credentials do not match.");
    }

    private void selectTrainerByUsername() {
        String username = readRequired("Trainer username: ");
        Optional<Trainer> trainer = gymFacade.getTrainerByUsername(username);
        if (trainer.isEmpty()) {
            System.out.println("Trainer not found.");
            return;
        }
        printTrainerProfile(trainer.get());
    }

    private void selectTraineeByUsername() {
        String username = readRequired("Trainee username: ");
        Optional<Trainee> trainee = gymFacade.getTraineeByUsername(username);
        if (trainee.isEmpty()) {
            System.out.println("Trainee not found.");
            return;
        }
        printTraineeProfile(trainee.get());
    }

    private void changeTraineePassword() {
        String username = readRequired("Trainee username: ");
        String password = readRequired("New password: ");
        gymFacade.changeTraineePassword(username, password);
        System.out.println("✓ Trainee password updated.");
    }

    private void changeTrainerPassword() {
        String username = readRequired("Trainer username: ");
        String password = readRequired("New password: ");
        gymFacade.changeTrainerPassword(username, password);
        System.out.println("✓ Trainer password updated.");
    }

    private void updateTrainerProfile() {
        String username = readRequired("Trainer username to update: ");
        Trainer trainer = gymFacade.getTrainerByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Trainer not found."));

        String firstName = readOptional("New first name (blank to keep): ");
        String lastName = readOptional("New last name (blank to keep): ");
        String specializationName = readOptional("New specialization (blank to keep): ");

        if (firstName != null) {
            trainer.getUser().setFirstName(firstName);
        }
        if (lastName != null) {
            trainer.getUser().setLastName(lastName);
        }
        if (specializationName != null) {
            trainer.getSpecialization().setTrainingTypeName(specializationName);
        }

        Trainer updated = gymFacade.updateTrainer(trainer);
        System.out.printf("✓ Trainer profile updated: %s%n", updated.getUser().getUsername());
    }

    private void updateTraineeProfile() {
        String username = readRequired("Trainee username to update: ");
        Trainee trainee = gymFacade.getTraineeByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Trainee not found."));

        String firstName = readOptional("New first name (blank to keep): ");
        String lastName = readOptional("New last name (blank to keep): ");
        String address = readOptional("New address (blank to keep): ");
        LocalDate dateOfBirth = readOptionalDate("New birth date yyyy-MM-dd (blank to keep): ");

        if (firstName != null) {
            trainee.getUser().setFirstName(firstName);
        }
        if (lastName != null) {
            trainee.getUser().setLastName(lastName);
        }
        if (address != null) {
            trainee.setAddress(address);
        }
        if (dateOfBirth != null) {
            trainee.setDateOfBirth(dateOfBirth);
        }

        Trainee updated = gymFacade.updateTrainee(trainee);
        System.out.printf("✓ Trainee profile updated: %s%n", updated.getUser().getUsername());
    }

    private void setTraineeActiveStatus() {
        String username = readRequired("Trainee username: ");
        boolean active = readBoolean("Set active? (true/false): ");
        Trainee trainee = gymFacade.setTraineeActiveStatus(username, active);
        System.out.printf("✓ Trainee %s is now %s.%n", trainee.getUser().getUsername(), trainee.getUser().isActive() ? "active" : "inactive");
    }

    private void setTrainerActiveStatus() {
        String username = readRequired("Trainer username: ");
        boolean active = readBoolean("Set active? (true/false): ");
        Trainer trainer = gymFacade.setTrainerActiveStatus(username, active);
        System.out.printf("✓ Trainer %s is now %s.%n", trainer.getUser().getUsername(), trainer.getUser().isActive() ? "active" : "inactive");
    }

    private void deleteTraineeByUsername() {
        String username = readRequired("Trainee username to delete: ");
        gymFacade.deleteTraineeByUsername(username);
        System.out.println("✓ Trainee deleted.");
    }

    private void getTraineeTrainingsByCriteria() {
        String username = readRequired("Trainee username: ");
        LocalDate fromDate = readOptionalDate("From date yyyy-MM-dd (blank to skip): ");
        LocalDate toDate = readOptionalDate("To date yyyy-MM-dd (blank to skip): ");
        String trainerName = readOptional("Trainer name contains (blank to skip): ");
        String trainingType = readOptional("Training type (blank to skip): ");

        List<Training> trainings = gymFacade.getTraineeTrainingsByCriteria(
                username,
                fromDate,
                toDate,
                trainerName,
                trainingType
        );
        printTrainings(trainings);
    }

    private void getTrainerTrainingsByCriteria() {
        String username = readRequired("Trainer username: ");
        LocalDate fromDate = readOptionalDate("From date yyyy-MM-dd (blank to skip): ");
        LocalDate toDate = readOptionalDate("To date yyyy-MM-dd (blank to skip): ");
        String traineeName = readOptional("Trainee name contains (blank to skip): ");

        List<Training> trainings = gymFacade.getTrainerTrainingsByCriteria(
                username,
                fromDate,
                toDate,
                traineeName
        );
        printTrainings(trainings);
    }

    private void addTraining() {
        String traineeUsername = readRequired("Trainee username: ");
        String trainerUsername = readRequired("Trainer username: ");

        Trainee trainee = gymFacade.getTraineeByUsername(traineeUsername)
                .orElseThrow(() -> new IllegalArgumentException("Trainee not found."));
        Trainer trainer = gymFacade.getTrainerByUsername(trainerUsername)
                .orElseThrow(() -> new IllegalArgumentException("Trainer not found."));

        Training training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingType(trainer.getSpecialization());
        training.setTrainingName(readRequired("Training name: "));
        training.setTrainingDate(readDate("Training date (yyyy-MM-dd): "));
        training.setTrainingDuration(readInt("Duration (minutes): "));

        gymFacade.addTraining(training);
        System.out.println("✓ Training added.");
    }

    private void getTrainersNotAssignedToTrainee() {
        String traineeUsername = readRequired("Trainee username: ");
        List<Trainer> trainers = gymFacade.getTrainersNotAssignedToTrainee(traineeUsername);
        if (trainers.isEmpty()) {
            System.out.println("No unassigned trainers found.");
            return;
        }
        trainers.forEach(this::printTrainerProfile);
    }

    private void updateTraineeTrainersList() {
        String traineeUsername = readRequired("Trainee username: ");
        String usernames = readOptional("Comma-separated trainer usernames (blank for empty list): ");
        List<String> trainerUsernames = usernames == null
                ? List.of()
                : Arrays.stream(usernames.split(","))
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .toList();

        Trainee updated = gymFacade.updateTraineeTrainersList(traineeUsername, trainerUsernames);
        System.out.printf("✓ Trainer list updated for %s. Assigned trainers: %d%n",
                updated.getUser().getUsername(),
                updated.getTrainers().size());
    }

    private void findTraineeById() {
        Long id = readLong("Enter trainee ID: ");
        Optional<Trainee> trainee = gymFacade.getTraineeById(id);

        if (trainee.isEmpty()) {
            System.out.println("Trainee not found.");
            return;
        }
        printTraineeProfile(trainee.get());
    }

    private void printTraineeProfile(Trainee trainee) {
        System.out.printf("ID: %d | %s %s | Username: %s | Active: %s | DOB: %s | Address: %s%n",
                trainee.getId(),
                trainee.getUser().getFirstName(),
                trainee.getUser().getLastName(),
                trainee.getUser().getUsername(),
                trainee.getUser().isActive(),
                trainee.getDateOfBirth(),
                trainee.getAddress());
    }

    private void printTrainerProfile(Trainer trainer) {
        System.out.printf("ID: %d | %s %s | Username: %s | Active: %s | Specialization: %s%n",
                trainer.getId(),
                trainer.getUser().getFirstName(),
                trainer.getUser().getLastName(),
                trainer.getUser().getUsername(),
                trainer.getUser().isActive(),
                trainer.getSpecialization() == null ? null : trainer.getSpecialization().getTrainingTypeName());
    }

    private void printTrainings(List<Training> trainings) {
        if (trainings.isEmpty()) {
            System.out.println("No trainings found.");
            return;
        }

        trainings.forEach(training -> System.out.printf(
                "ID: %d | %s | %s | %d min | Trainee: %s | Trainer: %s | Type: %s%n",
                training.getId(),
                training.getTrainingName(),
                training.getTrainingDate(),
                training.getTrainingDuration(),
                training.getTrainee().getUser().getUsername(),
                training.getTrainer().getUser().getUsername(),
                training.getTrainingType().getTrainingTypeName()
        ));
    }

    private String readRequired(String message) {
        while (true) {
            System.out.print(message);
            String value = scanner.nextLine().trim();
            if (!value.isEmpty()) {
                return value;
            }
            System.out.println("Value cannot be empty.");
        }
    }

    private String readOptional(String message) {
        System.out.print(message);
        String value = scanner.nextLine().trim();
        return value.isEmpty() ? null : value;
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
            } catch (DateTimeParseException e) {
                System.out.println("Use format yyyy-MM-dd");
            }
        }
    }

    private LocalDate readOptionalDate(String message) {
        while (true) {
            String value = readOptional(message);
            if (value == null) {
                return null;
            }
            try {
                return LocalDate.parse(value);
            } catch (DateTimeParseException e) {
                System.out.println("Use format yyyy-MM-dd");
            }
        }
    }

    private boolean readBoolean(String message) {
        while (true) {
            System.out.print(message);
            String value = scanner.nextLine().trim().toLowerCase();
            if ("true".equals(value) || "t".equals(value) || "yes".equals(value) || "y".equals(value) || "1".equals(value)) {
                return true;
            }
            if ("false".equals(value) || "f".equals(value) || "no".equals(value) || "n".equals(value) || "0".equals(value)) {
                return false;
            }
            System.out.println("Please enter true/false, yes/no, or 1/0.");
        }
    }

    @FunctionalInterface
    private interface MenuAction {
        void run();
    }
}
