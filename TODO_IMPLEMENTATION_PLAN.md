# GYM CRM - Implementation TODO List

## Overview
This document outlines all requirements and implementation steps for the Gym CRM Spring Core application.

---

## 1. DOMAIN MODEL ENTITIES

### TODO 1.1: Review/Create Domain Models
- [ ] Verify `Trainee` entity class exists
  - Fields: id, firstName, lastName, username, password, dateOfBirth, address, isActive
- [ ] Verify `Trainer` entity class exists
  - Fields: id, firstName, lastName, username, password, specialization, isActive
- [ ] Verify `Training` entity class exists
  - Fields: id, traineeId, trainerId, trainingName, trainingType, trainingDate, trainingDuration

---

## 2. STORAGE LAYER

### TODO 2.1: Implement In-Memory Storage Bean
- [ ] Create `StorageService` class as a Spring bean
- [ ] Implement three separate `Map<Long, Entity>` for each entity type:
  - `Map<Long, Trainee> traineeStorage`
  - `Map<Long, Trainer> trainerStorage`
  - `Map<Long, Training> trainingStorage`
- [ ] Use separate namespaces for each entity type
- [ ] Annotate with `@Component` or configure in `AppConfig`

### TODO 2.2: Implement Data Initialization from File
- [ ] Create a data initialization file (e.g., `initial-data.txt` or `initial-data.json`)
- [ ] Define file path in `application.properties` using property placeholder (e.g., `storage.init.file.path=classpath:initial-data.txt`)
- [ ] Implement `@PostConstruct` method or `InitializingBean` interface in `StorageService`
- [ ] Parse data from file and populate storage maps during application startup
- [ ] Add prepared test data for Trainee, Trainer, and Training entities

---

## 3. DAO LAYER

### TODO 3.1: Create Trainee DAO
- [ ] Create `TraineeDAO` interface with methods:
  - `Trainee create(Trainee trainee)`
  - `Trainee update(Trainee trainee)`
  - `void delete(Long id)`
  - `Optional<Trainee> findById(Long id)`
  - `List<Trainee> findAll()`
  - `Optional<Trainee> findByUsername(String username)`
- [ ] Create `TraineeDAOImpl` implementation class
- [ ] Inject `StorageService` using setter-based injection
- [ ] Annotate with `@Repository` or configure in `AppConfig`

### TODO 3.2: Create Trainer DAO
- [ ] Create `TrainerDAO` interface with methods:
  - `Trainer create(Trainer trainer)`
  - `Trainer update(Trainer trainer)`
  - `Optional<Trainer> findById(Long id)`
  - `List<Trainer> findAll()`
  - `Optional<Trainer> findByUsername(String username)`
- [ ] Create `TrainerDAOImpl` implementation class
- [ ] Inject `StorageService` using setter-based injection
- [ ] Annotate with `@Repository` or configure in `AppConfig`

### TODO 3.3: Create Training DAO
- [ ] Create `TrainingDAO` interface with methods:
  - `Training create(Training training)`
  - `Optional<Training> findById(Long id)`
  - `List<Training> findAll()`
  - `List<Training> findByTraineeId(Long traineeId)`
  - `List<Training> findByTrainerId(Long trainerId)`
- [ ] Create `TrainingDAOImpl` implementation class
- [ ] Inject `StorageService` using setter-based injection
- [ ] Annotate with `@Repository` or configure in `AppConfig`

---

## 4. SERVICE LAYER

### TODO 4.1: Implement Trainee Service
- [ ] Create `TraineeService` interface with methods:
  - `Trainee createTrainee(Trainee trainee)`
  - `Trainee updateTrainee(Trainee trainee)`
  - `void deleteTrainee(Long id)`
  - `Optional<Trainee> getTrainee(Long id)`
  - `List<Trainee> getAllTrainees()`
- [ ] Create `TraineeServiceImpl` implementation class
- [ ] Inject `TraineeDAO` using autowiring (setter-based injection)
- [ ] Implement username generation logic in create method:
  - Concatenate firstName + "." + lastName
  - Check if username already exists
  - If exists, append serial number (e.g., John.Smith, John.Smith1, John.Smith2)
- [ ] Implement password generation logic:
  - Generate random 10-character string
- [ ] Add proper logging (INFO, DEBUG, ERROR levels)
- [ ] Annotate with `@Service` or configure in `AppConfig`

