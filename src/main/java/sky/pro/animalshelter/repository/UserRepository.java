package sky.pro.animalshelter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sky.pro.animalshelter.model.Report;
import sky.pro.animalshelter.model.User;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select u from User u where u.id = ?1")
    User findUserById(Long id);

    @Query("select u from User u where u.chatId = ?1")
    User findUserByChatId(Long chatId);

    @Query("select u from User u where u.status = ?1")
    List<User> findAllAdopters(User.UserStatus status);

    @Query("select u from User u where u.status = ?1 and u.endTrialDate = ?2")
    List<User> findAdoptersWithEndOfTrial(User.UserStatus status, LocalDate endTrialDate);

    @Query("select u from User u inner join Report on u.id in (select r.clientId from Report as r where r.status = ?1 and r.sentDate = ?2)")
    List<User> findAdoptersByReportStatusAndSentDate(Report.ReportStatus reportStatus, LocalDate sentDate);

    @Query("select u from User u inner join Report on u.status = ?1 and u.id in (select r.clientId from Report as r where r.sentDate = ?2)")
    List<User> findAdoptersByStatusAndReportDate(User.UserStatus status, LocalDate sentDate);

}