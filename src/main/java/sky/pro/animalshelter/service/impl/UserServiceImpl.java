package sky.pro.animalshelter.service.impl;

import com.pengrad.telegrambot.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import sky.pro.animalshelter.model.Animal;
import sky.pro.animalshelter.model.Report;
import sky.pro.animalshelter.model.User;
import sky.pro.animalshelter.repository.UserRepository;
import sky.pro.animalshelter.service.AnimalService;
import sky.pro.animalshelter.service.UserService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static sky.pro.animalshelter.constants.Constants.CONTACT_EXAMPLE;
import static sky.pro.animalshelter.constants.Constants.SUCCESS_SAVING;
import static sky.pro.animalshelter.model.Animal.AnimalTypes.NO_ANIMAL;
import static sky.pro.animalshelter.model.User.UserStatus.ADOPTER_ON_TRIAL;
import static sky.pro.animalshelter.model.User.UserStatus.GUEST;

@Service
public class UserServiceImpl implements UserService {

    private final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private static final String REGEX_BOT_MESSAGE = "([\\W+]+)(\\s)(\\+7\\d{3}[-.]?\\d{3}[-.]?\\d{4})(\\s)([a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+)";

    private final UserRepository repository;
    private final AnimalService animalService;

    public UserServiceImpl(UserRepository repository, AnimalService animalService) {
        this.repository = repository;
        this.animalService = animalService;
    }

    /**
     * Сохранить созданного пользователя в репозитории
     *
     * @param user - созданный пользователь
     * @return savedUser - данные пользователя, которые были сохранены в репозитории
     */

    @Override
    public User save(User user) {
        user.setStatus(GUEST);
        user.setAnimal(animalService.getAnimalByName(NO_ANIMAL));
        return repository.save(user);
    }

    @Override
    public User edit(Long id, Long chatId, User user, User.UserStatus status, Animal.AnimalTypes type) {
        user.setId(id);
        user.setChatId(chatId);
        user.setStatus(status);
        user.setAnimal(animalService.getAnimalByName(type));
        return repository.save(user);
    }


    /**
     * Создание пользователя в репозитории через swagger, без бота
     *
     * @param user - Пользлватель создан волонтером
     * @return сохраненный пользователь, заполняется вручную
     */

    @Override
    public User createUserByVolunteer(User user, Animal.AnimalTypes type) {
        user.setAnimal(animalService.getAnimalByName(type));
        logger.info("Was invoked method to create a user by volunteer");
        return repository.save(user);
    }

    /**
     * Парсинг данных пользователя по имени, номеру телефона и почте
     *
     * @param userDataMessage получено сообщение от пользователя
     * @return parsing result
     */

    @Override
    public Optional<User> parse(String userDataMessage, Long chatId) {
        logger.info("Parsing method has been called");
        Pattern pattern = Pattern.compile(REGEX_BOT_MESSAGE);
        Matcher matcher = pattern.matcher(userDataMessage);
        User result = null;
        try {
            if (matcher.find()) {
                String name = matcher.group(1);
                String phoneNumber = matcher.group(3);
                String email = matcher.group(5);
                result = new User(name, phoneNumber, email);
                result.setChatId(chatId);
            }
        } catch (Exception e) {
            logger.error("Failed to parse user's data: " + userDataMessage, e);
        }
        return Optional.ofNullable(result);
    }

    @Override
    public String registrationUser(Message inputMessage) {
        String outputMessage;
        Optional<User> parseResult = parse(inputMessage.text(), inputMessage.chat().id());
        if (parseResult.isPresent()) {
            long chatId = inputMessage.chat().id();
            if (getUserByChatId(chatId) == null) {
                logger.info("Parse result is valid");
                save(parseResult.get());
                outputMessage = SUCCESS_SAVING;
            } else {
                logger.info("Data is already exists, it will be restored");
                User currentUser = getUserByChatId(chatId);
                Animal.AnimalTypes type = currentUser.getAnimal().getType();
                User editedUser = edit(currentUser.getId(), chatId, parseResult.get(), currentUser.getStatus(), type);
                logger.info("Client with id {} has been edited successfully: ", editedUser.getId());
                outputMessage = "Ваши данные успешно перезаписаны!";
            }
        } else {
            logger.info("Invalid registration data");
            outputMessage = CONTACT_EXAMPLE;
        }
        return outputMessage;
    }

    @Override
    public User getUserById(Long id) {
        logger.info("Was invoked method to find a user by Id");
        return repository.findById(id).orElse(null);
    }

    @Override
    public User getUserByChatId(Long chatId) {
        logger.info("Was invoked method to find user by chatId");
        return repository.findUserByChatId(chatId);
    }

    @Override
    public void deleteUserById(Long id) {
        logger.info("Was invoked method to delete a quest by Id");
        repository.deleteById(id);
    }

    @Override
    public Collection<User> getAllUsers() {
        logger.info("Was invoked method to get a list of all users");
        return repository.findAll();
    }

    @Override
    public List<User> getAllAdopters(User.UserStatus status) {
        logger.info("Was invoked method to get a list of all users with definite status");
        return repository.findAllAdopters(status);
    }

    @Override
    public boolean adopterOnTrialExists(Long chatId) {
        return (repository.findUserByChatId(chatId) != null
                && repository.findUserByChatId(chatId).getStatus().equals(ADOPTER_ON_TRIAL));
    }

    @Override
    public List<User> getAdoptersWithEndOfTrial(User.UserStatus status, LocalDate endTrialDate) {
        return repository.findAdoptersWithEndOfTrial(status, endTrialDate);
    }


    @Override
    public List<User> getAdoptersByReportStatusAndSentDate(Report.ReportStatus reportStatus, LocalDate sentDate) {
        return repository.findAdoptersByReportStatusAndSentDate(reportStatus, sentDate);
    }

    @Override
    public List<User> getAdoptersByStatusAndReportDate(User.UserStatus status, LocalDate sentDate) {
        return repository.findAdoptersByStatusAndReportDate(status, sentDate);
    }

    @Override
    public List<User> getAdoptersByStatusAndExtendedTrial(User.UserStatus status) {
        List<User> adoptersList = repository.findAllAdopters(ADOPTER_ON_TRIAL);
        List<User> adoptersWithExtendedTrial = new ArrayList<>();
        if (adoptersList != null) {
            for (User user : adoptersList) {
                if (user.getStartTrialDate().plusDays(30).equals(LocalDate.now())) {
                    adoptersWithExtendedTrial.add(user);
                }
            }
        }
        return adoptersWithExtendedTrial;
    }
}