### TODO 4.2: Implement Trainer Service
- [ ] Create `TrainerService` interface with methods:
  - `Trainer createTrainer(Trainer trainer)`
  - `Trainer updateTrainer(Trainer trainer)`
  - `Optional<Trainer> getTrainer(Long id)`
  - `List<Trainer> getAllTrainers()`
- [ ] Create `TrainerServiceImpl` implementation class
- [ ] Inject `TrainerDAO` using autowiring (setter-based injection)
- [ ] Implement username generation logic in create method:
  - Concatenate firstName + "." + lastName
  - Check if username already exists
  - If exists, append serial number
- [ ] Implement password generation logic:
  - Generate random 10-character string
- [ ] Add proper logging (INFO, DEBUG, ERROR levels)
- [ ] Annotate with `@Service` or configure in `AppConfig`

### TODO 4.3: Implement Training Service
- [ ] Create `TrainingService` interface with methods:
  - `Training createTraining(Training training)`
  - `Optional<Training> getTraining(Long id)`
  - `List<Training> getAllTrainings()`
  - `List<Training> getTrainingsByTrainee(Long traineeId)`
  - `List<Training> getTrainingsByTrainer(Long trainerId)`
- [ ] Create `TrainingServiceImpl` implementation class
- [ ] Inject `TrainingDAO` using autowiring (setter-based injection)
- [ ] Add proper logging (INFO, DEBUG, ERROR levels)
- [ ] Annotate with `@Service` or configure in `AppConfig`

---

## 5. FACADE LAYER

### TODO 5.1: Create Gym Facade
- [ ] Create `GymFacade` class to provide unified interface
- [ ] Inject all three services using constructor-based injection:
  - `TraineeService`
  - `TrainerService`
  - `TrainingService`
- [ ] Implement facade methods that delegate to appropriate services
- [ ] Add comprehensive logging
- [ ] Annotate with `@Component` or configure in `AppConfig`

---

## 6. UTILITY CLASSES

### TODO 6.1: Create Username Generator Utility
- [ ] Create `UsernameGenerator` utility class
- [ ] Implement method: `String generateUsername(String firstName, String lastName, Function<String, Boolean> existsChecker)`
- [ ] Handle duplicate username scenarios with serial number suffix
- [ ] Add unit tests

### TODO 6.2: Create Password Generator Utility
- [ ] Create `PasswordGenerator` utility class
- [ ] Implement method: `String generatePassword(int length)` (default 10 characters)
- [ ] Use alphanumeric characters (A-Z, a-z, 0-9)
- [ ] Add unit tests

---

## 7. CONFIGURATION

### TODO 7.1: Configure Spring Application Context
- [ ] Review/Update `AppConfig.java` with annotation-based configuration
- [ ] Use `@Configuration` annotation
- [ ] Enable component scanning with `@ComponentScan("com.gymcrm")`
- [ ] Enable property placeholder with `@PropertySource("classpath:application.properties")`
- [ ] Configure `PropertySourcesPlaceholderConfigurer` bean if needed

### TODO 7.2: Configure Properties File
- [ ] Update `application.properties` with:
  - `storage.init.file.path=classpath:initial-data.txt`
  - Logging configuration properties
  - Any other application-specific properties

### TODO 7.3: Configure Logging
- [ ] Review/Update `logback.xml` configuration
- [ ] Define loggers for each package:
  - `com.gymcrm.dao` - DEBUG level
  - `com.gymcrm.service` - INFO level
  - `com.gymcrm.config` - INFO level
- [ ] Configure console and file appenders
- [ ] Define appropriate logging patterns

---

## 8. DEPENDENCY INJECTION SETUP

### TODO 8.1: Configure Injection Strategies
- [ ] Verify StorageService is injected into DAOs using setter-based injection
- [ ] Verify DAOs are injected into Services using setter-based injection with `@Autowired`
- [ ] Verify Services are injected into Facade using constructor-based injection
- [ ] Add `@Autowired` annotations where appropriate

---

## 9. TESTING

### TODO 9.1: Unit Tests for DAOs
- [ ] Create `TraineeDAOTest` class
  - Test create, update, delete, findById, findAll operations
  - Mock StorageService
- [ ] Create `TrainerDAOTest` class
  - Test create, update, findById, findAll operations
  - Mock StorageService
