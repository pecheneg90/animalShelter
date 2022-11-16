package sky.pro.animalshelter.service;

import com.pengrad.telegrambot.model.Message;
import sky.pro.animalshelter.model.Animal;
import sky.pro.animalshelter.model.Report;
import sky.pro.animalshelter.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserService {

    User save(User user);

    User edit(Long id, Long chatId, User user, User.UserStatus status, Animal.AnimalTypes type);

    User createUserByVolunteer(User user, Animal.AnimalTypes type);

    Optional<User> parse(String userDataMessage, Long chatId);

    String registrationUser(Message inputMessage);

    User getUserById(Long id);

    User getUserByChatId(Long chatId);

    void deleteUserById(Long id);

    Collection<User> getAllUsers();

    List<User> getAllAdopters(User.UserStatus status);

    boolean adopterOnTrialExists(Long id);

    List<User> getAdoptersWithEndOfTrial(User.UserStatus status, LocalDate endTrialDate);

    List<User> getAdoptersByReportStatusAndSentDate(Report.ReportStatus reportStatus, LocalDate sentDate);

    List<User>getAdoptersByStatusAndReportDate(User.UserStatus status, LocalDate sentDate);

    List<User> getAdoptersByStatusAndExtendedTrial(User.UserStatus status);
}