- [ ] Create `TrainingDAOTest` class
  - Test create, findById, findAll operations
  - Mock StorageService

### TODO 9.2: Unit Tests for Services
- [ ] Create `TraineeServiceTest` class
  - Test all CRUD operations
  - Test username generation (including duplicate scenarios)
  - Test password generation
  - Mock TraineeDAO
- [ ] Create `TrainerServiceTest` class
  - Test all CRUD operations
  - Test username generation (including duplicate scenarios)
  - Test password generation
  - Mock TrainerDAO
- [ ] Create `TrainingServiceTest` class
  - Test create and select operations
  - Mock TrainingDAO

### TODO 9.3: Unit Tests for Utilities
- [ ] Create `UsernameGeneratorTest` class
  - Test basic username generation
  - Test duplicate username handling
  - Test edge cases (null, empty strings)
- [ ] Create `PasswordGeneratorTest` class
  - Test password length
  - Test randomness
  - Test character set compliance

### TODO 9.4: Integration Tests
- [ ] Create integration test for application context loading
- [ ] Test end-to-end scenarios through facade
- [ ] Test data initialization from file
- [ ] Verify all beans are properly wired

---

## 10. MAIN APPLICATION

### TODO 10.1: Update Main Application Class
- [ ] Review `GymCrmApplication.java`
- [ ] Load application context using AnnotationConfigApplicationContext
- [ ] Retrieve and test GymFacade bean
- [ ] Demonstrate create/update/delete/select operations
- [ ] Add logging for application startup and shutdown

---

## 11. DATA INITIALIZATION FILE

### TODO 11.1: Create Initial Data File
- [ ] Create `src/main/resources/initial-data.txt` (or JSON)
- [ ] Add sample Trainee records (at least 3-5)
- [ ] Add sample Trainer records (at least 3-5)
- [ ] Add sample Training records (at least 5-10)
- [ ] Ensure data format is parseable by StorageService

---

## 12. CODE QUALITY

### TODO 12.1: Logging Implementation
- [ ] Add INFO logs for all service method entries/exits
- [ ] Add DEBUG logs for DAO operations
- [ ] Add ERROR logs for exceptions
- [ ] Add WARN logs for validation failures
- [ ] Include meaningful context in log messages (IDs, usernames, etc.)

### TODO 12.2: Code Review and Cleanup
- [ ] Ensure all classes follow single responsibility principle
- [ ] Add JavaDoc comments to all public methods
- [ ] Verify exception handling is appropriate
- [ ] Ensure code follows consistent naming conventions
- [ ] Remove any unused imports or code

### TODO 12.3: Documentation
- [ ] Add README.md with project overview
- [ ] Document how to run the application
- [ ] Document how to run tests
- [ ] Document configuration properties

---

## IMPLEMENTATION ORDER RECOMMENDATION

1. **Phase 1: Foundation**
   - Domain models (if not exist)
   - Storage bean
   - DAO interfaces and implementations

2. **Phase 2: Business Logic**
   - Utility classes (Username & Password generators)
   - Service interfaces and implementations
   - Facade layer

3. **Phase 3: Configuration**
   - Spring configuration
   - Properties file
   - Logging configuration
   - Data initialization file

4. **Phase 4: Testing**
   - Unit tests for utilities
   - Unit tests for DAOs
   - Unit tests for services
   - Integration tests

5. **Phase 5: Finalization**
   - Main application
   - Code review
   - Documentation
   - Final testing

---

## SUCCESS CRITERIA

- [ ] All three services (Trainee, Trainer, Training) are fully implemented
- [ ] CRUD operations work correctly for all entities
- [ ] Username generation follows the specified rules
- [ ] Password generation creates 10-character random strings
- [ ] Spring context is properly configured
- [ ] Dependency injection works as specified (constructor for facade, setter for others)
- [ ] Storage initializes with data from file on startup
- [ ] All code has appropriate logging
- [ ] Unit test coverage is comprehensive
- [ ] Application runs without errors
- [ ] All requirements from the task document are met

---

## NOTES

- Pay special attention to username generation with duplicate handling
- Ensure thread-safety is considered for the in-memory storage
- Use appropriate design patterns (Factory, Strategy, etc.) where beneficial
- Follow Spring best practices for bean lifecycle management
- Ensure proper separation of concerns across layers
